package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.*;
import org.eclipse.emf.common.util.EList;

import java.util.Map;

class SignalElementYamlExporter extends AbstractElementYamlExporter<SignalElement> {

    @Override
    protected String getId(SignalElement element) {
        return element.getName();
    }

    @Override
    protected Map<String, Object> getProps(SignalElement element) {
        EList<AspectElement> aspects = getAspectsSet(element.getAspects());
        return Map.of("number", element.getNumber(), "aspects", aspects, "initial", element.getInitial().getName());
    }

    private EList<AspectElement> getAspectsSet(SignalAspectsElement aspects) {
        if (aspects instanceof OverrideSignalAspectsElement) {
            return ((OverrideSignalAspectsElement) aspects).getAspects();
        }

        if (aspects instanceof ReferenceSignalAspectsElement) {
            return ((ReferenceSignalAspectsElement) aspects).getAspects();
        }

        return null;
    }
}
