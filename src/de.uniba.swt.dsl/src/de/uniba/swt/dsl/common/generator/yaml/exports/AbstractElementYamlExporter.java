package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.common.util.YamlExporter;
import org.eclipse.emf.ecore.EObject;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractElementYamlExporter<T> {

    public void build(YamlExporter exporter, T element) {
        exporter.appendLine("- %s: %s", getIdName(), getId(element));
        exporter.increaseLevel();

        // body
        var props = getProps(element);
        if (props != null && props.size() > 0)
        {
            for (Map.Entry<String, Object> entry : props.entrySet()) {

                if (entry.getValue() == null)
                    continue;

                if (entry.getValue() instanceof Collection) {
                    var collection = (Collection<?>) entry.getValue();
                    if (collection.isEmpty())
                        continue;

                    exporter.appendLine("%s:", entry.getKey());
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
                    exporter.appendLine("%s: %s", entry.getKey(), entry.getValue());
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

    protected abstract Map<String, Object> getProps(T element);
}
