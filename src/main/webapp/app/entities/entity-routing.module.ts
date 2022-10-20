import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'board',
        data: { pageTitle: 'myApp2App.board.home.title' },
        loadChildren: () => import('./board/board.module').then(m => m.BoardModule),
      },
      {
        path: 'line',
        data: { pageTitle: 'myApp2App.line.home.title' },
        loadChildren: () => import('./line/line.module').then(m => m.LineModule),
      },
      {
        path: 'card',
        data: { pageTitle: 'myApp2App.card.home.title' },
        loadChildren: () => import('./card/card.module').then(m => m.CardModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
