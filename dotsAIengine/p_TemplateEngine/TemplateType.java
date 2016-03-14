//����� TemplateType ��������� ���� �������� � �� ��������������.
//����� ���� �� ������ ����������� �� �������� ������������� ����, ����� �� �������� ���������� ���� � �.�.
//������ ����� ������ � ����������� ������� ������������� ����� ��������

package p_TemplateEngine;

import java.awt.Point;
import javax.swing.ImageIcon;
import p_DotsAI.Protocol;
import p_DotsAI.Protocol.Game;
import p_JavaPatterns.Pattern_Resources;
import p_Statistics.MoveStatType;

public class TemplateType{
	
	//���� ��������
	public static int templateTypeBEGIN=0;
	public static int templateTypeSQUARE_SIDE=1;
	public static int templateTypeSQUARE=2;
	public static int templateTypeWALL=3;
	public static int templateTypeWALL_SIDE=4;
	public static int templateTypeABSTRACT_DEFENSE_WALL=5;
	public static int templateTypeABSTRACT_ATTACK_WALL=6;	
	public static int templateTypeGLOBAL_ATTACK=7;
	public static int templateTypeGLOBAL_DESTROY=8;
	public static int templateTypeWALL_DESTROY=9;
	public static int templateTypeFINAL_RED_ATTACK=10;
	public static int templateTypeCONTINUED_RED_ATTACK=11;
	public static int templateTypeGROUND_SIDE=12;
	public static int templateTypeGROUND=13;	
	public static int lastIndexOfTemplateType=14;

//������������ ������� ��� �������� � ����
public static String toString(int type) {
	if(type==templateTypeSQUARE_SIDE)return "sst";
	if(type==templateTypeSQUARE)return "sqt";
	if(type==templateTypeWALL)return "wlt";
	if(type==templateTypeWALL_SIDE)return "wst";
	if(type==templateTypeFINAL_RED_ATTACK)return "fra";
	if(type==templateTypeCONTINUED_RED_ATTACK)return "cra";
	if(type==templateTypeGROUND_SIDE)return "grs";
	if(type==templateTypeGROUND)return "grt";		
	if(type==templateTypeWALL_DESTROY)return "wdt";
	if(type==templateTypeBEGIN)return "bgt";
	if(type==templateTypeGLOBAL_ATTACK)return "gat";
	if(type==templateTypeGLOBAL_DESTROY)return "gdt";
	if(type==templateTypeABSTRACT_DEFENSE_WALL)return "adw";
	if(type==templateTypeABSTRACT_ATTACK_WALL)return "aaw";
	return "err";
}

//��� ������� ��� ����������� � ����������� ����������
public static String getTemplateTypeName(int type) {
	if(type==templateTypeSQUARE_SIDE)return "������� � ����";
	if(type==templateTypeSQUARE)return "���������� ������";
	if(type==templateTypeWALL)return "������";
	if(type==templateTypeWALL_SIDE)return "������ � ����";
	if(type==templateTypeWALL_DESTROY)return "������ ���� �����";
	if(type==templateTypeFINAL_RED_ATTACK)return "����� ������� - ����������";
	if(type==templateTypeCONTINUED_RED_ATTACK)return "����� ������� - � ���������";
	if(type==templateTypeGROUND_SIDE)return "���������� � ����";
	if(type==templateTypeGROUND)return "���������� �������";
	if(type==templateTypeBEGIN)return "������ ����";
	if(type==templateTypeGLOBAL_ATTACK)return "���������� �����";
	if(type==templateTypeGLOBAL_DESTROY)return "���������� ������";
	if(type==templateTypeABSTRACT_DEFENSE_WALL)return "����������� �������� �����";
	if(type==templateTypeABSTRACT_ATTACK_WALL)return "����������� ��������� �����";
	return "err";
}

//��� ���� � ���������� ����
public static MoveStatType getMoveStatType(int type){
	if(type==templateTypeSQUARE_SIDE)return MoveStatType.SQUARE_SIDE;
	if(type==templateTypeSQUARE)return MoveStatType.SQUARE;
	if(type==templateTypeWALL_DESTROY)return MoveStatType.WALL_DESTROY;
	if(type==templateTypeFINAL_RED_ATTACK)return MoveStatType.FINAL_RED_ATTACK;
	if(type==templateTypeCONTINUED_RED_ATTACK)return MoveStatType.CONTINUED_RED_ATTACK;
	if(type==templateTypeWALL_SIDE)return MoveStatType.WALL_SIDE;
	if(type==templateTypeWALL)return MoveStatType.WALL;
	if(type==templateTypeGROUND_SIDE)return MoveStatType.GROUND_SIDE;
	if(type==templateTypeGROUND)return MoveStatType.GROUND;
	if(type==templateTypeBEGIN)return MoveStatType.BEGIN;	
	if(type==templateTypeGLOBAL_ATTACK)return MoveStatType.GLOBAL_ATTACK;
	if(type==templateTypeGLOBAL_DESTROY)return MoveStatType.GLOBAL_DESTROY;
	if(type==templateTypeABSTRACT_DEFENSE_WALL)return MoveStatType.ABSTRACT_DEFENSE_WALL;
	if(type==templateTypeABSTRACT_ATTACK_WALL)return MoveStatType.ABSTRACT_ATTACK_WALL;
	return null;
}

//������� - ������ �������
public static ImageIcon getImageIcon(int type) {return new ImageIcon(Pattern_Resources.templateTypes+toString(type)+".png");}

//��� ������� ��� ���������� ����
public static int getTemplateType(MoveStatType type){
	if(type==MoveStatType.SQUARE_SIDE)return templateTypeSQUARE_SIDE;
	if(type==MoveStatType.SQUARE)return templateTypeSQUARE;
	if(type==MoveStatType.WALL_DESTROY)return templateTypeWALL_DESTROY;
	if(type==MoveStatType.FINAL_RED_ATTACK)return templateTypeFINAL_RED_ATTACK;
	if(type==MoveStatType.CONTINUED_RED_ATTACK)return templateTypeCONTINUED_RED_ATTACK;
	if(type==MoveStatType.WALL_SIDE)return templateTypeWALL_SIDE;
	if(type==MoveStatType.WALL)return templateTypeWALL;
	if(type==MoveStatType.GROUND_SIDE)return templateTypeGROUND_SIDE;
	if(type==MoveStatType.GROUND)return templateTypeGROUND;
	if(type==MoveStatType.BEGIN)return templateTypeBEGIN;	
	if(type==MoveStatType.GLOBAL_ATTACK)return templateTypeGLOBAL_ATTACK;
	if(type==MoveStatType.GLOBAL_DESTROY)return templateTypeGLOBAL_DESTROY;
	if(type==MoveStatType.ABSTRACT_DEFENSE_WALL)return templateTypeABSTRACT_DEFENSE_WALL;
	if(type==MoveStatType.ABSTRACT_ATTACK_WALL)return templateTypeABSTRACT_ATTACK_WALL;
	return lastIndexOfTemplateType;
}

//������� (���������� ��� �������� �������� � ���� ����) ��� ��� ��� �������
public static boolean isSide(int type){
	if(type==templateTypeBEGIN)return false;
	if(type==templateTypeSQUARE_SIDE)return true;		
	if(type==templateTypeSQUARE)return false;
	if(type==templateTypeWALL)return false;
	if(type==templateTypeWALL_SIDE)return true;
	if(type==templateTypeWALL_DESTROY)return false;
	if(type==templateTypeFINAL_RED_ATTACK)return false;
	if(type==templateTypeCONTINUED_RED_ATTACK)return false;
	if(type==templateTypeGROUND)return false;
	if(type==templateTypeGROUND_SIDE)return true;
	if(type==templateTypeGLOBAL_ATTACK)return false;
	if(type==templateTypeGLOBAL_DESTROY)return false;
	if(type==lastIndexOfTemplateType)return false;
	if(type==templateTypeABSTRACT_DEFENSE_WALL)return false;
	if(type==templateTypeABSTRACT_ATTACK_WALL)return false;
	return true;
}

//������ �� ���� �������� � ������ (�������� ��������� ������� � ��������� ������� �����) ��� � ����� (�������� ��������� �������)
public static boolean isTemplateBaseSearchFromStartIdx(int type){
	if(type==templateTypeBEGIN)return false;
	if(type==templateTypeSQUARE_SIDE)return false;		
	if(type==templateTypeSQUARE)return false;
	if(type==templateTypeWALL)return false;
	if(type==templateTypeWALL_SIDE)return false;
	if(type==templateTypeWALL_DESTROY)return false;
	if(type==templateTypeFINAL_RED_ATTACK)return true;
	if(type==templateTypeCONTINUED_RED_ATTACK)return false;
	if(type==templateTypeGROUND)return false;
	if(type==templateTypeGROUND_SIDE)return false;
	if(type==templateTypeGLOBAL_ATTACK)return false;
	if(type==templateTypeGLOBAL_DESTROY)return false;
	if(type==lastIndexOfTemplateType)return false;
	if(type==templateTypeABSTRACT_DEFENSE_WALL)return false;
	if(type==templateTypeABSTRACT_ATTACK_WALL)return false;
	return true;
}

//�������� ��� �������
public static int getTemplateType(String str){
	if(str.equals(toString(templateTypeBEGIN)))return templateTypeBEGIN;
	if(str.equals(toString(templateTypeSQUARE_SIDE)))return templateTypeSQUARE_SIDE;
	if(str.equals(toString(templateTypeSQUARE)))return templateTypeSQUARE;
	if(str.equals(toString(templateTypeWALL)))return templateTypeWALL;
	if(str.equals(toString(templateTypeWALL_SIDE)))return templateTypeWALL_SIDE;
	if(str.equals(toString(templateTypeWALL_DESTROY)))return templateTypeWALL_DESTROY;
	if(str.equals(toString(templateTypeFINAL_RED_ATTACK)))return templateTypeFINAL_RED_ATTACK;
	if(str.equals(toString(templateTypeCONTINUED_RED_ATTACK)))return templateTypeCONTINUED_RED_ATTACK;
	if(str.equals(toString(templateTypeGROUND)))return templateTypeGROUND;
	if(str.equals(toString(templateTypeGROUND_SIDE)))return templateTypeGROUND_SIDE;
	if(str.equals(toString(templateTypeGLOBAL_ATTACK)))return templateTypeGLOBAL_ATTACK;
	if(str.equals(toString(templateTypeGLOBAL_DESTROY)))return templateTypeGLOBAL_DESTROY;
	if(str.equals(toString(templateTypeABSTRACT_DEFENSE_WALL)))return templateTypeABSTRACT_DEFENSE_WALL;
	if(str.equals(toString(templateTypeABSTRACT_ATTACK_WALL)))return templateTypeABSTRACT_ATTACK_WALL;
	return lastIndexOfTemplateType;
}

//�������� ���������� �� ������� �������� ��� �������� ������ �������
public static byte[] getContentForTemplateCreation(Game game,Point p,byte[][] fieldState,int type){
	try{
		byte[] content=new byte[Protocol.maxSize*Protocol.maxSize];	
		int idx=0;
		for(int j=0;j<Protocol.maxSize;j++){
			for(int i=0;i<Protocol.maxSize;i++){
				try{
					content[idx]=fieldState[i+p.x-Protocol.maxSize/2][j+p.y-Protocol.maxSize/2];
					idx++;
				}catch(Exception e){//���� ������ ������� �� ���� ����, �� ������� ������� �����
					content[idx]=Protocol.templateDotType_ANY;
					idx++;
				}
			}
		}
		return content;	
	}catch(Exception e){return null;}
}

//���������� ������ �� ������� ��� ������������ ����� (��� ���������� ���� �������)
public static byte getContentSymbolNotSide(Game game,Point p,byte[][] fieldState,int i){
	try{
		return fieldState[i%Protocol.maxSize+p.x-Protocol.maxSize/2][i/Protocol.maxSize+p.y-Protocol.maxSize/2];
	}catch(Exception e){//���� ������ ������� �� ���� ����, �� ������� ������� �����
		return Protocol.templateDotType_ANY;
	}
}

//���������� ������ �� ������� ��� ������������ ����� (��� �������, ������������ ������ �� ������� ����)
public static byte getContentSymbolRight(Game game,Point p,byte[][] fieldState,int i,int tSize){		
	if(i%Protocol.maxSize==Protocol.maxSize-(Protocol.maxSize-tSize)/2-1){
		return Protocol.templateDotType_LAND;//��������������� ������� ���� ���������� ������ ���� LAND
	}
	try{
		return fieldState[game.sizeX-tSize+i%Protocol.maxSize-(Protocol.maxSize-tSize)/2+1][i/Protocol.maxSize+p.y-Protocol.maxSize/2];
	}catch(Exception e){//���� ������ ������� �� ���� ����, �� ������� ������� �����
		return Protocol.templateDotType_ANY;
	}
}

//���������� ������ �� ������� ��� ������������ ����� (��� �������, ������������ ����� �� ������� ����)
public static byte getContentSymbolLeft(Game game,Point p,byte[][] fieldState,int i,int tSize){	
	if(i%Protocol.maxSize==(Protocol.maxSize-tSize)/2){
		return Protocol.templateDotType_LAND;//��������������� ������� ���� ���������� ������ ���� LAND
	}
	try{
		return fieldState[i%Protocol.maxSize-(Protocol.maxSize-tSize)/2-1][i/Protocol.maxSize+p.y-Protocol.maxSize/2];
	}catch(Exception e){//���� ������ ������� �� ���� ����, �� ������� ������� �����
		return Protocol.templateDotType_ANY;
	}
}

//���������� ������ �� ������� ��� ������������ ����� (��� �������, ������������ ����� �� ������� ����)
public static byte getContentSymbolBottom(Game game,Point p,byte[][] fieldState,int i,int tSize){	
	if(i/Protocol.maxSize==Protocol.maxSize-(Protocol.maxSize-tSize)/2-1){
		return Protocol.templateDotType_LAND;//��������������� ������� ���� ���������� ������ ���� LAND
	}
	try{
		return fieldState[i%Protocol.maxSize+p.x-Protocol.maxSize/2][game.sizeY-tSize+i/Protocol.maxSize-(Protocol.maxSize-tSize)/2+1];
	}catch(Exception e){//���� ������ ������� �� ���� ����, �� ������� ������� �����
		return Protocol.templateDotType_ANY;
	}
}

//���������� ������ �� ������� ��� ������������ ����� (��� �������, ������������ ������ �� ������� ����)
public static byte getContentSymbolTop(Game game,Point p,byte[][] fieldState,int i,int tSize){
	if(i/Protocol.maxSize==(Protocol.maxSize-tSize)/2){
		return Protocol.templateDotType_LAND;//��������������� ������� ���� ���������� ������ ���� LAND
	}
	try{
		return fieldState[i%Protocol.maxSize+p.x-Protocol.maxSize/2][i/Protocol.maxSize-(Protocol.maxSize-tSize)/2-1];
	}catch(Exception e){//���� ������ ������� �� ���� ����, �� ������� ������� �����
		return Protocol.templateDotType_ANY;
	}
}
	
}
