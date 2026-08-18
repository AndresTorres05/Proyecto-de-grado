import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Rol {
  idRol: number;
  nombre: string;
}

export interface RolRequest {
  nombre: string;
}

@Injectable({ providedIn: 'root' })
export class RolService {

  private readonly apiUrl = 'http://localhost:8080/api/roles';

  constructor(private http: HttpClient) {}

  listar(): Observable<Rol[]> {
    return this.http.get<Rol[]>(this.apiUrl);
  }

  crear(request: RolRequest): Observable<Rol> {
    return this.http.post<Rol>(this.apiUrl, request);
  }

  actualizar(id: number, request: RolRequest): Observable<Rol> {
    return this.http.put<Rol>(`${this.apiUrl}/${id}`, request);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
