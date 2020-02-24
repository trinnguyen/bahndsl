package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.Tuple;
import org.eclipse.emf.common.util.EList;

import java.util.List;
import java.util.Map;

class SignalElementYamlExporter extends AbstractElementYamlExporter<SignalElement> {

    @Override
    protected String getId(SignalElement element) {
        return element.getName();
    }

    @Override
    protected List<Tuple<String, Object>> getProps(SignalElement element) {
        EList<AspectElement> aspects = getAspectsSet(element.getAspects());
        return List.of(Tuple.of("number", element.getNumber()),
                Tuple.of("aspects", aspects),
                Tuple.of("initial", element.getInitial().getName()));
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
