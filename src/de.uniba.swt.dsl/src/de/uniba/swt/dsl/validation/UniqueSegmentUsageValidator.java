package de.uniba.swt.dsl.validation;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.validation.util.ValidationException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.*;
import java.util.stream.Collectors;

public class UniqueSegmentUsageValidator {

    private Map<String,String> usedSegments = new HashMap<>();

    public void clear() {
        usedSegments.clear();
    }

    public void validateSegment(TrackSection section) throws ValidationException {
        // check block
        if (section instanceof BlockElement) {
            var block = (BlockElement) section;
            try {
                validateBlockSegments(block);
            } catch (ValidationException e) {
                usedSegments.put(block.getMainSeg().getName(), block.getName());
                for (SegmentElement overlap : block.getOverlaps()) {
                    usedSegments.put(overlap.getName(), block.getName());
                }
                throw e;
            }

            // check all overlap
            for (SegmentElement overlap : block.getOverlaps()) {
                ensureSingleUsage(section.getName(), overlap);
            }
        }

        ensureSingleUsage(section.getName(), section.getMainSeg());
    }

    private void validateBlockSegments(BlockElement block) throws ValidationException {
        if (block.getOverlaps().contains(block.getMainSeg())) {
            throw new ValidationException(String.format(ValidationErrors.UsedSegmentInMainFormat, block.getMainSeg().getName()), BahnPackage.Literals.TRACK_SECTION__MAIN_SEG);
        }

        int countOverlap = block.getOverlaps().size();
        if (countOverlap > 0) {
            if (block.getOverlaps().stream().distinct().count() < countOverlap) {
                throw new ValidationException(String.format(ValidationErrors.UsedSegmentInOverlapFormat, block.getOverlaps().get(0).getName()), BahnPackage.Literals.BLOCK_ELEMENT__OVERLAPS);
            }
        }
    }

    private void ensureSingleUsage(String elemName, SegmentElement segment) throws ValidationException {
        if (usedSegments.containsKey(segment.getName())) {
            var err = String.format(ValidationErrors.UsedSegmentFormat,
                    segment.getName(),
                    usedSegments.get(segment.getName()));
            throw new ValidationException(err, BahnPackage.Literals.TRACK_SECTION__MAIN_SEG);
        } else {
            usedSegments.put(segment.getName(), elemName);
        }
    }
}
