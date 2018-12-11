import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Role} from '../../model/api/user/Role';
import {rolesApiEndpoint} from '../../../app.constant';

@Injectable()
export class HttpRoleService {

  /**
   * Constructor
   *
   * @param httpClient The http client service to inject
   */
  constructor(private httpClient: HttpClient) {
  }

  /**
   * Get the list of roles
   *
   * @returns {Observable<Role[]>}
   */
  getRoles(): Observable<Role[]> {
    const url = `${rolesApiEndpoint}`;

    return this.httpClient.get<Role[]>(url);
  }


}