//����� MainFrame ������������ ����������� ��������� ��� ��������� ��������

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
	
	Thread t=new Thread(this);//�����
	JOptionPane msg=new JOptionPane();//����������� ���������
	Pattern_AddComponent add=new Pattern_AddComponent(this.getContentPane());//��� ����������� ���������� ������
	
	//��������� ������� ��� ���������� ������ � ���� ���������
	int otstupX1=140;//������ �� ��� x	
	int otstupX2=270;//������ �� ��� x
	int otstupX3=otstupX2+80;//������ �� ��� x
	int otstupY1=10;//������ �� ��� y
	int otstupY2=265;//������ �� ��� y		
	int otstupY3=otstupY1+55;//������ �� ��� y
	
	JButton buttonSave,buttonResave,buttonDelete;//�������� � ����� ��������
	JButton buttonInfoPanel,buttonUndo,buttonRedo,buttonRepaint;//��� �������������� ��������
	JButton buttonTransform,buttonType;//������ ������������ ����
	JButton buttonFirst,buttonLast,buttonPrevious,buttonNext,buttonDotIndex;//������ ��������� �� ���� ��������
	JButton buttonEMPTY,buttonBLUE,buttonRED,buttonANY,buttonRED_EMPTY,buttonBLUE_EMPTY,buttonLAND;//������ ��� ����� ����� �������
	JButton buttonLeft,buttonRight,buttonTop,buttonBottom,buttonLeftTop,buttonLeftBottom,buttonRightTop,buttonRightBottom;//������� ������ ����, ������ ����� ��� �������������� ��������
	JButton button5,button7,button9,button11,button13;//������ ������������� ������ �������� ����� �������, ������� ��� �������� ������ �������, ����� �� ������� ������� ����� �������
	
	JLabel butTemplateSequenceInBase,labMoveToSide,/*labCoordinates,*/labelCurrentTemplateType;
	
	JPopupMenu menu_transform=new JPopupMenu("�������"),menu_type=new JPopupMenu("���");
	JButton butOpen;//menu
	JMenuItem but90,but180,but270,butVertical,butGorizontal,butVertical90,buttonGorizontal90;//transform
	JMenuItem templateTypeItems[];
	
	public ImageIcon[] templateTypeIcons;
	
MainFrame(){

	this.setIconImage(Pattern_Resources.icon);//���������� ������ ��� ����
	new Pattern_JFrame(this,"Dots Template Editor",false,480,290,new Color(245,245,245));//������� ���� ���������
	
	//�������� ������������ ������� � ������� ���� � �������� � ����� ���������
	this.addMouseListener(this);
	this.addWindowListener(this);
	this.addMouseMotionListener(this);
		
	//���������� ������ �������� ���� �������
	labelCurrentTemplateType=(JLabel)add.component("label",175+otstupX1,otstupY1+otstupY2,20,18,"sqt",null);
	labelCurrentTemplateType.setIcon(new ImageIcon(Pattern_Resources.templateTypes+TemplateType.toString(TemplateType.templateTypeBEGIN)+".png"));
	
	//�������� � ������� ��������� �������� ��� �������������� �������
	buttonUndo=Pattern_Resources.getButton(add,Pattern_Resources.navigation+"undo.png",otstupX2+80,otstupY1+5,"�������� ���������");
	buttonRedo=Pattern_Resources.getButton(add,Pattern_Resources.navigation+"redo.png",otstupX2+105,otstupY1+5,"������� ���������");
	
	//������������ ������, ����� ������ ��� ��������� � �������� � ����
	buttonRepaint=Pattern_Resources.getButton(add,Pattern_Resources.navigation+"refresh.png",otstupX2+130,otstupY1+5,"��������");
	
	//������ ����� ��� �����
	buttonDelete=Pattern_Resources.getButton(add,Pattern_Resources.base+"delete.png",otstupX1-17,otstupY1+otstupY2,"������� ������");
	buttonFirst=Pattern_Resources.getButton(add,Pattern_Resources.navigation+"first.png",6+otstupX1,otstupY1+otstupY2,"");
	buttonPrevious=Pattern_Resources.getButton(add,Pattern_Resources.navigation+"previous.png",29+otstupX1,otstupY1+otstupY2,"");
	butTemplateSequenceInBase=(JLabel)add.component("label",54+otstupX1,otstupY1+otstupY2,40,18,"<HTML>0",null);
	buttonNext=Pattern_Resources.getButton(add,Pattern_Resources.navigation+"next.png",89+otstupX1,otstupY1+otstupY2,"");
	buttonLast=Pattern_Resources.getButton(add,Pattern_Resources.navigation+"last.png",112+otstupX1,otstupY1+otstupY2,"");

	//�������, ��������� � ������������� ������
	butOpen=Pattern_Resources.getButton(add,Pattern_Resources.base+"open.png",otstupX2+155,otstupY1+84,"������� � �������");
	buttonSave=Pattern_Resources.getButton(add,Pattern_Resources.base+"save.png",otstupX2+155,otstupY1+113,"��������� ��� �����");
	buttonResave=Pattern_Resources.getButton(add,Pattern_Resources.base+"resave.png",otstupX2+155,otstupY1+142,"������������ ������");
	
	//������ ��������� �������. ��� ��������� ����� ������ �� ��������������, ������� ������ ����� ������� ������������ ����
	but90=Pattern_Resources.getMenuItem(Pattern_Resources.rotates+"90.png", "�� 90 ��������");
	but180=Pattern_Resources.getMenuItem(Pattern_Resources.rotates+"180.png", "�� 180 ��������");
	but270=new JMenuItem("�� 270 ��������");
	butVertical=Pattern_Resources.getMenuItem(Pattern_Resources.rotates+"vert.png", "�� ���������");
	butGorizontal=Pattern_Resources.getMenuItem(Pattern_Resources.rotates+"gor.png", "�� �����������");
	butVertical90=new JMenuItem("�� ��������� � �� 90");
	buttonGorizontal90=new JMenuItem("�� ����������� � �� 90");
	
	//����, �������� ������ ��������� �������
	menu_transform.add(but90);menu_transform.add(but180);menu_transform.add(but270);menu_transform.addSeparator();
	menu_transform.add(butVertical);menu_transform.add(butGorizontal);menu_transform.addSeparator();
	menu_transform.add(butVertical90);menu_transform.add(buttonGorizontal90);
	buttonTransform=Pattern_Resources.getButton(add,Pattern_Resources.rotates+"transform.png",otstupX2+80,otstupY1+127,"������� �������");
	menu_transform.setPopupSize(180,otstupY1+180);
	buttonTransform.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){
		menu_transform.setLocation(MainFrame.this.getX()+400,otstupY1+MainFrame.this.getY()+30);
		if(menu_transform.isVisible())menu_transform.setVisible(false);else menu_transform.setVisible(true);
	}});
	
	//������ ����� ��������
	templateTypeItems=new JMenuItem[TemplateType.lastIndexOfTemplateType];
	templateTypeIcons=new ImageIcon[TemplateType.lastIndexOfTemplateType];
	for(int i=0;i<templateTypeItems.length;i++){
		JMenuItem item=new JMenuItem(TemplateType.getTemplateTypeName(i)+" ("+TemplateType.toString(i)+")");
		item.setIcon(TemplateType.getImageIcon(i));
		templateTypeIcons[i]=TemplateType.getImageIcon(i);
		templateTypeItems[i]=item;
		menu_type.add(templateTypeItems[i]);
	}
		
	//����, �������� ���� ��������. ��� ������� ���� �������� ������� ��������������� ���� ���, ������ ��������� ������ ����� �������������� ������� 
	buttonType=Pattern_Resources.getButton(add,Pattern_Resources.templateTypes+"type.png",otstupX2+155,otstupY1+5,"");
	buttonType.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){
		menu_type.setLocation(MainFrame.this.getX()+450,otstupY1+MainFrame.this.getY()+30);
		if(menu_type.isVisible())menu_type.setVisible(false);else menu_type.setVisible(true);
	}});
	
	//���������� ������� ��� �����, ������� ����� ��������� �� ���� ��� �������������� �������
	buttonDotIndex=(JButton)add.component("button",5,otstupY1,45,18,"",null);
	buttonDotIndex.setEnabled(false);
	buttonDotIndex.setText(""+Protocol.templateDotType_ANY);
	
	//������ ����� �����
	buttonEMPTY=Pattern_Resources.getButton(add,Pattern_Resources.dotTypes+"N.png",5,otstupY1+22,Protocol.templateDotType_EMPTY);
	buttonANY=Pattern_Resources.getButton(add,Pattern_Resources.dotTypes+"A.png",30,otstupY1+22,Protocol.templateDotType_ANY);
	buttonBLUE=Pattern_Resources.getButton(add,Pattern_Resources.dotTypes+"B.png",5,otstupY1+44,Protocol.templateDotType_BLUE);
	buttonBLUE_EMPTY=Pattern_Resources.getButton(add,Pattern_Resources.dotTypes+"C.png",30,otstupY1+44,Protocol.templateDotType_BLUE_or_EMPTY);
	buttonRED=Pattern_Resources.getButton(add,Pattern_Resources.dotTypes+"R.png",5,otstupY1+66,Protocol.templateDotType_RED);
	buttonRED_EMPTY=Pattern_Resources.getButton(add,Pattern_Resources.dotTypes+"P.png",30,otstupY1+66,Protocol.templateDotType_RED_or_EMPTY);
	buttonLAND=Pattern_Resources.getButton(add,Pattern_Resources.dotTypes+"L.png",5,otstupY1+88,Protocol.templateDotType_LAND);

	//�������� ��� ������� ������ ����� �����
	buttonRED.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){repaintIdButton(buttonRED);}});
	buttonRED_EMPTY.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){repaintIdButton(buttonRED_EMPTY);}});
	buttonBLUE.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){repaintIdButton(buttonBLUE);}});
	buttonBLUE_EMPTY.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){repaintIdButton(buttonBLUE_EMPTY);}});
	buttonEMPTY.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){repaintIdButton(buttonEMPTY);}});
	buttonANY.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){repaintIdButton(buttonANY);}});
	buttonLAND.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){repaintIdButton(buttonLAND);}});
	
	//������ �������� �������
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
	
	//������ ��������� ��������� ������� �������
	button5=Pattern_Resources.getButton(add,Pattern_Resources.frames+"5.png",210+otstupX1,otstupY1+220,"");
	button7=Pattern_Resources.getButton(add,Pattern_Resources.frames+"7.png",230+otstupX1,otstupY1+220,"");
	button9=Pattern_Resources.getButton(add,Pattern_Resources.frames+"9.png",250+otstupX1,otstupY1+220,"");
	button11=Pattern_Resources.getButton(add,Pattern_Resources.frames+"11.png",270+otstupX1,otstupY1+220,"");
	button13=Pattern_Resources.getButton(add,Pattern_Resources.frames+"13.png",290+otstupX1,otstupY1+220,"");
	
	//�������������� ������, ������ ���������� ID �������� �������
	buttonInfoPanel=(JButton)add.component("button",otstupX2+80,30+otstupY1,105,20,"������",null);
	buttonInfoPanel.setEnabled(false);
}

//������������� ������� ��� �����, ������� ����� ��������� �� ���� ��� �������������� �������
void repaintIdButton(JButton button){
	buttonDotIndex.setText(button.getName());
	buttonDotIndex.setBackground(button.getBackground());
}

//������ ������ ���������� ��������, ���� ��� �� ������������
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
