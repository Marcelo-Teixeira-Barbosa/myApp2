export interface IBoard {
  id: number;
  title?: string | null;
}

export type NewBoard = Omit<IBoard, 'id'> & { id: null };
