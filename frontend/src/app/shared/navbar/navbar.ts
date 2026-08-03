import { Component } from '@angular/core';

@Component({
  selector: 'app-navbar',
  imports: [],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class Navbar {
  protected readonly navLinks = [
    { label: 'Inicio', href: '#inicio' },
    { label: 'Solución', href: '#solucion' },
    { label: 'Beneficios', href: '#beneficios' },
    { label: 'Analítica', href: '#analitica' },
    { label: 'Nosotros', href: '#nosotros' },
    { label: 'Contacto', href: '#contacto' }
  ];
}
