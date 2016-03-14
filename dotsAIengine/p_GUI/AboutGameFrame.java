package p_GUI;

import java.awt.*;

import javax.swing.*;

import p_DotsAI.Protocol;
import p_JavaPatterns.Pattern_AddComponent;
import p_JavaPatterns.Pattern_JFrame;
import p_JavaPatterns.Pattern_Resources;

public class AboutGameFrame{//��������� ����, ������� ���������� ���������� � ���������

	public JFrame frame=new JFrame();
	Pattern_AddComponent add=new Pattern_AddComponent(frame.getContentPane());
	
	JLabel appImage=new JLabel(new ImageIcon(Pattern_Resources.gui+"dotsAI.png"));
	
public AboutGameFrame(){	

	new Pattern_JFrame(frame, "� ��������� "+Protocol.appName+" ������ "+Protocol.appVersion, false, 550, 199, null);
	frame.setIconImage(Pattern_Resources.icon);
	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	frame.setAlwaysOnTop(true);
	frame.getContentPane().add(appImage);//������� �������
	appImage.setBounds(0,-5, 250, 205);//������� �������� �������	
	
	JLabel info=(JLabel)add.component("label",255, -1, 300, 195,"<html><font size=3>��������� ��� ���� � ����� ������ ��<br><br><font size=5>"+
			Protocol.appName+"<font size=3><br><i>Dots Artificial Intelligence</i><br><br>" +
			" ������ "+Protocol.appVersion+" �� "+Protocol.appDate+
			"<br><br><font color=blue><u>http://playdots.ru/dotsai</u></font>"+
			"<br><br><br>"+Protocol.appAuthor,null);
	info.setBackground(Color.white);
}

}


