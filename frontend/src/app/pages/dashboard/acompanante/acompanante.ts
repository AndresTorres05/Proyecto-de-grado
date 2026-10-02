import { Component, OnInit, signal } from '@angular/core';

import { Icon } from '../../../shared/icon/icon';

import {
  ActividadService,
  Actividad,
  separarPorFecha
} from '../../../core/actividades/actividad.service';

import {
  AcompananteService,
  PersonaMayorAcompanada
} from '../../../core/acompanantes/acompanante.service';

import { AuthService } from '../../../core/auth/auth.service';
import { alCambiar } from '../../../core/tiempo-real/tiempo-real.service';

import { PANEL_CONFIG } from '../../../shared/panel-config/panel-config';

import { DatePipe, registerLocaleData } from '@angular/common';
import localeEs from '@angular/common/locales/es-CO';

/** Recordatorio de la lista "Recordatorios de hoy". */
interface Recordatorio {
  hora: string;
  detalle: string;
  persona: string;
}

/** Alerta sobre una persona mayor. */
interface AlertaConsulta {
  nombre: string;
  descripcion: string;
  prioridad: 'Alta' | 'Media' | 'Baja';
}

/**
 * Inicio del panel del acompañante: sus personas mayores, recordatorios,
 * alertas y próximas actividades. Las personas y las actividades vienen del
 * backend; los recordatorios y las alertas todavía son datos de ejemplo.
 */
@Component({
  selector: 'app-acompanante-dashboard',
  imports: [Icon, DatePipe],
  templateUrl: './acompanante.html',
  styleUrl: './acompanante.css'
})
export class AcompananteDashboard implements OnInit {

  protected readonly panelConfig = PANEL_CONFIG['ACOMPANANTE'];

  protected readonly navItems = this.panelConfig.navItems;

  protected readonly fechaActual = new Date();

  protected readonly nombreUsuario: string;

  constructor(
    private actividadService: ActividadService,
    private acompananteService: AcompananteService,
    private authService: AuthService
  ) {
    this.nombreUsuario = this.authService.getNombreUsuario();

    // Se recarga cuando otro usuario cambia actividades o vínculos.
    alCambiar(['actividades'], () => this.cargarActividades());
    alCambiar(['acompanamientos', 'usuarios'], () => this.cargarPersonasMayores());
  }

  ngOnInit(): void {
    this.cargarActividades();
    this.cargarPersonasMayores();
  }

  private cargarActividades(): void {
    this.actividadService.listar().subscribe((actividades) =>
      // Solo las próximas, de la más cercana a la más lejana.
      this.actividades.set(separarPorFecha(actividades).proximas)
    );
  }

  private cargarPersonasMayores(): void {
    this.acompananteService.obtenerPersonasMayores().subscribe(
      (personasMayores) =>
        this.personasMayores.set(personasMayores)
    );
  }

  /** Personas mayores con vínculo aceptado. */
  protected readonly personasMayores =
    signal<PersonaMayorAcompanada[]>([]);

  /** Datos de ejemplo: todavía no salen del backend. */
  protected readonly recordatorios: Recordatorio[] = [
    {
      hora: '10:00 a.m.',
      detalle: 'Losartán 50mg',
      persona: 'Carlos Julio Méndez'
    },
    {
      hora: '2:00 p.m.',
      detalle: 'Control de presión arterial',
      persona: 'Rosa Elvira Gómez'
    },
    {
      hora: '6:00 p.m.',
      detalle: 'Metformina 850mg',
      persona: 'Carlos Julio Méndez'
    }
  ];

  /** Datos de ejemplo: todavía no salen del backend. */
  protected readonly alertas: AlertaConsulta[] = [
    {
      nombre: 'Rosa Elvira Gómez',
      descripcion:
        'Sin registro de visita hace 15 días. Requiere seguimiento prioritario.',
      prioridad: 'Alta'
    }
  ];

  /** Próximas actividades de las organizaciones de sus personas mayores. */
  protected readonly actividades = signal<Actividad[]>([]);
}