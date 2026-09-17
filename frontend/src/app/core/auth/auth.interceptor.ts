import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const rutasPublicas = [
    '/api/auth/login',
    '/api/auth/login-otp',
    '/api/auth/registro',
    '/api/otp/send',
    '/api/otp/verify'
  ];

  const esRutaPublica = rutasPublicas.some(ruta =>
    req.url.includes(ruta)
  );

  // Estas rutas no necesitan token
  if (esRutaPublica) {
    return next(req);
  }

  // Para las demás peticiones sí enviamos el token
  const token = sessionStorage.getItem('token');

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