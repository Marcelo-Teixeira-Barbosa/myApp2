import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { LineFormService, LineFormGroup } from './line-form.service';
import { ILine } from '../line.model';
import { LineService } from '../service/line.service';
import { IBoard } from 'app/entities/board/board.model';
import { BoardService } from 'app/entities/board/service/board.service';

@Component({
  selector: 'jhi-line-update',
  templateUrl: './line-update.component.html',
})
export class LineUpdateComponent implements OnInit {
  isSaving = false;
  line: ILine | null = null;

  boardsSharedCollection: IBoard[] = [];

  editForm: LineFormGroup = this.lineFormService.createLineFormGroup();

  constructor(
    protected lineService: LineService,
    protected lineFormService: LineFormService,
    protected boardService: BoardService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareBoard = (o1: IBoard | null, o2: IBoard | null): boolean => this.boardService.compareBoard(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ line }) => {
      this.line = line;
      if (line) {
        this.updateForm(line);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const line = this.lineFormService.getLine(this.editForm);
    if (line.id !== null) {
      this.subscribeToSaveResponse(this.lineService.update(line));
    } else {
      this.subscribeToSaveResponse(this.lineService.create(line));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILine>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(line: ILine): void {
    this.line = line;
    this.lineFormService.resetForm(this.editForm, line);

    this.boardsSharedCollection = this.boardService.addBoardToCollectionIfMissing<IBoard>(this.boardsSharedCollection, line.board);
  }

  protected loadRelationshipsOptions(): void {
    this.boardService
      .query()
      .pipe(map((res: HttpResponse<IBoard[]>) => res.body ?? []))
      .pipe(map((boards: IBoard[]) => this.boardService.addBoardToCollectionIfMissing<IBoard>(boards, this.line?.board)))
      .subscribe((boards: IBoard[]) => (this.boardsSharedCollection = boards));
  }
}
