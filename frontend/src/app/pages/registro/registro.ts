import { Component, OnDestroy, signal } from '@angular/core';
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
export class Registro implements OnDestroy {

  // =========================================================
  // CAMPOS COMUNES
  // =========================================================

  nombreUsuario = '';
  rol = '';

  // =========================================================
  // CORREO Y CONTRASEÑA
  // =========================================================

  correo = '';
  contrasena = '';

  // =========================================================
  // SOLO ORGANIZACIÓN
  // =========================================================

  direccionOrganizacion = '';
  telefonoOrganizacion = '';

  // =========================================================
  // SOLO VOLUNTARIO
  // =========================================================

  disponibilidad = '';

  // =========================================================
  // TELÉFONO / OTP
  // PERSONA MAYOR Y ACOMPAÑANTE
  // =========================================================

  telefonoLocal = '';
  codigo = '';

  otpEnviado = signal(false);

  // =========================================================
  // SOLO PERSONA MAYOR
  // =========================================================

  fechaNacimiento = '';
  genero = '';
  direccionPersonaMayor = '';

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

  private readonly ROLES_POR_TELEFONO = [
    'PERSONA_MAYOR',
    'ACOMPANANTE'
  ];

  // =========================================================
  // ESTADOS
  // =========================================================

  cargando = signal(false);
  errorMensaje = signal<string | null>(null);
  infoMensaje = signal<string | null>(null);

  // =========================================================
  // TEMPORIZADOR OTP
  // =========================================================

  segundosReenvio = signal(0);

  private intervaloReenvio?: ReturnType<typeof setInterval>;

  constructor(private authService: AuthService) {}

  // =========================================================
  // ¿ES PERSONA MAYOR O ACOMPAÑANTE?
  // =========================================================

  get esRegistroPorTelefono(): boolean {
    return this.ROLES_POR_TELEFONO.includes(this.rol);
  }

  // =========================================================
  // TELÉFONO COMPLETO
  // =========================================================

  get telefonoCompleto(): string {

    const telefono =
      this.telefonoLocal.replace(/\D/g, '');

    return `+57${telefono}`;
  }

  // =========================================================
  // CAMBIO DE ROL
  // =========================================================

  onCambioRol(): void {

    this.otpEnviado.set(false);

    this.codigo = '';

    this.errorMensaje.set(null);

    this.infoMensaje.set(null);

    this.segundosReenvio.set(0);

    if (this.intervaloReenvio) {

      clearInterval(this.intervaloReenvio);

      this.intervaloReenvio = undefined;
    }

  }

  // =========================================================
  // ENVIAR / REENVIAR OTP
  // =========================================================

  enviarCodigo(): void {

    this.errorMensaje.set(null);
    this.infoMensaje.set(null);

    const telefono =
      this.telefonoLocal.replace(/\D/g, '');

    if (telefono.length !== 10) {

      this.errorMensaje.set(
        'Ingresa un número de celular válido (10 dígitos)'
      );

      return;
    }

    if (this.segundosReenvio() > 0) {
      return;
    }

    this.cargando.set(true);

    this.authService
      .enviarOtp(this.telefonoCompleto)
      .subscribe({

        next: () => {

          this.cargando.set(false);

          this.otpEnviado.set(true);

          this.iniciarTemporizador();

          this.infoMensaje.set(
            'Código solicitado. Puede tardar unos segundos en llegar.'
          );
        },

        error: () => {

          this.cargando.set(false);

          this.errorMensaje.set(
            'No se pudo enviar el código. Verifica el número.'
          );
        }

      });
  }

  // =========================================================
  // TEMPORIZADOR
  // =========================================================

  private iniciarTemporizador(): void {

    this.segundosReenvio.set(30);

    if (this.intervaloReenvio) {

      clearInterval(this.intervaloReenvio);
    }

    this.intervaloReenvio = setInterval(() => {

      const actual = this.segundosReenvio();

      if (actual <= 1) {

        this.segundosReenvio.set(0);

        if (this.intervaloReenvio) {

          clearInterval(this.intervaloReenvio);

          this.intervaloReenvio = undefined;
        }

      } else {

        this.segundosReenvio.set(actual - 1);
      }

    }, 1000);
  }

  // =========================================================
  // SUBMIT PRINCIPAL
  // =========================================================

  onSubmit(): void {

    this.errorMensaje.set(null);

    // Todos los roles se registran con correo + contraseña
    this.registrarConContrasena();

    
}

  // =========================================================
  // REGISTRO DIRECTO CON CORREO + CONTRASEÑA
  // =========================================================

  private registrarConContrasena(): void {

    if (!this.correo.trim()) {

      this.errorMensaje.set(
        'El correo es obligatorio para crear una cuenta con contraseña.'
      );

      return;
    }

    if (!this.contrasena.trim()) {

      this.errorMensaje.set(
        'La contraseña es obligatoria.'
      );

      return;
    }

    if (this.contrasena.trim().length < 6) {

      this.errorMensaje.set(
        'La contraseña debe tener mínimo 6 caracteres.'
      );

      return;
    }

    // Teléfono obligatorio para Persona Mayor/Acompañante
    if (this.esRegistroPorTelefono) {

      const telefono =
        this.telefonoLocal.replace(/\D/g, '');

      if (telefono.length !== 10) {

        this.errorMensaje.set(
          'Ingresa un número de celular válido (10 dígitos).'
        );

        return;
      }
    }

    this.cargando.set(true);

    this.authService.registro({

      nombreUsuario: this.nombreUsuario,

      correo: this.correo.trim(),

      contrasena: this.contrasena,

      rol: this.rol,

      // Persona Mayor / Organización
      direccion:
        this.rol === 'PERSONA_MAYOR'
          ? this.direccionPersonaMayor
          : this.rol === 'ORGANIZACION'
            ? this.direccionOrganizacion
            : undefined,

      // Teléfono de Persona Mayor/Acompañante
      // o teléfono de contacto de Organización
      telefono:
        this.esRegistroPorTelefono
          ? this.telefonoCompleto
          : this.rol === 'ORGANIZACION'
            ? this.telefonoOrganizacion
            : undefined,

      disponibilidad:
        this.rol === 'VOLUNTARIO'
          ? this.disponibilidad
          : undefined,

      fechaNacimiento:
        this.rol === 'PERSONA_MAYOR'
          ? this.fechaNacimiento
          : undefined,

      genero:
        this.rol === 'PERSONA_MAYOR'
          ? this.genero
          : undefined

    }).subscribe({

      next: (response) => {

        this.cargando.set(false);

        this.authService.redirigirSegunRol(
          response.rol
        );
      },

      error: (err) => {

        this.cargando.set(false);

        if (err.status === 409) {

          if (
            err.error?.mensaje ===
            'Ese teléfono ya está registrado'
          ) {

            this.errorMensaje.set(
              'Ese teléfono ya está registrado.'
            );

          } else {

            this.errorMensaje.set(
              'Ese correo ya está registrado.'
            );
          }

        } else if (err.status === 400) {

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

  // =========================================================
  // REGISTRO MEDIANTE OTP
  // =========================================================

  private registrarPorTelefono(): void {

    if (!this.codigo.trim()) {

      this.errorMensaje.set(
        'Ingresa el código que te llegó por SMS.'
      );

      return;
    }

    this.cargando.set(true);

    this.authService.registroOtp({

      telefono: this.telefonoCompleto,

      codigo: this.codigo,

      nombreUsuario: this.nombreUsuario,

      rol: this.rol,

      // Correo opcional para ambos
      correo:
        this.correo.trim()
          ? this.correo.trim()
          : undefined,

      // Solo Persona Mayor
      fechaNacimiento:
        this.rol === 'PERSONA_MAYOR'
          ? this.fechaNacimiento
          : undefined,

      genero:
        this.rol === 'PERSONA_MAYOR'
          ? this.genero
          : undefined,

      direccion:
        this.rol === 'PERSONA_MAYOR'
          ? this.direccionPersonaMayor
          : undefined

    }).subscribe({

      next: (response) => {

        this.cargando.set(false);

        this.authService.redirigirSegunRol(
          response.rol
        );
      },

      error: (err) => {

        this.cargando.set(false);

        if (err.status === 401) {

          this.errorMensaje.set(
            'Código incorrecto o expirado.'
          );

        } else if (err.status === 409) {

          if (
            err.error?.mensaje ===
            'Ese correo ya está registrado'
          ) {

            this.errorMensaje.set(
              'Ese correo ya está registrado.'
            );

          } else {

            this.errorMensaje.set(
              'Ese teléfono ya está registrado.'
            );
          }

        } else {

          this.errorMensaje.set(
            'No se pudo conectar con el servidor. Intenta de nuevo.'
          );
        }
      }

    });
  }

  // =========================================================
  // LIMPIAR TEMPORIZADOR
  // =========================================================

  ngOnDestroy(): void {

    if (this.intervaloReenvio) {

      clearInterval(this.intervaloReenvio);

      this.intervaloReenvio = undefined;
    }
  }
}