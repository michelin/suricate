import { Injectable } from '@angular/core';
import { StompHeaders } from '@stomp/ng2-stompjs/src/stomp-headers';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { RxStompConfig } from '@stomp/rx-stomp/esm5/rx-stomp-config';
import { RxStompState } from '@stomp/rx-stomp/esm5/rx-stomp-state';

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

  /**
   * Mocked configure method for the unit tests
   *
   * @param rxStompConfig The configuration
   */
  public configure(rxStompConfig: RxStompConfig): void {}
}
