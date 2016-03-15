//Класс TreeApplicationInGame используется для поиска хода в дереве
//в определенной игровой ситуации с известными координатами
//и с известным типом разворота шаблона

package p_TreeEngine;

import java.awt.Point;
import java.util.Random;
import p_DotsAI.Protocol.Game;
import p_GUI.GameField;
import p_TemplateEngine.TemplateFieldSideType;
import p_TemplateEngine.TemplateRotationType;
import p_TemplateEngine.TemplateType;
import p_TemplateEngine.TemplateView;

public class TreeApplicationInGame {

	public Point center;//центр шаблона, в котором находится дерево
	public TemplateView templateView;//используемое отображение шаблона
	public Tree tree;//используемое дерево
	public Point transformAIPoint;//повернутый ход ИИ с учетом типа разворота
	private TreeNodeDotsTree currentNode;//текущий элемент дерева
	private Random random;//генератор случайных чисел
	private int isSearchBySymmetry;//поиск хода ИИ: 0-еще не найден, 1-не по симметрии, 2-по симметрии
	public int templateRotationType;//тип поворота шаблона
	public int templateType;//тип шаблона
	public int templateID;//уникальный идентификатор шаблона

//создание объекта применения дерева в конкретной игровой ситуации
public TreeApplicationInGame(Game game,TemplateView templateView,int x,int y){
	//сохранение информации об игровой ситуации и применении алгоритмов ИИ
	this.templateView=templateView;
	tree=templateView.template.tree;
	isSearchBySymmetry=0;
	currentNode=tree.node;
	templateRotationType=templateView.templateRotationType;
	templateType=templateView.template.templateType;
	templateID=templateView.template.templateID;
	
	random=new Random();
	
	//определение центра шаблона
	center=new Point(x,y);//центр шаблона, в котором находится дерево
	if(TemplateType.isSide(templateType)){//если ипользуется шаблон бокового типа
		int s=TemplateFieldSideType.getFieldSideType(game, new Point(x,y));
		if(s==TemplateFieldSideType.templateFieldSideTypeTOP){center.y=templateView.template.sizeWithNotAny/2-1;center.x=x;}
		if(s==TemplateFieldSideType.templateFieldSideTypeBOTTOM){center.y=game.sizeY-templateView.template.sizeWithNotAny/2;center.x=x;}			
		if(s==TemplateFieldSideType.templateFieldSideTypeLEFT){center.x=templateView.template.sizeWithNotAny/2-1;center.y=y;}			
		if(s==TemplateFieldSideType.templateFieldSideTypeRIGHT){center.x=game.sizeX-templateView.template.sizeWithNotAny/2;center.y=y;}			
	}
}

//поиск хода в дереве
public boolean isExistsLevelMove(Game game,Point lastBluePoint,boolean isMoveOnlyByDefault,Point recommendedMove){
	if((lastBluePoint.x!=center.x||lastBluePoint.y!=center.y)&&!isMoveOnlyByDefault){//если совпадают, то переходить сразу к поиску default хода
		
		//найти в дереве lastBluePoint, в котором затем искать ответный красный ход
		for(int i=0;i<currentNode.childNodes.size();i++){
			TreeNodeDotsTree parent=currentNode.childNodes.get(i);
			if(parent.blueIsDefault)continue;
			if(parent.moveType==GameField.RED)continue;
			
			for(int j=0;j<parent.points.size();j++){
				Point p=TemplateRotationType.getTransformPoint(templateRotationType, parent.points.get(j));						
				
				if(isSearchBySymmetry==0||isSearchBySymmetry==1){
					if(p.x+center.x==lastBluePoint.x&&p.y+center.y==lastBluePoint.y){
						TreeNodeDotsTree n=null;
						
						if(recommendedMove==null){
							n=getRandomChildNode(game,parent,GameField.RED);
						}else{//рекомендованный ход - при повторе игры делать ход, который был, т.к. в деревьях может быть случайный выбор хода
							for(int k=0;k<parent.childNodes.size();k++){
								if(parent.childNodes.get(k).moveType==GameField.RED){
									n=parent.childNodes.get(k);
									Point newPoint=TemplateRotationType.getTransformPoint(templateRotationType, n.points.get(0));
									if(recommendedMove.x!=center.x+newPoint.x|recommendedMove.y!=center.y+newPoint.y){
										continue;
									}
								}
							}
						}
						
						if(n==null)continue;
						Point newPoint=TemplateRotationType.getTransformPoint(templateRotationType, n.points.get(0));
						transformAIPoint=new Point(center.x+newPoint.x,center.y+newPoint.y);	
						setMarkerOnCurrentNode(n);
						currentNode=n;
						isSearchBySymmetry=1;
						return true;
					}
				}
				
				if(isSearchBySymmetry==0||isSearchBySymmetry==2){
					if(tree.symmetryType==-1)continue;
					Point pSymmetry=tree.getSymmetryPoint(p,templateRotationType);
					
					if(pSymmetry.x+center.x==lastBluePoint.x&&pSymmetry.y+center.y==lastBluePoint.y){
						
						TreeNodeDotsTree n=null;
						
						if(recommendedMove==null){
							n=getRandomChildNode(game,parent,GameField.RED);
						}else{//рекомендованный ход - при повторе игры делать ход, который был, т.к. в макромах может быть случайный выбор хода
							for(int k=0;k<parent.childNodes.size();k++){
								if(parent.childNodes.get(k).moveType==GameField.RED){
									n=parent.childNodes.get(k);
									Point newPoint=TemplateRotationType.getTransformPoint(templateRotationType, n.points.get(0));
									Point newPointSymmetry=tree.getSymmetryPoint(newPoint,templateRotationType);
									if(recommendedMove.x!=center.x+newPointSymmetry.x|recommendedMove.y!=center.y+newPointSymmetry.y){
										continue;
									}
								}
							}
						}
						
						if(n==null)continue;
						Point newPoint=TemplateRotationType.getTransformPoint(templateRotationType, n.points.get(0));
						Point newPointSymmetry=tree.getSymmetryPoint(newPoint,templateRotationType);
						
						transformAIPoint=new Point(center.x+newPointSymmetry.x,center.y+newPointSymmetry.y);				
						setMarkerOnCurrentNode(n);
						currentNode=n;
						isSearchBySymmetry=2;
						return true;
					}
				}
			}
		}
	}
	
	//если lastBluePoint не найден или если первый ход в игре красный, то поиск default хода
	for(int i=0;i<currentNode.childNodes.size();i++){		
		TreeNodeDotsTree parent=currentNode.childNodes.get(i);
		if(!parent.blueIsDefault)continue;
		if(parent.moveType==GameField.RED)continue;
		
		TreeNodeDotsTree n=null;
		double randomDouble=0;
		if(recommendedMove==null){
			randomDouble=random.nextDouble();
			n=getRandomChildNode(game,parent,GameField.RED);
			
			if(tree.symmetryType==-1)randomDouble=0.6;
			
		}else{//рекомендованный ход - при повторе игры делать ход, который был, т.к. в макромах может быть случайный выбор хода
			for(int k=0;k<parent.childNodes.size();k++){
				if(parent.childNodes.get(k).moveType==GameField.RED){
					n=parent.childNodes.get(k);
					
					Point newPoint=TemplateRotationType.getTransformPoint(templateRotationType, n.points.get(0));
					if(recommendedMove.x==center.x+newPoint.x&recommendedMove.y==center.y+newPoint.y){
						randomDouble=0.6;
						break;
					}
					
					Point pSymmetry=tree.getSymmetryPoint(newPoint,templateRotationType);
					if(recommendedMove.x==center.x+pSymmetry.x&recommendedMove.y==center.y+pSymmetry.y&tree.symmetryType!=-1){
						randomDouble=0.4;
						break;
					}
				}
			}
		}
		if(n==null)continue;			
		Point newPoint=TemplateRotationType.getTransformPoint(templateRotationType, n.points.get(0));
				
		if(isSearchBySymmetry==0){
			if(randomDouble>0.5){
				
				if(recommendedMove!=null){//рекомендованный ход - при повторе игры делать ход, который был, т.к. в макромах может быть случайный выбор хода
					if(recommendedMove.x!=center.x+newPoint.x|recommendedMove.y!=center.y+newPoint.y){
						continue;
					}							
				}
				
				transformAIPoint=new Point(center.x+newPoint.x,center.y+newPoint.y);
				isSearchBySymmetry=1;
			}else{
				Point pSymmetry=tree.getSymmetryPoint(newPoint,templateRotationType);
				
				if(recommendedMove!=null){//рекомендованный ход - при повторе игры делать ход, который был, т.к. в деревьях может быть случайный выбор хода
					if(recommendedMove.x!=center.x+pSymmetry.x|recommendedMove.y!=center.y+pSymmetry.y){
						continue;
					}							
				}
				
				transformAIPoint=new Point(center.x+pSymmetry.x,center.y+pSymmetry.y);
				isSearchBySymmetry=2;
			}
		}else if(isSearchBySymmetry==1){							
			transformAIPoint=new Point(center.x+newPoint.x,center.y+newPoint.y);
		}else if(isSearchBySymmetry==2){
			Point pSymmetry=tree.getSymmetryPoint(newPoint,templateRotationType);
			transformAIPoint=new Point(center.x+pSymmetry.x,center.y+pSymmetry.y);				
		}
		
		setMarkerOnCurrentNode(n);
		currentNode=n;
		return true;
	}
	return false;
}

//метод ставит зеленый маркер в редакторе дерева напротив элемента дерева, по которому найден следующий ход
private void setMarkerOnCurrentNode(TreeNodeDotsTree n){
	//стереть маркер с предыдущего элемента дерева
	if(tree.previousNode!=null){
		tree.previousNode.setUserObject(tree.previousNode.toString().replaceAll("<font color=green>&#9679;</font>", ""));
		tree.treeModel.reload(tree.previousNode);
	}	
	tree.previousNode=n;
	
	//установить маркер на новый элемент дереваs
	currentNode.setUserObject(currentNode.toString().replaceAll("<font color=green>&#9679;</font>", ""));
	n.setUserObject(n.toString().replaceAll("<font color=green>&#9679;</font>", ""));
	if(n.moveType==GameField.BLUE)n.setUserObject(n.toString().replaceFirst("<font color=blue>","<font color=green>&#9679;</font><font color=blue>"));
	else n.setUserObject(n.toString().replaceFirst("<font color=red>","<font color=green>&#9679;</font><font color=red>"));
	tree.treeModel.reload(currentNode);
	tree.treeModel.reload(n);
}

//получить один из ходов ИИ
private TreeNodeDotsTree getRandomChildNode(Game game,TreeNodeDotsTree parentNode,int type){
	
	int childCount=0;//число дочерних ходов ИИ
	int childCountWithoutConditions=0;//число дочерних ходов ИИ с заданными условиями применения
	for(int i=0;i<parentNode.childNodes.size();i++){
		if(parentNode.childNodes.get(i).moveType==type)childCount++;
	}
	
	//если ходов ИИ у синего элемента нет, это можно трактовать как ошибку человека, который не задал ход ИИ,
	//хотя в некоторых случаях (для BEGIN шаблонов, используемых вначале игры), наличие синего элемента без дочерних красных
	//означает, что продолжение данной ситуации есть в другом BEGIN шаблоне,
	//хотя это явно не задается, что можно считать недостатком графического интерфейса
	if(childCount==0){System.out.println("childCount=0, tree idx="+tree.templateID);return null;}
	else if(childCount==1){//если дочерний элемент только один			
		if(parentNode.childNodes.get(0).moveType==type){//и это ход ИИ
			if(parentNode.childNodes.get(0).condition!=null){//в котором есть условие
				if(isSatisfies(game,parentNode.childNodes.get(0))){//и если это условие удовлетворено
					return parentNode.childNodes.get(0);//то этот ход ИИ возвращается
				}else{//если условие не удовлетворено
					addBSSmoves(game,parentNode);//добавляются ходы для защиты от окружений, которые могут быть против ИИ
					return null;
				}
			}else return parentNode.childNodes.get(0);//если нет условия, то ход ИИ возвращается
		}		
	}else{//если несколько дочерних элементов. При наличии равнозначных элементов ход берется случайно, обеспечивая непредсказуемость ИИ
		
		/*
		 здесь проверяются условия. Приоритет проверки условий установлен
		 по количеству условных ходов (на которых проверяется возможность нового окружения).
		 Чем меньше условных ходов, тем больше приоритет.
		 Приоритет имеют красные окружения, т.к. ближайший ход у ИИ.
		*/
		
		//найти максимальное число ходов для проверки условия на новое окружение
		int maxLengthOfIfMoves=0;
		for(int i=0;i<parentNode.childNodes.size();i++){
			if(parentNode.childNodes.get(i).condition!=null){
				if(parentNode.childNodes.get(i).condition.ifmoves.size()>maxLengthOfIfMoves){
					maxLengthOfIfMoves=parentNode.childNodes.get(i).condition.ifmoves.size();
				}
			}
		}
		
		//с учетом приоритетов пройти все условные ходы и найти подходящий ход (удовлетворяющий условию своего применения)
		int currentLengthOfIfMoves=1;
		while(currentLengthOfIfMoves<=maxLengthOfIfMoves){
			for(int i=0;i<parentNode.childNodes.size();i++){
				if(parentNode.childNodes.get(i).condition!=null){//если у хода есть условие
					if(
							//если условие соответствует длине списка условных ходов для проверки и тип возможного окружения красный
							parentNode.childNodes.get(i).condition.ifmoves.size()==currentLengthOfIfMoves
							&&
							parentNode.childNodes.get(i).condition.enclosureType.equalsIgnoreCase("r")
						){
							if(isSatisfies(game,parentNode.childNodes.get(i))){//если ход удовлетворяет условию
								return parentNode.childNodes.get(i);//то ход возвращается
							}
					}
				}
			}
			for(int i=0;i<parentNode.childNodes.size();i++){
				if(parentNode.childNodes.get(i).condition!=null){//если у хода есть условие
					if(
							//если условие соответствует длине списка условных ходов для проверки и тип возможного окружения синий
							parentNode.childNodes.get(i).condition.ifmoves.size()==currentLengthOfIfMoves
							&&
							parentNode.childNodes.get(i).condition.enclosureType.equalsIgnoreCase("b")
						){
							if(isSatisfies(game,parentNode.childNodes.get(i))){//если ход удовлетворяет условию
								return parentNode.childNodes.get(i);//то ход возвращается
							}
					}
				}
			}
			currentLengthOfIfMoves++;//проверяемая длина списка условных ходов на данном шаге
		}
		
		//следующий код выполняется, если не было выполнено ни одного условия
		
		addBSSmoves(game,parentNode);//добавляются ходы для защиты от окружений, которые могут быть против ИИ
		
		//поиск числа дочерних ветвей дерева, не имеющих ходов с условиями
		for(int i=0;i<parentNode.childNodes.size();i++){
			if(parentNode.childNodes.get(i).moveType==type&&parentNode.childNodes.get(i).condition==null)childCountWithoutConditions++;
		}
		
		//поиск случайного хода из набора ходов (ходы с условиями не берутся во внимание)
		int childIdx=random.nextInt(childCountWithoutConditions);
		int findedIdx=0;
		for(int i=0;i<parentNode.childNodes.size();i++){
			if(parentNode.childNodes.get(i).moveType==type&&parentNode.childNodes.get(i).condition==null){
				if(childIdx==findedIdx){
					return parentNode.childNodes.get(i);
				}else{
					findedIdx++;
				}
			}
		}
	}
	return null;
}

//проверка, удовлетворяется ли условие применения хода
boolean isSatisfies(Game game,TreeNodeDotsTree node){
	GameField clonedGame=game.gameField.clone();//клонирование игрового поля
	
	//ход, который делается в последнюю очередь. Этот ход (если он есть) равен ответному ходу ИИ.
	//откладывание такого хода связано с тем, что он может быть выполнен в домик
	Point pendingDot=null;
	
	//выполнение проверочных ходов в клонированной игре
	for(int i=0;i<node.condition.ifmoves.size();i++){
		
		//проверка на ход, который надо отложить
		if(node.condition.ifmoves.get(i).x==node.condition.move.x&&node.condition.ifmoves.get(i).y==node.condition.move.y){
			pendingDot=TemplateRotationType.getTransformPoint(templateRotationType,new Point(node.condition.move.x,node.condition.move.y));
			pendingDot.x+=center.x;
			pendingDot.y+=center.y;
			continue;
		}
		
		//разворот условного хода и его выполнение
		Point p=TemplateRotationType.getTransformPoint(templateRotationType,node.condition.ifmoves.get(i));
		p.x+=center.x;
		p.y+=center.y;
		clonedGame.addMove(p,node.condition.enclosureType.equals("b")?GameField.BLUE:GameField.RED);
	}
	
	//выполнение отложенного хода
	if(pendingDot!=null){
		clonedGame.addMove(pendingDot,node.condition.enclosureType.equals("b")?GameField.BLUE:GameField.RED);
	}
	
	//если после постановки проверочных ходов появилось больше окружений, то условие выполняется
	if(node.condition.enclosureType.equals("b")){
		if(clonedGame.blueEnclosures.size()>game.gameField.blueEnclosures.size()){			
			return true;
		}
	}else if(node.condition.enclosureType.equals("r")){
		if(clonedGame.redEnclosures.size()>game.gameField.redEnclosures.size()){
			return true;
		}	
	}
	return false;
}

//добавляются ходы, которые при поиске хода ИИ в дальнейшем проверяются на возможность окружения против ИИ.
//BSS - blue surround security - защита от синих окружений.
//Это окружения, которые выполняются вдали от последнего хода синих, т.е. для окружения остается только один разрыв в цепи.
//ИИ должен защититься от окружения постановкой точки в этот разрыв
void addBSSmoves(Game game,TreeNodeDotsTree parentNode){
	for(int i=0;i<parentNode.childNodes.size();i++){//просматриваем все дочерние элементы, содержащие ходы ИИ
		if(parentNode.childNodes.get(i).condition!=null){//если у хода есть условие
			//если проверочные хода синего цвета и их число равно одному, то добавить точку BSS типа в список
			if(parentNode.childNodes.get(i).condition.enclosureType.equals("b")&parentNode.childNodes.get(i).condition.ifmoves.size()==1){
				Point p=new Point(parentNode.childNodes.get(i).condition.move.x,parentNode.childNodes.get(i).condition.move.y);
				p=TemplateRotationType.getTransformPoint(templateRotationType,p);
				p.x+=center.x;
				p.y+=center.y;
				game.BSSpoints.add(p);
			}
		}
	}
}

}
