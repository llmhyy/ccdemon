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
import ccdemon.evaluation.main.CloneRecoverer.CollectedData;
import ccdemon.evaluation.util.ExcelExporterWithPOI;
import ccdemon.util.CCDemonUtil;
import clonepedia.model.ontology.CloneSets;

public class AnalyzeStatisticsHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		Job job = new Job("analyzing project statistics and exporting as excel"){

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				AnalyzeStatisticsHandler handler = new AnalyzeStatisticsHandler();
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
		String projectName = "jasperreports";
		ExcelExporterWithPOI exporter = new ExcelExporterWithPOI();
		exporter.start();

		//how many times we have run this program
		int globalRunTimeCount = 0;
		
		//TODO the max trial number to output one time
		int limitTrialNum = 30000;

		for(clonepedia.model.ontology.CloneSet clonepediaSet: sets.getCloneList()){

			System.out.println("--------------------current: " + sets.getCloneList().indexOf(clonepediaSet) + ", total: " + sets.getCloneList().size() + " -----------------------");
			System.out.println("Clone set ID: " + clonepediaSet.getId());
			
			/*if(clonepediaSet.size() > 10){
				continue;
			}*/
			/*if(!clonepediaSet.getId().equals("4100")){
				continue;
			}*/

			if(count >= limitTrialNum){
				String fileName = projectName + globalRunTimeCount;
				exporter.end(fileName);
				globalRunTimeCount++;
				exporter = new ExcelExporterWithPOI();
				exporter.start();
				count = 0;
			}
			
			CloneSet set = CCDemonUtil.adaptMCIDiffModel(clonepediaSet);
			
			ArrayList<CollectedData> datas = recoverer.getTrials(set);
			
			for(CollectedData data : datas){
				ArrayList<String> exportList = new ArrayList<String>();
				//cloneSetID
				exportList.add(clonepediaSet.getId());
				//instanceNum
				exportList.add(set.getInstances().size() + "");
				//avgLineNum
				exportList.add(data.getLineNum() + "");
				//typeIIorIII
				exportList.add(data.getTypeIIorIII());
				//type1to7-recall;
				if(data.getRecall() == 1.0){
					if(data.getSavedEditingEffort() == 1.0){
						exportList.add("1");
					}else if(data.getSavedEditingEffort() > 0){
						exportList.add("2");
					}else{
						exportList.add("3");
					}
				}else if(data.getRecall() > 0){
					if(data.getSavedEditingEffort() == 1.0){
						exportList.add("4");
					}else if(data.getSavedEditingEffort() > 0){
						exportList.add("5");
					}else{
						exportList.add("6");
					}
				}else if(data.getRecall() == 0.0){
					exportList.add("7");
				}else{
					exportList.add("8");
				}
				//type1to7-Fmeasure;
				if(data.getfMeature() == 1.0){
					if(data.getSavedEditingEffort() == 1.0){
						exportList.add("1");
					}else if(data.getSavedEditingEffort() > 0){
						exportList.add("2");
					}else{
						exportList.add("3");
					}
				}else if(data.getfMeature() > 0){
					if(data.getSavedEditingEffort() == 1.0){
						exportList.add("4");
					}else if(data.getSavedEditingEffort() > 0){
						exportList.add("5");
					}else{
						exportList.add("6");
					}
				}else if(data.getfMeature() == 0.0){
					exportList.add("7");
				}else{
					exportList.add("8");
				}
				//recall;
				exportList.add(data.getRecall() + "");
				//precision;
				exportList.add(data.getPrecision() + "");
				//Fmeasure
				exportList.add(data.getfMeature() + "");
				//historyNum
				exportList.add(data.getHistoryNum() + "");
				//environmentNum
				exportList.add(data.getEnvironmentNum() + "");
				//ruleNum
				exportList.add(data.getRuleNum() + "");
				//configurationEffort;
				exportList.add(data.getConfigurationEffort() + "");
				//savedEditingEffort;
				exportList.add(data.getSavedEditingEffort() + "");
				//PartialSEE;
				exportList.add(data.getPartialSEE() + "");
				//totalFalsePositiveNum;
				exportList.add(data.getTotalFalsePositiveNum() + "");
				//goodCaseNum;
				exportList.add(data.getGoodCaseNum() + "");
				//goodCaseNum;
				exportList.add(data.isInfluencedByFalsePositive() + "");
				//trialTime;
				exportList.add(data.getTrialTime() + "");
				//diffTime;
				exportList.add(data.getDiffTime() + "");
				//APITime;
				exportList.add(data.getAPITime() + "");
				//cloneInstance;;
				exportList.add(data.getCloneInstance().toString());
				
				exporter.export(exportList, count);
				count++;
				System.out.println("current line number: " + count);
			}
			
		}

		String fileName = projectName + globalRunTimeCount;
		exporter.end(fileName);
        System.out.println("excel export done");
	}

}
