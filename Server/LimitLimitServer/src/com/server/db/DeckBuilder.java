package com.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.server.game.Card;
import com.server.game.Deck;

public class DeckBuilder {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";

	private String dbUrl;
	private String dbName;
	private String user;
	private String password;

	public DeckBuilder(String db, String dbname, String user, String pass) {
		this.dbUrl = db;
		this.dbName = dbname;
		this.user = user;
		this.password = pass;
	}

	public void populateDeck(final Deck deck, boolean white) {
		Connection conn = null;
		Statement stmt = null;
		try {
			// STEP 2: Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver");

			// STEP 3: Open a connection
			System.out.println("Connecting to database...");
			String url = "jdbc:mysql://"+this.dbUrl+"/"+this.dbName;
			conn = DriverManager.getConnection(url, this.user, this.password);

			// STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			String sql;
			String table = white ? "white_card" : "black_card";
			sql = "SELECT * FROM "+table;
			ResultSet rs = stmt.executeQuery(sql);

			// STEP 5: Extract data from result set
			while (rs.next()) {
				Card card = this.resultToCard(rs);
				deck.addCard(card);
			}
			// STEP 6: Clean-up environment
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			} // nothing we can do
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			} // end finally try
		} // end try
		System.out.println("Goodbye!");
	}
	
	public Card resultToCard(ResultSet result) throws SQLException
	{
		return new Card(result.getInt("id"), result.getString("message"));
	}
}
