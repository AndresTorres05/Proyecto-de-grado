import { Component, Input, OnChanges, inject } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

/**
 * Set de iconos de línea (estilo consistente, trazo de 1.75) usados en toda la
 * aplicación en reemplazo de los emojis. Cada entrada es el contenido interno de
 * un <svg viewBox="0 0 24 24">. El color se hereda con `currentColor` y el
 * tamaño con `1em`, de modo que basta con ajustar `font-size` / `color` en el
 * contenedor.
 */
const ICONS: Record<string, string> = {
  home: '<path d="M3 10.75 12 4l9 6.75"/><path d="M5.5 9.5V20h13V9.5"/><path d="M10 20v-5h4v5"/>',
  activity: '<path d="M3 12h4l3 8 4-16 3 8h4"/>',
  heart:
    '<path d="M12 20s-7-4.35-9.5-8.5A5 5 0 0 1 12 6a5 5 0 0 1 9.5 5.5C19 15.65 12 20 12 20Z"/>',
  clock: '<circle cx="12" cy="12" r="8.5"/><path d="M12 7.5V12l3 2"/>',
  user: '<circle cx="12" cy="8" r="4"/><path d="M4.5 20a7.5 7.5 0 0 1 15 0"/>',
  users:
    '<circle cx="8.5" cy="8.5" r="3"/><path d="M2.5 19.5a6 6 0 0 1 12 0"/><path d="M15.5 6a3 3 0 0 1 0 6"/><path d="M16.5 14c2.4.7 4 2.6 4 5.5"/>',
  phone:
    '<path d="M6.5 3h3l1.5 5-2 1.5a12 12 0 0 0 5.5 5.5l1.5-2 5 1.5v3a2 2 0 0 1-2 2A16 16 0 0 1 4.5 5a2 2 0 0 1 2-2Z"/>',
  star: '<path d="m12 3.5 2.6 5.27 5.82.85-4.21 4.1.99 5.8L12 16.9l-5.2 2.72.99-5.8-4.21-4.1 5.82-.85Z"/>',
  pill: '<path d="M10.5 3.5 3.5 10.5a4.95 4.95 0 0 0 7 7l7-7a4.95 4.95 0 0 0-7-7Z"/><path d="m7 7 7 7"/>',
  gift:
    '<path d="M20 12v8a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1v-8"/><rect x="2.5" y="7.5" width="19" height="4.5" rx="1"/><path d="M12 7.5V21"/><path d="M12 7.5S10.5 3 8 3a2.25 2.25 0 0 0 0 4.5Z"/><path d="M12 7.5S13.5 3 16 3a2.25 2.25 0 0 1 0 4.5Z"/>',
  bell: '<path d="M6 9a6 6 0 0 1 12 0c0 4.5 1.5 6 2 6.5H4c.5-.5 2-2 2-6.5Z"/><path d="M10 19a2 2 0 0 0 4 0"/>',
  'bar-chart':
    '<path d="M4 20V4"/><path d="M4 20h17"/><rect x="6.5" y="11" width="3" height="6" rx="0.5"/><rect x="11.5" y="7" width="3" height="10" rx="0.5"/><rect x="16.5" y="13.5" width="3" height="3.5" rx="0.5"/>',
  map: '<path d="m9 4-5.5 2v14L9 18l6 2 5.5-2V4L15 6 9 4Z"/><path d="M9 4v14"/><path d="M15 6v14"/>',
  settings:
    '<circle cx="12" cy="12" r="3.25"/><path d="M19.4 12c0 .4 0 .8-.1 1.2l2 1.6-2 3.4-2.4-1a7.3 7.3 0 0 1-2 1.2L14.4 21H9.6l-.5-2.6a7.3 7.3 0 0 1-2-1.2l-2.4 1-2-3.4 2-1.6a7.4 7.4 0 0 1 0-2.4l-2-1.6 2-3.4 2.4 1a7.3 7.3 0 0 1 2-1.2L9.6 3h4.8l.5 2.6a7.3 7.3 0 0 1 2 1.2l2.4-1 2 3.4-2 1.6c.1.4.1.8.1 1.2Z"/>',
  tag: '<path d="M3.5 12.5V5A1.5 1.5 0 0 1 5 3.5h7.5L21 12l-8.5 8.5Z"/><circle cx="7.5" cy="7.5" r="1.4"/>',
  clipboard:
    '<rect x="8" y="3" width="8" height="4" rx="1"/><path d="M9 5H6.5A1.5 1.5 0 0 0 5 6.5v13A1.5 1.5 0 0 0 6.5 21h11a1.5 1.5 0 0 0 1.5-1.5v-13A1.5 1.5 0 0 0 17.5 5H15"/><path d="m9 13.5 2 2 4.5-5"/>',
  calendar:
    '<rect x="3.5" y="5" width="17" height="15.5" rx="1.5"/><path d="M8 3v4M16 3v4M3.5 10h17"/>',
  'alert-triangle':
    '<path d="M12 4 2.7 19a1.5 1.5 0 0 0 1.3 2.2h16a1.5 1.5 0 0 0 1.3-2.2L12 4Z"/><path d="M12 10v4"/><path d="M12 17.5h.01"/>',
  'alert-circle':
    '<circle cx="12" cy="12" r="8.5"/><path d="M12 8v4.5"/><path d="M12 16h.01"/>',
  power: '<path d="M12 3.5v8"/><path d="M6.4 7.4a8 8 0 1 0 11.2 0"/>',
  check: '<path d="M20 6.5 9.5 17 4 11.5"/>',
  'check-circle':
    '<circle cx="12" cy="12" r="8.5"/><path d="m8.5 12 2.5 2.5 4.5-5"/>',
  sparkles:
    '<path d="M12 3.5 13.9 9 19.5 11 13.9 13 12 18.5 10.1 13 4.5 11 10.1 9 12 3.5Z"/><path d="M18.5 15.5 19.3 18l2.5.8-2.5.8-.8 2.5-.8-2.5-2.5-.8 2.5-.8.8-2.5Z"/>',
  target:
    '<circle cx="12" cy="12" r="8.5"/><circle cx="12" cy="12" r="4.75"/><circle cx="12" cy="12" r="1.2" fill="currentColor" stroke="none"/>',
  hourglass:
    '<path d="M6 3h12"/><path d="M6 21h12"/><path d="M7 3c0 4.5 4.5 6 4.5 9S7 16.5 7 21"/><path d="M17 3c0 4.5-4.5 6-4.5 9S17 16.5 17 21"/>',
  info: '<circle cx="12" cy="12" r="8.5"/><path d="M12 11v5"/><path d="M12 8h.01"/>',
  sprout:
    '<path d="M12 21v-9"/><path d="M12 12c-2.8 0-5-2-5-5.5 2.8 0 5 2 5 5.5Z"/><path d="M12 12c0-3 2.2-5.5 5-5.5 0 3.5-2.2 5.5-5 5.5Z"/>',
  'arrow-left': '<path d="M20 12H5"/><path d="m11 19-7-7 7-7"/>',
  wrench:
    '<path d="M15.5 4.5a4.2 4.2 0 0 0-5.4 5.4l-6.1 6.1a1.9 1.9 0 0 0 2.7 2.7l6.1-6.1a4.2 4.2 0 0 0 5.4-5.4l-2.6 2.6-2.3-.6-.6-2.3 2.6-2.6Z"/>',
  dot: '<circle cx="12" cy="12" r="2.5" fill="currentColor" stroke="none"/>',
};

@Component({
  selector: 'app-icon',
  standalone: true,
  template: `<span class="app-icon" [innerHTML]="svg"></span>`,
  styles: [
    `
      :host {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        line-height: 0;
        vertical-align: -0.125em;
        flex-shrink: 0;
      }
      .app-icon {
        display: inline-flex;
      }
    `,
  ],
})
export class Icon implements OnChanges {
  private readonly sanitizer = inject(DomSanitizer);

  /** Nombre del icono. Ver claves de ICONS. */
  @Input() name = '';
  /** Grosor del trazo. */
  @Input() strokeWidth: number | string = 1.75;

  protected svg: SafeHtml = '';

  ngOnChanges(): void {
    const inner = ICONS[this.name] ?? ICONS['dot'];
    this.svg = this.sanitizer.bypassSecurityTrustHtml(
      `<svg viewBox="0 0 24 24" width="1em" height="1em" fill="none" ` +
        `stroke="currentColor" stroke-width="${this.strokeWidth}" ` +
        `stroke-linecap="round" stroke-linejoin="round" ` +
        `style="display:block;overflow:visible" aria-hidden="true">${inner}</svg>`
    );
  }
}
