import { ILine } from 'app/entities/line/line.model';

export interface ICard {
  id: number;
  title?: string | null;
  level?: number | null;
  desc?: string | null;
  line?: Pick<ILine, 'id'> | null;
}

export type NewCard = Omit<ICard, 'id'> & { id: null };
