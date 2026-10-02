import { Component, OnInit, signal } from '@angular/core';

import { FormsModule } from '@angular/forms';

import { Icon } from '../../../shared/icon/icon';

import { RouterLink } from '@angular/router';

import {
  ActividadService,
  Actividad,
  separarPorFecha,
  ActividadRequest
} from '../../../core/actividades/actividad.service';

import { AuthService } from '../../../core/auth/auth.service';
import { OrganizacionService } from '../../../core/organizacion/organizacion.service';
import { alCambiar } from '../../../core/tiempo-real/tiempo-real.service';

import { PANEL_CONFIG } from '../../../shared/panel-config/panel-config';

import { DatePipe, registerLocaleData } from '@angular/common';
import localeEs from '@angular/common/locales/es-CO';

registerLocaleData(localeEs);

/** Tarjeta de indicador. */
interface StatCard {
  icon: string;
  value: string;
  delta: string;
  label: string;
}

/** Alerta de la organización. */
interface Alerta {
  prioridad: 'Alta' | 'Media' | 'Baja';
  nombre: string;
  descripcion: string;
  tiempo: string;
}

/** Botón de acceso rápido a otra sección del panel. */
interface AccionRapida {
  icon: string;
  label: string;
  route: string;
  /**
   * Si es true, el enlace lleva ?abrir=registrar para que la página destino
   * (Personas mayores o Actividades) abra su formulario de una vez.
   */
  abrirFormulario?: boolean;
}

/** Insumo del inventario. */
interface Inventario {
  nombre: string;
  estado: string;
  detalle: string;
}

/** Donación recibida. */
interface Donacion {
  donante: string;
  tipo: string;
  valor: string;
  fecha: string;
}

/** Entrada de la bitácora de cambios. */
interface Bitacora {
  usuario: string;
  accion: string;
  tiempo: string;
}

/**
 * Inicio del panel de la organización. Las actividades (con su CRUD rápido)
 * y el nombre de la organización vienen del backend; los indicadores, las
 * alertas, las gráficas, el inventario, las donaciones y la bitácora todavía
 * son datos de ejemplo.
 */
@Component({
  selector: 'app-organizacion-dashboard',
  imports: [FormsModule, Icon, DatePipe, RouterLink],
  templateUrl: './organizacion.html',
  styleUrl: './organizacion.css'
})
export class OrganizacionDashboard implements OnInit {

  protected readonly panelConfig = PANEL_CONFIG['ORGANIZACION'];

  protected readonly navItems = this.panelConfig.navItems;

  protected readonly fechaActual = new Date();

  /** Nombre de la organización; se actualiza si lo cambian en "Mi información". */
  protected readonly nombreUsuario = signal('');

  /** Datos de ejemplo. */
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

  /** Datos de ejemplo. */
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

protected readonly accionesRapidas: AccionRapida[] = [
  {
    icon: 'user',
    label: 'Registrar persona mayor',
    route: '/panel/organizacion/personas-mayores',
    abrirFormulario: true
  },
  {
    icon: 'activity',
    label: 'Registrar actividad',
    route: '/panel/organizacion/actividades',
    abrirFormulario: true
  },
  {
    icon: 'heart',
    label: 'Registrar signos vitales',
    route: '/panel/organizacion/signos-vitales'
  },
  {
    icon: 'clipboard',
    label: 'Generar reporte',
    route: '/panel/organizacion/reportes' // esta ruta todavía no existe
  }
];

  protected readonly actividades = signal<Actividad[]>([]);

  protected readonly errorActividades = signal<string | null>(null);

  /** Formulario rápido de nueva actividad. */
  protected nuevaActividad: ActividadRequest = {
  nombre: '',
  descripcion: null,
  fecha: null,
  hora: null,
  lugar: null,
  tipo: null,
  cupos: null,
  responsable: null
};

  /** Actividad que se está editando en la lista; null si ninguna. */
  protected actividadEditandoId: number | null = null;

  protected actividadEditando: ActividadRequest = {
  nombre: '',
  descripcion: null,
  fecha: null,
  hora: null,
  lugar: null,
  tipo: null,
  cupos: null,
  responsable: null
};

constructor(
  private actividadService: ActividadService,
  private authService: AuthService,
  private organizacionService: OrganizacionService
) {
  this.nombreUsuario.set(this.authService.getNombreUsuario());

  alCambiar(['actividades'], () => this.cargarActividades());
  alCambiar(['usuarios'], () => this.cargarInformacionOrganizacion());
}

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

  private cargarActividades(): void {
    this.actividadService.listarMias().subscribe({
      next: (actividades) => {
        // Primero las próximas (la más cercana arriba) y luego las ya realizadas.
        const { proximas, pasadas } = separarPorFecha(actividades);
        this.actividades.set([...proximas, ...pasadas]);
      },
      error: () => {
        this.errorActividades.set(
          'No se pudieron cargar las actividades'
        );
      }
    });
  }

  crearActividad(): void {
    if (!this.nuevaActividad.nombre.trim()) {
      return;
    }

    this.errorActividades.set(null);

    this.actividadService.crear(this.nuevaActividad).subscribe({
      next: () => {
this.nuevaActividad = {
  nombre: '',
  descripcion: null,
  fecha: null,
  hora: null,
  lugar: null,
  tipo: null,
  cupos: null,
  responsable: null
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

  /** Pasa una fila de la lista a modo edición. */
  editarActividad(actividad: Actividad): void {
    this.actividadEditandoId = actividad.idActividad;

this.actividadEditando = {
  nombre: actividad.nombre,
  descripcion: actividad.descripcion,
  fecha: actividad.fecha,
  hora: actividad.hora,
  lugar: actividad.lugar,
  tipo: actividad.tipo,
  cupos: actividad.cupos,
  responsable: actividad.responsable
};
  }

  cancelarEdicionActividad(): void {
    this.actividadEditandoId = null;

this.actividadEditando = {
  nombre: '',
  descripcion: null,
  fecha: null,
  hora: null,
  lugar: null,
  tipo: null,
  cupos: null,
  responsable: null
};
  }

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

  /** Datos de ejemplo. */
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

  /** Datos de ejemplo. */
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

  /** Datos de ejemplo. */
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

  /** Leyenda de la gráfica de dona (datos de ejemplo). */
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