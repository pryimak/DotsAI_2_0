//Класс DotsAI является главным классом программы, с него начинается работа программы
//и он обеспечивает функционал главного окна программы

package p_DotsAI;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import p_GUI.AboutGameFrame;
import p_GUI.GameField;
import p_GUI.GameFieldGUI;
import p_GUI.GameGUI;
import p_GUI.GameManagement;
import p_JavaPatterns.Pattern_JFrame;
import p_JavaPatterns.Pattern_OpenFile;
import p_JavaPatterns.Pattern_ReadAndWriteFile;
import p_JavaPatterns.Pattern_Resources;
import p_TemplateEditor.DotsTemplateEditor;

public class DotsAI extends JFrame implements Runnable,MouseListener, WindowListener, MouseMotionListener{
	
	public GameGUI gameGUI=new GameGUI();//вспомагательный класс для обеспечения графического интерфейса главного окна программы
	public GameFieldGUI gameFieldGUI;//класс для рисования поля и точек
	public DotsTemplateEditor dotsTemplateEditor=null;//редактор шаблонов
	byte preX=0,preY=0,x,y;//точки курсора - предыдущая и текущая
	byte moveAIx=99,moveAIy=99;//ход ИИ, вначале равен 99
	Point point;//ход ИИ
	public Protocol protocol;//ищет ход ИИ и хранит текущие игры
	
	//переменные для определения времени
	long start;
	public long AItime;
	public int AImovesCount,AItotalTime;
	public double AIavgTime;
	
	public boolean isPause=false;//есть ли пауза в игре
	Random rand=new Random();//генератор случайных чисел
	public JLabel label=new JLabel();//метка с информацией
	public JLabel labelCoordinates=new JLabel();//метка с координатами
	
	public JMenuItem itemNewAIvsHuman,itemNewAIvsRandom,itemNewAIvsAI,itemOpenGame,itemSaveGame,itemAboutGame;//элементы меню
	public Thread t=new Thread(this);//поток, управляющий ходом игры
	public byte sizeX=39,sizeY=32;//размер поля
	int redX1=18,redY1=16,blueX1=18,blueY1=15,redX2=19,redY2=15,blueX2=19,blueY2=16;//ходы стартовой позиции
	public int gameIdx=0;//индекс игры против ИИ
	
public DotsAI(){
	
	//вывод информации о программе
	AboutGameFrame aboutGameFrame=new AboutGameFrame();
	aboutGameFrame.frame.setTitle("Загрузка. Подождите около 10 секунд...");
	
	protocol=new Protocol();//основной класс хранения игр и поиска ходов ИИ
	
	protocol.addNewGame(++gameIdx,EnemyType.HUMAN,sizeX,sizeY,redX1,redY1,blueX1,blueY1,redX2,redY2,blueX2,blueY2);//создание новой игры
	
	//создание главного окна программы
	new Pattern_JFrame(this,Protocol.appName+" "+Protocol.appVersion,false,GameFieldGUI.cellSize*(protocol.getGame(gameIdx).sizeX+1)+GameGUI.offsetX-25,
			GameFieldGUI.cellSize*(protocol.getGame(gameIdx).sizeY+1)+GameGUI.offsetY-35,new Color(255,255,255));
	
	gameFieldGUI=new GameFieldGUI(this.getGraphics(),GameGUI.offsetX,GameGUI.offsetY);//для рисования поля и точек
	
	this.setIconImage(Pattern_Resources.icon);//добавление иконки для окна
	
	//для обработки событий нажатия и перемещения курсора, а также событий с окном программы
	this.addMouseListener(this);
	this.addWindowListener(this);
	this.addMouseMotionListener(this);
	
	//создание меню
	JMenuBar menu=new JMenuBar();
	JMenu item=new JMenu("Файл");
	itemNewAIvsHuman=new JMenuItem("Новая игра AI vs Human");
	itemNewAIvsAI=new JMenuItem("Новая игра AI vs AI");
	itemNewAIvsRandom=new JMenuItem("Новая игра AI vs Random");
	itemOpenGame=new JMenuItem("Открыть игру");
	itemSaveGame=new JMenuItem("Сохранить игру");
	itemAboutGame=new JMenuItem("О программе");
	
	//добавление элементов в меню
	menu.add(item);
	item.add(itemNewAIvsHuman);
	item.add(itemNewAIvsAI);
	item.add(itemNewAIvsRandom);
	item.addSeparator();
	item.add(itemOpenGame);
	item.add(itemSaveGame);
	item.addSeparator();
	item.add(itemAboutGame);
	menu.add(label);
	menu.add(labelCoordinates);
	this.setJMenuBar(menu);
	
	aboutGameFrame.frame.dispose();//закрыть окно с информацией о программе
	
	this.move(DotsAI.this.getX(), DotsAI.this.getY()-150);//сдвинуть главное окно программы
	
	//создать редактор шаблонов
	dotsTemplateEditor=new DotsTemplateEditor(protocol.templateEngine,DotsAI.this.getX(),DotsAI.this.getY()+DotsAI.this.getHeight(),this);
	dotsTemplateEditor.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	
	gameGUI.newGame(this);//дополнительный графический интерфейс
	
	//добавление кнопок управления игрой
	GameManagement gameManagementFrame=new GameManagement(this);
	menu.add(gameManagementFrame.buttonPause);
	menu.add(gameManagementFrame.buttonBackLength1);
	menu.add(gameManagementFrame.buttonBackLength5);
	menu.add(gameManagementFrame.buttonCreateTemplate);
	
	aiMove(null);//сделать первый ход ИИ
	
	//по нажатию меню вывести окно с информацией об игре
	itemAboutGame.addMouseListener(new MouseListener() {
		public void mouseClicked(MouseEvent arg0) {}
		public void mouseEntered(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}
		public void mousePressed(MouseEvent arg0) {new AboutGameFrame();}
		public void mouseReleased(MouseEvent arg0) {}
	});
	
	//по нажатию меню открыть список сохраненных игр с возможностью загрузить игру
	itemOpenGame.addMouseListener(new MouseListener() {
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {
			t.stop();			
			setGameMoves(new Pattern_OpenFile(DotsAI.this,new String[]{".pai"}).getFileContent(),0);			
			t=new Thread(DotsAI.this);
			t.start();
		}
		public void mouseExited(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
	});
	
	//по нажатию меню сохранить текущую игру
	itemSaveGame.addMouseListener(new MouseListener() {
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {
			new Pattern_ReadAndWriteFile().WriteTxtFile(new File(Pattern_Resources.savedGames+"game ("+new Date().toLocaleString().replaceAll(":", "-")+").pai").toString(),getGameMoves());
		}
		public void mouseExited(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
	});
	
	//по нажатию меню начать новую игру человека с ИИ
	itemNewAIvsHuman.addMouseListener(new MouseListener() {	
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {
			//создать новую игру и сделать первый ход ИИ
			t.stop();
			protocol.addNewGame(++gameIdx,EnemyType.HUMAN,sizeX,sizeY,redX1,redY1,blueX1,blueY1,redX2,redY2,blueX2,blueY2);
			gameGUI.newGame(DotsAI.this);
			aiMove(null);
			t=new Thread(DotsAI.this);
			t.start();
		}
		public void mouseExited(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}});

	//по нажатию меню начать новую игру рандома с ИИ
	itemNewAIvsRandom.addMouseListener(new MouseListener(){	
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {
			//создать новую игру
			t.stop();
			protocol.addNewGame(++gameIdx,EnemyType.RANDOM,sizeX,sizeY,redX1,redY1,blueX1,blueY1,redX2,redY2,blueX2,blueY2);
			gameGUI.newGame(DotsAI.this);
			t=new Thread(DotsAI.this);
			t.start();
		}
		public void mouseExited(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}});
	
	//по нажатию меню начать новую игру ИИ с ИИ
	itemNewAIvsAI.addMouseListener(new MouseListener(){	
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {
			//создать две игры для каждого ИИ
			t.stop();
			protocol.addNewGame(++gameIdx,EnemyType.AI,sizeX,sizeY,redX1,redY1,blueX1,blueY1,redX2,redY2,blueX2,blueY2);
			protocol.addNewGame(-gameIdx,EnemyType.AI,sizeX,sizeY,blueX1,blueY1,redX1,redY1,blueX2,blueY2,redX2,redY2);
			gameGUI.newGame(DotsAI.this);			
			t=new Thread(DotsAI.this);
			t.start();
		}
		public void mouseExited(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}});
}

//по нажатию мыши сделать ход человека и найти ответный ход ИИ
public void mousePressed(MouseEvent me){
	x=getMouseClickX(me);
	y=getMouseClickY(me);
	if(protocol.getGame(gameIdx).gameField.canAddMove(x,y)&protocol.getGame(gameIdx).enemyType==EnemyType.HUMAN&(protocol.getGame(gameIdx).gameField.lastDotType==GameField.RED|protocol.getGame(gameIdx).getMovesCount()==0)){
		AIenemyMove(x,y);//сделать ход человека
		aiMove(null);//найти ответный ход ИИ
	}
}

public void windowActivated(WindowEvent e){
	t.stop();
	t=new Thread(this);
	t.start();
}

public void windowClosing(WindowEvent e){//при закрытии окна автоматически сохранить базу шаблонов
	protocol.templateEngine.saveTemplateBase();
}

public static void main(String[] args){//начать выполнение программы
	new DotsAI();
}

//автоматическое управление игрой ИИ против ИИ и ИИ против рандома
public void run(){try{	
	try{t.sleep(500);}catch(Exception e){}

	if(protocol.getGame(gameIdx).enemyType==EnemyType.RANDOM|protocol.getGame(gameIdx).enemyType==EnemyType.AI){
		
		for(;;){try{
			
			if(isPause){try{t.sleep(100);}catch(Exception e){}continue;}
			
			aiMove(null);
			
			if(protocol.getGame(gameIdx).enemyType==EnemyType.RANDOM){//если игра против рандома, найти случайный ход
				while(!protocol.getGame(gameIdx).gameField.canAddMove(moveAIx,moveAIy)){
					moveAIx=(byte) rand.nextInt(protocol.getGame(gameIdx).sizeX);
					moveAIy=(byte) rand.nextInt(protocol.getGame(gameIdx).sizeY);
				}
			}else if(protocol.getGame(gameIdx).enemyType==EnemyType.AI){//если игра против ИИ, найти ход второго ИИ
				protocol.addAIenemyMove(-gameIdx,point.x,point.y);
				Point p=protocol.getAImove(-gameIdx,10,null);
				moveAIx=(byte) p.x;
				moveAIy=(byte) p.y;
			}
			AIenemyMove(moveAIx,moveAIy);//сделать ход соперника ИИ

			//проверка на завершение игры - сдачей или заземлением
			if((protocol.getGame(gameIdx).lastAimove.x==-2&protocol.getGame(gameIdx).lastAimove.y==-2)|(protocol.getGame(gameIdx).lastAimove.x==-3&protocol.getGame(gameIdx).lastAimove.y==-3)|(protocol.getGame(gameIdx).lastAimove.x==-4&protocol.getGame(gameIdx).lastAimove.y==-4)){
				break;
			}
			//завершить игру если сделано слишком много ходов
			if(protocol.getGame(gameIdx).getMovesCount()>(protocol.getGame(gameIdx).sizeX*protocol.getGame(gameIdx).sizeY-100)){
				break;
			}
		}catch(Exception exc){break;}}
	}

	t.stop();
}catch(Exception e){}}

//выполнить ход ИИ и проверить игру на завершение
public void aiMove(Point recommendedMove){	
	
	//найти ход ИИ и замерять время
	start=System.currentTimeMillis();
	point=protocol.getAImove(gameIdx,10,recommendedMove);
	AItime=System.currentTimeMillis()-start;
	AItotalTime+=AItime;
	AImovesCount++;
	AIavgTime = new BigDecimal(AItotalTime/(double)AImovesCount/1000.0).setScale(2, RoundingMode.HALF_UP).doubleValue();
	
	//проверить игру на завершение
	boolean isGameEnd=false;
	if((point.x==-2&point.y==-2)|//ИИ заземлился, ИИ заканчивает игру
			(point.x==-3&point.y==-3)|//человек заземлился, ИИ сдается
			(point.x==-4&point.y==-4)//ИИ сдается, т.к. потерял много точек
		){
		isGameEnd=true;
	}
	
	//если игра завершена, то пустые места на поле заполняются точками проигравшего
	if(isGameEnd){
		protocol.getGame(gameIdx).gameField.setTerrytoryState(protocol.getGame(gameIdx));
		for(int moveAIx1=0;moveAIx1<protocol.getGame(gameIdx).sizeX;moveAIx1++){for(int moveAIy1=0;moveAIy1<protocol.getGame(gameIdx).sizeY;moveAIy1++){
			if(protocol.getGame(gameIdx).fieldTerrytoryState[moveAIx1][moveAIy1]==Protocol.templateDotType_EMPTY){
				if((point.x==-2&point.y==-2)|(point.x==-4&point.y==-4)){
					protocol.getGame(gameIdx).gameField.addMove(new Point(moveAIx1,moveAIy1),GameField.BLUE);
				}
				if(point.x==-3&point.y==-3){
					protocol.getGame(gameIdx).gameField.addMove(new Point(moveAIx1,moveAIy1),GameField.RED);
				}
			}
		}}
		DotsAI.this.label.setText(gameGUI.getLabelText(DotsAI.this));		
	}
	
	gameGUI.gameRepaint(DotsAI.this,true);//перерисовать игру
	
	//удалить игру при ее завершении
	if(isGameEnd){
		protocol.deleteGame(gameIdx);
		System.out.println("delete game "+gameIdx);
	}
}

//сделать ход соперника ИИ и перерисовать поле игры
public void AIenemyMove(int x,int y){
	protocol.addAIenemyMove(gameIdx,x,y);
	gameGUI.gameRepaint(DotsAI.this,true);
}

//подсветить курсор на текущей позиции и закрасить на предыдущей
public void mouseMoved(MouseEvent me){try{
	if(getMouseClickX(me)!=preX|getMouseClickY(me)!=preY){
		if(protocol.getGame(gameIdx).gameField.canAddMove(preX, preY)){
			gameFieldGUI.clearHint(preX, preY);		
		}		
		preX=getMouseClickX(me);preY=getMouseClickY(me);
		if(preX>=0&preX<protocol.getGame(gameIdx).sizeX&preY>=0&preY<protocol.getGame(gameIdx).sizeY)labelCoordinates.setText("<HTML><font color=gray>"+preX+":"+preY);
		if(protocol.getGame(gameIdx).gameField.canAddMove(preX, preY)){
			gameFieldGUI.drawHint(preX, preY);
		}
	}
}catch(Exception e){}}

//получить список ходов в игре в виде строки для последующего сохранения
public String getGameMoves(){
	String moves="";
	for(int i=0;i<protocol.getGame(gameIdx).movesMas.length;i++){
		if(protocol.getGame(gameIdx).movesMas[i][0]==0&protocol.getGame(gameIdx).movesMas[i][1]==0)break;
		moves+=protocol.getGame(gameIdx).movesMas[i][0]+"-"+protocol.getGame(gameIdx).movesMas[i][1]+";";
	}
	return moves;
}

//выполнить ходы в игре при загрузке из файла или отмене ходов
public void setGameMoves(String moves,int back){
	protocol.addNewGame(++gameIdx,EnemyType.HUMAN,sizeX,sizeY,redX1,redY1,blueX1,blueY1,redX2,redY2,blueX2,blueY2);
	gameGUI.newGame(DotsAI.this);
	
	//определить сколько всего ходов было сделано
	int totalMoves=0;
	for(int i=0;i<moves.length();i++){
		if(moves.substring(i, i+1).equals(";"))totalMoves++;
	}
	
	//найти и выполнить ходы
	int move=0;
	boolean isLastAiMove=false;
	for(int i=0;i<moves.length();i++){
		if(moves.substring(i, i+1).equals(";"))continue;
		
		int x=0,y=0;
		if(moves.substring(i+1, i+2).equals("-")){
			x=new Integer(moves.substring(i, i+1));i+=2;
			if(moves.substring(i+1, i+2).equals(";")){
				y=new Integer(moves.substring(i, i+1));i+=1;
			}
			else {y=new Integer(moves.substring(i, i+2));i+=2;}
		}else{
			x=new Integer(moves.substring(i, i+2));i+=3;
			if(moves.substring(i+1, i+2).equals(";")){
				y=new Integer(moves.substring(i, i+1));i+=1;
			}
			else {y=new Integer(moves.substring(i, i+2));i+=2;}
		}
		if(move%2==0){
			aiMove(new Point(x,y));
			isLastAiMove=true;
		}else{
			AIenemyMove(x,y);
			isLastAiMove=false;
		}
		move++;
		if(move>=totalMoves-back)break;
	}
	if(!isLastAiMove)aiMove(null);
}

//получить координаты поля из координат пикселя при нажатии кнопки мыши
byte getMouseClickX(MouseEvent me){return (byte)(((double)me.getX()+8-GameGUI.offsetX)/(double)GameFieldGUI.cellSize);};
byte getMouseClickY(MouseEvent me){return (byte)(((double)me.getY()+8-GameGUI.offsetY)/(double)GameFieldGUI.cellSize);};

//неиспользуемые методы, которые необходимо было добавить
public void windowClosed(WindowEvent e) {}
public void windowDeactivated(WindowEvent e) {}
public void windowDeiconified(WindowEvent e) {}
public void windowIconified(WindowEvent e) {}
public void windowOpened(WindowEvent e) {}
public void mouseClicked(MouseEvent me) {}
public void mouseEntered(MouseEvent me) {}
public void mouseExited(MouseEvent me) {}
public void mouseReleased(MouseEvent me) {}
public void mouseDragged(MouseEvent arg0) {}

}