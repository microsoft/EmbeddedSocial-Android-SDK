/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.socialplus.server;

import com.microsoft.socialplus.autorest.models.AppCompactView;
import com.microsoft.socialplus.server.exception.NetworkRequestException;
import com.microsoft.socialplus.server.model.UserRequest;
import com.microsoft.socialplus.server.model.account.CreateUserRequest;
import com.microsoft.socialplus.server.model.account.DeleteUserRequest;
import com.microsoft.socialplus.server.model.account.GetMyAppsRequest;
import com.microsoft.socialplus.server.model.account.GetUserAccountRequest;
import com.microsoft.socialplus.server.model.account.GetUserAccountResponse;
import com.microsoft.socialplus.server.model.account.GetUserProfileRequest;
import com.microsoft.socialplus.server.model.account.GetUserProfileResponse;
import com.microsoft.socialplus.server.model.account.GetUserRequest;
import com.microsoft.socialplus.server.model.account.GetUserResponse;
import com.microsoft.socialplus.server.model.account.LinkThirdPartyRequest;
import com.microsoft.socialplus.server.model.account.UnlinkUserThirdPartyAccountRequest;
import com.microsoft.socialplus.server.model.account.UpdateUserPhotoRequest;
import com.microsoft.socialplus.server.model.account.UpdateUserPublicAccountInfoRequest;
import com.microsoft.socialplus.server.model.account.UpdateUserVisibilityRequest;
import com.microsoft.socialplus.server.model.account.UserPasswordResponse;
import com.microsoft.socialplus.server.model.auth.AuthenticationResponse;
import com.microsoft.socialplus.server.model.auth.SignInWithThirdPartyRequest;

import java.util.List;

import retrofit2.Response;

/**
 * Interface for user account operations
 */
public interface IAccountService {

	// for tests only
	AuthenticationResponse createUser(CreateUserRequest request)
			throws NetworkRequestException;

	Response deleteUser(DeleteUserRequest request)
			throws NetworkRequestException;

	GetUserResponse getUser(GetUserRequest request)
			throws NetworkRequestException;

	GetUserAccountResponse getUserAccount(GetUserAccountRequest request)
			throws NetworkRequestException;

	GetUserProfileResponse getUserProfile(GetUserProfileRequest request)
			throws NetworkRequestException;

	Response linkUserThirdPartyAccount(LinkThirdPartyRequest request)
			throws NetworkRequestException;

	Response unlinkUserThirdPartyAccount(UnlinkUserThirdPartyAccountRequest request)
			throws NetworkRequestException;

	Response updateUserPhoto(UpdateUserPhotoRequest request)
			throws NetworkRequestException;

	Response updateUserPublicAccountInfo(UpdateUserPublicAccountInfoRequest request)
			throws NetworkRequestException;

	Response updateUserVisibility(UpdateUserVisibilityRequest request)
			throws NetworkRequestException;

	List<AppCompactView> getMyApps(GetMyAppsRequest request)
			throws NetworkRequestException;
}
