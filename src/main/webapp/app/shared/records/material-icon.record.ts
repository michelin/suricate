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
  [IconEnum.ARROW_DOWN]: 'keyboard_arrow_right',
  [IconEnum.BRANCH]: 'merge_type',
  [IconEnum.CATEGORY]: 'category',
  [IconEnum.CLOSE]: 'close',
  [IconEnum.COLUMN]: 'view_column',
  [IconEnum.DASHBOARD]: 'dashboard',
  [IconEnum.DELETE]: 'delete_forever',
  [IconEnum.EDIT]: 'edit',
  [IconEnum.EMAIL]: 'email',
  [IconEnum.ERROR]: 'error',
  [IconEnum.GENERAL_INFORMATION]: 'menu_book',
  [IconEnum.HEIGHT]: 'height',
  [IconEnum.HOME]: 'home',
  [IconEnum.INFO]: 'format_italic',
  [IconEnum.KEY]: 'vpn_key',
  [IconEnum.LANGUAGE]: 'language',
  [IconEnum.LOGOUT]: 'exit_to_app',
  [IconEnum.NAME]: 'short_text',
  [IconEnum.PASSWORD]: 'lock',
  [IconEnum.SHOW_PASSWORD]: 'visibility',
  [IconEnum.HIDE_PASSWORD]: 'visibility_off',
  [IconEnum.REFRESH]: 'refresh',
  [IconEnum.REPOSITORY_TYPE]: 'cloud_queue',
  [IconEnum.SAVE]: 'done',
  [IconEnum.SEARCH]: 'search',
  [IconEnum.SETTINGS]: 'settings',
  [IconEnum.SHARE_SCREEN]: 'screen_share',
  [IconEnum.STOP_SHARE_SCREEN]: 'stop_screen_share',
  [IconEnum.SUCCESS]: 'done',
  [IconEnum.TEXT]: 'notes',
  [IconEnum.THEME]: 'color_lens',
  [IconEnum.TV]: 'tv',
  [IconEnum.TV_LIVE]: 'live_tv',
  [IconEnum.UPLOAD]: 'cloud_upload',
  [IconEnum.URL]: 'link',
  [IconEnum.USERNAME]: 'android',
  [IconEnum.USERS]: 'people',
  [IconEnum.USER]: 'person',
  [IconEnum.USER_ADD]: 'person_add',
  [IconEnum.VALUE]: 'input',
  [IconEnum.WARNING]: 'warning',
  [IconEnum.WIDGET]: 'widgets',
  [IconEnum.WIDGET_CONFIGURATION]: 'list_alt'
};
