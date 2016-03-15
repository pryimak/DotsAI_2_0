//Класс TemplateFieldSideType описывает типы границ поля - слева, справа, сверху, снизу, а также без границы - внутри.
//Граница поля важна для шаблонов бокового типа, а также для шаблоново обычноо типа при их применении у края поля.

package p_TemplateEngine;

import java.awt.Point;

import p_DotsAI.Protocol;
import p_DotsAI.Protocol.Game;

public class TemplateFieldSideType{
	
//типы границ поля
public static int templateFieldSideTypeTOP=0;
public static int templateFieldSideTypeBOTTOM=1;
public static int templateFieldSideTypeLEFT=2;
public static int templateFieldSideTypeRIGHT=3;
public static int templateFieldSideTypeINSIDE=4;

//вернуть тип границы поля, возле которого находится анализируемая точка Point p
public static int getFieldSideType(Game game,Point p){	
	if(p.x<Protocol.maxSize-1&p.y<Protocol.maxSize-1){//слева сверху
		if(p.x<p.y)return templateFieldSideTypeLEFT;//слева
		else return templateFieldSideTypeTOP;//сверху
	}
	
	if(p.x<Protocol.maxSize-1&p.y>(game.sizeY-Protocol.maxSize)){//слева снизу
		if(p.x<game.sizeY-p.y)return templateFieldSideTypeLEFT;//слева
		else return templateFieldSideTypeBOTTOM;//снизу
	}
	
	if(p.x>(game.sizeX-Protocol.maxSize)&p.y<Protocol.maxSize-1){//справа сверху
		if(game.sizeX-p.x<p.y)return templateFieldSideTypeRIGHT;//справа
		else return templateFieldSideTypeTOP;//сверху
	}
	
	if(p.x>(game.sizeX-Protocol.maxSize)&p.y>(game.sizeY-Protocol.maxSize)){//справа снизу
		if(game.sizeX-p.x<game.sizeY-p.y)return templateFieldSideTypeRIGHT;//справа
		else return templateFieldSideTypeBOTTOM;//снизу
	}
	
	if(p.x>(game.sizeX-Protocol.maxSize))return templateFieldSideTypeRIGHT;//справа
	if(p.x<Protocol.maxSize-1)return templateFieldSideTypeLEFT;//слева
	if(p.y>(game.sizeY-Protocol.maxSize))return templateFieldSideTypeBOTTOM;//снизу
	if(p.y<Protocol.maxSize-1)return templateFieldSideTypeTOP;//сверху
	return templateFieldSideTypeINSIDE;//внутренняя часть поля, удаленная от границ
}

}
