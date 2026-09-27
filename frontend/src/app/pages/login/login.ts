import {
  Component,
  signal,
  OnDestroy
} from '@angular/core';

import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

type ModoLogin = 'correo' | 'celular';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login implements OnDestroy {

  modo = signal<ModoLogin>('celular');

  modoRecuperacion = signal(false);

  // =========================================================
  // CORREO / CONTRASEÑA
  // =========================================================

  correo = '';
  contrasena = '';


  // =========================================================
  // CELULAR / OTP
  // =========================================================

  celularLocal = '';
  codigo = '';

  // =========================================================
// RECUPERACIÓN DE CONTRASEÑA
// =========================================================

recuperacionOtpEnviado = signal(false);

recuperacionCodigo = '';

nuevaContrasena = '';

confirmarNuevaContrasena = '';

  otpEnviado = signal(false);

  // Contador para reenviar código
  segundosReenvio = signal(0);

  private intervaloReenvio: ReturnType<typeof setInterval> | null = null;


  // =========================================================
  // ESTADO
  // =========================================================

  cargando = signal(false);

  enviandoCodigo = signal(false);

  errorMensaje = signal<string | null>(null);

  infoMensaje = signal<string | null>(null);


  constructor(
    private authService: AuthService
  ) {}


  // =========================================================
  // CELULAR COMPLETO
  // =========================================================

  get celularCompleto(): string {

    return `+57${this.celularLocal.replace(/\D/g, '')}`;
  }


  // =========================================================
  // CAMBIAR MODO
  // =========================================================

cambiarModo(modo: ModoLogin): void {

  this.modo.set(modo);

  // Reiniciar el flujo OTP al cambiar de método
  this.otpEnviado.set(false);
  this.codigo = '';

  // Limpiar mensajes
  this.errorMensaje.set(null);
  this.infoMensaje.set(null);

  // Reiniciar contador
  this.detenerContador();
  this.segundosReenvio.set(0);
}

// =========================================================
// ABRIR RECUPERACIÓN DE CONTRASEÑA
// =========================================================

abrirRecuperacion(): void {

  this.modoRecuperacion.set(true);

  this.recuperacionOtpEnviado.set(false);

  this.recuperacionCodigo = '';

  this.nuevaContrasena = '';

  this.confirmarNuevaContrasena = '';

  this.celularLocal = '';

  this.errorMensaje.set(null);

  this.infoMensaje.set(null);

  this.detenerContador();

  this.segundosReenvio.set(0);
}

// =========================================================
// VOLVER AL LOGIN
// =========================================================

volverAlLogin(): void {

  this.modoRecuperacion.set(false);
  this.modo.set('correo');

  this.recuperacionOtpEnviado.set(false);

  this.recuperacionCodigo = '';

  this.nuevaContrasena = '';

  this.confirmarNuevaContrasena = '';

  this.celularLocal = '';

  this.errorMensaje.set(null);

  this.infoMensaje.set(null);

  this.detenerContador();

  this.segundosReenvio.set(0);
}

// =========================================================
// ENVIAR CÓDIGO PARA RECUPERAR CONTRASEÑA
// =========================================================

enviarCodigoRecuperacion(): void {

  this.errorMensaje.set(null);
  this.infoMensaje.set(null);

  const celular =
    this.celularLocal.replace(/\D/g, '');

  if (celular.length !== 10) {

    this.errorMensaje.set(
      'Ingresa un número de celular válido (10 dígitos)'
    );

    return;
  }

  if (this.segundosReenvio() > 0) {
    return;
  }

  this.enviandoCodigo.set(true);

  // ---------------------------------------------------------
  // Verificar que exista una cuenta
  // ---------------------------------------------------------

  this.authService
    .celularExiste(this.celularCompleto)
    .subscribe({

      next: (existe) => {

        if (!existe) {

          this.enviandoCodigo.set(false);

          this.errorMensaje.set(
            'No existe una cuenta asociada a este número de celular.'
          );

          return;
        }

        // ---------------------------------------------------
        // Mostrar inmediatamente la pantalla para ingresar
        // el código y la nueva contraseña
        // ---------------------------------------------------

        this.recuperacionOtpEnviado.set(true);

        this.infoMensaje.set(
          'Código solicitado. Puede tardar unos segundos en llegar.'
        );

        this.iniciarContador();

        // Ya no dejamos la pantalla bloqueada mostrando
        // "Enviando..." mientras TextBee responde.
        this.enviandoCodigo.set(false);

        // ---------------------------------------------------
        // Enviar OTP
        // ---------------------------------------------------

        this.authService
          .enviarOtp(this.celularCompleto)
          .subscribe({

            next: () => {

              console.log(
                'Código de recuperación enviado correctamente'
              );

            },

            error: (error) => {

              console.error(
                'Error al enviar código de recuperación:',
                error
              );

              this.errorMensaje.set(
                'No se pudo enviar el código. Intenta nuevamente.'
              );

              this.recuperacionOtpEnviado.set(false);

              this.detenerContador();

              this.segundosReenvio.set(0);
            }

          });

      },

      error: (error) => {

        this.enviandoCodigo.set(false);

        console.error(
          'Error al verificar celular:',
          error
        );

        this.errorMensaje.set(
          'No se pudo verificar el número. Intenta nuevamente.'
        );
      }

    });
}

// =========================================================
// RESTABLECER CONTRASEÑA
// =========================================================

restablecerContrasena(): void {

  this.errorMensaje.set(null);
  this.infoMensaje.set(null);


  if (!this.recuperacionCodigo.trim()) {

    this.errorMensaje.set(
      'Ingresa el código de verificación'
    );

    return;
  }


  if (!this.nuevaContrasena.trim()) {

    this.errorMensaje.set(
      'Ingresa una nueva contraseña'
    );

    return;
  }


  if (this.nuevaContrasena.length < 6) {

    this.errorMensaje.set(
      'La contraseña debe tener mínimo 6 caracteres'
    );

    return;
  }


  if (!this.confirmarNuevaContrasena.trim()) {

    this.errorMensaje.set(
      'Confirma tu nueva contraseña'
    );

    return;
  }


  if (
    this.nuevaContrasena !==
    this.confirmarNuevaContrasena
  ) {

    this.errorMensaje.set(
      'Las contraseñas no coinciden'
    );

    return;
  }


  this.cargando.set(true);


  this.authService
    .restablecerContrasena({

      celular: this.celularCompleto,

      codigo: this.recuperacionCodigo,

      contrasena: this.nuevaContrasena,

      confirmarContrasena:
        this.confirmarNuevaContrasena

    })
    .subscribe({

      next: () => {

        this.cargando.set(false);

        this.infoMensaje.set(
          'Contraseña actualizada correctamente. Ya puedes iniciar sesión.'
        );

        this.detenerContador();

        this.segundosReenvio.set(0);

        this.recuperacionOtpEnviado.set(false);

        this.nuevaContrasena = '';

        this.confirmarNuevaContrasena = '';

        this.recuperacionCodigo = '';

      },

      error: (err) => {

        this.cargando.set(false);

        console.error('ERROR RESTABLECER CONTRASEÑA:', err);
        console.error('STATUS:', err.status);
        console.error('ERROR BODY:', err.error);

        if (err.status === 400) {

          this.errorMensaje.set(
            err.error?.mensaje ||
            err.error?.message ||
            'Código incorrecto o expirado.'
          );

        } else {

          this.errorMensaje.set(
            'No se pudo actualizar la contraseña. Intenta nuevamente.'
          );
        }

      }

    });
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
    // Validar celular
    // ---------------------------------------------------------

    const celular =
      this.celularLocal.replace(/\D/g, '');

    if (celular.length !== 10) {

      this.errorMensaje.set(
        'Ingresa un número de celular válido (10 dígitos)'
      );

      return;
    }


    // ---------------------------------------------------------
    // Enviar OTP
    // ---------------------------------------------------------

    this.enviandoCodigo.set(true);

this.authService
  .celularExiste(this.celularCompleto)
  .subscribe({

    next: (existe) => {

      if (!existe) {
        this.enviandoCodigo.set(false);

        this.errorMensaje.set(
          'No existe una cuenta asociada a este número de celular.'
        );

        return;
      }

      this.authService
        .enviarOtp(this.celularCompleto)
        .subscribe({

          next: () => {

            this.enviandoCodigo.set(false);

            this.otpEnviado.set(true);

            this.infoMensaje.set(
              'Código solicitado. Puede tardar unos segundos en llegar.'
            );

            this.iniciarContador();
          },

          error: (error) => {

            this.enviandoCodigo.set(false);

            console.error(
              'Error al enviar código:',
              error
            );

            this.errorMensaje.set(
              'No se pudo enviar el código. Verifica el número.'
            );
          }
        });
    },

    error: (error) => {

      this.enviandoCodigo.set(false);

      console.error(
        'Error al verificar el celular:',
        error
      );

      this.errorMensaje.set(
        'No se pudo verificar el número. Intenta nuevamente.'
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

      this.loginPorCelular();
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

  const mensaje =
    err.error?.mensaje ||
    err.error?.message ||
    '';

  if (
    mensaje.toLowerCase().includes('contraseña') &&
    (
      mensaje.toLowerCase().includes('no tiene') ||
      mensaje.toLowerCase().includes('no registrada') ||
      mensaje.toLowerCase().includes('no registrada')
    )
  ) {
    this.errorMensaje.set(
      'Esta cuenta no tiene una contraseña registrada. Intenta iniciar sesión con tu número de celular.'
    );

  } else {
    this.errorMensaje.set(
      'Correo o contraseña incorrectos'
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
  // LOGIN POR CELULAR
  // =========================================================

  private loginPorCelular(): void {

    if (!this.codigo.trim()) {

      this.errorMensaje.set(
        'Ingresa el código que te llegó por SMS'
      );

      return;
    }


    this.cargando.set(true);

    this.authService
      .loginOtp({
        celular: this.celularCompleto,
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
              'No existe una cuenta con ese celular'
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