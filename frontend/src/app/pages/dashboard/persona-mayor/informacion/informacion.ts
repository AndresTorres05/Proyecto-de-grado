import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  PersonaMayorService,
  PersonaMayorResponse
} from '../../../../core/persona-mayor/persona-mayor.service';

@Component({
  selector: 'app-informacion',
  imports: [FormsModule],
  templateUrl: './informacion.html',
  styleUrl: './informacion.css'
})
export class Informacion implements OnInit {

  informacion: PersonaMayorResponse | null = null;

  cargando = true;

  editando = false;

  constructor(
    private personaMayorService: PersonaMayorService
  ) {}

  ngOnInit(): void {
    this.cargarInformacion();
  }

  cargarInformacion(): void {
    this.personaMayorService.obtenerInformacion().subscribe({
      next: (data) => {
        console.log('DATA COMPLETA:', data);
        console.log('NOMBRE:', data.nombre);

        this.informacion = data;
        this.cargando = false;

        console.log('INFORMACION EN COMPONENTE:', this.informacion);
        console.log('CARGANDO EN COMPONENTE:', this.cargando);
      },
      error: (error) => {
        console.error('Error al cargar la información:', error);
        this.cargando = false;
      }
    });
  }
}