//Класс TreeSymmetryType описывает типы симметрии деревьев для улучшения поиска ходов в дереве.
//Благодаря симметрии дерево можно разворачивать и таким образом найти в нем ход, если без разворота ход не находится.
//Здесь есть подобие повороту шаблонов.
//Однако вопрос не до конца изучен, особенно для диагональных симметрий.
//Также иногда встречаются баги при использовании симметрий.

//Симметрия используется не для всех деревьев. Для большинства деревьев она не задана.
//Симметрию дерева нужно задавать вручную, при этом расчитывая вручную линию, относительно которой симметрия существует.

package p_TreeEngine;

public class TreeSymmetryType {
	
	//типы симметррий дерева
	public static int treeSymmetryTypeGORIZONTAL=0;
	public static int treeSymmetryTypeVERTICAL=1;
	public static int treeSymmetryTypeMAIN_DIAGONAL=2;
	public static int treeSymmetryTypeSECOND_DIAGONAL=3;	
	
	//получить код типа симметрии для базы шаблонов
	public static String toStringMakrosSymmetryType(int symmetryType){
		if(symmetryType==TreeSymmetryType.treeSymmetryTypeGORIZONTAL)return "gor";
		if(symmetryType==TreeSymmetryType.treeSymmetryTypeVERTICAL)return "vert";
		if(symmetryType==TreeSymmetryType.treeSymmetryTypeMAIN_DIAGONAL)return "mdiag";
		if(symmetryType==TreeSymmetryType.treeSymmetryTypeSECOND_DIAGONAL)return "sdiag";
		return "error";
	}

	//получить описание типа симметрии
	public static String getTreeSymmetryTypeDescription(int symmetryType){
		if(symmetryType==TreeSymmetryType.treeSymmetryTypeGORIZONTAL)return "горизонталь";
		if(symmetryType==TreeSymmetryType.treeSymmetryTypeVERTICAL)return "вертикаль";
		if(symmetryType==TreeSymmetryType.treeSymmetryTypeMAIN_DIAGONAL)return "главная диагональ";
		if(symmetryType==TreeSymmetryType.treeSymmetryTypeSECOND_DIAGONAL)return "побочная диагональ";
		return "error";
	}
	
}
