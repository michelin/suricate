/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Params } from '@angular/router';

/**
 * Hold the information used to define an angular router link
 */
export class LinkConfiguration {
  /**
   * The redirection link
   */
  link: string | string[];
  /**
   * Query params associated to the link (use in case of internal links thru [routerLink])
   */
  queryParams?: Params;
  /**
   * If it's a link that not redirect in D4X app
   */
  externalApp?: boolean;
  /**
   * The link target if needed (blank, ...)
   */
  target?: string;
}
