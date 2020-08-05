package com.intraday.ptma.status;

import java.sql.*;
import java.text.DateFormat;
import java.text.DecimalFormat;

import COM.ibm.db2.jdbc.app.DB2Driver;

import java.util.*;
import java.util.Date;

import com.intraday.ptma.helper.Files;
import com.intraday.ptma.helper.HttpDownload;
import com.intraday.ptma.helper.IndexHistory;
import com.intraday.ptma.helper.StaticData;
import com.intraday.ptma.helper.YStock;
import com.intraday.ptma.helper.StaticData;

public class SQLOperationStatus {

	static Connection con = null;
	static Statement stmt1 = null;
	static Statement stmt2 = null;

	static ResultSet rs1 = null;
	static ResultSet rs2 = null;

	public static Connection getConnection() {
		// Create a variable for the connection string.
		String connectionUrl = "jdbc:sqlserver://localhost:50001;"
				+ "databaseName=INTRADAY";

		// Declare the JDBC objects.
		Connection con = null;

		try {
			// Establish the connection.
			// Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			// con = DriverManager.getConnection(connectionUrl, "lombardi",
			// "8105strecker");

			// load the DB2 Driver
			Class.forName("COM.ibm.db2.jdbc.app.DB2Driver");
			// establish a connection to DB2
			// con = DriverManager.getConnection("jdbc:db2:INTRADAY",
			// "bpmadmin",
			// "Db2pswd!");
			con = DriverManager.getConnection("jdbc:db2:INTRADAY", "db2admin",
					"password");
			con.setAutoCommit(true);

			System.out.println("Made connection to INTRADAY DB");

			stmt1 = con.createStatement();
			stmt2 = con.createStatement();

		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
			if (con != null)
				try {
					con.close();
				} catch (Exception ex) {
				}
		}

		return con;
	}

	public static void main(String[] args) {

		addStockStatus("TGP", 1, 9);
	}

	public static void addStockStatus(String nextStock, int daysRC, int rc) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			String SQL = "select  TDAYS,TTRC from BPMADMIN.TradingStatus WHERE SYMBOL ='"
					+ nextStock + "'";

			rs1 = stmt1.executeQuery(SQL);

			if (rs1.next()) {
				int days = rs1.getInt(1) + daysRC;
				int ttrc = rs1.getInt(2) + rc;
				SQL = "UPDATE BPMADMIN.TradingStatus SET TDAYS=" + days
						+ ", TTRC=" + ttrc + " WHERE SYMBOL='" + nextStock
						+ "'";
				stmt1.executeUpdate(SQL);
			} else {
				System.out.println("Record count not exists");
				SQL = "INSERT INTO BPMADMIN.TradingStatus VALUES('" + nextStock
						+ "'," + daysRC + "," + rc + ","+daysRC+")";
				stmt1.executeUpdate(SQL);

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static Hashtable getSparselyTradedStocks() {

		// definition for 10 working days total days>=8, and TTRC<240
		// remove all 10 working days records after wards
		// qualified or unqualified
		Hashtable results = new Hashtable();
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			int cBBDI = 0;
			int cIndex = 0;
			int nBBDI = 0;
			int nSeq = 0;

			String SQL = "select SYMBOL, WDAYS,TDAYS,TTRC from BPMADMIN.TradingStatus ORDER BY SYMBOL ASC";

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				String symbol = rs1.getString(1);
				int wdays = rs1.getInt(2);
				int tdays = rs1.getInt(3);
				int ttrc = rs1.getInt(4);

				if (wdays >= 10 && tdays > 0.6 * wdays) {
					String val=""+wdays+"-"+tdays+":"+ttrc;
					results.put(symbol, val);
					String SQL2 = "DELETE from BPMADMIN.TradingStatus WHERE SYMBOL='"
							+ symbol + "'";
					// stmt2.executeUpdate(SQL2);

				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return results;
	}

	public static void addWorkingDays(int days) {
		// add one more working day to all records
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			

			String SQL = "select SYMBOL, WDAYS,TDAYS,TTRC from BPMADMIN.TradingStatus ORDER BY SYMBOL ASC";

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				String symbol = rs1.getString(1);
				int wdays = rs1.getInt(2) + days;
				String SQL2 = "UPDATE BPMADMIN.TradingStatus  SET WDAYS="
						+ wdays + " WHERE SYMBOL='" + symbol + "'";
				stmt2.executeUpdate(SQL2);

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}

}
