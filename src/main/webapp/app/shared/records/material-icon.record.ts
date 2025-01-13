/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { IconEnum } from '../enums/icon.enum';

/**
 * Records used to store related material icons from https://fonts.google.com/icons
 */
type MaterialIcon = Record<string, string>;

export const MaterialIconRecords: MaterialIcon = {
  [IconEnum.ADD]: 'add',
  [IconEnum.ADD_GRID]: 'library_add',
  [IconEnum.BRANCH]: 'merge_type',
  [IconEnum.CATALOG]: 'apps',
  [IconEnum.CATEGORY]: 'category',
  [IconEnum.CLOSE]: 'close',
  [IconEnum.COLUMN]: 'view_column',
  [IconEnum.DASHBOARD]: 'dashboard',
  [IconEnum.DELETE]: 'delete',
  [IconEnum.DELETE_FOREVER]: 'delete_forever',
  [IconEnum.EDIT]: 'edit',
  [IconEnum.EMAIL]: 'email',
  [IconEnum.GRID]: 'grid_view',
  [IconEnum.HEIGHT]: 'height',
  [IconEnum.HELP]: 'help',
  [IconEnum.HIDE_PASSWORD]: 'visibility_off',
  [IconEnum.HOME]: 'home',
  [IconEnum.INFO]: 'format_italic',
  [IconEnum.KEY]: 'vpn_key',
  [IconEnum.LANGUAGE]: 'language',
  [IconEnum.LOGOUT]: 'exit_to_app',
  [IconEnum.NAME]: 'short_text',
  [IconEnum.PASSWORD]: 'lock',
  [IconEnum.PREFERENCES]: 'tune',
  [IconEnum.PROGRESS_BAR]: 'hourglass_bottom',
  [IconEnum.REFRESH]: 'refresh',
  [IconEnum.REPOSITORY]: 'inventory',
  [IconEnum.REPOSITORY_TYPE]: 'cloud_queue',
  [IconEnum.REPOSITORY_PRIORITY]: 'low_priority',
  [IconEnum.SAVE]: 'done',
  [IconEnum.SCREENSHOT]: 'screenshot',
  [IconEnum.SEARCH]: 'search',
  [IconEnum.SHARE_SCREEN]: 'screen_share',
  [IconEnum.SHOW_PASSWORD]: 'visibility',
  [IconEnum.SPEED]: 'speed',
  [IconEnum.STOP_SHARE_SCREEN]: 'stop_screen_share',
  [IconEnum.SUCCESS]: 'done',
  [IconEnum.SYNCHRONIZE]: 'sync',
  [IconEnum.TEXT]: 'notes',
  [IconEnum.THEME]: 'color_lens',
  [IconEnum.TV]: 'tv',
  [IconEnum.TV_LIVE]: 'live_tv',
  [IconEnum.UPLOAD]: 'upload_file',
  [IconEnum.URL]: 'link',
  [IconEnum.USERNAME]: 'face',
  [IconEnum.USERS]: 'people',
  [IconEnum.USER]: 'person',
  [IconEnum.USER_ADD]: 'person_add',
  [IconEnum.VALUE]: 'input',
  [IconEnum.WARNING]: 'warning',
  [IconEnum.WIDGET]: 'widgets',
  [IconEnum.WIDGET_CONFIGURATION]: 'settings'
};
