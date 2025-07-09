import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResetPasswordCodePage } from './reset-password-code-page';

describe('ResetPasswordCodePage', () => {
  let component: ResetPasswordCodePage;
  let fixture: ComponentFixture<ResetPasswordCodePage>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ResetPasswordCodePage]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ResetPasswordCodePage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
