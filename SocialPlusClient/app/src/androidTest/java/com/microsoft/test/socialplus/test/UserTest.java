/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 *
 */

package com.microsoft.test.socialplus.test;

import android.text.TextUtils;

import com.microsoft.autorest.models.Reason;
import com.microsoft.autorest.models.Visibility;
import com.microsoft.socialplus.server.IAccountService;
import com.microsoft.socialplus.server.IAuthenticationService;
import com.microsoft.socialplus.server.IReportService;
import com.microsoft.socialplus.server.SocialPlusServiceProvider;
import com.microsoft.socialplus.server.exception.ResourceAlreadyExistsException;
import com.microsoft.socialplus.server.model.UserRequest;
import com.microsoft.socialplus.server.model.account.GetUserAccountRequest;
import com.microsoft.socialplus.server.model.account.GetUserAccountResponse;
import com.microsoft.socialplus.server.model.account.GetUserProfileRequest;
import com.microsoft.socialplus.server.model.account.GetUserProfileResponse;
import com.microsoft.socialplus.server.model.account.GetUserRequest;
import com.microsoft.socialplus.server.model.account.GetUserResponse;
import com.microsoft.socialplus.server.model.account.UpdateUserPublicAccountInfoRequest;
import com.microsoft.socialplus.server.model.account.UpdateUserVisibilityRequest;
import com.microsoft.socialplus.server.model.auth.AuthenticationResponse;
import com.microsoft.socialplus.server.model.auth.SignOutRequest;
import com.microsoft.socialplus.server.model.report.ReportUserRequest;
import com.microsoft.socialplus.server.model.view.UserAccountView;
import com.microsoft.socialplus.server.model.view.UserCompactView;
import com.microsoft.socialplus.server.model.view.UserProfileView;
import com.microsoft.test.socialplus.TestConstants;

public class UserTest extends BaseRestServicesTest {
	
	IAccountService accountService;
	IAuthenticationService authenticationService;
	IReportService reportService;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		SocialPlusServiceProvider serviceProvider = getServiceProvider();
		accountService = serviceProvider.getAccountService();
		authenticationService = serviceProvider.getAuthenticationService();
		reportService = serviceProvider.getReportService();
	}

	public void testCreateDeleteUser() throws Exception {

		try {
			AuthenticationResponse response = createRandomUser();
			assertFalse("user handle is empty in response", TextUtils.isEmpty(response.getUserHandle()));
			assertFalse("session token signature is empty", TextUtils.isEmpty(response.getSessionTokenSignature()));

			deleteUser(response);
		} catch (ResourceAlreadyExistsException e) {
			//user already exists
		}
	}

	public void testSignOut() throws Exception {
		AuthenticationResponse response = createRandomUser();
		SignOutRequest signOutRequest = prepareUserRequest(new SignOutRequest(), response);
		authenticationService.signOut(signOutRequest);
	}

	public void testGetUserAccount() throws Exception {
		AuthenticationResponse authenticationResponse = createRandomUser();
		GetUserAccountRequest userRequest = prepareUserRequest(new GetUserAccountRequest(), authenticationResponse);
		GetUserAccountResponse userAccount = accountService.getUserAccount(userRequest);
		UserAccountView user = userAccount.getUser();
		assertNotNull("user data is null", user);
		assertEquals(user.getHandle(), authenticationResponse.getUserHandle());
		assertNotNull("user first name is empty", user.getFirstName());
		assertNotNull("user last name is empty", user.getLastName());
//		assertNotNull("user email is empty", user.getEmail()); //TODO email is unused
		assertNotNull("username is empty", user.getUsername());
	}

	public void testGetUser() throws Exception {
		AuthenticationResponse authenticationResponse = createRandomUser();
		GetUserRequest getUserRequest
				= prepareUserRequest(new GetUserRequest(authenticationResponse.getUserHandle()), authenticationResponse);
		GetUserResponse response = accountService.getUser(getUserRequest);
		UserCompactView user = response.getUser();
		assertNotNull("empty user in response", user);
		assertNotNull("empty user handle in response", user.getHandle());
		assertNotNull("empty user name in response", user.getFirstName());
		assertNotNull("empty user last name in response", user.getLastName());
	}

	public void testGetUserProfile() throws Exception {
		AuthenticationResponse authenticationResponse = createRandomUser();
		GetUserProfileRequest request
				= new GetUserProfileRequest(authenticationResponse.getUserHandle(), TestConstants.USERNAME);
		prepareUserRequest(request, authenticationResponse);
		GetUserProfileResponse response = accountService.getUserProfile(request);
		UserProfileView user = response.getUser();
		assertNotNull("empty user in response", user);
		assertNotNull("empty user handle in response", user.getHandle());
		assertNotNull("empty user name in response", user.getFirstName());
		assertNotNull("empty user last name in response", user.getLastName());
	}

	public void testUpdateUserPublicAccountInfo() throws Exception {
		AuthenticationResponse authenticationResponse = createRandomUser();
		UpdateUserPublicAccountInfoRequest request = new UpdateUserPublicAccountInfoRequest(
				TestConstants.NAME,
				TestConstants.NAME,
				TestConstants.BIO
		);
		accountService.updateUserPublicAccountInfo(prepareUserRequest(request, authenticationResponse));
	}

	public void testUpdateUserVisibility() throws Exception {
		AuthenticationResponse authenticationResponse = createRandomUser();
		UpdateUserVisibilityRequest request
				= new UpdateUserVisibilityRequest(Visibility.PUBLIC);
		accountService.updateUserVisibility(prepareUserRequest(request, authenticationResponse));
	}

	public void testReportUser() throws Exception {
		AuthenticationResponse authenticationResponse = createRandomUser();
		AuthenticationResponse randomUser = createRandomUser();
		try {
			ReportUserRequest reportUserRequest =
					new ReportUserRequest(randomUser.getUserHandle(), Reason.SPAM);
			reportService.reportUser(prepareUserRequest(reportUserRequest, authenticationResponse));
		} finally {
			try {
				deleteUser(randomUser);
			} catch (Exception e) {
				//ignore
			}
		}
	}
}
