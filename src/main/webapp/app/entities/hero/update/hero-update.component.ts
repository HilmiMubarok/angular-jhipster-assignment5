import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IHero, Hero } from '../hero.model';
import { HeroService } from '../service/hero.service';

@Component({
  selector: 'jhi-hero-update',
  templateUrl: './hero-update.component.html',
})
export class HeroUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.minLength(3)]],
  });

  constructor(protected heroService: HeroService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ hero }) => {
      this.updateForm(hero);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const hero = this.createFromForm();
    if (hero.id !== undefined) {
      this.subscribeToSaveResponse(this.heroService.update(hero));
    } else {
      this.subscribeToSaveResponse(this.heroService.create(hero));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IHero>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(hero: IHero): void {
    this.editForm.patchValue({
      id: hero.id,
      name: hero.name,
    });
  }

  protected createFromForm(): IHero {
    return {
      ...new Hero(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
    };
  }
}
