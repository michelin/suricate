import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

/**
 * Library Service
 */
@Injectable({ providedIn: 'root' })
export class LibraryService {
  /**
   * Are the required JS libraries loaded
   */
  public areJSScriptsLoaded = new Subject<boolean>();

  /**
   * Emit a new event according to the given value
   *
   * @param value The new value to emit
   */
  emitAreJSScriptsLoaded(value: boolean) {
    this.areJSScriptsLoaded.next(value);
  }
}
