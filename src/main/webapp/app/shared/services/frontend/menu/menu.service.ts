/*
 *  /*
 *  * Copyright 2012-2021 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MenuConfiguration } from '../../../models/frontend/menu/menu-configuration';
import { AuthenticationService } from '../authentication/authentication.service';
import { MenuCategoryConfiguration } from '../../../models/frontend/menu/menu-category-configuration';

/**
 * Service used to manage menu
 */
@Injectable({ providedIn: 'root' })
export class MenuService {
  /**
   * Routes where the menu should be hidden
   */
  public static readonly routesWithoutMenu = ['login', 'register', 'tv'];

  /**
   * Used to know if we should hide the menu
   *
   * @param activatedRoute The activated route by the displayed component
   */
  public static shouldHideMenu(activatedRoute: ActivatedRoute): boolean {
    return MenuService.routesWithoutMenu.includes(activatedRoute.routeConfig.path);
  }

  /**
   * Build the menu
   * @param admin Is the user admin or not ?
   */
  public static buildMenu(admin: boolean): MenuConfiguration {
    const menuConfiguration = new MenuConfiguration();

    if (admin) {
      menuConfiguration.categories.push(MenuService.buildAdminMenu());
    }

    menuConfiguration.categories.push(MenuService.buildWidgetMenu());

    return menuConfiguration;
  }

  /**
   * Build the admin menu
   */
  private static buildAdminMenu(): MenuCategoryConfiguration {
    return {
      label: 'admin',
      items: [
        {
          label: 'configuration.list',
          linkConfiguration: { link: ['/admin', 'configurations'] }
        },
        {
          label: 'dashboard.list',
          linkConfiguration: { link: ['/admin', 'dashboards'] }
        },
        {
          label: 'repository.list',
          linkConfiguration: { link: ['/admin', 'repositories'] }
        },
        {
          label: 'user.list',
          linkConfiguration: { link: ['/admin', 'users'] }
        }
      ]
    };
  }

  /**
   * Build the widget menu
   */
  private static buildWidgetMenu(): MenuCategoryConfiguration {
    return {
      label: 'widget.list',
      items: [
        {
          label: 'catalog',
          linkConfiguration: { link: ['/widgets', 'catalog'] }
        }
      ]
    };
  }
}
