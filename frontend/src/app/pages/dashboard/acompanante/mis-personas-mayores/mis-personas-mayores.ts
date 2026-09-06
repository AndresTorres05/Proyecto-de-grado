import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import {
  AcompananteService,
  PersonaMayorAcompanada,
  SolicitudAcompanamiento
} from '../../../../core/acompanantes/acompanante.service';

@Component({
  selector: 'app-mis-personas-mayores',
  templateUrl: './mis-personas-mayores.html',
  styleUrl: './mis-personas-mayores.css'
})
export class MisPersonasMayores implements OnInit {

  personasMayores: PersonaMayorAcompanada[] = [];
  solicitudes: SolicitudAcompanamiento[] = [];

  cargando = true;
  mensaje = '';
  error = '';

  constructor(
    private acompananteService: AcompananteService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarPersonasMayores();
    this.cargarSolicitudes();
  }

  cargarPersonasMayores(): void {
    this.acompananteService.obtenerPersonasMayores().subscribe({
      next: (data) => {
        this.personasMayores = data;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar personas mayores:', error);
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  cargarSolicitudes(): void {
    this.acompananteService.obtenerSolicitudes().subscribe({
      next: (data) => {
        this.solicitudes = data;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar solicitudes:', error);
      }
    });
  }

  aceptarSolicitud(idPersonaMayor: number): void {
    this.acompananteService.aceptarSolicitud(idPersonaMayor).subscribe({
      next: (respuesta) => {
        this.mensaje = respuesta;
        this.error = '';

        this.cargarSolicitudes();
        this.cargarPersonasMayores();
      },
      error: (error) => {
        console.error('Error al aceptar solicitud:', error);
        this.error = 'No fue posible aceptar la solicitud.';
        this.mensaje = '';
      }
    });
  }

  rechazarSolicitud(idPersonaMayor: number): void {
    this.acompananteService.rechazarSolicitud(idPersonaMayor).subscribe({
      next: (respuesta) => {
        this.mensaje = respuesta;
        this.error = '';

        this.cargarSolicitudes();
      },
      error: (error) => {
        console.error('Error al rechazar solicitud:', error);
        this.error = 'No fue posible rechazar la solicitud.';
        this.mensaje = '';
      }
    });
  }
}