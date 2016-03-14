//Класс MoveStatType перечисляет типы ходов по статистике

package p_Statistics;

import p_TemplateEngine.TemplateType;

public enum MoveStatType{
	
	//типы статистики
	BEGIN,
	FINAL_RED_ATTACK,
	CONTINUED_RED_ATTACK,
	SQUARE_SIDE,
	SQUARE,
	WALL_DESTROY,
	WALL,
	WALL_SIDE,
	ABSTRACT_DEFENSE_WALL,
	ABSTRACT_ATTACK_WALL,
	BLUE_SURROUND_SECURITY,
	GLOBAL_ATTACK,
	GLOBAL_DESTROY,
	EXPRESS_MAX_TERR_SECURITY,
	GROUND,
	GROUND_SIDE,
	RANDOM,
	ERROR;
	
	//вернуть тип статистики по индексу
	public static MoveStatType getMoveStatType(int index){	
		switch (index) {
			case 0: return BEGIN;
			case 1: return BLUE_SURROUND_SECURITY;			
			case 2:	return SQUARE_SIDE;
			case 3: return SQUARE;
			case 4: return WALL_DESTROY;
			case 5: return FINAL_RED_ATTACK;
			case 6: return ABSTRACT_DEFENSE_WALL;
			case 7: return ABSTRACT_ATTACK_WALL;
			case 8:return WALL_SIDE;
			case 9:return WALL;
			case 10:return GLOBAL_ATTACK;
			case 11:return GLOBAL_DESTROY;
			case 12:return CONTINUED_RED_ATTACK;
			case 13:return GROUND_SIDE;
			case 14:return GROUND;
			case 15:return RANDOM;	
			case 16:return EXPRESS_MAX_TERR_SECURITY;
			default:return ERROR;
		}
	}
	
	//вернуть индекс типа статистики
	public static int getMoveStatIndex(MoveStatType moveStatType) {
		switch (moveStatType){
			case BEGIN: 				return 0;
			case BLUE_SURROUND_SECURITY:return 1;			
			case SQUARE_SIDE:			return 2;			
			case SQUARE:				return 3;			
			case WALL_DESTROY:			return 4;
			case FINAL_RED_ATTACK:		return 5;
			case ABSTRACT_DEFENSE_WALL:	return 6;
			case ABSTRACT_ATTACK_WALL:	return 7;
			case WALL_SIDE:				return 8;
			case WALL:					return 9;
			case GLOBAL_ATTACK:			return 10;
			case GLOBAL_DESTROY:		return 11;
			case CONTINUED_RED_ATTACK:	return 12;
			case GROUND_SIDE:			return 13;
			case GROUND:				return 14;
			case RANDOM:				return 15;
			case EXPRESS_MAX_TERR_SECURITY:return 16;
			default:					return 17;
		}
	}
	
//вернуть название типа хода для статистики
public static String toString(MoveStatType moveStatType) {
	switch (moveStatType) {
		case BEGIN:					return TemplateType.getTemplateTypeName(TemplateType.templateTypeBEGIN)+" ("+TemplateType.toString(TemplateType.templateTypeBEGIN)+")";
		case SQUARE_SIDE:			return TemplateType.getTemplateTypeName(TemplateType.templateTypeSQUARE_SIDE)+" ("+TemplateType.toString(TemplateType.templateTypeSQUARE_SIDE)+")";
		case SQUARE:				return TemplateType.getTemplateTypeName(TemplateType.templateTypeSQUARE)+" ("+TemplateType.toString(TemplateType.templateTypeSQUARE)+")";
		case WALL:					return TemplateType.getTemplateTypeName(TemplateType.templateTypeWALL)+" ("+TemplateType.toString(TemplateType.templateTypeWALL)+")";
		case WALL_SIDE:				return TemplateType.getTemplateTypeName(TemplateType.templateTypeWALL_SIDE)+" ("+TemplateType.toString(TemplateType.templateTypeWALL_SIDE)+")";
		case BLUE_SURROUND_SECURITY:return "защита от окружения синих";
		case GROUND:				return TemplateType.getTemplateTypeName(TemplateType.templateTypeGROUND)+" ("+TemplateType.toString(TemplateType.templateTypeGROUND)+")";
		case GROUND_SIDE:			return TemplateType.getTemplateTypeName(TemplateType.templateTypeGROUND_SIDE)+" ("+TemplateType.toString(TemplateType.templateTypeGROUND_SIDE)+")";
		case RANDOM:				return "рандом";
		case EXPRESS_MAX_TERR_SECURITY:return "захват максимума территории";
		case FINAL_RED_ATTACK:		return TemplateType.getTemplateTypeName(TemplateType.templateTypeFINAL_RED_ATTACK)+" ("+TemplateType.toString(TemplateType.templateTypeFINAL_RED_ATTACK)+")";
		case CONTINUED_RED_ATTACK:	return TemplateType.getTemplateTypeName(TemplateType.templateTypeCONTINUED_RED_ATTACK)+" ("+TemplateType.toString(TemplateType.templateTypeCONTINUED_RED_ATTACK)+")";
		case WALL_DESTROY:			return TemplateType.getTemplateTypeName(TemplateType.templateTypeWALL_DESTROY)+" ("+TemplateType.toString(TemplateType.templateTypeWALL_DESTROY)+")";
		case GLOBAL_ATTACK:			return TemplateType.getTemplateTypeName(TemplateType.templateTypeGLOBAL_ATTACK)+" ("+TemplateType.toString(TemplateType.templateTypeGLOBAL_ATTACK)+")";
		case GLOBAL_DESTROY:		return TemplateType.getTemplateTypeName(TemplateType.templateTypeGLOBAL_DESTROY)+" ("+TemplateType.toString(TemplateType.templateTypeGLOBAL_DESTROY)+")";
		case ABSTRACT_DEFENSE_WALL:	return TemplateType.getTemplateTypeName(TemplateType.templateTypeABSTRACT_DEFENSE_WALL)+" ("+TemplateType.toString(TemplateType.templateTypeABSTRACT_DEFENSE_WALL)+")";
		case ABSTRACT_ATTACK_WALL:	return TemplateType.getTemplateTypeName(TemplateType.templateTypeABSTRACT_ATTACK_WALL)+" ("+TemplateType.toString(TemplateType.templateTypeABSTRACT_ATTACK_WALL)+")";
		default:return "-";
	}
}
	
}
