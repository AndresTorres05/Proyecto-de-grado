import { Component } from '@angular/core';

@Component({
  selector: 'app-benefits',
  imports: [],
  templateUrl: './benefits.html',
  styleUrl: './benefits.css'
})
export class Benefits {
  protected readonly stats = [
    { value: '2,210', label: 'Mayores', color: 'var(--vita-navy)' },
    { value: '740', label: 'Acompañantes', color: 'var(--vita-orange)' },
    { value: '460', label: 'Voluntarios', color: 'color-mix(in srgb, var(--vita-gold) 70%, var(--vita-navy) 30%)' }
  ];

  protected readonly bars = [
    { height: 40, color: 'var(--vita-navy)' },
    { height: 65, color: 'var(--vita-orange)' },
    { height: 50, color: 'var(--vita-gold)' },
    { height: 80, color: 'var(--vita-navy)' },
    { height: 60, color: 'var(--vita-orange)' },
    { height: 90, color: 'var(--vita-gold)' }
  ];

  protected readonly legend = [
    { color: 'var(--vita-navy)', label: 'Movilidad' },
    { color: 'var(--vita-orange)', label: 'Salud mental' },
    { color: 'var(--vita-gold)', label: 'Salud física' },
    { color: 'var(--vita-navy-light)', label: 'Otros' }
  ];
}
