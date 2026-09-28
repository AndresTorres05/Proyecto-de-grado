import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin, map, switchMap } from 'rxjs';

export interface PersonaMayorResponse {
  idUsuario: number;
  nombre: string;
  celular: string;
  correo: string;
  fechaNacimiento: string;
  genero: string;
  direccion: string;
  tieneContrasena: boolean;
}

export interface CambiarContrasenaRequest {
  contrasenaActual?: string;
  nuevaContrasena: string;
}

@Injectable({ providedIn: 'root' })
export class PersonaMayorService {

  private readonly authUrl = 'http://localhost:8080/api/auth/informacion';
  private readonly perfilUrl = 'http://localhost:8080/api/persona-mayor/perfil';
  private readonly contrasenaUrl = 'http://localhost:8080/api/auth/contrasena';

  constructor(private http: HttpClient) {}

  obtenerInformacion(): Observable<PersonaMayorResponse> {
    return forkJoin({
      identidad: this.http.get<any>(this.authUrl),
      perfil: this.http.get<any>(this.perfilUrl)
    }).pipe(
      map(({ identidad, perfil }) => ({
        idUsuario: identidad.idUsuario,
        nombre: identidad.nombre,
        celular: identidad.celular,
        correo: identidad.correo,
        tieneContrasena: identidad.tieneContrasena,
        fechaNacimiento: perfil.fechaNacimiento,
        genero: perfil.genero,
        direccion: perfil.direccion
      }))
    );
  }

  // Primero la identidad (nombre/correo): si el correo es rechazado,
  // no se guarda el perfil a medias.
  actualizarInformacion(informacion: PersonaMayorResponse): Observable<PersonaMayorResponse> {
    return this.http.put<any>(this.authUrl, {
      nombre: informacion.nombre,
      correo: informacion.correo
    }).pipe(
      switchMap((identidad) =>
        this.http.put<any>(this.perfilUrl, {
          fechaNacimiento: informacion.fechaNacimiento,
          genero: informacion.genero,
          direccion: informacion.direccion
        }).pipe(map((perfil) => ({ identidad, perfil })))
      ),
      map(({ identidad, perfil }) => ({
        idUsuario: identidad.idUsuario,
        nombre: identidad.nombre,
        celular: identidad.celular,
        correo: identidad.correo,
        tieneContrasena: identidad.tieneContrasena,
        fechaNacimiento: perfil.fechaNacimiento,
        genero: perfil.genero,
        direccion: perfil.direccion
      }))
    );
  }

  cambiarContrasena(request: CambiarContrasenaRequest): Observable<string> {
    return this.http.put(this.contrasenaUrl, request, { responseType: 'text' });
  }
}