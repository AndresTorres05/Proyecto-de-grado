import { ShellNavItem } from '../dashboard-shell/dashboard-shell';

export interface PanelConfig {
  roleLabel: string;
  roleAccent: string;
  navItems: ShellNavItem[];
}

export const PANEL_CONFIG: Record<string, PanelConfig> = {
  PERSONA_MAYOR: {
    roleLabel: 'Persona mayor',
    roleAccent: 'var(--gema-green)',
    navItems: [
      { icon: '🏠', label: 'Inicio', path: '/panel/persona-mayor' },
      { icon: '🏃', label: 'Mis actividades', path: '/panel/persona-mayor/actividades' },
      { icon: '❤️', label: 'Mis intereses', path: '/panel/persona-mayor/intereses' },
      { icon: '⏰', label: 'Mis recordatorios', path: '/panel/persona-mayor/recordatorios' },
      { icon: '👤', label: 'Mi información', path: '/panel/persona-mayor/informacion' },
      { icon: '☎️', label: 'Mis contactos', path: '/panel/persona-mayor/contactos' }
    ]
  },

  ORGANIZACION: {
    roleLabel: 'Organización',
    roleAccent: 'var(--gema-navy)',
    navItems: [
      { icon: '🏠', label: 'Inicio', path: '/panel/organizacion' },
      { icon: '🧓', label: 'Personas mayores', path: '/panel/organizacion/personas-mayores' },
      { icon: '🤝', label: 'Acompañantes', path: '/panel/organizacion/acompanantes' },
      { icon: '⭐', label: 'Voluntarios', path: '/panel/organizacion/voluntarios' },
      { icon: '🏃', label: 'Actividades', path: '/panel/organizacion/actividades' },
      { icon: '💊', label: 'Medicamentos', path: '/panel/organizacion/medicamentos' },
      { icon: '💝', label: 'Donaciones', path: '/panel/organizacion/donaciones' },
      { icon: '🔔', label: 'Alertas', path: '/panel/organizacion/alertas' },
      { icon: '📊', label: 'Analítica', path: '/panel/organizacion/analitica' },
      { icon: '🗺️', label: 'Mapa', path: '/panel/organizacion/mapa' },
      { icon: '⚙️', label: 'Administración', path: '/panel/organizacion/administracion' }
    ]
  },

  ACOMPANANTE: {
    roleLabel: 'Acompañante',
    roleAccent: 'var(--gema-orange)',
    navItems: [
      { icon: '🏠', label: 'Inicio', path: '/panel/acompanante' },
      { icon: '🧓', label: 'Mis personas mayores', path: '/panel/acompanante/personas-mayores' },
      { icon: '📋', label: 'Seguimiento', path: '/panel/acompanante/seguimiento' },
      { icon: '☎️', label: 'Contactos de emergencia', path: '/panel/acompanante/contactos-emergencia' },
      { icon: '🏃', label: 'Actividades', path: '/panel/acompanante/actividades' },
      { icon: '👤', label: 'Mi perfil', path: '/panel/acompanante/perfil' }
    ]
  },

  VOLUNTARIO: {
    roleLabel: 'Voluntario',
    roleAccent: 'var(--gema-gold)',
    navItems: [
      { icon: '🏠', label: 'Inicio', path: '/panel/voluntario' },
      { icon: '🏃', label: 'Mis actividades', path: '/panel/voluntario/actividades' },
      { icon: '🗓️', label: 'Disponibilidad', path: '/panel/voluntario/disponibilidad' },
      { icon: '🔔', label: 'Alertas', path: '/panel/voluntario/alertas' },
      { icon: '🧓', label: 'Personas que acompaño', path: '/panel/voluntario/personas' },
      { icon: '👤', label: 'Mi perfil', path: '/panel/voluntario/perfil' }
    ]
  }
};