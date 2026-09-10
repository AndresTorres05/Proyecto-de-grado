import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PersonaMayorResponse {
  idUsuario: number;
  nombre: string;
  telefono: string;
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

@Injectable({
  providedIn: 'root'
})
export class PersonaMayorService {

  private readonly apiUrl =
    'http://localhost:8080/api/persona-mayor';

  constructor(private http: HttpClient) {}

  obtenerInformacion(): Observable<PersonaMayorResponse> {
    return this.http.get<PersonaMayorResponse>(
      `${this.apiUrl}/informacion`
    );
  }

  actualizarInformacion(
    informacion: PersonaMayorResponse
  ): Observable<PersonaMayorResponse> {
    return this.http.put<PersonaMayorResponse>(
      `${this.apiUrl}/informacion`,
      informacion
    );
  }

  cambiarContrasena(
  request: CambiarContrasenaRequest
): Observable<string> {
  return this.http.put(
    `${this.apiUrl}/contrasena`,
    request,
    {
      responseType: 'text'
    }
  );
}
}