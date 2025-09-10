/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.server.exception;

/**
 *
 */
public class ServiceUnavailableException extends StatusException {

    public static final int STATUS_CODE = 503;

    public ServiceUnavailableException(String message) {
        super(STATUS_CODE, message);
    }

    public ServiceUnavailableException(String message, Throwable cause) {
        super(STATUS_CODE, message, cause);
    }

    public ServiceUnavailableException(Throwable cause) {
        super(STATUS_CODE, cause);
    }
}
