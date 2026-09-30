import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';

import {
  AcompananteService,
  PersonaMayorAcompanada,
  MedicamentoSeguimiento
} from '../../../../core/acompanantes/acompanante.service';
import {
  formatearHora,
  formatearProximaToma
} from '../../../../core/medicamentos/medicamento.service';
import { Icon } from '../../../../shared/icon/icon';
import { PersonCard } from '../../../../shared/person-card/person-card';
import { alCambiar } from '../../../../core/tiempo-real/tiempo-real.service';

@Component({
  selector: 'app-seguimiento',
  standalone: true,
  imports: [CommonModule, Icon, PersonCard],
  templateUrl: './seguimiento.html',
  styleUrl: './seguimiento.css'
})
export class Seguimiento implements OnInit {

  personasMayores: PersonaMayorAcompanada[] = [];
  personaSeleccionada: PersonaMayorAcompanada | null = null;

  medicamentos: MedicamentoSeguimiento[] = [];

  cargandoPersonas = true;
  cargandoMedicamentos = false;

  errorPersonas = '';
  errorMedicamentos = '';

  constructor(
    private acompananteService: AcompananteService,
    private cdr: ChangeDetectorRef
  ) {
    alCambiar(['acompanamientos', 'usuarios', 'medicamentos'], () => this.recargar());
  }

  ngOnInit(): void {
    this.cargarPersonasMayores();
  }

  // =========================================================
  // PERSONAS MAYORES
  // =========================================================

  cargarPersonasMayores(): void {
    this.cargandoPersonas = true;
    this.errorPersonas = '';

    this.acompananteService.obtenerPersonasMayores().subscribe({
      next: (data) => {
        this.personasMayores = data;
        this.cargandoPersonas = false;

        // Si solo existe una persona mayor,
        // la seleccionamos automáticamente.
        if (this.personasMayores.length === 1) {
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

  // Recarga por cambios de otros usuarios, sin perder la selección
  private recargar(): void {
    this.acompananteService.obtenerPersonasMayores().subscribe({
      next: (data) => {
        this.personasMayores = data;

        const idSeleccionada = this.personaSeleccionada?.idUsuario;
        this.personaSeleccionada =
          data.find((p) => p.idUsuario === idSeleccionada)
          ?? (data.length === 1 ? data[0] : null);

        if (this.personaSeleccionada) {
          this.cargarMedicamentos(this.personaSeleccionada.idUsuario, false);
        } else {
          this.medicamentos = [];
        }

        this.cdr.detectChanges();
      },
      error: (error) => console.error('Error al recargar personas mayores:', error)
    });
  }

  // =========================================================
  // SELECCIONAR PERSONA MAYOR
  // =========================================================

  seleccionarPersona(
    persona: PersonaMayorAcompanada
  ): void {
    this.personaSeleccionada = persona;
    this.medicamentos = [];

    this.cargarMedicamentos(persona.idUsuario);
  }

  // =========================================================
  // MEDICAMENTOS
  // =========================================================

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

  // =========================================================
  // PRÓXIMA TOMA
  // =========================================================

  obtenerProximaToma(
    medicamento: MedicamentoSeguimiento
  ): string {

    if (!medicamento.proximaToma) {
      return 'No programada';
    }

    return formatearProximaToma(medicamento.proximaToma);
  }

  obtenerPrimeraToma(
    medicamento: MedicamentoSeguimiento
  ): string {
    return formatearHora(medicamento.hora);
  }

  // =========================================================
  // ESTADO DEL MEDICAMENTO
  // =========================================================

  obtenerEstado(
    medicamento: MedicamentoSeguimiento
  ): string {

    return medicamento.activo
      ? 'Activo'
      : 'Inactivo';
  }
}