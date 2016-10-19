/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.event.sync;

import com.microsoft.socialplus.base.event.AbstractEvent;
import com.microsoft.socialplus.base.event.HandlingThread;
import com.microsoft.socialplus.base.event.ThreadType;

/**
 * Is raised when a post is successfully uploaded.
 */
@HandlingThread(ThreadType.MAIN)
public class PostUploadedEvent extends AbstractEvent {  }
