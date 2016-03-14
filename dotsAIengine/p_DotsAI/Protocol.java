package p_DotsAI;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import p_GUI.GameField;
import p_Statistics.MoveStatType;
import p_Statistics.StatisticsFrame;
import p_TemplateEngine.TemplateEngine;
import p_TemplateEngine.TemplateFieldSideType;
import p_TemplateEngine.TemplateType;
import p_TreeEngine.TreeApplicationInGame;

public class Protocol {
	
	public static String appName="DotsAI";//�������� ���������
	public static String appVersion="2.0";//������
	public static String appDate="12 ����� 2016";//����
	public static String appAuthor="copyright &#169; 2016 Pryimak Alexey";//�����
	
	public static byte maxSize=15;//������������ ������ �������
	
	public static byte templateDotType_ANY=0;//��� ����� ������� - ����� (�������� �����)
	public static byte templateDotType_LAND=1;//��� ����� ������� - ������� ���� (������ �������)
	public static byte templateDotType_EMPTY=2;//��� ����� ������� - ������ �����
	public static byte templateDotType_BLUE=3;//��� ����� ������� - ����� �����
	public static byte templateDotType_BLUE_or_EMPTY=4;//��� ����� ������� - ����� ��� ������ �����
	public static byte templateDotType_RED=5;//��� ����� ������� - ������� �����
	public static byte templateDotType_RED_or_EMPTY=6;//��� ����� ������� - ������� ��� ������
	
	Random rand=new Random();//��������� �������� �����
	
	public TemplateEngine templateEngine;//���� ��������
	public ArrayList<Game> games;//������ ���
	public StatisticsFrame stat;//���������� ����
	
	public Protocol(){
		//������������� ����������
		stat=new StatisticsFrame();
		templateEngine=new TemplateEngine();
		games=new ArrayList<Game>();
	}	
	
	//�������� ���� �� �� �������. ���� �������� � ������ � � ���� �������� ��� � ������ ��� �� ��� ��������� � ���� � ������� �������
	public Game getGame(int gameIdx) {
		for(int i=0;i<games.size();i++){
			if(games.get(i).gameIdx==gameIdx)return games.get(i);
		}
		return null;
	}
	
	//������� ���� �� �������. ���� ���������, ����� ��� �������������
	public void deleteGame(int gameIdx) {
		for(int i=0;i<games.size();i++){
			if(games.get(i).gameIdx==gameIdx)games.remove(i);
		}
	}
	
	//�������� ����� ���� � ������ ���
	public void addNewGame(int gameIdx,EnemyType enemyType1,byte sizeX,byte sizeY,int redX1,int redY1,int blueX1,int blueY1,int redX2,int redY2,int blueX2,int blueY2){
		games.add(new Game(gameIdx,enemyType1,sizeX,sizeY,redX1,redY1,blueX1,blueY1,redX2,redY2,blueX2,blueY2));
	}
	
	//����� Game ������ ��� ���������� �� ����
	public class Game{
		public int gameIdx;//������ ����
		public byte sizeX;//������ ���� ������ (X x Y)
		public byte sizeY;//������ ���� ������ (X x Y)
		public EnemyType enemyType;//��� ��������� - �������, ������ ��� ������ ��
		public GameField gameField;//������ �������� ����
		public Point lastAimove;//��������� ��� ��
		public byte[][] movesMas;//������ ����� � ����
		int movesCount;//����� ����� � ����
		
		public ArrayList<Point> BSSpoints;//������ �����, ������� �������� �������� ��������� �� � ��������� �������
		public ArrayList<TreeApplicationInGame> treeApplicationInGame;//������ ���������� �������� � ����
		boolean isCanGroung;//����� �� �� �����������
		public byte[][] fieldState;//��������� ����
		public byte[][] fieldTerrytoryState;//��������� ���� � ������ ���������� - ������ ����� � ������� ��������� ���� �����, ��� � �����
		
		//�� ������������ � ����, ���� ���������� - ��� �������� ���� "���������� �����"
		//boolean isUseGlobalAttackTemplate;//����� �� ��������� ���������. ������������ ������ ���� ��� � ����
		
		public Point moveAI;//��� �� � �������� ��� ������ - ���� ����� ������������ ������������� ���������� ������
		public int crossCount;//����� �������� � ����
		public int closestRedChainIdx;//���������� ���� ����� � ��
		public ArrayList<Chain> redChains;//������ ������� ����� ����� (����� ��)
		public ArrayList<Chain> blueChains;//������ ����� ����� ����� (����� ��������� ��)
		public String[][] fieldOfChains;//������ ���������� � �����
		boolean isMoveByBeginTemplate;//������� ���� ������ �� BeginTemplate �� ������� ���� �� ������� ���� �������
		private int lossLevel;//����� ������������ ���� ������ ��� ��, ����� �� ������
		
		//������ ������� ����� ���� �������. ��� ����� ����� � ������ �������, ��� �� ������� ������
		//������������ ��� ����������� �����, ����� ��� ��������� � ����� � ����������� ������ ����� ��������� ��
		public double fieldSpectrumRed[][];
		public double fieldSpectrumBlue[][];
		public double fieldSpectrumRedMax;
		public double fieldSpectrumBlueMax;
		
		//�������� ������� ����
		public Game(int gameIdx,EnemyType enemyType,byte sizeX,byte sizeY,
					int redX1,int redY1,int blueX1,int blueY1,int redX2,int redY2,int blueX2,int blueY2
				){
			stat.clearStat();//�������� ����������
			
			lossLevel=rand.nextInt(30)+17;//���������� ������� ����������� �����, ��� ������� �� �������
			
			//������ � ��������� ����
			this.sizeX=sizeX;
			this.sizeY=sizeY;
			fieldState=new byte[sizeX][sizeY];
			fieldTerrytoryState=new byte[sizeX][sizeY];
			
			//������� ����� � ������� �� ���������� �����
			fieldSpectrumRed=new double[sizeX][sizeY];
			fieldSpectrumBlue=new double[sizeX][sizeY];
			for(int i=0;i<sizeX;i++)for(int j=0;j<sizeY;j++){
				fieldSpectrumRed[i][j]=0;
				fieldSpectrumBlue[i][j]=0;
			}
			fieldSpectrumRedMax=0;
			fieldSpectrumBlueMax=0;
			
			//��������� ������ ���������
			this.gameIdx=gameIdx;
			closestRedChainIdx=-1;
			isMoveByBeginTemplate=true;			
			this.enemyType=enemyType;			
			moveAI=new Point();
			isCanGroung=false;			
			lastAimove=new Point(redX2,redY2);			
			BSSpoints=new ArrayList<Point>();
			treeApplicationInGame=new ArrayList<TreeApplicationInGame>();			
			movesCount=0;
			movesMas=new byte[sizeX*sizeY][3];
			gameField=new GameField(sizeX,sizeY);//������ �������� ����
			
			//�������� �� ������� ���� ����� ��������� �������
			gameField.addMove(new Point(redX1,redY1),GameField.RED);
			gameField.addMove(new Point(blueX1,blueY1), GameField.BLUE);
			gameField.addMove(new Point(redX2,redY2),GameField.RED);
			gameField.addMove(new Point(blueX2,blueY2), GameField.BLUE);
		}

		//������ ����� � ����
		public void addInMovesMas(Point p, int type){//red=0,blue=1		
			movesMas[movesCount][0]=(byte) p.x;
			movesMas[movesCount][1]=(byte) p.y;
			movesMas[movesCount][2]=(byte) type;
			movesCount++;
		}

		//������� ����� ����� � ����
		public int getMovesCount(){return movesCount;}
		
	}
	
	//��������� ��� ��������� ��
	public void addAIenemyMove(int gameIdx,int x,int y){
		Game game=getGame(gameIdx);
		if(game==null)return;
		game.addInMovesMas(new Point(x,y), GameField.BLUE);
		game.gameField.addMove(new Point(x,y), GameField.BLUE);
		if(game.movesCount%9==0){//������ ������ � ������ ��������� ������ 9 �����
			System.gc();
		}
	}
	
	//����� ��� ��
	public Point getAImove(int gameIdx,int level,Point recommendedMove){
		Game game=getGame(gameIdx);
		game.lastAimove=getAImovePoint(game,gameIdx,level,recommendedMove);//����� ���� ��
		game.addInMovesMas(game.lastAimove, GameField.RED);
		game.gameField.addMove(game.lastAimove, GameField.RED);
		return game.lastAimove;
	}
	
	//����� ��� ��. ��� ������ ��������� ��� �� �� ������ �� ��������, ���������� ����� �� ������ ���� ������������
	Point getAImovePoint(Game game,int gameIdx,int level,Point recommendedMove){
		
		//����� ���� �� blue surround security
		if(isMoveBlueSurroundSecurity(game)){stat.moveStatMas.addMoveStat(MoveStatType.BLUE_SURROUND_SECURITY);return game.moveAI;}
		
		game.gameField.setFieldState(game);//�������� ��������� ����
		
		//����� ���� �� ������ ����� ������������� � ���� ��������
		if(isMoveByTree(game,recommendedMove)){return game.moveAI;}		
				
		chainsSearch(game);//����� ����� � ����
		
		if(isEndGame(game,GameField.RED)){//�������� �� ��������� ���� - ���������� �� ��
			if(isMoveByGround(game,recommendedMove))return game.moveAI;//������� ��� �� �������� ����������, ���� ��� ��������
			else{
				game.closestRedChainIdx=-1;
				return new Point(-2,-2);//�� ����������, �� ����������� ����
			}
		}
		if(isEndGame(game,GameField.BLUE)){//�������� �� ��������� ���� - ���������� �� �������� ��
			game.closestRedChainIdx=-1;
			return new Point(-3,-3);//������� ����������, �� �������
		}
		if(game.gameField.scoreBlue-game.gameField.scoreRed>game.lossLevel){//�������� �� ��������� ���� - �� �������, ���� ������� ����� �����
			game.closestRedChainIdx=-1;
			return new Point(-4,-4);//�� �������, �.�. ������� ����� �����
		}
						
		//����� ���� �� ���������� ���� ������� (��� BEGIN)
		if(game.isMoveByBeginTemplate){//������ �� �� ������� ���� �������
			if(templateEngine.isMoveIfEqualsLikeAreaByPoint(this,game,TemplateType.templateTypeBEGIN,game.gameField.lastDot,game.fieldState,recommendedMove)){
				return game.moveAI;//��� �� ���������� ������� ������
			}else{//����� ����, ��� ������ ������� �� ������ ��� �� ���������� �������, ������ �� ��������� �������� ������ �� ����������
				game.isMoveByBeginTemplate=false;
			}
		}
		
		//����� ���� �� �������� �������, ���� ����� ��������� �� ���������� ������ ���� ����		
		try{if(TemplateFieldSideType.getFieldSideType(game,game.gameField.lastDot)!=TemplateFieldSideType.templateFieldSideTypeINSIDE){
			if(templateEngine.isMoveIfEqualsLikeAreaByPoint(this,game,TemplateType.templateTypeSQUARE_SIDE,game.gameField.lastDot,game.fieldState,recommendedMove))return game.moveAI;
		}}catch(Exception e){}

		//����� �� ������� ��������, ������� �������� ����� �� ������������ ���������� ���� ��������� �� 
		try{if(templateEngine.isMoveIfEqualsLikeAreaByPoint(this,game,TemplateType.templateTypeSQUARE,game.gameField.lastDot,game.fieldState,recommendedMove))return game.moveAI;}catch(Exception e){}
		
		//������� ���������� ����� ���������
		//����� ��� ���� ������ ����� ����� ��� �� GLOBAL_ATTACK �������
		/*try{if(game.isUseGlobalAttackTemplate&&game.getMovesCount()>15){
			ArrayList<Point> pBlueWallEnds = new ArrayList<Point>();
			for(int i=0;i<game.wallBlue.size();i++){
				pBlueWallEnds.add(new Point(game.wallBlue.get(i).wallEndX,game.wallBlue.get(i).wallEndY));
			}
			if(templateEngine.getPointIfEqualsLikeAreaByPointList(this,game,TemplateType.templateTypeGLOBAL_ATTACK,pBlueWallEnds,game.fieldState).x!=-1){
				game.isUseGlobalAttackTemplate=false;
				return game.moveAI;
			}
		}}catch(Exception e){}*/
			
		//<������ �������>
		if(!game.isCanGroung){
			for(int i=0;i<game.sizeX;i++){for(int j=0;j<game.sizeY;j++){//��������� �������
				game.fieldSpectrumRed[i][j]=0;
				game.fieldSpectrumBlue[i][j]=0;
			}}
			int depth=5;//������� ������� ����� �� ���������� ������������
			for(int i=0;i<game.sizeX;i++){for(int j=0;j<game.sizeY;j++){
				if(game.fieldState[i][j]==templateDotType_RED){//��������� ������� ������� �����
					for(int i1=-depth;i1<=depth;i1++){
						for(int j1=-depth;j1<=depth;j1++){
							if(i1==0&j1==0)continue;
							try{game.fieldSpectrumRed[i+i1][j+j1]+=1.0/Math.sqrt(i1*i1+j1*j1);}catch(Exception e){continue;}
						}
					}
				}else if(game.fieldState[i][j]==templateDotType_BLUE){//��������� ������� ����� �����
					for(int i1=-depth;i1<=depth;i1++){
						for(int j1=-depth;j1<=depth;j1++){
							if(i1==0&j1==0)continue;
							try{game.fieldSpectrumBlue[i+i1][j+j1]+=1.0/Math.sqrt(i1*i1+j1*j1);}catch(Exception e){continue;}
						}
					}
				}		
			}}			

			//���������, ����� � ������ ����� ������� � ����� ���� �������� ��������� �������
			game.fieldSpectrumRedMax=0;
			game.fieldSpectrumBlueMax=0;
			for(int i=0;i<game.sizeX;i++){for(int j=0;j<game.sizeY;j++){
				if(game.fieldState[i][j]==templateDotType_BLUE){game.fieldSpectrumBlue[i][j]=Integer.MAX_VALUE;continue;}
				if(game.fieldState[i][j]==templateDotType_RED){game.fieldSpectrumRed[i][j]=Integer.MAX_VALUE;continue;}
				if(game.fieldSpectrumBlue[i][j]>game.fieldSpectrumBlueMax)game.fieldSpectrumBlueMax=game.fieldSpectrumBlue[i][j];
				if(game.fieldSpectrumRed[i][j]>game.fieldSpectrumRedMax)game.fieldSpectrumRedMax=game.fieldSpectrumRed[i][j];
			}}
		}
		
		//������ ���� ��� ���� ����� ������� ������� ���� � ���������� ���� ��������� ��, ����� ����� ��� ���� ������ ������� �����
		game.closestRedChainIdx=getSmallerRedChainIdx(game);//����� ������ ����� ������� ���� �� � ���������� ������ ����
		
		//���� ����� ������� ���� �� ������� (�.�. ��� ���� �������� ���� ����), ����� �����������
		if(game.closestRedChainIdx==-1){
			game.isCanGroung=true;
		}else{
			game.isCanGroung=false;
		}
		
		//���� ��� ������ �����������, ����� ���� ���� ��� ����� ����� ������� ���� �� � ���������� ������ ����
		if(!game.isCanGroung){
			if(isMoveByRedOriented(game,game.redChains.get(game.closestRedChainIdx),level,recommendedMove))return game.moveAI;
		}
		//����� ���� ��� ���� ��������� ����� ��
		for(int i=0;i<game.redChains.size();i++){
			if(!game.isCanGroung){//�� ������ ��� ����� ������� ����
				if(i==game.closestRedChainIdx){
					continue;
				}
			}
			if(isMoveByRedOriented(game,game.redChains.get(i),level,recommendedMove)){//������ ��� ��� ���� ����� ����� �� ����� ��
				return game.moveAI;
			}
		}
			
		//����� ���� �� ����������
		if(isMoveByGround(game,recommendedMove))return game.moveAI;
		
		//�� �� ������ ������� ��� �� ������, ������ ��� �� �������� ��������
		game.gameField.setTerrytoryState(game);//����� �� ������ � ������ ��������� ����������� ���� � ������ �������������� ����������
		game.moveAI.x=game.lastAimove.x;game.moveAI.y=game.lastAimove.y;	
		while(!game.gameField.canAddMove((byte)game.moveAI.x,(byte)game.moveAI.y)||game.fieldTerrytoryState[game.moveAI.x][game.moveAI.y]!=templateDotType_EMPTY){
			game.moveAI.x=rand.nextInt(game.sizeX);
			game.moveAI.y=rand.nextInt(game.sizeY);			
		}
		stat.moveStatMas.addMoveStat(MoveStatType.RANDOM);
		return game.moveAI;
	}

	//����� ���� �� ������������ ���� ����� � ����� �� ����� ��
	private boolean isMoveByRedOriented(Game game,Chain chain,int level,Point recommendedMove){
		
		//����� ���� �� ������� ������� ���� ��������� (����� ������ ��� ����� ����� ����)
		try{if(templateEngine.isMoveIfEqualsLikeAreaByPoint(this,game,TemplateType.templateTypeWALL_DESTROY,new Point(chain.chainEndX,chain.chainEndY),game.fieldState,recommendedMove))return true;}catch(Exception e){}
		
		//����� �� ������� ����������� ����� �����, ����� ��������� ��������� ������ (����� ������ ��� ����� ����� ����)
		try{if(templateEngine.isMoveIfEqualsLikeAreaByPoint(this,game,TemplateType.templateTypeFINAL_RED_ATTACK,new Point(chain.chainEndX,chain.chainEndY),game.fieldState,recommendedMove))return true;}catch(Exception e){}
		
		//����� �� �������� �������, ����������� � ������������ ����� ����� ��� ���� ��������� ����� ���� 
		try{if(templateEngine.isMoveIfEqualsLikeAreaByPointList(this,game,TemplateType.templateTypeWALL_DESTROY,chain.points,game.fieldState,recommendedMove))return true;}catch(Exception e){}
		try{if(templateEngine.isMoveIfEqualsLikeAreaByPointList(this,game,TemplateType.templateTypeFINAL_RED_ATTACK,chain.points,game.fieldState,recommendedMove))return true;}catch(Exception e){}
		try{if(templateEngine.isMoveIfEqualsLikeAreaByPointList(this,game,TemplateType.templateTypeCONTINUED_RED_ATTACK,chain.points,game.fieldState,recommendedMove))return true;}catch(Exception e){}
		
		//����� ������������ ���� ��
		if(game.movesCount>20){//����������� ���� ��������, ���� � ���� ������� ������ 20 �����
			//����� ������������ ���� ���� ��� ����� �� ������ ����� �������� ����������� ����� ��� ������ ����
			for(int i=chain.abstractPoints.size()-1;i>=0;i--){//������ ��� � ��������� ����������� �����
				try{if(templateEngine.isMoveIfEqualsLikeAreaByPoint(this,game,TemplateType.templateTypeABSTRACT_ATTACK_WALL,chain.abstractPoints.get(i),game.fieldState,recommendedMove)){
					chain.abstractPoints.add(new Point(game.moveAI.x,game.moveAI.y));//��������� ����������� ��� ����������� � ������ ����������� �����
					return true;
				}}catch(Exception e){}
			}
			//����� ������������ ���� ���� ��� ����� ���� ��
			for(int i=chain.points.size();i>=0;i--){//������ ��� � ����� ����
				try{if(templateEngine.isMoveIfEqualsLikeAreaByPoint(this,game,TemplateType.templateTypeABSTRACT_ATTACK_WALL,chain.points.get(i),game.fieldState,recommendedMove)){
					chain.abstractPoints.add(new Point(game.moveAI.x,game.moveAI.y));//��������� ����������� ��� ����������� � ������ ����������� �����
					return true;
				}}catch(Exception e){}
			}
		}	
		
		//����� ���� �� �������� �����
		if(!chain.isAtSide(game)){//���� ���� �� �� ���� ����
			//� ������ ��������� ������� ��������� �� ������ ����� ������������, ���� ����� � ����������� ���
			//��� ������ ������ ������� ���������, ��� ������ �������� ����� �������������� ��� ������ ���� �� �������� ��� �����,
			//� ������ ��� ���� ����� ����������� ����. ���� ��� ������ �� ��� ���������, �.�. ��������� ������� ����������� �����s
			double koef=1.0+(10-level)/10.0;//���������� ������� ���������, ��� level=[0;10], ��� ����, ��� �������		
			for(double s=0;s<=game.fieldSpectrumBlueMax;s+=game.fieldSpectrumBlueMax/10.0){
				for(int i=0;i<chain.points.size()/koef;i++){//������ ��� � ����� ����
					if(game.fieldSpectrumBlue[chain.points.get(i).x][chain.points.get(i).y]<s
							||
						game.fieldSpectrumBlue[chain.points.get(i).x][chain.points.get(i).y]>s+game.fieldSpectrumBlueMax/10.0
					){
						continue;
					}
					try{if(isMoveByChain(game,chain.points.get(i).x,chain.points.get(i).y,recommendedMove)){//������ ��� �� ����
						return true;
					}}catch(Exception e){}
				}
			}
		}		
		
		return false;
	}

	//����� ���� �� �������� ����������
	private boolean isMoveByGround(Game game,Point recommendedMove){
		//����� �� �������� ���������� �� ��� ���� ����
		for(int i=0;i<game.redChains.size();i++){//�������� ��� ������� ���� � ���������� �� ����� ���� ���� ������ ����� � ����
			if(templateEngine.isMoveIfEqualsLikeAreaByPointList(this,game,TemplateType.templateTypeGROUND,game.redChains.get(i).points,game.fieldState,recommendedMove))return true;
		}
		//����� �� �������� ���������� ��� ���� ����
		for(int i=0;i<game.redChains.size();i++){//�������� ��� ����� ������� �����
			for(int j=0;j<game.redChains.get(i).points.size();j++){
				int x=game.redChains.get(i).points.get(j).x;
				int y=game.redChains.get(i).points.get(j).y;
				if(x<7||x>game.sizeX-8||y<7||y>game.sizeY-8){
					if(templateEngine.isMoveIfEqualsLikeAreaByPoint(this,game,TemplateType.templateTypeGROUND_SIDE,new Point(x,y),game.fieldState,recommendedMove))return true;
				}
			}
		}
		return false;
	}

	//����� ���� �� ��� ������ �� ��������� ��������� �� �� ��������� ��
	private boolean isMoveBlueSurroundSecurity(Game game){	
		for(int i=0;i<game.BSSpoints.size();i++){//��� ������ ����� �� ������ ����������� ����� ���������
			GameField e=game.gameField.clone();//��������� ����
			e.addMove(game.BSSpoints.get(i), GameField.BLUE);//������ ��� ����� ������ (������ ��������� ��)
			if(e.blueEnclosures.size()>game.gameField.blueEnclosures.size()){//���� ���� ��� ����� � ��������� ����� ��
				game.moveAI=game.BSSpoints.get(i);//�� �� ������ ���� ��� ��� ���������� ��������� ����� �����
				return true;
			}
		}
		return false;
	}

	//�������������� �������� �� ����������� ���� �� ����� ��� ����������
	public boolean isAICanMakeMove(byte x,byte y,Game game,int templateType){
		if(game.gameField.canAddMove(x, y)){
			//�������� ����������� �������� ��� �� ����� ������������ ������������� ����������
			Point p1=getMaxTerrSecurityMove(game, x, y);//�������� ���������� ��� ��
			if(p1.x!=x||p1.y!=y){
				game.moveAI=p1;
				stat.moveStatMas.addMoveStat(MoveStatType.EXPRESS_MAX_TERR_SECURITY);
				return true;
			}
			
			//move by express surround security 1
			/*if(templateType!=-1&templateType!=5){//��� �������� (templateType=-1) and BST �� ������ �� SurSec1
				if(Math.abs(x-game.gameField.lastDot.x)<2&&Math.abs(y-game.gameField.lastDot.y)<2){
					Point p=getExpressSurroundSecurity1Move(game, new Point(x,y));
					if(p.x!=x||p.y!=y){
						game.moveAI=p;
						stat.moveStatMas.addMoveStat(MoveStatType.EXPRESS_SURROUND_SECURITY_1,false);
						return true;
					}
				}
			}*/
			
			game.moveAI.x=x;
			game.moveAI.y=y;
			return true;
		}else return false;			
	}

	//����� ���� �� ������ ��������, ����� ����������� � ����
	private boolean isMoveByTree(Game game,Point recommendedMove){	
		for(int i=game.treeApplicationInGame.size()-1;i>=0;i--){
			TreeApplicationInGame ta=game.treeApplicationInGame.get(i);//���������� ������ � ���������� ����� ����
			
			//���� ��� ������ �������� ����� �� ������, �� ����� ���� �� ������ �� default ������ ����
			boolean isMoveOnlyByDefault=false;
			if(Math.abs(ta.center.x-game.gameField.lastDot.x)>7||Math.abs(ta.center.y-game.gameField.lastDot.y)>7){
				isMoveOnlyByDefault=true;
			}
			
			try{
				//��� ���������� ������������ ������ �� ������������ ����� ��������,
				//�������� ����������� �� ������������ ������� �������� �������, ������� �� ���������.
				//�.�. ������ ����������� �������� ������ ��� ����������� ������� �� ����� ����,
				//�� ��������� ���� ����� ��� ���������� � ���� ����� ����, 
				//� ������ ������ ��������� �� ������, ����� ������ �� ���� �� ������ ���
				if(i!=game.treeApplicationInGame.size()-1){
					if(!ta.templateView.isEquals(game,game.gameField.lastDot,game.fieldState,TemplateType.isSide(ta.templateType),TemplateFieldSideType.getFieldSideType(game, game.gameField.lastDot))){
						game.treeApplicationInGame.remove(i);
						continue;
					}
				}
				
				//����� ���� �� ������
				if(ta.isExistsLevelMove(game,game.gameField.lastDot,isMoveOnlyByDefault,recommendedMove)){
					
					//�������� ��������, ����� ���� ���������, � ������ �� ������� ������� ����������.
					//������ ��� �������� ����������� ������ ����� �� ��������� ������� ������ � �������.
					//����� ��� �� ����� ������ ��������. ������ ��� ������� � �������������� ������, ���� ��������
					/*
					try{if((x<7&&y>5&&y<game.sizeY-5)||(x>game.sizeX-6&&y>5&&y<game.sizeY-5)||(y<7&&x>5&&x<game.sizeX-5)||(y>game.sizeY-6&&x>5&&x<game.sizeX-5)){
						protocol.templateEngine.getPointIfEqualsLikeArea(protocol,game,TemplateType.SQUARE_SIDE,p,true);
					}}catch(Exception e){}
					try{protocol.templateEngine.getPointIfEqualsLikeArea(protocol,game,TemplateType.BLUE_SURROUND,p,true);}catch(Exception e){}
					try{protocol.templateEngine.getPointIfEqualsLikeArea(protocol,game,TemplateType.RED_SURROUND,p,true);}catch(Exception e){}
					try{protocol.templateEngine.getPointIfEqualsLikeArea(protocol,game,TemplateType.SQUARE,p,true);}catch(Exception e){}
					try{protocol.templateEngine.getPointIfEqualsLikeArea(protocol,game,TemplateType.WALL_DESTROY,getRedPointsForTemplateSearch(game,x,y),true);}catch(Exception e){}
					try{protocol.templateEngine.getPointIfEqualsLikeArea(protocol,game,TemplateType.RED_ATTACK,getRedPointsForTemplateSearch(game,x,y),true);}catch(Exception e){}
					*/
					
					//��� �� �� ������ ������
					game.moveAI=ta.transformAIPoint;
					if(isAICanMakeMove((byte)game.moveAI.x,(byte)game.moveAI.y,game,-1)){
						//���� ��� �� ��������, �� � ��������� �������� ���������� ������, �� ������ �������� ��� ������ ���
						if(templateEngine.foundedNumber!=0){
							templateEngine.foundedNumber=templateEngine.getBaseIndexByTemplateID(ta.tree.templateID);
							templateEngine.foundedIndex=ta.templateID;
							templateEngine.foundedTemplateType=ta.templateType;
						}
						stat.moveStatMas.addMoveStat(TemplateType.getMoveStatType(ta.templateType));
						return true;				
					}
					
					game.treeApplicationInGame.remove(i);//���� ��� ����������, �� ������ ��������� �� ������ ��������������� � ����������
					
				}
			}catch(Exception e){return false;}//��������� ����������, ���� ������ �������� �� ��������� ����	
		}
		return false;
	}

	//����� ���� �� �������� ��� ����� - ���� �������� WALL � ��� ���� ���� WALL_SIDE
	private boolean isMoveByChain(Game game,int x,int y,Point recommendedMove){
		//���� ����� (x,y), ��� ������� ������ ������, ��������� ����� ������, �� ������������ WALL_SIDE, �.�. ������� WALL � ���� ����� ������ ������������� ����
		if(x<7||x>game.sizeX-8||y<7||y>game.sizeY-8){
			if(templateEngine.isMoveIfEqualsLikeAreaByPoint(this,game,TemplateType.templateTypeWALL_SIDE,new Point(x,y),game.fieldState,recommendedMove))return true;
			if(x>3&y>3&x<game.sizeX-4&y<game.sizeY-4){
				if(templateEngine.isMoveIfEqualsLikeAreaByPoint(this,game,TemplateType.templateTypeWALL,new Point(x,y),game.fieldState,recommendedMove))return true;
			}		
		}else{
			if(templateEngine.isMoveIfEqualsLikeAreaByPoint(this,game,TemplateType.templateTypeWALL,new Point(x,y),game.fieldState,recommendedMove))return true;
		}	
		return false;
	}
	
	//����� ���������� ��� ��, ������� ������������� ����������� ����������
	public Point getMaxTerrSecurityMove(Game game,byte nextRedX,byte nextRedY){
		int sign=templateDotType_RED;//������ ������ ��� ������� �����
		
		int scoreMax=game.gameField.scoreRed-game.gameField.scoreBlue;//������� ������� � ����� ����
		Point AImove=new Point(nextRedX,nextRedY);
		int moveMax=0;
		
		for(int x=0;x<game.sizeX;x++){
			for(int y=0;y<game.sizeY;y++){
				if(!isCanMoveForSurroundSecurity(game,x,y,sign)){
					continue;
				}
				if(x==nextRedX&&y==nextRedY)continue;
				GameField singleGameEngine = game.gameField.clone();//������������ ����
	
				singleGameEngine.addMove(new Point(x,y),GameField.RED);
				if(!singleGameEngine.canAddMove(nextRedX,nextRedY)){
					int move=0;
					byte[][] field=singleGameEngine.getTerrytoryState();
					for(int i1=0;i1<game.sizeX;i1++){
						for(int j=0;j<game.sizeY;j++){
							if(field[i1][j]==sign){
								move++;
							}
						}
					}				
					if(scoreMax==(singleGameEngine.scoreRed-singleGameEngine.scoreBlue)){
						if(move>moveMax){
							AImove=new Point(x,y);
							moveMax=move;
						}
					}else{
						scoreMax=singleGameEngine.scoreRed-singleGameEngine.scoreBlue;
						AImove=new Point(x,y);
						moveMax=move;
					}
				}
			}
		}
		
		return AImove;
		
	}

	//��������, �������� �� �������� ��������� ������������ ������ �����.
	//���� ����� ����� ������ ���� ������� ������ �����, �� ��������� ����������
	private boolean isCanMoveForSurroundSecurity(Game game,int x,int y,int sign){	
		if(game.fieldState[x][y]!=templateDotType_EMPTY)return false;
		
		//���� ���� ���� ����� ������ ������� �����, �� ��������� ����� ����������
		int move=0;
		try{if(game.fieldState[x-1][y-1]==sign)move++;}catch(Exception e){}
		try{if(game.fieldState[x][y-1]==sign)move++;}catch(Exception e){}
		try{if(game.fieldState[x+1][y-1]==sign)move++;}catch(Exception e){}
		try{if(game.fieldState[x-1][y]==sign)move++;}catch(Exception e){}
		try{if(game.fieldState[x+1][y]==sign)move++;}catch(Exception e){}
		try{if(game.fieldState[x-1][y+1]==sign)move++;}catch(Exception e){}
		try{if(game.fieldState[x][y+1]==sign)move++;}catch(Exception e){}
		try{if(game.fieldState[x+1][y+1]==sign)move++;}catch(Exception e){}	
		if(move<2)return false;					
		
		return true;
	}

	//�������� �� ��, ����������� �� ����
	public boolean isEndGame(Game game,int moveWin){	
		
		game.gameField.setTerrytoryState(game);
		int scoreWin=0;
		int empty=0;
		int scoreLos=0;
		
		if(moveWin==GameField.BLUE){
			scoreWin=game.gameField.scoreBlue;
		}
		if(moveWin==GameField.RED){
			scoreWin=game.gameField.scoreRed;
		}
		                                                   
		GameField gameField=game.gameField.clone();//������������ ����
		
		for(int x=0;x<game.sizeX;x++){
			for(int y=0;y<game.sizeY;y++){		
				if(game.fieldTerrytoryState[x][y]!=templateDotType_EMPTY){
					continue;		
				}
				if(moveWin==GameField.BLUE){
					gameField.addMove(new Point(x,y),GameField.RED);
				}
				if(moveWin==GameField.RED){
					gameField.addMove(new Point(x,y),GameField.BLUE);
				}
			}
		}
		
		if(scoreWin<=getScoreForSurroundSecurity(gameField,moveWin)){return false;}
		
		for(;;){
			empty=0;
			byte[][] field=gameField.getFieldState();
			scoreLos=getScoreForSurroundSecurity(gameField,moveWin);
			for(int x=0;x<game.sizeX;x++){
				for(int y=0;y<game.sizeY;y++){
					if(empty>0){
						break;
					}
					if(field[x][y]==templateDotType_EMPTY){
						empty++;
						
						if(moveWin==GameField.BLUE){
							gameField.addMove(new Point(x,y),GameField.RED);
						}
						if(moveWin==GameField.RED){
							gameField.addMove(new Point(x,y),GameField.BLUE);
						}
						
						if(scoreWin<=getScoreForSurroundSecurity(gameField,moveWin)){
							return false;
						}
					}		
				}
			}		
			if(moveWin==GameField.BLUE&&scoreLos==getScoreForSurroundSecurity(gameField,moveWin)&&scoreWin>getScoreForSurroundSecurity(gameField,moveWin)){
				field=gameField.getFieldState();
				for(int x=0;x<game.sizeX;x++){
					for(int y=0;y<game.sizeY;y++){		
						if(field[x][y]!=templateDotType_EMPTY){
							continue;
						}
						gameField.addMove(new Point(x,y),GameField.RED);
					}
				}				
				break;
			}
			if(moveWin==GameField.RED&&scoreLos==getScoreForSurroundSecurity(gameField,moveWin)&&scoreWin>getScoreForSurroundSecurity(gameField,moveWin)){
				field=gameField.getFieldState();
				for(int x=0;x<game.sizeX;x++){
					for(int y=0;y<game.sizeY;y++){		
						if(field[x][y]!=templateDotType_EMPTY){
							continue;
						}
						gameField.addMove(new Point(x,y),GameField.BLUE);
					}
				}				
				break;
			}
			if(empty==0){break;}
		}
		
		if(scoreWin>getScoreForSurroundSecurity(gameField,moveWin)){
			return true;
		}
		return false;		
	}

	//��������������� ������� ��� �������� �� ��������� ����
	private int getScoreForSurroundSecurity(GameField singleGameEngine,int moveType){
		if(moveType==GameField.BLUE){
			return singleGameEngine.scoreRed;
		}else{
			return singleGameEngine.scoreBlue;
		}
	}
	
	//����� �����
	public void chainsSearch(Game game){
	
		//������ � ��������� �����
		game.fieldOfChains=new String[game.sizeX][game.sizeY];
		for(int i=0;i<game.sizeX;i++){//������ ������� ����������� ������� ������� N
			for(int j=0;j<game.sizeY;j++){
				game.fieldOfChains[i][j]="N";
			}
		}
		
		//������ ������� � ����� �����
		game.redChains=new ArrayList<Chain>();
		game.blueChains=new ArrayList<Chain>();
		game.crossCount=0;//����� �������� � ����
		
		//����� �������� � ���������� ����� 
		//(��� ������ ���� ���������� ����� - ������ ������ ������� ���� � ����� ����� ����, ����� �� ������������ ����� ������ �����)
		for(int i=0;i<game.sizeX-1;i++){
			for(int j=0;j<game.sizeY-1;j++){
				if(game.fieldState[i][j]==templateDotType_RED&&game.fieldState[i+1][j+1]==templateDotType_RED&&game.fieldState[i+1][j]==templateDotType_BLUE&&game.fieldState[i][j+1]==templateDotType_BLUE){			
					//������ ������, ��������� ����
					game.crossCount++;			
					addNewWallPoints(game,i, j,"R");
					addNewWallPoints(game,i+1, j,"B");			
					addNewWallPoints(game,i+1, j+1,"R");
					addNewWallPoints(game,i, j+1,"B");			
				}else if((game.fieldState[i][j]==templateDotType_BLUE&&game.fieldState[i+1][j+1]==templateDotType_BLUE&&game.fieldState[i+1][j]==templateDotType_RED&&game.fieldState[i][j+1]==templateDotType_RED)){
					//������ ������, ��������� ����
					game.crossCount++;
					addNewWallPoints(game,i+1, j,"R");
					addNewWallPoints(game,i, j,"B");
					addNewWallPoints(game,i, j+1,"R");
					addNewWallPoints(game,i+1, j+1,"B");
				}
			}
		}
		
		//���������� �����, ���� ��� ��������
		if(game.crossCount==0){
			Point point=getCentralPointForChainSearch(game,templateDotType_RED);
			if(point!=null)addNewWallPoints(game,point.x, point.y,"R");
			point=getCentralPointForChainSearch(game,templateDotType_BLUE);
			if(point!=null)addNewWallPoints(game,point.x, point.y,"B");	
		}
		
		//������ ������� ����� ��� ���� � �� ���������� � ������
		//(��� ������ ���� ������ ����� ����� - ������ ����� ���������� �����, ��� �� ������ �����)
		for(int i=0;i<game.blueChains.size();i++){
			searchChainPoints2(game,game.blueChains.get(i),templateDotType_BLUE);
		}
		for(int i=0;i<game.redChains.size();i++){
			searchChainPoints2(game,game.redChains.get(i),templateDotType_RED);
		}
		
		//������ ����������� ����� �������� ���, ������ ��� ������������� ����������� ��� ���������� ����� �� ����������� ��������
		//������ ���������� (abstract) blue �����
		/*for(int i=0;i<game.sizeX;i++)for(int j=0;j<game.sizeY;j++){
			if(game.fieldOfWalls[i][j]=="N"&&game.fieldState[i][j]==templateDotTypeBLUE){
				for(int l=3;l<20;l++){	
					if(!game.fieldOfWalls[i][j].equals("N")){
						//System.out.println("abstract point added; koord="+(i+1)+";"+(j+1));
						break;
					}
					for(int c=i-l;c<=i+l;c++){
						try{if(game.fieldOfWalls.get(c*game.sizeY+j-l).startsWith("PB")||game.fieldOfWalls.get(c*game.sizeY+j-l).startsWith("AB")){
							int wallIndex=new Integer(game.fieldOfWalls.get(c*game.sizeY+j-l).substring(2)).intValue();
							if(isRelatedForWallSearch(game,i,j,c,j-l,game.wallRed.get(wallIndex))){
								game.wallBlue.get(wallIndex).addAbstract(game,i+1, j+1, game.fieldOfWalls);
								break;
							}
						}}catch(Exception e){}
						try{if(game.fieldOfWalls.get(c*game.sizeY+j+l).startsWith("PB")||game.fieldOfWalls.get(c*game.sizeY+j+l).startsWith("AB")){
							int wallIndex=new Integer(game.fieldOfWalls.get(c*game.sizeY+j+l).substring(2)).intValue();
							if(isRelatedForWallSearch(game,i,j,c,j+l,game.wallRed.get(wallIndex))){
								game.wallBlue.get(wallIndex).addAbstract(game,i+1, j+1, game.fieldOfWalls);
								break;
							}
						}}catch(Exception e){}
					}
					for(int c=j-l;c<=j+l;c++){
						try{if(game.fieldOfWalls.get((i-l)*game.sizeY+c).startsWith("PB")||game.fieldOfWalls.get((i-l)*game.sizeY+c).startsWith("AB")){
							int wallIndex=new Integer(game.fieldOfWalls.get((i-l)*game.sizeY+c).substring(2)).intValue();
							if(isRelatedForWallSearch(game,i,j,i-l,c,game.wallRed.get(wallIndex))){
								game.wallBlue.get(wallIndex).addAbstract(game,i+1, j+1, game.fieldOfWalls);
								break;
						}
						}}catch(Exception e){}
						try{if(game.fieldOfWalls.get((i+l)*game.sizeY+c).startsWith("PB")||game.fieldOfWalls.get((i+l)*game.sizeY+c).startsWith("AB")){
							int wallIndex=new Integer(game.fieldOfWalls.get((i+l)*game.sizeY+c).substring(2)).intValue();
							if(isRelatedForWallSearch(game,i,j,i+l,c,game.wallBlue.get(wallIndex))){
								game.wallBlue.get(wallIndex).addAbstract(game,i+1, j+1, game.fieldOfWalls);
								break;
							}
						}}catch(Exception e){}
					}
				}
			}
		}*/
		
		//������ ���������� (abstract) red �����
		/*for(int i=0;i<game.sizeX;i++)for(int j=0;j<game.sizeY;j++){
			if(game.fieldOfWalls.get(i*game.sizeY+j).equals("N")&&game.fieldState[i][j]==templateDotTypeRED){
				for(int l=3;l<20;l++){	
					if(!game.fieldOfWalls.get(i*game.sizeY+j).equals("N")){
						//System.out.println("abstract point added; koord="+(i+1)+";"+(j+1));
						break;
					}
					for(int c=i-l;c<=i+l;c++){
						try{if(game.fieldOfWalls.get(c*game.sizeY+j-l).startsWith("PR")||game.fieldOfWalls.get(c*game.sizeY+j-l).startsWith("AR")){
							int wallIndex=new Integer(game.fieldOfWalls.get(c*game.sizeY+j-l).substring(2)).intValue();
							if(isRelatedForWallSearch(game,i,j,c,j-l,game.wallRed.get(wallIndex))){
								game.wallRed.get(wallIndex).addAbstract(game,i+1, j+1, game.fieldOfWalls);
								break;
							}
						}}catch(Exception e){}
						try{if(game.fieldOfWalls.get(c*game.sizeY+j+l).startsWith("PR")||game.fieldOfWalls.get(c*game.sizeY+j+l).startsWith("AR")){
							int wallIndex=new Integer(game.fieldOfWalls.get(c*game.sizeY+j+l).substring(2)).intValue();
							if(isRelatedForWallSearch(game,i,j,c,j+l,game.wallRed.get(wallIndex))){
								game.wallRed.get(wallIndex).addAbstract(game,i+1, j+1, game.fieldOfWalls);
								break;
							}
						}}catch(Exception e){}
					}
					for(int c=j-l;c<=j+l;c++){
						try{if(game.fieldOfWalls.get((i-l)*game.sizeY+c).startsWith("PR")||game.fieldOfWalls.get((i-l)*game.sizeY+c).startsWith("AR")){
							int wallIndex=new Integer(game.fieldOfWalls.get((i-l)*game.sizeY+c).substring(2)).intValue();
							if(isRelatedForWallSearch(game,i,j,i-l,c,game.wallRed.get(wallIndex))){
								game.wallRed.get(wallIndex).addAbstract(game,i+1, j+1, game.fieldOfWalls);
								break;
						}
						}}catch(Exception e){}
						try{if(game.fieldOfWalls.get((i+l)*game.sizeY+c).startsWith("PR")||game.fieldOfWalls.get((i+l)*game.sizeY+c).startsWith("AR")){
							int wallIndex=new Integer(game.fieldOfWalls.get((i+l)*game.sizeY+c).substring(2)).intValue();
							if(isRelatedForWallSearch(game,i,j,i+l,c,game.wallRed.get(wallIndex))){
								game.wallRed.get(wallIndex).addAbstract(game,i+1, j+1, game.fieldOfWalls);
								break;
							}
						}}catch(Exception e){}
					}
				}
			}
		}*/
		
		//������ ����� �����
		for(int i=0;i<game.blueChains.size();i++){
			game.blueChains.get(i).searchChainEnd(game,game.fieldOfChains);
		}
		for(int i=0;i<game.redChains.size();i++){
			game.redChains.get(i).searchChainEnd(game,game.fieldOfChains);
		}
	}
	
	//������� ����, ������� �������� ������ � ���������� ������ ����
	public int getSmallerRedChainIdx(Game game){
		
		int count=0;//����� ������� ���� ����� ���� ����
		for(int i=0;i<game.redChains.size();i++){
			if(game.redChains.get(i).isAtSide(game))count++;
		}
		
		if(count==game.redChains.size())return -1;//���� ��� ������ ����� ���� ����
		if(count==game.redChains.size()-1)//���� ��� ������ ����� ����� ����� ���� ����
			for(int i=0;i<game.redChains.size();i++){
				if(!game.redChains.get(i).isAtSide(game))return i;
			}

		double minLengthFromLastBlue=Double.MAX_VALUE;//����� ����� ����� ����� ����� � ���������� ���� ����� (��� ���� �� � ���� ����)
		
		int smaller=0;
		for(int i=0;i<game.redChains.size();i++){
			if(!game.redChains.get(i).isAtSide(game)){
				if(game.redChains.get(i).getLengthFromLastBlue(game.gameField.lastDot)<=minLengthFromLastBlue){
					minLengthFromLastBlue=game.redChains.get(i).getLengthFromLastBlue(game.gameField.lastDot);
					smaller=i;
				}
			};
		}
		return smaller;
	}
	
	//��������� ���� � ������ �� ����� (1-� ���� ������ ����� �����)
	private void addNewWallPoints(Game game,int x,int y,String sign){	
		if(!game.fieldOfChains[x][y].equals("N")){
			return;
		}
		
		if(sign.equals("R")){//���������� ������� ����
			game.redChains.add(new Chain(game,x,y,game.redChains.size(),"R",game.fieldOfChains));
			searchChainPoints1(game,game.redChains.get(game.redChains.size()-1),templateDotType_RED);
		}else{//���������� ����� ����
			game.blueChains.add(new Chain(game,x,y,game.blueChains.size(),"B",game.fieldOfChains));
			searchChainPoints1(game,game.blueChains.get(game.blueChains.size()-1),templateDotType_BLUE);
		}
	}

	//���������� �����, ������������� ���� (1-� ���� ������ - ������ ������ ������� ���� � ����� �����)
	private void searchChainPoints1(Game game,Chain wall,int sign){
		int antiSign;
		if(sign==templateDotType_RED)antiSign=templateDotType_BLUE;else antiSign=templateDotType_RED;
		//������ ������������ ����� � �������� ��������
		for(int i=0;i<wall.points.size();i++){
			int x=wall.points.get(i).x,y=wall.points.get(i).y;
			//�������� �����			
			try{if(game.fieldState[x-1][y-1]==sign&&(game.fieldState[x-1][y]!=antiSign||game.fieldState[x][y-1]!=antiSign)){wall.addPoint(game,x-1,y-1,game.fieldOfChains);}}catch(Exception e){}
			try{if(game.fieldState[x-1][y+1]==sign&&(game.fieldState[x-1][y]!=antiSign||game.fieldState[x][y+1]!=antiSign)){wall.addPoint(game,x-1,y+1,game.fieldOfChains);}}catch(Exception e){}
			try{if(game.fieldState[x+1][y-1]==sign&&(game.fieldState[x+1][y]!=antiSign||game.fieldState[x][y-1]!=antiSign)){wall.addPoint(game,x+1,y-1,game.fieldOfChains);}}catch(Exception e){}
			try{if(game.fieldState[x+1][y+1]==sign&&(game.fieldState[x+1][y]!=antiSign||game.fieldState[x][y+1]!=antiSign)){wall.addPoint(game,x+1,y+1,game.fieldOfChains);}}catch(Exception e){}
			try{if(game.fieldState[x+1][y]==sign){wall.addPoint(game,x+1,y,game.fieldOfChains);}}catch(Exception e){}
			try{if(game.fieldState[x][y+1]==sign){wall.addPoint(game,x,y+1,game.fieldOfChains);}}catch(Exception e){}
			try{if(game.fieldState[x-1][y]==sign){wall.addPoint(game,x-1,y,game.fieldOfChains);}}catch(Exception e){}
			try{if(game.fieldState[x][y-1]==sign){wall.addPoint(game,x,y-1,game.fieldOfChains);}}catch(Exception e){}
		}
	}
	
	//���������� �����, ������������� ���� (2-� ���� ������ - ������ ����� ���������� ���� �� ����� �����)
	//����������������� ������ ������ � ��������� ������� � �����
	private void searchChainPoints2(Game game,Chain wall, int sign){
		
		//�������� ��������������� ����
		int antiSign;
		if(sign==templateDotType_RED){
			antiSign=templateDotType_BLUE;
		}else{
			antiSign=templateDotType_RED;
		}
		
		//������ ������������ ����� � �������� ��������
		for(int i=0;i<wall.points.size();i++){
			int x=wall.points.get(i).x,y=wall.points.get(i).y;
			
			//�������� �����			
			try{if(game.fieldState[x-1][y-1]==sign&&(game.fieldState[x-1][y]!=antiSign||game.fieldState[x][y-1]!=antiSign)){wall.addPoint(game,x-1,y-1,game.fieldOfChains);}}catch(Exception e){}
			try{if(game.fieldState[x-1][y+1]==sign&&(game.fieldState[x-1][y]!=antiSign||game.fieldState[x][y+1]!=antiSign)){wall.addPoint(game,x-1,y+1,game.fieldOfChains);}}catch(Exception e){}
			try{if(game.fieldState[x+1][y-1]==sign&&(game.fieldState[x+1][y]!=antiSign||game.fieldState[x][y-1]!=antiSign)){wall.addPoint(game,x+1,y-1,game.fieldOfChains);}}catch(Exception e){}
			try{if(game.fieldState[x+1][y+1]==sign&&(game.fieldState[x+1][y]!=antiSign||game.fieldState[x][y+1]!=antiSign)){wall.addPoint(game,x+1,y+1,game.fieldOfChains);}}catch(Exception e){}
			try{if(game.fieldState[x+1][y]==sign){wall.addPoint(game,x+1,y,game.fieldOfChains);}}catch(Exception e){}
			try{if(game.fieldState[x][y+1]==sign){wall.addPoint(game,x,y+1,game.fieldOfChains);}}catch(Exception e){}
			try{if(game.fieldState[x-1][y]==sign){wall.addPoint(game,x-1,y,game.fieldOfChains);}}catch(Exception e){}
			try{if(game.fieldState[x][y-1]==sign){wall.addPoint(game,x,y-1,game.fieldOfChains);}}catch(Exception e){}
			
			//������� �����, ��������� �� 2
			try{if(game.fieldState[x+1][y]==templateDotType_EMPTY&&game.fieldState[x+2][y]==sign){wall.addPoint(game,x+2,y,game.fieldOfChains);/*wall.addConnection(game,x+2,y+1,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x][y+1]==templateDotType_EMPTY&&game.fieldState[x][y+2]==sign){wall.addPoint(game,x,y+2,game.fieldOfChains);/*wall.addConnection(game,x+1,y+2,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x-1][y]==templateDotType_EMPTY&&game.fieldState[x-2][y]==sign){wall.addPoint(game,x-2,y,game.fieldOfChains);/*wall.addConnection(game,x,y+1,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x][y-1]==templateDotType_EMPTY&&game.fieldState[x][y-2]==sign){wall.addPoint(game,x,y-2,game.fieldOfChains);/*wall.addConnection(game,x+1,y,game.fieldOfWalls);*/}}catch(Exception e){}	
			//������� �����, ��������� �� 2 ��������
			try{if(game.fieldState[x+1][y+1]==templateDotType_EMPTY&&game.fieldState[x+2][y+2]==sign){wall.addPoint(game,x+2,y+2,game.fieldOfChains);/*wall.addConnection(game,x+2,y+2,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x-1][y+1]==templateDotType_EMPTY&&game.fieldState[x-2][y+2]==sign){wall.addPoint(game,x-2,y+2,game.fieldOfChains);/*wall.addConnection(game,x,y+2,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x+1][y-1]==templateDotType_EMPTY&&game.fieldState[x+2][y-2]==sign){wall.addPoint(game,x+2,y-2,game.fieldOfChains);/*wall.addConnection(game,x+2,y,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x-1][y-1]==templateDotType_EMPTY&&game.fieldState[x-2][y-2]==sign){wall.addPoint(game,x-2,y-2,game.fieldOfChains);/*wall.addConnection(game,x,y,game.fieldOfWalls);*/}}catch(Exception e){}
			//������� �����, ��������� �� 2 �����
			try{if((game.fieldState[x+1][y]==templateDotType_EMPTY||game.fieldState[x+1][y-1]==templateDotType_EMPTY)&&game.fieldState[x+2][y-1]==sign){wall.addPoint(game,x+2,y-1,game.fieldOfChains);/*if(game.fieldState[x+1][y]==templateDotTypeNULL)wall.addConnection(game,x+2,y+1,game.fieldOfWalls);if(game.fieldState[x+1][y-1]==templateDotTypeNULL)wall.addConnection(game,x+2,y,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if((game.fieldState[x+1][y]==templateDotType_EMPTY||game.fieldState[x+1][y+1]==templateDotType_EMPTY)&&game.fieldState[x+2][y+1]==sign){wall.addPoint(game,x+2,y+1,game.fieldOfChains);/*if(game.fieldState[x+1][y]==templateDotTypeNULL)wall.addConnection(game,x+2,y+1,game.fieldOfWalls);if(game.fieldState[x+1][y+1]==templateDotTypeNULL)wall.addConnection(game,x+2,y+2,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if((game.fieldState[x][y+1]==templateDotType_EMPTY||game.fieldState[x+1][y+1]==templateDotType_EMPTY)&&game.fieldState[x+1][y+2]==sign){wall.addPoint(game,x+1,y+2,game.fieldOfChains);/*if(game.fieldState[x][y+1]==templateDotTypeNULL)wall.addConnection(game,x+1,y+2,game.fieldOfWalls);if(game.fieldState[x+1][y+1]==templateDotTypeNULL)wall.addConnection(game,x+2,y+2,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if((game.fieldState[x][y+1]==templateDotType_EMPTY||game.fieldState[x-1][y+1]==templateDotType_EMPTY)&&game.fieldState[x-1][y+2]==sign){wall.addPoint(game,x-1,y+2,game.fieldOfChains);/*if(game.fieldState[x][y+1]==templateDotTypeNULL)wall.addConnection(game,x+1,y+2,game.fieldOfWalls);if(game.fieldState[x-1][y+1]==templateDotTypeNULL)wall.addConnection(game,x,y+2,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if((game.fieldState[x-1][y]==templateDotType_EMPTY||game.fieldState[x-1][y+1]==templateDotType_EMPTY)&&game.fieldState[x-2][y+1]==sign){wall.addPoint(game,x-2,y+21,game.fieldOfChains);/*if(game.fieldState[x-1][y]==templateDotTypeNULL)wall.addConnection(game,x,y+1,game.fieldOfWalls);if(game.fieldState[x-1][y+1]==templateDotTypeNULL)wall.addConnection(game,x,y+2,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if((game.fieldState[x-1][y]==templateDotType_EMPTY||game.fieldState[x-1][y-1]==templateDotType_EMPTY)&&game.fieldState[x-2][y-1]==sign){wall.addPoint(game,x-2,y-1,game.fieldOfChains);/*if(game.fieldState[x-1][y]==templateDotTypeNULL)wall.addConnection(game,x,y+1,game.fieldOfWalls);if(game.fieldState[x-1][y-1]==templateDotTypeNULL)wall.addConnection(game,x,y,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if((game.fieldState[x][y-1]==templateDotType_EMPTY||game.fieldState[x-1][y-1]==templateDotType_EMPTY)&&game.fieldState[x-1][y-2]==sign){wall.addPoint(game,x-1,y-2,game.fieldOfChains);/*if(game.fieldState[x][y-1]==templateDotTypeNULL)wall.addConnection(game,x+1,y,game.fieldOfWalls);if(game.fieldState[x-1][y-1]==templateDotTypeNULL)wall.addConnection(game,x,y,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if((game.fieldState[x][y-1]==templateDotType_EMPTY||game.fieldState[x+1][y-1]==templateDotType_EMPTY)&&game.fieldState[x+1][y-2]==sign){wall.addPoint(game,x+1,y-2,game.fieldOfChains);/*if(game.fieldState[x][y-1]==templateDotTypeNULL)wall.addConnection(game,x+1,y,game.fieldOfWalls);if(game.fieldState[x+1][y-1]==templateDotTypeNULL)wall.addConnection(game,x+2,y,game.fieldOfWalls);*/}}catch(Exception e){}
		
			//������� �����, ��������� �� 3
			try{if(game.fieldState[x+1][y]==templateDotType_EMPTY&&game.fieldState[x+2][y]==templateDotType_EMPTY&&game.fieldState[x+3][y]==sign){wall.addPoint(game,x+3,y,game.fieldOfChains);/*wall.addConnection(game,x+3,y+1,game.fieldOfWalls);wall.addConnection(game,x+2,y+1,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x][y+1]==templateDotType_EMPTY&&game.fieldState[x][y+2]==templateDotType_EMPTY&&game.fieldState[x][y+3]==sign){wall.addPoint(game,x,y+3,game.fieldOfChains);/*wall.addConnection(game,x+1,y+3,game.fieldOfWalls);wall.addConnection(game,x+1,y+2,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x-1][y]==templateDotType_EMPTY&&game.fieldState[x-2][y]==templateDotType_EMPTY&&game.fieldState[x-3][y]==sign){wall.addPoint(game,x-3,y,game.fieldOfChains);/*wall.addConnection(game,x-1,y+1,game.fieldOfWalls);wall.addConnection(game,x,y+1,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x][y-1]==templateDotType_EMPTY&&game.fieldState[x][y-2]==templateDotType_EMPTY&&game.fieldState[x][y-3]==sign){wall.addPoint(game,x,y-3,game.fieldOfChains);/*wall.addConnection(game,x+1,y-1,game.fieldOfWalls);wall.addConnection(game,x+1,y,game.fieldOfWalls);*/}}catch(Exception e){}
			//������� �����, ��������� �� 4
			try{if(game.fieldState[x+1][y]==templateDotType_EMPTY&&game.fieldState[x+2][y]==templateDotType_EMPTY&&game.fieldState[x+3][y]==templateDotType_EMPTY&&game.fieldState[x+4][y]==sign){wall.addPoint(game,x+4,y,game.fieldOfChains);/*wall.addConnection(game,x+3,y+1,game.fieldOfWalls);wall.addConnection(game,x+2,y+1,game.fieldOfWalls);wall.addConnection(game,x+4,y+1,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x][y+1]==templateDotType_EMPTY&&game.fieldState[x][y+2]==templateDotType_EMPTY&&game.fieldState[x][y+3]==templateDotType_EMPTY&&game.fieldState[x][y+4]==sign){wall.addPoint(game,x,y+4,game.fieldOfChains);/*wall.addConnection(game,x+1,y+3,game.fieldOfWalls);wall.addConnection(game,x+1,y+2,game.fieldOfWalls);wall.addConnection(game,x+1,y+4,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x-1][y]==templateDotType_EMPTY&&game.fieldState[x-2][y]==templateDotType_EMPTY&&game.fieldState[x-3][y]==templateDotType_EMPTY&&game.fieldState[x-4][y]==sign){wall.addPoint(game,x-4,y,game.fieldOfChains);/*wall.addConnection(game,x-1,y+1,game.fieldOfWalls);wall.addConnection(game,x,y+1,game.fieldOfWalls);wall.addConnection(game,x-2,y+1,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x][y-1]==templateDotType_EMPTY&&game.fieldState[x][y-2]==templateDotType_EMPTY&&game.fieldState[x][y-3]==templateDotType_EMPTY&&game.fieldState[x][y-4]==sign){wall.addPoint(game,x,y-4,game.fieldOfChains);/*wall.addConnection(game,x+1,y-1,game.fieldOfWalls);wall.addConnection(game,x+1,y,game.fieldOfWalls);wall.addConnection(game,x+1,y-2,game.fieldOfWalls);*/}}catch(Exception e){}
			//������� �����, ��������� �� 3 �����
			try{if((game.fieldState[x+1][y]==templateDotType_EMPTY||game.fieldState[x+1][y-1]==templateDotType_EMPTY)&&(game.fieldState[x+2][y]==templateDotType_EMPTY||game.fieldState[x+2][y-1]==templateDotType_EMPTY)&&game.fieldState[x+3][y-1]==sign){
				wall.addPoint(game,x+3,y-1,game.fieldOfChains);
				//if(game.fieldState[x+1][y]==templateDotTypeNULL)wall.addConnection(game,x+2,y+1,game.fieldOfWalls);if(game.fieldState[x+1][y-1]==templateDotTypeNULL)wall.addConnection(game,x+2,y,game.fieldOfWalls);
				//if(game.fieldState[x+2][y]==templateDotTypeNULL)wall.addConnection(game,x+3,y+1,game.fieldOfWalls);if(game.fieldState[x+2][y-1]==templateDotTypeNULL)wall.addConnection(game,x+3,y,game.fieldOfWalls);
			}}catch(Exception e){}
			try{if((game.fieldState[x+1][y]==templateDotType_EMPTY||game.fieldState[x+1][y+1]==templateDotType_EMPTY)&&(game.fieldState[x+2][y]==templateDotType_EMPTY||game.fieldState[x+2][y+1]==templateDotType_EMPTY)&&game.fieldState[x+3][y+1]==sign){
				wall.addPoint(game,x+3,y+1,game.fieldOfChains);
				//if(game.fieldState[x+1][y]==templateDotTypeNULL)wall.addConnection(game,x+2,y+1,game.fieldOfWalls);if(game.fieldState[x+1][y+1]==templateDotTypeNULL)wall.addConnection(game,x+2,y+2,game.fieldOfWalls);
				//if(game.fieldState[x+2][y]==templateDotTypeNULL)wall.addConnection(game,x+3,y+1,game.fieldOfWalls);if(game.fieldState[x+2][y+1]==templateDotTypeNULL)wall.addConnection(game,x+3,y+2,game.fieldOfWalls);
			}}catch(Exception e){}
			try{if((game.fieldState[x][y+1]==templateDotType_EMPTY||game.fieldState[x+1][y+1]==templateDotType_EMPTY)&&(game.fieldState[x][y+2]==templateDotType_EMPTY||game.fieldState[x+1][y+2]==templateDotType_EMPTY)&&game.fieldState[x+1][y+3]==sign){
				wall.addPoint(game,x+1,y+3,game.fieldOfChains);
				//if(game.fieldState[x][y+1]==templateDotTypeNULL)wall.addConnection(game,x+1,y+2,game.fieldOfWalls);if(game.fieldState[x+1][y+1]==templateDotTypeNULL)wall.addConnection(game,x+2,y+2,game.fieldOfWalls);
				//if(game.fieldState[x][y+2]==templateDotTypeNULL)wall.addConnection(game,x+1,y+3,game.fieldOfWalls);if(game.fieldState[x+1][y+2]==templateDotTypeNULL)wall.addConnection(game,x+2,y+3,game.fieldOfWalls);
			}}catch(Exception e){}
			try{if((game.fieldState[x][y+1]==templateDotType_EMPTY||game.fieldState[x-1][y+1]==templateDotType_EMPTY)&&(game.fieldState[x][y+2]==templateDotType_EMPTY||game.fieldState[x-1][y+2]==templateDotType_EMPTY)&&game.fieldState[x-1][y+3]==sign){
				wall.addPoint(game,x-1,y+3,game.fieldOfChains);
				//if(game.fieldState[x][y+1]==templateDotTypeNULL)wall.addConnection(game,x+1,y+2,game.fieldOfWalls);if(game.fieldState[x-1][y+1]==templateDotTypeNULL)wall.addConnection(game,x,y+2,game.fieldOfWalls);
				//if(game.fieldState[x][y+2]==templateDotTypeNULL)wall.addConnection(game,x+1,y+3,game.fieldOfWalls);if(game.fieldState[x-1][y+2]==templateDotTypeNULL)wall.addConnection(game,x,y+3,game.fieldOfWalls);
			}}catch(Exception e){}
			try{if((game.fieldState[x-1][y]==templateDotType_EMPTY||game.fieldState[x-1][y+1]==templateDotType_EMPTY)&&(game.fieldState[x-2][y]==templateDotType_EMPTY||game.fieldState[x-2][y+1]==templateDotType_EMPTY)&&game.fieldState[x-3][y+1]==sign){
				wall.addPoint(game,x-3,y+1,game.fieldOfChains);
				//if(game.fieldState[x-1][y]==templateDotTypeNULL)wall.addConnection(game,x,y+1,game.fieldOfWalls);if(game.fieldState[x-1][y+1]==templateDotTypeNULL)wall.addConnection(game,x,y+2,game.fieldOfWalls);
				//if(game.fieldState[x-2][y]==templateDotTypeNULL)wall.addConnection(game,x-1,y+1,game.fieldOfWalls);if(game.fieldState[x-2][y+1]==templateDotTypeNULL)wall.addConnection(game,x-1,y+2,game.fieldOfWalls);
			}}catch(Exception e){}
			try{if((game.fieldState[x-1][y]==templateDotType_EMPTY||game.fieldState[x-1][y-1]==templateDotType_EMPTY)&&(game.fieldState[x-2][y]==templateDotType_EMPTY||game.fieldState[x-2][y-1]==templateDotType_EMPTY)&&game.fieldState[x-3][y-1]==sign){
				wall.addPoint(game,x-3,y-1,game.fieldOfChains);
				//if(game.fieldState[x-1][y]==templateDotTypeNULL)wall.addConnection(game,x,y+1,game.fieldOfWalls);if(game.fieldState[x-1][y-1]==templateDotTypeNULL)wall.addConnection(game,x,y,game.fieldOfWalls);
				//if(game.fieldState[x-2][y]==templateDotTypeNULL)wall.addConnection(game,x-1,y+1,game.fieldOfWalls);if(game.fieldState[x-2][y-1]==templateDotTypeNULL)wall.addConnection(game,x-1,y,game.fieldOfWalls);
			}}catch(Exception e){}
			try{if((game.fieldState[x][y-1]==templateDotType_EMPTY||game.fieldState[x-1][y-1]==templateDotType_EMPTY)&&(game.fieldState[x][y-2]==templateDotType_EMPTY||game.fieldState[x-1][y-2]==templateDotType_EMPTY)&&game.fieldState[x-1][y-3]==sign){
				wall.addPoint(game,x-1,y-3,game.fieldOfChains);
				//if(game.fieldState[x][y-1]==templateDotTypeNULL)wall.addConnection(game,x+1,y,game.fieldOfWalls);if(game.fieldState[x-1][y-1]==templateDotTypeNULL)wall.addConnection(game,x,y,game.fieldOfWalls);
				//if(game.fieldState[x][y-2]==templateDotTypeNULL)wall.addConnection(game,x+1,y-1,game.fieldOfWalls);if(game.fieldState[x-1][y-2]==templateDotTypeNULL)wall.addConnection(game,x,y-1,game.fieldOfWalls);
			}}catch(Exception e){}
			try{if((game.fieldState[x][y-1]==templateDotType_EMPTY||game.fieldState[x+1][y-1]==templateDotType_EMPTY)&&(game.fieldState[x][y-2]==templateDotType_EMPTY||game.fieldState[x+1][y-2]==templateDotType_EMPTY)&&game.fieldState[x+1][y-3]==sign){
				wall.addPoint(game,x+1,y-3,game.fieldOfChains);
				//if(game.fieldState[x][y-1]==templateDotTypeNULL)wall.addConnection(game,x+1,y,game.fieldOfWalls);if(game.fieldState[x+1][y-1]==templateDotTypeNULL)wall.addConnection(game,x+2,y,game.fieldOfWalls);
				//if(game.fieldState[x][y-2]==templateDotTypeNULL)wall.addConnection(game,x+1,y-1,game.fieldOfWalls);if(game.fieldState[x+1][y-2]==templateDotTypeNULL)wall.addConnection(game,x+2,y-1,game.fieldOfWalls);
			}}catch(Exception e){}
		}
	}
	
	//������ ���� ����� ����� ������� � ����� ����� ������������ �������
	/*private boolean isRelatedForWallSearch(Game game,int x1,int y1,int x2,int y2,Wall wall){
		
		if(x1==x2){//�����������
			if(y1<y2){//������ ����
				for(int i=y1+1;i<y2;i++)if(game.fieldOfWalls[x1][i].startsWith("P")||game.fieldOfWalls[x1][i].startsWith("A")||game.fieldOfWalls[x1][i].startsWith("C"))return false;
				for(int i=y1+1;i<y2;i++)wall.addLink(game,x1, i, game.fieldOfWalls);
				return true;
			}else{//����� �����
				for(int i=y1-1;i>y2;i--)if(game.fieldOfWalls[x1][i].startsWith("P")||game.fieldOfWalls[x1][i].startsWith("A")||game.fieldOfWalls[x1][i].startsWith("C"))return false;
				for(int i=y1-1;i>y2;i--)wall.addLink(game,x1, i, game.fieldOfWalls);
				return true;
			}
		}
		else if(y1==y2){//�������������
			if(x1<x2){//����� �������
				for(int i=x1+1;i<x2;i++)if(game.fieldOfWalls[i][y1].startsWith("P")||game.fieldOfWalls[i][y1].startsWith("A")||game.fieldOfWalls[i][y1].startsWith("C"))return false;
				for(int i=x1+1;i<x2;i++)wall.addLink(game,i, y1, game.fieldOfWalls);
				return true;
			}else{//������ ������
				for(int i=x1-1;i>x2;i--)if(game.fieldOfWalls[i][y1].startsWith("P")||game.fieldOfWalls[i][y1].startsWith("A")||game.fieldOfWalls[i][y1].startsWith("C"))return false;
				for(int i=x1-1;i>x2;i--)wall.addLink(game,i, y1, game.fieldOfWalls);
				return true;
			}
		}
		
		double stepX=(double)Math.abs(x2-x1)/(double)Math.abs(y2-y1);
		double stepY=(double)Math.abs(y2-y1)/(double)Math.abs(x2-x1);
		//System.out.println("stepX="+stepX+"; stepY="+stepY);
		int max;
		if(stepY>stepX){		
			max=Math.abs(y2-y1);
			stepY=1;
		}else{
			max=Math.abs(x2-x1);
			stepX=1;
		}
		if(x2>x1&&y2>y1){
			for(int i=1;i<max;i++){
				if(game.fieldOfWalls.get((int)(x1+stepX*i)*game.sizeY+(int)(y1+stepY*i)).startsWith("P")||
						game.fieldOfWalls.get((int)(x1+stepX*i)*game.sizeY+(int)(y1+stepY*i)).startsWith("A")||
						game.fieldOfWalls.get((int)(x1+stepX*i)*game.sizeY+(int)(y1+stepY*i)).startsWith("C"))
				return false;
			}
			for(int i=1;i<max;i++){
				if(game.fieldState[(int)(x1+stepX*i)][(int)(y1+stepY*i)]==templateDotTypeNULL)wall.addLink(game,(int)(x1+stepX*i)+1,(int)(y1+stepY*i)+1, game.fieldOfWalls);
			}
			return true;
		}
		else if(x2<x1&&y2<y1){
			for(int i=1;i<max;i++){
				if(game.fieldOfWalls.get((int)(x1-stepX*i)*game.sizeY+(int)(y1-stepY*i)).startsWith("P")||
						game.fieldOfWalls.get((int)(x1-stepX*i)*game.sizeY+(int)(y1-stepY*i)).startsWith("A")||
						game.fieldOfWalls.get((int)(x1-stepX*i)*game.sizeY+(int)(y1-stepY*i)).startsWith("C"))
				return false;
			}
			for(int i=1;i<max;i++){
				if(game.fieldState[(int)(x1-stepX*i)][(int)(y1-stepY*i)]==templateDotTypeNULL)wall.addLink(game,(int)(x1-stepX*i)+1,(int)(y1-stepY*i)+1, game.fieldOfWalls);
			}
			return true;
		}
		else if(x2>x1&&y2<y1){
			for(int i=1;i<max;i++){
				if(game.fieldOfWalls.get((int)(x1+stepX*i)*game.sizeY+(int)(y1-stepY*i)).startsWith("P")||
						game.fieldOfWalls.get((int)(x1+stepX*i)*game.sizeY+(int)(y1-stepY*i)).startsWith("A")||
						game.fieldOfWalls.get((int)(x1+stepX*i)*game.sizeY+(int)(y1-stepY*i)).startsWith("C"))
				return false;
			}
			for(int i=1;i<max;i++){
				if(game.fieldState[(int)(x1+stepX*i)][(int)(y1-stepY*i)]==templateDotTypeNULL)wall.addLink(game,(int)(x1+stepX*i)+1,(int)(y1-stepY*i)+1, game.fieldOfWalls);
			}
			return true;
		}
		else if(x2<x1&&y2>y1){
			for(int i=1;i<max;i++){
				if(game.fieldOfWalls.get((int)(x1-stepX*i)*game.sizeY+(int)(y1+stepY*i)).startsWith("P")||
						game.fieldOfWalls.get((int)(x1-stepX*i)*game.sizeY+(int)(y1+stepY*i)).startsWith("A")||
						game.fieldOfWalls.get((int)(x1-stepX*i)*game.sizeY+(int)(y1+stepY*i)).startsWith("C"))
				return false;
			}
			for(int i=1;i<max;i++){
				if(game.fieldState[(int)(x1-stepX*i)][(int)(y1+stepY*i)]==templateDotTypeNULL)wall.addLink(game,(int)(x1-stepX*i)+1,(int)(y1+stepY*i)+1, game.fieldOfWalls);
			}
			return true;
		}
		
		return false;
	}*/
	
	//���������� ��������� � ������ ���� ����� ����� sign
	private Point getCentralPointForChainSearch(Game game,int sign){
		int centralX=(int)(game.sizeX/2.0);
		int centralY=(int)(game.sizeY/2.0);
		
		int depth=(int)(Math.max(game.sizeX, game.sizeY)/2.0);
			
		for(int i=0;i<=depth;i++){//���������� ������� ������� ����� �� ����
			try{if(game.fieldState[centralX-i][centralY-i]==sign)return new Point(centralX-i,centralY-i);}catch(Exception e){}
			try{if(game.fieldState[centralX-i][centralY+i]==sign)return new Point(centralX-i,centralY+i);}catch(Exception e){}
			try{if(game.fieldState[centralX+i][centralY-i]==sign)return new Point(centralX+i,centralY-i);}catch(Exception e){}
			try{if(game.fieldState[centralX+i][centralY+i]==sign)return new Point(centralX+i,centralY+i);}catch(Exception e){}
			
			try{if(game.fieldState[centralX][centralY-i]==sign)return new Point(centralX,centralY-i);}catch(Exception e){}
			try{if(game.fieldState[centralX][centralY+i]==sign)return new Point(centralX,centralY+i);}catch(Exception e){}
			try{if(game.fieldState[centralX-i][centralY]==sign)return new Point(centralX-i,centralY);}catch(Exception e){}
			try{if(game.fieldState[centralX+i][centralY]==sign)return new Point(centralX+i,centralY);}catch(Exception e){}
			
			if(i>0){
				for(int j=0;j<i-1;j++){
					try{if(game.fieldState[centralX-i+j][centralY-i]==sign)return new Point(centralX-i+j,centralY-i);}catch(Exception e){}
					try{if(game.fieldState[centralX-i][centralY+i-j]==sign)return new Point(centralX-i,centralY+i-j);}catch(Exception e){}
					try{if(game.fieldState[centralX+i][centralY-i+j]==sign)return new Point(centralX+i,centralY-i+j);}catch(Exception e){}
					try{if(game.fieldState[centralX+i-j][centralY+i]==sign)return new Point(centralX+i-j,centralY+i);}catch(Exception e){}
					
					try{if(game.fieldState[centralX+j][centralY-i]==sign)return new Point(centralX+j,centralY-i);}catch(Exception e){}
					try{if(game.fieldState[centralX-j][centralY+i]==sign)return new Point(centralX-j,centralY+i);}catch(Exception e){}
					try{if(game.fieldState[centralX-i][centralY-j]==sign)return new Point(centralX-i,centralY-j);}catch(Exception e){}
					try{if(game.fieldState[centralX+i][centralY+j]==sign)return new Point(centralX+i,centralY+j);}catch(Exception e){}
				}
			}
		}
		return null;
	}	
}
