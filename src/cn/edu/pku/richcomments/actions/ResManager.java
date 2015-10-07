package cn.edu.pku.richcomments.actions;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import cn.edu.pku.richcomments.dom.RCDesc;

public class ResManager {
	String resDir;

	public ResManager(String workingDir) {
		this.resDir = workingDir;
	}
	
	public void add(RCDesc desc) throws IOException{
		String url = desc.url;
		if (url.startsWith("file://")){
			String filepath = url.substring(7);
			String fileOriginalName = filepath.substring(filepath.lastIndexOf(File.separator)+1);
			String filename = fileOriginalName;
			String destPath = resDir + fileOriginalName;
			int fExtPos = fileOriginalName.indexOf('.');
			String fileExt = null;
			if (fExtPos>=0){
				fileExt = fileOriginalName.substring(fExtPos+1);
				fileOriginalName = fileOriginalName.substring(0,fExtPos);
			}
			File dest = new File(destPath);
			int existCnt=0;
			while (dest.exists()){
				if (fileExt==null)
					filename = fileOriginalName + existCnt;
				else
					filename = fileOriginalName + existCnt + "." + fileExt;
				destPath = resDir + File.separator + filename;
				dest = new File(destPath);
				existCnt++;
			}
			FileUtils.copyFile(new File(filepath), dest, true);
			desc.url = "file:///" + filename;
		}
	}
}
