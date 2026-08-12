import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink],
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