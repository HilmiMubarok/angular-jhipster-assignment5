import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IHero, Hero } from '../hero.model';

import { HeroService } from './hero.service';

describe('Hero Service', () => {
  let service: HeroService;
  let httpMock: HttpTestingController;
  let elemDefault: IHero;
  let expectedResult: IHero | IHero[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(HeroService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Hero', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Hero()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Hero', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Hero', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
        },
        new Hero()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Hero', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Hero', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addHeroToCollectionIfMissing', () => {
      it('should add a Hero to an empty array', () => {
        const hero: IHero = { id: 123 };
        expectedResult = service.addHeroToCollectionIfMissing([], hero);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(hero);
      });

      it('should not add a Hero to an array that contains it', () => {
        const hero: IHero = { id: 123 };
        const heroCollection: IHero[] = [
          {
            ...hero,
          },
          { id: 456 },
        ];
        expectedResult = service.addHeroToCollectionIfMissing(heroCollection, hero);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Hero to an array that doesn't contain it", () => {
        const hero: IHero = { id: 123 };
        const heroCollection: IHero[] = [{ id: 456 }];
        expectedResult = service.addHeroToCollectionIfMissing(heroCollection, hero);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(hero);
      });

      it('should add only unique Hero to an array', () => {
        const heroArray: IHero[] = [{ id: 123 }, { id: 456 }, { id: 97723 }];
        const heroCollection: IHero[] = [{ id: 123 }];
        expectedResult = service.addHeroToCollectionIfMissing(heroCollection, ...heroArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const hero: IHero = { id: 123 };
        const hero2: IHero = { id: 456 };
        expectedResult = service.addHeroToCollectionIfMissing([], hero, hero2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(hero);
        expect(expectedResult).toContain(hero2);
      });

      it('should accept null and undefined values', () => {
        const hero: IHero = { id: 123 };
        expectedResult = service.addHeroToCollectionIfMissing([], null, hero, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(hero);
      });

      it('should return initial array if no Hero is added', () => {
        const heroCollection: IHero[] = [{ id: 123 }];
        expectedResult = service.addHeroToCollectionIfMissing(heroCollection, undefined, null);
        expect(expectedResult).toEqual(heroCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
