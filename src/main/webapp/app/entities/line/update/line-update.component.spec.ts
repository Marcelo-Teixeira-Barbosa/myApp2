import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { LineFormService } from './line-form.service';
import { LineService } from '../service/line.service';
import { ILine } from '../line.model';
import { IBoard } from 'app/entities/board/board.model';
import { BoardService } from 'app/entities/board/service/board.service';

import { LineUpdateComponent } from './line-update.component';

describe('Line Management Update Component', () => {
  let comp: LineUpdateComponent;
  let fixture: ComponentFixture<LineUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let lineFormService: LineFormService;
  let lineService: LineService;
  let boardService: BoardService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [LineUpdateComponent],
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
      .overrideTemplate(LineUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LineUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    lineFormService = TestBed.inject(LineFormService);
    lineService = TestBed.inject(LineService);
    boardService = TestBed.inject(BoardService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Board query and add missing value', () => {
      const line: ILine = { id: 456 };
      const board: IBoard = { id: 2181 };
      line.board = board;

      const boardCollection: IBoard[] = [{ id: 32356 }];
      jest.spyOn(boardService, 'query').mockReturnValue(of(new HttpResponse({ body: boardCollection })));
      const additionalBoards = [board];
      const expectedCollection: IBoard[] = [...additionalBoards, ...boardCollection];
      jest.spyOn(boardService, 'addBoardToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ line });
      comp.ngOnInit();

      expect(boardService.query).toHaveBeenCalled();
      expect(boardService.addBoardToCollectionIfMissing).toHaveBeenCalledWith(
        boardCollection,
        ...additionalBoards.map(expect.objectContaining)
      );
      expect(comp.boardsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const line: ILine = { id: 456 };
      const board: IBoard = { id: 48233 };
      line.board = board;

      activatedRoute.data = of({ line });
      comp.ngOnInit();

      expect(comp.boardsSharedCollection).toContain(board);
      expect(comp.line).toEqual(line);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILine>>();
      const line = { id: 123 };
      jest.spyOn(lineFormService, 'getLine').mockReturnValue(line);
      jest.spyOn(lineService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ line });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: line }));
      saveSubject.complete();

      // THEN
      expect(lineFormService.getLine).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(lineService.update).toHaveBeenCalledWith(expect.objectContaining(line));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILine>>();
      const line = { id: 123 };
      jest.spyOn(lineFormService, 'getLine').mockReturnValue({ id: null });
      jest.spyOn(lineService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ line: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: line }));
      saveSubject.complete();

      // THEN
      expect(lineFormService.getLine).toHaveBeenCalled();
      expect(lineService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILine>>();
      const line = { id: 123 };
      jest.spyOn(lineService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ line });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(lineService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareBoard', () => {
      it('Should forward to boardService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(boardService, 'compareBoard');
        comp.compareBoard(entity, entity2);
        expect(boardService.compareBoard).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
