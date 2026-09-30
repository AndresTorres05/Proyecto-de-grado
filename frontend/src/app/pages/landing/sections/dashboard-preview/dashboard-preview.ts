import { Component } from '@angular/core';
import { Icon } from '../../../../shared/icon/icon';

interface DashboardStat {
  icon: string;
  value: string;
  delta: string;
  label: string;
}

@Component({
  selector: 'app-dashboard-preview',
  imports: [Icon],
  templateUrl: './dashboard-preview.html',
  styleUrl: './dashboard-preview.css'
})
export class DashboardPreview {
  protected readonly stats: DashboardStat[] = [
    {
      icon: 'users',
      value: '2,210',
      delta: '+12%',
      label: 'Personas mayores registradas'
    },
    {
      icon: 'handshake',
      value: '740',
      delta: '+8%',
      label: 'Acompañantes activos'
    },
    {
      icon: 'star',
      value: '460',
      delta: '+15%',
      label: 'Voluntarios en programa'
    },
    {
      icon: 'building',
      value: '38',
      delta: '+3%',
      label: 'Instituciones aliadas'
    }
  ];
}
