import { Component, OnInit, signal } from '@angular/core';

import { Icon } from '../../../shared/icon/icon';

import {
  ActividadService,
  Actividad,
  separarPorFecha
} from '../../../core/actividades/actividad.service';

import { AuthService } from '../../../core/auth/auth.service';
import { alCambiar } from '../../../core/tiempo-real/tiempo-real.service';

import { PANEL_CONFIG } from '../../../shared/panel-config/panel-config';

import { DatePipe, registerLocaleData } from '@angular/common';
import localeEs from '@angular/common/locales/es-CO';

registerLocaleData(localeEs);

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
  imports: [Icon, DatePipe],
  templateUrl: './voluntario.html',
  styleUrl: './voluntario.css'
})
export class VoluntarioDashboard implements OnInit {

  // =========================================================
  // CONFIGURACIÓN DEL PANEL
  // =========================================================

  protected readonly panelConfig = PANEL_CONFIG['VOLUNTARIO'];

  protected readonly navItems = this.panelConfig.navItems;

  protected readonly fechaActual = new Date();

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

    alCambiar(['actividades'], () => this.cargarActividades());
  }

  // =========================================================
  // INICIALIZACIÓN
  // =========================================================

  ngOnInit(): void {
    this.cargarActividades();
  }

  private cargarActividades(): void {
    this.actividadService.listar().subscribe((actividades) =>
      // Solo las próximas, de la más cercana a la más lejana
      this.actividades.set(separarPorFecha(actividades).proximas)
    );
  }

  // =========================================================
  // ESTADÍSTICAS
  // =========================================================

  protected readonly stats: StatCard[] = [
    {
      icon: 'activity',
      value: '8',
      label: 'Actividades este mes'
    },
    {
      icon: 'clock',
      value: '24h',
      label: 'Horas de acompañamiento'
    },
    {
      icon: 'users',
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