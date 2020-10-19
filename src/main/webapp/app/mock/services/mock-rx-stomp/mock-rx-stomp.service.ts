import { Injectable } from '@angular/core';
import { StompHeaders } from '@stomp/ng2-stompjs/src/stomp-headers';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MockRxStompService {
  /**
   * Constructor
   */
  constructor() {}

  /**
   * Mocked initAndConnect method for the unit tests
   */
  public activate(): void {}

  /**
   * Mocked subscribe method for the unit tests
   *
   * @param queueName The name of the queue
   * @param headers Any optional headers
   */
  public watch(queueName: string, headers?: StompHeaders): Observable<void> {
    return of();
  }

  /**
   * Mocked disconnect method for the unit tests
   */
  public deactivate(): void {}
}
