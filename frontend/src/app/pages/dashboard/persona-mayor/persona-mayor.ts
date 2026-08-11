import { Component } from '@angular/core';
import { DashboardShell, ShellNavItem } from '../../../shared/dashboard-shell/dashboard-shell';

interface Actividad {
  icon: string;
  nombre: string;
  cuando: string;
  lugar: string;
}

interface AccesoRapido {
  icon: string;
  label: string;
}

@Component({
  selector: 'app-persona-mayor-dashboard',
  imports: [DashboardShell],
  templateUrl: './persona-mayor.html',
  styleUrl: './persona-mayor.css'
})
export class PersonaMayorDashboard {
  protected readonly navItems: ShellNavItem[] = [
    { icon: '🏠', label: 'Inicio', active: true },
    { icon: '🏃', label: 'Mis actividades' },
    { icon: '⏰', label: 'Mis recordatorios' },
    { icon: '👤', label: 'Mi información' },
    { icon: '☎️', label: 'Mis contactos' }
  ];

  protected readonly recordatorio = {
    hora: '2:00 p.m.',
    detalle: 'Tomar Losartán 50mg',
    nota: 'Con un vaso de agua, después de almorzar.'
  };

  protected readonly actividades: Actividad[] = [
    { icon: '🏃', nombre: 'Fisioterapia grupal', cuando: 'Hoy · 3:00 p.m.', lugar: 'Centro de salud San Cristóbal' },
    { icon: '🎨', nombre: 'Taller de memoria', cuando: 'Lunes · 10:00 a.m.', lugar: 'Salón comunal Entrenubes' }
  ];

  protected readonly acompanante = {
    nombre: 'Laura Peña',
    rol: 'Tu acompañante',
    telefono: '310 555 2233'
  };

  protected readonly accesos: AccesoRapido[] = [
    { icon: '👤', label: 'Mi información' },
    { icon: '☎️', label: 'Mis contactos de emergencia' }
  ];
}
