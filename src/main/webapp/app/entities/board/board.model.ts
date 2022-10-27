import { ILine } from 'app/entities/line/line.model';
export interface IBoard {
  id: number;
  title?: string | null;
  line?: ILine | null;
}

export type NewBoard = Omit<IBoard, 'id'> & { id: null };
