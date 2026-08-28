import { Component, OnInit, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AuthService } from '../../../../core/auth/auth.service';
import {
  GustoService,
  Gusto,
  CategoriaGusto
} from '../../../../core/gustos/gusto.service';

interface CategoriaTab {
  valor: CategoriaGusto;
  label: string;
  icon: string;
}

@Component({
  selector: 'app-intereses',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './intereses.html',
  styleUrl: './intereses.css'
})
export class Intereses implements OnInit {

  protected readonly categorias: CategoriaTab[] = [
    { valor: 'GUSTO', label: 'Gustos', icon: '❤️' },
    { valor: 'TALENTO', label: 'Talentos', icon: '✨' },
    { valor: 'HOBBY', label: 'Hobbies', icon: '🎯' }
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
  ) {}

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
   * Devuelve un emoji diferente dependiendo
   * del tipo y del nombre del interés.
   *
   * Si posteriormente agregamos nuevos intereses
   * desde la base de datos, también tendrán un emoji.
   */
  obtenerEmoji(gusto: Gusto, indice: number): string {
    const nombre = gusto.nombre.toLowerCase();

    if (gusto.categoria === 'GUSTO') {
      if (nombre.includes('música') || nombre.includes('musica')) return '🎵';
      if (nombre.includes('cine') || nombre.includes('película') || nombre.includes('pelicula')) return '🎬';
      if (nombre.includes('comida') || nombre.includes('cocina') || nombre.includes('gastronom')) return '🍲';
      if (nombre.includes('viaje') || nombre.includes('viajar')) return '✈️';
      if (nombre.includes('naturaleza')) return '🌳';
      if (nombre.includes('animales') || nombre.includes('mascota')) return '🐶';
      if (nombre.includes('arte')) return '🎨';
      if (nombre.includes('baile') || nombre.includes('danza')) return '💃';
      if (nombre.includes('lectura') || nombre.includes('libro')) return '📚';
      if (nombre.includes('jardín') || nombre.includes('jardin')) return '🌷';

      const emojisGustos = ['❤️', '🎵', '🎬', '🍲', '✈️', '🌳', '🐶', '🎨', '💃', '📚', '🌷', '☕'];
      return emojisGustos[indice % emojisGustos.length];
    }

    if (gusto.categoria === 'TALENTO') {
      if (nombre.includes('cocina') || nombre.includes('cocinar')) return '👨‍🍳';
      if (nombre.includes('cantar') || nombre.includes('canto')) return '🎤';
      if (nombre.includes('pintar') || nombre.includes('pintura')) return '🖌️';
      if (nombre.includes('dibujar') || nombre.includes('dibujo')) return '✏️';
      if (nombre.includes('escribir') || nombre.includes('escritura')) return '✍️';
      if (nombre.includes('bailar') || nombre.includes('danza')) return '💃';
      if (nombre.includes('fotografía') || nombre.includes('fotografia')) return '📷';
      if (nombre.includes('jardinería') || nombre.includes('jardineria')) return '🌱';
      if (nombre.includes('manualidad')) return '🧶';

      const emojisTalentos = ['🎤', '🎨', '✍️', '👨‍🍳', '📷', '🌱', '🧶', '🎹', '🎭', '🔨'];
      return emojisTalentos[indice % emojisTalentos.length];
    }

    const emojisHobbies = ['📖', '♟️', '🎮', '🧩', '🚶', '🚲', '🌿', '🧘', '⚽', '🎣', '🧶', '🎸', '🃏', '🏊', '📺'];
    return emojisHobbies[indice % emojisHobbies.length];
  }
}