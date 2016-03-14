//Класс TemplateEngine управляет базой игровых ситуаций (шаблонов и деревьев).
//Здесь выполняется инициализация, сохранение, обновление базы, удаление шаблонов и поиск шаблона для хода ИИ.

package p_TemplateEngine;

import java.awt.Point;
import java.util.ArrayList;

import p_DotsAI.Protocol;
import p_DotsAI.Protocol.Game;
import p_JavaPatterns.Pattern_ReadAndWriteFile;

public class TemplateEngine{
	
public ArrayList<Template> baseOfTemplates;//база игровых ситуаций (шаблонов и деревьев)
public int maxTemplateIndex;//максимальный индекс шаблона в базе
private Pattern_ReadAndWriteFile file;//класс чтения и записи файлов
private String templateFileName;//имя файла базы игровых ситуаций
public int[] templateTypeCount;//массив с числом шаблонов каждого типа

//Параметры найденного шаблона для его отображения в редакторе шаблонов
public int foundedNumber;//порядковый номер шаблона в базе
public int foundedIndex;//индекс шаблона
public int foundedTemplateType;//тип шаблона

//Конструктор класса загружает и обрабатывает базу игровых ситуаций из файла
public TemplateEngine(){
	
	//инициализация переменных
	templateFileName="dotsAIresources//templateBase.txt";	
	file=new Pattern_ReadAndWriteFile();
	maxTemplateIndex=0;		
	templateTypeCount=new int[TemplateType.lastIndexOfTemplateType];
	int templateCount=0;
	int treeCount=0;
	for(int i=0;i<templateTypeCount.length;i++){
		templateTypeCount[i]=0;
	}
	
	String str=file.ReadTxtFile(templateFileName);//прочитать файл базы
	baseOfTemplates=new ArrayList<Template>();//создать список для хранения базы	
	
	while(str.length()>0){//парсить весь файл, в каждой итерации while обрабатывается один шаблон
		int idx=str.indexOf("</"+TML.template+">")+TML.template.length()+3;//получить последний символ, описывающий один шаблон
		String strTemplate=str.substring(0,idx);//прочесть текст шаблона
		Template t=new Template(strTemplate);//создать объект шаблона	
		baseOfTemplates.add(t);//добавить шаблон в список шаблонов
		
		//обновить число шаблонов, деревьев и число шаблонов каждого типа
		templateCount++;
		if(t.tree!=null){
			treeCount++;		
		}
		templateTypeCount[t.templateType]++;
		
		str=str.substring(idx);//удалить текст обработанного шаблона, остается текст необработанных шаблонов
	}	
		
	foundedNumber=0;//порядковый номер шаблона в базе
	foundedIndex=0;//индекс шаблона
	foundedTemplateType=TemplateType.templateTypeBEGIN;//тип шаблона BEGIN, т.к. вначале игры ИИ делает ход по этому шаблону
	
	for(int i=0;i<baseOfTemplates.size();i++){//найти максимальный индекс шаблона
		if(baseOfTemplates.get(i).templateID>maxTemplateIndex){
			maxTemplateIndex=baseOfTemplates.get(i).templateID;
		}			
	}
	
	//вывести в консоль информацию об обработаной базе
	System.out.println("число шаблонов = "+templateCount);
	System.out.println("число деревьев = "+treeCount);
	System.out.println("максимальный индекс шаблона = "+maxTemplateIndex);
	System.out.println("------------------------------------------------------------");
}

//Метод сохраняет базу игровых ситаций
public void saveTemplateBase(){
	ArrayList<Template> newBase=new ArrayList<Template>();//список для хранения сохраняемой базы
	
	for(int k=0;k<baseOfTemplates.size();k++){//для каждого шаблона найти число ANY (зеленых) точек
		baseOfTemplates.get(k).setANYDotsCount();
	}
	
	//обновить базу с учетом числа ANY точек, т.е. выполняется сортировка по числу таких точек,
	//вначале в новую базу записываются самые подробные шаблоны (с минимумом ANY точек).
	//Сортировка выполняется, т.к. поиск шаблона для хода ИИ происходит начиная с самых подробных шаблонов.
	String str="";
	for(int k=Protocol.maxSize*Protocol.maxSize;k>=0;k--){
		for(int i=0;i<baseOfTemplates.size();i++){
			if(baseOfTemplates.get(i).ANYDotsCount!=k){
				continue;
			}
			String newTemplate=templateToString(baseOfTemplates.get(i));
			str+=newTemplate;
			newBase.add(new Template(newTemplate));
		}
	}
	//база обновляется, как файл базы, так и список объектов в программе
	file.WriteTxtFile(templateFileName,str);
	baseOfTemplates=newBase;
	
	System.out.println("template base saved");
}

//Метод переводит объект шаблона в текст согласно кодировке шаблона для последующего сохранения в файл
private String templateToString(Template t){
	String s="";
	s+="<"+TML.template+">";
	s+="<"+TML.id+":"+t.templateID+">";//индекс шаблона
	s+="<"+TML.type+":"+TemplateType.toString(t.templateType)+">";//тип шаблона
	s+="<"+TML.content+":"+arrayToString(t.templateContent)+">";//ситуация на поле без логики
	
	s+="<"+TML.tree+">";
	try{
		if(t.tree.symmetryType!=-1){
			s=s+t.tree.getSymmetryTypeString();//тип симметрии дерева
		}
		s=s+t.tree.node.getString();//тест дерева
	}catch(Exception e){
		s=s+"<1b:default></1b>";//при создании нового шаблона у него нет дерева, поэтому обрабатывается исключение, в котором создается дерево
	}
	
	s+="</"+TML.tree+">";	
	s+="</"+TML.template+">";
	return s;
}

//Метод проверяет, существует ли в базе шаблон при сохранении нового шаблона.
//Если шаблона нет, то в этом методе он добавляется в базу
public boolean isExistTemplateWhenNewSave(byte[] content,String type){
	if(isExistTemplateView(content)){//Если шаблон уже существует, то он не добавляется в базу
		return true;
	}
	
	String strTemplateContent=arrayToString(content);
	
	maxTemplateIndex=maxTemplateIndex+1;
	
	String s="";
	s+="<"+TML.template+">";
	s+="<"+TML.id+":"+maxTemplateIndex+">";
	s+="<"+TML.type+":"+type+">";
	s+="<"+TML.content+":"+strTemplateContent+">";
	
	s+="</"+TML.template+">";
	
	baseOfTemplates.add(new Template(s));
	saveTemplateBase();	
	return false;
}	

//Метод проверяет, существует ли в базе шаблон при пересохранении (после редактирования) существующего шаблона.
//Если редактированный шаблон уникален, то в этом методе он заменяется в базе.
//При этом templateIndexInBase является порядковым номером хранения шаблона в базе, а не индексом (уникальным кодом) шаблона
public boolean isExistTemplateWhenResave(byte[] content,int templateIndexInBase,String type){
	if(isExistTemplateView(content)){//Если шаблон уже существует, то его пересохранения не происходит.
		return true;
	}
	
	String strTemplateContent=arrayToString(content);
	
	String s="";
	s+="<"+TML.template+">";
	s+="<"+TML.id+":"+baseOfTemplates.get(templateIndexInBase).templateID+">";
	s+="<"+TML.type+":"+type+">";
	s+="<"+TML.content+":"+strTemplateContent+">";
	
	if(baseOfTemplates.get(templateIndexInBase).tree!=null){
		s+="<"+TML.tree+">";
		if(baseOfTemplates.get(templateIndexInBase).tree.symmetryType!=-1){
			s=s+baseOfTemplates.get(templateIndexInBase).tree.getSymmetryTypeString();
		}
		s=s+baseOfTemplates.get(templateIndexInBase).tree.node.getString();
		s+="</"+TML.tree+">";
	}
	s+="</"+TML.template+">";
	
	baseOfTemplates.get(templateIndexInBase).changeTemplate(s);
	
	saveTemplateBase();
	return false;
}

//используется в DotsTemplateEditor для поиска совпадений в базе при сохранении новых шаблонов или пересохранении
private boolean isExistTemplateView(byte[] content){
	for(int j=0;j<baseOfTemplates.size();j++){
		for(int i=0;i<baseOfTemplates.get(j).templateView.size();i++){
			int count=0;
			for(int k=0;k<Protocol.maxSize*Protocol.maxSize;k++){
				if(baseOfTemplates.get(j).templateView.get(i).templateContentArray[k]!=content[k]){
					break;
				}
				count++;
			}
			if(count==Protocol.maxSize*Protocol.maxSize){
				return true;//вариант в базе есть
			}
		}
	}
	return false;
}

//поиск шаблона для поиска хода для списка точек ArrayList<Point> point
public boolean isMoveIfEqualsLikeAreaByPointList(Protocol protocol,Game game,int templateType,ArrayList<Point> pointList,byte[][] fieldState,Point recommendedMove){
	for(int i=0;i<baseOfTemplates.size();i++){
		try{
			//определяется, проходить ли по списку шаблонов в обратном порядке.
			//Для некоторых типов шаблонов нужен поиск в обратном порядке, т.е. не с самых подробных шаблонов 
			//с минимумом ANY точек (как для большинства типов шаблонов), а поиск с наименее подробных шаблонов
			int idx=TemplateType.isTemplateBaseSearchFromStartIdx(templateType)?i:(baseOfTemplates.size()-1-i);
			
			//поиск выполняется только для шаблонов одного типа, поэтому при несовпадени типа переходим к следующему шаблону
			if(baseOfTemplates.get(idx).templateType!=templateType){
				continue;
			}
			
			//поиск совпадения шаблона с игровой ситуацией для списка точек.
			//Если совпадение найдено, в методе isMoveIfEqualsLikeAreaByPointList выполняется поиск хода ИИ
			if(baseOfTemplates.get(idx).isMoveIfEqualsLikeAreaByPointList(protocol,game,pointList,fieldState,templateType,recommendedMove)){
				//если совпадение есть, запоминаем найденный шаблон
				foundedNumber=idx;
				foundedIndex=baseOfTemplates.get(idx).templateID;					
				foundedTemplateType=templateType;
				return true;			
			}
		}catch(Exception e){}
	}
	return false;
}

//поиск шаблона для поиска хода для одной точки Point point
public boolean isMoveIfEqualsLikeAreaByPoint(Protocol protocol,Game game,int templateType,Point point,byte[][] fieldState,Point recommendedMove){
	for(int i=0;i<baseOfTemplates.size();i++){try{
		
		//определяется, проходить ли по списку шаблонов в обратном порядке.
		//Для некоторых типов шаблонов нужен поиск в обратном порядке, т.е. не с самых подробных шаблонов 
		//с минимумом ANY точек (как для большинства типов шаблонов), а поиск с наименее подробных шаблонов
		int idx=TemplateType.isTemplateBaseSearchFromStartIdx(templateType)?i:(baseOfTemplates.size()-1-i);
		
		//поиск выполняется только для шаблонов одного типа, поэтому при несовпадени типа переходим к следующему шаблону
		if(baseOfTemplates.get(idx).templateType!=templateType){
			continue;
		}
		
		//поиск совпадения шаблона с игровой ситуацией для одной точки.
		//Если совпадение найдено, в методе isMoveIfEqualsLikeAreaByPointList выполняется поиск хода ИИ
		if(baseOfTemplates.get(idx).isMoveIfEqualsLikeAreaByPoint(protocol,game,point,fieldState,templateType,recommendedMove)){
			//если совпадение есть, запоминаем найденный шаблон
			foundedNumber=idx;
			foundedIndex=baseOfTemplates.get(idx).templateID;
			foundedTemplateType=templateType;
			return true;
		}
	}catch(Exception e){}}
	return false;
}

//Метод удаляет шаблон из базы и пересохраняет базу
public void deleteTemplate(int index){
	baseOfTemplates.remove(index);
	saveTemplateBase();
}

//Метод ищет и возвращает шаблон по его ID
public Template getTemplateByTemplateID(int templateID){
	for(int i=0;i<baseOfTemplates.size();i++){
		if(baseOfTemplates.get(i).templateID==templateID){
			return baseOfTemplates.get(i);
		}
	}
	return null;
}

//Метод ищет и возвращает индекс шаблона в базе по его ID
public int getBaseIndexByTemplateID(int templateID){
	for(int i=0;i<baseOfTemplates.size();i++){
		if(baseOfTemplates.get(i).templateID==templateID){
			return i;
		}
	}
	return baseOfTemplates.size();
}

//Метод переводит контент шаблона из массива в строку
private String arrayToString(byte[] arr){
	String s="";
	for(int i=0;i<arr.length;i++){
		s+=arr[i];
	}
	return s;
}

}
