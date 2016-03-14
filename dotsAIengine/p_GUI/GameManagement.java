//Класс добавляет в главное окно программы несколько кнопок управления игрой.
//Это кнопки:
//- поставить игру на паузу, что полезно в игре ИИ против рандома или против ИИ
//- отмотать игру на 1 или 5 ходов назад, что полезно при плохом ходе ИИ для возвращения с целью добавления/редактирования шаблона
//- кнопка создания шаблона, при нажатии которой игровая ситуация на поле переносится в редактор шаблонов. При этом
//для добавления шаблона в базу нужно нажать кнопку сохранения шаблона уже в редакторе шаблонов

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

public class GameManagement{//заставка

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
	
	buttonBackLength1.setToolTipText("1 ход назад");
	buttonBackLength5.setToolTipText("5 ходов назад");
	buttonCreateTemplate.setToolTipText("Создать шаблон");
	buttonPause.setToolTipText("Пауза при игре с рандомом");
}

//создать новый шаблон. При этом он не сохранятеся в базе, а отображается в редакторе шаблонов
private void createTemplate(){
	JOptionPane msg=new JOptionPane();
	try{
		int x=new Integer(msg.showInputDialog("Введите координату X", "0"));
		int y=new Integer(msg.showInputDialog("Введите координату Y", "0"));
		dotsAI.protocol.getGame(dotsAI.gameIdx).gameField.setFieldState(dotsAI.protocol.getGame(dotsAI.gameIdx));
		dotsAI.dotsTemplateEditor.moveFromAI(
				TemplateType.getContentForTemplateCreation(dotsAI.protocol.getGame(dotsAI.gameIdx),new Point(x, y),dotsAI.protocol.getGame(dotsAI.gameIdx).fieldState,TemplateType.templateTypeSQUARE),
				0,
				TemplateType.templateTypeSQUARE);
	}catch(Exception exc){}
}

//включить/выключить паузу в игре
private void pauseGameWithRandom(){
	if(dotsAI.isPause)dotsAI.isPause=false;
	else dotsAI.isPause=true;
}

//отмотать игру на 1 или 5 ходов назад
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