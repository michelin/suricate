import {Injectable} from '@angular/core';
import {CanActivate, CanActivateChild, Router} from '@angular/router';
import {Observable} from 'rxjs/Observable';
import {UserService} from '../../../modules/user/user.service';
import {of} from 'rxjs/observable/of';

/**
 * The admin guard
 */
@Injectable()
export class AdminGuard implements CanActivate, CanActivateChild {

  /**
   * The constructor
   *
   * @param {UserService} _userService The user service
   * @param {Router} _router The router
   */
  constructor(private _userService: UserService,
              private _router: Router) {

  }

  /**
   * Activate root routes
   * @returns {Observable<boolean>}
   */
  canActivate(): Observable<boolean> {
    if (this._userService.isAdmin()) {
      return of(true);
    }

    this._router.navigate(['home']);
    return of(false);
  }

  /**
   * For Child routes
   * @returns {Observable<boolean>}
   */
  canActivateChild(): Observable<boolean> {
    return this.canActivate();
  }
}
