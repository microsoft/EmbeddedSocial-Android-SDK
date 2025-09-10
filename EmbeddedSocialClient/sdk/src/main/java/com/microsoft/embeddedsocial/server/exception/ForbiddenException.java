/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.exception;

/**
 * Server exceptions in case resource not found
 */
public class ForbiddenException extends StatusException {
    public static final int STATUS_CODE = 403;

    public ForbiddenException(String message) {
        super(STATUS_CODE, message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(STATUS_CODE, message, cause);
    }

    public ForbiddenException(Throwable cause) {
        super(STATUS_CODE, cause);
    }

}
