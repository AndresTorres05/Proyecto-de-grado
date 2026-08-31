import { Component, OnInit, signal } from '@angular/core';
import {
  AcompananteService,
  Acompanante
} from '../../../../core/acompanantes/acompanante.service';
import { EmergenciaService } from '../../../../core/emergencia/emergencia.service';

@Component({
  selector: 'app-contactos',
  standalone: true,
  imports: [],
  templateUrl: './contactos.html',
  styleUrl: './contactos.css'
})
export class Contactos implements OnInit {

  protected readonly acompanante = signal<Acompanante | null>(null);

  protected readonly mostrandoConfirmacionEmergencia = signal(false);
  protected readonly enviandoEmergencia = signal(false);
  protected readonly mensajeEmergencia = signal<string | null>(null);
  protected readonly errorEmergencia = signal<string | null>(null);

  constructor(
    private acompananteService: AcompananteService,
    private emergenciaService: EmergenciaService
  ) {}

  ngOnInit(): void {
    this.acompananteService.obtenerAcompanantes().subscribe({
      next: (acompanantes) => {
        this.acompanante.set(acompanantes[0] ?? null);
      },
      error: () => {
        this.acompanante.set(null);
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