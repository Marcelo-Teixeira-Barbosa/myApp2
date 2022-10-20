import { ILine, NewLine } from './line.model';

export const sampleWithRequiredData: ILine = {
  id: 85488,
};

export const sampleWithPartialData: ILine = {
  id: 31857,
};

export const sampleWithFullData: ILine = {
  id: 53778,
  title: 'copying',
};

export const sampleWithNewData: NewLine = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
