//Класс DotsTemplateEditor обеспечивает функционал редактора шаблонов.
//Элементы графического интерфейса создаются отдельно в классе MainFrame

package p_TemplateEditor;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import javax.swing.Icon;
import p_DotsAI.DotsAI;
import p_DotsAI.Protocol;
import p_GUI.GameFieldGUI;
import p_JavaPatterns.Pattern_UndoRedoStrings;
import p_TemplateEngine.Template;
import p_TemplateEngine.TemplateEngine;
import p_TemplateEngine.TemplateRotationType;
import p_TemplateEngine.TemplateType;
import p_TreeEditor.DotsTreeEditor;


public class DotsTemplateEditor extends MainFrame{
	
	//private Thread t=new Thread(this);//поток
	public TemplateEngine base;//база шаблонов
	
	int preX=-1,preY=-1;//хранит предыдущие координаты курсора на поле
	int baseTemplateIndex=0;//порядковый номер текущего шаблона в базе
	Pattern_UndoRedoStrings changes=new Pattern_UndoRedoStrings();//хранит состояния поля для отмены и возвращения изменений при редактировании шаблонов
	Template template=null;//текущий шаблон
	public DotsTreeEditor dotsTreeEditor;//окно редактора деревьев
	private DotsAI dotsAI;//основное окно программы с игровым полем
	
	byte[][] field;//хранит состояние поля шаблона
	GameFieldGUI gameFieldGUI;//для рисования поля	
	int otstupX=90,otstupY=35;//отступы для поля

//создание редактора шаблонов
public DotsTemplateEditor(TemplateEngine base,int moveX,int moveY,DotsAI pointsAI){
	this.dotsAI=pointsAI;
	this.move(moveX+pointsAI.protocol.getGame(pointsAI.gameIdx).sizeX*2-60,moveY+5);//смещение окна
		
	//массив для хранения поля шаблона
	field=new byte[Protocol.maxSize][Protocol.maxSize];
	for(byte x=0;x<Protocol.maxSize;x++){
		for(byte y=0;y<Protocol.maxSize;y++){
			field[x][y]=Protocol.templateDotType_EMPTY;
		}
	}
	
	gameFieldGUI=new GameFieldGUI(this.getGraphics(),otstupX,otstupY);//для рисования шаблонов
	this.base=base;//база шаблонов
	dotsTreeEditor=new DotsTreeEditor(base);//редактор деревьев

	//действия при нажатии кнопок типов шаблонов
	for(int i=0;i<templateTypeItems.length;i++){
		templateTypeItems[i].setText(base.templateTypeCount[i]+" - "+templateTypeItems[i].getText());
		templateTypeItems[i].addActionListener(getActionListener(templateTypeItems[i].getIcon(),i));
	}

	buttonActions();//действия при нажатии на различные кнопки
	
	//нарисовать первый шаблон в базе
	baseTemplateIndex=0;
	setBaseTemplate(0);	
	paint();
}

//действия при нажатии кнопок типов шаблонов
public ActionListener getActionListener(final Icon icon,final int n){
	return new ActionListener(){public void actionPerformed(ActionEvent arg0){
		labelCurrentTemplateType.setIcon(icon);
		labelCurrentTemplateType.setText(TemplateType.toString(n));
	}};
}

//отобразить на поле шаблон для найденного хода ИИ
public void moveFromAI(byte[] content,int foundedNumber,int templateType){	
	if(foundedNumber!=0){
		setBaseTemplate(foundedNumber);
		changes.insert(getContent());
	}else{
		moveByContent(content);
		changes.insert(getContent());
	}
};

//отобразить на поле шаблон из базы шаблонов
private void setBaseTemplate(int templateNumber){
	while(templateNumber<0)templateNumber++;
	while(templateNumber>base.baseOfTemplates.size()-1)templateNumber--;
	template=base.baseOfTemplates.get(templateNumber);
	
	moveByContent(template.templateContent);	

	buttonInfoPanelChange(""+template.templateID);
	butTemplateSequenceInBase.setText("<HTML>"+(templateNumber+1));
	
	baseTemplateIndex=templateNumber;
	changes.insert(getContent());
	labelCurrentTemplateType.setText(TemplateType.toString(template.templateType));
	labelCurrentTemplateType.setIcon(templateTypeIcons[template.templateType]);
	
	dotsTreeEditor.setTree(template);
}

//при нажатии кнопкой мыши на поле выполнить добавление в шаблон и рисование точки
public void mousePressed(MouseEvent me) {
	int x=getMouseClickX(me);
	int y=getMouseClickY(me);
	
	try{if(x<dotsAI.protocol.getGame(dotsAI.gameIdx).sizeX&x>=0&y<dotsAI.protocol.getGame(dotsAI.gameIdx).sizeY&y>=0){
		move(x,y,new Byte(buttonDotIndex.getText().substring(buttonDotIndex.getText().lastIndexOf(">")+1)).byteValue());
		changes.insert(getContent());
		paint();
	}}catch(Exception e){}
}

public void windowClosing(WindowEvent e){}
public void windowActivated(WindowEvent e){t=new Thread(this);t.start();}//перерисовка поля при активации окна редактора шаблонов

//перерисовка поля с задержкой
public void run(){
	try{t.sleep(500);}catch(Exception e){}
	paint();
	try{dotsTreeEditor.gameFieldGUI.drawDotsForTreeEditor(dotsTreeEditor.gameField,true,dotsTreeEditor.template);}catch(Exception e){}
	t.stop();
}

//изменение надписи на информационной панели
void buttonInfoPanelChange(String str){
	buttonInfoPanel.setText(str);
	if(str.length()<40&str.length()>5){
		Font f=new Font("Tahoma", 14, 14);
		Graphics g=this.getGraphics();
		g.setFont(f);
		g.drawString(str, 63, 120);
	}
};

//добавление действий при нажатии на кнопки
void buttonActions(){
	
	//перерисовать поле
	buttonRepaint.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0) {paint();}});
	
	//удалить шаблон из базы
	buttonDelete.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0) {
		setBaseTemplate(baseTemplateIndex);
		if(msg.showConfirmDialog(DotsTemplateEditor.this,"удалить шаблон?","удалить шаблон?",0)==0){
			buttonInfoPanelChange("удален");
			changes.insert(getContent());
			base.deleteTemplate(baseTemplateIndex);
			setBaseTemplate(baseTemplateIndex);
		}
	}});
	
	//добавить новый шаблон в базу
	buttonSave.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0) {
			if(base.isExistTemplateWhenNewSave(getContent(),labelCurrentTemplateType.getText()))buttonInfoPanelChange("уже есть");
			else {
				butTemplateSequenceInBase.setText("<HTML>"+base.baseOfTemplates.size());
				baseTemplateIndex=base.baseOfTemplates.size()-1;
				t=new Thread(DotsTemplateEditor.this);t.start();
				setBaseTemplate(base.getBaseIndexByTemplateID(base.maxTemplateIndex));
				buttonInfoPanelChange("добавлен");
			}
	}});
	
	//пересохранить шаблон в базе
	buttonResave.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0) {
		int templateIdx=base.baseOfTemplates.get(baseTemplateIndex).templateID;
		if(base.isExistTemplateWhenResave(getContent(),baseTemplateIndex,labelCurrentTemplateType.getText()))buttonInfoPanelChange("уже есть");
		else{
			setBaseTemplate(base.getBaseIndexByTemplateID(templateIdx));
			buttonInfoPanelChange("пересохранено");			
		}
	}});	
	
	//отменить изменения на поле при редактировании шаблона
	buttonUndo.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){moveByContent(changes.getUndo());}});
	
	//вернуть отмененные изменения на поле при редактировании шаблона
	buttonRedo.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){moveByContent(changes.getRedo());}});
	
	//сдвиг шаблона в стороны
	buttonRight.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){changes.insert(getContent());moveToSide("r");}});
	buttonLeft.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){changes.insert(getContent());moveToSide("l");}});
	buttonTop.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){changes.insert(getContent());moveToSide("t");}});
	buttonRightTop.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){changes.insert(getContent());moveToSide("rt");}});
	buttonLeftTop.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){changes.insert(getContent());moveToSide("lt");}});
	buttonBottom.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){changes.insert(getContent());moveToSide("b");}});
	buttonRightBottom.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){changes.insert(getContent());moveToSide("rb");}});
	buttonLeftBottom.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){changes.insert(getContent());moveToSide("lb");}});
	
	//установка значимого размера шаблона
	button5.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){changes.insert(getContent());setFieldFrame(5);}});
	button7.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){changes.insert(getContent());setFieldFrame(7);}});
	button9.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){changes.insert(getContent());setFieldFrame(9);}});
	button11.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){changes.insert(getContent());setFieldFrame(11);}});
	button13.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){changes.insert(getContent());setFieldFrame(13);}});
	
	//навигация по базе шаблонов
	buttonPrevious.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){try{
		while(!TemplateType.toString(base.baseOfTemplates.get(baseTemplateIndex-1).templateType).equals(labelCurrentTemplateType.getText())){baseTemplateIndex--;}
		setBaseTemplate(baseTemplateIndex-1);
	}catch(Exception e){}}});
	buttonNext.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){try{
		while(!TemplateType.toString(base.baseOfTemplates.get(baseTemplateIndex+1).templateType).equals(labelCurrentTemplateType.getText())){baseTemplateIndex++;}
		setBaseTemplate(baseTemplateIndex+1);
	}catch(Exception e){}}});
	buttonLast.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){
		int idx=base.baseOfTemplates.size()-1;
		while(!TemplateType.toString(base.baseOfTemplates.get(idx).templateType).equals(labelCurrentTemplateType.getText())){idx--;}
		baseTemplateIndex=idx;
		setBaseTemplate(baseTemplateIndex);		
	}});
	buttonFirst.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){
		int idx=0;
		while(!TemplateType.toString(base.baseOfTemplates.get(idx).templateType).equals(labelCurrentTemplateType.getText())){idx++;}
		baseTemplateIndex=idx;
		setBaseTemplate(baseTemplateIndex);
	}});
	
	//открыть шаблон по ID шаблона. По умолчанию выводится ID последнего добавленного в базу шаблона
	butOpen.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0){try{
		int i=new Integer(msg.showInputDialog("Перейти к шаблону с номером (например "+base.maxTemplateIndex+")",base.maxTemplateIndex+"")).intValue();
		setBaseTemplate(base.getBaseIndexByTemplateID(i));
		changes.insert(getContent());
	}catch(Exception e){}}});
	
	//выполнить разворот шаблона (при этом ходы в дереве не поворачиваются, поэтому не желательно сохранять развернутый шаблон, инчае нужно редактировать дерево)
	but90.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0) {try{
		moveByContent(TemplateRotationType.getTransformArray(TemplateRotationType.templateRotationTypeR90, getContent()));changes.insert(getContent());
	}catch(Exception e){}}});
	but180.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0) {try{
		moveByContent(TemplateRotationType.getTransformArray(TemplateRotationType.templateRotationTypeR180, getContent()));changes.insert(getContent());
	}catch(Exception e){}}});
	but270.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0) {try{
		moveByContent(TemplateRotationType.getTransformArray(TemplateRotationType.templateRotationTypeR270, getContent()));changes.insert(getContent());
	}catch(Exception e){}}});
	butVertical.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0) {try{
		moveByContent(TemplateRotationType.getTransformArray(TemplateRotationType.templateRotationTypeVERTICAL, getContent()));changes.insert(getContent());
	}catch(Exception e){}}});
	butGorizontal.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0) {try{
		moveByContent(TemplateRotationType.getTransformArray(TemplateRotationType.templateRotationTypeGORIZONTAL, getContent()));changes.insert(getContent());
	}catch(Exception e){}}});
	butVertical90.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0) {try{
		moveByContent(TemplateRotationType.getTransformArray(TemplateRotationType.templateRotationTypeVERTICAL90, getContent()));changes.insert(getContent());
	}catch(Exception e){}}});
	buttonGorizontal90.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent arg0) {try{
		moveByContent(TemplateRotationType.getTransformArray(TemplateRotationType.templateRotationTypeGORIZONTAL90, getContent()));changes.insert(getContent());
	}catch(Exception e){}}});
}

//при перемещении курсора мыши рисовать новый ползунок, а предыдущий закрашивать
public void mouseMoved(MouseEvent me){	
	int x=getMouseClickX(me),y=getMouseClickY(me);
	if(x!=preX|y!=preY){//произошло перемещение курсора
		try{
			if(field[preX][preY]==Protocol.templateDotType_EMPTY){
				gameFieldGUI.clearHint(preX, preY);
			}			
		}catch(Exception e){}
		preX=x;
		preY=y;
		try{
			if(field[x][y]==Protocol.templateDotType_EMPTY){
				gameFieldGUI.drawHint(preX, preY);
			}
		}catch(Exception e){}
	}	
}

//нарисовать поле с шаблоном
public void paint(){
	try{	
		gameFieldGUI.drawGameFieldArea(Protocol.maxSize,Protocol.maxSize,true);
		gameFieldGUI.drawDotsForTemplateEditor(field,template);
	}catch(Exception e){}
}

//нарисовать поле с шаблоном
void moveByContent(byte[] content){
	for(int i=0;i<content.length;i++){
		move(i%Protocol.maxSize,i/Protocol.maxSize,content[i]);
	}
	paint();
}

//нарисовать поле с шаблоном
void moveByField(byte content[][]){
	for(int i=0;i<Protocol.maxSize;i++){
		for(int j=0;j<Protocol.maxSize;j++){
			move(i,j,content[i][j]);
		}
	}
	paint();
}	

//поставить точку любого типа на поле с шаблоном
public void move(int x, int y, byte moveType){
	if((x<0)|(y<0)|(x>=Protocol.maxSize)|(y>=Protocol.maxSize)){}
	else field[x][y]=moveType;	
}

//получить содержимое поля
byte[] getContent(){
	byte[] content=new byte[Protocol.maxSize*Protocol.maxSize];
	int idx=0;
	for(int j=0;j<Protocol.maxSize;j++){
		for(int i=0;i<Protocol.maxSize;i++){
			content[idx]=field[i][j];
			idx++;
		}
	}
	return content;
}

//при сдвиге шаблона в сторону
void moveToSide(String sideName){
	if(sideName.equalsIgnoreCase("lt")){
		for(int i=0;i<Protocol.maxSize-1;i++){
			for(int j=0;j<Protocol.maxSize-1;j++){
				field[i][j]=field[i+1][j+1];
			}
		}
		for(int i=0;i<Protocol.maxSize;i++){
			field[i][Protocol.maxSize-1]=Protocol.templateDotType_EMPTY;
		}
		for(int j=0;j<Protocol.maxSize;j++){
			field[Protocol.maxSize-1][j]=Protocol.templateDotType_EMPTY;
		}
	}else if(sideName.equalsIgnoreCase("rt")){
		for(int i=Protocol.maxSize-1;i>0;i--){
			for(int j=0;j<Protocol.maxSize-1;j++){
				field[i][j]=field[i-1][j+1];
			}
		}
		for(int i=0;i<Protocol.maxSize;i++){
			field[i][Protocol.maxSize-1]=Protocol.templateDotType_EMPTY;
		}
		for(int j=0;j<Protocol.maxSize;j++){
			field[0][j]=Protocol.templateDotType_EMPTY;
		}
	}else if(sideName.equalsIgnoreCase("t")){
		for(int i=0;i<Protocol.maxSize;i++){
			for(int j=0;j<Protocol.maxSize-1;j++){
				field[i][j]=field[i][j+1];
			}
		}
		for(int i=0;i<Protocol.maxSize;i++){
			field[i][Protocol.maxSize-1]=Protocol.templateDotType_EMPTY;
		}
	}else if(sideName.equalsIgnoreCase("l")){
		for(int j=0;j<Protocol.maxSize;j++){
			for(int i=0;i<Protocol.maxSize-1;i++){
				field[i][j]=field[i+1][j];
			}
		}
		for(int j=0;j<Protocol.maxSize;j++){
			field[Protocol.maxSize-1][j]=Protocol.templateDotType_EMPTY;
		}
	}else if(sideName.equalsIgnoreCase("r")){
		for(int i=Protocol.maxSize-1;i>0;i--){
			for(int j=0;j<Protocol.maxSize;j++){
				field[i][j]=field[i-1][j];
			}
		}
		for(int j=0;j<Protocol.maxSize;j++){
			field[0][j]=Protocol.templateDotType_EMPTY;
		}
	}else if(sideName.equalsIgnoreCase("lb")){
		for(int i=0;i<Protocol.maxSize-1;i++){
			for(int j=Protocol.maxSize-1;j>0;j--){
				field[i][j]=field[i+1][j-1];
			}
		}
		for(int i=0;i<Protocol.maxSize;i++){
			field[i][0]=Protocol.templateDotType_EMPTY;
		}
		for(int j=0;j<Protocol.maxSize;j++){
			field[Protocol.maxSize-1][j]=Protocol.templateDotType_EMPTY;
		}
	}else if(sideName.equalsIgnoreCase("rb")){
		for(int i=Protocol.maxSize-1;i>0;i--){
			for(int j=Protocol.maxSize-1;j>0;j--){
				field[j][i]=field[j-1][i-1];
			}
		}
		for(int i=0;i<Protocol.maxSize;i++){
			field[i][0]=Protocol.templateDotType_EMPTY;
		}
		for(int j=0;j<Protocol.maxSize;j++){
			field[0][j]=Protocol.templateDotType_EMPTY;
		}
	}else if(sideName.equalsIgnoreCase("b")){
		for(int i=Protocol.maxSize-1;i>0;i--){
			for(int j=0;j<Protocol.maxSize;j++){
				field[j][i]=field[j][i-1];
			}
		}
		for(int i=0;i<Protocol.maxSize;i++){
			field[i][0]=Protocol.templateDotType_EMPTY;
		}
	}
	
	moveByField(field);//перерисовать поле
}

//установить значимый размер поля
void setFieldFrame(int size){
	
	if(size==13){
		for(int i=0;i<Protocol.maxSize;i++){
			field[i][0]=Protocol.templateDotType_ANY;
			field[i][Protocol.maxSize-1]=Protocol.templateDotType_ANY;
		}
		for(int i=0;i<Protocol.maxSize;i++){
			field[0][i]=Protocol.templateDotType_ANY;
			field[Protocol.maxSize-1][i]=Protocol.templateDotType_ANY;
		}
	}else if(size==11){
		for(int i=0;i<Protocol.maxSize;i++){
			field[i][0]=Protocol.templateDotType_ANY;
			field[i][Protocol.maxSize-1]=Protocol.templateDotType_ANY;
			field[i][1]=Protocol.templateDotType_ANY;
			field[i][Protocol.maxSize-2]=Protocol.templateDotType_ANY;
		}
		for(int i=0;i<Protocol.maxSize;i++){
			field[0][i]=Protocol.templateDotType_ANY;
			field[Protocol.maxSize-1][i]=Protocol.templateDotType_ANY;
			field[1][i]=Protocol.templateDotType_ANY;
			field[Protocol.maxSize-2][i]=Protocol.templateDotType_ANY;
		}
	}else if(size==9){
		for(int i=0;i<Protocol.maxSize;i++){
			field[i][0]=Protocol.templateDotType_ANY;
			field[i][Protocol.maxSize-1]=Protocol.templateDotType_ANY;
			field[i][1]=Protocol.templateDotType_ANY;
			field[i][Protocol.maxSize-2]=Protocol.templateDotType_ANY;
			field[i][2]=Protocol.templateDotType_ANY;
			field[i][Protocol.maxSize-3]=Protocol.templateDotType_ANY;
		}
		for(int i=0;i<Protocol.maxSize;i++){
			field[0][i]=Protocol.templateDotType_ANY;
			field[Protocol.maxSize-1][i]=Protocol.templateDotType_ANY;
			field[1][i]=Protocol.templateDotType_ANY;
			field[Protocol.maxSize-2][i]=Protocol.templateDotType_ANY;
			field[2][i]=Protocol.templateDotType_ANY;
			field[Protocol.maxSize-3][i]=Protocol.templateDotType_ANY;
		}
	}else if(size==7){
		for(int i=0;i<Protocol.maxSize;i++){
			field[i][0]=Protocol.templateDotType_ANY;
			field[i][Protocol.maxSize-1]=Protocol.templateDotType_ANY;
			field[i][1]=Protocol.templateDotType_ANY;
			field[i][Protocol.maxSize-2]=Protocol.templateDotType_ANY;
			field[i][2]=Protocol.templateDotType_ANY;
			field[i][Protocol.maxSize-3]=Protocol.templateDotType_ANY;
			field[i][3]=Protocol.templateDotType_ANY;
			field[i][Protocol.maxSize-4]=Protocol.templateDotType_ANY;
		}
		for(int i=0;i<Protocol.maxSize;i++){
			field[0][i]=Protocol.templateDotType_ANY;
			field[Protocol.maxSize-1][i]=Protocol.templateDotType_ANY;
			field[1][i]=Protocol.templateDotType_ANY;
			field[Protocol.maxSize-2][i]=Protocol.templateDotType_ANY;
			field[2][i]=Protocol.templateDotType_ANY;
			field[Protocol.maxSize-3][i]=Protocol.templateDotType_ANY;
			field[3][i]=Protocol.templateDotType_ANY;
			field[Protocol.maxSize-4][i]=Protocol.templateDotType_ANY;
		}
	}else if(size==5){
		for(int i=0;i<Protocol.maxSize;i++){
			field[i][0]=Protocol.templateDotType_ANY;
			field[i][Protocol.maxSize-1]=Protocol.templateDotType_ANY;
			field[i][1]=Protocol.templateDotType_ANY;
			field[i][Protocol.maxSize-2]=Protocol.templateDotType_ANY;
			field[i][2]=Protocol.templateDotType_ANY;
			field[i][Protocol.maxSize-3]=Protocol.templateDotType_ANY;
			field[i][3]=Protocol.templateDotType_ANY;
			field[i][Protocol.maxSize-4]=Protocol.templateDotType_ANY;
			field[i][4]=Protocol.templateDotType_ANY;
			field[i][Protocol.maxSize-5]=Protocol.templateDotType_ANY;
		}
		for(int i=0;i<Protocol.maxSize;i++){
			field[0][i]=Protocol.templateDotType_ANY;
			field[Protocol.maxSize-1][i]=Protocol.templateDotType_ANY;
			field[1][i]=Protocol.templateDotType_ANY;
			field[Protocol.maxSize-2][i]=Protocol.templateDotType_ANY;
			field[2][i]=Protocol.templateDotType_ANY;
			field[Protocol.maxSize-3][i]=Protocol.templateDotType_ANY;
			field[3][i]=Protocol.templateDotType_ANY;
			field[Protocol.maxSize-4][i]=Protocol.templateDotType_ANY;
			field[4][i]=Protocol.templateDotType_ANY;
			field[Protocol.maxSize-5][i]=Protocol.templateDotType_ANY;
		}
	}
	
	moveByField(field);//перерисовать поле
}

//получить координаты поля из координат пикселя при нажатии кнопки мыши
int getMouseClickX(MouseEvent me){return (int)(((double)me.getX()-14-(double)(4*GameFieldGUI.cellSize))/(double)GameFieldGUI.cellSize);};
int getMouseClickY(MouseEvent me){return (int)(((double)me.getY()-12-(double)(GameFieldGUI.cellSize))/(double)GameFieldGUI.cellSize);};

}