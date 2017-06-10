package ccdemon.evaluation.handler;

import java.util.ArrayList;

import mcidiff.model.CloneSet;
import mcidiff.model.Token;
import mcidiff.model.TokenSeq;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import ccdemon.evaluation.main.CloneRecoverer;
import ccdemon.evaluation.main.CloneRecoverer.CollectedData;
import ccdemon.model.ConfigurationPoint;
import ccdemon.util.CCDemonUtil;
import clonepedia.model.ontology.CloneSets;

public class SearchTypeConfigurationPointHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Job job = new Job("searching type configuration point"){

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				SearchTypeConfigurationPointHandler handler = new SearchTypeConfigurationPointHandler();
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
		
		ArrayList<CollectedData> dataList = new ArrayList<>();
		
		for(clonepedia.model.ontology.CloneSet clonepediaSet: sets.getCloneList()){

			CloneSet set = CCDemonUtil.adaptMCIDiffModel(clonepediaSet);
			ArrayList<CollectedData> datas = recoverer.getTrials(set);
			
			for(CollectedData data : datas){
				if(data.getConfigurationPointNum() >= 5){
					for(ConfigurationPoint cp : data.getCps().getConfigurationPoints()){
						if(cp.isType()){
							ArrayList<String> superClassTypes = new ArrayList<String>();
							boolean contextRelated = false;
							boolean sameParent = true;
							for(TokenSeq seq : cp.getSeqMultiset().getSequences()){
								Token t = seq.getTokens().get(0);
								ASTNode node = t.getNode();
								if(node == null){ //this point is e*
									sameParent = false;
									continue;
								}
								
								SimpleName name = (SimpleName)node;
								ITypeBinding binding = (ITypeBinding) name.resolveBinding();
								if(binding.getSuperclass() != null && !binding.getSuperclass().getQualifiedName().equals("java.lang.Object")){
									if(!superClassTypes.contains(binding.getSuperclass().getQualifiedName())){
										superClassTypes.add(binding.getSuperclass().getQualifiedName());
									}
								}else{
									sameParent = false;
									continue;
								}
								
								while(node != null && !(node instanceof MethodDeclaration) && !(node instanceof TypeDeclaration)){
									node = node.getParent();
								}
								if(node instanceof MethodDeclaration){
									MethodDeclaration md = (MethodDeclaration) node;
									Type type = md.getReturnType2();
									if(type != null){
										String typeName = type.toString();
										if(typeName.contains(seq.getText()) || seq.getText().contains(typeName)){
											contextRelated = true;
										}
									}
									while(!(node instanceof TypeDeclaration) && node != null){
										node = node.getParent();
									}
								}
								if(node instanceof TypeDeclaration){
									TypeDeclaration td = (TypeDeclaration)node;
									String typeName = td.getName().getIdentifier();
									if(typeName.contains(seq.getText()) || seq.getText().contains(typeName)){
										contextRelated = true;
									}
								}
							}
							if(sameParent){
								sameParent = (superClassTypes.size() == 1);
							}

							if(!contextRelated && sameParent){
								dataList.add(data);
							}
						}
					}
				}
			}
		}

		System.out.println("-------------------------------------- Result -----------------------------------");
		System.out.println(dataList.toString());
	}
}
