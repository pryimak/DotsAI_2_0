//установить фильтр на файлы при открытии файлов с сохраненными играми

package p_JavaPatterns;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class Pattern_ExtensionFileFilter extends FileFilter{

	private String[] strFile;

public Pattern_ExtensionFileFilter(String[] strFile){this.strFile=strFile;}
public String getDescription(){return "";}

public boolean accept(File f){
	if (f.isDirectory()){
		return true;
	}
	for(int i=0;i<strFile.length;i++){
		if (f.getName().toLowerCase().endsWith(strFile[i])){
			return true;
		}
	}
	return false;
}

}