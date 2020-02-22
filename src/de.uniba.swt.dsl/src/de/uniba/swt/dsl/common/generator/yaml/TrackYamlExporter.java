package de.uniba.swt.dsl.common.generator.yaml;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.generator.yaml.exports.ElementExporterFactory;
import de.uniba.swt.dsl.common.util.StringUtil;

import java.util.*;

class TrackYamlExporter extends AbstractBidibYamlExporter {
    @Override
    protected String getHeaderComment() {
        return "Track configuration";
    }

    @Override
    protected void exportContent(RootModule rootModule) {
        Map<String, Set<ModuleProperty>> map = buildMap(rootModule);

        appendLine("boards:");
        increaseLevel();
        for (Map.Entry<String, Set<ModuleProperty>> entry : map.entrySet()) {
            exportBoard(entry.getKey(), entry.getValue());
        }
        decreaseLevel();
    }

    private void exportBoard(String boardName, Set<ModuleProperty> properties) {
        appendLine("- id: %s", boardName);
        increaseLevel();

        List<Object> pointItems = new ArrayList<>();
        for (ModuleProperty property : properties) {
            if (property instanceof SegmentsProperty) {
                exportSection("segments:", ((SegmentsProperty) property).getItems());
            } else if (property instanceof SignalsProperty) {
                exportSection("signals-board:", ((SignalsProperty) property).getItems());
            } else if (property instanceof PointsProperty) {
                pointItems.addAll(((PointsProperty) property).getItems());
            } else if (property instanceof PeripheralsProperty) {
                pointItems.addAll(((PeripheralsProperty) property).getItems());
            }
        }

        // check points
        if (pointItems.size() > 0) {
            exportSection("points-board:", pointItems);
        }

        decreaseLevel();
    }

    private void exportSection(String section, List<?> items) {
        appendLine(section);
        increaseLevel();
        for (Object item : items) {
            ElementExporterFactory.build(this, item);
        }
        decreaseLevel();
    }

    private Map<String, Set<ModuleProperty>> buildMap(RootModule rootModule) {
        Map<String, Set<ModuleProperty>> map = new HashMap<>();
        for (ModuleProperty property : rootModule.getProperties()) {
            String boardName = null;
            if (property instanceof SegmentsProperty) {
                boardName = ((SegmentsProperty) property).getBoard().getName();
            } else if (property instanceof SignalsProperty) {
                boardName = ((SignalsProperty) property).getBoard().getName();
            } else if (property instanceof PeripheralsProperty) {
                boardName = ((PeripheralsProperty) property).getBoard().getName();
            } else if (property instanceof PointsProperty) {
                boardName = ((PointsProperty) property).getBoard().getName();
            }

            // check
            if (StringUtil.isNotEmpty(boardName)) {
                if (!map.containsKey(boardName)) {
                    var set = new HashSet<ModuleProperty>();
                    set.add(property);
                    map.put(boardName, set);
                } else {
                    map.get(boardName).add(property);
                }
            }
        }
        return map;
    }
}

