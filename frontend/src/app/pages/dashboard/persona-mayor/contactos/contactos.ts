import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  AcompananteService,
  Acompanante
} from '../../../../core/acompanantes/acompanante.service';
import { EmergenciaService } from '../../../../core/emergencia/emergencia.service';
import { alCambiar } from '../../../../core/tiempo-real/tiempo-real.service';
import { Icon } from '../../../../shared/icon/icon';
import { PersonCard } from '../../../../shared/person-card/person-card';
import { CancelarAsociacion } from '../../../../shared/cancelar-asociacion/cancelar-asociacion';

@Component({
  selector: 'app-contactos',
  standalone: true,
  imports: [Icon, FormsModule, PersonCard, CancelarAsociacion],
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
  protected relacionAcompanante = '';

  protected readonly agregandoAcompanante = signal(false);
  protected readonly errorAcompanante = signal<string | null>(null);
  protected readonly mensajeAcompanante = signal<string | null>(null);

  protected readonly acompananteACancelar = signal<Acompanante | null>(null);
  protected readonly mensajeCancelacion = signal<string | null>(null);
  protected readonly errorCancelacion = signal<string | null>(null);

  constructor(
    private acompananteService: AcompananteService,
    private emergenciaService: EmergenciaService
  ) {
    alCambiar(['acompanamientos', 'usuarios'], () => this.cargarAcompanantes());
  }

  ngOnInit(): void {
    this.cargarAcompanantes();
  }

  cargarAcompanantes(): void {
    this.acompananteService.obtenerAcompanantes().subscribe({
      next: (acompanantes) => {
        this.acompanantes.set(acompanantes);
      },
      error: () => {
        this.acompanantes.set([]);
      }
    });
  }

  mostrarConfirmacionCancelacion(acompanante: Acompanante): void {
    this.acompananteACancelar.set(acompanante);
    this.mensajeCancelacion.set(null);
    this.errorCancelacion.set(null);
  }

  cerrarConfirmacionCancelacion(): void {
    this.acompananteACancelar.set(null);
  }

  confirmarCancelacion(): void {
    const acompanante = this.acompananteACancelar();

    if (!acompanante) return;

    this.acompananteACancelar.set(null);

    this.acompananteService.cancelarAcompanante(acompanante.idUsuario).subscribe({
      next: (respuesta) => {
        this.mensajeCancelacion.set(respuesta);
        this.cargarAcompanantes();
      },
      error: (error) => {
        this.errorCancelacion.set(
          error?.error || 'No se pudo cancelar la asociación.'
        );
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
    this.relacionAcompanante = '';

    this.errorAcompanante.set(null);
}

agregarAcompanante(): void {

  this.errorAcompanante.set(null);
  this.mensajeAcompanante.set(null);

  if (
    !this.celularAcompanante.trim() ||
    !this.relacionAcompanante.trim()
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
  relacion: this.relacionAcompanante
}).subscribe({
    next: (respuesta) => {

      this.agregandoAcompanante.set(false);
      this.mostrandoFormularioAcompanante.set(false);

      this.mensajeAcompanante.set(respuesta);

      this.celularAcompanante = '';
      this.relacionAcompanante = '';

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