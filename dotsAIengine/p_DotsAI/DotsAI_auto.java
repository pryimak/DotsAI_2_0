//����� ������� ���� ��� ������������ (� ��������� ������ ����� ���������� ���).
//��� ���� �� ������ �������.
//���� ������ - ������������ ������������ ������������� ���� ������ ��

package p_DotsAI;

import java.awt.Point;
import java.util.Random;

import p_DotsAI.Protocol.Game;
import p_GUI.EngineFrame;

public class DotsAI_auto implements Runnable{
	public Protocol protocol;//������ ��� ������ ���� �� � ���������� ����
	Thread t1,t2,t3,t4,t5;//������ ���� ����������� ��������� �������
	int gameIdx=1;//������ ����
	Random rand=new Random();//��������� ��������� �����
	public byte sizeX=39,sizeY=32;//������ ����
	int redX1=19,redY1=17,blueX1=19,blueY1=16,redX2=20,redY2=16,blueX2=20,blueY2=17;//���� ��������� �������
	
	DotsAI_auto(){
		
		protocol=new Protocol();		
		
		//�������� ���� ���
		protocol.addNewGame(1,EnemyType.RANDOM,sizeX,sizeY,redX1,redY1,blueX1,blueY1,redX2,redY2,blueX2,blueY2);
		protocol.addNewGame(2,EnemyType.RANDOM,sizeX,sizeY,redX1,redY1,blueX1,blueY1,redX2,redY2,blueX2,blueY2);
		protocol.addNewGame(3,EnemyType.RANDOM,sizeX,sizeY,redX1,redY1,blueX1,blueY1,redX2,redY2,blueX2,blueY2);
		protocol.addNewGame(4,EnemyType.RANDOM,sizeX,sizeY,redX1,redY1,blueX1,blueY1,redX2,redY2,blueX2,blueY2);
		protocol.addNewGame(5,EnemyType.RANDOM,sizeX,sizeY,redX1,redY1,blueX1,blueY1,redX2,redY2,blueX2,blueY2);

		//��� ������ ���� ��������� ����
		new EngineFrame(protocol.getGame(1).gameField);
		new EngineFrame(protocol.getGame(2).gameField);
		new EngineFrame(protocol.getGame(3).gameField);
		new EngineFrame(protocol.getGame(4).gameField);
		new EngineFrame(protocol.getGame(5).gameField);
		
		//������ ���� ����������� ��������� �������
		t1=new Thread(this);
		t2=new Thread(this);
		t3=new Thread(this);
		t4=new Thread(this);
		t5=new Thread(this);
		
		//������ �������
		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
	}
		
	public void run(){
		int gameIdx0=gameIdx;
		System.out.println("start game "+gameIdx0);
		
		//������ ���� ����� �����
		if(gameIdx0==1){gameIdx++;}
		else if(gameIdx0==2){gameIdx++;try{t2.sleep(1000);}catch(InterruptedException e){}}
		else if(gameIdx0==3){gameIdx++;try{t3.sleep(2000);}catch(InterruptedException e){}}
		else if(gameIdx0==4){gameIdx++;try{t4.sleep(3000);}catch(InterruptedException e){}}
		else if(gameIdx0==5){gameIdx++;try{t5.sleep(4000);}catch(InterruptedException e){}}		
		
		Point point=protocol.getAImove(gameIdx0,10,null);//�� ������ ������ ��� � ����
		
		Game game=protocol.getGame(gameIdx0);//����� ������� ����
		
		for(;;){
			//����� � ���������� ����� �������
			byte x=-1,y=-1;
			while(!game.gameField.canAddMove(x,y)){
				x=(byte)(Math.abs(rand.nextInt(game.sizeX))+1);
				y=(byte)(Math.abs(rand.nextInt(game.sizeY))+1);
			}
			protocol.addAIenemyMove(gameIdx0,x,y);
			System.out.println("random move "+x+","+y);
			
			//����� ���� ��
			point=protocol.getAImove(gameIdx0,10,null);
			//�������� ��������� ����
			if((point.x==-2&point.y==-2)|//�� ����������, �� ����������� ����
					(point.x==-3&point.y==-3)|//������� ����������, �� �������
					(point.x==-4&point.y==-4)//�� �������, �.�. ������� ����� �����
				){
				break;
			}
			System.out.println("AI move "+point.x+","+point.y);
			System.out.println("score AI:random "+protocol.getGame(gameIdx0).gameField.scoreRed+","+protocol.getGame(gameIdx0).gameField.scoreBlue);
			System.out.println("moves "+protocol.getGame(gameIdx0).movesCount);
			
			System.out.println("======================================="+(int)(Runtime.getRuntime().freeMemory()/1000000.0));
		}
		System.out.println("end game"+gameIdx0);
	}
	
	public static void main(String[] args){new DotsAI_auto();}

}
