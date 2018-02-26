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

import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {DashboardService} from '../dashboard.service';
import {Project} from '../../../shared/model/dto/Project';
import {Widget} from '../../../shared/model/dto/Widget';
import {DomSanitizer, SafeHtml, SafeStyle} from '@angular/platform-browser';
import {AbstractHttpService} from '../../../shared/services/abstract-http.service';
import {HeaderDashboardSharedService} from '../../core/header-dashboard-shared.service';

@Component({
  selector: 'app-dashboard-detail',
  templateUrl: './dashboard-detail.component.html',
  styleUrls: ['./dashboard-detail.component.css']
})
export class DashboardDetailComponent implements OnInit {
  public project: Project;
  public gridOptions: {};

  constructor(private route: ActivatedRoute,
              private dashboardService: DashboardService,
              private headerDashboardSharedService: HeaderDashboardSharedService,
              private changeDetectorRef: ChangeDetectorRef,
              private domSanitizer: DomSanitizer) { }

  ngOnInit() {
    this.headerDashboardSharedService.projectDashboardToDisplay.subscribe(project => {
      this.project = project;
    });

    this.route.params.subscribe( params =>
        this.dashboardService
            .getOneById(params['id'])
            .subscribe( project => {
              this.headerDashboardSharedService.projectDashboardToDisplay.next(project);

              this.gridOptions = {
                'max_cols': project.maxColumn,
                'auto_resize': true,
                'maintain_ratio': true,
              };
              this.changeDetectorRef.detectChanges();
            })
    );
  }

  getHtmlFormWidget(widget: Widget): SafeHtml {
    return this.domSanitizer.bypassSecurityTrustHtml(`
      <style>
        ${widget.css}
      </style>
      ${widget.html}
    `);
  }

  getHtmlScriptsFromProject(librariesToken: string[]): SafeHtml {
    let scripts = '';

    for (const libraryToken of librariesToken) {
      scripts = scripts.concat(`
                  <script type="text/javascript" charset="UTF-8"
                          src="${AbstractHttpService.BASE_URL}/${AbstractHttpService.ASSET_URL}/${libraryToken}"></script>
                `);
    }

    return this.domSanitizer.bypassSecurityTrustHtml(scripts);
  }

  getWidgetCommonCSS(): SafeHtml {
    return this.domSanitizer.bypassSecurityTrustHtml(`
      <style>
        .grid-item h1 {
            margin-bottom: 12px;
            text-align: center;
            font-size: 1em;
            font-weight: 400;
            margin-right: 10px;
            margin-left: 10px;
          }
          .grid-item h2 {
            text-transform: uppercase;
            font-size: 3em;
            font-weight: 700;
            color: #fff;
          }
          .grid .widget a {
            text-decoration: none;
          }
          .grid-item p {
            padding: 0;
            margin: 0;
          }
          .grid-item .more-info {
            color: rgba(255, 255, 255, 0.5);
            font-size: 0.6em;
            position: absolute;
            bottom: 32px;
            left: 0;
            right: 0;
          }
          .grid-item .updated-at {
            font-size: 15px;
            position: absolute;
            bottom: 12px;
            left: 0;
            right: 0;
            color: rgba(0, 0, 0, 0.3);
          }
          .grid-item > div {
            position: relative;
            top: 50%;
            transform: translateY(-50%);
          }
        </style>
    `);
  }
}
