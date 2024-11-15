/*
 * generated by Xtext 2.20.0
 */
package de.uniba.swt.dsl.ui.labeling;

import com.google.inject.Inject;

import de.uniba.swt.dsl.bahn.BlocksProperty;
import de.uniba.swt.dsl.bahn.BoardsProperty;
import de.uniba.swt.dsl.bahn.ConfigProp;
import de.uniba.swt.dsl.bahn.ConfigValue;
import de.uniba.swt.dsl.bahn.CrossingsProperty;
import de.uniba.swt.dsl.bahn.LayoutElement;
import de.uniba.swt.dsl.bahn.LayoutProperty;
import de.uniba.swt.dsl.bahn.LayoutReference;
import de.uniba.swt.dsl.bahn.Length;
import de.uniba.swt.dsl.bahn.PeripheralsProperty;
import de.uniba.swt.dsl.bahn.PlatformsProperty;
import de.uniba.swt.dsl.bahn.PointsProperty;
import de.uniba.swt.dsl.bahn.SegmentsProperty;
import de.uniba.swt.dsl.bahn.ReversersProperty;
import de.uniba.swt.dsl.bahn.SignalElement;
import de.uniba.swt.dsl.bahn.SignalsProperty;
import de.uniba.swt.dsl.bahn.TrackSection;
import de.uniba.swt.dsl.bahn.TrainTypeValue;
import de.uniba.swt.dsl.bahn.TrainsProperty;
import de.uniba.swt.dsl.bahn.Weight;

import java.util.Iterator;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.xtext.ui.label.DefaultEObjectLabelProvider;

/**
 * Provides labels for EObjects.
 * 
 * See https://www.eclipse.org/Xtext/documentation/310_eclipse_support.html#label-provider
 */
public class BahnLabelProvider extends DefaultEObjectLabelProvider {

	@Inject
	public BahnLabelProvider(AdapterFactoryLabelProvider delegate) {
		super(delegate);
	}
	
	String text(BoardsProperty element) {
		return "BiDiB Boards";
		
	}
	
	String text(SegmentsProperty element) {
		return "Segments for " + element.getBoard().getName();
	}

	String text(ReversersProperty element) {
		return "Reversers for " + element.getBoard().getName();
	}

	String text(SignalsProperty element) {
		return "Signals for " + element.getBoard().getName();
	}
	
	String text(PointsProperty element) {
		return "Points for " + element.getBoard().getName();
	}
	
	String text(PeripheralsProperty element) {
		return "Peripherals for " + element.getBoard().getName();
	}
	
	String text(BlocksProperty element) {
		return "Blocks";
	}
	
	String text(CrossingsProperty element) {
		return "Crossings";
	}
	
	String text(TrainsProperty element) {
		return "Trains";
	}
	
	String text(LayoutProperty element) {
		return "Network Layout";
	}
	
	String text(PlatformsProperty element) {
		return "Station Platforms";
	}
	
	String text(LayoutElement element) {
		Iterator<LayoutReference> blocks = element.getBlocks().iterator();
		Iterator<String> connectors = element.getConnectors().iterator();
		
		String result = "";
		while (blocks.hasNext() || connectors.hasNext()) {
	        if (blocks.hasNext()) {
	        	EObject block = blocks.next().getElem();
	        	String blockName = "Undefined";
	        	if (block instanceof TrackSection) {
	        		blockName = ((TrackSection)block).getName();
	        	} else if (block instanceof SignalElement) {
	        		blockName = ((SignalElement)block).getName();
	        	}
	        	result += blockName;
	        }
	        
	        if (connectors.hasNext()) {
	        	result += connectors.next();
	        }
	    }
		
		return result;
	}
	
	String text(ConfigProp element) {
		ConfigValue value = element.getValue();
		if (value instanceof TrainTypeValue) {
			return ((TrainTypeValue)value).getType().getLiteral();
		} else if (value instanceof Weight){
			return ((Weight)value).getValue() + ((Weight)value).getUnit().getLiteral();
		} else if (value instanceof Length){
			return ((Length)value).getValue() + ((Length)value).getUnit().getLiteral();
		}
		
		return value.toString();
	}
}
