import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'hero',
        data: { pageTitle: 'myappApp.hero.home.title' },
        loadChildren: () => import('./hero/hero.module').then(m => m.HeroModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
