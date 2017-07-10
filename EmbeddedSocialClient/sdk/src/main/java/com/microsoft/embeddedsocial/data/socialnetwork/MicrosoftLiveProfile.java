/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.socialnetwork;

/**
 * Encapsulates part of profile for Microsoft Live social network.
 */
public class MicrosoftLiveProfile {
	private String id;
	private String first_name;
	private String last_name;
	private ProfileEmails emails;

	private MicrosoftLiveError error;

	public String getId() {
		return id;
	}

	public String getFirstName() {
		return first_name;
	}

	public String getLastName() {
		return last_name;
	}

	public String getPreferredEmail() {
		return (emails == null) ? null : emails.getPreferred();
	}

	public MicrosoftLiveError getError() {
		return error;
	}

	public class ProfileEmails {
		private String account;
		private String preferred;
		private String business;
		private String personal;

		public String getAccount() {
			return account;
		}

		public String getPreferred() {
			return preferred;
		}

		public String getBusiness() {
			return business;
		}

		public String getPersonal() {
			return personal;
		}
	}

}
