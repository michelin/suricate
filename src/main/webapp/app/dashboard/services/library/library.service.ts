import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

/**
 * Service managing the external JS libraries used by the widgets
 */
@Injectable({ providedIn: 'root' })
export class LibraryService {
  /**
   * List of JS libraries to load for the current project
   */
  public loadedExternalLibraries: string[] = [];

  /**
   * Number of JS libraries to load for the current project
   */
  public numberOfExternalLibrariesToLoad: number;

  /**
   * Are the required JS libraries loaded
   */
  public allExternalLibrariesLoaded = new Subject<boolean>();

  /**
   * Emit a new event according to the given value
   *
   * @param value The new value to emit
   */
  emitAreJSScriptsLoaded(value: boolean) {
    this.allExternalLibrariesLoaded.next(value);
  }

  /**
   * Callback function called when a JS library has been injected in the DOM and loaded from the Back-End.
   * Mark the library linked with the given token as loaded.
   * Then, if all the required JS libraries have been loaded, then emit an event to the subscribers.
   *
   * @param token A token representing a JS library
   */
  public markScriptAsLoaded(token: string) {
    if (this.loadedExternalLibraries.indexOf(token) === -1) {
      this.loadedExternalLibraries.push(token);
    }

    if (this.loadedExternalLibraries.length === this.numberOfExternalLibrariesToLoad) {
      this.emitAreJSScriptsLoaded(true);
    }
  }
}
