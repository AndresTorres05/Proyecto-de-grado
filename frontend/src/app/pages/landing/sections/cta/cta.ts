import { Component } from '@angular/core';

@Component({
  selector: 'app-cta',
  imports: [],
  templateUrl: './cta.html',
  styleUrl: './cta.css'
})
export class Cta {
  protected readonly stats = [
    { number: '12', label: 'Módulos integrados' },
    { number: '38+', label: 'Organizaciones' },
    { number: '100%', label: 'Datos seguros' }
  ];
}
