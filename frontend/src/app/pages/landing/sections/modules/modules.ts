import { Component } from '@angular/core';
import { Icon } from '../../../../shared/icon/icon';

@Component({
  selector: 'app-modules',
  imports: [Icon],
  templateUrl: './modules.html',
  styleUrl: './modules.css'
})
export class Modules {
  protected readonly modules: { icon: string; title: string; subtitle: string; badge?: string }[] = [
    { icon: 'user', title: 'Personas Mayores', subtitle: 'Expedientes digitales', badge: 'Nuevo' },
    { icon: 'handshake', title: 'Acompañantes', subtitle: 'Gestión y asignación', badge: 'Nuevo' },
    { icon: 'star', title: 'Voluntarios', subtitle: 'Coordinación y tareas', badge: 'Nuevo' },
    { icon: 'building', title: 'Organizaciones', subtitle: 'Alianzas y redes' },
    { icon: 'pill', title: 'Medicamentos', subtitle: 'Control y alertas' },
    { icon: 'activity', title: 'Actividades', subtitle: 'Programación y seguimiento' },
    { icon: 'bell', title: 'Alertas', subtitle: 'Notificaciones críticas' },
    { icon: 'bar-chart', title: 'Analítica', subtitle: 'Inteligencia de datos' }
  ];
}
