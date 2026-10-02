import { ShellNavItem } from '../dashboard-shell/dashboard-shell';

export interface PanelConfig {
  roleLabel: string;
  roleAccent: string;
  navItems: ShellNavItem[];
}

// Convención de la barra lateral: "Inicio" siempre va primero y "Perfil" siempre
// al final; en medio, las secciones de más a menos importantes. Los nombres son
// cortos y sin posesivos ("Recordatorios", no "Mis recordatorios"), y una misma
// sección se llama igual y usa el mismo ícono en todos los roles.
export const PANEL_CONFIG: Record<string, PanelConfig> = {
  PERSONA_MAYOR: {
    roleLabel: 'Persona mayor',
    roleAccent: 'var(--vita-green)',
    navItems: [
      { icon: 'home', label: 'Inicio', path: '/panel/persona-mayor' },
      { icon: 'clock', label: 'Recordatorios', path: '/panel/persona-mayor/recordatorios' },
      { icon: 'phone', label: 'Contactos', path: '/panel/persona-mayor/contactos' },
      { icon: 'activity', label: 'Signos vitales', path: '/panel/persona-mayor/signos-vitales' },
      { icon: 'calendar', label: 'Actividades', path: '/panel/persona-mayor/actividades' },
      { icon: 'building', label: 'Organizaciones', path: '/panel/persona-mayor/organizaciones' },
      { icon: 'heart', label: 'Intereses', path: '/panel/persona-mayor/intereses' },
      { icon: 'user', label: 'Perfil', path: '/panel/persona-mayor/perfil' }
    ]
  },

  ORGANIZACION: {
    roleLabel: 'Organización',
    roleAccent: 'var(--vita-navy)',
    navItems: [
      { icon: 'home', label: 'Inicio', path: '/panel/organizacion' },
      { icon: 'users', label: 'Personas mayores', path: '/panel/organizacion/personas-mayores' },
      { icon: 'activity', label: 'Signos vitales', path: '/panel/organizacion/signos-vitales' },
      { icon: 'calendar', label: 'Actividades', path: '/panel/organizacion/actividades' },
      { icon: 'bar-chart', label: 'Analítica', path: '/panel/organizacion/analitica' },
      { icon: 'star', label: 'Voluntarios', path: '/panel/organizacion/voluntarios' },
      //{ icon: 'users', label: 'Acompañantes', path: '/panel/organizacion/acompanantes' },
      //{ icon: 'bell', label: 'Alertas', path: '/panel/organizacion/alertas' },
      //{ icon: 'pill', label: 'Medicamentos', path: '/panel/organizacion/medicamentos' },
      //{ icon: 'gift', label: 'Donaciones', path: '/panel/organizacion/donaciones' },
      //{ icon: 'map', label: 'Mapa', path: '/panel/organizacion/mapa' },
      { icon: 'user', label: 'Perfil', path: '/panel/organizacion/perfil' }
    ]
  },

  ACOMPANANTE: {
    roleLabel: 'Acompañante',
    roleAccent: 'var(--vita-complemento)',
    navItems: [
      { icon: 'home', label: 'Inicio', path: '/panel/acompanante' },
      { icon: 'users', label: 'Personas mayores', path: '/panel/acompanante/personas-mayores' },
      { icon: 'clipboard', label: 'Seguimiento', path: '/panel/acompanante/seguimiento' },
      { icon: 'calendar', label: 'Actividades', path: '/panel/acompanante/actividades' },
      { icon: 'user', label: 'Perfil', path: '/panel/acompanante/perfil' }
    ]
  },

  VOLUNTARIO: {
    roleLabel: 'Voluntario',
    roleAccent: 'var(--vita-gold)',
    navItems: [
      { icon: 'home', label: 'Inicio', path: '/panel/voluntario' },
      { icon: 'users', label: 'Personas mayores', path: '/panel/voluntario/personas' },
      { icon: 'bell', label: 'Alertas', path: '/panel/voluntario/alertas' },
      { icon: 'calendar', label: 'Actividades', path: '/panel/voluntario/actividades' },
      { icon: 'clock', label: 'Disponibilidad', path: '/panel/voluntario/disponibilidad' },
      { icon: 'user', label: 'Perfil', path: '/panel/voluntario/perfil' }
    ]
  }
};
