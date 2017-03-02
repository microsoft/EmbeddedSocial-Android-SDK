/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.service.handler;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.microsoft.embeddedsocial.autorest.models.ImageType;
import com.microsoft.embeddedsocial.autorest.models.Visibility;
import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.actions.Action;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.model.AccountData;
import com.microsoft.embeddedsocial.data.model.AccountDataDifference;
import com.microsoft.embeddedsocial.server.IAccountService;
import com.microsoft.embeddedsocial.server.ImageUploader;
import com.microsoft.embeddedsocial.server.EmbeddedSocialServiceProvider;
import com.microsoft.embeddedsocial.server.exception.NetworkRequestException;
import com.microsoft.embeddedsocial.server.model.account.UpdateUserPhotoRequest;
import com.microsoft.embeddedsocial.server.model.account.UpdateUserPublicAccountInfoRequest;
import com.microsoft.embeddedsocial.server.model.account.UpdateUserVisibilityRequest;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.service.ServiceAction;

import java.io.IOException;

/**
 * Updates account.
 */
public class UpdateAccountHandler extends ActionHandler {

	private final Context context;
	private final IAccountService server = GlobalObjectRegistry.getObject(EmbeddedSocialServiceProvider.class).getAccountService();

	public UpdateAccountHandler(Context context) {
		this.context = context;
	}

	@Override
	protected void handleAction(Action action, ServiceAction serviceAction, Intent intent) {
		Bundle extras = intent.getExtras();
		AccountDataDifference difference = extras.getParcelable(IntentExtras.ACCOUNT_DATA_DIFFERENCE);
		AccountData accountData = UserAccount.getInstance().getAccountDetails();
		try {
			updatePhotoIfNeeded(difference, accountData);
			updatePublicInfoIfNeeded(difference, accountData);
			updatePrivacyIfNeeded(difference, accountData);
		} catch (IOException | NetworkRequestException e) {
			DebugLog.logException(e);
			action.fail();
		} finally {
			UserAccount.getInstance().updateAccountDetails(accountData);
		}
	}

	private void updatePrivacyIfNeeded(AccountDataDifference difference, AccountData accountData) throws NetworkRequestException {
		if (difference.isPrivacyChanged()) {
			Visibility visibility = difference.isPrivate() ? Visibility.PRIVATE : Visibility.PUBLIC;
			server.updateUserVisibility(new UpdateUserVisibilityRequest(visibility));
			accountData.setIsPrivate(difference.isPrivate());
		}
	}

	private void updatePublicInfoIfNeeded(AccountDataDifference difference, AccountData accountData) throws NetworkRequestException {
		if (difference.isPublicInfoChanged()) {
			server.updateUserPublicAccountInfo(new UpdateUserPublicAccountInfoRequest(
				difference.getFirstName(),
				difference.getLastName(),
				difference.getBio()));
			accountData.setFirstName(difference.getFirstName());
			accountData.setLastName(difference.getLastName());
			accountData.setBio(difference.getBio());
		}
	}

	private void updatePhotoIfNeeded(AccountDataDifference difference, AccountData accountData) throws IOException, NetworkRequestException {
		if (difference.isPhotoUriChanged()) {
			Uri photoUri = difference.getPhotoUri();
			String photoUrl = photoUri != null ? ImageUploader.uploadImage(context, photoUri, ImageType.USERPHOTO) : null;
			server.updateUserPhoto(new UpdateUserPhotoRequest(photoUrl));
			accountData.setUserPhotoUrl(photoUrl);
		}
	}

}
