import { Component, Input } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
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

  
}