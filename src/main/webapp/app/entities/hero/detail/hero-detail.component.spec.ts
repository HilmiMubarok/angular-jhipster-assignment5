import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { HeroDetailComponent } from './hero-detail.component';

describe('Hero Management Detail Component', () => {
  let comp: HeroDetailComponent;
  let fixture: ComponentFixture<HeroDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [HeroDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ hero: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(HeroDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(HeroDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load hero on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.hero).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
