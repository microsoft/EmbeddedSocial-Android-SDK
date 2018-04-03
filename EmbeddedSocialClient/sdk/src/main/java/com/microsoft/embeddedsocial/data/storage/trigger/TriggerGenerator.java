/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.data.storage.trigger;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Is used for automated trigger generation.
 */
public class TriggerGenerator {

	private TriggerGenerator() {  }

	/**
	 * Creates a new trigger that is launched after delete on a table.
	 * @param triggerName   trigger name
	 * @param tableName     table name
	 * @param statements    trigger body
	 * @return  trigger instance.
	 */
	public static ISqlTrigger newOnAfterDeleteTrigger(String triggerName, String tableName,
	                                                  String... statements) {

		return newOnActionTrigger(triggerName, tableName, DatabaseAction.AFTER_DELETE, statements);
	}

	/**
	 * Creates a new trigger that is launched before insert on a table.
	 * @param triggerName   trigger name
	 * @param tableName     table name
	 * @param statements    trigger body
	 * @return  trigger instance.
	 */
	public static ISqlTrigger newOnBeforeInsertTrigger(String triggerName, String tableName,
	                                                   String... statements) {

		return newOnActionTrigger(triggerName, tableName, DatabaseAction.BEFORE_INSERT, statements);
	}

	/**
	 * Creates a new trigger that is launched after insert on a table.
	 * @param triggerName   trigger name
	 * @param tableName     table name
	 * @param statements    trigger body
	 * @return  trigger instance.
	 */
	public static ISqlTrigger newOnAfterInsertTrigger(String triggerName, String tableName,
	                                                  String... statements) {

		return newOnActionTrigger(triggerName, tableName, DatabaseAction.AFTER_INSERT, statements);
	}

	/**
	 * Creates a new trigger that is launched after the specified action on a table.
	 * @param triggerName       trigger name
	 * @param tableName         table name
	 * @param databaseAction    the action that launches the trigger
	 * @param statements        trigger body
	 * @return  trigger instance.
	 */
	public static ISqlTrigger newOnActionTrigger(String triggerName, String tableName,
	                                             DatabaseAction databaseAction,
	                                             String... statements) {

		return new TriggerBuilder(triggerName, tableName)
			.setAction(databaseAction)
			.setStatements(statements)
			.build();
	}

	/**
	 * Is used to build SQL triggers.
	 */
	public static class TriggerBuilder {

		private final String triggerName;
		private final String tableName;
		private String whenClause;
		private List<String> statements = new ArrayList<>();
		private DatabaseAction action;

		/**
		 * Creates an instance.
		 * @param triggerName   name of the trigger to construct
		 * @param tableName     table name for the trigger
		 */
		public TriggerBuilder(String triggerName, String tableName) {
			this.triggerName = triggerName;
			this.tableName = tableName;
		}

		/**
		 * Sets trigger action
		 * @param   action    trigger action
		 * @return  this instance.
		 */
		public TriggerBuilder setAction(DatabaseAction action) {
			this.action = action;
			return this;
		}

		/**
		 * Sets trigger statements.
		 * @param statements    statements to execute
		 * @return  this instance.
		 */
		public TriggerBuilder setStatements(String... statements) {
			this.statements.clear();
			return addStatements(statements);
		}

		/**
		 * Sets 'when' condition.
		 * @param conditionClause   'when' condition clause
		 * @return  this instance.
		 */
		public TriggerBuilder setWhen(String conditionClause) {
			this.whenClause = conditionClause;
			return this;
		}

		/**
		 * Adds more statements to this trigger
		 * @param   statements    statements to add
		 * @return  this instance.
		 */
		public TriggerBuilder addStatements(String... statements) {
			this.statements.addAll(Arrays.asList(statements));
			return this;
		}

		/**
		 * Builds the trigger.
		 * @return  {@linkplain ISqlTrigger} instance.
		 */
		public ISqlTrigger build() {
			StringBuilder builder = new StringBuilder("create trigger ");
			builder.append(triggerName)
				.append("\n").append(action)
				.append(" on ").append(tableName);

			if (!TextUtils.isEmpty(whenClause)) {
				builder.append("\nwhen ").append(whenClause);
			}
			String statement = builder.append("\nbegin\n    ")
				.append(TextUtils.join(";\n     ", statements))
				.append(";\nend").toString();

			return () -> statement;
		}
	}

	/**
	 * Database action for triggers.
	 */
	public enum DatabaseAction {

		AFTER_DELETE("after delete"),
		AFTER_INSERT("after insert"),
		BEFORE_DELETE("before delete"),
		BEFORE_INSERT("before insert");

		private final String sqlValue;

		DatabaseAction(String sqlValue) {
			this.sqlValue = sqlValue;
		}

		@Override
		public String toString() {
			return sqlValue;
		}
	}
}
