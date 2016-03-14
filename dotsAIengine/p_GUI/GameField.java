//Класс хранит состояние поля и считает траектории возможных окружений.
//Требования к хранению состояния поля - это легковесность - как можно меньше памяти для хранения.
//При проверке ситуаций возможных окружений необходимо клонировать поле целиком, 
//что гораздо быстрее, чем делать ходы от первого до последнего.
//Поэтому, чем меньше памяти нужно для хранения поля, тем быстрее клонируется состояние поля.
//Ведь операция клонирования - это операция копирования памяти.

//Алгоритм поиска окружений имеет простую идею и высокую скорость,
//однако код алгоритма значительно осложнен необходимость обрабатывать редкие случаи поиска траекторий окружений - тупиков.

package p_GUI;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;

import p_DotsAI.Protocol;
import p_DotsAI.Protocol.Game;

public class GameField {

	public static byte BLUE=1;//1=blue
	public static byte RED=2;//2=red
	private byte BLUE_IN_RED=3;//3=окруженный синий
	private byte RED_IN_BLUE=4;//4=окруженный красный
	private byte EMPTY=0;//0=пустое место на поле
	private byte EMPTY_IN_RED_ENCIRCLEMENT=-1;//-1=пустое место внутри красного окружения
	private byte EMPTY_IN_BLUE_ENCIRCLEMENT=-2;//-2=пустое место внутри синего окружения
	private byte EMPTY_IN_RED_HOUSE=-3;//-3=пустое место в красном домике
	private byte EMPTY_IN_BLUE_HOUSE=-4;//-4=пустое место в синем домике
	
	public byte[][] gameField;//игровое поле
	public byte[][] fieldForBuffer;//вспомогательное буферное поле	
	public int scoreBlue,scoreRed;//число набранных очков
	public byte sizeX,sizeY;//размер поля
	public Point lastDot=new Point();//последний ход
	public byte lastDotType;//последний ход, 1=blue, 2=red
	public ArrayList<Polygon> blueEnclosures=new ArrayList<Polygon>();//синие окружения
	public ArrayList<Polygon> redEnclosures=new ArrayList<Polygon>();//красные окружения
	
	public GameField(byte sizeX,byte sizeY){//создание игрового поля, начало игры
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
    	
    	//хотя может быть Polygons и не надо клонировать
    	obj.blueEnclosures=clonePolygons(blueEnclosures);//синие окружения
    	obj.redEnclosures=clonePolygons(redEnclosures);//красные окружения
    	
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
	
	public void addMove(Point p,byte type){//сделать ход, 2=red, 1=blue
		addMove((byte)p.x,(byte)p.y,type);
	}
	
	public void addMove(byte x,byte y,byte type){//сделать ход, 2=red, 1=blue
		if(!canAddMove(x,y))return;//нельзя сделать ход
		
		boolean isInHouseOfSameColor=false;//сделан ли ход в домик
		if(
				(gameField[x][y]==EMPTY_IN_BLUE_HOUSE&type==BLUE)
				||
				(gameField[x][y]==EMPTY_IN_RED_HOUSE&type==RED)
			){
			isInHouseOfSameColor=true;//ход сделан в домик чужого цвета
		}
		
		boolean isInHouseOfOtherColor=false;//сделан ли ход в домик
		if(
				(gameField[x][y]==EMPTY_IN_BLUE_HOUSE&type==RED)
				||
				(gameField[x][y]==EMPTY_IN_RED_HOUSE&type==BLUE)
			){
			isInHouseOfOtherColor=true;//ход сделан в домик чужого цвета
		}
		
		gameField[x][y]=type;
		lastDotType=type;//запомнить последний ход
				
		//запомнить координаты последнего хода
		lastDot.x=x;
		lastDot.y=y;
		
		if(isInHouseOfSameColor)return;//если ход в домике того же цвета, то не искать окружение		
				
		//найдено ли новое окружение, поиск окружения
		boolean isFoundNewEnclosure=false;
		try{isFoundNewEnclosure=isFoundNewEnclosure(x,y,type,(byte)-1,(byte)-1);}catch(Exception e){e.printStackTrace();}		
		
		if(!isFoundNewEnclosure&&isInHouseOfOtherColor){//если точка поставлена в домик и не создает нового окружения, значит она окружается
			for(byte j=y;j<sizeY;j++){
				//поиск точки соперника, образующей границы домика, которая будет границей нового окружения
				try{if(gameField[x][j]==getTranformedType(type)&&(gameField[x][j-1]==EMPTY_IN_BLUE_HOUSE||gameField[x][j-1]==EMPTY_IN_RED_HOUSE||gameField[x][j-1]==type||gameField[x][j-1]==getTranformedType(type))){
					if(isFoundNewEnclosure(x,j,getTranformedType(type),x,y))break;//домик стал окружением
					else{
						continue;
					}
				}}catch(Exception e){e.printStackTrace();}
			}
		}
	}
	
	private boolean isFoundNewEnclosure(byte x,byte y,byte type,byte i1,byte j1){//поиск окружения
				
		//если у точки меньше двух соседей того же цвета, то нельзя провести окружение
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
		
		//проверяются на вхождение в новое окружение 4 соседние позиции точки по горизонтали и вертикали, но не диагонали
		boolean isFoundNewEnclosure=false;//найдено ли хотя бы одно реальное окружение (не домик)
		ArrayList<Point> foundEnclosuresPoints=new ArrayList<Point>();//хранит по одной точке каждого найденного окружения
		if(i1==-1&&j1==-1){//проверка - создает ли точка новое окружение, даже если она поставлена в домик
			try{if(fieldForBuffer[x+1][y]==0&&gameField[x+1][y]!=type&&gameField[x+1][y]!=type+2&&gameField[x+1][y]!=getTranformedType(type)+2){
				if(x+1==0||x+1==sizeX-1||y==0||y==sizeY-1){}//если на границе поля - ничего не делать
				else{
					fieldForBuffer[x+1][y]=3;
					boolean isCreatedNewEnclosure=analyzeForEnclosure((byte)3,getTranformedType(type),(byte)(x+1),y);//создается ли новое окружение
					if(isCreatedNewEnclosure)foundEnclosuresPoints.add(new Point(x+1,y));//добавление точки из найденного окружения
				}
			}}catch(Exception e){}
			try{if(fieldForBuffer[x][y+1]==0&&gameField[x][y+1]!=type&&gameField[x][y+1]!=type+2&&gameField[x][y+1]!=getTranformedType(type)+2){
				if(x==0||x==sizeX-1||y+1==0||y+1==sizeY-1){}//если на границе поля - ничего не делать
				else{
					fieldForBuffer[x][y+1]=4;
					boolean isCreatedNewEnclosure=analyzeForEnclosure((byte)4,getTranformedType(type),x,(byte)(y+1));//создается ли новое окружение
					if(isCreatedNewEnclosure)foundEnclosuresPoints.add(new Point(x,y+1));//добавление точки из найденного окружения
				}
			}}catch(Exception e){}
			try{if(fieldForBuffer[x-1][y]==0&&gameField[x-1][y]!=type&&gameField[x-1][y]!=type+2&&gameField[x-1][y]!=getTranformedType(type)+2){
				if(x-1==0||x-1==sizeX-1||y==0||y==sizeY-1){}//если на границе поля - ничего не делать
				else{
					fieldForBuffer[x-1][y]=5;			
					boolean isCreatedNewEnclosure=analyzeForEnclosure((byte)5,getTranformedType(type),(byte)(x-1),y);//создается ли новое окружение
					if(isCreatedNewEnclosure)foundEnclosuresPoints.add(new Point(x-1,y));//добавление точки из найденного окружения
				}
			}}catch(Exception e){}
			try{if(fieldForBuffer[x][y-1]==0&&gameField[x][y-1]!=type&&gameField[x][y-1]!=type+2&&gameField[x][y-1]!=getTranformedType(type)+2){
				if(x==0||x==sizeX-1||y-1==0||y-1==sizeY-1){}//если на границе поля - ничего не делать
				else{
					fieldForBuffer[x][y-1]=6;
					boolean isCreatedNewEnclosure=analyzeForEnclosure((byte)6,getTranformedType(type),x,(byte)(y-1));//создается ли новое окружение
					if(isCreatedNewEnclosure)foundEnclosuresPoints.add(new Point(x,y-1));//добавление точки из найденного окружения
				}
			}}catch(Exception e){}
		}else{//если точка поставлена в домик и не создает нового окружения, значит она окружается
			fieldForBuffer[i1][j1]=2;
			boolean isCreatedNewEnclosure=analyzeForEnclosure((byte)2,getTranformedType(type),i1,j1);//создается ли новое окружение
			if(isCreatedNewEnclosure)foundEnclosuresPoints.add(new Point(i1,j1));//добавление точки из найденного окружения
		}
		
		//против одного из видов тупиков (вертикальное вхождение одного окружения в другое)
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
				//против одного из видов тупиков (горизонтальное вхождение одного окружения в другое)
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
		
		for(byte k=0;k<foundEnclosuresPoints.size();k++){//поиск границ окружения и корректировка счета для найденных окружений
			//поиск границ окружения (поиск полигона)
			Polygon p=searchPolygon(type,x,y,(byte)foundEnclosuresPoints.get(k).x,(byte)foundEnclosuresPoints.get(k).y,fieldForBuffer[foundEnclosuresPoints.get(k).x][foundEnclosuresPoints.get(k).y]);
			if(p==null)continue;
			
			int foundedScore=0;//число захваченных точек соперника в окружении
			for(byte i=0;i<sizeX;i++){
				for(byte j=0;j<sizeY;j++){
					if(fieldForBuffer[i][j]==fieldForBuffer[foundEnclosuresPoints.get(k).x][foundEnclosuresPoints.get(k).y]){
						if(gameField[i][j]==getTranformedType(type)){//подсчет числа захваченных точек соперника в окружении
							foundedScore++;
							gameField[i][j]=(byte)(getTranformedType(type)+2);
						}else if(gameField[i][j]==type+2){//подсчет числа освобожденных из окружения соперника точек своего цвета
							if(type==BLUE){scoreRed--;}
							else if(type==RED){scoreBlue--;}
							gameField[i][j]=type;
						}else if(gameField[i][j]==EMPTY){//пустые места в окружении отмечаются как -1
							if(type==BLUE)gameField[i][j]=EMPTY_IN_BLUE_ENCIRCLEMENT;
							else if(type==RED)gameField[i][j]=EMPTY_IN_RED_ENCIRCLEMENT;
						}						
					}
				}
			}
			if(foundedScore>0){//если найдено окружение (не домик)
				if(type==BLUE){scoreBlue+=foundedScore;blueEnclosures.add(p);isFoundNewEnclosure=true;}
				else if(type==RED){scoreRed+=foundedScore;redEnclosures.add(p);isFoundNewEnclosure=true;}
				
				for(byte i=0;i<sizeX;i++){
					for(byte j=0;j<sizeY;j++){
						if(fieldForBuffer[i][j]==fieldForBuffer[foundEnclosuresPoints.get(k).x][foundEnclosuresPoints.get(k).y]
								&&
							(gameField[i][j]==EMPTY_IN_BLUE_HOUSE||gameField[i][j]==EMPTY_IN_RED_HOUSE)
						){
							if(type==BLUE)gameField[i][j]=EMPTY_IN_BLUE_ENCIRCLEMENT;
							else if(type==RED)gameField[i][j]=EMPTY_IN_RED_ENCIRCLEMENT;//для домиков внутри окружения ставится -1, чтобы запретить в них ставить точки
						}
					}
				}
			}else{//если найден домик
				
				for(byte i=0;i<sizeX;i++){
					for(byte j=0;j<sizeY;j++){
						if(fieldForBuffer[i][j]==fieldForBuffer[foundEnclosuresPoints.get(k).x][foundEnclosuresPoints.get(k).y]){
							if(gameField[i][j]<=EMPTY){//для мест внутри домика ставится EMPTY_IN_, чтобы можно было в них ставить точки
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
				fieldForBuffer[i][j]=EMPTY;//очистка буферного поля
			}
		}
		
		return isFoundNewEnclosure;
	}
	
	private Polygon searchPolygon(byte type,byte x,byte y,byte i,byte j,byte idx){//поиск границ окружения (полигона)
		ArrayList<Point> point=new ArrayList<Point>();//хранить точки, образующие границу окружения
		point.add(new Point(x,y));//добавить точку, от которой начинается поиск
		
		ArrayList<Point> pointDeadEnd=new ArrayList<Point>();//точки, входящие в тупик
		
		byte initX=x;//первая точка в окружении
		byte initY=y;
		byte dir=0;//направление прохода по окружению
		
		Point p=new Point();//выбираем соседнюю точку исходя из направления
		
		byte iterationsCount=0;
		byte preX=x;
		byte preY=y;
		
		for(;;){
			for(int k=0;k<Integer.MAX_VALUE;k++){//ищем следующую точку по всем направлениям
				
				dir++;//меняем направление
				if(dir>7)dir-=8;//корректируем направление
				if(dir<0)dir+=8;
				
				//выбираем соседнюю точку исходя из направления
				if(dir==0){p.x=x+1;p.y=y;}
				else if(dir==1){p.x=x+1;p.y=y+1;}
				else if(dir==2){p.x=x;p.y=y+1;}
				else if(dir==3){p.x=x-1;p.y=y+1;}
				else if(dir==4){p.x=x-1;p.y=y;}
				else if(dir==5){p.x=x-1;p.y=y-1;}
				else if(dir==6){p.x=x;p.y=y-1;}
				else{p.x=x+1;p.y=y-1;}

				//против одного из видов тупиков	
				if(preX==x&preY==y)iterationsCount++;
				else iterationsCount=0;
				preX=x;
				preY=y;
				if(iterationsCount>7){	
					fieldForBuffer[x][y]=idx;
					point.removeAll(point);
					point.add(new Point(initX,initY));//добавить точку, от которой начинается поиск
					x=initX;
					y=initY;
					continue;
				}
				
				//дошли до исходной точки, значит полигон найден
				if(p.x==initX&&p.y==initY){
					if(point.size()>3){
						if(point.get(point.size()-2).x==point.get(point.size()-1).x&&point.get(point.size()-2).y==point.get(point.size()-1).y){
							//удалить одну из разновидностей тупика
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
						}else if(point.size()==4){//удалить другой вид тупика
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
						break;//найден полный полигон
					}else{
						dir++;
						continue;
					}
				}
				if(p.x<0||p.y<0||p.x>sizeX-1||p.y>sizeY-1)continue;//точка выходит за край поля - не рассматриваем ее
				if(gameField[p.x][p.y]==type&fieldForBuffer[p.x][p.y]!=idx){//если точка того же типа, что и полигон
					boolean isChainDot=false;//если точка граничит с содержимым внутри окружения, значит она является его границей
					try{if(fieldForBuffer[p.x+1][p.y]==idx)isChainDot=true;}catch(Exception e){}
					try{if(fieldForBuffer[p.x-1][p.y]==idx)isChainDot=true;}catch(Exception e){}
					try{if(fieldForBuffer[p.x][p.y+1]==idx)isChainDot=true;}catch(Exception e){}
					try{if(fieldForBuffer[p.x][p.y-1]==idx)isChainDot=true;}catch(Exception e){}					
					if(!isChainDot)continue;
					
					if(pointDeadEnd.size()>1){//выход из тупика
						if(p.x==pointDeadEnd.get(1).x&&p.y==pointDeadEnd.get(1).y){
							
							boolean isRemoved=false;
							for(int n=0;n<point.size();n++){
								if(pointDeadEnd.get(1).x==point.get(n).x&&pointDeadEnd.get(1).y==point.get(n).y){									
									for(int n1=point.size()-1;n1>=n;n1--){//удаление из тупика
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
								pointDeadEnd.removeAll(pointDeadEnd);//удаление рассмотренных точек тупика
								dir-=4;
								continue;
							}
						}
					}
					
					//подозрение на тупик
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
							point.add(new Point(initX,initY));//добавить точку, от которой начинается поиск
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
							point.add(new Point(initX,initY));//добавить точку, от которой начинается поиск
							continue;
						}}catch(Exception e){}
						
						if(pointDeadEnd.size()>0){//удаление предыдущего тупика при нахождении нового							
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
							point.add(new Point(initX,initY));//добавить точку, от которой начинается поиск
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
							point.add(new Point(initX,initY));//добавить точку, от которой начинается поиск
							continue;
						}}catch(Exception e){}
						
						if(pointDeadEnd.size()>0){//удаление предыдущего тупика при нахождении нового							
							if(Math.abs(pointDeadEnd.get(pointDeadEnd.size()-1).x-p.x)<=1&&Math.abs(pointDeadEnd.get(pointDeadEnd.size()-1).y-p.y)<=1){}
							else{pointDeadEnd.removeAll(pointDeadEnd);}
						}
						pointDeadEnd.add(new Point(p.x,p.y));
					}}catch(Exception e){}
										
					if(isChainDot){//найдена ли следующая точка цепи окружения						
						point.add(new Point(p.x,p.y));//добавляем следующую точку
						x=(byte)p.x;
						y=(byte)p.y;
						dir-=4;
						if(point.size()>1000)return null;//пока неудачная обработка одного из видов тупиков
						continue;
					}
				}
			}
			
			if(point.size()<7){
				break;
			}else{					
				//поле для обработки тупиков из точек
				int[][] fieldForDeadEnd=new int[sizeX][sizeY];
				for(int k=0;k<point.size();k++){//отметить на поле точки, образующие границу полигона
					fieldForDeadEnd[point.get(k).x][point.get(k).y]=1;
				}
				
				for(int i1=0;i1<sizeX;i1++){
					for(int j1=0;j1<sizeY;j1++){//отметить на поле найденные ранее точки внутри найденного полигона
						if(fieldForBuffer[i1][j1]==idx)fieldForDeadEnd[i1][j1]=idx+3;
					}
				}
				
				int count=1;
				while(count>0){//отметить все точки внутри полигона, принадлежащие вложенным полигонам
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
				
				//все точки внутри полигона, не найденные ранее, сейчас отмечаются
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
		
		//создается и заполняется новый полигон
		Polygon polygon=new Polygon();
		polygon.xpoints=new int[point.size()];
		polygon.ypoints=new int[point.size()];
		for(int k=0;k<point.size();k++){
			polygon.xpoints[k]=point.get(k).x;
			polygon.ypoints[k]=point.get(k).y;
		}
		return polygon;
	}
	
	private boolean analyzeForEnclosure(byte idx,byte type,byte x,byte y){//поиск окружения
		int count=0;
		//поиск соседей начиная от точки x,y
		if(x!=-1){
			if(x<sizeX-1){//не на краю поля
				for(int k=x+1;k<sizeX;k++){
					if(gameField[k][y]<=0||gameField[k][y]==type||gameField[k][y]==getTranformedType(type)+2){
						fieldForBuffer[k][y]=idx;//отметить соседа, что он входит в то же окружение
						count++;
						if(k==sizeX-1)return false;//если сосед граничит с краем поля, значит окружение не создается
					}else break;
				}
			}
			if(y<sizeY-1){//не на краю поля
				for(int k=y+1;k<sizeY;k++){
					if(gameField[x][k]<=0||gameField[x][k]==type||gameField[x][k]==getTranformedType(type)+2){
						fieldForBuffer[x][k]=idx;//отметить соседа, что он входит в то же окружение
						count++;
						if(k==sizeY-1)return false;//если сосед граничит с краем поля, значит окружение не создается
					}else break;
				}
			}
			if(x>0){//не на краю поля
				for(int k=x-1;k>=0;k--){
					if(gameField[k][y]<=0||gameField[k][y]==type||gameField[k][y]==getTranformedType(type)+2){
						fieldForBuffer[k][y]=idx;//отметить соседа, что он входит в то же окружение
						count++;
						if(k==0)return false;//если сосед граничит с краем поля, значит окружение не создается
					}else break;
				}
			}
			if(y>0){//не на краю поля
				for(int k=y-1;k>=0;k--){
					if(gameField[x][k]<=0||gameField[x][k]==type||gameField[x][k]==getTranformedType(type)+2){
						fieldForBuffer[x][k]=idx;//отметить соседа, что он входит в то же окружение
						count++;
						if(k==0)return false;//если сосед граничит с краем поля, значит окружение не создается
					}else break;
				}
			}
		}
		while(count>0){//поиск пока не будут найдены все соседи
			count=0;
			for(byte i=0;i<sizeX;i++){
				for(byte j=0;j<sizeY;j++){
					if(fieldForBuffer[i][j]==idx){//поиск соседей для уже найденных точек окружения
						if(i<sizeX-1){//не на краю поля
							for(int k=i+1;k<sizeX;k++){
								if(fieldForBuffer[k][j]<idx){//если точка еще не была найдена для данного окружения
									if(fieldForBuffer[k][j]>1)return false;//неудача поиска окружения была найдена в предыдущих idx
									if(gameField[k][j]<=0||gameField[k][j]==type||gameField[k][j]==getTranformedType(type)+2){
										fieldForBuffer[k][j]=idx;//отметить соседа, что он входит в то же окружение
										count++;
										if(k==sizeX-1)return false;//если сосед граничит с краем поля, значит окружение не создается
									}else break;
								}else break;								
							}
						}
						if(j<sizeY-1){//не на краю поля
							for(int k=j+1;k<sizeY;k++){
								if(fieldForBuffer[i][k]<idx){//если точка еще не была найдена для данного окружения
									if(fieldForBuffer[i][k]>1)return false;//неудача поиска окружения была найдена в предыдущих idx
									if(gameField[i][k]<=0||gameField[i][k]==type||gameField[i][k]==getTranformedType(type)+2){
										fieldForBuffer[i][k]=idx;//отметить соседа, что он входит в то же окружение
										count++;
										if(k==sizeY-1)return false;//если сосед граничит с краем поля, значит окружение не создается
									}else break;
								}else break;
							}
						}
						if(i>0){//не на краю поля
							for(int k=i-1;k>=0;k--){
								if(fieldForBuffer[k][j]<idx){//если точка еще не была найдена для данного окружения
									if(fieldForBuffer[k][j]>1)return false;//неудача поиска окружения была найдена в предыдущих idx
									if(gameField[k][j]<=0||gameField[k][j]==type||gameField[k][j]==getTranformedType(type)+2){
										fieldForBuffer[k][j]=idx;//отметить соседа, что он входит в то же окружение
										count++;
										if(k==0)return false;//если сосед граничит с краем поля, значит окружение не создается
									}else break;
								}else break;
							}
						}
						if(j>0){//не на краю поля
							for(int k=j-1;k>=0;k--){
								if(fieldForBuffer[i][k]<idx){//если точка еще не была найдена для данного окружения
									if(fieldForBuffer[i][k]>1)return false;//неудача поиска окружения была найдена в предыдущих idx
									if(gameField[i][k]<=0||gameField[i][k]==type||gameField[i][k]==getTranformedType(type)+2){
										fieldForBuffer[i][k]=idx;//отметить соседа, что он входит в то же окружение
										count++;
										if(k==0)return false;//если сосед граничит с краем поля, значит окружение не создается
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
	
	private byte getTranformedType(byte type){//получить точку противоположного цвета
		if(type==BLUE)return RED;
		if(type==RED)return BLUE;
		return 0;
	}
	
	public boolean canAddMove(byte x,byte y){//можно ли сделать ход на поле
		try{
			if(gameField[x][y]==EMPTY||gameField[x][y]==EMPTY_IN_BLUE_HOUSE||gameField[x][y]==EMPTY_IN_RED_HOUSE)return true;
			else return false;
		}catch(Exception e){return false;}		
	}
		
	public byte[][] getFieldState(){//получить состояние поля
		byte[][]fieldState=new byte[sizeX][sizeY];
		for(byte x=0;x<sizeX;x++){
			for(byte y=0;y<sizeY;y++){
				fieldState[x][y]=toStringDotType(gameField[x][y]);
			}
		}
		return fieldState;
	}

	public byte[][] getTerrytoryState(){//получить состояние поля с учетом территории, т.е. точки внутри домика имеют цвет домика
		byte[][]fieldTerrytoryState=new byte[sizeX][sizeY];
		for(byte x=0;x<sizeX;x++){
			for(byte y=0;y<sizeY;y++){
				fieldTerrytoryState[x][y]=toTerrytoryStringDotType(gameField[x][y]);		
			}
		}
		return fieldTerrytoryState;
	}
	
	//получить в объект с игрой состояние игрового поля 
	public void setFieldState(Game game){
		for(byte x=0;x<sizeX;x++){
			for(byte y=0;y<sizeY;y++){
				game.fieldState[x][y]=toStringDotType(gameField[x][y]);
			}
		}
	}
	
	//получить в объект с игрой состояние игрового поля с учетом территории внутри домиков (пустые места внутри домиков получают цвет домика)
	public void setTerrytoryState(Game game){
		for(byte x=0;x<sizeX;x++){
			for(byte y=0;y<sizeY;y++){
				game.fieldTerrytoryState[x][y]=toTerrytoryStringDotType(gameField[x][y]);
			}
		}
	}
	
	//вернуть принадлежность точки на поле с учетом территории, т.к. пустые места внутри домиков получают цвет домика
	public byte toTerrytoryStringDotType(int type){//работает как обычный StringDotType, т.к. не различает пространство внутри домиков по цвету
		if(type==EMPTY)return Protocol.templateDotType_EMPTY;//empty
		if(type==BLUE||type==RED_IN_BLUE||type==EMPTY_IN_BLUE_HOUSE||type==EMPTY_IN_BLUE_ENCIRCLEMENT)return Protocol.templateDotType_BLUE;//blue
		if(type==RED||type==BLUE_IN_RED||type==EMPTY_IN_RED_HOUSE||type==EMPTY_IN_RED_ENCIRCLEMENT)return Protocol.templateDotType_RED;//red
		return (byte)type;
	}
	
	//вернуть принадлежность точки на поле
	public byte toStringDotType(int type){
		if(type==EMPTY||type==EMPTY_IN_BLUE_HOUSE||type==EMPTY_IN_RED_HOUSE)return Protocol.templateDotType_EMPTY;//empty
		if(type==BLUE||type==RED_IN_BLUE||type==EMPTY_IN_BLUE_ENCIRCLEMENT)return Protocol.templateDotType_BLUE;//blue
		if(type==RED||type==BLUE_IN_RED||type==EMPTY_IN_RED_ENCIRCLEMENT)return Protocol.templateDotType_RED;//red
		return (byte)type;
	}
}
