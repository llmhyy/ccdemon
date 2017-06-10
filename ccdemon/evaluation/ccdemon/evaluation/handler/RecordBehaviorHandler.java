package ccdemon.evaluation.handler;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;

import ccdemon.evaluation.model.DataRecord;

public class RecordBehaviorHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
//		IProject project = CCDemonUtil.retrieveWorkingJavaProject().getProject();
//		IFile file = project.getFile("/record_data" + (++DataRecord.recordTime));
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		URI uri = root.getLocationURI();
		
		String content = "Behavior Record: \n" + 
				"Next Time: " + DataRecord.toNextTime + "\n" +
				"Prev Time: " + DataRecord.toPrevTime + "\n" +
				"ManualEdit Time: " + DataRecord.manualEditTime + "\n";
		content += "Each Configuration Time: " + "\n";
		for(Long interval : DataRecord.focusIntervals){
			content += interval + "ms,";
		}
		content += "\nEach Focus Gain Time: " + "\n";
		for(String time : DataRecord.focusGainTimes){
			content += time + ",";
		}
		content += "\nEach Focus Lost Time: " + "\n";
		for(String time : DataRecord.focusLostTimes){
			content += time + ",";
		}
		content += "\nEach Go Next Time: " + "\n";
		for(String time : DataRecord.toNextTimes){
			content += time + ",";
		}
		content += "\nEach Go Prev Time: " + "\n";
		for(String time : DataRecord.toPrevTimes){
			content += time + ",";
		}
		content += "\nEach Manual Editing Time: " + "\n";
		for(String time : DataRecord.manualEditTimes){
			content += time + ",";
		}
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("HH-mm-ss-SS",Locale.SIMPLIFIED_CHINESE);
			String timeStr = sdf.format(new Date());
			
			FileWriter writer = new FileWriter(uri.getPath() + "/record_data" + timeStr);
			writer.write(content);
			writer.close();
			
			MessageDialog.openInformation(null, "Record Done", "Experiment information recorded, please continue your task.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		InputStream source = new ByteArrayInputStream(content.getBytes());
//		try {
//			file.create(source, false, null);
//		} catch (CoreException e) {
//			e.printStackTrace();
//		}
		
		DataRecord.clear();
		return null;
	}

}
