import { Component, computed, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { MedicionAnalitica, SaludAnalitica } from '../../../../../core/analitica/analitica.service';
import {
  CampoSigno, Indicador, LIMITES, NOMBRE_INDICADOR, RANGOS, camposImposibles, evaluarIndicador
} from '../../../../../core/signos-vitales/rangos';
import {
  ANCHO_BARRA, ESTADO, GRIS_SUAVE, SERIE, base, ejeCategorias, ejeValores,
  estiloLinea, estiloSegmento, leyenda, tooltip
} from '../../../../../shared/echart/tema';
import { ClicGrafica, OpcionesGrafica } from '../../../../../shared/echart/echart';
import { Icon } from '../../../../../shared/icon/icon';
import { GraficaCard, TablaGrafica } from '../componentes/grafica-card';
import { Kpi } from '../componentes/kpi';

const DIA = 24 * 60 * 60 * 1000;
const DIAS_SIN_MEDICION = 30;

// Indicadores que muestra la analítica (temperatura no).
type IndicadorAnalitica = Exclude<Indicador, 'temperatura'>;
const INDICADORES: IndicadorAnalitica[] = ['presion', 'pulso', 'oxigeno'];

// Campos de la medición que forman cada indicador
const CAMPOS: Record<IndicadorAnalitica, CampoSigno[]> = {
  presion: ['presionSistolica', 'presionDiastolica'],
  pulso: ['frecuenciaCardiaca'],
  oxigeno: ['saturacionOxigeno']
};

// Color para "posible error de registro": es un problema del dato, no de
// salud, así que va en gris oscuro (no en los colores de estado).
const COLOR_ERROR = '#6b7785';

interface PersonaAtencion {
  idUsuario: number;
  nombre: string;
  motivos: string[];
  indicadores: IndicadorAnalitica[];  // fuera de rango en la última medición
  errores: number;                    // mediciones con valores imposibles en el período
  ultima: string | null;
}

/** Indicadores de la medición con algún valor imposible. */
function indicadoresConError(m: MedicionAnalitica): IndicadorAnalitica[] {
  const imposibles = camposImposibles(m);
  return INDICADORES.filter((i) => CAMPOS[i].some((c) => imposibles.includes(c)));
}

/**
 * Reporte 2: salud de las personas mayores asociadas a partir de sus
 * signos vitales (estado actual, seguimiento y evolución).
 */
@Component({
  selector: 'app-reporte-salud',
  imports: [FormsModule, Icon, Kpi, GraficaCard],
  templateUrl: './reporte-salud.html',
  styleUrls: ['../reporte.css', './reporte-salud.css']
})
export class ReporteSalud {

  readonly datos = input.required<SaludAnalitica>();
  /** Inicio del período (YYYY-MM-DD) o null = todo. */
  readonly desde = input<string | null>(null);

  protected readonly indicadores = INDICADORES;
  protected readonly nombreIndicador = NOMBRE_INDICADOR;

  // Filtros interactivos
  protected readonly filtroIndicador = signal<IndicadorAnalitica | null>(null);
  protected readonly personaElegida = signal<number | null>(null);
  protected readonly indicadorEvolucion = signal<IndicadorAnalitica>('presion');

  // ---------- Datos base ----------

  private readonly medicionesPorPersona = computed(() => {
    const mapa = new Map<number, MedicionAnalitica[]>();
    for (const m of this.datos().mediciones) {
      const lista = mapa.get(m.idPersonaMayor) ?? [];
      lista.push(m);
      mapa.set(m.idPersonaMayor, lista);
    }
    return mapa;
  });

  private readonly ultimaPorPersona = computed(() => {
    const mapa = new Map<number, MedicionAnalitica>();
    for (const [id, lista] of this.medicionesPorPersona()) {
      mapa.set(id, lista[lista.length - 1]); // vienen ordenadas por fecha
    }
    return mapa;
  });

  private readonly medicionesPeriodo = computed(() => {
    const desde = this.desde();
    return desde
      ? this.datos().mediciones.filter((m) => m.fechaHora >= desde)
      : this.datos().mediciones;
  });

  /** Mediciones del período con algún valor imposible. */
  private readonly medicionesConError = computed(() =>
    this.medicionesPeriodo().filter((m) => camposImposibles(m).length > 0));

  // ---------- Personas que requieren atención ----------

  protected readonly atencion = computed<PersonaAtencion[]>(() => {
    const ahora = Date.now();
    const lista: PersonaAtencion[] = [];

    const erroresPorPersona = new Map<number, number>();
    for (const m of this.medicionesConError()) {
      erroresPorPersona.set(m.idPersonaMayor, (erroresPorPersona.get(m.idPersonaMayor) ?? 0) + 1);
    }

    for (const persona of this.datos().personas) {
      const ultima = this.ultimaPorPersona().get(persona.idUsuario);
      const errores = erroresPorPersona.get(persona.idUsuario) ?? 0;
      const motivos: string[] = [];
      let indicadores: IndicadorAnalitica[] = [];

      if (!ultima) {
        motivos.push('Nunca se le han medido signos vitales');
      } else {
        indicadores = INDICADORES.filter((i) => evaluarIndicador(ultima, i) === false);
        const conError = indicadoresConError(ultima);
        for (const i of indicadores) {
          const valor = this.valor(ultima, i);
          motivos.push(conError.includes(i)
            ? `${NOMBRE_INDICADOR[i]}: ${valor} (valor imposible)`
            : `${NOMBRE_INDICADOR[i]}: ${valor}`);
        }
        const dias = Math.floor((ahora - new Date(ultima.fechaHora).getTime()) / DIA);
        if (dias > DIAS_SIN_MEDICION) {
          motivos.push(`Sin medición hace ${dias} días`);
        }
      }

      if (errores > 0) {
        motivos.push(errores === 1
          ? '1 medición con valores imposibles (posible error de registro)'
          : `${errores} mediciones con valores imposibles (posible error de registro)`);
      }

      if (motivos.length > 0) {
        lista.push({
          idUsuario: persona.idUsuario,
          nombre: persona.nombre,
          motivos,
          indicadores,
          errores,
          ultima: ultima?.fechaHora ?? null
        });
      }
    }

    // Primero valores fuera de rango, luego errores de registro
    return lista.sort((a, b) =>
      b.indicadores.length - a.indicadores.length || b.errores - a.errores || a.nombre.localeCompare(b.nombre));
  });

  protected readonly atencionFiltrada = computed(() => {
    const filtro = this.filtroIndicador();
    return filtro ? this.atencion().filter((p) => p.indicadores.includes(filtro)) : this.atencion();
  });

  // ---------- Indicadores ----------

  protected readonly kpis = computed(() => {
    const total = this.datos().personas.length;
    const conSeguimiento = new Set(this.medicionesPeriodo().map((m) => m.idPersonaMayor)).size;
    const fueraDeRango = this.atencion().filter((p) => p.indicadores.length > 0).length;
    const sinReciente = this.atencion().filter((p) =>
      p.motivos.some((m) => m.startsWith('Sin medición') || m.startsWith('Nunca'))).length;

    return {
      total,
      conSeguimiento,
      mediciones: this.medicionesPeriodo().length,
      fueraDeRango,
      sinReciente,
      errores: this.medicionesConError().length,
      ejemploError: this.ejemploError()
    };
  });

  /** La medición imposible más reciente, como ejemplo real para el indicador. */
  private readonly ejemploError = computed(() => {
    const conError = this.medicionesConError();
    if (conError.length === 0) {
      return 'Ningún valor imposible en el período';
    }

    const m = conError[conError.length - 1]; // vienen ordenadas por fecha
    const campo = camposImposibles(m)[0];
    const { nombre, unidad } = LIMITES[campo];
    const valor = m[campo as keyof MedicionAnalitica];
    const persona = this.datos().personas.find((p) => p.idUsuario === m.idPersonaMayor)?.nombre ?? '';

    return `Más reciente: ${nombre.toLowerCase()} ${valor} ${unidad} (${persona}, ${this.fecha(m.fechaHora)})`;
  });

  // ---------- 1. Estado actual por indicador ----------

  private readonly estadoActual = computed(() =>
    INDICADORES.map((indicador) => {
      let normal = 0;
      let revisar = 0;
      let error = 0;
      for (const m of this.ultimaPorPersona().values()) {
        const evaluacion = evaluarIndicador(m, indicador);
        if (evaluacion === null) continue;
        if (indicadoresConError(m).includes(indicador)) error++;
        else if (evaluacion) normal++;
        else revisar++;
      }
      return { indicador, normal, revisar, error };
    })
  );

  protected readonly graficaEstado = computed<OpcionesGrafica>(() => {
    const datos = [...this.estadoActual()].reverse();
    const hayErrores = datos.some((d) => d.error > 0);
    const series = [
      { nombre: 'Normal', color: ESTADO.bueno, valores: datos.map((d) => d.normal) },
      { nombre: 'Revisar', color: ESTADO.aviso, valores: datos.map((d) => d.revisar) },
      ...(hayErrores
        ? [{ nombre: 'Posible error de registro', color: COLOR_ERROR, valores: datos.map((d) => d.error) }]
        : [])
    ];

    return base({
      legend: { ...leyenda(), data: series.map((s) => s.nombre) },
      grid: { left: 8, right: 24, top: 36, bottom: 8, containLabel: true },
      tooltip: tooltip({ trigger: 'axis', axisPointer: { type: 'shadow' } }),
      xAxis: ejeValores(),
      yAxis: ejeCategorias(datos.map((d) => NOMBRE_INDICADOR[d.indicador])),
      series: series.map((s, i) => ({
        name: s.nombre, type: 'bar', stack: 'estado', barMaxWidth: ANCHO_BARRA,
        cursor: s.nombre === 'Normal' ? 'default' : 'pointer',
        itemStyle: estiloSegmento(s.color, i === series.length - 1),
        data: s.valores
      }))
    });
  });

  protected readonly tablaEstado = computed<TablaGrafica>(() => ({
    columnas: ['Indicador', 'Normal', 'Revisar', 'Posible error de registro'],
    filas: this.estadoActual().map((d) => [NOMBRE_INDICADOR[d.indicador], d.normal, d.revisar, d.error])
  }));

  /** Clic en una barra: filtra la tabla de atención por ese indicador. */
  protected filtrarPorEstado(evento: ClicGrafica): void {
    if (evento.serie === 'Normal') return;
    const indicador = INDICADORES.find((i) => NOMBRE_INDICADOR[i] === evento.nombre) ?? null;
    this.filtroIndicador.set(this.filtroIndicador() === indicador ? null : indicador);
  }

  // ---------- 2. Mediciones en el tiempo ----------

  private readonly medicionesEnElTiempo = computed(() => {
    const mediciones = this.medicionesPeriodo();
    const desde = this.desde();
    if (mediciones.length === 0 && !desde) {
      return { porMes: false, puntos: [] as { etiqueta: string; total: number }[] };
    }

    const inicio = desde ? new Date(`${desde}T00:00`) : new Date(mediciones[0].fechaHora);
    const semanas = (Date.now() - inicio.getTime()) / (7 * DIA);
    const porMes = semanas > 26; // períodos largos: por mes

    const clave = (fecha: Date) => {
      if (porMes) return `${fecha.getFullYear()}-${String(fecha.getMonth() + 1).padStart(2, '0')}`;
      const lunes = new Date(fecha);
      lunes.setHours(0, 0, 0, 0);
      lunes.setDate(lunes.getDate() - ((lunes.getDay() + 6) % 7));
      return lunes.toLocaleDateString('en-CA');
    };

    // Todos los períodos (también los que no tienen mediciones)
    const conteo = new Map<string, number>();
    const cursor = new Date(inicio);
    while (cursor.getTime() <= Date.now()) {
      conteo.set(clave(cursor), 0);
      if (porMes) cursor.setMonth(cursor.getMonth() + 1, 1);
      else cursor.setDate(cursor.getDate() + 7);
    }
    conteo.set(clave(new Date()), conteo.get(clave(new Date())) ?? 0);

    for (const m of mediciones) {
      const k = clave(new Date(m.fechaHora));
      conteo.set(k, (conteo.get(k) ?? 0) + 1);
    }

    const formato = (k: string) => {
      const [anio, mes, dia] = k.split('-').map(Number);
      return porMes
        ? new Date(anio, mes - 1, 1).toLocaleDateString('es-CO', { month: 'short', year: 'numeric' })
        : new Date(anio, mes - 1, dia).toLocaleDateString('es-CO', { day: 'numeric', month: 'short' });
    };

    return {
      porMes,
      puntos: [...conteo.entries()]
        .sort(([a], [b]) => a.localeCompare(b))
        .map(([k, total]) => ({ etiqueta: formato(k), total }))
    };
  });

  protected readonly graficaTiempo = computed<OpcionesGrafica>(() => {
    const { puntos } = this.medicionesEnElTiempo();
    return base({
      grid: { left: 8, right: 24, top: 16, bottom: 8, containLabel: true },
      tooltip: tooltip({ trigger: 'axis' }),
      xAxis: ejeCategorias(puntos.map((p) => p.etiqueta), {
        boundaryGap: false,
        // Que la primera y la última fecha no se corten en los bordes
        axisLabel: { color: '#586576', fontSize: 12, hideOverlap: true, alignMinLabel: 'left', alignMaxLabel: 'right' }
      }),
      yAxis: ejeValores(),
      series: [{ name: 'Mediciones', ...estiloLinea(SERIE[0], true), data: puntos.map((p) => p.total) }]
    });
  });

  protected readonly tablaTiempo = computed<TablaGrafica>(() => ({
    columnas: [this.medicionesEnElTiempo().porMes ? 'Mes' : 'Semana del', 'Mediciones'],
    filas: this.medicionesEnElTiempo().puntos.map((p) => [p.etiqueta, p.total])
  }));

  protected readonly tituloTiempo = computed(() =>
    this.medicionesEnElTiempo().porMes ? 'Mediciones por mes' : 'Mediciones por semana');

  // ---------- 3. Evolución de una persona ----------

  /** Personas con al menos una medición (para el selector). */
  protected readonly personasConMediciones = computed(() =>
    this.datos().personas.filter((p) => this.medicionesPorPersona().has(p.idUsuario)));

  protected readonly personaActual = computed(() => {
    const elegida = this.personaElegida();
    const disponibles = this.personasConMediciones();
    if (elegida !== null && disponibles.some((p) => p.idUsuario === elegida)) {
      return elegida;
    }
    // Por defecto: la primera que requiere atención por valores, o la primera con datos
    const conAlerta = this.atencion().find((p) =>
      (p.indicadores.length > 0 || p.errores > 0) && this.medicionesPorPersona().has(p.idUsuario));
    return conAlerta?.idUsuario ?? disponibles[0]?.idUsuario ?? null;
  });

  private readonly serieEvolucion = computed(() => {
    const id = this.personaActual();
    const desde = this.desde();
    return (id !== null ? this.medicionesPorPersona().get(id) ?? [] : [])
      .filter((m) => !desde || m.fechaHora >= desde);
  });

  /** Valor de un campo para la gráfica: los imposibles no se dibujan. */
  private valorGraficable(m: MedicionAnalitica, campo: CampoSigno): number | null {
    const valor = m[campo as keyof MedicionAnalitica] as number | null;
    if (valor === null) return null;
    const { min, max } = LIMITES[campo];
    return valor < min || valor > max ? null : valor;
  }

  /** Cuántas mediciones del indicador elegido se omiten por imposibles. */
  protected readonly omitidas = computed(() => {
    const campos = CAMPOS[this.indicadorEvolucion()];
    return this.serieEvolucion().filter((m) => {
      const imposibles = camposImposibles(m);
      return campos.some((c) => imposibles.includes(c));
    }).length;
  });

  protected readonly descripcionEvolucion = computed(() => {
    const texto = 'Mediciones de la persona dentro del período. La franja gris es el rango normal de referencia.';
    const n = this.omitidas();
    if (n === 0) return texto;
    return `${texto} ${n === 1 ? '1 medición con un valor imposible no se grafica' : `${n} mediciones con valores imposibles no se grafican`} (ver tabla, marcadas con ⚠).`;
  });

  protected readonly graficaEvolucion = computed<OpcionesGrafica>(() => {
    const indicador = this.indicadorEvolucion();
    const lista = this.serieEvolucion();
    const punto = (m: MedicionAnalitica, campo: CampoSigno) =>
      [m.fechaHora.replace('T', ' '), this.valorGraficable(m, campo)] as [string, number | null];

    // Series a dibujar según el indicador
    const definiciones: { nombre: string; campo: CampoSigno; color: string; rango: { min: number; max: number }; franja: string }[] =
      indicador === 'presion'
        ? [
            { nombre: 'Sistólica (mmHg)', campo: 'presionSistolica', color: SERIE[0], rango: RANGOS.sistolica, franja: 'Normal sistólica' },
            { nombre: 'Diastólica (mmHg)', campo: 'presionDiastolica', color: SERIE[1], rango: RANGOS.diastolica, franja: 'Normal diastólica' }
          ]
        : indicador === 'pulso'
          ? [{ nombre: 'Pulso (lpm)', campo: 'frecuenciaCardiaca', color: SERIE[0], rango: RANGOS.pulso, franja: 'Rango normal' }]
          : [{ nombre: 'Oxígeno (%)', campo: 'saturacionOxigeno', color: SERIE[0], rango: RANGOS.oxigeno, franja: 'Rango normal' }];

    const series = definiciones.map((d) => ({
      ...d,
      puntos: lista.map((m) => punto(m, d.campo)).filter((p) => p[1] !== null)
    }));
    const valores = series.flatMap((s) => s.puntos.map((p) => p[1] as number));
    const sinDatos = valores.length === 0;

    // Eje Y: siempre incluye el rango normal (para que se vea la franja)
    const bajo = Math.min(...valores, ...definiciones.map((d) => d.rango.min));
    const alto = Math.max(...valores, ...definiciones.map((d) => d.rango.max));
    const margen = Math.max((alto - bajo) * 0.15, 2);
    const minimoY = Math.max(0, Math.floor(bajo - margen));
    const maximoY = indicador === 'oxigeno' ? 100 : Math.ceil(alto + margen);

    // Eje X: todo el período (o los últimos 30 días si es "Todo" y no hay datos)
    const ahora = new Date();
    const desde = this.desde();
    const inicio = desde
      ? new Date(`${desde}T00:00`)
      : lista.length > 0
        ? new Date(lista[0].fechaHora)
        : new Date(ahora.getTime() - 30 * DIA);

    const unidad = RANGOS[indicador === 'presion' ? 'sistolica' : indicador === 'pulso' ? 'pulso' : 'oxigeno'].unidad;

    return base({
      legend: definiciones.length > 1 ? leyenda() : undefined,
      grid: { left: 8, right: 24, top: definiciones.length > 1 ? 40 : 20, bottom: 8, containLabel: true },
      tooltip: tooltip({ trigger: 'axis', valueFormatter: (v: number | null) => (v === null ? '—' : `${v} ${unidad}`) }),
      xAxis: {
        type: 'time',
        min: inicio.getTime(),
        max: ahora.getTime(),
        axisLine: { lineStyle: { color: '#c3cad4' } },
        axisLabel: { color: '#586576', fontSize: 11, hideOverlap: true },
        splitLine: { show: false }
      },
      yAxis: ejeValores({ min: minimoY, max: maximoY, minInterval: 0 }),
      series: series.map((s) => ({
        name: s.nombre,
        ...estiloLinea(s.color),
        connectNulls: true,
        data: s.puntos,
        markArea: {
          silent: true,
          itemStyle: { color: GRIS_SUAVE, opacity: 0.6 },
          label: { show: true, position: 'insideTopLeft', color: '#586576', fontSize: 11 },
          data: [[{ name: s.franja, yAxis: s.rango.min }, { yAxis: s.rango.max }]]
        }
      })),
      // Sin datos: el marco completo con un aviso en el centro
      graphic: sinDatos
        ? [{
            type: 'text',
            left: 'center',
            top: 'middle',
            silent: true,
            style: {
              text: `Sin mediciones de ${NOMBRE_INDICADOR[indicador].toLowerCase()} en este período`,
              fill: '#586576',
              fontSize: 14,
              fontWeight: 600,
              backgroundColor: 'rgba(255,255,255,0.85)',
              padding: [8, 14],
              borderRadius: 8
            }
          }]
        : []
    });
  });

  protected readonly tablaEvolucion = computed<TablaGrafica>(() => ({
    columnas: ['Fecha', 'Presión (mmHg)', 'Pulso (lpm)', 'Oxígeno (%)'],
    filas: this.serieEvolucion().map((m) => {
      const imposibles = camposImposibles(m);
      const marca = (campo: CampoSigno, texto: string) => (imposibles.includes(campo) ? `⚠ ${texto}` : texto);
      const presionErronea = imposibles.includes('presionSistolica') || imposibles.includes('presionDiastolica');
      return [
        m.fechaHora.replace('T', ' '),
        m.presionSistolica !== null || m.presionDiastolica !== null
          ? `${presionErronea ? '⚠ ' : ''}${m.presionSistolica ?? '-'}/${m.presionDiastolica ?? '-'}` : '—',
        m.frecuenciaCardiaca !== null ? marca('frecuenciaCardiaca', String(m.frecuenciaCardiaca)) : '—',
        m.saturacionOxigeno !== null ? marca('saturacionOxigeno', String(m.saturacionOxigeno)) : '—'
      ];
    })
  }));

  protected verEvolucion(idUsuario: number): void {
    this.personaElegida.set(idUsuario);
    document.getElementById('evolucion-persona')?.scrollIntoView({ behavior: 'smooth', block: 'center' });
  }

  // ---------- Utilidades ----------

  protected fecha(fechaHora: string | null): string {
    if (!fechaHora) return '—';
    return new Date(fechaHora).toLocaleDateString('es-CO', { day: 'numeric', month: 'short', year: 'numeric' });
  }

  private valor(m: MedicionAnalitica, i: IndicadorAnalitica): string {
    switch (i) {
      case 'presion': return `${m.presionSistolica ?? '-'}/${m.presionDiastolica ?? '-'} mmHg`;
      case 'pulso': return `${m.frecuenciaCardiaca} lpm`;
      case 'oxigeno': return `${m.saturacionOxigeno} %`;
    }
  }
}
