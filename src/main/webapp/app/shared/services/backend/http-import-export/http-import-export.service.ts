import {Injectable} from '@angular/core';
import {AbstractHttpService} from "../abstract-http/abstract-http.service";
import {Observable, of} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {HttpFilter} from "../../../models/backend/http-filter";
import {Page} from "../../../models/backend/page";
import {ImportExport} from "../../../models/backend/import-export/import-export";

@Injectable({
  providedIn: 'root'
})
export class HttpImportExportService implements AbstractHttpService<ImportExport> {
  /**
   * Global endpoint for exports
   */
  private static readonly importExportApiEndpoint = `${AbstractHttpService.baseApiEndpoint}/v1`;

  /**
   * Constructor
   * @param httpClient the http client to inject
   */
  constructor(private readonly httpClient: HttpClient) { }

  /**
   * Create an export of app data
   */
  public exportData(): Observable<ImportExport> {
    const url = `${HttpImportExportService.importExportApiEndpoint}/export`;

    return this.httpClient.get<ImportExport>(url, {});
  }

  /**
   * Import data from the exported json file
   *
   * @param importExport The data to import
   */
  public importDashboard(importExport: ImportExport): Observable<void> {
    const url = `${HttpImportExportService.importExportApiEndpoint}/import`;

    return this.httpClient.post<void>(url, importExport);
  }

  public create(): Observable<ImportExport> {
    return of();
  }

  delete(id: number | string): Observable<void> {
    return undefined;
  }

  getAll(filter: HttpFilter | undefined): Observable<(ImportExport)[] | Page<ImportExport>> {
    return of([]);
  }

  getById(id: number | string): Observable<ImportExport> {
    return undefined;
  }

  update(id: number | string, entity: ImportExport): Observable<void> {
    return undefined;
  }
}
