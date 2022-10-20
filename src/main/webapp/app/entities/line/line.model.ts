import { IBoard } from 'app/entities/board/board.model';

export interface ILine {
  id: number;
  title?: string | null;
  board?: IBoard | null;
}

export type NewLine = Omit<ILine, 'id'> & { id: null };
