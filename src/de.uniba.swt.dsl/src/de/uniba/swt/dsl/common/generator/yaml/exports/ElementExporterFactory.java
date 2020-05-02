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

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.ExtraBlockElement;
import de.uniba.swt.dsl.common.util.PointAspect;
import de.uniba.swt.dsl.common.util.YamlExporter;

public class ElementExporterFactory {
    public static <T> void build(YamlExporter exporter, T obj) {
        if (obj instanceof SegmentElement) {
            new SegmentElementYamlExporter().build(exporter, (SegmentElement) obj);
            return;
        }

        if (obj instanceof RegularSignalElement) {
            new RegularSignalElementYamlExporter().build(exporter, (RegularSignalElement) obj);
            return;
        }

        if (obj instanceof CompositionSignalElement) {
            new CompositionSignalElementYamlExporter().build(exporter, (CompositionSignalElement) obj);
            return;
        }

        if (obj instanceof AspectElement) {
            new AspectElementYamlExporter().build(exporter, (AspectElement) obj);
            return;
        }

        if (obj instanceof PointElement) {
            new PointElementYamlExporter().build(exporter, (PointElement) obj);
            return;
        }

        if (obj instanceof PointAspect) {
            new PointAspectYamlExporter().build(exporter, (PointAspect) obj);
            return;
        }

        if (obj instanceof BoardElement) {
            new BoardElementYamlExporter().build(exporter, (BoardElement) obj);
            return;
        }

        if (obj instanceof BoardFeatureElement) {
            new BoardFeatureElementYamlExporter().build(exporter, (BoardFeatureElement) obj);
            return;
        }

        if (obj instanceof TrainElement) {
            new TrainElementYamlExporter().build(exporter, (TrainElement) obj);
            return;
        }

        if (obj instanceof TrainPeripheral) {
            new TrainPeripheralYamlExporter().build(exporter, (TrainPeripheral) obj);
            return;
        }

        if (obj instanceof ExtraBlockElement) {
            new ExtraBlockElementYamlExporter().build(exporter, (ExtraBlockElement) obj);
            return;
        }

        if (obj instanceof CrossingElement) {
            new CrossingElementYamlExporter().build(exporter, (CrossingElement) obj);
            return;
        }

        if (obj instanceof SignalType) {
            new SignalTypeYamlExporter().build(exporter, (SignalType) obj);
            return;
        }
    }
}
