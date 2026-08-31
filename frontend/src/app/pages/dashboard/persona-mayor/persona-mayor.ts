import { Component, OnInit, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/auth/auth.service';
import { ActividadService, Actividad } from '../../../core/actividades/actividad.service';
import { GustoService, Gusto, CategoriaGusto } from '../../../core/gustos/gusto.service';
import { EmergenciaService } from '../../../core/emergencia/emergencia.service';

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
  imports: [FormsModule],
  templateUrl: './persona-mayor.html',
  styleUrl: './persona-mayor.css'
})
export class PersonaMayorDashboard implements OnInit {

  protected readonly nombreUsuario: string;

  protected readonly recordatorio = {
    hora: '2:00 p.m.',
    detalle: 'Tomar Losartán 50mg',
    nota: 'Con un vaso de agua, después de almorzar.'
  };

  protected readonly actividades = signal<Actividad[]>([]);

  protected readonly categorias: CategoriaTab[] = [
    { valor: 'GUSTO', label: 'Gustos', icon: '❤️' },
    { valor: 'TALENTO', label: 'Talentos', icon: '✨' },
    { valor: 'HOBBY', label: 'Hobbies', icon: '🎯' }
  ];

  protected readonly categoriaActiva = signal<CategoriaGusto>('GUSTO');

  protected readonly gustosDisponibles = signal<Gusto[]>([]);
  protected readonly gustosSeleccionados = signal<Set<number>>(new Set());
  protected readonly guardandoGustos = signal(false);
  protected readonly errorGustos = signal<string | null>(null);

  protected readonly gustosDeCategoriaActiva = computed(() =>
    this.gustosDisponibles().filter((g) => g.categoria === this.categoriaActiva())
  );

  protected readonly mostrandoConfirmacionEmergencia = signal(false);
  protected readonly enviandoEmergencia = signal(false);
  protected readonly mensajeEmergencia = signal<string | null>(null);
  protected readonly errorEmergencia = signal<string | null>(null);

  protected readonly accesos: AccesoRapido[] = [
    { icon: '👤', label: 'Mi información' },
    { icon: '☎️', label: 'Mis contactos de emergencia' }
  ];

  constructor(
    private authService: AuthService,
    private actividadService: ActividadService,
    private gustoService: GustoService,
    private emergenciaService: EmergenciaService
  ) {
    this.nombreUsuario = this.authService.getNombreUsuario();
  }

  ngOnInit(): void {
    this.actividadService.listar().subscribe((actividades) => this.actividades.set(actividades));

    const idPersonaMayor = this.authService.getIdUsuario();
    if (idPersonaMayor === null) {
      return;
    }

    this.gustoService.listar().subscribe((gustos) => this.gustosDisponibles.set(gustos));
    this.gustoService.listarAsignados(idPersonaMayor).subscribe((gustos) =>
      this.gustosSeleccionados.set(new Set(gustos.map((g) => g.idGusto)))
    );
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