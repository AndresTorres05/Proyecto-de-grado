import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Actividad {
  idActividad: number;
  idOrganizacion: number;
  nombre: string;
  descripcion: string | null;
  fecha: string | null;
  hora: string | null;
  lugar: string | null;
  tipo: string | null;
  cupos: number | null;
}

export interface ActividadDisponible {
  idActividad: number;
  nombre: string;
  descripcion: string | null;
  fecha: string | null;
  hora: string | null;
  lugar: string | null;
  tipo: string | null;
  cupos: number | null;
  inscrito: boolean;
}

export interface ActividadRequest {
  nombre: string;
  descripcion: string | null;
  fecha: string | null;
  hora: string | null;
  lugar: string | null;
  tipo: string | null;
  cupos: number | null;
}

export interface ParticipanteActividad {
  idPersonaMayor: number;
  nombre: string;
  telefono: string | null;
  asistio: boolean | null;
}

@Injectable({ providedIn: 'root' })
export class ActividadService {

  private readonly apiUrl = 'http://localhost:8080/api/actividades';

  constructor(private http: HttpClient) {}

  listar(): Observable<Actividad[]> {
    return this.http.get<Actividad[]>(this.apiUrl);
  }

  listarMias(): Observable<Actividad[]> {
    return this.http.get<Actividad[]>(`${this.apiUrl}/mias`);
  }

  listarDisponibles(): Observable<ActividadDisponible[]> {
    return this.http.get<ActividadDisponible[]>(`${this.apiUrl}/disponibles`);
  }

  inscribirse(id: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${id}/inscribirse`, {});
  }

  cancelarInscripcion(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}/inscribirse`);
  }

  crear(request: ActividadRequest): Observable<Actividad> {
    return this.http.post<Actividad>(this.apiUrl, request);
  }

  actualizar(id: number, request: ActividadRequest): Observable<Actividad> {
    return this.http.put<Actividad>(`${this.apiUrl}/${id}`, request);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
  listarParticipantes(id: number): Observable<ParticipanteActividad[]> {
  return this.http.get<ParticipanteActividad[]>(
    `${this.apiUrl}/${id}/participantes`
  );
}

registrarAsistencia(
  idActividad: number,
  idPersonaMayor: number,
  asistio: boolean
): Observable<void> {
  return this.http.put<void>(
    `${this.apiUrl}/${idActividad}/participantes/${idPersonaMayor}/asistencia`,
    { asistio }
  );
}
}