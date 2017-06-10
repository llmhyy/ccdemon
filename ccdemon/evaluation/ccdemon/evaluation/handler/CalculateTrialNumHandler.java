package ccdemon.evaluation.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import clonepedia.model.ontology.CloneSets;

public class CalculateTrialNumHandler extends AbstractHandler{
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Job job = new Job("calculating trial number"){

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				CalculateTrialNumHandler handler = new CalculateTrialNumHandler();
				handler.run();
				return Status.OK_STATUS;
			}
			
		};
		job.schedule();
		

		return null;
	}
	
	public void run(){
		CloneSets sets = clonepedia.Activator.plainSets;;
		
		System.out.println("Clone Set number: " + sets.getCloneList().size());
		int trialNum = 0;
		for(clonepedia.model.ontology.CloneSet clonepediaSet: sets.getCloneList()){
			trialNum += clonepediaSet.size() * (clonepediaSet.size() - 1);
		}
		
		System.out.println("Trial number: " + trialNum);
		
	}

}
