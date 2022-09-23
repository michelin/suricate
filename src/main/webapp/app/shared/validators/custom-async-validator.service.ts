import { Injectable } from '@angular/core';
import { AbstractControl, ValidationErrors } from '@angular/forms';
import { map } from 'rxjs/operators';
import { HttpRepositoryService } from '../services/backend/http-repository/http-repository.service';
import { Observable } from 'rxjs';
import { HttpFilter } from '../models/backend/http-filter';
import { HttpFilterService } from '../services/backend/http-filter/http-filter.service';

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
   * @param control The form value
   */
  public validateRepositoryUniquePriority(control: AbstractControl): Observable<ValidationErrors> {
    return this.httpRepositoryService.getAll(HttpFilterService.getInfiniteFilter()).pipe(
      map(repositories => {
        return repositories.content.filter(repository => repository.priority === control.value).length >= 1
          ? { uniquePriority: true }
          : null;
      })
    );
  }
}
