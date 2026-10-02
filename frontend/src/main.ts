import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';

// Punto de entrada del frontend: arranca la aplicación con la configuración
// de app.config.ts.
bootstrapApplication(App, appConfig)
  .catch((err) => console.error(err));
