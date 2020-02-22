package de.uniba.swt.dsl.common.generator;

import de.uniba.swt.dsl.bahn.RootModule;
import org.eclipse.xtext.generator.IFileSystemAccess2;

public interface GeneratorProvider {
    void run(IFileSystemAccess2 fsa, RootModule rootModule);
}
