import { ICard, NewCard } from './card.model';

export const sampleWithRequiredData: ICard = {
  id: 57013,
};

export const sampleWithPartialData: ICard = {
  id: 79141,
  title: 'Wooden Nakfa reboot',
  level: 51411,
  desc: 'New',
};

export const sampleWithFullData: ICard = {
  id: 32166,
  title: 'Riel',
  level: 50175,
  desc: 'Mississippi',
};

export const sampleWithNewData: NewCard = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
