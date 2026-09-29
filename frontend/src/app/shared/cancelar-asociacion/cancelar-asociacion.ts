import { Component, input, output } from '@angular/core';
import { Icon } from '../icon/icon';

/**
 * Confirmacion estandar para cancelar la asociacion con una persona
 * (persona mayor, acompanante u organizacion). La pagina decide cuando
 * mostrarlo y que hacer al confirmar.
 */
@Component({
  selector: 'app-cancelar-asociacion',
  imports: [Icon],
  templateUrl: './cancelar-asociacion.html',
  styleUrl: './cancelar-asociacion.css'
})
export class CancelarAsociacion {

  readonly nombre = input<string | null | undefined>(null);

  /** Que pasa despues de cancelar, visto desde quien cancela. */
  readonly advertencia = input('');

  readonly confirmar = output<void>();
  readonly cerrar = output<void>();
}
