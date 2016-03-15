//Класс MainFrame описывает элементы графического интерфейса для редактора деревьев

package p_TreeEditor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import p_JavaPatterns.Pattern_AddComponent;
import p_JavaPatterns.Pattern_JFrame;
import p_JavaPatterns.Pattern_Resources;

public class MainFrame extends JFrame implements MouseListener{
	
	JOptionPane msg=new JOptionPane();//всплывающее сообщение
	Pattern_AddComponent add=new Pattern_AddComponent(this.getContentPane());//для упрощенного добавления кнопок
	Graphics graphics;//для рисования поля в окне редактора
	
	JButton buttonAddMove,buttonDeleteMove,buttonEditMove,//кнопки для добавления, удаления, редактирования элементов дерева
		buttonType,//кнопка для изменения цвета точек, которые ставятся на поле
		buttonTypeRed,buttonTypeBlue,//кнопки для включения постановки красных и синих точек
		buttonTypeEmpty,//кнопки для включения постановки пустых точек
		buttonBlueDeleteForTreeEdit,//кнопки для стирания синих точек
		buttonExpand,buttonCollapse,//кнопки для раскрытия и сжатия элементов дерева
		buttonPaintChilds,//кнопки для рисования на поле маркеров дочерних элементов дерева
		buttonAddCondition,buttonDeleteCondition,//кнопки добавления и удаления условия к ходам
		buttonSetTreeSymmetry;//кнопка установки симметрии дерева

	JButton butRedAttack,butBlueAttack;//кнопки для включения постановки красных и синих условных точек
	
	int X=300,Y=-120;//отступы для размещения кнопок в окне режактора дерева
	
	JTree tree;//дерево ходов
	
MainFrame(){

	this.setIconImage(Pattern_Resources.icon);//добавление иконки к окну редактора
	new Pattern_JFrame(this,"Dots Tree Editor",false,530,780,new Color(245,245,245));//создание окна редактора
	this.move(getX()+440, getY());//смещение окна редактора
	this.addMouseListener(this);//для событий при нажатии мыши
	
	graphics=this.getGraphics();//график для рисования в окне редактора деревьев
	
	//добавление перечисленных выше кнопок и в некоторых случаях действий при их нажатии
	
	buttonType=(JButton)add.component("button",10+X,130+Y,70,20,"B",null);	
	buttonTypeBlue=Pattern_Resources.getButton(add, Pattern_Resources.dotTypes+"B.png", 10+X,160+Y, "");
	buttonBlueDeleteForTreeEdit=Pattern_Resources.getButton(add, Pattern_Resources.dotTypes+"blueDeleteForTreeEdit.png", 10+X,185+Y, "");
	buttonTypeRed=Pattern_Resources.getButton(add, Pattern_Resources.dotTypes+"R.png", 35+X,160+Y, "");
	buttonTypeEmpty=Pattern_Resources.getButton(add, Pattern_Resources.dotTypes+"N.png", 60+X,160+Y, "");
	
	buttonType.setBackground(Color.blue);
	buttonType.setForeground(Color.lightGray);
	buttonType.setEnabled(false);
	
	buttonTypeBlue.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			buttonType.setText("B");buttonType.setBackground(Color.blue);buttonType.setForeground(Color.lightGray);
		}
	});
	buttonBlueDeleteForTreeEdit.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			buttonType.setText("E");buttonType.setBackground(Color.blue);buttonType.setForeground(Color.lightGray);
		}
	});
	buttonTypeRed.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			buttonType.setText("R");buttonType.setBackground(Color.red);buttonType.setForeground(Color.black);
		}
	});
	buttonTypeEmpty.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			buttonType.setText("N");buttonType.setBackground(Color.white);buttonType.setForeground(Color.black);
		}
	});
		
	buttonAddMove=(JButton)add.component("button",X-5,165,115,20,"<html>добавить ход",null);
	buttonDeleteMove=(JButton)add.component("button",X-5,190,115,20,"<html>удалить ход",null);
	buttonEditMove=(JButton)add.component("button",X-5,215,115,20,"<html>редакт. ход",null);
	
	buttonAddCondition=(JButton)add.component("button",X+80,240,145,20,"<html>добавить условие",null);
	buttonDeleteCondition=(JButton)add.component("button",X+80,265,145,20,"<html>удалить условие",null);
	
	buttonExpand=(JButton)add.component("button",88+X,10,138,20,"<html>раскрыть дерево",null);
	buttonCollapse=(JButton)add.component("button",88+X,35,138,20,"<html>свернуть дерево",null);
	
	buttonPaintChilds=(JButton)add.component("button",88+X,65,138,45,"<html>показать/скрыть вложенные точки",null);
	buttonSetTreeSymmetry=(JButton)add.component("button",88+X,115,138,45,"<html>указать тип симметрии",null);	
	
	butRedAttack=Pattern_Resources.getButton(add,Pattern_Resources.targetDotTypes+"ra.png",35+X,185+Y,"ra");
	butBlueAttack=Pattern_Resources.getButton(add,Pattern_Resources.targetDotTypes+"ba.png",60+X,185+Y,"ba");
	
	butRedAttack.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			buttonType.setText("RA");buttonType.setBackground(Color.red);buttonType.setForeground(Color.black);
		}
	});
	butBlueAttack.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			buttonType.setText("BA");buttonType.setBackground(Color.blue);buttonType.setForeground(Color.lightGray);
		}
	});
}

//неиспользуемые методы, которые необходимо было добавить
public void mousePressed(MouseEvent me) {}
public void mouseClicked(MouseEvent me) {}
public void mouseEntered(MouseEvent me) {}
public void mouseExited(MouseEvent me) {}
public void mouseReleased(MouseEvent me) {}

}
