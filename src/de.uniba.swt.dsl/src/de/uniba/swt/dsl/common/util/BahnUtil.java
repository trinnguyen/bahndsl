package de.uniba.swt.dsl.common.util;

import de.uniba.swt.dsl.bahn.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.EcoreUtil2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

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

    public static List<FuncDecl> getDecls(Resource resource) {
        var bahnModel = getBahnModel(resource);
        if (bahnModel != null) {
            List<FuncDecl> decls = new ArrayList<>();
            for (Component component : bahnModel.getComponents()) {
                if (component instanceof FuncDecl) {
                    decls.add((FuncDecl)component);
                }
            }
            return decls;
        }

        return null;
    }

    public static BooleanLiteral createBooleanLiteral(boolean val) {
        var liter = BahnFactory.eINSTANCE.createBooleanLiteral();
        liter.setBoolValue(val);
        return liter;
    }

    public static Expression createNumLiteral(int val) {
        var liter = BahnFactory.eINSTANCE.createNumberLiteral();
        liter.setValue(val);
        return liter;
    }

    public static String getNameWithoutExtension(String fileName) {
        if (fileName == null)
            return null;

        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
    }

    private static final String CodeNamingPrefix = "intern_";

    public static String generateCodeNaming(String id) {
        var prefix = CodeNamingPrefix + id.toLowerCase();
        var names = new String[]{
                prefix + "_tick",
                prefix + "_reset",
                generateLogicNaming(id),
                prefix + "_tick_data"};
        return  "#code.naming \"" + String.join("\",\"", names) + "\"";
    }

    public static String generateLogicNaming(String id) {
        return CodeNamingPrefix + id.toLowerCase() + "_logic";
    }

    public static boolean hasBreakStmtInBlock(Statement blockStmt) {
        return hasStmtInBlock(blockStmt, BreakStmt.class);
    }

    public static boolean hasReturnStmtInBlock(Statement blockStmt) {
        return hasStmtInBlock(blockStmt, ReturnStmt.class);
    }

    private static boolean hasStmtInBlock(Statement blockStmt, Class<?> stmtClass) {
        if (stmtClass.isInstance(blockStmt))
            return true;

        if (blockStmt instanceof IterationStmt) {
            var iterationStmt = (IterationStmt) blockStmt;
            if (iterationStmt.getStmts() != null) {
                for (Statement stmt : iterationStmt.getStmts().getStmts()) {
                    if (hasStmtInBlock(stmt, stmtClass))
                        return true;
                }
            }
        }

        if (blockStmt instanceof SelectionStmt) {
            var selectionStmt = (SelectionStmt) blockStmt;
            if (selectionStmt.getThenStmts() != null && selectionStmt.getThenStmts().getStmts() != null) {
                for (Statement stmt : selectionStmt.getThenStmts().getStmts()) {
                    if (hasStmtInBlock(stmt, stmtClass))
                        return true;
                }
            }
            if (selectionStmt.getElseStmts() != null && selectionStmt.getElseStmts().getStmts() != null) {
                for (Statement stmt : selectionStmt.getElseStmts().getStmts()) {
                    if (hasStmtInBlock(stmt, stmtClass))
                        return true;
                }
            }
        }

        return false;
    }

    public static boolean isInsideIterationStmt(EObject eObject) {
        return eObject != null && isIteration(eObject.eContainer());
    }

    private static boolean isIteration(EObject eObject) {
        if (eObject == null)
            return false;

        if (eObject instanceof IterationStmt)
            return true;

        if (eObject instanceof ForeachStmt)
            return true;

        if (eObject instanceof FuncDecl)
            return false;

        return isIteration(eObject.eContainer());
    }

    public static boolean hasBreakStmt(StatementList stmtList) {
        if (stmtList != null && stmtList.getStmts() != null) {
            for (Statement stmt : stmtList.getStmts()) {
                if (hasBreakStmtInBlock(stmt))
                    return true;
            }
        }

        return false;
    }

    public static Long parseHex(String strValue) throws Exception {
        try	{
            return Long.parseLong(strValue.substring(2), 16);
        } catch (NumberFormatException ex) {
            throw new Exception("Invalid hex value: " + strValue);
        }
    }
}
