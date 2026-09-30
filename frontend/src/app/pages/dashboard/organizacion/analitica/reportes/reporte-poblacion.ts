import { Component, computed, input } from '@angular/core';

import { PoblacionAnalitica } from '../../../../../core/analitica/analitica.service';
import {
  ANCHO_BARRA, tooltip, GRIS, RAMPA_AZUL, SERIE, base, ejeCategorias, ejeValores,
  estiloBarra, etiquetaValor, leyenda
} from '../../../../../shared/echart/tema';
import { OpcionesGrafica } from '../../../../../shared/echart/echart';
import { GraficaCard, TablaGrafica } from '../componentes/grafica-card';
import { Kpi } from '../componentes/kpi';

const RANGOS_EDAD = [
  { etiqueta: 'Menos de 60', min: 0, max: 59 },
  { etiqueta: '60 a 69', min: 60, max: 69 },
  { etiqueta: '70 a 79', min: 70, max: 79 },
  { etiqueta: '80 a 89', min: 80, max: 89 },
  { etiqueta: '90 o más', min: 90, max: 200 }
];

// Color fijo por categoría de interés (sigue a la categoría, no al ranking)
const CATEGORIAS: { valor: string; nombre: string; color: string }[] = [
  { valor: 'GUSTO', nombre: 'Gustos', color: SERIE[0] },
  { valor: 'TALENTO', nombre: 'Talentos', color: SERIE[1] },
  { valor: 'HOBBY', nombre: 'Hobbies', color: SERIE[2] }
];

// Color fijo por género
const GENEROS: { valor: string; color: string }[] = [
  { valor: 'Femenino', color: SERIE[0] },
  { valor: 'Masculino', color: SERIE[1] },
  { valor: 'Otro', color: SERIE[2] },
  { valor: 'Sin dato', color: GRIS }
];

const pct = (parte: number, total: number) => (total > 0 ? Math.round((parte / total) * 100) : 0);

/**
 * Reporte 3: quiénes son las personas mayores de la organización
 * (edad, género, EPS e intereses). No depende del período.
 */
@Component({
  selector: 'app-reporte-poblacion',
  imports: [Kpi, GraficaCard],
  templateUrl: './reporte-poblacion.html',
  styleUrl: '../reporte.css'
})
export class ReportePoblacion {

  readonly datos = input.required<PoblacionAnalitica>();

  private readonly edades = computed(() => {
    const hoy = new Date();
    return this.datos().personas
      .filter((p) => !!p.fechaNacimiento)
      .map((p) => {
        const [anio, mes, dia] = p.fechaNacimiento!.split('-').map(Number);
        let edad = hoy.getFullYear() - anio;
        if (hoy.getMonth() + 1 < mes || (hoy.getMonth() + 1 === mes && hoy.getDate() < dia)) edad--;
        return edad;
      });
  });

  // ---------- Indicadores ----------

  protected readonly kpis = computed(() => {
    const total = this.datos().personas.length;
    const edades = this.edades();
    const promedio = edades.length ? Math.round(edades.reduce((s, e) => s + e, 0) / edades.length) : null;
    const conEps = this.datos().personas.filter((p) => !!p.eps).length;

    return {
      total,
      edadPromedio: promedio !== null ? `${promedio} años` : '—',
      detalleEdad: edades.length < total
        ? `${total - edades.length} sin fecha de nacimiento`
        : 'De todas las personas asociadas',
      conEps: `${pct(conEps, total)} %`,
      detalleEps: `${conEps} de ${total} tienen EPS registrada`,
      conIntereses: `${pct(this.datos().personasConIntereses, total)} %`,
      detalleIntereses: `${this.datos().personasConIntereses} de ${total} registraron sus intereses`
    };
  });

  // ---------- 1. Rangos de edad ----------

  private readonly porEdad = computed(() =>
    RANGOS_EDAD.map((r) => ({
      etiqueta: r.etiqueta,
      personas: this.edades().filter((e) => e >= r.min && e <= r.max).length
    }))
  );

  protected readonly graficaEdad = computed<OpcionesGrafica>(() => {
    const datos = this.porEdad();
    return base({
      grid: { left: 8, right: 16, top: 28, bottom: 8, containLabel: true },
      tooltip: tooltip({ trigger: 'axis', axisPointer: { type: 'shadow' } }),
      xAxis: ejeCategorias(datos.map((d) => d.etiqueta)),
      yAxis: ejeValores(),
      series: [{
        name: 'Personas', type: 'bar', barMaxWidth: 40,
        label: etiquetaValor('top'),
        // Rangos ordenados: rampa azul de claro (jóvenes) a oscuro (mayores)
        data: datos.map((d, i) => ({ value: d.personas, itemStyle: estiloBarra(RAMPA_AZUL[i]) }))
      }]
    });
  });

  protected readonly tablaEdad = computed<TablaGrafica>(() => ({
    columnas: ['Rango de edad', 'Personas'],
    filas: this.porEdad().map((d) => [d.etiqueta, d.personas])
  }));

  // ---------- 2. Género ----------

  private readonly porGenero = computed(() => {
    const total = this.datos().personas.length;
    return GENEROS
      .map((g) => {
        const personas = this.datos().personas.filter((p) =>
          g.valor === 'Sin dato' ? !p.genero : p.genero === g.valor).length;
        return { ...g, personas, porcentaje: pct(personas, total) };
      })
      .filter((g) => g.personas > 0);
  });

  protected readonly graficaGenero = computed<OpcionesGrafica>(() => {
    const datos = this.porGenero();
    const total = this.datos().personas.length;
    return base({
      legend: { ...leyenda(), top: 'auto', bottom: 0, left: 'center' },
      tooltip: tooltip({ trigger: 'item', formatter: '{b}: {c} ({d} %)' }),
      series: [{
        name: 'Género', type: 'pie', radius: ['52%', '74%'], center: ['50%', '45%'],
        avoidLabelOverlap: true,
        itemStyle: { borderColor: '#ffffff', borderWidth: 2 },
        label: { show: true, color: '#12355b', fontSize: 12, fontWeight: 600, formatter: '{d} %' },
        data: datos.map((d) => ({ name: d.valor, value: d.personas, itemStyle: { color: d.color } }))
      }],
      // Total en el centro de la dona
      title: {
        text: String(total), subtext: 'personas', left: 'center', top: '36%',
        textStyle: { color: '#12355b', fontSize: 24, fontWeight: 700 },
        subtextStyle: { color: '#586576', fontSize: 12 }
      }
    });
  });

  protected readonly tablaGenero = computed<TablaGrafica>(() => ({
    columnas: ['Género', 'Personas', 'Porcentaje'],
    filas: this.porGenero().map((g) => [g.valor, g.personas, `${g.porcentaje} %`])
  }));

  // ---------- 3. EPS ----------

  private readonly porEps = computed(() => {
    const conteo = new Map<string, number>();
    for (const p of this.datos().personas) {
      const eps = p.eps?.trim() || 'Sin EPS registrada';
      conteo.set(eps, (conteo.get(eps) ?? 0) + 1);
    }
    const lista = [...conteo.entries()]
      .map(([eps, personas]) => ({ eps, personas }))
      .sort((a, b) => b.personas - a.personas);

    // Más de 8: el resto se agrupa en "Otras"
    if (lista.length <= 8) return lista;
    const otras = lista.slice(7).reduce((s, e) => s + e.personas, 0);
    return [...lista.slice(0, 7), { eps: 'Otras', personas: otras }];
  });

  protected readonly graficaEps = computed<OpcionesGrafica>(() => {
    const datos = [...this.porEps()].reverse();
    return base({
      grid: { left: 8, right: 40, top: 8, bottom: 8, containLabel: true },
      tooltip: tooltip({ trigger: 'axis', axisPointer: { type: 'shadow' } }),
      xAxis: ejeValores(),
      yAxis: ejeCategorias(datos.map((d) => d.eps), {
        axisLabel: { color: '#586576', fontSize: 12, width: 150, overflow: 'truncate' }
      }),
      series: [{
        name: 'Personas', type: 'bar', barMaxWidth: ANCHO_BARRA,
        label: etiquetaValor('right'),
        data: datos.map((d) => ({
          value: d.personas,
          itemStyle: estiloBarra(d.eps === 'Sin EPS registrada' || d.eps === 'Otras' ? GRIS : SERIE[0], true)
        }))
      }]
    });
  });

  protected readonly tablaEps = computed<TablaGrafica>(() => ({
    columnas: ['EPS', 'Personas'],
    filas: this.porEps().map((d) => [d.eps, d.personas])
  }));

  // ---------- 4. Intereses más comunes ----------

  private readonly topIntereses = computed(() => this.datos().intereses.slice(0, 10));

  protected readonly graficaIntereses = computed<OpcionesGrafica>(() => {
    const datos = [...this.topIntereses()].reverse();
    const presentes = CATEGORIAS.filter((c) => datos.some((d) => d.categoria === c.valor));
    return base({
      legend: leyenda(),
      grid: { left: 8, right: 40, top: 36, bottom: 8, containLabel: true },
      tooltip: tooltip({
        trigger: 'item',
        formatter: (p: { dataIndex: number }) => {
          const d = datos[p.dataIndex];
          const categoria = CATEGORIAS.find((c) => c.valor === d.categoria)?.nombre ?? d.categoria;
          return `<strong>${d.nombre}</strong><br>${categoria} · ${d.personas} personas`;
        }
      }),
      xAxis: ejeValores(),
      yAxis: ejeCategorias(datos.map((d) => d.nombre)),
      // Una serie por categoría, apiladas: cada barra solo tiene valor en
      // su categoría, así la leyenda explica los colores.
      series: presentes.map((c) => ({
        name: c.nombre, type: 'bar', stack: 'intereses', barMaxWidth: ANCHO_BARRA,
        itemStyle: estiloBarra(c.color, true),
        label: { ...etiquetaValor('right'), formatter: (p: { value: number | null }) => (p.value ? String(p.value) : '') },
        data: datos.map((d) => (d.categoria === c.valor ? d.personas : null))
      }))
    });
  });

  protected readonly tablaIntereses = computed<TablaGrafica>(() => ({
    columnas: ['Interés', 'Categoría', 'Personas'],
    filas: this.topIntereses().map((d) => [
      d.nombre,
      CATEGORIAS.find((c) => c.valor === d.categoria)?.nombre ?? d.categoria,
      d.personas
    ])
  }));
}
