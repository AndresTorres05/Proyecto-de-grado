import { Component, OnInit, signal } from '@angular/core';

import { DashboardShell } from '../../../shared/dashboard-shell/dashboard-shell';
import { Icon } from '../../../shared/icon/icon';

import {
  ActividadService,
  Actividad
} from '../../../core/actividades/actividad.service';

import {
  AcompananteService,
  PersonaMayorAcompanada
} from '../../../core/acompanantes/acompanante.service';

import { AuthService } from '../../../core/auth/auth.service';

import { PANEL_CONFIG } from '../../../shared/panel-config/panel-config';

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
  parentesco: string;
  telefono: string;
  persona: string;
}

@Component({
  selector: 'app-acompanante-dashboard',
  imports: [DashboardShell, Icon],
  templateUrl: './acompanante.html',
  styleUrl: './acompanante.css'
})
export class AcompananteDashboard implements OnInit {

  // =========================================================
  // CONFIGURACIÓN DEL PANEL
  // =========================================================

  protected readonly panelConfig = PANEL_CONFIG['ACOMPANANTE'];

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
    private acompananteService: AcompananteService,
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
      parentesco: 'Hija',
      telefono: '300 456 7890',
      persona: 'Rosa Elvira Gómez'
    },
    {
      nombre: 'Pedro Méndez',
      parentesco: 'Hijo',
      telefono: '311 222 3344',
      persona: 'Carlos Julio Méndez'
    }
  ];

  // =========================================================
  // ACTIVIDADES
  // =========================================================

  protected readonly actividades = signal<Actividad[]>([]);
}