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

  // Copia que se edita en el modal; "informacion" solo cambia
  // cuando el backend confirma el guardado.
  formulario: OrganizacionResponse | null = null;

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

  abrirEdicion(): void {

    if (!this.informacion) return;

    this.formulario = { ...this.informacion };
    this.editando = true;
  }

  guardarCambios(): void {

    if (!this.formulario) {
      return;
    }

    this.mostrandoConfirmacion = true;
  }

  // ==========================================
  // CONFIRMAR GUARDADO
  // ==========================================

  confirmarGuardado(): void {

    if (!this.formulario) {
      return;
    }

    this.organizacionService
      .actualizarInformacion(this.formulario)
      .subscribe({

        next: (data) => {

          this.informacion = data;
          this.formulario = null;

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
    this.formulario = null;
  }
}