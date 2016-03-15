//Класс GameFieldGUI обеспечивает рисование игрового поля, точек, окружений и дополнительных элементов для редакторов шаблонов и деревьев

package p_GUI;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.ArrayList;
import p_DotsAI.DotsAI;
import p_DotsAI.Protocol;
import p_TemplateEngine.Template;

public class GameFieldGUI{
	
	public static int cellSize=17;//ширина клетки поля
	public static int dotRadius=5;//радиус точки

	public Color blue=new Color(0,102,204);//цвет точек синего игрока
	Color gray=new Color(223,223,223);//цвет сетки поля
	
	int offsetX,offsetY;//отступы
	Graphics g;
	
public GameFieldGUI(Graphics g,int offsetX,int offsetY){
	this.g=g;
	this.offsetX=offsetX;
	this.offsetY=offsetY;
}

//рисование области поля и координат без рисования точек на поле
public void drawGameFieldArea(GameField gameField,boolean isNumberingFromCenter){
	drawGameFieldArea(gameField.sizeX,gameField.sizeY,isNumberingFromCenter);
}

//рисование области поля и координат без рисования точек на поле
public void drawGameFieldArea(int sizeX,int sizeY,boolean isNumberingFromCenter){
	//закрашивание области поля
	g.setColor(Color.white);
	g.fillRect(offsetX-cellSize-12, offsetY-cellSize/2, (sizeX+2)*cellSize-7, (sizeY+1)*cellSize-3);

	//рисование сетки поля
	g.setColor(gray);
	for(int i=0;i<sizeX;i++){
		g.fillRect(i*cellSize+offsetX, offsetY, 1, (sizeY-1)*cellSize);
	}
	for(int i=0;i<sizeY;i++){
		g.fillRect(offsetX, i*cellSize+offsetY, (sizeX-1)*cellSize, 1);
	}
	
	((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	
	//рисование координат
	g.setColor(Color.black);
	g.setFont(new Font("verdana",9,9));
	for(int i=0;i<sizeX;i++){
		if(isNumberingFromCenter){g.drawString((i-sizeX/2)+"",i*cellSize+offsetX-3, sizeY*cellSize+offsetY);}
		else{
			if(i+1<10)g.drawString(i+"",i*cellSize+offsetX-3, sizeY*cellSize+offsetY);
			else g.drawString(i+"",i*cellSize+offsetX-5, sizeY*cellSize+offsetY);
		}
	}
	for(int i=0;i<sizeY;i++){
		if(isNumberingFromCenter){g.drawString((i-sizeY/2)+"",offsetX-22,i*cellSize+offsetY+3);}
		else{
			if(i<10)g.drawString(i+"",offsetX-22,i*cellSize+offsetY+3);
			else g.drawString(i+"",offsetX-27,i*cellSize+offsetY+3);
		}
	}
	
	((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
}

//рисование поля
public void drawGameField(GameField gameField,boolean isNumberingFromCenter,boolean isShowLastDot){//рисование поля	
	drawGameFieldArea(gameField,isNumberingFromCenter);
	drawGameField(gameField,isShowLastDot);	
}

//нарисовать точки шаблона в редакторе деревьев
public void drawDotsForTreeEditor(GameField gameField,boolean isNumberingFromCenter,Template template){//рисование поля	
	drawGameFieldArea(gameField,isNumberingFromCenter);
	
	for(int i=0;i<template.templateContent.length;i++){
		byte x=(byte)(i%Protocol.maxSize);
		byte y=(byte)(i/Protocol.maxSize);
		
		if(template.templateContent[i]==Protocol.templateDotType_RED){
			if(gameField.canAddMove(x, y)){
				drawDot(x, y, Color.red);
			}
		}else if(template.templateContent[i]==Protocol.templateDotType_BLUE){
			if(gameField.canAddMove(x, y)){
				drawDot(x, y, blue);
			}
		}else if(template.templateContent[i]==Protocol.templateDotType_ANY){
			drawDot(x, y, Color.green);
		}else if(template.templateContent[i]==Protocol.templateDotType_RED_or_EMPTY){
			drawDot(x, y, Color.pink);
		}else if(template.templateContent[i]==Protocol.templateDotType_BLUE_or_EMPTY){
			drawDot(x, y, Color.cyan);
		}else if(template.templateContent[i]==Protocol.templateDotType_LAND){
			drawLandDot(x, y);
		}
	}
	
	drawGameField(gameField,false);
	
	drawTemplateFrame(template);//нарисовать квадратную область для шаблона
}

//рисование поля
public void drawGameField(GameField gameField,boolean isShowLastDot){
	
	((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	
	//рисование точек поля
	for(int i=0;i<gameField.sizeX;i++){
		for(int j=0;j<gameField.sizeY;j++){
			if(gameField.gameField[i][j]>0){
				if(gameField.gameField[i][j]==1|gameField.gameField[i][j]==3)g.setColor(blue);
				else if(gameField.gameField[i][j]==2|gameField.gameField[i][j]==4)g.setColor(Color.red);
				drawDot(i,j);
			}
		}
	}
	
	//рисование окружений
	drawPolygons(gameField.redEnclosures,Color.red,new Color(255,0,0,70));
	drawPolygons(gameField.blueEnclosures,blue,new Color(0,102,204,70));
	
	//рисование последнего хода
	g.setColor(Color.white);
	g.fillRect(gameField.lastDot.x*cellSize+offsetX-2, gameField.lastDot.y*cellSize+offsetY-2, 4, 4);
	
	//выделение линиями последнего хода
	if(isShowLastDot){
		g.setColor(Color.green);
		g.drawLine(gameField.lastDot.x*cellSize+offsetX-2, offsetY, gameField.lastDot.x*cellSize+offsetX-2, offsetY+gameField.sizeY*cellSize);
		g.drawLine(offsetX, gameField.lastDot.y*cellSize+offsetY-2, offsetX+gameField.sizeX*cellSize, gameField.lastDot.y*cellSize+offsetY-2);
	}
	
	((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
}

//нарисовать области окружения
public void drawPolygons(ArrayList<Polygon> surr,Color c,Color c1){
	for(int i=0;i<surr.size();i++){
		int[] x=new int[surr.get(i).xpoints.length];
		int[] y=new int[surr.get(i).xpoints.length];
			
		for(int j=0;j<surr.get(i).xpoints.length;j++){
			x[j]=surr.get(i).xpoints[j]*cellSize+offsetX;
			y[j]=surr.get(i).ypoints[j]*cellSize+offsetY;
		}
		
		g.setColor(c);
		g.drawPolygon(x,y,surr.get(i).xpoints.length);
		
		g.setColor(c1);
		g.fillPolygon(x,y,surr.get(i).xpoints.length);
	}
}

//нарисовать точки концов цепей
public void paintChainsDots(DotsAI dotsAI){try{
	for(int i=0;i<dotsAI.protocol.getGame(dotsAI.gameIdx).redChains.size();i++){		
		drawDot(dotsAI.protocol.getGame(dotsAI.gameIdx).redChains.get(i).chainEndX, dotsAI.protocol.getGame(dotsAI.gameIdx).redChains.get(i).chainEndY*cellSize,Color.black,dotRadius-2);
	}
	
	if(dotsAI.protocol.getGame(dotsAI.gameIdx).closestRedChainIdx!=-1){		
		drawDot(dotsAI.protocol.getGame(dotsAI.gameIdx).redChains.get(dotsAI.protocol.getGame(dotsAI.gameIdx).closestRedChainIdx).chainEndX, dotsAI.protocol.getGame(dotsAI.gameIdx).redChains.get(dotsAI.protocol.getGame(dotsAI.gameIdx).closestRedChainIdx).chainEndY,Color.yellow,dotRadius-2);
	}
	
	for(int i=0;i<dotsAI.protocol.getGame(dotsAI.gameIdx).redChains.size();i++){
		if(dotsAI.protocol.getGame(dotsAI.gameIdx).redChains.get(i).isAtSide(dotsAI.protocol.getGame(dotsAI.gameIdx))){			
			drawDot(dotsAI.protocol.getGame(dotsAI.gameIdx).redChains.get(i).chainEndX, dotsAI.protocol.getGame(dotsAI.gameIdx).redChains.get(i).chainEndY,Color.green,dotRadius-2);
		}
	}
}catch(Exception e){}}

//нарисовать закрашенный квадрат
public void fillSquare(Graphics graphics,int x,int y,Color color,int level){
	graphics.setColor(color);
	graphics.fillRect(x*cellSize+offsetX-dotRadius, y*cellSize+offsetY-dotRadius, dotRadius*2, dotRadius*2);
	graphics.setColor(Color.white);
	graphics.setFont(new Font("verdana",9,9));
	graphics.drawString(level+"", x*cellSize+offsetX-dotRadius+2 + (((level+"").length()==1)?1:-3), y*cellSize+offsetY+dotRadius-1);
}

//нарисовать рамку квадрата
public void drawSquare(Graphics graphics,int x,int y,Color color,int level){
	graphics.setColor(Color.white);
	graphics.fillRect(x*cellSize+offsetX-dotRadius, y*cellSize+offsetY-dotRadius, dotRadius*2, dotRadius*2);
	graphics.setColor(color);
	graphics.drawRect(x*cellSize+offsetX-dotRadius, y*cellSize+offsetY-dotRadius, dotRadius*2, dotRadius*2);
	graphics.setFont(new Font("verdana",9,9));
	graphics.drawString(level+"", x*cellSize+offsetX-dotRadius+2 + (((level+"").length()==1)?1:-3), y*cellSize+offsetY+dotRadius-1);
}

//нарисовать точку
public void drawDot(int x,int y){	
	g.fillOval(x*cellSize+offsetX-dotRadius, y*cellSize+offsetY-dotRadius, dotRadius*2, dotRadius*2);	
}

//нарисовать точку
public void drawDot(int x,int y,Color color){
	g.setColor(color);
	((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	g.fillOval(x*cellSize+offsetX-dotRadius, y*cellSize+offsetY-dotRadius, dotRadius*2, dotRadius*2);
	((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
}

//стереть точку
public void clearDot(int x,int y){
	g.setColor(Color.white);
	((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	g.fillOval(x*cellSize+offsetX-dotRadius-1, y*cellSize+offsetY-dotRadius-1, dotRadius*2+2, dotRadius*2+2);
	((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
}

//нарисовать точку
public void drawDot(int x,int y,Color color,int size){
	g.setColor(color);
	((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	g.fillOval(x*cellSize+offsetX-size, y*cellSize+offsetY-size, size*2, size*2);
	((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
}

//нарисовать точку границы поля
public void drawLandDot(int x,int y){
	g.setColor(Color.black);
	g.fillRect(x*cellSize+offsetX-cellSize/2, y*cellSize+offsetY-cellSize/2, cellSize, cellSize);
}

//нарисовать точки условных ходов
public void drawAttackDot(boolean isRed,int x,int y){
	if(isRed)g.setColor(Color.red);
	else g.setColor(blue);
	
	((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	g.drawOval(x*cellSize+offsetX-dotRadius-2, y*cellSize+offsetY-dotRadius-2, dotRadius*2+4, dotRadius*2+4);
	g.drawOval(x*cellSize+offsetX-dotRadius-1, y*cellSize+offsetY-dotRadius-1, dotRadius*2+2, dotRadius*2+2);
	g.setColor(Color.black);
	g.drawString("A",x*cellSize+offsetX-4, y*cellSize+offsetY+4);
	((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
}

//нарисовать точки шаблона в редакторе шаблонов
public void drawDotsForTemplateEditor(byte field[][],Template template){
	((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
	for(int x=0;x<Protocol.maxSize;x++){
		for(int y=0;y<Protocol.maxSize;y++){
			if(field[x][y]==Protocol.templateDotType_LAND){/*land*/
				drawLandDot(x,y);
			}else{
				if(field[x][y]==Protocol.templateDotType_ANY)g.setColor(Color.green);/*any*/							
				else if(field[x][y]==Protocol.templateDotType_EMPTY)g.setColor(Color.white);/*null*/
				else if(field[x][y]==Protocol.templateDotType_BLUE)g.setColor(blue);/*blue*/				
				else if(field[x][y]==Protocol.templateDotType_RED)g.setColor(Color.red);/*red*/						
				else if(field[x][y]==Protocol.templateDotType_RED_or_EMPTY)g.setColor(Color.pink);/*red null*/								
				else if(field[x][y]==Protocol.templateDotType_BLUE_or_EMPTY)g.setColor(Color.cyan);/*blue null*/				
				
				drawDot(x,y);
			}
		}
	}
	((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
	
	drawTemplateFrame(template);//нарисовать квадратную область для шаблона
}

//стереть ползунок курсора
public void clearHint(int x,int y){
	g.setColor(Color.white);
	g.drawOval(x*cellSize+offsetX-3, y*cellSize+offsetY-3, 6, 6);
}

//нарисовать ползунок курсора
public void drawHint(int x,int y){
	g.setColor(Color.black);
	g.drawOval(x*cellSize+offsetX-3, y*cellSize+offsetY-3, 6, 6);
}

//нарисовать рамки в редакторе шаблонов - рамку для центральной точки и рамку для значимой части шаблона
private void drawTemplateFrame(Template template){
	//подкрасить центральную точку
	g.setColor(new Color(0,0,0,100));
	g.drawRect(Protocol.maxSize/2*cellSize+offsetX-dotRadius-2, Protocol.maxSize/2*cellSize+offsetY-dotRadius-2, dotRadius*2+3, dotRadius*2+3);
	
	//нарисовать квадратную область для шаблона
	g.setColor(new Color(0,0,0,100));
	g.drawRect((Protocol.maxSize-template.sizeWithNotAny)/2*cellSize+offsetX-dotRadius-3, (Protocol.maxSize-template.sizeWithNotAny)/2*cellSize+offsetY-dotRadius-3, template.sizeWithNotAny*cellSize, template.sizeWithNotAny*cellSize);
}

}
