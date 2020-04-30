package de.uniba.swt.dsl.validation;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.util.BahnUtil;
import de.uniba.swt.dsl.common.util.Tuple;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.*;
import java.util.function.Function;

public class BoardValidator {
    private final List<Class> supportedSingleSectionClasses = List.of(
            BoardsProperty.class,
            BlocksProperty.class,
            PlatformsProperty.class,
            CrossingsProperty.class,
            LayoutProperty.class,
            TrainsProperty.class);

    public List<Tuple<String, Integer>> findSingleSectionError(RootModule module) {
        List<Tuple<String, Integer>> items = new ArrayList<>();

        Set<String> flags = new HashSet<>();
        for (int i = 0; i < module.getProperties().size(); i++) {
            var prop = module.getProperties().get(i);
            var supportedClass = supportedSingleSectionClasses.stream().filter(c -> c.isInstance(prop)).findFirst();
            if (supportedClass.isEmpty())
                continue;

            // add up
            var name = supportedClass.get().getSimpleName().replace("Property", "").toLowerCase();
            if (!flags.contains(name)) {
                flags.add(name);
            } else {
                items.add(Tuple.of(String.format(ValidationErrors.SingleSectionFormat, name), i));
            }
        }

        return items;
    }

    public List<Tuple<String, Integer>> findSingleTrackByBoardErrors(RootModule module) {
        List<Tuple<String, Integer>> items = new ArrayList<>();

        Set<String> flags = new HashSet<>();
        for (int i = 0; i < module.getProperties().size(); i++) {
            var prop = module.getProperties().get(i);
            Tuple<String, String> sectionBoardPair = getSectionInfo(prop);
            if (sectionBoardPair == null)
                continue;

            // add up
            if (!flags.contains(sectionBoardPair.getFirst())) {
                flags.add(sectionBoardPair.getFirst());
            } else {
                items.add(Tuple.of(String.format(ValidationErrors.SingleSectionByBoardFormat, sectionBoardPair.getFirst(), sectionBoardPair.getSecond()), i));
            }
        }

        return items;
    }

    private Tuple<String, String> getSectionInfo(ModuleProperty prop) {
        if (prop instanceof SegmentsProperty) {
            return Tuple.of("segments", ((SegmentsProperty) prop).getBoard().getName());
        }

        if (prop instanceof SignalsProperty) {
            return Tuple.of("signals", ((SignalsProperty) prop).getBoard().getName());
        }

        if (prop instanceof PeripheralsProperty) {
            return Tuple.of("peripherals", ((PeripheralsProperty) prop).getBoard().getName());
        }

        if (prop instanceof PointsProperty) {
            return Tuple.of("points", ((PointsProperty) prop).getBoard().getName());
        }

        return null;
    }

    public <T> List<Tuple<String, Integer>> validateUniqueHex(List<T> items, Function<T, String> addrMapper) {
        List<Tuple<String, Integer>> result = new ArrayList<>();
        Set<Long> values = new HashSet<>();
        for (int i = 0; i < items.size(); i++) {
            var hexVal = addrMapper.apply(items.get(i));
            Long hexAddr = BahnUtil.parseHex(hexVal);

            if (values.contains(hexAddr)) {
                result.add(Tuple.of(String.format(ValidationErrors.DefinedBoardAddressFormat, hexVal), i));
            } else {
                values.add(hexAddr);
            }
        }
        return result;
    }
}
