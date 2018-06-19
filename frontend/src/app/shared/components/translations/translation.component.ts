import {Component, Input} from "@angular/core";
import {TranslateService} from "@ngx-translate/core";

@Component({
    selector: 'app-translate',
    templateUrl: './translation.component.html'
})
export class TranslationComponent {

    @Input() key: string;
}