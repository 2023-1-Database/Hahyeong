package DB2023;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

//책 상세 페이지
public class DB2023Team04_BookDetail {	
	public static void main(String[] args) {
		BookDetailFrame BookDetail = new BookDetailFrame("001", "happy345"); //일단 임시로 bookID는 "001", userID는 "happy345"로 넣어두었는데, 이 부분은 아인님이 페이지 전환 수정하실 때 편하신대로 수정하시면 될 것 같습니다. 일단 String 타입의 bookID와 userID를 넘겨주도록 코드를 짰습니다!
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

//책 상세 프레임
class BookDetailFrame extends MainFrame {

	public BookDetailFrame(String bookID, String userID) {
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
		
		//대여하기&이전으로 버튼 패널
		JPanel detailButton = new JPanel();
		detailButton.setLayout(new FlowLayout(FlowLayout.CENTER,20,50));
		
		//DB연결 및 데이터 불러오기
		DB2023Team04_JDBC db= new DB2023Team04_JDBC(); 
		
		//책 상세정보 출력
		try {
			//PreparedStatement 사용하여 검색 분야와 검색어 지정			
			PreparedStatement psSearch_A = db.connection.prepareStatement("select Book_Title, Author, Publisher, Pub_year, Call_num, Book_num from DB2023_Book where Book_ID = ?;");
			psSearch_A.setString(1, bookID); //책 상세 정보	
			PreparedStatement psSearch_B = db.connection.prepareStatement("select Status from DB2023_Book_Status where Book_ID = ?;");
			psSearch_B.setString(1, bookID); //책 상태
			PreparedStatement ps_borrow1 = db.connection.prepareStatement("update DB2023_Book_Status set Status = \"" + "대출중" + "\" where Book_ID = ?;");
			ps_borrow1.setString(1, bookID); //책 상세 정보	
			PreparedStatement ps_borrow2 = db.connection.prepareStatement("update DB2023_Book_Status set Status = \"" + "대출가능" + "\" where Book_ID = ?;");
			ps_borrow2.setString(1, bookID); //책 상태
			//결과 저장
			ResultSet rSet_A = psSearch_A.executeQuery();
			ResultSet rSet_B = psSearch_B.executeQuery();
			
			while(rSet_A.next()) {		
				detailLabel(detail, "책 제목: " + rSet_A.getString("Book_Title"));
				detailLabel(detail, "저자: " + rSet_A.getString("Author"));
				detailLabel(detail, "출판사: " + rSet_A.getString("Publisher"));
				detailLabel(detail, "출판년도: " + rSet_A.getString("Pub_year"));
				detailLabel(detail, "분류기호: " + rSet_A.getString("Call_num"));
				detailLabel(detail, "도서기호: " + rSet_A.getString("Book_num"));
			}
			while(rSet_B.next()) {
				detailLabel(detail, "대여 상태: " + rSet_B.getString("Status"));
				String sql_book = "update DB2023_Book_Status set Status = ? where Book_ID = ?;";
				String sql_user = "update DB2023_Member set Status = ? where Member_ID = ?;";
				if(rSet_B.getString("Status").equals("대출가능")) {
					JButton borrowButton = new JButton("대출하기");
					detailButton.add(borrowButton);
					borrowButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
								// 확인 다이얼로그 창
								int result = JOptionPane.showConfirmDialog(null, "책을 대출하시겠습니까?", "Confirm", JOptionPane.YES_NO_OPTION);
								if(result == JOptionPane.CLOSED_OPTION) 
									; // 사용자가 "예", "아니오"의 선택 없이 다이얼로그 창을 닫은 경우
								else if(result == JOptionPane.YES_OPTION) { //"예"를 선택한 경우
									PreparedStatement ps_book = null;
									PreparedStatement ps_user = null;
									
									try { 
										//트랜잭션 시작
										db.connection.setAutoCommit(false);
										
										ps_book = db.connection.prepareStatement(sql_book);
										Statement stmt_book = db.connection.createStatement();
										ps_book.setString(1, "대출중");
										ps_book.setString(2, bookID);
										ps_book.executeUpdate();
										
										ps_user = db.connection.prepareStatement(sql_user);
										Statement stmt_user = db.connection.createStatement();
										ps_user.setBoolean(1, true);
										ps_user.setString(2, userID);
										ps_user.executeUpdate();		

										db.connection.commit(); //트랜잭션 커밋
										db.connection.setAutoCommit(true);
									} catch (Throwable e1){
								        if(db.connection!= null){
								            try{
								                db.connection.rollback();
								            }catch (SQLException ex){}
								        }
								        Throwable occuredException = e1;
								    }finally {
								        if(ps_book != null) try{ ps_book.close();} catch (SQLException ex) {}
								        if(ps_user != null) try{ ps_user.close();} catch (SQLException ex){}
								        if(db.connection != null) try{ db.connection.close();} catch (SQLException ex){}
								    }
									new BookDetailFrame(bookID, userID);
									dispose();
								}
								else 
									;// 사용자가 "아니오"를 선택한 경우
										
						}
								
					});
				} else if(rSet_B.getString("Status").equals("대출중")) {
					JButton borrowButton = new JButton("반납하기");
					detailButton.add(borrowButton);
					borrowButton.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
								// 확인 다이얼로그 창
								int result = JOptionPane.showConfirmDialog(null, "책을 반납하시겠습니까?", "Confirm", JOptionPane.YES_NO_OPTION);
								if(result == JOptionPane.CLOSED_OPTION) 
									; // 사용자가 "예", "아니오"의 선택 없이 다이얼로그 창을 닫은 경우
								else if(result == JOptionPane.YES_OPTION) { //"예"를 선택한 경우
									PreparedStatement ps_book = null;
									PreparedStatement ps_user = null;
									
									try { 
										//트랜잭션 시작
										db.connection.setAutoCommit(false);
										
										ps_book = db.connection.prepareStatement(sql_book);
										Statement stmt_book = db.connection.createStatement();
										ps_book.setString(1, "대출가능");
										ps_book.setString(2, bookID);
										ps_book.executeUpdate();
										
										ps_user = db.connection.prepareStatement(sql_user);
										Statement stmt_user = db.connection.createStatement();
										ps_user.setBoolean(1, false);
										ps_user.setString(2, userID);
										ps_user.executeUpdate();		

										db.connection.commit(); //트랜잭션 커밋
										db.connection.setAutoCommit(true);
									} catch (Throwable e1){
								        if(db.connection!= null){
								            try{
								                db.connection.rollback();
								            }catch (SQLException ex){}
								        }
								        Throwable occuredException = e1;
								    }finally {
								        if(ps_book != null) try{ ps_book.close();} catch (SQLException ex) {}
								        if(ps_user != null) try{ ps_user.close();} catch (SQLException ex){}
								        if(db.connection != null) try{ db.connection.close();} catch (SQLException ex){}
								    }
									new BookDetailFrame(bookID, userID);
									dispose();
								}
								else 
									;// 사용자가 "아니오"를 선택한 경우
										
						}
								
					});
				}
			}
		}
		catch (SQLException sqle) {
			System.out.println("SQLException:" + sqle);
		}		
		
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