package cn.edu.pku.richcomments.gif;

import java.awt.Panel;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class GifViewer extends Panel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6527877390555848438L;
	Icon icon;
	JLabel label = new JLabel("Hello");
	
	public GifViewer(){
		super();
	}
	
	public void showGif(String url){
		try {
			icon = new ImageIcon(new URL(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (label!=null)
			this.remove(label);
		label = new JLabel(icon);
		this.add(label);
	}
	
	public static void main(String[] args){
        JFrame f = new JFrame("Animation");
        GifViewer comp = new GifViewer();
		f.getContentPane().add(comp);
		//comp.showGif("http://upload.wikimedia.org/wikipedia/commons/4/45/Dijksta_Anim.gif");
		comp.showGif("file:///C:/Users/Administrator/Desktop/Dijksta_Anim.gif");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);

	}
}
