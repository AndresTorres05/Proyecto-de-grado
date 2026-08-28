import { Component } from '@angular/core';
import { ActivatedRoute, RouterOutlet } from '@angular/router';
import { DashboardShell, ShellNavItem } from '../dashboard-shell/dashboard-shell';
import { AuthService } from '../../core/auth/auth.service';
import { PANEL_CONFIG } from '../panel-config/panel-config';

@Component({
  selector: 'app-panel-shell-layout',
  standalone: true,
  imports: [DashboardShell, RouterOutlet],
  template: `
    <app-dashboard-shell
      [roleLabel]="roleLabel"
      [roleAccent]="roleAccent"
      [userName]="nombreUsuario"
      [userInitials]="iniciales"
      [navItems]="navItems"
      [notifCount]="1"
      [accessible]="true"
    >
      <router-outlet></router-outlet>
    </app-dashboard-shell>
  `
})
export class PanelShellLayout {
  protected readonly nombreUsuario: string;
  protected readonly iniciales: string;
  protected readonly roleLabel: string;
  protected readonly roleAccent: string;
  protected readonly navItems: ShellNavItem[];

  constructor(private route: ActivatedRoute, private authService: AuthService) {
    this.nombreUsuario = this.authService.getNombreUsuario();
    this.iniciales = this.nombreUsuario.charAt(0).toUpperCase();

    const rol = this.route.snapshot.data['rol'] as string;
    const config = PANEL_CONFIG[rol];

    this.roleLabel = config?.roleLabel ?? '';
    this.roleAccent = config?.roleAccent ?? 'var(--gema-navy)';
    this.navItems = config?.navItems ?? [];
  }
}