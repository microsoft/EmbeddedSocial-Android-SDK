/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.fragment;

import android.content.Context;
import android.os.Bundle;

import com.microsoft.socialplus.base.event.EventBus;
import com.microsoft.socialplus.data.model.AccountData;
import com.microsoft.socialplus.event.data.ProfileDataUpdatedEvent;
import com.microsoft.socialplus.event.relationship.UserFollowedStateChangedEvent;
import com.microsoft.socialplus.fetcher.FetchersFactory;
import com.microsoft.socialplus.fetcher.base.Callback;
import com.microsoft.socialplus.fetcher.base.Fetcher;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.ui.adapter.ProfileInfoAdapter;
import com.microsoft.socialplus.ui.fragment.base.BaseListContentFragment;
import com.squareup.otto.Subscribe;

/**
 * Shows user's profile.
 */
public class ProfileInfoFragment extends BaseListContentFragment<ProfileInfoAdapter> {

	private String userHandle;
	private Fetcher<AccountData> fetcher;

	public ProfileInfoFragment() {
		addThemeToMerge(R.style.SocialPlusSdkThemeOverlayFeed);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		Bundle arguments = getArguments();
		userHandle = arguments.getString(IntentExtras.USER_HANDLE);
	}

	@Override
	protected ProfileInfoAdapter createInitialAdapter() {
		if (fetcher == null) {
			fetcher = FetchersFactory.createProfileFetcher(userHandle);
		}
		ProfileInfoAdapter adapter = new ProfileInfoAdapter(getActivity(), fetcher, userHandle);
		adapter.addFetcherCallback(new Callback() {
			@Override
			public void onDataRequestSucceeded() {
				notifyDataUpdatedIfNeeded();
			}
		});
		return adapter;
	}

	@Override
	public void onResume() {
		super.onResume();
		notifyDataUpdatedIfNeeded();
	}

	protected void notifyDataUpdatedIfNeeded() {
		ProfileInfoAdapter adapter = getAdapter();
		if (adapter.getDataSize() > 0) {
			EventBus.post(new ProfileDataUpdatedEvent(userHandle, adapter.getItem(0)));
		}
	}

	@Subscribe
	public void onFollowedStatusChanged(UserFollowedStateChangedEvent event) {
		if (event.isForUser(userHandle)) {
			getAdapter().onFollowedStatusChanged(event.getFollowedStatus());
		}
	}

	public static ProfileInfoFragment create(String userHandle) {
		ProfileInfoFragment fragment = new ProfileInfoFragment();
		Bundle arguments = new Bundle();
		arguments.putString(IntentExtras.USER_HANDLE, userHandle);
		fragment.setArguments(arguments);
		return fragment;
	}

}
