/*
 *  /*
 *  * Copyright 2012-2021 the original author or authors.
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

/**
 * Utils class for images
 */
export class ImageUtils {
  /**
   * Get the content type of a base 64 URL
   *
   * @param base64URL The base 64 URL
   */
  public static getContentTypeFromBase64URL(base64URL: string): string {
    return base64URL ? base64URL.split(';base64,')[0].split(':')[1] : null;
  }

  /**
   * Get the data of a base 64 URL
   *
   * @param base64URL The base 64 URL
   */
  public static getDataFromBase64URL(base64URL: string): string {
    return base64URL ? base64URL.split(';base64,')[1] : null;
  }
}
