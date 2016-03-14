//����� Chain ������ ���������� � ����� ����� �������� � ������ �������.
//������ ����� ���������� � ������ Protocol.

package p_DotsAI;

import java.awt.Point;
import java.util.ArrayList;

import p_DotsAI.Protocol.Game;

public class Chain{

	public ArrayList<Point> points;//������� ����
	public ArrayList<Point> abstractPoints;//���� ��, ��������� �� ����������� ��������
	
	int chainStartX;//����� ������ ����
	int chainStartY;//����� ������ ����
	public int chainEndX;//����� ����� ����
	public int chainEndY;//����� ����� ����
	int wallIndex;//������ ����
	double chainLength;//����� ����
	String sign;//���� ���� - ������� ��� �����
	
	//����� ���������� ����� �������� ������ �������� �� ���� � �������� ��������� (����������� �����)
	//� ������� � ����� (�����). ��-�� ���������� ����������� ����������� ��������� 
	//����� ����� ����� �� ����������, ������ �� ��� �� �������
	//public ArrayList<Point> connections;//�����
	//public ArrayList<Point> abstracts;//����������� �����
	//public ArrayList<Point> links;//����������� �����
	
	public Chain(Game game,int x,int y,int wallIndex,String sign,String[][] fieldOfChains){		
		points=new ArrayList<Point>();
		abstractPoints=new ArrayList<Point>();
		//connections=new ArrayList<Point>();
		//abstracts=new ArrayList<Point>();
		//links=new ArrayList<Point>();
		points.add(new Point(x,y));
		
		//����� ������ ����
		chainStartX=x;
		chainStartY=y;
		
		this.wallIndex=wallIndex;//������ ����
		this.sign=sign;//���� ���� - ������� ��� �����
		fieldOfChains[x][y]="P"+sign+wallIndex;//��� ����� ����
	}
	
	//����� ����� ����
	public void searchChainEnd(Game game,String[][] fieldOfChains){//������ ����� �����
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
	
	//��������� ����� ����
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
	
	public void addConnection(Game game,int x,int y,String[][] fieldOfWalls){//������ ������ - ������ ������������ � ������
		if(!fieldOfWalls[x][y].equals("N")){return;}
		fieldOfWalls[x][y]="C"+sign+wallIndex;
		connections.add(new Point(x,y));
	}*/
	
	//����������� ���������� �� ����� ����� �� ��������� ������������ ����� �����
	public double getLengthFromLastBlue(Point p){
		chainLength=Math.sqrt(Math.abs(chainEndX-p.x)*Math.abs(chainEndX-p.x)+Math.abs(chainEndY-p.y)*Math.abs(chainEndY-p.y));
		return chainLength;
	}
	
	//��������, ��������� �� ����� ���� �� ���� ����
	public boolean isAtSide(Game game){
		if(chainEndX<2|chainEndY<2|chainEndX>game.sizeX-3|chainEndY>game.sizeY-3){return true;}else{return false;}
	}		
}
