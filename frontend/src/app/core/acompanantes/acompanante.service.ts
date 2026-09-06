import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Acompanante {
  idUsuario: number;
  nombre: string;
  telefono: string;
  parentesco: string;
}

export interface PersonaMayorAcompanada {
  idUsuario: number;
  nombre: string;
  telefono: string;
}

export interface SolicitudAcompanamiento {
  idUsuario: number;
  nombre: string;
  telefono: string;
}

@Injectable({ providedIn: 'root' })
export class AcompananteService {

  private readonly apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  obtenerAcompanantes(): Observable<Acompanante[]> {
    return this.http.get<Acompanante[]>(`${this.apiUrl}/persona-mayor/acompanantes`);
  }

  obtenerPersonasMayores(): Observable<PersonaMayorAcompanada[]> {
    return this.http.get<PersonaMayorAcompanada[]>(`${this.apiUrl}/acompanante/personas-mayores`);
  }

  agregarAcompanante(datos: {
    telefono: string;
    parentesco: string;
  }): Observable<string> {
    return this.http.post(
      `${this.apiUrl}/persona-mayor/acompanantes`,
      datos,
      { responseType: 'text' }
    );
  }

obtenerSolicitudes(): Observable<SolicitudAcompanamiento[]> {
  return this.http.get<SolicitudAcompanamiento[]>(
    `${this.apiUrl}/acompanante/personas-mayores/solicitudes`
  );
}

aceptarSolicitud(idPersonaMayor: number): Observable<string> {
  return this.http.put(
    `${this.apiUrl}/acompanante/personas-mayores/solicitudes/${idPersonaMayor}/aceptar`,
    {},
    { responseType: 'text' }
  );
}

rechazarSolicitud(idPersonaMayor: number): Observable<string> {
  return this.http.put(
    `${this.apiUrl}/acompanante/personas-mayores/solicitudes/${idPersonaMayor}/rechazar`,
    {},
    { responseType: 'text' }
  );
}

}
