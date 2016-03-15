//Класс TreeNodeDotsTree описывает каждый элемент дерева ходов
//и хранит в себе все вложенные элементы дерева (они имеют тот же тип).

package p_TreeEngine;

import java.awt.Point;
import java.util.ArrayList;
import javax.swing.tree.DefaultMutableTreeNode;
import p_DotsAI.Protocol;
import p_GUI.GameField;

public class TreeNodeDotsTree extends DefaultMutableTreeNode{

	public int level;//уровень дерева
	public int nodeId;//код элемента дерева
	public int moveType;//тип хода в данном элементе дерева - красный или синий
	
	//список ходов в данном элементе дерева. Для красных ходов (ходов ИИ) список имеет только одну точку, 
	//для синих ходов (ходов человека) список может иметь больше одной точки - это означает, что ход может быть сделан в одну из точек
	public ArrayList<Point> points;
	
	public TreeCondition condition;//условие применения хода
	public ArrayList<TreeNodeDotsTree> childNodes;//вложенные элементы дерева
	public TreeNodeDotsTree parentNode;//родительский элемент дерева
	public boolean blueIsDefault;//если это синий ход, то является ли он default
		
public TreeNodeDotsTree(String name,String str,int level,ArrayList<Integer> nodeIndexes,TreeNodeDotsTree parentNode,TreeCondition condition){	
	super(name);//текст, который выводится при описании элемента дерева в редакторе деревьев	
	blueIsDefault=false;	
	this.level=level;
	this.parentNode=parentNode;
	nodeId=nodeIndexes.get(nodeIndexes.size()-1);
	points=new ArrayList<Point>();
	this.condition=condition;
	childNodes=new ArrayList<TreeNodeDotsTree>();
	
	//добавить дочерние элементы дерева. Хотя дочерние элементы дерева могут быть или красными, или синими, 
	//дальше происходит попытка добавить оба типа, что не совсем правильно, хотя и не влияет на результат
	addRedMoves(str,nodeIndexes);
	addBlueMoves(str,nodeIndexes);			
}

//установить данному элементу дерева условие его применения. Метод вызывается из редактора деревьев
public void setCondition(TreeCondition condition){
	this.condition=condition;
}

//парсить строку и добавить дочерние элементы дерева синего цвета
public void addBlueMoves(String str,ArrayList<Integer> nodeIndexes){
	int idx=0;
	String beginB="<"+(level+1)+"b:";//начало описания элемента дерева
	String endB="</"+(level+1)+"b>";//конец описания элемента дерева
	for(;;){
		idx=str.indexOf(beginB);
		if(idx==-1)break;
		
		//обрезание строки
		String s="";
		try{s=str.substring(idx,str.indexOf(endB)+endB.length());}catch(Exception e){break;}
		str=str.replaceFirst(s, "");		
		s=s.replaceFirst(beginB, "");
		s=s.replaceFirst(endB, "");
		
		ArrayList<Point> points=getMovesFromString(s.substring(0,s.indexOf(">")),GameField.BLUE);//получить список синих ходов
		nodeIndexes.add(new Integer(nodeIndexes.size()));//добавить индекс элемента дерева
		TreeNodeDotsTree n=null;

		if(points.size()>0){//если закодирован синий ход с точными координатами
			String blueMoves="";//получить текст из списка ходов
			for(int i=0;i<points.size();i++){
				blueMoves+="("+points.get(i).x+","+points.get(i).y+"); ";
			}
			//создать дочерний элемент дерева
			n=new TreeNodeDotsTree("<html><id="+(nodeIndexes.size()-1)+"><font color=blue>"+blueMoves+"</font></html>",s,level+1,nodeIndexes,this,null);
			n.points=points;
		}else{//если закодирован синий ход как default
			//создать дочерний элемент дерева
			n=new TreeNodeDotsTree("<html><id="+(nodeIndexes.size()-1)+"><font color=blue>default</font></html>",s,level+1,nodeIndexes,this,null);
			n.blueIsDefault=true;
		}
		
		//сохранить дочерний элемент дерева
		childNodes.add(n);
		this.add(n);
		n.moveType=GameField.BLUE;
	}
}

//парсить строку и добавить дочерние элементы дерева красного цвета
public void addRedMoves(String str,ArrayList<Integer> nodeIndexes){
	int idx=0;
	String beginR="<"+(level+1)+"r:";//начало описания элемента дерева
	String endR="</"+(level+1)+"r>";//конец описания элемента дерева
	for(;;){
		idx=str.indexOf(beginR);
		if(idx==-1)break;
		
		//обрезание строки
		String s="";
		try{s=str.substring(idx,str.indexOf(endR)+endR.length());}catch(Exception e){break;}
		str=str.replaceFirst(s, "");
		s=s.replaceFirst(beginR, "");
		s=s.replaceFirst(endR, "");

		nodeIndexes.add(new Integer(nodeIndexes.size()));//добавить индекс элемента дерева
		
		TreeCondition condition=null;
		ArrayList<Point> points=null;
		if(s.substring(0,s.indexOf(">")).startsWith("move:")){//если у хода есть условие его применения
			condition=new TreeCondition(s.substring(0,s.indexOf(">")));
			points=new ArrayList<Point>();
			points.add(new Point(condition.move.x,condition.move.y));
		}else{//если у хода нет условия
			points=getMovesFromString(s.substring(0,s.indexOf(">")),GameField.RED);//получить красный ход
		}
		
		//сделать текст условия для отображения в редакторе деревьев
		String conditionStr="";
		if(condition!=null){
			conditionStr=getConditionText(condition);
		}
		
		//создать дочерний элемент дерева
		TreeNodeDotsTree n=new TreeNodeDotsTree("<html><id="+(nodeIndexes.size()-1)+"><font color=red>("+points.get(0).x+","+points.get(0).y+")</font>"+conditionStr+"</html>",s,level+1,nodeIndexes,this,condition);
		
		//сохранить дочерний элемент дерева
		childNodes.add(n);
		this.add(n);
		n.points=points;
		n.moveType=GameField.RED;
	}
}

//сделать текст условия для отображения в редакторе деревьев
public String getConditionText(TreeCondition condition){
	return " если создается "+
			(condition.enclosureType.equals("b")?"<font color=blue>синее</font>":"<font color=red>красное</font>")+
			" окружение при ходах "+condition.getStringOfIfMoves();
}

//парсить строку, чтобы получить список точек (ходов в элементе дерева)
public ArrayList<Point> getMovesFromString(String str,int type){	
	ArrayList<Point> points=new ArrayList<Point>();	
	
	//для красного хода можно получить только одну точку
	if(type==GameField.RED){		
		int x=new Integer(str.substring(0, str.indexOf(",")));
		int y=new Integer(str.substring(str.indexOf(",")+1));
		points.add(new Point(x, y));
		if(Math.abs(x)>7||Math.abs(y)>7)System.out.println("ошибочно задан ход в дереве "+x+","+y);
		return points;
	}
	
	//для синего хода можно получить одну точку или список точек, а также default ход
	if(type==GameField.BLUE){
		if(str.equals("default")){
			return points;
		}
		for(;;){
			int idx=str.indexOf(";");
			String s="";
			if(idx==-1)s=str;
			else s=str.substring(0, idx);
			
			int x=new Integer(s.substring(0, str.indexOf(",")));
			int y=new Integer(s.substring(str.indexOf(",")+1));
			points.add(new Point(x, y));
			if(Math.abs(x)>7||Math.abs(y)>7)System.out.println("ошибочно задан ход в дереве "+x+","+y);
			
			str=str.replaceFirst(s+";", "");
			
			if(idx==-1)return points;
		}		
	}
	return points;
}

//получить элемент дерева по его коду
public TreeNodeDotsTree getTreeNodeByID(int nodeId){
	if(this.nodeId==nodeId){
		return this;
	}else{
		for(int i=0;i<childNodes.size();i++){
			if(childNodes.get(i).getTreeNodeByID(nodeId)!=null){
				if(childNodes.get(i).getTreeNodeByID(nodeId).nodeId==nodeId){
					return childNodes.get(i).getTreeNodeByID(nodeId);
				}
			}
		}
	}
	return null;
}

//получить строку, содержащую дочерние элементы данного элемента дерева, для сохранения в базу шаблонов.
//Снова уточнение, что у элемента дерева должны быть или только синие, или только красные дочерние элементы
public String getString(){
	String s="";
	
	//получить красные дочерние элементы
	for(int i=0;i<childNodes.size();i++){
		if(childNodes.get(i)==null)continue;
		if(childNodes.get(i).moveType==GameField.RED){
			s+="<"+childNodes.get(i).level+"r:";
			
			if(childNodes.get(i).condition!=null)s+=childNodes.get(i).condition.getString();
			else s+=childNodes.get(i).points.get(0).x+","+childNodes.get(i).points.get(0).y;
			
			s+=">";
			s+=childNodes.get(i).getString();
			s+="</"+childNodes.get(i).level+"r>";
		}
	}
	
	//получить синие дочерние элементы
	for(int i=0;i<childNodes.size();i++){
		if(childNodes.get(i)==null)continue;
		if(childNodes.get(i).moveType==GameField.BLUE){
			s+="<"+childNodes.get(i).level+"b:";
			
			if(!childNodes.get(i).blueIsDefault){
				for(int j=0;j<childNodes.get(i).points.size();j++){
					s+=childNodes.get(i).points.get(j).x+","+childNodes.get(i).points.get(j).y+";";
				}
				s=s.substring(0, s.length()-1);//чтобы удалить последнюю ;
			}else{
				s+="default";
			}
			s+=">";
			s+=childNodes.get(i).getString();
			s+="</"+childNodes.get(i).level+"b>";
		}
	}
	return s;
}

//следующие 4 метода описывают изменение точки отсчета 
//из формата (0;15 - точка относительно угла шаблона) в формат (-7;7 - точка относительно центра шаблона)

//перевести формат и добавить в список точки
public void addPoints(ArrayList<Point> pointList){
	for(int i=0;i<pointList.size();i++){
		points.add(new Point(pointList.get(i).x-Protocol.maxSize/2,pointList.get(i).y-Protocol.maxSize/2));
	}
}

//перевести формат и добавить в список одну точку
public void addPoint(Point point){
	points.add(new Point(point.x-Protocol.maxSize/2,point.y-Protocol.maxSize/2));
}

//изменить список точек - очистить его и добавить новые точки
public void changePoints(ArrayList<Point> pointList){
	points.clear();
	for(int i=0;i<pointList.size();i++){
		points.add(new Point(pointList.get(i).x-Protocol.maxSize/2,pointList.get(i).y-Protocol.maxSize/2));
	}
}

//изменить список точек - очистить его и добавить одну новую точку
public void changePoint(Point point){
	points.clear();
	points.add(new Point(point.x-Protocol.maxSize/2,point.y-Protocol.maxSize/2));
}

}
