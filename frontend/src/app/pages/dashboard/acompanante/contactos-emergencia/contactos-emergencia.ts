import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  AcompananteService,
  PersonaMayorAcompanada,
  ContactoEmergencia
} from '../../../../core/acompanantes/acompanante.service';
import { Icon } from '../../../../shared/icon/icon';
import { PersonCard } from '../../../../shared/person-card/person-card';

@Component({
  selector: 'app-contactos-emergencia',
  standalone: true,
  imports: [CommonModule, Icon, PersonCard],
  templateUrl: './contactos-emergencia.html',
  styleUrl: './contactos-emergencia.css'
})
export class ContactosEmergencia implements OnInit {

  personasMayores: PersonaMayorAcompanada[] = [];

  personaSeleccionada: PersonaMayorAcompanada | null = null;

  contactos: ContactoEmergencia[] = [];

  cargandoPersonas = true;
  cargandoContactos = false;

  errorPersonas = '';
  errorContactos = '';

  constructor(
    private acompananteService: AcompananteService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarPersonasMayores();
  }

  // =========================================================
  // CARGAR PERSONAS MAYORES
  // =========================================================

  cargarPersonasMayores(): void {

    this.cargandoPersonas = true;
    this.errorPersonas = '';

    this.acompananteService.obtenerPersonasMayores().subscribe({

      next: (data) => {

        this.personasMayores = data;
        this.cargandoPersonas = false;

        // Si solamente acompaña a una persona mayor,
        // la seleccionamos automáticamente.
        if (this.personasMayores.length === 1) {
          this.seleccionarPersona(this.personasMayores[0]);
        }

        this.cdr.detectChanges();
      },

      error: (error) => {

        console.error(
          'Error al cargar las personas mayores:',
          error
        );

        this.cargandoPersonas = false;

        this.errorPersonas =
          'No se pudieron cargar las personas mayores asociadas.';

        this.cdr.detectChanges();
      }

    });
  }

  // =========================================================
  // SELECCIONAR PERSONA MAYOR
  // =========================================================

  seleccionarPersona(
    persona: PersonaMayorAcompanada
  ): void {

    this.personaSeleccionada = persona;

    this.contactos = [];

    this.cargarContactos(persona.idUsuario);
  }

  // =========================================================
  // CARGAR CONTACTOS
  // =========================================================

  cargarContactos(
    idPersonaMayor: number
  ): void {

    this.cargandoContactos = true;
    this.errorContactos = '';

    this.acompananteService
      .obtenerContactosEmergencia(idPersonaMayor)
      .subscribe({

        next: (data) => {

          this.contactos = data;

          this.cargandoContactos = false;

          this.cdr.detectChanges();
        },

        error: (error) => {

          console.error(
            'Error al cargar los contactos:',
            error
          );

          this.cargandoContactos = false;

          if (error.status === 403) {

            this.errorContactos =
              'No tienes autorización para consultar los contactos de esta persona mayor.';

          } else {

            this.errorContactos =
              'No se pudieron cargar los contactos.';
          }

          this.cdr.detectChanges();
        }

      });
  }

}