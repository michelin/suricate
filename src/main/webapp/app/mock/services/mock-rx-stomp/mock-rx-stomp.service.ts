import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { RxStompConfig, StompHeaders } from '@stomp/rx-stomp';

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
  public deactivate(): Promise<void> {
    return Promise.resolve();
  }

  /**
   * Mocked configure method for the unit tests
   *
   * @param rxStompConfig The configuration
   */
  public configure(rxStompConfig: RxStompConfig): void {}
}
