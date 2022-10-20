import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { LineComponent } from './list/line.component';
import { LineDetailComponent } from './detail/line-detail.component';
import { LineUpdateComponent } from './update/line-update.component';
import { LineDeleteDialogComponent } from './delete/line-delete-dialog.component';
import { LineRoutingModule } from './route/line-routing.module';

@NgModule({
  imports: [SharedModule, LineRoutingModule],
  declarations: [LineComponent, LineDetailComponent, LineUpdateComponent, LineDeleteDialogComponent],
})
export class LineModule {}
