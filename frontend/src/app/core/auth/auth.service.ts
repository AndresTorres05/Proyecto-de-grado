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
  disponibilidad?: string;
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

  private readonly autenticadoSignal = signal(
    !!sessionStorage.getItem('token')
  );

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

    // Avisar a toda la aplicación que hay una sesión
    this.autenticadoSignal.set(true);
  }

  redirigirSegunRol(rol: string): void {
    const ruta = RUTAS_POR_ROL[rol] ?? '/';
    this.router.navigateByUrl(ruta);
  }

  logout(): void {
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