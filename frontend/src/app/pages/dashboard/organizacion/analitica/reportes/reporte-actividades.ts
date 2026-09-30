import { Component, computed, input } from '@angular/core';

import { ActividadAnalitica } from '../../../../../core/analitica/analitica.service';
import {
  ANCHO_BARRA, tooltip, GRIS_SUAVE, SERIE, base, ejeCategorias, ejeValores,
  estiloBarra, estiloSegmento, etiquetaValor, formatoNumero, leyenda
} from '../../../../../shared/echart/tema';
import { OpcionesGrafica } from '../../../../../shared/echart/echart';
import { GraficaCard, TablaGrafica } from '../componentes/grafica-card';
import { Kpi } from '../componentes/kpi';

const MESES = ['ene', 'feb', 'mar', 'abr', 'may', 'jun', 'jul', 'ago', 'sep', 'oct', 'nov', 'dic'];
const pct = (parte: number, total: number) => (total > 0 ? Math.round((parte / total) * 100) : 0);

/**
 * Reporte 1: actividades de la organización y la participación de las
 * personas mayores (inscripción, asistencia, ocupación de cupos).
 */
@Component({
  selector: 'app-reporte-actividades',
  imports: [Kpi, GraficaCard],
  templateUrl: './reporte-actividades.html',
  styleUrl: '../reporte.css'
})
export class ReporteActividades {

  readonly actividades = input.required<ActividadAnalitica[]>();

  // ---------- Indicadores ----------

  protected readonly kpis = computed(() => {
    const lista = this.actividades();
    const inscritos = lista.reduce((s, a) => s + a.inscritos, 0);
    const asistentes = lista.reduce((s, a) => s + a.asistentes, 0);
    const registros = lista.reduce((s, a) => s + a.conRegistro, 0);

    const conCupos = lista.filter((a) => (a.cupos ?? 0) > 0);
    const cupos = conCupos.reduce((s, a) => s + (a.cupos ?? 0), 0);
    const inscritosConCupos = conCupos.reduce((s, a) => s + Math.min(a.inscritos, a.cupos ?? 0), 0);

    return {
      actividades: lista.length,
      inscritos,
      promedio: lista.length ? inscritos / lista.length : 0,
      asistencia: registros > 0 ? `${pct(asistentes, registros)} %` : '—',
      detalleAsistencia: registros > 0
        ? `${asistentes} de ${registros} con asistencia tomada`
        : 'Aún no se ha tomado asistencia',
      ocupacion: cupos > 0 ? `${pct(inscritosConCupos, cupos)} %` : '—',
      detalleOcupacion: cupos > 0
        ? `${inscritosConCupos} de ${cupos} cupos ocupados`
        : 'Ninguna actividad tiene cupos definidos'
    };
  });

  // ---------- 1. Inscritos y asistentes por mes ----------

  private readonly porMes = computed(() => {
    const meses = new Map<string, { actividades: number; inscritos: number; asistentes: number }>();
    for (const a of this.actividades()) {
      const clave = a.fecha.slice(0, 7); // YYYY-MM
      const m = meses.get(clave) ?? { actividades: 0, inscritos: 0, asistentes: 0 };
      m.actividades++;
      m.inscritos += a.inscritos;
      m.asistentes += a.asistentes;
      meses.set(clave, m);
    }
    return [...meses.entries()]
      .sort(([a], [b]) => a.localeCompare(b))
      .map(([clave, v]) => {
        const [anio, mes] = clave.split('-');
        return { etiqueta: `${MESES[Number(mes) - 1]} ${anio}`, ...v };
      });
  });

  protected readonly graficaMeses = computed<OpcionesGrafica>(() => {
    const datos = this.porMes();
    return base({
      legend: leyenda(),
      tooltip: tooltip({ trigger: 'axis', axisPointer: { type: 'shadow' } }),
      xAxis: ejeCategorias(datos.map((d) => d.etiqueta)),
      yAxis: ejeValores(),
      series: [
        { name: 'Inscritos', type: 'bar', barMaxWidth: ANCHO_BARRA, barGap: '12%',
          itemStyle: estiloBarra(SERIE[0]), data: datos.map((d) => d.inscritos) },
        { name: 'Asistentes', type: 'bar', barMaxWidth: ANCHO_BARRA,
          itemStyle: estiloBarra(SERIE[1]), data: datos.map((d) => d.asistentes) }
      ]
    });
  });

  protected readonly tablaMeses = computed<TablaGrafica>(() => ({
    columnas: ['Mes', 'Actividades', 'Inscritos', 'Asistentes'],
    filas: this.porMes().map((d) => [d.etiqueta, d.actividades, d.inscritos, d.asistentes])
  }));

  // ---------- 2. Participación por tipo ----------

  private readonly porTipo = computed(() => {
    const tipos = new Map<string, { actividades: number; inscritos: number }>();
    for (const a of this.actividades()) {
      const tipo = a.tipo?.trim() || 'Sin tipo';
      const t = tipos.get(tipo) ?? { actividades: 0, inscritos: 0 };
      t.actividades++;
      t.inscritos += a.inscritos;
      tipos.set(tipo, t);
    }
    return [...tipos.entries()]
      .map(([tipo, v]) => ({ tipo, ...v }))
      .sort((a, b) => b.inscritos - a.inscritos || b.actividades - a.actividades);
  });

  protected readonly graficaTipos = computed<OpcionesGrafica>(() => {
    // En barras horizontales la primera categoría queda abajo: se invierte
    // para que la mayor quede arriba.
    const datos = [...this.porTipo()].reverse();
    return base({
      grid: { left: 8, right: 40, top: 8, bottom: 8, containLabel: true },
      tooltip: tooltip({ trigger: 'axis', axisPointer: { type: 'shadow' } }),
      xAxis: ejeValores(),
      yAxis: ejeCategorias(datos.map((d) => d.tipo)),
      series: [{
        name: 'Inscritos', type: 'bar', barMaxWidth: ANCHO_BARRA,
        itemStyle: estiloBarra(SERIE[0], true),
        label: etiquetaValor('right'),
        data: datos.map((d) => d.inscritos)
      }]
    });
  });

  protected readonly tablaTipos = computed<TablaGrafica>(() => ({
    columnas: ['Tipo de actividad', 'Actividades', 'Inscritos'],
    filas: this.porTipo().map((d) => [d.tipo, d.actividades, d.inscritos])
  }));

  // ---------- 3. Ocupación de cupos por actividad ----------

  private readonly ocupacion = computed(() =>
    this.actividades()
      .filter((a) => (a.cupos ?? 0) > 0)
      .map((a) => {
        const ocupados = Math.min(a.inscritos, a.cupos!);
        return { nombre: a.nombre, fecha: a.fecha, ocupados, libres: a.cupos! - ocupados, cupos: a.cupos!, porcentaje: pct(ocupados, a.cupos!) };
      })
      .sort((a, b) => b.porcentaje - a.porcentaje || b.ocupados - a.ocupados)
      .slice(0, 10)
  );

  protected readonly graficaOcupacion = computed<OpcionesGrafica>(() => {
    const datos = [...this.ocupacion()].reverse();
    return base({
      legend: leyenda(),
      grid: { left: 8, right: 48, top: 36, bottom: 8, containLabel: true },
      tooltip: tooltip({
        trigger: 'axis',
        axisPointer: { type: 'shadow' },
        formatter: (p: { dataIndex: number }[]) => {
          const d = datos[p[0].dataIndex];
          return `<strong>${d.nombre}</strong><br>${d.ocupados} de ${d.cupos} cupos (${d.porcentaje} %)`;
        }
      }),
      xAxis: ejeValores(),
      yAxis: ejeCategorias(datos.map((d) => d.nombre), {
        axisLabel: { color: '#586576', fontSize: 12, width: 140, overflow: 'truncate' }
      }),
      series: [
        { name: 'Inscritos', type: 'bar', stack: 'cupos', barMaxWidth: ANCHO_BARRA,
          itemStyle: estiloSegmento(SERIE[0], false), data: datos.map((d) => d.ocupados) },
        { name: 'Cupos libres', type: 'bar', stack: 'cupos', barMaxWidth: ANCHO_BARRA,
          itemStyle: estiloSegmento(GRIS_SUAVE, true),
          label: { show: true, position: 'right', color: '#12355b', fontSize: 12, fontWeight: 600,
            formatter: (p: { dataIndex: number }) => `${datos[p.dataIndex].porcentaje} %` },
          data: datos.map((d) => d.libres) }
      ]
    });
  });

  protected readonly tablaOcupacion = computed<TablaGrafica>(() => ({
    columnas: ['Actividad', 'Fecha', 'Inscritos', 'Cupos', 'Ocupación'],
    filas: this.ocupacion().map((d) => [d.nombre, d.fecha, d.ocupados, d.cupos, `${d.porcentaje} %`])
  }));

  protected readonly formatoNumero = formatoNumero;
}
