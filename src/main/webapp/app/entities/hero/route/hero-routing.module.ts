import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { HeroComponent } from '../list/hero.component';
import { HeroDetailComponent } from '../detail/hero-detail.component';
import { HeroUpdateComponent } from '../update/hero-update.component';
import { HeroRoutingResolveService } from './hero-routing-resolve.service';

const heroRoute: Routes = [
  {
    path: '',
    component: HeroComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: HeroDetailComponent,
    resolve: {
      hero: HeroRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: HeroUpdateComponent,
    resolve: {
      hero: HeroRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: HeroUpdateComponent,
    resolve: {
      hero: HeroRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(heroRoute)],
  exports: [RouterModule],
})
export class HeroRoutingModule {}
