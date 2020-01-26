package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.layout.models.*;
import de.uniba.swt.dsl.common.layout.models.graph.AbstractEdge;
import org.apache.log4j.Logger;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import java.util.Set;
import java.util.Stack;
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
			var routes = routesFinder.findAllRoutes(networkLayout, "sig3", "sig2");
			logger.info(printRoutes(routes));

		} catch (LayoutException e) {
			throw new RuntimeException(e);
		}
	}

	private String printRoutes(Set<Stack<AbstractEdge>> routes) {
		return "routes " + routes.size()+ ": {\n" +
				routes
						.stream()
						.map(r -> r
								.stream()
								.map(Object::toString)
								.collect(Collectors.joining(" -> ")))
						.collect(Collectors.joining("\n"))
				+ "\n}";
	}
}
