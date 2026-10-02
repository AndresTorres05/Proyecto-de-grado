import { OpcionesGrafica } from './echart';

/**
 * Tema visual de las gráficas (analítica).
 *
 * Los colores de las series son la paleta categórica validada con el
 * validador de daltonismo (los colores de marca de Vita+ no pasan: el navy y
 * el dorado quedan fuera de la banda de luminosidad y la lavanda se ve
 * gris). La marca se usa en textos, bordes y fondos.
 *
 * Reglas: colores de serie en orden fijo (nunca por ranking), un solo eje
 * Y por gráfica, marcas delgadas y cuadrícula tenue.
 */

/**
 * Colores de las series, en este orden. Los 3 primeros también sirven cuando
 * todas las series se ven a la vez (dona, varias líneas).
 */
export const SERIE = [
  '#2a78d6', // azul
  '#eb6834', // naranja
  '#1baf7a', // aqua
  '#eda100', // amarillo
  '#e87ba4', // magenta
  '#008300', // verde
  '#4a3aa7', // violeta
  '#e34948'  // rojo
];

/** Colores de estado: solo cuando el color significa bien o mal, y siempre con etiqueta. */
export const ESTADO = {
  bueno: '#0ca30c',
  aviso: '#fab219',
  critico: '#d03b3b'
};

/** Escala de azules para rangos ordenados, por ejemplo edades. */
export const RAMPA_AZUL = ['#86b6ef', '#5598e7', '#2a78d6', '#1c5cab', '#104281'];

// Grises para "resto", "sin dato" y pistas de fondo.
export const GRIS = '#c9d2dd';
export const GRIS_SUAVE = '#e3e9f1';

// Colores de texto, cuadrícula y ejes, iguales a los de la app.
const TINTA = '#12355b';
const TINTA_SECUNDARIA = '#586576';
const TINTA_TENUE = '#93a0b0';
const CUADRICULA = '#e3e9f1';
const EJE = '#c3cad4';
const FUENTE = "Inter, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Arial, sans-serif";

const numero = new Intl.NumberFormat('es-CO', { maximumFractionDigits: 1 });
/** Número en formato es-CO, con máximo un decimal. */
export const formatoNumero = (v: number) => numero.format(v);

/** Tooltip con el estilo común; `extra` define el disparador, formato, etc. */
export function tooltip(extra: Record<string, unknown> = {}) {
  return {
    confine: true,
    backgroundColor: '#ffffff',
    borderColor: CUADRICULA,
    borderWidth: 1,
    padding: [8, 12],
    textStyle: { color: TINTA, fontFamily: FUENTE, fontSize: 13 },
    extraCssText: 'box-shadow: 0 8px 24px rgba(18,53,91,.12); border-radius: 10px;',
    ...extra
  };
}

/** Opciones comunes a todas las gráficas. */
export function base(extra: OpcionesGrafica = {}): OpcionesGrafica {
  return {
    aria: { enabled: true },
    animationDuration: 400,
    textStyle: { fontFamily: FUENTE, color: TINTA_SECUNDARIA, fontSize: 12 },
    grid: { left: 8, right: 24, top: 36, bottom: 8, containLabel: true },
    tooltip: tooltip(),
    ...extra
  };
}

/** Leyenda arriba (siempre que haya 2 o más series). */
export function leyenda(): OpcionesGrafica {
  return {
    top: 0,
    left: 0,
    icon: 'roundRect',
    itemWidth: 12,
    itemHeight: 12,
    itemGap: 18,
    textStyle: { color: TINTA_SECUNDARIA, fontSize: 12, fontFamily: FUENTE }
  };
}

/** Eje de categorías con el estilo común. */
export function ejeCategorias(datos: string[], extra: Record<string, unknown> = {}) {
  return {
    type: 'category',
    data: datos,
    axisLine: { lineStyle: { color: EJE } },
    axisTick: { show: false },
    axisLabel: { color: TINTA_SECUNDARIA, fontSize: 12 },
    ...extra
  };
}

/** Eje de valores con cuadrícula tenue y números en formato es-CO. */
export function ejeValores(extra: Record<string, unknown> = {}) {
  return {
    type: 'value',
    splitLine: { lineStyle: { color: CUADRICULA, width: 1 } },
    axisLabel: { color: TINTA_TENUE, fontSize: 11, formatter: (v: number) => formatoNumero(v) },
    minInterval: 1,
    ...extra
  };
}

/** Barras: máx. 24 px, extremo redondeado 4 px y base recta. */
export function estiloBarra(color: string, horizontal = false) {
  return {
    color,
    borderRadius: horizontal ? [0, 4, 4, 0] : [4, 4, 0, 0]
  };
}

/** Ancho máximo de las barras, en píxeles. */
export const ANCHO_BARRA = 24;

/** Segmento de barra apilada: 2 px de separación del color del fondo. */
export function estiloSegmento(color: string, ultimo: boolean, horizontal = true) {
  return {
    color,
    borderColor: '#ffffff',
    borderWidth: 1,
    borderRadius: ultimo ? (horizontal ? [0, 4, 4, 0] : [4, 4, 0, 0]) : 0
  };
}

/** Línea de 2 px con marcadores de 8 px y anillo del color del fondo. */
export function estiloLinea(color: string, conArea = false) {
  return {
    type: 'line',
    smooth: false,
    symbol: 'circle',
    symbolSize: 8,
    lineStyle: { width: 2, color },
    itemStyle: { color, borderColor: '#ffffff', borderWidth: 2 },
    ...(conArea ? { areaStyle: { color, opacity: 0.1 } } : {})
  };
}

/** Etiqueta de valor en la punta de la barra (texto con tinta, no con el color). */
export function etiquetaValor(posicion: 'right' | 'top', formato?: (v: number) => string) {
  return {
    show: true,
    position: posicion,
    color: TINTA,
    fontSize: 12,
    fontWeight: 600,
    formatter: (p: { value: number }) => (formato ? formato(p.value) : formatoNumero(p.value))
  };
}
