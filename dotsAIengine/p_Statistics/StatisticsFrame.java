//Класс StatisticsFrame выводит в окно статистику использования алгоритмов ИИ.
//В основном отображается статистика использования различных типов шаблонов.
//Кроме типов шаблонов отображается число ходов по рандому, число ходов по BSS точкам и число ходов по максимизации захваченной территории

package p_Statistics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import p_DotsAI.DotsAI;
import p_JavaPatterns.Pattern_JFrame;
import p_JavaPatterns.Pattern_Resources;
import p_TemplateEngine.TemplateType;

public class StatisticsFrame{
	
	public JFrame frame;
	int max=0;
	int sumOfMoveStat;//всего ходов
	int sumOfMoveStatTemplates;//всего ходов по шаблонам (включая макросы, исключая EXPRESS_SURROUND и RANDOM)
	public MoveStatCount moveStatMas=new MoveStatCount();
	BufferedImage images[];
	int Y=30;
	int X=25;
	int barWidth=240;
	int barHeight=20;
		
public StatisticsFrame(){
	frame=new JFrame();
	new Pattern_JFrame(frame, "Статистика ИИ", false, barWidth+60, 53+moveStatMas.getMoveStatLength()*barHeight, Color.white);
	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	frame.move(frame.getX()-510,frame.getY()-25);
	frame.setIconImage(Pattern_Resources.icon);
	
	images=new BufferedImage[moveStatMas.getMoveStatLength()];
	for(int i=0;i<moveStatMas.getMoveStatLength();i++){
		File imageFile=new File(Pattern_Resources.templateTypes+TemplateType.toString(TemplateType.getTemplateType(MoveStatType.getMoveStatType(i)))+".png");
		try{
			images[i]=ImageIO.read(imageFile);
		}catch(Exception e){
			try{images[i]=ImageIO.read(new File(Pattern_Resources.templateTypes+"no_template.png"));}
			catch (IOException e1){images[i]=null;}
		}
	}
}

//очистить статистику ходов
public void clearStat(){
	max=0;
	sumOfMoveStat=0;
	sumOfMoveStatTemplates=0;
	for(int i=0;i<moveStatMas.moveStatistics.length;i++){
		moveStatMas.moveStatistics[i]=0;
	}
	moveStatMas.moveStatMax=0;
}

//нарисовать окно со статистикой
public void paintFrame(DotsAI pointsAI){

	Graphics g=frame.getGraphics();	
	g.clearRect(0, 0, frame.getWidth(), frame.getHeight());	
	
	sumOfMoveStat=0;
	for(int i=0;i<moveStatMas.getMoveStatLength();i++){
		g.setColor(Color.pink);
		g.fillRect(X, Y+barHeight*i-2, barWidth, 15);
		sumOfMoveStat+=moveStatMas.moveStatistics[i];
	}
	sumOfMoveStatTemplates=
			sumOfMoveStat
			-
			moveStatMas.moveStatistics[MoveStatType.getMoveStatIndex(MoveStatType.EXPRESS_MAX_TERR_SECURITY)]
			-
			moveStatMas.moveStatistics[MoveStatType.getMoveStatIndex(MoveStatType.RANDOM)];
	
	for(int i=0;i<moveStatMas.getMoveStatLength();i++){
		g.setColor(Color.green);
		g.fillRect(X, Y+barHeight*i-2, (int)((float)moveStatMas.moveStatistics[i]/sumOfMoveStat*barWidth), 15);
	}
	
	g.setColor(Color.black);	
	for(int i=0;i<moveStatMas.getMoveStatLength();i++){
		g.drawString(MoveStatType.toString(MoveStatType.getMoveStatType(i)), X+5, Y+10+barHeight*i);
	}
	
	g.setColor(Color.yellow);
	g.fillRect(10, Y+barHeight*moveStatMas.getMoveStatLength(), barWidth+55, 13);
	g.fillRect(10, Y+16+barHeight*moveStatMas.getMoveStatLength(), barWidth+55, 13);
	g.setColor(Color.black);
	g.drawString("Последний ход ИИ:      "+pointsAI.protocol.getGame(pointsAI.gameIdx).moveAI.x+";"+pointsAI.protocol.getGame(pointsAI.gameIdx).moveAI.y, 15, Y+11+barHeight*moveStatMas.getMoveStatLength());	
	g.drawString(moveStatMas.lastAImoveString, 15, Y+27+barHeight*moveStatMas.getMoveStatLength());
	
	max=moveStatMas.getMoveStatMax();
	
	g.setColor(Color.black);		
	for(int i=0;i<moveStatMas.getMoveStatLength();i++){		
		g.fillRect(barWidth+X+5, Y+barHeight*i-2, 35, 15);		
	}
		
	for(int i=0;i<moveStatMas.getMoveStatLength();i++){
		if(moveStatMas.moveStatistics[i]>0)g.setColor(Color.green);
		else g.setColor(Color.pink);		
		g.drawString(""+moveStatMas.moveStatistics[i], barWidth+35, Y+10+barHeight*i);
		if(images[i]!=null)g.drawImage(images[i],6, Y-2+barHeight*i,null);
	}
	
	//итоговые суммы ходов
	g.setColor(Color.lightGray);
	g.fillRect(10, Y+32+barHeight*moveStatMas.getMoveStatLength(), barWidth+55, 13);
	
	g.setColor(Color.black);
	g.drawString("Всего ходов ИИ: "+sumOfMoveStat, 15, Y+43+barHeight*moveStatMas.getMoveStatLength());
}

}
