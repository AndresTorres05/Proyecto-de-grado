import { Injectable } from '@angular/core';
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

export interface LoginResponse {
  token: string;
  rol: string;
  mensaje: string;
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

  constructor(private http: HttpClient, private router: Router) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, request).pipe(
      tap((response) => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('rol', response.rol);
      })
    );
  }

  registro(request: RegistroRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.apiUrl}/registro`, request).pipe(
      tap((response) => {
        localStorage.setItem('token', response.token);
        localStorage.setItem('rol', response.rol);
      })
    );
  }

  redirigirSegunRol(rol: string): void {
    const ruta = RUTAS_POR_ROL[rol] ?? '/';
    this.router.navigateByUrl(ruta);
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('rol');
    this.router.navigateByUrl('/');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  estaAutenticado(): boolean {
    return !!this.getToken();
  }
}