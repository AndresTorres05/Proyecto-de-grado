import { Component } from '@angular/core';
import { Icon } from '../../../../shared/icon/icon';

@Component({
  selector: 'app-mission',
  imports: [Icon],
  templateUrl: './mission.html',
  styleUrl: './mission.css'
})
export class Mission {
  protected readonly pillars = [
    {
      icon: 'handshake',
      title: 'Colaboración real',
      text: 'Todos los actores conectados en tiempo real, coordinados para el mismo objetivo.'
    },
    {
      icon: 'smartphone',
      title: 'Tecnología accesible',
      text: 'Diseñada para que cualquier persona, con o sin conocimiento técnico, pueda usarla.'
    },
    {
      icon: 'lightbulb',
      title: 'Impacto medible',
      text: 'Cada acción queda registrada y se convierte en datos para mejorar continuamente.'
    }
  ];
}
