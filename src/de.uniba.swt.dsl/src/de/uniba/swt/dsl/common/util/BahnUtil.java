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

package de.uniba.swt.dsl.common.util;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.validation.typing.ExprDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.EcoreUtil2;

import java.util.*;
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
                prefix + "_logic",
                prefix + "_tick_data"};
        return  "#code.naming \"" + String.join("\",\"", names) + "\"";
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

    public static FuncDecl findFuncDecl(EObject obj) {
        if (obj instanceof FuncDecl) {
            return (FuncDecl) obj;
        }

        if (obj.eContainer() != null)
            return findFuncDecl(obj.eContainer());

        return null;
    }

    public static Long parseHex(String strValue) throws NumberFormatException {
        return Long.parseLong(strValue.substring(2), 16);
    }

    public static VarDeclStmt createVarDeclStmt(String name, ExprDataType dataType, Expression initialValue) {

        var decl = BahnFactory.eINSTANCE.createVarDecl();
        decl.setName(name);
        decl.setType(dataType.getDataType());
        decl.setArray(dataType.isArray());

        return createVarDeclStmt(decl, initialValue);
    }

    public static VarDeclStmt createVarDeclStmt(VarDecl decl, Expression initialValue) {
        var stmt = BahnFactory.eINSTANCE.createVarDeclStmt();
        stmt.setDecl(decl);

        if (initialValue != null)
            assignExpression(stmt, initialValue);

        return stmt;
    }

    public static ValuedReferenceExpr createVarRef(VarDeclStmt temp) {
        return createVarRef(temp.getDecl());
    }

    public static ValuedReferenceExpr createVarRef(RefVarDecl decl) {
        var ref = BahnFactory.eINSTANCE.createValuedReferenceExpr();
        ref.setLength(false);
        ref.setIndexExpr(null);
        ref.setDecl(decl);
        return ref;
    }

    public static void assignExpression(VarDeclStmt declStmt, Expression expr) {
        if (expr != null) {
            var assignment = BahnFactory.eINSTANCE.createVariableAssignment();
            assignment.setExpr(expr);

            declStmt.setAssignment(assignment);
        }
    }

    public static AssignmentStmt createAssignmentStmt(RefVarDecl decl, Expression expr) {
        var assignment = BahnFactory.eINSTANCE.createVariableAssignment();
        assignment.setExpr(expr);

        var stmt = BahnFactory.eINSTANCE.createAssignmentStmt();
        stmt.setReferenceExpr(createVarRef(decl));
        stmt.setAssignment(assignment);

        return stmt;
    }

    public static void findCalledFunctions(Set<FuncDecl> set, FuncDecl decl) {
        if (decl == null || decl.getStmtList() == null || decl.getStmtList().getStmts() == null)
            return;

        var exprs = EcoreUtil2.eAllOfType(decl, RegularFunctionCallExpr.class);
        if (exprs.size() > 0) {
            for (RegularFunctionCallExpr expr : exprs) {
                if (set.contains(expr.getDecl()))
                    continue;

                set.add(expr.getDecl());

                // subcall
                findCalledFunctions(set, expr.getDecl());
            }
        }
    }

    private static String getOsName() {
        var result = System.getProperty("os.name");
        return result != null ? result.toLowerCase() : "";
    }

    public static boolean isMacOS() {
        return getOsName().startsWith("mac");
    }

    public static boolean isWindows() {
        return getOsName().startsWith("win");
    }
}
