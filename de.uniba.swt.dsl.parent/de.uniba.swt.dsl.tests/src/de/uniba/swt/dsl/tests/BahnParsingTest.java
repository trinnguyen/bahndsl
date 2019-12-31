package de.uniba.swt.dsl.tests;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.inject.Inject;

import de.uniba.swt.dsl.bahn.ModuleObject;

@ExtendWith(InjectionExtension.class)
@InjectWith(BahnInjectorProvider.class)
public class BahnParsingTest {
	
	@Inject
	ParseHelper<ModuleObject> parseHelper;
	
    @Test
    void testSuccess() {
    	
    }
}
