import { Component, OnDestroy, OnInit, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  CitaMedicaService,
  CitaMedica,
  CitaMedicaRequest,
  TEXTO_AVISOS_CITA,
  formatearFechaCita,
  momentoDeCita,
  separarCitas,
  tiempoParaCita
} from '../../../../core/citas-medicas/cita-medica.service';
import { formatearHora } from '../../../../core/medicamentos/medicamento.service';
import { alCambiar } from '../../../../core/tiempo-real/tiempo-real.service';
import { Icon } from '../../../../shared/icon/icon';

/** Citas pasadas que se muestran antes de pulsar "Ver todas". */
const PASADAS_VISIBLES = 5;

/**
 * Citas médicas de la persona mayor: las próximas, el historial de las que
 * ya pasaron y un formulario para registrarlas o editarlas. salud-service
 * envía los recordatorios por SMS un día antes y una hora antes.
 */
@Component({
  selector: 'app-citas-medicas',
  standalone: true,
  imports: [FormsModule, Icon],
  templateUrl: './citas-medicas.html',
  styleUrl: './citas-medicas.css'
})
export class CitasMedicas implements OnInit, OnDestroy {

  protected readonly textoAvisos = TEXTO_AVISOS_CITA;
  protected readonly formatearHora = formatearHora;

  /** Hora actual; se refresca cada minuto para que las citas pasen solas al historial. */
  protected readonly ahora = signal(new Date());
  private intervaloReloj?: ReturnType<typeof setInterval>;

  protected readonly citas = signal<CitaMedica[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal<string | null>(null);

  protected readonly proximas = computed(() => separarCitas(this.citas(), this.ahora()).proximas);
  protected readonly pasadas = computed(() => separarCitas(this.citas(), this.ahora()).pasadas);

  protected readonly verTodasLasPasadas = signal(false);
  protected readonly pasadasVisibles = computed(() =>
    this.verTodasLasPasadas() ? this.pasadas() : this.pasadas().slice(0, PASADAS_VISIBLES)
  );

  protected readonly mostrandoFormulario = signal(false);
  protected readonly guardando = signal(false);
  protected readonly errorFormulario = signal<string | null>(null);
  protected readonly idEditando = signal<number | null>(null);

  // Campos del formulario
  titulo = '';
  lugar = '';
  fecha = '';
  hora = '';
  observaciones = '';

  constructor(private citaMedicaService: CitaMedicaService) {
    alCambiar(['citas-medicas'], () => this.cargar(false));
  }

  ngOnInit(): void {
    this.cargar();
    this.intervaloReloj = setInterval(() => this.ahora.set(new Date()), 60_000);
  }

  ngOnDestroy(): void {
    clearInterval(this.intervaloReloj);
  }

  /** Con mostrarCargando en false, la lista se actualiza sin parpadear. */
  private cargar(mostrarCargando = true): void {
    if (mostrarCargando) {
      this.cargando.set(true);
    }
    this.error.set(null);

    this.citaMedicaService.listar().subscribe({
      next: (citas) => {
        this.citas.set(citas);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar tus citas médicas.');
        this.cargando.set(false);
      }
    });
  }

  protected fechaDe(cita: CitaMedica): string {
    return formatearFechaCita(cita.fecha, this.ahora());
  }

  protected faltaPara(cita: CitaMedica): string | null {
    return tiempoParaCita(cita, this.ahora());
  }

  /** Si la fecha y hora escritas en el formulario ya pasaron (la cita irá al historial). */
  protected fechaFormularioYaPaso(): boolean {
    if (!this.fecha || !this.hora) {
      return false;
    }
    const cita = { fecha: this.fecha, hora: this.hora } as CitaMedica;
    return momentoDeCita(cita) <= this.ahora();
  }

  /** Abre el selector de fecha u hora al hacer clic en cualquier parte del campo. */
  protected abrirSelector(event: Event): void {
    const input = event.target as HTMLInputElement;
    try {
      input.showPicker();
    } catch {
      // Navegadores sin showPicker(): se deja el comportamiento normal.
    }
  }

  abrirFormularioNuevo(): void {
    this.idEditando.set(null);
    this.limpiarFormulario();
    this.mostrandoFormulario.set(true);
  }

  abrirFormularioEditar(cita: CitaMedica): void {
    this.idEditando.set(cita.idCita);
    this.titulo = cita.titulo;
    this.lugar = cita.lugar;
    this.fecha = cita.fecha;
    this.hora = cita.hora;
    this.observaciones = cita.observaciones ?? '';
    this.errorFormulario.set(null);
    this.mostrandoFormulario.set(true);
  }

  cancelarFormulario(): void {
    this.mostrandoFormulario.set(false);
    this.errorFormulario.set(null);
    this.limpiarFormulario();
  }

  private limpiarFormulario(): void {
    this.titulo = '';
    this.lugar = '';
    this.fecha = '';
    this.hora = '';
    this.observaciones = '';
  }

  /** Crea o actualiza la cita, según si se está editando. */
  guardar(): void {
    if (!this.titulo.trim() || !this.lugar.trim() || !this.fecha || !this.hora) {
      this.errorFormulario.set('Escribe el motivo, el lugar, la fecha y la hora de la cita.');
      return;
    }

    this.guardando.set(true);
    this.errorFormulario.set(null);

    const request: CitaMedicaRequest = {
      titulo: this.titulo,
      lugar: this.lugar,
      fecha: this.fecha,
      hora: this.hora,
      observaciones: this.observaciones || undefined
    };

    const idEditando = this.idEditando();
    const peticion = idEditando
      ? this.citaMedicaService.actualizar(idEditando, request)
      : this.citaMedicaService.crear(request);

    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.mostrandoFormulario.set(false);
        this.limpiarFormulario();
        this.cargar(false);
      },
      error: (err) => {
        this.guardando.set(false);
        this.errorFormulario.set(
          typeof err?.error === 'string' && err.error ? err.error : 'No se pudo guardar la cita.'
        );
      }
    });
  }

  eliminar(cita: CitaMedica): void {
    const confirmado = confirm(`¿Eliminar la cita "${cita.titulo}"?`);
    if (!confirmado) {
      return;
    }

    this.citaMedicaService.eliminar(cita.idCita).subscribe({
      next: () => this.cargar(false),
      error: () => this.error.set('No se pudo eliminar la cita.')
    });
  }
}
