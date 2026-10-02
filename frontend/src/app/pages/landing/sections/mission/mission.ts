import { Component } from '@angular/core';

/**
 * Sección "Nuestra misión" de la landing: introducción y los tres pilares
 * del proyecto.
 */
@Component({
  selector: 'app-mission',
  imports: [],
  templateUrl: './mission.html',
  styleUrl: './mission.css'
})
export class Mission {
  protected readonly pillars = [
    {
      icon: '🤝',
      title: 'Colaboración real',
      text: 'Todos los actores conectados en tiempo real, coordinados para el mismo objetivo.'
    },
    {
      icon: '📱',
      title: 'Tecnología accesible',
      text: 'Diseñada para que cualquier persona, con o sin conocimiento técnico, pueda usarla.'
    },
    {
      icon: '💡',
      title: 'Impacto medible',
      text: 'Cada acción queda registrada y se convierte en datos para mejorar continuamente.'
    }
  ];
}
