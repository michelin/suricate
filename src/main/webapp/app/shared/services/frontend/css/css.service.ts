/*
 * Copyright 2012-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Injectable } from '@angular/core';
import { CssSelector } from '../../../models/frontend/css-parser/css-selector';
import { CssRule } from '../../../models/frontend/css-parser/css-rule';

import * as CSSParser from 'jotform-css.js/css.js';

/**
 * The dashboard service
 */
@Injectable({ providedIn: 'root' })
export class CssService {
  /**
   * Constructor
   */
  constructor() {}

  /**
   * Used to parse the css from a string file
   *
   * @param cssContent The css file
   */
  public static getParsedCss(cssContent: string): CssSelector[] {
    if (cssContent) {
      const parser = new CSSParser.cssjs();
      return parser.parseCSS(cssContent);
    }
  }

  /**
   * Extract the value of a property from the css file
   *
   * @param cssContent The css content
   * @param cssSelector The selector of the rule needed to retrieve the property
   * @param propertyName The property that we should use to retrieve the value
   */
  public static extractCssValue(cssContent: string, cssSelector: string, propertyName: string): string {
    let propertyValue = null;

    if (cssContent && cssSelector && propertyName) {
      const cssParsed: CssSelector[] = CssService.getParsedCss(cssContent);

      if (cssParsed) {
        const cssSelectorFound: CssSelector = cssParsed.find((currCssSelector: CssSelector) => currCssSelector.selector === cssSelector);

        if (cssSelectorFound) {
          const cssRule: CssRule = cssSelectorFound.rules.find((currCssRule: CssRule) => currCssRule.directive === propertyName);
          propertyValue = cssRule ? cssRule.value : null;
        }
      }
    }
    return propertyValue;
  }

  /**
   * Function used to build a css file from the css selector list
   *
   * @param cssSelectors The list of selectors
   */
  public static buildCssFile(cssSelectors: CssSelector[]): string {
    let cssFile = '';

    if (cssSelectors && cssSelectors.length > 0) {
      cssSelectors.forEach((cssSelector: CssSelector) => {
        cssFile = cssFile.concat(`${cssSelector.selector} { \n`);
        cssFile = cssFile.concat(CssService.buildCssRuleFile(cssSelector.rules));
        cssFile = cssFile.concat(`} \n\n`);
      });
    }

    return cssFile;
  }

  /**
   * Used to build string properties for a list of css rules
   *
   * @param cssRules The list of rules
   */
  private static buildCssRuleFile(cssRules: CssRule[]): string {
    let cssRuleProperties = '';

    if (cssRules && cssRules.length > 0) {
      cssRules.forEach((cssRule: CssRule) => {
        cssRuleProperties = cssRuleProperties.concat(`\t ${cssRule.directive}: ${cssRule.value}; \n`);
      });
    }

    return cssRuleProperties;
  }

  /**
   * Used to build css selector for grid background color
   *
   * @param backgroundColor The background color in hexa
   */
  public static buildCssGridBackgroundColor(backgroundColor: string): CssSelector {
    return {
      selector: '.grid',
      rules: [
        {
          directive: 'background-color',
          value: backgroundColor
        }
      ]
    };
  }
}
