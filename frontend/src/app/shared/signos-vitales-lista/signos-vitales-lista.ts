import { Component, input } from '@angular/core';
import { DatePipe } from '@angular/common';

import { SignoVitalResponse } from '../../core/signos-vitales/signos-vitales.services';
import { Icon } from '../icon/icon';

/**
 * Lista de registros de signos vitales (con estados de cargando, error
 * y vacio). La usan el modal de organizacion/acompañante y la pagina de
 * historial de la persona mayor; cada una carga los registros con el
 * endpoint de su rol.
 */
@Component({
  selector: 'app-signos-vitales-lista',
  imports: [Icon, DatePipe],
  templateUrl: './signos-vitales-lista.html',
  styleUrl: './signos-vitales-lista.css'
})
export class SignosVitalesLista {

  readonly registros = input<SignoVitalResponse[]>([]);
  readonly cargando = input(false);
  readonly error = input<string | null>(null);

  /** En el modal la lista tiene altura máxima con scroll; en una página, no. */
  readonly compacta = input(true);

  readonly tituloVacio = input('No tiene signos vitales registrados');
  readonly textoVacio = input(
    'Cuando se registren signos vitales de esta persona mayor, aparecerán aquí.'
  );
}
