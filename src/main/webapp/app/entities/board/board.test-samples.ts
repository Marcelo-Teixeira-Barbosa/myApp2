import { IBoard, NewBoard } from './board.model';

export const sampleWithRequiredData: IBoard = {
  id: 31483,
};

export const sampleWithPartialData: IBoard = {
  id: 29419,
};

export const sampleWithFullData: IBoard = {
  id: 44405,
  title: 'Mali circuit ivory',
};

export const sampleWithNewData: NewBoard = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
