/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.actions;

import com.microsoft.embeddedsocial.data.model.AccountDataDifference;
import com.microsoft.embeddedsocial.data.model.CreateAccountData;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.service.ServiceAction;
import com.microsoft.embeddedsocial.service.WorkerService;
import com.microsoft.embeddedsocial.ui.util.SocialNetworkAccount;

import android.content.Context;
import android.os.Bundle;

/**
 * Launches actions.
 */
public final class ActionsLauncher {

    private ActionsLauncher() {
    }

    private static Action createAndStartAction(String tag) {
        Action action = new Action(tag);
        action.start();
        return action;
    }

    /**
     * Builds a service intent and sends it.
     */
    private static final class ActionIntentBuilder {

        private final Bundle extras = new Bundle();
        private final Action action;

        private ActionIntentBuilder(Action action) {
            this.action = action;
            extras.putLong(IntentExtras.ACTION_ID, action.getId());
        }

        ActionIntentBuilder setAuthorization(String authorization) {
            extras.putString(IntentExtras.AUTHORIZATION, authorization);
            return this;
        }

        ActionIntentBuilder setThirdPartyAccount(SocialNetworkAccount thirdPartyAccount) {
            extras.putParcelable(IntentExtras.THIRD_PARTY_ACCOUNT, thirdPartyAccount);
            return this;
        }

        ActionIntentBuilder setUserHandle(String userHandle) {
            extras.putString(IntentExtras.USER_HANDLE, userHandle);
            return this;
        }

        ActionIntentBuilder setCreateAccountData(CreateAccountData createAccountData) {
            extras.putParcelable(IntentExtras.CREATE_ACCOUNT_DATA, createAccountData);
            return this;
        }

        ActionIntentBuilder setAccountDataDifference(AccountDataDifference difference) {
            extras.putParcelable(IntentExtras.ACCOUNT_DATA_DIFFERENCE, difference);
            return this;
        }

        ActionIntentBuilder setCommentHandle(String commentHandle) {
            extras.putString(IntentExtras.COMMENT_HANDLE, commentHandle);
            return this;
        }

        ActionIntentBuilder setReplyHandle(String replyHandle) {
            extras.putString(IntentExtras.REPLY_HANDLE, replyHandle);
            return this;
        }

        Action launch(Context context, ServiceAction serviceAction) {
            WorkerService.getLauncher(context).launchService(serviceAction, extras);
            return action;
        }

        static ActionIntentBuilder forActionWithTag(String tag) {
            Action action = createAndStartAction(tag);
            return new ActionIntentBuilder(action);
        }

    }


}
