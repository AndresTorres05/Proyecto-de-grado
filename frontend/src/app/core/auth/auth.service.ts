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
  correo: string;
  contrasena: string;
  rol: string;
}

export interface EnviarOtpRequest {
  telefono: string;
}

export interface OtpLoginRequest {
  telefono: string;
  codigo: string;
}

export interface OtpRegistroRequest {
  telefono: string;
  codigo: string;
  nombreUsuario: string;
  rol: string;
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

  private readonly autenticadoSignal = signal(
    !!localStorage.getItem('token')
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

  enviarOtp(telefono: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(
      `${this.apiUrl}/otp/enviar`,
      { telefono }
    );
  }

  loginOtp(request: OtpLoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(
        `${this.apiUrl}/otp/login`,
        request
      )
      .pipe(
        tap((response) => this.guardarSesion(response))
      );
  }

  registroOtp(request: OtpRegistroRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(
        `${this.apiUrl}/otp/registro`,
        request
      )
      .pipe(
        tap((response) => this.guardarSesion(response))
      );
  }

  private guardarSesion(response: LoginResponse): void {
    localStorage.setItem('token', response.token);
    localStorage.setItem('rol', response.rol);
    localStorage.setItem('idUsuario', String(response.idUsuario));
    localStorage.setItem('nombreUsuario', response.nombreUsuario);

    // Avisar a toda la aplicación que hay una sesión
    this.autenticadoSignal.set(true);
  }

  redirigirSegunRol(rol: string): void {
    const ruta = RUTAS_POR_ROL[rol] ?? '/';
    this.router.navigateByUrl(ruta);
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('rol');
    localStorage.removeItem('idUsuario');
    localStorage.removeItem('nombreUsuario');

    // Avisar a toda la aplicación que la sesión terminó
    this.autenticadoSignal.set(false);

    this.router.navigateByUrl('/');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getIdUsuario(): number | null {
    const idUsuario = localStorage.getItem('idUsuario');
    return idUsuario ? Number(idUsuario) : null;
  }

  getNombreUsuario(): string {
    return localStorage.getItem('nombreUsuario') ?? 'Usuario';
  }

  getRol(): string | null {
    return localStorage.getItem('rol');
  }

  estaAutenticado(): boolean {
    return this.autenticadoSignal();
  }

  estaAutenticadoSignal() {
    return this.autenticadoSignal;
  }
}