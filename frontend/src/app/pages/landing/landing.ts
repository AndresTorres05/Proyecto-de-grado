import { Component } from '@angular/core';

import { Navbar } from '../../shared/navbar/navbar';
import { Footer } from '../../shared/footer/footer';
import { Hero } from './sections/hero/hero';
import { Modules } from './sections/modules/modules';
import { Mission } from './sections/mission/mission';
import { QuienesSomosComponent } from "./sections/quienes-somos/quienes-somos";
import { Reto } from "./sections/reto/reto";

/** Página de inicio pública: reúne las secciones de la landing en orden. */
@Component({
  selector: 'app-landing',
  imports: [Navbar, Hero, Modules, Mission, Footer, QuienesSomosComponent, Reto],
  templateUrl: './landing.html',
  styleUrl: './landing.css'
})
export class Landing {}
