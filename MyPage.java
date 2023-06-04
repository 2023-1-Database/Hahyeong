package DB2023;
import java.sql.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

//마이페이지
public class MyPage {	
	public static void main(String[] args) {
		MyPageFrame MyPage = new MyPageFrame();
	}
}

class MainFrame extends JFrame{
	public MainFrame() {
		setSize(1000,750);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("DB2023Team04");
		setLayout(new BorderLayout());
		
		//header 부분 (홈 화면으로 되돌아가는 버튼)
		JPanel header = new JPanel();
		header.setLayout(new FlowLayout());
		header.setBorder(BorderFactory.createEmptyBorder(10 , 0 , 0 , 0));
		
		JButton homebutton = new JButton("도비");
		homebutton.setPreferredSize(new Dimension(900,70));
		homebutton.setFont(new Font("맑은 고딕", Font.BOLD, 30));
		header.add(homebutton);
		
		
		//sidebar 부분 (로그인, 마이페이지, 신권신청)
		JPanel sidebar = new JPanel();
		sidebar.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));
		sidebar.setBorder(BorderFactory.createEmptyBorder(5 , 45 , 0 , 5));	
		sidebar.setPreferredSize(new Dimension(200,600));
		JTextField id = new JTextField(15);
		id.setText("ID");
		JTextField passwd = new JTextField(15);
		passwd.setText("Password");
		sidebar.add(id);
		sidebar.add(passwd);
		sideButton(sidebar, "Login");
		sideButton(sidebar, "회원 가입");
		sideButton(sidebar, "마이 페이지");
		sideButton(sidebar, "신권 신청하기");
		
		add(header, BorderLayout.PAGE_START);
		add(sidebar, BorderLayout.LINE_START);
		setVisible(true);
	}
	
	//sidebar 버튼 생성 메서드
	public void sideButton(JPanel panel, String text) {
		JButton button = new JButton(text);
		button.setPreferredSize(new Dimension(150,50));
		panel.add(button);
	}
}

class MyPageFrame extends MainFrame {

	public MyPageFrame() {
		super();
		setTitle("DB2023Team04_BookDetail");
		
		//body 부분
		JPanel body = new JPanel();
		body.setLayout(new BorderLayout());
		body.setBorder(BorderFactory.createEmptyBorder(0 , 5 , 0 , 0));
		
		//회원정보
		JPanel myPage = new JPanel();
		myPage.setLayout(new FlowLayout(FlowLayout.CENTER,0,5));
		JLabel myPageTitle = new JLabel("마이페이지");
		myPageTitle.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		myPageTitle.setPreferredSize(new Dimension(700,50));
		myPage.add(myPageTitle);
		
		myPageLabel(myPage, "이름: ");
		myPageLabel(myPage, "ID: ");
		myPageLabel(myPage, "Email: ");
		myPageLabel(myPage, "대여 중인 책: ");
		myPageLabel(myPage, "작성한 한 줄 평:");
		
		//이전으로 버튼
		JPanel myPageButton = new JPanel();
		myPageButton.setLayout(new FlowLayout(FlowLayout.CENTER,0,130));
		
		JButton prevButton = new JButton("이전으로");
		myPageButton.add(prevButton);
        
        body.add(myPage, BorderLayout.CENTER);
        body.add(myPageButton, BorderLayout.SOUTH);
        
        super.add(body, BorderLayout.CENTER);
	}
	
	public void myPageLabel(JPanel panel, String string) {
		JLabel label = new JLabel(string);
		label.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
		label.setPreferredSize(new Dimension(700,50));
		panel.add(label);
	}
}
