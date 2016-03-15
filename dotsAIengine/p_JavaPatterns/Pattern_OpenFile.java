//Класс обеспечивает графические элементы для открытия файлов с сохраненными играми

package p_JavaPatterns;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class Pattern_OpenFile {

	JFileChooser fc;
	Pattern_ReadAndWriteFile file=new Pattern_ReadAndWriteFile();
	String strSave="";
	String fileType="";
	
public Pattern_OpenFile(JFrame frame,String[]fileFilter){
	Pattern_AddComponent add=new Pattern_AddComponent(frame.getContentPane());
	fc=(JFileChooser)add.component("fileChooser",0,0,0,0,"",actionJFileChooser);
	if(fileFilter!=null)fc.setFileFilter(new Pattern_ExtensionFileFilter(fileFilter));
	fc.setCurrentDirectory(new File(Pattern_Resources.savedGames));
	fc.showOpenDialog(frame);
}	

ActionListener actionJFileChooser=new ActionListener(){
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("ApproveSelection")){
			try {strSave=file.ReadTxtFile(fc.getSelectedFile().getPath());
			} catch (Exception e1) {e1.printStackTrace();}
		}
	}
};

public String getFileContent(){
	return strSave;
}

}
