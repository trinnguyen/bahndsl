package de.uniba.swt.dsl.validation;

import de.uniba.swt.dsl.bahn.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.Collection;

public class BoardRefValidator {
    public <T extends ModuleProperty> void validateBoard(T host, BoardElement board) throws Exception {
        Collection<EStructuralFeature.Setting> settings = EcoreUtil.UsageCrossReferencer.find(board, board.eResource());
        for (EStructuralFeature.Setting s : settings) {
            var obj = s.getEObject();
            if (obj.getClass().equals(host.getClass())
                    && !obj.equals(host)) {
                throw new Exception(String.format("Board %s are used multiple times for %s", board.getName(), getPrefix(host)));
            }
        }
    }

    private <T extends ModuleProperty> String getPrefix(T host) {
        if (host instanceof SignalsProperty)
            return "signals";
        if (host instanceof PointsProperty)
            return "points";

        if (host instanceof  SegmentsProperty)
            return "segments";

        return null;
    }
}
