package p_DotsAI;

public enum EnemyType{
	
	RANDOM,HUMAN,AI;//аббревиатура типа соперника, против которого играет ИИ

public String toString() {
	switch (this) {
		case RANDOM:	return "Random";
		case HUMAN:		return "Human";
		case AI:		return "DotsAI";
		default:return "err";
	}
}
	
}
