package ccdemon.evaluation.handler;

import java.util.ArrayList;

import mcidiff.main.SeqMCIDiff;
import mcidiff.model.CloneInstance;
import mcidiff.model.CloneSet;
import mcidiff.model.SeqMultiset;
import mcidiff.model.TokenSeq;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;

import ccdemon.util.CCDemonUtil;
import clonepedia.model.ontology.CloneSets;

public class CountTypeIIorIIIHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Job job = new Job("counting type II or III"){

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				CountTypeIIorIIIHandler handler = new CountTypeIIorIIIHandler();
				try {
					handler.run();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return Status.OK_STATUS;
			}
			
		};
		job.schedule();
		

		return null;
	}

	private void run() throws Exception{
		CloneSets sets = clonepedia.Activator.plainSets;
		int count_typeII = 0;
		int count_typeIII = 0;
		
		for(clonepedia.model.ontology.CloneSet clonepediaSet: sets.getCloneList()){
			
			System.out.println("Clone set ID: " + clonepediaSet.getId());
			
			CloneSet set = CCDemonUtil.adaptMCIDiffModel(clonepediaSet);

			SeqMCIDiff diff = new SeqMCIDiff();
			IJavaProject proj = CCDemonUtil.retrieveWorkingJavaProject();
			ArrayList<SeqMultiset> diffList;
			diffList = diff.diff(set, proj);
			
			ArrayList<SeqMultiset> matchableDiffs = findMatchableDiff(diffList, set.getInstances().get(0));
			
			boolean isTypeIII = false;
			for(int i = 0; i < matchableDiffs.size() && !isTypeIII; i++){
				SeqMultiset seqMulti = matchableDiffs.get(i);
				for(TokenSeq seq : seqMulti.getSequences()){
					if(seq.isEpisolonTokenSeq()){
						isTypeIII = true;
						break;
					}
				}
			}
			if(isTypeIII){
				count_typeIII++;
			}else{
				count_typeII++;
			}
		}
		System.out.println("==========================Result==========================");
		System.out.println("count_typeII: " + count_typeII);
		System.out.println("count_typeIII: " + count_typeIII);
	}
	

	
	/**
	 * Given a target instance, the diffs can be divided into two categories: 
	 * Type I: The token sequence for target instance is different from the token sequences for other instances, 
	 * in this case, those token sequences in other instances are exactly the same.
	 * Type II (i.e., matchable diff): The token sequences for other instances are different with each other.
	 * 
	 * By differentiating these differences, I am able to identify the correct candidate for each configuration
	 * point.
	 * 
	 * @param diffList
	 * @return
	 */
	private ArrayList<SeqMultiset> findMatchableDiff(ArrayList<SeqMultiset> diffList, CloneInstance targetInstance){
		ArrayList<SeqMultiset> typeTwoDiffList = new ArrayList<>();
		
		for(SeqMultiset diff : diffList){
			//TokenSeq targetSeq = diff.findTokenSeqByCloneInstance(targetInstance);
			int difference = 0;
			for(int i = 0; i < diff.getSequences().size(); i++){
				if(!diff.getSequences().get(i).getCloneInstance().equals(targetInstance)){
					for(int j = i + 1; j < diff.getSequences().size(); j++){
						if(!diff.getSequences().get(j).getCloneInstance().equals(targetInstance)){
							if(!diff.getSequences().get(i).equals(diff.getSequences().get(j))){
								difference++;
							}
						}
					}
				}
			}
			if(difference != 0){
				SeqMultiset newDiff = new SeqMultiset();
				for(TokenSeq seq : diff.getSequences()){
					newDiff.addTokenSeq(seq);
				}
				typeTwoDiffList.add(newDiff);
			}
		}
		
		return typeTwoDiffList;
	}
}
