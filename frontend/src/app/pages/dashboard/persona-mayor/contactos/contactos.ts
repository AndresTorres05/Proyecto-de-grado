import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  AcompananteService,
  Acompanante
} from '../../../../core/acompanantes/acompanante.service';
import { EmergenciaService } from '../../../../core/emergencia/emergencia.service';
import { Icon } from '../../../../shared/icon/icon';

@Component({
  selector: 'app-contactos',
  standalone: true,
  imports: [Icon, FormsModule],
  templateUrl: './contactos.html',
  styleUrl: './contactos.css'
})
export class Contactos implements OnInit {

  protected readonly acompanantes = signal<Acompanante[]>([]);

  protected readonly mostrandoConfirmacionEmergencia = signal(false);
  protected readonly enviandoEmergencia = signal(false);
  protected readonly mensajeEmergencia = signal<string | null>(null);
  protected readonly errorEmergencia = signal<string | null>(null);

  protected readonly mostrandoFormularioAcompanante = signal(false);

  protected celularAcompanante = '';
  protected parentescoAcompanante = '';

  protected readonly agregandoAcompanante = signal(false);
  protected readonly errorAcompanante = signal<string | null>(null);
  protected readonly mensajeAcompanante = signal<string | null>(null);

  constructor(
    private acompananteService: AcompananteService,
    private emergenciaService: EmergenciaService
  ) {}

  ngOnInit(): void {
    this.acompananteService.obtenerAcompanantes().subscribe({
      next: (acompanantes) => {
        this.acompanantes.set(acompanantes);
      },
      error: () => {
        this.acompanantes.set([]);
      }
    });
  }

  mostrarFormularioAcompanante(): void {
    this.mostrandoFormularioAcompanante.set(true);
    this.errorAcompanante.set(null);
    this.mensajeAcompanante.set(null);
  }

  cancelarFormularioAcompanante(): void {
    this.mostrandoFormularioAcompanante.set(false);

    this.celularAcompanante = '';
    this.parentescoAcompanante = '';

    this.errorAcompanante.set(null);
}

agregarAcompanante(): void {

  this.errorAcompanante.set(null);
  this.mensajeAcompanante.set(null);

  if (
    !this.celularAcompanante.trim() ||
    !this.parentescoAcompanante.trim()
  ) {
    this.errorAcompanante.set(
      'Por favor completa todos los campos.'
    );
    return;
  }

  this.agregandoAcompanante.set(true);

  const celularIngresado = this.celularAcompanante.trim();

if (!/^\d{10}$/.test(celularIngresado)) {
  this.errorAcompanante.set(
    'Ingresa un número de celular válido de 10 dígitos.'
  );
  return;
}

const celular = '+57' + celularIngresado;

this.agregandoAcompanante.set(true);

this.acompananteService.agregarAcompanante({
  celular: celular,
  parentesco: this.parentescoAcompanante
}).subscribe({
    next: (respuesta) => {

      this.agregandoAcompanante.set(false);
      this.mostrandoFormularioAcompanante.set(false);

      this.mensajeAcompanante.set(respuesta);

      this.celularAcompanante = '';
      this.parentescoAcompanante = '';

      // Actualizar la tarjeta del acompañante
      this.acompananteService.obtenerAcompanantes().subscribe({
        next: (acompanantes) => {
          this.acompanantes.set(acompanantes);
        }
      });
    },

    error: (error) => {

      this.agregandoAcompanante.set(false);

      const mensaje =
        error?.error ||
        'No se pudo agregar el acompañante.';

      this.errorAcompanante.set(mensaje);
    }
  });
}

  activarConfirmacionEmergencia(): void {
    this.mostrandoConfirmacionEmergencia.set(true);
    this.mensajeEmergencia.set(null);
    this.errorEmergencia.set(null);
  }

  cancelarEmergencia(): void {
    this.mostrandoConfirmacionEmergencia.set(false);
  }

  confirmarEmergencia(): void {
    this.enviandoEmergencia.set(true);
    this.errorEmergencia.set(null);
    this.mensajeEmergencia.set(null);

    this.emergenciaService.activarEmergencia().subscribe({
      next: (respuesta) => {
        this.enviandoEmergencia.set(false);
        this.mostrandoConfirmacionEmergencia.set(false);
        this.mensajeEmergencia.set(respuesta);
      },
      error: (error) => {
        this.enviandoEmergencia.set(false);

        const mensaje =
          error?.error || 'No se pudo enviar la alerta de emergencia.';

        this.errorEmergencia.set(mensaje);
      }
    });
  }
}