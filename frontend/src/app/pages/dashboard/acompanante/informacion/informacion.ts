import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { EliminarCuenta } from '../../../../shared/eliminar-cuenta/eliminar-cuenta';

import { AuthService } from '../../../../core/auth/auth.service';

import {
  AcompananteService,
  AcompanantePerfil
} from '../../../../core/acompanantes/acompanante.service';

@Component({
  selector: 'app-informacion',
  imports: [FormsModule, EliminarCuenta],
  templateUrl: './informacion.html',
  styleUrl: './informacion.css'
})
export class Informacion implements OnInit {

  informacion: AcompanantePerfil | null = null;

  // Copia que se edita en el modal; "informacion" solo cambia
  // cuando el backend confirma el guardado.
  errorGuardado = '';

  formulario: AcompanantePerfil | null = null;

  cargando = true;
  editando = false;
  mostrandoConfirmacion = false;

  // =========================
  // CONTRASEÑA
  // =========================

  mostrandoContrasena = false;
  contrasenaActual = '';
  nuevaContrasena = '';
  confirmarContrasena = '';

  errorContrasena = '';
  mensajeContrasena = '';
  guardandoContrasena = false;

  constructor(
    private acompananteService: AcompananteService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarInformacion();
  }

  // =========================
  // CARGAR INFORMACIÓN
  // =========================

  cargarInformacion(): void {

    this.informacion = null;
    this.cargando = true;

    this.acompananteService.obtenerInformacion().subscribe({

      next: (data) => {

        this.informacion = data;
        this.cargando = false;

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error(
          'Error al cargar la información:',
          error
        );

        this.cargando = false;

        this.cdr.detectChanges();
      }

    });
  }

  // =========================
  // EDITAR INFORMACIÓN
  // =========================

  abrirEdicion(): void {

    if (!this.informacion) return;

    this.formulario = { ...this.informacion };
    this.errorGuardado = '';
    this.editando = true;
  }

  guardarCambios(): void {

    if (!this.formulario) return;

    this.mostrandoConfirmacion = true;
  }

  confirmarGuardado(): void {

    if (!this.formulario) return;

    this.acompananteService
      .actualizarInformacion({
        nombre: this.formulario.nombre,
        correo: this.formulario.correo || undefined
      })
      .subscribe({

        next: (data) => {

          this.informacion = data;
          this.formulario = null;
          this.authService.actualizarNombreUsuario(data.nombre);
          this.editando = false;
          this.mostrandoConfirmacion = false;

          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(
            'Error al actualizar la información:',
            error
          );

          // El backend responde el motivo en texto (p. ej. correo inválido)
          this.errorGuardado = typeof error.error === 'string' && error.error
            ? error.error
            : 'No se pudo guardar la información. Intenta de nuevo.';

          this.mostrandoConfirmacion = false;
          this.cdr.detectChanges();
        }

      });
  }

  cancelarGuardado(): void {
    this.mostrandoConfirmacion = false;
  }

  cancelarEdicion(): void {

    this.editando = false;
    this.formulario = null;
  }

  // =========================
  // CONTRASEÑA
  // =========================

  abrirModalContrasena(): void {

    this.contrasenaActual = '';
    this.nuevaContrasena = '';
    this.confirmarContrasena = '';

    this.errorContrasena = '';
    this.mensajeContrasena = '';

    this.mostrandoContrasena = true;
  }

  cerrarModalContrasena(): void {

    if (this.guardandoContrasena) return;

    this.mostrandoContrasena = false;

    this.contrasenaActual = '';
    this.nuevaContrasena = '';
    this.confirmarContrasena = '';

    this.errorContrasena = '';
    this.mensajeContrasena = '';
  }

  guardarContrasena(): void {

    this.errorContrasena = '';
    this.mensajeContrasena = '';

    if (!this.informacion) return;

    // Si ya tiene contraseña, debe ingresar la actual
    if (this.informacion.tieneContrasena) {

      if (!this.contrasenaActual.trim()) {

        this.errorContrasena =
          'Ingresa tu contraseña actual.';

        return;
      }
    }

    // Nueva contraseña
    if (!this.nuevaContrasena.trim()) {

      this.errorContrasena =
        'Ingresa una nueva contraseña.';

      return;
    }

    // Mínimo 6 caracteres
    if (this.nuevaContrasena.length < 6) {

      this.errorContrasena =
        'La contraseña debe tener mínimo 6 caracteres.';

      return;
    }

    // Confirmación
    if (
      this.nuevaContrasena !==
      this.confirmarContrasena
    ) {

      this.errorContrasena =
        'Las contraseñas no coinciden.';

      return;
    }

    this.guardandoContrasena = true;

    this.acompananteService
      .cambiarContrasena({

        contrasenaActual:
          this.informacion.tieneContrasena
            ? this.contrasenaActual
            : undefined,

        nuevaContrasena:
          this.nuevaContrasena

      })
      .subscribe({

        next: () => {

          this.guardandoContrasena = false;

          if (this.informacion) {
            this.informacion.tieneContrasena = true;
          }

          this.mensajeContrasena =
            'Contraseña guardada correctamente.';

          this.contrasenaActual = '';
          this.nuevaContrasena = '';
          this.confirmarContrasena = '';

          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(
            'Error al cambiar contraseña:',
            error
          );

          this.guardandoContrasena = false;

          if (error.status === 401) {

            this.errorContrasena =
              'La contraseña actual es incorrecta.';

          } else if (error.error) {

            this.errorContrasena =
              typeof error.error === 'string'
                ? error.error
                : 'No se pudo actualizar la contraseña.';

          } else {

            this.errorContrasena =
              'No se pudo actualizar la contraseña.';
          }

          this.cdr.detectChanges();
        }

      });
  }
}