/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.base.GlobalObjectRegistry;
import com.microsoft.embeddedsocial.event.click.NavigationItemClickedEvent;
import com.microsoft.embeddedsocial.sdk.Options;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.data.Preferences;
import com.microsoft.embeddedsocial.event.data.ProfileDataUpdatedEvent;
import com.microsoft.embeddedsocial.event.data.UpdateNotificationCountEvent;
import com.microsoft.embeddedsocial.image.ImageViewContentLoader;
import com.microsoft.embeddedsocial.sdk.NavigationProfileHelper;
import com.microsoft.embeddedsocial.ui.activity.HomeActivity;
import com.microsoft.embeddedsocial.ui.activity.OptionsActivity;
import com.microsoft.embeddedsocial.ui.activity.PinsActivity;
import com.microsoft.embeddedsocial.ui.activity.PopularActivity;
import com.microsoft.embeddedsocial.ui.activity.RecentActivityActivity;
import com.microsoft.embeddedsocial.ui.activity.SearchActivity;
import com.microsoft.embeddedsocial.ui.activity.base.BaseActivity;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseFragment;
import com.microsoft.embeddedsocial.ui.util.NavigationIntentUtils;
import com.microsoft.embeddedsocial.ui.util.NotificationCountChecker;
import com.microsoft.embeddedsocial.ui.view.NavigationItemView;
import com.squareup.otto.Subscribe;

/**
 * Navigation menu.
 */
public class NavigationFragment extends BaseFragment {

	private static final String CURRENT_ITEM_EXTRA = "currentItem";
	private static final String SHOW_PROFILE_EXTRA = "showProfile";

	private NotificationCountChecker notificationCountChecker;

	private ViewGroup navigationPanel;
	private NavigationItemView notificationsView;
	@SuppressWarnings("FieldCanBeLocal")
	private ImageViewContentLoader photoLoader;

	private int activeItemId;
	private boolean isShowProfile;

	private NavigationIntentUtils navigation;

	public NavigationFragment() {
		addThemeToMerge(R.style.EmbeddedSocialSdkAppTheme_Light);
		addEventListener(new Object() {
			@Subscribe
			public void onUpdateNotificationCount(UpdateNotificationCountEvent event) {
				updateNotificationCount();
			}
		});
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		navigation = new NavigationIntentUtils(getActivity());
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		notificationCountChecker = new NotificationCountChecker(getContext());
		activeItemId = getArguments().getInt(CURRENT_ITEM_EXTRA);
		isShowProfile = getArguments().getBoolean(SHOW_PROFILE_EXTRA);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.es_navigation_menu;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		navigationPanel = findView(view, R.id.es_navigationPanel);
		navigationPanel.setOnTouchListener((v, event) -> true);
		notificationsView = findView(navigationPanel, R.id.es_navigationActivity);
		setupNavigationView();
	}

	private void setupNavigationView() {
		Options options = GlobalObjectRegistry.getObject(Options.class);
		final boolean signedIn = UserAccount.getInstance().isSignedIn();
		setupNavigationItem(R.id.es_navigationHome, navigation::gotoHome,
				signedIn && !BaseActivity.isNavigationDrawerDisabled(HomeActivity.NAME));
		setupNavigationItem(R.id.es_navigationSearch, navigation::gotoSearch,
				options.isSearchEnabled() && !BaseActivity.isNavigationDrawerDisabled(SearchActivity.NAME));
		setupNavigationItem(R.id.es_navigationPopular, navigation::gotoPopular,
				!BaseActivity.isNavigationDrawerDisabled(PopularActivity.NAME));
		setupNavigationItem(R.id.es_navigationPins, navigation::gotoPins,
				signedIn && !BaseActivity.isNavigationDrawerDisabled(PinsActivity.NAME));
		setupNavigationItem(R.id.es_navigationActivity, navigation::gotoActivityFeed,
				signedIn && !BaseActivity.isNavigationDrawerDisabled(RecentActivityActivity.NAME));
		setupNavigationItem(R.id.es_navigationOptions, navigation::gotoOptions,
				!BaseActivity.isNavigationDrawerDisabled(OptionsActivity.NAME));
	}

	private void setupNavigationProfile() {
		if (isShowProfile) {
			photoLoader = NavigationProfileHelper.setupNavigationProfile(
				getActivity(), photoLoader, navigationPanel, activeItemId);
		} else {
			navigationPanel.findViewById(R.id.es_navigationProfile).setVisibility(View.GONE);
		}
	}

	private void setupNavigationItem(@IdRes int viewId, Runnable onClickCallback, boolean visible) {
		NavigationItemView itemView = findView(navigationPanel, viewId);
		if (visible) {
			itemView.setVisibility(View.VISIBLE);
			if (viewId == activeItemId) {
				itemView.setHighlighted(true);
			} else {
				setNavigationCallback(itemView, onClickCallback);
			}
		} else {
			itemView.setVisibility(View.GONE);
		}
	}

	private void setNavigationCallback(View view, Runnable callback) {
		view.setOnClickListener(v -> {
			EventBus.post(new NavigationItemClickedEvent());
			callback.run();
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		notificationCountChecker.onResume();
		updateNotificationCount();
		setupNavigationView();
		setupNavigationProfile();
	}

	@Override
	public void onPause() {
		notificationCountChecker.onPause();
		super.onPause();
	}

	private void updateNotificationCount() {
		notificationsView.setNotificationCount(Preferences.getInstance().getNotificationCount());
	}

	@Override
	public void onStart() {
		super.onStart();
		setupNavigationProfile();
	}

	@Subscribe
	public void onProfileDataUpdated(ProfileDataUpdatedEvent event) {
		if (event.isForUser(UserAccount.getInstance().getUserHandle())) {
			setupNavigationProfile();
		}
	}

	public static NavigationFragment create(@IdRes int activeItemId, boolean isShowProfile) {
		NavigationFragment fragment = new NavigationFragment();
		Bundle arguments = new Bundle();
		arguments.putInt(CURRENT_ITEM_EXTRA, activeItemId);
		arguments.putBoolean(SHOW_PROFILE_EXTRA, isShowProfile);
		fragment.setArguments(arguments);
		return fragment;
	}

	public static NavigationFragment create(@IdRes int activeItemId) {
		return create(activeItemId, true);
	}

}
