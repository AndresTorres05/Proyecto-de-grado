import { Component, OnInit, signal } from '@angular/core';

import { DashboardShell } from '../../../shared/dashboard-shell/dashboard-shell';

import {
  ActividadService,
  Actividad
} from '../../../core/actividades/actividad.service';

import { AuthService } from '../../../core/auth/auth.service';

import { PANEL_CONFIG } from '../../../shared/panel-config/panel-config';

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

  // =========================================================
  // CONFIGURACIÓN DEL PANEL
  // =========================================================

  protected readonly panelConfig = PANEL_CONFIG['VOLUNTARIO'];

  protected readonly navItems = this.panelConfig.navItems;

  // =========================================================
  // USUARIO
  // =========================================================

  protected readonly nombreUsuario: string;

  // =========================================================
  // CONSTRUCTOR
  // =========================================================

  constructor(
    private actividadService: ActividadService,
    private authService: AuthService
  ) {
    this.nombreUsuario = this.authService.getNombreUsuario();
  }

  // =========================================================
  // INICIALIZACIÓN
  // =========================================================

  ngOnInit(): void {
    this.actividadService.listar().subscribe((actividades) =>
      this.actividades.set(actividades)
    );
  }

  // =========================================================
  // ESTADÍSTICAS
  // =========================================================

  protected readonly stats: StatCard[] = [
    {
      icon: '🏃',
      value: '8',
      label: 'Actividades este mes'
    },
    {
      icon: '⏱️',
      value: '24h',
      label: 'Horas de acompañamiento'
    },
    {
      icon: '🧓',
      value: '15',
      label: 'Personas mayores apoyadas'
    }
  ];

  // =========================================================
  // PRÓXIMA ACTIVIDAD
  // =========================================================

  protected readonly proximaActividad = {
    nombre: 'Fisioterapia grupal',
    fecha: 'Hoy · 3:00 p.m. — 4:30 p.m.',
    lugar: 'Centro de salud San Cristóbal',
    participantes: 12
  };

  // =========================================================
  // ACTIVIDADES
  // =========================================================

  protected readonly actividades = signal<Actividad[]>([]);

  // =========================================================
  // DISPONIBILIDAD
  // =========================================================

  protected readonly disponibilidad: Disponibilidad[] = [
    {
      dia: 'Lun',
      disponible: true
    },
    {
      dia: 'Mar',
      disponible: true
    },
    {
      dia: 'Mié',
      disponible: false
    },
    {
      dia: 'Jue',
      disponible: true
    },
    {
      dia: 'Vie',
      disponible: true
    },
    {
      dia: 'Sáb',
      disponible: false
    },
    {
      dia: 'Dom',
      disponible: false
    }
  ];

  // =========================================================
  // ALERTAS
  // =========================================================

  protected readonly alertas: AlertaConsulta[] = [
    {
      nombre: 'Jornada de vacunación',
      descripcion:
        'Se requieren 2 voluntarios adicionales para mañana.',
      prioridad: 'Media'
    },
    {
      nombre: 'Rosa Elvira Gómez',
      descripcion:
        'Sin visita registrada hace 15 días. Revisar antes de la próxima actividad.',
      prioridad: 'Alta'
    }
  ];
}