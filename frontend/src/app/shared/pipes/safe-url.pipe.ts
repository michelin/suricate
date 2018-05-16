import { Pipe, PipeTransform } from '@angular/core';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';

@Pipe({
  name: 'safeUrl'
})
export class SafeUrlPipe implements PipeTransform {

  constructor(private domSanitizer: DomSanitizer) {}

  transform(valueToSanitize: string): SafeHtml {
    return this.domSanitizer.bypassSecurityTrustUrl(valueToSanitize);
  }

}
