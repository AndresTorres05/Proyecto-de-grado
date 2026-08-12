import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './registro.html',
  styleUrl: './registro.css'
})
export class Registro {

  nombreUsuario = '';
  correo = '';
  contrasena = '';
  rol = '';

  roles = [
    { valor: 'ORGANIZACION', etiqueta: 'Organización' },
    { valor: 'VOLUNTARIO', etiqueta: 'Voluntario' },
    { valor: 'ACOMPANANTE', etiqueta: 'Acompañante' },
    { valor: 'PERSONA_MAYOR', etiqueta: 'Persona mayor' }
  ];

  cargando = signal(false);
  errorMensaje = signal<string | null>(null);

  constructor(private authService: AuthService) {}

  onSubmit(): void {
    this.errorMensaje.set(null);
    this.cargando.set(true);

    this.authService.registro({
      nombreUsuario: this.nombreUsuario,
      correo: this.correo,
      contrasena: this.contrasena,
      rol: this.rol
    }).subscribe({
      next: (response) => {
        this.cargando.set(false);
        this.authService.redirigirSegunRol(response.rol);
      },
      error: (err) => {
        this.cargando.set(false);
        if (err.status === 409) {
          this.errorMensaje.set('Ese correo ya está registrado');
        } else if (err.status === 400) {
          this.errorMensaje.set('Revisa los datos ingresados');
        } else {
          this.errorMensaje.set('No se pudo conectar con el servidor. Intenta de nuevo.');
        }
      }
    });
  }
}