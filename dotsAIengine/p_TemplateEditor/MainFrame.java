//Класс MainFrame обеспечивает графический интерфейс для редактора шаблонов

package p_TemplateEditor;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import p_DotsAI.Protocol;
import p_JavaPatterns.Pattern_AddComponent;
import p_JavaPatterns.Pattern_JFrame;
import p_JavaPatterns.Pattern_Resources;
import p_TemplateEngine.TemplateType;

public class MainFrame extends JFrame implements Runnable,MouseListener, WindowListener, MouseMotionListener{
	
	Thread t=new Thread(this);//поток
	JOptionPane msg=new JOptionPane();//всплывающее сообщение
	Pattern_AddComponent add=new Pattern_AddComponent(this.getContentPane());//для упрощенного добавления кнопок
	
	//различные отступы для размещения кнопок в окне программы
	int otstupX1=140;//отступ по оси x	
	int otstupX2=270;//отступ по оси x
	int otstupX3=otstupX2+80;//отступ по оси x
	int otstupY1=10;//отступ по оси y
	int otstupY2=265;//отступ по оси y		
	int otstupY3=otstupY1+55;//отступ по оси y
	
	JButton buttonSave,buttonResave,buttonDelete;//действия с базой шаблонов
	JButton buttonInfoPanel,buttonUndo,buttonRedo,buttonRepaint;//для редактирования шаблонов
	JButton buttonTransform,buttonType;//кнопки всплывающего меню
	JButton buttonFirst,buttonLast,buttonPrevious,buttonNext,buttonDotIndex;//кнопки навигации по базе шаблонов
	JButton buttonEMPTY,buttonBLUE,buttonRED,buttonANY,buttonRED_EMPTY,buttonBLUE_EMPTY,buttonLAND;//кнопки для типов точек шаблона
	JButton buttonLeft,buttonRight,buttonTop,buttonBottom,buttonLeftTop,buttonLeftBottom,buttonRightTop,buttonRightBottom;//стрелки сдвига поля, иногда нужны при редактировании шаблонов
	JButton button5,button7,button9,button11,button13;//кнопку устанавливают размер значимой части шаблона, полезны при создании нового шаблона, чтобы не ставить зеленые точки вручную
	
	JLabel butTemplateSequenceInBase,labMoveToSide,/*labCoordinates,*/labelCurrentTemplateType;
	
	JPopupMenu menu_transform=new JPopupMenu("Поворот"),menu_type=new JPopupMenu("Тип");
	JButton butOpen;//menu
	JMenuItem but90,but180,but270,butVertical,butGorizontal,butVertical90,buttonGorizontal90;//transform
	JMenuItem templateTypeItems[];
	
	public ImageIcon[] templateTypeIcons;
	
MainFrame(){

	this.setIconImage(Pattern_Resources.icon);//установить иконку для окна
	new Pattern_JFrame(this,"Dots Template Editor",false,480,290,new Color(245,245,245));//создать окно программы
	
	//добавить обработчиков движени и нажатия мыши и действий с окном программы
	this.addMouseListener(this);
	this.addWindowListener(this);
	this.addMouseMotionListener(this);
		
	//отображает иконку текущего типа шаблона
	labelCurrentTemplateType=(JLabel)add.component("label",175+otstupX1,otstupY1+otstupY2,20,18,"sqt",null);
	labelCurrentTemplateType.setIcon(new ImageIcon(Pattern_Resources.templateTypes+TemplateType.toString(TemplateType.templateTypeBEGIN)+".png"));
	
	//отменить и вернуть последнее действие при редактировании шаблона
	buttonUndo=Pattern_Resources.getButton(add,Pattern_Resources.navigation+"undo.png",otstupX2+80,otstupY1+5,"отменить изменения");
	buttonRedo=Pattern_Resources.getButton(add,Pattern_Resources.navigation+"redo.png",otstupX2+105,otstupY1+5,"вернуть изменения");
	
	//перерисовать шаблон, нужно только при проблемах с графикой в окне
	buttonRepaint=Pattern_Resources.getButton(add,Pattern_Resources.navigation+"refresh.png",otstupX2+130,otstupY1+5,"обновить");
	
	//кнопки снизу под полем
	buttonDelete=Pattern_Resources.getButton(add,Pattern_Resources.base+"delete.png",otstupX1-17,otstupY1+otstupY2,"удалить шаблон");
	buttonFirst=Pattern_Resources.getButton(add,Pattern_Resources.navigation+"first.png",6+otstupX1,otstupY1+otstupY2,"");
	buttonPrevious=Pattern_Resources.getButton(add,Pattern_Resources.navigation+"previous.png",29+otstupX1,otstupY1+otstupY2,"");
	butTemplateSequenceInBase=(JLabel)add.component("label",54+otstupX1,otstupY1+otstupY2,40,18,"<HTML>0",null);
	buttonNext=Pattern_Resources.getButton(add,Pattern_Resources.navigation+"next.png",89+otstupX1,otstupY1+otstupY2,"");
	buttonLast=Pattern_Resources.getButton(add,Pattern_Resources.navigation+"last.png",112+otstupX1,otstupY1+otstupY2,"");

	//открыть, сохранить и пересохранить шаблон
	butOpen=Pattern_Resources.getButton(add,Pattern_Resources.base+"open.png",otstupX2+155,otstupY1+84,"перейти к шаблону");
	buttonSave=Pattern_Resources.getButton(add,Pattern_Resources.base+"save.png",otstupX2+155,otstupY1+113,"сохранить как новый");
	buttonResave=Pattern_Resources.getButton(add,Pattern_Resources.base+"resave.png",otstupX2+155,otstupY1+142,"перезаписать шаблон");
	
	//кнопки разворота шаблона. При развороте точки дерева не поворачиваются, поэтому дерево будет хранить неправильные ходы
	but90=Pattern_Resources.getMenuItem(Pattern_Resources.rotates+"90.png", "На 90 градусов");
	but180=Pattern_Resources.getMenuItem(Pattern_Resources.rotates+"180.png", "На 180 градусов");
	but270=new JMenuItem("На 270 градусов");
	butVertical=Pattern_Resources.getMenuItem(Pattern_Resources.rotates+"vert.png", "По вертикали");
	butGorizontal=Pattern_Resources.getMenuItem(Pattern_Resources.rotates+"gor.png", "По горизонтали");
	butVertical90=new JMenuItem("По вертикали и на 90");
	buttonGorizontal90=new JMenuItem("По горизонтали и на 90");
	
	//меню, хранящее кнопки разворота шаблона
	menu_transform.add(but90);menu_transform.add(but180);menu_transform.add(but270);menu_transform.addSeparator();
	menu_transform.add(butVertical);menu_transform.add(butGorizontal);menu_transform.addSeparator();
	menu_transform.add(butVertical90);menu_transform.add(buttonGorizontal90);
	buttonTransform=Pattern_Resources.getButton(add,Pattern_Resources.rotates+"transform.png",otstupX2+80,otstupY1+127,"поворот шаблона");
	menu_transform.setPopupSize(180,otstupY1+180);
	buttonTransform.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){
		menu_transform.setLocation(MainFrame.this.getX()+400,otstupY1+MainFrame.this.getY()+30);
		if(menu_transform.isVisible())menu_transform.setVisible(false);else menu_transform.setVisible(true);
	}});
	
	//иконки типов шаблонов
	templateTypeItems=new JMenuItem[TemplateType.lastIndexOfTemplateType];
	templateTypeIcons=new ImageIcon[TemplateType.lastIndexOfTemplateType];
	for(int i=0;i<templateTypeItems.length;i++){
		JMenuItem item=new JMenuItem(TemplateType.getTemplateTypeName(i)+" ("+TemplateType.toString(i)+")");
		item.setIcon(TemplateType.getImageIcon(i));
		templateTypeIcons[i]=TemplateType.getImageIcon(i);
		templateTypeItems[i]=item;
		menu_type.add(templateTypeItems[i]);
	}
		
	//меню, хранящее типы шаблонов. При нажатии типа текущему шаблону устанавливается этот тип, однако требуется нажать также пересохранение шаблона 
	buttonType=Pattern_Resources.getButton(add,Pattern_Resources.templateTypes+"type.png",otstupX2+155,otstupY1+5,"");
	buttonType.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){
		menu_type.setLocation(MainFrame.this.getX()+450,otstupY1+MainFrame.this.getY()+30);
		if(menu_type.isVisible())menu_type.setVisible(false);else menu_type.setVisible(true);
	}});
	
	//отображает текущий тип точек, которые будут ставиться на поле при редактировании шаблона
	buttonDotIndex=(JButton)add.component("button",5,otstupY1,45,18,"",null);
	buttonDotIndex.setEnabled(false);
	buttonDotIndex.setText(""+Protocol.templateDotType_ANY);
	
	//кнопки типов точек
	buttonEMPTY=Pattern_Resources.getButton(add,Pattern_Resources.dotTypes+"N.png",5,otstupY1+22,Protocol.templateDotType_EMPTY);
	buttonANY=Pattern_Resources.getButton(add,Pattern_Resources.dotTypes+"A.png",30,otstupY1+22,Protocol.templateDotType_ANY);
	buttonBLUE=Pattern_Resources.getButton(add,Pattern_Resources.dotTypes+"B.png",5,otstupY1+44,Protocol.templateDotType_BLUE);
	buttonBLUE_EMPTY=Pattern_Resources.getButton(add,Pattern_Resources.dotTypes+"C.png",30,otstupY1+44,Protocol.templateDotType_BLUE_or_EMPTY);
	buttonRED=Pattern_Resources.getButton(add,Pattern_Resources.dotTypes+"R.png",5,otstupY1+66,Protocol.templateDotType_RED);
	buttonRED_EMPTY=Pattern_Resources.getButton(add,Pattern_Resources.dotTypes+"P.png",30,otstupY1+66,Protocol.templateDotType_RED_or_EMPTY);
	buttonLAND=Pattern_Resources.getButton(add,Pattern_Resources.dotTypes+"L.png",5,otstupY1+88,Protocol.templateDotType_LAND);

	//действия при нажатии кнопок типов точек
	buttonRED.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){repaintIdButton(buttonRED);}});
	buttonRED_EMPTY.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){repaintIdButton(buttonRED_EMPTY);}});
	buttonBLUE.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){repaintIdButton(buttonBLUE);}});
	buttonBLUE_EMPTY.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){repaintIdButton(buttonBLUE_EMPTY);}});
	buttonEMPTY.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){repaintIdButton(buttonEMPTY);}});
	buttonANY.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){repaintIdButton(buttonANY);}});
	buttonLAND.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){repaintIdButton(buttonLAND);}});
	
	//кнопки смещения шаблона
	buttonLeftTop=Pattern_Resources.getButton(add,Pattern_Resources.arrows+"lt.png",otstupX3,otstupY3,"");
	buttonTop=Pattern_Resources.getButton(add,Pattern_Resources.arrows+"top.png",otstupX3+25,otstupY3,"");
	buttonRightTop=Pattern_Resources.getButton(add,Pattern_Resources.arrows+"rt.png",otstupX3+50,otstupY3,"");
	buttonLeft=Pattern_Resources.getButton(add,Pattern_Resources.arrows+"left.png",otstupX3,otstupY3+25,"");
	labMoveToSide=(JLabel)add.component("label",otstupX3+28,otstupY3+23,20,20,"",null);
	buttonRight=Pattern_Resources.getButton(add,Pattern_Resources.arrows+"right.png",otstupX3+50,otstupY3+25,"");
	buttonLeftBottom=Pattern_Resources.getButton(add,Pattern_Resources.arrows+"lb.png",otstupX3,otstupY3+50,"");
	buttonBottom=Pattern_Resources.getButton(add,Pattern_Resources.arrows+"bottom.png",otstupX3+25,otstupY3+50,"");
	buttonRightBottom=Pattern_Resources.getButton(add,Pattern_Resources.arrows+"rb.png",otstupX3+50,otstupY3+50,"");
	labMoveToSide.setIcon(new ImageIcon(Pattern_Resources.arrows+"move.png"));
	
	//кнопки установки значимого размера шаблона
	button5=Pattern_Resources.getButton(add,Pattern_Resources.frames+"5.png",210+otstupX1,otstupY1+220,"");
	button7=Pattern_Resources.getButton(add,Pattern_Resources.frames+"7.png",230+otstupX1,otstupY1+220,"");
	button9=Pattern_Resources.getButton(add,Pattern_Resources.frames+"9.png",250+otstupX1,otstupY1+220,"");
	button11=Pattern_Resources.getButton(add,Pattern_Resources.frames+"11.png",270+otstupX1,otstupY1+220,"");
	button13=Pattern_Resources.getButton(add,Pattern_Resources.frames+"13.png",290+otstupX1,otstupY1+220,"");
	
	//информационная панель, обычно показывает ID текущего шаблона
	buttonInfoPanel=(JButton)add.component("button",otstupX2+80,30+otstupY1,105,20,"панель",null);
	buttonInfoPanel.setEnabled(false);
}

//устанавливает текущий тип точек, которые будут ставиться на поле при редактировании шаблона
void repaintIdButton(JButton button){
	buttonDotIndex.setText(button.getName());
	buttonDotIndex.setBackground(button.getBackground());
}

//данные методы необходимо добавить, хотя они не используются
public void run(){}
public void mousePressed(MouseEvent me) {}
public void windowActivated(WindowEvent e) {}
public void windowClosed(WindowEvent e) {}
public void windowClosing(WindowEvent e) {}
public void windowDeactivated(WindowEvent e) {}
public void windowDeiconified(WindowEvent e) {}
public void windowIconified(WindowEvent e) {}
public void windowOpened(WindowEvent e) {}
public void mouseClicked(MouseEvent me) {}
public void mouseEntered(MouseEvent me) {}
public void mouseExited(MouseEvent me) {}
public void mouseReleased(MouseEvent me) {}
public void mouseDragged(MouseEvent arg0) {}
public void mouseMoved(MouseEvent me) {}

}
