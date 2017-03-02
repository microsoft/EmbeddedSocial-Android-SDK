/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.handler;

import android.content.Intent;
import android.os.Bundle;

import com.microsoft.embeddedsocial.actions.Action;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.server.IRelationshipService;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.relationship.RemoveFollowerRequest;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.service.ServiceAction;

/**
 * Sends a remove follower request to the server.
 */
public class RemoveFollowerHandler extends ActionHandler {

    private final IRelationshipService relationshipService = GlobalObjectRegistry
            .getObject(EmbeddedSocialServiceProvider.class)
            .getRelationshipService();

    public RemoveFollowerHandler() { }

    @Override
    protected void handleAction(Action action, ServiceAction serviceAction, Intent intent) {
        Bundle extras = intent.getExtras();
        String userHandle = extras.getString(IntentExtras.USER_HANDLE);

        RemoveFollowerRequest removeFollowerRequest = new RemoveFollowerRequest(userHandle);
        try {
            relationshipService.removeFollower(removeFollowerRequest);
        } catch (NetworkRequestException e) {
            DebugLog.logException(e);
            action.fail();
        }
    }

    @Override
    public void dispose() {

    }
}
