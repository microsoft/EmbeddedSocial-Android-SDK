/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.actions;

import android.content.Context;
import android.os.Bundle;

import com.microsoft.socialplus.data.model.AccountDataDifference;
import com.microsoft.socialplus.data.model.CreateAccountData;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.service.ServiceAction;
import com.microsoft.socialplus.service.WorkerService;
import com.microsoft.socialplus.ui.util.SocialNetworkAccount;

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

	public static Action signOut(Context context) {
		return ActionIntentBuilder.forActionWithTag(Action.Tags.SIGN_OUT)
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
