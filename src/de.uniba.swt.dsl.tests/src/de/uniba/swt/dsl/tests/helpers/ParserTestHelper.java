/*
 *
 * Copyright (C) 2020 University of Bamberg, Software Technologies Research Group
 * <https://www.uni-bamberg.de/>, <http://www.swt-bamberg.de/>
 *
 * This file is part of the BahnDSL project, a domain-specific language
 * for configuring and modelling model railways.
 *
 * BahnDSL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BahnDSL is a RESEARCH PROTOTYPE and distributed WITHOUT ANY WARRANTY, without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details.
 *
 * The following people contributed to the conception and realization of the
 * present BahnDSL (in alphabetic order by surname):
 *
 * - Tri Nguyen <https://github.com/trinnguyen>
 *
 */

package de.uniba.swt.dsl.tests.helpers;

import java.util.stream.Collectors;

import com.google.inject.Inject;
import de.uniba.swt.dsl.bahn.BahnModel;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.Assertions;

public class ParserTestHelper {

	@Inject
	private ParseHelper<BahnModel> parseHelper;

	public BahnModel parseModel(CharSequence text) {
		try {
			return parseHelper.parse(text);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void assertNoParsingErrors(CharSequence text) {
		var model = parseModel(text);
		assertNoParsingErrors(model);
	}
	
	public void assertNoParsingErrors(EObject model) {
		// not null
		Assertions.assertNotNull(model);
		
		// errors
		assertNoIssues(model.eResource().getErrors());
	}
	
	public void assertNoParsingWarnings(EObject model) {
		// not null
		Assertions.assertNotNull(model);
		
		// errors
		assertNoIssues(model.eResource().getWarnings());
	}
	
	public void assertParsingError(EObject model, String expectedErrorMsg) {
		// not null
		Assertions.assertNotNull(model);
		
		// check having error
		assertParsingIssue(model.eResource().getErrors(), expectedErrorMsg);
	}
	
	public void assertParsingWarning(EObject model, String expectedErrorMsg) {
		// not null
		Assertions.assertNotNull(model);
		
		// check having error
		assertParsingIssue(model.eResource().getWarnings(), expectedErrorMsg);
	}
	
	private void assertParsingIssue(EList<Diagnostic> issues, String expectedErrorMsg) {
		String errorMsg = "Expected parsing error";
		if (expectedErrorMsg != null) {
			errorMsg += ": " + expectedErrorMsg;
		}
		
		// ensure no empty
		if (issues.isEmpty()) 
			Assertions.fail(errorMsg);
		
		// check message
		if (expectedErrorMsg != null) {
			if (issues.stream().anyMatch(i -> i.getMessage().toLowerCase().contains(expectedErrorMsg.toLowerCase())))
				return;
			
			errorMsg += ", but actual: " + issues.stream().map(i -> i.getMessage()).collect(Collectors.joining(","));
			Assertions.fail(errorMsg);
		}
	}
	
	private void assertNoIssues(EList<Diagnostic> issues)
	{
		if (!issues.isEmpty())
			Assertions.fail("Unexpected errors: " + generateErrorString(issues));
	}

	private String generateErrorString(EList<Diagnostic> issues) {
		return issues.stream().map(e -> e.getMessage()).collect(Collectors.joining(","));
	}
}
