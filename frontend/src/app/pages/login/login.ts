import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

type ModoLogin = 'correo' | 'telefono';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  modo = signal<ModoLogin>('correo');

  // Correo/contraseña
  correo = '';
  contrasena = '';

  // Teléfono/OTP
  telefonoLocal = '';
  codigo = '';
  otpEnviado = signal(false);

  cargando = signal(false);
  errorMensaje = signal<string | null>(null);
  infoMensaje = signal<string | null>(null);

  constructor(private authService: AuthService) {}

  get telefonoCompleto(): string {
    return `+57${this.telefonoLocal.replace(/\D/g, '')}`;
  }

  cambiarModo(modo: ModoLogin): void {
    this.modo.set(modo);
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
        this.infoMensaje.set('Te enviamos un código por SMS. Revisa tu celular.');
      },
      error: () => {
        this.cargando.set(false);
        this.errorMensaje.set('No se pudo enviar el código. Verifica el número.');
      }
    });
  }

  onSubmit(): void {
    this.errorMensaje.set(null);

    if (this.modo() === 'correo') {
      this.loginPorCorreo();
    } else {
      this.loginPorTelefono();
    }
  }

  private loginPorCorreo(): void {
    this.cargando.set(true);

    this.authService.login({ correo: this.correo, contrasena: this.contrasena }).subscribe({
      next: (response) => {
        this.cargando.set(false);
        this.authService.redirigirSegunRol(response.rol);
      },
      error: (err) => {
        this.cargando.set(false);
        if (err.status === 401) {
          this.errorMensaje.set('Correo o contraseña incorrectos');
        } else {
          this.errorMensaje.set('No se pudo conectar con el servidor. Intenta de nuevo.');
        }
      }
    });
  }

  private loginPorTelefono(): void {
    if (!this.codigo.trim()) {
      this.errorMensaje.set('Ingresa el código que te llegó por SMS');
      return;
    }

    this.cargando.set(true);

    this.authService.loginOtp({ telefono: this.telefonoCompleto, codigo: this.codigo }).subscribe({
      next: (response) => {
        this.cargando.set(false);
        this.authService.redirigirSegunRol(response.rol);
      },
      error: (err) => {
        this.cargando.set(false);
        if (err.status === 401) {
          this.errorMensaje.set('Código incorrecto o expirado');
        } else if (err.status === 404) {
          this.errorMensaje.set('No existe una cuenta con ese teléfono');
        } else {
          this.errorMensaje.set('No se pudo conectar con el servidor. Intenta de nuevo.');
        }
      }
    });
  }
}