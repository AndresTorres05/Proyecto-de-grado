import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';

import {
  OrganizacionService,
  OrganizacionResponse
} from '../../../../core/organizacion/organizacion.service';

@Component({
  selector: 'app-administracion',
  imports: [FormsModule],
  templateUrl: './administracion.html',
  styleUrl: './administracion.css'
})
export class Administracion implements OnInit {

  informacion: OrganizacionResponse | null = null;

  cargando = true;
  editando = false;
  mostrandoConfirmacion = false;

  constructor(
    private organizacionService: OrganizacionService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarInformacion();
  }

  // ==========================================
  // CARGAR INFORMACIÓN
  // ==========================================

  cargarInformacion(): void {

    this.informacion = null;
    this.cargando = true;

    this.organizacionService.obtenerInformacion().subscribe({

      next: (data) => {

        this.informacion = data;
        this.cargando = false;

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error(
          'Error al cargar la información de la organización:',
          error
        );

        this.cargando = false;

        this.cdr.detectChanges();
      }

    });
  }

  // ==========================================
  // INICIAR GUARDADO
  // ==========================================

  guardarCambios(): void {

    if (!this.informacion) {
      return;
    }

    this.mostrandoConfirmacion = true;
  }

  // ==========================================
  // CONFIRMAR GUARDADO
  // ==========================================

  confirmarGuardado(): void {

    if (!this.informacion) {
      return;
    }

    this.organizacionService
      .actualizarInformacion(this.informacion)
      .subscribe({

        next: (data) => {

          this.informacion = data;

          this.editando = false;
          this.mostrandoConfirmacion = false;

          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(
            'Error al actualizar la información de la organización:',
            error
          );

          this.mostrandoConfirmacion = false;
        }

      });
  }

  // ==========================================
  // CANCELAR CONFIRMACIÓN
  // ==========================================

  cancelarGuardado(): void {

    this.mostrandoConfirmacion = false;
  }

  // ==========================================
  // CANCELAR EDICIÓN
  // ==========================================

  cancelarEdicion(): void {

    this.editando = false;

    this.cargarInformacion();
  }
}