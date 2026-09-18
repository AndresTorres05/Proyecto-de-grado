import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// =========================================================
// ACOMPAÑANTE
// =========================================================

export interface Acompanante {
  idUsuario: number;
  nombre: string;
  telefono: string;
  parentesco: string;
}

// =========================================================
// PERSONAS MAYORES DEL ACOMPAÑANTE
// =========================================================

export interface PersonaMayorAcompanada {
  idUsuario: number;
  nombre: string;
  telefono: string;
}

// =========================================================
// SOLICITUDES
// =========================================================

export interface SolicitudAcompanamiento {
  idUsuario: number;
  nombre: string;
  telefono: string;
}

// =========================================================
// PERFIL DEL ACOMPAÑANTE
// =========================================================

export interface AcompanantePerfil {
  idUsuario: number;
  nombre: string;
  telefono: string;
  correo: string | null;
  tieneContrasena: boolean;
}

export interface ActualizarAcompananteRequest {
  nombre: string;
  correo?: string;
}

export interface CambiarContrasenaRequest {
  contrasenaActual?: string;
  nuevaContrasena: string;
}

// =========================================================
// MEDICAMENTOS - SEGUIMIENTO
// =========================================================

export interface MedicamentoSeguimiento {
  idMedicamento: number;
  nombre: string;
  dosis: string | null;
  frecuencia: string | null;
  intervaloHoras: number;
  hora: string | null;
  fechaInicio: string | null;
  fechaFin: string | null;
  proximaToma: string | null;
  ultimaToma: string | null;
  activo: boolean;
}

// =========================================================
// CONTACTOS DE EMERGENCIA
// =========================================================

export interface ContactoEmergencia {
  idUsuario: number;
  nombre: string;
  telefono: string;
  parentesco: string;
}

// =========================================================
// ACTIVIDADES
// =========================================================

export interface Actividad {
  idActividad: number;
  idOrganizacion: number;
  nombre: string;
  fecha: string | null;
  lugar: string | null;
  tipo: string | null;
}

// =========================================================
// SERVICIO
// =========================================================

@Injectable({
  providedIn: 'root'
})
export class AcompananteService {

  private readonly apiUrl = 'http://localhost:8080/api';
private readonly authUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  // =========================================================
  // ACOMPAÑANTES DE UNA PERSONA MAYOR
  // =========================================================

  obtenerAcompanantes(): Observable<Acompanante[]> {
    return this.http.get<Acompanante[]>(
      `${this.apiUrl}/persona-mayor/acompanantes`
    );
  }

  agregarAcompanante(
    datos: {
      telefono: string;
      parentesco: string;
    }
  ): Observable<string> {
    return this.http.post(
      `${this.apiUrl}/persona-mayor/acompanantes`,
      datos,
      {
        responseType: 'text'
      }
    );
  }

  // =========================================================
  // PERSONAS MAYORES DEL ACOMPAÑANTE
  // =========================================================

  obtenerPersonasMayores(): Observable<PersonaMayorAcompanada[]> {
    return this.http.get<PersonaMayorAcompanada[]>(
      `${this.apiUrl}/acompanante/personas-mayores`
    );
  }

  // =========================================================
  // SOLICITUDES
  // =========================================================

  obtenerSolicitudes(): Observable<SolicitudAcompanamiento[]> {
    return this.http.get<SolicitudAcompanamiento[]>(
      `${this.apiUrl}/acompanante/personas-mayores/solicitudes`
    );
  }

  aceptarSolicitud(
    idPersonaMayor: number
  ): Observable<string> {
    return this.http.put(
      `${this.apiUrl}/acompanante/personas-mayores/solicitudes/${idPersonaMayor}/aceptar`,
      {},
      {
        responseType: 'text'
      }
    );
  }

  rechazarSolicitud(
    idPersonaMayor: number
  ): Observable<string> {
    return this.http.put(
      `${this.apiUrl}/acompanante/personas-mayores/solicitudes/${idPersonaMayor}/rechazar`,
      {},
      {
        responseType: 'text'
      }
    );
  }

  // =========================================================
  // INFORMACIÓN DEL ACOMPAÑANTE
  // =========================================================

  obtenerInformacion(): Observable<AcompanantePerfil> {
  return this.http.get<AcompanantePerfil>(
    `${this.authUrl}/informacion`
  );
}

actualizarInformacion(
  datos: ActualizarAcompananteRequest
): Observable<AcompanantePerfil> {
  return this.http.put<AcompanantePerfil>(
    `${this.authUrl}/informacion`,
    datos
  );
}

  // =========================================================
  // CONTRASEÑA
  // =========================================================

  cambiarContrasena(
  datos: CambiarContrasenaRequest
): Observable<string> {
  return this.http.put(
    `${this.authUrl}/contrasena`,
    datos,
    {
      responseType: 'text'
    }
  );
}

  // =========================================================
  // SEGUIMIENTO - MEDICAMENTOS
  // =========================================================

  obtenerMedicamentosSeguimiento(
    idPersonaMayor: number
  ): Observable<MedicamentoSeguimiento[]> {
    return this.http.get<MedicamentoSeguimiento[]>(
      `${this.apiUrl}/acompanante/seguimiento/${idPersonaMayor}/medicamentos`
    );
  }

  // =========================================================
  // CONTACTOS DE EMERGENCIA
  // =========================================================

  obtenerContactosEmergencia(
    idPersonaMayor: number
  ): Observable<ContactoEmergencia[]> {
    return this.http.get<ContactoEmergencia[]>(
      `${this.apiUrl}/acompanante/seguimiento/${idPersonaMayor}/contactos`
    );
  }

  // =========================================================
  // ACTIVIDADES - ACOMPAÑANTE
  // =========================================================

  obtenerActividades(): Observable<Actividad[]> {
    return this.http.get<Actividad[]>(
      `${this.apiUrl}/actividades`
    );
  }
}