package de.uniba.swt.dsl.generator;

import org.eclipse.emf.ecore.EObject;

public class AstGenerator {

	public static String generateDynamicCode(EObject mo) {
		return dumpAst(mo, "");
	}
	
	
	public static String dumpAst(EObject obj, String indent) {
	    String res = indent + obj.toString().replaceFirst(".*[.]impl[.](.*)Impl[^(]*", "$1 ");

	    
	    for (EObject sub : obj.eCrossReferences()) {
	        res += " ->" + sub.toString().replaceFirst (".*[.]impl[.](.*)Impl[^(]*", "$1 ");
	    }
	    res += "\n";
	    for (EObject content :obj.eContents()) {
	        res += dumpAst (content, indent+"    ");
	    }
	    return res;
	}

}
