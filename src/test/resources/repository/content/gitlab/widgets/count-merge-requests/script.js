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

function run() {
  var perPage = 100;
  var data = {};
  var mergeRequests = [];
  var page = 1;

  data.project = JSON.parse(
    Packages.get(WIDGET_CONFIG_GITLAB_URL + '/api/v4/projects/' + SURI_PROJECT, 'PRIVATE-TOKEN', WIDGET_CONFIG_GITLAB_TOKEN)
  ).name;

  var response = JSON.parse(
    Packages.get(
      WIDGET_CONFIG_GITLAB_URL +
        '/api/v4/projects/' +
        SURI_PROJECT +
        '/merge_requests?per_page=' +
        perPage +
        '&page=' +
        page +
        '&state=' +
        SURI_MR_STATE,
      'PRIVATE-TOKEN',
      WIDGET_CONFIG_GITLAB_TOKEN
    )
  );

  mergeRequests = mergeRequests.concat(response);

  while (response && response.length > 0 && response.length === perPage) {
    page++;

    response = JSON.parse(
      Packages.get(
        WIDGET_CONFIG_GITLAB_URL +
          '/api/v4/projects/' +
          SURI_PROJECT +
          '/merge_requests?per_page=' +
          perPage +
          '&page=' +
          page +
          '&state=' +
          SURI_MR_STATE,
        'PRIVATE-TOKEN',
        WIDGET_CONFIG_GITLAB_TOKEN
      )
    );

    mergeRequests = mergeRequests.concat(response);
  }

  data.numberOfMRs = mergeRequests.length;

  if (SURI_PREVIOUS && JSON.parse(SURI_PREVIOUS).numberOfMRs) {
    data.evolution = (((data.numberOfMRs - JSON.parse(SURI_PREVIOUS).numberOfMRs) * 100) / JSON.parse(SURI_PREVIOUS).numberOfMRs).toFixed(
      1
    );
    data.arrow = data.evolution == 0 ? '' : data.evolution > 0 ? 'up' : 'down';
  }

  if (SURI_MR_STATE != 'all') {
    data.mrsState = SURI_MR_STATE;
  }

  return JSON.stringify(data);
}
