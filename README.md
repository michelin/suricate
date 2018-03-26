# Suricate [![Open Source Love](https://badges.frapsoft.com/os/v3/open-source-150x25.png?v=103)](https://github.com/ellerbrock/open-source-badges/)

[![Build Status](https://travis-ci.org/suricate-io/suricate.svg?branch=master)](https://travis-ci.org/suricate-io/suricate)
[![codecov](https://codecov.io/gh/suricate-io/suricate/branch/master/graph/badge.svg)](https://codecov.io/gh/suricate-io/suricate)
[![Maintainability](https://api.codeclimate.com/v1/badges/093032ef74459c9f8a44/maintainability)](https://codeclimate.com/github/suricate-io/suricate/maintainability)
[![GitHub issues](https://img.shields.io/github/issues/suricate-io/suricate.svg)](https://github.com/suricate-io/suricate/issues/)
[![GitHub PR](https://img.shields.io/github/issues-pr/suricate-io/suricate.svg)](https://github.com/suricate-io/suricate/pulls/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## Global information

:construction_worker: **Work in progress... First release in plan for June 2018 but feel free to help** :construction_worker:

## Contribution :beers:

Everyone is welcome to contribute to Suricate. You have several possibilities:

* Report a [bug](https://github.com/suricate-io/suricate/issues/new?template=bug.md)
* Suggest a [feature](https://github.com/suricate-io/suricate/issues/new?template=improvement.md)
* Developp a feature and create a [pull request](https://github.com/suricate-io/suricate/pulls)

## Installation

### Backend application

1. Download and install [maven](https://maven.apache.org/download.cgi)
2. Run the following commands

```bash
### Under "suricate" folder
## Switch to backend project
$ cd backend

## Create the binary
$ mvn package

## Run project
# Default environment
$ java -jar ./target/monitoring.jar
# Dev environment
$ java -jar -Dspring.profiles.active=dev ./target/monitoring.jar
```

### Frontend application

1. Download and install [NodeJs](https://nodejs.org/en/download/) (this soft allow you to use NPM packages)
2. Run the following commands

```bash
### Under "suricate" folder
## Switch to frontend project
$ cd frontend

## import dependencies
$ npm install

## Run the project
$ npm start
```

The application is accessible on <http://localhost:4200/>  
If you have no access create a new account on the application.

## Configuration

With the default Spring profile the backend

* use an H2 in memory database
* load all widgets from GitHub
* Use Database user access

### Development

In development you can override some configurations :

* Database
* Authentication provider
* Widget folder path

#### 1. Code formatting

Follow the style you see used in the primary repository!  
Consistency with the rest of the project always trumps other considerations. It doesn’t matter if you have your own style or if the rest of the code breaks with the greater community - just follow along.

Don't forget to include licence header in **all** files

```JAVA
 /*
 * Copyright 2012-2018 the original author or authors.
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

##### Automatically add the licence with IntelliJ

1. In the Settings/Preferences dialog (Ctrl+Alt+S), click Copyright under Editor, and then click Copyright Profiles. The Copyright Profiles page opens.

2. Click on the **Add Button** :
    * Choose a name : Apache 2
    * Under Copyright text copy/paste the licence text above
    * Then click on **Apply**

3. Then click **Copyright** and select the Apache 2 copright as **Default** and click on Apply.

4. Verify that you have (under Copyright > Formatting) for Java, JavaScript and TypeScript **Use default settings** select.

#### 2. Database

With the default properties an H2-Console is available :

* URL : *<http://localhost:8080/h2-console>*
* Database access : *jdbc:h2:mem:dev;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE*
* Database Username : *sa*
* Database Password :

You can override database properties by uncommenting and filling the following lines (file : resources/application-dev.yml)

```YAML
# datasource:
#   url:
#   driverClassName:
#   username:
#   password:
```

#### 3. Authentication provider

Two types of authentication providers has been implemented : LDAP and DATABASE  
By default authentication is set to DATABASE

You can override these properties by changing *database* to *ldap* in the following lines (file : resources/application-dev.yml) and by uncommenting and filling the ldap properties

``` YAML
### Security (ldap | database) ###
provider: database
###   LDAP Authentication   ###
# ldap:
#  url:
#  userSearchFilter:
#  firstNameAttributName:
#  lastNameAttributName:
#  mailAttributName:
```

#### 4. Widgets Management

In development it could be easier to have the Widget repository in local follow the steps if you want to manage it locally :

1. Clone the repo from GitHub : *[Widget Repository](https://github.com/suricate-io/widgets)*
2. Open the file in backend project located at : *resources/application-dev.yml*
3. Add the Widget Repository path to the following line : *widgets.local.folderPath:*

```YAML
#### Example :
widgets.local.folderPath: C:\\my\\widget\\folder\\path
```

##### Management Rules

When launching the backend with *-Dspring.profiles.active=#env#* argument

1. If *widgets.local.folderPath* in *resources/application-#env#.yml* is not blank, the repository used is the local one
2. Else we get the repository from GitHub
