import { Component } from '@angular/core';

import { Navbar } from '../../shared/navbar/navbar';
import { Footer } from '../../shared/footer/footer';
import { Hero } from './sections/hero/hero';
import { Modules } from './sections/modules/modules';
import { Benefits } from './sections/benefits/benefits';
import { DashboardPreview } from './sections/dashboard-preview/dashboard-preview';
import { Mission } from './sections/mission/mission';
import { Cta } from './sections/cta/cta';
import { QuienesSomosComponent } from "./sections/quienes-somos/quienes-somos";
import { Reto } from "./sections/reto/reto";


@Component({
  selector: 'app-landing',
  imports: [Navbar, Hero, Modules, Benefits, DashboardPreview, Mission, Cta, Footer, QuienesSomosComponent, Reto],
  templateUrl: './landing.html',
  styleUrl: './landing.css'
})
export class Landing {}
