package com.microsoft.embeddedsocial.ui.util;

import com.microsoft.embeddedsocial.account.AuthorizationCause;
import com.microsoft.embeddedsocial.base.utils.EnumUtils;
import com.microsoft.embeddedsocial.event.RequestSignInEvent;
import com.microsoft.embeddedsocial.event.click.OpenCommentEvent;
import com.microsoft.embeddedsocial.event.click.OpenTopicEvent;
import com.microsoft.embeddedsocial.event.click.OpenUserProfileEvent;
import com.microsoft.embeddedsocial.event.click.ViewCommentCoverImageEvent;
import com.microsoft.embeddedsocial.event.click.ViewCoverImageEvent;
import com.microsoft.embeddedsocial.service.IntentExtras;
import com.microsoft.embeddedsocial.ui.activity.CommentActivity;
import com.microsoft.embeddedsocial.ui.activity.SignInActivity;
import com.microsoft.embeddedsocial.ui.activity.TopicActivity;
import com.microsoft.embeddedsocial.ui.activity.ViewImageActivity;
import com.squareup.otto.Subscribe;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Event listener for common actions in the SDK
 */
public class CommonBehaviorEventListener {
    public static final int REQUESTCODE_SIGN_IN = 1001;

    private final Activity activity;
    private final Fragment fragment;

    public CommonBehaviorEventListener(Activity activity) {
        this.activity = activity;
        fragment = null;
    }

    public CommonBehaviorEventListener(Fragment fragment) {
        this.activity = fragment.getActivity();
        this.fragment = fragment;
    }

    @Subscribe
    public void onOpenTopic(OpenTopicEvent topicEvent) {
        Intent intent = new Intent(activity.getApplication(), TopicActivity.class);
        intent.putExtra(IntentExtras.TOPIC_EXTRA, topicEvent.getTopic());
        intent.putExtra(IntentExtras.TOPIC_HANDLE, topicEvent.getTopic().getHandle());
        intent.putExtra(IntentExtras.JUMP_TO_EDIT, topicEvent.jumpToEdit());
        activity.startActivity(intent);
    }

    @Subscribe
    public void onViewCoverImage(ViewCoverImageEvent viewCoverImageEvent) {
        Intent intent = new Intent(activity.getApplication(), ViewImageActivity.class);
        intent.putExtra(
                IntentExtras.COVER_IMAGE_URL_EXTRA,
                viewCoverImageEvent.getTopic().getImageLocation().getOriginalUrl());
        activity.startActivity(intent);
    }

    @Subscribe
    public void onViewCommentCoverImage(ViewCommentCoverImageEvent viewCommentCoverImageEvent) {
        Intent intent = new Intent(activity.getApplication(), ViewImageActivity.class);
        intent.putExtra(
                IntentExtras.COVER_IMAGE_URL_EXTRA,
                viewCommentCoverImageEvent.getComment().getImageLocation().getOriginalUrl());
        activity.startActivity(intent);
    }

    @Subscribe
    public void onOpenComment(OpenCommentEvent commentEvent) {
        Intent intent = new Intent(activity.getApplication(), CommentActivity.class);
        intent.putExtra(IntentExtras.COMMENT_EXTRA, commentEvent.getComment());
        intent.putExtra(IntentExtras.COMMENT_HANDLE, commentEvent.getComment().getHandle());
        intent.putExtra(IntentExtras.JUMP_TO_EDIT, commentEvent.jumpToEdit());
        activity.startActivity(intent);
    }

    @Subscribe
    public void onOpenUserProfileEvent(OpenUserProfileEvent openUserProfileEvent) {
        ProfileOpenHelper.openUserProfile(activity, openUserProfileEvent.getUser());
    }

    @Subscribe
    public void onSignInRequest(RequestSignInEvent event) {
        onSignInRequest(event.getAuthorizationCause());
    }

    protected void onSignInRequest(AuthorizationCause authorizationCause) {
        Intent intent = new Intent(activity, SignInActivity.class);
        EnumUtils.putValue(intent, IntentExtras.REASON_TO_SIGN_IN, authorizationCause);

        // Give the fragment a chance to handle the onActivityResult if possible
        if (fragment != null) {
            fragment.startActivityForResult(intent, REQUESTCODE_SIGN_IN);
        } else {
            activity.startActivityForResult(intent, REQUESTCODE_SIGN_IN);
        }
    }
}
