package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.layout.models.*;
import org.apache.log4j.Logger;
import org.eclipse.xtext.generator.IFileSystemAccess2;

public class LayoutGenerator {
	
	private final static Logger logger = Logger.getLogger(LayoutGenerator.class);

	private NetworkLayoutBuilder networkLayoutBuilder;

	public LayoutGenerator() {
		networkLayoutBuilder = new NetworkLayoutBuilder();
	}

	public void run(IFileSystemAccess2 fsa, RootModule rootModule) {
		var layoutProp = rootModule.getProperties().stream().filter(p -> p instanceof LayoutProperty).map(p -> (LayoutProperty)p).findFirst();
		layoutProp.ifPresent(moduleProperty -> buildLayout(rootModule, moduleProperty));
	}

	private void buildLayout(RootModule rootModule, LayoutProperty layoutProp) {
		try {
			var networkLayout = networkLayoutBuilder.build(layoutProp);
			logger.info(networkLayout);
		} catch (LayoutException e) {
			throw new RuntimeException(e);
		}
	}
}
