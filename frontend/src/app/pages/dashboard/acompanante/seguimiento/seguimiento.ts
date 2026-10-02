import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  AcompananteService,
  PersonaMayorAcompanada,
  MedicamentoSeguimiento
} from '../../../../core/acompanantes/acompanante.service';
import {
  formatearHora,
  formatearProximaToma
} from '../../../../core/medicamentos/medicamento.service';
import {
  CitaMedica,
  formatearFechaCita,
  separarCitas,
  tiempoParaCita
} from '../../../../core/citas-medicas/cita-medica.service';
import { Icon } from '../../../../shared/icon/icon';
import { alCambiar } from '../../../../core/tiempo-real/tiempo-real.service';

/** Citas pasadas que se muestran en el seguimiento (las más recientes). */
const CITAS_PASADAS_VISIBLES = 5;

/**
 * Seguimiento de medicamentos y citas médicas: el acompañante ve los
 * medicamentos de una de sus personas mayores con la próxima toma, y sus
 * citas próximas y recientes. Si acompaña a varias, la elige en un
 * selector (empieza con la primera).
 */
@Component({
  selector: 'app-seguimiento',
  standalone: true,
  imports: [CommonModule, FormsModule, Icon],
  templateUrl: './seguimiento.html',
  styleUrl: './seguimiento.css'
})
export class Seguimiento implements OnInit {

  personasMayores: PersonaMayorAcompanada[] = [];
  personaSeleccionada: PersonaMayorAcompanada | null = null;

  medicamentos: MedicamentoSeguimiento[] = [];

  citasProximas: CitaMedica[] = [];
  citasPasadas: CitaMedica[] = [];

  cargandoPersonas = true;
  cargandoMedicamentos = false;
  cargandoCitas = false;

  errorPersonas = '';
  errorMedicamentos = '';
  errorCitas = '';

  protected readonly formatearHora = formatearHora;

  constructor(
    private acompananteService: AcompananteService,
    private cdr: ChangeDetectorRef
  ) {
    alCambiar(['acompanamientos', 'usuarios', 'medicamentos', 'citas-medicas'], () => this.recargar());
  }

  ngOnInit(): void {
    this.cargarPersonasMayores();
  }

  cargarPersonasMayores(): void {
    this.cargandoPersonas = true;
    this.errorPersonas = '';

    this.acompananteService.obtenerPersonasMayores().subscribe({
      next: (data) => {
        this.personasMayores = data;
        this.cargandoPersonas = false;

        // Se empieza con la primera; si hay varias, se cambia en el selector.
        if (this.personasMayores.length > 0) {
          this.seleccionarPersona(this.personasMayores[0]);
        }

        this.cdr.detectChanges();
      },

      error: (error) => {
        console.error(
          'Error al cargar personas mayores:',
          error
        );

        this.cargandoPersonas = false;
        this.errorPersonas =
          'No se pudieron cargar las personas mayores asociadas.';

        this.cdr.detectChanges();
      }
    });
  }

  /** Recarga por cambios de otros usuarios sin perder la persona seleccionada. */
  private recargar(): void {
    this.acompananteService.obtenerPersonasMayores().subscribe({
      next: (data) => {
        this.personasMayores = data;

        const idSeleccionada = this.personaSeleccionada?.idUsuario;
        this.personaSeleccionada =
          data.find((p) => p.idUsuario === idSeleccionada)
          ?? data[0]
          ?? null;

        if (this.personaSeleccionada) {
          this.cargarMedicamentos(this.personaSeleccionada.idUsuario, false);
          this.cargarCitas(this.personaSeleccionada.idUsuario, false);
        } else {
          this.medicamentos = [];
          this.citasProximas = [];
          this.citasPasadas = [];
        }

        this.cdr.detectChanges();
      },
      error: (error) => console.error('Error al recargar personas mayores:', error)
    });
  }

  seleccionarPersona(
    persona: PersonaMayorAcompanada
  ): void {
    this.personaSeleccionada = persona;
    this.medicamentos = [];
    this.citasProximas = [];
    this.citasPasadas = [];

    this.cargarMedicamentos(persona.idUsuario);
    this.cargarCitas(persona.idUsuario);
  }

  seleccionarPorId(idPersonaMayor: number): void {
    const persona = this.personasMayores.find((p) => p.idUsuario === idPersonaMayor);

    if (persona) {
      this.seleccionarPersona(persona);
    }
  }

  /** Con mostrarCargando en false, la lista se actualiza sin parpadear (cambios en vivo). */
  cargarMedicamentos(
    idPersonaMayor: number,
    mostrarCargando = true
  ): void {
    if (mostrarCargando) {
      this.cargandoMedicamentos = true;
    }
    this.errorMedicamentos = '';

    this.acompananteService
      .obtenerMedicamentosSeguimiento(idPersonaMayor)
      .subscribe({
        next: (data) => {
          this.medicamentos = data;
          this.cargandoMedicamentos = false;

          this.cdr.detectChanges();
        },

        error: (error) => {
          console.error(
            'Error al cargar medicamentos:',
            error
          );

          this.cargandoMedicamentos = false;

          if (error.status === 403) {
            this.errorMedicamentos =
              'No tienes autorización para consultar esta información.';
          } else {
            this.errorMedicamentos =
              'No se pudieron cargar los medicamentos.';
          }

          this.cdr.detectChanges();
        }
      });
  }

  /** Citas próximas (todas) y las pasadas más recientes. */
  cargarCitas(
    idPersonaMayor: number,
    mostrarCargando = true
  ): void {
    if (mostrarCargando) {
      this.cargandoCitas = true;
    }
    this.errorCitas = '';

    this.acompananteService
      .obtenerCitasMedicasSeguimiento(idPersonaMayor)
      .subscribe({
        next: (data) => {
          const { proximas, pasadas } = separarCitas(data, new Date());
          this.citasProximas = proximas;
          this.citasPasadas = pasadas.slice(0, CITAS_PASADAS_VISIBLES);
          this.cargandoCitas = false;

          this.cdr.detectChanges();
        },

        error: (error) => {
          console.error('Error al cargar citas médicas:', error);

          this.cargandoCitas = false;
          this.errorCitas = error.status === 403
            ? 'No tienes autorización para consultar esta información.'
            : 'No se pudieron cargar las citas médicas.';

          this.cdr.detectChanges();
        }
      });
  }

  fechaDeCita(cita: CitaMedica): string {
    return formatearFechaCita(cita.fecha);
  }

  faltaParaCita(cita: CitaMedica): string | null {
    return tiempoParaCita(cita);
  }

  obtenerProximaToma(
    medicamento: MedicamentoSeguimiento
  ): string {

    if (!medicamento.proximaToma) {
      return 'No programada';
    }

    return formatearProximaToma(medicamento.proximaToma);
  }

  /** Hora de referencia del medicamento (la de la primera toma). */
  obtenerPrimeraToma(
    medicamento: MedicamentoSeguimiento
  ): string {
    return formatearHora(medicamento.hora);
  }

  obtenerEstado(
    medicamento: MedicamentoSeguimiento
  ): string {

    return medicamento.activo
      ? 'Activo'
      : 'Inactivo';
  }
}