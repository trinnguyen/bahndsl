package de.uniba.swt.dsl.tests;

import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource.Diagnostic;
import org.junit.jupiter.api.Assertions;

public class ParserTestHelper {
	
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
