import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LefleatComponent } from './lefleat.component';

describe('LefleatComponent', () => {
  let component: LefleatComponent;
  let fixture: ComponentFixture<LefleatComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LefleatComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LefleatComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
