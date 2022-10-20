import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ILine } from '../line.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../line.test-samples';

import { LineService } from './line.service';

const requireRestSample: ILine = {
  ...sampleWithRequiredData,
};

describe('Line Service', () => {
  let service: LineService;
  let httpMock: HttpTestingController;
  let expectedResult: ILine | ILine[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(LineService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Line', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const line = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(line).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Line', () => {
      const line = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(line).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Line', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Line', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Line', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addLineToCollectionIfMissing', () => {
      it('should add a Line to an empty array', () => {
        const line: ILine = sampleWithRequiredData;
        expectedResult = service.addLineToCollectionIfMissing([], line);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(line);
      });

      it('should not add a Line to an array that contains it', () => {
        const line: ILine = sampleWithRequiredData;
        const lineCollection: ILine[] = [
          {
            ...line,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addLineToCollectionIfMissing(lineCollection, line);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Line to an array that doesn't contain it", () => {
        const line: ILine = sampleWithRequiredData;
        const lineCollection: ILine[] = [sampleWithPartialData];
        expectedResult = service.addLineToCollectionIfMissing(lineCollection, line);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(line);
      });

      it('should add only unique Line to an array', () => {
        const lineArray: ILine[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const lineCollection: ILine[] = [sampleWithRequiredData];
        expectedResult = service.addLineToCollectionIfMissing(lineCollection, ...lineArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const line: ILine = sampleWithRequiredData;
        const line2: ILine = sampleWithPartialData;
        expectedResult = service.addLineToCollectionIfMissing([], line, line2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(line);
        expect(expectedResult).toContain(line2);
      });

      it('should accept null and undefined values', () => {
        const line: ILine = sampleWithRequiredData;
        expectedResult = service.addLineToCollectionIfMissing([], null, line, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(line);
      });

      it('should return initial array if no Line is added', () => {
        const lineCollection: ILine[] = [sampleWithRequiredData];
        expectedResult = service.addLineToCollectionIfMissing(lineCollection, undefined, null);
        expect(expectedResult).toEqual(lineCollection);
      });
    });

    describe('compareLine', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareLine(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareLine(entity1, entity2);
        const compareResult2 = service.compareLine(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareLine(entity1, entity2);
        const compareResult2 = service.compareLine(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareLine(entity1, entity2);
        const compareResult2 = service.compareLine(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
