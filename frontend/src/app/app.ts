import { Component, AfterViewInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements AfterViewInit {

  ngAfterViewInit(): void {
    const aplicarAutocompleteOff = () => {
      document.querySelectorAll('input, textarea, select, form').forEach((element) => {
        element.setAttribute('autocomplete', 'off');
      });
    };

    // Aplicar inicialmente
    aplicarAutocompleteOff();

    // Aplicar también cuando Angular cambie de página/componente
    const observer = new MutationObserver(() => {
      aplicarAutocompleteOff();
    });

    observer.observe(document.body, {
      childList: true,
      subtree: true
    });
  }
}