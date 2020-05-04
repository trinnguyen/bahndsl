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

package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.common.util.Tuple;
import de.uniba.swt.dsl.common.util.YamlExporter;
import org.eclipse.emf.ecore.EObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;

abstract class AbstractElementYamlExporter<T> {

    public void build(YamlExporter exporter, T element) {
        exporter.appendLine("- %s: %s", getIdName(), getId(element));
        exporter.increaseLevel();

        // body
        var props = getProps(element);
        if (props != null && props.size() > 0)
        {
            for (Tuple<String, Object> entry : props) {

                if (entry.getSecond() == null)
                    continue;

                if (entry.getSecond() instanceof Collection) {
                    var collection = (Collection<?>) entry.getSecond();
                    if (collection.isEmpty())
                        continue;

                    exporter.appendLine("%s:", entry.getFirst());
                    exporter.increaseLevel();
                    for (Object item : collection) {
                        // simply print the value
                        if (isPrimitiveObj(item)) {
                            exporter.appendLine("- %s", item.toString());
                        } else {
                            ElementExporterFactory.build(exporter, item);
                        }
                    }
                    exporter.decreaseLevel();
                } else {
                    exporter.appendLine("%s: %s", entry.getFirst(), entry.getSecond());
                }
            }
        }
        //end body
        exporter.decreaseLevel();
    }

    private static boolean isPrimitiveObj(Object item) {
        if (item instanceof Number)
            return true;

        if (item instanceof String)
            return true;

        return item instanceof Boolean;
    }

    protected String getIdName() {
        return "id";
    }

    protected abstract String getId(T element);

    protected abstract List<Tuple<String, Object>> getProps(T element);
}
