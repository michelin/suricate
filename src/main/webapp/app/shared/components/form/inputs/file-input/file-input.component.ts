import {Component, OnInit} from '@angular/core';
import {InputComponent} from '../input.component';
import {animate, style, transition, trigger} from '@angular/animations';
import {FileUtils} from '../../../../utils/FileUtils';

/**
 * Component that manage the file input
 */
@Component({
  selector: 'app-file-input',
  templateUrl: './file-input.component.html',
  styleUrls: ['./file-input.component.scss'],
  animations: [
    trigger('animationError', [
      transition(':enter', [
        style({opacity: 0, transform: 'translateY(-100%)'}),
        animate('300ms cubic-bezier(0.55, 0, 0.55, 0.2)', style({opacity: 1, transform: 'translateY(0%)'})),
      ]),
    ])
  ]
})
export class FileInputComponent extends InputComponent implements OnInit {

  /**
   * The image as base 64
   */
  imgBase64: string | ArrayBuffer;

  /**
   * If it's not an image we set the filename
   */
  filename: string;

  /**
   * Constructor
   */
  constructor() {
    super();
  }

  /**
   * When the component is init
   */
  ngOnInit(): void {
    this.setBase64Image(this.field.value);
  }

  setBase64Image(base64Url?, filename?: string) {
    if (FileUtils.isBase64UrlIsAnImage(base64Url)) {
      this.imgBase64 = base64Url;
      this.filename = null;
    } else {
      this.imgBase64 = null;
      this.filename = filename;
    }
  }

  /**
   * Convert a file into base64
   *
   * @param event The change event
   */
  convertFileBase64(event) {
    if (event.target.files && event.target.files.length > 0) {
      const file: File = event.target.files[0];

      FileUtils.convertFileToBase64(file).subscribe(base64Url => {
        this.setBase64Image(base64Url, file.name);
        super.getFormControl().setValue(base64Url);
        super.getFormControl().markAsDirty();
      });
    }

    super.getFormControl().markAsTouched();
  }

  /**
   * {@inheritDoc}
   */
  isInputFieldOnError(): boolean {
    return super.isInputFieldOnError();
  }
}
