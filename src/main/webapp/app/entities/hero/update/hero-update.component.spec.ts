import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { HeroService } from '../service/hero.service';
import { IHero, Hero } from '../hero.model';

import { HeroUpdateComponent } from './hero-update.component';

describe('Hero Management Update Component', () => {
  let comp: HeroUpdateComponent;
  let fixture: ComponentFixture<HeroUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let heroService: HeroService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [HeroUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(HeroUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(HeroUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    heroService = TestBed.inject(HeroService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const hero: IHero = { id: 456 };

      activatedRoute.data = of({ hero });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(hero));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Hero>>();
      const hero = { id: 123 };
      jest.spyOn(heroService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ hero });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: hero }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(heroService.update).toHaveBeenCalledWith(hero);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Hero>>();
      const hero = new Hero();
      jest.spyOn(heroService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ hero });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: hero }));
      saveSubject.complete();

      // THEN
      expect(heroService.create).toHaveBeenCalledWith(hero);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Hero>>();
      const hero = { id: 123 };
      jest.spyOn(heroService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ hero });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(heroService.update).toHaveBeenCalledWith(hero);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
