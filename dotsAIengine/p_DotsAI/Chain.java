//Класс Chain хранит информацию о цепях точек красного и синего игроков.
//Анализ цепей происходит в классе Protocol.

package p_DotsAI;

import java.awt.Point;
import java.util.ArrayList;

import p_DotsAI.Protocol.Game;

public class Chain{

	public ArrayList<Point> points;//обычные ходы
	public ArrayList<Point> abstractPoints;//ходы ИИ, сделанные по абстрактным шаблонам
	
	int chainStartX;//точка начала цепи
	int chainStartY;//точка начала цепи
	public int chainEndX;//точка конца цепи
	public int chainEndY;//точка конца цепи
	int wallIndex;//индекс цепи
	double chainLength;//длина цепи
	String sign;//знак цепи - красная или синяя
	
	//ранее происходил более глубокий анализ ситуации на поле и искались удаленные (абстрактные точки)
	//и разрывы в цепях (связи). Из-за сложностей выполненной оптимизации программы 
	//поиск таких точек не происходит, однако он был бы полезен
	//public ArrayList<Point> connections;//связи
	//public ArrayList<Point> abstracts;//абстрактные точки
	//public ArrayList<Point> links;//абстрактные связи
	
	public Chain(Game game,int x,int y,int wallIndex,String sign,String[][] fieldOfChains){		
		points=new ArrayList<Point>();
		abstractPoints=new ArrayList<Point>();
		//connections=new ArrayList<Point>();
		//abstracts=new ArrayList<Point>();
		//links=new ArrayList<Point>();
		points.add(new Point(x,y));
		
		//точка начала цепи
		chainStartX=x;
		chainStartY=y;
		
		this.wallIndex=wallIndex;//индекс цепи
		this.sign=sign;//знак цепи - красная или синяя
		fieldOfChains[x][y]="P"+sign+wallIndex;//код точки цепи
	}
	
	//поиск конца цепи
	public void searchChainEnd(Game game,String[][] fieldOfChains){//искать концы цепей
		chainEndX=chainStartX;
		chainEndY=chainStartY;
		chainLength=0;
		for(int x=0;x<game.sizeX;x++)for(int y=0;y<game.sizeY;y++){
			if(fieldOfChains[x][y].equals("P"+sign+wallIndex)){
				if(chainLength<=Math.sqrt(Math.abs(x-chainStartX)*Math.abs(x-chainStartX)+Math.abs(y-chainStartY)*Math.abs(y-chainStartY))
						&&
					Math.max(Math.abs(x-chainStartX),Math.abs(y-chainStartY))>Math.max(Math.abs(chainEndX-chainStartX),Math.abs(chainEndY-chainStartY))
					){
						chainLength=Math.sqrt(Math.abs(x-chainStartX)*Math.abs(x-chainStartX)+Math.abs(y-chainStartY)*Math.abs(y-chainStartY));
						chainEndX=x;
						chainEndY=y;
				}
			}
		}
		chainLength=Math.abs(chainEndX-chainStartX)+Math.abs(chainEndY-chainStartY);		
	}
	
	//запомнить точку цепи
	public void addPoint(Game game,int x,int y,String[][] fieldOfWalls){
		if(!fieldOfWalls[x][y].equals("N")){return;}
		fieldOfWalls[x][y]="P"+sign+wallIndex;
		points.add(new Point(x,y));
	}
	
	/*public void addAbstract(Game game,int x,int y,String[][] fieldOfWalls){
		if(!fieldOfWalls[x][y].equals("N")){return;}
		fieldOfWalls[x][y]="A"+sign+wallIndex;
		abstracts.add(new Point(x,y));
	}
	
	public void addLink(Game game,int x,int y,String[][] fieldOfWalls){
		if(!fieldOfWalls[x][y].equals("N")){return;}
		fieldOfWalls[x][y]="L"+sign+wallIndex;
		links.add(new Point(x,y));
	}
	
	public void addConnection(Game game,int x,int y,String[][] fieldOfWalls){//пустые клетки - пустые пространства в стенах
		if(!fieldOfWalls[x][y].equals("N")){return;}
		fieldOfWalls[x][y]="C"+sign+wallIndex;
		connections.add(new Point(x,y));
	}*/
	
	//определение расстояния от конца ветки до последней поставленной синей точки
	public double getLengthFromLastBlue(Point p){
		chainLength=Math.sqrt(Math.abs(chainEndX-p.x)*Math.abs(chainEndX-p.x)+Math.abs(chainEndY-p.y)*Math.abs(chainEndY-p.y));
		return chainLength;
	}
	
	//проверка, находится ли конец цепи на краю поля
	public boolean isAtSide(Game game){
		if(chainEndX<2|chainEndY<2|chainEndX>game.sizeX-3|chainEndY>game.sizeY-3){return true;}else{return false;}
	}		
}
