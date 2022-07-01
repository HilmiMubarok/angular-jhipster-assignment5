import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IHero, Hero } from '../hero.model';
import { HeroService } from '../service/hero.service';

@Injectable({ providedIn: 'root' })
export class HeroRoutingResolveService implements Resolve<IHero> {
  constructor(protected service: HeroService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IHero> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((hero: HttpResponse<Hero>) => {
          if (hero.body) {
            return of(hero.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Hero());
  }
}
