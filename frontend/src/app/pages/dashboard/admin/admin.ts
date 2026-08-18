import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DashboardShell, ShellNavItem } from '../../../shared/dashboard-shell/dashboard-shell';
import { RolService, Rol } from '../../../core/roles/rol.service';
import { GustoService, Gusto } from '../../../core/gustos/gusto.service';

@Component({
  selector: 'app-admin-dashboard',
  imports: [DashboardShell, FormsModule],
  templateUrl: './admin.html',
  styleUrl: './admin.css'
})
export class AdminDashboard implements OnInit {
  protected readonly navItems: ShellNavItem[] = [
    { icon: '🏷️', label: 'Tipos de usuario', active: true },
    { icon: '❤️', label: 'Gustos' }
  ];

  protected readonly roles = signal<Rol[]>([]);
  protected readonly gustos = signal<Gusto[]>([]);

  protected nuevoRolNombre = '';
  protected rolEditandoId: number | null = null;
  protected rolEditandoNombre = '';
  protected errorRoles = signal<string | null>(null);

  protected nuevoGustoNombre = '';
  protected gustoEditandoId: number | null = null;
  protected gustoEditandoNombre = '';
  protected errorGustos = signal<string | null>(null);

  constructor(
    private rolService: RolService,
    private gustoService: GustoService
  ) {}

  ngOnInit(): void {
    this.cargarRoles();
    this.cargarGustos();
  }

  private cargarRoles(): void {
    this.rolService.listar().subscribe((roles) => this.roles.set(roles));
  }

  private cargarGustos(): void {
    this.gustoService.listar().subscribe((gustos) => this.gustos.set(gustos));
  }

  crearRol(): void {
    if (!this.nuevoRolNombre.trim()) {
      return;
    }
    this.errorRoles.set(null);
    this.rolService.crear({ nombre: this.nuevoRolNombre.trim() }).subscribe({
      next: () => {
        this.nuevoRolNombre = '';
        this.cargarRoles();
      },
      error: (err) => this.errorRoles.set(this.mensajeError(err, 'Ya existe un tipo de usuario con ese nombre'))
    });
  }

  editarRol(rol: Rol): void {
    this.rolEditandoId = rol.idRol;
    this.rolEditandoNombre = rol.nombre;
  }

  cancelarEdicionRol(): void {
    this.rolEditandoId = null;
    this.rolEditandoNombre = '';
  }

  guardarRol(): void {
    if (this.rolEditandoId === null || !this.rolEditandoNombre.trim()) {
      return;
    }
    this.errorRoles.set(null);
    this.rolService.actualizar(this.rolEditandoId, { nombre: this.rolEditandoNombre.trim() }).subscribe({
      next: () => {
        this.cancelarEdicionRol();
        this.cargarRoles();
      },
      error: (err) => this.errorRoles.set(this.mensajeError(err, 'No se pudo actualizar el tipo de usuario'))
    });
  }

  eliminarRol(rol: Rol): void {
    this.errorRoles.set(null);
    this.rolService.eliminar(rol.idRol).subscribe({
      next: () => this.cargarRoles(),
      error: (err) =>
        this.errorRoles.set(
          this.mensajeError(err, 'No se puede eliminar: hay usuarios con este tipo de usuario asignado')
        )
    });
  }

  crearGusto(): void {
    if (!this.nuevoGustoNombre.trim()) {
      return;
    }
    this.errorGustos.set(null);
    this.gustoService.crear({ nombre: this.nuevoGustoNombre.trim() }).subscribe({
      next: () => {
        this.nuevoGustoNombre = '';
        this.cargarGustos();
      },
      error: (err) => this.errorGustos.set(this.mensajeError(err, 'Ya existe un gusto con ese nombre'))
    });
  }

  editarGusto(gusto: Gusto): void {
    this.gustoEditandoId = gusto.idGusto;
    this.gustoEditandoNombre = gusto.nombre;
  }

  cancelarEdicionGusto(): void {
    this.gustoEditandoId = null;
    this.gustoEditandoNombre = '';
  }

  guardarGusto(): void {
    if (this.gustoEditandoId === null || !this.gustoEditandoNombre.trim()) {
      return;
    }
    this.errorGustos.set(null);
    this.gustoService.actualizar(this.gustoEditandoId, { nombre: this.gustoEditandoNombre.trim() }).subscribe({
      next: () => {
        this.cancelarEdicionGusto();
        this.cargarGustos();
      },
      error: (err) => this.errorGustos.set(this.mensajeError(err, 'No se pudo actualizar el gusto'))
    });
  }

  eliminarGusto(gusto: Gusto): void {
    this.errorGustos.set(null);
    this.gustoService.eliminar(gusto.idGusto).subscribe({
      next: () => this.cargarGustos(),
      error: (err) =>
        this.errorGustos.set(this.mensajeError(err, 'No se puede eliminar: hay personas con este gusto asignado'))
    });
  }

  private mensajeError(err: any, porDefecto: string): string {
    return typeof err?.error === 'string' ? err.error : porDefecto;
  }
}
