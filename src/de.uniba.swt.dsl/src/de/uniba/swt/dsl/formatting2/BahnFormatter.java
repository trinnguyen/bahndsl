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

package de.uniba.swt.dsl.formatting2;

import de.uniba.swt.dsl.bahn.*;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.formatting2.AbstractFormatter2;
import org.eclipse.xtext.formatting2.IFormattableDocument;
import org.eclipse.xtext.formatting2.ITextReplacer;
import org.eclipse.xtext.formatting2.internal.SinglelineCodeCommentReplacer;
import org.eclipse.xtext.formatting2.internal.SinglelineDocCommentReplacer;
import org.eclipse.xtext.formatting2.regionaccess.IComment;
import org.eclipse.xtext.formatting2.regionaccess.ISemanticRegion;
import org.eclipse.xtext.grammaranalysis.impl.GrammarElementTitleSwitch;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.xbase.lib.Pair;

public class BahnFormatter extends AbstractFormatter2 {

    private static final Logger logger = Logger.getLogger(BahnFormatter.class);


    @Override
    public ITextReplacer createCommentReplacer(IComment comment) {
        EObject grammarElement = comment.getGrammarElement();
        if (grammarElement instanceof AbstractRule) {
            String ruleName = ((AbstractRule) grammarElement).getName();
            if (ruleName.startsWith("SL")) {
                if (comment.getLineRegions().get(0).getIndentation().getLength() > 0)
                    return new SinglelineDocCommentReplacer(comment, "#");
                else
                    return new SinglelineCodeCommentReplacer(comment, "#");
            }
        }
        String elementName = new GrammarElementTitleSwitch().showQualified().showRule().doSwitch(grammarElement);
        throw new IllegalStateException("No " + ITextReplacer.class.getSimpleName() + " configured for " + elementName);
    }


    @Override
    public void format(Object obj, IFormattableDocument document) {
        if (obj instanceof XtextResource) {
            _format((XtextResource)obj, document);
            return;
        }

        if (obj instanceof BahnModel) {
            formatBahnModel((BahnModel)obj, document);
            return;
        }

        if (obj instanceof EObject) {
            _format((EObject)obj, document);
            return;
        }

        if (obj == null) {
            _format((Void)null, document);
            return;
        }

        _format(obj, document);
    }

    private void formatBahnModel(BahnModel model, IFormattableDocument document) {
        logger.debug("formatBahnModel: " + model);
        for (Component component : model.getComponents()) {
            if (component instanceof FuncDecl) {
                for (ISemanticRegion end : textRegionExtensions.allRegionsFor(component).keywords("end")) {
                    document.prepend(end, p -> p.newLine());
                }
                formatFuncDecl((FuncDecl) component, document);
            } else if (component instanceof RootModule) {
                formatRootModule((RootModule) component, document);
            }
            document.append(component, p -> p.newLine());
        }
    }

    private void formatFuncDecl(FuncDecl decl, IFormattableDocument document) {
        if (decl.getStmtList() != null) {
            formatStatementList(decl.getStmtList(), document);
        }
    }

    private void formatStatementList(StatementList statementList, IFormattableDocument document) {
        document.surround(statementList, p -> p.indent());
        for (Statement stmt : statementList.getStmts()) {
            document.prepend(stmt, p -> p.newLine());
            formatStmt(stmt, document);
        }
    }

    private void formatStmt(Statement stmt, IFormattableDocument document) {

        if (stmt instanceof SelectionStmt) {
            formatSelectionStmt((SelectionStmt) stmt, document);
            return;
        }

        if (stmt instanceof IterationStmt) {
            formatIterationStmt((IterationStmt) stmt, document);
            return;
        }

        if (stmt instanceof ForeachStmt) {
            formatForeachStmt((ForeachStmt) stmt, document);
            return;
        }

        // others
        document.surround(textRegionExtensions.allRegionsFor(stmt).keyword("="), p -> p.oneSpace());
    }

    private void formatSelectionStmt(SelectionStmt stmt, IFormattableDocument document) {
        document.prepend(textRegionExtensions.regionFor(stmt).keyword("else"), p -> p.newLine());
        formatExpr(stmt.getExpr(), document);
        if (stmt.getThenStmts() != null) {
            formatStatementList(stmt.getThenStmts(), document);
        }

        if (stmt.getElseStmts() != null) {
            formatStatementList(stmt.getElseStmts(), document);
        }
    }

    private void formatIterationStmt(IterationStmt stmt, IFormattableDocument document) {
        formatExpr(stmt.getExpr(), document);
        formatStatementList(stmt.getStmts(), document);
    }

    private void formatForeachStmt(ForeachStmt stmt, IFormattableDocument document) {
        formatStatementList(stmt.getStmts(), document);
    }

    private void formatExpr(Expression expr, IFormattableDocument document) {
        document.surround(textRegionExtensions.allRegionsFor(expr).feature(BahnPackage.Literals.OP_EXPRESSION__OP), p -> p.oneSpace());
    }

    private void formatRootModule(RootModule module, IFormattableDocument document) {
        if (module.getProperties() != null) {
            formatList(module.getProperties(), document);
        }
    }

    private void formatList(EList<? extends EObject> list, IFormattableDocument document) {
        for (EObject eObject : list) {
            for (ISemanticRegion end : textRegionExtensions.regionFor(eObject).keywords("end")) {
                document.prepend(end, p -> p.newLine());
            }

            document.prepend(eObject, p -> p.newLine());
            document.surround(eObject, p -> p.indent());

            // get sublist
            var subList = getSublist(eObject);
            if (subList != null) {
                formatList(subList, document);
            }

            // format single item
            formatConfigObject(eObject, document);
        }
    }

    private void formatConfigObject(EObject eObject, IFormattableDocument document) {
        if (eObject instanceof BoardElement) {
            formatPairs(eObject, document, "features", "end");
            formatList(((BoardElement) eObject).getFeatures(), document);
            return;
        }

        if (eObject instanceof TrainElement) {
            formatPairs(eObject, document, "calibration", "end");
            formatPairs(eObject, document, "peripherals", "end");
            formatList(((TrainElement) eObject).getPeripherals(), document);
            return;
        }
    }

    private void formatPairs(EObject eObject, IFormattableDocument document, String start, String end) {
        var pairs = textRegionExtensions.regionFor(eObject).keywordPairs(start, end);
        for (Pair<ISemanticRegion, ISemanticRegion> pair : pairs) {
            document.surround(pair.getKey(), p -> {
                p.newLine();
                p.indent();
            });
            document.surround(pair.getValue(), p -> {
                p.newLine();
                p.indent();
            });
        }
    }

    private EList<? extends EObject> getSublist(EObject eObject) {
        if (eObject instanceof BoardsProperty) {
            return ((BoardsProperty) eObject).getItems();
        }

        if (eObject instanceof SegmentsProperty) {
            return ((SegmentsProperty) eObject).getItems();
        }

        if (eObject instanceof SignalsProperty) {
            return ((SignalsProperty) eObject).getItems();
        }

        if (eObject instanceof PointsProperty) {
            return ((PointsProperty) eObject).getItems();
        }

        if (eObject instanceof PeripheralsProperty) {
            return ((PeripheralsProperty) eObject).getItems();
        }

        if (eObject instanceof BlocksProperty) {
            return ((BlocksProperty) eObject).getItems();
        }

        if (eObject instanceof PlatformsProperty) {
            return ((PlatformsProperty) eObject).getItems();
        }

        if (eObject instanceof LayoutProperty) {
            return ((LayoutProperty) eObject).getItems();
        }

        if (eObject instanceof TrainsProperty) {
            return ((TrainsProperty) eObject).getItems();
        }

        if (eObject instanceof CrossingsProperty) {
            return ((CrossingsProperty) eObject).getItems();
        }

        return null;
    }
}
