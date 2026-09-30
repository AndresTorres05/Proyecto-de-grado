import { Component, OnInit, ChangeDetectorRef } from '@angular/core';

import {
  ActividadService,
  Actividad,
  separarPorFecha
} from '../../../../core/actividades/actividad.service';
import { alCambiar } from '../../../../core/tiempo-real/tiempo-real.service';
import { ActividadCard } from '../../../../shared/actividad-card/actividad-card';
import { Icon } from '../../../../shared/icon/icon';

@Component({
  selector: 'app-actividades-acompanante',
  standalone: true,
  imports: [ActividadCard, Icon],
  templateUrl: './actividades.html',
  styleUrls: ['../../../../shared/actividad-card/actividades-pagina.css']
})
export class ActividadesComponent implements OnInit {

  // Próximas (de la más cercana a la más lejana) e historial (de la más reciente a la más antigua)
  proximas: Actividad[] = [];
  pasadas: Actividad[] = [];

  cargando = true;
  error = '';

  constructor(
    private actividadService: ActividadService,
    private cdr: ChangeDetectorRef
  ) {
    alCambiar(['actividades'], () => this.cargarActividades(false));
  }

  ngOnInit(): void {
    this.cargarActividades();
  }

  cargarActividades(mostrarCargando = true): void {
    if (mostrarCargando) {
      this.cargando = true;
    }
    this.error = '';

    this.actividadService.listar().subscribe({
      next: (actividades) => {
        const { proximas, pasadas } = separarPorFecha(actividades);
        this.proximas = proximas;
        this.pasadas = pasadas;
        this.cargando = false;

        this.cdr.detectChanges();
      },

      error: (error) => {
        console.error('Error al cargar actividades:', error);

        this.error = 'No se pudieron cargar las actividades.';
        this.cargando = false;

        this.cdr.detectChanges();
      }
    });
  }
}
