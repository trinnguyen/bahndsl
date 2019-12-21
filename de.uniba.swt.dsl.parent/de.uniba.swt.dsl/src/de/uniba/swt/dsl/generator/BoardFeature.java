package de.uniba.swt.dsl.generator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
class BoardFeature {
	private long number;
	private long value;
}