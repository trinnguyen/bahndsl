package de.uniba.swt.dsl.generator;

import de.uniba.swt.dsl.bahn.AspectsProperty;
import de.uniba.swt.dsl.bahn.BoardsProperty;
import de.uniba.swt.dsl.bahn.ModuleObject;
import de.uniba.swt.dsl.bahn.ModuleProperty;
import de.uniba.swt.dsl.bahn.OverridePointAspectsElement;
import de.uniba.swt.dsl.bahn.OverrideSignalAspectsElement;
import de.uniba.swt.dsl.bahn.PointAspectsElement;
import de.uniba.swt.dsl.bahn.PointElement;
import de.uniba.swt.dsl.bahn.PointsProperty;
import de.uniba.swt.dsl.bahn.ReferencePointAspectsElement;
import de.uniba.swt.dsl.bahn.ReferenceSignalAspectsElement;
import de.uniba.swt.dsl.bahn.SegmentsProperty;
import de.uniba.swt.dsl.bahn.SignalAspectsElement;
import de.uniba.swt.dsl.bahn.SignalElement;
import de.uniba.swt.dsl.bahn.SignalsProperty;
import de.uniba.swt.dsl.bahn.TrainsProperty;
import de.uniba.swt.dsl.common.models.Aspect;
import de.uniba.swt.dsl.common.models.Board;
import de.uniba.swt.dsl.common.models.BoardFeature;
import de.uniba.swt.dsl.common.models.NetworkModel;
import de.uniba.swt.dsl.common.models.Point;
import de.uniba.swt.dsl.common.models.Segment;
import de.uniba.swt.dsl.common.models.Signal;
import de.uniba.swt.dsl.common.models.Train;
import de.uniba.swt.dsl.common.models.TrainPeripheral;
import de.uniba.swt.dsl.common.util.BahnException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class ModelConverter {
	
	public NetworkModel buildNetworkModel(ModuleObject e) {
		
		SignalsProperty signalsProp = null;
		PointsProperty pointsProp = null;
		
		NetworkModel network = new NetworkModel();
		network.name = e.getName();
		for (ModuleProperty property : e.getProperties()) {
			if (property instanceof BoardsProperty) {
				network.boards = convertBoards((BoardsProperty)property);
				continue;
			}
			
			if (property instanceof AspectsProperty) {
				network.aspects = convertAspects((AspectsProperty)property);
				continue;
			}
			
			if (property instanceof SegmentsProperty) {
				network.segments = convertSegments((SegmentsProperty)property);
				continue;
			}
			
			if (property instanceof TrainsProperty) {
				network.trains = convertTrains((TrainsProperty)property);
				continue;
			}
			
			if (property instanceof SignalsProperty) {
				signalsProp = (SignalsProperty)property;
				continue;
			}
			
			if (property instanceof PointsProperty) {
				pointsProp = (PointsProperty)property;
				continue;
			}
		}
		
		
		// second loop with aspects are already loaded
		if (signalsProp != null)
			network.signals = convertSignals(signalsProp, network.aspects);
		
		if (pointsProp != null)
			network.points = convertPoints(pointsProp, network.aspects);
		
		return network;
	}
	
	
	List<Aspect> convertAspects(AspectsProperty property) {
		return property.getItems().stream().map(p -> new Aspect(p.getName(), p.getValue())).collect(Collectors.toList());
	}
		
	List<Board> convertBoards(BoardsProperty property) {
		return property.getItems().stream().map(p -> new Board(
			p.getName(),
			p.getUniqueId(),
			p.getFeatures().stream().map(f -> new BoardFeature(f.getNumber(), f.getValue())).collect(Collectors.toList())
		)).collect(Collectors.toList());
	}
	
	List<Segment> convertSegments(SegmentsProperty property) {
		return property.getItems().stream().map(p -> new Segment(
			p.getName(),
			property.getBoard().getName(),
			p.getAddress()
		)).collect(Collectors.toList());
	}
	
	List<Signal> convertSignals(SignalsProperty property, List<Aspect> aspects) {
		return property.getItems().stream().map(p -> convertToSignal(property.getBoard().getName(), p, aspects)).collect(Collectors.toList());
	}
	
	Signal convertToSignal(String boardId, SignalElement s, List<Aspect> aspects) {
		List<Aspect> resultAspects = convertToSignalAspects(s.getAspects(), aspects);
		Aspect initialAspect = findAspect(s.getInitial().getName(), resultAspects);
		return new Signal(
			s.getName(),
			boardId,
			s.getNumber(),
			resultAspects,
			initialAspect
		);
	}
	
	List<Aspect> convertToSignalAspects(SignalAspectsElement elements, List<Aspect> globalAspects) {
		if (elements instanceof OverrideSignalAspectsElement) {
			OverrideSignalAspectsElement overrideElements = (OverrideSignalAspectsElement)elements;
			return overrideElements.getAspects().stream().map(a -> new Aspect(a.getName(), a.getValue())).collect(Collectors.toList());
		}
		
		if (elements instanceof ReferenceSignalAspectsElement) {
			ReferenceSignalAspectsElement refElements = (ReferenceSignalAspectsElement)elements;
			return refElements.getAspects().stream().map(ra -> findAspect(ra.getName(), globalAspects)).filter(a -> a != null).collect(Collectors.toList());
		}
		
		throw new BahnException("Invalid signal aspects");
	}
	
	List<Point> convertPoints(PointsProperty property, List<Aspect> aspects) {
		return property.getItems().stream().map(p -> convertToPoint(property.getBoard().getName(), p, aspects)).collect(Collectors.toList());
	}
	
	Point convertToPoint(String boardId, PointElement p, List<Aspect> aspects) {
		List<Aspect> resultAspects = convertToPointAspects(p.getAspects(), aspects);
		Aspect initialAspect = findAspect(p.getInitial().getName(), resultAspects);
		return new Point(
			p.getName(),
			boardId,
			p.getNumber(),
			resultAspects,
			initialAspect
		);
	}
	
	List<Aspect> convertToPointAspects(PointAspectsElement elements, List<Aspect> globalAspects) {
		if (elements instanceof OverridePointAspectsElement) {
			OverridePointAspectsElement overrideElements = (OverridePointAspectsElement)elements;
			return overrideElements.getOverrideAspects().stream().map(a -> new Aspect(a.getName(), a.getValue())).collect(Collectors.toList());
		}
		
		if (elements instanceof ReferencePointAspectsElement) {
			ReferencePointAspectsElement refElements = (ReferencePointAspectsElement)elements;
			return refElements.getReferenceAspects().stream().map(ra -> findAspect(ra.getName(), globalAspects)).filter(Objects::nonNull).collect(Collectors.toList());
		}
		
		throw new BahnException("Invalid point aspects");
	}
	
	
	Aspect findAspect(String id, List<Aspect> aspects) {
		if (aspects != null) {
			for (Aspect aspect : aspects) {
				if (aspect.getId().equals(id))
					return aspect;
			}
		}
		
		return null;
	}
	
	List<Train> convertTrains(TrainsProperty property) {
		return property.getItems().stream().map(t -> new Train(
			t.getName(),
			t.getAddress(),
			t.getSteps(),
			new ArrayList<Integer>(t.getCalibrations()),
			t.getPeripherals().stream().map(p -> new TrainPeripheral(p.getName(), p.getBit(), p.getInitial())).collect(Collectors.toList())
		)).collect(Collectors.toList());
	}
}