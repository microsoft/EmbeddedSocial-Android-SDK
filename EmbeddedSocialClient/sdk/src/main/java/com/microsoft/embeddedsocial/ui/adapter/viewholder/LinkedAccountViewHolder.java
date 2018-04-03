/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.adapter.viewholder;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.microsoft.embeddedsocial.server.model.view.ThirdPartyAccountView;
import com.microsoft.embeddedsocial.sdk.R;

/**
 * Init linked account layout.
 */
public class LinkedAccountViewHolder extends RecyclerView.ViewHolder {
	private TextView accountName;
	private SwitchCompat accountState;

	public LinkedAccountViewHolder(View itemView) {
		super(itemView);
		accountName = (TextView) itemView.findViewById(R.id.es_account_name);
		accountState = (SwitchCompat) itemView.findViewById(R.id.es_account_state);
	}

	public void renderItem(ThirdPartyAccountView account, IOnCheckedListener onCheckedListener) {
		accountName.setText(account.getThirdPartyName());
		accountState.setEnabled(true);
		accountState.setTag(account.getIdentityProvider());
		accountState.setChecked(account.hasAccountHandle());
		// Fixed undefined switch state after swipe
		accountState.setOnTouchListener((v, event) -> {
			if (event.getAction() == MotionEvent.ACTION_CANCEL) {
				final CompoundButton button = (CompoundButton) v;
				button.setChecked(button.isChecked());
			}
			return false;
		});
		accountState.setOnCheckedChangeListener(new InternalOnCheckedChangeListener(onCheckedListener));
	}

	public interface IOnCheckedListener {
		void onCheckedChanged(SwitchCompat switchCompat, boolean isChecked);
	}

	private static class InternalOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
		private final IOnCheckedListener onCheckedListener;

		public InternalOnCheckedChangeListener(IOnCheckedListener onCheckedListener) {
			this.onCheckedListener = onCheckedListener;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// Possible skipped state changed after swipe
			if (buttonView.isPressed()) {
				buttonView.setEnabled(false);
				onCheckedListener.onCheckedChanged((SwitchCompat) buttonView, isChecked);
			}
		}
	}
}
