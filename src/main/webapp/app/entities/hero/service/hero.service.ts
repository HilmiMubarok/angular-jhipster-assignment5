import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IHero, getHeroIdentifier } from '../hero.model';

export type EntityResponseType = HttpResponse<IHero>;
export type EntityArrayResponseType = HttpResponse<IHero[]>;

@Injectable({ providedIn: 'root' })
export class HeroService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/heroes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(hero: IHero): Observable<EntityResponseType> {
    return this.http.post<IHero>(this.resourceUrl, hero, { observe: 'response' });
  }

  update(hero: IHero): Observable<EntityResponseType> {
    return this.http.put<IHero>(`${this.resourceUrl}/${getHeroIdentifier(hero) as number}`, hero, { observe: 'response' });
  }

  partialUpdate(hero: IHero): Observable<EntityResponseType> {
    return this.http.patch<IHero>(`${this.resourceUrl}/${getHeroIdentifier(hero) as number}`, hero, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IHero>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IHero[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addHeroToCollectionIfMissing(heroCollection: IHero[], ...heroesToCheck: (IHero | null | undefined)[]): IHero[] {
    const heroes: IHero[] = heroesToCheck.filter(isPresent);
    if (heroes.length > 0) {
      const heroCollectionIdentifiers = heroCollection.map(heroItem => getHeroIdentifier(heroItem)!);
      const heroesToAdd = heroes.filter(heroItem => {
        const heroIdentifier = getHeroIdentifier(heroItem);
        if (heroIdentifier == null || heroCollectionIdentifiers.includes(heroIdentifier)) {
          return false;
        }
        heroCollectionIdentifiers.push(heroIdentifier);
        return true;
      });
      return [...heroesToAdd, ...heroCollection];
    }
    return heroCollection;
  }
}
