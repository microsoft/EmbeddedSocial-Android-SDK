/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.activity.base;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.microsoft.socialplus.account.UserAccount;
import com.microsoft.socialplus.base.event.EventBus;
import com.microsoft.socialplus.base.function.Producer;
import com.microsoft.socialplus.data.model.AccountData;
import com.microsoft.socialplus.data.model.TopicFeedType;
import com.microsoft.socialplus.event.data.ProfileDataUpdatedEvent;
import com.microsoft.socialplus.sdk.BuildConfig;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.ui.activity.AddPostActivity;
import com.microsoft.socialplus.ui.fragment.FeedViewMenuListenerFragment;
import com.microsoft.socialplus.ui.fragment.ProfileFeedFragment;
import com.microsoft.socialplus.ui.fragment.ProfileInfoFragment;
import com.microsoft.socialplus.ui.util.ContentUpdateHelper;
import com.microsoft.socialplus.ui.util.SimplePagerAdapter;
import com.squareup.otto.Subscribe;

/**
 * Base class for activities showing a profile.
 */
public abstract class BaseProfileActivity extends BaseTabsActivity {
	private String userHandle;
	private String userName;
	private boolean isCurrentUser;
	private boolean feedIsReadable;

	private final Object eventListener = new Object() {
		@Subscribe
		public void onProfileLoaded(ProfileDataUpdatedEvent event) {
			if (event.isForUser(userHandle)) {
				AccountData accountData = event.getAccountData();
				userName = accountData.getFullName();
				if (!isCurrentUser && feedIsReadable != accountData.arePostsReadable()) {
					feedIsReadable = accountData.arePostsReadable();
					notifyTabsChanged();
					setTabsIndicatorVisible(feedIsReadable);
				}
				ActionBar actionBar = getSupportActionBar();
				if (actionBar != null) {
					actionBar.setTitle(event.getAccountData().getFullName());
				}
			}
		}
	};

	protected BaseProfileActivity() {
	}

	protected BaseProfileActivity(int activeNavigationItemId) {
		super(activeNavigationItemId);
	}

	@Override
	protected void setupLayout() {
		initExtraVariables();

		super.setupLayout();

		setTabsIndicatorVisible(feedIsReadable);
	}

	/**
	 * Init variables from the start intent.
	 */
	protected abstract void initExtraVariables();

	protected void setUserHandle(String userHandle) {
		this.userHandle = userHandle;
	}

	protected void setUserName(String userName) {
		this.userName = userName;
	}

	protected void setIsCurrentUser(boolean isCurrentUser) {
		this.isCurrentUser = isCurrentUser;
	}

	protected void setFeedIsReadable(boolean feedIsReadable) {
		this.feedIsReadable = feedIsReadable;
	}

	@Override
	protected PagerAdapter createPagerAdapter() {
		SimplePagerAdapter.Page[] pages = new SimplePagerAdapter.Page[]{
			new SimplePagerAdapter.Page(R.string.sp_profile, () -> ProfileInfoFragment.create(userHandle)),
			new SimplePagerAdapter.Page(R.string.sp_menu_recent, createFragmentProducer(TopicFeedType.USER_RECENT)),
			new SimplePagerAdapter.Page(R.string.sp_menu_popular, createFragmentProducer(TopicFeedType.USER_POPULAR))
		};
		return new SimplePagerAdapter(this, getSupportFragmentManager(), pages) {
			@Override
			public int getCount() {
				return feedIsReadable ? super.getCount() : 1;
			}
		};
	}

	private Producer<Fragment> createFragmentProducer(TopicFeedType topicFeedType) {
		return () -> ProfileFeedFragment.create(userHandle, topicFeedType);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.sp_actionReportUser) {
			ContentUpdateHelper.startUserReport(this, userHandle, userName);
			return true;
		} else if (itemId == R.id.sp_actionBlockUser) {
			UserAccount.getInstance().blockUser(userHandle);
			return true;
		} else if (itemId == R.id.sp_addPost) {
			startActivity(new Intent(this, AddPostActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		if (getCurrentPagePosition() == 0) {
			if (isCurrentUser) {
				if (BuildConfig.STANDALONE_APP) {
					inflater.inflate(R.menu.sp_my_profile, menu);
				}
			} else {
				inflater.inflate(R.menu.sp_user_block, menu);
				inflater.inflate(R.menu.sp_user_report, menu);
			}
		} else {
			inflater.inflate(R.menu.sp_feed_display_method, menu);
		}
		return true;
	}


	protected void onPageSelected(int position) {
		invalidateOptionsMenu();
	}

	@Override
	protected void onResume() {
		super.onResume();
		EventBus.register(eventListener);
	}

	@Override
	protected void onPause() {
		EventBus.unregister(eventListener);
		super.onPause();
	}

	@Override
	protected void setupFragments() {
		super.setupFragments();
		getSupportFragmentManager().beginTransaction().add(new FeedViewMenuListenerFragment(), FeedViewMenuListenerFragment.TAG).commit();
	}
}
