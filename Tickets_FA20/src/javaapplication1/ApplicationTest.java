package javaapplication1;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ApplicationTest {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		Dao obj = new Dao();
		obj.createTables();
		System.out.println(obj.getMonthFromToday());
	}

}
