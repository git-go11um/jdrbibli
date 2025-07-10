import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomePublicComponent } from './Home-Public.Component';

describe('HomePublicComponent', () => {
  let component: HomePublicComponent;
  let fixture: ComponentFixture<HomePublicComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [HomePublicComponent]  // <== ici dans declarations, pas imports
    }).compileComponents();

    fixture = TestBed.createComponent(HomePublicComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
