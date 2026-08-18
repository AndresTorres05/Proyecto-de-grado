import { Component, OnInit, signal } from '@angular/core';
import { DashboardShell, ShellNavItem } from '../../../shared/dashboard-shell/dashboard-shell';
import { ActividadService, Actividad } from '../../../core/actividades/actividad.service';

interface PersonaMayor {
  nombre: string;
  edad: number;
  estado: 'Al día' | 'Alerta activa';
  proximaVisita: string;
}

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
  imports: [DashboardShell],
  templateUrl: './acompanante.html',
  styleUrl: './acompanante.css'
})
export class AcompananteDashboard implements OnInit {
  constructor(private actividadService: ActividadService) {}

  ngOnInit(): void {
    this.actividadService.listar().subscribe((actividades) => this.actividades.set(actividades));
  }

  protected readonly navItems: ShellNavItem[] = [
    { icon: '🏠', label: 'Inicio', active: true },
    { icon: '🧓', label: 'Mis personas mayores' },
    { icon: '📋', label: 'Seguimiento' },
    { icon: '☎️', label: 'Contactos de emergencia' },
    { icon: '🏃', label: 'Actividades' },
    { icon: '👤', label: 'Mi perfil' }
  ];

  protected readonly personasMayores: PersonaMayor[] = [
    { nombre: 'Rosa Elvira Gómez', edad: 78, estado: 'Alerta activa', proximaVisita: 'Hoy · 4:00 p.m.' },
    { nombre: 'Carlos Julio Méndez', edad: 82, estado: 'Al día', proximaVisita: 'Jue 13 ago · 10:00 a.m.' }
  ];

  protected readonly recordatorios: Recordatorio[] = [
    { hora: '10:00 a.m.', detalle: 'Losartán 50mg', persona: 'Carlos Julio Méndez' },
    { hora: '2:00 p.m.', detalle: 'Control de presión arterial', persona: 'Rosa Elvira Gómez' },
    { hora: '6:00 p.m.', detalle: 'Metformina 850mg', persona: 'Carlos Julio Méndez' }
  ];

  protected readonly alertas: AlertaConsulta[] = [
    {
      nombre: 'Rosa Elvira Gómez',
      descripcion: 'Sin registro de visita hace 15 días. Requiere seguimiento prioritario.',
      prioridad: 'Alta'
    }
  ];

  protected readonly contactos: ContactoEmergencia[] = [
    { nombre: 'Marta Gómez', parentesco: 'Hija', telefono: '300 456 7890', persona: 'Rosa Elvira Gómez' },
    { nombre: 'Pedro Méndez', parentesco: 'Hijo', telefono: '311 222 3344', persona: 'Carlos Julio Méndez' }
  ];

  protected readonly actividades = signal<Actividad[]>([]);
}
