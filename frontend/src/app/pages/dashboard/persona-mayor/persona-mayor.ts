import { Component, OnInit, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Icon } from '../../../shared/icon/icon';
import { AuthService } from '../../../core/auth/auth.service';
import { ActividadService, ActividadDisponible } from '../../../core/actividades/actividad.service';
import { GustoService, Gusto, CategoriaGusto } from '../../../core/gustos/gusto.service';
import { AcompananteService, Acompanante } from '../../../core/acompanantes/acompanante.service';
import { EmergenciaService } from '../../../core/emergencia/emergencia.service';
import { MedicamentoService, Medicamento } from '../../../core/medicamentos/medicamento.service';
import { DatePipe, registerLocaleData } from '@angular/common';
import localeEs from '@angular/common/locales/es-CO';

registerLocaleData(localeEs);

interface AccesoRapido {
  icon: string;
  label: string;
}

interface CategoriaTab {
  valor: CategoriaGusto;
  label: string;
  icon: string;
}

@Component({
  selector: 'app-persona-mayor-dashboard',
  imports: [FormsModule, Icon, DatePipe],
  templateUrl: './persona-mayor.html',
  styleUrl: './persona-mayor.css',
})
export class PersonaMayorDashboard implements OnInit {

  protected readonly nombreUsuario: string;
  protected readonly fechaActual = new Date();

  protected readonly medicamentos = signal<Medicamento[]>([]);
  protected readonly cargandoMedicamentos = signal(true);
  protected readonly confirmandoToma = signal(false);

  protected readonly proximoMedicamento = computed(() => {
    const lista = this.medicamentos();
    if (lista.length === 0) {
      return null;
    }

    return [...lista].sort(
      (a, b) => new Date(a.proximaToma).getTime() - new Date(b.proximaToma).getTime()
    )[0];
  });

  // Solo actividades con asistencia confirmada (inscrito) y que no
  // hayan pasado, ordenadas de la más cercana a la más lejana.
  protected readonly actividades = signal<ActividadDisponible[]>([]);

  protected readonly categorias: CategoriaTab[] = [
    { valor: 'GUSTO', label: 'Gustos', icon: 'heart' },
    { valor: 'TALENTO', label: 'Talentos', icon: 'sparkles' },
    { valor: 'HOBBY', label: 'Hobbies', icon: 'target' }
  ];

  protected readonly categoriaActiva = signal<CategoriaGusto>('GUSTO');

  protected readonly gustosDisponibles = signal<Gusto[]>([]);
  protected readonly gustosSeleccionados = signal<Set<number>>(new Set());
  protected readonly guardandoGustos = signal(false);
  protected readonly errorGustos = signal<string | null>(null);

  protected readonly gustosDeCategoriaActiva = computed(() =>
    this.gustosDisponibles().filter((g) => g.categoria === this.categoriaActiva())
  );

  protected readonly acompanante = signal<Acompanante | null>(null);
  protected readonly mostrandoConfirmacionEmergencia = signal(false);
  protected readonly enviandoEmergencia = signal(false);
  protected readonly mensajeEmergencia = signal<string | null>(null);
  protected readonly errorEmergencia = signal<string | null>(null);

  /*protected readonly accesos: AccesoRapido[] = [
    { icon: 'user', label: 'Mi información' },
    { icon: 'phone', label: 'Mis contactos de emergencia' }
  ];*/

  constructor(
    private authService: AuthService,
    private actividadService: ActividadService,
    private gustoService: GustoService,
    private acompananteService: AcompananteService,
    private emergenciaService: EmergenciaService,
    private medicamentoService: MedicamentoService
  ) {
    this.nombreUsuario = this.authService.getNombreUsuario();
  }

  ngOnInit(): void {
    this.actividadService.listarDisponibles().subscribe((actividades) => {
      // Fecha local (YYYY-MM-DD); toISOString() usaría UTC.
      const hoy = new Date().toLocaleDateString('en-CA');
      this.actividades.set(
        actividades
          .filter((a) => a.inscrito && (!a.fecha || a.fecha >= hoy))
          .sort((a, b) =>
            (a.fecha ?? '9999').localeCompare(b.fecha ?? '9999')
            || (a.hora ?? '').localeCompare(b.hora ?? ''))
      );
    });
    this.acompananteService.obtenerAcompanantes().subscribe((acompanantes) =>
      this.acompanante.set(acompanantes[0] ?? null)
    );
    this.cargarMedicamentos();

    const idPersonaMayor = this.authService.getIdUsuario();
    if (idPersonaMayor === null) {
      return;
    }

    this.gustoService.listar().subscribe((gustos) => this.gustosDisponibles.set(gustos));
    this.gustoService.listarAsignados(idPersonaMayor).subscribe((gustos) =>
      this.gustosSeleccionados.set(new Set(gustos.map((g) => g.idGusto)))
    );
  }

  private cargarMedicamentos(): void {
    this.cargandoMedicamentos.set(true);

    this.medicamentoService.listar().subscribe({
      next: (medicamentos) => {
        this.medicamentos.set(medicamentos);
        this.cargandoMedicamentos.set(false);
      },
      error: () => {
        this.cargandoMedicamentos.set(false);
      }
    });
  }

  estaVencido(medicamento: Medicamento): boolean {
    return new Date(medicamento.proximaToma).getTime() <= Date.now();
  }

  formatearProximaToma(medicamento: Medicamento): string {
    const fecha = new Date(medicamento.proximaToma);
    return fecha.toLocaleString('es-CO', { dateStyle: 'short', timeStyle: 'short' });
  }

  confirmarTomaProximoMedicamento(): void {
    const medicamento = this.proximoMedicamento();
    if (!medicamento) {
      return;
    }

    this.confirmandoToma.set(true);

    this.medicamentoService.confirmarToma(medicamento.idMedicamento).subscribe({
      next: () => {
        this.confirmandoToma.set(false);
        this.cargarMedicamentos();
      },
      error: () => {
        this.confirmandoToma.set(false);
      }
    });
  }

  cambiarCategoria(categoria: CategoriaGusto): void {
    this.categoriaActiva.set(categoria);
  }

  contarSeleccionados(categoria: CategoriaGusto): number {
    const seleccionados = this.gustosSeleccionados();
    return this.gustosDisponibles().filter((g) => g.categoria === categoria && seleccionados.has(g.idGusto)).length;
  }

  estaSeleccionado(idGusto: number): boolean {
    return this.gustosSeleccionados().has(idGusto);
  }

  alternarGusto(idGusto: number): void {
    const seleccionados = new Set(this.gustosSeleccionados());
    if (seleccionados.has(idGusto)) {
      seleccionados.delete(idGusto);
    } else {
      seleccionados.add(idGusto);
    }
    this.gustosSeleccionados.set(seleccionados);
  }

  guardarGustos(): void {
    const idPersonaMayor = this.authService.getIdUsuario();
    if (idPersonaMayor === null) {
      return;
    }

    this.guardandoGustos.set(true);
    this.errorGustos.set(null);

    this.gustoService.asignar(idPersonaMayor, Array.from(this.gustosSeleccionados())).subscribe({
      next: () => this.guardandoGustos.set(false),
      error: () => {
        this.guardandoGustos.set(false);
        this.errorGustos.set('No se pudieron guardar tus gustos');
      }
    });
  }

  activarConfirmacionEmergencia(): void {
    this.mostrandoConfirmacionEmergencia.set(true);
    this.mensajeEmergencia.set(null);
    this.errorEmergencia.set(null);
  }

  cancelarEmergencia(): void {
    this.mostrandoConfirmacionEmergencia.set(false);
  }

  confirmarEmergencia(): void {
    this.enviandoEmergencia.set(true);
    this.errorEmergencia.set(null);
    this.mensajeEmergencia.set(null);

    this.emergenciaService.activarEmergencia().subscribe({
      next: (respuesta) => {
        this.enviandoEmergencia.set(false);
        this.mostrandoConfirmacionEmergencia.set(false);
        this.mensajeEmergencia.set(respuesta);
      },
      error: (error) => {
        this.enviandoEmergencia.set(false);

        const mensaje =
          error?.error || 'No se pudo enviar la alerta de emergencia.';

        this.errorEmergencia.set(mensaje);
      }
    });
  }
}