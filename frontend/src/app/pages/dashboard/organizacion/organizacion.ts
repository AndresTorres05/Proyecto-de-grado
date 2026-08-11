import { Component } from '@angular/core';
import { DashboardShell, ShellNavItem } from '../../../shared/dashboard-shell/dashboard-shell';

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

interface Actividad {
  nombre: string;
  fecha: string;
  lugar: string;
  asistentes: number;
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
  imports: [DashboardShell],
  templateUrl: './organizacion.html',
  styleUrl: './organizacion.css'
})
export class OrganizacionDashboard {
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

  protected readonly actividades: Actividad[] = [
    { nombre: 'Taller de memoria y cognición', fecha: 'Hoy · 10:00 a.m.', lugar: 'Salón comunal Entrenubes', asistentes: 18 },
    { nombre: 'Fisioterapia grupal', fecha: 'Hoy · 3:00 p.m.', lugar: 'Centro de salud San Cristóbal', asistentes: 12 },
    { nombre: 'Jornada de vacunación', fecha: 'Mañana · 8:00 a.m.', lugar: 'UPL Entrenubes', asistentes: 40 },
    { nombre: 'Encuentro intergeneracional', fecha: 'Vie 14 ago · 2:00 p.m.', lugar: 'Parque Entrenubes', asistentes: 25 }
  ];

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
