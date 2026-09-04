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

  // Campos comunes
  nombreUsuario = '';
  rol = '';

  // Campos para correo/contraseña (Organización, Voluntario)
  correo = '';
  contrasena = '';

  // Solo Organización
  direccionOrganizacion = '';
  telefonoOrganizacion = '';

  // Solo Voluntario
  disponibilidad = '';

  // Campos para teléfono/OTP (Persona mayor, Acompañante)
  telefonoLocal = '';
  codigo = '';
  otpEnviado = signal(false);

  // Solo Acompañante
  //parentesco = '';

  // Solo Persona mayor
  fechaNacimiento = '';
  genero = '';
  direccionPersonaMayor = '';

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

  private readonly ROLES_POR_TELEFONO = ['PERSONA_MAYOR', 'ACOMPANANTE'];

  cargando = signal(false);
  errorMensaje = signal<string | null>(null);
  infoMensaje = signal<string | null>(null);

  constructor(private authService: AuthService) {}

  get esRegistroPorTelefono(): boolean {
    return this.ROLES_POR_TELEFONO.includes(this.rol);
  }

  get telefonoCompleto(): string {
    return `+57${this.telefonoLocal.replace(/\D/g, '')}`;
  }

  onCambioRol(): void {
    this.otpEnviado.set(false);
    this.codigo = '';
    this.errorMensaje.set(null);
    this.infoMensaje.set(null);
  }

  enviarCodigo(): void {
    this.errorMensaje.set(null);
    this.infoMensaje.set(null);

    if (this.telefonoLocal.trim().length < 10) {
      this.errorMensaje.set('Ingresa un número de celular válido (10 dígitos)');
      return;
    }

    this.cargando.set(true);

    this.authService.enviarOtp(this.telefonoCompleto).subscribe({
      next: () => {
        this.cargando.set(false);
        this.otpEnviado.set(true);
        this.infoMensaje.set('Código solicitado. Puede tardar unos segundos en llegar.');
      },
      error: () => {
        this.cargando.set(false);
        this.errorMensaje.set('No se pudo enviar el código. Verifica el número.');
      }
    });
  }

  onSubmit(): void {
    this.errorMensaje.set(null);

    if (this.esRegistroPorTelefono) {
      this.registrarPorTelefono();
    } else {
      this.registrarPorCorreo();
    }
  }

  private registrarPorCorreo(): void {
    this.cargando.set(true);

    this.authService.registro({
      nombreUsuario: this.nombreUsuario,
      correo: this.correo,
      contrasena: this.contrasena,
      rol: this.rol,
      direccion: this.rol === 'ORGANIZACION' ? this.direccionOrganizacion : undefined,
      telefono: this.rol === 'ORGANIZACION' ? this.telefonoOrganizacion : undefined,
      disponibilidad: this.rol === 'VOLUNTARIO' ? this.disponibilidad : undefined
    }).subscribe({
      next: (response) => {
        this.cargando.set(false);
        this.authService.redirigirSegunRol(response.rol);
      },
      error: (err) => {
        this.cargando.set(false);
        if (err.status === 409) {
          this.errorMensaje.set('Ese correo ya está registrado');
        } else if (err.status === 400) {
          this.errorMensaje.set('Revisa los datos ingresados');
        } else {
          this.errorMensaje.set('No se pudo conectar con el servidor. Intenta de nuevo.');
        }
      }
    });
  }

  private registrarPorTelefono(): void {
    if (!this.codigo.trim()) {
      this.errorMensaje.set('Ingresa el código que te llegó por SMS');
      return;
    }

    this.cargando.set(true);

    this.authService.registroOtp({
      telefono: this.telefonoCompleto,
      codigo: this.codigo,
      nombreUsuario: this.nombreUsuario,
      rol: this.rol,
      //parentesco: this.rol === 'ACOMPANANTE' ? this.parentesco : undefined,
      fechaNacimiento: this.rol === 'PERSONA_MAYOR' ? this.fechaNacimiento : undefined,
      genero: this.rol === 'PERSONA_MAYOR' ? this.genero : undefined,
      direccion: this.rol === 'PERSONA_MAYOR' ? this.direccionPersonaMayor : undefined
    }).subscribe({
      next: (response) => {
        this.cargando.set(false);
        this.authService.redirigirSegunRol(response.rol);
      },
      error: (err) => {
        this.cargando.set(false);
        if (err.status === 401) {
          this.errorMensaje.set('Código incorrecto o expirado');
        } else if (err.status === 409) {
          this.errorMensaje.set('Ese teléfono ya está registrado');
        } else {
          this.errorMensaje.set('No se pudo conectar con el servidor. Intenta de nuevo.');
        }
      }
    });
  }
}