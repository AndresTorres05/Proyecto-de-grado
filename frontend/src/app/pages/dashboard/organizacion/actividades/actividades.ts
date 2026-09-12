import { Component, OnInit, signal } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import {
  ActividadService,
  Actividad,
  ActividadRequest,
  ParticipanteActividad
} from '../../../../core/actividades/actividad.service';

import { AuthService } from '../../../../core/auth/auth.service';
import { OrganizacionService } from '../../../../core/organizacion/organizacion.service';
import { Icon } from '../../../../shared/icon/icon';

@Component({
  selector: 'app-actividades',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    DatePipe,
    Icon
  ],
  templateUrl: './actividades.html',
  styleUrl: './actividades.css'
})
export class Actividades implements OnInit {

  protected readonly actividades = signal<Actividad[]>([]);
  protected readonly error = signal<string | null>(null);
  protected readonly cargando = signal(false);

  protected readonly nombreUsuario = signal('');

  // Crear actividad
  protected mostrarFormulario = signal(false);

  protected mostrarConfirmacion = signal(false);

  protected tipoConfirmacion: 'crear' | 'editar' = 'crear';

  protected actividadEliminando: Actividad | null = null;
protected mostrarConfirmacionEliminacion = signal(false);

protected nuevaActividad: ActividadRequest = {
  nombre: '',
  descripcion: null,
  fecha: null,
  hora: null,
  lugar: null,
  tipo: null,
  cupos: null,
  responsable: null
};

  // Editar actividad
  protected actividadEditandoId: number | null = null;

protected actividadEditando: ActividadRequest = {
  nombre: '',
  descripcion: null,
  fecha: null,
  hora: null,
  lugar: null,
  tipo: null,
  cupos: null,
  responsable: null
};

  // Participantes
  protected actividadParticipantes = signal<Actividad | null>(null);
  protected participantes = signal<ParticipanteActividad[]>([])
  protected cargandoParticipantes = signal(false);

constructor(
  private actividadService: ActividadService,
  private authService: AuthService,
  private organizacionService: OrganizacionService,
  private route: ActivatedRoute
) {
    this.nombreUsuario.set(this.authService.getNombreUsuario());
  }

ngOnInit(): void {
  this.cargarActividades();
  this.cargarInformacionOrganizacion();

  this.route.queryParams.subscribe(params => {
    if (params['abrir'] === 'registrar') {
      this.abrirFormulario();
    }
  });
}

  private cargarInformacionOrganizacion(): void {
    this.organizacionService.obtenerInformacion().subscribe({
      next: (data) => {
        this.nombreUsuario.set(data.nombre);
      },
      error: (error) => {
        console.error(
          'Error al cargar la información de la organización:',
          error
        );
      }
    });
  }

  private cargarActividades(): void {
    this.cargando.set(true);
    this.error.set(null);

    this.actividadService.listarMias().subscribe({
      next: (actividades) => {
        const ordenadas = [...actividades].sort((a, b) => {
          if (!a.fecha && !b.fecha) return 0;
          if (!a.fecha) return 1;
          if (!b.fecha) return -1;

          return a.fecha.localeCompare(b.fecha);
        });

        this.actividades.set(ordenadas);
        this.cargando.set(false);
      },
      error: (error) => {
        console.error('Error al cargar actividades:', error);
        this.error.set('No se pudieron cargar las actividades');
        this.cargando.set(false);
      }
    });
  }

  protected actividadesProximas(): Actividad[] {
    const hoy = this.fechaHoy();

    return this.actividades().filter(
      actividad => actividad.fecha !== null && actividad.fecha >= hoy
    );
  }

  protected actividadesPasadas(): Actividad[] {
    const hoy = this.fechaHoy();

    return this.actividades()
      .filter(
        actividad => actividad.fecha !== null && actividad.fecha < hoy
      )
      .reverse();
  }

  protected fechaHoy(): string {
    const ahora = new Date();

    const year = ahora.getFullYear();
    const month = String(ahora.getMonth() + 1).padStart(2, '0');
    const day = String(ahora.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }

  protected abrirFormulario(): void {
this.nuevaActividad = {
  nombre: '',
  descripcion: null,
  fecha: null,
  hora: null,
  lugar: null,
  tipo: null,
  cupos: null,
  responsable: null
};

    this.mostrarFormulario.set(true);
  }

  protected cerrarFormulario(): void {
    this.mostrarFormulario.set(false);
  }

protected crearActividad(): void {

  if (!this.nuevaActividad.nombre.trim()) {
    this.error.set('El nombre de la actividad es obligatorio');
    return;
  }

  if (!this.nuevaActividad.fecha) {
    this.error.set('La fecha de la actividad es obligatoria');
    return;
  }

  if (!this.nuevaActividad.hora) {
    this.error.set('La hora de la actividad es obligatoria');
    return;
  }

  if (!this.nuevaActividad.lugar?.trim()) {
    this.error.set('El lugar de la actividad es obligatorio');
    return;
  }

  if (
    this.nuevaActividad.fecha < new Date().toISOString().split('T')[0]
  ) {
    this.error.set(
      'No se puede crear una actividad con una fecha anterior a hoy'
    );
    return;
  }

  this.error.set(null);
  this.tipoConfirmacion = 'crear';
  this.mostrarConfirmacion.set(true);
}

protected confirmarCreacion(): void {
  this.mostrarConfirmacion.set(false);

  if (this.tipoConfirmacion === 'crear') {
    this.actividadService.crear(this.nuevaActividad).subscribe({
      next: () => {
        this.cerrarFormulario();
        this.cargarActividades();
      },
      error: (error) => {
        console.error('Error al crear actividad:', error);
        this.error.set('No se pudo crear la actividad.');
      }
    });

    return;
  }

  if (this.tipoConfirmacion === 'editar' && this.actividadEditandoId !== null) {
    this.actividadService
      .actualizar(
        this.actividadEditandoId,
        this.actividadEditando
      )
      .subscribe({
        next: () => {
          this.cancelarEdicion();
          this.cargarActividades();
        },
        error: (error) => {
          console.error('Error al actualizar actividad:', error);
          this.error.set('No se pudo actualizar la actividad');
        }
      });
  }
}

protected cancelarConfirmacion(): void {
  this.mostrarConfirmacion.set(false);
}

  protected editarActividad(actividad: Actividad): void {
    if (actividad.fecha && actividad.fecha < this.fechaHoy()) {
      return;
    }

    this.actividadEditandoId = actividad.idActividad;

this.actividadEditando = {
  nombre: actividad.nombre,
  descripcion: actividad.descripcion,
  fecha: actividad.fecha,
  hora: actividad.hora,
  lugar: actividad.lugar,
  tipo: actividad.tipo,
  cupos: actividad.cupos,
  responsable: actividad.responsable
};
  }

  protected cancelarEdicion(): void {
    this.actividadEditandoId = null;

this.actividadEditando = {
  nombre: '',
  descripcion: null,
  fecha: null,
  hora: null,
  lugar: null,
  tipo: null,
  cupos: null,
  responsable: null
};
  }

protected guardarActividad(): void {
  if (
    this.actividadEditandoId === null ||
    !this.actividadEditando.nombre.trim()
  ) {
    return;
  }

  this.error.set(null);
  this.tipoConfirmacion = 'editar';
  this.mostrarConfirmacion.set(true);
}

 protected eliminarActividad(actividad: Actividad): void {
  this.actividadEliminando = actividad;
  this.error.set(null);
  this.mostrarConfirmacionEliminacion.set(true);
}

protected confirmarEliminacion(): void {
  if (!this.actividadEliminando) {
    return;
  }

  const idActividad = this.actividadEliminando.idActividad;

  this.mostrarConfirmacionEliminacion.set(false);

  this.actividadService.eliminar(idActividad).subscribe({
    next: () => {
      this.actividadEliminando = null;
      this.cargarActividades();
    },
    error: (error) => {
      console.error('Error al eliminar actividad:', error);
      this.error.set('No se pudo eliminar la actividad');
      this.actividadEliminando = null;
    }
  });
}

protected cancelarEliminacion(): void {
  this.mostrarConfirmacionEliminacion.set(false);
  this.actividadEliminando = null;
}

  protected verParticipantes(actividad: Actividad): void {
    this.actividadParticipantes.set(actividad);
    this.participantes.set([]);
    this.cargandoParticipantes.set(true);

    this.actividadService.listarParticipantes(
      actividad.idActividad
    ).subscribe({
next: (participantes) => {
  this.participantes.set(participantes);
  this.cargandoParticipantes.set(false);
},
      error: (error) => {
        console.error('Error al cargar participantes:', error);
        this.error.set('No se pudieron cargar los participantes');
        this.cargandoParticipantes.set(false);
      }
    });
  }

  protected cerrarParticipantes(): void {
    this.actividadParticipantes.set(null);
    this.participantes.set([]);
  }

  protected registrarAsistencia(
    participante: ParticipanteActividad,
    asistio: boolean
  ): void {

const actividad = this.actividadParticipantes();

if (!actividad) {
  return;
}

if (actividad.fecha && actividad.fecha < this.fechaHoy()) {
  return;
}

    this.actividadService
      .registrarAsistencia(
        actividad.idActividad,
        participante.idPersonaMayor,
        asistio
      )
      .subscribe({
        next: () => {
          participante.asistio = asistio;
          this.participantes.set([...this.participantes()]);
        },
        error: (error) => {
          console.error(
            'Error al registrar asistencia:',
            error
          );

          this.error.set(
            'No se pudo registrar la asistencia'
          );
        }
      });
  }

  protected contarAsistentes(): number {
    return this.participantes().filter(
      participante => participante.asistio === true
    ).length;
  }

  protected contarConfirmados(): number {
    return this.participantes().length;
  }
}