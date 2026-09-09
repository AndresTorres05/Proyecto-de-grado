import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import {
  OrganizacionService,
  PersonaMayorOrganizacion
} from '../../../../core/organizacion/organizacion.service';

import { Icon } from '../../../../shared/icon/icon';

@Component({
  selector: 'app-personas-mayores',
  standalone: true,
  imports: [Icon, FormsModule],
  templateUrl: './personas-mayores.html',
  styleUrl: './personas-mayores.css'
})
export class PersonasMayores implements OnInit {

  protected readonly personasMayores =
    signal<PersonaMayorOrganizacion[]>([]);

  protected readonly mostrandoFormulario =
    signal(false);

  protected readonly agregandoPersonaMayor =
    signal(false);

  protected readonly mensaje =
    signal<string | null>(null);

  protected readonly error =
    signal<string | null>(null);

  protected telefono = '';

  constructor(
    private organizacionService: OrganizacionService
  ) {}

  ngOnInit(): void {
    this.cargarPersonasMayores();
  }

  // ==========================================
  // CARGAR PERSONAS MAYORES
  // ==========================================

  cargarPersonasMayores(): void {

    this.organizacionService.obtenerPersonasMayores().subscribe({
      next: (personas) => {
        this.personasMayores.set(personas);
      },

      error: (error) => {
        console.error(
          'Error al cargar personas mayores:',
          error
        );

        this.personasMayores.set([]);
      }
    });
  }


  // ==========================================
  // MOSTRAR FORMULARIO
  // ==========================================

  mostrarFormulario(): void {

    this.mostrandoFormulario.set(true);

    this.telefono = '';

    this.mensaje.set(null);
    this.error.set(null);
  }


  // ==========================================
  // CERRAR FORMULARIO
  // ==========================================

  cancelarFormulario(): void {

    this.mostrandoFormulario.set(false);

    this.telefono = '';

    this.mensaje.set(null);
    this.error.set(null);
  }


  // ==========================================
  // ASOCIAR PERSONA MAYOR
  // ==========================================

  asociarPersonaMayor(): void {

    this.mensaje.set(null);
    this.error.set(null);

    const telefonoIngresado =
      this.telefono.trim();


    // ==========================================
    // VALIDAR CAMPO
    // ==========================================

    if (!telefonoIngresado) {

      this.error.set(
        'Por favor ingresa el teléfono de la persona mayor.'
      );

      return;
    }


    // ==========================================
    // VALIDAR FORMATO
    // ==========================================

    if (!/^\d{10}$/.test(telefonoIngresado)) {

      this.error.set(
        'Ingresa un número de teléfono válido de 10 dígitos.'
      );

      return;
    }


    // ==========================================
    // FORMATO COLOMBIANO
    // ==========================================

    const telefono =
      '+57' + telefonoIngresado;


    this.agregandoPersonaMayor.set(true);


    // ==========================================
    // ENVIAR SOLICITUD
    // ==========================================

    this.organizacionService
      .asociarPersonaMayor(telefono)
      .subscribe({

        next: (respuesta) => {

          this.agregandoPersonaMayor.set(false);

          this.mostrandoFormulario.set(false);

          this.telefono = '';

          this.mensaje.set(respuesta);

          // Actualizar la lista
          this.cargarPersonasMayores();
        },

        error: (error) => {

          this.agregandoPersonaMayor.set(false);

          const mensaje =
            error?.error ||
            'No se pudo enviar la solicitud.';

          this.error.set(mensaje);
        }

      });
  }
}