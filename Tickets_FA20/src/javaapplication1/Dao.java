package javaapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dao {
	// instance fields
	static Connection connect = null;
	Statement statement = null;

	// constructor
	public Dao() {
	  
	}

	public Connection getConnection() {
		// Setup the connection with the DB
		try {
			connect = DriverManager
					.getConnection(ConnectionURL.url);
//			connect = DriverManager
//					.getConnection("jdbc:mysql://localhost/test", "root", "4111");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connect;
	}

	// CRUD implementation

	public void createTables() {
		// variables for SQL Query table creations
		final String createTicketsTable = "CREATE TABLE jgonz_tickets(tid INT AUTO_INCREMENT PRIMARY KEY, ticket_desc VARCHAR(200), ticket_issuer VARCHAR(30), status VARCHAR(10), start_date VARCHAR(10), end_date VARCHAR(10))";
		final String createUsersTable = "CREATE TABLE jgonz_users(uid INT AUTO_INCREMENT PRIMARY KEY, uname VARCHAR(30), upass VARCHAR(30), admin int)";
		final String createLoginsTable = "create table jgonz_logins(uid INT, loginday varchar(20), logintime varchar(20));";
		try {

			// execute queries to create tables

			statement = getConnection().createStatement();
			statement.executeUpdate(createLoginsTable);
			statement.executeUpdate(createTicketsTable);
			statement.executeUpdate(createUsersTable);
			
			System.out.println("Created tables in given database...");

			// end create table
			// close connection/statement object
			statement.close();
			connect.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// add users to user table
//		addUsers();
	}

	public void addUsers() {
		// add list of users from userlist.csv file to users table

		// variables for SQL Query inserts
		String sql;

		Statement statement;
		BufferedReader br;
		List<List<String>> array = new ArrayList<>(); // list to hold (rows & cols)

		// read data from file
		try {
			br = new BufferedReader(new FileReader(new File("./userlist.csv")));

			String line;
			while ((line = br.readLine()) != null) {
				array.add(Arrays.asList(line.split(",")));
			}
		} catch (Exception e) {
			System.out.println("There was a problem loading the file");
		}

		try {

			// Setup the connection with the DB

			statement = getConnection().createStatement();

			// create loop to grab each array index containing a list of values
			// and PASS (insert) that data into your User table
			for (List<String> rowData : array) {

				sql = "insert into jgonz_users(uname,upass,admin) " + "values('" + rowData.get(0) + "'," + " '"
						+ rowData.get(1) + "','" + rowData.get(2) + "');";
				statement.executeUpdate(sql);
			}
			System.out.println("Inserts completed in the given database...");

			// close statement object
			statement.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public int insertRecords(String ticketName, String ticketDesc, String status, String startDate, String endDate) {
		int id = 0;
		try {
			statement = getConnection().createStatement();
			statement.executeUpdate("Insert into jgonz_tickets" + "(ticket_issuer, ticket_desc, status, start_date, end_date) values(" + " '"
					+ ticketName + "','" + ticketDesc + "','" + status + "','" + startDate + "','" + endDate + "');" , Statement.RETURN_GENERATED_KEYS);

			// retrieve ticket id number newly auto generated upon record insertion
			ResultSet resultSet = null;
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				// retrieve first field in table
				id = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;

	}

	public ResultSet readRecords(boolean isAdmin, String userName) {

		ResultSet results = null;
		try {
			statement = connect.createStatement();
			if(isAdmin) {
				results = statement.executeQuery("SELECT * FROM jgonz_tickets");
			} else {
				results = statement.executeQuery("SELECT * FROM jgonz_tickets WHERE ticket_issuer = '" +userName+ "'");
			}
			//connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}

	//update ticket
	public int updateTicket(int ticketID, String ticketDesc, String ticketIssr, String status, String startDate, String endDate) {
		String sql = "update jgonz_tickets set ticket_desc = '" + ticketDesc + "', ticket_issuer = '" +ticketIssr + "', status = '"+ status + "', start_date = '" +startDate+ "', end_date = '" +endDate+"' where tid = "+ticketID+";";
		int returnValue = -1;
		try {
			statement = connect.createStatement();
			returnValue = statement.executeUpdate(sql);
			if (returnValue == 1) {
				System.out.println("Ticket ID " + ticketID + " exists and was modified.");
			} else {
				System.out.println("Error.");
			}
			statement.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return returnValue;

	}
	
	public String[] getTicketData(int ticketID) throws SQLException {
		ResultSet results = null;
		try {
			statement = connect.createStatement();
			results = statement.executeQuery("select * from jgonz_tickets where tid = "+ticketID+";");
			//connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		results.next();
		String[] data = new String[7];
		for (int i=1; i<=6; i++) {
			data[i] = results.getString(i);
		}
		return data;
	}
	
	//delete ticket
	public int deleteTicket(int ticketID) {
		String sql = "delete from jgonz_tickets where tid = "+ticketID+ ";";
		int returnValue = -1;
		try {
			statement = connect.createStatement();
			returnValue = statement.executeUpdate(sql);
			if (returnValue == 1) {
				System.out.println("Ticket ID " + ticketID + " exists and was deleted.");
			} else {
				System.out.println("Ticket ID " + ticketID + " does not exist.");
			}
			statement.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return returnValue;
	}
	
	public String getCurrentDay() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/YYYY");  
		LocalDateTime now = LocalDateTime.now();  
		return dtf.format(now); 
	}
	
	public String getMonthFromToday() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/YYYY");  
		LocalDateTime now = LocalDateTime.now();
		now = now.plusMonths(1);
		return dtf.format(now); 
	}
	
	public String getCurrentDayTime() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/YYYY HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		return dtf.format(now);
	}
	
	public int closeTicket(int ticketID, String userName, boolean isAdmin) {
		if (isAdmin) {
			System.out.println("Ticket closing was requested by an admin.");
		} else {
			System.out.println("Ticket closing was requested by a regular user.");
		}
		String sqlRegular = "update jgonz_tickets set status = 'Closed' where tid = " + ticketID +  " and ticket_issuer = '" + userName + "';";
		String sqlAdmin = "update jgonz_tickets set status = 'Closed' where tid = " + ticketID + ";";
		int returnValue = -1;
		try {
			statement = getConnection().createStatement();
			if (isAdmin) {
				returnValue = statement.executeUpdate(sqlAdmin);
			} else {
				returnValue = statement.executeUpdate(sqlRegular);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(returnValue == 1) {
			System.out.println("Ticket closed successfully.");
		} else {
			System.out.println("Ticket was not closed successfully.");
		}
		
		return returnValue;
	}
	
	public int createUser(String userName, String userPass, String isAdmin) {
		int returnValue = -1;
		try {
			statement = getConnection().createStatement();
			returnValue = statement.executeUpdate("Insert into jgonz_users" + "(uname, upass, admin) values(" + " '"
					+ userName + "','" + userPass + "','" + isAdmin + "');" , Statement.RETURN_GENERATED_KEYS);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (returnValue == 0) {
			System.out.println("User create failed.");
		} else {
			System.out.println("User create success.");
		}
		return returnValue;
	}
	
	public int logTime(int userID) {
		//islogin true = login
		//islogin false = logout
		String timeFull = getCurrentDayTime();
		String sql;
		String day = timeFull.substring(0,10);
		String time = timeFull.substring(11,19);
		
		sql = "insert into jgonz_logins(uid, loginday, logintime) values (" + userID + ", '" + day + "', '" + time + "')";
		
		int returnValue = -1;
		try {
			statement = connect.createStatement();
			returnValue = statement.executeUpdate(sql);
			statement.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return returnValue;
	}
	
	public ResultSet readLogins(int userID) {
		String sqlRegular = "SELECT * FROM jgonz_logins";
		String sqlSearch = "select * from jgonz_logins where uid = " + userID + ";";
		ResultSet results = null;
		try {
			statement = connect.createStatement();
			if(userID == -1) {
				results = statement.executeQuery(sqlRegular);
			} else {
				results = statement.executeQuery(sqlSearch);
			}
			//connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}
	
	public ResultSet getTicketResult(int ticketID, String userName, boolean isAdmin) {
		String sql = "";
		if (isAdmin) {
			sql = "select * from jgonz_tickets where tid = "+ticketID+";";
		} else {
			sql = "select * from jgonz_tickets where tid = "+ticketID+" and ticket_issuer = '" + userName + "';";
		}
		ResultSet results = null;
		try {
			statement = connect.createStatement();
			results = statement.executeQuery(sql);
			//connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}
	
}
