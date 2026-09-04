import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
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
    private personaMayorService: PersonaMayorService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarInformacion();
  }

  cargarInformacion(): void {
    this.informacion = null;
    this.cargando = true;

    this.personaMayorService.obtenerInformacion().subscribe({
      next: (data) => {
        this.informacion = data;
        this.cargando = false;
        this.cdr.detectChanges();   // <-- fuerza el repintado
      },
      error: (error) => {
        console.error('Error al cargar la información:', error);
        this.cargando = false;
        this.cdr.detectChanges();   // <-- también aquí
      }
    });
  }

  guardarCambios(): void {
    if (!this.informacion) return;

    this.personaMayorService.actualizarInformacion(this.informacion).subscribe({
      next: (data) => {
        this.informacion = data;
        this.editando = false;
        this.cdr.detectChanges();
      },
      error: (error) => console.error('Error al actualizar la información:', error)
    });
  }
}