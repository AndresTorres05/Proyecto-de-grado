import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  MedicamentoService,
  Medicamento,
  MedicamentoRequest,
  MINUTOS_AVISO_PREVIO,
  formatearHora,
  formatearProximaToma
} from '../../../../core/medicamentos/medicamento.service';
import { Icon } from '../../../../shared/icon/icon';

// Opciones de "Cada cuántas horas": de 1 a 12, más una vez al día.
const INTERVALOS_HORAS = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 24];

@Component({
  selector: 'app-recordatorios',
  standalone: true,
  imports: [FormsModule, Icon],
  templateUrl: './recordatorios.html',
  styleUrl: './recordatorios.css'
})
export class Recordatorios implements OnInit, OnDestroy {

  protected readonly minutosAvisoPrevio = MINUTOS_AVISO_PREVIO;
  protected readonly formatearHora = formatearHora;
  protected readonly formatearProximaToma = formatearProximaToma;

  private intervaloReloj?: ReturnType<typeof setInterval>;

  protected readonly medicamentos = signal<Medicamento[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal<string | null>(null);

  protected readonly mostrandoFormulario = signal(false);
  protected readonly guardando = signal(false);
  protected readonly errorFormulario = signal<string | null>(null);
  protected readonly idEditando = signal<number | null>(null);

  // Campos del formulario
  nombre = '';
  dosis = '';
  frecuencia = '';
  intervaloHoras: number | null = null;
  hora = '';
  fechaInicio = '';
  fechaFin = '';

  constructor(private medicamentoService: MedicamentoService) {}

  // Si un medicamento ya guardado tiene un intervalo fuera de la lista,
  // se agrega para no perderlo al editar.
  protected opcionesIntervalo(): number[] {
    const actual = this.intervaloHoras;
    if (actual && !INTERVALOS_HORAS.includes(actual)) {
      return [...INTERVALOS_HORAS, actual].sort((a, b) => a - b);
    }
    return INTERVALOS_HORAS;
  }

  // Abre el selector de hora al hacer clic en cualquier parte del campo,
  // no solo en el iconito del reloj.
  protected abrirSelectorHora(event: Event): void {
    const input = event.target as HTMLInputElement;
    try {
      input.showPicker();
    } catch {
      // Navegadores sin showPicker(): se deja el comportamiento normal.
    }
  }

  ngOnInit(): void {
    this.cargar();
    // Cuando pasa la hora de una toma, el backend la avanza sola a la
    // siguiente; se recarga la lista para mostrar la nueva "Próxima toma".
    this.intervaloReloj = setInterval(() => {
      const ahora = Date.now();
      const hayTomaPasada = this.medicamentos().some(
        (m) => new Date(m.proximaToma).getTime() <= ahora
      );
      if (hayTomaPasada) {
        this.cargar(false);
      }
    }, 60_000);
  }

  ngOnDestroy(): void {
    clearInterval(this.intervaloReloj);
  }

  private cargar(mostrarCargando = true): void {
    if (mostrarCargando) {
      this.cargando.set(true);
    }
    this.error.set(null);

    this.medicamentoService.listar().subscribe({
      next: (medicamentos) => {
        this.medicamentos.set(medicamentos);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar tus medicamentos.');
        this.cargando.set(false);
      }
    });
  }

  // Horas del día en que toca el medicamento, p. ej. cada 8 horas desde
  // las 8:00 -> "8:00 a. m. · 4:00 p. m. · 12:00 a. m.". Solo cuando el
  // intervalo cabe exacto en el día y no son demasiadas horas para leer.
  protected horarioDelDia(medicamento: Medicamento): string | null {
    const intervalo = medicamento.intervaloHoras;
    if (!medicamento.hora || !intervalo || 24 % intervalo !== 0 || 24 / intervalo > 6) {
      return null;
    }

    const [h, m] = medicamento.hora.split(':').map(Number);
    const tomas: string[] = [];
    for (let i = 0; i < 24 / intervalo; i++) {
      const hora = (h + i * intervalo) % 24;
      tomas.push(formatearHora(`${hora}:${m}`));
    }
    return tomas.join(' · ');
  }

  abrirFormularioNuevo(): void {
    this.idEditando.set(null);
    this.limpiarFormulario();
    this.mostrandoFormulario.set(true);
  }

  abrirFormularioEditar(medicamento: Medicamento): void {
    this.idEditando.set(medicamento.idMedicamento);
    this.nombre = medicamento.nombre;
    this.dosis = medicamento.dosis;
    this.frecuencia = medicamento.frecuencia;
    this.intervaloHoras = medicamento.intervaloHoras;
    this.hora = medicamento.hora;
    this.fechaInicio = medicamento.fechaInicio ?? '';
    this.fechaFin = medicamento.fechaFin ?? '';
    this.mostrandoFormulario.set(true);
  }

  cancelarFormulario(): void {
    this.mostrandoFormulario.set(false);
    this.errorFormulario.set(null);
    this.limpiarFormulario();
  }

  private limpiarFormulario(): void {
    this.nombre = '';
    this.dosis = '';
    this.frecuencia = '';
    this.intervaloHoras = null;
    this.hora = '';
    this.fechaInicio = '';
    this.fechaFin = '';
  }

  guardar(): void {
    if (!this.nombre.trim() || !this.hora.trim() || !this.intervaloHoras) {
      this.errorFormulario.set('Escribe el nombre, cada cuántas horas y a qué hora te lo tomas.');
      return;
    }

    this.guardando.set(true);
    this.errorFormulario.set(null);

    const request: MedicamentoRequest = {
      nombre: this.nombre,
      dosis: this.dosis,
      frecuencia: this.frecuencia,
      intervaloHoras: this.intervaloHoras,
      hora: this.hora,
      fechaInicio: this.fechaInicio || undefined,
      fechaFin: this.fechaFin || undefined
    };

    const idEditando = this.idEditando();
    const peticion = idEditando
      ? this.medicamentoService.actualizar(idEditando, request)
      : this.medicamentoService.crear(request);

    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.mostrandoFormulario.set(false);
        this.limpiarFormulario();
        this.cargar();
      },
      error: () => {
        this.guardando.set(false);
        this.errorFormulario.set('No se pudo guardar el medicamento.');
      }
    });
  }

  eliminar(medicamento: Medicamento): void {
    const confirmado = confirm(`¿Eliminar ${medicamento.nombre} de tus medicamentos?`);
    if (!confirmado) {
      return;
    }

    this.medicamentoService.eliminar(medicamento.idMedicamento).subscribe({
      next: () => this.cargar(),
      error: () => this.error.set('No se pudo eliminar el medicamento.')
    });
  }
}