import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthenticationService } from '../../../shared/services/frontend/authentication/authentication.service';
import { HttpRotationService } from '../../../shared/services/backend/http-rotation/http-rotation.service';

@Injectable({
  providedIn: 'root'
})
export class RotationService {
  /**
   * Constructor
   *
   * @param httpRotationService The HTTP rotation service
   */
  constructor(private readonly httpRotationService: HttpRotationService) {}

  /**
   * Check if the rotation should be displayed without rights
   *
   * @param rotationToken The rotation token
   */
  public shouldDisplayedReadOnly(rotationToken: string): Observable<boolean> {
    return this.httpRotationService.getRotationUsers(rotationToken).pipe(
      map(rotationUsers => {
        return (
          !AuthenticationService.isAdmin() &&
          !rotationUsers.some(user => user.username === AuthenticationService.getConnectedUser().username)
        );
      })
    );
  }
}
