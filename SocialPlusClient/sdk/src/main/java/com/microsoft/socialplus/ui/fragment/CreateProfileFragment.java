/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.ui.fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.microsoft.socialplus.account.UserAccount;
import com.microsoft.socialplus.actions.Action;
import com.microsoft.socialplus.actions.ActionsLauncher;
import com.microsoft.socialplus.actions.OngoingActions;
import com.microsoft.socialplus.base.utils.BitmapUtils;
import com.microsoft.socialplus.base.utils.ViewUtils;
import com.microsoft.socialplus.data.model.AccountData;
import com.microsoft.socialplus.data.model.CreateAccountData;
import com.microsoft.socialplus.event.signin.UserSignedInEvent;
import com.microsoft.socialplus.image.CoverLoader;
import com.microsoft.socialplus.image.ImageLoader;
import com.microsoft.socialplus.image.ImageLocation;
import com.microsoft.socialplus.image.ImageViewContentLoader;
import com.microsoft.socialplus.image.UserPhotoLoader;
import com.microsoft.socialplus.sdk.R;
import com.microsoft.socialplus.service.IntentExtras;
import com.microsoft.socialplus.ui.activity.base.BaseActivity;
import com.microsoft.socialplus.ui.fragment.base.BaseFragmentWithProgress;
import com.microsoft.socialplus.ui.fragment.module.PhotoProviderModule;
import com.microsoft.socialplus.ui.theme.ThemeAttributes;
import com.microsoft.socialplus.ui.util.FieldNotEmptyValidator;
import com.microsoft.socialplus.ui.util.FitWidthSizeSpec;
import com.microsoft.socialplus.ui.util.SocialNetworkAccount;
import com.microsoft.socialplus.ui.view.TextInput;
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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        BaseActivity owner = getOwner();
        owner.showBottomBar();
        owner.setOnDoneClickListener(v -> onDone());
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.sp_fragment_create_profile;
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

            CreateAccountData createAccountData = new CreateAccountData.Builder()
                    .setFirstName(firstNameView.getText())
                    .setLastName(lastNameView.getText())
                    .setBio(bioView.getText())
                    .setPhotoUri(userPhotoUri)
                    .setIdentityProvider(thirdPartyAccount.getAccountType())
                    .setThirdPartyAccessToken(thirdPartyAccount.getThirdPartyAccessToken())
                    .build();

            ActionsLauncher.createAccount(getContext(), createAccountData);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onSignedIn(UserSignedInEvent event) {
        Toast.makeText(getActivity(), event.getMessageId(), Toast.LENGTH_LONG).show();
        finishActivity();
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
            Toast.makeText(getContext(), R.string.sp_message_correct_input, Toast.LENGTH_SHORT).show();
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
                largePhotoLoader.load(photoLocation, ViewUtils.getDisplayWidth(getOwner()));
            }

            if (isTablet()) {
                profilePhotoLoader = new UserPhotoLoader(profilePhotoView);
                profilePhotoLoader.load(
                        photoLocation,
                        getResources().getDimensionPixelSize(R.dimen.sp_user_icon_size)
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

        firstNameView = findView(view, R.id.sp_firstNameLayout);
        firstNameView.setValidator(new FieldNotEmptyValidator(getContext()));
        inputFields.add(firstNameView);
        lastNameView = findView(view, R.id.sp_lastNameLayout);
        lastNameView.setValidator(new FieldNotEmptyValidator(getContext()));
        inputFields.add(lastNameView);
        bioView = findView(view, R.id.sp_bioLayout);
        uploadPhotoView = findView(view, R.id.sp_uploadPhotoLayout);
        largePhotoView = findView(view, R.id.sp_largePhoto);
        profilePhotoView = findView(view, R.id.sp_profileImage);
        photoLayout = findView(view, R.id.sp_photoLayout);
        photoLayout.setVisibility(View.GONE);
        if (findView(view, R.id.sp_editPhoto) != null) {
            setOnClickListener(view, R.id.sp_editPhoto, editPhotoOnClickListener);
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
            showToast(R.string.sp_message_wait_until_account_created);
            return false;
        }
        return super.onBackPressed();
    }

    @Override
    protected void setProgressVisible(boolean progressVisible) {
        super.setProgressVisible(progressVisible);
        if (progressVisible) {
            getOwner().hideBottomBar();
        } else {
            getOwner().showBottomBar();
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
                profilePhotoView.setImageResource(ThemeAttributes.getResourceId(getContext(), R.styleable.sp_AppTheme_sp_userNoPhotoIcon));
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
            return new FitWidthSizeSpec(getOwner());
        }
    }
}
