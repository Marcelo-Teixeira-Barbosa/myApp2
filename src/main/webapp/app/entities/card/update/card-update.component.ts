import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { CardFormService, CardFormGroup } from './card-form.service';
import { ICard } from '../card.model';
import { CardService } from '../service/card.service';
import { ILine } from 'app/entities/line/line.model';
import { LineService } from 'app/entities/line/service/line.service';

@Component({
  selector: 'jhi-card-update',
  templateUrl: './card-update.component.html',
})
export class CardUpdateComponent implements OnInit {
  isSaving = false;
  card: ICard | null = null;

  linesSharedCollection: ILine[] = [];

  editForm: CardFormGroup = this.cardFormService.createCardFormGroup();

  constructor(
    protected cardService: CardService,
    protected cardFormService: CardFormService,
    protected lineService: LineService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareLine = (o1: ILine | null, o2: ILine | null): boolean => this.lineService.compareLine(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ card }) => {
      this.card = card;
      if (card) {
        this.updateForm(card);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const card = this.cardFormService.getCard(this.editForm);
    if (card.id !== null) {
      this.subscribeToSaveResponse(this.cardService.update(card));
    } else {
      this.subscribeToSaveResponse(this.cardService.create(card));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICard>>): void {
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

  protected updateForm(card: ICard): void {
    this.card = card;
    this.cardFormService.resetForm(this.editForm, card);

    this.linesSharedCollection = this.lineService.addLineToCollectionIfMissing<ILine>(this.linesSharedCollection, card.line);
  }

  protected loadRelationshipsOptions(): void {
    this.lineService
      .query()
      .pipe(map((res: HttpResponse<ILine[]>) => res.body ?? []))
      .pipe(map((lines: ILine[]) => this.lineService.addLineToCollectionIfMissing<ILine>(lines, this.card?.line)))
      .subscribe((lines: ILine[]) => (this.linesSharedCollection = lines));
  }
}
