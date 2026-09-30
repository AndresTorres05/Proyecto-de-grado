import { Component, OnInit, WritableSignal, computed, inject, signal } from '@angular/core';
import { Observable } from 'rxjs';

import {
  ActividadAnalitica,
  AnaliticaService,
  PoblacionAnalitica,
  SaludAnalitica
} from '../../../../core/analitica/analitica.service';
import { alCambiar } from '../../../../core/tiempo-real/tiempo-real.service';
import { Icon } from '../../../../shared/icon/icon';
import { ReporteActividades } from './reportes/reporte-actividades';
import { ReportePoblacion } from './reportes/reporte-poblacion';
import { ReporteSalud } from './reportes/reporte-salud';

type Reporte = 'actividades' | 'salud' | 'poblacion';
type Periodo = '30' | '90' | '365' | 'todo';

interface Estado<T> {
  datos: T | null;
  cargando: boolean;
  error: string | null;
  actualizado: Date | null;
}

const vacio = <T>(): Estado<T> => ({ datos: null, cargando: false, error: null, actualizado: null });

/**
 * Analítica de la organización: tres reportes (pestañas) con indicadores y
 * gráficas de Apache ECharts. Cada reporte se carga la primera vez que se
 * abre y se recarga solo cuando cambian sus datos (tiempo real).
 */
@Component({
  selector: 'app-analitica',
  imports: [Icon, ReporteActividades, ReporteSalud, ReportePoblacion],
  templateUrl: './analitica.html',
  styleUrl: './analitica.css'
})
export class Analitica implements OnInit {

  private analiticaService = inject(AnaliticaService);

  protected readonly reportes: { id: Reporte; nombre: string; icono: string; descripcion: string }[] = [
    { id: 'actividades', nombre: 'Actividades y participación', icono: 'activity',
      descripcion: 'Cuántas actividades haces, cuántas personas se inscriben y cuántas asisten.' },
    { id: 'salud', nombre: 'Salud de la población', icono: 'heart',
      descripcion: 'Estado de los signos vitales y personas que requieren atención.' },
    { id: 'poblacion', nombre: 'Perfil de la población', icono: 'users',
      descripcion: 'Quiénes son tus personas mayores: edad, género, EPS e intereses.' }
  ];

  protected readonly periodos: { id: Periodo; nombre: string }[] = [
    { id: '30', nombre: 'Últimos 30 días' },
    { id: '90', nombre: 'Últimos 90 días' },
    { id: '365', nombre: 'Último año' },
    { id: 'todo', nombre: 'Todo' }
  ];

  protected readonly reporteActivo = signal<Reporte>('actividades');
  protected readonly periodo = signal<Periodo>('90');

  protected readonly actividades = signal<Estado<ActividadAnalitica[]>>(vacio());
  protected readonly salud = signal<Estado<SaludAnalitica>>(vacio());
  protected readonly poblacion = signal<Estado<PoblacionAnalitica>>(vacio());

  /** El reporte de población no depende del período. */
  protected readonly usaPeriodo = computed(() => this.reporteActivo() !== 'poblacion');

  protected readonly descripcionActiva = computed(() =>
    this.reportes.find((r) => r.id === this.reporteActivo())!.descripcion);

  /** Inicio del período (YYYY-MM-DD) o null si es "Todo". */
  protected readonly desde = computed(() => {
    const periodo = this.periodo();
    if (periodo === 'todo') return null;
    const fecha = new Date();
    fecha.setDate(fecha.getDate() - Number(periodo));
    return fecha.toLocaleDateString('en-CA');
  });

  protected readonly estadoActivo = computed<Estado<unknown>>(() => {
    switch (this.reporteActivo()) {
      case 'actividades': return this.actividades();
      case 'salud': return this.salud();
      case 'poblacion': return this.poblacion();
    }
  });

  constructor() {
    // Solo se recarga el reporte si ya se había abierto.
    alCambiar(['actividades'], () => this.actividades().datos && this.cargarActividades());
    alCambiar(['signos-vitales', 'organizaciones', 'usuarios'], () => this.salud().datos && this.cargarSalud());
    alCambiar(['organizaciones', 'usuarios', 'gustos'], () => this.poblacion().datos && this.cargarPoblacion());
  }

  ngOnInit(): void {
    this.cargarActividades();
  }

  protected elegirReporte(reporte: Reporte): void {
    this.reporteActivo.set(reporte);
    if (reporte === 'salud' && !this.salud().datos) this.cargarSalud();
    if (reporte === 'poblacion' && !this.poblacion().datos) this.cargarPoblacion();
  }

  protected elegirPeriodo(periodo: Periodo): void {
    this.periodo.set(periodo);
    // Actividades se filtra en el servidor; salud filtra en el navegador.
    this.cargarActividades();
  }

  protected recargar(): void {
    switch (this.reporteActivo()) {
      case 'actividades': this.cargarActividades(); break;
      case 'salud': this.cargarSalud(); break;
      case 'poblacion': this.cargarPoblacion(); break;
    }
  }

  // ---------- Carga ----------

  private cargarActividades(): void {
    const hasta = this.periodo() === 'todo' ? null : new Date().toLocaleDateString('en-CA');
    this.cargar(this.actividades, this.analiticaService.actividades(this.desde(), hasta));
  }

  private cargarSalud(): void {
    this.cargar(this.salud, this.analiticaService.salud());
  }

  private cargarPoblacion(): void {
    this.cargar(this.poblacion, this.analiticaService.poblacion());
  }

  /** Mantiene los datos anteriores mientras recarga (sin parpadeo). */
  private cargar<T>(estado: WritableSignal<Estado<T>>, peticion: Observable<T>): void {
    estado.update((e) => ({ ...e, cargando: true, error: null }));
    peticion.subscribe({
      next: (datos) => estado.set({ datos, cargando: false, error: null, actualizado: new Date() }),
      error: (error) => {
        console.error('Error al cargar la analítica:', error);
        estado.update((e) => ({ ...e, cargando: false, error: 'No se pudieron cargar los datos del reporte.' }));
      }
    });
  }

  protected hora(fecha: Date | null): string {
    return fecha ? fecha.toLocaleTimeString('es-CO', { hour: 'numeric', minute: '2-digit' }) : '';
  }
}
