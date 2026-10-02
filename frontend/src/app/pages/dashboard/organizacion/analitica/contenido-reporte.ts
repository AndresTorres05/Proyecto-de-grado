import { OpcionesGrafica } from '../../../../shared/echart/echart';
import { TablaGrafica } from './componentes/grafica-card';
import { DatoKpi } from './componentes/kpi';

/** Una gráfica del reporte (o solo una tabla, si opciones es null). */
export interface SeccionReporte {
  titulo: string;
  descripcion: string;
  opciones: OpcionesGrafica | null;
  tabla: TablaGrafica;
}

/** Lo que un reporte entrega para exportarlo a PDF. */
export interface ContenidoReporte {
  indicadores: DatoKpi[];
  secciones: SeccionReporte[];
  nota?: string;
}

/** Cada reporte implementa esto para el botón "Descargar PDF". */
export interface ReporteExportable {
  contenidoPdf(): ContenidoReporte;
}
