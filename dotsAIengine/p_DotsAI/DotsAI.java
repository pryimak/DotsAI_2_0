//����� DotsAI �������� ������� ������� ���������, � ���� ���������� ������ ���������
//� �� ������������ ���������� �������� ���� ���������

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
	
	public GameGUI gameGUI=new GameGUI();//��������������� ����� ��� ����������� ������������ ���������� �������� ���� ���������
	public GameFieldGUI gameFieldGUI;//����� ��� ��������� ���� � �����
	public DotsTemplateEditor dotsTemplateEditor=null;//�������� ��������
	byte preX=0,preY=0,x,y;//����� ������� - ���������� � �������
	byte moveAIx=99,moveAIy=99;//��� ��, ������� ����� 99
	Point point;//��� ��
	public Protocol protocol;//���� ��� �� � ������ ������� ����
	
	//���������� ��� ����������� �������
	long start;
	public long AItime;
	public int AImovesCount,AItotalTime;
	public double AIavgTime;
	
	public boolean isPause=false;//���� �� ����� � ����
	Random rand=new Random();//��������� ��������� �����
	public JLabel label=new JLabel();//����� � �����������
	public JLabel labelCoordinates=new JLabel();//����� � ������������
	
	public JMenuItem itemNewAIvsHuman,itemNewAIvsRandom,itemNewAIvsAI,itemOpenGame,itemSaveGame,itemAboutGame;//�������� ����
	public Thread t=new Thread(this);//�����, ����������� ����� ����
	public byte sizeX=39,sizeY=32;//������ ����
	int redX1=18,redY1=16,blueX1=18,blueY1=15,redX2=19,redY2=15,blueX2=19,blueY2=16;//���� ��������� �������
	public int gameIdx=0;//������ ���� ������ ��
	
public DotsAI(){
	
	//����� ���������� � ���������
	AboutGameFrame aboutGameFrame=new AboutGameFrame();
	aboutGameFrame.frame.setTitle("��������. ��������� ����� 10 ������...");
	
	protocol=new Protocol();//�������� ����� �������� ��� � ������ ����� ��
	
	protocol.addNewGame(++gameIdx,EnemyType.HUMAN,sizeX,sizeY,redX1,redY1,blueX1,blueY1,redX2,redY2,blueX2,blueY2);//�������� ����� ����
	
	//�������� �������� ���� ���������
	new Pattern_JFrame(this,Protocol.appName+" "+Protocol.appVersion,false,GameFieldGUI.cellSize*(protocol.getGame(gameIdx).sizeX+1)+GameGUI.offsetX-25,
			GameFieldGUI.cellSize*(protocol.getGame(gameIdx).sizeY+1)+GameGUI.offsetY-35,new Color(255,255,255));
	
	gameFieldGUI=new GameFieldGUI(this.getGraphics(),GameGUI.offsetX,GameGUI.offsetY);//��� ��������� ���� � �����
	
	this.setIconImage(Pattern_Resources.icon);//���������� ������ ��� ����
	
	//��� ��������� ������� ������� � ����������� �������, � ����� ������� � ����� ���������
	this.addMouseListener(this);
	this.addWindowListener(this);
	this.addMouseMotionListener(this);
	
	//�������� ����
	JMenuBar menu=new JMenuBar();
	JMenu item=new JMenu("����");
	itemNewAIvsHuman=new JMenuItem("����� ���� AI vs Human");
	itemNewAIvsAI=new JMenuItem("����� ���� AI vs AI");
	itemNewAIvsRandom=new JMenuItem("����� ���� AI vs Random");
	itemOpenGame=new JMenuItem("������� ����");
	itemSaveGame=new JMenuItem("��������� ����");
	itemAboutGame=new JMenuItem("� ���������");
	
	//���������� ��������� � ����
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
	
	aboutGameFrame.frame.dispose();//������� ���� � ����������� � ���������
	
	this.move(DotsAI.this.getX(), DotsAI.this.getY()-150);//�������� ������� ���� ���������
	
	//������� �������� ��������
	dotsTemplateEditor=new DotsTemplateEditor(protocol.templateEngine,DotsAI.this.getX(),DotsAI.this.getY()+DotsAI.this.getHeight(),this);
	dotsTemplateEditor.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	
	gameGUI.newGame(this);//�������������� ����������� ���������
	
	//���������� ������ ���������� �����
	GameManagement gameManagementFrame=new GameManagement(this);
	menu.add(gameManagementFrame.buttonPause);
	menu.add(gameManagementFrame.buttonBackLength1);
	menu.add(gameManagementFrame.buttonBackLength5);
	menu.add(gameManagementFrame.buttonCreateTemplate);
	
	aiMove(null);//������� ������ ��� ��
	
	//�� ������� ���� ������� ���� � ����������� �� ����
	itemAboutGame.addMouseListener(new MouseListener() {
		public void mouseClicked(MouseEvent arg0) {}
		public void mouseEntered(MouseEvent arg0) {}
		public void mouseExited(MouseEvent arg0) {}
		public void mousePressed(MouseEvent arg0) {new AboutGameFrame();}
		public void mouseReleased(MouseEvent arg0) {}
	});
	
	//�� ������� ���� ������� ������ ����������� ��� � ������������ ��������� ����
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
	
	//�� ������� ���� ��������� ������� ����
	itemSaveGame.addMouseListener(new MouseListener() {
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {
			new Pattern_ReadAndWriteFile().WriteTxtFile(new File(Pattern_Resources.savedGames+"game ("+new Date().toLocaleString().replaceAll(":", "-")+").pai").toString(),getGameMoves());
		}
		public void mouseExited(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
	});
	
	//�� ������� ���� ������ ����� ���� �������� � ��
	itemNewAIvsHuman.addMouseListener(new MouseListener() {	
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {
			//������� ����� ���� � ������� ������ ��� ��
			t.stop();
			protocol.addNewGame(++gameIdx,EnemyType.HUMAN,sizeX,sizeY,redX1,redY1,blueX1,blueY1,redX2,redY2,blueX2,blueY2);
			gameGUI.newGame(DotsAI.this);
			aiMove(null);
			t=new Thread(DotsAI.this);
			t.start();
		}
		public void mouseExited(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}});

	//�� ������� ���� ������ ����� ���� ������� � ��
	itemNewAIvsRandom.addMouseListener(new MouseListener(){	
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {
			//������� ����� ����
			t.stop();
			protocol.addNewGame(++gameIdx,EnemyType.RANDOM,sizeX,sizeY,redX1,redY1,blueX1,blueY1,redX2,redY2,blueX2,blueY2);
			gameGUI.newGame(DotsAI.this);
			t=new Thread(DotsAI.this);
			t.start();
		}
		public void mouseExited(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}});
	
	//�� ������� ���� ������ ����� ���� �� � ��
	itemNewAIvsAI.addMouseListener(new MouseListener(){	
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {
			//������� ��� ���� ��� ������� ��
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

//�� ������� ���� ������� ��� �������� � ����� �������� ��� ��
public void mousePressed(MouseEvent me){
	x=getMouseClickX(me);
	y=getMouseClickY(me);
	if(protocol.getGame(gameIdx).gameField.canAddMove(x,y)&protocol.getGame(gameIdx).enemyType==EnemyType.HUMAN&(protocol.getGame(gameIdx).gameField.lastDotType==GameField.RED|protocol.getGame(gameIdx).getMovesCount()==0)){
		AIenemyMove(x,y);//������� ��� ��������
		aiMove(null);//����� �������� ��� ��
	}
}

public void windowActivated(WindowEvent e){
	t.stop();
	t=new Thread(this);
	t.start();
}

public void windowClosing(WindowEvent e){//��� �������� ���� ������������� ��������� ���� ��������
	protocol.templateEngine.saveTemplateBase();
}

public static void main(String[] args){//������ ���������� ���������
	new DotsAI();
}

//�������������� ���������� ����� �� ������ �� � �� ������ �������
public void run(){try{	
	try{t.sleep(500);}catch(Exception e){}

	if(protocol.getGame(gameIdx).enemyType==EnemyType.RANDOM|protocol.getGame(gameIdx).enemyType==EnemyType.AI){
		
		for(;;){try{
			
			if(isPause){try{t.sleep(100);}catch(Exception e){}continue;}
			
			aiMove(null);
			
			if(protocol.getGame(gameIdx).enemyType==EnemyType.RANDOM){//���� ���� ������ �������, ����� ��������� ���
				while(!protocol.getGame(gameIdx).gameField.canAddMove(moveAIx,moveAIy)){
					moveAIx=(byte) rand.nextInt(protocol.getGame(gameIdx).sizeX);
					moveAIy=(byte) rand.nextInt(protocol.getGame(gameIdx).sizeY);
				}
			}else if(protocol.getGame(gameIdx).enemyType==EnemyType.AI){//���� ���� ������ ��, ����� ��� ������� ��
				protocol.addAIenemyMove(-gameIdx,point.x,point.y);
				Point p=protocol.getAImove(-gameIdx,10,null);
				moveAIx=(byte) p.x;
				moveAIy=(byte) p.y;
			}
			AIenemyMove(moveAIx,moveAIy);//������� ��� ��������� ��

			//�������� �� ���������� ���� - ������ ��� �����������
			if((protocol.getGame(gameIdx).lastAimove.x==-2&protocol.getGame(gameIdx).lastAimove.y==-2)|(protocol.getGame(gameIdx).lastAimove.x==-3&protocol.getGame(gameIdx).lastAimove.y==-3)|(protocol.getGame(gameIdx).lastAimove.x==-4&protocol.getGame(gameIdx).lastAimove.y==-4)){
				break;
			}
			//��������� ���� ���� ������� ������� ����� �����
			if(protocol.getGame(gameIdx).getMovesCount()>(protocol.getGame(gameIdx).sizeX*protocol.getGame(gameIdx).sizeY-100)){
				break;
			}
		}catch(Exception exc){break;}}
	}

	t.stop();
}catch(Exception e){}}

//��������� ��� �� � ��������� ���� �� ����������
public void aiMove(Point recommendedMove){	
	
	//����� ��� �� � �������� �����
	start=System.currentTimeMillis();
	point=protocol.getAImove(gameIdx,10,recommendedMove);
	AItime=System.currentTimeMillis()-start;
	AItotalTime+=AItime;
	AImovesCount++;
	AIavgTime = new BigDecimal(AItotalTime/(double)AImovesCount/1000.0).setScale(2, RoundingMode.HALF_UP).doubleValue();
	
	//��������� ���� �� ����������
	boolean isGameEnd=false;
	if((point.x==-2&point.y==-2)|//�� ����������, �� ����������� ����
			(point.x==-3&point.y==-3)|//������� ����������, �� �������
			(point.x==-4&point.y==-4)//�� �������, �.�. ������� ����� �����
		){
		isGameEnd=true;
	}
	
	//���� ���� ���������, �� ������ ����� �� ���� ����������� ������� ������������
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
	
	gameGUI.gameRepaint(DotsAI.this,true);//������������ ����
	
	//������� ���� ��� �� ����������
	if(isGameEnd){
		protocol.deleteGame(gameIdx);
		System.out.println("delete game "+gameIdx);
	}
}

//������� ��� ��������� �� � ������������ ���� ����
public void AIenemyMove(int x,int y){
	protocol.addAIenemyMove(gameIdx,x,y);
	gameGUI.gameRepaint(DotsAI.this,true);
}

//���������� ������ �� ������� ������� � ��������� �� ����������
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

//�������� ������ ����� � ���� � ���� ������ ��� ������������ ����������
public String getGameMoves(){
	String moves="";
	for(int i=0;i<protocol.getGame(gameIdx).movesMas.length;i++){
		if(protocol.getGame(gameIdx).movesMas[i][0]==0&protocol.getGame(gameIdx).movesMas[i][1]==0)break;
		moves+=protocol.getGame(gameIdx).movesMas[i][0]+"-"+protocol.getGame(gameIdx).movesMas[i][1]+";";
	}
	return moves;
}

//��������� ���� � ���� ��� �������� �� ����� ��� ������ �����
public void setGameMoves(String moves,int back){
	protocol.addNewGame(++gameIdx,EnemyType.HUMAN,sizeX,sizeY,redX1,redY1,blueX1,blueY1,redX2,redY2,blueX2,blueY2);
	gameGUI.newGame(DotsAI.this);
	
	//���������� ������� ����� ����� ���� �������
	int totalMoves=0;
	for(int i=0;i<moves.length();i++){
		if(moves.substring(i, i+1).equals(";"))totalMoves++;
	}
	
	//����� � ��������� ����
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

//�������� ���������� ���� �� ��������� ������� ��� ������� ������ ����
byte getMouseClickX(MouseEvent me){return (byte)(((double)me.getX()+8-GameGUI.offsetX)/(double)GameFieldGUI.cellSize);};
byte getMouseClickY(MouseEvent me){return (byte)(((double)me.getY()+8-GameGUI.offsetY)/(double)GameFieldGUI.cellSize);};

//�������������� ������, ������� ���������� ���� ��������
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