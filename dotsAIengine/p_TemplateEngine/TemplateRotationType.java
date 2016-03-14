//Класс TemplateRotationType хранится список типов разворотов шаблона.
//Каждый шаблон можно развернуть 8 способами.

package p_TemplateEngine;

import java.awt.Point;

import p_DotsAI.Protocol;

public class TemplateRotationType{

	//типы разворота шаблона
	public static int templateRotationTypeR0=0;
	public static int templateRotationTypeR90=1;
	public static int templateRotationTypeR180=2;
	public static int templateRotationTypeR270=3;
	public static int templateRotationTypeGORIZONTAL=4;
	public static int templateRotationTypeVERTICAL=5;
	public static int templateRotationTypeGORIZONTAL90=6;
	public static int templateRotationTypeVERTICAL90=7;
	
	//получить разворот шаблона "byte[] arr" по типу "int templateRotationType"
	public static byte[] getTransformArray(int templateRotationType,byte[] arr) {
		byte[] strNew=new byte[Protocol.maxSize*Protocol.maxSize];
		if(templateRotationType==templateRotationTypeR0){//чтобы создался новый объект
			for(int i=0;i<Protocol.maxSize*Protocol.maxSize;i++){
				strNew[i]=arr[i];
			}
			return arr;
		}if(templateRotationType==templateRotationTypeR90){
			int idx=0;
			for(int i=0;i<Protocol.maxSize;i++){for(int j=Protocol.maxSize-1;j>=0;j--){strNew[idx]=arr[Protocol.maxSize*j+i];idx++;}}
			return strNew;
		}
		if(templateRotationType==templateRotationTypeR180){arr=getTransformArray(templateRotationTypeR90,arr);return(getTransformArray(templateRotationTypeR90,arr));}
		if(templateRotationType==templateRotationTypeR270){arr=getTransformArray(templateRotationTypeR180,arr);return(getTransformArray(templateRotationTypeR90,arr));}
		if(templateRotationType==templateRotationTypeGORIZONTAL){arr=getTransformArray(templateRotationTypeR180,arr);return(getTransformArray(templateRotationTypeVERTICAL,arr));}
		if(templateRotationType==templateRotationTypeVERTICAL){
			int idx=0;
			for(int i=Protocol.maxSize-1;i>=0;i--){
				for(int j=0;j<Protocol.maxSize;j++){
					strNew[idx]=arr[Protocol.maxSize*i+j];
					idx++;
				}
			}
			return strNew;
		}if(templateRotationType==templateRotationTypeGORIZONTAL90){arr=getTransformArray(templateRotationTypeGORIZONTAL,arr);return(getTransformArray(templateRotationTypeR90,arr));}
		if(templateRotationType==templateRotationTypeVERTICAL90){arr=getTransformArray(templateRotationTypeVERTICAL,arr);return(getTransformArray(templateRotationTypeR90,arr));}
		return arr;
	}
	
	//получить разворот определенной точки "Point p" по типу "int templateRotationType"
	public static Point getTransformPoint(int templateRotationType,Point p) {
		if(templateRotationType==templateRotationTypeR0)return new Point(p.x,p.y);
		if(templateRotationType==templateRotationTypeR90)return new Point(-p.y,p.x);
		if(templateRotationType==templateRotationTypeR180)return new Point(-p.x,-p.y);
		if(templateRotationType==templateRotationTypeR270)return new Point(p.y,-p.x);
		if(templateRotationType==templateRotationTypeGORIZONTAL)return new Point(-p.x,p.y);
		if(templateRotationType==templateRotationTypeVERTICAL)return new Point(p.x,-p.y);
		if(templateRotationType==templateRotationTypeGORIZONTAL90)return new Point(-p.y,-p.x);
		if(templateRotationType==templateRotationTypeVERTICAL90)return new Point(p.y,p.x);
		return new Point(p.x,p.y);
	}
	
}
