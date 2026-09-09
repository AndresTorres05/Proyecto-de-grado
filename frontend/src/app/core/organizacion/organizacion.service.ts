import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface OrganizacionResponse {
  idOrganizacion: number;
  nombre: string;
  direccion: string;
  telefono: string;
  correo: string;
}

@Injectable({
  providedIn: 'root'
})
export class OrganizacionService {

  private readonly apiUrl = 'http://localhost:8080/api/organizacion';

  constructor(private http: HttpClient) {}

  obtenerInformacion(): Observable<OrganizacionResponse> {
    return this.http.get<OrganizacionResponse>(
      `${this.apiUrl}/informacion`
    );
  }

  actualizarInformacion(
    informacion: OrganizacionResponse
  ): Observable<OrganizacionResponse> {
    return this.http.put<OrganizacionResponse>(
      `${this.apiUrl}/informacion`,
      informacion
    );
  }
}