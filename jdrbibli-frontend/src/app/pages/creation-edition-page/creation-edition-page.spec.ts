import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreationEditionPage } from './creation-edition-page';

describe('CreationEditionPage', () => {
  let component: CreationEditionPage;
  let fixture: ComponentFixture<CreationEditionPage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreationEditionPage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreationEditionPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
