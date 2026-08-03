import { Component } from '@angular/core';

@Component({
  selector: 'app-benefits',
  imports: [],
  templateUrl: './benefits.html',
  styleUrl: './benefits.css'
})
export class Benefits {
  protected readonly stats = [
    { value: '2,210', label: 'Mayores', color: 'var(--gema-navy)' },
    { value: '740', label: 'Acompañantes', color: 'var(--gema-orange)' },
    { value: '460', label: 'Voluntarios', color: 'color-mix(in srgb, var(--gema-gold) 70%, var(--gema-navy) 30%)' }
  ];

  protected readonly bars = [
    { height: 40, color: 'var(--gema-navy)' },
    { height: 65, color: 'var(--gema-orange)' },
    { height: 50, color: 'var(--gema-gold)' },
    { height: 80, color: 'var(--gema-navy)' },
    { height: 60, color: 'var(--gema-orange)' },
    { height: 90, color: 'var(--gema-gold)' }
  ];

  protected readonly legend = [
    { color: 'var(--gema-navy)', label: 'Movilidad' },
    { color: 'var(--gema-orange)', label: 'Salud mental' },
    { color: 'var(--gema-gold)', label: 'Salud física' },
    { color: 'var(--gema-navy-light)', label: 'Otros' }
  ];
}
