import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Reto } from './reto';

describe('Reto', () => {
  let component: Reto;
  let fixture: ComponentFixture<Reto>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Reto],
    }).compileComponents();

    fixture = TestBed.createComponent(Reto);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
