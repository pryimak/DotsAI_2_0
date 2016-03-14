//����� Template ��������� ������� ������� ��������. ��� ������� ����� ������ 15�15 �����.
//������ ������������ � ������� ��������� �� ����. ��� �� ���������� ������ ��� �� � ������,
//������� ��������� � ������� �������.
//�.�. ������ ����� ���������� ������� ���������, �� ��� ������� ��������� ���� ��������� ����� TemplateView
//� �� ����� ���� ������� �������� �� ���� ������������ �� ����� ����������� �������.
//� ������� ���� ������� �����, ������������ ANY. ����� ����� ����������, ��� �������, ����� ����� ������������ ������� ����� �� ������� ����.
//�� ������� ������ ����� �� ��������� ��������� ������� � ������� ���������.
//������ ������� ����� ��������� � ����� �������, ������� � ������� ���� �������� ����������� �����,
//�� ������� ����� ��������� ���������. � ����� �� ����� ��������� �� ����������.
//���� ��������� ��������� �� ����� �������, � �� ������ �� �������� �����, �� �������� ��������� �������� � 5-15 ���.

package p_TemplateEngine;

import java.awt.Point;
import java.util.ArrayList;

import p_DotsAI.Protocol;
import p_DotsAI.Protocol.Game;
import p_TreeEngine.Tree;
import p_TreeEngine.TreeApplicationInGame;

public class Template{
	
	public int templateID;//���������� ������ �������
	public int templateType;//��� �������
	public byte[] templateContent;//���������� ������� - ������� ��������
	ArrayList<TemplateView> templateView;//����������� �������� � ������ ��� ���������� 0,90,180,270,Vert,Gor,Vert90,ViewGor90
	public Tree tree;//������ �����, ������������� �������
	public int ANYDotsCount;//��� ���������� ���� �������� ����� �� �����������	
	public int sizeWithNotAny=Protocol.maxSize;//������ �������� ����� ������� (��� ������� "ANY" �����)
	
public Template(String strTemplate){
	ANYDotsCount=0;
	tree=null;	
	changeTemplate(strTemplate);//������ �������� �� ������ � ���� ������
}

//��������� ����� ��������� ������ ������� ��� ������������ ������ ��� ������� �������������� �������
public void changeTemplate(String strTemplate){
	
	//����� �� ������ �������� ������, ���, ���������� ������� � ��� ������
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
	
	templateView=new ArrayList<TemplateView>();//������� ������ ����������� ��������
	
	templateView.add(new TemplateView(this,TemplateRotationType.templateRotationTypeR0));//����������� ������� ��� ���������, ��� �� �������� � ����
	
	//����� ��������� ����������� �������� � ������, �� ��� ���������� ����������� � ��� ��������� � ������, ��� �� �����������
	byte[] strTemplate180=TemplateRotationType.getTransformArray(TemplateRotationType.templateRotationTypeR180,templateContent);
	if(isEqualsLikeTemplateView(strTemplate180,templateContent)){//����� �� ��������� ���������� TemplateView	
	}else{
		templateView.add(new TemplateView(this,TemplateRotationType.templateRotationTypeR180));//�������� �� 180 ��������
	}
	
	byte[] strTemplateGorizontal=TemplateRotationType.getTransformArray(TemplateRotationType.templateRotationTypeGORIZONTAL,templateContent);
	if(isEqualsLikeTemplateView(strTemplateGorizontal,templateContent)//����� �� ��������� ���������� TemplateView
			||isEqualsLikeTemplateView(strTemplateGorizontal,strTemplate180)){
	}else{
		templateView.add(new TemplateView(this,TemplateRotationType.templateRotationTypeGORIZONTAL));//�������� �� �����������
	}
	
	byte[] strTemplateVertical=TemplateRotationType.getTransformArray(TemplateRotationType.templateRotationTypeVERTICAL,templateContent);
	if(isEqualsLikeTemplateView(strTemplateVertical,templateContent)//����� �� ��������� ���������� TemplateView
			||isEqualsLikeTemplateView(strTemplateVertical,strTemplate180)
			||isEqualsLikeTemplateView(strTemplateVertical,strTemplateGorizontal)){
	}else{
		templateView.add(new TemplateView(this,TemplateRotationType.templateRotationTypeVERTICAL));//�������� �� ���������
	}
	
	byte[] strTemplate90=TemplateRotationType.getTransformArray(TemplateRotationType.templateRotationTypeR90,templateContent);
	if(isEqualsLikeTemplateView(strTemplate90,templateContent)//����� �� ��������� ���������� TemplateView
			||isEqualsLikeTemplateView(strTemplate90,strTemplate180)
			||isEqualsLikeTemplateView(strTemplate90,strTemplateGorizontal)
			||isEqualsLikeTemplateView(strTemplate90,strTemplateVertical)){
	}else{
		templateView.add(new TemplateView(this,TemplateRotationType.templateRotationTypeR90));//�������� �� 90 ��������
	}

	byte[] strTemplate270=TemplateRotationType.getTransformArray(TemplateRotationType.templateRotationTypeR270,templateContent);
	if(isEqualsLikeTemplateView(strTemplate270,templateContent)//����� �� ��������� ���������� TemplateView
			||isEqualsLikeTemplateView(strTemplate270,strTemplate180)
			||isEqualsLikeTemplateView(strTemplate270,strTemplateGorizontal)
			||isEqualsLikeTemplateView(strTemplate270,strTemplateVertical)
			||isEqualsLikeTemplateView(strTemplate270,strTemplate90)){
		
	}else{
		templateView.add(new TemplateView(this,TemplateRotationType.templateRotationTypeR270));//�������� �� 270 ��������
	}
	
	byte[] strTemplateGorizontal90=TemplateRotationType.getTransformArray(TemplateRotationType.templateRotationTypeGORIZONTAL90,templateContent);
	if(isEqualsLikeTemplateView(strTemplateGorizontal90,templateContent)//����� �� ��������� ���������� TemplateView
			||isEqualsLikeTemplateView(strTemplateGorizontal90,strTemplate180)
			||isEqualsLikeTemplateView(strTemplateGorizontal90,strTemplateGorizontal)
			||isEqualsLikeTemplateView(strTemplateGorizontal90,strTemplateVertical)
			||isEqualsLikeTemplateView(strTemplateGorizontal90,strTemplate90)
			||isEqualsLikeTemplateView(strTemplateGorizontal90,strTemplate270)){
	}else{
		templateView.add(new TemplateView(this,TemplateRotationType.templateRotationTypeGORIZONTAL90));//�������� �� 90 �������� � �� �����������
	}
	
	byte[] strTemplateVertical90=TemplateRotationType.getTransformArray(TemplateRotationType.templateRotationTypeVERTICAL90,templateContent);
	if(isEqualsLikeTemplateView(strTemplateVertical90,templateContent)//����� �� ��������� ���������� TemplateView
			||isEqualsLikeTemplateView(strTemplateVertical90,strTemplate180)
			||isEqualsLikeTemplateView(strTemplateVertical90,strTemplateGorizontal)
			||isEqualsLikeTemplateView(strTemplateVertical90,strTemplateVertical)
			||isEqualsLikeTemplateView(strTemplateVertical90,strTemplate90)
			||isEqualsLikeTemplateView(strTemplateVertical90,strTemplate270)
			||isEqualsLikeTemplateView(strTemplateVertical90,strTemplateGorizontal90)){
	}else{
		templateView.add(new TemplateView(this,TemplateRotationType.templateRotationTypeVERTICAL90));//�������� �� 90 �������� � �� ���������
	}
	
	setSizeWithNotAny(templateContent);//����� �������� ������ ������� (��� ANY ����� �� �����)
}

//�������� ��� TemplateView �� �� ��������
boolean isEqualsLikeTemplateView(byte[] arr1,byte[] arr2){
	for(int i=0;i<arr1.length;i++){
		if(arr1[i]!=arr2[i]){
			return false;
		}
	}
	return true;
}

//��� ���������� ���� �������� ����� �� ����������� ����� ����� ANY �����
public void setANYDotsCount(){
	ANYDotsCount=0;
	for(int i=0;i<templateContent.length;i++){
		if(templateContent[i]==Protocol.templateDotType_ANY){
			ANYDotsCount++;
		}
	}
}

//������������ ��� ������ ������� ��� ���� �� ������������ ����� ����� Point point
public TemplateView getTemplateViewIfEqualsLikeAreaByPoint(Game game,Point point,byte[][] fieldState,int templateType){
	boolean isSide=TemplateType.isSide(templateType);//���� ��� ������� - ������� (��� ���� ����)
	int fieldSideType=TemplateFieldSideType.getFieldSideType(game, point);//��� ������� ������� - ����, �����, ����, ���
	for(int i=0;i<templateView.size();i++){//�������� ������ TemplateView, ����������� � �������
		if(templateView.get(i).isEquals(game,point,fieldState,isSide,fieldSideType)){
			return templateView.get(i);//������� ���������� ������� �������� � templateView 
		}
	}	
	return null;
}

//������������ ��� ������ ������� ��� ���� �� ������������ ������ ����� ArrayList<Point> pointList
public boolean isMoveIfEqualsLikeAreaByPointList(Protocol protocol,Game game,ArrayList<Point> pointList,byte[][] fieldState,int templateType,Point recommendedMove){	
	for(int j=0;j<pointList.size();j++){
		
		//�� ������ TemplateView, ����������� � �������, ���� ����������� � ������� ��������� �� ����
		TemplateView tw=getTemplateViewIfEqualsLikeAreaByPoint(game,pointList.get(j),fieldState,templateType);
		
		if(tw==null){//��� ���������� ������� �������� �� � ����� templateView � �������
			continue;
		}
		
		try{
			//������� ���������� ������� �������� � templateView, ����� ������ ��� �� � ������
			TreeApplicationInGame ta=new TreeApplicationInGame(game,tw,pointList.get(j).x,pointList.get(j).y);//���������� ������ � ������� ��������
			if(ta.isExistsLevelMove(game,game.gameField.lastDot,false,recommendedMove)){//���� ��� �� ���� � ������
				if(isMoveByAI(protocol,game,ta)){//���� ��� �� �������� �� ����
					game.treeApplicationInGame.add(ta);//��������� � ������ �������������� � ���� ��������
					return true;
				}else continue;				
			}else continue;
		}catch(Exception e){continue;}
	}
	return false;
}

//������������ ��� ������ ������� ��� ���� �� ������������ ����� ����� Point point
public boolean isMoveIfEqualsLikeAreaByPoint(Protocol protocol,Game game,Point point,byte[][] fieldState,int templateType,Point recommendedMove){
	
	//�� ������ TemplateView, ����������� � �������, ���� ����������� � ������� ��������� �� ����
	TemplateView tw=getTemplateViewIfEqualsLikeAreaByPoint(game,point,fieldState,templateType);
	
	if(tw==null){//��� ���������� ������� �������� �� � ����� templateView � �������
		return false;
	}
	
	try{
		//������� ���������� ������� �������� � templateView, ����� ������ ��� �� � ������
		TreeApplicationInGame ta=new TreeApplicationInGame(game,tw,point.x,point.y);//���������� ������ � ������� ��������
		if(ta.isExistsLevelMove(game,game.gameField.lastDot,false,recommendedMove)){//���� ��� �� ���� � ������
			if(isMoveByAI(protocol,game,ta)){//���� ��� �� �������� �� ����
				game.treeApplicationInGame.add(ta);//��������� � ������ �������������� � ���� ��������
				return true;
			}else return false;
		}else return false;		
	}catch(Exception e){return false;}
}

//���� ��� �� �������� �� ����
public boolean isMoveByAI(Protocol protocol,Game game,TreeApplicationInGame ta){
	game.moveAI=ta.transformAIPoint;//���������� ��� ��
	if(protocol.isAICanMakeMove((byte)game.moveAI.x,(byte)game.moveAI.y,game,-1)){//���� ��� �� �������� �� ����, ����� ����� �� ����� ���� �������
		if(protocol.templateEngine.foundedNumber!=0){
			//���������� ��������� ���������� ������� ��� ��� ����������� � ��������� ��������
			protocol.templateEngine.foundedNumber=protocol.templateEngine.getBaseIndexByTemplateID(ta.tree.templateID);
			protocol.templateEngine.foundedIndex=ta.templateID;
			protocol.templateEngine.foundedTemplateType=ta.templateType;
		}
		protocol.stat.moveStatMas.addMoveStat(TemplateType.getMoveStatType(ta.templateType));//��������� ��� � ���������� �����
		return true;				
	}
	return false;
}

//����� �������� ������ ������� (��� ANY ����� �� �����)
private void setSizeWithNotAny(byte[] templateContent){
	if(!TemplateType.isSide(templateType)){//����� �������� ������ ������� �� ��� ������� ����� ��������
		
		sizeWithNotAny=15;//�������� ������ ������� ����� 15x15		
		int notAnyCount=0;
		for(int i=0;i<15;i++)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
		for(int i=210;i<225;i++)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
		for(int i=15;i<210;i+=15)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
		for(int i=29;i<224;i+=15)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
		
		if(notAnyCount==0){
			sizeWithNotAny=13;//�������� ������ ������� ����� 13x13
			notAnyCount=0;
			for(int i=16;i<29;i++)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
			for(int i=196;i<209;i++)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
			for(int i=31;i<196;i+=15)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
			for(int i=43;i<208;i+=15)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
			
			if(notAnyCount==0){
				sizeWithNotAny=11;//�������� ������ ������� ����� 11x11
				notAnyCount=0;
				for(int i=32;i<43;i++)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
				for(int i=182;i<193;i++)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
				for(int i=47;i<182;i+=15)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
				for(int i=57;i<192;i+=15)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
				
				if(notAnyCount==0){
					sizeWithNotAny=9;//�������� ������ ������� ����� 9x9
					notAnyCount=0;
					for(int i=48;i<57;i++)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
					for(int i=168;i<177;i++)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
					for(int i=63;i<168;i+=15)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
					for(int i=71;i<176;i+=15)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
					
					if(notAnyCount==0){
						sizeWithNotAny=7;//�������� ������ ������� ����� 7x7
						notAnyCount=0;
						for(int i=64;i<71;i++)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
						for(int i=154;i<161;i++)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
						for(int i=79;i<154;i+=15)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
						for(int i=85;i<160;i+=15)if(templateContent[i]!=Protocol.templateDotType_ANY)notAnyCount++;
						
						if(notAnyCount==0){
							sizeWithNotAny=5;//�������� ������ ������� ����� 5x5
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
	}else{//����� �������� ������ ������� ��� ������� ����� ��������, LAND - ����� ������� ����
		sizeWithNotAny=15;//�������� ������ ������� ����� 15x15
		if(templateContent[114]==Protocol.templateDotType_LAND)sizeWithNotAny=5;
		else if(templateContent[115]==Protocol.templateDotType_LAND)sizeWithNotAny=7;
		else if(templateContent[116]==Protocol.templateDotType_LAND)sizeWithNotAny=9;
		else if(templateContent[117]==Protocol.templateDotType_LAND)sizeWithNotAny=11;
		else if(templateContent[118]==Protocol.templateDotType_LAND)sizeWithNotAny=13;
		else if(templateContent[119]==Protocol.templateDotType_LAND)sizeWithNotAny=15;
	}
}

}
