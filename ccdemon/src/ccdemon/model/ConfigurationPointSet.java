package ccdemon.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import mcidiff.model.CloneInstance;
import mcidiff.model.CloneSet;
import mcidiff.model.SeqMultiset;
import mcidiff.model.Token;
import mcidiff.model.TokenSeq;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import ccdemon.model.rule.NameInstance;
import ccdemon.model.rule.NamingRule;
import ccdemon.model.rule.RuleItem;


public class ConfigurationPointSet {
	
	private ArrayList<ConfigurationPoint> configurationPoints = new ArrayList<>();
	//private CloneSet referrableCloneSet;
	
	private CompilationUnit pastedCompilationUnit;
	private int startPositionInPastedFile;
	
	private NamingRule rule;
	private OccurrenceTable occurrences;
	
	public static long APITime = 0;

	public class ContextContent{
		private AbstractTypeDeclaration typeDeclaration;
		private MethodDeclaration methodDeclaration;
		
		/**
		 * @param typeDeclaration
		 * @param methodDeclaration
		 */
		public ContextContent(AbstractTypeDeclaration typeDeclaration,
				MethodDeclaration methodDeclaration) {
			super();
			this.typeDeclaration = typeDeclaration;
			this.methodDeclaration = methodDeclaration;
		}
		/**
		 * @return the typeDeclaration
		 */
		public AbstractTypeDeclaration getTypeDeclaration() {
			return typeDeclaration;
		}
		/**
		 * @param typeDeclaration the typeDeclaration to set
		 */
		public void setTypeDeclaration(AbstractTypeDeclaration typeDeclaration) {
			this.typeDeclaration = typeDeclaration;
		}
		/**
		 * @return the methodDeclaration
		 */
		public MethodDeclaration getMethodDeclaration() {
			return methodDeclaration;
		}
		/**
		 * @param methodDeclaration the methodDeclaration to set
		 */
		public void setMethodDeclaration(MethodDeclaration methodDeclaration) {
			this.methodDeclaration = methodDeclaration;
		}
	}
	
	public ConfigurationPointSet(){}
	
	/**
	 * @param configurationPoints
	 */
	public ConfigurationPointSet(ArrayList<ConfigurationPoint> configurationPoints, 
			CloneSet referrableCloneSet, CompilationUnit pastedCompilationUnit,
			int startPositionInPastedFile) {
		super();
		this.configurationPoints = configurationPoints;
		//this.referrableCloneSet = referrableCloneSet;
		this.pastedCompilationUnit = pastedCompilationUnit;
		this.startPositionInPastedFile = startPositionInPastedFile;
	}
	
	/**
	 * identify environmental and rule-based candidates; in addition, the method will also rank them.
	 * @param referrableCloneSets
	 */
	public void prepareForInstallation(ArrayList<ReferrableCloneSet> referrableCloneSets){
		
		expandEnvironmentBasedCandidates(this.configurationPoints);
		generateNamingRules(this.configurationPoints, referrableCloneSets.get(0).getCloneSet());
		
		this.occurrences = constructCandidateOccurrences(referrableCloneSets);
		
		ContextContent content = parseThisContext();
		if(content.getTypeDeclaration() != null){
			String typeName = content.getTypeDeclaration().getName().getIdentifier();
			getRule().applyTypeNameRule(typeName);
		}
		if(content.getMethodDeclaration() != null){
			String methodName = content.getMethodDeclaration().getName().getIdentifier();
			ASTNode node = content.getMethodDeclaration().getReturnType2();
			String methodReturnType = null;
			if(null == node){
				methodReturnType = content.getMethodDeclaration().getName().getFullyQualifiedName();
			}
			else{
				methodReturnType = content.getMethodDeclaration().getReturnType2().toString();
			}
			//String methodReturnType = content.getMethodDeclaration().getReturnType2().toString();
			getRule().applyMethodNameRule(methodName);
			getRule().applyMethodReturnTypeRule(methodReturnType);
		}
		
		adjustCandidateRanking();
	}
	
	private ContextContent parseThisContext(){
		
		final ContextContent content = new ContextContent(null, null);
		
		pastedCompilationUnit.accept(new ASTVisitor() {
			public boolean visit(TypeDeclaration td){
				int start = td.getStartPosition();
				int end = start + td.getLength();
				
				if(start <= startPositionInPastedFile && end >= startPositionInPastedFile){
					content.setTypeDeclaration(td);
				}
				
				return true;
			}
			
			public boolean visit(MethodDeclaration md){
				int start = md.getStartPosition();
				int end = start + md.getLength();
				
				if(start <= startPositionInPastedFile && end >= startPositionInPastedFile){
					content.setMethodDeclaration(md);
				}
				
				return false;
			}
		});
		
		return content;
	}
	
	private void generateNamingRules(
			ArrayList<ConfigurationPoint> configurationPoints, CloneSet referrableCloneSet) {
		
		NamingRule rule = new NamingRule();
		
		RuleItem typeNameItem = new RuleItem(null);
		typeNameItem.setTypeItem(true);
		rule.addItem(typeNameItem);
		
		RuleItem methodNameItem = new RuleItem(null);
		methodNameItem.setMethodNameItem(true);
		rule.addItem(methodNameItem);
		
		RuleItem methodReturnTypeItem = new RuleItem(null);
		methodReturnTypeItem.setMethodReturnTypeItem(true);
		rule.addItem(methodReturnTypeItem);
		
		RuleItem[] items = new RuleItem[configurationPoints.size()];
		for(int i=0; i<items.length; i++){
			items[i] = new RuleItem(configurationPoints.get(i));
			rule.addItem(items[i]);
		}
		
		for(CloneInstance instance: referrableCloneSet.getInstances()){
			ContextContent content = parseContextPoint(instance);	
			
			String typeName = content.getTypeDeclaration().getName().getIdentifier();
			typeNameItem.addNameInstance(new NameInstance(typeName, true));
			
			if(content.getMethodDeclaration() != null){
				String methodName = content.getMethodDeclaration().getName().getIdentifier();			
				methodNameItem.addNameInstance(new NameInstance(methodName, true));	
				
				ASTNode node = content.getMethodDeclaration().getReturnType2();
				String methodReturnType = null;
				if(null == node){
					methodReturnType = content.getMethodDeclaration().getName().getFullyQualifiedName();
				}
				else{
					methodReturnType = content.getMethodDeclaration().getReturnType2().toString();
				}
				methodReturnTypeItem.addNameInstance(new NameInstance(methodReturnType, true));
			}
			else{
				methodNameItem.addNameInstance(new NameInstance("", true));
				methodReturnTypeItem.addNameInstance(new NameInstance("", true));
			}
			
			for(int i=0; i<configurationPoints.size(); i++){
				ConfigurationPoint point = configurationPoints.get(i);
				SeqMultiset set = point.getSeqMultiset();
				TokenSeq seq = set.findTokenSeqByCloneInstance(instance.getFileName(), 
						instance.getStartLine(), instance.getEndLine());
				
				items[i].addNameInstance(new NameInstance(seq.getText(), seq.isSingleToken()));
			}
		}
		
		rule.parseNamingPattern();

		setRule(rule);
	}
 
	private ContextContent parseContextPoint(CloneInstance instance) {
		ASTNode node = null;
		for(Token t: instance.getTokenList()){
			if(!t.isEpisolon()){
				node = t.getNode();
				break;
			}
		}
		AbstractTypeDeclaration typeDeclaration = null;
		MethodDeclaration methodDeclaration = null;
		
		while(node != null){
			if(node instanceof MethodDeclaration && methodDeclaration == null){
				methodDeclaration = (MethodDeclaration)node;
			}
			else if(node instanceof AbstractTypeDeclaration && typeDeclaration == null){
				typeDeclaration = (AbstractTypeDeclaration)node;
			}
			
			if(methodDeclaration != null && typeDeclaration != null){
				break;
			}
			
			node = node.getParent();
		}
		
		ContextContent items = new ContextContent(typeDeclaration, methodDeclaration);
		return items;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void expandEnvironmentBasedCandidates(
			ArrayList<ConfigurationPoint> configurationPoints) {
		APITime = 0;
		for(ConfigurationPoint point: configurationPoints){
			if(point.isType()){
				//find its sibling types
				ArrayList<Class> types = point.getSuperClasses();
				ArrayList<Class> siblings = new ArrayList<Class>();
				for(Class c : types){
					long startAPITime = System.currentTimeMillis();
					ConfigurationBuilder cb = new ConfigurationBuilder().setScanners(new SubTypesScanner());
					cb.addUrls(ClasspathHelper.forClass(c));
				    Reflections reflections = new Reflections(cb);				
					Set<Class<?>> subset = reflections.getSubTypesOf(c);
					long endAPITime = System.currentTimeMillis();
					APITime += endAPITime - startAPITime;
					if(subset.size() != 0){
						for(Class sub : subset){
							//filter non direct class out
							if(!sub.getSuperclass().equals(c)){
								continue;
							}
							
							//avoid duplication with existing type candidate
							boolean candidateExist = false;
							for(Candidate candidate : point.getCandidates()){
								if(candidate.getText().equals(sub.getSimpleName())){
									candidateExist = true;
									break;
								}
							}
							if(!candidateExist && !siblings.contains(sub)){
								siblings.add(sub);
								//point.getCandidates().add(new Candidate(sub.getSimpleName(), 0 ,Candidate.ENVIRONMENT, point));
							}
							if(!point.contains(sub.getSimpleName())){
								point.addCandidate(new Candidate(sub.getSimpleName(), 0, Candidate.ENVIRONMENT, point));								
							}
						}
					}
				}
			}
			else if(point.isVariableOrField()){
				//find compatible variable in the context
				ArrayList<String> types = point.getVariableOrFieldTypes();
				ArrayList<String> superTypes = point.getVariableOrFieldSuperTypes();
				ArrayList<String> interfaceTypes = point.getVariableOrFieldInterfaceTypes();
				String pastedVariableName = point.getCopiedTokenSeq().getText();
				
				NodeFinder finder = new NodeFinder(pastedCompilationUnit, startPositionInPastedFile, 1);
				ASTNode node = finder.getCoveringNode();
				while(!(node instanceof MethodDeclaration || node instanceof TypeDeclaration || node instanceof CompilationUnit)){
					node = node.getParent();
				}
				//if copied code is put in a method, we need to search for the variable in the method as well
				if(node instanceof MethodDeclaration){
					VariableVisitor visitor = new VariableVisitor(types, superTypes, interfaceTypes, pastedVariableName);
					node.accept(visitor);
					for(String variable : visitor.getCompatibleVariables()){
						//point.getCandidates().add(new Candidate(variable, 0, Candidate.ENVIRONMENT, point));
						point.addCandidate(new Candidate(variable, 0, Candidate.ENVIRONMENT, point));
					}
				}
				//otherwise, we just search those fields
				FieldVisitor visitor = new FieldVisitor(types, superTypes, interfaceTypes, pastedVariableName);
				pastedCompilationUnit.accept(visitor);
				for(String variable : visitor.getCompatibleVariables()){
					//point.getCandidates().add(new Candidate(variable, 0, Candidate.ENVIRONMENT, point));
					point.addCandidate(new Candidate(variable, 0, Candidate.ENVIRONMENT, point));
				}
			}
			else if(point.isMethod()){
				//do nothing for now
			}
		}
	}

	private OccurrenceTable constructCandidateOccurrences(ArrayList<ReferrableCloneSet> referrableCloneSets) {
		ReferrableCloneSet rcs = referrableCloneSets.get(0);
		String[][] occurrenceTable = new String[rcs.getCloneSet().size()][configurationPoints.size()];
		CloneInstance[] instanceArray = rcs.getCloneSet().getInstances().toArray(new CloneInstance[0]);
		
		for(int i=0; i<instanceArray.length; i++){
			CloneInstance instance = instanceArray[i];
			
			for(int j=0; j<configurationPoints.size(); j++){
				ConfigurationPoint cp = configurationPoints.get(j);
				TokenSeq tokenSeq = cp.getSeqMultiset().findTokenSeqByCloneInstance(instance.getFileName(), 
						instance.getStartLine(), instance.getEndLine());
				if(tokenSeq != null){
					occurrenceTable[i][j] = tokenSeq.getText();
				}
			}
		}
		
		return new OccurrenceTable(occurrenceTable);
	}

	public void adjustCandidateRanking() {
		CandidateComparator comparator = new CandidateComparator(this);
		
		for(ConfigurationPoint point: this.configurationPoints){
			Collections.sort(point.getCandidates(), comparator);
			
			System.currentTimeMillis();
		}
		
		/*if(isFirstRanking){
			for(ConfigurationPoint point: this.configurationPoints){
				Candidate originalCandidate = point.getOriginalCandidate();
				if(originalCandidate != null){
					int index = -1;
					for(int i=0; i<point.getCandidates().size(); i++){
						if(point.getCandidates().get(i) == originalCandidate){
							index = i;
						}
					}
					
					for(int i=0; i<index; i++){
						Candidate cand = point.getCandidates().get(i);
						point.getCandidates().set(i+1, cand);
					}
					point.getCandidates().set(0, originalCandidate);
				}
				
			}
			
			isFirstRanking = false;
		}*/
		
	}
	
	public int size(){
		return this.getConfigurationPoints().size();
	}
	
	/**
	 * @return the rule
	 */
	public NamingRule getRule() {
		return rule;
	}

	/**
	 * @param rule the rule to set
	 */
	public void setRule(NamingRule rule) {
		this.rule = rule;
	}

	/**
	 * @return the configurationPoints
	 */
	public ArrayList<ConfigurationPoint> getConfigurationPoints() {
		return configurationPoints;
	}

	/**
	 * @param configurationPoints the configurationPoints to set
	 */
	public void setConfigurationPoints(
			ArrayList<ConfigurationPoint> configurationPoints) {
		this.configurationPoints = configurationPoints;
	}
	
	public class VariableVisitor extends ASTVisitor{
		ArrayList<String> types;
		ArrayList<String> superTypes;
		ArrayList<String> interfaceTypes;
		ArrayList<String> compatibleVariables = new ArrayList<String>();
		String pastedVariableName;
		
		public VariableVisitor(ArrayList<String> types, ArrayList<String> superTypes, ArrayList<String> interfaceTypes, String pastedVariableName){
			this.types = types;
			this.superTypes = superTypes;
			this.interfaceTypes = interfaceTypes;
			this.pastedVariableName = pastedVariableName;
		}
		
		public boolean visit(SimpleName name){
			//avoid finding variable appears in the pasted code fragment
			if(name.getStartPosition() > startPositionInPastedFile){
				return false;
			}
			IBinding binding = name.resolveBinding();
			if(binding != null && binding instanceof IVariableBinding){
				if(types.contains(((IVariableBinding) binding).getType().getQualifiedName()) ||
						(((IVariableBinding) binding).getType().getSuperclass() != null &&
						!((IVariableBinding)binding).getType().getSuperclass().getQualifiedName().equals("java.lang.Object") &&
						superTypes.contains(((IVariableBinding) binding).getType().getSuperclass().getQualifiedName()))){
					//don't add the just pasted variable in, otherwise will cause duplication
					if(!name.toString().equals(pastedVariableName) && !this.compatibleVariables.contains(name.toString())){
						this.compatibleVariables.add(name.toString());
					}
				}
				for(ITypeBinding interfaceBinding : ((IVariableBinding) binding).getType().getInterfaces()){
					if(interfaceTypes.contains(interfaceBinding.getQualifiedName())){
						//don't add the just pasted variable in, otherwise will cause duplication
						if(!name.toString().equals(pastedVariableName) && !this.compatibleVariables.contains(name.toString())){
							this.compatibleVariables.add(name.toString());
						}
					}
				}
			}
			return false;
		}
		
		public ArrayList<String> getCompatibleVariables(){
			return this.compatibleVariables;
		}
	}
	
	public class FieldVisitor extends ASTVisitor{
		ArrayList<String> types;
		ArrayList<String> superTypes;
		ArrayList<String> interfaceTypes;
		ArrayList<String> compatibleVariables = new ArrayList<String>();
		String pastedVariableName;
		
		public FieldVisitor(ArrayList<String> types, ArrayList<String> superTypes, ArrayList<String> interfaceTypes, String pastedVariableName){
			this.types = types;
			this.superTypes = superTypes;
			this.interfaceTypes = interfaceTypes;
			this.pastedVariableName = pastedVariableName;
		}
		
		public boolean visit(FieldDeclaration field){
			//avoid finding field appears in the pasted code fragment
			if(field.getStartPosition() > startPositionInPastedFile){
				return false;
			}
			if(types.contains(field.getType().resolveBinding().getQualifiedName()) ||
					(field.getType().resolveBinding().getSuperclass() != null &&
					!field.getType().resolveBinding().getSuperclass().getQualifiedName().equals("java.lang.Object") &&
					superTypes.contains(field.getType().resolveBinding().getSuperclass().getQualifiedName()))){
				//don't add the just pasted variable in, otherwise will cause duplication
				String fieldName = ((VariableDeclarationFragment)field.fragments().get(0)).getName().toString();
				if(!fieldName.equals(pastedVariableName) && !this.compatibleVariables.contains(fieldName)){
					this.compatibleVariables.add(fieldName);
				}
			}
			for(ITypeBinding interfaceBinding : field.getType().resolveBinding().getInterfaces()){
				if(interfaceTypes.contains(interfaceBinding.getQualifiedName())){
					//don't add the just pasted variable in, otherwise will cause duplication
					String fieldName = ((VariableDeclarationFragment)field.fragments().get(0)).getName().toString();
					if(!fieldName.equals(pastedVariableName) && !this.compatibleVariables.contains(fieldName)){
						this.compatibleVariables.add(fieldName);
					}
				}
			}
			return false;
		}
		
		public ArrayList<String> getCompatibleVariables(){
			return this.compatibleVariables;
		}
	}

	/**
	 * @return the occurrences
	 */
	public OccurrenceTable getOccurrences() {
		return occurrences;
	}
	
	
}
