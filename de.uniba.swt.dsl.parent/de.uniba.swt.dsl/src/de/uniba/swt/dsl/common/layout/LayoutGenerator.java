package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.layout.models.*;
import de.uniba.swt.dsl.common.util.LogHelper;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import java.util.stream.Collectors;

public class LayoutGenerator {
	
	private final static Logger logger = Logger.getLogger(LayoutGenerator.class);

	private NetworkLayoutBuilder networkLayoutBuilder = new NetworkLayoutBuilder();
	private NetworkRoutesExplorer routesExplorer = new NetworkRoutesExplorer();
	private DotExporter dotExporter = new DotExporter();
	private InterlockingYamlExporter yamlExporter = new InterlockingYamlExporter();

	public void run(IFileSystemAccess2 fsa, RootModule rootModule) {
		var layoutProp = rootModule.getProperties().stream().filter(p -> p instanceof LayoutProperty).map(p -> (LayoutProperty)p).findFirst();
		layoutProp.ifPresent(moduleProperty -> buildLayout(fsa, rootModule, moduleProperty));
	}

	private void buildLayout(IFileSystemAccess2 fsa, RootModule rootModule, LayoutProperty layoutProp) {
		try {
			// network
			var networkLayout = networkLayoutBuilder.build(layoutProp);
			logger.debug(networkLayout);

			// generate graph
			var graph = networkLayout.generateGraph();
			fsa.generateFile(rootModule.getName() + "_diagram.dot", dotExporter.render(rootModule.getName(), graph));

			// find all routes
			var signals = getAllSignals(rootModule);
			if (signals == null) {
				logger.warn("No defined signal");
				return;
			}

			var routes = routesExplorer.findAllRoutes(networkLayout, signals.stream().map(SignalElement::getName).collect(Collectors.toSet()));
			logger.debug(LogHelper.printObject(routes));

			// generate yaml
			fsa.generateFile("interlocking_table.yml", yamlExporter.generate(routes));

		} catch (LayoutException e) {
			throw new RuntimeException(e);
		}
	}

	private EList<SignalElement> getAllSignals(RootModule rootModule) {
		var signalsProp = rootModule.getProperties().stream().filter(p -> p instanceof SignalsProperty).map(p -> (SignalsProperty)p).findFirst();
		return signalsProp.map(SignalsProperty::getItems).orElse(null);
	}
}
