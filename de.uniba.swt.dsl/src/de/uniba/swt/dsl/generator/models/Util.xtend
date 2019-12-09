package de.uniba.swt.dsl.generator.models

class Util {
	static def toHexString(long value) {
		return "0x" + (value < 16 ? "0" : "") + Long.toHexString(value).toUpperCase
	}	
}