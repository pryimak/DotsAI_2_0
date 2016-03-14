//����� TemplateEngine ��������� ����� ������� �������� (�������� � ��������).
//����� ����������� �������������, ����������, ���������� ����, �������� �������� � ����� ������� ��� ���� ��.

package p_TemplateEngine;

import java.awt.Point;
import java.util.ArrayList;

import p_DotsAI.Protocol;
import p_DotsAI.Protocol.Game;
import p_JavaPatterns.Pattern_ReadAndWriteFile;

public class TemplateEngine{
	
public ArrayList<Template> baseOfTemplates;//���� ������� �������� (�������� � ��������)
public int maxTemplateIndex;//������������ ������ ������� � ����
private Pattern_ReadAndWriteFile file;//����� ������ � ������ ������
private String templateFileName;//��� ����� ���� ������� ��������
public int[] templateTypeCount;//������ � ������ �������� ������� ����

//��������� ���������� ������� ��� ��� ����������� � ��������� ��������
public int foundedNumber;//���������� ����� ������� � ����
public int foundedIndex;//������ �������
public int foundedTemplateType;//��� �������

//����������� ������ ��������� � ������������ ���� ������� �������� �� �����
public TemplateEngine(){
	
	//������������� ����������
	templateFileName="dotsAIresources//templateBase.txt";	
	file=new Pattern_ReadAndWriteFile();
	maxTemplateIndex=0;		
	templateTypeCount=new int[TemplateType.lastIndexOfTemplateType];
	int templateCount=0;
	int treeCount=0;
	for(int i=0;i<templateTypeCount.length;i++){
		templateTypeCount[i]=0;
	}
	
	String str=file.ReadTxtFile(templateFileName);//��������� ���� ����
	baseOfTemplates=new ArrayList<Template>();//������� ������ ��� �������� ����	
	
	while(str.length()>0){//������� ���� ����, � ������ �������� while �������������� ���� ������
		int idx=str.indexOf("</"+TML.template+">")+TML.template.length()+3;//�������� ��������� ������, ����������� ���� ������
		String strTemplate=str.substring(0,idx);//�������� ����� �������
		Template t=new Template(strTemplate);//������� ������ �������	
		baseOfTemplates.add(t);//�������� ������ � ������ ��������
		
		//�������� ����� ��������, �������� � ����� �������� ������� ����
		templateCount++;
		if(t.tree!=null){
			treeCount++;		
		}
		templateTypeCount[t.templateType]++;
		
		str=str.substring(idx);//������� ����� ������������� �������, �������� ����� �������������� ��������
	}	
		
	foundedNumber=0;//���������� ����� ������� � ����
	foundedIndex=0;//������ �������
	foundedTemplateType=TemplateType.templateTypeBEGIN;//��� ������� BEGIN, �.�. ������� ���� �� ������ ��� �� ����� �������
	
	for(int i=0;i<baseOfTemplates.size();i++){//����� ������������ ������ �������
		if(baseOfTemplates.get(i).templateID>maxTemplateIndex){
			maxTemplateIndex=baseOfTemplates.get(i).templateID;
		}			
	}
	
	//������� � ������� ���������� �� ����������� ����
	System.out.println("����� �������� = "+templateCount);
	System.out.println("����� �������� = "+treeCount);
	System.out.println("������������ ������ ������� = "+maxTemplateIndex);
	System.out.println("------------------------------------------------------------");
}

//����� ��������� ���� ������� �������
public void saveTemplateBase(){
	ArrayList<Template> newBase=new ArrayList<Template>();//������ ��� �������� ����������� ����
	
	for(int k=0;k<baseOfTemplates.size();k++){//��� ������� ������� ����� ����� ANY (�������) �����
		baseOfTemplates.get(k).setANYDotsCount();
	}
	
	//�������� ���� � ������ ����� ANY �����, �.�. ����������� ���������� �� ����� ����� �����,
	//������� � ����� ���� ������������ ����� ��������� ������� (� ��������� ANY �����).
	//���������� �����������, �.�. ����� ������� ��� ���� �� ���������� ������� � ����� ��������� ��������.
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
	//���� �����������, ��� ���� ����, ��� � ������ �������� � ���������
	file.WriteTxtFile(templateFileName,str);
	baseOfTemplates=newBase;
	
	System.out.println("template base saved");
}

//����� ��������� ������ ������� � ����� �������� ��������� ������� ��� ������������ ���������� � ����
private String templateToString(Template t){
	String s="";
	s+="<"+TML.template+">";
	s+="<"+TML.id+":"+t.templateID+">";//������ �������
	s+="<"+TML.type+":"+TemplateType.toString(t.templateType)+">";//��� �������
	s+="<"+TML.content+":"+arrayToString(t.templateContent)+">";//�������� �� ���� ��� ������
	
	s+="<"+TML.tree+">";
	try{
		if(t.tree.symmetryType!=-1){
			s=s+t.tree.getSymmetryTypeString();//��� ��������� ������
		}
		s=s+t.tree.node.getString();//���� ������
	}catch(Exception e){
		s=s+"<1b:default></1b>";//��� �������� ������ ������� � ���� ��� ������, ������� �������������� ����������, � ������� ��������� ������
	}
	
	s+="</"+TML.tree+">";	
	s+="</"+TML.template+">";
	return s;
}

//����� ���������, ���������� �� � ���� ������ ��� ���������� ������ �������.
//���� ������� ���, �� � ���� ������ �� ����������� � ����
public boolean isExistTemplateWhenNewSave(byte[] content,String type){
	if(isExistTemplateView(content)){//���� ������ ��� ����������, �� �� �� ����������� � ����
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

//����� ���������, ���������� �� � ���� ������ ��� �������������� (����� ��������������) ������������� �������.
//���� ��������������� ������ ��������, �� � ���� ������ �� ���������� � ����.
//��� ���� templateIndexInBase �������� ���������� ������� �������� ������� � ����, � �� �������� (���������� �����) �������
public boolean isExistTemplateWhenResave(byte[] content,int templateIndexInBase,String type){
	if(isExistTemplateView(content)){//���� ������ ��� ����������, �� ��� �������������� �� ����������.
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

//������������ � DotsTemplateEditor ��� ������ ���������� � ���� ��� ���������� ����� �������� ��� ��������������
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
				return true;//������� � ���� ����
			}
		}
	}
	return false;
}

//����� ������� ��� ������ ���� ��� ������ ����� ArrayList<Point> point
public boolean isMoveIfEqualsLikeAreaByPointList(Protocol protocol,Game game,int templateType,ArrayList<Point> pointList,byte[][] fieldState,Point recommendedMove){
	for(int i=0;i<baseOfTemplates.size();i++){
		try{
			//������������, ��������� �� �� ������ �������� � �������� �������.
			//��� ��������� ����� �������� ����� ����� � �������� �������, �.�. �� � ����� ��������� �������� 
			//� ��������� ANY ����� (��� ��� ����������� ����� ��������), � ����� � �������� ��������� ��������
			int idx=TemplateType.isTemplateBaseSearchFromStartIdx(templateType)?i:(baseOfTemplates.size()-1-i);
			
			//����� ����������� ������ ��� �������� ������ ����, ������� ��� ����������� ���� ��������� � ���������� �������
			if(baseOfTemplates.get(idx).templateType!=templateType){
				continue;
			}
			
			//����� ���������� ������� � ������� ��������� ��� ������ �����.
			//���� ���������� �������, � ������ isMoveIfEqualsLikeAreaByPointList ����������� ����� ���� ��
			if(baseOfTemplates.get(idx).isMoveIfEqualsLikeAreaByPointList(protocol,game,pointList,fieldState,templateType,recommendedMove)){
				//���� ���������� ����, ���������� ��������� ������
				foundedNumber=idx;
				foundedIndex=baseOfTemplates.get(idx).templateID;					
				foundedTemplateType=templateType;
				return true;			
			}
		}catch(Exception e){}
	}
	return false;
}

//����� ������� ��� ������ ���� ��� ����� ����� Point point
public boolean isMoveIfEqualsLikeAreaByPoint(Protocol protocol,Game game,int templateType,Point point,byte[][] fieldState,Point recommendedMove){
	for(int i=0;i<baseOfTemplates.size();i++){try{
		
		//������������, ��������� �� �� ������ �������� � �������� �������.
		//��� ��������� ����� �������� ����� ����� � �������� �������, �.�. �� � ����� ��������� �������� 
		//� ��������� ANY ����� (��� ��� ����������� ����� ��������), � ����� � �������� ��������� ��������
		int idx=TemplateType.isTemplateBaseSearchFromStartIdx(templateType)?i:(baseOfTemplates.size()-1-i);
		
		//����� ����������� ������ ��� �������� ������ ����, ������� ��� ����������� ���� ��������� � ���������� �������
		if(baseOfTemplates.get(idx).templateType!=templateType){
			continue;
		}
		
		//����� ���������� ������� � ������� ��������� ��� ����� �����.
		//���� ���������� �������, � ������ isMoveIfEqualsLikeAreaByPointList ����������� ����� ���� ��
		if(baseOfTemplates.get(idx).isMoveIfEqualsLikeAreaByPoint(protocol,game,point,fieldState,templateType,recommendedMove)){
			//���� ���������� ����, ���������� ��������� ������
			foundedNumber=idx;
			foundedIndex=baseOfTemplates.get(idx).templateID;
			foundedTemplateType=templateType;
			return true;
		}
	}catch(Exception e){}}
	return false;
}

//����� ������� ������ �� ���� � ������������� ����
public void deleteTemplate(int index){
	baseOfTemplates.remove(index);
	saveTemplateBase();
}

//����� ���� � ���������� ������ �� ��� ID
public Template getTemplateByTemplateID(int templateID){
	for(int i=0;i<baseOfTemplates.size();i++){
		if(baseOfTemplates.get(i).templateID==templateID){
			return baseOfTemplates.get(i);
		}
	}
	return null;
}

//����� ���� � ���������� ������ ������� � ���� �� ��� ID
public int getBaseIndexByTemplateID(int templateID){
	for(int i=0;i<baseOfTemplates.size();i++){
		if(baseOfTemplates.get(i).templateID==templateID){
			return i;
		}
	}
	return baseOfTemplates.size();
}

//����� ��������� ������� ������� �� ������� � ������
private String arrayToString(byte[] arr){
	String s="";
	for(int i=0;i<arr.length;i++){
		s+=arr[i];
	}
	return s;
}

}
