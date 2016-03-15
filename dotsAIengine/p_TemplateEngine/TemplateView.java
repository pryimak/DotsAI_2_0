//Класс TemplateView хранит отображения шаблона, т.к. каждый шаблон можно развернуть 8 способами.
//Отображения описаны и хранятся в классе Template. Если отображение для данного шаблона совпадает
//с добавленным ранее отображением, то новое отображение не добавляется (проверка этого происходит классе Template)

package p_TemplateEngine;

import java.awt.Point;
import p_DotsAI.Protocol;
import p_DotsAI.Protocol.Game;

public class TemplateView {
	
public byte[] templateContentArray;//сохраненное отображение шаблона 15х15
public int templateRotationType;//тип разворота шаблона
public Template template;//шаблон, которому принадлежит данное отображение
	
//создаем объект отображения
public TemplateView(Template template,int templateRotationType){
	this.template=template;
	this.templateRotationType=templateRotationType;
		
	//развернуть шаблон и получить его отображение
	templateContentArray=TemplateRotationType.getTransformArray(templateRotationType,template.templateContent);
}
	
//сравнить отображение шаблона с игровой ситуацией на поле, 
//в случае совпадения из дерева данного шаблона будет искаться ход (этот поиск выполняется в другом методе, а не в этом)
public boolean isEquals(Game game,Point point,byte[][] fieldState,boolean isSide,int fieldSideType){
	try{		
		if(!isSide){//шаблон не для края поля
			//закомментированная строка может заменить все строки внутри данного if, но это уменьшить скорость поиска
			//for(int i=0;i<Template.size*Template.size;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			
			//поиск для размера значимой части шаблона=3
			for(int i=6;i<9;i++){
				for(int j=6;j<9;j++){
					int idx=i*15+j;
					if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,idx),templateContentArray[idx])){return false;}
				}
			}
			if(template.sizeWithNotAny==3)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=5
			for(int i=80;i<85;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			for(int i=140;i<145;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			for(int i=95;i<136;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			for(int i=99;i<140;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==5)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=7
			for(int i=64;i<71;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			for(int i=154;i<161;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			for(int i=79;i<154;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			for(int i=85;i<160;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==7)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=9
			for(int i=48;i<57;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			for(int i=168;i<177;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			for(int i=63;i<168;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			for(int i=71;i<176;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==9)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=11
			for(int i=32;i<43;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			for(int i=182;i<193;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			for(int i=47;i<182;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			for(int i=57;i<192;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==11)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=13
			for(int i=16;i<29;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			for(int i=196;i<209;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			for(int i=31;i<196;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			for(int i=43;i<208;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==13)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=15
			for(int i=0;i<15;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			for(int i=210;i<225;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			for(int i=15;i<210;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			for(int i=29;i<224;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolNotSide(game,point,fieldState,i),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==15)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
		}else if(fieldSideType==TemplateFieldSideType.templateFieldSideTypeLEFT){
			//закомментированная строка может заменить все строки внутри данного if, но это уменьшить скорость поиска
			//for(int i=0;i<Template.size*Template.size;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i]))return false;

			//поиск для размера значимой части шаблона=3
			for(int i=6;i<9;i++){
				for(int j=6;j<9;j++){
					int idx=i*15+j;
					if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,idx,template.sizeWithNotAny),templateContentArray[idx])){return false;}
				}
			}
			if(template.sizeWithNotAny==3)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=5
			for(int i=80;i<85;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=140;i<145;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=95;i<136;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=99;i<140;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==5)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=7
			for(int i=64;i<71;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=154;i<161;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=79;i<154;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=85;i<160;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==7)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=9
			for(int i=48;i<57;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=168;i<177;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=63;i<168;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=71;i<176;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==9)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=11
			for(int i=32;i<43;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=182;i<193;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=47;i<182;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=57;i<192;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==11)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=13
			for(int i=16;i<29;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=196;i<209;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=31;i<196;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=43;i<208;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==13)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=15
			for(int i=0;i<15;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=210;i<225;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=15;i<210;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=29;i<224;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolLeft(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==15)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
		}else if(fieldSideType==TemplateFieldSideType.templateFieldSideTypeRIGHT){
			//закомментированная строка может заменить все строки внутри данного if, но это уменьшить скорость поиска
			//for(int i=0;i<Template.size*Template.size;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i]))return false;
			
			//поиск для размера значимой части шаблона=3
			for(int i=6;i<9;i++){
				for(int j=6;j<9;j++){
					int idx=i*15+j;
					if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,idx,template.sizeWithNotAny),templateContentArray[idx])){return false;}
				}
			}
			if(template.sizeWithNotAny==3)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=5
			for(int i=80;i<85;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=140;i<145;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=95;i<136;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=99;i<140;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==5)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=7
			for(int i=64;i<71;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=154;i<161;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=79;i<154;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=85;i<160;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==7)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=9
			for(int i=48;i<57;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=168;i<177;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=63;i<168;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=71;i<176;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==9)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=11
			for(int i=32;i<43;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=182;i<193;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=47;i<182;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=57;i<192;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==11)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=13
			for(int i=16;i<29;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=196;i<209;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=31;i<196;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=43;i<208;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==13)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=15
			for(int i=0;i<15;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=210;i<225;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=15;i<210;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=29;i<224;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolRight(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==15)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
		}else if(fieldSideType==TemplateFieldSideType.templateFieldSideTypeTOP){
			//закомментированная строка может заменить все строки внутри данного if, но это уменьшить скорость поиска
			//for(int i=0;i<Template.size*Template.size;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i]))return false;

			//поиск для размера значимой части шаблона=3
			for(int i=6;i<9;i++){
				for(int j=6;j<9;j++){
					int idx=i*15+j;
					if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,idx,template.sizeWithNotAny),templateContentArray[idx])){return false;}
				}
			}
			if(template.sizeWithNotAny==3)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=5
			for(int i=80;i<85;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=140;i<145;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=95;i<136;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=99;i<140;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==5)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=7
			for(int i=64;i<71;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=154;i<161;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=79;i<154;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=85;i<160;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==7)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=9
			for(int i=48;i<57;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=168;i<177;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=63;i<168;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=71;i<176;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==9)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=11
			for(int i=32;i<43;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=182;i<193;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=47;i<182;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=57;i<192;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==11)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=13
			for(int i=16;i<29;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=196;i<209;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=31;i<196;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=43;i<208;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==13)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=15
			for(int i=0;i<15;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=210;i<225;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=15;i<210;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=29;i<224;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolTop(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==15)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
		}else if(fieldSideType==TemplateFieldSideType.templateFieldSideTypeBOTTOM){
			//закомментированная строка может заменить все строки внутри данного if, но это уменьшить скорость поиска
			//for(int i=0;i<Template.size*Template.size;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i]))return false;

			//поиск для размера значимой части шаблона=3
			for(int i=6;i<9;i++){
				for(int j=6;j<9;j++){
					int idx=i*15+j;
					if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,idx,template.sizeWithNotAny),templateContentArray[idx])){return false;}
				}
			}
			if(template.sizeWithNotAny==3)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=5
			for(int i=80;i<85;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=140;i<145;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=95;i<136;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=99;i<140;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==5)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=7
			for(int i=64;i<71;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=154;i<161;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=79;i<154;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=85;i<160;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==7)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=9
			for(int i=48;i<57;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=168;i<177;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=63;i<168;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=71;i<176;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==9)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=11
			for(int i=32;i<43;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=182;i<193;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=47;i<182;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=57;i<192;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==11)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=13
			for(int i=16;i<29;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=196;i<209;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=31;i<196;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=43;i<208;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==13)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
			//поиск для размера значимой части шаблона=15
			for(int i=0;i<15;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=210;i<225;i++)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=15;i<210;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			for(int i=29;i<224;i+=15)if(!isEqualsBySymbol(TemplateType.getContentSymbolBottom(game,point,fieldState,i,template.sizeWithNotAny),templateContentArray[i])){return false;}
			if(template.sizeWithNotAny==15)return true;//совпадение отображения шаблона с игровой ситуацией найдено, поиск окончен
			
		}
		return false;
	}catch(Exception e){return false;}
}
	
//сравнить соответствующие точку отображение шаблона с точкой игровой ситуацией на поле,
//в случае совпадения вернуть true
private boolean isEqualsBySymbol(byte field,byte template){
	try{
		if(field==template){//полное совпадение
			return true;
		}else if(template==Protocol.templateDotType_ANY){//зеленая точка ANY
			return true;
		}else if(field==Protocol.templateDotType_RED){//красная точка
			if(template==Protocol.templateDotType_RED_or_EMPTY)return true;//красная или пустая RED_or_EMPTY
			else return false;
		}else if(field==Protocol.templateDotType_BLUE){//синяя точка
			if(template==Protocol.templateDotType_BLUE_or_EMPTY)return true;//синяя или пустая BLUE_or_EMPTY
			else return false;
		}else if(field==Protocol.templateDotType_EMPTY){//пустая точка
			if(template==Protocol.templateDotType_RED_or_EMPTY||template==Protocol.templateDotType_BLUE_or_EMPTY)return true;
			else return false;
		}
		return false;
	}catch(Exception e){return false;}
}
	
}
