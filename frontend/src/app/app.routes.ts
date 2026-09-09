import { Routes } from '@angular/router';

import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [

  // =========================
  // PÁGINAS PÚBLICAS
  // =========================

  {
    path: '',
    loadComponent: () =>
      import('./pages/landing/landing').then((m) => m.Landing)
  },

  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login').then((m) => m.Login)
  },

  {
    path: 'registro',
    loadComponent: () =>
      import('./pages/registro/registro').then((m) => m.Registro)
  },


  // =========================
  // PANEL ORGANIZACIÓN
  // =========================

  {
    path: 'panel/organizacion',
    loadComponent: () =>
      import('./shared/panel-shell-layout/panel-shell-layout')
        .then((m) => m.PanelShellLayout),

    canActivate: [authGuard],
    data: { rol: 'ORGANIZACION' },

    children: [

      {
        path: '',
        loadComponent: () =>
          import('./pages/dashboard/organizacion/organizacion')
            .then((m) => m.OrganizacionDashboard)
      },

      {
        path: 'personas-mayores',
        loadComponent: () =>
          import('./pages/dashboard/organizacion/personas-mayores/personas-mayores')
            .then((m) => m.PersonasMayores),
        data: { titulo: 'Personas mayores' }
      },

      {
        path: 'acompanantes',
        loadComponent: () =>
          import('./shared/en-construccion/en-construccion')
            .then((m) => m.EnConstruccion),
        data: { titulo: 'Acompañantes' }
      },

      {
        path: 'voluntarios',
        loadComponent: () =>
          import('./shared/en-construccion/en-construccion')
            .then((m) => m.EnConstruccion),
        data: { titulo: 'Voluntarios' }
      },

      {
        path: 'actividades',
        loadComponent: () =>
          import('./shared/en-construccion/en-construccion')
            .then((m) => m.EnConstruccion),
        data: { titulo: 'Actividades' }
      },

      {
        path: 'medicamentos',
        loadComponent: () =>
          import('./shared/en-construccion/en-construccion')
            .then((m) => m.EnConstruccion),
        data: { titulo: 'Medicamentos' }
      },

      {
        path: 'donaciones',
        loadComponent: () =>
          import('./shared/en-construccion/en-construccion')
            .then((m) => m.EnConstruccion),
        data: { titulo: 'Donaciones' }
      },

      {
        path: 'alertas',
        loadComponent: () =>
          import('./shared/en-construccion/en-construccion')
            .then((m) => m.EnConstruccion),
        data: { titulo: 'Alertas' }
      },

      {
        path: 'analitica',
        loadComponent: () =>
          import('./shared/en-construccion/en-construccion')
            .then((m) => m.EnConstruccion),
        data: { titulo: 'Analítica' }
      },

      {
        path: 'mapa',
        loadComponent: () =>
          import('./shared/en-construccion/en-construccion')
            .then((m) => m.EnConstruccion),
        data: { titulo: 'Mapa' }
      },

      {
        path: 'administracion',
        loadComponent: () =>
          import('./pages/dashboard/organizacion/administracion/administracion')
            .then((m) => m.Administracion),
        data: { titulo: 'Administración' }
      }

    ]
  },


  // =========================
  // PANEL VOLUNTARIO
  // =========================

  {
    path: 'panel/voluntario',
    loadComponent: () =>
      import('./shared/panel-shell-layout/panel-shell-layout')
        .then((m) => m.PanelShellLayout),

    canActivate: [authGuard],
    data: { rol: 'VOLUNTARIO' },

    children: [

      {
        path: '',
        loadComponent: () =>
          import('./pages/dashboard/voluntario/voluntario')
            .then((m) => m.VoluntarioDashboard)
      },

      {
        path: 'actividades',
        loadComponent: () =>
          import('./shared/en-construccion/en-construccion')
            .then((m) => m.EnConstruccion),
        data: { titulo: 'Mis actividades' }
      },

      {
        path: 'disponibilidad',
        loadComponent: () =>
          import('./shared/en-construccion/en-construccion')
            .then((m) => m.EnConstruccion),
        data: { titulo: 'Disponibilidad' }
      },

      {
        path: 'alertas',
        loadComponent: () =>
          import('./shared/en-construccion/en-construccion')
            .then((m) => m.EnConstruccion),
        data: { titulo: 'Alertas' }
      },

      {
        path: 'personas',
        loadComponent: () =>
          import('./shared/en-construccion/en-construccion')
            .then((m) => m.EnConstruccion),
        data: { titulo: 'Personas que acompaño' }
      },

      {
        path: 'perfil',
        loadComponent: () =>
          import('./shared/en-construccion/en-construccion')
            .then((m) => m.EnConstruccion),
        data: { titulo: 'Mi perfil' }
      }

    ]
  },


  // =========================
  // PANEL ACOMPAÑANTE
  // =========================

  {
    path: 'panel/acompanante',
    loadComponent: () =>
      import('./shared/panel-shell-layout/panel-shell-layout')
        .then((m) => m.PanelShellLayout),

    canActivate: [authGuard],
    data: { rol: 'ACOMPANANTE' },

    children: [

      {
        path: '',
        loadComponent: () =>
          import('./pages/dashboard/acompanante/acompanante')
            .then((m) => m.AcompananteDashboard)
      },

      {
        path: 'personas-mayores',
        loadComponent: () =>
          import('./pages/dashboard/acompanante/mis-personas-mayores/mis-personas-mayores')
            .then((m) => m.MisPersonasMayores)
      },

      {
        path: 'seguimiento',
        loadComponent: () =>
          import('./shared/en-construccion/en-construccion')
            .then((m) => m.EnConstruccion),
        data: { titulo: 'Seguimiento' }
      },

      {
        path: 'contactos-emergencia',
        loadComponent: () =>
          import('./shared/en-construccion/en-construccion')
            .then((m) => m.EnConstruccion),
        data: { titulo: 'Contactos de emergencia' }
      },

      {
        path: 'actividades',
        loadComponent: () =>
          import('./shared/en-construccion/en-construccion')
            .then((m) => m.EnConstruccion),
        data: { titulo: 'Actividades' }
      },

      {
        path: 'perfil',
        loadComponent: () =>
          import('./shared/en-construccion/en-construccion')
            .then((m) => m.EnConstruccion),
        data: { titulo: 'Mi perfil' }
      }

    ]
  },


  // =========================
  // PANEL PERSONA MAYOR
  // =========================

  {
    path: 'panel/persona-mayor',

    loadComponent: () =>
      import('./shared/panel-shell-layout/panel-shell-layout')
        .then((m) => m.PanelShellLayout),

    canActivate: [authGuard],
    data: { rol: 'PERSONA_MAYOR' },

    children: [

      {
        path: '',
        loadComponent: () =>
          import('./pages/dashboard/persona-mayor/persona-mayor')
            .then((m) => m.PersonaMayorDashboard)
      },

      {
  path: 'actividades',
  loadComponent: () =>
    import('./pages/dashboard/persona-mayor/actividades/actividades').then((m) => m.Actividades)
},

      {
        path: 'intereses',
        loadComponent: () =>
          import('./pages/dashboard/persona-mayor/intereses/intereses')
            .then((m) => m.Intereses)
      },

      {
  path: 'recordatorios',
  loadComponent: () =>
    import('./pages/dashboard/persona-mayor/recordatorios/recordatorios').then((m) => m.Recordatorios)
},

      {
        path: 'informacion',
        loadComponent: () =>
          import('./pages/dashboard/persona-mayor/informacion/informacion')
            .then((m) => m.Informacion)
      },
      {
        path: 'contactos',
        loadComponent: () =>
          import('./pages/dashboard/persona-mayor/contactos/contactos')
            .then((m) => m.Contactos)
      },
      {
  path: 'organizaciones',
  loadComponent: () =>
    import('./pages/dashboard/persona-mayor/organizaciones/organizaciones')
      .then((m) => m.Organizaciones)
}
    ]
  }

];