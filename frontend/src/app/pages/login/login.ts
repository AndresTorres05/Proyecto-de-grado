import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {

  correo = '';
  contrasena = '';

  cargando = signal(false);
  errorMensaje = signal<string | null>(null);

  constructor(private authService: AuthService) {}

  onSubmit(): void {
    this.errorMensaje.set(null);
    this.cargando.set(true);

    this.authService.login({ correo: this.correo, contrasena: this.contrasena }).subscribe({
      next: (response) => {
        this.cargando.set(false);
        this.authService.redirigirSegunRol(response.rol);
      },
      error: (err) => {
        this.cargando.set(false);
        if (err.status === 401) {
          this.errorMensaje.set('Correo o contraseña incorrectos');
        } else {
          this.errorMensaje.set('No se pudo conectar con el servidor. Intenta de nuevo.');
        }
      }
    });
  }
}