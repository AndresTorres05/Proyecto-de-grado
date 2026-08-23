import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  imports: [],
  templateUrl: './footer.html',
  styleUrl: './footer.scss'
})
export class Footer {
  protected readonly anioActual = 2026;

  protected readonly redesSociales = [
    { texto: 'GH', etiqueta: 'GitHub Repository', url: 'https://github.com/JuanDGarridoR/Proyecto-de-grado.git' },
    { texto: 'in', etiqueta: 'LinkedIn', url: '#' },
    { texto: 'f', etiqueta: 'Facebook', url: '#' },
    { texto: '▶', etiqueta: 'YouTube', url: '#' }
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