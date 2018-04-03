/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.model;

import android.text.TextUtils;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.microsoft.embeddedsocial.data.storage.DbSchemas;
import com.microsoft.embeddedsocial.autorest.models.PublisherType;

import java.io.Serializable;

/**
 * Encapsulates data for a new post.
 */
@DatabaseTable(tableName = DbSchemas.AddedPosts.TABLE_NAME)
public class AddPostData implements Serializable {

	@DatabaseField(generatedId = true)
	private int id; // used only in the local database

	@DatabaseField
	private String title;

	@DatabaseField
	private PublisherType publisherType;

	@DatabaseField
	private String description;

	@DatabaseField
	private String imagePath;

	/**
	 * Default constructor for ORM.
	 */
	@SuppressWarnings("unused")
	AddPostData() {  }

	/**
	 * Creates an instance.
	 * @param title			post title
	 * @param description	post description (might be empty)
	 * @param imagePath		path to attached image (might be empty)
	 */
	public AddPostData(String title, String description, String imagePath, PublisherType publisherType) {
		this.description = description;
		this.title = title;
		this.imagePath = imagePath;
		this.publisherType = publisherType;
	}

	public int getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getImagePath() {
		return imagePath;
	}

	public PublisherType getPublisherType() {
		return publisherType;
	}

	/**
	 * Whether am image was added to the post (topic).
	 */
	public boolean hasImage() {
		return !TextUtils.isEmpty(imagePath);
	}

	public String getTitle() {
		return title;
	}
}
