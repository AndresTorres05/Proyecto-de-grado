import { Component, Input } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

export interface ShellNavItem {
  icon: string;
  label: string;
  active?: boolean;
  path?: string;
}

@Component({
  selector: 'app-dashboard-shell',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './dashboard-shell.html',
  styleUrl: './dashboard-shell.css',
  host: {
    '[style.--role-accent]': 'roleAccent',
    '[class.shell-host--accessible]': 'accessible'
  }
})
export class DashboardShell {

  @Input() roleLabel = '';
  @Input() roleAccent = 'var(--gema-navy)';
  @Input() userName = '';
  @Input() userInitials = '';
  @Input() navItems: ShellNavItem[] = [];
  @Input() notifCount = 0;
  @Input() accessible = false;

  protected readonly roleSwitcher = [
    { label: 'Organización', path: '/panel/organizacion' },
    { label: 'Voluntario', path: '/panel/voluntario' },
    { label: 'Acompañante', path: '/panel/acompanante' },
    { label: 'Persona mayor', path: '/panel/persona-mayor' },
    { label: 'Admin', path: '/panel/admin' }
  ];
}