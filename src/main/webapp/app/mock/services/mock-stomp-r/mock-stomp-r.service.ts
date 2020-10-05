import { Injectable } from '@angular/core';
import { StompHeaders } from '@stomp/ng2-stompjs/src/stomp-headers';
import { Observable, of } from 'rxjs';
import { Message } from '@stomp/stompjs';

@Injectable({
  providedIn: 'root'
})
export class MockStompRService {
  /**
   * Constructor
   */
  constructor() {}

  /**
   * Mocked initAndConnect method for the unit tests
   */
  public initAndConnect(): void {}

  /**
   * Mocked subscribe method for the unit tests
   *
   * @param queueName The name of the queue
   * @param headers Any optional headers
   */
  public subscribe(queueName: string, headers?: StompHeaders): Observable<void> {
    return of();
  }
}
