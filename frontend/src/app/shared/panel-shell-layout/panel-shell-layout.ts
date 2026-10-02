import { Component, computed } from '@angular/core';
import { ActivatedRoute, RouterOutlet } from '@angular/router';
import { DashboardShell, ShellNavItem } from '../dashboard-shell/dashboard-shell';
import { AuthService } from '../../core/auth/auth.service';
import { PANEL_CONFIG } from '../panel-config/panel-config';

/**
 * Plantilla de los paneles: arma DashboardShell con la configuración del rol
 * de la ruta (data.rol) y muestra la página hija adentro.
 */
@Component({
  selector: 'app-panel-shell-layout',
  standalone: true,
  imports: [DashboardShell, RouterOutlet],
  template: `
    <app-dashboard-shell
      [roleLabel]="roleLabel"
      [roleAccent]="roleAccent"
      [userName]="nombreUsuario()"
      [userInitials]="iniciales()"
      [navItems]="navItems"
      [accessible]="true"
    >
      <router-outlet></router-outlet>
    </app-dashboard-shell>
  `
})
export class PanelShellLayout {
  /** Se recalcula solo cuando cambia el nombre en AuthService (por ejemplo, en "Mi información"). */
  protected readonly nombreUsuario = computed(() => this.authService.getNombreUsuario());
  protected readonly iniciales = computed(() => this.nombreUsuario().charAt(0).toUpperCase());

  protected readonly roleLabel: string;
  protected readonly roleAccent: string;
  protected readonly navItems: ShellNavItem[];

  constructor(
      private route: ActivatedRoute,
      private authService: AuthService
  ) {
      const rol = this.route.snapshot.data['rol'] as string;
      const config = PANEL_CONFIG[rol];

      this.roleLabel = config?.roleLabel ?? '';
      this.roleAccent = config?.roleAccent ?? 'var(--vita-navy)';
      this.navItems = config?.navItems ?? [];
  }
}
