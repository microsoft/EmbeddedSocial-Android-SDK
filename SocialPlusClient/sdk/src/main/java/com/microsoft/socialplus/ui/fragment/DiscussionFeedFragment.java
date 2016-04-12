/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.microsoft.socialplus.autorest.models.ContentType;
import com.microsoft.socialplus.account.AuthorizationCause;
import com.microsoft.socialplus.account.UserAccount;
import com.microsoft.socialplus.base.event.EventBus;
import com.microsoft.socialplus.base.utils.EnumUtils;
import com.microsoft.socialplus.base.utils.ViewUtils;
import com.microsoft.socialplus.data.model.AccountData;
import com.microsoft.socialplus.event.ScrollPositionEvent;
import com.microsoft.socialplus.event.content.LikeAddedEvent;
import com.microsoft.socialplus.event.content.LikeRemovedEvent;
import com.microsoft.socialplus.event.relationship.UserFollowedStateChangedEvent;
import com.microsoft.socialplus.fetcher.base.Callback;
import com.microsoft.socialplus.fetcher.base.FetchableRecyclerView;
import com.microsoft.socialplus.fetcher.base.ViewState;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.server.model.view.TopicView;
import com.microsoft.socialplus.server.model.view.UserCompactView;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.ui.adapter.DiscussionFeedAdapter;
import com.microsoft.socialplus.ui.adapter.renderer.ProfileInfoRenderer;
import com.microsoft.socialplus.ui.fragment.base.BaseListContentFragment;
import com.microsoft.socialplus.ui.util.ProfileOpenHelper;
import com.microsoft.socialplus.ui.util.TextHelper;
import com.squareup.otto.Subscribe;

/**
 * Fragment to display topic and comments feed.
 */
public abstract class DiscussionFeedFragment extends BaseListContentFragment<DiscussionFeedAdapter> {

	private EditText noteText;
	private boolean scrolledDown;
	private Handler uiHandler;
	private Button doneButton;

	private ProfileInfoRenderer.ProfileViewHolder profileViewHolder;

	protected abstract void onDonePressed(String text);

	protected abstract String getHandle();

	protected abstract int getNoteHint();

	protected abstract AccountData getAuthorProfile();

	protected abstract UserCompactView getAuthor();

	protected DiscussionFeedFragment() {
		addThemeToMerge(R.style.SocialPlusSdkThemeOverlayTopic);
		uiHandler = new Handler(Looper.getMainLooper());
	}

	public void showDoneButton() {
		doneButton.setVisibility(View.VISIBLE);
	}

	public void hideDoneButton() {
		doneButton.setVisibility(View.INVISIBLE);
	}

	public void setOnDoneClickListener(View.OnClickListener listener) {
		doneButton.setOnClickListener(listener);
	}

	@Override
	protected void initRecyclerView() {
		if (UserAccount.getInstance().isSignedIn() && !isLocal()) {
			FetchableRecyclerView recyclerView = getRecyclerView();
			final View enterNote = LayoutInflater.from(getActivity()).inflate(R.layout.sp_view_enter_note, recyclerView, false);
			noteText = (EditText) enterNote.findViewById(R.id.sp_noteText);

			doneButton = (Button) enterNote.findViewById(R.id.sp_doneButton);
			noteText.setHint(getNoteHint());
			setOnDoneClickListener(v -> {
				if (noteText != null) {
					onDonePressed(noteText.getText().toString());
					noteText.setText("");
					noteText.clearFocus();
					ViewUtils.hideKeyboard(this);
				}
			});
			noteText.setOnFocusChangeListener((v, hasFocus) -> {
				if (hasFocus) {
					if (!TextHelper.isEmpty(((EditText) v).getText())) {
						showDoneButton();
					} else {
						hideDoneButton();
					}
				}
			});
			noteText.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					// Not used
				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (!TextHelper.isEmpty(s)) {
						showDoneButton();
					} else {
						hideDoneButton();
					}
				}

				@Override
				public void afterTextChanged(Editable s) {
					// Not used
				}
			});
			recyclerView.addFooterView(enterNote);
		}

		super.initRecyclerView();
	}

	@Override
	public void onViewStateChanged(ViewState viewState) {
		if (viewState == ViewState.DATA) {
			if (!scrolledDown && shouldJumpToInputView()) {
				EventBus.post(new ScrollPositionEvent(ScrollPositionEvent.EDIT_POSITION));
				scrolledDown = true;
			}
		}
		super.onViewStateChanged(viewState);
	}

	private boolean shouldJumpToInputView() {
		Bundle arguments = getArguments();
		return arguments != null && (arguments.getBoolean(IntentExtras.JUMP_TO_EDIT) || signedInFromCommentButton(arguments));
	}

	private boolean signedInFromCommentButton(Bundle arguments) {
		return EnumUtils.getValue(arguments, IntentExtras.REASON_TO_SIGN_IN, AuthorizationCause.class) == AuthorizationCause.COMMENT;
	}

	private boolean isLocal() {
		Bundle arguments = getArguments();
		if (arguments == null) {
			return false;
		}
		TopicView topicView = arguments.getParcelable(IntentExtras.TOPIC_EXTRA);
		return (topicView != null && topicView.isLocal());
	}

	@Override
	public void onResume() {
		super.onResume();
		EventBus.register(eventListener);
	}

	@Override
	public void onPause() {
		super.onPause();
		EventBus.unregister(eventListener);
	}


	// TODO: handle block event

	private void setLike(ContentType contentType, String handle, boolean likeStatus) {
		switch (contentType) {
			case TOPIC:
				getAdapter().setTopicLike(likeStatus);
				break;
			case COMMENT:
				getAdapter().setCommentLike(handle, likeStatus);
				break;
			case REPLY:
				getAdapter().setReplyLike(handle, likeStatus);
				break;
		}
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ViewGroup profileView = findView(view, R.id.sp_profileLayout);
		synchronized (this) {
			profileViewHolder = profileView != null ? new ProfileInfoRenderer.ProfileViewHolder(profileView) : null;
		}
		renderAuthorIfNeeded();
	}

	private void renderAuthorIfNeeded() {
		AccountData profile = getAuthorProfile();
		synchronized (this) {
			if (isAdded() && profileViewHolder != null && profile != null) {
				UserCompactView author = getAuthor();
				ProfileInfoRenderer renderer = new ProfileInfoRenderer(getContext(), author.getHandle(), ProfileInfoRenderer.RenderType.SMALL);
				renderer.inflatePhoto(profileViewHolder.getRootView());
				profileViewHolder.initViews();
				profileViewHolder.setUserHandle(author.getHandle());
				profileViewHolder.cardContent.setOnClickListener(v -> ProfileOpenHelper.openUserProfile(getContext(), author));
				renderer.renderItem(profile, profileViewHolder);
			}
		}
	}

	@Override
	protected void setAdapter(DiscussionFeedAdapter adapter) {
		adapter.addFetcherCallback(new Callback() {
			@Override
			public void onDataRequestSucceeded() {
				renderAuthorIfNeeded();
			}

			@Override
			public void onDataUpdated() {
				renderAuthorIfNeeded();
			}
		});
		super.setAdapter(adapter);
	}

	private Object eventListener = new Object() {

		@Subscribe
		public void onLikeAdded(LikeAddedEvent likeAddedEvent) {
			if (likeAddedEvent.isResult()) {
				setLike(likeAddedEvent.getData().getContentType(),
					likeAddedEvent.getData().getHandle(),
					true);
			}
		}

		@Subscribe
		public void onLikeRemoved(LikeRemovedEvent likeRemovedEvent) {
			if (likeRemovedEvent.isResult()) {
				setLike(likeRemovedEvent.getData().getContentType(),
					likeRemovedEvent.getData().getHandle(),
					false);
			}
		}

		@Subscribe
		public void onScrollPositionEvent(ScrollPositionEvent scrollPositionEvent) {
			if (scrollPositionEvent.getPosition() == ScrollPositionEvent.EDIT_POSITION) {
				if (noteText != null) {
					getRecyclerView().scrollToPosition(getRecyclerView().getItemCount() - 1);
					uiHandler.post(() -> ViewUtils.focusAndShowKeyboard(noteText));
				}
			} else {
				final RecyclerView.ViewHolder viewHolder = getRecyclerView().findViewHolderForAdapterPosition(scrollPositionEvent.getPosition());
				getRecyclerView().smoothScrollBy(0, viewHolder.itemView.getTop() + viewHolder.itemView.getHeight());
			}
		}

		@Subscribe
		public void onFollowedStateChangedEvent(UserFollowedStateChangedEvent event) {
			UserCompactView author = getAuthor();
			if (event.isForUser(author.getHandle())) {
				AccountData profile = getAuthorProfile();
				if (profile != null) {
					profile.setFollowedStatus(event.getFollowedStatus());
					renderAuthorIfNeeded();
				}
			}
		}
	};
}
