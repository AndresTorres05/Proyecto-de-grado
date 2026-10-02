import { Component, input, output } from '@angular/core';

import { Icon } from '../icon/icon';

/** Acompañante tal como lo muestra el modal. */
export interface AcompananteResumen {
  idUsuario: number;
  nombre: string;
  celular: string;
  relacion: string | null;
}

/**
 * Modal estándar con los acompañantes de una persona mayor. Solo
 * presenta: cada página carga la lista con el endpoint que le
 * corresponde a su rol.
 */
@Component({
  selector: 'app-acompanantes-modal',
  imports: [Icon],
  templateUrl: './acompanantes-modal.html',
  styleUrl: './acompanantes-modal.css'
})
export class AcompanantesModal {

  readonly nombre = input<string | null | undefined>(null);
  readonly acompanantes = input<AcompananteResumen[]>([]);
  readonly cargando = input(false);
  readonly error = input<string | null>(null);

  readonly cerrar = output<void>();
}
