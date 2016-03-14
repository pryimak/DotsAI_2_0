//����� ��������� � ������� ���� ��������� ��������� ������ ���������� �����.
//��� ������:
//- ��������� ���� �� �����, ��� ������� � ���� �� ������ ������� ��� ������ ��
//- �������� ���� �� 1 ��� 5 ����� �����, ��� ������� ��� ������ ���� �� ��� ����������� � ����� ����������/�������������� �������
//- ������ �������� �������, ��� ������� ������� ������� �������� �� ���� ����������� � �������� ��������. ��� ����
//��� ���������� ������� � ���� ����� ������ ������ ���������� ������� ��� � ��������� ��������

package p_GUI;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import p_DotsAI.EnemyType;
import p_DotsAI.DotsAI;
import p_JavaPatterns.Pattern_AddComponent;
import p_JavaPatterns.Pattern_Resources;
import p_TemplateEngine.TemplateType;

public class GameManagement{//��������

	private Pattern_AddComponent add;
	public JMenuItem buttonBackLength1,buttonBackLength5,buttonCreateTemplate,buttonPause;
	private DotsAI dotsAI;
	
public GameManagement(DotsAI pointsAI){
	
	this.dotsAI=pointsAI;

	add=new Pattern_AddComponent(pointsAI.getContentPane());
	
	buttonBackLength1=(JMenuItem)add.component("jmenuitem",30, 15, 20, 20,"",new ActionListener(){
		public void actionPerformed(ActionEvent arg0) {
			back(1);
	}});
	buttonBackLength5=(JMenuItem)add.component("jmenuitem",55, 15, 20, 20,"",new ActionListener(){
		public void actionPerformed(ActionEvent arg0) {
			back(5);
	}});
	buttonCreateTemplate=(JMenuItem)add.component("jmenuitem",110, 15, 30, 25,"",new ActionListener(){
		public void actionPerformed(ActionEvent arg0) {
			createTemplate();			
	}});
	buttonPause=(JMenuItem)add.component("jmenuitem",150, 15, 70, 20,"",new ActionListener(){
		public void actionPerformed(ActionEvent arg0) {
			pauseGameWithRandom();			
	}});
	
	buttonBackLength1.setIcon(new ImageIcon(Pattern_Resources.navigation+"undo1.png"));
	buttonBackLength5.setIcon(new ImageIcon(Pattern_Resources.navigation+"undo5.png"));
	buttonCreateTemplate.setIcon(new ImageIcon(Pattern_Resources.base+"save_small.png"));
	buttonPause.setIcon(new ImageIcon(Pattern_Resources.navigation+"pause.png"));
	
	buttonBackLength1.setToolTipText("1 ��� �����");
	buttonBackLength5.setToolTipText("5 ����� �����");
	buttonCreateTemplate.setToolTipText("������� ������");
	buttonPause.setToolTipText("����� ��� ���� � ��������");
}

//������� ����� ������. ��� ���� �� �� ����������� � ����, � ������������ � ��������� ��������
private void createTemplate(){
	JOptionPane msg=new JOptionPane();
	try{
		int x=new Integer(msg.showInputDialog("������� ���������� X", "0"));
		int y=new Integer(msg.showInputDialog("������� ���������� Y", "0"));
		dotsAI.protocol.getGame(dotsAI.gameIdx).gameField.setFieldState(dotsAI.protocol.getGame(dotsAI.gameIdx));
		dotsAI.dotsTemplateEditor.moveFromAI(
				TemplateType.getContentForTemplateCreation(dotsAI.protocol.getGame(dotsAI.gameIdx),new Point(x, y),dotsAI.protocol.getGame(dotsAI.gameIdx).fieldState,TemplateType.templateTypeSQUARE),
				0,
				TemplateType.templateTypeSQUARE);
	}catch(Exception exc){}
}

//��������/��������� ����� � ����
private void pauseGameWithRandom(){
	if(dotsAI.isPause)dotsAI.isPause=false;
	else dotsAI.isPause=true;
}

//�������� ���� �� 1 ��� 5 ����� �����
private void back(int backLength){	
	if(dotsAI.protocol.getGame(dotsAI.gameIdx).enemyType==EnemyType.RANDOM){
		dotsAI.protocol.getGame(dotsAI.gameIdx).enemyType=EnemyType.HUMAN;
		dotsAI.t.stop();
	}
	if(dotsAI.protocol.getGame(dotsAI.gameIdx).getMovesCount()<=1){
		return;
	}else{
		String moves=dotsAI.getGameMoves();
		dotsAI.setGameMoves(moves,backLength*2);
	}
}

}