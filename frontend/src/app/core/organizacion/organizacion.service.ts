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

export interface PersonaMayorOrganizacion {
  idUsuario: number;
  nombre: string;
  telefono: string;
}

export interface OrganizacionSolicitud {
  idOrganizacion: number;
  nombre: string;
  telefono: string;
  correo: string;
  direccion: string;
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
  obtenerPersonasMayores(): Observable<PersonaMayorOrganizacion[]> {
  return this.http.get<PersonaMayorOrganizacion[]>(
    `${this.apiUrl}/personas-mayores`
  );
}

asociarPersonaMayor(telefono: string): Observable<string> {
  return this.http.post(
    `${this.apiUrl}/personas-mayores`,
    { telefono },
    { responseType: 'text' }
  );
}

cancelarAsociacionPersonaMayor(idPersonaMayor: number): Observable<string> {
  return this.http.delete(
    `${this.apiUrl}/personas-mayores/${idPersonaMayor}`,
    { responseType: 'text' }
  );
}

obtenerSolicitudesOrganizaciones(): Observable<OrganizacionSolicitud[]> {
  return this.http.get<OrganizacionSolicitud[]>(
    'http://localhost:8080/api/persona-mayor/organizaciones/solicitudes'
  );
}

aceptarSolicitudOrganizacion(idOrganizacion: number): Observable<string> {
  return this.http.put(
    `http://localhost:8080/api/persona-mayor/organizaciones/solicitudes/${idOrganizacion}/aceptar`,
    {},
    { responseType: 'text' }
  );
}

rechazarSolicitudOrganizacion(idOrganizacion: number): Observable<string> {
  return this.http.put(
    `http://localhost:8080/api/persona-mayor/organizaciones/solicitudes/${idOrganizacion}/rechazar`,
    {},
    { responseType: 'text' }
  );
}
obtenerOrganizaciones(): Observable<OrganizacionSolicitud[]> {
  return this.http.get<OrganizacionSolicitud[]>(
    'http://localhost:8080/api/persona-mayor/organizaciones'
  );
}
cancelarAsociacionOrganizacion(
  idOrganizacion: number
): Observable<string> {
  return this.http.delete(
    `http://localhost:8080/api/persona-mayor/organizaciones/${idOrganizacion}`,
    { responseType: 'text' }
  );
}
}