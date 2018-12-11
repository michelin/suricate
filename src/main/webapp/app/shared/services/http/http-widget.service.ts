import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ApiActionEnum} from '../../model/api/enums/ApiActionEnum';
import {Observable} from 'rxjs';
import {Widget} from '../../model/api/Widget';
import {categoriesApiEndpoint, widgetsApiEndpoint} from '../../../app.constant';
import {tap} from 'rxjs/operators';
import {WidgetService} from '../../../modules/widget/widget.service';

@Injectable()
export class HttpWidgetService {

  /**
   * Constructor
   *
   * @param {HttpClient} httpClient The http client service
   * @param {WidgetService} widgetService The widget service to inject
   */
  constructor(private httpClient: HttpClient,
              private widgetService: WidgetService) {
  }

  /**
   * Get the list of widgets
   *
   * @param {ApiActionEnum} action Action to be executed by the backend
   * @returns {Observable<Widget[]>} The list of widgets as observable
   */
  getAll(action?: ApiActionEnum): Observable<Widget[]> {
    let url = `${widgetsApiEndpoint}`;
    if (action) {
      url = url.concat(`?action=${action}`);
    }

    return this.httpClient.get<Widget[]>(url).pipe(
      tap(widgets => {
        if (action && action === ApiActionEnum.REFRESH) {
          this.widgetService.widgets = widgets;
        }
      })
    );
  }

  /**
   * Update a widget
   *
   * @param {Widget} widget The widget to update
   * @returns {Observable<Widget>} The widget updated
   */
  updateWidget(widget: Widget): Observable<Widget> {
    const url = `${widgetsApiEndpoint}/${widget.id}`;

    return this.httpClient.post<Widget>(url, widget);
  }

  /**
   * Get every widget for a category
   *
   * @param {number} categoryId The category id
   * @returns {Observable<Widget[]>} The widgets as observable
   */
  getWidgetsByCategoryId(categoryId: number): Observable<Widget[]> {
    const url = `${categoriesApiEndpoint}/${categoryId}/widgets`;

    return this.httpClient.get<Widget[]>(url);
  }
}