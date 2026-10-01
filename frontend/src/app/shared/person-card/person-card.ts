import { Component, input, output } from '@angular/core';
import { Icon } from '../icon/icon';

export type PersonCardEstado = 'activo' | 'pendiente';

/**
 * Tarjeta estandar para mostrar una persona (persona mayor, acompanante o
 * contacto de emergencia) en los listados de los paneles. Muestra identidad,
 * celular, relacion opcional, y un estado.
 * Las acciones propias de cada pagina (aceptar, cancelar asociacion...) se
 * proyectan con el atributo `card-actions`.
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
  readonly etiqueta = input('');
  readonly celular = input('');
  readonly relacion = input<string | null | undefined>(null);
  readonly icono = input('user');

  readonly estado = input<string | null>(null);
  readonly estadoTipo = input<PersonCardEstado>('activo');

  readonly clickable = input(false);
  readonly seleccionada = input(false);
  readonly cardClick = output<void>();

  protected onClick(): void {
    if (this.clickable()) {
      this.cardClick.emit();
    }
  }
}
