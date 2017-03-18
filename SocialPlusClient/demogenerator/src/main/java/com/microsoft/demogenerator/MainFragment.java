package com.microsoft.demogenerator;

import com.microsoft.embeddedsocial.auth.GoogleNativeAuthenticator;
import com.microsoft.embeddedsocial.auth.IAuthenticationCallback;
import com.microsoft.embeddedsocial.autorest.models.BlobType;
import com.microsoft.embeddedsocial.autorest.models.ContentType;
import com.microsoft.embeddedsocial.autorest.models.IdentityProvider;
import com.microsoft.embeddedsocial.autorest.models.ImageType;
import com.microsoft.embeddedsocial.autorest.models.PublisherType;
import com.microsoft.embeddedsocial.data.Preferences;
import com.microsoft.embeddedsocial.server.ImageUploader;
import com.microsoft.embeddedsocial.server.model.account.CreateUserRequest;
import com.microsoft.embeddedsocial.server.model.account.UpdateUserPhotoRequest;
import com.microsoft.embeddedsocial.server.model.auth.AuthenticationResponse;
import com.microsoft.embeddedsocial.server.model.content.comments.AddCommentRequest;
import com.microsoft.embeddedsocial.server.model.content.comments.AddCommentResponse;
import com.microsoft.embeddedsocial.server.model.content.replies.AddReplyRequest;
import com.microsoft.embeddedsocial.server.model.content.replies.AddReplyResponse;
import com.microsoft.embeddedsocial.server.model.content.topics.GetTopicNameRequest;
import com.microsoft.embeddedsocial.server.model.like.AddLikeRequest;
import com.microsoft.embeddedsocial.ui.util.SocialNetworkAccount;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainFragment extends Fragment implements IAuthenticationCallback {
    /**
     * TODO
     * Create the names of the users in the demo
     * Each user should have a first and last name separated by a space
     */
    public static final String[] USER_NAMES = {"Albert Einstein, Max Planck"};

    /**
     * TODO
     * Script goes here
     * Options are:
     *   Upload a user photo
     *   Get topic by name
     *   Post a comment (optional: with image)
     *   Post a reply
     *   Like content
     */
    public void createDemo() {
        User albert = users.get(0);
        User max = users.get(1);

        String topicHandle = getTopicFromTopicName("route_1_1_100113");
        String comment = createComment(albert, topicHandle, "This is a great route");
        String reply = createReply(max, comment, "I agree");
        like(albert, reply, ContentType.REPLY);

        printUserInfo();
    }

    private static final String KEY = "users";

    ArrayList<User> users;
    Button createUsersButton;
    Button createDemoButton;

    public MainFragment() {
        users = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY, users);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        createUsersButton = (Button)getView().findViewById(R.id.create_user);
        createUsersButton.setOnClickListener(v -> new GoogleNativeAuthenticator(this, this).startAuthenticationAsync());

        createDemoButton = (Button)getView().findViewById(R.id.create_demo);
        createDemoButton.setOnClickListener(v -> createDemoButtonHandler());

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY)) {
            users = savedInstanceState.getParcelableArrayList(KEY);
            if (users.size() == USER_NAMES.length) {
                createUsersButton.setVisibility(View.GONE);
                createDemoButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onAuthenticationSuccess(SocialNetworkAccount account) {
        new Thread(()->{
            //sign in with 3rd party
            String[] name = USER_NAMES[users.size()].split(" ");
            CreateUserRequest createUserRequest = new CreateUserRequest.Builder()
                    .setFirstName(name[0])
                    .setLastName(name[1])
                    .setIdentityProvider(IdentityProvider.GOOGLE)
                    .setAccessToken(account.getThirdPartyAccessToken())
                    .setRequestToken(account.getThirdPartyRequestToken())
                    .build();

            AuthenticationResponse response;
            try {
                response = createUserRequest.send();
            } catch(Exception e) {
                logError(e);
                return;
            }

            User newUser = new User(response.getUserHandle(), response.getSessionToken());
            users.add(newUser);

            if (users.size() == USER_NAMES.length) {
                // Use the last user's authorization as the default for uploading images
                Preferences.getInstance().setAuthorizationToken(newUser.authorization);

                Handler mainHandler = new Handler(getContext().getMainLooper());
                mainHandler.post(() -> createUsersButton.setVisibility(View.GONE));
                mainHandler.post(() -> createDemoButton.setVisibility(View.VISIBLE));
            }
        }).start();
    }

    @Override
    public void onAuthenticationError(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
    }

    public void createDemoButtonHandler() {
        new Thread(() -> createDemo()).start();
    }

    /**
     * Create a comment with an image
     * Returns the comment handle
     */
    public String createComment(User user, String topicHandle, String text, @DrawableRes int image) {
        String photoHandle = _uploadPhoto(image, ImageType.CONTENTBLOB);
        return _createComment(user, topicHandle, text, BlobType.IMAGE, photoHandle);
    }

    /**
     * Returns the comment handle
     */
    public String createComment(User user, String topicHandle, String text) {
        return _createComment(user, topicHandle, text, BlobType.UNKNOWN, null);
    }

    /**
     * Returns the reply handle
     */
    public String createReply(User user, String commentHandle, String text) {
        AddReplyRequest request = new AddReplyRequest(commentHandle, text);
        request.setAuthorization(user.authorization);

        AddReplyResponse response = null;
        try {
            response = request.send();
        } catch (Exception e) {
            logError(e);
        }

        return response.getReplyHandle();
    }

    public void like(User user, String contentHandle, ContentType contentType) {
        AddLikeRequest request = new AddLikeRequest(contentHandle, contentType);
        request.setAuthorization(user.authorization);
        try {
            request.send();
        } catch (Exception e) {
            logError(e);
        }
    }

    public String getTopicFromTopicName(String name) {
        GetTopicNameRequest request = new GetTopicNameRequest(name, PublisherType.APP);
        String result = null;
        try {
            return request.send();
        } catch (Exception e) {
            logError(e);
        }
        return result;
    }

    public void uploadProfilePhoto(User user, @DrawableRes int photoResource) {
        String photoHandle = _uploadPhoto(photoResource, ImageType.USERPHOTO);
        UpdateUserPhotoRequest request = new UpdateUserPhotoRequest(photoHandle);
        request.setAuthorization(user.authorization);
        try {
            request.send();
        } catch (Exception e) {
            logError(e);
        }
    }

    /**
     * Returns the comment handle
     */
    public String _createComment(User user, String topicHandle, String text, BlobType contentBlobType, String contentBlobUrl) {
        AddCommentRequest request = new AddCommentRequest(topicHandle, text, contentBlobType, contentBlobUrl);
        request.setAuthorization(user.authorization);

        AddCommentResponse response = null;
        try {
            response = request.send();
        } catch (Exception e) {
            logError(e);
        }

        return response.getCommentHandle();
    }

    /**
     * Returns the photo URL
     */
    public String _uploadPhoto(@DrawableRes int photoResource, ImageType imageType) {

        Uri photoUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + getContext().getPackageName() + "/"
                + getResources().getResourceTypeName(photoResource) + "/"
                + getResources().getResourceEntryName(photoResource));

        try {
            return ImageUploader.uploadImage(getContext(), photoUri, imageType);
        } catch (Exception e) {
            logError(e);
        }
        return null;
    }

    public void logError(Exception e) {
        e.printStackTrace();
        printUserInfo();
    }

    public void printUserInfo() {
        System.out.println("Don't forget to log the user authorization for clean up");
        for (User user : users) {
            System.out.println(user);
        }
    }
}
