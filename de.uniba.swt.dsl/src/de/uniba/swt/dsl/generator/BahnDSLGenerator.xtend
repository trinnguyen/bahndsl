/*
 * generated by Xtext 2.20.0
 */
package de.uniba.swt.dsl.generator

import com.google.inject.Inject
import de.uniba.swt.dsl.bahnDSL.ModuleObject
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.AbstractGenerator
import org.eclipse.xtext.generator.IFileSystemAccess2
import org.eclipse.xtext.generator.IGeneratorContext

/**
 * Generates code from your model files on save.
 * 
 * See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#code-generation
 */
class BahnDSLGenerator extends AbstractGenerator {

	@Inject 
	BidibYamlConfigGenerator bidibGenerator
	
	@Inject
	ModelConverter modelConverter

	override void doGenerate(Resource resource, IFileSystemAccess2 fsa, IGeneratorContext context) {
		var e = resource.contents.get(0)
		if (e instanceof ModuleObject) {
			val network = modelConverter.buildNetworkModel(e)
			
			// bidib_board_config
			fsa.generateFile("bidib_board_config.yml", bidibGenerator.dumpBoardConfig(network.name, network.boards))
			
			// bidib_track_config
			fsa.generateFile("bidib_track_config.yml", bidibGenerator.dumpTrackConfig(network))
			
			// bidib_train_config
			fsa.generateFile("bidib_train_config.yml", bidibGenerator.dumpTrainConfig(network.name, network.trains))
		}
	}
}