package ccdemon.ui;

import java.util.ArrayList;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.link.ProposalPosition;

public class RankedProposalPosition extends ProposalPosition {
	
	private ArrayList<ICompletionProposal> proposalList = new ArrayList<ICompletionProposal>();
	
	public RankedProposalPosition(IDocument document, int offset, int length,
			ICompletionProposal[] proposals) {
		super(document, offset, length, proposals);
		
		ICompletionProposal[] fProposals = super.getChoices();
		for(ICompletionProposal p : fProposals){
			proposalList.add(p);
		}
	}
	
	public ICompletionProposal[] getChoices() {
		ICompletionProposal[] returnProposals = new ICompletionProposal[proposalList.size()];
		for(ICompletionProposal p : proposalList){
			returnProposals[proposalList.indexOf(p)] = p;
		}
		return returnProposals;
	}
	
	public void setChoices(ICompletionProposal[] proposals){
		proposalList.clear();
		for(ICompletionProposal p : proposals){
			proposalList.add(p);
		}
	}

}
