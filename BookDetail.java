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

//책 상세 페이지
public class BookDetail {	
	public static void main(String[] args) {
		BookDetailFrame BookDetail = new BookDetailFrame();
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

class BookDetailFrame extends MainFrame {

	public BookDetailFrame() {
		super();
		setTitle("DB2023Team04_BookDetail");
		
		//body 부분(책 상세 정보, 대여하기/이전으로)
		JPanel body = new JPanel();
		body.setLayout(new BorderLayout());
		body.setBorder(BorderFactory.createEmptyBorder(0 , 5 , 0 , 0));
		
		//책 상세 정보
		JPanel detail = new JPanel();
		detail.setLayout(new FlowLayout(FlowLayout.CENTER,0,5));
		JLabel detailTitle = new JLabel("도서 상세 정보");
		detailTitle.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		detailTitle.setPreferredSize(new Dimension(700,50));
		detail.add(detailTitle);
		
		detailLabel(detail, "책 제목: ");
		detailLabel(detail, "저자: ");
		detailLabel(detail, "출판사: ");
		detailLabel(detail, "출판년도: ");
		detailLabel(detail, "분류기호: ");
		detailLabel(detail, "도서기호: ");
		detailLabel(detail, "대여 가능 여부: "); //여기는 데이터 연결
		
		//대여하기&이전으로 버튼
		JPanel detailButton = new JPanel();
		detailButton.setLayout(new FlowLayout(FlowLayout.CENTER,20,75));
		
		JButton borrowButton = new JButton("대여하기");
		detailButton.add(borrowButton);
		JButton prevButton = new JButton("이전으로");
		detailButton.add(prevButton);
        
        body.add(detail, BorderLayout.CENTER);
        body.add(detailButton, BorderLayout.SOUTH);
        
        super.add(body, BorderLayout.CENTER);
	}
	
	public void detailLabel(JPanel panel, String string) {
		JLabel label = new JLabel(string);
		label.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
		label.setPreferredSize(new Dimension(700,50));
		panel.add(label);
	}
}