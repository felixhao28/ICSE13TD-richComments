package cn.edu.pku.richcomments;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.osgi.framework.BundleContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import com.sun.jna.NativeLibrary;

import cn.edu.pku.richcomments.dom.RCDesc;
import cn.edu.pku.richcomments.views.RCView;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "cn.edu.pku.richComments"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}


	public static void setVLCPath(String path) {
		if (path == null)
			path = "C:\\Program Files\\VideoLAN\\VLC";
		NativeLibrary.addSearchPath("libvlc", path);
	}
	
	/*
	 * (non-Javadoc)<<<f d>>>
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		setVLCPath(null);
		super.start(context);
		plugin = this;
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
		ISelectionService selectionService = activeWorkbenchWindow.getSelectionService();
		final IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
		selectionService.addSelectionListener(new ISelectionListener() {
			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				if (part instanceof CompilationUnitEditor) {
					CompilationUnitEditor CUEditor = (CompilationUnitEditor) part;
					IDocumentProvider docProvider = CUEditor.getDocumentProvider();
					IEditorInput editorInput = CUEditor.getEditorInput();
					IFile file = (IFile) editorInput.getAdapter(IFile.class);
					IPath fullPath = file.getFullPath();
					ICompilationUnit workingCopy = getWorkingCopy(fullPath);
					IDocument document = docProvider.getDocument(editorInput);
					
					TextSelection tsel = (TextSelection) selection;
					int pos = tsel.getOffset()+tsel.getLength();
					try {
						String contents = workingCopy.getBuffer().getContents();
						System.out.println(pos);
						int end = contents.indexOf(">>>", pos);
						if (end==-1)
							return;
						int next = contents.indexOf("<<<", pos);
						if (next==-1 || next>end){
							//inside an RC desc
							int start = contents.lastIndexOf("<<<", pos);
							RCDesc desc = RCDesc.buildFromString(contents.substring(start,end+3));
							activePage.showView("cn.edu.pku.richcomments.views.RCView");
							RCView view = (RCView) activePage.findView("cn.edu.pku.richcomments.views.RCView");
							IPath workspaceDir = ResourcesPlugin.getWorkspace().getRoot().getLocation();
							view.updateRes(desc,workspaceDir, fullPath.makeAbsolute());
							CUEditor.setSelection(null);
						}
					} catch (JavaModelException e) {
						e.printStackTrace();
					} catch (PartInitException e) {
						e.printStackTrace();
					}
					
				}
			}

			public ICompilationUnit getWorkingCopy(IPath path) {
				//get corresponding working copy
				ICompilationUnit compUnit = null;
				ICompilationUnit[] copies = JavaCore.getWorkingCopies(null);
				for (ICompilationUnit copy: copies){
					IPath path2 = copy.getPath().makeAbsolute();
					if (path.makeAbsolute().equals(path2)){
						compUnit = copy;
						break;
					}
				}
				return compUnit;
			}
		});
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
