package p_GUI;

import java.awt.*;

import javax.swing.*;

import p_DotsAI.Protocol;
import p_JavaPatterns.Pattern_AddComponent;
import p_JavaPatterns.Pattern_JFrame;
import p_JavaPatterns.Pattern_Resources;

public class AboutGameFrame{//создается окно, которое отображает информацию о программе

	public JFrame frame=new JFrame();
	Pattern_AddComponent add=new Pattern_AddComponent(frame.getContentPane());
	
	JLabel appImage=new JLabel(new ImageIcon(Pattern_Resources.gui+"dotsAI.png"));
	
public AboutGameFrame(){	

	new Pattern_JFrame(frame, "О программе "+Protocol.appName+" версия "+Protocol.appVersion, false, 550, 199, null);
	frame.setIconImage(Pattern_Resources.icon);
	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	frame.setAlwaysOnTop(true);
	frame.getContentPane().add(appImage);//фоновый рисунок
	appImage.setBounds(0,-5, 250, 205);//размеры фонового рисунка	
	
	JLabel info=(JLabel)add.component("label",255, -1, 300, 195,"<html><font size=3>Программа для игры в точки против ИИ<br><br><font size=5>"+
			Protocol.appName+"<font size=3><br><i>Dots Artificial Intelligence</i><br><br>" +
			" версия "+Protocol.appVersion+" от "+Protocol.appDate+
			"<br><br><font color=blue><u>http://playdots.ru/dotsai</u></font>"+
			"<br><br>"+Protocol.appLicense+"<br>"+Protocol.appAuthor,null);
	info.setBackground(Color.white);
}

}


