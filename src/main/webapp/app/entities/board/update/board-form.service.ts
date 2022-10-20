import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IBoard, NewBoard } from '../board.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBoard for edit and NewBoardFormGroupInput for create.
 */
type BoardFormGroupInput = IBoard | PartialWithRequiredKeyOf<NewBoard>;

type BoardFormDefaults = Pick<NewBoard, 'id'>;

type BoardFormGroupContent = {
  id: FormControl<IBoard['id'] | NewBoard['id']>;
  title: FormControl<IBoard['title']>;
};

export type BoardFormGroup = FormGroup<BoardFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BoardFormService {
  createBoardFormGroup(board: BoardFormGroupInput = { id: null }): BoardFormGroup {
    const boardRawValue = {
      ...this.getFormDefaults(),
      ...board,
    };
    return new FormGroup<BoardFormGroupContent>({
      id: new FormControl(
        { value: boardRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      title: new FormControl(boardRawValue.title),
    });
  }

  getBoard(form: BoardFormGroup): IBoard | NewBoard {
    return form.getRawValue() as IBoard | NewBoard;
  }

  resetForm(form: BoardFormGroup, board: BoardFormGroupInput): void {
    const boardRawValue = { ...this.getFormDefaults(), ...board };
    form.reset(
      {
        ...boardRawValue,
        id: { value: boardRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): BoardFormDefaults {
    return {
      id: null,
    };
  }
}
