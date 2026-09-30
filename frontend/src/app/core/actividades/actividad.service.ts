import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// =========================================================
// ACTIVIDAD
// =========================================================

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
  responsable: string | null;
}

// =========================================================
// ACTIVIDAD DISPONIBLE PARA PERSONA MAYOR
// =========================================================

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

// =========================================================
// CREAR / ACTUALIZAR ACTIVIDAD
// =========================================================

export interface ActividadRequest {
  nombre: string;
  descripcion: string | null;
  fecha: string | null;
  hora: string | null;
  lugar: string | null;
  tipo: string | null;
  cupos: number | null;
  responsable: string | null;
}

// =========================================================
// PARTICIPANTES
// =========================================================

export interface ParticipanteActividad {
  idPersonaMayor: number;
  nombre: string;
  celular: string | null;
  asistio: boolean | null;
}

// =========================================================
// PRÓXIMAS / HISTORIAL (mismo criterio en todos los perfiles)
// =========================================================

/** Fecha de hoy YYYY-MM-DD en hora local (toISOString() usaría UTC). */
export function fechaHoy(): string {
  return new Date().toLocaleDateString('en-CA');
}

/**
 * Separa las actividades en próximas (desde hoy) e historial, en orden
 * cronológico:
 *  - Próximas: de la más cercana a la más lejana. Las que no tienen fecha
 *    van al final.
 *  - Historial: de la más reciente a la más antigua.
 * Dentro del mismo día se ordena por hora.
 */
export function separarPorFecha<T extends { fecha: string | null; hora: string | null }>(
  actividades: T[]
): { proximas: T[]; pasadas: T[] } {
  const hoy = fechaHoy();
  // "9:00" -> "09:00" para que la comparación de texto respete la hora
  const clave = (a: T) =>
    `${a.fecha ?? '9999-12-31'}T${a.hora ? a.hora.padStart(5, '0') : '99:99'}`;

  const proximas = actividades
    .filter((a) => a.fecha === null || a.fecha >= hoy)
    .sort((a, b) => clave(a).localeCompare(clave(b)));

  const pasadas = actividades
    .filter((a) => a.fecha !== null && a.fecha < hoy)
    .sort((a, b) => clave(b).localeCompare(clave(a)));

  return { proximas, pasadas };
}

// =========================================================
// SERVICIO
// =========================================================

@Injectable({
  providedIn: 'root'
})
export class ActividadService {

  private readonly apiUrl = 'http://localhost:8080/api/actividades';

  constructor(private http: HttpClient) {}

  // =========================================================
  // LISTAR ACTIVIDADES
  // =========================================================

  listar(): Observable<Actividad[]> {
    return this.http.get<Actividad[]>(this.apiUrl);
  }

  // =========================================================
  // ACTIVIDADES DE MI ORGANIZACIÓN
  // =========================================================

  listarMias(): Observable<Actividad[]> {
    return this.http.get<Actividad[]>(
      `${this.apiUrl}/mias`
    );
  }

  // =========================================================
  // ACTIVIDADES DISPONIBLES PARA PERSONA MAYOR
  // =========================================================

  listarDisponibles(): Observable<ActividadDisponible[]> {
    return this.http.get<ActividadDisponible[]>(
      `${this.apiUrl}/disponibles`
    );
  }

  // =========================================================
  // INSCRIBIRSE EN UNA ACTIVIDAD
  // =========================================================

  inscribirse(id: number): Observable<void> {
    return this.http.post<void>(
      `${this.apiUrl}/${id}/inscribirse`,
      {}
    );
  }

  // =========================================================
  // CANCELAR INSCRIPCIÓN
  // =========================================================

  cancelarInscripcion(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${id}/inscribirse`
    );
  }

  // =========================================================
  // CREAR ACTIVIDAD
  // =========================================================

  crear(request: ActividadRequest): Observable<Actividad> {
    return this.http.post<Actividad>(
      this.apiUrl,
      request
    );
  }

  // =========================================================
  // ACTUALIZAR ACTIVIDAD
  // =========================================================

  actualizar(
    id: number,
    request: ActividadRequest
  ): Observable<Actividad> {
    return this.http.put<Actividad>(
      `${this.apiUrl}/${id}`,
      request
    );
  }

  // =========================================================
  // ELIMINAR ACTIVIDAD
  // =========================================================

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }

  // =========================================================
  // LISTAR PARTICIPANTES
  // =========================================================

  listarParticipantes(
    id: number
  ): Observable<ParticipanteActividad[]> {
    return this.http.get<ParticipanteActividad[]>(
      `${this.apiUrl}/${id}/participantes`
    );
  }

  // =========================================================
  // REGISTRAR ASISTENCIA
  // =========================================================

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