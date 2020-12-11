package javaapplication1;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class Tickets extends JFrame implements ActionListener {

	// class level member objects
	Dao dao = new Dao(); // for CRUD operations
	Boolean chkIfAdmin = null;

	private String userName = null;
	
	// Main menu object items
	private JMenu mnuFile = new JMenu("File");
	private JMenu mnuTickets = new JMenu("Tickets");
	private JMenu mnuAdmin = new JMenu("Admin");

	// Sub menu item objects for all Main menu item objects
	JMenuItem mnuItemExit;
	JMenuItem mnuItemUpdate;
	JMenuItem mnuItemDelete;
	JMenuItem mnuItemUserCreate;
	JMenuItem mnuItemOpenTicket;
	JMenuItem mnuItemViewTicket;
	JMenuItem mnuItemViewTicketbyID;
	JMenuItem mnuItemCloseTicket;
	JMenuItem mnuItemLoginReport;

	public Tickets(Boolean isAdmin, String userName) {
		super(userName + " logged in!");
		this.userName = userName;
		chkIfAdmin = isAdmin;
		createMenu();
		prepareGUI();

	}

	private void createMenu() {

		/* Initialize sub menu items **************************************/

		// initialize sub menu item for File main menu
		mnuItemExit = new JMenuItem("Exit");
		// add to File main menu item
		mnuFile.add(mnuItemExit);

		// initialize first sub menu items for Admin main menu
		mnuItemUpdate = new JMenuItem("Update Ticket");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemUpdate);

		// initialize second sub menu items for Admin main menu
		mnuItemDelete = new JMenuItem("Delete Ticket");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemDelete);
		
		// initialize create user for Admin main menu
		mnuItemUserCreate = new JMenuItem("Create User");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemUserCreate);
		
		mnuItemLoginReport = new JMenuItem("Login Report");
		mnuAdmin.add(mnuItemLoginReport);

		// initialize first sub menu item for Tickets main menu
		mnuItemOpenTicket = new JMenuItem("Create Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemOpenTicket);

		// initialize second sub menu item for Tickets main menu
		mnuItemViewTicket = new JMenuItem("View Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemViewTicket);
		
		mnuItemViewTicketbyID = new JMenuItem("View Ticket by ID");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemViewTicketbyID);
		
		mnuItemCloseTicket = new JMenuItem("Close Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemCloseTicket);
		

		// initialize any more desired sub menu items below

		/* Add action listeners for each desired menu item *************/
		mnuItemExit.addActionListener(this);
		mnuItemUpdate.addActionListener(this);
		mnuItemDelete.addActionListener(this);
		mnuItemUserCreate.addActionListener(this);
		mnuItemOpenTicket.addActionListener(this);
		mnuItemViewTicket.addActionListener(this);
		mnuItemViewTicketbyID.addActionListener(this);
		mnuItemCloseTicket.addActionListener(this);
		mnuItemLoginReport.addActionListener(this);

	}

	private void prepareGUI() {

		// create JMenu bar
		JMenuBar bar = new JMenuBar();
		bar.add(mnuFile); // add main menu items in order, to JMenuBar
		bar.add(mnuTickets);
		if(chkIfAdmin) {
			bar.add(mnuAdmin);
		}
		// add menu bar components to frame
		setJMenuBar(bar);

		addWindowListener(new WindowAdapter() {
			// define a window close operation
			public void windowClosing(WindowEvent wE) {
				System.exit(0);
			}
		});
		// set frame options
		setSize(400, 400);
		getContentPane().setBackground(Color.LIGHT_GRAY);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// implement actions for sub menu items
		if (e.getSource() == mnuItemExit) {
			System.exit(0);
		} else if (e.getSource() == mnuItemOpenTicket) {
			JTextField tdField = new JTextField(userName);
			// get ticket information
			String ticketName = JOptionPane.showInputDialog(tdField, "Enter your name");
			String ticketDesc = JOptionPane.showInputDialog(null, "Enter a ticket description");

			// insert ticket information to database

			int id = dao.insertRecords(ticketName, ticketDesc, "Open", dao.getCurrentDay(), dao.getMonthFromToday());

			// display results if successful or not to console / dialog box
			if (id != 0) {
				System.out.println("Ticket ID : " + id + " created successfully!!!");
				JOptionPane.showMessageDialog(null, "Ticket id: " + id + " created");
			} else
				System.out.println("Ticket cannot be created!!!");
		}

		else if (e.getSource() == mnuItemViewTicket) {

			// retrieve all tickets details for viewing in JTable
			try {

				// Use JTable built in functionality to build a table model and
				// display the table model off your result set!!!
				JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords(chkIfAdmin, userName)));
				jt.setBounds(30, 40, 200, 400);
				JScrollPane sp = new JScrollPane(jt);
				add(sp);
				setVisible(true); // refreshes or repaints frame on screen

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == mnuItemDelete) {
			
				String ticketIDstr = JOptionPane.showInputDialog(null, "Enter the ticket ID you wish to delete:");
				int ticketID = Integer.parseInt(ticketIDstr);
				int result = -1;
				int reply = JOptionPane.showConfirmDialog(null, "Delete ticket ID " + ticketID + "?", "Delete Check", JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION) {
					result = dao.deleteTicket(ticketID);
				}
				
				if(result == 1) {
					JOptionPane.showMessageDialog(null, "Ticket ID " + ticketID + " deleted.");	
				} else if (result == -1) {
					JOptionPane.showMessageDialog(null, "Deletion of Ticket ID " + ticketID + " was cancelled.");	
				}
				else {
					JOptionPane.showMessageDialog(null, "Ticket ID " + ticketID + " does not exist.");	
				}		
		} else if (e.getSource() == mnuItemUpdate) {
			String ticketIDstr = JOptionPane.showInputDialog(null, "Enter the ticket ID you wish to update:");
			int ticketID = Integer.parseInt(ticketIDstr);
			String[] ticketData = null;
			try {
				ticketData = dao.getTicketData(ticketID);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, "Please enter a valid ticket ID.");
//				e1.printStackTrace();
				
			}
			JTextField tdField = new JTextField(ticketData[2], 10);
		    JTextField tiField = new JTextField(ticketData[3], 10);
		    JTextField stField = new JTextField(ticketData[4], 10);
		    JTextField sdField = new JTextField(ticketData[5], 10);
		    JTextField edField = new JTextField(ticketData[6], 10);
			
		    JPanel myPanel = new JPanel();
		    myPanel.add(new JLabel("Ticket Description: "));
		    myPanel.add(tdField);
		    myPanel.add(Box.createHorizontalStrut(15));
		    myPanel.add(new JLabel("Ticket Issuer: "));
		    myPanel.add(tiField);
		    myPanel.add(Box.createHorizontalStrut(15));
		    myPanel.add(new JLabel("Ticket Status: "));
		    myPanel.add(stField);
		    myPanel.add(Box.createHorizontalStrut(15));
		    myPanel.add(new JLabel("Start Date: "));
		    myPanel.add(sdField);
		    myPanel.add(Box.createHorizontalStrut(15));
		    myPanel.add(new JLabel("End Date: "));
		    myPanel.add(edField);
		    myPanel.add(Box.createHorizontalStrut(15));
			
		    int result = JOptionPane.showConfirmDialog(null, myPanel, 
		               "Editing ticket ID " + ticketID, JOptionPane.OK_CANCEL_OPTION);
		      if (result == JOptionPane.OK_OPTION) {
		    	  int resultfromsql = dao.updateTicket(ticketID, tdField.getText(), tiField.getText(), stField.getText(), sdField.getText(), edField.getText());
		    	  System.out.println("Ticket ID " + ticketID + " modified.");
		    	  if (resultfromsql == 1) {
		    		  JOptionPane.showMessageDialog(null, "Ticket ID " + ticketID + " modified successfully.");	
		    	  } else {
		    		  JOptionPane.showMessageDialog(null, "There was an error in modifying Ticket ID " + ticketID + ".");	
		    	  }
		      }
		} else if (e.getSource() == mnuItemUserCreate) {
			String newUserName = JOptionPane.showInputDialog(null, "Enter a user name:");
			String newUserPass = JOptionPane.showInputDialog(null, "Enter a password:");
			String newUserAdmin;
			int reply = JOptionPane.showConfirmDialog(null, "Is this user an admin?", "Admin Check", JOptionPane.YES_NO_OPTION);
			if (reply == JOptionPane.YES_OPTION) {
			    newUserAdmin = "1";
			} else {
				newUserAdmin = "0";
			}
			int resultfromsql = dao.createUser(newUserName, newUserPass, newUserAdmin);
			if (resultfromsql == 1) {
	    		  JOptionPane.showMessageDialog(null, "User was created successfully.");	
	    	  } else {
	    		  JOptionPane.showMessageDialog(null, "There was an error in creating the user.");	
	    	  }
		} else if (e.getSource() == mnuItemCloseTicket) {
			String ticketIDstr = JOptionPane.showInputDialog(null, "Enter a ticket ID to close:");
			int ticketID = Integer.parseInt(ticketIDstr);
			int result = dao.closeTicket(ticketID, userName, chkIfAdmin);
			if(result == 1) {
				JOptionPane.showMessageDialog(null, "Ticket ID " + ticketID + " was closed.");	
			} else {
				JOptionPane.showMessageDialog(null, "Ticket ID " + ticketID + " could not be closed.");	
			}	
		} else if (e.getSource() == mnuItemLoginReport) {
			//retrieve login information
			int reply = JOptionPane.showConfirmDialog(null, "Would you like to search a specific user's login times?", "Range Check", JOptionPane.YES_NO_OPTION);
			int userID = -1;
			if (reply == JOptionPane.YES_OPTION) {
				String userIDstr = JOptionPane.showInputDialog(null, "Enter a user ID to search: ");
				userID = Integer.parseInt(userIDstr);
			}
			try {
				
				// Use JTable built in functionality to build a table model and
				// display the table model off your result set!!!
				JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readLogins(userID)));
				jt.setBounds(30, 40, 200, 400);
				JScrollPane sp = new JScrollPane(jt);
				add(sp);
				setVisible(true); // refreshes or repaints frame on screen

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == mnuItemViewTicketbyID) {
			String ticketIDstr = JOptionPane.showInputDialog(null, "Enter a ticket ID to view:");
			int ticketID = Integer.parseInt(ticketIDstr);
			
			try {
				JTable jt = new JTable(ticketsJTable.buildTableModel(dao.getTicketResult(ticketID, userName, chkIfAdmin)));
				jt.setBounds(30, 40, 200, 400);
				JScrollPane sp = new JScrollPane(jt);
				add(sp);
				setVisible(true); // refreshes or repaints frame on screen

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		/*
		 * continue implementing any other desired sub menu items (like for update and
		 * delete sub menus for example) with similar syntax & logic as shown above
		 */

	}

}
