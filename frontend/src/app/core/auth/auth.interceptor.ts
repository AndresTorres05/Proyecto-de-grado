import { inject } from '@angular/core';
import { HttpInterceptorFn } from '@angular/common/http';
import { AuthService } from './auth.service';

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

  // Estas rutas no necesitan token
  if (esRutaPublica) {
    return next(req);
  }

  // Para las demás peticiones sí enviamos el token
  // Se lee a través de AuthService para que antes se haya restaurado
  // la sesión recordada (si esta pestaña se acaba de abrir).
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