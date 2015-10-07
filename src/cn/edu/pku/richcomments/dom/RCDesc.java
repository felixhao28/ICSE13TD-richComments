package cn.edu.pku.richcomments.dom;

/**
 * A rich comment descriptor is consisted of three fields,
 * as 
 * title - A short string describing this multimedia resource<br>
 * type - "image" "audio" "video" "web page"<br>
 * url - either local file path or web url<br>
 * @author Felix
 *
 */
public class RCDesc {
	public String title;
	public String type;
	public String url;
	
	
	public RCDesc(String title, String type, String url) {
		super();
		this.title = title;
		this.type = type;
		this.url = url;
	}


	public static RCDesc buildFromString(String desc){
		String[] split = desc.substring(3, desc.length()-3).split(";");
		return new RCDesc(split[0], split[1], split[2]);
	}
	
	public String toString(){
		return "<<<" + title + ";" + type + ";" + url + ">>>";
	}
}
