import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { BoardFormService } from './board-form.service';
import { BoardService } from '../service/board.service';
import { IBoard } from '../board.model';

import { BoardUpdateComponent } from './board-update.component';

describe('Board Management Update Component', () => {
  let comp: BoardUpdateComponent;
  let fixture: ComponentFixture<BoardUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let boardFormService: BoardFormService;
  let boardService: BoardService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [BoardUpdateComponent],
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
      .overrideTemplate(BoardUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BoardUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    boardFormService = TestBed.inject(BoardFormService);
    boardService = TestBed.inject(BoardService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const board: IBoard = { id: 456 };

      activatedRoute.data = of({ board });
      comp.ngOnInit();

      expect(comp.board).toEqual(board);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBoard>>();
      const board = { id: 123 };
      jest.spyOn(boardFormService, 'getBoard').mockReturnValue(board);
      jest.spyOn(boardService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ board });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: board }));
      saveSubject.complete();

      // THEN
      expect(boardFormService.getBoard).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(boardService.update).toHaveBeenCalledWith(expect.objectContaining(board));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBoard>>();
      const board = { id: 123 };
      jest.spyOn(boardFormService, 'getBoard').mockReturnValue({ id: null });
      jest.spyOn(boardService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ board: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: board }));
      saveSubject.complete();

      // THEN
      expect(boardFormService.getBoard).toHaveBeenCalled();
      expect(boardService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBoard>>();
      const board = { id: 123 };
      jest.spyOn(boardService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ board });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(boardService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
