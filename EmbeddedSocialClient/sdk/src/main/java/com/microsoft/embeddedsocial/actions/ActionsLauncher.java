/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.actions;

import android.content.Context;
import android.os.Bundle;

import com.microsoft.embeddedsocial.data.model.AccountDataDifference;
import com.microsoft.embeddedsocial.service.ServiceAction;
import com.microsoft.embeddedsocial.service.WorkerService;
import com.microsoft.embeddedsocial.ui.util.SocialNetworkAccount;
import com.microsoft.embeddedsocial.data.model.CreateAccountData;
import com.microsoft.embeddedsocial.service.IntentExtras;

/**
 * Launches actions.
 */
public final class ActionsLauncher {

	private ActionsLauncher() {
	}

	public static Action signInUsingThirdParty(Context context, SocialNetworkAccount thirdPartyAccount) {
		return ActionIntentBuilder.forActionWithTag(Action.Tags.SIGN_IN)
				.setThirdPartyAccount(thirdPartyAccount)
				.launch(context, ServiceAction.SIGN_IN);
	}

	public static Action getComment(Context context, String commentHandle) {
		return ActionIntentBuilder.forActionWithTag(Action.Tags.GET_COMMENT)
				.setCommentHandle(commentHandle)
				.launch(context, ServiceAction.GET_COMMENT);
	}

	public static Action getReply(Context context, String replyHandle) {
		return ActionIntentBuilder.forActionWithTag(Action.Tags.GET_REPLY)
				.setReplyHandle(replyHandle)
				.launch(context, ServiceAction.GET_REPLY);
	}

	public static Action signOut(Context context, String authorization) {
		return ActionIntentBuilder.forActionWithTag(Action.Tags.SIGN_OUT)
				.setAuthorization(authorization)
				.launch(context, ServiceAction.SIGN_OUT);
	}

	public static Action createAccount(Context context, CreateAccountData createAccountData) {
		return ActionIntentBuilder.forActionWithTag(Action.Tags.CREATE_ACCOUNT)
				.setCreateAccountData(createAccountData)
				.launch(context, ServiceAction.CREATE_ACCOUNT);
	}

	public static Action updateAccount(Context context, AccountDataDifference difference) {
		return ActionIntentBuilder.forActionWithTag(Action.Tags.UPDATE_ACCOUNT)
				.setAccountDataDifference(difference)
				.launch(context, ServiceAction.UPDATE_ACCOUNT);
	}

	public static Action deleteAccount(Context context) {
		return ActionIntentBuilder.forActionWithTag(Action.Tags.DELETE_ACCOUNT).launch(context, ServiceAction.DELETE_ACCOUNT);
	}

	public static Action removeFollower(Context context, String userHandle) {
		return ActionIntentBuilder.forActionWithTag(Action.Tags.REMOVE_FOLLOWER)
				.setUserHandle(userHandle)
				.launch(context, ServiceAction.REMOVE_FOLLOWER);
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
