import { Component, signal } from '@angular/core';
import { AuthService } from '../../core/auth/auth.service';

/**
 * Bloque "Zona de peligro" para eliminar la cuenta, valido para
 * cualquier rol. Va en la pagina de perfil/administracion de cada
 * panel (no en el menu del usuario) para que no quede tan a la mano.
 */
@Component({
  selector: 'app-eliminar-cuenta',
  templateUrl: './eliminar-cuenta.html',
  styleUrl: './eliminar-cuenta.css'
})
export class EliminarCuenta {

  protected readonly mostrandoModal = signal(false);
  protected readonly textoConfirmacion = signal('');
  protected readonly eliminando = signal(false);
  protected readonly error = signal('');

  constructor(private authService: AuthService) {}

  abrirModal(): void {
    this.textoConfirmacion.set('');
    this.error.set('');
    this.mostrandoModal.set(true);
  }

  cerrarModal(): void {
    if (this.eliminando()) return;
    this.mostrandoModal.set(false);
  }

  confirmacionValida(): boolean {
    return this.textoConfirmacion().trim() === 'ELIMINAR';
  }

  confirmar(): void {
    if (!this.confirmacionValida()) return;

    this.eliminando.set(true);
    this.error.set('');

    this.authService.eliminarCuenta().subscribe({
      next: () => {
        this.eliminando.set(false);
        this.mostrandoModal.set(false);
        this.authService.logout();
      },
      error: (error) => {
        console.error('Error al eliminar la cuenta:', error);
        this.eliminando.set(false);
        this.error.set('No se pudo eliminar la cuenta. Intenta de nuevo más tarde.');
      }
    });
  }
}
