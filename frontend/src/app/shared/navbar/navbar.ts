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
    { label: 'Inicio', href: '#hero' },
    { label: 'El Reto', href: '#reto' },
    { label: 'Quiénes Somos', href: '#quienes-somos' },
    { label: 'Misión', href: '#mission' },
    { label: 'Módulos', href: '#modules' },
    { label: 'Contacto', href: '#contacto' }
  ];
}