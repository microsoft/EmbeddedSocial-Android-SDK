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
import com.microsoft.embeddedsocial.event.content.GetReplyEvent;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.ReplyButtonListener;
import com.microsoft.embeddedsocial.ui.adapter.viewholder.ReplyViewHolder;
import com.microsoft.embeddedsocial.actions.ActionsLauncher;
import com.microsoft.embeddedsocial.event.content.ReplyRemovedEvent;
import com.microsoft.embeddedsocial.server.model.view.ReplyView;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.ui.activity.DisplayNoteActivity;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseFragment;
import com.squareup.otto.Subscribe;

/**
 * Fragment to display single reply.
 */
public class DisplayReplyFragment extends BaseFragment {
	private ReplyButtonListener replyButtonListener;

	private View progressLayout;
	private View contentLayout;
	private View messageLayout;

	private String commentHandle;

	@Override
	protected int getLayoutId() {
		return R.layout.es_fragment_reply;
	}

	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setHasOptionsMenu(false);

		getActivity().setTitle(R.string.es_screen_title_display_reply);

		Bundle arguments = getArguments();
		if (arguments == null || !arguments.containsKey(IntentExtras.REPLY_HANDLE)) {
			// POSSIBLE ERROR
			DebugLog.e("Wrong call. Reply handle should be passed in the extras.");
			return;
		}

		initView(view);

		final String replyHandle = arguments.getString(IntentExtras.REPLY_HANDLE);
		ActionsLauncher.getReply(getActivity(), replyHandle);
		displayLoadView();
	}

	@Subscribe
	public void onGetReply(GetReplyEvent getReplyEvent) {
		if (getReplyEvent.isResult()) {
			commentHandle = getReplyEvent.getReplyView().getCommentHandle();
			displayCommentView(getReplyEvent.getReplyView());
		} else {
			displayMessageView();
		}
	}

	private void initView(View rootView) {
		replyButtonListener = new ReplyButtonListener(getActivity());

		contentLayout = rootView.findViewById(R.id.es_contentLayout);
		progressLayout = rootView.findViewById(R.id.es_progressLayout);
		messageLayout = rootView.findViewById(R.id.es_messageLayout);
		Button openCommentButton = (Button) rootView.findViewById(R.id.es_openCommentButton);
		openCommentButton.setOnClickListener(v -> {
			if (TextUtils.isEmpty(commentHandle)) {
				return;
			}

			Intent intent = new Intent(getActivity(), DisplayNoteActivity.class);
			intent.putExtra(IntentExtras.COMMENT_HANDLE, commentHandle);
			startActivity(intent);
		});
	}

	private void displayLoadView() {
		contentLayout.setVisibility(View.GONE);
		progressLayout.setVisibility(View.VISIBLE);
		messageLayout.setVisibility(View.GONE);
	}

	private void displayCommentView(ReplyView replyView) {
		contentLayout.setVisibility(View.VISIBLE);
		progressLayout.setVisibility(View.GONE);
		messageLayout.setVisibility(View.GONE);

		ReplyViewHolder replyViewHolder = new ReplyViewHolder(replyButtonListener, contentLayout);
		replyViewHolder.renderSingleItem(replyView);
	}

	private void displayMessageView() {
		contentLayout.setVisibility(View.GONE);
		progressLayout.setVisibility(View.GONE);
		messageLayout.setVisibility(View.VISIBLE);
	}

	@Subscribe
	public void onReplyRemoved(ReplyRemovedEvent replyRemovedEvent) {
		if (replyRemovedEvent.isSuccessful()) {
			getActivity().finish();
			Toast.makeText(getActivity(), R.string.es_content_removed_reply, Toast.LENGTH_SHORT).show();
		}
	}

}
