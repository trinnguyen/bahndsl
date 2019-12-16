package de.uniba.swt.dsl.generator

import de.uniba.swt.dsl.bahnDSL.AspectsProperty
import de.uniba.swt.dsl.bahnDSL.BoardsProperty
import de.uniba.swt.dsl.bahnDSL.ModuleObject
import de.uniba.swt.dsl.bahnDSL.PointsProperty
import de.uniba.swt.dsl.bahnDSL.SegmentsProperty
import de.uniba.swt.dsl.bahnDSL.SignalsProperty
import de.uniba.swt.dsl.bahnDSL.TrainsProperty

class ModelConverter {
	
	def buildNetworkModel(ModuleObject e) {
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
				SignalsProperty:
					network.signals = convertSignals(property)
				PointsProperty:
					network.points = convertPoints(property)
				TrainsProperty:
					network.trains = convertTrains(property)
			}
		}
		
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
	
	def convertSignals(SignalsProperty property) {
		return property.items.map[p | new Signal(
			p.id,
			property.boardId,
			p.number,
			null,
			null
		)].toSet
	}
	
	def convertPoints(PointsProperty property) {
		return property.items.map[p | new Point(
			p.id,
			property.boardId,
			p.number,
			null,
			null
		)].toSet
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