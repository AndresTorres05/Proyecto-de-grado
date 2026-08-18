import { Component, OnInit, signal } from '@angular/core';
import { DashboardShell, ShellNavItem } from '../../../shared/dashboard-shell/dashboard-shell';
import { ActividadService, Actividad } from '../../../core/actividades/actividad.service';

interface StatCard {
  icon: string;
  value: string;
  label: string;
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
export class VoluntarioDashboard implements OnInit {
  constructor(private actividadService: ActividadService) {}

  ngOnInit(): void {
    this.actividadService.listar().subscribe((actividades) => this.actividades.set(actividades));
  }

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

  protected readonly actividades = signal<Actividad[]>([]);

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
