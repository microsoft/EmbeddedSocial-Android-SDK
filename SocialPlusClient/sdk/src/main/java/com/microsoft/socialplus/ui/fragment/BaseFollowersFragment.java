/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.fragment;

import android.text.TextUtils;

import com.microsoft.socialplus.account.UserAccount;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.ui.adapter.renderer.UserRenderer;
import com.microsoft.socialplus.ui.fragment.base.BaseUsersListFragment;

/**
 * Base functionality for following/followers fragments.
 */
public abstract class BaseFollowersFragment extends BaseUsersListFragment {

	@Override
	protected UserRenderer createRenderer() {
		return new UserRenderer(getContext());
	}

	protected String getUserHandleArgument() {
		String userHandle = getActivity().getIntent().getStringExtra(IntentExtras.USER_HANDLE);
		return TextUtils.isEmpty(userHandle) ? UserAccount.getInstance().getUserHandle() : userHandle;
	}
}
