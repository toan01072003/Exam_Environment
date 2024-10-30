package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.mysql.cj.jdbc.DatabaseMetaData;

public class JDBCUtil {
	public static Connection getConnection() {
 		Connection c = null;
		try {
			
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			
			
			String url = "jdbc:mySQL://localhost:3306/testonlineenvironment";
			String username = "root";
			String password = "123456789";
			
			
			c = DriverManager.getConnection(url, username, password);

		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return c;
	}

	public static void closeConnection(Connection c) {
		if (c != null)
			try {
				c.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public static void printInfor(Connection c) {
		try {
			if (c != null) {
				DatabaseMetaData mtdt = (DatabaseMetaData) c.getMetaData();
				System.out.println(mtdt.getDatabaseProductName());
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

