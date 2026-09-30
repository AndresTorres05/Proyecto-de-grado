import { Component, OnInit, signal } from '@angular/core';

import { DashboardShell } from '../../../shared/dashboard-shell/dashboard-shell';
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

interface Recordatorio {
  hora: string;
  detalle: string;
  persona: string;
}

interface AlertaConsulta {
  nombre: string;
  descripcion: string;
  prioridad: 'Alta' | 'Media' | 'Baja';
}

interface ContactoEmergencia {
  nombre: string;
  relacion: string;
  celular: string;
  persona: string;
}

@Component({
  selector: 'app-acompanante-dashboard',
  imports: [DashboardShell, Icon, DatePipe],
  templateUrl: './acompanante.html',
  styleUrl: './acompanante.css'
})
export class AcompananteDashboard implements OnInit {

  // =========================================================
  // CONFIGURACIÓN DEL PANEL
  // =========================================================

  protected readonly panelConfig = PANEL_CONFIG['ACOMPANANTE'];

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
    private acompananteService: AcompananteService,
    private authService: AuthService
  ) {
    this.nombreUsuario = this.authService.getNombreUsuario();

    alCambiar(['actividades'], () => this.cargarActividades());
    alCambiar(['acompanamientos', 'usuarios'], () => this.cargarPersonasMayores());
  }

  // =========================================================
  // INICIALIZACIÓN
  // =========================================================

  ngOnInit(): void {
    this.cargarActividades();
    this.cargarPersonasMayores();
  }

  private cargarActividades(): void {
    this.actividadService.listar().subscribe((actividades) =>
      // Solo las próximas, de la más cercana a la más lejana
      this.actividades.set(separarPorFecha(actividades).proximas)
    );
  }

  private cargarPersonasMayores(): void {
    this.acompananteService.obtenerPersonasMayores().subscribe(
      (personasMayores) =>
        this.personasMayores.set(personasMayores)
    );
  }

  // =========================================================
  // PERSONAS MAYORES
  // =========================================================

  protected readonly personasMayores =
    signal<PersonaMayorAcompanada[]>([]);

  // =========================================================
  // RECORDATORIOS
  // =========================================================

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

  // =========================================================
  // ALERTAS
  // =========================================================

  protected readonly alertas: AlertaConsulta[] = [
    {
      nombre: 'Rosa Elvira Gómez',
      descripcion:
        'Sin registro de visita hace 15 días. Requiere seguimiento prioritario.',
      prioridad: 'Alta'
    }
  ];

  // =========================================================
  // CONTACTOS DE EMERGENCIA
  // =========================================================

  protected readonly contactos: ContactoEmergencia[] = [
    {
      nombre: 'Marta Gómez',
      relacion: 'Hija',
      celular: '300 456 7890',
      persona: 'Rosa Elvira Gómez'
    },
    {
      nombre: 'Pedro Méndez',
      relacion: 'Hijo',
      celular: '311 222 3344',
      persona: 'Carlos Julio Méndez'
    }
  ];

  // =========================================================
  // ACTIVIDADES
  // =========================================================

  protected readonly actividades = signal<Actividad[]>([]);
}