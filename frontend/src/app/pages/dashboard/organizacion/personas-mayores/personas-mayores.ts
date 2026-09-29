import { Component, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import {
  OrganizacionService,
  PersonaMayorOrganizacion,
  AcompanantePersonaMayor
} from '../../../../core/organizacion/organizacion.service';

import {
  SignosVitalesService,
  SignoVitalResponse
} from '../../../../core/signos-vitales/signos-vitales.services';

import { Icon } from '../../../../shared/icon/icon';
import { PersonCard } from '../../../../shared/person-card/person-card';

@Component({
  selector: 'app-personas-mayores',
  standalone: true,
  imports: [Icon, FormsModule, PersonCard, DatePipe],
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

  protected readonly mostrandoAcompanantes =
  signal(false);

protected readonly cargandoAcompanantes =
  signal(false);

protected readonly acompanantes =
  signal<AcompanantePersonaMayor[]>([]);

protected personaMayorSeleccionada:
  PersonaMayorOrganizacion | null = null;

protected readonly mostrandoSignosVitales =
  signal(false);

protected readonly cargandoSignosVitales =
  signal(false);

protected readonly errorSignosVitales =
  signal<string | null>(null);

protected readonly signosVitales =
  signal<SignoVitalResponse[]>([]);

  protected readonly error = signal<string | null>(null);
  protected readonly mostrandoConfirmacion = signal(false);
  protected personaSeleccionada: PersonaMayorOrganizacion | null = null;
  protected celular = '';

constructor(
  private organizacionService: OrganizacionService,
  private signosVitalesService: SignosVitalesService,
  private route: ActivatedRoute
) {}

ngOnInit(): void {
  this.cargarPersonasMayores();

  this.route.queryParams.subscribe(params => {
    if (params['abrir'] === 'registrar') {
      this.mostrarFormulario();
    }
  });
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

    this.celular = '';

    this.mensaje.set(null);
    this.error.set(null);
  }


  // ==========================================
  // CERRAR FORMULARIO
  // ==========================================

  cancelarFormulario(): void {

    this.mostrandoFormulario.set(false);

    this.celular = '';

    this.mensaje.set(null);
    this.error.set(null);
  }


  // ==========================================
  // ASOCIAR PERSONA MAYOR
  // ==========================================

  asociarPersonaMayor(): void {

    this.mensaje.set(null);
    this.error.set(null);

    const celularIngresado =
      this.celular.trim();


    // ==========================================
    // VALIDAR CAMPO
    // ==========================================

    if (!celularIngresado) {

      this.error.set(
        'Por favor ingresa el celular de la persona mayor.'
      );

      return;
    }


    // ==========================================
    // VALIDAR FORMATO
    // ==========================================

    if (!/^\d{10}$/.test(celularIngresado)) {

      this.error.set(
        'Ingresa un número de celular válido de 10 dígitos.'
      );

      return;
    }


    // ==========================================
    // FORMATO COLOMBIANO
    // ==========================================

    const celular =
      '+57' + celularIngresado;


    this.agregandoPersonaMayor.set(true);


    // ==========================================
    // ENVIAR SOLICITUD
    // ==========================================

    this.organizacionService
      .asociarPersonaMayor(celular)
      .subscribe({

        next: (respuesta) => {

          this.agregandoPersonaMayor.set(false);

          this.mostrandoFormulario.set(false);

          this.celular = '';

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
mostrarConfirmacion(persona: PersonaMayorOrganizacion): void {
  this.personaSeleccionada = persona;
  this.mostrandoConfirmacion.set(true);
  this.mensaje.set(null);
  this.error.set(null);
}

cerrarConfirmacion(): void {
  this.mostrandoConfirmacion.set(false);
  this.personaSeleccionada = null;
}

confirmarCancelacion(): void {
  if (!this.personaSeleccionada) return;

  const persona = this.personaSeleccionada;

  this.mensaje.set(null);
  this.error.set(null);
  this.mostrandoConfirmacion.set(false);

  this.organizacionService
    .cancelarAsociacionPersonaMayor(persona.idUsuario)
    .subscribe({
      next: (respuesta) => {
        this.personaSeleccionada = null;
        this.mensaje.set(respuesta);
        this.cargarPersonasMayores();
      },
      error: (error) => {
        console.error('Error al cancelar la asociación:', error);
        const mensaje =
          error?.error || 'No se pudo cancelar la asociación.';
        this.error.set(mensaje);
        this.personaSeleccionada = null;
      }
    });
}

mostrarAcompanantes(persona: PersonaMayorOrganizacion): void {

  this.personaMayorSeleccionada = persona;

  this.acompanantes.set([]);

  this.mostrandoAcompanantes.set(true);

  this.cargandoAcompanantes.set(true);

  this.organizacionService
    .obtenerAcompanantesPersonaMayor(persona.idUsuario)
    .subscribe({

      next: (acompanantes) => {

        this.acompanantes.set(acompanantes);

        this.cargandoAcompanantes.set(false);

      },

      error: (error) => {

        console.error(
          'Error al cargar acompañantes:',
          error
        );

        this.acompanantes.set([]);

        this.cargandoAcompanantes.set(false);

      }

    });
}

cerrarAcompanantes(): void {

  this.mostrandoAcompanantes.set(false);

  this.personaMayorSeleccionada = null;

  this.acompanantes.set([]);
}

mostrarSignosVitales(persona: PersonaMayorOrganizacion): void {

  this.personaMayorSeleccionada = persona;

  this.signosVitales.set([]);

  this.errorSignosVitales.set(null);

  this.mostrandoSignosVitales.set(true);

  this.cargandoSignosVitales.set(true);

  this.signosVitalesService
    .listarUltimos(persona.idUsuario)
    .subscribe({

      next: (registros) => {

        this.signosVitales.set(registros);

        this.cargandoSignosVitales.set(false);

      },

      error: (error) => {

        console.error(
          'Error al cargar signos vitales:',
          error
        );

        this.errorSignosVitales.set(
          'No se pudieron cargar los signos vitales.'
        );

        this.cargandoSignosVitales.set(false);

      }

    });
}

cerrarSignosVitales(): void {

  this.mostrandoSignosVitales.set(false);

  this.personaMayorSeleccionada = null;

  this.signosVitales.set([]);
}

}