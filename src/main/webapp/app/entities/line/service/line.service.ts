import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILine, NewLine } from '../line.model';

export type PartialUpdateLine = Partial<ILine> & Pick<ILine, 'id'>;

export type EntityResponseType = HttpResponse<ILine>;
export type EntityArrayResponseType = HttpResponse<ILine[]>;

@Injectable({ providedIn: 'root' })
export class LineService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/lines');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(line: NewLine): Observable<EntityResponseType> {
    return this.http.post<ILine>(this.resourceUrl, line, { observe: 'response' });
  }

  update(line: ILine): Observable<EntityResponseType> {
    return this.http.put<ILine>(`${this.resourceUrl}/${this.getLineIdentifier(line)}`, line, { observe: 'response' });
  }

  partialUpdate(line: PartialUpdateLine): Observable<EntityResponseType> {
    return this.http.patch<ILine>(`${this.resourceUrl}/${this.getLineIdentifier(line)}`, line, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ILine>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILine[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getLineIdentifier(line: Pick<ILine, 'id'>): number {
    return line.id;
  }

  compareLine(o1: Pick<ILine, 'id'> | null, o2: Pick<ILine, 'id'> | null): boolean {
    return o1 && o2 ? this.getLineIdentifier(o1) === this.getLineIdentifier(o2) : o1 === o2;
  }

  addLineToCollectionIfMissing<Type extends Pick<ILine, 'id'>>(
    lineCollection: Type[],
    ...linesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const lines: Type[] = linesToCheck.filter(isPresent);
    if (lines.length > 0) {
      const lineCollectionIdentifiers = lineCollection.map(lineItem => this.getLineIdentifier(lineItem)!);
      const linesToAdd = lines.filter(lineItem => {
        const lineIdentifier = this.getLineIdentifier(lineItem);
        if (lineCollectionIdentifiers.includes(lineIdentifier)) {
          return false;
        }
        lineCollectionIdentifiers.push(lineIdentifier);
        return true;
      });
      return [...linesToAdd, ...lineCollection];
    }
    return lineCollection;
  }
}
