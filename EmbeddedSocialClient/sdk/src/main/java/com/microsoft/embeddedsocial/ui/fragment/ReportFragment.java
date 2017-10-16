/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.autorest.models.Reason;
import com.microsoft.embeddedsocial.data.storage.UserActionProxy;
import com.microsoft.embeddedsocial.event.dialog.OnPositiveButtonClickedEvent;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.ui.activity.base.BaseActivity;
import com.microsoft.embeddedsocial.ui.dialog.AlertDialogFragment;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseFragment;
import com.squareup.otto.Subscribe;

/**
 * Fragment for reporting content or user.
 */
public class ReportFragment extends BaseFragment {
	private static final String REPORT_RESULT_ID = "report_result";

	private String reportUserHandle;
	private String contentHandle;
	private Reason reason;
	private ContentType contentType;
	private View selectLayout;
	private View resultLayout;
	private View bottomBar;

	@Override
	protected int getLayoutId() {
		return R.layout.es_fragment_report;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		setHasOptionsMenu(true);
		initBaseView(view);

		bottomBar = findView(view, R.id.es_bottomBar);
		bottomBar.setVisibility(View.VISIBLE);
		Button doneButton = findView(view, R.id.es_doneButton);
		doneButton.setOnClickListener(v -> {
				UserActionProxy userActionProxy = new UserActionProxy(getContext());
			if (!TextUtils.isEmpty(reportUserHandle)) {
				userActionProxy.reportUser(reportUserHandle, reason);
			} else {
				userActionProxy.reportContent(contentHandle, contentType, reason);
			}
			if (isTablet()) {
				setDoneViewOnTheTablet();
			} else {
				setDoneViewOnThePhone();
			}
		});

		Bundle arguments = getArguments();
		if (arguments.containsKey(IntentExtras.USER_HANDLE)) {
			initUserView(view, arguments.getString(IntentExtras.USER_HANDLE));
			return;
		}

		if (arguments.containsKey(IntentExtras.REPORT_CONTENT_HANDLE_EXTRA) &&
			arguments.containsKey(IntentExtras.REPORT_CONTENT_TYPE_EXTRA)) {
			initContentView(view,
					arguments.getString(IntentExtras.REPORT_CONTENT_HANDLE_EXTRA),
					(ContentType) arguments.getSerializable(IntentExtras.REPORT_CONTENT_TYPE_EXTRA));
		}
	}

	@Subscribe
	public void onPositiveButtonClickedEvent(OnPositiveButtonClickedEvent event) {
		if (REPORT_RESULT_ID.equals(event.getDialogId())) {
			getActivity().finish();
		}
	}

	private void initUserView(View rootView, String reportUserHandle) {
		this.reportUserHandle = reportUserHandle;
		((TextView) rootView.findViewById(R.id.es_text_report_question))
			.setText(getString(R.string.es_report_question_pattern, getString(R.string.es_report_question_user)));
	}

	private void initContentView(View rootView, String contentHandle, ContentType contentType) {
		this.contentHandle = contentHandle;
		this.contentType = contentType;

		int contentTypeResId = R.string.es_report_question_post;
		switch (this.contentType) {
			case TOPIC:
				contentTypeResId = R.string.es_report_question_post;
				break;
			case COMMENT:
				contentTypeResId = R.string.es_report_question_comment;
				break;
			case REPLY:
				contentTypeResId = R.string.es_report_question_reply;
				break;
		}

		((TextView) rootView.findViewById(R.id.es_text_report_question))
			.setText(getString(R.string.es_report_question_pattern, getString(contentTypeResId)));
	}

	private void initBaseView(View rootView) {
		selectLayout = rootView.findViewById(R.id.es_select_layout);
		resultLayout = rootView.findViewById(R.id.es_result_layout);

		ListView listReportReason = (ListView) rootView.findViewById(R.id.es_list_report_reason);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(
			getActivity(), android.R.layout.simple_list_item_single_choice,
			getResources().getStringArray(R.array.es_report_reasons_array));
		listReportReason.setAdapter(adapter);
		listReportReason.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		// set default reason in case user does not interact with menu
		reason = Reason.THREATSCYBERBULLYINGHARASSMENT;
		listReportReason.setItemChecked(0, true);

		listReportReason.setOnItemClickListener((parent, view, position, id) -> {
			switch (position) {
				case 0:
					reason = Reason.THREATSCYBERBULLYINGHARASSMENT;
					break;
				case 1:
					reason = Reason.CHILDENDANGERMENTEXPLOITATION;
					break;
				case 2:
					reason = Reason.OFFENSIVECONTENT;
					break;
				case 3:
					reason = Reason.VIRUSSPYWAREMALWARE;
					break;
				case 4:
					reason = Reason.CONTENTINFRINGEMENT;
					break;
				case 5:
					reason = Reason.OTHER;
			}
		});
	}

	private void setDoneViewOnThePhone() {
		selectLayout.setVisibility(View.GONE);
		resultLayout.setVisibility(View.VISIBLE);
		bottomBar.setVisibility(View.GONE);
	}

	private void setDoneViewOnTheTablet() {
		new AlertDialogFragment.Builder(getActivity(), REPORT_RESULT_ID)
			.setTitle(R.string.es_report_result_title)
			.setMessage(R.string.es_report_result_text)
			.setPositiveButton(android.R.string.ok)
			.show(getActivity(), null);
	}
}
