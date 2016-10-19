/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.service.handler;

import android.content.Intent;
import android.os.Bundle;

import com.microsoft.socialplus.actions.Action;
import com.microsoft.socialplus.base.GlobalObjectRegistry;
import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.server.IRelationshipService;
import com.microsoft.socialplus.server.SocialPlusServiceProvider;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.relationship.RemoveFollowerRequest;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.service.ServiceAction;

/**
 * Sends a remove follower request to the server.
 */
public class RemoveFollowerHandler extends ActionHandler {

    private final IRelationshipService relationshipService = GlobalObjectRegistry
            .getObject(SocialPlusServiceProvider.class)
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
