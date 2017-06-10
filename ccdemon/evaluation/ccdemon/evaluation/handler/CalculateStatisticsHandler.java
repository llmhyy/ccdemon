package ccdemon.evaluation.handler;

import java.util.ArrayList;

import mcidiff.model.CloneInstance;
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
import ccdemon.util.CCDemonUtil;
import clonepedia.model.ontology.CloneSets;

public class CalculateStatisticsHandler extends AbstractHandler{
	
	class TrialType{
		int count = 0;
		ArrayList<Double> config_effort = new ArrayList<Double>();
		ArrayList<Double> saveedit_effort = new ArrayList<Double>();
		ArrayList<CloneInstance> instances = new ArrayList<CloneInstance>();
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		Job job = new Job("calculating project statistics"){

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				CalculateStatisticsHandler handler = new CalculateStatisticsHandler();
				handler.run();
				return Status.OK_STATUS;
			}
			
		};
		job.schedule();
		

		return null;
	}
	
	private void run(){
		CloneRecoverer recoverer = new CloneRecoverer();
		CloneSets sets = clonepedia.Activator.plainSets;;
		
		TrialType[] types = new TrialType[7];
		for(int i = 0; i < types.length; i++){
			types[i] = new TrialType();
		}
		
		int total_historyNum = 0;
		int total_environmentNum = 0;
		int total_ruleNum = 0;
		int total_totalNum = 0;
		ArrayList<Double> correctness = new ArrayList<Double>();
		
		for(clonepedia.model.ontology.CloneSet clonepediaSet: sets.getCloneList()){
			
			System.out.println("Clone set ID: " + clonepediaSet.getId());
			
			CloneSet set = CCDemonUtil.adaptMCIDiffModel(clonepediaSet);
			ArrayList<CollectedData> datas = recoverer.getTrials(set);
			
			for(CollectedData data : datas){
				if(data.getRecall() == 1.0){
					if(data.getSavedEditingEffort() == 1.0){
						types[0].count++;
						types[0].config_effort.add(data.getConfigurationEffort());
						types[0].saveedit_effort.add(data.getSavedEditingEffort());
					}else if(data.getSavedEditingEffort() > 0){
						types[1].count++;
						types[1].config_effort.add(data.getConfigurationEffort());
						types[1].saveedit_effort.add(data.getSavedEditingEffort());
					}else{
						types[2].count++;
						types[2].instances.add(data.getCloneInstance());
					}
				}else if(data.getRecall() > 0){
					if(data.getSavedEditingEffort() == 1.0){
						types[3].count++;
						types[3].config_effort.add(data.getConfigurationEffort());
					}else if(data.getSavedEditingEffort() > 0){
						types[4].count++;
						types[4].config_effort.add(data.getConfigurationEffort());
						types[4].saveedit_effort.add(data.getSavedEditingEffort());
					}else{
						types[5].count++;
						types[5].instances.add(data.getCloneInstance());
					}
				}else{
					types[6].count++;
					types[6].instances.add(data.getCloneInstance());
				}
				
				total_historyNum += data.getHistoryNum();
				total_environmentNum += data.getEnvironmentNum();
				total_ruleNum += data.getRuleNum();
				total_totalNum += data.getTotalNum();
				correctness.add(data.getRecall());
			}
		}
		
		int count_for_alltypes = 0;
		for(TrialType t : types){
			count_for_alltypes += t.count;
		}
		
		double correctness_average = calculateScoreMean(correctness);
		double correctness_std = calculateSTD(correctness, correctness_average);
		
		double t1_percentage = 1.0d * types[0].count / count_for_alltypes;
		double t2_percentage = 1.0d * types[1].count / count_for_alltypes;
		double t3_percentage = 1.0d * types[2].count / count_for_alltypes;
		double t4_percentage = 1.0d * types[3].count / count_for_alltypes;
		double t5_percentage = 1.0d * types[4].count / count_for_alltypes;
		double t6_percentage = 1.0d * types[5].count / count_for_alltypes;
		double t7_percentage = 1.0d * types[6].count / count_for_alltypes;
		
		double t1_avg_config = calculateScoreMean(types[0].config_effort);
		double t2_avg_config = calculateScoreMean(types[1].config_effort);
		double t4_avg_config = calculateScoreMean(types[3].config_effort);
		double t5_avg_config = calculateScoreMean(types[4].config_effort);
		double t1_std_config = calculateSTD(types[0].config_effort, t1_avg_config);
		double t2_std_config = calculateSTD(types[1].config_effort, t2_avg_config);
		double t4_std_config = calculateSTD(types[3].config_effort, t4_avg_config);
		double t5_std_config = calculateSTD(types[4].config_effort, t5_avg_config);
		
		double t2_avg_saveedit = calculateScoreMean(types[1].saveedit_effort);
		double t5_avg_saveedit = calculateScoreMean(types[4].saveedit_effort);
		double t2_std_saveedit = calculateSTD(types[1].saveedit_effort, t2_avg_saveedit);
		double t5_std_saveedit = calculateSTD(types[4].saveedit_effort, t5_avg_saveedit);
		
		System.out.println("-------------------------------------- Result -----------------------------------");
		System.out.println("correctness_avg: " + correctness_average);
		System.out.println("correctness_std: " + correctness_std);
		System.out.println("=============================================");
		System.out.println("t1_count: " + types[0].count + ", t1_percentage: " + t1_percentage);
		System.out.println("t2_count: " + types[1].count + ", t2_percentage: " + t2_percentage);
		System.out.println("t3_count: " + types[2].count + ", t3_percentage: " + t3_percentage);
		System.out.println("t4_count: " + types[3].count + ", t4_percentage: " + t4_percentage);
		System.out.println("t5_count: " + types[4].count + ", t5_percentage: " + t5_percentage);
		System.out.println("t6_count: " + types[5].count + ", t6_percentage: " + t6_percentage);
		System.out.println("t7_count: " + types[6].count + ", t7_percentage: " + t7_percentage);
		System.out.println("=============================================");
		System.out.println("t1_avg_config: " + t1_avg_config);
		System.out.println("t2_avg_config: " + t2_avg_config);
		System.out.println("t4_avg_config: " + t4_avg_config);
		System.out.println("t5_avg_config: " + t5_avg_config);
		System.out.println("t1_std_config: " + t1_std_config);
		System.out.println("t2_std_config: " + t2_std_config);
		System.out.println("t4_std_config: " + t4_std_config);
		System.out.println("t5_std_config: " + t5_std_config);
		System.out.println("=============================================");
		System.out.println("t2_avg_saveedit: " + t2_avg_saveedit);
		System.out.println("t5_avg_saveedit: " + t5_avg_saveedit);
		System.out.println("t2_std_saveedit: " + t2_std_saveedit);
		System.out.println("t5_std_saveedit: " + t5_std_saveedit);
		System.out.println("=============================================");
		System.out.println("total_historyNum: " + total_historyNum);
		System.out.println("total_environmentNum: " + total_environmentNum);
		System.out.println("total_ruleNum: " + total_ruleNum);
		System.out.println("total_totalNum: " + total_totalNum);
		System.out.println("=============================================");
		System.out.println("Trial for type3: " + types[2].instances.toString());
		System.out.println("Trial for type6: " + types[5].instances.toString());
		System.out.println("Trial for type7: " + types[6].instances.toString());
	}


	private static double calculateScoreMean(ArrayList<Double> rawScores) {
		double scoreAll = 0.0;
		for (Double score : rawScores) {
			scoreAll += score;
		}
		return scoreAll/rawScores.size();
	}
	
	private static double calculateSTD(ArrayList<Double> rawScores, double scoreMean) {
		double allSquare = 0.0;
		for (Double rawScore : rawScores) {
			allSquare += (rawScore - scoreMean)*(rawScore - scoreMean);
		}
		double denominator = rawScores.size() * (rawScores.size() - 1);
		return Math.sqrt(allSquare/denominator);
	}
}
