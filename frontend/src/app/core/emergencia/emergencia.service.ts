import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

/** Botón de emergencia de la persona mayor. */
@Injectable({
  providedIn: 'root'
})
export class EmergenciaService {

  private readonly apiUrl =
    'http://localhost:8080/api/persona-mayor/emergencia';

  constructor(private http: HttpClient) {}

  /**
   * Envía la alerta por SMS a sus acompañantes y organizaciones. Responde
   * con un texto que dice a cuántos se avisó.
   */
  activarEmergencia(): Observable<string> {
    return this.http.post(
      this.apiUrl,
      {},
      {
        responseType: 'text'
      }
    );
  }
}