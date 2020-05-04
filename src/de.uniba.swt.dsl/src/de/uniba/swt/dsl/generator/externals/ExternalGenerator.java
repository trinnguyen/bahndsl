/*
 *
 * Copyright (C) 2020 University of Bamberg, Software Technologies Research Group
 * <https://www.uni-bamberg.de/>, <http://www.swt-bamberg.de/>
 *
 * This file is part of the BahnDSL project, a domain-specific language
 * for configuring and modelling model railways.
 *
 * BahnDSL is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BahnDSL is a RESEARCH PROTOTYPE and distributed WITHOUT ANY WARRANTY, without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details.
 *
 * The following people contributed to the conception and realization of the
 * present BahnDSL (in alphabetic order by surname):
 *
 * - Tri Nguyen <https://github.com/trinnguyen>
 *
 */

package de.uniba.swt.dsl.generator.externals;

import org.eclipse.xtext.generator.AbstractFileSystemAccess;
import org.eclipse.xtext.generator.AbstractFileSystemAccess2;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import java.util.Arrays;

public abstract class ExternalGenerator {

    protected abstract String[] supportedTools();

    protected boolean executeArgs(String[] args, IFileSystemAccess2 fsa, CliRuntimeExecutor runtimeExec) {
        for (String cli : supportedTools()) {
            if (runtimeExec.internalExecuteCli(cli, args, getDefaultOutputPath(fsa)))
                return true;
        }

        System.err.println(String.format("None of the command lines exist: %s", Arrays.toString(supportedTools())));
        return false;
    }

    private String getDefaultOutputPath(IFileSystemAccess2 fsa) {
        if (fsa instanceof AbstractFileSystemAccess) {
            var concreteFsa = (AbstractFileSystemAccess2) fsa;
            var map = concreteFsa.getOutputConfigurations();
            var key = AbstractFileSystemAccess.DEFAULT_OUTPUT;
            return (map != null && map.containsKey(key)) ? map.get(key).getOutputDirectory() : "";
        }

        return "";
    }


    public boolean generate(IFileSystemAccess2 fsa, CliRuntimeExecutor runtimeExec) {
        cleanUp(fsa);
        return execute(fsa, runtimeExec);
    }

    protected abstract boolean execute(IFileSystemAccess2 fsa, CliRuntimeExecutor runtimeExec);

    /**
     * Remove previous generated file
     */
    protected void cleanUp(IFileSystemAccess2 fsa) {
        var names = generatedFileNames();
        if (names != null) {
            for (String name : names) {
                fsa.deleteFile(name);
            }
        }
    }

    /**
     * Generated file names used for cleaning up
     * @return file names
     */
    protected abstract String[] generatedFileNames();
}
