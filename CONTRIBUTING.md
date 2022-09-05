## How to contribute to Suricate

[How Can I Contribute?](#how-to-contribute)

- [Reporting Bugs](#reporting-bugs)
- [Pull Requests](#pull-requests)
- [Suggesting Enhancements](#suggesting-enhancements)
- [Your First Code Contribution](#your-first-code-contribution)

[Styleguides](#styleguides)

- [Git Commit Messages](#git-commit-messages)
- [Quick Coding Guide](#quick-coding-guide)

### How to contribute

#### Suggest an improvement

To suggest a new feature on suricate, [open a ticket](https://github.com/michelin/suricate/issues/new?template=improvement.md) and fill required information

#### Reporting bugs

- **Ensure the bug was not already reported** by searching on GitHub under [Issues](https://github.com/michelin/suricate/issues).

- Perform basic troubleshooting steps:

  - Make sure you’re on the **latest version**. If you’re not on the most recent version, your problem may have been solved already! Upgrading is always the best first step.
  - **Try older versions**. If you’re already on the latest release, try rolling back a few minor versions (e.g. if on 1.7, try 1.5 or 1.6) and see if the problem goes away. This will help the devs narrow down when the problem first arose in the commit log.
  - (optionally) Try **switching dependency versions**. If the software in question has dependencies (other libraries, etc) try upgrading/downgrading those as well.

- If you're unable to find an open issue addressing the problem, [open a new one](https://github.com/michelin/suricate/issues/new?template=bug.md). Be sure to include a **title and clear description**, as much relevant information as possible, and a **code sample** or an **executable test case** demonstrating the expected behavior that is not occurring.

- If possible, use the relevant bug report templates to create the issue.

#### Pull requests

- Open a new GitHub pull request with the patch. Pull requests are displayed in the release notes, be sure to:
  - Use the past tense ("Added new feature...", "Fixed bug on...")
  - Add tags to the PR ("documentation" for documentation updates, "bug" for bug fixing, ...)

### Styleguides

#### Git commit messages

- Use the present tense ("Add feature" not "Added feature")
- Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
- Limit the first line to 72 characters or less
- Reference issues and pull requests liberally after the first line
- When only changing documentation, include `[ci skip]` in the commit title
- Consider starting the commit message with an applicable emoji:
  - :art: `:art:` when improving the format/structure of the code
  - :racehorse: `:racehorse:` when improving performance
  - :memo: `:memo:` when writing docs
  - :bug: `:bug:` when fixing a bug
  - :fire: `:fire:` when removing code or files
  - :green_heart: `:green_heart:` when fixing the CI build
  - :white_check_mark: `:white_check_mark:` when adding tests
  - :lock: `:lock:` when dealing with security
  - :arrow_up: `:arrow_up:` when upgrading dependencies
  - :arrow_down: `:arrow_down:` when downgrading dependencies
  - :shirt: `:shirt:` when removing linter warnings

#### Quick Coding Guide

##### Code formatting

Follow the style you see used in the primary repository! Consistency with the rest of the project always trumps other considerations. It doesn’t matter if you have your own style or if the rest of the code breaks with the greater community - just follow along.

Don't forget to include licence header in **all** files

```
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
```

##### Documentation isn’t optional

It’s not! Patches without documentation will be returned to sender. By “documentation” we mean:

Don’t forget to include versionadded/versionchanged ReST directives at the bottom of any new or changed Python docstrings!

New features should ideally include updates to prose documentation, including useful example code snippets.

All submissions should have a changelog entry crediting the contributor and/or any individuals instrumental in identifying the problem.

##### Tests aren’t optional

Any bugfix that doesn’t include a test proving the existence of the bug being fixed, may be suspect. Ditto for new features that can’t prove they actually work.

We’ve found that test-first development really helps make features better architected and identifies potential edge cases earlier instead of later. Writing tests before the implementation is strongly encouraged.
