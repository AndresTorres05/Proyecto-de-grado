import { Component, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe, registerLocaleData } from '@angular/common';
import localeEs from '@angular/common/locales/es-CO';
import { ActividadService, ActividadDisponible } from '../../../../core/actividades/actividad.service';
import { alCambiar } from '../../../../core/tiempo-real/tiempo-real.service';
import { Icon } from '../../../../shared/icon/icon';

registerLocaleData(localeEs);

@Component({
  selector: 'app-actividades',
  standalone: true,
  imports: [Icon],
  templateUrl: './actividades.html',
  styleUrl: './actividades.css'
})
export class Actividades implements OnInit {

  protected readonly actividades = signal<ActividadDisponible[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly procesandoId = signal<number | null>(null);

  constructor(private actividadService: ActividadService) {
    alCambiar(['actividades'], () => this.cargar(false));
  }

  ngOnInit(): void {
    this.cargar();
  }

  private cargar(mostrarCargando = true): void {
    if (mostrarCargando) {
      this.cargando.set(true);
    }
    this.error.set(null);

    this.actividadService.listarDisponibles().subscribe({
      next: (actividades) => {
        this.actividades.set(actividades);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar las actividades.');
        this.cargando.set(false);
      }
    });
  }

  inscribirse(actividad: ActividadDisponible): void {
    this.procesandoId.set(actividad.idActividad);

    this.actividadService.inscribirse(actividad.idActividad).subscribe({
      next: () => {
        this.procesandoId.set(null);
        this.cargar();
      },
      error: () => {
        this.procesandoId.set(null);
        this.error.set('No se pudo completar la inscripción.');
      }
    });
  }

  cancelarInscripcion(actividad: ActividadDisponible): void {
    this.procesandoId.set(actividad.idActividad);

    this.actividadService.cancelarInscripcion(actividad.idActividad).subscribe({
      next: () => {
        this.procesandoId.set(null);
        this.cargar();
      },
      error: () => {
        this.procesandoId.set(null);
        this.error.set('No se pudo cancelar la inscripción.');
      }
    });
  }
}