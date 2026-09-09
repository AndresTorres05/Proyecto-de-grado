import { Component, OnInit, signal } from '@angular/core';

import { FormsModule } from '@angular/forms';

import { DashboardShell } from '../../../shared/dashboard-shell/dashboard-shell';
import { Icon } from '../../../shared/icon/icon';

import {
  ActividadService,
  Actividad,
  ActividadRequest
} from '../../../core/actividades/actividad.service';

import { AuthService } from '../../../core/auth/auth.service';
import { OrganizacionService } from '../../../core/organizacion/organizacion.service';

import { PANEL_CONFIG } from '../../../shared/panel-config/panel-config';

import { DatePipe, registerLocaleData } from '@angular/common';
import localeEs from '@angular/common/locales/es-CO';

interface StatCard {
  icon: string;
  value: string;
  delta: string;
  label: string;
}

interface Alerta {
  prioridad: 'Alta' | 'Media' | 'Baja';
  nombre: string;
  descripcion: string;
  tiempo: string;
}

interface AccionRapida {
  icon: string;
  label: string;
}

interface Inventario {
  nombre: string;
  estado: string;
  detalle: string;
}

interface Donacion {
  donante: string;
  tipo: string;
  valor: string;
  fecha: string;
}

interface Bitacora {
  usuario: string;
  accion: string;
  tiempo: string;
}

@Component({
  selector: 'app-organizacion-dashboard',
  imports: [DashboardShell, FormsModule, Icon, DatePipe],
  templateUrl: './organizacion.html',
  styleUrl: './organizacion.css'
})
export class OrganizacionDashboard implements OnInit {

  // =========================================================
  // CONFIGURACIÓN DEL PANEL
  // =========================================================

  protected readonly panelConfig = PANEL_CONFIG['ORGANIZACION'];

  protected readonly navItems = this.panelConfig.navItems;

  protected readonly fechaActual = new Date();

  // =========================================================
  // USUARIO
  // =========================================================

  protected readonly nombreUsuario = signal('');

  // =========================================================
  // ESTADÍSTICAS
  // =========================================================

  protected readonly stats: StatCard[] = [
    {
      icon: 'user',
      value: '2,210',
      delta: '+12%',
      label: 'Personas mayores registradas'
    },
    {
      icon: 'users',
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
      icon: 'bell',
      value: '12',
      delta: '3 urgentes',
      label: 'Alertas activas'
    }
  ];

  // =========================================================
  // ALERTAS
  // =========================================================

  protected readonly alertas: Alerta[] = [
    {
      prioridad: 'Alta',
      nombre: 'Rosa Elvira Gómez',
      descripcion:
        'Sin registro de visita hace 15 días. Requiere seguimiento prioritario.',
      tiempo: 'Hace 2 horas'
    },
    {
      prioridad: 'Alta',
      nombre: 'Inventario · Losartán 50mg',
      descripcion:
        'Stock por debajo del mínimo establecido (4 unidades restantes).',
      tiempo: 'Hace 5 horas'
    },
    {
      prioridad: 'Media',
      nombre: 'José Antonio Ruiz',
      descripcion:
        'Condición de salud reportada como prioritaria en la última caracterización.',
      tiempo: 'Ayer'
    },
    {
      prioridad: 'Baja',
      nombre: 'Kit de vendajes',
      descripcion:
        'Próximo a fecha de vencimiento (12 días).',
      tiempo: 'Ayer'
    }
  ];

  // =========================================================
  // ACCIONES RÁPIDAS
  // =========================================================

  protected readonly accionesRapidas: AccionRapida[] = [
    {
      icon: 'user',
      label: 'Registrar persona mayor'
    },
    {
      icon: 'activity',
      label: 'Registrar actividad'
    },
    {
      icon: 'gift',
      label: 'Registrar donación'
    },
    {
      icon: 'clipboard',
      label: 'Generar reporte'
    }
  ];

  // =========================================================
  // ACTIVIDADES
  // =========================================================

  protected readonly actividades = signal<Actividad[]>([]);

  protected readonly errorActividades = signal<string | null>(null);

  protected nuevaActividad: ActividadRequest = {
    nombre: '',
    fecha: null,
    lugar: null,
    tipo: null
  };

  protected actividadEditandoId: number | null = null;

  protected actividadEditando: ActividadRequest = {
    nombre: '',
    fecha: null,
    lugar: null,
    tipo: null
  };

  // =========================================================
  // CONSTRUCTOR
  // =========================================================

constructor(
  private actividadService: ActividadService,
  private authService: AuthService,
  private organizacionService: OrganizacionService
) {
  this.nombreUsuario.set(this.authService.getNombreUsuario());
}

  // =========================================================
  // INICIALIZACIÓN
  // =========================================================

ngOnInit(): void {
  this.cargarActividades();
  this.cargarInformacionOrganizacion();
}


private cargarInformacionOrganizacion(): void {

  this.organizacionService.obtenerInformacion().subscribe({

    next: (data) => {

      console.log('NOMBRE DESDE BACKEND:', data.nombre);

      this.nombreUsuario.set(data.nombre);

      console.log('NOMBRE EN DASHBOARD:', this.nombreUsuario);
    },

    error: (error) => {
      console.error(
        'Error al cargar la información de la organización:',
        error
      );
    }

  });
}

  // =========================================================
  // CARGAR ACTIVIDADES
  // =========================================================


  private cargarActividades(): void {
    this.actividadService.listarMias().subscribe({
      next: (actividades) => {
        this.actividades.set(actividades);
      },
      error: () => {
        this.errorActividades.set(
          'No se pudieron cargar las actividades'
        );
      }
    });
  }

  // =========================================================
  // CREAR ACTIVIDAD
  // =========================================================

  crearActividad(): void {
    if (!this.nuevaActividad.nombre.trim()) {
      return;
    }

    this.errorActividades.set(null);

    this.actividadService.crear(this.nuevaActividad).subscribe({
      next: () => {
        this.nuevaActividad = {
          nombre: '',
          fecha: null,
          lugar: null,
          tipo: null
        };

        this.cargarActividades();
      },

      error: () => {
        this.errorActividades.set(
          'No se pudo crear la actividad'
        );
      }
    });
  }

  // =========================================================
  // EDITAR ACTIVIDAD
  // =========================================================

  editarActividad(actividad: Actividad): void {
    this.actividadEditandoId = actividad.idActividad;

    this.actividadEditando = {
      nombre: actividad.nombre,
      fecha: actividad.fecha,
      lugar: actividad.lugar,
      tipo: actividad.tipo
    };
  }

  // =========================================================
  // CANCELAR EDICIÓN
  // =========================================================

  cancelarEdicionActividad(): void {
    this.actividadEditandoId = null;

    this.actividadEditando = {
      nombre: '',
      fecha: null,
      lugar: null,
      tipo: null
    };
  }

  // =========================================================
  // GUARDAR ACTIVIDAD EDITADA
  // =========================================================

  guardarActividad(): void {
    if (
      this.actividadEditandoId === null ||
      !this.actividadEditando.nombre.trim()
    ) {
      return;
    }

    this.errorActividades.set(null);

    this.actividadService
      .actualizar(
        this.actividadEditandoId,
        this.actividadEditando
      )
      .subscribe({
        next: () => {
          this.cancelarEdicionActividad();
          this.cargarActividades();
        },

        error: () => {
          this.errorActividades.set(
            'No se pudo actualizar la actividad'
          );
        }
      });
  }

  // =========================================================
  // ELIMINAR ACTIVIDAD
  // =========================================================

  eliminarActividad(actividad: Actividad): void {
    this.errorActividades.set(null);

    this.actividadService
      .eliminar(actividad.idActividad)
      .subscribe({
        next: () => {
          this.cargarActividades();
        },

        error: () => {
          this.errorActividades.set(
            'No se pudo eliminar la actividad'
          );
        }
      });
  }

  // =========================================================
  // INVENTARIO
  // =========================================================

  protected readonly inventario: Inventario[] = [
    {
      nombre: 'Losartán 50mg',
      estado: 'Stock bajo',
      detalle: '4 unidades disponibles'
    },
    {
      nombre: 'Kit de vendajes',
      estado: 'Por vencer',
      detalle: 'Vence en 12 días'
    },
    {
      nombre: 'Metformina 850mg',
      estado: 'Stock bajo',
      detalle: '9 unidades disponibles'
    }
  ];

  // =========================================================
  // DONACIONES
  // =========================================================

  protected readonly donaciones: Donacion[] = [
    {
      donante: 'Fundación Manos Amigas',
      tipo: 'Monetaria',
      valor: '$1.200.000',
      fecha: '08 ago 2026'
    },
    {
      donante: 'Supermercado La Colina',
      tipo: 'Alimentos',
      valor: '35 kits',
      fecha: '06 ago 2026'
    },
    {
      donante: 'Anónimo',
      tipo: 'Monetaria',
      valor: '$300.000',
      fecha: '04 ago 2026'
    }
  ];

  // =========================================================
  // BITÁCORA
  // =========================================================

  protected readonly bitacora: Bitacora[] = [
    {
      usuario: 'Voluntario · Camilo Rey',
      accion: 'registró asistencia en Fisioterapia grupal',
      tiempo: 'Hace 34 min'
    },
    {
      usuario: 'Acompañante · Laura Peña',
      accion:
        'registró una visita de seguimiento a Rosa Elvira Gómez',
      tiempo: 'Hace 1 hora'
    },
    {
      usuario: 'Sistema',
      accion:
        'generó una alerta por inventario bajo de Losartán 50mg',
      tiempo: 'Hace 5 horas'
    },
    {
      usuario: 'Organización · Ana Torres',
      accion:
        'actualizó la caracterización de José Antonio Ruiz',
      tiempo: 'Ayer'
    }
  ];

  // =========================================================
  // GRÁFICA
  // =========================================================

  protected readonly donutLegend = [
    {
      color: 'var(--vita-navy)',
      label: 'Movilidad 40%'
    },
    {
      color: 'var(--vita-navy-light)',
      label: 'Salud mental 25%'
    },
    {
      color: 'var(--vita-orange)',
      label: 'Salud física 20%'
    },
    {
      color: 'var(--vita-gold)',
      label: 'Otros 15%'
    }
  ];
}