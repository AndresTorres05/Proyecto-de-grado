import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

/** SMS enviado al celular del usuario (emergencias, recordatorios...). */
export interface Notificacion {
  id: number;
  mensaje: string;
  fechaEnvio: string;
  leida: boolean;
}

export interface NotificacionesResponse {
  noLeidas: number;
  notificaciones: Notificacion[];
}

@Injectable({ providedIn: 'root' })
export class NotificacionService {

  private readonly apiUrl = 'http://localhost:8080/api/notificaciones';

  constructor(private http: HttpClient) {}

  /** Las 20 más recientes y cuántas faltan por leer. */
  listar(): Observable<NotificacionesResponse> {
    return this.http.get<NotificacionesResponse>(this.apiUrl);
  }

  marcarLeidas(): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/leidas`, {});
  }
}
