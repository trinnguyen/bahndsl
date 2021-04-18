package de.uniba.swt.dsl.ui.commands;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.xtext.generator.JavaIoFileSystemAccess;

import com.google.inject.Injector;

import de.uniba.swt.dsl.BahnStandaloneSetup;
import de.uniba.swt.dsl.generator.StandaloneApp;

public class BahnCompileUtil {
	
	private static String ext = "bahn";
	
	public static void processCommand(ExecutionEvent event, String mode) {
		var files = getFiles(event);

		
		// start generating
    	if (!files.isEmpty()) {
    		var wb = PlatformUI.getWorkbench();
    		var service = wb.getProgressService();
    		try {
				service.busyCursorWhile(new IRunnableWithProgress() {
					
					@Override
					public void run(IProgressMonitor monitor) {
						process(files, mode, monitor);
					}
				});
			} catch (InvocationTargetException | InterruptedException e) {
				StatusManager.getManager().handle(new Status(Status.WARNING, "de.uniba.swt.dsl.ui", e.getMessage(), e), StatusManager.LOG);	
			}
    	} else {
    		StatusManager.getManager().handle(new Status(Status.WARNING, "de.uniba.swt.dsl.ui", "No Bahn files is selected", null), StatusManager.LOG);
    	}
	}
	
	private static void process(List<IFile> files, String mode, IProgressMonitor pm) {
		pm.beginTask("Run Bahn compiler with mode: " + mode, files.size());
		
		// init
		Injector injector = BahnStandaloneSetup.doSetup();
		StandaloneApp app = injector.getInstance(StandaloneApp.class);
		JavaIoFileSystemAccess fsa = injector.getInstance(JavaIoFileSystemAccess.class);
		
		// process
		for (IFile file : files) {
			var path = file.getLocation().toOSString();
			pm.subTask("Process file " + path);
			app.runGenerator(path, fsa, null, mode);
			
			// refresh
			try {
				file.getProject().refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			} catch (CoreException e) {
				e.printStackTrace();
				StatusManager.getManager().handle(new Status(Status.WARNING, "de.uniba.swt.dsl.ui", e.getMessage(), e), StatusManager.LOG);
			}
			
			// finish work
			pm.worked(1);
		}
		pm.done();
	}

	private static List<IFile> getFiles(ExecutionEvent event) {
		
		var selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
		if (selection instanceof IStructuredSelection) {
			var files = new ArrayList<IFile>();
	    	var strSelection = (IStructuredSelection) selection;
	    	var iterator = strSelection.iterator();
	    	while (iterator.hasNext()) {
	    		var element = iterator.next();
	    		if (element instanceof IFile) {
	    			var file = (IFile) element;
	    			if (file.exists() && file.getFileExtension() != null && file.getFileExtension().endsWith(ext)) {
	    				files.add(file);	
	    			}
	    		}
	    	}
	    	
	    	return files;
		}
		
		return List.of();
	}
}
