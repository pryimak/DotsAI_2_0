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
	
	public static String appName="DotsAI";//название программы
	public static String appVersion="2.0";//версия
	public static String appDate="12 марта 2016";//дата
	public static String appAuthor="copyright &#169; 2016 Pryimak Alexey";//автор
	
	public static byte maxSize=15;//максимальный размер шаблона
	
	public static byte templateDotType_ANY=0;//тип точки шаблона - любая (зеленого цвета)
	public static byte templateDotType_LAND=1;//тип точки шаблона - граница поля (черный квадрат)
	public static byte templateDotType_EMPTY=2;//тип точки шаблона - пустое место
	public static byte templateDotType_BLUE=3;//тип точки шаблона - синяя точка
	public static byte templateDotType_BLUE_or_EMPTY=4;//тип точки шаблона - синяя или пустая точка
	public static byte templateDotType_RED=5;//тип точки шаблона - красная точка
	public static byte templateDotType_RED_or_EMPTY=6;//тип точки шаблона - красная или пустая
	
	Random rand=new Random();//генератор случаных чисел
	
	public TemplateEngine templateEngine;//база шаблонов
	public ArrayList<Game> games;//список игр
	public StatisticsFrame stat;//статистика игры
	
	public Protocol(){
		//инициализация переменных
		stat=new StatisticsFrame();
		templateEngine=new TemplateEngine();
		games=new ArrayList<Game>();
	}	
	
	//получить игру по ее индексу. Игры хранятся в списке и в игру делается ход и ищется ход ИИ при обращении к игре с помощью индекса
	public Game getGame(int gameIdx) {
		for(int i=0;i<games.size();i++){
			if(games.get(i).gameIdx==gameIdx)return games.get(i);
		}
		return null;
	}
	
	//удалить игру по индексу. Игра удаляется, когда она заканчивается
	public void deleteGame(int gameIdx) {
		for(int i=0;i<games.size();i++){
			if(games.get(i).gameIdx==gameIdx)games.remove(i);
		}
	}
	
	//добавить новую игру в список игр
	public void addNewGame(int gameIdx,EnemyType enemyType1,byte sizeX,byte sizeY,int redX1,int redY1,int blueX1,int blueY1,int redX2,int redY2,int blueX2,int blueY2){
		games.add(new Game(gameIdx,enemyType1,sizeX,sizeY,redX1,redY1,blueX1,blueY1,redX2,redY2,blueX2,blueY2));
	}
	
	//класс Game хранит всю информацию об игре
	public class Game{
		public int gameIdx;//индекс игры
		public byte sizeX;//размер поля клеток (X x Y)
		public byte sizeY;//размер поля клеток (X x Y)
		public EnemyType enemyType;//тип соперника - человек, рандом или другой ИИ
		public GameField gameField;//модель игрового поля
		public Point lastAimove;//последний ход ИИ
		public byte[][] movesMas;//список ходов в игре
		int movesCount;//число ходов в игре
		
		public ArrayList<Point> BSSpoints;//список точек, которые помогают избежать окружения ИИ в некоторых случаях
		public ArrayList<TreeApplicationInGame> treeApplicationInGame;//список применения деревьев в игре
		boolean isCanGroung;//может ли ИИ заземляться
		public byte[][] fieldState;//состояние поля
		public byte[][] fieldTerrytoryState;//состояние поля с учетом территории - пустые места в домиках считаются того цвета, что и домик
		
		//не используется в игре, цель применения - для шаблонов типа "глобальной атаки"
		//boolean isUseGlobalAttackTemplate;//можно ли атаковать глобально. Использовать только один раз в игре
		
		public Point moveAI;//ход ИИ в процессе его поиска - сюда могут записываться промежуточные результаты поиска
		public int crossCount;//число скрестов в игре
		public int closestRedChainIdx;//наименьшая цепь точек у ИИ
		public ArrayList<Chain> redChains;//список красных цепей точек (цепей ИИ)
		public ArrayList<Chain> blueChains;//список синих цепей точек (цепей соперника ИИ)
		public String[][] fieldOfChains;//хранит информацию о цепях
		boolean isMoveByBeginTemplate;//вначале игры ходить по BeginTemplate до первого хода по другому типу шаблона
		private int lossLevel;//какое преимущество надо добыть над ИИ, чтобы ИИ сдался
		
		//хранят влияние точек двух игроков. Чем ближе точка к данной позиции, тем ее влияние больше
		//используются для абстрактных ходов, чтобы они ставились в места с минимальным числом точек соперника ИИ
		public double fieldSpectrumRed[][];
		public double fieldSpectrumBlue[][];
		public double fieldSpectrumRedMax;
		public double fieldSpectrumBlueMax;
		
		//создание объекта игры
		public Game(int gameIdx,EnemyType enemyType,byte sizeX,byte sizeY,
					int redX1,int redY1,int blueX1,int blueY1,int redX2,int redY2,int blueX2,int blueY2
				){
			stat.clearStat();//очистить статистику
			
			lossLevel=rand.nextInt(30)+17;//определить разницу проигранных точек, при которых ИИ сдается
			
			//размер и состояние поля
			this.sizeX=sizeX;
			this.sizeY=sizeY;
			fieldState=new byte[sizeX][sizeY];
			fieldTerrytoryState=new byte[sizeX][sizeY];
			
			//влияние синих и красных на окружающие точки
			fieldSpectrumRed=new double[sizeX][sizeY];
			fieldSpectrumBlue=new double[sizeX][sizeY];
			for(int i=0;i<sizeX;i++)for(int j=0;j<sizeY;j++){
				fieldSpectrumRed[i][j]=0;
				fieldSpectrumBlue[i][j]=0;
			}
			fieldSpectrumRedMax=0;
			fieldSpectrumBlueMax=0;
			
			//запомнить другие параметры
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
			gameField=new GameField(sizeX,sizeY);//модель игрового поля
			
			//добавить на игровое поле точки стартовой позиции
			gameField.addMove(new Point(redX1,redY1),GameField.RED);
			gameField.addMove(new Point(blueX1,blueY1), GameField.BLUE);
			gameField.addMove(new Point(redX2,redY2),GameField.RED);
			gameField.addMove(new Point(blueX2,blueY2), GameField.BLUE);
		}

		//список ходов в игре
		public void addInMovesMas(Point p, int type){//red=0,blue=1		
			movesMas[movesCount][0]=(byte) p.x;
			movesMas[movesCount][1]=(byte) p.y;
			movesMas[movesCount][2]=(byte) type;
			movesCount++;
		}

		//вернуть число ходов в игре
		public int getMovesCount(){return movesCount;}
		
	}
	
	//выполнить ход соперника ИИ
	public void addAIenemyMove(int gameIdx,int x,int y){
		Game game=getGame(gameIdx);
		if(game==null)return;
		game.addInMovesMas(new Point(x,y), GameField.BLUE);
		game.gameField.addMove(new Point(x,y), GameField.BLUE);
		if(game.movesCount%9==0){//чистка мусора в памяти программы каждые 9 ходов
			System.gc();
		}
	}
	
	//найти ход ИИ
	public Point getAImove(int gameIdx,int level,Point recommendedMove){
		Game game=getGame(gameIdx);
		game.lastAimove=getAImovePoint(game,gameIdx,level,recommendedMove);//поиск хода ИИ
		game.addInMovesMas(game.lastAimove, GameField.RED);
		game.gameField.addMove(game.lastAimove, GameField.RED);
		return game.lastAimove;
	}
	
	//найти ход ИИ. Как только находится ход ИИ по одному из способов, дальнейший поиск на данном ходе прекращается
	Point getAImovePoint(Game game,int gameIdx,int level,Point recommendedMove){
		
		//поиск хода ИИ blue surround security
		if(isMoveBlueSurroundSecurity(game)){stat.moveStatMas.addMoveStat(MoveStatType.BLUE_SURROUND_SECURITY);return game.moveAI;}
		
		game.gameField.setFieldState(game);//получить состояние игры
		
		//поиск хода по списку ранее применявшихся в игре деревьев
		if(isMoveByTree(game,recommendedMove)){return game.moveAI;}		
				
		chainsSearch(game);//поиск цепей в игре
		
		if(isEndGame(game,GameField.RED)){//проверка на окончание игры - заземлился ли ИИ
			if(isMoveByGround(game,recommendedMove))return game.moveAI;//вернуть ход по шаблонам заземления, если это возможно
			else{
				game.closestRedChainIdx=-1;
				return new Point(-2,-2);//ИИ заземлился, ИИ заканчивает игру
			}
		}
		if(isEndGame(game,GameField.BLUE)){//проверка на окончание игры - заземлился ли соперник ИИ
			game.closestRedChainIdx=-1;
			return new Point(-3,-3);//человек заземлился, ИИ сдается
		}
		if(game.gameField.scoreBlue-game.gameField.scoreRed>game.lossLevel){//проверка на окончание игры - ИИ сдается, если потерял много точек
			game.closestRedChainIdx=-1;
			return new Point(-4,-4);//ИИ сдается, т.к. потерял много точек
		}
						
		//поиск хода по начальному типу шаблона (тип BEGIN)
		if(game.isMoveByBeginTemplate){//искать ли по данному типу шаблона
			if(templateEngine.isMoveIfEqualsLikeAreaByPoint(this,game,TemplateType.templateTypeBEGIN,game.gameField.lastDot,game.fieldState,recommendedMove)){
				return game.moveAI;//ход по начальному шаблону найден
			}else{//после того, как сделан впервые не найден ход по начальному шаблону, больше по начальным шаблонам поиска не происходит
				game.isMoveByBeginTemplate=false;
			}
		}
		
		//поиск хода по боковому шаблону, если точка соперника ИИ поставлена вблизи края поля		
		try{if(TemplateFieldSideType.getFieldSideType(game,game.gameField.lastDot)!=TemplateFieldSideType.templateFieldSideTypeINSIDE){
			if(templateEngine.isMoveIfEqualsLikeAreaByPoint(this,game,TemplateType.templateTypeSQUARE_SIDE,game.gameField.lastDot,game.fieldState,recommendedMove))return game.moveAI;
		}}catch(Exception e){}

		//поиск по обычным шаблонам, которые содержат ответ ИИ относительно последнего хода соперника ИИ 
		try{if(templateEngine.isMoveIfEqualsLikeAreaByPoint(this,game,TemplateType.templateTypeSQUARE,game.gameField.lastDot,game.fieldState,recommendedMove))return game.moveAI;}catch(Exception e){}
		
		//шаблоны глобальной атаки отключены
		//найти для всех концов синих цепей ход по GLOBAL_ATTACK шаблону
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
			
		//<расчет спектра>
		if(!game.isCanGroung){
			for(int i=0;i<game.sizeX;i++){for(int j=0;j<game.sizeY;j++){//обнуление спектра
				game.fieldSpectrumRed[i][j]=0;
				game.fieldSpectrumBlue[i][j]=0;
			}}
			int depth=5;//глубина влияния точки на окружающее пространство
			for(int i=0;i<game.sizeX;i++){for(int j=0;j<game.sizeY;j++){
				if(game.fieldState[i][j]==templateDotType_RED){//считается влияние красных точек
					for(int i1=-depth;i1<=depth;i1++){
						for(int j1=-depth;j1<=depth;j1++){
							if(i1==0&j1==0)continue;
							try{game.fieldSpectrumRed[i+i1][j+j1]+=1.0/Math.sqrt(i1*i1+j1*j1);}catch(Exception e){continue;}
						}
					}
				}else if(game.fieldState[i][j]==templateDotType_BLUE){//считается влияние синих точек
					for(int i1=-depth;i1<=depth;i1++){
						for(int j1=-depth;j1<=depth;j1++){
							if(i1==0&j1==0)continue;
							try{game.fieldSpectrumBlue[i+i1][j+j1]+=1.0/Math.sqrt(i1*i1+j1*j1);}catch(Exception e){continue;}
						}
					}
				}		
			}}			

			//коррекция, чтобы в местах точек красных и синих были значения максимума влияния
			game.fieldSpectrumRedMax=0;
			game.fieldSpectrumBlueMax=0;
			for(int i=0;i<game.sizeX;i++){for(int j=0;j<game.sizeY;j++){
				if(game.fieldState[i][j]==templateDotType_BLUE){game.fieldSpectrumBlue[i][j]=Integer.MAX_VALUE;continue;}
				if(game.fieldState[i][j]==templateDotType_RED){game.fieldSpectrumRed[i][j]=Integer.MAX_VALUE;continue;}
				if(game.fieldSpectrumBlue[i][j]>game.fieldSpectrumBlueMax)game.fieldSpectrumBlueMax=game.fieldSpectrumBlue[i][j];
				if(game.fieldSpectrumRed[i][j]>game.fieldSpectrumRedMax)game.fieldSpectrumRedMax=game.fieldSpectrumRed[i][j];
			}}
		}
		
		//искать ходы для всех точек ближней красной цепи к последнему ходу соперника ИИ, затем поиск для всех других красных цепей
		game.closestRedChainIdx=getSmallerRedChainIdx(game);//найти индекс самой близкой цепи ИИ к последнему синему ходу
		
		//если самая близкая цепь не найдена (т.е. все цепи достигли края поля), можно заземляться
		if(game.closestRedChainIdx==-1){
			game.isCanGroung=true;
		}else{
			game.isCanGroung=false;
		}
		
		//если еще нельзя заземляться, поиск хода идет для точек самой близкой цепи ИИ к последнему синему ходу
		if(!game.isCanGroung){
			if(isMoveByRedOriented(game,game.redChains.get(game.closestRedChainIdx),level,recommendedMove))return game.moveAI;
		}
		//поиск хода для всех остальных цепей ИИ
		for(int i=0;i<game.redChains.size();i++){
			if(!game.isCanGroung){//не искать для самой близкой цепи
				if(i==game.closestRedChainIdx){
					continue;
				}
			}
			if(isMoveByRedOriented(game,game.redChains.get(i),level,recommendedMove)){//искать ход для всех точек одной из цепей ИИ
				return game.moveAI;
			}
		}
			
		//поиск хода по заземлению
		if(isMoveByGround(game,recommendedMove))return game.moveAI;
		
		//ни по одному шаблону ход не найден, значит ход ИИ делается случайно
		game.gameField.setTerrytoryState(game);//чтобы не ходить в домики соперника сохраняется поле с учетом контролируемой территории
		game.moveAI.x=game.lastAimove.x;game.moveAI.y=game.lastAimove.y;	
		while(!game.gameField.canAddMove((byte)game.moveAI.x,(byte)game.moveAI.y)||game.fieldTerrytoryState[game.moveAI.x][game.moveAI.y]!=templateDotType_EMPTY){
			game.moveAI.x=rand.nextInt(game.sizeX);
			game.moveAI.y=rand.nextInt(game.sizeY);			
		}
		stat.moveStatMas.addMoveStat(MoveStatType.RANDOM);
		return game.moveAI;
	}

	//поиск хода ИИ относительно всех точек в одной из цепей ИИ
	private boolean isMoveByRedOriented(Game game,Chain chain,int level,Point recommendedMove){
		
		//поиск хода по шаблону разрыва цепи соперника (поиск только для точки конца цепи)
		try{if(templateEngine.isMoveIfEqualsLikeAreaByPoint(this,game,TemplateType.templateTypeWALL_DESTROY,new Point(chain.chainEndX,chain.chainEndY),game.fieldState,recommendedMove))return true;}catch(Exception e){}
		
		//поиск по шаблону завершающей части атаки, когда окружение соперника близко (поиск только для точки конца цепи)
		try{if(templateEngine.isMoveIfEqualsLikeAreaByPoint(this,game,TemplateType.templateTypeFINAL_RED_ATTACK,new Point(chain.chainEndX,chain.chainEndY),game.fieldState,recommendedMove))return true;}catch(Exception e){}
		
		//поиск по шаблонам разрыва, завершающей и продолжающей части атаки для всех остальных точек цепи 
		try{if(templateEngine.isMoveIfEqualsLikeAreaByPointList(this,game,TemplateType.templateTypeWALL_DESTROY,chain.points,game.fieldState,recommendedMove))return true;}catch(Exception e){}
		try{if(templateEngine.isMoveIfEqualsLikeAreaByPointList(this,game,TemplateType.templateTypeFINAL_RED_ATTACK,chain.points,game.fieldState,recommendedMove))return true;}catch(Exception e){}
		try{if(templateEngine.isMoveIfEqualsLikeAreaByPointList(this,game,TemplateType.templateTypeCONTINUED_RED_ATTACK,chain.points,game.fieldState,recommendedMove))return true;}catch(Exception e){}
		
		//поиск абстраткного хода ИИ
		if(game.movesCount>20){//абстрактные ходы делаются, если в игре сделано больше 20 ходов
			//поиск абстрактного хода идет для точек из списка ранее сделаных абстрактных ходов для данной цепи
			for(int i=chain.abstractPoints.size()-1;i>=0;i--){//искать ход с последних добавленных точек
				try{if(templateEngine.isMoveIfEqualsLikeAreaByPoint(this,game,TemplateType.templateTypeABSTRACT_ATTACK_WALL,chain.abstractPoints.get(i),game.fieldState,recommendedMove)){
					chain.abstractPoints.add(new Point(game.moveAI.x,game.moveAI.y));//найденный абстрактный ход добавляется в список абстрактных ходов
					return true;
				}}catch(Exception e){}
			}
			//поиск абстрактного хода идет для точек цепи ИИ
			for(int i=chain.points.size();i>=0;i--){//искать ход с конца стен
				try{if(templateEngine.isMoveIfEqualsLikeAreaByPoint(this,game,TemplateType.templateTypeABSTRACT_ATTACK_WALL,chain.points.get(i),game.fieldState,recommendedMove)){
					chain.abstractPoints.add(new Point(game.moveAI.x,game.moveAI.y));//найденный абстрактный ход добавляется в список абстрактных ходов
					return true;
				}}catch(Exception e){}
			}
		}	
		
		//поиск хода по шаблонам цепей
		if(!chain.isAtSide(game)){//если цепь не на краю поля
			//в данной программе уровень сложности ИИ всегда задан максимальный, хотя можно и варьировать его
			//чем слабее задать уровень сложности, тем меньше шаблонов будет использоваться для поиска хода по шаблонам для цепей,
			//а значит тем хуже будут построенные цепи. Хотя это сейчас не так актуально, т.к. появились шаблоны абстрактных ходовs
			double koef=1.0+(10-level)/10.0;//определить уровень сложности, где level=[0;10], чем выше, тем сильнее		
			for(double s=0;s<=game.fieldSpectrumBlueMax;s+=game.fieldSpectrumBlueMax/10.0){
				for(int i=0;i<chain.points.size()/koef;i++){//искать ход с конца стен
					if(game.fieldSpectrumBlue[chain.points.get(i).x][chain.points.get(i).y]<s
							||
						game.fieldSpectrumBlue[chain.points.get(i).x][chain.points.get(i).y]>s+game.fieldSpectrumBlueMax/10.0
					){
						continue;
					}
					try{if(isMoveByChain(game,chain.points.get(i).x,chain.points.get(i).y,recommendedMove)){//найден ход по цепи
						return true;
					}}catch(Exception e){}
				}
			}
		}		
		
		return false;
	}

	//поиск хода по шаблонам заземления
	private boolean isMoveByGround(Game game,Point recommendedMove){
		//поиск по шаблонам заземления не для края поля
		for(int i=0;i<game.redChains.size();i++){//проходим все красные цепи и отправляем на поиск хода весь список точек в цепи
			if(templateEngine.isMoveIfEqualsLikeAreaByPointList(this,game,TemplateType.templateTypeGROUND,game.redChains.get(i).points,game.fieldState,recommendedMove))return true;
		}
		//поиск по шаблонам заземления для края поля
		for(int i=0;i<game.redChains.size();i++){//проходим все точки красных цепей
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

	//поиск хода ИИ для защиты от возможных окружений ИИ от соперника ИИ
	private boolean isMoveBlueSurroundSecurity(Game game){	
		for(int i=0;i<game.BSSpoints.size();i++){//для каждой точки из списка сохраненных точек проверяем
			GameField e=game.gameField.clone();//клонируем игру
			e.addMove(game.BSSpoints.get(i), GameField.BLUE);//делаем ход синей точкой (точкой соперника ИИ)
			if(e.blueEnclosures.size()>game.gameField.blueEnclosures.size()){//если этот ход ведет к окружению точек ИИ
				game.moveAI=game.BSSpoints.get(i);//то ИИ делает этот ход для блокировки окружения своих точек
				return true;
			}
		}
		return false;
	}

	//дополнительная проверка на возможность хода ИИ после его нахождения
	public boolean isAICanMakeMove(byte x,byte y,Game game,int templateType){
		if(game.gameField.canAddMove(x, y)){
			//проверка возможности улучшить ход ИИ путем максимизации захватываемой территории
			Point p1=getMaxTerrSecurityMove(game, x, y);//получить улучшенный ход ИИ
			if(p1.x!=x||p1.y!=y){
				game.moveAI=p1;
				stat.moveStatMas.addMoveStat(MoveStatType.EXPRESS_MAX_TERR_SECURITY);
				return true;
			}
			
			//move by express surround security 1
			/*if(templateType!=-1&templateType!=5){//для макросов (templateType=-1) and BST не искать по SurSec1
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

	//поиск хода по списку деревьев, ранее примененных в игре
	private boolean isMoveByTree(Game game,Point recommendedMove){	
		for(int i=game.treeApplicationInGame.size()-1;i>=0;i--){
			TreeApplicationInGame ta=game.treeApplicationInGame.get(i);//применение дерева в конкретной точке поля
			
			//если ход синего делается вдали от дерева, то поиск хода ИИ только по default синему ходу
			boolean isMoveOnlyByDefault=false;
			if(Math.abs(ta.center.x-game.gameField.lastDot.x)>7||Math.abs(ta.center.y-game.gameField.lastDot.y)>7){
				isMoveOnlyByDefault=true;
			}
			
			try{
				//для последнего добавленного дерева не использовать такую проверку,
				//проверка выполняется на соответствие игровой ситуации шаблону, который ее описывает.
				//Т.к. список примененных деревьев хранит все примененные деревья во время игры,
				//то состояние поля могло уже измениться в этом месте поля, 
				//а значит дерево удаляется из списка, чтобы больше по нему не искать ход
				if(i!=game.treeApplicationInGame.size()-1){
					if(!ta.templateView.isEquals(game,game.gameField.lastDot,game.fieldState,TemplateType.isSide(ta.templateType),TemplateFieldSideType.getFieldSideType(game, game.gameField.lastDot))){
						game.treeApplicationInGame.remove(i);
						continue;
					}
				}
				
				//поиск хода по дереву
				if(ta.isExistsLevelMove(game,game.gameField.lastDot,isMoveOnlyByDefault,recommendedMove)){
					
					//перехват макросов, когда один кончается, а другой из другого шаблона начинается.
					//Раньше тут искалось продолжение дерева ходов за пределами данного дерева и шаблона.
					//Поиск шел по всему списку шаблонов. Однако это сложный и противоречивый способ, пока отключен
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
					
					//ход ИИ по дереву найден
					game.moveAI=ta.transformAIPoint;
					if(isAICanMakeMove((byte)game.moveAI.x,(byte)game.moveAI.y,game,-1)){
						//если ход ИИ возможен, то в редакторе шаблонов отобразить шаблон, по дереву которого был найден ход
						if(templateEngine.foundedNumber!=0){
							templateEngine.foundedNumber=templateEngine.getBaseIndexByTemplateID(ta.tree.templateID);
							templateEngine.foundedIndex=ta.templateID;
							templateEngine.foundedTemplateType=ta.templateType;
						}
						stat.moveStatMas.addMoveStat(TemplateType.getMoveStatType(ta.templateType));
						return true;				
					}
					
					game.treeApplicationInGame.remove(i);//если ход невозможен, то дерево удаляется из списка рассматриваемых в дальнейшем
					
				}
			}catch(Exception e){return false;}//обработка исключения, если макрос применим за пределами поля	
		}
		return false;
	}

	//поиск хода по шаблонам для цепей - типы шаблонов WALL и для края поля WALL_SIDE
	private boolean isMoveByChain(Game game,int x,int y,Point recommendedMove){
		//если точка (x,y), для которой ищется шаблон, находится возле границ, то использовать WALL_SIDE, т.к. обычный WALL у края может давать неоптимальные ходы
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
	
	//найти улучшенный ход ИИ, который максимизирует захваченную территорию
	public Point getMaxTerrSecurityMove(Game game,byte nextRedX,byte nextRedY){
		int sign=templateDotType_RED;//искать только для красных точек
		
		int scoreMax=game.gameField.scoreRed-game.gameField.scoreBlue;//текущая разница в счете игры
		Point AImove=new Point(nextRedX,nextRedY);
		int moveMax=0;
		
		for(int x=0;x<game.sizeX;x++){
			for(int y=0;y<game.sizeY;y++){
				if(!isCanMoveForSurroundSecurity(game,x,y,sign)){
					continue;
				}
				if(x==nextRedX&&y==nextRedY)continue;
				GameField singleGameEngine = game.gameField.clone();//клонирование игры
	
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

	//проверка, возможно ли создание окружения относительно данной точки.
	//Если точка имеет меньше двух соседей своего цвета, то окружение невозможно
	private boolean isCanMoveForSurroundSecurity(Game game,int x,int y,int sign){	
		if(game.fieldState[x][y]!=templateDotType_EMPTY)return false;
		
		//если одна своя точка вокруг целевой точки, то окружение будет невозможно
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

	//проверка на то, закончилась ли игра
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
		                                                   
		GameField gameField=game.gameField.clone();//клонирование игры
		
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

	//вспомогательная функция при проверке на окончание игры
	private int getScoreForSurroundSecurity(GameField singleGameEngine,int moveType){
		if(moveType==GameField.BLUE){
			return singleGameEngine.scoreRed;
		}else{
			return singleGameEngine.scoreBlue;
		}
	}
	
	//поиск цепей
	public void chainsSearch(Game game){
	
		//массив с хранением цепей
		game.fieldOfChains=new String[game.sizeX][game.sizeY];
		for(int i=0;i<game.sizeX;i++){//массив сначала заполняется пустыми точками N
			for(int j=0;j<game.sizeY;j++){
				game.fieldOfChains[i][j]="N";
			}
		}
		
		//списки красных и синих цепей
		game.redChains=new ArrayList<Chain>();
		game.blueChains=new ArrayList<Chain>();
		game.crossCount=0;//число скрестов в игре
		
		//поиск скрестов и добавление цепей 
		//(это первый этап добавления цепей - ищутся только близкие друг к другу точки цепи, чтобы не перепутались точки разных цепей)
		for(int i=0;i<game.sizeX-1;i++){
			for(int j=0;j<game.sizeY-1;j++){
				if(game.fieldState[i][j]==templateDotType_RED&&game.fieldState[i+1][j+1]==templateDotType_RED&&game.fieldState[i+1][j]==templateDotType_BLUE&&game.fieldState[i][j+1]==templateDotType_BLUE){			
					//найден скрест, добавляем цепи
					game.crossCount++;			
					addNewWallPoints(game,i, j,"R");
					addNewWallPoints(game,i+1, j,"B");			
					addNewWallPoints(game,i+1, j+1,"R");
					addNewWallPoints(game,i, j+1,"B");			
				}else if((game.fieldState[i][j]==templateDotType_BLUE&&game.fieldState[i+1][j+1]==templateDotType_BLUE&&game.fieldState[i+1][j]==templateDotType_RED&&game.fieldState[i][j+1]==templateDotType_RED)){
					//найден скрест, добавляем цепи
					game.crossCount++;
					addNewWallPoints(game,i+1, j,"R");
					addNewWallPoints(game,i, j,"B");
					addNewWallPoints(game,i, j+1,"R");
					addNewWallPoints(game,i+1, j+1,"B");
				}
			}
		}
		
		//добавление точек, если нет скрестов
		if(game.crossCount==0){
			Point point=getCentralPointForChainSearch(game,templateDotType_RED);
			if(point!=null)addNewWallPoints(game,point.x, point.y,"R");
			point=getCentralPointForChainSearch(game,templateDotType_BLUE);
			if(point!=null)addNewWallPoints(game,point.x, point.y,"B");	
		}
		
		//искать близкие точки для стен и их добавление в массив
		//(это второй этап поиска точек цепей - ищутся более отдаленные точки, чем на первом этапе)
		for(int i=0;i<game.blueChains.size();i++){
			searchChainPoints2(game,game.blueChains.get(i),templateDotType_BLUE);
		}
		for(int i=0;i<game.redChains.size();i++){
			searchChainPoints2(game,game.redChains.get(i),templateDotType_RED);
		}
		
		//раньше абстрактные точки искались так, сейчас они автоматически добавляются при нахождении точек по абстрактным шаблонам
		//искать отдаленные (abstract) blue точки
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
		
		//искать отдаленные (abstract) red точки
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
		
		//искать концы цепей
		for(int i=0;i<game.blueChains.size();i++){
			game.blueChains.get(i).searchChainEnd(game,game.fieldOfChains);
		}
		for(int i=0;i<game.redChains.size();i++){
			game.redChains.get(i).searchChainEnd(game,game.fieldOfChains);
		}
	}
	
	//вернуть цепь, которая наиболее близка к последнему синему ходу
	public int getSmallerRedChainIdx(Game game){
		
		int count=0;//число красных стен около края поля
		for(int i=0;i<game.redChains.size();i++){
			if(game.redChains.get(i).isAtSide(game))count++;
		}
		
		if(count==game.redChains.size())return -1;//если все стенки около края поля
		if(count==game.redChains.size()-1)//если все стенки кроме одной около края поля
			for(int i=0;i<game.redChains.size();i++){
				if(!game.redChains.get(i).isAtSide(game))return i;
			}

		double minLengthFromLastBlue=Double.MAX_VALUE;//конец какой стены ближе всего к последнему ходу синих (для стен не у края поля)
		
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
	
	//создаются цепи и ищутся их точки (1-й этап поиска точек цепей)
	private void addNewWallPoints(Game game,int x,int y,String sign){	
		if(!game.fieldOfChains[x][y].equals("N")){
			return;
		}
		
		if(sign.equals("R")){//добавление красной цепи
			game.redChains.add(new Chain(game,x,y,game.redChains.size(),"R",game.fieldOfChains));
			searchChainPoints1(game,game.redChains.get(game.redChains.size()-1),templateDotType_RED);
		}else{//добавление синей цепи
			game.blueChains.add(new Chain(game,x,y,game.blueChains.size(),"B",game.fieldOfChains));
			searchChainPoints1(game,game.blueChains.get(game.blueChains.size()-1),templateDotType_BLUE);
		}
	}

	//добавление точек, принадлежащих цепи (1-й этап поиска - ищутся только близкие друг к другу точки)
	private void searchChainPoints1(Game game,Chain wall,int sign){
		int antiSign;
		if(sign==templateDotType_RED)antiSign=templateDotType_BLUE;else antiSign=templateDotType_RED;
		//пройти существующие точки и добавить соседние
		for(int i=0;i<wall.points.size();i++){
			int x=wall.points.get(i).x,y=wall.points.get(i).y;
			//соседние точки			
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
	
	//добавление точек, принадлежащих цепи (2-й этап поиска - ищутся более отдаленные друг от друга точки)
	//закоментированные строки искали и добавляли разрывы в цепях
	private void searchChainPoints2(Game game,Chain wall, int sign){
		
		//получить противоположный знак
		int antiSign;
		if(sign==templateDotType_RED){
			antiSign=templateDotType_BLUE;
		}else{
			antiSign=templateDotType_RED;
		}
		
		//пройти существующие точки и добавить соседние
		for(int i=0;i<wall.points.size();i++){
			int x=wall.points.get(i).x,y=wall.points.get(i).y;
			
			//соседние точки			
			try{if(game.fieldState[x-1][y-1]==sign&&(game.fieldState[x-1][y]!=antiSign||game.fieldState[x][y-1]!=antiSign)){wall.addPoint(game,x-1,y-1,game.fieldOfChains);}}catch(Exception e){}
			try{if(game.fieldState[x-1][y+1]==sign&&(game.fieldState[x-1][y]!=antiSign||game.fieldState[x][y+1]!=antiSign)){wall.addPoint(game,x-1,y+1,game.fieldOfChains);}}catch(Exception e){}
			try{if(game.fieldState[x+1][y-1]==sign&&(game.fieldState[x+1][y]!=antiSign||game.fieldState[x][y-1]!=antiSign)){wall.addPoint(game,x+1,y-1,game.fieldOfChains);}}catch(Exception e){}
			try{if(game.fieldState[x+1][y+1]==sign&&(game.fieldState[x+1][y]!=antiSign||game.fieldState[x][y+1]!=antiSign)){wall.addPoint(game,x+1,y+1,game.fieldOfChains);}}catch(Exception e){}
			try{if(game.fieldState[x+1][y]==sign){wall.addPoint(game,x+1,y,game.fieldOfChains);}}catch(Exception e){}
			try{if(game.fieldState[x][y+1]==sign){wall.addPoint(game,x,y+1,game.fieldOfChains);}}catch(Exception e){}
			try{if(game.fieldState[x-1][y]==sign){wall.addPoint(game,x-1,y,game.fieldOfChains);}}catch(Exception e){}
			try{if(game.fieldState[x][y-1]==sign){wall.addPoint(game,x,y-1,game.fieldOfChains);}}catch(Exception e){}
			
			//дальние точки, удаленные на 2
			try{if(game.fieldState[x+1][y]==templateDotType_EMPTY&&game.fieldState[x+2][y]==sign){wall.addPoint(game,x+2,y,game.fieldOfChains);/*wall.addConnection(game,x+2,y+1,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x][y+1]==templateDotType_EMPTY&&game.fieldState[x][y+2]==sign){wall.addPoint(game,x,y+2,game.fieldOfChains);/*wall.addConnection(game,x+1,y+2,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x-1][y]==templateDotType_EMPTY&&game.fieldState[x-2][y]==sign){wall.addPoint(game,x-2,y,game.fieldOfChains);/*wall.addConnection(game,x,y+1,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x][y-1]==templateDotType_EMPTY&&game.fieldState[x][y-2]==sign){wall.addPoint(game,x,y-2,game.fieldOfChains);/*wall.addConnection(game,x+1,y,game.fieldOfWalls);*/}}catch(Exception e){}	
			//дальние точки, удаленные на 2 наискось
			try{if(game.fieldState[x+1][y+1]==templateDotType_EMPTY&&game.fieldState[x+2][y+2]==sign){wall.addPoint(game,x+2,y+2,game.fieldOfChains);/*wall.addConnection(game,x+2,y+2,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x-1][y+1]==templateDotType_EMPTY&&game.fieldState[x-2][y+2]==sign){wall.addPoint(game,x-2,y+2,game.fieldOfChains);/*wall.addConnection(game,x,y+2,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x+1][y-1]==templateDotType_EMPTY&&game.fieldState[x+2][y-2]==sign){wall.addPoint(game,x+2,y-2,game.fieldOfChains);/*wall.addConnection(game,x+2,y,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x-1][y-1]==templateDotType_EMPTY&&game.fieldState[x-2][y-2]==sign){wall.addPoint(game,x-2,y-2,game.fieldOfChains);/*wall.addConnection(game,x,y,game.fieldOfWalls);*/}}catch(Exception e){}
			//дальние точки, удаленные на 2 конем
			try{if((game.fieldState[x+1][y]==templateDotType_EMPTY||game.fieldState[x+1][y-1]==templateDotType_EMPTY)&&game.fieldState[x+2][y-1]==sign){wall.addPoint(game,x+2,y-1,game.fieldOfChains);/*if(game.fieldState[x+1][y]==templateDotTypeNULL)wall.addConnection(game,x+2,y+1,game.fieldOfWalls);if(game.fieldState[x+1][y-1]==templateDotTypeNULL)wall.addConnection(game,x+2,y,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if((game.fieldState[x+1][y]==templateDotType_EMPTY||game.fieldState[x+1][y+1]==templateDotType_EMPTY)&&game.fieldState[x+2][y+1]==sign){wall.addPoint(game,x+2,y+1,game.fieldOfChains);/*if(game.fieldState[x+1][y]==templateDotTypeNULL)wall.addConnection(game,x+2,y+1,game.fieldOfWalls);if(game.fieldState[x+1][y+1]==templateDotTypeNULL)wall.addConnection(game,x+2,y+2,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if((game.fieldState[x][y+1]==templateDotType_EMPTY||game.fieldState[x+1][y+1]==templateDotType_EMPTY)&&game.fieldState[x+1][y+2]==sign){wall.addPoint(game,x+1,y+2,game.fieldOfChains);/*if(game.fieldState[x][y+1]==templateDotTypeNULL)wall.addConnection(game,x+1,y+2,game.fieldOfWalls);if(game.fieldState[x+1][y+1]==templateDotTypeNULL)wall.addConnection(game,x+2,y+2,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if((game.fieldState[x][y+1]==templateDotType_EMPTY||game.fieldState[x-1][y+1]==templateDotType_EMPTY)&&game.fieldState[x-1][y+2]==sign){wall.addPoint(game,x-1,y+2,game.fieldOfChains);/*if(game.fieldState[x][y+1]==templateDotTypeNULL)wall.addConnection(game,x+1,y+2,game.fieldOfWalls);if(game.fieldState[x-1][y+1]==templateDotTypeNULL)wall.addConnection(game,x,y+2,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if((game.fieldState[x-1][y]==templateDotType_EMPTY||game.fieldState[x-1][y+1]==templateDotType_EMPTY)&&game.fieldState[x-2][y+1]==sign){wall.addPoint(game,x-2,y+21,game.fieldOfChains);/*if(game.fieldState[x-1][y]==templateDotTypeNULL)wall.addConnection(game,x,y+1,game.fieldOfWalls);if(game.fieldState[x-1][y+1]==templateDotTypeNULL)wall.addConnection(game,x,y+2,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if((game.fieldState[x-1][y]==templateDotType_EMPTY||game.fieldState[x-1][y-1]==templateDotType_EMPTY)&&game.fieldState[x-2][y-1]==sign){wall.addPoint(game,x-2,y-1,game.fieldOfChains);/*if(game.fieldState[x-1][y]==templateDotTypeNULL)wall.addConnection(game,x,y+1,game.fieldOfWalls);if(game.fieldState[x-1][y-1]==templateDotTypeNULL)wall.addConnection(game,x,y,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if((game.fieldState[x][y-1]==templateDotType_EMPTY||game.fieldState[x-1][y-1]==templateDotType_EMPTY)&&game.fieldState[x-1][y-2]==sign){wall.addPoint(game,x-1,y-2,game.fieldOfChains);/*if(game.fieldState[x][y-1]==templateDotTypeNULL)wall.addConnection(game,x+1,y,game.fieldOfWalls);if(game.fieldState[x-1][y-1]==templateDotTypeNULL)wall.addConnection(game,x,y,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if((game.fieldState[x][y-1]==templateDotType_EMPTY||game.fieldState[x+1][y-1]==templateDotType_EMPTY)&&game.fieldState[x+1][y-2]==sign){wall.addPoint(game,x+1,y-2,game.fieldOfChains);/*if(game.fieldState[x][y-1]==templateDotTypeNULL)wall.addConnection(game,x+1,y,game.fieldOfWalls);if(game.fieldState[x+1][y-1]==templateDotTypeNULL)wall.addConnection(game,x+2,y,game.fieldOfWalls);*/}}catch(Exception e){}
		
			//дальние точки, удаленные на 3
			try{if(game.fieldState[x+1][y]==templateDotType_EMPTY&&game.fieldState[x+2][y]==templateDotType_EMPTY&&game.fieldState[x+3][y]==sign){wall.addPoint(game,x+3,y,game.fieldOfChains);/*wall.addConnection(game,x+3,y+1,game.fieldOfWalls);wall.addConnection(game,x+2,y+1,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x][y+1]==templateDotType_EMPTY&&game.fieldState[x][y+2]==templateDotType_EMPTY&&game.fieldState[x][y+3]==sign){wall.addPoint(game,x,y+3,game.fieldOfChains);/*wall.addConnection(game,x+1,y+3,game.fieldOfWalls);wall.addConnection(game,x+1,y+2,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x-1][y]==templateDotType_EMPTY&&game.fieldState[x-2][y]==templateDotType_EMPTY&&game.fieldState[x-3][y]==sign){wall.addPoint(game,x-3,y,game.fieldOfChains);/*wall.addConnection(game,x-1,y+1,game.fieldOfWalls);wall.addConnection(game,x,y+1,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x][y-1]==templateDotType_EMPTY&&game.fieldState[x][y-2]==templateDotType_EMPTY&&game.fieldState[x][y-3]==sign){wall.addPoint(game,x,y-3,game.fieldOfChains);/*wall.addConnection(game,x+1,y-1,game.fieldOfWalls);wall.addConnection(game,x+1,y,game.fieldOfWalls);*/}}catch(Exception e){}
			//дальние точки, удаленные на 4
			try{if(game.fieldState[x+1][y]==templateDotType_EMPTY&&game.fieldState[x+2][y]==templateDotType_EMPTY&&game.fieldState[x+3][y]==templateDotType_EMPTY&&game.fieldState[x+4][y]==sign){wall.addPoint(game,x+4,y,game.fieldOfChains);/*wall.addConnection(game,x+3,y+1,game.fieldOfWalls);wall.addConnection(game,x+2,y+1,game.fieldOfWalls);wall.addConnection(game,x+4,y+1,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x][y+1]==templateDotType_EMPTY&&game.fieldState[x][y+2]==templateDotType_EMPTY&&game.fieldState[x][y+3]==templateDotType_EMPTY&&game.fieldState[x][y+4]==sign){wall.addPoint(game,x,y+4,game.fieldOfChains);/*wall.addConnection(game,x+1,y+3,game.fieldOfWalls);wall.addConnection(game,x+1,y+2,game.fieldOfWalls);wall.addConnection(game,x+1,y+4,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x-1][y]==templateDotType_EMPTY&&game.fieldState[x-2][y]==templateDotType_EMPTY&&game.fieldState[x-3][y]==templateDotType_EMPTY&&game.fieldState[x-4][y]==sign){wall.addPoint(game,x-4,y,game.fieldOfChains);/*wall.addConnection(game,x-1,y+1,game.fieldOfWalls);wall.addConnection(game,x,y+1,game.fieldOfWalls);wall.addConnection(game,x-2,y+1,game.fieldOfWalls);*/}}catch(Exception e){}
			try{if(game.fieldState[x][y-1]==templateDotType_EMPTY&&game.fieldState[x][y-2]==templateDotType_EMPTY&&game.fieldState[x][y-3]==templateDotType_EMPTY&&game.fieldState[x][y-4]==sign){wall.addPoint(game,x,y-4,game.fieldOfChains);/*wall.addConnection(game,x+1,y-1,game.fieldOfWalls);wall.addConnection(game,x+1,y,game.fieldOfWalls);wall.addConnection(game,x+1,y-2,game.fieldOfWalls);*/}}catch(Exception e){}
			//дальние точки, удаленные на 3 конем
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
	
	//раньше этот метод искал разрывы в цепях между абстрактными точками
	/*private boolean isRelatedForWallSearch(Game game,int x1,int y1,int x2,int y2,Wall wall){
		
		if(x1==x2){//вертикально
			if(y1<y2){//сверху вниз
				for(int i=y1+1;i<y2;i++)if(game.fieldOfWalls[x1][i].startsWith("P")||game.fieldOfWalls[x1][i].startsWith("A")||game.fieldOfWalls[x1][i].startsWith("C"))return false;
				for(int i=y1+1;i<y2;i++)wall.addLink(game,x1, i, game.fieldOfWalls);
				return true;
			}else{//снизу вверх
				for(int i=y1-1;i>y2;i--)if(game.fieldOfWalls[x1][i].startsWith("P")||game.fieldOfWalls[x1][i].startsWith("A")||game.fieldOfWalls[x1][i].startsWith("C"))return false;
				for(int i=y1-1;i>y2;i--)wall.addLink(game,x1, i, game.fieldOfWalls);
				return true;
			}
		}
		else if(y1==y2){//горизонтально
			if(x1<x2){//слева направо
				for(int i=x1+1;i<x2;i++)if(game.fieldOfWalls[i][y1].startsWith("P")||game.fieldOfWalls[i][y1].startsWith("A")||game.fieldOfWalls[i][y1].startsWith("C"))return false;
				for(int i=x1+1;i<x2;i++)wall.addLink(game,i, y1, game.fieldOfWalls);
				return true;
			}else{//справа налево
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
	
	//нахождение ближайшей к центру поля точки цвета sign
	private Point getCentralPointForChainSearch(Game game,int sign){
		int centralX=(int)(game.sizeX/2.0);
		int centralY=(int)(game.sizeY/2.0);
		
		int depth=(int)(Math.max(game.sizeX, game.sizeY)/2.0);
			
		for(int i=0;i<=depth;i++){//нахождение крайних красных точек на поле
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
