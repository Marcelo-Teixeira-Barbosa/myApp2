import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../board.test-samples';

import { BoardFormService } from './board-form.service';

describe('Board Form Service', () => {
  let service: BoardFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BoardFormService);
  });

  describe('Service methods', () => {
    describe('createBoardFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBoardFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
          })
        );
      });

      it('passing IBoard should create a new form with FormGroup', () => {
        const formGroup = service.createBoardFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
          })
        );
      });
    });

    describe('getBoard', () => {
      it('should return NewBoard for default Board initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createBoardFormGroup(sampleWithNewData);

        const board = service.getBoard(formGroup) as any;

        expect(board).toMatchObject(sampleWithNewData);
      });

      it('should return NewBoard for empty Board initial value', () => {
        const formGroup = service.createBoardFormGroup();

        const board = service.getBoard(formGroup) as any;

        expect(board).toMatchObject({});
      });

      it('should return IBoard', () => {
        const formGroup = service.createBoardFormGroup(sampleWithRequiredData);

        const board = service.getBoard(formGroup) as any;

        expect(board).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBoard should not enable id FormControl', () => {
        const formGroup = service.createBoardFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBoard should disable id FormControl', () => {
        const formGroup = service.createBoardFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
