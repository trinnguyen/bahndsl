package de.uniba.swt.dsl.common.generator;

import de.uniba.swt.dsl.bahn.BahnModel;
import org.apache.log4j.Logger;
import org.eclipse.xtext.generator.IFileSystemAccess2;

public abstract class GeneratorProvider {

    private static final Logger logger = Logger.getLogger(GeneratorProvider.class);

    /**
     * Generate code
     * @param fsa fsa
     * @param bahnModel model
     */
    public void generate(IFileSystemAccess2 fsa, BahnModel bahnModel) {
        logger.debug(String.format("Start code generator: %s, cleaning up", getClass().getSimpleName()));
        cleanUp(fsa);
        if (bahnModel != null)
            execute(fsa, bahnModel);
    }

    /**
     * Internal execution of code generation
     * @param fsa fsa
     * @param bahnModel model
     */
    protected abstract void execute(IFileSystemAccess2 fsa, BahnModel bahnModel);

    /**
     * Remove previous generated file
     */
    protected void cleanUp(IFileSystemAccess2 fsa) {
        var names = generatedFileNames();
        if (names != null) {
            for (String name : names) {
                try {
                    fsa.deleteFile(name);
                } catch (Exception ex) {
                    logger.warn(String.format("Failed to delete file: %s, msg: %s", name, ex.getMessage()));
                }

            }
        }
    }

    /**
     * Generated file names used for cleaning up
     * @return file names
     */
    protected abstract String[] generatedFileNames();
}
