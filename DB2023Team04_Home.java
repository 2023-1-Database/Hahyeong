import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

//공통된 부분 프레임
class DB2023Team04_MainFrame extends JFrame{
	private Frame frame;
	private JPanel bodyPanel;
	private CardLayout cardLayout;
	private int login = 2; //로그인 여부 및 관리자 여부를 확인하는 변수. 0은 비회원, 1은 회원, 2는 관리자
	private String user_ID = "admin"; //로그인시 사용할 유저 아이디
	private JLabel loginJLabel;
	private JButton loginButton;
	private JButton mypageButton;
	private JPanel searchbody; // 검색 결과에 필요
	
	DB2023Team04_JDBC db= new DB2023Team04_JDBC(); 

	JRadioButton RBCallnum = new JRadioButton("분류 기호");
	JRadioButton RBAuthor = new JRadioButton("작가");
	JRadioButton RBTitle = new JRadioButton("제목", true);
	ButtonGroup radioGroup = new ButtonGroup();
	
	//메인 프레임. cardlayout 사용하여 페이지 간의 이동 구현
	public DB2023Team04_MainFrame() {
		frame = new Frame();
		frame.setSize(1000,750);
		frame.setTitle("DB2023Team04");
		
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		
		bodyPanel = new JPanel();
		cardLayout = new CardLayout();
		bodyPanel.setLayout(cardLayout);		
		
		mainUI(mainPanel);
		
		JPanel Home = new JPanel();
		Home.setLayout(new BorderLayout());
		HomeFrame(Home);
		
		JPanel searchResult = new JPanel();
		searchResult.setLayout(new BorderLayout());
		SearchResultFrame(searchResult);
		
		JPanel login = new JPanel();
		login.setLayout(new BorderLayout());
		LoginFrame(login);
		
		JPanel newBook = new JPanel();
		newBook.setLayout(new BorderLayout());
		NewBookFrame(newBook);
		
		JPanel RequestnewBook = new JPanel();
		RequestnewBook.setLayout(new BorderLayout());
		RequestNewBookFrame(RequestnewBook);

		JPanel Signin = new JPanel();
		Signin.setLayout(new BorderLayout());
		SigninFrame(Signin);

		JPanel myPage = new JPanel();
		myPage.setLayout(new BorderLayout());
		MyPageFrame(myPage);

		JPanel AdminPage = new JPanel();
		AdminPage.setLayout(new BorderLayout());
		AdminPageFrame(AdminPage);
		
		JPanel Book = new JPanel();
		Book.setLayout(new BorderLayout());
		BookFrame(Book);
		
		bodyPanel.add(Home, "Home");
		bodyPanel.add(searchResult, "searchResult");
		bodyPanel.add(login, "login");
		bodyPanel.add(newBook, "newBook");
		bodyPanel.add(RequestnewBook, "RequestnewBook");
		bodyPanel.add(Signin, "Signin");
		bodyPanel.add(myPage, "myPage");
		bodyPanel.add(AdminPage, "AdminPage");
		bodyPanel.add(Book, "Book");
		
		mainPanel.add(bodyPanel);
		
		frame.add(mainPanel);
		frame.setVisible(true);
		
		// 창 종료시 자동 종료
		frame.addWindowListener(new WindowAdapter() {
	        @Override
	         public void windowClosing(WindowEvent e) {
	                     System.exit(0);
	          }
		});
	}

	//공통된 UI 부분 (헤더, sidebar)
	public void mainUI(JPanel panel) {
		//헤더의 타이틀 부분
		JPanel header = new JPanel();
		header.setLayout(new FlowLayout());
		header.setBorder(BorderFactory.createEmptyBorder(10 , 0 , 0 , 0));
		JButton homebutton = new JButton("도비");
		homebutton.setPreferredSize(new Dimension(900,70));
		homebutton.setFont(new Font("맑은 고딕", Font.BOLD, 30));
		homebutton.addActionListener(new TitleListener());
		header.add(homebutton);
		
		
		//sidebar 부분 (로그인, 마이페이지, 신권신청)
		JPanel sidebar = new JPanel();
		sidebar.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 15));
		sidebar.setBorder(BorderFactory.createEmptyBorder(5 , 45 , 0 , 5));	
		sidebar.setPreferredSize(new Dimension(200,600));
		
		//로그인 여부 출력
		loginJLabel = new JLabel("비회원입니다.");
		sidebar.add(loginJLabel);
		
		loginButton = new JButton("로그인");
		JButton signupButton = new JButton("회원 가입");
		JButton newbookButton = new JButton("신권 신청");
		mypageButton = new JButton("마이페이지");
		sideButton(sidebar, loginButton, new LoginpageListener(loginButton, mypageButton));
		sideButton(sidebar, signupButton, new SignupPageListener());
		sideButton(sidebar, mypageButton, new MypagePageListener());
		sideButton(sidebar, newbookButton, new NewBookPageListener());
		
		panel.add(header, BorderLayout.PAGE_START);
		panel.add(sidebar, BorderLayout.LINE_START);
		
	}
	
	//sidebar 버튼 생성 메서드
	public void sideButton(JPanel panel, JButton button, ActionListener listener) {
		button.setPreferredSize(new Dimension(150,50));
		button.addActionListener(listener);
		panel.add(button);
	}
	
	//책 버튼 생성 메서드
	public void BookButton(JPanel panel, String string, String id) {
		JButton button = new JButton(string);
		button.setPreferredSize(new Dimension(700,50));
		button.putClientProperty("Book_ID", id);
		button.addActionListener(new BookClick());
		panel.add(button);
	}
	
	//각 페이지별 프레임 메서드	
	//홈 페이지
	public void HomeFrame(JPanel panel) {
		
		//body 부분(검색, 추천 도서)
		JPanel body = new JPanel();
		body.setLayout(new BorderLayout());
		body.setBorder(BorderFactory.createEmptyBorder(30 , 5 , 0 , 45));
		
		//검색
		JPanel searchArea = new JPanel();		
		searchArea.setLayout(new BorderLayout(0,15));		
		JLabel searchLabel = new JLabel("도서 검색");
		searchLabel.setHorizontalAlignment(JLabel.CENTER);
		searchLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		searchArea.add(searchLabel, BorderLayout.NORTH);
		
		JTextField searchfield = new JTextField(30);
		JButton searchButton = new JButton("Search");
		searchButton.addActionListener(new SearchListener(searchfield));
		JPanel search = new JPanel();
		search.add(searchfield);
		search.add(searchButton);
		searchArea.add(search, BorderLayout.CENTER);
		
		JPanel searchOption = new JPanel();
		
		radioGroup.add(RBCallnum);
		radioGroup.add(RBAuthor);
		radioGroup.add(RBTitle);
		searchOption.add(RBCallnum);
		searchOption.add(RBAuthor);
		searchOption.add(RBTitle);
		searchArea.add(searchOption, BorderLayout.SOUTH);
		
		
		//추천도서
		JPanel recommend = new JPanel();
		recommend.setLayout(new FlowLayout(FlowLayout.CENTER,0,5));
		JLabel recomLabel = new JLabel("추천 도서");
		recomLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		recomLabel.setPreferredSize(new Dimension(700,50));
		recommend.add(recomLabel);
		try {
			//PreparedStatement 사용하여 추천도서 select
			PreparedStatement psSearch_A = db.connection.prepareStatement("select * from DB2023_Book_result where Author like \"박%\";");
			//결과 저장
			ResultSet rSet = psSearch_A.executeQuery();
			
			while(rSet.next()) {
				BookButton(recommend, (rSet.getString("Call_num")+"  |  "+rSet.getString("Author")+"  |  "+ rSet.getString("Book_Title")), rSet.getString("Book_ID"));
			}
		}
		catch (SQLException sqle) {
			System.out.println("SQLException:" + sqle);
		}
		body.add(searchArea, BorderLayout.NORTH);
		body.add(recommend, BorderLayout.CENTER);
		
		panel.add(body);
	}

	//검색 결과 페이지
	public void SearchResultFrame(JPanel panel) {		
		searchbody = new JPanel();
		searchbody.setLayout(new FlowLayout(FlowLayout.LEFT));
		searchbody.setBorder(BorderFactory.createEmptyBorder(0 , 5 , 0 , 45));
		panel.add(searchbody);
	}

	//신권 신청 목록 페이지
	public void NewBookFrame (JPanel panel){
        
    	Object[][] data = new Object[][] {};//창에서 출력할 신권 신청 테이블 정보
    	ResultSet rset;// 테이블 객체
    	DefaultTableModel Model = new DefaultTableModel(data,new String[] {"책 이름","저자","출판사","신청 상태","신청한 회원"});
    	
    	//신권 신청 창 ,  닫는 버튼
    	JTable ltable=new JTable(Model); 
    	JScrollPane jScrollPane=new JScrollPane(ltable);// 스크롤
    	JButton close=new JButton("돌아가기");

    	//패널
    	JPanel newbookPanel = new JPanel();
    	newbookPanel.setLayout(new FlowLayout());
    	JPanel Panel1 = new JPanel();
    	
    	//라벨
    	JLabel newLabel = new JLabel("신권 신청 목록");
    	Font labelFont = newLabel.getFont();
    	newLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20)); // 폰트 크기를 20로 설정
    	newLabel.setPreferredSize(new Dimension(700,55));
    	
    	//신권 신청하기 올리기 버튼
    	JButton newReq= new JButton("신권 신청하기");

    	//신권 신청 목록 
		try {//창에서 신권신청 목록을 출력하기 위한 쿼리
			
			Statement stmt = db.connection.createStatement();
			rset = stmt.executeQuery("select Book_Title, Author,Publisher,Req_Status, Member_ID from DB2023_new_req");//모든 신청 목록 출력
			
			String[] columns=new String[] {"책 이름","저자","출판사","신청 상태","신청한 회원"};
					
			while(rset.next()) {//테이블에 신권 신청 리스트 넣기
		        Model.insertRow(0, new Object[] {rset.getString("Book_Title"),rset.getString("Author"),rset.getString("Publisher"),rset.getString("Req_Status"),rset.getString("Member_ID")," "});
			}
			JScrollPane scroll=new JScrollPane(ltable);
			scroll.setPreferredSize(new Dimension(700,200));
			
			Panel1.add(scroll);
    			
    	}catch(SQLException e) {
    		e.printStackTrace();
    	}
        
        // 글쓰기 버튼
        newReq.addActionListener(new ApplyListener());
        	
        // 홈으로 돌아가기
        close.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		cardLayout.show(frame,"Home");
        	}
        });
        
        // 신권 신청 목록 패널에 라벨 , 버튼 붙이기
        newbookPanel.add(newLabel);
    	newbookPanel.add(Panel1);
        newbookPanel.add(newReq);
        newbookPanel.add(close);
        
        // 신권 신청 목록 패널 붙이기
        panel.add(newbookPanel, BorderLayout.CENTER);    			
	}
	
	// 신권 신청 페이지
	public void RequestNewBookFrame(JPanel panel) {
		
		//전체 패널
		JPanel requestPanel = new JPanel();
		requestPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
		
		// textfield가 붙을 패널
		JPanel northPanel = new JPanel();
		northPanel.setPreferredSize(new Dimension(700,55));
		JPanel center1Panel = new JPanel();
		center1Panel.setPreferredSize(new Dimension(700,55));
		JPanel center2Panel = new JPanel();
		center2Panel.setPreferredSize(new Dimension(700,55));
		
		//버튼 붙을 패널
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

    	JLabel newLabel = new JLabel("신권 신청하기");
    	Font labelFont = newLabel.getFont();
    	newLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20)); // 폰트 크기를 20로 설정
    	newLabel.setPreferredSize(new Dimension(700,55));
    	
		// 라벨
		JLabel titleL = new JLabel("책 제목");
		JLabel authorL = new JLabel(" 저자 ");
		JLabel publisherL = new JLabel("출판사");
		titleL.setPreferredSize(new Dimension(100, 30));
		authorL.setPreferredSize(new Dimension(100, 30));
		publisherL.setPreferredSize(new Dimension(100, 30));
		
		
		// 텍스트필드
		JTextField title = new JTextField();
		JTextField author = new JTextField();
		JTextField publisher = new JTextField();
		
		//신권 신청 버튼
		JButton newbtn = new JButton("신권 신청하기");
		newbtn.addActionListener(new BookRequestListener(title, author, publisher));
		JButton cancel = new JButton("취소");
		cancel.addActionListener(new NewBookPageListener()); 
		
		// 텍스트 상자 크기 조정
		title.setPreferredSize(new Dimension(300, 30));
		author.setPreferredSize(new Dimension(300, 30));
		publisher.setPreferredSize(new Dimension(300, 30));
		
		// 패널에 라벨, 텍스트 필드 붙이기
		northPanel.add(titleL);
		northPanel.add(title);
		center1Panel.add(authorL);
		center1Panel.add(author);
		center2Panel.add(publisherL);
		center2Panel.add(publisher);
		bottomPanel.add(newbtn);
		bottomPanel.add(cancel);
		
		
		// 전체 패널에 패널, 버튼 붙이기
		requestPanel.add(newLabel);
		requestPanel.add(northPanel);
		requestPanel.add(center1Panel);
		requestPanel.add(center2Panel);
		requestPanel.add(bottomPanel);

		panel.add(requestPanel);		
	}
	
	// 로그인 페이지
	public void LoginFrame(JPanel panel) {
		//전체 패널		
		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
		
		// textfield가 붙을 패널
		JPanel northPanel = new JPanel();
		northPanel.setPreferredSize(new Dimension(700,55));
		JPanel centerPanel = new JPanel();
		centerPanel.setPreferredSize(new Dimension(700,55));
		
		//버튼 붙을 패널
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));


    	JLabel newLabel = new JLabel("로그인");
    	newLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20)); // 폰트 크기를 20로 설정
    	newLabel.setPreferredSize(new Dimension(700,55));
    	
    	
		JLabel idLabel = new JLabel(" 아이디 : ");
		idLabel.setPreferredSize(new Dimension(100,30));
		JTextField idTextField = new JTextField(15);
		northPanel.add(idLabel);
		northPanel.add(idTextField);
		
		JLabel passLabel = new JLabel(" 비밀번호 : ");
		passLabel.setPreferredSize(new Dimension(100,30));
		JPasswordField  passTextField  = new JPasswordField(15);
		centerPanel.add(passLabel);
		centerPanel.add(passTextField);

		JButton loginButton = new JButton("로그인");
		loginButton.addActionListener(new LoginListener(idTextField, passTextField));

		JButton signupButton = new JButton("회원가입");
		signupButton.addActionListener(new SignupPageListener());
		
		bottomPanel.add(loginButton);
		bottomPanel.add(signupButton);

		loginPanel.add(newLabel);
		loginPanel.add(northPanel);
		loginPanel.add(centerPanel);
		loginPanel.add(bottomPanel);
		
		panel.add(loginPanel);
	}
	
	//회원 가입 페이지
	public void SigninFrame(JPanel panel) {
		JPanel singinPanel = new JPanel();
		singinPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
		
		// textfield가 붙을 패널
		JPanel panel1 = new JPanel();
		panel1.setPreferredSize(new Dimension(700,55));
		JPanel panel2 = new JPanel();
		panel2.setPreferredSize(new Dimension(700,55));
		JPanel panel3 = new JPanel();
		panel3.setPreferredSize(new Dimension(700,55));
		JPanel panel4 = new JPanel();
		panel4.setPreferredSize(new Dimension(700,55));
		JPanel panel5 = new JPanel();
		panel5.setPreferredSize(new Dimension(700,55));
		JPanel panel6 = new JPanel();
		panel6.setPreferredSize(new Dimension(700,55));

		JLabel idLabel = new JLabel("아이디 : ");
		idLabel.setPreferredSize(new Dimension(100,30));
		JLabel passLabel = new JLabel("비밀번호 : ");
		passLabel.setPreferredSize(new Dimension(100,30));
		JLabel passReLabel = new JLabel("비밀번호 재확인 : ");
		passReLabel.setPreferredSize(new Dimension(100,30));
		JLabel nameLabel = new JLabel("이름 : ");
		nameLabel.setPreferredSize(new Dimension(100,30));
		JLabel emailLabel = new JLabel("이메일 : ");
		emailLabel.setPreferredSize(new Dimension(100,30));

		JTextField idTf = new JTextField(15);
		JPasswordField passTf = new JPasswordField(15);
		JPasswordField passReTf = new JPasswordField(15);
		JTextField nameTf = new JTextField(15);
		JTextField emailTf = new JTextField(15);

		panel1.add(idLabel);
		panel1.add(idTf);
		panel2.add(passLabel);
		panel2.add(passTf);
		panel3.add(passReLabel);
		panel3.add(passReTf);
		panel4.add(nameLabel);
		panel4.add(nameTf);
		panel5.add(emailLabel);
		panel5.add(emailTf);

		JLabel signupLabel = new JLabel("회원가입");
		signupLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		signupLabel.setPreferredSize(new Dimension(700,55));

		JButton registerButton = new JButton("회원가입");
		registerButton.addActionListener(new RegisterListener(idTf, passTf, passReTf, nameTf, emailTf));
		panel6.add(registerButton);

		singinPanel.add(signupLabel);
		singinPanel.add(panel1);
		singinPanel.add(panel2);
		singinPanel.add(panel3);
		singinPanel.add(panel4);
		singinPanel.add(panel5);
		singinPanel.add(panel6);
		
		panel.add(singinPanel);
	}
	
	//마이 페이지
	JPanel mypagepanel;
	public void MyPageFrame(JPanel panel) {
		mypagepanel = new JPanel();
		mypagepanel.setLayout(new BorderLayout());
		mypagepanel.setBorder(BorderFactory.createEmptyBorder(0 , 5 , 0 , 0));
		mypagepanel.setBounds(getBounds());
		
		//회원정보
		JPanel myPage = new JPanel();
		myPage.setLayout(new FlowLayout(FlowLayout.LEFT,15,5));
		JLabel myPageTitle = new JLabel("마이페이지");
		myPageTitle.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		myPageTitle.setPreferredSize(new Dimension(700,50));
		myPage.add(myPageTitle);
		
		try {
			//PreparedStatement 사용하여 검색 분야와 검색어 지정			
			PreparedStatement psSearch_A = db.connection.prepareStatement("select Member_name, Email from DB2023_Member where Member_ID = ?;");
			psSearch_A.setString(1, user_ID); //사용자 정보			
			PreparedStatement psSearch_B = db.connection.prepareStatement("select Book_Title, Return_date from mypageView where Member_ID = ?;");
			psSearch_B.setString(1, user_ID); //사용자가 대여중인 책 정보
			PreparedStatement psSearch_C = db.connection.prepareStatement("select Book_ID, Review from DB2023_Member natural join DB2023_Review where Member_ID = ?;");
			psSearch_C.setString(1, user_ID); //사용자가 최근 작성한 한줄평
			//결과 저장
			ResultSet rSet_A = psSearch_A.executeQuery();
			ResultSet rSet_B = psSearch_B.executeQuery();
			ResultSet rSet_C = psSearch_C.executeQuery();
			
			while(rSet_A.next()) {		
				myPageLabel(myPage, "이름: " + rSet_A.getString("Member_name"));
				myPageLabel(myPage, "ID: " + user_ID);
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
		
		JButton prevButton = new JButton("홈으로");
		prevButton.addActionListener(new TitleListener());
		myPageButton.add(prevButton);
        
		mypagepanel.add(myPage, BorderLayout.CENTER);
		mypagepanel.add(myPageButton, BorderLayout.SOUTH);

		panel.add(mypagepanel);
	}
	
	//마이 페이지에서 사용할 Label
	public void myPageLabel(JPanel panel, String string) {
		JLabel label = new JLabel(string);
		label.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
		label.setPreferredSize(new Dimension(700,50));
		panel.add(label);
	}
	
	//마이 페이지에서 사용할 Label2
	public void reviewLabel(JPanel panel, String string) {
		JLabel label = new JLabel(string);
		label.setFont(new Font("맑은 고딕", Font.ITALIC, 15));
		panel.add(label);
	}
	
	//관리자 페이지
	public void AdminPageFrame(JPanel panel) {
		
		JPanel adminPanel = new JPanel(new BorderLayout());
		
		JPanel mainPanel;//다른 것들을 붙일 메인 판넬
		JPanel bttPanel;//버튼들을 붙일 판넬
		JTextArea bookTextArea;//여기에 책 목록을 보여줌
		JScrollPane scrollPane;//책목록 영역의 스크롤바.
		JTextField Book_idTf;//책id
		JTextField Call_numTf;//분류기호
		JTextField Book_numTf;//도서기호
		JTextField Book_TitleTf;//책제목
		JTextField AuthorTf;//저자
		JTextField PublisherTf;//출판사
		JTextField Pub_yearTf;//발행년도
		JTextField Book_regTf;//등록일
		JPanel subPanel;//텍스트필드들을 붙일 판넬
		//책 정보 라벨들과 텍스트필드들 각각의 판넬
		JPanel Book_idPanel;
		JPanel Call_numPanel;
		JPanel Book_numPanel;
		JPanel Book_TitlePanel;
		JPanel AuthorPanel;
		JPanel PublisherPanel;
		JPanel Pub_yearPanel;
		JPanel Book_regPanel;
		
		String Book_id="", Call_num="", Book_num="", Book_Title="", Author="", Publisher="", Pub_year="", Book_reg="";
		
		
		//메인판넬 선언, 레이아웃 지정
		mainPanel=new JPanel();
		mainPanel.setLayout(new GridLayout(4,1));
		
		//머릿말 판넬 선언, 글자 넣기
		JPanel centerPanel=new JPanel();
		JLabel titleLabel=new JLabel("책 정보 수정");
		titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		titleLabel.setPreferredSize(new Dimension(750,50));
		centerPanel.add(titleLabel);
		
		//책목록 텍스트필드
		bookTextArea = new JTextArea();
		bookTextArea.setEditable(false);
		
		//책목록 텍스트필드의 스크롤바 선언
		scrollPane=new JScrollPane(bookTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		//책 정보들을 입력하는 텍스트필드들이 들어갈 서브판넬 선언
		subPanel = new JPanel();
		subPanel.setLayout(new GridLayout(4,2));
		
		//책 정보들을 입력하는 각각의 텍스트필드 옆에 들어갈 가이드 텍스트
		JLabel Book_idLabel = new JLabel("책ID : ");
		JLabel Call_numLabel = new JLabel("분류기호 : ");
		JLabel Book_numLabel = new JLabel("도서기호 : ");
		JLabel Book_TitleLabel = new JLabel("책제목 : ");
		JLabel AuthorLabel = new JLabel("저자 : ");
		JLabel PublisherLabel = new JLabel("출판사 : ");
		JLabel Pub_yearLabel = new JLabel("발행년도 : ");
		JLabel Book_regLabel = new JLabel("등록일 : ");
		
		//텍스트필드 선언
		Book_idTf = new JTextField(15);
		Call_numTf = new JTextField(15);
		Book_numTf = new JTextField(15);
		Book_TitleTf = new JTextField(11);
		AuthorTf = new JTextField(11);
		PublisherTf = new JTextField(11);
		Pub_yearTf = new JTextField(11);
		Book_regTf = new JTextField(11);
		
		//아이디판넬
		Book_idPanel=new JPanel();
		Book_idPanel.setLayout(new FlowLayout());
		//아이디라벨 
		Book_idPanel.add(Book_idLabel);
		//아이디텍스트필드
		Book_idPanel.add(Book_idTf);
		//서브판넬에 붙이기
		subPanel.add(Book_idPanel);
		
		//분류기호판넬
		Call_numPanel=new JPanel();
		Call_numPanel.setLayout(new FlowLayout());
		//분류기호라벨 
		Call_numPanel.add(Call_numLabel);
		//분류기호텍스트필드
		Call_numPanel.add(Call_numTf);
		//서브판넬에 붙이기
		subPanel.add(Call_numPanel);
		
		//도서기호판넬
		Book_numPanel=new JPanel();
		Book_numPanel.setLayout(new FlowLayout());
		//도서기호라벨 
		Book_numPanel.add(Book_numLabel);
		//도서기호텍스트필드
		Book_numPanel.add(Book_numTf);
		//서브판넬에 붙이기
		subPanel.add(Book_numPanel);
		
		//책제목판넬
		Book_TitlePanel=new JPanel();
		Book_TitlePanel.setLayout(new FlowLayout());
		//책제목라벨 
		Book_TitlePanel.add(Book_TitleLabel);
		//책제목텍스트필드
		Book_TitlePanel.add(Book_TitleTf);
		//서브판넬에 붙이기
		subPanel.add(Book_TitlePanel);

		//저자판넬
		AuthorPanel=new JPanel();
		AuthorPanel.setLayout(new FlowLayout());
		//저자라벨 
		AuthorPanel.add(AuthorLabel);
		//저자텍스트필드
		AuthorPanel.add(AuthorTf);
		//서브판넬에 붙이기
		subPanel.add(AuthorPanel);
		
		//출판사판넬
		PublisherPanel=new JPanel();
		PublisherPanel.setLayout(new FlowLayout());
		//출판사라벨 
		PublisherPanel.add(PublisherLabel);
		//출판사텍스트필드
		PublisherPanel.add(PublisherTf);
		//서브판넬에 붙이기
		subPanel.add(PublisherPanel);
		
		//출판년도판넬
		Pub_yearPanel=new JPanel();
		Pub_yearPanel.setLayout(new FlowLayout());
		//출판년도라벨 
		Pub_yearPanel.add(Pub_yearLabel);
		//출판년도텍스트필드
		Pub_yearPanel.add(Pub_yearTf);
		//서브판넬에 붙이기
		subPanel.add(Pub_yearPanel);
		
		//분류기호판넬
		Book_regPanel=new JPanel();
		Book_regPanel.setLayout(new FlowLayout());
		//분류기호라벨 
		Book_regPanel.add(Book_regLabel);
		//분류기호텍스트필드
		Book_regPanel.add(Book_regTf);
		//서브판넬에 붙이기
		subPanel.add(Book_regPanel);
		
		//버튼들을 붙일 판넬
		bttPanel=new JPanel();
		bttPanel.setLayout(new FlowLayout());
		
		//수정버튼
		JButton udtButton = new JButton("수정");
		udtButton.addActionListener(new AdminEditListener(Book_idTf, Call_numTf, Book_numTf, Book_TitleTf, AuthorTf, PublisherTf, Pub_yearTf, Book_regTf));
		bttPanel.add(udtButton);
		
		//삭제버튼
		JButton delButton = new JButton("삭제");
		delButton.addActionListener(new AdminDeleteListener(Book_idTf));
		bttPanel.add(delButton);
		
		//신권추가버튼
		JButton addButton = new JButton("신권 추가");
		addButton.addActionListener(new AdminNewBookListener(Book_idTf, Call_numTf, Book_numTf, Book_TitleTf, AuthorTf, PublisherTf, Pub_yearTf, Book_regTf));
		bttPanel.add(addButton);
		
		//조회버튼
		JButton checkButton= new JButton("책 정보 조회");
		checkButton.addActionListener(new AdminCheckListener(bookTextArea));
		bttPanel.add(checkButton);
		
		//메인판넬에 판넬들 붙이기		
		mainPanel.add(bttPanel);
		mainPanel.add(subPanel);
		mainPanel.add(scrollPane);	

		panel.add(centerPanel, BorderLayout.NORTH);
		panel.add(mainPanel, BorderLayout.CENTER);
		
	}
	
	//책 상세 페이지
	JPanel bookPanel;
	public void BookFrame(JPanel panel) {
		bookPanel = new JPanel();
		JPanel body = new JPanel();
		body.setLayout(new BorderLayout());
		body.setBorder(BorderFactory.createEmptyBorder(0 , 5 , 0 , 0));
		panel.add(bookPanel);
	}
	
	//책 상세 페이지에서 출력할 Label
	public void detailLabel(JPanel panel, String string) {
		JLabel label = new JLabel(string);
		label.setFont(new Font("맑은 고딕", Font.PLAIN, 15));
		label.setPreferredSize(new Dimension(700,50));
		panel.add(label);
	}
	
	
	// button listener
	//검색 버튼
	class SearchListener implements ActionListener{
		JTextField text;
		String target;
		String targetoutput;
		
		public SearchListener(JTextField text) {
			this.text = text;
		}
	
		public void actionPerformed(ActionEvent e) {		
			if(RBCallnum.isSelected()) {
				target = "Call_num";	
				targetoutput = "분류 기호";
			}
			else if (RBAuthor.isSelected()) {
				target = "Author";
				targetoutput = "작가";
			}
			else if (RBTitle.isSelected()) {
				target = "Book_Title";
				targetoutput = "제목";
			}
			
			searchbody.removeAll();
			
			JLabel resultcondition = new JLabel("검색 결과 : 로 검색한 상위 10개의 결과입니다.");
			resultcondition.setFont(new Font("맑은 고딕", Font.BOLD, 20)); // 폰트 크기를 20로 설정
			resultcondition.setPreferredSize(new Dimension(700,55));
			searchbody.add(resultcondition);
			
			try {
				//PreparedStatement 사용하여 검색 분야와 검색어 지정
				PreparedStatement psSearch_A = db.connection.prepareStatement("select * from DB2023_Book_result where " + target + " like ?;");
				psSearch_A.setString(1, "%"+text.getText()+"%");
				//결과 저장
				ResultSet rSet = psSearch_A.executeQuery();
				
				while(rSet.next()) {
					BookButton(searchbody, (rSet.getString("Call_num")+"  |  "+rSet.getString("Author")+"  |  "+ rSet.getString("Book_Title")), rSet.getString("Book_ID"));
				}
			}
			catch (SQLException sqle) {
				System.out.println("SQLException:" + sqle);
			}
			resultcondition.setText("검색 결과 : "+ targetoutput + ", " + text.getText()+" (으)로 검색한 상위 10개의 결과입니다.");
			text.setText("");
			cardLayout.show(bodyPanel,"searchResult");
		}
	}

	//신권 신청 버튼
	class BookRequestListener implements ActionListener {
		String sql = "insert into DB2023_new_req values (?,?,?,?,?,?)";
		String title = "";
		String author = "";
		String publisher = "";
		
		public BookRequestListener(JTextField title, JTextField author, JTextField publisher) {
			this.title = title.getText();
			this.author = author.getText();
			this.publisher = publisher.getText();
		}
		
		public void actionPerformed(ActionEvent e) {
			// 확인 다이얼로그 창
			int result = JOptionPane.showConfirmDialog(null, "이대로 신청하시겠습니까?", "Confirm", JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.CLOSED_OPTION) 
				; // 사용자가 "예", "아니오"의 선택 없이 다이얼로그 창을 닫은 경우
			else if(result == JOptionPane.YES_OPTION) {
				try { // ps에 값 저장
					db.connection.setAutoCommit(false);
					PreparedStatement ps = db.connection.prepareStatement(sql);
					Statement stmt = db.connection.createStatement();
					ResultSet rset = stmt.executeQuery("select max(Req_num) from DB2023_new_req");
					while(rset.next()) {
						ps.setInt(1, (rset.getInt(1)+1));
					}
					ps.setString(2, title);
					ps.setString(3, author);
					ps.setString(4, publisher);
					ps.setString(5, "신청접수");
					ps.setString(6, user_ID);
					ps.executeUpdate();
					db.connection.commit();
					System.out.println("등록 완료");
					db.connection.setAutoCommit(true);
					System.out.println("setAutoCommit = true");
				}catch(SQLException e1) {
					e1.printStackTrace();
					try {
						if(db.connection!=null)
							db.connection.rollback();
					}catch(SQLException se) {
						se.printStackTrace();
					}
				}
				cardLayout.show(bodyPanel,"newBook");					
			}// 사용자가 "예"를 선택한 경우
			else 
				;// 사용자가 "아니오"를 선택한 경우
		}					
	}
	
	//로그인 버튼
	class LoginListener implements ActionListener{
		JTextField idField;
		JPasswordField passwordField;
		
		public LoginListener(JTextField idField, JPasswordField passwordField) {
			this.idField = idField;
			this.passwordField = passwordField;
		}
		
		public void actionPerformed(ActionEvent e) {
			
			try {
				String id = idField.getText();
				String pass = String.valueOf(passwordField.getPassword());
				
				PreparedStatement sql_query = db.connection.prepareStatement("SELECT password FROM DB2023_Member WHERE Member_ID = ? AND Password = ?");
				sql_query.setString(1, id);
				sql_query.setString(2, pass);
				ResultSet rset = sql_query.executeQuery();
				
				if(rset.next()) {
					if (pass.equals(rset.getString(1))) {
						JOptionPane.showMessageDialog(null, "Login Success", "로그인 성공", 1);
						user_ID = id;
						if(user_ID.equals("admin")) {
							login = 2;
							mypageButton.setText("관리자 페이지");
						}
						else {
							login = 1;
						}
						loginJLabel.setText(id+"님 환영합니다.");
						loginButton.setText("로그아웃");	
						cardLayout.show(bodyPanel,"Home");
					}
				} 
				else
					JOptionPane.showMessageDialog(null, "Login Failed", "로그인 실패", 1);	
				idField.setText("");
				passwordField.setText("");
				
			} catch (SQLException ex) {
				JOptionPane.showMessageDialog(null, "Login Failed", "로그인 실패", 1);
				System.out.println("SQLException" + ex);
			}
		}
	}		
	
	//회원가입 버튼
	class RegisterListener implements ActionListener{
		JTextField idTF;
		JPasswordField passTF;
		JPasswordField passReTF;
		JTextField nameTF;
		JTextField emailTF;
		
		public RegisterListener(JTextField id, JPasswordField pass, JPasswordField passRe, JTextField name, JTextField email) {
			this.idTF = id;
			this.passTF = pass;
			this.passReTF = passRe;
			this.nameTF = name;
			this.emailTF = email;
		}
		public void actionPerformed(ActionEvent e) {

			String sql = "insert into DB2023_Member values (?,?,?,?)";
			String id = idTF.getText();
			String pass = String.valueOf(passTF.getPassword());
			String passRe = String.valueOf(passReTF.getPassword());;
			String name = nameTF.getText();
			String email = emailTF.getText();
			
			if (!pass.equals(passRe)) {
				JOptionPane.showMessageDialog(null, "비밀번호가 서로 맞지 않습니다", "비밀번호 오류", 1);
			}  else if (id.equals("")) {
				JOptionPane.showMessageDialog(null, "아이디는 비워둘 수 없습니다", "아이디 오류", 1);
			}  else if (pass.equals("")) {
				JOptionPane.showMessageDialog(null, "비밀번호는 비워둘 수 없습니다", "비밀번호 오류", 1);
			}  else if (name.equals("")) {
				JOptionPane.showMessageDialog(null, "이름은 비워둘 수 없습니다", "이름 오류", 1);
			}  else {
				try {
					
					PreparedStatement pstmt = db.connection.prepareStatement(sql);

					pstmt.setString(1, id);
					pstmt.setString(2, pass);
					pstmt.setString(3, name);
					pstmt.setString(4, email);

					int r = pstmt.executeUpdate();
					System.out.println("변경된 row " + r);
					JOptionPane.showMessageDialog(null, "회원 가입 완료!", "회원가입", 1);
					cardLayout.show(bodyPanel,"login");
				} catch (SQLException e1) {
					System.out.println("SQL error" + e1.getMessage());
					if (e1.getMessage().contains("PRIMARY")) {
						JOptionPane.showMessageDialog(null, "아이디 중복!", "아이디 중복 오류", 1);
					} else
						JOptionPane.showMessageDialog(null, "정보를 제대로 입력해주세요!", "오류", 1);
				} // try ,catch
			}
		}
	}

	// 책 상세 정보 버튼
	class BookClick implements ActionListener{
	
		public void actionPerformed(ActionEvent e) {
			bookPanel.removeAll();
			
			JButton button = (JButton) e.getSource();
			String bookID = (String) button.getClientProperty("Book_ID");
			
			//책 상세 정보
			JPanel detail = new JPanel();
			detail.setLayout(new FlowLayout(FlowLayout.CENTER,0,5));
			JLabel detailTitle = new JLabel("도서 상세 정보");
			detailTitle.setFont(new Font("맑은 고딕", Font.BOLD, 20));
			detailTitle.setPreferredSize(new Dimension(700,50));
			bookPanel.add(detailTitle);

			//대여하기&이전으로 버튼 패널
			JPanel detailButton = new JPanel();
			detailButton.setLayout(new FlowLayout());
			detailButton.setPreferredSize(new Dimension(700,50));
			
			//책 상세정보 출력
			try {
				//PreparedStatement 사용하여 검색 분야와 검색어 지정			
				PreparedStatement psSearch_A = db.connection.prepareStatement("select Book_Title, Author, Publisher, Pub_year, Call_num, Book_num from DB2023_Book where Book_ID = ?;");
				psSearch_A.setString(1, bookID); //책 상세 정보	
				PreparedStatement psSearch_B = db.connection.prepareStatement("select Status, Member_ID from DB2023_Book_Status where Book_ID = ?;");
				psSearch_B.setString(1, bookID); //책 상태
				//결과 저장
				ResultSet rSet_A = psSearch_A.executeQuery();
				ResultSet rSet_B = psSearch_B.executeQuery();
				
				while(rSet_A.next()) {		
					detailLabel(bookPanel, "책 제목: " + rSet_A.getString("Book_Title"));
					detailLabel(bookPanel, "저자: " + rSet_A.getString("Author"));
					detailLabel(bookPanel, "출판사: " + rSet_A.getString("Publisher"));
					detailLabel(bookPanel, "출판년도: " + rSet_A.getString("Pub_year"));
					detailLabel(bookPanel, "분류기호: " + rSet_A.getString("Call_num"));
					detailLabel(bookPanel, "도서기호: " + rSet_A.getString("Book_num"));
				}
				while(rSet_B.next()) {
					detailLabel(bookPanel, "대여 상태: " + rSet_B.getString("Status"));
					if(rSet_B.getString("Status").equals("대출가능")) {
						JButton borrowButton = new JButton("대출하기");						
						borrowButton.addActionListener(new BorrowListener(bookID));
						detailButton.add(borrowButton);
					} else if((rSet_B.getString("Status").equals("대출중")) && rSet_B.getString("Member_ID").equals(user_ID)) {
						JButton borrowButton = new JButton("반납하기");
						borrowButton.addActionListener(new ReturnListener(bookID));
						detailButton.add(borrowButton);
					}
				}
			}
			catch (SQLException sqle) {
				System.out.println("SQLException:" + sqle);
			}		
			
			JButton prevButton = new JButton("홈으로");
			prevButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cardLayout.show(bodyPanel,"Home");
				}
			});
			detailButton.add(prevButton);
			
			bookPanel.add(detailButton);
			cardLayout.show(bodyPanel,"Book");
		}	
	}
	
	// 책 대여
	class BorrowListener implements ActionListener{	
		String bookID;
		public BorrowListener(String bookID) {
			this.bookID = bookID;
		}
		public void actionPerformed(ActionEvent e) {
			String sql_book = "update DB2023_Book_Status set Status = ? where Book_ID = ?;";
			String sql_user = "update DB2023_Member set Status = ? where Member_ID = ?;";
			
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
					ps_user.setString(2, user_ID);
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
			    	System.out.println("동작");
			        if(ps_book != null) try{ ps_book.close();} catch (SQLException ex) {}
			        if(ps_user != null) try{ ps_user.close();} catch (SQLException ex){}
			        //if(db.connection != null) try{ db.connection.close();} catch (SQLException ex){}
					
			    }cardLayout.show(bodyPanel,"Home");
			}
			else 
				;// 사용자가 "아니오"를 선택한 경우
		}
	}
	
	class ReturnListener implements ActionListener{
		String bookID;
		public ReturnListener(String bookID) {
			this.bookID = bookID;
		}
		
		public void actionPerformed(ActionEvent e) {
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
					String sql_book = "update DB2023_Book_Status set Status = ? where Book_ID = ?;";
					String sql_user = "update DB2023_Member set Status = ? where Member_ID = ?;";
					
					ps_book = db.connection.prepareStatement(sql_book);
					Statement stmt_book = db.connection.createStatement();
					ps_book.setString(1, "대출가능");
					ps_book.setString(2, bookID);
					ps_book.executeUpdate();
					
					ps_user = db.connection.prepareStatement(sql_user);
					Statement stmt_user = db.connection.createStatement();
					ps_user.setBoolean(1, false);
					ps_user.setString(2, user_ID);
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
			        System.out.println(e1);
			    }finally {
			        if(ps_book != null) try{ ps_book.close();} catch (SQLException ex) {}
			        if(ps_user != null) try{ ps_user.close();} catch (SQLException ex){}
			        //if(db.connection != null) try{ db.connection.close();} catch (SQLException ex){}
			    }
				cardLayout.show(bodyPanel,"Home");
			}
			else 
				;// 사용자가 "아니오"를 선택한 경우	
		}		
	}

	// 관리자 페이지 책 조회
	class AdminCheckListener implements ActionListener{
		JTextArea bookTextArea;
		public AdminCheckListener(JTextArea bookTextArea) {
			this.bookTextArea = bookTextArea;
		}
		public void actionPerformed(ActionEvent e) {
			String sql = "select * from DB2023_Book";
			try {
				//sql문 실행
				Statement stmt = db.connection.createStatement();
				
				//sql 결과를 받아오는 rset 변수
				ResultSet rset=stmt.executeQuery(sql);
				
				//책목록 area를 초기화.
				bookTextArea.setText("");
				
				//각 행에 대하여 책 목록을 textarea에 출력
				while(rset.next()) {
					bookTextArea.append("책 ID: "+rset.getString(1)+", 분류기호: "+rset.getString(2)+", 도서 기호: "+rset.getString(3)+", 책 제목: "+rset.getString(4)+", 저자: "+rset.getString(5)+", 출판사: "+rset.getString(6)+", 발행년도: "+rset.getInt(7)+", 등록일: "+rset.getString(8)+"\n");
				}
				
				System.out.println("책 정보 조회");
				
			}catch(SQLException e1) {
				System.out.println("error");
				JOptionPane.showMessageDialog(null, "조회 불가능!", "책 정보 조회", 1);
			}
		}		
	}
	
	// 관리자 페이지 신권 신청
	class AdminNewBookListener implements ActionListener{
		JTextField Book_idTf;
		JTextField Call_numTf;
		JTextField Book_numTf;
		JTextField Book_TitleTf;
		JTextField AuthorTf;
		JTextField PublisherTf;
		JTextField Pub_yearTf;
		JTextField Book_regTf;
		
		public AdminNewBookListener(JTextField Book_idTf, JTextField Call_numTf, JTextField Book_numTf, JTextField Book_TitleTf, JTextField AuthorTf, JTextField PublisherTf, JTextField Pub_yearTf, JTextField Book_regTf) {
			this.Book_idTf = Book_idTf;
			this.Call_numTf = Call_numTf;
			this.Book_numTf = Book_numTf;
			this.Book_TitleTf = Book_TitleTf;
			this.AuthorTf = AuthorTf;
			this.PublisherTf = PublisherTf;
			this.Pub_yearTf = Pub_yearTf;
			this.Book_regTf = Book_regTf;
		}
		
		
		public void actionPerformed(ActionEvent e) {
			
			//사용자가 입력한 텍스트 변수에 저장
			String Book_id= Book_idTf.getText();
			String Call_num= Call_numTf.getText();
			String Book_num= Book_numTf.getText();
			String Book_Title= Book_TitleTf.getText();
			String Author= AuthorTf.getText();
			String Publisher= PublisherTf.getText();
			String Pub_year= Pub_yearTf.getText();
			String Book_reg=Book_regTf.getText();
			
			//수행할 sql문
			String sql = "insert into DB2023_Book values(?,?,?,?,?,?,?,?)";
			try {				
				//삽입 sql문 실행
				PreparedStatement pstmt=db.connection.prepareStatement(sql);
				pstmt.setString(1,  Book_id);
				pstmt.setString(2, Call_num);
				pstmt.setString(3, Book_num);
				pstmt.setString(4, Book_Title);
				pstmt.setString(5, Author);
				pstmt.setString(6, Publisher);
				pstmt.setString(7, Pub_year);
				pstmt.setString(8, Book_reg);
				pstmt.executeUpdate();
				
				System.out.println("신권추가 ");
				JOptionPane.showMessageDialog(null, "추가 완료!", "신권 정보 추가", 1);
				
			}catch(SQLException e1) {
				System.out.println("error");
				JOptionPane.showMessageDialog(null, "추가 불가능!", "신권 정보 추가", 1);
			}
			Book_idTf.setText("");
			Call_numTf.setText("");
			Book_numTf.setText("");
			Book_TitleTf.setText("");
			AuthorTf.setText("");
			PublisherTf.setText("");
			Pub_yearTf.setText("");
			Book_regTf.setText("");
		}
	}
	
	// 관리자 페이지 책 삭제
	class AdminDeleteListener implements ActionListener{
		JTextField Book_idTF;
		public AdminDeleteListener(JTextField Book_idTF) {
			this.Book_idTF = Book_idTF;
		}
		public void actionPerformed(ActionEvent e) {
			//정보를 입력(id만 입력해도 됨) 후 삭제 버튼을 누르면			
			//사용자로부터 텍스트 받아와서 저장
			String Book_id=Book_idTF.getText();
			
			//수행할 sql문장
			String sql="delete from DB2023_Book where Book_ID=?";
			try {
				//삭제 sql문 실행
				PreparedStatement pstmt=db.connection.prepareStatement(sql);
				pstmt.setString(1, Book_id);
				pstmt.executeUpdate();
				
				System.out.println("삭제");
				JOptionPane.showMessageDialog(null, "삭제 완료!", "책 정보 삭제", 1);
				
			}catch(SQLException e1) {
				System.out.println("error");
			}
			Book_idTF.setText("");
		}
	}
	
	// 관리자 페이지 책 수정
	class AdminEditListener implements ActionListener{
		JTextField Book_idTf;
		JTextField Call_numTf;
		JTextField Book_numTf;
		JTextField Book_TitleTf;
		JTextField AuthorTf;
		JTextField PublisherTf;
		JTextField Pub_yearTf;
		JTextField Book_regTf;
		
		public AdminEditListener(JTextField Book_idTf, JTextField Call_numTf, JTextField Book_numTf, JTextField Book_TitleTf, JTextField AuthorTf, JTextField PublisherTf, JTextField Pub_yearTf, JTextField Book_regTf) {
			this.Book_idTf = Book_idTf;
			this.Call_numTf = Call_numTf;
			this.Book_numTf = Book_numTf;
			this.Book_TitleTf = Book_TitleTf;
			this.AuthorTf = AuthorTf;
			this.PublisherTf = PublisherTf;
			this.Pub_yearTf = Pub_yearTf;
			this.Book_regTf = Book_regTf;
		}
		
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			
			//사용자로부터 텍스트 받아와서 변수에 저장
			String Book_id=Book_idTf.getText();
			String Call_num=Call_numTf.getText();
			String Book_num=Book_numTf.getText();
			String Book_Title=Book_TitleTf.getText();
			String Author=AuthorTf.getText();
			String Publisher=PublisherTf.getText();
			String Pub_year=Pub_yearTf.getText();
			String Book_reg=Book_regTf.getText();
			
			//수행할 sql문장
			String sql = "update DB2023_Book set Book_ID=?, Call_num=?, Book_num=?, Book_Title=?, Author=?, Publisher=?, Pub_year=?, Book_register=? where Book_ID=?";	
			try {
				//수정 sql문 실행
				PreparedStatement pstmt=db.connection.prepareStatement(sql);
				pstmt.setString(1, Book_id);
				pstmt.setString(2, Call_num);
				pstmt.setString(3, Book_num);
				pstmt.setString(4, Book_Title);
				pstmt.setString(5, Author);
				pstmt.setString(6, Publisher);
				pstmt.setString(7, Pub_year);
				pstmt.setString(8, Book_reg);
				pstmt.setString(9, Book_id);
				pstmt.executeUpdate();
				
				System.out.println("업데이트 ");
				JOptionPane.showMessageDialog(null, "수정 완료!", "책 정보 수정", 1);
				
			}catch(SQLException e1) {
				System.out.println("error");
				JOptionPane.showMessageDialog(null, "수정 불가!", "책 정보 수정", 1);
			}
			Book_idTf.setText("");
			Call_numTf.setText("");
			Book_numTf.setText("");
			Book_TitleTf.setText("");
			AuthorTf.setText("");
			PublisherTf.setText("");
			Pub_yearTf.setText("");
			Book_regTf.setText("");
		}
	}
	
	//타이틀로 이동하는 버튼
	class TitleListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			cardLayout.show(bodyPanel,"Home");
		}
	}
	
	//로그인 페이지로 이동하는 버튼
	class LoginpageListener implements ActionListener{
		JButton loginButton;
		JButton mypageButton;
		
		public LoginpageListener(JButton loginButton, JButton mypageButton) {
			this.loginButton = loginButton;
			this.mypageButton = mypageButton;
		}
		
		public void actionPerformed(ActionEvent e) {
			
			switch (login) {
			case 0:
				cardLayout.show(bodyPanel,"login");
				break;
			
			case 1:
			case 2:
				//로그아웃
				user_ID = "";
				login = 0;
				loginButton.setText("로그인");
				mypageButton.setText("마이페이지");
				loginJLabel.setText("비회원입니다.");
				mypagepanel.removeAll();
				JOptionPane.showMessageDialog(null, "로그아웃 되었습니다.", "Logout", 1);
				break;
			default:
				break;
			}			
		}
	}
		
	//회원가입 페이지로 이동하는 버튼
	class SignupPageListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			cardLayout.show(bodyPanel,"Signin");
		}	
	}
	
	//마이페이지 또는 관리자 페이지로 이동하는 버튼
	class MypagePageListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			if(login == 1) {

				//회원정보
				JPanel myPage = new JPanel();
				myPage.setLayout(new FlowLayout(FlowLayout.LEFT,15,5));
				JLabel myPageTitle = new JLabel("마이페이지");
				myPageTitle.setFont(new Font("맑은 고딕", Font.BOLD, 20));
				myPageTitle.setPreferredSize(new Dimension(700,50));
				myPage.add(myPageTitle);
				
				try {
					//PreparedStatement 사용하여 검색 분야와 검색어 지정			
					PreparedStatement psSearch_A = db.connection.prepareStatement("select Member_name, Email from DB2023_Member where Member_ID = ?;");
					psSearch_A.setString(1, user_ID); //사용자 정보			
					PreparedStatement psSearch_B = db.connection.prepareStatement("select Book_Title, Return_date from mypageView where Member_ID = ?;");
					psSearch_B.setString(1, user_ID); //사용자가 대여중인 책 정보
					PreparedStatement psSearch_C = db.connection.prepareStatement("select Book_ID, Review from DB2023_Member natural join DB2023_Review where Member_ID = ?;");
					psSearch_C.setString(1, user_ID); //사용자가 최근 작성한 한줄평
					//결과 저장
					ResultSet rSet_A = psSearch_A.executeQuery();
					ResultSet rSet_B = psSearch_B.executeQuery();
					ResultSet rSet_C = psSearch_C.executeQuery();
					
					while(rSet_A.next()) {		
						myPageLabel(myPage, "이름: " + rSet_A.getString("Member_name"));
						myPageLabel(myPage, "ID: " + user_ID);
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
				
				JButton prevButton = new JButton("돌아가기");
				myPageButton.add(prevButton);
		        
				mypagepanel.add(myPage, BorderLayout.CENTER);
				mypagepanel.add(myPageButton, BorderLayout.SOUTH);
				cardLayout.show(bodyPanel,"myPage");			
			}			
			else if(login == 2) {
				cardLayout.show(bodyPanel,"AdminPage");
			}
			else {
				cardLayout.show(bodyPanel,"login");				
			}
		}		
	}
	
	//신권 신청 목록 페이지로 이동하는 버튼	
	class NewBookPageListener implements ActionListener{
		
		public void actionPerformed(ActionEvent e) {
			if(login == 0) {
				cardLayout.show(bodyPanel,"login");
			}
			else 
				cardLayout.show(bodyPanel,"newBook");
		}		
	}
	
	//신권 신청 페이지로 이동하는 버튼
	class ApplyListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			cardLayout.show(bodyPanel,"RequestnewBook");
		}
    }	
}

class DB2023Team04_JDBC {	
	static Connection connection;
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/DB2023Team04?useUnicode=true&serverTimezone=Asia/Seoul";
	static final String USER = "root";
	static final String PASS = "0817";
	
	//데이터베이스와 연결
	public DB2023Team04_JDBC() {
		try{
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			Statement stmt = connection.createStatement();
			System.out.println("Mysql 서버 연동 성공");			
		}
		catch(SQLException sqle) {
			System.out.println("SQLException:" + sqle);
		}
	}	
	// 추천 도서
}

public class DB2023Team04_Home {
	
	public static void main(String[] args) {
		//DB2023Team04_JDBC jdbc = new DB2023Team04_JDBC();
		DB2023Team04_MainFrame mainFrame = new DB2023Team04_MainFrame();
	}
}