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

package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.fsa.FsaUtil;
import de.uniba.swt.dsl.common.generator.GeneratorProvider;
import de.uniba.swt.dsl.common.layout.models.LayoutException;
import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.layout.models.Route;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.common.util.LogHelper;
import org.apache.log4j.Logger;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LayoutGenerator extends GeneratorProvider {

	private static final String LayoutDiagramFileName = "layout_diagram.dot";

	private static final String InterlockingFileName = "interlocking_table.yml";

	private final static Logger logger = Logger.getLogger(LayoutGenerator.class);

	private final NetworkLayoutBuilder networkLayoutBuilder = new NetworkLayoutBuilder();
	private final NetworkRoutesExplorer routesExplorer = new NetworkRoutesExplorer();
	private final DotExporter dotExporter = new DotExporter();
	private final InterlockingYamlExporter yamlExporter = new InterlockingYamlExporter();
	private NetworkLayout networkLayout;

	@Override
	protected String[] generatedFileNames() {
		return new String[] { InterlockingFileName };
	}

	@Override
	protected void execute(IFileSystemAccess2 fsa, BahnModel bahnModel, String routeType) {
		var rootModule = BahnUtil.getRootModule(bahnModel);
		if (rootModule == null)
			return;

		var layoutProp = rootModule.getProperties().stream().filter(p -> p instanceof LayoutProperty).map(p -> (LayoutProperty)p).findFirst();
		layoutProp.ifPresent(moduleProperty -> buildLayout(fsa, rootModule, moduleProperty, routeType));
	}

	public NetworkLayout getNetworkLayout() {
		return networkLayout;
	}

	private void buildLayout(IFileSystemAccess2 fsa, RootModule rootModule, LayoutProperty layoutProp, String routeType) {
		if (layoutProp.getItems().isEmpty())
			return;
		
		try {
			// network
			networkLayout = networkLayoutBuilder.build(layoutProp);
			logger.debug(networkLayout);

			// generate graph
			var graph = networkLayout.generateGraph();
			dotExporter.export(fsa, LayoutDiagramFileName, networkLayout, graph);

			// find all routes
			var signals = getAllSignals(rootModule);
			if (signals == null) {
				logger.warn("No defined signal");
				return;
			}

			List<Route> routes = routesExplorer.findAllRoutes(networkLayout, signals, routeType);
			logger.debug(LogHelper.printObject(routes));

			// generate yaml
			yamlExporter.generate(fsa, InterlockingFileName, routes);

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
