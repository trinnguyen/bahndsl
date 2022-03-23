package de.uniba.swt.dsl.generator;

import org.eclipse.xtext.generator.GeneratorContext;

public class BahnGeneratorContext extends GeneratorContext {
    private String routeType = null;

    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    public String getRouteType() {
        return this.routeType;
    }
}
