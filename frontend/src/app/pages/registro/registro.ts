import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './registro.html',
  styleUrl: './registro.css'
})
export class Registro {

  // =========================================================
  // CAMPOS COMUNES
  // =========================================================

  nombreUsuario = '';
  rol = '';
  aceptaTerminos = false;
  mostrarTerminos = false;

  // =========================================================
  // CORREO Y CONTRASEÑA
  // =========================================================

  correo = '';
  contrasena = '';

// =========================================================
// DATOS PERSONALES
// =========================================================

celularLocal = '';
fechaNacimiento = '';
genero = '';
direccion = '';

// =========================================================
// CONFIRMACIÓN DE CONTRASEÑA
// =========================================================

confirmarContrasena = '';

  // =========================================================
  // ROLES
  // =========================================================

  roles = [
    { valor: 'ORGANIZACION', etiqueta: 'Organización' },
    { valor: 'VOLUNTARIO', etiqueta: 'Voluntario' },
    { valor: 'ACOMPANANTE', etiqueta: 'Acompañante' },
    { valor: 'PERSONA_MAYOR', etiqueta: 'Persona mayor' }
  ];

  generos = [
    { valor: 'FEMENINO', etiqueta: 'Femenino' },
    { valor: 'MASCULINO', etiqueta: 'Masculino' },
    { valor: 'OTRO', etiqueta: 'Otro' }
  ];

  // =========================================================
  // ESTADOS
  // =========================================================

  cargando = signal(false);
  errorMensaje = signal<string | null>(null);
  infoMensaje = signal<string | null>(null);



  constructor(private authService: AuthService) {}

  // =========================================================
  // CELULAR COMPLETO
  // =========================================================

  get celularCompleto(): string {

    const celular =
      this.celularLocal.replace(/\D/g, '');

    return `+57${celular}`;
  }

  // =========================================================
  // CAMBIO DE ROL
  // =========================================================

onCambioRol(): void {
  this.errorMensaje.set(null);
  this.infoMensaje.set(null);

  this.correo = '';
  this.contrasena = '';
  this.confirmarContrasena = '';
  this.celularLocal = '';
  this.fechaNacimiento = '';
  this.genero = '';
  this.direccion = '';
}

  abrirTerminos(): void {
  this.mostrarTerminos = true;
}

cerrarTerminos(): void {
  this.mostrarTerminos = false;
}

  // =========================================================
  // SUBMIT PRINCIPAL
  // =========================================================

onSubmit(): void {
  this.errorMensaje.set(null);

  if (!this.aceptaTerminos) {
    this.errorMensaje.set(
      'Debes aceptar los términos y condiciones para crear tu cuenta.'
    );
    return;
  }

  this.registrar();
}

// =========================================================
// REGISTRO
// =========================================================

private registrar(): void {

  const celular = this.celularLocal.replace(/\D/g, '');
  const correo = this.correo.trim();
  const contrasena = this.contrasena.trim();
  const confirmarContrasena = this.confirmarContrasena.trim();

  // Celular obligatorio para todos los perfiles
  if (celular.length !== 10) {
    this.errorMensaje.set(
      'Ingresa un número de celular válido (10 dígitos).'
    );
    return;
  }

  // Correo y contraseña deben ir juntos
  if (correo && !contrasena) {
    this.errorMensaje.set(
      'Si ingresas un correo, debes ingresar una contraseña.'
    );
    return;
  }

  if (!correo && contrasena) {
    this.errorMensaje.set(
      'Si ingresas una contraseña, debes ingresar un correo.'
    );
    return;
  }

  // Validar contraseña
  if (contrasena && contrasena.length < 6) {
    this.errorMensaje.set(
      'La contraseña debe tener mínimo 6 caracteres.'
    );
    return;
  }

  // Confirmación de contraseña
  if (contrasena && !confirmarContrasena) {
    this.errorMensaje.set(
      'Debes confirmar la contraseña.'
    );
    return;
  }

  if (contrasena && contrasena !== confirmarContrasena) {
    this.errorMensaje.set(
      'Las contraseñas no coinciden.'
    );
    return;
  }

  // Datos obligatorios para perfiles personales
  if (
    this.rol === 'PERSONA_MAYOR' ||
    this.rol === 'ACOMPANANTE' ||
    this.rol === 'VOLUNTARIO'
  ) {

    if (!this.fechaNacimiento) {
      this.errorMensaje.set(
        'La fecha de nacimiento es obligatoria.'
      );
      return;
    }

    if (!this.genero) {
      this.errorMensaje.set(
        'El género es obligatorio.'
      );
      return;
    }

    if (!this.direccion.trim()) {
      this.errorMensaje.set(
        'La dirección es obligatoria.'
      );
      return;
    }
  }

  // Dirección obligatoria para organización
  if (
    this.rol === 'ORGANIZACION' &&
    !this.direccion.trim()
  ) {
    this.errorMensaje.set(
      'La dirección de la organización es obligatoria.'
    );
    return;
  }

  this.cargando.set(true);

  this.authService.registro({

    nombreUsuario: this.nombreUsuario.trim(),

    correo: correo || undefined,

    contrasena: contrasena || undefined,

    rol: this.rol,

    celular: this.celularCompleto,

    fechaNacimiento:
      this.rol === 'PERSONA_MAYOR' ||
      this.rol === 'ACOMPANANTE' ||
      this.rol === 'VOLUNTARIO'
        ? this.fechaNacimiento
        : undefined,

    genero:
      this.rol === 'PERSONA_MAYOR' ||
      this.rol === 'ACOMPANANTE' ||
      this.rol === 'VOLUNTARIO'
        ? this.genero
        : undefined,

    direccion: this.direccion.trim()

  }).subscribe({

    next: (response) => {

      this.cargando.set(false);

      this.authService.redirigirSegunRol(
        response.rol
      );
    },

    error: (err) => {

      this.cargando.set(false);

      if (err.status === 400 || err.status === 409) {

        this.errorMensaje.set(
          err.error?.mensaje ||
          'Revisa los datos ingresados.'
        );

      } else {

        this.errorMensaje.set(
          'No se pudo conectar con el servidor. Intenta de nuevo.'
        );
      }
    }

  });
}
}