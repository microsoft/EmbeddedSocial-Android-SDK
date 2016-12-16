/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * Encapsulates some separate Fragment's behavior.
 */
@SuppressWarnings("UnusedParameters")
public abstract class Module {

	private final BaseFragment owner;

	protected Module(BaseFragment owner) {
		this.owner = owner;
	}

	protected void onAttach(Context context) {
	}

	protected void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
	}

	protected void onStart() {
	}

	protected void onResume() {
	}

	protected void onSaveInstanceState(Bundle outState) {
	}

	protected void onPause() {
	}

	protected void onStop() {
	}

	protected void onDestroyView() {
	}

	protected void onDetach() {
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	}

	protected void onCreate(Bundle savedInstanceState) {
	}

	protected BaseFragment getOwner() {
		return owner;
	}

	protected Context getContext() {
		return owner.getActivity();
	}

	protected void onPrepareOptionsMenu(Menu menu) {
	}

	protected boolean onOptionsItemSelected(MenuItem item) {
		return false;
	}

}
