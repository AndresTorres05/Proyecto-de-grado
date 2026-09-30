import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import {
  AcompananteService,
  PersonaMayorAcompanada,
  SolicitudAcompanamiento
} from '../../../../core/acompanantes/acompanante.service';
import { SignoVitalResponse } from '../../../../core/signos-vitales/signos-vitales.services';
import { Icon } from '../../../../shared/icon/icon';
import { PersonCard } from '../../../../shared/person-card/person-card';
import { CancelarAsociacion } from '../../../../shared/cancelar-asociacion/cancelar-asociacion';
import { SignosVitalesModal } from '../../../../shared/signos-vitales-modal/signos-vitales-modal';
import { alCambiar } from '../../../../core/tiempo-real/tiempo-real.service';

@Component({
  selector: 'app-mis-personas-mayores',
  imports: [Icon, PersonCard, CancelarAsociacion, SignosVitalesModal],
  templateUrl: './mis-personas-mayores.html',
  styleUrl: './mis-personas-mayores.css'
})
export class MisPersonasMayores implements OnInit {

  personasMayores: PersonaMayorAcompanada[] = [];
  solicitudes: SolicitudAcompanamiento[] = [];

  cargando = true;
  mensaje = '';
  error = '';

  personaACancelar: PersonaMayorAcompanada | null = null;

  personaSignosVitales: PersonaMayorAcompanada | null = null;
  signosVitales: SignoVitalResponse[] = [];
  cargandoSignosVitales = false;
  errorSignosVitales: string | null = null;

  constructor(
    private acompananteService: AcompananteService,
    private cdr: ChangeDetectorRef
  ) {
    alCambiar(['acompanamientos', 'usuarios'], () => {
      this.cargarPersonasMayores();
      this.cargarSolicitudes();
    });

    // Si el modal de signos vitales está abierto, se actualiza en vivo
    alCambiar(['signos-vitales'], () => {
      if (this.personaSignosVitales) {
        this.cargarSignosVitales(this.personaSignosVitales.idUsuario);
      }
    });
  }

  ngOnInit(): void {
    this.cargarPersonasMayores();
    this.cargarSolicitudes();
  }

  cargarPersonasMayores(): void {
    this.acompananteService.obtenerPersonasMayores().subscribe({
      next: (data) => {
        this.personasMayores = data;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar personas mayores:', error);
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  cargarSolicitudes(): void {
    this.acompananteService.obtenerSolicitudes().subscribe({
      next: (data) => {
        this.solicitudes = data;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar solicitudes:', error);
      }
    });
  }

  aceptarSolicitud(idPersonaMayor: number): void {
    this.acompananteService.aceptarSolicitud(idPersonaMayor).subscribe({
      next: (respuesta) => {
        this.mensaje = respuesta;
        this.error = '';

        this.cargarSolicitudes();
        this.cargarPersonasMayores();
      },
      error: (error) => {
        console.error('Error al aceptar solicitud:', error);
        this.error = 'No fue posible aceptar la solicitud.';
        this.mensaje = '';
      }
    });
  }

  rechazarSolicitud(idPersonaMayor: number): void {
    this.acompananteService.rechazarSolicitud(idPersonaMayor).subscribe({
      next: (respuesta) => {
        this.mensaje = respuesta;
        this.error = '';

        this.cargarSolicitudes();
      },
      error: (error) => {
        console.error('Error al rechazar solicitud:', error);
        this.error = 'No fue posible rechazar la solicitud.';
        this.mensaje = '';
      }
    });
  }

  mostrarConfirmacionCancelacion(persona: PersonaMayorAcompanada): void {
    this.personaACancelar = persona;
    this.mensaje = '';
    this.error = '';
  }

  cerrarConfirmacionCancelacion(): void {
    this.personaACancelar = null;
  }

  confirmarCancelacion(): void {
    const persona = this.personaACancelar;

    if (!persona) return;

    this.personaACancelar = null;

    this.acompananteService.cancelarAsociacionPersonaMayor(persona.idUsuario).subscribe({
      next: (respuesta) => {
        this.mensaje = respuesta;
        this.cargarPersonasMayores();
      },
      error: (error) => {
        console.error('Error al cancelar la asociación:', error);
        this.error = error?.error || 'No se pudo cancelar la asociación.';
        this.cdr.detectChanges();
      }
    });
  }

  mostrarSignosVitales(persona: PersonaMayorAcompanada): void {
    this.personaSignosVitales = persona;
    this.signosVitales = [];
    this.errorSignosVitales = null;
    this.cargandoSignosVitales = true;

    this.cargarSignosVitales(persona.idUsuario);
  }

  private cargarSignosVitales(idPersonaMayor: number): void {
    this.acompananteService.obtenerSignosVitalesSeguimiento(idPersonaMayor).subscribe({
      next: (registros) => {
        this.signosVitales = registros;
        this.cargandoSignosVitales = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar signos vitales:', error);
        this.errorSignosVitales = 'No se pudieron cargar los signos vitales.';
        this.cargandoSignosVitales = false;
        this.cdr.detectChanges();
      }
    });
  }

  cerrarSignosVitales(): void {
    this.personaSignosVitales = null;
    this.signosVitales = [];
  }
}