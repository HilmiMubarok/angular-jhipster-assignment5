import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { HeroComponent } from './list/hero.component';
import { HeroDetailComponent } from './detail/hero-detail.component';
import { HeroUpdateComponent } from './update/hero-update.component';
import { HeroDeleteDialogComponent } from './delete/hero-delete-dialog.component';
import { HeroRoutingModule } from './route/hero-routing.module';

@NgModule({
  imports: [SharedModule, HeroRoutingModule],
  declarations: [HeroComponent, HeroDetailComponent, HeroUpdateComponent, HeroDeleteDialogComponent],
  entryComponents: [HeroDeleteDialogComponent],
})
export class HeroModule {}
