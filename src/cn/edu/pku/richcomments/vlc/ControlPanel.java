package cn.edu.pku.richcomments.vlc;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;


public class ControlPanel extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6159130571647199207L;
	Button playbtn,pausebtn,stopbtn;
	Button fullscreenbtn,originalsize;
	
	EmbeddedMediaPlayer player;
	SizeController sizer;
	public ControlPanel(EmbeddedMediaPlayer player,SizeController sizer){
		super();
		this.player = player;
		this.sizer = sizer;
		this.setLayout(new FlowLayout(FlowLayout.CENTER));
		mkBtns();
	}
	
	/**
	 * 0 -- playing<br>
	 * 1 -- paused<br>
	 * 2 -- stopped
	 */
	int status=0;
	
	public void mkBtns() {
		playbtn = new Button("ctrl:play");
		playbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				player.play();
				if (status==1){
					pausebtn.setLabel("ctrl:pause");
					status=0;
				}
			}
		});
		this.add(playbtn);
		pausebtn = new Button("ctrl:pause");
		pausebtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				player.pause();
				if (status==1){
					pausebtn.setLabel("ctrl:pause");
					status=0;
				}else{
					pausebtn.setLabel("ctrl:resume");
					status=1;
				}
			}
		});
		this.add(pausebtn);
		stopbtn = new Button("ctrl:stop");
		stopbtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				player.stop();
			}
		});
		this.add(stopbtn);
//		fullscreenbtn = new Button("size:fullscreen");
//		fullscreenbtn.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				player.setFullScreen(false);
//				player.setFullScreen(true);
//			}
//		});
//		this.add(fullscreenbtn);

		originalsize = new Button("size:original");
		originalsize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Dimension videoDimension = player.getVideoDimension();
				sizer.setSize(videoDimension.height, videoDimension.width);
			}
		});
		this.add(originalsize);
	}
	
	public static void main(String args[]){
		  JFrame frame = new JFrame("vlcj Tutorial");
		  frame.setContentPane(new ControlPanel(null,null));
		    
		    frame.setLocation(100, 100);
		    frame.setSize(1050, 600);
		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    frame.setVisible(true);
	}
}
