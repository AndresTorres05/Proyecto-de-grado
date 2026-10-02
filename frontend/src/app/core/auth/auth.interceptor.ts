import { inject } from '@angular/core';
import { HttpInterceptorFn } from '@angular/common/http';
import { AuthService } from './auth.service';

/**
 * Agrega el token (Authorization: Bearer ...) a cada petición al backend,
 * menos a las de login, registro y envío del código OTP.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const rutasPublicas = [
    '/api/auth/login',
    '/api/auth/login-otp',
    '/api/auth/registro',
    '/api/otp/send'
  ];

  const esRutaPublica = rutasPublicas.some(ruta =>
    req.url.includes(ruta)
  );

  // Estas rutas no necesitan token.
  if (esRutaPublica) {
    return next(req);
  }

  // Se lee a través de AuthService para que, si la pestaña se acaba de
  // abrir, primero se restaure la sesión recordada.
  const token = inject(AuthService).getToken();

  if (!token) {
    return next(req);
  }

  return next(
    req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    })
  );
};