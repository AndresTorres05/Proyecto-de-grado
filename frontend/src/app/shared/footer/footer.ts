import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  imports: [],
  templateUrl: './footer.html',
  styleUrl: './footer.css'
})
export class Footer {
  protected readonly anioActual = 2026;

  protected readonly redesSociales = [
    { texto: 'X', etiqueta: 'X / Twitter' },
    { texto: 'in', etiqueta: 'LinkedIn' },
    { texto: 'f', etiqueta: 'Facebook' },
    { texto: '▶', etiqueta: 'YouTube' }
  ];

  protected readonly enlacesPlataforma = [
    'Módulos',
    'Analítica',
    'Integraciones',
    'API pública',
    'Seguridad'
  ];

  protected readonly enlacesOrganizacion = [
    'Nuestra misión',
    'Equipo',
    'Alianzas',
    'Blog',
    'Prensa'
  ];
}
