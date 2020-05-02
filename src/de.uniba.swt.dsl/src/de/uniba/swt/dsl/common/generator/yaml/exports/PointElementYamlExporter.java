/*
 * This file is part of the BahnDSL project, a domain-specific language
 * for configuring and modelling model railways
 *
 * BahnDSL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BahnDSL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BahnDSL.  If not, see <https://www.gnu.org/licenses/>.
 *
 * The following people contributed to the conception and realization of the
 * present BahnDSL (in alphabetic order by surname):
 *
 * - Tri Nguyen <https://github.com/trinnguyen>
 */

package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.PointAspectType;
import de.uniba.swt.dsl.bahn.PointElement;
import de.uniba.swt.dsl.common.util.PointAspect;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.List;
import java.util.Map;

public class PointElementYamlExporter extends AbstractElementYamlExporter<PointElement> {
    @Override
    protected String getId(PointElement element) {
        return element.getName();
    }

    @Override
    protected List<Tuple<String, Object>> getProps(PointElement element) {
        var aspects = List.of(
                new PointAspect(PointAspectType.NORMAL, element.getNormalValue()),
                new PointAspect(PointAspectType.REVERSE, element.getReverseValue()));
        return List.of(Tuple.of("number", element.getNumber()),
                Tuple.of("aspects", aspects),
                Tuple.of("initial", element.getInitial().getName().toLowerCase()),
                Tuple.of("segment", element.getMainSeg().getName()));
    }
}
