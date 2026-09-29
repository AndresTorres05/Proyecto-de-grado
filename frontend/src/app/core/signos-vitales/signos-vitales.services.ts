import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface SignoVitalRequest {
  presionSistolica: number | null;
  presionDiastolica: number | null;
  frecuenciaCardiaca: number | null;
  temperatura: number | null;
  saturacionOxigeno: number | null;
  frecuenciaRespiratoria: number | null;
  peso: number | null;
  observaciones: string;
}

export interface SignoVitalResponse {
  idSignoVital: number;
  fechaHora: string;
  presionSistolica: number | null;
  presionDiastolica: number | null;
  frecuenciaCardiaca: number | null;
  temperatura: number | null;
  saturacionOxigeno: number | null;
  frecuenciaRespiratoria: number | null;
  peso: number | null;
  observaciones: string | null;
}

export interface PersonaMayor {
  idUsuario: number;
  nombre: string;
  celular: string | null;
  correo: string | null;
}

// Todo pasa por el api-gateway (8080): valida el token, pone el X-User-Id
// de la organización y es el único que maneja CORS.
@Injectable({
  providedIn: 'root',
})
export class SignosVitalesService {
  private http = inject(HttpClient);

  private apiUrl = 'http://localhost:8080/api/organizacion/signos-vitales';
  private personasApiUrl = 'http://localhost:8080/api/organizacion/personas-mayores';

  registrar(idPersonaMayor: number, datos: SignoVitalRequest): Observable<SignoVitalResponse> {
    return this.http.post<SignoVitalResponse>(`${this.apiUrl}/${idPersonaMayor}`, datos);
  }

  /** Últimos 10 registros de la persona mayor, del más reciente al más antiguo. */
  listarUltimos(idPersonaMayor: number): Observable<SignoVitalResponse[]> {
    return this.http.get<SignoVitalResponse[]>(`${this.apiUrl}/${idPersonaMayor}`);
  }

  listarPersonasMayores(): Observable<PersonaMayor[]> {
    return this.http.get<PersonaMayor[]>(this.personasApiUrl);
  }
}
