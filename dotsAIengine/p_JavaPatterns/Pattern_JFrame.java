/*���� ����� ������������ �������� JFrame ���� ��� ������,
 * ���������� ��������� ������� ������, � ����� �������� ��������
 *
 * ��� ����� ���������� � ���������� ������ ��������� ���:
 *
 *		JFrame frame=new JFrame();
 *		public Main(){
 * 			new C_JFrame(frame,title,false,600,800,Color.cyan);
 * 		}
 *
 * ��� "frame" - ������ � ������� ������, ���������� ���� JFrame ��� �������� ������
 * ��� "title" - ��������� ���� � ������� ���������
 * ��� "false" - ����������� ��������� ������ ���� ���� boolean
 * ��� 600,800 - ������ � ������ ����
 * ��� Color.cyan - ������� ���� ����
 */

package p_JavaPatterns;

import java.awt.Color;
import java.awt.Toolkit;
import javax.swing.JFrame;

public class Pattern_JFrame {//�����

//����������� � �����������
public Pattern_JFrame(JFrame frame, String title, boolean resizable,int width,int height,Color background){
	frame.setTitle(title);//���������� ���������
	frame.setResizable(resizable);//���������� ����������� ��������� ������ ����
	
	//���������� ���������� � ������� � ������� ����
	frame.getContentPane().setLayout(new p_JavaPatterns.Pattern_Layout(width,height));
	frame.getContentPane().setBackground(background);//���������� ���� ����
	frame.pack();//��� ������ ���� �� �����
	frame.show();//��� ������ ���� �� �����
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//��� ����������� �������� ����
	setCenterAlign(frame);
}

public void setCenterAlign(JFrame frame){
	frame.move((int)(Toolkit.getDefaultToolkit().getScreenSize().width*0.5f-frame.getWidth()/2), 
		(int)(Toolkit.getDefaultToolkit().getScreenSize().height*0.5f-frame.getHeight()/2));
}

}
