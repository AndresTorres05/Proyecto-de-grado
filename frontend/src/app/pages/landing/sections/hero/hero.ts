import { Component } from '@angular/core';
import { Icon } from '../../../../shared/icon/icon';

@Component({
  selector: 'app-hero',
  imports: [Icon],
  templateUrl: './hero.html',
  styleUrl: './hero.css'
})
export class Hero {
  protected readonly stats = [
    { value: '2,200+', label: 'Personas mayores' },
    { value: '840+', label: 'Acompañantes activos' },
    { value: '95%', label: 'Satisfacción' }
  ];

  protected readonly floatCards = [
    { icon: 'bell', label: 'Recordatorio', text: 'Medicación 14:00', accent: false },
    { icon: 'activity', label: 'Actividad', text: 'Fisioterapia', accent: true },
    { icon: 'bar-chart', label: 'Indicador', text: 'Todo en orden', accent: false },
    { icon: 'monitor', label: 'Dashboard', text: 'Ver en vivo', accent: false },
    { icon: 'alert-triangle', label: 'Alerta', text: 'Revisar visita', accent: true }
  ];

  protected readonly rolePills = [
    { label: 'Institución', className: 'pill-institution' },
    { label: 'Persona Mayor', className: 'pill-elder' },
    { label: 'Acompañante', className: 'pill-companion' },
    { label: 'Voluntario', className: 'pill-volunteer' }
  ];
}
