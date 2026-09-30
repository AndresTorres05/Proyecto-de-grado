import { Component, OnInit, inject, signal } from '@angular/core';

import {
  SignosVitalesService,
  SignoVitalResponse
} from '../../../../core/signos-vitales/signos-vitales.services';
import { SignosVitalesLista } from '../../../../shared/signos-vitales-lista/signos-vitales-lista';

/**
 * Historial de signos vitales de la persona mayor. Usa la misma lista
 * que el modal de "Últimos signos vitales" de organización/acompañante.
 */
@Component({
  selector: 'app-persona-mayor-signos-vitales',
  imports: [SignosVitalesLista],
  templateUrl: './signos-vitales.html',
  styleUrl: './signos-vitales.css'
})
export class SignosVitalesPersonaMayor implements OnInit {

  private signosVitalesService = inject(SignosVitalesService);

  protected readonly registros = signal<SignoVitalResponse[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.signosVitalesService.listarPropios().subscribe({
      next: (registros) => {
        this.registros.set(registros);
        this.cargando.set(false);
      },
      error: (error) => {
        console.error('Error al cargar los signos vitales:', error);
        this.error.set('No se pudo cargar tu historial de signos vitales.');
        this.cargando.set(false);
      }
    });
  }
}
