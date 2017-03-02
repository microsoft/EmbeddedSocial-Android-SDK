/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.view;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import com.microsoft.embeddedsocial.base.utils.ObjectUtils;
import com.microsoft.embeddedsocial.base.utils.ViewUtils;

/**
 * Extension of {@link TextInputLayout} allowing a validation of the input.
 * Warning: it doesn't add an {@link EditText} itself!
 */
public class TextInput extends TextInputLayout {

	private Validator validator;

	public TextInput(Context context) {
		super(context);
	}

	public TextInput(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TextInput(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	public void onViewAdded(View child) {
		super.onViewAdded(child);
		if (child instanceof EditText) {
			EditText editText = (EditText) child;
			editText.setOnFocusChangeListener((v, hasFocus) -> {
				if (!hasFocus) {
					validate();
				}
			});
			editText.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					setError(null);
				}
			});
		}
	}

	public boolean validate() {
		EditText editText = acquireEditText();
		if (validator != null && !validator.isValid(ViewUtils.getText(editText))) {
			String text = String.valueOf(editText.getText());
			boolean isValid = validator.isValid(text);
			setErrorMessage(isValid ? null : validator.errorMessage);
			return isValid;
		} else {
			return true;
		}
	}

	private void setErrorMessage(CharSequence error) {
		if (!ObjectUtils.equal(error, getError())) {
			setError(error);
			setErrorEnabled(!TextUtils.isEmpty(error));
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		EditText editText = getEditText();
		if (editText != null) {
			editText.setOnFocusChangeListener(null);
			setErrorMessage(null);
		}
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

	public void focusAndShowKeyboard() {
		ViewUtils.focusAndShowKeyboard(acquireEditText());
	}

	public String getText() {
		return ViewUtils.getText(acquireEditText());
	}

	public void setText(String text) {
		acquireEditText().setText(text);
	}

	private EditText acquireEditText() {
		EditText editText = getEditText();
		if (editText == null) {
			throw new RuntimeException("Now child EditText");
		}
		return editText;
	}

	/**
	 * Checks if the input is valid.
	 */
	public abstract static class Validator {

		private final CharSequence errorMessage;

		protected Validator(CharSequence errorMessage) {
			this.errorMessage = errorMessage;
		}

		protected abstract boolean isValid(String text);

	}

}
