import { Component, Input } from '@angular/core';
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

// Página de "Mi información" de cada rol (no todas se llaman igual).
const RUTA_MI_INFORMACION: Record<string, string> = {
  PERSONA_MAYOR: '/panel/persona-mayor/informacion',
  ACOMPANANTE: '/panel/acompanante/perfil',
  ORGANIZACION: '/panel/organizacion/administracion',
  VOLUNTARIO: '/panel/voluntario/perfil'
};

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

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  toggleMenuUsuario(): void {
    this.menuUsuarioAbierto = !this.menuUsuarioAbierto;
  }

  cerrarSesion(): void {
    this.authService.logout();
  }

  irAMiInformacion(): void {
    this.menuUsuarioAbierto = false;
    const rol = this.authService.getRol();
    const ruta = rol ? RUTA_MI_INFORMACION[rol] : undefined;

    if (ruta) {
      this.router.navigateByUrl(ruta);
    }
  }
}