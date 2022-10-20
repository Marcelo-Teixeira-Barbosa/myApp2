import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { LineDetailComponent } from './line-detail.component';

describe('Line Management Detail Component', () => {
  let comp: LineDetailComponent;
  let fixture: ComponentFixture<LineDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [LineDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ line: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(LineDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(LineDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load line on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.line).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
