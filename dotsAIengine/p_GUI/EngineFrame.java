package p_GUI;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JFrame;

import p_JavaPatterns.Pattern_JFrame;

public class EngineFrame implements Runnable{//создает окно для рисования игрового поля без никаких других жлементов интерфейса
	
	JFrame frame=new JFrame();
	GameField engine;
	Thread t=new Thread(this);
	GameFieldGUI gameFieldGUI;//рисование игрового поля
	
public EngineFrame(GameField engine){
	this.engine=engine;
	
	new Pattern_JFrame(frame,"",false,GameFieldGUI.cellSize*(engine.sizeX)+GameGUI.offsetX,
			GameFieldGUI.cellSize*(engine.sizeY)+GameGUI.offsetY-20,new Color(255,255,255));	
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	gameFieldGUI=new GameFieldGUI(frame.getGraphics(),GameGUI.offsetX,GameGUI.offsetY);//рисование игрового поля
	t.start();
}
	
public void paint(Graphics g){
	g.setColor(new Color(254,254,254));
	g.fillRect(0, 50, GameFieldGUI.cellSize*(engine.sizeX-2+1)+GameGUI.offsetX, engine.sizeY-2*(GameFieldGUI.cellSize+1));
	gameFieldGUI.drawGameField(engine,false,false);
}

public void run(){
	for(;;){
		try{t.sleep(1000);}
		catch(Exception e){}
		paint(frame.getGraphics());
	}
}

}