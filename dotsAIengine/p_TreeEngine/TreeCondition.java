//Класс TreeCondition описывает условия для узлов дерева ходов.
//Обычно узлы деревьев заданы без условий, для них этот класс не используется и ставится null.
//При наличии условия создается объект класса.
//Условия можно добавить в дерево только для ходов ИИ.

//Условия выглядят так: сделать данный ход, если определенная последовательность ходов синих или красных
//создает новое синее или красное окружение соответственно. 
//Т.е. условия используются для упрощенного создания собственных окружений и защиты от окружений соперника.

//В базе шаблонов условие кодируется как "move:1,-1;ifmoves:0,-1;1,-1;enclosure:b", где
//move - ход ИИ, который необходимо выполнить при соблюдении условия,
//ifmoves - список ходов, по которым нужно выполнить проверку на возможность создания нового окружения,
//enclosure - тип окружения, для которого нужно выполнить проверку на его создание.

//На перспективу можно сделать более гибкую логику условий.
//Если сейчас делается проверка насоздание нового окружения, то можно сделать проверку на его несоздание.
//Также для проверки используется только список условных ходов, а можно было бы использовать информацию о том,
//колько разрывов есть в цепях, которые не охвачены шаблоном, а находятся за его пределами 
//(для более точного определения числа точек, оставшихся до выполнения окружения).

package p_TreeEngine;

import java.awt.Point;
import java.util.ArrayList;

public class TreeCondition {
	
	public Point move;//ход ИИ
	public ArrayList<Point> ifmoves;//ходы для выполнения условия
	public String enclosureType;//тип создаваемого окружения для выполнения условия
	
	//создать объект путем парсинга строки
	public TreeCondition(String str){		

		//тип создаваемого окружения
		int i=str.indexOf("enclosure:");
		if(i!=-1)enclosureType=str.substring(i+10,i+11);
		else{
			enclosureType="";
			System.out.println("тип создаваемого окружения не задан - это ошибка, он должен быть обязательно, str="+str);
		}
		
		//ход при соблюдении условия
		String s1=str.substring(str.indexOf("move:")+5);
		s1=s1.substring(0,s1.indexOf(";"));
		move=new Point(new Integer(s1.substring(0, s1.indexOf(","))), new Integer(s1.substring(s1.indexOf(",")+1)));
		
		//условия применения (список ходов) хода
		ifmoves=new ArrayList<Point>();		
		try{
			String str1=str.substring(str.indexOf("ifmoves:")+8);
			for(;;){
				int idx=str1.indexOf(";");
				String s="";
				if(idx==-1)break;
				else s=str1.substring(0, idx);
				
				int x=new Integer(s.substring(0, str1.indexOf(",")));
				int y=new Integer(s.substring(str1.indexOf(",")+1));
				ifmoves.add(new Point(x, y));
				if(Math.abs(x)>7||Math.abs(y)>7)System.out.println("ошибочно задано условие хода "+x+","+y);
				
				str1=str1.replaceFirst(s+";", "");
			}
		}catch(Exception e){System.out.println("условных ходов нет - это ошибка, они должны быть обязательно, str="+str);}
	}
	
	//получить условие в виде строки для его сохранения в базу
	String getString(){
		String strIfmoves="ifmoves:";
		for(int i=0;i<ifmoves.size();i++){
			strIfmoves+=ifmoves.get(i).x+","+ifmoves.get(i).y+";";
		}		
		return "move:"+move.x+","+move.y+";"+strIfmoves+"enclosure:"+enclosureType;
	}
	
	//получить список условных ходов для отображения в графическом интерфейсе дерева
	String getStringOfIfMoves(){
		String strIfmoves="";
		for(int i=0;i<ifmoves.size();i++){
			strIfmoves+=ifmoves.get(i).x+","+ifmoves.get(i).y+"; ";
		}		
		return strIfmoves;
	}
}
