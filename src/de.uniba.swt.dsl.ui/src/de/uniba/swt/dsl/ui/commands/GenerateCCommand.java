package de.uniba.swt.dsl.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import de.uniba.swt.dsl.generator.StandaloneApp;

public class GenerateCCommand extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		BahnCompileUtil.processCommand(event, StandaloneApp.MODE_C_CODE, StandaloneApp.ROUTE_SIMPLE);
		return null;
	}
	
}