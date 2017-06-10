package ccdemon.model;

import java.util.Comparator;

public class CandidateComparator implements Comparator<Candidate>{

	private ConfigurationPointSet points;
	
	/**
	 * @param points
	 * @param table
	 */
	public CandidateComparator(ConfigurationPointSet points) {
		super();
		this.points = points;
	}

	@Override
	public int compare(Candidate candidate1, Candidate candidate2) {
		double ruleWeight = 0.6;
		double environmentWeight = 0;
		double historyWeight = 0;
		
		ConfigurationPoint point = candidate1.getConfigurationPoint();
		
		if(point.toString().contains("double")){
			//System.currentTimeMillis();
		}
		
		//the maxium entropy is log(m)
		double threshold = 0.5 * Math.log(point.getSeqMultiset().getSize());
		
		if(point.getHistoryEntropy() >= threshold){
			environmentWeight = 0.3;
			historyWeight = 0.1;
		}
		else{
			historyWeight = 0.3;
			environmentWeight = 0.1;
		}
		
		double score1 = candidate1.computeScore(ruleWeight, environmentWeight, historyWeight, points);
		double score2 = candidate2.computeScore(ruleWeight, environmentWeight, historyWeight, points);

		if(score1 == score2){
			return 0;
		}
		
		int flag = (score1 - score2 > 0) ? -1 : 1; 
		
		return flag;
	}

}
