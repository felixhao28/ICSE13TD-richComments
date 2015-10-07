package cn.edu.pku.richcomments.views;

import java.awt.Frame;
import java.awt.Window;

import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import cn.edu.pku.richcomments.dom.RCDesc;
import cn.edu.pku.richcomments.gif.GifViewer;
import cn.edu.pku.richcomments.vlc.SizeController;
import cn.edu.pku.richcomments.vlc.VLCPlayer;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class RCView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "cn.edu.pku.richcomments.views.RCView";

	/**
	 * The constructor.
	 */
	public RCView() {
	}
	
	public void dispose(){
		super.dispose();
	}

	/**
	 * UI components.
	 */
	private Composite superParent;
	private Composite videoPanel;
	private Frame videoFrame;
	private VLCPlayer mediaPlayer;
	private Composite gifPanel;
	private GifViewer gifviewer;
	private Frame gifFrame;

	/**
	 * Create the native media player components.
	 */
	private void createMediaPlayer() {
		mediaPlayer = new VLCPlayer(new SizeController() {
			@Override
			public void setSize(int height, int width) {}
			@Override
			public Window getFrame() {
				return videoFrame;
			}
		});
		videoFrame.add(mediaPlayer);

		mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void finished(MediaPlayer mediaPlayer) {
				currentRes=null;
			}
		});
	}

	
	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent){
		superParent = parent;
		stackLayout = new StackLayout();
		parent.setLayout(stackLayout);
		
		mkVideoComposite(parent);
		mkGifComposite(parent);
		
		stackLayout.topControl = gifPanel;
		superParent.layout();
	}

	public void mkGifComposite(Composite parent) {
		gifPanel = new Composite(superParent,SWT.EMBEDDED);
		gifPanel.setLayout(new FillLayout());
		gifFrame = SWT_AWT.new_Frame(gifPanel);
		gifviewer = new GifViewer();
		gifFrame.add(gifviewer);
	}

	public void mkVideoComposite(Composite parent) {
		videoPanel = new Composite(superParent, SWT.EMBEDDED);
		videoPanel.setLayout(new FillLayout());
		videoFrame = SWT_AWT.new_Frame(videoPanel);
		createMediaPlayer();
	}

	String currentRes=null;
	private StackLayout stackLayout;
	public void updateRes(RCDesc desc, IPath workspaceDir, IPath iPath) {
		this.setPartName(desc.type + ":" + desc.title);
		if (desc.type.equals("animated gif")) {
			stackLayout.topControl = gifPanel;
			if (desc.url.startsWith("file://")) {
				String filename = getLocalPath(desc, workspaceDir, iPath);
				gifviewer.showGif("file:///" + filename);
			} else {
				gifviewer.showGif(desc.url);
			}
		} else if (desc.type.equals("image") || desc.type.equals("video") || desc.type.equals("audio")) {
			stackLayout.topControl = videoPanel;
			if (desc.url.startsWith("file://")) {
				String filename = getLocalPath(desc, workspaceDir, iPath);
				if (currentRes==null || currentRes!=filename){
					currentRes = filename;
					mediaPlayer.playMedia(filename);
				}
			} else {
				if (currentRes==null || currentRes!=desc.url){
					currentRes = desc.url;
					mediaPlayer.playMedia(desc.url);
				}
			}
		}
		superParent.layout();
		gifviewer.invalidate();
		superParent.redraw();
	}

	public String getLocalPath(RCDesc desc, IPath workspaceDir, IPath iPath) {
		String workspaceDirStr = workspaceDir.makeAbsolute().toOSString();
		String fileRelativePath = iPath.toOSString();
		String resLoc = desc.url.substring(7);
		String filename = workspaceDirStr + fileRelativePath + ".files\\"
				+ resLoc;
		return filename;
	}

	@Override
	public void setFocus() {
		// show.setFocus();
	}

}