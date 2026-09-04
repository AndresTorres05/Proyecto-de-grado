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
    nombreUsuario: string;
    telefono: string;
    parentesco: string;
  }): Observable<string> {
    return this.http.post(
      `${this.apiUrl}/persona-mayor/acompanantes`,
      datos,
      { responseType: 'text' }
    );
  }
}
