package ccdemon.handlers;

import mcidiff.util.ASTUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import ccdemon.model.SelectedCodeRange;
import ccdemon.util.CCDemonUtil;
import ccdemon.util.SharedData;

public class CopyHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		CCDemonUtil.callBackDefaultEvent("copy", event);
		
		/**
		 * record the location of copied code
		 */
		AbstractTextEditor activeEditor = (AbstractTextEditor) HandlerUtil.getActiveEditor(event);
		ISourceViewer sourceViewer = (ISourceViewer) activeEditor.getAdapter(ITextOperationTarget.class);		
		IEditorInput input = activeEditor.getEditorInput(); 
		IFile file = ((FileEditorInput) input).getFile(); 		
		Point point = sourceViewer.getSelectedRange();
		
		SelectedCodeRange range = new SelectedCodeRange(file.getRawLocation().toOSString(), point.x, point.x + point.y);
		SharedData.copiedRange = range;
		
		CompilationUnit cu = ASTUtil.generateCompilationUnit(range.getFileName(), null);
		System.out.println("startline: " + cu.getLineNumber(range.getStartPosition()) + ", endline: " + cu.getLineNumber(range.getEndPosition()));
		System.out.println("startPosition: " + point.x);
		System.out.println("length: " + point.y);
		
		return null;
	}

}
