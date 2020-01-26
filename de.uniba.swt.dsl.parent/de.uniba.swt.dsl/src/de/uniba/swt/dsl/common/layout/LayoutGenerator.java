package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.layout.models.*;
import de.uniba.swt.dsl.common.layout.models.graph.AbstractEdge;
import de.uniba.swt.dsl.common.util.LogHelper;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import java.util.*;
import java.util.stream.Collectors;

public class LayoutGenerator {
	
	private final static Logger logger = Logger.getLogger(LayoutGenerator.class);

	private NetworkLayoutBuilder networkLayoutBuilder = new NetworkLayoutBuilder();
	private RoutesFinder routesFinder = new RoutesFinder();
	private DotExporter dotExporter = new DotExporter();

	public void run(IFileSystemAccess2 fsa, RootModule rootModule) {
		var layoutProp = rootModule.getProperties().stream().filter(p -> p instanceof LayoutProperty).map(p -> (LayoutProperty)p).findFirst();
		layoutProp.ifPresent(moduleProperty -> buildLayout(fsa, rootModule, moduleProperty));
	}

	private void buildLayout(IFileSystemAccess2 fsa, RootModule rootModule, LayoutProperty layoutProp) {
		try {
			// network
			var networkLayout = networkLayoutBuilder.build(layoutProp);
			logger.info(networkLayout);

			// generate graph
			var graph = networkLayout.generateGraph();

			// generate dot layout
			fsa.generateFile(rootModule.getName() + "_diagram.dot", dotExporter.render(rootModule.getName(), graph));

			// find all routes
			var signals = getAllSignals(rootModule);
			List<Route> routes = new ArrayList<>();
			if (signals != null) {
				for (int i = 0; i < signals.size(); i++) {
					for (int j = 0; j < signals.size(); j++) {
						if (i == j)
							continue;

						var src = signals.get(i).getName();
						var dest = signals.get(j).getName();
						var paths = routesFinder.findAllRoutes(networkLayout, src, dest);
						if (paths != null && !paths.isEmpty()) {
							routes.addAll(paths);
						}
					}
				}

			}

			logger.info(LogHelper.printObject(routes));

		} catch (LayoutException e) {
			throw new RuntimeException(e);
		}
	}

	private EList<SignalElement> getAllSignals(RootModule rootModule) {
		var signalsProp = rootModule.getProperties().stream().filter(p -> p instanceof SignalsProperty).map(p -> (SignalsProperty)p).findFirst();
		return signalsProp.map(SignalsProperty::getItems).orElse(null);

	}
}
