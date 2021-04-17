package de.uniba.swt.dsl.common.fsa;

import org.eclipse.xtext.generator.AbstractFileSystemAccess;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.generator.OutputConfiguration;

import java.io.File;
import java.util.Map;

import static com.google.common.collect.Maps.transformValues;

public class FsaUtil {

    public static String getFolderPath(IFileSystemAccess2 fsa) {
        if (fsa instanceof AbstractFileSystemAccess) {
            return getPathes((AbstractFileSystemAccess)fsa).get(IFileSystemAccess2.DEFAULT_OUTPUT);
        }

        return null;
    }

    public static File getFile(IFileSystemAccess2 fsa, String fileName) {
        return getFile(fsa, fileName, IFileSystemAccess2.DEFAULT_OUTPUT);
    }

    public static File getFile(IFileSystemAccess2 fsa, String fileName, String outputConfigName) {
        if (fsa instanceof AbstractFileSystemAccess) {
            var instanceFsa = (AbstractFileSystemAccess) fsa;
            String outlet = getPathes(instanceFsa).get(outputConfigName);
            if (outlet == null) {
                throw new IllegalArgumentException("A slot with name '" + outputConfigName + "' has not been configured.");
            } else {
                String pathName = toSystemFileName(outlet + "/" + fileName);
                return (new File(pathName)).getAbsoluteFile();
            }
        }

        return null;
    }

    private static Map<String, String> getPathes(AbstractFileSystemAccess fsa) {
        return transformValues(fsa.getOutputConfigurations(), OutputConfiguration::getOutputDirectory);
    }

    private static String toSystemFileName(String fileName) {
        return fileName.replace("/", File.separator);
    }
}
