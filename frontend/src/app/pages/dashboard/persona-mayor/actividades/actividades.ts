import { Component, OnInit, computed, signal } from '@angular/core';
import { CommonModule, DatePipe, registerLocaleData } from '@angular/common';
import localeEs from '@angular/common/locales/es-CO';
import {
  ActividadService,
  ActividadDisponible,
  separarPorFecha
} from '../../../../core/actividades/actividad.service';
import { alCambiar } from '../../../../core/tiempo-real/tiempo-real.service';
import { Icon } from '../../../../shared/icon/icon';
import { ActividadCard } from '../../../../shared/actividad-card/actividad-card';

registerLocaleData(localeEs);

@Component({
  selector: 'app-actividades',
  standalone: true,
  imports: [Icon, ActividadCard],
  templateUrl: './actividades.html',
  styleUrls: ['../../../../shared/actividad-card/actividades-pagina.css', './actividades.css']
})
export class Actividades implements OnInit {

  protected readonly actividades = signal<ActividadDisponible[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly procesandoId = signal<number | null>(null);

  // Próximas (de la más cercana a la más lejana) e historial (de la más reciente a la más antigua)
  protected readonly separadas = computed(() => separarPorFecha(this.actividades()));

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