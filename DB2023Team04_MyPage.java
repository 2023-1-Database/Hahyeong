package DB2023;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

//마이페이지
public class DB2023Team04_MyPage {	
	public static void main(String[] args) {
		MyPageFrame MyPage = new MyPageFrame("happy345"); //일단 임시로 userID를 "happy345"로 넣어두었는데, 이 부분은 아인님이 페이지 전환 수정하실 때 편하신대로 수정하시면 될 것 같습니다. 일단 String 타입의 userID를 넘겨주도록 코드를 짰습니다!
	}
}

//메인프레임
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

//마이페이지 프레임
class MyPageFrame extends MainFrame {
	
	public MyPageFrame(String userID) {
		super();
		setTitle("DB2023Team04_BookDetail");
		
		//body 부분
		JPanel body = new JPanel();
		body.setLayout(new BorderLayout());
		body.setBorder(BorderFactory.createEmptyBorder(0 , 5 , 0 , 0));
		
		//회원정보
		JPanel myPage = new JPanel();
		myPage.setLayout(new FlowLayout(FlowLayout.LEFT,15,5));
		JLabel myPageTitle = new JLabel("마이페이지");
		myPageTitle.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		myPageTitle.setPreferredSize(new Dimension(700,50));
		myPage.add(myPageTitle);
		
		//DB연결 및 데이터 불러오기
		DB2023Team04_JDBC db= new DB2023Team04_JDBC(); 
		
		try {
			//PreparedStatement 사용하여 검색 분야와 검색어 지정			
			PreparedStatement psSearch_A = db.connection.prepareStatement("select Member_name, Email from DB2023_Member where Member_ID = ?;");
			psSearch_A.setString(1, userID); //사용자 정보			
			PreparedStatement psSearch_B = db.connection.prepareStatement("select Book_Title, Return_date from mypageView where Member_ID = ?;");
			psSearch_B.setString(1, userID); //사용자가 대여중인 책 정보
			PreparedStatement psSearch_C = db.connection.prepareStatement("select Book_ID, Review from DB2023_Member natural join DB2023_Review where Member_ID = ?;");
			psSearch_C.setString(1, userID); //사용자가 최근 작성한 한줄평
			//결과 저장
			ResultSet rSet_A = psSearch_A.executeQuery();
			ResultSet rSet_B = psSearch_B.executeQuery();
			ResultSet rSet_C = psSearch_C.executeQuery();
			
			while(rSet_A.next()) {		
				myPageLabel(myPage, "이름: " + rSet_A.getString("Member_name"));
				myPageLabel(myPage, "ID: " + userID);
				myPageLabel(myPage, "Email: " + rSet_A.getString("Email"));
			}
			while(rSet_B.next()) {		
				myPageLabel(myPage, "대여중인 책: " + rSet_B.getString("Book_Title") + "(" + rSet_B.getDate("Return_date") + "까지 반납)");
			}
			while(rSet_C.next()) {		
				reviewLabel(myPage, "\"" + rSet_C.getString("Review") + "\"(책 번호: " + rSet_C.getString("Book_ID") + ")");
			}
			
		}
		catch (SQLException sqle) {
			System.out.println("SQLException:" + sqle);
		}
		
		//이전으로 버튼
		JPanel myPageButton = new JPanel();
		myPageButton.setLayout(new FlowLayout(FlowLayout.CENTER,0,100));
		
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
	public void reviewLabel(JPanel panel, String string) {
		JLabel label = new JLabel(string);
		label.setFont(new Font("맑은 고딕", Font.ITALIC, 15));
		panel.add(label);
	}
}