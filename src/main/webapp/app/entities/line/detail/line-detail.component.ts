import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ILine } from '../line.model';

@Component({
  selector: 'jhi-line-detail',
  templateUrl: './line-detail.component.html',
})
export class LineDetailComponent implements OnInit {
  line: ILine | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ line }) => {
      this.line = line;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
