/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Observable, Observer } from 'rxjs';

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
  public static base64ToBlob(base64Data: string, contentType: string, sliceSize?: number): Blob {
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

    return new Blob(byteArrays, { type: contentType });
  }

  /**
   * Convert a blob to a file
   *
   * @param blob The Blob file
   * @param filename The filename
   */
  public static convertBlobToFile(blob: Blob, filename: string): File {
    return new File([blob], filename, { type: blob.type });
  }

  /**
   * Convert a file into base 64
   *
   * @param file The file to convert
   */
  public static convertFileToBase64(file: File): Observable<string | ArrayBuffer> {
    return new Observable((observer: Observer<string | ArrayBuffer>) => {
      const fileReader = new FileReader();

      fileReader.onerror = (err: ProgressEvent<FileReader>) => observer.error(err);
      fileReader.onabort = (err: ProgressEvent<FileReader>) => observer.error(err);
      fileReader.onload = () => {
        if (fileReader.result) {
          observer.next(fileReader.result);
        }
      };
      fileReader.onloadend = () => observer.complete();

      fileReader.readAsDataURL(file);

      return () => {
        fileReader.abort();
      };
    });
  }

  /**
   * Test if the base 64 url is an image
   * @param base64Url
   */
  public static isBase64UrlIsAnImage(base64Url: string) {
    const base64ImagePattern = '^data:image/(gif|jpe?g|png);base64,.+$';
    const regexp = new RegExp(base64ImagePattern);

    return regexp.test(base64Url);
  }
}
