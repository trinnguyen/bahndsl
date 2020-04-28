package de.uniba.swt.dsl.ui.wizard;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "de.uniba.swt.dsl.ui.wizard.messages"; //$NON-NLS-1$
	
	public static String SampleBahnProject_Label;
	public static String SampleBahnProject_Description;
	public static String BahnModelFile_Label;
	public static String BahnModelFile_Description;
	
	static {
	// initialize resource bundle
	NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
