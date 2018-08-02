/*
 *  /*
 *  * Copyright 2012-2018 the original author or authors.
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

import {RepositoryTypeEnum} from './enums/RepositoryTypeEnum';
import {Widget} from './Widget';

/**
 * Hold the repository informations
 */
export class Repository {

  /**
   * The repository id
   */
  id: number;

  /**
   * The repository name
   */
  name: string;

  /**
   * The repository url
   */
  url: string;

  /**
   * The repository branch to clone
   */
  branch: string;

  /**
   * The login to use for the connection to the remote repository
   */
  login: string;

  /**
   * The password to use for the connection to the remote repository
   */
  password: string;

  /**
   * The path of the repository in case of a local folder
   */
  localPath: string;

  /**
   * The type of repository
   */
  type: RepositoryTypeEnum;

  /**
   * If the repository is enable or not
   */
  enabled: boolean;

  /**
   * The list of related widgets
   */
  widgets: Widget[];
}
