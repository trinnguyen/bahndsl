package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.generator.GeneratorProvider;
import de.uniba.swt.dsl.common.layout.models.LayoutException;
import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.common.util.LogHelper;
import org.apache.log4j.Logger;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class LayoutGenerator extends GeneratorProvider {

	private static final String InterlockingFileName = "interlocking_table.yml";

	private final static Logger logger = Logger.getLogger(LayoutGenerator.class);

	private NetworkLayoutBuilder networkLayoutBuilder = new NetworkLayoutBuilder();
	private NetworkRoutesExplorer routesExplorer = new NetworkRoutesExplorer();
	private DotExporter dotExporter = new DotExporter();
	private InterlockingYamlExporter yamlExporter = new InterlockingYamlExporter();
	private NetworkLayout networkLayout;

	@Override
	protected String[] generatedFileNames() {
		return new String[] { InterlockingFileName };
	}

	@Override
	protected void execute(IFileSystemAccess2 fsa, BahnModel bahnModel) {
		var rootModule = BahnUtil.getRootModule(bahnModel);
		if (rootModule == null)
			return;

		var layoutProp = rootModule.getProperties().stream().filter(p -> p instanceof LayoutProperty).map(p -> (LayoutProperty)p).findFirst();
		layoutProp.ifPresent(moduleProperty -> buildLayout(fsa, rootModule, moduleProperty));
	}

	public NetworkLayout getNetworkLayout() {
		return networkLayout;
	}

	private void buildLayout(IFileSystemAccess2 fsa, RootModule rootModule, LayoutProperty layoutProp) {
		if (layoutProp.getItems().isEmpty())
			return;
		
		try {
			// network
			networkLayout = networkLayoutBuilder.build(layoutProp);
			logger.debug(networkLayout);

			// generate graph
			var graph = networkLayout.generateGraph();
			fsa.generateFile("layout_diagram.dot", dotExporter.render(networkLayout, graph));

			// find all routes
			var signals = getAllSignals(rootModule);
			if (signals == null) {
				logger.warn("No defined signal");
				return;
			}

			var routes = routesExplorer.findAllRoutes(networkLayout, signals);
			logger.debug(LogHelper.printObject(routes));

			// generate yaml
			fsa.generateFile(InterlockingFileName, yamlExporter.generate(routes));

		} catch (LayoutException e) {
			throw new RuntimeException(e);
		}
	}

	private Set<String> getAllSignals(RootModule rootModule) {
		return rootModule.getProperties()
				.stream()
				.filter(p -> p instanceof SignalsProperty)
				.map(p -> ((SignalsProperty) p).getItems())
				.flatMap(Collection::stream)
				.map(SignalElement::getName)
				.collect(Collectors.toSet());
	}
}
