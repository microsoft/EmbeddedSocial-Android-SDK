/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.socialplus.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.microsoft.socialplus.actions.ActionsLauncher;
import com.microsoft.socialplus.base.utils.debug.DebugLog;
import com.microsoft.socialplus.event.content.CommentRemovedEvent;
import com.microsoft.socialplus.event.content.GetCommentEvent;
import com.microsoft.socialplus.fetcher.base.ViewState;
import com.microsoft.socialplus.fetcher.base.ViewStateListener;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.server.model.view.CommentView;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.ui.activity.TopicActivity;
import com.microsoft.socialplus.ui.adapter.viewholder.CommentButtonListener;
import com.microsoft.socialplus.ui.adapter.viewholder.CommentViewHolder;
import com.microsoft.socialplus.ui.fragment.base.BaseFragment;
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
		return R.layout.sp_fragment_comment;
	}

	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setHasOptionsMenu(false);

		getActivity().setTitle(R.string.sp_screen_title_display_comment);

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

		contentLayout = rootView.findViewById(R.id.sp_contentLayout);
		progressLayout = rootView.findViewById(R.id.sp_progressLayout);
		messageLayout = rootView.findViewById(R.id.sp_messageLayout);
		Button openTopicButton = (Button) rootView.findViewById(R.id.sp_openTopicButton);
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
			Toast.makeText(getActivity(), R.string.sp_content_removed_comment, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onViewStateChanged(ViewState viewState) {
		// Not used
	}
}
