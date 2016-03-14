//Класс MoveStatCount хранит массив с числом ходов по каждому типу статистики

package p_Statistics;

public class MoveStatCount {

	public int moveStatistics[];
	public int moveStatMax;
	public String lastAImoveString="";
	public MoveStatType lastAImoveType;
	
	public void addMoveStat(MoveStatType moveStatType){	
		moveStatistics[MoveStatType.getMoveStatIndex(moveStatType)]++;
		
		lastAImoveString=MoveStatType.toString(moveStatType);
		lastAImoveType=moveStatType;
	}

	public int getMoveStatLength(){
		if(moveStatistics==null){
			moveStatistics=new int[MoveStatType.getMoveStatIndex(MoveStatType.ERROR)];
		}
		return moveStatistics.length;
	}

	public int getMoveStatMax(){	
		moveStatMax=0;
		for(int i=0;i<moveStatistics.length;i++){
			if(moveStatistics[i]>moveStatMax)moveStatMax=moveStatistics[i];
		}
		return moveStatMax+1;
	}

	public void clearMoveStat(){	
		for(int i=0;i<moveStatistics.length;i++){
			moveStatistics[i]=0;
		}
		lastAImoveString="";
	}
}
