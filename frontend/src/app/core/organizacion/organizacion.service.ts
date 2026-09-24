import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, switchMap } from 'rxjs';

export interface OrganizacionResponse {
  idOrganizacion: number;
  nombre: string;
  direccion: string;
  celular: string;
  correo: string;
}

export interface PersonaMayorOrganizacion {
  idUsuario: number;
  nombre: string;
  celular: string;
}

export interface OrganizacionSolicitud {
  idOrganizacion: number;
  nombre: string;
  celular: string;
  correo: string;
  direccion: string;
}

@Injectable({
  providedIn: 'root'
})
export class OrganizacionService {

  private readonly apiUrl = 'http://localhost:8080/api/organizacion';
  private readonly authInformacionUrl = 'http://localhost:8080/api/auth/informacion';

  constructor(private http: HttpClient) {}

  obtenerInformacion(): Observable<OrganizacionResponse> {
    return this.http.get<OrganizacionResponse>(
      `${this.apiUrl}/informacion`
    );
  }

  // nombre/correo son de identidad (auth-backend); organizacion-service
  // solo guarda la dirección. Primero se guarda la identidad para que la
  // respuesta de organizacion-service ya traiga el nombre nuevo.
  actualizarInformacion(
    informacion: OrganizacionResponse
  ): Observable<OrganizacionResponse> {
    return this.http.put(this.authInformacionUrl, {
      nombre: informacion.nombre,
      correo: informacion.correo
    }).pipe(
      switchMap(() => this.http.put<OrganizacionResponse>(
        `${this.apiUrl}/informacion`,
        { direccion: informacion.direccion }
      ))
    );
  }
  obtenerPersonasMayores(): Observable<PersonaMayorOrganizacion[]> {
  return this.http.get<PersonaMayorOrganizacion[]>(
    `${this.apiUrl}/personas-mayores`
  );
}

asociarPersonaMayor(celular: string): Observable<string> {
  return this.http.post(
    `${this.apiUrl}/personas-mayores`,
    { celular },
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