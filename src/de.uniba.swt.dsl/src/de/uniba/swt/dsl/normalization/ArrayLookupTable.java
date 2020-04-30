package de.uniba.swt.dsl.normalization;

import com.google.inject.Singleton;
import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.Tuple;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class ArrayLookupTable {
    private final Map<String, Tuple<RefVarDecl, RefVarDecl>> mapArray = new HashMap<>();
    private String functionName;
    private int counter = 0;

    public void resetFunc(String name) {
        this.mapArray.clear();
        this.functionName = name.toLowerCase();
        this.counter = 0;
    }

    public void insert(RefVarDecl decl, RefVarDecl lenDecl) {

        // add to map
        mapArray.put(decl.getName(), Tuple.of(decl, lenDecl));
    }

    public String generateTempLenVar(String name) {
        return String.format("_%s_%s_cnt_%d", functionName, name, ++counter);
    }

    public RefVarDecl lookupVarDecl(String name) {
        var tuple = mapArray.get(name);
        return tuple != null ? tuple.getFirst() : null;
    }

    public RefVarDecl lookupLengthDecl(String name) {
        var tuple = mapArray.get(name);
        return tuple != null ? tuple.getSecond() : null;
    }

    public ValuedReferenceExpr createArrayVarExpr(String name) {
        var ref = BahnFactory.eINSTANCE.createValuedReferenceExpr();
        ref.setLength(false);
        ref.setIndexExpr(null);
        ref.setDecl(lookupVarDecl(name));
        return ref;
    }

    public ValuedReferenceExpr createLenVarExpr(String name) {
        var ref = BahnFactory.eINSTANCE.createValuedReferenceExpr();
        ref.setLength(false);
        ref.setIndexExpr(null);
        ref.setDecl(lookupLengthDecl(name));
        return ref;
    }
}
