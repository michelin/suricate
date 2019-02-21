/*
 *  /*
 *  * Copyright 2012-2018 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

import {Observable} from 'rxjs';

/**
 * Utils class for images
 */
export class FileUtils {

  /**
   * Function that transform base64URL into a Blob
   *
   * @param base64Data The base 64 data in the url
   * @param contentType The img content type
   * @param sliceSize Buffer size
   */
  static base64ToBlob(base64Data: string, contentType: string, sliceSize?: number): Blob {
    contentType = contentType || '';
    sliceSize = sliceSize || 512;

    const byteCharacters = atob(base64Data);
    const byteArrays = [];

    for (let offset = 0; offset < byteCharacters.length; offset += sliceSize) {
      const slice = byteCharacters.slice(offset, offset + sliceSize);

      const byteNumbers = new Array(slice.length);
      for (let i = 0; i < slice.length; i++) {
        byteNumbers[i] = slice.charCodeAt(i);
      }

      const byteArray = new Uint8Array(byteNumbers);

      byteArrays.push(byteArray);
    }

    return new Blob(byteArrays, {type: contentType});
  };

  /**
   * Convert a blob to a file
   *
   * @param blob The Blob file
   * @param filename The filename
   * @param lastModifiedDate The last modified date
   */
  static convertBlobToFile(blob: Blob, filename: string, lastModifiedDate: Date): File {
    const file: any = blob;
    file.lastModified = lastModifiedDate;
    file.name = filename;

    return file as File;
  }

  /**
   * Convert a file into into base 64
   *
   * @param file The file to convert
   */
  static convertFileToBase64(file: File): Observable<string | ArrayBuffer> {
    return Observable.create( observable => {
      const fileReader = new FileReader();

      fileReader.onerror = err => observable.error(err);
      fileReader.onabort = err => observable.error(err);
      fileReader.onload = () => observable.next(fileReader.result);
      fileReader.onloadend = () => observable.complete();

      return fileReader.readAsDataURL(file);
    });
  }

}
