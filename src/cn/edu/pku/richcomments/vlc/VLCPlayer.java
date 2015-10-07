package cn.edu.pku.richcomments.vlc;
import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.Window;

import javax.swing.JFrame;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.DefaultFullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;

import com.sun.jna.NativeLibrary;

public class VLCPlayer extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7936095940475080507L;
	private ControlPanel ctrlPanel;
	private EmbeddedMediaPlayer player;

	public static void setVLCPath(String path) {
		if (path == null)
			path = "C:\\Program Files\\VideoLAN\\VLC";
		NativeLibrary.addSearchPath("libvlc", path);
	}

	public VLCPlayer(final SizeController sizer) {
		super();
		this.setLayout(new BorderLayout());
		EmbeddedMediaPlayerComponent mediaPlayerComponent = new EmbeddedMediaPlayerComponent(){
			/**
			 * 
			 */
			private static final long serialVersionUID = -3294518588032763879L;

			protected FullScreenStrategy onGetFullScreenStrategy() {
		         return new DefaultFullScreenStrategy(sizer.getFrame());
		     }
		};
		add(mediaPlayerComponent, BorderLayout.CENTER);
		player = mediaPlayerComponent.getMediaPlayer();
		ctrlPanel = new ControlPanel(player, new SizeController() {
			@Override
			public void setSize(int height, int width) {
				sizer.setSize(height+ctrlPanel.getHeight(), width);
			}
			@Override
			public Window getFrame() {
				return null;
			}
		});
		add(ctrlPanel, BorderLayout.SOUTH);
	}

	public void playMedia(String mrl) {
		player.playMedia(mrl);
	}

	public static void main(String args[]) {
		setVLCPath(null);
		final JFrame frame = new JFrame("vlcj Tutorial");
		frame.setSize(1050, 600);
		VLCPlayer VLCPlayer = new VLCPlayer(new SizeController() {
			@Override
			public void setSize(int height, int width) {
				frame.setSize(width, height);
			}
			@Override
			public Window getFrame() {
				return frame;
			}
		});
		frame.setContentPane(VLCPlayer);

		frame.setLocation(100, 100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		VLCPlayer.playMedia("A:\\runtime-EclipseApplication\\testProject\\src\\SSSS.java.files\\test.avi");
	}

	public void addMediaPlayerEventListener(
			MediaPlayerEventAdapter mediaPlayerEventAdapter) {
		player.addMediaPlayerEventListener(mediaPlayerEventAdapter);
	}
}
