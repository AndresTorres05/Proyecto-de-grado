import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

import {
  OrganizacionService,
  OrganizacionSolicitud
} from '../../../../core/organizacion/organizacion.service';

import { Icon } from '../../../../shared/icon/icon';

@Component({
  selector: 'app-organizaciones',
  standalone: true,
  imports: [Icon, CommonModule],
  templateUrl: './organizaciones.html',
  styleUrl: './organizaciones.css'
})
export class Organizaciones implements OnInit {

  protected readonly organizaciones =
    signal<OrganizacionSolicitud[]>([]);

  protected readonly solicitudes =
    signal<OrganizacionSolicitud[]>([]);

  protected readonly procesandoSolicitud =
    signal<number | null>(null);

  protected readonly mensaje =
    signal<string | null>(null);

  protected readonly error =
    signal<string | null>(null);

  constructor(
    private organizacionService: OrganizacionService
  ) {}

  ngOnInit(): void {
    this.cargarOrganizaciones();
    this.cargarSolicitudes();
  }

  cargarOrganizaciones(): void {
    this.organizacionService.obtenerOrganizaciones().subscribe({
      next: (organizaciones) => {
        this.organizaciones.set(organizaciones);
      },
      error: (error) => {
        console.error(
          'Error al cargar organizaciones:',
          error
        );

        this.organizaciones.set([]);
      }
    });
  }

  cargarSolicitudes(): void {
    this.organizacionService.obtenerSolicitudesOrganizaciones().subscribe({
      next: (solicitudes) => {
        this.solicitudes.set(solicitudes);
      },
      error: (error) => {
        console.error(
          'Error al cargar solicitudes:',
          error
        );

        this.solicitudes.set([]);
      }
    });
  }

  aceptarSolicitud(
    idOrganizacion: number
  ): void {

    this.mensaje.set(null);
    this.error.set(null);

    this.procesandoSolicitud.set(idOrganizacion);

    this.organizacionService
      .aceptarSolicitudOrganizacion(idOrganizacion)
      .subscribe({
next: (respuesta) => {

  console.log('Solicitud aceptada:', respuesta);

  this.procesandoSolicitud.set(null);

  this.mensaje.set(respuesta);

  this.cargarSolicitudes();

  this.organizacionService.obtenerOrganizaciones().subscribe({
    next: (organizaciones) => {
      console.log('Organizaciones aceptadas:', organizaciones);
      this.organizaciones.set(organizaciones);
    },
    error: (error) => {
      console.error(
        'Error al cargar organizaciones aceptadas:',
        error
      );
    }
  });
},

        error: (error) => {

          this.procesandoSolicitud.set(null);

          const mensaje =
            error?.error ||
            'No se pudo aceptar la solicitud.';

          this.error.set(mensaje);
        }
      });
  }

  rechazarSolicitud(
    idOrganizacion: number
  ): void {

    this.mensaje.set(null);
    this.error.set(null);

    this.procesandoSolicitud.set(idOrganizacion);

    this.organizacionService
      .rechazarSolicitudOrganizacion(idOrganizacion)
      .subscribe({
        next: (respuesta) => {

          this.procesandoSolicitud.set(null);

          this.mensaje.set(respuesta);

          this.cargarSolicitudes();
        },

        error: (error) => {

          this.procesandoSolicitud.set(null);

          const mensaje =
            error?.error ||
            'No se pudo rechazar la solicitud.';

          this.error.set(mensaje);
        }
      });
  }
}