import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/landing/landing').then((m) => m.Landing)
  },
  {
    path: 'panel/organizacion',
    loadComponent: () =>
      import('./pages/dashboard/organizacion/organizacion').then((m) => m.OrganizacionDashboard)
  },
  {
    path: 'panel/voluntario',
    loadComponent: () =>
      import('./pages/dashboard/voluntario/voluntario').then((m) => m.VoluntarioDashboard)
  },
  {
    path: 'panel/acompanante',
    loadComponent: () =>
      import('./pages/dashboard/acompanante/acompanante').then((m) => m.AcompananteDashboard)
  },
  {
    path: 'panel/persona-mayor',
    loadComponent: () =>
      import('./pages/dashboard/persona-mayor/persona-mayor').then((m) => m.PersonaMayorDashboard)
  }
];
