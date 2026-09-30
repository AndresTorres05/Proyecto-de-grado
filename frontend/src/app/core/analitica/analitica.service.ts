import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

// Datos "crudos" que devuelve organizacion-service; los indicadores y las
// gráficas se calculan en cada reporte.

export interface ActividadAnalitica {
  idActividad: number;
  nombre: string;
  tipo: string | null;
  fecha: string;          // YYYY-MM-DD
  cupos: number | null;
  inscritos: number;
  asistentes: number;     // asistio = true
  conRegistro: number;    // se tomó asistencia (asistio no es null)
}

export interface PersonaAnalitica {
  idUsuario: number;
  nombre: string;
}

export interface MedicionAnalitica {
  idPersonaMayor: number;
  fechaHora: string;      // YYYY-MM-DDTHH:mm
  presionSistolica: number | null;
  presionDiastolica: number | null;
  frecuenciaCardiaca: number | null;
  temperatura: number | null;
  saturacionOxigeno: number | null;
  frecuenciaRespiratoria: number | null;
  peso: number | null;
}

export interface SaludAnalitica {
  personas: PersonaAnalitica[];
  mediciones: MedicionAnalitica[];
}

export interface PersonaPoblacion {
  idUsuario: number;
  nombre: string;
  fechaNacimiento: string | null;
  genero: string | null;
  eps: string | null;
}

export interface InteresConteo {
  nombre: string;
  categoria: string;
  personas: number;
}

export interface PoblacionAnalitica {
  personas: PersonaPoblacion[];
  intereses: InteresConteo[];
  personasConIntereses: number;
}

@Injectable({ providedIn: 'root' })
export class AnaliticaService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/organizacion/analitica';

  /** Actividades con fecha entre desde y hasta (YYYY-MM-DD; null = sin límite). */
  actividades(desde: string | null, hasta: string | null): Observable<ActividadAnalitica[]> {
    let params = new HttpParams();
    if (desde) params = params.set('desde', desde);
    if (hasta) params = params.set('hasta', hasta);
    return this.http.get<ActividadAnalitica[]>(`${this.apiUrl}/actividades`, { params });
  }

  salud(): Observable<SaludAnalitica> {
    return this.http.get<SaludAnalitica>(`${this.apiUrl}/salud`);
  }

  poblacion(): Observable<PoblacionAnalitica> {
    return this.http.get<PoblacionAnalitica>(`${this.apiUrl}/poblacion`);
  }
}
