import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = (route) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.estaAutenticado()) {
    return router.parseUrl('/login');
  }

  const rolRequerido = route.data['rol'] as string | undefined;
  const rolUsuario = authService.getRol();

  if (rolRequerido && rolUsuario !== rolRequerido) {
    // Está logueado, pero con otro rol: lo mandamos a SU panel correcto, no al que pidió
    authService.redirigirSegunRol(rolUsuario ?? '');
    return false;
  }

  return true;
};