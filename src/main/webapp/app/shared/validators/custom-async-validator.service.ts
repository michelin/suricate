import { Injectable } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, ValidationErrors } from '@angular/forms';
import { map } from 'rxjs/operators';
import { HttpRepositoryService } from '../services/backend/http-repository/http-repository.service';
import { Observable } from 'rxjs';
import { HttpFilter } from '../models/backend/http-filter';
import { HttpFilterService } from '../services/backend/http-filter/http-filter.service';
import { Repository } from '../models/backend/repository/repository';

@Injectable({
  providedIn: 'root'
})
export class CustomAsyncValidatorService {
  /**
   * Constructor
   * @param httpRepositoryService The HTTP repository service
   */
  constructor(private readonly httpRepositoryService: HttpRepositoryService) {}

  /**
   * Check repository priority uniqueness
   * @param currentRepository The current repository
   */
  public validateRepositoryUniquePriority(currentRepository: Repository): AsyncValidatorFn {
    return (control: AbstractControl): Observable<ValidationErrors> => {
      return this.httpRepositoryService.getAll(HttpFilterService.getInfiniteFilter()).pipe(
        map(repositories => {
          return repositories.content
            .filter(repository => repository.id !== currentRepository.id)
            .filter(repository => repository.priority === control.value).length >= 1
            ? { uniquePriority: true }
            : null;
        })
      );
    };
  }
}
