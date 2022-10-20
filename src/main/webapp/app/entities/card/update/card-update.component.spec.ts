import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CardFormService } from './card-form.service';
import { CardService } from '../service/card.service';
import { ICard } from '../card.model';
import { ILine } from 'app/entities/line/line.model';
import { LineService } from 'app/entities/line/service/line.service';

import { CardUpdateComponent } from './card-update.component';

describe('Card Management Update Component', () => {
  let comp: CardUpdateComponent;
  let fixture: ComponentFixture<CardUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let cardFormService: CardFormService;
  let cardService: CardService;
  let lineService: LineService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CardUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(CardUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CardUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    cardFormService = TestBed.inject(CardFormService);
    cardService = TestBed.inject(CardService);
    lineService = TestBed.inject(LineService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Line query and add missing value', () => {
      const card: ICard = { id: 456 };
      const line: ILine = { id: 18360 };
      card.line = line;

      const lineCollection: ILine[] = [{ id: 2070 }];
      jest.spyOn(lineService, 'query').mockReturnValue(of(new HttpResponse({ body: lineCollection })));
      const additionalLines = [line];
      const expectedCollection: ILine[] = [...additionalLines, ...lineCollection];
      jest.spyOn(lineService, 'addLineToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ card });
      comp.ngOnInit();

      expect(lineService.query).toHaveBeenCalled();
      expect(lineService.addLineToCollectionIfMissing).toHaveBeenCalledWith(
        lineCollection,
        ...additionalLines.map(expect.objectContaining)
      );
      expect(comp.linesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const card: ICard = { id: 456 };
      const line: ILine = { id: 5618 };
      card.line = line;

      activatedRoute.data = of({ card });
      comp.ngOnInit();

      expect(comp.linesSharedCollection).toContain(line);
      expect(comp.card).toEqual(card);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICard>>();
      const card = { id: 123 };
      jest.spyOn(cardFormService, 'getCard').mockReturnValue(card);
      jest.spyOn(cardService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ card });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: card }));
      saveSubject.complete();

      // THEN
      expect(cardFormService.getCard).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(cardService.update).toHaveBeenCalledWith(expect.objectContaining(card));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICard>>();
      const card = { id: 123 };
      jest.spyOn(cardFormService, 'getCard').mockReturnValue({ id: null });
      jest.spyOn(cardService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ card: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: card }));
      saveSubject.complete();

      // THEN
      expect(cardFormService.getCard).toHaveBeenCalled();
      expect(cardService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICard>>();
      const card = { id: 123 };
      jest.spyOn(cardService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ card });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(cardService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareLine', () => {
      it('Should forward to lineService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(lineService, 'compareLine');
        comp.compareLine(entity, entity2);
        expect(lineService.compareLine).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
