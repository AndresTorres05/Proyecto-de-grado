import { Component, input, output } from '@angular/core';
import { Icon } from '../icon/icon';

/** Color de la etiqueta de estado: activo (verde) o pendiente. */
export type PersonCardEstado = 'activo' | 'pendiente';

/**
 * Tarjeta estándar para mostrar una persona (persona mayor, acompañante o
 * contacto de emergencia) en los listados de los paneles: nombre, celular,
 * relación opcional y un estado. Las acciones de cada página (aceptar,
 * cancelar el vínculo...) se proyectan con el atributo card-actions.
 */
@Component({
  selector: 'app-person-card',
  imports: [Icon],
  templateUrl: './person-card.html',
  styleUrl: './person-card.css',
  host: {
    '[class.person-card--clickable]': 'clickable()',
    '[class.person-card--selected]': 'seleccionada()',
    '[attr.role]': 'clickable() ? "button" : null',
    '[attr.tabindex]': 'clickable() ? 0 : null',
    '[attr.aria-pressed]': 'clickable() ? seleccionada() : null',
    '(click)': 'onClick()',
    '(keydown.enter)': 'onClick()'
  }
})
export class PersonCard {

  readonly nombre = input.required<string>();
  /** Texto pequeño sobre el nombre, por ejemplo "Persona mayor". */
  readonly etiqueta = input('');
  readonly celular = input('');
  readonly relacion = input<string | null | undefined>(null);
  readonly icono = input('user');

  readonly estado = input<string | null>(null);
  readonly estadoTipo = input<PersonCardEstado>('activo');

  /** Si es true, toda la tarjeta funciona como botón (clic o Enter) y emite cardClick. */
  readonly clickable = input(false);
  readonly seleccionada = input(false);
  readonly cardClick = output<void>();

  protected onClick(): void {
    if (this.clickable()) {
      this.cardClick.emit();
    }
  }
}
