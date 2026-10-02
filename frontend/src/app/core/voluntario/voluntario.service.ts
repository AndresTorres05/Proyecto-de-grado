import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type EstadoVinculo = 'PENDIENTE' | 'ACEPTADA' | 'RECHAZADA';

/** Organización vista por el voluntario, con el estado de su vínculo (null = sin solicitud). */
export interface OrganizacionVoluntario {
  idOrganizacion: number;
  nombre: string;
  direccion: string | null;
  celular: string | null;
  correo: string | null;
  estado: EstadoVinculo | null;
}

// Todo pasa por el api-gateway (8080): valida el token y pone el
// X-User-Id del voluntario.
@Injectable({
  providedIn: 'root',
})
export class VoluntarioService {
  private http = inject(HttpClient);

  private apiUrl = 'http://localhost:8080/api/voluntario/organizaciones';

  /** Todas las organizaciones con el estado del vínculo del voluntario. */
  listarOrganizaciones(): Observable<OrganizacionVoluntario[]> {
    return this.http.get<OrganizacionVoluntario[]>(this.apiUrl);
  }

  solicitarVinculacion(idOrganizacion: number): Observable<string> {
    return this.http.post(`${this.apiUrl}/${idOrganizacion}/solicitud`, {}, { responseType: 'text' });
  }

  /** Cancela una solicitud pendiente, descarta una rechazada o desvincula. */
  eliminarVinculo(idOrganizacion: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/${idOrganizacion}`, { responseType: 'text' });
  }
}
