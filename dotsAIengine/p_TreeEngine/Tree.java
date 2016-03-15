//Класс Tree описывает дерево, принадлежащее к шаблону игровой ситуации.
//Каждому шаблону может соответствовать только одно дерево.
//Элементы дерева носят название "node" или "treeNode"

package p_TreeEngine;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.tree.DefaultTreeModel;

import p_TemplateEngine.TML;
import p_TemplateEngine.TemplateRotationType;

public class Tree{
	
	public int templateID;//индекс шаблона, которому принадлежит дерево
	public ArrayList<Integer> nodeIndexes;//максимальный индекс нода, увеличивается при добавлении нода (уникальный для каждого нода для навигации по нодам)
	public TreeNodeDotsTree node;//Корень дерева
	public DefaultTreeModel treeModel;//для обновления главного node, чтобы не создавать новый объект JTree
	public int symmetryType;//тип симметрии дерева
	public double symmetryBorder;//линия, относительно которой проходит симметрия дерева
	
	public TreeNodeDotsTree previousNode=null;//нужно только чтобы с него стирать зеленые маркеры, отображающие текущий ход в дереве

//создать объект для дерева
public Tree(String str,int templateID){
	changeTree(str,templateID);
}

//отдельный метод обработки текста дерева вне конструктора создан для случаев редактирования дерева
public void changeTree(String str,int templateID){
	this.templateID=templateID;//индекс шаблона, которому принадлежит дерево
	
	//определить тип симметрии для дерева
	if(str.contains("<"+TML.symmetry+":")){
		setMakrosSymmetry(str.substring(0,str.indexOf(">")));
		str=str.substring(str.indexOf(">")+1);
	}else{
		symmetryType=-1;
	}
	
	nodeIndexes=new ArrayList<Integer>();//список вложенных элементов дерева
	nodeIndexes.add(new Integer(0));//добавить первый вложенный элемент дерева
	node=new TreeNodeDotsTree("",str,0,nodeIndexes,null,null);//добавить остальные элементы дерева путем парсинга текста	
	setNodeText();
	treeModel=new DefaultTreeModel(node);//для управления деревом
}

//установить текст для корня дерева
public void setNodeText(){
	node.setUserObject("<html><id=0><font>Дерево ходов"+((symmetryType!=-1)?getSymmetryDescription():"")+"</font></html>");
}

//текст с описанием симметрии дерева
public String getSymmetryDescription(){
	return ". Симметрия макроса: "+TreeSymmetryType.getTreeSymmetryTypeDescription(symmetryType)+" ("+TreeSymmetryType.toStringMakrosSymmetryType(symmetryType)+"), смещение: "+symmetryBorder;
}

//установить симметрию дерева
void setMakrosSymmetry(String str){
	if(str.contains("gor"))symmetryType=TreeSymmetryType.treeSymmetryTypeGORIZONTAL;
	else if(str.contains("vert"))symmetryType=TreeSymmetryType.treeSymmetryTypeVERTICAL;
	else if(str.contains("mdiag"))symmetryType=TreeSymmetryType.treeSymmetryTypeMAIN_DIAGONAL;
	else if(str.contains("sdiag"))symmetryType=TreeSymmetryType.treeSymmetryTypeSECOND_DIAGONAL;
			
	symmetryBorder=new Double(str.substring(str.indexOf(",")+1)).doubleValue();
}

//получить текст с типом симметрии
public String getSymmetryTypeString(){
	return "<"+TML.symmetry+":"+TreeSymmetryType.toStringMakrosSymmetryType(symmetryType)+","+symmetryBorder+">";
}

//получить симметричную точку дерева
public Point getSymmetryPoint(Point point,int templateRotationType){
	try{	
		//получить повернутую точку дерева согласно развороту шаблона
		Point p=TemplateRotationType.getTransformPoint(templateRotationType, point);
		
		//получить повернутую точку дерева согласно симметрии дерева
		if(symmetryType==TreeSymmetryType.treeSymmetryTypeGORIZONTAL){
			double d=Math.abs(p.x-symmetryBorder);
			if(symmetryBorder>p.x)return TemplateRotationType.getTransformPoint(templateRotationType,new Point((int)(symmetryBorder+d),p.y));
			if(symmetryBorder<p.x)return TemplateRotationType.getTransformPoint(templateRotationType,new Point((int)(symmetryBorder-d),p.y));
			if(symmetryBorder==p.x){System.out.println("error GORIZONTAL symmetryBorder==p.x");return p;}
		}
		if(symmetryType==TreeSymmetryType.treeSymmetryTypeVERTICAL){
			double d=Math.abs(p.y-symmetryBorder);
			if(symmetryBorder>p.y)return TemplateRotationType.getTransformPoint(templateRotationType,new Point(p.x,(int)(symmetryBorder+d)));
			if(symmetryBorder<p.y)return TemplateRotationType.getTransformPoint(templateRotationType,new Point(p.x,(int)(symmetryBorder-d)));
			if(symmetryBorder==p.y){System.out.println("error VERTICAL symmetryBorder==p.y");return p;}
		}
		
		//для диагональных симметрий вопрос не изучен и возможны ошибки
		//if(symmetryType==MakrosSymmetryType.MAIN_DIAGONAL)return new Point(-p.y,-p.x);
		//if(symmetryType==MakrosSymmetryType.SECOND_DIAGONAL)return new Point(-p.y,-p.x);
		
		return p;
	}catch(Exception e){return point;}
}

}
