package de.uniba.swt.dsl.validation;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.validation.util.ValidationException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.Collection;
import java.util.stream.Collectors;

public class UniqueSegmentValidator {

    public void validateSegment(TrackSection section) throws ValidationException {
        try {
            // check block
            if (section instanceof BlockElement) {
                var block = (BlockElement) section;
                validateBlockSegments(block);

                // check all overlap
                for (SegmentElement overlap : block.getOverlaps()) {
                    ensureSingleUsage(section, overlap);
                }
            }

            // check main
            ensureSingleUsage(section, section.getMainSeg());
        } catch (Exception e) {
            throw new ValidationException(e.getMessage(), BahnPackage.Literals.TRACK_SECTION__MAIN_SEG);
        }
    }

    private void validateBlockSegments(BlockElement block) throws Exception {
        if (block.getOverlaps().contains(block.getMainSeg()))
            throw new Exception(String.format("Segment %s is already used as main segment", block.getMainSeg().getName()));

        int countOverlap = block.getOverlaps().size();
        if (countOverlap > 0) {
            if (block.getOverlaps().stream().distinct().count() < countOverlap) {
                throw new Exception(String.format("Segment %s is already used as an overlap", block.getOverlaps().get(0).getName()));
            }
        }
    }

    private void ensureSingleUsage(TrackSection section, SegmentElement segment) throws Exception {
        Collection<EStructuralFeature.Setting> settings = EcoreUtil.UsageCrossReferencer.find(segment, section.eResource());
        for (EStructuralFeature.Setting setting : settings) {
            if (setting.getEObject() instanceof TrackSection) {
                var checkSection = (TrackSection) setting.getEObject();
                if (!checkSection.equals(section)) {
                    throw new Exception(String.format("Segment %s is already used in another place %s",
                            segment.getName(),
                            checkSection.getName()));
                }
            }
        }
    }
}
