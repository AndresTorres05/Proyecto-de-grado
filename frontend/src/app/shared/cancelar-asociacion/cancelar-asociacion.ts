import { Component, input, output } from '@angular/core';
import { Icon } from '../icon/icon';

/**
 * Confirmación estándar para cancelar el vínculo con una persona mayor, un
 * acompañante o una organización. La página decide cuándo mostrarla y qué
 * hacer al confirmar.
 */
@Component({
  selector: 'app-cancelar-asociacion',
  imports: [Icon],
  templateUrl: './cancelar-asociacion.html',
  styleUrl: './cancelar-asociacion.css'
})
export class CancelarAsociacion {

  /** Con quién se cancela el vínculo. */
  readonly nombre = input<string | null | undefined>(null);

  /** Qué pasa después de cancelar, visto desde quien cancela. */
  readonly advertencia = input('');

  readonly confirmar = output<void>();
  readonly cerrar = output<void>();
}
