import { ChangeDetectorRef, Component, Input } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { Icon } from '../icon/icon';

export interface ShellNavItem {
  /** Nombre de icono (ver set en shared/icon/icon.ts). */
  icon: string;
  label: string;
  active?: boolean;
  path?: string;
}

@Component({
  selector: 'app-dashboard-shell',
  imports: [RouterLink, RouterLinkActive, Icon],
  templateUrl: './dashboard-shell.html',
  styleUrl: './dashboard-shell.css',
  host: {
    '[style.--role-accent]': 'roleAccent',
    '[class.shell-host--accessible]': 'accessible'
  }
})
export class DashboardShell {

  @Input() roleLabel = '';
  @Input() roleAccent = 'var(--vita-navy)';
  @Input() userName = '';
  @Input() userInitials = '';
  @Input() navItems: ShellNavItem[] = [];
  @Input() notifCount = 0;
  @Input() accessible = false;

  menuUsuarioAbierto = false;

  mostrandoEliminarCuenta = false;
  textoConfirmacion = '';
  eliminandoCuenta = false;
  errorEliminarCuenta = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  abrirEliminarCuenta(): void {
    this.menuUsuarioAbierto = false;
    this.textoConfirmacion = '';
    this.errorEliminarCuenta = '';
    this.mostrandoEliminarCuenta = true;
  }

  cerrarEliminarCuenta(): void {
    this.mostrandoEliminarCuenta = false;
  }

  confirmarEliminarCuenta(): void {
    this.eliminandoCuenta = true;
    this.errorEliminarCuenta = '';

    this.authService.eliminarCuenta().subscribe({
      next: () => {
        this.eliminandoCuenta = false;
        this.mostrandoEliminarCuenta = false;
        this.authService.logout();
      },
      error: (error) => {
        console.error('Error al eliminar la cuenta:', error);
        this.eliminandoCuenta = false;
        this.errorEliminarCuenta =
          'No se pudo eliminar la cuenta. Intenta de nuevo más tarde.';
        this.cdr.detectChanges();
      }
    });
  }

  toggleMenuUsuario(): void {
    this.menuUsuarioAbierto = !this.menuUsuarioAbierto;
  }

  cerrarSesion(): void {
    this.authService.logout();
  }

  irAMiInformacion(): void {
    this.menuUsuarioAbierto = false;
    this.router.navigateByUrl('/panel/persona-mayor/informacion');
  }
}