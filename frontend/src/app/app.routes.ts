import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./pages/landing/landing').then((m) => m.Landing)
  },
  {
    path: 'login',
    loadComponent: () => import('./pages/login/login').then((m) => m.Login)
  },
  {
    path: 'registro',
    loadComponent: () => import('./pages/registro/registro').then((m) => m.Registro)
  },
  {
    path: 'panel/organizacion',
    loadComponent: () =>
      import('./pages/dashboard/organizacion/organizacion').then((m) => m.OrganizacionDashboard),
    canActivate: [authGuard],
    data: { rol: 'ORGANIZACION' }
  },
  {
    path: 'panel/voluntario',
    loadComponent: () =>
      import('./pages/dashboard/voluntario/voluntario').then((m) => m.VoluntarioDashboard),
    canActivate: [authGuard],
    data: { rol: 'VOLUNTARIO' }
  },
  {
    path: 'panel/acompanante',
    loadComponent: () =>
      import('./pages/dashboard/acompanante/acompanante').then((m) => m.AcompananteDashboard),
    canActivate: [authGuard],
    data: { rol: 'ACOMPANANTE' }
  },
  {
    path: 'panel/persona-mayor',
    loadComponent: () =>
      import('./pages/dashboard/persona-mayor/persona-mayor').then((m) => m.PersonaMayorDashboard),
    canActivate: [authGuard],
    data: { rol: 'PERSONA_MAYOR' }
  },
  {
    path: 'panel/admin',
    loadComponent: () => import('./pages/dashboard/admin/admin').then((m) => m.AdminDashboard),
    canActivate: [authGuard]
  }
];