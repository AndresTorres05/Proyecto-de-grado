import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
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
  idPersonaMayor: number;
  nombreUsuario: string;
  celular: string | null;
  correo: string | null;
}

@Injectable({
  providedIn: 'root',
})
export class SignosVitalesService {
  private http = inject(HttpClient);

  private apiUrl = 'http://localhost:8084/api/organizacion/signos-vitales';

  registrar(
    idPersonaMayor: number,
    datos: SignoVitalRequest,
    idUsuarioOrganizacion: number,
  ): Observable<SignoVitalResponse> {
    const headers = new HttpHeaders({
      'X-User-Id': idUsuarioOrganizacion.toString(),
    });

    return this.http.post<SignoVitalResponse>(`${this.apiUrl}/${idPersonaMayor}`, datos, {
      headers,
    });
  }
  private personasApiUrl = 'http://localhost:8085/api/organizacion/personas-mayores';

  listarPersonasMayores(idUsuarioOrganizacion: number): Observable<PersonaMayor[]> {
    const headers = new HttpHeaders({
      'X-User-Id': idUsuarioOrganizacion.toString(),
    });

    return this.http.get<PersonaMayor[]>(this.personasApiUrl, { headers });
  }
}
