# Adding a New Datatype for Defining Railway Resources in a BahnDSL Model
* Update Bahn.xtext grammar to allow the definition of the new datatype in a BahnDSL model. See ConfigKey, SignalType, and PeripheralType for examples
* Update standardlib.bahn using the new BahnDSL grammar to define the new datatype. See config, signaltypes, and peripheraltypes for examples. Note that the identifiers cannot clash with the BahnDSL grammar terminals, otherwise they would be recognised as a terminal
* Update BahnScopeProvider.getGlobalCandidates() to support the resolution of the new datatype during validation
* Update ExtrasYamlExporter to include the new datatype in the YAML extras configuration file
* Create a dedicated YamlExporter to handle the exporting of the new datatype to Yaml. See PeripheralTypeYamlExporter for example
* Update swtbahn-cli YAML parser to handle the new datatype when parsing the relevant configuration files

# Adding a New Board Property in a BahnDSL Model
* Update Bahn.xtext grammar to allow the definition of a new board property in a BahnDSL model. See SignalsProperty and PeripheralsProperty for examples
* Update standardlib.bahn schema with the board property keys that should be accessible when defining a BahnDSL method. See schema.route and schema.peripheral for examples. Note that the identifiers cannot clash with the BahnDSL grammar terminals, otherwise they would be recognised as a terminal
* Update TrackYamlExporter to include the new board property in the YAML track configuration file
* Create a dedicated YamlExporter to handle the exporting of the new datatype to Yaml. See ElementExporterFactory and PeripheralElementYamlExporter for example
* Update swtbahn-cli YAML parser to handle the new datatype when parsing the relevant configuration files

# Adding Support to Get and Set New Railway States From a BahnDSL Model
* Update Bahn.xtext grammar with keywords representing the allowed states by adding them to enum TrackState
* Update SyntacticTransformer.convertTrackState() to convert the state keywords into strings
* Update bahn_data_util.c to convert the state keyword to YAML key and to support getting and setting the new states
