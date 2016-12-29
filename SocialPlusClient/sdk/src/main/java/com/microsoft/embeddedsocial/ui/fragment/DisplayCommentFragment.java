/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.microsoft.embeddedsocial.base.utils.debug.DebugLog;
import com.microsoft.embeddedsocial.event.content.CommentRemovedEvent;
import com.microsoft.embeddedsocial.event.content.GetCommentEvent;
import com.microsoft.embeddedsocial.fetcher.base.ViewState;
import com.microsoft.embeddedsocial.fetcher.base.ViewStateListener;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.server.model.view.CommentView;
import com.microsoft.embeddedsocial.ui.activity.TopicActivity;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.CommentViewHolder;
import com.microsoft.embeddedsocial.actions.ActionsLauncher;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.CommentButtonListener;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseFragment;
import com.squareup.otto.Subscribe;

/**
 * Fragment to display single comment.
 */
public class DisplayCommentFragment extends BaseFragment implements ViewStateListener {
	private CommentButtonListener commentButtonListener;

	private View progressLayout;
	private View contentLayout;
	private View messageLayout;

	private String topicHandle;

	@Override
	protected int getLayoutId() {
		return R.layout.es_fragment_comment;
	}

	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setHasOptionsMenu(false);

		getActivity().setTitle(R.string.es_screen_title_display_comment);

		Bundle arguments = getArguments();
		if (arguments == null || !arguments.containsKey(IntentExtras.COMMENT_HANDLE)) {
			// POSSIBLE ERROR
			DebugLog.e("Wrong call. Comment handle should be passed in the extras.");
			return;
		}

		initView(view);

		final String commentHandle = arguments.getString(IntentExtras.COMMENT_HANDLE);
		ActionsLauncher.getComment(getActivity(), commentHandle);
		displayLoadView();
	}

	@Subscribe
	public void onGetComment(GetCommentEvent getCommentEvent) {
		if (getCommentEvent.isResult()) {
			topicHandle = getCommentEvent.getCommentView().getTopicHandle();
			displayCommentView(getCommentEvent.getCommentView());
		} else {
			displayMessageView();
		}
	}

	private void initView(View rootView) {
		commentButtonListener = new CommentButtonListener(getActivity(), CommentButtonListener.Container.COMMENT);

		contentLayout = rootView.findViewById(R.id.es_contentLayout);
		progressLayout = rootView.findViewById(R.id.es_progressLayout);
		messageLayout = rootView.findViewById(R.id.es_messageLayout);
		Button openTopicButton = (Button) rootView.findViewById(R.id.es_openTopicButton);
		openTopicButton.setOnClickListener(v -> {
			if (TextUtils.isEmpty(topicHandle)) {
				return;
			}

			Intent intent = new Intent(getActivity(), TopicActivity.class);
			intent.putExtra(IntentExtras.TOPIC_HANDLE, topicHandle);
			startActivity(intent);
		});
	}

	private void displayLoadView() {
		contentLayout.setVisibility(View.GONE);
		progressLayout.setVisibility(View.VISIBLE);
		messageLayout.setVisibility(View.GONE);
	}

	private void displayCommentView(CommentView commentView) {
		contentLayout.setVisibility(View.VISIBLE);
		progressLayout.setVisibility(View.GONE);
		messageLayout.setVisibility(View.GONE);

		CommentViewHolder commentViewHolder = new CommentViewHolder(commentButtonListener, contentLayout);
		commentViewHolder.renderSingleItem(commentView);
	}

	private void displayMessageView() {
		contentLayout.setVisibility(View.GONE);
		progressLayout.setVisibility(View.GONE);
		messageLayout.setVisibility(View.VISIBLE);
	}

	@Subscribe
	public void onCommentRemoved(CommentRemovedEvent commentRemovedEvent) {
		if (commentRemovedEvent.isSuccessful()) {
			getActivity().finish();
			Toast.makeText(getActivity(), R.string.es_content_removed_comment, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onViewStateChanged(ViewState viewState, Exception exception) {
		// Not used
	}
}
