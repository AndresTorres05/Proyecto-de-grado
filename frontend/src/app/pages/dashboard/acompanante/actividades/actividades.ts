import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';

import {
  ActividadService,
  Actividad
} from '../../../../core/actividades/actividad.service';
import { alCambiar } from '../../../../core/tiempo-real/tiempo-real.service';

@Component({
  selector: 'app-actividades-acompanante',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './actividades.html',
  styleUrl: './actividades.css'
})
export class ActividadesComponent implements OnInit {

  actividades: Actividad[] = [];
  cargando = true;
  error = '';

  constructor(
    private actividadService: ActividadService,
    private cdr: ChangeDetectorRef
  ) {
    alCambiar(['actividades'], () => this.cargarActividades(false));
  }

  ngOnInit(): void {
    console.log('🚀 ACTIVIDADES COMPONENTE CREADO');
    this.cargarActividades();
  }

  cargarActividades(mostrarCargando = true): void {
    console.log('🟡 1. Entrando a cargarActividades');

    if (mostrarCargando) {
      this.cargando = true;
    }
    this.error = '';

    this.actividadService.listar().subscribe({
      next: (actividades) => {

        console.log('🔵 2. RESPUESTA RECIBIDA:', actividades);

        this.actividades = actividades;
        this.cargando = false;

        console.log('🟢 Estado actualizado:', {
          cantidad: this.actividades.length,
          cargando: this.cargando
        });

        // Forzar actualización de la vista
        this.cdr.detectChanges();
      },

      error: (error) => {
        console.error('🔴 ERROR:', error);

        this.error = 'No se pudieron cargar las actividades.';
        this.cargando = false;

        this.cdr.detectChanges();
      }
    });
  }

  formatearFecha(fecha: string | null): string {
    if (!fecha) return 'Fecha no disponible';

    const fechaObj = new Date(fecha + 'T00:00:00');

    return fechaObj.toLocaleDateString('es-CO', {
      day: '2-digit',
      month: 'long',
      year: 'numeric'
    });
  }
}