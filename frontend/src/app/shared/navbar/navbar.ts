import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { Icon } from '../icon/icon';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, Icon],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar {

  protected readonly navLinks = [
    { label: 'Inicio', href: '#hero' },
    { label: 'El Reto', href: '#reto' },
    { label: 'Quiénes Somos', href: '#quienes-somos' },
    { label: 'Misión', href: '#mission' },
    { label: 'Módulos', href: '#modules' },
    { label: 'Contacto', href: '#contacto' }
  ];

  protected readonly autenticado;

  constructor(protected authService: AuthService) {
    this.autenticado = this.authService.estaAutenticadoSignal();
  }

  get nombreUsuario(): string {
    return this.authService.getNombreUsuario();
  }

  get inicialUsuario(): string {
    return this.nombreUsuario
      .trim()
      .charAt(0)
      .toUpperCase();
  }

  irAlDashboard(): void {
    const rol = this.authService.getRol();

    if (rol) {
      this.authService.redirigirSegunRol(rol);
    }
  }

  cerrarSesion(): void {
    this.authService.logout();
  }
}