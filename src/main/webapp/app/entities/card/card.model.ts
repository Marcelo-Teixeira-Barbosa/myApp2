import { ILine } from 'app/entities/line/line.model';
import { IBoard } from '../board/board.model';

export interface ICard {
  id: number;
  title?: string | null;
  level?: number | null;
  desc?: string | null;
  line?: ILine | null;
  board?: IBoard | null;
}

export type NewCard = Omit<ICard, 'id'> & { id: null };
