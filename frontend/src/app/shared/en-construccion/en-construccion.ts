import { Component, Signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';

@Component({
  selector: 'app-en-construccion',
  standalone: true,
  template: `
    <div class="en-construccion">
      <span class="en-construccion__icon" aria-hidden="true">🚧</span>
      <h2>{{ titulo() }}</h2>
      <p>Esta sección está en construcción. Muy pronto vas a poder usarla desde aquí.</p>
    </div>
  `,
  styles: [`
    .en-construccion {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      text-align: center;
      padding: 60px 20px;
      color: var(--gema-text-gray);
    }
    .en-construccion__icon {
      font-size: 2.5rem;
      margin-bottom: 12px;
    }
    .en-construccion h2 {
      color: var(--gema-navy);
      margin-bottom: 8px;
    }
  `]
})
export class EnConstruccion {
  protected readonly titulo: Signal<string>;

  constructor(private route: ActivatedRoute) {
    this.titulo = toSignal(
      this.route.data.pipe(map((data) => data['titulo'] ?? 'Próximamente')),
      { initialValue: 'Próximamente' }
    );
  }
}