package de.uniba.swt.dsl.validation;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.validation.util.ValidationException;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.Collection;
import java.util.stream.Collectors;

public class UniqueSegmentValidator {

    public void validateSegment(TrackSection section) throws ValidationException {
        Collection<EStructuralFeature.Setting> settings = EcoreUtil.UsageCrossReferencer.find(section.getMainSeg(), section.eResource());

        var object = settings.stream().map(EStructuralFeature.Setting::getEObject)
                .filter(obj -> obj instanceof TrackSection && !obj.equals(section))
                .findFirst();
        if (object.isPresent()) {
            throw new ValidationException(String.format("Segment %s is already used in another track section %s", section.getMainSeg().getName(), ((TrackSection)object.get()).getName()), BahnPackage.Literals.TRACK_SECTION__MAIN_SEG);
        }
    }
}
