package de.uniba.swt.dsl.common.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;

public class BahnUtil {
    public static void replaceEObject(EObject oldObj, EObject newObj) {
        if (newObj != null) {
            EcoreUtil2.replace(oldObj, newObj);
        }
    }
}
