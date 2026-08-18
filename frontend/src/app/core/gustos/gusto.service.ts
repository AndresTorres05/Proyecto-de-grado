import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Gusto {
  idGusto: number;
  nombre: string;
}

export interface GustoRequest {
  nombre: string;
}

@Injectable({ providedIn: 'root' })
export class GustoService {

  private readonly apiUrl = 'http://localhost:8080/api/gustos';

  constructor(private http: HttpClient) {}

  listar(): Observable<Gusto[]> {
    return this.http.get<Gusto[]>(this.apiUrl);
  }

  crear(request: GustoRequest): Observable<Gusto> {
    return this.http.post<Gusto>(this.apiUrl, request);
  }

  actualizar(id: number, request: GustoRequest): Observable<Gusto> {
    return this.http.put<Gusto>(`${this.apiUrl}/${id}`, request);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  listarAsignados(idPersonaMayor: number): Observable<Gusto[]> {
    return this.http.get<Gusto[]>(`http://localhost:8080/api/persona-mayor/${idPersonaMayor}/gustos`);
  }

  asignar(idPersonaMayor: number, idsGustos: number[]): Observable<Gusto[]> {
    return this.http.put<Gusto[]>(`http://localhost:8080/api/persona-mayor/${idPersonaMayor}/gustos`, {
      idsGustos
    });
  }
}
