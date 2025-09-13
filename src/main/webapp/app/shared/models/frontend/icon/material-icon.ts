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

import { Icon } from '../../../enums/icon';

/**
 * Records used to store related material icons from https://fonts.google.com/icons
 */
type MaterialIcon = Record<string, string>;

export const MaterialIconRecords: MaterialIcon = {
	[Icon.ADD]: 'add',
	[Icon.ADD_GRID]: 'library_add',
	[Icon.BRANCH]: 'merge_type',
	[Icon.CATALOG]: 'apps',
	[Icon.CATEGORY]: 'category',
	[Icon.CLOSE]: 'close',
	[Icon.COLUMN]: 'view_column',
	[Icon.COPY]: 'content_copy',
	[Icon.DASHBOARD]: 'dashboard',
	[Icon.DELETE]: 'delete',
	[Icon.DELETE_FOREVER]: 'delete_forever',
	[Icon.EDIT]: 'edit',
	[Icon.EMAIL]: 'email',
	[Icon.GRID]: 'grid_view',
	[Icon.HEIGHT]: 'height',
	[Icon.HELP]: 'help',
	[Icon.HIDE_PASSWORD]: 'visibility_off',
	[Icon.HOME]: 'home',
	[Icon.INFO]: 'format_italic',
	[Icon.KEY]: 'vpn_key',
	[Icon.LANGUAGE]: 'language',
	[Icon.LOGOUT]: 'exit_to_app',
	[Icon.NAME]: 'short_text',
	[Icon.PASSWORD]: 'lock',
	[Icon.PREFERENCES]: 'tune',
	[Icon.PROGRESS_BAR]: 'hourglass_bottom',
	[Icon.REFRESH]: 'refresh',
	[Icon.REPOSITORY]: 'inventory',
	[Icon.REPOSITORY_TYPE]: 'cloud_queue',
	[Icon.REPOSITORY_PRIORITY]: 'low_priority',
	[Icon.SAVE]: 'done',
	[Icon.SCREENSHOT]: 'screenshot',
	[Icon.SEARCH]: 'search',
	[Icon.SHARE_SCREEN]: 'screen_share',
	[Icon.SHOW_PASSWORD]: 'visibility',
	[Icon.SPEED]: 'speed',
	[Icon.STOP_SHARE_SCREEN]: 'stop_screen_share',
	[Icon.SUCCESS]: 'done',
	[Icon.SYNCHRONIZE]: 'sync',
	[Icon.TEXT]: 'notes',
	[Icon.THEME]: 'color_lens',
	[Icon.TV]: 'tv',
	[Icon.TV_LIVE]: 'live_tv',
	[Icon.UPLOAD]: 'upload_file',
	[Icon.URL]: 'link',
	[Icon.USERNAME]: 'face',
	[Icon.USERS]: 'people',
	[Icon.USER]: 'person',
	[Icon.USER_ADD]: 'person_add',
	[Icon.VALUE]: 'input',
	[Icon.WARNING]: 'warning',
	[Icon.WIDGET]: 'widgets',
	[Icon.WIDGET_CONFIGURATION]: 'settings'
};
