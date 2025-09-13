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

import { Role } from '../../../enums/role';

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
	roles: Role[];

	/**
	 * The expiration date as long
	 */
	exp: number;
}
