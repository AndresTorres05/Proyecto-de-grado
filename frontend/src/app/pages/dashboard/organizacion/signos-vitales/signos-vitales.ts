import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import {
  SignosVitalesService,
  SignoVitalRequest,
  PersonaMayor
} from '../../../../core/signos-vitales/signos-vitales.services';

import { AuthService } from '../../../../core/auth/auth.service';

@Component({
  selector: 'app-signos-vitales',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './signos-vitales.html',
  styleUrl: './signos-vitales.css'
})
export class SignosVitales implements OnInit {

  private signosVitalesService = inject(SignosVitalesService);
  private authService = inject(AuthService);

  personasMayores: PersonaMayor[] = [];

  idPersonaMayor: number | null = null;

  presionSistolica: number | null = null;
  presionDiastolica: number | null = null;
  frecuenciaCardiaca: number | null = null;
  temperatura: number | null = null;
  saturacionOxigeno: number | null = null;
  frecuenciaRespiratoria: number | null = null;
  peso: number | null = null;

  observaciones = '';

  cargandoPersonas = false;
  guardando = false;

  mensajeExito = '';
  mensajeError = '';

  ngOnInit(): void {
    this.cargarPersonasMayores();
  }

  cargarPersonasMayores(): void {

    const idUsuario = this.authService.getIdUsuario();

    if (!idUsuario) {
      this.mensajeError = 'No se pudo identificar al usuario.';
      return;
    }

    this.cargandoPersonas = true;
    this.mensajeError = '';

    this.signosVitalesService
      .listarPersonasMayores(idUsuario)
      .subscribe({
        next: (personas) => {
          this.personasMayores = personas;
          this.cargandoPersonas = false;
        },

        error: (error) => {
          console.error('Error cargando personas mayores:', error);

          this.mensajeError =
            'No se pudieron cargar las personas mayores.';

          this.cargandoPersonas = false;
        }
      });
  }

  registrarSignosVitales(): void {

    this.mensajeExito = '';
    this.mensajeError = '';

    const idUsuario = this.authService.getIdUsuario();

    if (!idUsuario) {
      this.mensajeError = 'No se pudo identificar al usuario.';
      return;
    }

    if (!this.idPersonaMayor) {
      this.mensajeError =
        'Debes seleccionar una persona mayor.';
      return;
    }

    this.guardando = true;

    const datos: SignoVitalRequest = {
      presionSistolica: this.presionSistolica,
      presionDiastolica: this.presionDiastolica,
      frecuenciaCardiaca: this.frecuenciaCardiaca,
      temperatura: this.temperatura,
      saturacionOxigeno: this.saturacionOxigeno,
      frecuenciaRespiratoria: this.frecuenciaRespiratoria,
      peso: this.peso,
      observaciones: this.observaciones
    };

    this.signosVitalesService
      .registrar(
        this.idPersonaMayor,
        datos,
        idUsuario
      )
      .subscribe({
        next: () => {

          this.guardando = false;

          this.mensajeExito =
            'Los signos vitales se registraron correctamente.';

          this.limpiarFormulario();
        },

        error: (error) => {

          console.error(
            'Error registrando signos vitales:',
            error
          );

          this.guardando = false;

          if (error.status === 403) {
            this.mensajeError =
              'La persona mayor no está asociada a esta organización.';
          } else if (error.status === 404) {
            this.mensajeError =
              'No se encontró la organización asociada al usuario.';
          } else {
            this.mensajeError =
              'No se pudieron registrar los signos vitales.';
          }
        }
      });
  }

  limpiarFormulario(): void {

    this.idPersonaMayor = null;

    this.presionSistolica = null;
    this.presionDiastolica = null;
    this.frecuenciaCardiaca = null;
    this.temperatura = null;
    this.saturacionOxigeno = null;
    this.frecuenciaRespiratoria = null;
    this.peso = null;

    this.observaciones = '';
  }
}