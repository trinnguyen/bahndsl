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
