package ccdemon.evaluation.handler;

import java.util.ArrayList;

import mcidiff.model.CloneSet;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import ccdemon.evaluation.main.CloneRecoverer;
import ccdemon.evaluation.main.CloneRecoverer.CPData;
import ccdemon.evaluation.main.CloneRecoverer.CollectedData;
import ccdemon.evaluation.util.CPExcelExporterWithPOI;
import ccdemon.util.CCDemonUtil;
import clonepedia.model.ontology.CloneSets;

public class AnalyzeCPHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Job job = new Job("analyzing configuration points with right candidate and exporting as excel"){

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				AnalyzeCPHandler handler = new AnalyzeCPHandler();
				handler.run();
				return Status.OK_STATUS;
			}
			
		};
		job.schedule();

		return null;
	}

	private void run(){
		CloneRecoverer recoverer = new CloneRecoverer();
		CloneSets sets = clonepedia.Activator.plainSets;
		int count = 0;

		//TODO what is the program name
		String projectName = "osworkflow";
		CPExcelExporterWithPOI exporter = new CPExcelExporterWithPOI();
		exporter.start();
		
//		ArrayList<String> output = new ArrayList<String>();
		
		for(clonepedia.model.ontology.CloneSet clonepediaSet: sets.getCloneList()){

			System.out.println("--------------------current: " + sets.getCloneList().indexOf(clonepediaSet) + ", total: " + sets.getCloneList().size() + " -----------------------");
			System.out.println("Clone set ID: " + clonepediaSet.getId());

//			if(!clonepediaSet.getId().equals("15827")){
//				continue;
//			}
			
			CloneSet set = CCDemonUtil.adaptMCIDiffModel(clonepediaSet);
			
			ArrayList<CollectedData> datas = recoverer.getTrials(set);
			
			for(CollectedData data : datas){
				ArrayList<CPData> cpDataList = data.getCpDataList();
				
				for(CPData cpData : cpDataList){
					ArrayList<String> exportList = new ArrayList<String>();
					exportList.add(cpData.rightCandidateRank + "");
					exportList.add(cpData.totalCandidateNum + "");
					exportList.add(data.toString());
					
//					if(cpData.totalCandidateNum > 200){
//						output.add("rank: " + cpData.rightCandidateRank + ", total: " + cpData.totalCandidateNum);
//					}
					
					exporter.export(exportList, count);
					count++;
				}
			}
		}

		exporter.end(projectName);
        System.out.println("excel export done");
//        System.out.println(output);
	}
}
