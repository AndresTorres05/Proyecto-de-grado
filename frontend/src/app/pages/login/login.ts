import {
  Component,
  signal,
  OnDestroy
} from '@angular/core';

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
export class Login implements OnDestroy {

  modo = signal<ModoLogin>('correo');

  // =========================================================
  // CORREO / CONTRASEÑA
  // =========================================================

  correo = '';
  contrasena = '';


  // =========================================================
  // TELÉFONO / OTP
  // =========================================================

  telefonoLocal = '';
  codigo = '';

  otpEnviado = signal(false);

  // Contador para reenviar código
  segundosReenvio = signal(0);

  private intervaloReenvio: ReturnType<typeof setInterval> | null = null;


  // =========================================================
  // ESTADO
  // =========================================================

  cargando = signal(false);

  errorMensaje = signal<string | null>(null);

  infoMensaje = signal<string | null>(null);


  constructor(
    private authService: AuthService
  ) {}


  // =========================================================
  // TELÉFONO COMPLETO
  // =========================================================

  get telefonoCompleto(): string {

    return `+57${this.telefonoLocal.replace(/\D/g, '')}`;
  }


  // =========================================================
  // CAMBIAR MODO
  // =========================================================

  cambiarModo(modo: ModoLogin): void {

    this.modo.set(modo);

    this.otpEnviado.set(false);

    this.codigo = '';

    this.errorMensaje.set(null);

    this.infoMensaje.set(null);

    this.detenerContador();

    this.segundosReenvio.set(0);
  }


  // =========================================================
  // ENVIAR / REENVIAR CÓDIGO
  // =========================================================

  enviarCodigo(): void {

    this.errorMensaje.set(null);

    this.infoMensaje.set(null);


    // ---------------------------------------------------------
    // Comprobar si todavía está bloqueado el reenvío
    // ---------------------------------------------------------

    if (this.segundosReenvio() > 0) {

      return;
    }


    // ---------------------------------------------------------
    // Validar teléfono
    // ---------------------------------------------------------

    const telefono =
      this.telefonoLocal.replace(/\D/g, '');

    if (telefono.length !== 10) {

      this.errorMensaje.set(
        'Ingresa un número de celular válido (10 dígitos)'
      );

      return;
    }


    // ---------------------------------------------------------
    // Enviar OTP
    // ---------------------------------------------------------

    this.cargando.set(true);

    this.authService
      .enviarOtp(this.telefonoCompleto)
      .subscribe({

        next: () => {

          this.cargando.set(false);

          this.otpEnviado.set(true);

          this.infoMensaje.set(
            'Código solicitado. Puede tardar unos segundos en llegar.'
          );

          // Comenzar contador de 30 segundos
          this.iniciarContador();
        },

        error: (error) => {

          this.cargando.set(false);

          console.error(
            'Error al enviar código:',
            error
          );

          this.errorMensaje.set(
            'No se pudo enviar el código. Verifica el número.'
          );
        }
      });
  }


  // =========================================================
  // INICIAR CONTADOR
  // =========================================================

  private iniciarContador(): void {

    // Evitar varios intervalos al mismo tiempo
    this.detenerContador();

    this.segundosReenvio.set(30);


    this.intervaloReenvio = setInterval(() => {

      const segundosActuales =
        this.segundosReenvio();


      if (segundosActuales <= 1) {

        this.segundosReenvio.set(0);

        this.detenerContador();

        return;
      }


      this.segundosReenvio.set(
        segundosActuales - 1
      );

    }, 1000);
  }


  // =========================================================
  // DETENER CONTADOR
  // =========================================================

  private detenerContador(): void {

    if (this.intervaloReenvio !== null) {

      clearInterval(this.intervaloReenvio);

      this.intervaloReenvio = null;
    }
  }


  // =========================================================
  // SUBMIT
  // =========================================================

  onSubmit(): void {

    this.errorMensaje.set(null);


    if (this.modo() === 'correo') {

      this.loginPorCorreo();

    } else {

      this.loginPorTelefono();
    }
  }


  // =========================================================
  // LOGIN POR CORREO
  // =========================================================

  private loginPorCorreo(): void {

    this.cargando.set(true);

    this.authService
      .login({
        correo: this.correo,
        contrasena: this.contrasena
      })
      .subscribe({

        next: (response) => {

          this.cargando.set(false);

          this.authService
            .redirigirSegunRol(response.rol);
        },

        error: (err) => {

          this.cargando.set(false);

          if (err.status === 401) {

            this.errorMensaje.set(
              'Correo o contraseña incorrectos'
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
  // LOGIN POR TELÉFONO
  // =========================================================

  private loginPorTelefono(): void {

    if (!this.codigo.trim()) {

      this.errorMensaje.set(
        'Ingresa el código que te llegó por SMS'
      );

      return;
    }


    this.cargando.set(true);

    this.authService
      .loginOtp({
        telefono: this.telefonoCompleto,
        codigo: this.codigo
      })
      .subscribe({

        next: (response) => {

          this.cargando.set(false);

          this.detenerContador();

          this.segundosReenvio.set(0);

          this.authService
            .redirigirSegunRol(response.rol);
        },

        error: (err) => {

          this.cargando.set(false);

          if (err.status === 401) {

            this.errorMensaje.set(
              'Código incorrecto o expirado'
            );

          } else if (err.status === 404) {

            this.errorMensaje.set(
              'No existe una cuenta con ese teléfono'
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
  // DESTRUIR COMPONENTE
  // =========================================================

  ngOnDestroy(): void {

    this.detenerContador();
  }
}