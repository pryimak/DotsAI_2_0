//Класс обеспечивает графический интерфейс для игры

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

	AudioClip sound=null;//звук хода
	String spc="&nbsp;&nbsp;&nbsp;";//отступ текста
	public static int offsetX=GameFieldGUI.cellSize*2+3;//отступ поля
	public static int offsetY=GameFieldGUI.cellSize*3+10;//отступ поля
	
public GameGUI(){
	//загрузить файл со звуком хода
	File f=new File(Pattern_Resources.gui+"move.wav");
	try{sound=Applet.newAudioClip(f.toURL());}catch(Exception e){System.out.println("sound fails");}
}

//создание новой игры
public void newGame(DotsAI dotsAI){	
	dotsAI.label.setText(getLabelText(dotsAI));//информация об игре в панели вверху окна	
	dotsAI.AImovesCount=0;//число ходов ИИ
	dotsAI.AItotalTime=0;//общее время ходов ИИ
	dotsAI.protocol.getGame(dotsAI.gameIdx).gameField.setFieldState(dotsAI.protocol.getGame(dotsAI.gameIdx));//установить состояние поля
	dotsAI.protocol.chainsSearch(dotsAI.protocol.getGame(dotsAI.gameIdx));//найти ветви ходов
	
	if(dotsAI.isPause)dotsAI.isPause=false;//закончить паузу, если она есть
	
	gameRepaint(dotsAI,true);//перерисовать поле игры
}

public void gameRepaint(DotsAI dotsAI,boolean isRepaintTE){
	//не играть звук хода при игре ИИ против рандома или против ИИ
	if(dotsAI.protocol.getGame(dotsAI.gameIdx).enemyType!=EnemyType.RANDOM&&dotsAI.protocol.getGame(dotsAI.gameIdx).enemyType!=EnemyType.AI){
		sound.play();	
	}
	
	dotsAI.label.setText(getLabelText(dotsAI));//информация об игре в панели вверху окна
	
	//в игре против рандома или ИИ подсвечивать последний ход вертикальной и горизонтальной зелеными линиями
	boolean isShowLastDot=false;
	if(dotsAI.protocol.getGame(dotsAI.gameIdx).enemyType==EnemyType.RANDOM||dotsAI.protocol.getGame(dotsAI.gameIdx).enemyType==EnemyType.AI){
		if(dotsAI.protocol.getGame(dotsAI.gameIdx).gameField.lastDotType==GameField.RED){
			isShowLastDot=true;
		}
	}
	
	dotsAI.gameFieldGUI.drawGameField(dotsAI.protocol.getGame(dotsAI.gameIdx).gameField,false,isShowLastDot);//нарисовать поле игры
	dotsAI.protocol.stat.paintFrame(dotsAI);//обновить окно со статистикой ходов
	
	try{
		if(dotsAI.protocol.getGame(dotsAI.gameIdx).getMovesCount()>0&isRepaintTE&dotsAI.protocol.getGame(dotsAI.gameIdx).gameField.lastDotType==GameField.RED){
			//нарисовать в редакторе шаблонов шаблон, по которому выполнен последний ход ИИ
			dotsAI.dotsTemplateEditor.moveFromAI(
				dotsAI.protocol.templateEngine.getTemplateByTemplateID(dotsAI.protocol.templateEngine.foundedIndex).templateContent,
				dotsAI.protocol.templateEngine.foundedNumber,
				dotsAI.protocol.templateEngine.foundedTemplateType
			);
		}
	}catch(Exception e){}
	
	dotsAI.gameFieldGUI.paintChainsDots(dotsAI);//нарисовать точки цепей (в настоящее время это концы цепей)
}

//информация об игре в панели вверху окна
public String getLabelText(DotsAI dotsAI){try{
	return "<HTML><FONT color=blue>"+spc+dotsAI.protocol.getGame(dotsAI.gameIdx).enemyType.toString()+"</FONT><FONT color=black> "+dotsAI.protocol.getGame(dotsAI.gameIdx).gameField.scoreBlue+" - "+dotsAI.protocol.getGame(dotsAI.gameIdx).gameField.scoreRed+
		"</FONT><FONT color=red> "+dotsAI.protocol.appName+" "+dotsAI.protocol.appVersion+"</FONT>"+spc+"ходов <FONT color=green>"+(dotsAI.protocol.getGame(dotsAI.gameIdx).getMovesCount()+4)+"</FONT>"+spc+
		"AI time"+"<FONT color=green> "+
		new BigDecimal((double)(dotsAI.AItime)/1000.0).setScale(2,RoundingMode.HALF_UP).doubleValue()+
		"</FONT>"+spc+
		"avg AI time"+"<FONT color=green> "+dotsAI.AIavgTime+"</FONT></HTML>";
}catch(Exception e){
	return "<HTML><FONT color=blue>"+spc+spc+dotsAI.protocol.getGame(dotsAI.gameIdx).enemyType.toString()+"</FONT><FONT color=black> 0 - 0</FONT><FONT color=red> "+Protocol.appName+" "+Protocol.appVersion+"</FONT>"+spc+"ход <FONT color=green>8</FONT></HTML>";
}}

}
