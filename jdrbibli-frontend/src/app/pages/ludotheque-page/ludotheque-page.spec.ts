import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LudothequePage } from './ludotheque-page';

describe('LudothequePage', () => {
  let component: LudothequePage;
  let fixture: ComponentFixture<LudothequePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LudothequePage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LudothequePage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
