import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBoard, NewBoard } from '../board.model';

export type PartialUpdateBoard = Partial<IBoard> & Pick<IBoard, 'id'>;

export type EntityResponseType = HttpResponse<IBoard>;
export type EntityArrayResponseType = HttpResponse<IBoard[]>;

@Injectable({ providedIn: 'root' })
export class BoardService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/boards');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(board: NewBoard): Observable<EntityResponseType> {
    return this.http.post<IBoard>(this.resourceUrl, board, { observe: 'response' });
  }

  update(board: IBoard): Observable<EntityResponseType> {
    return this.http.put<IBoard>(`${this.resourceUrl}/${this.getBoardIdentifier(board)}`, board, { observe: 'response' });
  }

  partialUpdate(board: PartialUpdateBoard): Observable<EntityResponseType> {
    return this.http.patch<IBoard>(`${this.resourceUrl}/${this.getBoardIdentifier(board)}`, board, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IBoard>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBoard[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getBoardIdentifier(board: Pick<IBoard, 'id'>): number {
    return board.id;
  }

  compareBoard(o1: Pick<IBoard, 'id'> | null, o2: Pick<IBoard, 'id'> | null): boolean {
    return o1 && o2 ? this.getBoardIdentifier(o1) === this.getBoardIdentifier(o2) : o1 === o2;
  }

  addBoardToCollectionIfMissing<Type extends Pick<IBoard, 'id'>>(
    boardCollection: Type[],
    ...boardsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const boards: Type[] = boardsToCheck.filter(isPresent);
    if (boards.length > 0) {
      const boardCollectionIdentifiers = boardCollection.map(boardItem => this.getBoardIdentifier(boardItem)!);
      const boardsToAdd = boards.filter(boardItem => {
        const boardIdentifier = this.getBoardIdentifier(boardItem);
        if (boardCollectionIdentifiers.includes(boardIdentifier)) {
          return false;
        }
        boardCollectionIdentifiers.push(boardIdentifier);
        return true;
      });
      return [...boardsToAdd, ...boardCollection];
    }
    return boardCollection;
  }
}
