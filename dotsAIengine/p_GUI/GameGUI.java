//����� ������������ ����������� ��������� ��� ����

package p_GUI;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;

import p_DotsAI.DotsAI;
import p_DotsAI.EnemyType;
import p_DotsAI.Protocol;
import p_JavaPatterns.Pattern_Resources;

public class GameGUI{

	AudioClip sound=null;//���� ����
	String spc="&nbsp;&nbsp;&nbsp;";//������ ������
	public static int offsetX=GameFieldGUI.cellSize*2+3;//������ ����
	public static int offsetY=GameFieldGUI.cellSize*3+10;//������ ����
	
public GameGUI(){
	//��������� ���� �� ������ ����
	File f=new File(Pattern_Resources.gui+"move.wav");
	try{sound=Applet.newAudioClip(f.toURL());}catch(Exception e){System.out.println("sound fails");}
}

//�������� ����� ����
public void newGame(DotsAI dotsAI){	
	dotsAI.label.setText(getLabelText(dotsAI));//���������� �� ���� � ������ ������ ����	
	dotsAI.AImovesCount=0;//����� ����� ��
	dotsAI.AItotalTime=0;//����� ����� ����� ��
	dotsAI.protocol.getGame(dotsAI.gameIdx).gameField.setFieldState(dotsAI.protocol.getGame(dotsAI.gameIdx));//���������� ��������� ����
	dotsAI.protocol.chainsSearch(dotsAI.protocol.getGame(dotsAI.gameIdx));//����� ����� �����
	
	if(dotsAI.isPause)dotsAI.isPause=false;//��������� �����, ���� ��� ����
	
	gameRepaint(dotsAI,true);//������������ ���� ����
}

public void gameRepaint(DotsAI dotsAI,boolean isRepaintTE){
	//�� ������ ���� ���� ��� ���� �� ������ ������� ��� ������ ��
	if(dotsAI.protocol.getGame(dotsAI.gameIdx).enemyType!=EnemyType.RANDOM&&dotsAI.protocol.getGame(dotsAI.gameIdx).enemyType!=EnemyType.AI){
		sound.play();	
	}
	
	dotsAI.label.setText(getLabelText(dotsAI));//���������� �� ���� � ������ ������ ����
	
	//� ���� ������ ������� ��� �� ������������ ��������� ��� ������������ � �������������� �������� �������
	boolean isShowLastDot=false;
	if(dotsAI.protocol.getGame(dotsAI.gameIdx).enemyType==EnemyType.RANDOM||dotsAI.protocol.getGame(dotsAI.gameIdx).enemyType==EnemyType.AI){
		if(dotsAI.protocol.getGame(dotsAI.gameIdx).gameField.lastDotType==GameField.RED){
			isShowLastDot=true;
		}
	}
	
	dotsAI.gameFieldGUI.drawGameField(dotsAI.protocol.getGame(dotsAI.gameIdx).gameField,false,isShowLastDot);//���������� ���� ����
	dotsAI.protocol.stat.paintFrame(dotsAI);//�������� ���� �� ����������� �����
	
	try{
		if(dotsAI.protocol.getGame(dotsAI.gameIdx).getMovesCount()>0&isRepaintTE&dotsAI.protocol.getGame(dotsAI.gameIdx).gameField.lastDotType==GameField.RED){
			//���������� � ��������� �������� ������, �� �������� �������� ��������� ��� ��
			dotsAI.dotsTemplateEditor.moveFromAI(
				dotsAI.protocol.templateEngine.getTemplateByTemplateID(dotsAI.protocol.templateEngine.foundedIndex).templateContent,
				dotsAI.protocol.templateEngine.foundedNumber,
				dotsAI.protocol.templateEngine.foundedTemplateType
			);
		}
	}catch(Exception e){}
	
	dotsAI.gameFieldGUI.paintChainsDots(dotsAI);//���������� ����� ����� (� ��������� ����� ��� ����� �����)
}

//���������� �� ���� � ������ ������ ����
public String getLabelText(DotsAI dotsAI){try{
	return "<HTML><FONT color=blue>"+spc+dotsAI.protocol.getGame(dotsAI.gameIdx).enemyType.toString()+"</FONT><FONT color=black> "+dotsAI.protocol.getGame(dotsAI.gameIdx).gameField.scoreBlue+" - "+dotsAI.protocol.getGame(dotsAI.gameIdx).gameField.scoreRed+
		"</FONT><FONT color=red> "+dotsAI.protocol.appName+" "+dotsAI.protocol.appVersion+"</FONT>"+spc+"����� <FONT color=green>"+(dotsAI.protocol.getGame(dotsAI.gameIdx).getMovesCount()+4)+"</FONT>"+spc+
		"AI time"+"<FONT color=green> "+
		new BigDecimal((double)(dotsAI.AItime)/1000.0).setScale(2,RoundingMode.HALF_UP).doubleValue()+
		"</FONT>"+spc+
		"avg AI time"+"<FONT color=green> "+dotsAI.AIavgTime+"</FONT></HTML>";
}catch(Exception e){
	return "<HTML><FONT color=blue>"+spc+spc+dotsAI.protocol.getGame(dotsAI.gameIdx).enemyType.toString()+"</FONT><FONT color=black> 0 - 0</FONT><FONT color=red> "+Protocol.appName+" "+Protocol.appVersion+"</FONT>"+spc+"��� <FONT color=green>8</FONT></HTML>";
}}

}
