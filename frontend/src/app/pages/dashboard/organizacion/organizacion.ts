import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DashboardShell, ShellNavItem } from '../../../shared/dashboard-shell/dashboard-shell';
import { ActividadService, Actividad, ActividadRequest } from '../../../core/actividades/actividad.service';

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
  imports: [DashboardShell, FormsModule],
  templateUrl: './organizacion.html',
  styleUrl: './organizacion.css'
})
export class OrganizacionDashboard implements OnInit {
  protected readonly navItems: ShellNavItem[] = [
    { icon: '🏠', label: 'Inicio', active: true },
    { icon: '🧓', label: 'Personas mayores' },
    { icon: '🤝', label: 'Acompañantes' },
    { icon: '⭐', label: 'Voluntarios' },
    { icon: '🏃', label: 'Actividades' },
    { icon: '💊', label: 'Medicamentos' },
    { icon: '💝', label: 'Donaciones' },
    { icon: '🔔', label: 'Alertas' },
    { icon: '📊', label: 'Analítica' },
    { icon: '🗺️', label: 'Mapa' },
    { icon: '⚙️', label: 'Administración' }
  ];

  protected readonly stats: StatCard[] = [
    { icon: '🧓', value: '2,210', delta: '+12%', label: 'Personas mayores registradas' },
    { icon: '🤝', value: '740', delta: '+8%', label: 'Acompañantes activos' },
    { icon: '⭐', value: '460', delta: '+15%', label: 'Voluntarios en programa' },
    { icon: '🔔', value: '12', delta: '3 urgentes', label: 'Alertas activas' }
  ];

  protected readonly alertas: Alerta[] = [
    {
      prioridad: 'Alta',
      nombre: 'Rosa Elvira Gómez',
      descripcion: 'Sin registro de visita hace 15 días. Requiere seguimiento prioritario.',
      tiempo: 'Hace 2 horas'
    },
    {
      prioridad: 'Alta',
      nombre: 'Inventario · Losartán 50mg',
      descripcion: 'Stock por debajo del mínimo establecido (4 unidades restantes).',
      tiempo: 'Hace 5 horas'
    },
    {
      prioridad: 'Media',
      nombre: 'José Antonio Ruiz',
      descripcion: 'Condición de salud reportada como prioritaria en la última caracterización.',
      tiempo: 'Ayer'
    },
    {
      prioridad: 'Baja',
      nombre: 'Kit de vendajes',
      descripcion: 'Próximo a fecha de vencimiento (12 días).',
      tiempo: 'Ayer'
    }
  ];

  protected readonly accionesRapidas: AccionRapida[] = [
    { icon: '🧓', label: 'Registrar persona mayor' },
    { icon: '🏃', label: 'Registrar actividad' },
    { icon: '💝', label: 'Registrar donación' },
    { icon: '📋', label: 'Generar reporte' }
  ];

  protected readonly actividades = signal<Actividad[]>([]);
  protected readonly errorActividades = signal<string | null>(null);

  protected nuevaActividad: ActividadRequest = { nombre: '', fecha: null, lugar: null, tipo: null };

  protected actividadEditandoId: number | null = null;
  protected actividadEditando: ActividadRequest = { nombre: '', fecha: null, lugar: null, tipo: null };

  constructor(private actividadService: ActividadService) {}

  ngOnInit(): void {
    this.cargarActividades();
  }

  private cargarActividades(): void {
    this.actividadService.listarMias().subscribe({
      next: (actividades) => this.actividades.set(actividades),
      error: () => this.errorActividades.set('No se pudieron cargar las actividades')
    });
  }

  crearActividad(): void {
    if (!this.nuevaActividad.nombre.trim()) {
      return;
    }
    this.errorActividades.set(null);
    this.actividadService.crear(this.nuevaActividad).subscribe({
      next: () => {
        this.nuevaActividad = { nombre: '', fecha: null, lugar: null, tipo: null };
        this.cargarActividades();
      },
      error: () => this.errorActividades.set('No se pudo crear la actividad')
    });
  }

  editarActividad(actividad: Actividad): void {
    this.actividadEditandoId = actividad.idActividad;
    this.actividadEditando = {
      nombre: actividad.nombre,
      fecha: actividad.fecha,
      lugar: actividad.lugar,
      tipo: actividad.tipo
    };
  }

  cancelarEdicionActividad(): void {
    this.actividadEditandoId = null;
  }

  guardarActividad(): void {
    if (this.actividadEditandoId === null || !this.actividadEditando.nombre.trim()) {
      return;
    }
    this.errorActividades.set(null);
    this.actividadService.actualizar(this.actividadEditandoId, this.actividadEditando).subscribe({
      next: () => {
        this.cancelarEdicionActividad();
        this.cargarActividades();
      },
      error: () => this.errorActividades.set('No se pudo actualizar la actividad')
    });
  }

  eliminarActividad(actividad: Actividad): void {
    this.errorActividades.set(null);
    this.actividadService.eliminar(actividad.idActividad).subscribe({
      next: () => this.cargarActividades(),
      error: () => this.errorActividades.set('No se pudo eliminar la actividad')
    });
  }

  protected readonly inventario: Inventario[] = [
    { nombre: 'Losartán 50mg', estado: 'Stock bajo', detalle: '4 unidades disponibles' },
    { nombre: 'Kit de vendajes', estado: 'Por vencer', detalle: 'Vence en 12 días' },
    { nombre: 'Metformina 850mg', estado: 'Stock bajo', detalle: '9 unidades disponibles' }
  ];

  protected readonly donaciones: Donacion[] = [
    { donante: 'Fundación Manos Amigas', tipo: 'Monetaria', valor: '$1.200.000', fecha: '08 ago 2026' },
    { donante: 'Supermercado La Colina', tipo: 'Alimentos', valor: '35 kits', fecha: '06 ago 2026' },
    { donante: 'Anónimo', tipo: 'Monetaria', valor: '$300.000', fecha: '04 ago 2026' }
  ];

  protected readonly bitacora: Bitacora[] = [
    { usuario: 'Voluntario · Camilo Rey', accion: 'registró asistencia en Fisioterapia grupal', tiempo: 'Hace 34 min' },
    { usuario: 'Acompañante · Laura Peña', accion: 'registró una visita de seguimiento a Rosa Elvira Gómez', tiempo: 'Hace 1 hora' },
    { usuario: 'Sistema', accion: 'generó una alerta por inventario bajo de Losartán 50mg', tiempo: 'Hace 5 horas' },
    { usuario: 'Organización · Ana Torres', accion: 'actualizó la caracterización de José Antonio Ruiz', tiempo: 'Ayer' }
  ];

  protected readonly donutLegend = [
    { color: 'var(--gema-navy)', label: 'Movilidad 40%' },
    { color: 'var(--gema-navy-light)', label: 'Salud mental 25%' },
    { color: 'var(--gema-orange)', label: 'Salud física 20%' },
    { color: 'var(--gema-gold)', label: 'Otros 15%' }
  ];
}
