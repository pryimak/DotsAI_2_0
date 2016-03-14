//Класс Template описывает шаблоны игровых ситуаций. Все шаблоны имеют размер 15х15 точек.
//Шаблон сравнивается с игровой ситуацией на поле. При их совпадении ищется ход ИИ в дереве,
//которое относится к данному шаблону.
//Т.к. шаблон можно развернуть восемью способами, то для каждого разворота есть отдельный класс TemplateView
//и на самом деле игровая ситауция на поле сравнивается со всеми разворотами шаблона.
//В шаблоне есть зеленые точки, называющиеся ANY. Такие точки показывают, что неважно, какая точка соответсвует зеленой точке на игровом поле.
//По зеленым точкам можно не проводить сравнение шаблона с игровой ситауцией.
//Обычно зеленые точки находятся с краев шаблона, поэтому у шаблона есть значимая центральная часть,
//по которой стоит проводить сравнение. И тогда по краям сравнение не проводится.
//Если проводить сравнение по всему шаблону, а не только по значимой части, то скорость сравнения снизится в 5-15 раз.

package p_TemplateEngine;

import java.awt.Point;
import java.util.ArrayList;

import p_DotsAI.Protocol;
import p_DotsAI.Protocol.Game;
import p_TreeEngine.Tree;
import p_TreeEngine.TreeApplicationInGame;

public class Template{
	
	public int templateID;//уникальный индекс шаблона
	public int templateType;//тип шаблона
	public byte[] templateContent;//содержимое шаблона - игровая ситуация
	ArrayList<TemplateView> templateView;//отображение шаблонов с учетом его разворотов 0,90,180,270,Vert,Gor,Vert90,ViewGor90
	public Tree tree;//дерево ходов, принадлежащее шаблону
	public int ANYDotsCount;//для сортировки базы шаблонов перед ее сохранением	
	public int sizeWithNotAny=Protocol.maxSize;//размер значимой части шаблона (без зеленых "ANY" точек)
	
public Template(String strTemplate){
	ANYDotsCount=0;
	tree=null;	
	changeTemplate(strTemplate);//шаблон парсится из строки в этом методе
}

//отдельный метод обработки текста шаблона вне конструктора создан для случаев редактирования шаблона
public void changeTemplate(String strTemplate){
	
	//далее из строки получаем индекс, тип, содержимое шаблона и его дерево
	templateID=new Integer(strTemplate.substring(strTemplate.indexOf("<"+TML.id+":")+TML.id.length()+2,strTemplate.indexOf("<"+TML.id+":")+TML.id.length()+7)).intValue();
	templateType=TemplateType.getTemplateType(strTemplate.substring(strTemplate.indexOf("<"+TML.type+":")+TML.type.length()+2,strTemplate.indexOf("<"+TML.type+":")+TML.type.length()+5));
	
	String content=strTemplate.substring(strTemplate.indexOf("<"+TML.content+":")+TML.content.length()+2,strTemplate.indexOf("<"+TML.content+":")+TML.content.length()+Protocol.maxSize*Protocol.maxSize+2);
	templateContent=new byte[Protocol.maxSize*Protocol.maxSize];
	for(int i=0;i<Protocol.maxSize*Protocol.maxSize;i++){
		templateContent[i]=new Byte(content.substring(i, i+1)).byteValue();
	}
	
	if(strTemplate.contains("<"+TML.tree+">")&strTemplate.contains("</"+TML.tree+">")){
		tree=new Tree(strTemplate.substring(strTemplate.indexOf("<"+TML.tree+">")+TML.tree.length()+2,strTemplate.indexOf("</"+TML.tree+">")),templateID);
	}	
	
	templateView=new ArrayList<TemplateView>();//создаем список отображений шаблонов
	
	templateView.add(new TemplateView(this,TemplateRotationType.templateRotationTypeR0));//отображение шаблона без разворота, как он хранится в базе
	
	//далее добавляем отображения шаблонов в список, но при совпадении отображения с уже имеющимся в списке, оно не добавляется
	byte[] strTemplate180=TemplateRotationType.getTransformArray(TemplateRotationType.templateRotationTypeR180,templateContent);
	if(isEqualsLikeTemplateView(strTemplate180,templateContent)){//чтобы не добавлять одинаковые TemplateView	
	}else{
		templateView.add(new TemplateView(this,TemplateRotationType.templateRotationTypeR180));//разворот на 180 градусов
	}
	
	byte[] strTemplateGorizontal=TemplateRotationType.getTransformArray(TemplateRotationType.templateRotationTypeGORIZONTAL,templateContent);
	if(isEqualsLikeTemplateView(strTemplateGorizontal,templateContent)//чтобы не добавлять одинаковые TemplateView
			||isEqualsLikeTemplateView(strTemplateGorizontal,strTemplate180)){
	}else{
		templateView.add(new TemplateView(this,TemplateRotationType.templateRotationTypeGORIZONTAL));//разворот по горизонтали
	}
	
	byte[] strTemplateVertical=TemplateRotationType.getTransformArray(TemplateRotationType.templateRotationTypeVERTICAL,templateContent);
	if(isEqualsLikeTemplateView(strTemplateVertical,templateContent)//чтобы не добавлять одинаковые TemplateView
			||isEqualsLikeTemplateView(strTemplateVertical,strTemplate180)
			||isEqualsLikeTemplateView(strTemplateVertical,strTemplateGorizontal)){
	}else{
		templateView.add(new TemplateView(this,TemplateRotationType.templateRotationTypeVERTICAL));//разворот по вертикали
	}
	
	byte[] strTemplate90=TemplateRotationType.getTransformArray(TemplateRotationType.templateRotationTypeR90,templateContent);
	if(isEqualsLikeTemplateView(strTemplate90,templateContent)//чтобы не добавлять одинаковые TemplateView
			||isEqualsLikeTemplateView(strTemplate90,strTemplate180)
			||isEqualsLikeTemplateView(strTemplate90,strTemplateGorizontal)
			||isEqualsLikeTemplateView(strTemplate90,strTemplateVertical)){
	}else{
		templateView.add(new TemplateView(this,TemplateRotationType.templateRotationTypeR90));//разворот на 90 градусов
	}

	byte[] strTemplate270=TemplateRotationType.getTransformArray(TemplateRotationType.templateRotationTypeR270,templateContent);
	if(isEqualsLikeTemplateView(strTemplate270,templateContent)//чтобы не добавлять одинаковые TemplateView
			||isEqualsLikeTemplateView(strTemplate270,strTemplate180)
			||isEqualsLikeTemplateView(strTemplate270,strTemplateGorizontal)
			||isEqualsLikeTemplateView(strTemplate270,strTemplateVertical)
			||isEqualsLikeTemplateView(strTemplate270,strTemplate90)){
		
	}else{
		templateView.add(new TemplateView(this,TemplateRotationType.templateRotationTypeR270));//разворот на 270 градусов
	}
	
	byte[] strTemplateGorizontal90=TemplateRotationType.getTransformArray(TemplateRotationType.templateRotationTypeGORIZONTAL90,templateContent);
	if(isEqualsLikeTemplateView(strTemplateGorizontal90,templateContent)//чтобы не добавлять одинаковые TemplateView
			||isEqualsLikeTemplateView(strTemplateGorizontal90,strTemplate180)
			||isEqualsLikeTemplateView(strTemplateGorizontal90,strTemplateGorizontal)
			||isEqualsLikeTemplateView(strTemplateGorizontal90,strTemplateVertical)
			||isEqualsLikeTemplateView(strTemplateGorizontal90,strTemplate90)
			||isEqualsLikeTemplateView(strTemplateGorizontal90,strTemplate270)){
	}else{
		templateView.add(new TemplateView(this,TemplateRotationType.templateRotationTypeGORIZONTAL90));//разворот на 90 градусов и по горизонтали
	}
	
	byte[] strTemplateVertical90=TemplateRotationType.getTransformArray(TemplateRotationType.templateRotationTypeVERTICAL90,templateContent);
	if(isEqualsLikeTemplateView(strTemplateVertical90,templateContent)//чтобы не добавлять одинаковые TemplateView
			||isEqualsLikeTemplateView(strTemplateVertical90,strTemplate180)
			||isEqualsLikeTemplateView(strTemplateVertical90,strTemplateGorizontal)
			||isEqualsLikeTemplateView(strTemplateVertical90,strTemplateVertical)
			||isEqualsLikeTemplateView(strTemplateVertical90,strTemplate90)
			||isEqualsLikeTemplateView(strTemplateVertical90,strTemplate270)
			||isEqualsLikeTemplateView(strTemplateVertical90,strTemplateGorizontal90)){
	}else{
		templateView.add(new TemplateView(this,TemplateRotationType.templateRotationTypeVERTICAL90));//разворот на 90 градусов и по вертикали
	}
	
	setSizeWithNotAny(templateContent);//найти значимый размер шаблона (без ANY точек на краях)
}

//сравнить два TemplateView по их контенту
boolean isEqualsLikeTemplateView(byte[] arr1,byte[] arr2){
	for(int i=0;i<arr1.length;i++){
		if(arr1[i]!=arr2[i]){
			return false;
		}
	}
	return true;
}

//для сортировки базы шаблонов перед ее сохранением найти число ANY точек
public void setANYDotsCount(){
	ANYDotsCount=0;
	for(int i=0;i<templateContent.length;i++){
		if(templateContent[i]==Protocol.templateDotType_ANY){
			ANYDotsCount++;
		}
	}
}

//используется при поиске шаблона для хода ИИ относительно одной точки Point point
public TemplateView getTemplateViewIfEqualsLikeAreaByPoint(Game game,Point point,byte[][] fieldState,int templateType){
	boolean isSide=TemplateType.isSide(templateType);//если тип шаблона - боковой (для края поля)
	int fieldSideType=TemplateFieldSideType.getFieldSideType(game, point);//тип боковой стороны - лево, право, верх, низ
	for(int i=0;i<templateView.size();i++){//проходим список TemplateView, относящихся к шаблону
		if(templateView.get(i).isEquals(game,point,fieldState,isSide,fieldSideType)){
			return templateView.get(i);//найдено совпадение игровой ситуации с templateView 
		}
	}	
	return null;
}

//используется при поиске шаблона для хода ИИ относительно списка точек ArrayList<Point> pointList
public boolean isMoveIfEqualsLikeAreaByPointList(Protocol protocol,Game game,ArrayList<Point> pointList,byte[][] fieldState,int templateType,Point recommendedMove){	
	for(int j=0;j<pointList.size();j++){
		
		//из списка TemplateView, относящихся к шаблону, ищем совпадающий с игровой ситуацией на поле
		TemplateView tw=getTemplateViewIfEqualsLikeAreaByPoint(game,pointList.get(j),fieldState,templateType);
		
		if(tw==null){//нет совпадения игровой ситуации ни с одним templateView в шаблоне
			continue;
		}
		
		try{
			//найдено совпадение игровой ситуации с templateView, далее ищется ход ИИ в дереве
			TreeApplicationInGame ta=new TreeApplicationInGame(game,tw,pointList.get(j).x,pointList.get(j).y);//применение дерева в игровой ситуации
			if(ta.isExistsLevelMove(game,game.gameField.lastDot,false,recommendedMove)){//если ход ИИ есть в дереве
				if(isMoveByAI(protocol,game,ta)){//если ход ИИ возможен на поле
					game.treeApplicationInGame.add(ta);//добавляем в список использованных в игре деревьев
					return true;
				}else continue;				
			}else continue;
		}catch(Exception e){continue;}
	}
	return false;
}

//используется при поиске шаблона для хода ИИ относительно одной точки Point point
public boolean isMoveIfEqualsLikeAreaByPoint(Protocol protocol,Game game,Point point,byte[][] fieldState,int templateType,Point recommendedMove){
	
	//из списка TemplateView, относящихся к шаблону, ищем совпадающий с игровой ситуацией на поле
	TemplateView tw=getTemplateViewIfEqualsLikeAreaByPoint(game,point,fieldState,templateType);
	
	if(tw==null){//нет совпадения игровой ситуации ни с одним templateView в шаблоне
		return false;
	}
	
	try{
		//найдено совпадение игровой ситуации с templateView, далее ищется ход ИИ в дереве
		TreeApplicationInGame ta=new TreeApplicationInGame(game,tw,point.x,point.y);//применение дерева в игровой ситуации
		if(ta.isExistsLevelMove(game,game.gameField.lastDot,false,recommendedMove)){//если ход ИИ есть в дереве
			if(isMoveByAI(protocol,game,ta)){//если ход ИИ возможен на поле
				game.treeApplicationInGame.add(ta);//добавляем в список использованных в игре деревьев
				return true;
			}else return false;
		}else return false;		
	}catch(Exception e){return false;}
}

//если ход ИИ возможен на поле
public boolean isMoveByAI(Protocol protocol,Game game,TreeApplicationInGame ta){
	game.moveAI=ta.transformAIPoint;//запоминаем ход ИИ
	if(protocol.isAICanMakeMove((byte)game.moveAI.x,(byte)game.moveAI.y,game,-1)){//если ход ИИ возможен на поле, также здесь он может быть улучшен
		if(protocol.templateEngine.foundedNumber!=0){
			//запоминаем параметры найденного шаблона для его отображения в редакторе шаблонов
			protocol.templateEngine.foundedNumber=protocol.templateEngine.getBaseIndexByTemplateID(ta.tree.templateID);
			protocol.templateEngine.foundedIndex=ta.templateID;
			protocol.templateEngine.foundedTemplateType=ta.templateType;
		}
		protocol.stat.moveStatMas.addMoveStat(TemplateType.getMoveStatType(ta.templateType));//сохраняем ход в статистику ходов
		return true;				
	}
	return false;
}

//найти значимый размер шаблона (без ANY точек на краях)
private void setSizeWithNotAny(byte[] templateContent){
	if(!TemplateType.isSide(templateType)){//найти значимый размер шаблона не для боковых типов шаблонов
		
		sizeWithNotAny=15;//значимый размер шаблона равен 15x15		
		int notAnyCount=0;
		for(int i=0;i<15;i++)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
		for(int i=210;i<225;i++)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
		for(int i=15;i<210;i+=15)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
		for(int i=29;i<224;i+=15)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
		
		if(notAnyCount==0){
			sizeWithNotAny=13;//значимый размер шаблона равен 13x13
			notAnyCount=0;
			for(int i=16;i<29;i++)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
			for(int i=196;i<209;i++)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
			for(int i=31;i<196;i+=15)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
			for(int i=43;i<208;i+=15)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
			
			if(notAnyCount==0){
				sizeWithNotAny=11;//значимый размер шаблона равен 11x11
				notAnyCount=0;
				for(int i=32;i<43;i++)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
				for(int i=182;i<193;i++)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
				for(int i=47;i<182;i+=15)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
				for(int i=57;i<192;i+=15)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
				
				if(notAnyCount==0){
					sizeWithNotAny=9;//значимый размер шаблона равен 9x9
					notAnyCount=0;
					for(int i=48;i<57;i++)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
					for(int i=168;i<177;i++)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
					for(int i=63;i<168;i+=15)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
					for(int i=71;i<176;i+=15)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
					
					if(notAnyCount==0){
						sizeWithNotAny=7;//значимый размер шаблона равен 7x7
						notAnyCount=0;
						for(int i=64;i<71;i++)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
						for(int i=154;i<161;i++)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
						for(int i=79;i<154;i+=15)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
						for(int i=85;i<160;i+=15)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
						
						if(notAnyCount==0){
							sizeWithNotAny=5;//значимый размер шаблона равен 5x5
							notAnyCount=0;
							for(int i=80;i<85;i++)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
							for(int i=140;i<145;i++)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
							for(int i=95;i<136;i+=15)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
							for(int i=99;i<140;i+=15)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
							if(notAnyCount==0)sizeWithNotAny=3;
						}
					}
				}
			}
		}
	}else{//найти значимый размер шаблона для боковых типов шаблонов, LAND - точка границы поля
		sizeWithNotAny=15;//значимый размер шаблона равен 15x15
		if(templateContent[114]==Protocol.templateDotType_LAND)sizeWithNotAny=5;
		else if(templateContent[115]==Protocol.templateDotType_LAND)sizeWithNotAny=7;
		else if(templateContent[116]==Protocol.templateDotType_LAND)sizeWithNotAny=9;
		else if(templateContent[117]==Protocol.templateDotType_LAND)sizeWithNotAny=11;
		else if(templateContent[118]==Protocol.templateDotType_LAND)sizeWithNotAny=13;
		else if(templateContent[119]==Protocol.templateDotType_LAND)sizeWithNotAny=15;
	}
}

}
