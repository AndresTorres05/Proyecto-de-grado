import { ShellNavItem } from '../dashboard-shell/dashboard-shell';

export interface PanelConfig {
  roleLabel: string;
  roleAccent: string;
  navItems: ShellNavItem[];
}

export const PANEL_CONFIG: Record<string, PanelConfig> = {
  PERSONA_MAYOR: {
    roleLabel: 'Persona mayor',
    roleAccent: 'var(--vita-green)',
    navItems: [
      { icon: 'home', label: 'Inicio', path: '/panel/persona-mayor' },
      { icon: 'activity', label: 'Mis actividades', path: '/panel/persona-mayor/actividades' },
      { icon: 'heart', label: 'Mis intereses', path: '/panel/persona-mayor/intereses' },
      { icon: 'clock', label: 'Mis recordatorios', path: '/panel/persona-mayor/recordatorios' },
      { icon: 'user', label: 'Mi información', path: '/panel/persona-mayor/informacion' },
      { icon: 'phone', label: 'Mis contactos', path: '/panel/persona-mayor/contactos' }
    ]
  },

  ORGANIZACION: {
    roleLabel: 'Organización',
    roleAccent: 'var(--vita-navy)',
    navItems: [
      { icon: 'home', label: 'Inicio', path: '/panel/organizacion' },
      { icon: 'user', label: 'Personas mayores', path: '/panel/organizacion/personas-mayores' },
      { icon: 'users', label: 'Acompañantes', path: '/panel/organizacion/acompanantes' },
      { icon: 'star', label: 'Voluntarios', path: '/panel/organizacion/voluntarios' },
      { icon: 'activity', label: 'Actividades', path: '/panel/organizacion/actividades' },
      { icon: 'pill', label: 'Medicamentos', path: '/panel/organizacion/medicamentos' },
      { icon: 'gift', label: 'Donaciones', path: '/panel/organizacion/donaciones' },
      { icon: 'bell', label: 'Alertas', path: '/panel/organizacion/alertas' },
      { icon: 'bar-chart', label: 'Analítica', path: '/panel/organizacion/analitica' },
      { icon: 'map', label: 'Mapa', path: '/panel/organizacion/mapa' },
      { icon: 'settings', label: 'Administración', path: '/panel/organizacion/administracion' }
    ]
  },

  ACOMPANANTE: {
    roleLabel: 'Acompañante',
    roleAccent: 'var(--vita-complemento)',
    navItems: [
      { icon: 'home', label: 'Inicio', path: '/panel/acompanante' },
      { icon: 'users', label: 'Mis personas mayores', path: '/panel/acompanante/personas-mayores' },
      { icon: 'clipboard', label: 'Seguimiento', path: '/panel/acompanante/seguimiento' },
      { icon: 'phone', label: 'Contactos de emergencia', path: '/panel/acompanante/contactos-emergencia' },
      { icon: 'activity', label: 'Actividades', path: '/panel/acompanante/actividades' },
      { icon: 'user', label: 'Mi perfil', path: '/panel/acompanante/perfil' }
    ]
  },

  VOLUNTARIO: {
    roleLabel: 'Voluntario',
    roleAccent: 'var(--vita-gold)',
    navItems: [
      { icon: 'home', label: 'Inicio', path: '/panel/voluntario' },
      { icon: 'activity', label: 'Mis actividades', path: '/panel/voluntario/actividades' },
      { icon: 'calendar', label: 'Disponibilidad', path: '/panel/voluntario/disponibilidad' },
      { icon: 'bell', label: 'Alertas', path: '/panel/voluntario/alertas' },
      { icon: 'users', label: 'Personas que acompaño', path: '/panel/voluntario/personas' },
      { icon: 'user', label: 'Mi perfil', path: '/panel/voluntario/perfil' }
    ]
  }
};
