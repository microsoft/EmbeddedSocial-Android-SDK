/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.exception;

/**
 *
 */
public class InternalServerException extends StatusException {

    public static final int STATUS_CODE = 500;

    public InternalServerException(String message) {
        super(STATUS_CODE, message);
    }

    public InternalServerException(String message, Throwable cause) {
        super(STATUS_CODE, message, cause);
    }

    public InternalServerException(Throwable cause) {
        super(STATUS_CODE, cause);
    }
}
