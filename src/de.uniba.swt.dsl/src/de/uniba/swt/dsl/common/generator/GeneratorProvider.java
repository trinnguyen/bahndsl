/*
 * This file is part of the BahnDSL project, a domain-specific language
 * for configuring and modelling model railways
 *
 * BahnDSL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BahnDSL is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BahnDSL.  If not, see <https://www.gnu.org/licenses/>.
 *
 * The following people contributed to the conception and realization of the
 * present BahnDSL (in alphabetic order by surname):
 *
 * - Tri Nguyen <https://github.com/trinnguyen>
 */

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
