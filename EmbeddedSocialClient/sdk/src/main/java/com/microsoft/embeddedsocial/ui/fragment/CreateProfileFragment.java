/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.ui.fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.microsoft.embeddedsocial.account.UserAccount;
import com.microsoft.embeddedsocial.actions.Action;
import com.microsoft.embeddedsocial.actions.OngoingActions;
import com.microsoft.embeddedsocial.base.utils.BitmapUtils;
import com.microsoft.embeddedsocial.base.utils.ViewUtils;
import com.microsoft.embeddedsocial.data.model.AccountData;
import com.microsoft.embeddedsocial.event.signin.CreateUserFailedEvent;
import com.microsoft.embeddedsocial.event.signin.UserSignedInEvent;
import com.microsoft.embeddedsocial.image.CoverLoader;
import com.microsoft.embeddedsocial.image.ImageLoader;
import com.microsoft.embeddedsocial.image.ImageLocation;
import com.microsoft.embeddedsocial.image.ImageViewContentLoader;
import com.microsoft.embeddedsocial.sdk.R;
import com.microsoft.embeddedsocial.ui.activity.base.BaseActivity;
import com.microsoft.embeddedsocial.ui.fragment.base.BaseFragmentWithProgress;
import com.microsoft.embeddedsocial.ui.fragment.module.PhotoProviderModule;
import com.microsoft.embeddedsocial.ui.theme.ThemeAttributes;
import com.microsoft.embeddedsocial.ui.util.FieldNotEmptyValidator;
import com.microsoft.embeddedsocial.ui.util.FitWidthSizeSpec;
import com.microsoft.embeddedsocial.ui.util.SocialNetworkAccount;
import com.microsoft.embeddedsocial.ui.view.TextInput;
import com.microsoft.embeddedsocial.actions.ActionsLauncher;
import com.microsoft.embeddedsocial.data.model.CreateAccountData;
import com.microsoft.embeddedsocial.image.UserPhotoLoader;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.squareup.otto.Subscribe;

import java.util.LinkedList;
import java.util.List;

/**
 * Fragment to create profile.
 */
@SuppressWarnings("FieldCanBeLocal")
public class CreateProfileFragment extends BaseFragmentWithProgress {
    private static final String PREF_PHOTO_URI = "photo";

    private final PhotoProviderModule photoProvider;

    private TextInput firstNameView;
    private TextInput lastNameView;
    private TextInput bioView;
    private View uploadPhotoView;
    private View photoLayout;
    private ImageView largePhotoView;
    private ImageView profilePhotoView;
    private ImageViewContentLoader largePhotoLoader;
    private ImageViewContentLoader profilePhotoLoader;
    private View bottomBar;
    private Button doneButton;

    private EditImageOnClickListener editPhotoOnClickListener;
    private SelectImageOnClickListener selectPhotoOnClickListener;

    private List<TextInput> inputFields = new LinkedList<>();

    private Uri userPhotoUri;

    SocialNetworkAccount thirdPartyAccount;

    public CreateProfileFragment() {
        photoProvider = new PhotoProviderModule(this, new SelectProfilePhotoConsumer());
        addModule(photoProvider);
    }

    @Override
    public void setArguments(Bundle args) {
        thirdPartyAccount = args.getParcelable(IntentExtras.THIRD_PARTY_ACCOUNT);
        args.remove(IntentExtras.THIRD_PARTY_ACCOUNT);
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.es_fragment_create_profile;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        if (savedInstanceState != null) {
            userPhotoUri = savedInstanceState.getParcelable(PREF_PHOTO_URI);
            renderUserPhoto(UserAccount.getInstance().getAccountDetails());
        }
        String firstName = thirdPartyAccount.getFirstName();
        String lastName = thirdPartyAccount.getLastName();
        if (firstName != null) {
            firstNameView.setText(firstName);
        }
        if (lastName != null) {
            lastNameView.setText(lastName);
        }
    }

    private void onDone() {
        if (checkCorrectness()) {
            hideKeyboard();

            setProgressVisible(true);

            CreateAccountData createAccountData = new CreateAccountData.Builder()
                    .setFirstName(firstNameView.getText())
                    .setLastName(lastNameView.getText())
                    .setBio(bioView.getText())
                    .setPhotoUri(userPhotoUri)
                    .setIdentityProvider(thirdPartyAccount.getAccountType())
                    .setThirdPartyAccessToken(thirdPartyAccount.getThirdPartyAccessToken())
                    .setThirdPartyRequestToken(thirdPartyAccount.getThirdPartyRequestToken())
                    .build();

            thirdPartyAccount.clearTokens();
            ActionsLauncher.createAccount(getContext(), createAccountData);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onSignedIn(UserSignedInEvent event) {
        setProgressVisible(false);
        Toast.makeText(getActivity(), event.getMessageId(), Toast.LENGTH_LONG).show();
        finishActivity();
    }

    @Subscribe
    public void onCreateUserFailed(CreateUserFailedEvent event) {
        setProgressVisible(false);
        Toast.makeText(getActivity(), getContext().getString(R.string.es_msg_general_create_user_error), Toast.LENGTH_LONG).show();
    }

    public boolean checkCorrectness() {
        boolean result = true;
        TextInput firstViewWithError = null;
        for (TextInput inputView : inputFields) {
            boolean viewInputIsCorrect = inputView.validate();
            result &= viewInputIsCorrect;
            if (!viewInputIsCorrect && firstViewWithError == null) {
                firstViewWithError = inputView;
            }
        }
        if (firstViewWithError != null) {
            firstViewWithError.focusAndShowKeyboard();
            Toast.makeText(getContext(), R.string.es_message_correct_input, Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    private void renderUserPhoto(AccountData accountData) {
        ImageLocation photoLocation = accountData.getUserPhotoLocation();
        boolean hasPhoto = photoLocation != null;
        updatePhotoLayout(hasPhoto);
        if (hasPhoto) {
            if (largePhotoView != null) {
                largePhotoLoader = new CoverLoader(largePhotoView);
                largePhotoLoader.load(photoLocation, ViewUtils.getDisplayWidth(getActivity()));
            }

            if (isTablet()) {
                profilePhotoLoader = new UserPhotoLoader(profilePhotoView);
                profilePhotoLoader.load(
                        photoLocation,
                        getResources().getDimensionPixelSize(R.dimen.es_user_icon_size)
                );
            }
        }
    }

    protected void updatePhotoLayout(boolean hasPhoto) {
        if (photoLayout != null) {
            ViewUtils.setVisible(photoLayout, hasPhoto);
        }
        if (isTablet()) {
            uploadPhotoView.setOnClickListener((hasPhoto) ? editPhotoOnClickListener : selectPhotoOnClickListener);
        } else {
            ViewUtils.setVisible(uploadPhotoView, !hasPhoto);
        }
    }

    protected void initViews(View view) {
        editPhotoOnClickListener = new EditImageOnClickListener(photoProvider);
        selectPhotoOnClickListener = new SelectImageOnClickListener(photoProvider);

        firstNameView = findView(view, R.id.es_firstNameLayout);
        firstNameView.setValidator(new FieldNotEmptyValidator(getContext()));
        inputFields.add(firstNameView);
        lastNameView = findView(view, R.id.es_lastNameLayout);
        lastNameView.setValidator(new FieldNotEmptyValidator(getContext()));
        inputFields.add(lastNameView);
        bioView = findView(view, R.id.es_bioLayout);
        uploadPhotoView = findView(view, R.id.es_uploadPhotoLayout);
        largePhotoView = findView(view, R.id.es_largePhoto);
        profilePhotoView = findView(view, R.id.es_profileImage);
        photoLayout = findView(view, R.id.es_photoLayout);
        bottomBar = findView(view, R.id.es_bottomBar);
        doneButton = findView(view, R.id.es_doneButton);
        bottomBar.setVisibility(View.VISIBLE);
        doneButton.setOnClickListener(v -> onDone());

        photoLayout.setVisibility(View.GONE);
        if (findView(view, R.id.es_editPhoto) != null) {
            setOnClickListener(view, R.id.es_editPhoto, editPhotoOnClickListener);
        }
        if (!isTablet()) {
            uploadPhotoView.setOnClickListener(selectPhotoOnClickListener);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (userPhotoUri != null) {
            outState.putParcelable(PREF_PHOTO_URI, userPhotoUri);
        }
    }

    @Override
    public boolean onBackPressed() {
        hideKeyboard();
        if (OngoingActions.hasActionsWithTag(Action.Tags.UPDATE_ACCOUNT)) {
            showToast(R.string.es_message_wait_until_account_created);
            return false;
        }
        return super.onBackPressed();
    }

    @Override
    protected void setProgressVisible(boolean progressVisible) {
        super.setProgressVisible(progressVisible);
        if (progressVisible) {
            bottomBar.setVisibility(View.GONE);
        } else {
            bottomBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        photoProvider.dispose();
    }

    private class EditImageOnClickListener implements View.OnClickListener {
        final PhotoProviderModule photoProviderModule;

        public EditImageOnClickListener(PhotoProviderModule photoProviderModule) {
            this.photoProviderModule = photoProviderModule;
        }

        @Override
        public void onClick(View v) {
            photoProviderModule.showEditImageDialog();
        }
    }

    private class SelectImageOnClickListener implements View.OnClickListener {
        final PhotoProviderModule photoProviderModule;

        public SelectImageOnClickListener(PhotoProviderModule photoProviderModule) {
            this.photoProviderModule = photoProviderModule;
        }

        @Override
        public void onClick(View v) {
            photoProviderModule.showSelectImageDialog();
        }
    }

    private class SelectProfilePhotoConsumer implements PhotoProviderModule.Consumer {
        @Override
        public void onPhotoSelected(Uri photoUri) {
            if (largePhotoView != null) {
                ImageLoader.cancel(largePhotoView);
                largePhotoView.setImageBitmap(null);
            }
            if (isTablet()) {
                ImageLoader.cancel(profilePhotoView);
                profilePhotoView.setImageResource(ThemeAttributes.getResourceId(getContext(), R.styleable.es_AppTheme_es_userNoPhotoIcon));
            }
            userPhotoUri = photoUri;
            updatePhotoLayout(photoUri != null);
        }

        @Override
        public void onPhotoLoaded(Uri photoUri, Bitmap thumbnail) {
            if (userPhotoUri.equals(photoUri) && thumbnail != null && isAdded()) {
                if (largePhotoView != null) {
                    largePhotoView.setImageBitmap(thumbnail);
                }
                profilePhotoView.setImageBitmap(thumbnail);
            }
        }

        @Override
        public BitmapUtils.SizeSpec getSizeSpec() {
            return new FitWidthSizeSpec(getActivity());
        }
    }
}
