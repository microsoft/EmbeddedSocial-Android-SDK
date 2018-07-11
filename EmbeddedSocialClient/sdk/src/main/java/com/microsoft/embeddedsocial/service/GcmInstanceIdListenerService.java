/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service;

import com.google.firebase.iid.FirebaseInstanceIdService;

import com.microsoft.embeddedsocial.gcm.GcmTokenHolder;

/**
 * Listens to InstanceID API callbacks.
 */
public class GcmInstanceIdListenerService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        GcmTokenHolder.create(this).resetToken();
        WorkerService.getLauncher(this).launchService(ServiceAction.GCM_REGISTER);
    }
}
