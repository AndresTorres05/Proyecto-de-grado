import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Medicamento {
  idMedicamento: number;
  nombre: string;
  dosis: string;
  frecuencia: string;
  intervaloHoras: number;
  hora: string;
  fechaInicio: string;
  fechaFin: string | null;
  proximaToma: string;
  ultimaToma: string | null;
  activo: boolean;
}

export interface MedicamentoRequest {
  nombre: string;
  dosis: string;
  frecuencia: string;
  intervaloHoras: number;
  hora: string;
  fechaInicio?: string;
  fechaFin?: string;
}

@Injectable({ providedIn: 'root' })
export class MedicamentoService {

  private readonly apiUrl = 'http://localhost:8080/api/medicamentos';

  constructor(private http: HttpClient) {}

  listar(): Observable<Medicamento[]> {
    return this.http.get<Medicamento[]>(this.apiUrl);
  }

  crear(request: MedicamentoRequest): Observable<Medicamento> {
    return this.http.post<Medicamento>(this.apiUrl, request);
  }

  actualizar(id: number, request: MedicamentoRequest): Observable<Medicamento> {
    return this.http.put<Medicamento>(`${this.apiUrl}/${id}`, request);
  }

  confirmarToma(id: number): Observable<Medicamento> {
    return this.http.post<Medicamento>(`${this.apiUrl}/${id}/confirmar-toma`, {});
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}