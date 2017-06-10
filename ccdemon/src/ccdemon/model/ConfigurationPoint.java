package ccdemon.model;


import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import mcidiff.model.SeqMultiset;
import mcidiff.model.Token;
import mcidiff.model.TokenSeq;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.SimpleName;

import ccdemon.util.CCDemonUtil;

public class ConfigurationPoint {
	/**
	 * The token sequence that need to be changed.
	 */
	private TokenSeq modifiedTokenSeq;
	/**
	 * The copied token sequence, which is different from (@code modifiedTokenSeq) in that
	 * their position and AST nodes are different.
	 */
	private TokenSeq copiedTokenSeq;
	/**
	 * candidate token sequences to replace {@code tokenSeq} field
	 */
	private SeqMultiset seqMultiset;
	
	private String currentValue;
	private double historyEntropy;
	
	private boolean configured = false;
	
	private ArrayList<Candidate> candidates = new ArrayList<>();
	/**
	 * @param copiedTokenSeq
	 * @param seqMultiset
	 */
	public ConfigurationPoint(TokenSeq copiedTokenSeq, SeqMultiset seqMultiset) {
		super();
		this.copiedTokenSeq = copiedTokenSeq;
		this.seqMultiset = seqMultiset;
		this.currentValue = copiedTokenSeq.getText();
		
		organizeHistoricalCandidate(getSeqMultiset());
	}
	
	public boolean containsExpression(){
		for(TokenSeq seq: this.seqMultiset.getSequences()){
			if(!seq.isSingleToken()){
				return true;
			}
		}
		
		return false;
	}
	
	public void addCandidate(Candidate candidate){
		Candidate existingCandidate = null;
		for(Candidate c: getCandidates()){
			if(c.getText().toLowerCase().equals(candidate.getText().toLowerCase())){
				existingCandidate = c;
				break;
			}
		}
		
		if(existingCandidate == null){
			getCandidates().add(candidate);
		}
		else{
			for(String origin: candidate.getOriginList()){
				existingCandidate.addOrigin(origin);
			}
		}
	}
	
	public Candidate getOriginalCandidate(){
		for(Candidate candidate: getCandidates()){
			if(candidate.getText().equals(copiedTokenSeq.getText())){
				return candidate;
			}
		}
		
		return null;
	}
	
	public void clearRuleGeneratedCandidates(){
		Iterator<Candidate> iterator = this.candidates.iterator();
		while(iterator.hasNext()){
			Candidate candidate = iterator.next();
			if(candidate.isRuleBased() && candidate.getOriginList().size()==1){
				iterator.remove();
			}
		}
	}
	
	public boolean contains(String candidateString){
		for(Candidate candidate: this.candidates){
			if(candidate.getText().equals(candidateString)){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean containsByIgnoringCase(String candidateString){
		for(Candidate candidate: this.candidates){
			if(candidate.getText().toLowerCase().equals(candidateString.toLowerCase())){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isType(){
		for(TokenSeq tokenSeq: seqMultiset.getSequences()){
			if(tokenSeq.isSingleToken()){
				Token t = tokenSeq.getTokens().get(0);
				ASTNode node = t.getNode();
				if(node instanceof SimpleName){
					SimpleName name = (SimpleName)node;
					IBinding binding = name.resolveBinding();
					if(binding != null && binding instanceof ITypeBinding){
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean isVariableOrField(){
		for(TokenSeq tokenSeq: seqMultiset.getSequences()){
			if(tokenSeq.isSingleToken()){
				Token t = tokenSeq.getTokens().get(0);
				ASTNode node = t.getNode();
				if(node instanceof SimpleName){
					SimpleName name = (SimpleName)node;
					IBinding binding = name.resolveBinding();
					if(binding != null && binding instanceof IVariableBinding){
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean isMethod(){
		for(TokenSeq tokenSeq: seqMultiset.getSequences()){
			if(tokenSeq.isSingleToken()){
				Token t = tokenSeq.getTokens().get(0);
				ASTNode node = t.getNode();
				if(node instanceof SimpleName){
					SimpleName name = (SimpleName)node;
					IBinding binding = name.resolveBinding();
					if(binding != null && binding instanceof IMethodBinding){
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean isConstructor(){
		for(TokenSeq tokenSeq: seqMultiset.getSequences()){
			if(tokenSeq.isSingleToken()){
				Token t = tokenSeq.getTokens().get(0);
				ASTNode node = t.getNode();
				if(node instanceof SimpleName){
					SimpleName name = (SimpleName)node;
					IBinding binding = name.resolveBinding();
					if(binding != null && binding instanceof IMethodBinding){
						IMethodBinding mBinding = (IMethodBinding)binding;
						return mBinding.isConstructor();
					}
				}
			}
		}
		
		return false;
	}

	private void organizeHistoricalCandidate(SeqMultiset seqMultiset) {
		HashMap<TokenSeq, Integer> map = new HashMap<>();
		for(TokenSeq seq: seqMultiset.getSequences()){
			Integer count = map.get(seq);
			if(count == null){
				count = new Integer(1);
			}
			else{
				count++;
			}
			
			map.put(seq, count);
		}
		
		double entropy = 0;
		for(TokenSeq seq: map.keySet()){
			double occurrence = map.get(seq);
			double freq = occurrence/seqMultiset.getSize();
			entropy += -1 * freq * Math.log(freq);
			
			/**
			 * transfer some special token, in this case, "else" need an extra while space.
			 */
			String text = seq.getText();
			text = transferTextAccordingHeuristicRule(text);
			seq.setText(text);
			
			Candidate candidate = new Candidate(seq.getText(), 0, Candidate.HISTORY, this);
			candidates.add(candidate);
		}
		setHistoryEntropy(entropy);
	}
	
	/**
	 * transfer some special token, in this case, "else" need an extra while space.
	 */
	private String transferTextAccordingHeuristicRule(String text){
		if(text.endsWith("else")){
			text += " ";
			return text;
		}
		return text;
	}
	
	public int findHistoryCandidateOccurenceNumber(String candidateText){
		int count = 0;
		for(TokenSeq seq: this.seqMultiset.getSequences()){
			String seqText = seq.getText();
			if(seqText.equals(candidateText)){
				count++;
			}
		}
		
		return count;
	}
	
	public int getHistoryCandidateNumber(){
		int count = 0;
		for(Candidate candidate: getCandidates()){
			if(candidate.isHistoryBased()){
				count++;
			}
		}
		
		return count;
	}

	@SuppressWarnings("rawtypes")
	public ArrayList<Class> getSuperClasses(){
	    URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		try {
			IJavaProject proj = CCDemonUtil.retrieveWorkingJavaProject();
			IClasspathEntry[] entries = proj.getRawClasspath();
			IPath fullPathOfProj = proj.getProject().getLocation();
			for(IClasspathEntry entry : entries){
				File f = new File(fullPathOfProj.toOSString() + "\\" + entry.getPath().makeRelativeTo(proj.getPath()).toOSString());
//				File f = entry.getPath().toFile();
			    URI u = f.toURI();
			    Class<URLClassLoader> urlClass = URLClassLoader.class;
			    Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
			    method.setAccessible(true);
			    method.invoke(urlClassLoader, new Object[]{u.toURL()});
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		ArrayList<Class> classList = new ArrayList<Class>();
		for(TokenSeq tokenSeq: seqMultiset.getSequences()){
			if(tokenSeq.isSingleToken()){
				Token t = tokenSeq.getTokens().get(0);
				ASTNode node = t.getNode();
				if(node instanceof SimpleName){
					SimpleName name = (SimpleName)node;
					IBinding binding = name.resolveBinding();
					if(binding != null && binding instanceof ITypeBinding){
						ITypeBinding typeBinding = (ITypeBinding) binding;
						try {
							ITypeBinding superTypeBinding = typeBinding.getSuperclass();
							if(superTypeBinding != null){//not Object.class
								Class<?> c = Class.forName(superTypeBinding.getQualifiedName(), true, urlClassLoader);
								if(!classList.contains(c)){
									classList.add(c);
								}
							}
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		return classList;
	}
	
	public ArrayList<String> getVariableOrFieldTypes(){
		ArrayList<String> typeList = new ArrayList<String>();
		for(TokenSeq tokenSeq: seqMultiset.getSequences()){
			if(tokenSeq.isSingleToken()){
				Token t = tokenSeq.getTokens().get(0);
				ASTNode node = t.getNode();
				if(node instanceof SimpleName){
					SimpleName name = (SimpleName)node;
					IBinding binding = name.resolveBinding();
					if(binding != null && binding instanceof IVariableBinding){
						if(!typeList.contains(((IVariableBinding)binding).getType().getQualifiedName())){
							typeList.add(((IVariableBinding)binding).getType().getQualifiedName());
						}
					}
				}
			}
		}
		
		return typeList;
	}


	public ArrayList<String> getVariableOrFieldSuperTypes(){
		ArrayList<String> superTypeList = new ArrayList<String>();
		for(TokenSeq tokenSeq: seqMultiset.getSequences()){
			if(tokenSeq.isSingleToken()){
				Token t = tokenSeq.getTokens().get(0);
				ASTNode node = t.getNode();
				if(node instanceof SimpleName){
					SimpleName name = (SimpleName)node;
					IBinding binding = name.resolveBinding();
					if(binding != null && binding instanceof IVariableBinding){
						//super class
						if(((IVariableBinding)binding).getType().getSuperclass() != null &&
								!((IVariableBinding)binding).getType().getSuperclass().getQualifiedName().equals("java.lang.Object") &&
								!superTypeList.contains(((IVariableBinding)binding).getType().getSuperclass().getQualifiedName())){
							String superTypeName = ((IVariableBinding)binding).getType().getSuperclass().getQualifiedName();
							if(!superTypeName.equals("java.lang.Object")){
								superTypeList.add(superTypeName);								
							}
							
						}
					}
				}
			}
		}
		
		return superTypeList;
	}
	
	public ArrayList<String> getVariableOrFieldInterfaceTypes(){
		ArrayList<String> interfaceTypeList = new ArrayList<String>();
		for(TokenSeq tokenSeq: seqMultiset.getSequences()){
			if(tokenSeq.isSingleToken()){
				Token t = tokenSeq.getTokens().get(0);
				ASTNode node = t.getNode();
				if(node instanceof SimpleName){
					SimpleName name = (SimpleName)node;
					IBinding binding = name.resolveBinding();
					if(binding != null && binding instanceof IVariableBinding){
						//interface
						if(((IVariableBinding)binding).getType().getInterfaces().length != 0){
							for(ITypeBinding interfaceType : ((IVariableBinding)binding).getType().getInterfaces()){
								if(!interfaceTypeList.contains(interfaceType.getQualifiedName())){
									String interfaceName = interfaceType.getQualifiedName();
									if(!interfaceName.equals("java.io.Serializable")){
										interfaceTypeList.add(interfaceName);										
									}
								}
							}
						}
					}
				}
			}
		}
		
		return interfaceTypeList;
	}
	
	/**
	 * @return the tokenSeq
	 */
	public String toString(){
		return copiedTokenSeq.toString();
	}
	
	public TokenSeq getCopiedTokenSeq() {
		return copiedTokenSeq;
	}
	/**
	 * @return the candidates
	 */
	public ArrayList<Candidate> getCandidates() {
		return candidates;
	}

	/**
	 * @param candidates the candidates to set
	 */
	public void setCandidates(ArrayList<Candidate> candidates) {
		this.candidates = candidates;
	}

	/**
	 * @param tokenSeq the tokenSeq to set
	 */
	public void setCopiedTokenSeq(TokenSeq tokenSeq) {
		this.copiedTokenSeq = tokenSeq;
	}
	/**
	 * @return the seqMultiset
	 */
	public SeqMultiset getSeqMultiset() {
		return seqMultiset;
	}
	/**
	 * @param seqMultiset the seqMultiset to set
	 */
	public void setSeqMultiset(SeqMultiset seqMultiset) {
		this.seqMultiset = seqMultiset;
	}
	/**
	 * @return the modifiedTokenSeq
	 */
	public TokenSeq getModifiedTokenSeq() {
		return modifiedTokenSeq;
	}
	/**
	 * @param modifiedTokenSeq the modifiedTokenSeq to set
	 */
	public void setModifiedTokenSeq(TokenSeq modifiedTokenSeq) {
		this.modifiedTokenSeq = modifiedTokenSeq;
	}

	/**
	 * @return the currentValue
	 */
	public String getCurrentValue() {
		return currentValue;
	}

	/**
	 * @param currentValue the currentValue to set
	 */
	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}

	/**
	 * @return the historyEntropy
	 */
	public double getHistoryEntropy() {
		return historyEntropy;
	}

	/**
	 * @param historyEntropy the historyEntropy to set
	 */
	public void setHistoryEntropy(double historyEntropy) {
		this.historyEntropy = historyEntropy;
	}

	/**
	 * @return the configured
	 */
	public boolean isConfigured() {
		return configured;
	}

	/**
	 * @param configured the configured to set
	 */
	public void setConfigured(boolean configured) {
		this.configured = configured;
	}
	
	
}
