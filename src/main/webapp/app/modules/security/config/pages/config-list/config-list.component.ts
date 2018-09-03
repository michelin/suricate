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
import {FlatTreeControl} from '@angular/cdk/tree';
import {Component} from '@angular/core';
import {ConfigDb, FileFlatNode, FileNode} from '../../config.db';
import {MatTreeFlatDataSource, MatTreeFlattener} from '@angular/material';
import {Observable, of as observableOf} from 'rxjs/index';

/**
 * @title Tree with nested nodes
 */
@Component({
  selector: 'app-config-list',
  templateUrl: 'config-list.component.html',
  styleUrls: ['config-list.component.css'],
  providers: [ConfigDb]
})
export class ConfigListComponent {

  configTreeControl: FlatTreeControl<FileFlatNode>;
  treeFlattener: MatTreeFlattener<FileNode, FileFlatNode>;
  configDataSource: MatTreeFlatDataSource<FileNode, FileFlatNode>;

  constructor(database: ConfigDb) {
    this.treeFlattener = new MatTreeFlattener(this.transformer, this._getLevel,
        this._isExpandable, this._getChildren);
    this.configTreeControl = new FlatTreeControl<FileFlatNode>(this._getLevel, this._isExpandable);
    this.configDataSource = new MatTreeFlatDataSource(this.configTreeControl, this.treeFlattener);

    database.dataChange.subscribe(data => this.configDataSource.data = data);
  }

  transformer = (node: FileNode, level: number) => {
    return new FileFlatNode(!!node.children, node.filename, level, node.type);
  }

  private _getLevel = (node: FileFlatNode) => node.level;

  private _isExpandable = (node: FileFlatNode) => node.expandable;

  private _getChildren = (node: FileNode): Observable<FileNode[]> => observableOf(node.children);

  hasChild = (_: number, _nodeData: FileFlatNode) => _nodeData.expandable;
}
