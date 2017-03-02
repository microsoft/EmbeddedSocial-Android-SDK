/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server;

import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;

/**
 * Can be used as a reference to a server method.
 *
 * @param <P> method parameter type
 * @param <R> return value type
 */
public interface ServerMethod<P, R> {
	R call(P parameter) throws NetworkRequestException;
}
