import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';

export interface LoginRequest {
  correo: string;
  contrasena: string;
}

export interface RegistroRequest {
  nombreUsuario: string;
  correo?: string;
  contrasena?: string;
  rol: string;
  direccion?: string;
  celular?: string;
  fechaNacimiento?: string;
  genero?: string;
  codigo?: string;
}

export interface EnviarOtpRequest {
  celular: string;
}

export interface OtpEnviarResponse {
  success: boolean;
  message: string;
}

export interface OtpLoginRequest {
  celular: string;
  codigo: string;
}

export interface OtpRegistroRequest {
  celular: string;
  codigo: string;
  nombreUsuario: string;
  rol: string;
  correo?: string;
  fechaNacimiento?: string;
  genero?: string;
  direccion?: string;
}

export interface LoginResponse {
  token: string;
  rol: string;
  mensaje: string;
  idUsuario: number;
  nombreUsuario: string;
}

export interface RestablecerContrasenaRequest {
  celular: string;
  codigo: string;
  contrasena: string;
  confirmarContrasena: string;
}

// Cada pestaña guarda su sesión en sessionStorage (así se pueden tener
// varias cuentas abiertas a la vez). Además se guarda una copia de la
// última sesión iniciada en localStorage, que sobrevive al cerrar el
// navegador: al abrir una ventana nueva sin sesión se restaura esa copia.
const CLAVES_SESION = ['token', 'rol', 'idUsuario', 'nombreUsuario'] as const;
const CLAVE_SESION_RECORDADA = 'sesionRecordada';

type Sesion = Record<(typeof CLAVES_SESION)[number], string>;

function tokenVigente(token: string): boolean {
  try {
    const payload = JSON.parse(
      atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/'))
    );
    return typeof payload.exp !== 'number' || payload.exp * 1000 > Date.now();
  } catch {
    return false;
  }
}

function leerSesionRecordada(): Sesion | null {
  try {
    const guardada = localStorage.getItem(CLAVE_SESION_RECORDADA);
    return guardada ? (JSON.parse(guardada) as Sesion) : null;
  } catch {
    return null;
  }
}

function escribirSesionRecordada(sesion: Sesion | null): void {
  try {
    if (sesion) {
      localStorage.setItem(CLAVE_SESION_RECORDADA, JSON.stringify(sesion));
    } else {
      localStorage.removeItem(CLAVE_SESION_RECORDADA);
    }
  } catch {
    // Sin acceso a localStorage (p. ej. modo privado estricto): la sesión
    // simplemente no se recuerda al cerrar la ventana.
  }
}

// Si esta pestaña no tiene sesión, intenta recuperar la última recordada.
// Devuelve si la pestaña quedó con sesión.
function restaurarSesion(): boolean {
  if (sessionStorage.getItem('token')) {
    return true;
  }

  const recordada = leerSesionRecordada();

  if (!recordada?.token) {
    return false;
  }

  if (!tokenVigente(recordada.token)) {
    escribirSesionRecordada(null);
    return false;
  }

  for (const clave of CLAVES_SESION) {
    sessionStorage.setItem(clave, recordada[clave]);
  }

  return true;
}

const RUTAS_POR_ROL: Record<string, string> = {
  ORGANIZACION: '/panel/organizacion',
  VOLUNTARIO: '/panel/voluntario',
  ACOMPANANTE: '/panel/acompanante',
  PERSONA_MAYOR: '/panel/persona-mayor'
};

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly apiUrl = 'http://localhost:8080/api/auth';
  private readonly otpApiUrl = 'http://localhost:8080/api/otp';

  private readonly autenticadoSignal = signal(restaurarSesion());

  // Nombre mostrado en el panel (arriba a la derecha). Es un signal
  // para que al editarlo en "Mi información" se vea al instante.
  private readonly nombreUsuarioSignal = signal(
    sessionStorage.getItem('nombreUsuario') ?? 'Usuario'
  );

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.apiUrl}/login`, request)
      .pipe(
        tap((response) => this.guardarSesion(response))
      );
  }

  registro(request: RegistroRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.apiUrl}/registro`, request)
      .pipe(
        tap((response) => this.guardarSesion(response))
      );
  }

  celularExiste(celular: string): Observable<boolean> {
  return this.http.get<boolean>(
    `${this.apiUrl}/celular-existe`,
    {
      params: { celular }
    }
  );
}

restablecerContrasena(
  request: RestablecerContrasenaRequest
) {
  return this.http.post(
    `${this.apiUrl}/restablecer-contrasena`,
    request
  );
}

  // El envío de OTP vive en messaging-backend (/api/otp/send),
  // no en auth-backend. Espera "phoneNumber", no "celular", y
  // responde {success, message}, no un LoginResponse.
  enviarOtp(celular: string): Observable<OtpEnviarResponse> {
    return this.http.post<OtpEnviarResponse>(
      `${this.otpApiUrl}/send`,
      { phoneNumber: celular }
    );
  }

  loginOtp(request: OtpLoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(
        `${this.apiUrl}/login-otp`,
        request
      )
      .pipe(
        tap((response) => this.guardarSesion(response))
      );
  }

  // El registro por OTP usa el MISMO /api/auth/registro de siempre;
  // auth-backend decide internamente si valida el codigo o no según
  // si viene correo+contrasena. No existe un endpoint separado.
  registroOtp(request: OtpRegistroRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(
        `${this.apiUrl}/registro`,
        request
      )
      .pipe(
        tap((response) => this.guardarSesion(response))
      );
  }

  // Borra la cuenta del usuario autenticado (cualquier rol).
  // El backend toma el id del token.
  eliminarCuenta(): Observable<string> {
    return this.http.delete(`${this.apiUrl}/cuenta`, {
      responseType: 'text'
    });
  }

  private guardarSesion(response: LoginResponse): void {
    sessionStorage.setItem('token', response.token);
    sessionStorage.setItem('rol', response.rol);
    sessionStorage.setItem('idUsuario', String(response.idUsuario));
    sessionStorage.setItem('nombreUsuario', response.nombreUsuario);
    this.nombreUsuarioSignal.set(response.nombreUsuario);

    escribirSesionRecordada({
      token: response.token,
      rol: response.rol,
      idUsuario: String(response.idUsuario),
      nombreUsuario: response.nombreUsuario
    });

    // Avisar a toda la aplicación que hay una sesión
    this.autenticadoSignal.set(true);
  }

  redirigirSegunRol(rol: string): void {
    const ruta = RUTAS_POR_ROL[rol] ?? '/';
    this.router.navigateByUrl(ruta);
  }

  logout(): void {
    // Solo se olvida la sesión recordada si es la de esta pestaña;
    // así cerrar sesión aquí no afecta a otra cuenta abierta en otra.
    if (leerSesionRecordada()?.token === sessionStorage.getItem('token')) {
      escribirSesionRecordada(null);
    }

    sessionStorage.removeItem('token');
    sessionStorage.removeItem('rol');
    sessionStorage.removeItem('idUsuario');
    sessionStorage.removeItem('nombreUsuario');
    this.nombreUsuarioSignal.set('Usuario');

    // Avisar a toda la aplicación que la sesión terminó
    this.autenticadoSignal.set(false);

    this.router.navigateByUrl('/');
  }

  getToken(): string | null {
    return sessionStorage.getItem('token');
  }

  getIdUsuario(): number | null {
    const idUsuario = sessionStorage.getItem('idUsuario');
    return idUsuario ? Number(idUsuario) : null;
  }

  // Lee un signal: usado dentro de computed()/plantillas se
  // actualiza solo cuando cambia el nombre.
  getNombreUsuario(): string {
    return this.nombreUsuarioSignal();
  }

  // Llamar después de guardar un nombre nuevo (cualquier rol).
  actualizarNombreUsuario(nombre: string): void {
    sessionStorage.setItem('nombreUsuario', nombre);
    this.nombreUsuarioSignal.set(nombre);

    const recordada = leerSesionRecordada();
    if (recordada && recordada.token === sessionStorage.getItem('token')) {
      escribirSesionRecordada({ ...recordada, nombreUsuario: nombre });
    }
  }

  getRol(): string | null {
    return sessionStorage.getItem('rol');
  }

  estaAutenticado(): boolean {
    return this.autenticadoSignal();
  }

  estaAutenticadoSignal() {
    return this.autenticadoSignal;
  }
}