import { Component, OnInit, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AuthService } from '../../../../core/auth/auth.service';
import {
  GustoService,
  Gusto,
  CategoriaGusto
} from '../../../../core/gustos/gusto.service';
import { alCambiar } from '../../../../core/tiempo-real/tiempo-real.service';
import { Icon } from '../../../../shared/icon/icon';

interface CategoriaTab {
  valor: CategoriaGusto;
  label: string;
  icon: string;
}

// Mapeo exacto: nombre del gusto tal como está en la base de datos -> ícono.
// Si en el futuro agregas un gusto nuevo que no esté aquí, se usa el ícono
// por defecto de su categoría (ver iconoPorDefecto).
const NOMBRE_A_ICONO: Record<string, string> = {
  // GUSTO
  'Leer': 'book',
  'Escuchar música': 'music',
  'Ver televisión o películas': 'film',
  'Cocinar': 'chef-hat',
  'Pasear al aire libre': 'sun',
  'Fotografía': 'camera',
  'Viajar': 'plane',
  'Compartir con mascotas': 'paw',
  'Ver deportes': 'activity',
  'Conversar con amigos': 'message-circle',

  // TALENTO
  'Tocar un instrumento': 'guitar',
  'Cantar': 'mic',
  'Pintar o dibujar': 'palette',
  'Escribir': 'pen',
  'Manualidades y artesanías': 'scissors',
  'Costura o tejido': 'yarn',
  'Baile': 'dance',
  'Carpintería': 'hammer',
  'Repostería': 'cake',
  'Actuación o teatro': 'drama',

  // HOBBY
  'Ejercicio físico': 'dumbbell',
  'Caminar': 'footprints',
  'Yoga o estiramiento': 'stretch',
  'Jardinería': 'sprout',
  'Pesca': 'fish',
  'Juegos de mesa': 'dice',
  'Rompecabezas': 'puzzle',
  'Ciclismo': 'bike',
  'Natación': 'swim',
  'Voluntariado': 'users'
};

const ICONO_POR_DEFECTO: Record<CategoriaGusto, string> = {
  GUSTO: 'heart',
  TALENTO: 'sparkles',
  HOBBY: 'target'
};

@Component({
  selector: 'app-intereses',
  standalone: true,
  imports: [FormsModule, Icon],
  templateUrl: './intereses.html',
  styleUrl: './intereses.css'
})
export class Intereses implements OnInit {

  protected readonly categorias: CategoriaTab[] = [
    { valor: 'GUSTO', label: 'Gustos', icon: 'heart' },
    { valor: 'TALENTO', label: 'Talentos', icon: 'sparkles' },
    { valor: 'HOBBY', label: 'Pasatiempos', icon: 'target' }
  ];

  protected readonly categoriaActiva = signal<CategoriaGusto>('GUSTO');
  protected readonly gustosDisponibles = signal<Gusto[]>([]);
  protected readonly gustosSeleccionados = signal<Set<number>>(new Set());
  protected readonly guardandoGustos = signal(false);
  protected readonly cargando = signal(true);
  protected readonly errorGustos = signal<string | null>(null);

  protected readonly gustosDeCategoriaActiva = computed(() =>
    this.gustosDisponibles().filter(
      gusto => gusto.categoria === this.categoriaActiva()
    )
  );

  constructor(
    private authService: AuthService,
    private gustoService: GustoService
  ) {
    // Solo se refresca el catálogo: la selección puede tener cambios
    // sin guardar que no se deben perder.
    alCambiar(['gustos'], () =>
      this.gustoService.listar().subscribe((gustos) => this.gustosDisponibles.set(gustos))
    );
  }

  ngOnInit(): void {
    const idPersonaMayor = this.authService.getIdUsuario();

    if (idPersonaMayor === null) {
      this.errorGustos.set('No se pudo identificar al usuario.');
      this.cargando.set(false);
      return;
    }

    this.cargarIntereses(idPersonaMayor);
  }

  private cargarIntereses(idPersonaMayor: number): void {
    this.cargando.set(true);
    this.errorGustos.set(null);

    this.gustoService.listar().subscribe({
      next: (gustos) => {
        this.gustosDisponibles.set(gustos);

        this.gustoService.listarAsignados(idPersonaMayor).subscribe({
          next: (gustosAsignados) => {
            this.gustosSeleccionados.set(
              new Set(gustosAsignados.map(gusto => gusto.idGusto))
            );
            this.cargando.set(false);
          },
          error: () => {
            this.errorGustos.set('No se pudieron cargar tus intereses guardados.');
            this.cargando.set(false);
          }
        });
      },
      error: () => {
        this.errorGustos.set('No se pudieron cargar los intereses disponibles.');
        this.cargando.set(false);
      }
    });
  }

  cambiarCategoria(categoria: CategoriaGusto): void {
    this.categoriaActiva.set(categoria);
  }

  contarSeleccionados(categoria: CategoriaGusto): number {
    const seleccionados = this.gustosSeleccionados();

    return this.gustosDisponibles()
      .filter(gusto => gusto.categoria === categoria && seleccionados.has(gusto.idGusto))
      .length;
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

    const idsGustos = Array.from(this.gustosSeleccionados());

    this.gustoService.asignar(idPersonaMayor, idsGustos).subscribe({
      next: () => {
        this.guardandoGustos.set(false);
      },
      error: () => {
        this.guardandoGustos.set(false);
        this.errorGustos.set('No se pudieron guardar tus intereses.');
      }
    });
  }

  /**
   * Devuelve el nombre del ícono (para <app-icon [name]="...">) que
   * corresponde a este gusto, según su nombre exacto en la base de datos.
   * Si el nombre no está mapeado (por ejemplo un gusto nuevo agregado
   * directamente en la BD), cae al ícono por defecto de su categoría.
   */
  obtenerIcono(gusto: Gusto): string {
    return NOMBRE_A_ICONO[gusto.nombre] ?? ICONO_POR_DEFECTO[gusto.categoria];
  }
}