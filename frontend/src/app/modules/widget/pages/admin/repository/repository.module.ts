import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RepositoryListComponent} from './repository-list/repository-list.component';
import {RepositoryService} from './repository.service';
import {SharedModule} from '../../../../../shared/shared.module';
import {LayoutModule} from '../../../../../layout/layout.module';

@NgModule({
  imports: [
    CommonModule,
    LayoutModule,
    SharedModule
  ],
  declarations: [
    RepositoryListComponent
  ],
  providers: [
    RepositoryService
  ]
})
export class RepositoryModule {
}
