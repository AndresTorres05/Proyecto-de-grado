import { Component, input, output } from '@angular/core';

import { SignoVitalResponse } from '../../core/signos-vitales/signos-vitales.services';
import { SignosVitalesLista } from '../signos-vitales-lista/signos-vitales-lista';

/**
 * Modal estandar con los ultimos registros de signos vitales de una
 * persona mayor. Solo presenta: cada pagina carga los registros con el
 * endpoint que le corresponde a su rol. La lista en si es
 * SignosVitalesLista, la misma que usa el historial de la persona mayor.
 */
@Component({
  selector: 'app-signos-vitales-modal',
  imports: [SignosVitalesLista],
  templateUrl: './signos-vitales-modal.html',
  styleUrl: './signos-vitales-modal.css'
})
export class SignosVitalesModal {

  readonly nombre = input<string | null | undefined>(null);
  readonly registros = input<SignoVitalResponse[]>([]);
  readonly cargando = input(false);
  readonly error = input<string | null>(null);

  readonly cerrar = output<void>();
}
