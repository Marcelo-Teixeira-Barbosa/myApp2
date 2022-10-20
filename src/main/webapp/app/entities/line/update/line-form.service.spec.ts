import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../line.test-samples';

import { LineFormService } from './line-form.service';

describe('Line Form Service', () => {
  let service: LineFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LineFormService);
  });

  describe('Service methods', () => {
    describe('createLineFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLineFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            board: expect.any(Object),
          })
        );
      });

      it('passing ILine should create a new form with FormGroup', () => {
        const formGroup = service.createLineFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            title: expect.any(Object),
            board: expect.any(Object),
          })
        );
      });
    });

    describe('getLine', () => {
      it('should return NewLine for default Line initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createLineFormGroup(sampleWithNewData);

        const line = service.getLine(formGroup) as any;

        expect(line).toMatchObject(sampleWithNewData);
      });

      it('should return NewLine for empty Line initial value', () => {
        const formGroup = service.createLineFormGroup();

        const line = service.getLine(formGroup) as any;

        expect(line).toMatchObject({});
      });

      it('should return ILine', () => {
        const formGroup = service.createLineFormGroup(sampleWithRequiredData);

        const line = service.getLine(formGroup) as any;

        expect(line).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILine should not enable id FormControl', () => {
        const formGroup = service.createLineFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLine should disable id FormControl', () => {
        const formGroup = service.createLineFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
