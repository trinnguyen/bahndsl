package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.layout.models.*;
import de.uniba.swt.dsl.common.models.Signal;
import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.common.util.LogHelper;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.formatting.IFormatterExtension;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import javax.sound.sampled.BooleanControl;
import java.util.*;

public class LayoutProvider {
	
	private final static Logger logger = Logger.getLogger(LayoutProvider.class);

	private NetworkLayoutBuilder networkLayoutBuilder;

	public LayoutProvider() {
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
