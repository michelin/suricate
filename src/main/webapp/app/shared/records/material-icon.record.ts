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

import { IconEnum } from '../enums/icon.enum';

/**
 * Records used to store related material icons from https://material.io/resources/icons
 */
export const MaterialIconRecords: Record<IconEnum, string> = {
  [IconEnum.ADD]: 'add',
  [IconEnum.DELETE]: 'delete_forever',
  [IconEnum.EDIT]: 'edit',
  [IconEnum.CLOSE]: 'close',
  [IconEnum.SAVE]: 'done',
  [IconEnum.REFRESH]: 'refresh',
  [IconEnum.GENERAL_INFORMATION]: 'menu_book',
  [IconEnum.USERS]: 'supervisor_account',
  [IconEnum.TV]: 'tv',
  [IconEnum.TV_LIVE]: 'live_tv',
  [IconEnum.CATEGORY]: 'category',
  [IconEnum.WIDGET]: 'widgets',
  [IconEnum.WIDGET_CONFIGURATION]: 'list_alt'
};
