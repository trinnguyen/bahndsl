/*
 * generated by Xtext 2.20.0
 */
package de.uniba.swt.dsl;

import org.eclipse.xtext.linking.ILinker;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
public class BahnRuntimeModule extends AbstractBahnRuntimeModule {
    @Override
    public Class<? extends ILinker> bindILinker() {
        return StandardLibLazyLinker.class;
    }
}
