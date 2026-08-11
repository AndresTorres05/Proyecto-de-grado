import { Component } from '@angular/core';
import { DashboardShell, ShellNavItem } from '../../../shared/dashboard-shell/dashboard-shell';

interface StatCard {
  icon: string;
  value: string;
  label: string;
}

interface ActividadProgramada {
  nombre: string;
  fecha: string;
  lugar: string;
  estado: 'Confirmada' | 'Pendiente';
}

interface Disponibilidad {
  dia: string;
  disponible: boolean;
}

interface AlertaConsulta {
  nombre: string;
  descripcion: string;
  prioridad: 'Alta' | 'Media' | 'Baja';
}

@Component({
  selector: 'app-voluntario-dashboard',
  imports: [DashboardShell],
  templateUrl: './voluntario.html',
  styleUrl: './voluntario.css'
})
export class VoluntarioDashboard {
  protected readonly navItems: ShellNavItem[] = [
    { icon: '🏠', label: 'Inicio', active: true },
    { icon: '🏃', label: 'Mis actividades' },
    { icon: '🗓️', label: 'Disponibilidad' },
    { icon: '🔔', label: 'Alertas' },
    { icon: '🧓', label: 'Personas que acompaño' },
    { icon: '👤', label: 'Mi perfil' }
  ];

  protected readonly stats: StatCard[] = [
    { icon: '🏃', value: '8', label: 'Actividades este mes' },
    { icon: '⏱️', value: '24h', label: 'Horas de acompañamiento' },
    { icon: '🧓', value: '15', label: 'Personas mayores apoyadas' }
  ];

  protected readonly proximaActividad = {
    nombre: 'Fisioterapia grupal',
    fecha: 'Hoy · 3:00 p.m. — 4:30 p.m.',
    lugar: 'Centro de salud San Cristóbal',
    participantes: 12
  };

  protected readonly actividades: ActividadProgramada[] = [
    { nombre: 'Fisioterapia grupal', fecha: 'Hoy · 3:00 p.m.', lugar: 'Centro de salud San Cristóbal', estado: 'Confirmada' },
    { nombre: 'Jornada de vacunación', fecha: 'Mañana · 8:00 a.m.', lugar: 'UPL Entrenubes', estado: 'Confirmada' },
    { nombre: 'Encuentro intergeneracional', fecha: 'Vie 14 ago · 2:00 p.m.', lugar: 'Parque Entrenubes', estado: 'Pendiente' },
    { nombre: 'Taller de memoria y cognición', fecha: 'Lun 17 ago · 10:00 a.m.', lugar: 'Salón comunal Entrenubes', estado: 'Pendiente' }
  ];

  protected readonly disponibilidad: Disponibilidad[] = [
    { dia: 'Lun', disponible: true },
    { dia: 'Mar', disponible: true },
    { dia: 'Mié', disponible: false },
    { dia: 'Jue', disponible: true },
    { dia: 'Vie', disponible: true },
    { dia: 'Sáb', disponible: false },
    { dia: 'Dom', disponible: false }
  ];

  protected readonly alertas: AlertaConsulta[] = [
    {
      nombre: 'Jornada de vacunación',
      descripcion: 'Se requieren 2 voluntarios adicionales para mañana.',
      prioridad: 'Media'
    },
    {
      nombre: 'Rosa Elvira Gómez',
      descripcion: 'Sin visita registrada hace 15 días. Revisar antes de la próxima actividad.',
      prioridad: 'Alta'
    }
  ];
}
