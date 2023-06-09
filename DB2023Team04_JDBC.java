package DB2023;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DB2023Team04_JDBC {
	
	static Connection connection;
	static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/DB2023Team04?useUnicode=true&serverTimezone=Asia/Seoul";
	static final String USER = "root";
	static final String PASS = "0817";
	
	
	//데이터베이스 연결
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
}
