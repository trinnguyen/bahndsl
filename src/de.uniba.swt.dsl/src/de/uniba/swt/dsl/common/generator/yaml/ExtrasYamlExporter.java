package de.uniba.swt.dsl.common.generator.yaml;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.layout.models.vertex.AbstractVertexMember;
import de.uniba.swt.dsl.common.layout.models.vertex.LayoutVertex;
import de.uniba.swt.dsl.common.layout.models.vertex.VertexMemberType;
import de.uniba.swt.dsl.common.util.ExtraBlockElement;
import de.uniba.swt.dsl.common.util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

public class ExtrasYamlExporter extends AbstractBidibYamlExporter {
    private NetworkLayout networkLayout;

    public String export(RootModule rootModule, NetworkLayout networkLayout) {
        this.networkLayout = networkLayout;
        return export(rootModule);
    }

    @Override
    protected String getHeaderComment() {
        return "Block layout configuration";
    }

    @Override
    protected void exportContent(RootModule rootModule) {

        Map<String, Set<String>> mapSignals = getMapSignals(networkLayout);

        Set<ExtraBlockElement> blocks = rootModule.getProperties().stream().filter(p -> p instanceof BlocksProperty).map(p -> ((BlocksProperty) p).getItems()).flatMap(Collection::stream).map(b -> createExtraItem(b, mapSignals)).collect(Collectors.toSet());
        Set<ExtraBlockElement> platforms = rootModule.getProperties().stream().filter(p -> p instanceof PlatformsProperty).map(p -> ((PlatformsProperty) p).getItems()).flatMap(Collection::stream).map(b -> createExtraItem(b, mapSignals)).collect(Collectors.toSet());
        Set<CrossingElement> crossings = rootModule.getProperties().stream().filter(p -> p instanceof CrossingsProperty).map(p -> ((CrossingsProperty) p).getItems()).flatMap(Collection::stream).collect(Collectors.toSet());

        // blocks
        exportSection("blocks:", blocks);
        exportSection("platforms:", platforms);
        exportSection("crossings:", crossings);
    }

    private Map<String, Set<String>> getMapSignals(NetworkLayout networkLayout) {
        Map<String, Set<String>> map = new HashMap<>();
        for (LayoutVertex vertex : networkLayout.getVertices()) {
            String blockName = null;
            Set<String> signals = new HashSet<>();
            for (AbstractVertexMember member : vertex.getMembers()) {
                if (member.getType() == VertexMemberType.Block) {
                    blockName = member.getName();
                } else if (member.getType() == VertexMemberType.Signal) {
                    signals.add(member.getKey());
                }
            }

            // update
            if (StringUtil.isNotEmpty(blockName)) {
                if (!map.containsKey(blockName)) {
                    map.put(blockName, signals);
                } else {
                    map.get(blockName).addAll(signals);
                }
            }
        }

        return map;
    }

    private ExtraBlockElement createExtraItem(BlockElement blockElement, Map<String, Set<String>> mapSignals) {
        return new ExtraBlockElement(blockElement, networkLayout.getBlockDirection(blockElement.getName()), mapSignals.get(blockElement.getName()));
    }
}
