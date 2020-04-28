package de.uniba.swt.dsl.common.generator.yaml;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.layout.models.NetworkLayout;
import de.uniba.swt.dsl.common.layout.models.vertex.AbstractVertexMember;
import de.uniba.swt.dsl.common.layout.models.vertex.LayoutVertex;
import de.uniba.swt.dsl.common.layout.models.vertex.VertexMemberType;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.common.util.ExtraBlockElement;
import de.uniba.swt.dsl.common.util.StringUtil;
import org.eclipse.emf.ecore.resource.Resource;

import java.util.*;
import java.util.stream.Collectors;

class ExtrasYamlExporter extends AbstractBidibYamlExporter {
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

        Map<String, List<String>> mapSignals = getMapSignals();

        List<ExtraBlockElement> blocks = rootModule.getProperties().stream().filter(p -> p instanceof BlocksProperty).map(p -> ((BlocksProperty) p).getItems()).flatMap(Collection::stream).map(b -> createExtraItem(b, mapSignals)).collect(Collectors.toList());
        List<ExtraBlockElement> platforms = rootModule.getProperties().stream().filter(p -> p instanceof PlatformsProperty).map(p -> ((PlatformsProperty) p).getItems()).flatMap(Collection::stream).map(b -> createExtraItem(b, mapSignals)).collect(Collectors.toList());
        List<CrossingElement> crossings = rootModule.getProperties().stream().filter(p -> p instanceof CrossingsProperty).map(p -> ((CrossingsProperty) p).getItems()).flatMap(Collection::stream).collect(Collectors.toList());
        List<SignalType> signaltypes = getSignalTypes(rootModule);
        List<CompositionSignalElement> compositeSignals = new ArrayList<>();

        // load
        for (ModuleProperty property : rootModule.getProperties()) {
            List<SignalElement> elements = null;
            if (property instanceof SignalsProperty) {
                elements = ((SignalsProperty) property).getItems();
            } else if (property instanceof PeripheralsProperty) {
                elements = ((PeripheralsProperty) property).getItems();
            }

            // export
            if (elements != null) {
                var items = elements.stream().filter(s -> s instanceof CompositionSignalElement)
                        .map(s -> (CompositionSignalElement)s)
                        .collect(Collectors.toList());
                compositeSignals.addAll(items);
            }
        }

        // blocks
        exportSection("blocks:", blocks);
        exportSection("platforms:", platforms);
        exportSection("crossings:", crossings);
        exportSection("signaltypes:", signaltypes);
        exportSection("compositions:", compositeSignals);
    }

    private List<SignalType> getSignalTypes(RootModule rootModule) {
        var set = rootModule.eResource().getResourceSet();

        List<SignalType> result = new ArrayList<>();
        for (Resource resource : set.getResources()) {
            var bahnModel = BahnUtil.getBahnModel(resource);
            if (bahnModel != null && bahnModel.getSignalTypes() != null && bahnModel.getSignalTypes().getTypes() != null) {
                result.addAll(bahnModel.getSignalTypes().getTypes());
            }
        }

        return result;
    }

    private Map<String, List<String>> getMapSignals() {
        if (networkLayout == null)
            return null;

        Map<String, List<String>> map = new HashMap<>();
        for (LayoutVertex vertex : networkLayout.getVertices()) {
            String blockName = null;
            List<String> signals = new ArrayList<>();
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

    private ExtraBlockElement createExtraItem(BlockElement blockElement, Map<String, List<String>> mapSignals) {
        var direction = networkLayout != null ? networkLayout.getBlockDirection(blockElement.getName()) : null;
        var signals = mapSignals != null ? mapSignals.get(blockElement.getName()) : null;
        return new ExtraBlockElement(blockElement, direction, signals);
    }
}
