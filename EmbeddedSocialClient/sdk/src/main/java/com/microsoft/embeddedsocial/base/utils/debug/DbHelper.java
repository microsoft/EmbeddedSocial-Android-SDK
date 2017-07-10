/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.utils.debug;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/** This class is used to import and export SQLite databases. */
public class DbHelper {
	private final File dbFolder;
	private final File externalDbFolder;

	private enum CopyMode { EXPORT, IMPORT }
	
	/** Main constructor.
	 * @param context				application context which is used to get DB folder automatically
	 * @param externalFolderName	name of the folder on SD Card, where exported files will be saved */
	public DbHelper(Context context, String externalFolderName) {
		String dbPath = Environment.getDataDirectory() + "/data/" +
			context.getPackageName() + "/databases/";
		dbFolder = new File(dbPath);
		externalDbFolder = new File(Environment.getExternalStorageDirectory(), externalFolderName);
	}
	
	/** exports all db files found in application database directory. */	
	public boolean exportDbs() {
		return moveDbs(CopyMode.EXPORT);
	}
	
	/** imports all db files found in external database directory.
	 * must be called BEFORE any connections to db are established. */
	public boolean importDbs() {
		return moveDbs(CopyMode.IMPORT);
	}
	
	/** Moves all databases from one folder to another depending on mode.
	 * @param mode	the mode which defines direction of move
	 * @return		true if databases were moved successfully */
	private boolean moveDbs(CopyMode mode) {
		File sourceDir = (mode == CopyMode.EXPORT) ? dbFolder : externalDbFolder;
		File destDir = (mode == CopyMode.EXPORT) ? externalDbFolder : dbFolder;
		if (!destDir.exists()) {
			if (!destDir.mkdirs()) {
				return false;
			}
		}
		File[] oldFiles = destDir.listFiles();
		if(oldFiles != null)
			for (File file : oldFiles)
				file.delete();
		String[] files = sourceDir.list();
		if (files != null && files.length > 0) {
			for (String dbFile : files) {
				copyDbFile(dbFile, sourceDir, destDir);
			}
		} else {
			return false;
		}
		
		return true;
	}
	
	/** Copies a single file from source dir to dest dir.
	 * @param file			file name
	 * @param sourceFolder	source folder
	 * @param destFolder	destination folder
	 * @return				true if operation was successful, false otherwise */
	private boolean copyDbFile(String file, File sourceFolder, File destFolder) {
		try {
			File sourceFile = new File(sourceFolder, file);
			File destFile = new File(destFolder, file);
			if (destFile.createNewFile()) {
				copyFile(sourceFile, destFile);
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}

	/** Copies the source file to the dest file
	 * @param src			source file
	 * @param dst			destination file
	 * @throws IOException 	if IOException occurs while copying*/
	@SuppressLint("NewApi")
	private void copyFile(File src, File dst) throws IOException {
		try (FileChannel inChannel = new FileInputStream(src).getChannel();
		     FileChannel outChannel = new FileOutputStream(dst).getChannel()) {

			inChannel.transferTo(0, inChannel.size(), outChannel);
		}
	}
}