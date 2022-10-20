import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IBoard } from '../board.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../board.test-samples';

import { BoardService } from './board.service';

const requireRestSample: IBoard = {
  ...sampleWithRequiredData,
};

describe('Board Service', () => {
  let service: BoardService;
  let httpMock: HttpTestingController;
  let expectedResult: IBoard | IBoard[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(BoardService);
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

    it('should create a Board', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const board = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(board).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Board', () => {
      const board = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(board).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Board', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Board', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Board', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addBoardToCollectionIfMissing', () => {
      it('should add a Board to an empty array', () => {
        const board: IBoard = sampleWithRequiredData;
        expectedResult = service.addBoardToCollectionIfMissing([], board);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(board);
      });

      it('should not add a Board to an array that contains it', () => {
        const board: IBoard = sampleWithRequiredData;
        const boardCollection: IBoard[] = [
          {
            ...board,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBoardToCollectionIfMissing(boardCollection, board);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Board to an array that doesn't contain it", () => {
        const board: IBoard = sampleWithRequiredData;
        const boardCollection: IBoard[] = [sampleWithPartialData];
        expectedResult = service.addBoardToCollectionIfMissing(boardCollection, board);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(board);
      });

      it('should add only unique Board to an array', () => {
        const boardArray: IBoard[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const boardCollection: IBoard[] = [sampleWithRequiredData];
        expectedResult = service.addBoardToCollectionIfMissing(boardCollection, ...boardArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const board: IBoard = sampleWithRequiredData;
        const board2: IBoard = sampleWithPartialData;
        expectedResult = service.addBoardToCollectionIfMissing([], board, board2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(board);
        expect(expectedResult).toContain(board2);
      });

      it('should accept null and undefined values', () => {
        const board: IBoard = sampleWithRequiredData;
        expectedResult = service.addBoardToCollectionIfMissing([], null, board, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(board);
      });

      it('should return initial array if no Board is added', () => {
        const boardCollection: IBoard[] = [sampleWithRequiredData];
        expectedResult = service.addBoardToCollectionIfMissing(boardCollection, undefined, null);
        expect(expectedResult).toEqual(boardCollection);
      });
    });

    describe('compareBoard', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBoard(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareBoard(entity1, entity2);
        const compareResult2 = service.compareBoard(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareBoard(entity1, entity2);
        const compareResult2 = service.compareBoard(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareBoard(entity1, entity2);
        const compareResult2 = service.compareBoard(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
