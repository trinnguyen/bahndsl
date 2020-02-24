package de.uniba.swt.dsl.common.generator.yaml.exports;

import de.uniba.swt.dsl.common.util.Tuple;
import de.uniba.swt.dsl.common.util.YamlExporter;
import org.eclipse.emf.ecore.EObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AbstractElementYamlExporter<T> {

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
