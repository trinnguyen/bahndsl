package de.uniba.swt.dsl.generator

import de.uniba.swt.dsl.bahn.AspectsProperty
import de.uniba.swt.dsl.bahn.BoardsProperty
import de.uniba.swt.dsl.bahn.ModuleObject
import de.uniba.swt.dsl.bahn.OverridePointAspectsElement
import de.uniba.swt.dsl.bahn.OverrideSignalAspectsElement
import de.uniba.swt.dsl.bahn.PointAspectsElement
import de.uniba.swt.dsl.bahn.PointElement
import de.uniba.swt.dsl.bahn.PointsProperty
import de.uniba.swt.dsl.bahn.ReferencePointAspectsElement
import de.uniba.swt.dsl.bahn.ReferenceSignalAspectsElement
import de.uniba.swt.dsl.bahn.SegmentsProperty
import de.uniba.swt.dsl.bahn.SignalAspectsElement
import de.uniba.swt.dsl.bahn.SignalElement
import de.uniba.swt.dsl.bahn.SignalsProperty
import de.uniba.swt.dsl.bahn.TrainsProperty
import java.util.Set

class ModelConverter {
	
	def buildNetworkModel(ModuleObject e) {
		
		var SignalsProperty signalsProp = null;
		var PointsProperty pointsProp = null;
		
		var network = new NetworkModel()
		network.name = e.name
		for (property : e.properties) {
			switch property {
				BoardsProperty:
					network.boards = convertBoards(property)
				AspectsProperty:
					network.aspects = convertAspects(property)	
				SegmentsProperty:
					network.segments = convertSegments(property)
				TrainsProperty:
					network.trains = convertTrains(property)
				SignalsProperty:
					signalsProp = property
				PointsProperty:
					pointsProp = property
			}
		}
		
		// second loop with aspects are already loaded
		network.signals = convertSignals(signalsProp, network.aspects)
		network.points = convertPoints(pointsProp, network.aspects)
		
		return network
	}
	
	
	def convertAspects(AspectsProperty property) {
		return property.items.map[p | new Aspect(
			p.id,
			p.value
		)].toSet
	}
		
	def convertBoards(BoardsProperty property) {
		return property.items.map[p | new Board(
			p.id,
			p.uniqueId,
			p.features.map[f | new BoardFeature(f.number, f.value)].toSet
		)].toSet
	}
	
	def convertSegments(SegmentsProperty property) {
		return property.items.map[p | new Segment(
			p.id,
			property.boardId,
			p.address
		)].toSet
	}
	
	def convertSignals(SignalsProperty property, Set<Aspect> aspects) {
		return property.items.map[p | convertToSignal(property.boardId, p, aspects)].toSet
	}
	
	def convertToSignal(String boardId, SignalElement s, Set<Aspect> aspects) {
		val resultAspects = convertToSignalAspects(s.aspects, aspects)
		val initialAspect = findAspect(s.initial, resultAspects)
		new Signal(
			s.id,
			boardId,
			s.number,
			resultAspects,
			initialAspect
		)
	}
	
	def convertToSignalAspects(SignalAspectsElement elements, Set<Aspect> globalAspects) {
		if (elements instanceof OverrideSignalAspectsElement) {
			val overrideElements = elements as OverrideSignalAspectsElement;
			return overrideElements.overrideAspects.map[a | new Aspect(a.id, a.value)].toSet
		}
		
		if (elements instanceof ReferenceSignalAspectsElement) {
			val refElements = elements as ReferenceSignalAspectsElement;
			return refElements.referenceAspects.map[ra | findAspect(ra, globalAspects)].filter[a | a !== null].toSet
		}
		
		throw new Exception("Invalid signal aspects")
	}
	
	def convertPoints(PointsProperty property, Set<Aspect> aspects) {
		return property.items.map[p | convertToPoint(property.boardId, p, aspects)].toSet
	}
	
	def convertToPoint(String boardId, PointElement p, Set<Aspect> aspects) {
		val resultAspects = convertToPointAspects(p.aspects, aspects)
		val initialAspect = findAspect(p.initial, resultAspects)
		new Point(
			p.id,
			boardId,
			p.number,
			resultAspects,
			initialAspect
		)
	}
	
	def convertToPointAspects(PointAspectsElement elements, Set<Aspect> globalAspects) {
		if (elements instanceof OverridePointAspectsElement) {
			val overrideElements = elements as OverridePointAspectsElement;
			return overrideElements.overrideAspects.map[a | new Aspect(a.id, a.value)].toSet
		}
		
		if (elements instanceof ReferencePointAspectsElement) {
			val refElements = elements as ReferencePointAspectsElement;
			return refElements.referenceAspects.map[ra | findAspect(ra.id, globalAspects)].filter[a | a !== null].toSet
		}
		
		throw new Exception("Invalid point aspects")
	}
	
	
	def findAspect(String id, Set<Aspect> aspects) {
		if (aspects !== null) {
			for (aspect : aspects) {
				if (aspect.id.equals(id))
					return aspect
			}
		}
		
		return null
	}
	
	def convertTrains(TrainsProperty property) {
		return property.items.map[t | new Train(
			t.id,
			t.address,
			t.steps,
			t.calibrations.map[c | new Integer(c)].toSet,
			t.peripherals.map[p | new TrainPeripheral(p.id, p.bit, p.initial)].toSet
		)].toSet
	}
}