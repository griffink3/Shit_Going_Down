package edu.brown.cs.sgd.general;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import edu.brown.cs.sgd.user.User;

public final class UserDatabase {
	private static Connection conn = null;
	private static Set<User> toUpdate = new HashSet<>();
	private static int usersUpdate = 0;
	
	public static Connection getConn() {
		return conn;
	}
	

	
	public static void startUpConn(String db) {
		try {
			Class.forName("org.sqlite.JDBC");
		    String urlToDB = "jdbc:sqlite:" + db;
		    conn = DriverManager.getConnection(urlToDB);
		    Statement stat = conn.createStatement();
		    stat.executeUpdate("PRAGMA foreign_keys = ON;");
		    PreparedStatement prep = conn.prepareStatement("CREATE TABLE IF NOT EXISTS user ("
		    		+ "id INTEGER,"
		    		+ "username TEXT,"
		    		+ "password TEXT,"
		    		+ "happy NUMERIC,"
		    		+ "sad NUMERIC,"
		    		+ "political NUMERIC,"
		    		+ "mystery NUMERIC,"
		    		+ "love NUMERIC,"
		    		+ "PRIMARY KEY (id, username));");
		    prep.executeUpdate();
		    prep.close();
		    System.out.println("Database is set!");
		} catch (SQLException e) {
			System.out.println("ERROR: Sql is wrong!");
		} catch (ClassNotFoundException e) {
			System.out.println("ERROR: this is a problem");
		}
	}
	
	public static void addUser(User one) {
		if (conn != null) {
			try {
				PreparedStatement prep = conn.prepareStatement("INSERT INTO user VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
				prep.setInt(1, one.getID());
				prep.setString(2, one.getLogin());
				prep.setString(3, one.getPassword());
				prep.setDouble(4, 0.0);
				prep.setDouble(5, 0.0);
				prep.setDouble(6, 0.0);
				prep.setDouble(7, 0.0);
				prep.setDouble(8, 0.0);
				prep.addBatch();
				prep.executeBatch();
				prep.close();
			} catch (SQLException e) {
				System.out.println("ERROR: Major yikes cannot insert user.");
			}
		}
	}
	
	public static void addUpdate(User a) {
		usersUpdate++;
		toUpdate.add(a);
		if (usersUpdate == 5) {
			updatePreferences();
		}
	}
	
	public static void updatePreferences() {
		for (User u : toUpdate) {
			updateUser(u);
		}
		toUpdate.clear();
		usersUpdate = 0;
	}
	
	private static void updateUser(User a) {
		if (conn != null) {
			try {
				PreparedStatement prep = conn.prepareStatement("UPDATE user SET happy = ?, "
						+ "sad = ?, political = ?, mystery = ?, love = ? WHERE user.username = ?;");
				prep.setDouble(1, a.getPreference("happy"));
				prep.setDouble(2, a.getPreference("sad"));
				prep.setDouble(3, a.getPreference("political"));
				prep.setDouble(4, a.getPreference("mystery"));
				prep.setDouble(5, a.getPreference("love"));
				prep.setString(6, a.getLogin());
				prep.executeUpdate();
			} catch (SQLException e) {
				System.out.println("ERROR: updating sentiment scores.");
			}
		}
	}
}
