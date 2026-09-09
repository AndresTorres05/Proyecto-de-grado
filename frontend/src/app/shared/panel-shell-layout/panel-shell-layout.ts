import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, RouterOutlet } from '@angular/router';
import { DashboardShell, ShellNavItem } from '../dashboard-shell/dashboard-shell';
import { AuthService } from '../../core/auth/auth.service';
import { PANEL_CONFIG } from '../panel-config/panel-config';
import { OrganizacionService } from '../../core/organizacion/organizacion.service';

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
  protected nombreUsuario = '';
  protected iniciales = '';
  protected readonly roleLabel: string;
  protected readonly roleAccent: string;
  protected readonly navItems: ShellNavItem[];

  constructor(
      private route: ActivatedRoute,
      private authService: AuthService,
      private organizacionService: OrganizacionService,
      private cdr: ChangeDetectorRef
  ) {

      this.nombreUsuario = this.authService.getNombreUsuario();
      this.iniciales = this.nombreUsuario.charAt(0).toUpperCase();

      const rol = this.route.snapshot.data['rol'] as string;
      const config = PANEL_CONFIG[rol];

      this.roleLabel = config?.roleLabel ?? '';
      this.roleAccent = config?.roleAccent ?? 'var(--vita-navy)';
      this.navItems = config?.navItems ?? [];

      if (rol === 'ORGANIZACION') {
          this.cargarNombreOrganizacion();
      }
  }
  private cargarNombreOrganizacion(): void {

      this.organizacionService.obtenerInformacion().subscribe({

            next: (data) => {

                this.nombreUsuario = data.nombre;
                this.iniciales = data.nombre.charAt(0).toUpperCase();

                this.cdr.detectChanges();
            },

          error: (error) => {

              console.error(
                  'Error al cargar el nombre de la organización:',
                  error
              );
          }

      });
  }
}