/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for license information.
 */

package com.microsoft.embeddedsocial.base.expression;

import com.floreysoft.jmte.Engine;

import java.util.HashMap;
import java.util.Map;

/**
 * Allows to replace variables in an expression by their names.
 */
public class Template {

	private final Engine engine = new Engine();
	private final Map<String, Object> expressionParams = new HashMap<>();
	private final String expression;

	/**
	 * Creates a new instance.
	 * @param expression    template expression
	 */
	public Template(String expression) {
		this.expression = expression;
	}

	/**
	 * Adds template variable to replace in the expression.
	 * @param   name      variable name
	 * @param   value     variable value
	 * @return  <code>this</code> instance.
	 */
	public Template var(String name, Object value) {
		expressionParams.put(name, value);
		return this;
	}

	/**
	 * Renders the final expression where variable names are substituted with their values.
	 * @return  final expression.
	 */
	public String render() {
		return engine.transform(expression, expressionParams);
	}
}
