import { Component, input, output, signal } from '@angular/core';

import { ClicGrafica, EChart, OpcionesGrafica } from '../../../../../shared/echart/echart';

export interface TablaGrafica {
  columnas: string[];
  filas: (string | number)[][];
}

/**
 * Tarjeta de una gráfica del reporte: título, descripción, controles
 * propios (proyectados con [acciones]) y un botón para ver los mismos
 * datos como tabla (la vista accesible de cada gráfica).
 */
@Component({
  selector: 'app-grafica-card',
  imports: [EChart],
  templateUrl: './grafica-card.html',
  styleUrl: './grafica-card.css'
})
export class GraficaCard {

  readonly titulo = input.required<string>();
  readonly descripcion = input('');
  readonly opciones = input.required<OpcionesGrafica>();
  readonly tabla = input.required<TablaGrafica>();
  readonly alto = input(300);
  readonly vacio = input(false);
  readonly textoVacio = input('No hay datos para mostrar en este período.');

  readonly clic = output<ClicGrafica>();

  protected readonly verTabla = signal(false);
}
