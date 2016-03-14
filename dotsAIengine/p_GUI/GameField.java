//����� ������ ��������� ���� � ������� ���������� ��������� ���������.
//���������� � �������� ��������� ���� - ��� ������������� - ��� ����� ������ ������ ��� ��������.
//��� �������� �������� ��������� ��������� ���������� ����������� ���� �������, 
//��� ������� �������, ��� ������ ���� �� ������� �� ����������.
//�������, ��� ������ ������ ����� ��� �������� ����, ��� ������� ����������� ��������� ����.
//���� �������� ������������ - ��� �������� ����������� ������.

//�������� ������ ��������� ����� ������� ���� � ������� ��������,
//������ ��� ��������� ����������� �������� ������������� ������������ ������ ������ ������ ���������� ��������� - �������.

package p_GUI;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;

import p_DotsAI.Protocol;
import p_DotsAI.Protocol.Game;

public class GameField {

	public static byte BLUE=1;//1=blue
	public static byte RED=2;//2=red
	private byte BLUE_IN_RED=3;//3=���������� �����
	private byte RED_IN_BLUE=4;//4=���������� �������
	private byte EMPTY=0;//0=������ ����� �� ����
	private byte EMPTY_IN_RED_ENCIRCLEMENT=-1;//-1=������ ����� ������ �������� ���������
	private byte EMPTY_IN_BLUE_ENCIRCLEMENT=-2;//-2=������ ����� ������ ������ ���������
	private byte EMPTY_IN_RED_HOUSE=-3;//-3=������ ����� � ������� ������
	private byte EMPTY_IN_BLUE_HOUSE=-4;//-4=������ ����� � ����� ������
	
	public byte[][] gameField;//������� ����
	public byte[][] fieldForBuffer;//��������������� �������� ����	
	public int scoreBlue,scoreRed;//����� ��������� �����
	public byte sizeX,sizeY;//������ ����
	public Point lastDot=new Point();//��������� ���
	public byte lastDotType;//��������� ���, 1=blue, 2=red
	public ArrayList<Polygon> blueEnclosures=new ArrayList<Polygon>();//����� ���������
	public ArrayList<Polygon> redEnclosures=new ArrayList<Polygon>();//������� ���������
	
	public GameField(byte sizeX,byte sizeY){//�������� �������� ����, ������ ����
		gameField=new byte[sizeX][sizeY];
		fieldForBuffer=new byte[sizeX][sizeY];
		scoreBlue=0;
		scoreRed=0;
		this.sizeX=sizeX;
		this.sizeY=sizeY;
	}
	
	public GameField clone(){
		GameField obj=new GameField(sizeX,sizeY);
    	obj.lastDotType=lastDotType;
    	obj.lastDot=new Point(lastDot.x,lastDot.y);
    	obj.scoreBlue=scoreBlue;
    	obj.scoreRed=scoreRed;
    	obj.sizeX=sizeX;
    	obj.sizeY=sizeY;
    	
    	obj.gameField=new byte[sizeX][sizeY];
    	obj.fieldForBuffer=new byte[sizeX][sizeY];
    	
    	for (byte x = 0; x < sizeX; x++) {
			for (byte y = 0; y < sizeY; y++) {
				obj.gameField[x][y]=gameField[x][y];
			}
		}
    	
    	//���� ����� ���� Polygons � �� ���� �����������
    	obj.blueEnclosures=clonePolygons(blueEnclosures);//����� ���������
    	obj.redEnclosures=clonePolygons(redEnclosures);//������� ���������
    	
        return obj;
    }
	
	private ArrayList<Polygon> clonePolygons(ArrayList<Polygon> incomeEnclosures){
		ArrayList<Polygon> enclosures=new ArrayList<Polygon>();
		for (int i = 0; i < incomeEnclosures.size(); i++) {
			Polygon p=new Polygon();
			p.xpoints=new int[incomeEnclosures.get(i).xpoints.length];
			p.ypoints=new int[incomeEnclosures.get(i).ypoints.length];
			for(int k=0;k<incomeEnclosures.get(i).xpoints.length;k++){
				p.xpoints[k]=incomeEnclosures.get(i).xpoints[k];
				p.ypoints[k]=incomeEnclosures.get(i).ypoints[k];
			}			
			enclosures.add(p);
		}
		return enclosures;
	}
	
	public void addMove(Point p,byte type){//������� ���, 2=red, 1=blue
		addMove((byte)p.x,(byte)p.y,type);
	}
	
	public void addMove(byte x,byte y,byte type){//������� ���, 2=red, 1=blue
		if(!canAddMove(x,y))return;//������ ������� ���
		
		boolean isInHouseOfSameColor=false;//������ �� ��� � �����
		if(
				(gameField[x][y]==EMPTY_IN_BLUE_HOUSE&type==BLUE)
				||
				(gameField[x][y]==EMPTY_IN_RED_HOUSE&type==RED)
			){
			isInHouseOfSameColor=true;//��� ������ � ����� ������ �����
		}
		
		boolean isInHouseOfOtherColor=false;//������ �� ��� � �����
		if(
				(gameField[x][y]==EMPTY_IN_BLUE_HOUSE&type==RED)
				||
				(gameField[x][y]==EMPTY_IN_RED_HOUSE&type==BLUE)
			){
			isInHouseOfOtherColor=true;//��� ������ � ����� ������ �����
		}
		
		gameField[x][y]=type;
		lastDotType=type;//��������� ��������� ���
				
		//��������� ���������� ���������� ����
		lastDot.x=x;
		lastDot.y=y;
		
		if(isInHouseOfSameColor)return;//���� ��� � ������ ���� �� �����, �� �� ������ ���������		
				
		//������� �� ����� ���������, ����� ���������
		boolean isFoundNewEnclosure=false;
		try{isFoundNewEnclosure=isFoundNewEnclosure(x,y,type,(byte)-1,(byte)-1);}catch(Exception e){e.printStackTrace();}		
		
		if(!isFoundNewEnclosure&&isInHouseOfOtherColor){//���� ����� ���������� � ����� � �� ������� ������ ���������, ������ ��� ����������
			for(byte j=y;j<sizeY;j++){
				//����� ����� ���������, ���������� ������� ������, ������� ����� �������� ������ ���������
				try{if(gameField[x][j]==getTranformedType(type)&&(gameField[x][j-1]==EMPTY_IN_BLUE_HOUSE||gameField[x][j-1]==EMPTY_IN_RED_HOUSE||gameField[x][j-1]==type||gameField[x][j-1]==getTranformedType(type))){
					if(isFoundNewEnclosure(x,j,getTranformedType(type),x,y))break;//����� ���� ����������
					else{
						continue;
					}
				}}catch(Exception e){e.printStackTrace();}
			}
		}
	}
	
	private boolean isFoundNewEnclosure(byte x,byte y,byte type,byte i1,byte j1){//����� ���������
				
		//���� � ����� ������ ���� ������� ���� �� �����, �� ������ �������� ���������
		byte neighbors=0;
		try{if(gameField[x+1][y]==type){neighbors++;}}catch(Exception e){}
		try{if(gameField[x][y+1]==type){neighbors++;}}catch(Exception e){}
		try{if(gameField[x-1][y]==type){neighbors++;}}catch(Exception e){}
		try{if(gameField[x][y-1]==type){neighbors++;}}catch(Exception e){}
		try{if(gameField[x+1][y-1]==type){neighbors++;}}catch(Exception e){}
		try{if(gameField[x-1][y+1]==type){neighbors++;}}catch(Exception e){}
		try{if(gameField[x+1][y+1]==type){neighbors++;}}catch(Exception e){}
		try{if(gameField[x-1][y-1]==type){neighbors++;}}catch(Exception e){}
		if(neighbors<2)return false;
		
		//����������� �� ��������� � ����� ��������� 4 �������� ������� ����� �� ����������� � ���������, �� �� ���������
		boolean isFoundNewEnclosure=false;//������� �� ���� �� ���� �������� ��������� (�� �����)
		ArrayList<Point> foundEnclosuresPoints=new ArrayList<Point>();//������ �� ����� ����� ������� ���������� ���������
		if(i1==-1&&j1==-1){//�������� - ������� �� ����� ����� ���������, ���� ���� ��� ���������� � �����
			try{if(fieldForBuffer[x+1][y]==0&&gameField[x+1][y]!=type&&gameField[x+1][y]!=type+2&&gameField[x+1][y]!=getTranformedType(type)+2){
				if(x+1==0||x+1==sizeX-1||y==0||y==sizeY-1){}//���� �� ������� ���� - ������ �� ������
				else{
					fieldForBuffer[x+1][y]=3;
					boolean isCreatedNewEnclosure=analyzeForEnclosure((byte)3,getTranformedType(type),(byte)(x+1),y);//��������� �� ����� ���������
					if(isCreatedNewEnclosure)foundEnclosuresPoints.add(new Point(x+1,y));//���������� ����� �� ���������� ���������
				}
			}}catch(Exception e){}
			try{if(fieldForBuffer[x][y+1]==0&&gameField[x][y+1]!=type&&gameField[x][y+1]!=type+2&&gameField[x][y+1]!=getTranformedType(type)+2){
				if(x==0||x==sizeX-1||y+1==0||y+1==sizeY-1){}//���� �� ������� ���� - ������ �� ������
				else{
					fieldForBuffer[x][y+1]=4;
					boolean isCreatedNewEnclosure=analyzeForEnclosure((byte)4,getTranformedType(type),x,(byte)(y+1));//��������� �� ����� ���������
					if(isCreatedNewEnclosure)foundEnclosuresPoints.add(new Point(x,y+1));//���������� ����� �� ���������� ���������
				}
			}}catch(Exception e){}
			try{if(fieldForBuffer[x-1][y]==0&&gameField[x-1][y]!=type&&gameField[x-1][y]!=type+2&&gameField[x-1][y]!=getTranformedType(type)+2){
				if(x-1==0||x-1==sizeX-1||y==0||y==sizeY-1){}//���� �� ������� ���� - ������ �� ������
				else{
					fieldForBuffer[x-1][y]=5;			
					boolean isCreatedNewEnclosure=analyzeForEnclosure((byte)5,getTranformedType(type),(byte)(x-1),y);//��������� �� ����� ���������
					if(isCreatedNewEnclosure)foundEnclosuresPoints.add(new Point(x-1,y));//���������� ����� �� ���������� ���������
				}
			}}catch(Exception e){}
			try{if(fieldForBuffer[x][y-1]==0&&gameField[x][y-1]!=type&&gameField[x][y-1]!=type+2&&gameField[x][y-1]!=getTranformedType(type)+2){
				if(x==0||x==sizeX-1||y-1==0||y-1==sizeY-1){}//���� �� ������� ���� - ������ �� ������
				else{
					fieldForBuffer[x][y-1]=6;
					boolean isCreatedNewEnclosure=analyzeForEnclosure((byte)6,getTranformedType(type),x,(byte)(y-1));//��������� �� ����� ���������
					if(isCreatedNewEnclosure)foundEnclosuresPoints.add(new Point(x,y-1));//���������� ����� �� ���������� ���������
				}
			}}catch(Exception e){}
		}else{//���� ����� ���������� � ����� � �� ������� ������ ���������, ������ ��� ����������
			fieldForBuffer[i1][j1]=2;
			boolean isCreatedNewEnclosure=analyzeForEnclosure((byte)2,getTranformedType(type),i1,j1);//��������� �� ����� ���������
			if(isCreatedNewEnclosure)foundEnclosuresPoints.add(new Point(i1,j1));//���������� ����� �� ���������� ���������
		}
		
		//������ ������ �� ����� ������� (������������ ��������� ������ ��������� � ������)
		if(foundEnclosuresPoints.size()>0){
				boolean isFoundDeadEnd=false;
				for(int i=0;i<foundEnclosuresPoints.size();i++){
					if(
							(foundEnclosuresPoints.get(i).x==x-1&&foundEnclosuresPoints.get(i).y==y)
							|
							(foundEnclosuresPoints.get(i).x==x+1&&foundEnclosuresPoints.get(i).y==y)
						){
						isFoundDeadEnd=true;
						break;
					}
				}
				try{if(isFoundDeadEnd&&fieldForBuffer[x-1][y]==fieldForBuffer[x+1][y]){
					byte idx=fieldForBuffer[x-1][y];
					if(fieldForBuffer[x][y+1]!=idx&&fieldForBuffer[x][y+1]>2){
						boolean isFoundDeadEnd1=false;
						for(int i=0;i<foundEnclosuresPoints.size();i++){
							if(foundEnclosuresPoints.get(i).x==x&&foundEnclosuresPoints.get(i).y==y+1){
								isFoundDeadEnd1=true;
								foundEnclosuresPoints.remove(i);
								break;
							}
						}
						if(isFoundDeadEnd1){
							byte idx1=fieldForBuffer[x][y+1];								
							for(byte i=0;i<sizeX;i++){
								for(byte j=0;j<sizeY;j++){
									if(fieldForBuffer[i][j]==idx1)fieldForBuffer[i][j]=idx;
								}
							}
							
							if(gameField[x-1][y+1]==type)fieldForBuffer[x-1][y+1]=idx;
							if(gameField[x+1][y+1]==type)fieldForBuffer[x+1][y+1]=idx;
						}
										
					}
					if(fieldForBuffer[x][y-1]!=idx&&fieldForBuffer[x][y-1]>2){
						boolean isFoundDeadEnd1=false;
						for(int i=0;i<foundEnclosuresPoints.size();i++){
							if(foundEnclosuresPoints.get(i).x==x&&foundEnclosuresPoints.get(i).y==y-1){
								isFoundDeadEnd1=true;
								foundEnclosuresPoints.remove(i);
								break;
							}
						}
						if(isFoundDeadEnd1){
							byte idx1=fieldForBuffer[x][y-1];									
							for(byte i=0;i<sizeX;i++){
								for(byte j=0;j<sizeY;j++){
									if(fieldForBuffer[i][j]==idx1)fieldForBuffer[i][j]=idx;
								}
							}
							
							if(gameField[x-1][y-1]==type)fieldForBuffer[x-1][y-1]=idx;
							if(gameField[x+1][y-1]==type)fieldForBuffer[x+1][y-1]=idx;
						}			
					}
				}}catch(Exception e){}
				//������ ������ �� ����� ������� (�������������� ��������� ������ ��������� � ������)
				boolean isFoundDeadEnd2=false;
				for(int i=0;i<foundEnclosuresPoints.size();i++){
					if(
							(foundEnclosuresPoints.get(i).x==x&&foundEnclosuresPoints.get(i).y==y-1)
							|
							(foundEnclosuresPoints.get(i).x==x&&foundEnclosuresPoints.get(i).y==y+1)
						){
						isFoundDeadEnd2=true;
						break;
					}
				}
				try{if(isFoundDeadEnd2&&fieldForBuffer[x][y-1]==fieldForBuffer[x][y+1]){
					byte idx=fieldForBuffer[x][y-1];
					if(fieldForBuffer[x+1][y]!=idx&&fieldForBuffer[x+1][y]>2){
						boolean isFoundDeadEnd1=false;
						for(int i=0;i<foundEnclosuresPoints.size();i++){
							if(foundEnclosuresPoints.get(i).x==x+1&&foundEnclosuresPoints.get(i).y==y){
								isFoundDeadEnd1=true;
								foundEnclosuresPoints.remove(i);
								break;
							}
						}
						if(isFoundDeadEnd1){
							byte idx1=fieldForBuffer[x+1][y];								
							for(byte i=0;i<sizeX;i++){
								for(byte j=0;j<sizeY;j++){
									if(fieldForBuffer[i][j]==idx1)fieldForBuffer[i][j]=idx;
								}
							}
							
							if(gameField[x+1][y-1]==type)fieldForBuffer[x+1][y-1]=idx;
							if(gameField[x+1][y+1]==type)fieldForBuffer[x+1][y+1]=idx;
						}
										
					}
					if(fieldForBuffer[x-1][y]!=idx&&fieldForBuffer[x-1][y]>2){
						boolean isFoundDeadEnd1=false;
						for(int i=0;i<foundEnclosuresPoints.size();i++){
							if(foundEnclosuresPoints.get(i).x==x-1&&foundEnclosuresPoints.get(i).y==y){
								isFoundDeadEnd1=true;
								foundEnclosuresPoints.remove(i);
								break;
							}
						}
						if(isFoundDeadEnd1){
							byte idx1=fieldForBuffer[x-1][y];									
							for(byte i=0;i<sizeX;i++){
								for(byte j=0;j<sizeY;j++){
									if(fieldForBuffer[i][j]==idx1)fieldForBuffer[i][j]=idx;
								}
							}
							
							if(gameField[x-1][y-1]==type)fieldForBuffer[x-1][y-1]=idx;
							if(gameField[x-1][y+1]==type)fieldForBuffer[x-1][y+1]=idx;
						}			
					}
				}}catch(Exception e){}
		}
		
		for(byte k=0;k<foundEnclosuresPoints.size();k++){//����� ������ ��������� � ������������� ����� ��� ��������� ���������
			//����� ������ ��������� (����� ��������)
			Polygon p=searchPolygon(type,x,y,(byte)foundEnclosuresPoints.get(k).x,(byte)foundEnclosuresPoints.get(k).y,fieldForBuffer[foundEnclosuresPoints.get(k).x][foundEnclosuresPoints.get(k).y]);
			if(p==null)continue;
			
			int foundedScore=0;//����� ����������� ����� ��������� � ���������
			for(byte i=0;i<sizeX;i++){
				for(byte j=0;j<sizeY;j++){
					if(fieldForBuffer[i][j]==fieldForBuffer[foundEnclosuresPoints.get(k).x][foundEnclosuresPoints.get(k).y]){
						if(gameField[i][j]==getTranformedType(type)){//������� ����� ����������� ����� ��������� � ���������
							foundedScore++;
							gameField[i][j]=(byte)(getTranformedType(type)+2);
						}else if(gameField[i][j]==type+2){//������� ����� ������������� �� ��������� ��������� ����� ������ �����
							if(type==BLUE){scoreRed--;}
							else if(type==RED){scoreBlue--;}
							gameField[i][j]=type;
						}else if(gameField[i][j]==EMPTY){//������ ����� � ��������� ���������� ��� -1
							if(type==BLUE)gameField[i][j]=EMPTY_IN_BLUE_ENCIRCLEMENT;
							else if(type==RED)gameField[i][j]=EMPTY_IN_RED_ENCIRCLEMENT;
						}						
					}
				}
			}
			if(foundedScore>0){//���� ������� ��������� (�� �����)
				if(type==BLUE){scoreBlue+=foundedScore;blueEnclosures.add(p);isFoundNewEnclosure=true;}
				else if(type==RED){scoreRed+=foundedScore;redEnclosures.add(p);isFoundNewEnclosure=true;}
				
				for(byte i=0;i<sizeX;i++){
					for(byte j=0;j<sizeY;j++){
						if(fieldForBuffer[i][j]==fieldForBuffer[foundEnclosuresPoints.get(k).x][foundEnclosuresPoints.get(k).y]
								&&
							(gameField[i][j]==EMPTY_IN_BLUE_HOUSE||gameField[i][j]==EMPTY_IN_RED_HOUSE)
						){
							if(type==BLUE)gameField[i][j]=EMPTY_IN_BLUE_ENCIRCLEMENT;
							else if(type==RED)gameField[i][j]=EMPTY_IN_RED_ENCIRCLEMENT;//��� ������� ������ ��������� �������� -1, ����� ��������� � ��� ������� �����
						}
					}
				}
			}else{//���� ������ �����
				
				for(byte i=0;i<sizeX;i++){
					for(byte j=0;j<sizeY;j++){
						if(fieldForBuffer[i][j]==fieldForBuffer[foundEnclosuresPoints.get(k).x][foundEnclosuresPoints.get(k).y]){
							if(gameField[i][j]<=EMPTY){//��� ���� ������ ������ �������� EMPTY_IN_, ����� ����� ���� � ��� ������� �����
								if(type==BLUE)gameField[i][j]=EMPTY_IN_BLUE_HOUSE;
								else if(type==RED)gameField[i][j]=EMPTY_IN_RED_HOUSE;
							}
						}
					}
				}
			}
		}
		
		for(byte i=0;i<sizeX;i++){
			for(byte j=0;j<sizeY;j++){
				fieldForBuffer[i][j]=EMPTY;//������� ��������� ����
			}
		}
		
		return isFoundNewEnclosure;
	}
	
	private Polygon searchPolygon(byte type,byte x,byte y,byte i,byte j,byte idx){//����� ������ ��������� (��������)
		ArrayList<Point> point=new ArrayList<Point>();//������� �����, ���������� ������� ���������
		point.add(new Point(x,y));//�������� �����, �� ������� ���������� �����
		
		ArrayList<Point> pointDeadEnd=new ArrayList<Point>();//�����, �������� � �����
		
		byte initX=x;//������ ����� � ���������
		byte initY=y;
		byte dir=0;//����������� ������� �� ���������
		
		Point p=new Point();//�������� �������� ����� ������ �� �����������
		
		byte iterationsCount=0;
		byte preX=x;
		byte preY=y;
		
		for(;;){
			for(int k=0;k<Integer.MAX_VALUE;k++){//���� ��������� ����� �� ���� ������������
				
				dir++;//������ �����������
				if(dir>7)dir-=8;//������������ �����������
				if(dir<0)dir+=8;
				
				//�������� �������� ����� ������ �� �����������
				if(dir==0){p.x=x+1;p.y=y;}
				else if(dir==1){p.x=x+1;p.y=y+1;}
				else if(dir==2){p.x=x;p.y=y+1;}
				else if(dir==3){p.x=x-1;p.y=y+1;}
				else if(dir==4){p.x=x-1;p.y=y;}
				else if(dir==5){p.x=x-1;p.y=y-1;}
				else if(dir==6){p.x=x;p.y=y-1;}
				else{p.x=x+1;p.y=y-1;}

				//������ ������ �� ����� �������	
				if(preX==x&preY==y)iterationsCount++;
				else iterationsCount=0;
				preX=x;
				preY=y;
				if(iterationsCount>7){	
					fieldForBuffer[x][y]=idx;
					point.removeAll(point);
					point.add(new Point(initX,initY));//�������� �����, �� ������� ���������� �����
					x=initX;
					y=initY;
					continue;
				}
				
				//����� �� �������� �����, ������ ������� ������
				if(p.x==initX&&p.y==initY){
					if(point.size()>3){
						if(point.get(point.size()-2).x==point.get(point.size()-1).x&&point.get(point.size()-2).y==point.get(point.size()-1).y){
							//������� ���� �� �������������� ������
							int xToDelete=point.get(point.size()-1).x;
							int yToDelete=point.get(point.size()-1).y;
							fieldForBuffer[x][y]=idx;
							for(int i1=point.size()-1;i1>=0;i1--){
								if(point.get(i1).x==xToDelete&&point.get(i1).y==yToDelete){
									point.remove(i1);
								}
							}
							x=initX;
							y=initY;
							continue;
						}else if(point.size()==4){//������� ������ ��� ������
							boolean isFoundDeadEnd=false;
							for(int i1=point.size()-1;i1>=0;i1--){
								int inCount=0;
								try{if(fieldForBuffer[point.get(i1).x+1][point.get(i1).y]==idx)inCount++;}catch(Exception e){}
								try{if(fieldForBuffer[point.get(i1).x-1][point.get(i1).y]==idx)inCount++;}catch(Exception e){}
								try{if(fieldForBuffer[point.get(i1).x][point.get(i1).y+1]==idx)inCount++;}catch(Exception e){}
								try{if(fieldForBuffer[point.get(i1).x][point.get(i1).y-1]==idx)inCount++;}catch(Exception e){}
								if(inCount>1){
									fieldForBuffer[point.get(i1).x][point.get(i1).y]=idx;
									for(int i2=point.size()-1;i2>0;i2--){
										point.remove(i2);
									}
									isFoundDeadEnd=true;
									break;
								}
							}
							if(isFoundDeadEnd){
								x=initX;
								y=initY;
								continue;
							}
						}
						break;//������ ������ �������
					}else{
						dir++;
						continue;
					}
				}
				if(p.x<0||p.y<0||p.x>sizeX-1||p.y>sizeY-1)continue;//����� ������� �� ���� ���� - �� ������������� ��
				if(gameField[p.x][p.y]==type&fieldForBuffer[p.x][p.y]!=idx){//���� ����� ���� �� ����, ��� � �������
					boolean isChainDot=false;//���� ����� �������� � ���������� ������ ���������, ������ ��� �������� ��� ��������
					try{if(fieldForBuffer[p.x+1][p.y]==idx)isChainDot=true;}catch(Exception e){}
					try{if(fieldForBuffer[p.x-1][p.y]==idx)isChainDot=true;}catch(Exception e){}
					try{if(fieldForBuffer[p.x][p.y+1]==idx)isChainDot=true;}catch(Exception e){}
					try{if(fieldForBuffer[p.x][p.y-1]==idx)isChainDot=true;}catch(Exception e){}					
					if(!isChainDot)continue;
					
					if(pointDeadEnd.size()>1){//����� �� ������
						if(p.x==pointDeadEnd.get(1).x&&p.y==pointDeadEnd.get(1).y){
							
							boolean isRemoved=false;
							for(int n=0;n<point.size();n++){
								if(pointDeadEnd.get(1).x==point.get(n).x&&pointDeadEnd.get(1).y==point.get(n).y){									
									for(int n1=point.size()-1;n1>=n;n1--){//�������� �� ������
										fieldForBuffer[point.get(n1).x][point.get(n1).y]=idx;
										point.remove(n1);
									}
									isRemoved=true;
									break;
								}
							}
							if(isRemoved){
								x=(byte)point.get(point.size()-1).x;
								y=(byte)point.get(point.size()-1).y;								
								pointDeadEnd.removeAll(pointDeadEnd);//�������� ������������� ����� ������
								dir-=4;
								continue;
							}
						}
					}
					
					//���������� �� �����
					try{if(fieldForBuffer[p.x-1][p.y]==idx&&fieldForBuffer[p.x+1][p.y]==idx){
						try{if(fieldForBuffer[p.x-1][p.y-1]==idx&&fieldForBuffer[p.x+1][p.y-1]==idx&&gameField[p.x][p.y-1]==type&&fieldForBuffer[p.x][p.y-1]!=idx/*&&gameField[p.x][p.y+1]==EMPTY*/){
							if(p.x==initX&&p.y-1==initY){
								fieldForBuffer[p.x][p.y]=idx;
							}else{
								fieldForBuffer[p.x][p.y-1]=idx;
							}
							x=initX;
							y=initY;
							point.removeAll(point);
							point.add(new Point(initX,initY));//�������� �����, �� ������� ���������� �����
							continue;
						}}catch(Exception e){}
						
						try{if(fieldForBuffer[p.x-1][p.y+1]==idx&&fieldForBuffer[p.x+1][p.y+1]==idx&&gameField[p.x][p.y+1]==type&&fieldForBuffer[p.x][p.y+1]!=idx/*&&gameField[p.x][p.y-1]==EMPTY*/){
							if(p.x==initX&&p.y+1==initY){
								fieldForBuffer[p.x][p.y]=idx;
							}else{
								fieldForBuffer[p.x][p.y+1]=idx;
							}
							x=initX;
							y=initY;
							point.removeAll(point);
							point.add(new Point(initX,initY));//�������� �����, �� ������� ���������� �����
							continue;
						}}catch(Exception e){}
						
						if(pointDeadEnd.size()>0){//�������� ����������� ������ ��� ���������� ������							
							if(Math.abs(pointDeadEnd.get(pointDeadEnd.size()-1).x-p.x)<=1&&Math.abs(pointDeadEnd.get(pointDeadEnd.size()-1).y-p.y)<=1){}
							else{pointDeadEnd.removeAll(pointDeadEnd);}
						}
						pointDeadEnd.add(new Point(p.x,p.y));
					}}catch(Exception e){}
					try{if(fieldForBuffer[p.x][p.y-1]==idx&&fieldForBuffer[p.x][p.y+1]==idx){
						try{if(fieldForBuffer[p.x-1][p.y-1]==idx&&fieldForBuffer[p.x-1][p.y+1]==idx&&gameField[p.x-1][p.y]==type&&fieldForBuffer[p.x-1][p.y]!=idx/*&&gameField[p.x+1][p.y]==EMPTY*/){
							if(p.x-1==initX&&p.y==initY){
								fieldForBuffer[p.x][p.y]=idx;
							}else{
								fieldForBuffer[p.x-1][p.y]=idx;
							}
							x=initX;
							y=initY;
							point.removeAll(point);
							point.add(new Point(initX,initY));//�������� �����, �� ������� ���������� �����
							continue;
						}}catch(Exception e){}
						
						try{if(fieldForBuffer[p.x+1][p.y-1]==idx&&fieldForBuffer[p.x+1][p.y+1]==idx&&gameField[p.x+1][p.y]==type&&fieldForBuffer[p.x+1][p.y]!=idx/*&&gameField[p.x-1][p.y]==EMPTY*/){
							if(p.x+1==initX&&p.y==initY){
								fieldForBuffer[p.x][p.y]=idx;
							}else{
								fieldForBuffer[p.x+1][p.y]=idx;
							}
							x=initX;
							y=initY;
							point.removeAll(point);
							point.add(new Point(initX,initY));//�������� �����, �� ������� ���������� �����
							continue;
						}}catch(Exception e){}
						
						if(pointDeadEnd.size()>0){//�������� ����������� ������ ��� ���������� ������							
							if(Math.abs(pointDeadEnd.get(pointDeadEnd.size()-1).x-p.x)<=1&&Math.abs(pointDeadEnd.get(pointDeadEnd.size()-1).y-p.y)<=1){}
							else{pointDeadEnd.removeAll(pointDeadEnd);}
						}
						pointDeadEnd.add(new Point(p.x,p.y));
					}}catch(Exception e){}
										
					if(isChainDot){//������� �� ��������� ����� ���� ���������						
						point.add(new Point(p.x,p.y));//��������� ��������� �����
						x=(byte)p.x;
						y=(byte)p.y;
						dir-=4;
						if(point.size()>1000)return null;//���� ��������� ��������� ������ �� ����� �������
						continue;
					}
				}
			}
			
			if(point.size()<7){
				break;
			}else{					
				//���� ��� ��������� ������� �� �����
				int[][] fieldForDeadEnd=new int[sizeX][sizeY];
				for(int k=0;k<point.size();k++){//�������� �� ���� �����, ���������� ������� ��������
					fieldForDeadEnd[point.get(k).x][point.get(k).y]=1;
				}
				
				for(int i1=0;i1<sizeX;i1++){
					for(int j1=0;j1<sizeY;j1++){//�������� �� ���� ��������� ����� ����� ������ ���������� ��������
						if(fieldForBuffer[i1][j1]==idx)fieldForDeadEnd[i1][j1]=idx+3;
					}
				}
				
				int count=1;
				while(count>0){//�������� ��� ����� ������ ��������, ������������� ��������� ���������
					count=0;
					for(int i1=1;i1<sizeX-1;i1++){
						for(int j1=1;j1<sizeY-1;j1++){
							if(fieldForDeadEnd[i1][j1]==idx+3){
								if(fieldForDeadEnd[i1][j1+1]==0){
									fieldForDeadEnd[i1][j1+1]=idx+3;
									count++;
								}
								if(fieldForDeadEnd[i1+1][j1]==0){
									fieldForDeadEnd[i1+1][j1]=idx+3;
									count++;
								}
								if(fieldForDeadEnd[i1][j1-1]==0){
									fieldForDeadEnd[i1][j1-1]=idx+3;
									count++;
								}
								if(fieldForDeadEnd[i1-1][j1]==0){
									fieldForDeadEnd[i1-1][j1]=idx+3;
									count++;
								}
							}
						}
					}
				}
				
				//��� ����� ������ ��������, �� ��������� �����, ������ ����������
				for(int i1=0;i1<sizeX;i1++){
					for(int j1=0;j1<sizeY;j1++){
						if(fieldForDeadEnd[i1][j1]==idx+3){
							fieldForBuffer[i1][j1]=idx;
						}
					}
				}
				break;
			}
		}
		
		//��������� � ����������� ����� �������
		Polygon polygon=new Polygon();
		polygon.xpoints=new int[point.size()];
		polygon.ypoints=new int[point.size()];
		for(int k=0;k<point.size();k++){
			polygon.xpoints[k]=point.get(k).x;
			polygon.ypoints[k]=point.get(k).y;
		}
		return polygon;
	}
	
	private boolean analyzeForEnclosure(byte idx,byte type,byte x,byte y){//����� ���������
		int count=0;
		//����� ������� ������� �� ����� x,y
		if(x!=-1){
			if(x<sizeX-1){//�� �� ���� ����
				for(int k=x+1;k<sizeX;k++){
					if(gameField[k][y]<=0||gameField[k][y]==type||gameField[k][y]==getTranformedType(type)+2){
						fieldForBuffer[k][y]=idx;//�������� ������, ��� �� ������ � �� �� ���������
						count++;
						if(k==sizeX-1)return false;//���� ����� �������� � ����� ����, ������ ��������� �� ���������
					}else break;
				}
			}
			if(y<sizeY-1){//�� �� ���� ����
				for(int k=y+1;k<sizeY;k++){
					if(gameField[x][k]<=0||gameField[x][k]==type||gameField[x][k]==getTranformedType(type)+2){
						fieldForBuffer[x][k]=idx;//�������� ������, ��� �� ������ � �� �� ���������
						count++;
						if(k==sizeY-1)return false;//���� ����� �������� � ����� ����, ������ ��������� �� ���������
					}else break;
				}
			}
			if(x>0){//�� �� ���� ����
				for(int k=x-1;k>=0;k--){
					if(gameField[k][y]<=0||gameField[k][y]==type||gameField[k][y]==getTranformedType(type)+2){
						fieldForBuffer[k][y]=idx;//�������� ������, ��� �� ������ � �� �� ���������
						count++;
						if(k==0)return false;//���� ����� �������� � ����� ����, ������ ��������� �� ���������
					}else break;
				}
			}
			if(y>0){//�� �� ���� ����
				for(int k=y-1;k>=0;k--){
					if(gameField[x][k]<=0||gameField[x][k]==type||gameField[x][k]==getTranformedType(type)+2){
						fieldForBuffer[x][k]=idx;//�������� ������, ��� �� ������ � �� �� ���������
						count++;
						if(k==0)return false;//���� ����� �������� � ����� ����, ������ ��������� �� ���������
					}else break;
				}
			}
		}
		while(count>0){//����� ���� �� ����� ������� ��� ������
			count=0;
			for(byte i=0;i<sizeX;i++){
				for(byte j=0;j<sizeY;j++){
					if(fieldForBuffer[i][j]==idx){//����� ������� ��� ��� ��������� ����� ���������
						if(i<sizeX-1){//�� �� ���� ����
							for(int k=i+1;k<sizeX;k++){
								if(fieldForBuffer[k][j]<idx){//���� ����� ��� �� ���� ������� ��� ������� ���������
									if(fieldForBuffer[k][j]>1)return false;//������� ������ ��������� ���� ������� � ���������� idx
									if(gameField[k][j]<=0||gameField[k][j]==type||gameField[k][j]==getTranformedType(type)+2){
										fieldForBuffer[k][j]=idx;//�������� ������, ��� �� ������ � �� �� ���������
										count++;
										if(k==sizeX-1)return false;//���� ����� �������� � ����� ����, ������ ��������� �� ���������
									}else break;
								}else break;								
							}
						}
						if(j<sizeY-1){//�� �� ���� ����
							for(int k=j+1;k<sizeY;k++){
								if(fieldForBuffer[i][k]<idx){//���� ����� ��� �� ���� ������� ��� ������� ���������
									if(fieldForBuffer[i][k]>1)return false;//������� ������ ��������� ���� ������� � ���������� idx
									if(gameField[i][k]<=0||gameField[i][k]==type||gameField[i][k]==getTranformedType(type)+2){
										fieldForBuffer[i][k]=idx;//�������� ������, ��� �� ������ � �� �� ���������
										count++;
										if(k==sizeY-1)return false;//���� ����� �������� � ����� ����, ������ ��������� �� ���������
									}else break;
								}else break;
							}
						}
						if(i>0){//�� �� ���� ����
							for(int k=i-1;k>=0;k--){
								if(fieldForBuffer[k][j]<idx){//���� ����� ��� �� ���� ������� ��� ������� ���������
									if(fieldForBuffer[k][j]>1)return false;//������� ������ ��������� ���� ������� � ���������� idx
									if(gameField[k][j]<=0||gameField[k][j]==type||gameField[k][j]==getTranformedType(type)+2){
										fieldForBuffer[k][j]=idx;//�������� ������, ��� �� ������ � �� �� ���������
										count++;
										if(k==0)return false;//���� ����� �������� � ����� ����, ������ ��������� �� ���������
									}else break;
								}else break;
							}
						}
						if(j>0){//�� �� ���� ����
							for(int k=j-1;k>=0;k--){
								if(fieldForBuffer[i][k]<idx){//���� ����� ��� �� ���� ������� ��� ������� ���������
									if(fieldForBuffer[i][k]>1)return false;//������� ������ ��������� ���� ������� � ���������� idx
									if(gameField[i][k]<=0||gameField[i][k]==type||gameField[i][k]==getTranformedType(type)+2){
										fieldForBuffer[i][k]=idx;//�������� ������, ��� �� ������ � �� �� ���������
										count++;
										if(k==0)return false;//���� ����� �������� � ����� ����, ������ ��������� �� ���������
									}else break;
								}else break;
							}
						}
					}
				}
			}
		}
		return true;
	}
	
	private byte getTranformedType(byte type){//�������� ����� ���������������� �����
		if(type==BLUE)return RED;
		if(type==RED)return BLUE;
		return 0;
	}
	
	public boolean canAddMove(byte x,byte y){//����� �� ������� ��� �� ����
		try{
			if(gameField[x][y]==EMPTY||gameField[x][y]==EMPTY_IN_BLUE_HOUSE||gameField[x][y]==EMPTY_IN_RED_HOUSE)return true;
			else return false;
		}catch(Exception e){return false;}		
	}
		
	public byte[][] getFieldState(){//�������� ��������� ����
		byte[][]fieldState=new byte[sizeX][sizeY];
		for(byte x=0;x<sizeX;x++){
			for(byte y=0;y<sizeY;y++){
				fieldState[x][y]=toStringDotType(gameField[x][y]);
			}
		}
		return fieldState;
	}

	public byte[][] getTerrytoryState(){//�������� ��������� ���� � ������ ����������, �.�. ����� ������ ������ ����� ���� ������
		byte[][]fieldTerrytoryState=new byte[sizeX][sizeY];
		for(byte x=0;x<sizeX;x++){
			for(byte y=0;y<sizeY;y++){
				fieldTerrytoryState[x][y]=toTerrytoryStringDotType(gameField[x][y]);		
			}
		}
		return fieldTerrytoryState;
	}
	
	//�������� � ������ � ����� ��������� �������� ���� 
	public void setFieldState(Game game){
		for(byte x=0;x<sizeX;x++){
			for(byte y=0;y<sizeY;y++){
				game.fieldState[x][y]=toStringDotType(gameField[x][y]);
			}
		}
	}
	
	//�������� � ������ � ����� ��������� �������� ���� � ������ ���������� ������ ������� (������ ����� ������ ������� �������� ���� ������)
	public void setTerrytoryState(Game game){
		for(byte x=0;x<sizeX;x++){
			for(byte y=0;y<sizeY;y++){
				game.fieldTerrytoryState[x][y]=toTerrytoryStringDotType(gameField[x][y]);
			}
		}
	}
	
	//������� �������������� ����� �� ���� � ������ ����������, �.�. ������ ����� ������ ������� �������� ���� ������
	public byte toTerrytoryStringDotType(int type){//�������� ��� ������� StringDotType, �.�. �� ��������� ������������ ������ ������� �� �����
		if(type==EMPTY)return Protocol.templateDotType_EMPTY;//empty
		if(type==BLUE||type==RED_IN_BLUE||type==EMPTY_IN_BLUE_HOUSE||type==EMPTY_IN_BLUE_ENCIRCLEMENT)return Protocol.templateDotType_BLUE;//blue
		if(type==RED||type==BLUE_IN_RED||type==EMPTY_IN_RED_HOUSE||type==EMPTY_IN_RED_ENCIRCLEMENT)return Protocol.templateDotType_RED;//red
		return (byte)type;
	}
	
	//������� �������������� ����� �� ����
	public byte toStringDotType(int type){
		if(type==EMPTY||type==EMPTY_IN_BLUE_HOUSE||type==EMPTY_IN_RED_HOUSE)return Protocol.templateDotType_EMPTY;//empty
		if(type==BLUE||type==RED_IN_BLUE||type==EMPTY_IN_BLUE_ENCIRCLEMENT)return Protocol.templateDotType_BLUE;//blue
		if(type==RED||type==BLUE_IN_RED||type==EMPTY_IN_RED_ENCIRCLEMENT)return Protocol.templateDotType_RED;//red
		return (byte)type;
	}
}
