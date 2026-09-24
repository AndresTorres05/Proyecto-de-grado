import { Component, OnDestroy, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MedicamentoService, Medicamento, MedicamentoRequest } from '../../../../core/medicamentos/medicamento.service';

// Cuánto antes de la hora de la toma se habilita "Ya la tomé".
// Debe coincidir con MINUTOS_ANTES_PARA_CONFIRMAR de salud-service.
const MINUTOS_ANTES_PARA_CONFIRMAR = 15;

@Component({
  selector: 'app-recordatorios',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './recordatorios.html',
  styleUrl: './recordatorios.css'
})
export class Recordatorios implements OnInit, OnDestroy {

  // Hora actual, se refresca sola para que el botón "Ya la tomé"
  // se habilite a la hora correspondiente sin recargar la página.
  protected readonly ahora = signal(Date.now());
  private intervaloReloj?: ReturnType<typeof setInterval>;

  protected readonly medicamentos = signal<Medicamento[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal<string | null>(null);

  protected readonly mostrandoFormulario = signal(false);
  protected readonly guardando = signal(false);
  protected readonly errorFormulario = signal<string | null>(null);
  protected readonly idEditando = signal<number | null>(null);
  protected readonly confirmandoId = signal<number | null>(null);

  // Campos del formulario
  nombre = '';
  dosis = '';
  frecuencia = '';
  intervaloHoras: number | null = null;
  hora = '';
  fechaInicio = '';
  fechaFin = '';

  constructor(private medicamentoService: MedicamentoService) {}

  ngOnInit(): void {
    this.cargar();
    this.intervaloReloj = setInterval(() => this.ahora.set(Date.now()), 30_000);
  }

  ngOnDestroy(): void {
    clearInterval(this.intervaloReloj);
  }

  private cargar(): void {
    this.cargando.set(true);
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

  estaVencido(medicamento: Medicamento): boolean {
    return new Date(medicamento.proximaToma).getTime() <= this.ahora();
  }

  // Solo se puede confirmar a la hora de la toma o poco antes.
  puedeConfirmar(medicamento: Medicamento): boolean {
    return this.ahora() >= this.habilitadoDesde(medicamento);
  }

  formatearHabilitadoDesde(medicamento: Medicamento): string {
    return new Date(this.habilitadoDesde(medicamento))
      .toLocaleTimeString('es-CO', { timeStyle: 'short' });
  }

  private habilitadoDesde(medicamento: Medicamento): number {
    return new Date(medicamento.proximaToma).getTime()
      - MINUTOS_ANTES_PARA_CONFIRMAR * 60_000;
  }

  formatearProximaToma(medicamento: Medicamento): string {
    const fecha = new Date(medicamento.proximaToma);
    return fecha.toLocaleString('es-CO', { dateStyle: 'short', timeStyle: 'short' });
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
      this.errorFormulario.set('Nombre, hora de la primera toma e intervalo son obligatorios.');
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

  confirmarToma(medicamento: Medicamento): void {
    this.confirmandoId.set(medicamento.idMedicamento);

    this.medicamentoService.confirmarToma(medicamento.idMedicamento).subscribe({
      next: () => {
        this.confirmandoId.set(null);
        this.cargar();
      },
      error: () => {
        this.confirmandoId.set(null);
        this.error.set('No se pudo confirmar la toma.');
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