/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
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

import com.microsoft.embeddedsocial.account.AuthorizationCause;
import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.base.event.EventBus;
import com.microsoft.embeddedsocial.base.utils.BitmapUtils;
import com.microsoft.embeddedsocial.base.utils.ObjectUtils;
import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.data.model.AccountData;
import com.microsoft.embeddedsocial.event.ScrollPositionEvent;
import com.microsoft.embeddedsocial.event.relationship.UserFollowedStateChangedEvent;
import com.microsoft.embeddedsocial.fetcher.base.FetchableRecyclerView;
import com.microsoft.embeddedsocial.fetcher.base.ViewState;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.TopicView;
import com.microsoft.embeddedsocial.server.model.view.UserCompactView;
import com.microsoft.embeddedsocial.ui.adapter.DiscussionFeedAdapter;
import com.microsoft.embeddedsocial.ui.adapter.renderer.ProfileInfoRenderer;
import com.microsoft.embeddedsocial.ui.fragment.module.PhotoProviderModule;
import com.microsoft.embeddedsocial.ui.util.FitWidthSizeSpec;
import com.microsoft.embeddedsocial.ui.util.ProfileOpenHelper;
import com.microsoft.embeddedsocial.ui.util.TextHelper;
import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.base.utils.EnumUtils;
import com.microsoft.embeddedsocial.data.storage.PostStorage;
import com.microsoft.embeddedsocial.event.content.LikeAddedEvent;
import com.microsoft.embeddedsocial.event.content.LikeRemovedEvent;
import com.microsoft.embeddedsocial.fetcher.base.Callback;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseListContentFragment;
import com.squareup.otto.Subscribe;

import java.io.IOException;

/**
 * Fragment to display topic and comments feed.
 */
public abstract class DiscussionFeedFragment extends BaseListContentFragment<DiscussionFeedAdapter>
		implements PhotoProviderModule.Consumer {
	private static final String PREF_IMAGE_URI = "imageUri";
	private static final int IMAGE_PREVIEW_DPS = 150;

	private EditText noteText;
	private boolean scrolledDown;
	private Handler uiHandler;
	private Button doneButton;
	protected ImageButton imageButton;
	private ImageView coverView;
	private PostStorage postStorage;
	private PhotoProviderModule photoProvider;
	private Uri imageUri;
	private Activity mActivity;
	private boolean isSignedIn;

	private ProfileInfoRenderer.ProfileViewHolder profileViewHolder;

	protected abstract void onDonePressed(String text, String imagePath);

	protected abstract String getHandle();

	protected abstract int getNoteHint();

	protected abstract AccountData getAuthorProfile();

	protected abstract UserCompactView getAuthor();

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mActivity = activity;
		isSignedIn = UserAccount.getInstance().isSignedIn();
	}

	protected DiscussionFeedFragment() {
		addThemeToMerge(R.style.EmbeddedSocialSdkThemeOverlayTopic);
		uiHandler = new Handler(Looper.getMainLooper());
		photoProvider = new PhotoProviderModule(this, this);
		addModule(photoProvider);
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

	public void setAttachImageClickListener(View.OnClickListener listener) {
		imageButton.setOnClickListener(listener);
		coverView.setOnClickListener(listener);
	}

	@Override
	protected void initRecyclerView() {
		if (UserAccount.getInstance().isSignedIn() && !isLocal()) {
			FetchableRecyclerView recyclerView = getRecyclerView();
			final View enterNote = LayoutInflater.from(getContext()).inflate(R.layout.es_view_enter_note, recyclerView, false);
			noteText = (EditText) enterNote.findViewById(R.id.es_noteText);
			doneButton = (Button) enterNote.findViewById(R.id.es_doneButton);
			imageButton = (ImageButton) enterNote.findViewById(R.id.es_attachImageButton);
			coverView = (ImageView) enterNote.findViewById(R.id.es_noteImage);

			noteText.setHint(getNoteHint());
			setOnDoneClickListener(v -> {
				if (noteText != null) {
					String imagePath = null;
					try {
						imagePath = postStorage.storeImageToTempFile(imageUri);
					} catch (IOException e){
						DebugLog.i("Store Discussion item image failed");
					}
					onDonePressed(noteText.getText().toString(), imagePath);
					noteText.setText("");
					hideView(R.id.es_noteImage);
					noteText.clearFocus();
					ViewUtils.hideKeyboard(getActivity());
				}
			});

			setAttachImageClickListener(v -> {
				if (imageUri == null) {
					// no image present
					photoProvider.showSelectImageDialog();
				} else {
					photoProvider.showEditImageDialog();
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
	public void onViewStateChanged(ViewState viewState, Exception exception) {
		if (viewState == ViewState.DATA) {
			if (!scrolledDown && shouldJumpToInputView()) {
				EventBus.post(new ScrollPositionEvent(ScrollPositionEvent.EDIT_POSITION));
				scrolledDown = true;
			}
		}
		super.onViewStateChanged(viewState, exception);
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
		// Check if the user has signed in since the last time this fragment was shown
		if (!isSignedIn && UserAccount.getInstance().isSignedIn()) {
			onUserSignedIn();
		}
		EventBus.register(eventListener);
	}

	@Override
	public void onUserSignedIn() {
		initRecyclerView();
		isSignedIn = true;
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
		ViewGroup profileView = findView(view, R.id.es_profileLayout);
		postStorage = new PostStorage(view.getContext());
		synchronized (this) {
			profileViewHolder = profileView != null ? new ProfileInfoRenderer.ProfileViewHolder(profileView) : null;
		}

		if (savedInstanceState != null) {
			imageUri = savedInstanceState.getParcelable(PREF_IMAGE_URI);
			if (imageUri != null) {
				photoProvider.loadBitmap(imageUri);
			}
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
			if (author != null && event.isForUser(author.getHandle())) {
				AccountData profile = getAuthorProfile();
				if (profile != null) {
					profile.setFollowedStatus(event.getFollowedStatus());
					renderAuthorIfNeeded();
				}
			}
		}
	};

	@Override
	public void onPhotoSelected(Uri newImageUri) {
		this.imageUri = newImageUri;
		if (imageUri == null) {
			// image was removed
			hideView(R.id.es_noteImage);
		}
	}

	@Override
	public void onPhotoLoaded(Uri loadedImageUri, Bitmap thumbnail) {
		if (ObjectUtils.equal(imageUri, loadedImageUri)) {
			if (thumbnail != null) {
				showView(R.id.es_noteImage);
				ViewGroup.LayoutParams layoutParams = coverView.getLayoutParams();
				double imageRatio = (double) thumbnail.getWidth() / thumbnail.getHeight();
				float density = getContext().getResources().getDisplayMetrics().density;
				layoutParams.height = (int)(IMAGE_PREVIEW_DPS * density + 0.5f);
				int imageViewWidth = (int) (layoutParams.height * imageRatio);
				layoutParams.width = imageViewWidth;
				coverView.setLayoutParams(layoutParams);
			} else {
				hideView(R.id.es_noteImage);
			}
			coverView.setImageBitmap(thumbnail);
		}
	}

	@Override
	public BitmapUtils.SizeSpec getSizeSpec() {
		return new FitWidthSizeSpec(mActivity);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable(PREF_IMAGE_URI, imageUri);
	}
}
