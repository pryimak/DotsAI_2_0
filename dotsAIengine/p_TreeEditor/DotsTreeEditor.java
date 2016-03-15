package p_TreeEditor;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import p_DotsAI.Protocol;
import p_GUI.GameField;
import p_GUI.GameFieldGUI;
import p_GUI.GameGUI;
import p_TemplateEngine.Template;
import p_TemplateEngine.TemplateEngine;
import p_TreeEngine.Tree;
import p_TreeEngine.TreeCondition;
import p_TreeEngine.TreeSymmetryType;
import p_TreeEngine.TreeNodeDotsTree;

public class DotsTreeEditor extends MainFrame{
	
	JScrollPane scroll;//область прокрутки, в которой будет отображаться дерево для возможности его прокрутки вверх-вниз и влево-вправо
	TemplateEngine base;//база шаблонов
	public Template template;//текущий шаблон
	public GameField gameField;//поле с отображением шаблона и ходов дерева
	public GameFieldGUI gameFieldGUI;//рисование игрового поля
	
	ArrayList<Point> blueDots;//добавляемые в дерево ходы человека
	ArrayList<Point> ifMovesDots;//точки для условных ходов
	Point redDot;//добавляемый в дерево ход ИИ
	
	int selectedNodeId;//ID выбранного элемента дерева	
	Tree treeOfDots;//дерево
	TreeNodeDotsTree node;//элемент дерева
	DefaultTreeModel treeModel;//для управления деревом
	
	boolean isAlwaysExpand=true;//раскрывать дерево ходов
	boolean isShowChilds=false;//рисовать вложенные в элемент дерева ходы
	
//создать объект редактора деревьев
public DotsTreeEditor(TemplateEngine base){
	this.base=base;
	addButtons();//добавить кнопки
	
	ArrayList<Integer> nodeIndexes=new ArrayList<Integer>();//индексы элементов дерева
	nodeIndexes.add(new Integer(0));
	node=new TreeNodeDotsTree("<html><id=0><font>Дерево ходов</font></html>","",0,nodeIndexes,null,null);//установить верхний элемент дерева
	treeModel=new DefaultTreeModel(node);
	tree=new JTree(treeModel);
	
	//создать область прокрутки
	scroll=new JScrollPane(tree);
	scroll.setBounds(10, 300, 510, 475);
	super.getContentPane().add(scroll);
	scroll.updateUI();
	
	//действия при выборе элемента дерева
	tree.addTreeSelectionListener(new TreeSelectionListener() {
		public void valueChanged(TreeSelectionEvent s) {
			try{
				updateOnTreeSelection();
			}catch(Exception e){}
		}
	});

	gameFieldGUI=new GameFieldGUI(graphics,GameGUI.offsetX,GameGUI.offsetY-20);//рисование игрового поля в редакторе деревьев
}

//установить дерево ходов в редактор деревьев и нарисовать дерево
public void setTree(Template template){
	selectedNodeId=0;
	this.template=template;	
	treeOfDots=template.tree;

	drawTemplate(template);
	
	if(treeOfDots!=null){
		tree.setModel(treeOfDots.treeModel);		
		if(isAlwaysExpand)for(int i = 0; i < tree.getRowCount(); i ++)tree.expandRow(i);
	}else{
		tree.setModel(treeModel);
	}
	
	blueDots=new ArrayList<Point>();
	ifMovesDots=new ArrayList<Point>();
	redDot=null;
}

//обновить редактор при выборе элемента дерева
void updateOnTreeSelection(){
	if(treeOfDots==null)return;
	
	String str=tree.getLastSelectedPathComponent().toString();
	String id=str.substring(str.indexOf("<id=")+4, str.indexOf("><font"));
	selectedNodeId=new Integer(id);
	
	TreeNodeDotsTree selectedTreeNode=treeOfDots.node.getTreeNodeByID(selectedNodeId);
	if(selectedTreeNode.moveType!=-1){
		if(selectedTreeNode.moveType==GameField.RED){
			buttonType.setText("B");buttonType.setBackground(Color.blue);buttonType.setForeground(Color.lightGray);
		}else if(selectedTreeNode.moveType==GameField.BLUE){
			buttonType.setText("R");buttonType.setBackground(Color.red);buttonType.setForeground(Color.black);
		}
	}
	repaintField();
}

//при нажатии мышки на игровом поле редактора деревьев
public void mousePressed(MouseEvent me) {
	byte x=getMouseClickX(me);
	byte y=getMouseClickY(me);
	if(buttonType.getText().equals("B")){//добавить синий ход на поле
		if(gameField.canAddMove(x,y)){
			blueDots.add(new Point(x,y));
			if(redDot!=null){
				if(redDot.x==x&redDot.y==y){//удалить ход ИИ при ходе человека в одну и ту же точку
					redDot=null;
				}
			}
			for(int i=0;i<blueDots.size();i++){//нарисовать синие ходы на поле
				gameFieldGUI.drawDot(blueDots.get(i).x, blueDots.get(i).y,gameFieldGUI.blue);
			}
		}
	}if(buttonType.getText().equals("E")){//удалить синие точки при редактировании синего node

		TreeNodeDotsTree selectedTreeNode=treeOfDots.node.getTreeNodeByID(selectedNodeId);
		if(selectedTreeNode.moveType==GameField.RED)return;

		boolean isNeedToClear=false;//для проверки - принадлежит ли точка к выделеному node
		for(int i=0;i<selectedTreeNode.points.size();i++){
			if(selectedTreeNode.points.get(i).x==(x-Protocol.maxSize/2)&selectedTreeNode.points.get(i).y==(y-Protocol.maxSize/2)){//удалить ход человека при ходе ИИ в одну и ту же точку
				selectedTreeNode.points.remove(i);
				isNeedToClear=true;
				break;
			}
		}
		if(!isNeedToClear)return;

		//обновить текст к элементу дерева
		if(selectedTreeNode.points.size()>0){
			String blueMoves="";
			for(int i=0;i<selectedTreeNode.points.size();i++){
				blueMoves+="("+selectedTreeNode.points.get(i).x+","+selectedTreeNode.points.get(i).y+"); ";
			}
			selectedTreeNode.blueIsDefault=false;
			selectedTreeNode.setUserObject("<html><id="+selectedTreeNode.nodeId+"><font color=blue>"+blueMoves+"</font></html>");
		}else if(selectedTreeNode.points.size()==0){
			selectedTreeNode.blueIsDefault=true;
			selectedTreeNode.setUserObject("<html><id="+selectedTreeNode.nodeId+"><font color=blue>default</font></html>");
		}
	
		//обновить поле и дерево
		repaintField();
		treeOfDots.treeModel.reload(selectedTreeNode);
		if(isAlwaysExpand)for(int i = 0; i < tree.getRowCount(); i++)tree.expandRow(i);		
		
	}else if(buttonType.getText().equals("R")){//добавить красный ход на поле
		if(gameField.canAddMove(x,y)){
			
			if(redDot!=null)gameFieldGUI.clearDot(redDot.x, redDot.y);
			redDot=new Point(x,y);
			for(int i=0;i<blueDots.size();i++){
				if(blueDots.get(i).x==x&blueDots.get(i).y==y){//удалить ход человека при ходе ИИ в одну и ту же точку
					blueDots.remove(i);
				}
			}
			gameFieldGUI.drawDot(redDot.x, redDot.y,Color.red);//нарисовать добавленный ход
		}
	}else if(buttonType.getText().equals("N")){//добавить пустой ход на поле
		if(gameField.canAddMove(x,y)){
			if(redDot!=null){
				if(redDot.x==x&redDot.y==y){//очистить точку - удалить ход ИИ
					redDot=null;
					gameFieldGUI.drawDot(x, y,Color.white);
				}
			}
			for(int i=0;i<blueDots.size();i++){
				if(blueDots.get(i).x==x&blueDots.get(i).y==y){//очистить точку - удалить ход человека
					blueDots.remove(i);
					gameFieldGUI.drawDot(x, y,Color.white);
				}
			}			
		}
	}else if(buttonType.getText().equals("RA")||buttonType.getText().equals("BA")){//добавить условный ход на поле
		boolean isSetToPointRed=false;
		TreeNodeDotsTree selectedTreeNode=treeOfDots.node.getTreeNodeByID(selectedNodeId);
		if(selectedTreeNode.moveType==GameField.RED){
			if(x-Protocol.maxSize/2==selectedTreeNode.points.get(0).x&&y-Protocol.maxSize/2==selectedTreeNode.points.get(0).y){
				isSetToPointRed=true;
			}
		}
			
		if(gameField.canAddMove(x,y)||isSetToPointRed){			
			boolean isAlreadyAdded=false;
			for(int i=0;i<ifMovesDots.size();i++){
				if(x==ifMovesDots.get(i).x&&y==ifMovesDots.get(i).y)isAlreadyAdded=true;
				
			}
			if(!isAlreadyAdded){
				ifMovesDots.add(new Point(x,y));
				gameFieldGUI.drawAttackDot(buttonType.getText().equals("RA")?true:false,x,y);
			}			
		}
	}
}

//перерисовать поле
void repaintField(){
	
	if(selectedNodeId==0){
		drawTemplate(template);
	}else{
		drawTemplate(template);
		
		TreeNodeDotsTree selectedTreeNode=treeOfDots.node.getTreeNodeByID(selectedNodeId);
		ArrayList<TreeNodeDotsTree> listOfNodes=new ArrayList<TreeNodeDotsTree>();		
		listOfNodes.add(selectedTreeNode);
		
		while(listOfNodes.get(listOfNodes.size()-1).parentNode!=null){
			TreeNodeDotsTree n=listOfNodes.get(listOfNodes.size()-1).parentNode;			
			if(n.level>0){
				listOfNodes.add(n);
			}else{
				break;
			}
		}
		
		//сделать ходы в игровом поле для дочерних элементов
		for(int i=listOfNodes.size()-1;i>=0;i--){
			if(listOfNodes.get(i).moveType==GameField.RED){
				Point p=new Point();
				p.x=listOfNodes.get(i).points.get(0).x+Protocol.maxSize/2;
				p.y=listOfNodes.get(i).points.get(0).y+Protocol.maxSize/2;
				if(gameField.canAddMove((byte)p.x,(byte)p.y)){
					gameField.addMove((byte)p.x,(byte)p.y, GameField.RED);
				}
			}else{
				if(listOfNodes.get(i).blueIsDefault){
					continue;
				}else{
					for(int j=0;j<listOfNodes.get(i).points.size();j++){
						Point p=new Point();
						p.x=listOfNodes.get(i).points.get(j).x+Protocol.maxSize/2;
						p.y=listOfNodes.get(i).points.get(j).y+Protocol.maxSize/2;
						if(gameField.canAddMove((byte)p.x,(byte)p.y)){
							gameField.addMove((byte)p.x,(byte)p.y, GameField.BLUE);
						}
					}
				}
			}
		}
		
		gameFieldGUI.drawGameField(gameField,false);
		
		for(int j=0;j<listOfNodes.size();j++){//нарисовать синие квадраты когда выбор из нескольких синих ходов
			if(listOfNodes.get(j).moveType==GameField.BLUE&listOfNodes.get(j).points.size()>1){
				for(int i=0;i<listOfNodes.get(j).points.size();i++){
					gameFieldGUI.fillSquare(getGraphics(), listOfNodes.get(j).points.get(i).x+Protocol.maxSize/2, listOfNodes.get(j).points.get(i).y+Protocol.maxSize/2, Color.blue, listOfNodes.get(j).level);
				}
			}
		}
		
		//нарисовать точки с условиями
		if(selectedTreeNode.condition!=null){
			boolean isRed=selectedTreeNode.condition.enclosureType.equals("r")?true:false;
			for(int i=0;i<selectedTreeNode.condition.ifmoves.size();i++){
				gameFieldGUI.drawAttackDot(
						isRed,
						selectedTreeNode.condition.ifmoves.get(i).x+Protocol.maxSize/2,
						selectedTreeNode.condition.ifmoves.get(i).y+Protocol.maxSize/2
				);
			}
		}
	}
	
	//нарисовать квадраты с маркерами для дочерних элементов
	if(isShowChilds){
		TreeNodeDotsTree selectedTreeNode=treeOfDots.node.getTreeNodeByID(selectedNodeId);
		for(int i=0;i<selectedTreeNode.childNodes.size();i++){
			TreeNodeDotsTree child=selectedTreeNode.childNodes.get(i);
			if(child.moveType==GameField.RED){
				gameFieldGUI.drawSquare(getGraphics(), child.points.get(0).x+Protocol.maxSize/2, child.points.get(0).y+Protocol.maxSize/2, Color.red, i);
			}else if(child.moveType==GameField.BLUE){
				for(int j=0;j<child.points.size();j++){
					gameFieldGUI.drawSquare(getGraphics(), child.points.get(j).x+Protocol.maxSize/2, child.points.get(j).y+Protocol.maxSize/2, Color.blue, i);
				}
			}
		}
	}
}

//добавить действия при нажатии кнопок
void addButtons(){
	
	buttonAddCondition.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e){
			
			TreeNodeDotsTree selectedTreeNode=treeOfDots.node.getTreeNodeByID(selectedNodeId);
			if(selectedTreeNode.moveType==GameField.RED&&ifMovesDots.size()>0){
				
				String ifmoves="";
				for(int i=0;i<ifMovesDots.size();i++){
					ifmoves+=(ifMovesDots.get(i).x-Protocol.maxSize/2)+","+(ifMovesDots.get(i).y-Protocol.maxSize/2)+";";
				}
				
				String str="move:"+selectedTreeNode.points.get(0).x+","+selectedTreeNode.points.get(0).y +
						";ifmoves:" + ifmoves +
						(buttonType.getText().equals("RA")?"enclosure:r":"enclosure:b");
				
				TreeCondition condition=new TreeCondition(str);
				selectedTreeNode.setCondition(condition);
				selectedTreeNode.setUserObject(
						"<html><id="+selectedTreeNode.nodeId+
						"><font color=red>("+selectedTreeNode.points.get(0).x+","+selectedTreeNode.points.get(0).y+
						")</font>"+selectedTreeNode.getConditionText(condition)+"</html>");
				
				ifMovesDots.clear();
				
				repaintField();
				treeOfDots.treeModel.reload(selectedTreeNode);
				if(isAlwaysExpand)for(int i = 0; i < tree.getRowCount(); i ++)tree.expandRow(i);
			}
		}
	});
	
	buttonDeleteCondition.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			
			TreeNodeDotsTree selectedTreeNode=treeOfDots.node.getTreeNodeByID(selectedNodeId);
			
			selectedTreeNode.condition=null;
			selectedTreeNode.setUserObject(
					"<html><id="+selectedTreeNode.nodeId+
					"><font color=red>("+selectedTreeNode.points.get(0).x+","+selectedTreeNode.points.get(0).y+
					")</font></html>");
			
			repaintField();
			treeOfDots.treeModel.reload(selectedTreeNode);
			if(isAlwaysExpand)for(int i = 0; i < tree.getRowCount(); i ++)tree.expandRow(i);
		}
	});
	
	buttonAddMove.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			
			if(treeOfDots==null){
				treeOfDots=new Tree("<html><id=0><font>Дерево ходов</font></html>",template.templateID);
				template.tree=treeOfDots;
				tree.setModel(treeOfDots.treeModel);
				if(isAlwaysExpand)for(int i = 0; i < tree.getRowCount(); i ++)tree.expandRow(i);
			}
			
			TreeNodeDotsTree selectedTreeNode=treeOfDots.node.getTreeNodeByID(selectedNodeId);
			TreeNodeDotsTree n=null;
			if(blueDots.size()>0&(selectedTreeNode.moveType==GameField.RED|selectedTreeNode.level==0)){//add blue
				treeOfDots.nodeIndexes.add(new Integer(treeOfDots.nodeIndexes.size()));
				String blueMoves="";
				for(int i=0;i<blueDots.size();i++){
					blueMoves+="("+(blueDots.get(i).x-Protocol.maxSize/2)+","+(blueDots.get(i).y-Protocol.maxSize/2)+"); ";
				}
				n=new TreeNodeDotsTree("<html><id="+(treeOfDots.nodeIndexes.size()-1)+"><font color=blue>"+blueMoves+"</font></html>","",selectedTreeNode.level+1,treeOfDots.nodeIndexes,selectedTreeNode,null);
				n.addPoints(blueDots);				
				selectedTreeNode.childNodes.add(n);
				selectedTreeNode.add(n);
				n.moveType=GameField.BLUE;				
			}else if(redDot!=null){if(selectedTreeNode.moveType==GameField.BLUE|selectedTreeNode.level==0){//add red
				treeOfDots.nodeIndexes.add(new Integer(treeOfDots.nodeIndexes.size()));
				n=new TreeNodeDotsTree("<html><id="+(treeOfDots.nodeIndexes.size()-1)+"><font color=red>("+(redDot.x-Protocol.maxSize/2)+","+(redDot.y-Protocol.maxSize/2)+")</font></html>","",selectedTreeNode.level+1,treeOfDots.nodeIndexes,selectedTreeNode,null);
				selectedTreeNode.childNodes.add(n);
				selectedTreeNode.add(n);
				n.addPoint(redDot);
				n.moveType=GameField.RED;
			}}else if(redDot==null){if(blueDots.size()==0&(selectedTreeNode.moveType==GameField.RED|selectedTreeNode.level==0)){//add blue default				
				treeOfDots.nodeIndexes.add(new Integer(treeOfDots.nodeIndexes.size()));
				n=new TreeNodeDotsTree("<html><id="+(treeOfDots.nodeIndexes.size()-1)+"><font color=blue>default</font></html>","",selectedTreeNode.level+1,treeOfDots.nodeIndexes,selectedTreeNode,null);
				n.blueIsDefault=true;
				selectedTreeNode.childNodes.add(n);
				selectedTreeNode.add(n);
				n.moveType=GameField.BLUE;
			}}
			if(n==null){
				blueDots.clear();
				redDot=null;
				return;
			}
			
			repaintField();
			treeOfDots.treeModel.reload(selectedTreeNode);
			if(isAlwaysExpand)for(int i = 0; i < tree.getRowCount(); i ++)tree.expandRow(i);
			
			blueDots.clear();
			redDot=null;
		}
	});
	
	buttonEditMove.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			
			TreeNodeDotsTree selectedTreeNode=treeOfDots.node.getTreeNodeByID(selectedNodeId);
			if(selectedTreeNode.level==0)return;
			
			if(redDot!=null&selectedTreeNode.moveType==GameField.RED){//change red
				System.out.println("try change red");
				selectedTreeNode.setUserObject("<html><id="+selectedTreeNode.nodeId+"><font color=red>("+(redDot.x-Protocol.maxSize/2)+","+(redDot.y-Protocol.maxSize/2)+")</font></html>");
				selectedTreeNode.changePoint(redDot);
			}else if(selectedTreeNode.moveType==GameField.BLUE){
				if(blueDots.size()>0){//change blue
					System.out.println("try change blue");
					String blueMoves="";
					for(int i=0;i<blueDots.size();i++){
						blueMoves+="("+(blueDots.get(i).x-Protocol.maxSize/2)+","+(blueDots.get(i).y-Protocol.maxSize/2)+"); ";
					}
					selectedTreeNode.blueIsDefault=false;
					selectedTreeNode.setUserObject("<html><id="+selectedTreeNode.nodeId+"><font color=blue>"+blueMoves+"</font></html>");
					selectedTreeNode.changePoints(blueDots);
				}else if(redDot==null&blueDots.size()==0){//change blue to default
					System.out.println("try change blue default");
					selectedTreeNode.blueIsDefault=true;
					selectedTreeNode.setUserObject("<html><id="+selectedTreeNode.nodeId+"><font color=blue>default</font></html>");
				}
			}else{
				blueDots.clear();
				redDot=null;
				return;
			}
			
			repaintField();
			treeOfDots.treeModel.reload(selectedTreeNode);
			if(isAlwaysExpand)for(int i = 0; i < tree.getRowCount(); i ++)tree.expandRow(i);
			
			blueDots.clear();
			redDot=null;
		}
	});
	
	buttonDeleteMove.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			TreeNodeDotsTree selectedTreeNode=treeOfDots.node.getTreeNodeByID(selectedNodeId);
			if(selectedTreeNode.level==0)return;
			
			TreeNodeDotsTree parent=selectedTreeNode.parentNode;
			for(int i=0;i<parent.childNodes.size();i++){
				if(parent.childNodes.get(i).nodeId==selectedNodeId){
					parent.childNodes.remove(i);
					parent.remove(i);
					break;
				}
			}
			selectedNodeId=parent.nodeId;
			repaintField();
			treeOfDots.treeModel.reload(parent);
		}
	});
	
	buttonExpand.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			isAlwaysExpand=true;
			for(int i = 0; i < tree.getRowCount(); i ++)tree.expandRow(i);
		}
	});
	
	buttonCollapse.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			isAlwaysExpand=false;
			for(int i = 1; i < tree.getRowCount(); i ++)tree.collapseRow(i);
		}
	});
	
	buttonPaintChilds.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(isShowChilds){
				isShowChilds=false;
				repaintField();
			}else{
				isShowChilds=true;
				repaintField();
			}
		}
	});
	
	buttonSetTreeSymmetry.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JOptionPane msg=new JOptionPane();
			int symmetryType=TreeSymmetryType.treeSymmetryTypeGORIZONTAL;
			double symmetryBorder=0;
			try{
				int x=new Integer(msg.showInputDialog("<html>Введите число 0,1,2,3 - тип симметрии:<br>"
						+ TreeSymmetryType.treeSymmetryTypeGORIZONTAL+" - "+TreeSymmetryType.getTreeSymmetryTypeDescription(TreeSymmetryType.treeSymmetryTypeGORIZONTAL)+"<br>"
						+ TreeSymmetryType.treeSymmetryTypeVERTICAL+" - "+TreeSymmetryType.getTreeSymmetryTypeDescription(TreeSymmetryType.treeSymmetryTypeVERTICAL)+"<br>"
						+ TreeSymmetryType.treeSymmetryTypeMAIN_DIAGONAL+" - "+TreeSymmetryType.getTreeSymmetryTypeDescription(TreeSymmetryType.treeSymmetryTypeMAIN_DIAGONAL)+"<br>"
						+ TreeSymmetryType.treeSymmetryTypeSECOND_DIAGONAL+" - "+TreeSymmetryType.getTreeSymmetryTypeDescription(TreeSymmetryType.treeSymmetryTypeSECOND_DIAGONAL)+"</html>", "0"));
				if(x==0)symmetryType=TreeSymmetryType.treeSymmetryTypeGORIZONTAL;
				else if(x==1)symmetryType=TreeSymmetryType.treeSymmetryTypeVERTICAL;
				else if(x==2)symmetryType=TreeSymmetryType.treeSymmetryTypeMAIN_DIAGONAL;
				else if(x==3)symmetryType=TreeSymmetryType.treeSymmetryTypeSECOND_DIAGONAL;
				
				symmetryBorder=new Double(msg.showInputDialog("Введите смещение - число типа double", "0")).doubleValue();
				
				treeOfDots.symmetryType=symmetryType;
				treeOfDots.symmetryBorder=symmetryBorder;
				treeOfDots.setNodeText();
				treeOfDots.treeModel.reload(treeOfDots.node);
				
			}catch(Exception exc){}
		}
	});
}

//нарисовать шаблон на поле редактора деревьев
void drawTemplate(Template template){

	gameField=new GameField(Protocol.maxSize,Protocol.maxSize);

	for(int i=0;i<template.templateContent.length;i++){
		if(gameField.canAddMove((byte)(i%Protocol.maxSize/*+Template.size/2-1*/), (byte)(i/Protocol.maxSize/*+Template.size/2-1*/))){
			byte moveType=0;
			if(template.templateContent[i]==Protocol.templateDotType_BLUE)moveType=GameField.BLUE;
			else if(template.templateContent[i]==Protocol.templateDotType_RED)moveType=GameField.RED;
			if(moveType==0)continue;
			gameField.addMove((byte)(i%Protocol.maxSize/*+Template.size/2-1*/),(byte)(i/Protocol.maxSize/*+Template.size/2-1*/),moveType);
		}
	}
	gameFieldGUI.drawDotsForTreeEditor(gameField,true,template);
}

//получить координаты поля из координат пикселя при нажатии кнопки мыши
byte getMouseClickX(MouseEvent me){return (byte)(((double)me.getX()+8-GameGUI.offsetX)/(double)GameFieldGUI.cellSize);};
byte getMouseClickY(MouseEvent me){return (byte)(((double)me.getY()+8-GameGUI.offsetY+20)/(double)GameFieldGUI.cellSize);};
	
}
