import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IHero } from '../hero.model';
import { HeroService } from '../service/hero.service';

@Component({
  templateUrl: './hero-delete-dialog.component.html',
})
export class HeroDeleteDialogComponent {
  hero?: IHero;

  constructor(protected heroService: HeroService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.heroService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
