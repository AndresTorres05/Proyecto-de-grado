import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class EmergenciaService {

  private readonly apiUrl =
    'http://localhost:8080/api/persona-mayor/emergencia';

  constructor(private http: HttpClient) {}

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