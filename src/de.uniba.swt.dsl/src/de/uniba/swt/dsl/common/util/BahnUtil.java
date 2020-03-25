package de.uniba.swt.dsl.common.util;

import de.uniba.swt.dsl.bahn.BahnModel;
import de.uniba.swt.dsl.bahn.Component;
import de.uniba.swt.dsl.bahn.FuncDecl;
import de.uniba.swt.dsl.bahn.RootModule;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.EcoreUtil2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BahnUtil {
    public static void replaceEObject(EObject oldObj, EObject newObj) {
        if (newObj != null) {
            EcoreUtil2.replace(oldObj, newObj);
        }
    }

    public static BahnModel getBahnModel(Resource resource) {
        if (resource.getContents().size() > 0) {
            EObject e = resource.getContents().get(0);
            if (e instanceof BahnModel)
                return (BahnModel) e;
        }

        return null;
    }

    public static RootModule getRootModule(BahnModel bahnModel) {
        if (bahnModel != null) {
            for (Component component : bahnModel.getComponents()) {
                if (component instanceof RootModule)
                    return (RootModule) component;
            }
        }

        return null;
    }

    public static RootModule getRootModule(Resource resource) {
        return getRootModule(getBahnModel(resource));
    }

    public static List<FuncDecl> getDecls(ResourceSet set) {
        return set.getResources().stream().map(BahnUtil::getDecls).flatMap(Collection::stream).collect(Collectors.toList());
    }

    private static List<FuncDecl> getDecls(Resource resource) {
        List<FuncDecl> decls = new ArrayList<>();
        var bahnModel = getBahnModel(resource);
        if (bahnModel != null) {
            for (Component component : bahnModel.getComponents()) {
                if (component instanceof FuncDecl) {
                    decls.add((FuncDecl)component);
                }
            }
        }

        return decls;
    }
}
