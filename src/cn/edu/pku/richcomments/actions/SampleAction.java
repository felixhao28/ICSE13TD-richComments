package cn.edu.pku.richcomments.actions;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.text.edits.InsertEdit;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.jface.dialogs.MessageDialog;

import cn.edu.pku.richcomments.dom.RCDesc;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class SampleAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;

	/**
	 * The constructor.
	 */
	public SampleAction() {
	}

	public ICompilationUnit getWorkingCopy(IPath path) {
		// get corresponding working copy
		ICompilationUnit compUnit = null;
		ICompilationUnit[] copies = JavaCore.getWorkingCopies(null);
		for (ICompilationUnit copy : copies) {
			IPath path2 = copy.getPath().makeAbsolute();
			if (path.makeAbsolute().equals(path2)) {
				compUnit = copy;
				break;
			}
		}
		return compUnit;
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		IWorkbenchPage page = window.getActivePage();
		IEditorPart editor = page.getActiveEditor();
		if (!(editor instanceof CompilationUnitEditor)) {
			return;
		}

		RCDesc desc = DescInput.getDesc();
		if (desc==null)
			return;

		CompilationUnitEditor CUEditor = (CompilationUnitEditor) editor;
		IDocumentProvider docProvider = CUEditor.getDocumentProvider();
		IEditorInput editorInput = CUEditor.getEditorInput();
		IFile file = (IFile) editorInput.getAdapter(IFile.class);
		IPath fullPath = file.getFullPath();
		ICompilationUnit workingCopy = getWorkingCopy(fullPath);
		IDocument document = docProvider.getDocument(editorInput);
		
		// get selection
		ISelection sel = editor.getEditorSite().getSelectionProvider()
				.getSelection();
		ITextSelection tSel = (ITextSelection) sel;
		int offset = tSel.getOffset();
		int length = tSel.getLength();
		int pos = offset + length;

		TextEdit edit = null;
		IPath workspaceDir = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		String workspaceDirStr = workspaceDir.makeAbsolute().toOSString();
		String fileRelativePath = fullPath.toOSString();
		String resDir = workspaceDirStr + fileRelativePath + ".files\\";
		try {
			new ResManager(resDir).add(desc);
		} catch (IOException e1) {
			MessageBox messageBox = new MessageBox(null,SWT.OK);
			messageBox.setMessage(e1.getMessage());
			messageBox.setText("Error");
			messageBox.open(); 
		}
		try {
			IRegion lineReg = document.getLineInformationOfOffset(pos);
			String line = document
					.get(lineReg.getOffset(), lineReg.getLength());
			int wcCnt = 0;
			while (wcCnt<line.length() && (line.charAt(wcCnt) == ' ' || line.charAt(wcCnt) == '\t'))
				wcCnt++;

			String prefix;
			if (line.startsWith("/*", wcCnt))
				prefix = "//";
			else if (line.startsWith("//", wcCnt))
				prefix = "//";
			else if (line.startsWith("*", wcCnt))
				prefix = "*";
			else
				prefix = "";
			int cmtStart = wcCnt + prefix.length();
			int wcCnt2 = cmtStart;
			while (wcCnt2<line.length() && (line.charAt(wcCnt2) == ' ' || line.charAt(wcCnt2) == '\t'))
				wcCnt2++;
			edit = new InsertEdit(lineReg.getOffset(), line.substring(0, wcCnt)
					+ prefix + line.substring(cmtStart, wcCnt2)
					+ desc.toString() + System.lineSeparator());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		try {
			workingCopy.applyTextEdit(edit, null);
			workingCopy.reconcile(ICompilationUnit.NO_AST, false, null, null);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}