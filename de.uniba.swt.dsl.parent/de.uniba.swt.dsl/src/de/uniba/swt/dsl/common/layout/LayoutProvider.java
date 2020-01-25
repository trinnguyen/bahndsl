package de.uniba.swt.dsl.common.layout;

import de.uniba.swt.dsl.bahn.*;
import de.uniba.swt.dsl.common.layout.models.*;
import de.uniba.swt.dsl.common.models.Signal;
import de.uniba.swt.dsl.common.util.BahnConstants;
import de.uniba.swt.dsl.common.util.LogHelper;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.formatting.IFormatterExtension;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import javax.sound.sampled.BooleanControl;
import java.util.*;

public class LayoutProvider {
	
	private final static Logger logger = Logger.getLogger(LayoutProvider.class);

	private NetworkLayout networkLayout;
	private NetworkValidator networkValidator;

	public LayoutProvider() {
		networkLayout = new NetworkLayout();
		networkValidator = new NetworkValidator();
	}

	public void run(IFileSystemAccess2 fsa, RootModule rootModule) {
		logger.info("Start running layout provider");
		networkLayout.clear();
		
		var layoutProp = rootModule.getProperties().stream().filter(p -> p instanceof LayoutProperty).map(p -> (LayoutProperty)p).findFirst();
		layoutProp.ifPresent(moduleProperty -> buildLayout(rootModule, moduleProperty));
	}

	private void buildLayout(RootModule rootModule, LayoutProperty layoutProp) {
		// 1. Build vertices
		buildVertices(rootModule, layoutProp.getItems());
		logger.info(networkLayout);

		// 2. Validation
		networkValidator.checkWelformness(networkLayout);
	}

	private void buildVertices(RootModule rootModule, EList<LayoutElement> elements) {
		for (var layoutElement : elements) {
			if (layoutElement.getBlocks().size() > 1) {
				int i = 0;
				while (i < layoutElement.getBlocks().size() - 1) {
					// first
					var members = convertToVertexMembers(layoutElement.getBlocks().get(i),
							layoutElement.getBlocks().get(++i));

					// check if duplicating
					LayoutVertex vertex = networkLayout.findVertex(members.get(0));
					LayoutVertex finalVertex = vertex;
					if (members.stream().skip(1).allMatch(member -> {
						var tmpVertex = networkLayout.findVertex(member);
						return tmpVertex != null && tmpVertex.equals(finalVertex);
					})) {
						logger.warn("Duplicated layout connector");
					} else {
						if (vertex == null) {
							vertex = networkLayout.addNewVertex();
						}
						networkLayout.addMembersToVertex(members, vertex);
					}
				}
			}
		}
	}

	private List<VertexMember> convertToVertexMembers(LayoutReference firstRef, LayoutReference secondRef) {
		if (isSignal(firstRef)) {
			if (!isBlock(secondRef)) {
				throw new RuntimeException("Signal can only connect to a block");
			}
			var signalMember = new SignalVertexMember((SignalElement) firstRef.getElem(), (BlockElement)secondRef.getElem());
			var blockMember = new BlockVertexMember((BlockElement)secondRef.getElem(), secondRef.getProp());
			return List.of(signalMember, blockMember);
		}

		if (isSignal(secondRef)) {
			if (!isBlock(firstRef)) {
				throw new RuntimeException("Signal can only connect to a block");
			}
			var signalMember = new SignalVertexMember((SignalElement) secondRef.getElem(), (BlockElement)firstRef.getElem());
			var blockMember = new BlockVertexMember((BlockElement)firstRef.getElem(), firstRef.getProp());
			return List.of(blockMember, signalMember);
		}

		return List.of(convertToVertexMember(firstRef), convertToVertexMember(secondRef));
	}

	private VertexMember convertToVertexMember(LayoutReference ref) {
		if (isPoint(ref))
			return new PointVertexMember((BlockElement) ref.getElem(), ref.getProp());

		return new BlockVertexMember((BlockElement) ref.getElem(), ref.getProp());
	}

	private boolean isSignal(LayoutReference ref) {
		return ref.getElem() instanceof SignalElement;
	}

	private boolean isBlock(LayoutReference ref) {
		return ref.getElem() instanceof BlockElement;
	}

	private boolean isPoint(LayoutReference ref) {
		return ref.getElem() instanceof BlockElement
				&& ref.getProp() != null
				&& BahnConstants.POINT_PROPS.contains(ref.getProp().toLowerCase());
	}
}
