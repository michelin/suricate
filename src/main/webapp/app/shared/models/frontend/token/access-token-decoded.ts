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

import {RoleEnum} from '../../../enums/role.enum';

/**
 * Represent the access token decoded
 */
export interface AccessTokenDecoded {
    /**
     * The subject
     */
    sub: string;

    /**
     * User firstname
     */
    firstname: string;

    /**
     * User mail
     */
    lastname: string;

    /**
     * User mail
     */
    email: string;

    /**
     * The avatar URL
     */
    avatar_url: string;

    /**
     * The authentication method
     */
    mode: string;

    /**
     * The list of roles of the user
     */
    roles: RoleEnum[];

    /**
     * The expiration date as long
     */
    exp: number;
}
