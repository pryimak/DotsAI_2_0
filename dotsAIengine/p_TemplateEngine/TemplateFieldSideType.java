//����� TemplateFieldSideType ��������� ���� ������ ���� - �����, ������, ������, �����, � ����� ��� ������� - ������.
//������� ���� ����� ��� �������� �������� ����, � ����� ��� ��������� ������� ���� ��� �� ���������� � ���� ����.

package p_TemplateEngine;

import java.awt.Point;

import p_DotsAI.Protocol;
import p_DotsAI.Protocol.Game;

public class TemplateFieldSideType{
	
//���� ������ ����
public static int templateFieldSideTypeTOP=0;
public static int templateFieldSideTypeBOTTOM=1;
public static int templateFieldSideTypeLEFT=2;
public static int templateFieldSideTypeRIGHT=3;
public static int templateFieldSideTypeINSIDE=4;

//������� ��� ������� ����, ����� �������� ��������� ������������� ����� Point p
public static int getFieldSideType(Game game,Point p){	
	if(p.x<Protocol.maxSize-1&p.y<Protocol.maxSize-1){//����� ������
		if(p.x<p.y)return templateFieldSideTypeLEFT;//�����
		else return templateFieldSideTypeTOP;//������
	}
	
	if(p.x<Protocol.maxSize-1&p.y>(game.sizeY-Protocol.maxSize)){//����� �����
		if(p.x<game.sizeY-p.y)return templateFieldSideTypeLEFT;//�����
		else return templateFieldSideTypeBOTTOM;//�����
	}
	
	if(p.x>(game.sizeX-Protocol.maxSize)&p.y<Protocol.maxSize-1){//������ ������
		if(game.sizeX-p.x<p.y)return templateFieldSideTypeRIGHT;//������
		else return templateFieldSideTypeTOP;//������
	}
	
	if(p.x>(game.sizeX-Protocol.maxSize)&p.y>(game.sizeY-Protocol.maxSize)){//������ �����
		if(game.sizeX-p.x<game.sizeY-p.y)return templateFieldSideTypeRIGHT;//������
		else return templateFieldSideTypeBOTTOM;//�����
	}
	
	if(p.x>(game.sizeX-Protocol.maxSize))return templateFieldSideTypeRIGHT;//������
	if(p.x<Protocol.maxSize-1)return templateFieldSideTypeLEFT;//�����
	if(p.y>(game.sizeY-Protocol.maxSize))return templateFieldSideTypeBOTTOM;//�����
	if(p.y<Protocol.maxSize-1)return templateFieldSideTypeTOP;//������
	return templateFieldSideTypeINSIDE;//���������� ����� ����, ��������� �� ������
}

}
