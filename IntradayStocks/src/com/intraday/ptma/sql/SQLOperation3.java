package com.intraday.ptma.sql;

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

public class SQLOperation3 {

	static Connection con = null;
	static Statement stmt1 = null;
	static Statement stmt2 = null;
	static Statement stmt3 = null;
	static CallableStatement cstmt = null;
	static PreparedStatement pstmt = null;
	static boolean disLog = false;

	static ResultSet rs1 = null;
	static ResultSet rs2 = null;
	static ResultSet rs3 = null;

	static Connection conEOD = null;
	static Statement stmt1EOD = null;
	static Statement stmt2EOD = null;
	static Statement stmt3EOD = null;
	static ResultSet rs1EOD = null;
	static ResultSet rs2EOD = null;
	static ResultSet rs3EOD = null;

	static String SQL = null;
	static long startIndexLast = 0;
	static long endIndexLast = 0;
	public static int sortedByPrice = 1;
	public static int sortedByMarketcap = 2;
	public static float oneYearGainTotal = 0.0f;
	public static int aboveTenPercentage = 0;
	public static int aboveTwentyPercentage = 0;
	public static int aboveThirtyPercentage = 0;
	public static int aboveFourtyPercentage = 0;
	public static int aboveFiftyPercentage = 0;
	public static int aboveSixtyPercentage = 0;
	public static int aboveSeventyPercentage = 0;
	public static int aboveEightyPercentage = 0;
	public static int aboveNintyPercentage = 0;
	public static int aboveOnehundrePercentage = 0;
	public static int belowMinusTenPercentage = 0;
	public static int belowMinusTwentyPercentage = 0;
	public static int belowMinusThirtyPercentage = 0;
	public static int belowMinusFourtyPercentage = 0;
	public static int belowMinusFiftyPercentage = 0;
	public static int belowMinusSixtyPercentage = 0;
	public static int belowMinusSeventyPercentage = 0;
	public static int belowMinusEightyPercentage = 0;
	public static int belowMinusNintyPercentage = 0;
	public static int belowMinusOnehundrePercentage = 0;
	public static float maxGainTotal = 0.0f;
	public static float maxLossTotal = 0.0f;
	public static int stockCount = 0;
	public static int seqIndex = 0;
	public static Hashtable dateSeqMap = null;
	public static int cIndex = 0;
	public static long currentMaxIndex = 0L;

	// a number for intraday add operation, i.e., how many intervals
	// we need to for historical continuous calculation
	public static int backCountNeeded = 300;
	// a number for intraday add operation, i.e., how many intervals
	// we need to for historical continuous calculation

	// statistics that measure aws peak, trough length, yield
	public static long awsdLength = 0L;
	public static long awsdCount = 0L;
	public static long awsuLength = 0L;
	public static long awsuCount = 0L;
	public static long awsduLength = 0L;
	public static long awsduCount = 0L;
	public static double awsduYield = 0;
	public static long awsudLength = 0L;
	public static long awsudCount = 0L;
	public static double awsudYield = 0;
	public static long awsddLength = 0L;
	public static long awsddCount = 0L;
	public static double awsddYield = 0;
	public static long awsuuLength = 0L;
	public static long awsuuCount = 0L;
	public static double awsuuYield = 0;

	// use this value to calculate history update
	// for last, but have not narrow the program the
	// most efficient way, used in 1. calculateSellingScores(String, int,
	// boolean)
	// 2. calculateUTISteps23(String, int ,boolean) 3.
	// calculateUTISteps1(String, int ,boolean)
	// 4. calculateLBBIHistory(String , int ,boolean ) 5.
	// calculateLastPTS(String , int)
	// 6. calculateTruePTS(String , int ,boolean ) 7. calculateATT(String , int
	// ,boolean )
	// 8. getDS3PerTrendHistoryByPercentageStep1(String , int , boolean )
	// 9. getDS3PerTrendHistoryByPercentageStep2(String , int , boolean )
	// 10. getDS3PerTrendHistoryByPercentageStep3(String , int , boolean )
	private static int backCount = 420;

	// statistics that measure aws peak, trough length, yield

	// ******** 2/8/2018 new code ***************
	public static void overlayAllBBScore(long index) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS  WHERE SEQINDEX ="
					+ index;

			System.out.println(SQL);
			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Thread.sleep(5000);
			Enumeration en = results.keys();

			long t1 = System.currentTimeMillis();
			int loop = 0;
			while (en.hasMoreElements()) {
				String stock = en.nextElement().toString();
				overlayBBScore(stock, index);
				loop++;

				if (loop % 20 == 0) {
					long t2 = System.currentTimeMillis();
					System.out.println("So far " + loop
							+ " stocks calculation done, time cost "
							+ (t2 - t1) / 1000 + " seconds");
					Thread.sleep(5000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void overlayBBScore(String symbol, long preMaxIndex) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			// if (!isCalculationDone(symbol)) {
			System.out.println(symbol + " not done...");
			SQL = "SELECT SEQINDEX,VARCHAR_FORMAT(CURRENTDATE,'MM-DD-YYYY') AS VDATE, BBSCORE  FROM BPMADMIN.INTRADAYSTOCKS where symbol='"
					+ symbol
					+ "'  AND BBSCORE>=1000 and SEQINDEX>="
					+ (preMaxIndex - 2 * backCountNeeded)
					+ " order by seqIndex DESC";

			rs1 = stmt1.executeQuery(SQL);

			String date1 = "";
			int bbscore1 = 0;
			String date2 = "";
			int bbscore2 = 0;
			boolean accumlated = false;

			while (rs1.next()) {
				long i_seqIndex = rs1.getLong(1);
				if (bbscore1 == 0) {
					date1 = rs1.getString(2);
					bbscore1 = rs1.getInt(3);
				} else if (bbscore2 == 0) {
					date2 = rs1.getString(2);
					bbscore2 = rs1.getInt(3);
				} else {

					if (date1.equalsIgnoreCase(date2)) {
						if (bbscore1 >= bbscore2) {
							if (!accumlated) {
								bbscore1 = bbscore1 + bbscore1 / 1000
										+ bbscore2 / 1000;
								accumlated = true;
							} else if (accumlated) {
								bbscore1 = bbscore1 + bbscore2 / 1000;
							}

						} else if (bbscore1 < bbscore2) {
							if (!accumlated) {
								bbscore1 = bbscore2 + bbscore1 / 1000
										+ bbscore2 / 1000;
								accumlated = true;
							} else if (accumlated) {
								bbscore1 = bbscore2 + bbscore2 / 1000
										+ bbscore1 / 1000;
							}

						}

						date2 = rs1.getString(2);
						bbscore2 = rs1.getInt(3);

					} else {
						// update interday table based on date

						String sqlUpdate = "UPDATE BPMADMIN.INTRADAYSTOCKS set IBBS="
								+ bbscore1
								+ " where symbol='"
								+ symbol
								+ "' AND CURRENTDATE='"
								+ date1
								+ "' and CHOUR=9 and CMINUTE=30";
						stmt2.executeUpdate(sqlUpdate);
						System.out.println(sqlUpdate);

						date1 = date2;
						bbscore1 = bbscore2;
						date2 = rs1.getString(2);
						bbscore2 = rs1.getInt(3);
						accumlated = false;
					}

					System.out.println("BBSCORE " + bbscore1 + " at " + date1
							+ " at seqindex " + i_seqIndex);
				}

			}

			if (date1.equalsIgnoreCase(date2)) {
				if (bbscore1 >= bbscore2) {
					if (!accumlated) {
						bbscore1 = bbscore1 + bbscore1 / 1000 + bbscore2 / 1000;
						accumlated = true;
					} else if (accumlated) {
						bbscore1 = bbscore1 + bbscore2 / 1000;
					}

				} else if (bbscore1 < bbscore2) {
					if (!accumlated) {
						bbscore1 = bbscore2 + bbscore1 / 1000 + bbscore2 / 1000;
						accumlated = true;
					} else if (accumlated) {
						bbscore1 = bbscore2 + bbscore2 / 1000 + bbscore1 / 1000;
					}

				}

				/*
				 * only update if date transition, other not update String
				 * sqlUpdate = "UPDATE BPMADMIN.INTRADAYSTOCKS set IBBS=" +
				 * bbscore1 + " where symbol='" + symbol + "' AND CURRENTDATE='"
				 * + date1 + "' and CHOUR=9 and CMINUTE=30";
				 * System.out.println(sqlUpdate);
				 * stmt1.executeUpdate(sqlUpdate);
				 * System.out.println(sqlUpdate);
				 */

			} else {

				// update interday table based on date
				String sqlUpdate = "UPDATE BPMADMIN.INTRADAYSTOCKS set IBBS="
						+ bbscore1 + " where symbol='" + symbol
						+ "' AND CURRENTDATE='" + date1
						+ "' and CHOUR=9 and CMINUTE=30";
				stmt1.executeUpdate(sqlUpdate);
				System.out.println(sqlUpdate);

				date1 = date2;
				bbscore1 = bbscore2;
			}
			// }

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	// ******** 2/8/2018 new code ***************

	// ****** NEW CODE ********** 1/31/2018
	public static void calculateAverageDIPSCORE(long seqIndex, boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS  WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Thread.sleep(5000);
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				// resetDCPT(nextStock);
				// getDCPTrendHistoryByPercentageStep1(nextStock,(long)seqIndex);
				calculateAverageDIPSCOREforStock(nextStock, seqIndex, lastOnly);
				long t2 = System.currentTimeMillis();
				System.out.println(nextStock
						+ " Stock DIPSCORE History calculation done "
						+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;

				if (!lastOnly && loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					System.out.println("totalGains " + totalGains
							+ ", totalDays " + totalDays);
					System.out.println("Average DIPSCORE " + 100.0f
							* totalGains / totalDays);

					System.out.println("totalGainsP " + totalGainsP
							+ ", totalDaysP " + totalDaysP);

					System.out.println("Average Pos DIPSCORE " + 100.0f
							* totalGainsP / totalDaysP);

					System.out.println("totalGainsN " + totalGainsN
							+ ", totalDaysN " + totalDaysN);

					System.out.println("Average Neg DIPSCORE " + 100.0f
							* totalGainsN / totalDaysN);

					Thread.sleep(5000);
				} else if (loopCount % 200 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(10000);
				}
			}

			System.out.println("Average DIPSCORE " + 100.0f * totalGains
					/ totalDays);

			System.out.println("Average Pos DIPSCORE  " + 100.0f * totalGainsP
					/ totalDaysP);

			System.out.println("Average Neg DIPSCORE  " + 100.0f * totalGainsN
					/ totalDaysN);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateAverageDIPSCOREforStock(String symbol,
			long seqIndex, boolean lastOnly) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			int bottomIndex = 0;

			SQL = "select DIPSCORE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' and SEQINDEX<= "
					+ seqIndex
					+ "  order by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				double DIPSCORE = rs1.getInt(1) * 0.01d;

				if (DIPSCORE > 0 && DIPSCORE < 180) {
					totalGainsP = totalGainsP + DIPSCORE;
					totalDaysP = totalDaysP + 1;
					totalGains = totalGains + DIPSCORE;
					totalDays = totalDays + 1;
				} else if (DIPSCORE < 0 && DIPSCORE > -180) {
					totalGainsN = totalGainsN + DIPSCORE;
					totalDaysN = totalDaysN + 1;
					totalGains = totalGains + DIPSCORE;
					totalDays = totalDays + 1;
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	// ****** NEW CODE ********** 1/31/2018

	// ****** NEW CODE ********** 1/29/2018
	public static void calculateAverageDS3PER(long seqIndex, boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Thread.sleep(5000);
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				// resetDCPT(nextStock);
				// getDCPTrendHistoryByPercentageStep1(nextStock,(long)seqIndex);
				calculateAverageDS3PERforStock(nextStock, seqIndex, lastOnly);
				long t2 = System.currentTimeMillis();
				System.out.println(nextStock
						+ " Stock DCPER History calculation done "
						+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;

				if (!lastOnly && loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					System.out.println("totalGains " + totalGains
							+ ", totalDays " + totalDays);
					System.out.println("Average DCPER " + 100.0f * totalGains
							/ totalDays);

					System.out.println("totalGainsP " + totalGainsP
							+ ", totalDaysP " + totalDaysP);

					System.out.println("Average Pos DCPER " + 100.0f
							* totalGainsP / totalDaysP);

					System.out.println("totalGainsN " + totalGainsN
							+ ", totalDaysN " + totalDaysN);

					System.out.println("Average Neg DCPER " + 100.0f
							* totalGainsN / totalDaysN);

					Thread.sleep(5000);
				} else if (loopCount % 200 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(10000);
				}
			}

			System.out.println("Average DCPER " + 100.0f * totalGains
					/ totalDays);

			System.out.println("Average Pos DCPER  " + 100.0f * totalGainsP
					/ totalDaysP);

			System.out.println("Average Neg DCPER  " + 100.0f * totalGainsN
					/ totalDaysN);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateAverageDS3PERforStock(String symbol,
			long seqIndex, boolean lastOnly) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			int bottomIndex = 0;

			SQL = "select DCPERCENT from BPMADMIN.INTRADAYSTOCKS where symbol='"
					+ symbol
					+ "' and SEQINDEX<= "
					+ seqIndex
					+ "  order by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				float DS3PER = rs1.getFloat(1);

				if (DS3PER > 0 && DS3PER < 40) {
					totalGainsP = totalGainsP + DS3PER;
					totalDaysP = totalDaysP + 1;
					totalGains = totalGains + DS3PER;
					totalDays = totalDays + 1;
				} else if (DS3PER < 0 && DS3PER > -40) {
					totalGainsN = totalGainsN + DS3PER;
					totalDaysN = totalDaysN + 1;
					totalGains = totalGains + DS3PER;
					totalDays = totalDays + 1;
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	// ****** NEW CODE ********** 1/29/2018

	// ***** new code **** 1/24/2018
	public static void calculateAverageDSISUM(long seqIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from  BPMADMIN.INTRADAYSTOCKS  WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Thread.sleep(5000);
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				calculateAverageDSISUMforStock(nextStock, seqIndex);
				long t2 = System.currentTimeMillis();
				System.out.println(nextStock
						+ " Stock DIPSCORE History calculation done "
						+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;

				if (loopCount % 200 == 0) {
					System.out.println(loopCount + " stocks have been done");
					System.out.println("total SUMDS3P " + totalGains
							+ ", totalDays " + totalDays);
					System.out.println("Average SUMDS3P " + 1000.0f
							* totalGains / totalDays);

					System.out.println("total POS SUMDS3P " + totalGainsP
							+ ", totalDaysP " + totalDaysP);

					System.out.println("Average Pos SUMDS3P " + 1000.0f
							* totalGainsP / totalDaysP);

					System.out.println("totalGainsN " + totalGainsN
							+ ", totalDaysN " + totalDaysN);

					System.out.println("Average Neg SUMDS3P " + 1000.0f
							* totalGainsN / totalDaysN);

					Thread.sleep(12000);
				} else if (loopCount % 1000 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(10000);
				}
			}

			System.out.println("Average SUMDS3P " + 1000.0f * totalGains
					/ totalDays);

			System.out.println("Average Pos SUMDS3P " + 1000.0f * totalGainsP
					/ totalDaysP);

			System.out.println("Average Neg SUMDS3P " + 1000.0f * totalGainsN
					/ totalDaysN);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateAverageDSISUMforStock(String symbol,
			long seqIndex) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select SUMDS3P  from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' and SEQINDEX<= "
					+ seqIndex
					+ "   order by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				float dsi3 = rs1.getInt(1) * 1.0f;

				if (dsi3 > 0.00001) {
					totalGains = totalGains + dsi3 / 1000;
					totalDays = totalDays + 1;
					totalGainsP = totalGainsP + dsi3 / 1000;
					totalDaysP = totalDaysP + 1;
					System.out.println("totalGainsP: " + totalGainsP
							+ " ,totalDaysP:" + totalDaysP);
				} else if (dsi3 < -0.00001) {
					totalGains = totalGains + dsi3 / 1000;
					totalDays = totalDays + 1;
					totalGainsN = totalGainsN + dsi3 / 1000;
					totalDaysN = totalDaysN + 1;
					System.out.println("totalGainsN: " + totalGainsN
							+ " ,totalDaysN:" + totalDaysN);

				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	// ****** new code **** 1/24/2018

	public static void calculateAverageSPYYield(String symbol, long seqIndex) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select FINALPRICE  from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' and SEQINDEX<= "
					+ seqIndex
					+ "   order by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);
			float p1 = 0.0f;
			float p2 = 0.0f;

			while (rs1.next()) {
				if (p1 < 0.000000001f) {
					p1 = rs1.getFloat(1);
				} else if (p2 < 0.000000001) {
					p2 = rs1.getFloat(1);
				} else {

					if (p2 >= p1) {
						totalGains = totalGains + 100.0f * (p2 - p1) / p1;
						totalDays = totalDays + 1;
						totalGainsP = totalGainsP + 100.0f * (p2 - p1) / p1;
						totalDaysP = totalDaysP + 1;
						System.out.println("totalGainsP: " + totalGainsP
								+ " ,totalDaysP:" + totalDaysP);
					} else if (p2 < p1) {
						totalGains = totalGains + 100.0f * (p2 - p1) / p1;
						totalDays = totalDays + 1;
						totalGainsN = totalGainsN + 100.0f * (p2 - p1) / p1;
						totalDaysN = totalDaysN + 1;
						System.out.println("totalGainsN: " + totalGainsN
								+ " ,totalDaysN:" + totalDaysN);

					}
					p1 = p2;
					p2 = rs1.getFloat(1);
				}
			}

			System.out.println("Average SPY YIELD   " + totalGains / totalDays);

			System.out.println("Average Pos SPY YIELD   " + totalGainsP
					/ totalDaysP);

			System.out.println("Average Neg SPY YIELD   " + totalGainsN
					/ totalDaysN);
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void resetConnection() {

		if (cstmt != null) {
			try {
				cstmt.close();
			} catch (Exception ex) {

			} finally {
				cstmt = null;
			}
		}

		if (rs1 != null) {
			try {
				rs1.close();
			} catch (Exception ex) {

			} finally {
				rs1 = null;
			}
		}
		if (rs2 != null) {
			try {
				rs2.close();
			} catch (Exception ex) {

			} finally {
				rs2 = null;
			}
		}
		if (rs3 != null) {
			try {
				rs3.close();
			} catch (Exception ex) {

			} finally {
				rs3 = null;
			}
		}
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (Exception ex) {

			} finally {
				pstmt = null;
			}
		}
		if (stmt1 != null) {
			try {
				stmt1.close();
			} catch (Exception ex) {

			} finally {
				stmt1 = null;
			}
		}
		if (stmt2 != null) {
			try {
				stmt2.close();
			} catch (Exception ex) {

			} finally {
				stmt2 = null;
			}
		}
		if (stmt3 != null) {
			try {
				stmt3.close();
			} catch (Exception ex) {

			} finally {
				stmt3 = null;
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (Exception ex) {

			} finally {
				con = null;
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// markWeekEnds("SPY", true);
		//resetCalculationNotDoneTag(24971L);
		// calculateAverageDSISUM(19441l);
		// calculateAverageDS3IP(19441,false);
		// calculateAverageDCPIP(19441,false);
		System.out.println("Done... ");
		// populateTradingTimeTable();
		System.out.println("Done");
		// EFOI,COPX
		try {
			Thread.sleep(100000000);

			// getIndexStatus(10693, "DCP");
			// markWeekEnds("^AORD",true);
			// getIndexChangePercentage(10686);
			System.out.println("Done... ");
			Thread.sleep(10000000);
			// Hashtable allStocks = new Hashtable();
			// allStocks.put("EMES", "EMES");
			Hashtable allStocks = getCurrentAllStocks(43066l);
			System.out.println("Total stocks " + allStocks.size());
			populateGSEQTable();
			int count = 0;
			Enumeration en = allStocks.keys();
			while (en.hasMoreElements()) {
				String symbol = en.nextElement().toString();
				// if (isStockSplitted(symbol)) {
				// verifyStockForSplit(symbol);
				// }
				// calculateACPTMAHistory(symbol);
				// calculateBBGOHistory2(symbol);
				count++;
				System.out.println(symbol + " history done ");
				if (count % 50 == 0) {
					System.out
							.println("Finished another 50 stocks history calculation");
					try {
						Thread.sleep(10000);
					} catch (Exception ex) {

					}
				}
			}
			// String symbol = "NHTC";
			// Connection con = SQLOperation.getConnection();
			// SQLOperation.calculateFFPHistory(symbol);
			// calculateACPTMAHistory(symbol);
			// calculateLastPTMA(symbol, 10463);

			// for (int i = 10600; i > 2349; i--) {
			// doTodayIndexCalculation(i);
			// doTodayIndexAVGCalculation(i);
			// System.out.println("Calculation done for " + i);
			// }

			con.close();
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}

	public static Hashtable getSymbolsFromYahooDB(int seqIndex) {
		Hashtable results = new Hashtable();

		try {
			Connection conYahoo = getYahooDBConnection();
			Statement stmtYahoo = conYahoo.createStatement();

			SQL = "select SYMBOL FROM BPMADMIN.YAHOODB where SEQINDEX="
					+ seqIndex;

			ResultSet rsYahoo = stmtYahoo.executeQuery(SQL);

			int count = 1;
			while (rsYahoo.next()) {
				String symbol = rsYahoo.getString(1);
				System.out.println("Add stock " + symbol);
				results.put(symbol, symbol);
			}
			rsYahoo.close();
			stmtYahoo.close();
			conYahoo.close();

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

		return results;
	}

	public static Connection getYahooDBConnection() {
		// Create a variable for the connection string.
		String connectionUrl = "jdbc:sqlserver://localhost:50001;"
				+ "databaseName=STOCKDB";

		// Declare the JDBC objects.
		Connection conYahoo = null;

		try {
			// Establish the connection.
			// Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			// con = DriverManager.getConnection(connectionUrl, "lombardi",
			// "8105strecker");

			// load the DB2 Driver
			Class.forName("COM.ibm.db2.jdbc.app.DB2Driver");
			// establish a connection to DB2
			// con = DriverManager.getConnection("jdbc:db2:STOCKDB", "bpmadmin",
			// "Db2pswd!");
			conYahoo = DriverManager.getConnection("jdbc:db2:STOCKDB",
					"db2admin", "password");
			conYahoo.setAutoCommit(true);
			System.out.println("Connection made to STOCKDB");

		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
			if (conYahoo != null)
				try {
					conYahoo.close();
				} catch (Exception ex) {
				}
		}

		return conYahoo;
	}

	public static Connection getEODConnection() {
		// Create a variable for the connection string.
		String connectionUrl = "jdbc:sqlserver://localhost:50001;"
				+ "databaseName=STOCKDB";

		// Declare the JDBC objects.
		// Connection conEOD = null;

		try {
			// Establish the connection.
			// Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			// con = DriverManager.getConnection(connectionUrl, "lombardi",
			// "8105strecker");

			// load the DB2 Driver
			Class.forName("COM.ibm.db2.jdbc.app.DB2Driver");
			// establish a connection to DB2
			// con = DriverManager.getConnection("jdbc:db2:STOCKDB", "bpmadmin",
			// "Db2pswd!");
			conEOD = DriverManager.getConnection("jdbc:db2:STOCKDB",
					"db2admin", "password");
			conEOD.setAutoCommit(true);
			System.out.println("Connection made to STOCKDB");

			stmt1EOD = conEOD.createStatement();
			// stmt2EOD = con.createStatement();
			// stmt3EOD = con.createStatement();

		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
			if (conEOD != null)
				try {
					conEOD.close();
				} catch (Exception ex) {
				}
		}

		return conEOD;
	}

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
			stmt3 = con.createStatement();

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

	public static void insertHistoryRecordNoCheck(Hashtable results,
			int ignoreRecordCount) {
		Hashtable dates = new Hashtable();

		Hashtable indexMap = StaticData.dateMap(false);
		Enumeration en = indexMap.keys();

		while (en.hasMoreElements()) {
			String index = en.nextElement().toString();
			String date = indexMap.get(index).toString();
			dates.put(date, index);

		}

		int loopCount = 0;
		for (int k = 1; k <= results.size(); k++) {
			loopCount++;
			YStock stock = (YStock) results.get("" + k);
			String dateString = stock.getDate();

			if (dates.containsKey(dateString)) {
				String indexString = dates.get(dateString).toString();
				int seqIndex = Integer.parseInt(indexString);
				if (loopCount > ignoreRecordCount)
					insertRecord(seqIndex, stock);
			} else {
				System.out.println("Stop insert into records at " + dateString);
				break;
			}
		}

	}

	public static void insertEODRecord(YStock stock, java.sql.Date cdate) {

		dateSeqMap = StaticData.dateMap(true);

		int failedLoop = 0;
		String dateString = stock.getDate();

		if (dateSeqMap.containsKey(dateString)) {
			String indexString = dateSeqMap.get(dateString).toString();
			int seqIndex = Integer.parseInt(indexString);
			cIndex = seqIndex;
			try {
				insertRecordSP(seqIndex, stock, cdate);
			} catch (Exception ex) {

			}
			/*
			 * if (!recordExist(seqIndex, stock)) { if (insertRecord(seqIndex,
			 * stock) == false) { failedLoop++; if (failedLoop >= 3) {
			 * System.out .println("Failed insert 3 times, break loop");
			 * 
			 * } } } else { System.out.println(
			 * "Could not insert record, Date seq mapping not found for " +
			 * dateString);
			 * 
			 * }
			 */
		}

	}

	public static void insertHistoryRecord(Hashtable results) {
		Hashtable dates = new Hashtable();

		Hashtable indexMap = StaticData.dateMap(false);
		Enumeration en = indexMap.keys();

		while (en.hasMoreElements()) {
			String index = en.nextElement().toString();
			String date = indexMap.get(index).toString();
			dates.put(date, index);

		}

		int failedLoop = 0;
		for (int k = 1; k <= results.size(); k++) {

			YStock stock = (YStock) results.get("" + k);
			String dateString = stock.getDate();

			if (dates.containsKey(dateString)) {
				String indexString = dates.get(dateString).toString();
				int seqIndex = Integer.parseInt(indexString);

				if (!recordExist(seqIndex, stock)) {
					if (insertRecord(seqIndex, stock) == false) {
						failedLoop++;
						if (failedLoop >= 10) {
							System.out
									.println("Failed insert 10 times, break loop");
							break;
						}
					}
				} else {
					System.out.println("Stop insert into records at "
							+ dateString);
					break;
				}
			}

		}

	}

	public static Hashtable getIndexes() {
		Hashtable results = new Hashtable();

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select distinct symbol FROM BPMADMIN.INTRADAYSTOCKS where symbol like '^%'; ";

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				String symb = rs1.getString(1);
				results.put(symb, symb);
			}

			results.put("SPY", "SPY");

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return results;
	}

	public static Hashtable getStocks() {
		Hashtable results = new Hashtable();

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select distinct symbol FROM BPMADMIN.INTRADAYSTOCKS";

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				String symb = rs1.getString(1);
				results.put(symb, symb);
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return results;
	}

	public static boolean recordExist(int SEQINDEX, YStock vstock) {
		boolean exist = false;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select count(*) from BPMADMIN.INTRADAYSTOCKS where SEQINDEX="
					+ SEQINDEX + " AND SYMBOL='" + vstock.getSymbol() + "'";

			System.out.println(SQL);
			rs1 = stmt1.executeQuery(SQL);

			int count = 0;
			if (rs1.next()) {
				count = rs1.getInt(1);
			}

			if (count > 0)
				exist = true;

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return exist;
	}

	public static boolean calculateDSIHistorySP(String symbol, long maxPreIndex) {
		boolean success = false;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			cstmt = con.prepareCall("CALL BPMADMIN.CALCULATEDSIHISTORY(?,?)");

			cstmt.setString(1, symbol);
			cstmt.registerOutParameter(2, Types.INTEGER);

			cstmt.execute();

		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			// e.printStackTrace();
		}

		return success;

	}

	public static void calculateDSI24History(int seqIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex + " AND DSI=0";

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				String nextStock = en.nextElement().toString();
				calculateDSI24HistorySP(nextStock, 0L);
				loopCount++;
				if (loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(8000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateDSISumDeltaHistory(int seqIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex
					+ " AND DELTA1>-0.000001 AND DELTA1<0.000001 Order by symbol desc";

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();
			String sql2 = "";
			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
				// sql2 =
				// "Update BPMADMIN.INTRADAYSTOCKS SET DELTA1=0,DELTA2=0,DELTA3=0,DELTA4=0,DELTA5=0 WHERE SYMBOL ='"
				// + nextStock + "'";

				// stmt2.executeUpdate(sql2);
				// System.out.println("clean delta records of "+nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;

			while (en.hasMoreElements()) {
				String nextStock = en.nextElement().toString();
				SQL = "select  DSI3,SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
						+ nextStock
						+ "' and DSI<>0 and DSI5<>0 order by SEQINDEX DESC";

				rs1 = stmt1.executeQuery(SQL);
				int seqIndex1, seqIndex2, seqIndex3 = 0;
				float dsisum1, dsisum2, dsisum3;
				seqIndex1 = 0;
				seqIndex2 = 0;
				seqIndex3 = 0;
				dsisum1 = 0.0f;
				dsisum2 = 0.0f;
				dsisum3 = 0.0f;
				while (rs1.next()) {
					float dsi3 = rs1.getFloat(1);
					int index = rs1.getInt(2);
					if (seqIndex1 == 0) {
						seqIndex1 = index;
						dsisum1 = dsi3;
					} else if (seqIndex2 == 0) {
						seqIndex2 = index;
						dsisum2 = dsi3;
					} else if (seqIndex3 == 0) {
						seqIndex3 = index;
						dsisum3 = dsi3;
					} else {

						sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET DELTA1= "
								+ (dsisum1 - dsisum2) + " WHERE SYMBOL ='"
								+ nextStock + "' and SEQINDEX = " + seqIndex1;

						stmt2.executeUpdate(sql2);

						sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET DELTA2= "
								+ (dsisum1 - dsisum3) + " WHERE SYMBOL ='"
								+ nextStock + "' and SEQINDEX = " + seqIndex1;

						stmt2.executeUpdate(sql2);
						seqIndex1 = seqIndex2;
						seqIndex2 = seqIndex3;
						seqIndex3 = index;
						dsisum1 = dsisum2;
						dsisum2 = dsisum3;
						dsisum3 = dsi3;

					}

				}
				System.out.println(nextStock + " HAS BEEN PROCESSED ");
				loopCount++;
				if (loopCount % 50 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(5000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateDeltaSumHistory(int seqIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex
					+ " AND DELTA3>-0.0000001 AND DELTA3<0.0000001 Order by symbol desc";

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();
			String sql2 = "";
			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
				// sql2 =
				// "Update BPMADMIN.INTRADAYSTOCKS SET DELTA1=0,DELTA2=0,DELTA3=0,DELTA4=0,DELTA5=0 WHERE SYMBOL ='"
				// + nextStock + "'";

				// stmt2.executeUpdate(sql2);
				// System.out.println("clean delta records of "+nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int total = 0;

			while (en.hasMoreElements()) {
				String nextStock = en.nextElement().toString();
				SQL = "select  DELTA1,DELTA2,SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
						+ nextStock
						+ "' and DSI<>0 and DSI5<>0 order by SEQINDEX DESC";

				rs1 = stmt1.executeQuery(SQL);

				int[] seqIndexs = new int[6];
				float[] delta1s = new float[6];
				float[] delta2s = new float[6];
				for (int k = 0; k < 6; k++) {
					seqIndexs[k] = 0;
					delta1s[k] = 0.0f;
					delta2s[k] = 0.0f;
				}
				int loopCount = 0;

				while (rs1.next()) {
					float delta1 = rs1.getFloat(1);
					float delta2 = rs1.getFloat(2);
					int index = rs1.getInt(3);
					if (loopCount < 6) {
						seqIndexs[loopCount] = index;
						delta1s[loopCount] = delta1;
						delta2s[loopCount] = delta2;
					} else {
						float delta1sum = 0.0f;
						float delta2sum = 0.0f;
						int lastIndex = loopCount % 6;
						for (int w = 0; w < 6; w++) {
							delta1sum = delta1sum + delta1s[w];
							delta2sum = delta2sum + delta2s[w];
						}
						sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET DELTA3= "
								+ delta1sum + ", DELTA4 = " + delta2sum
								+ " WHERE SYMBOL ='" + nextStock
								+ "' and SEQINDEX = " + seqIndexs[lastIndex];

						stmt2.executeUpdate(sql2);

						seqIndexs[lastIndex] = index;
						delta1s[lastIndex] = delta1;
						delta2s[lastIndex] = delta2;

					}
					loopCount++;
				}
				System.out.println(nextStock + " HAS BEEN PROCESSED ");

				total++;
				if (total % 30 == 0) {
					System.out.println(total + " stocks have been done");
					Thread.sleep(5000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateDSISumHistory(int seqIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex + " AND DSI3>-0.000001 AND DSI3<0.000001";

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			String sql2 = "";
			while (en.hasMoreElements()) {
				String nextStock = en.nextElement().toString();
				SQL = "select  DSI,DSI5,SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
						+ nextStock
						+ "' and DSI<>0 and DSI5<>0 order by SEQINDEX DESC";

				rs1 = stmt1.executeQuery(SQL);

				while (rs1.next()) {
					float dsi = rs1.getFloat(1);
					float dsi5 = rs1.getFloat(2);
					int index = rs1.getInt(3);

					sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET DSI3= "
							+ (dsi + dsi5) + " WHERE SYMBOL ='" + nextStock
							+ "' and SEQINDEX = " + index;

					stmt2.executeUpdate(sql2);

				}
				System.out.println(nextStock + " HAS BEEN PROCESSED ");
				loopCount++;
				if (loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(8000);
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateDSISumHistory(String symbol, long maxPreIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			SQL = "select  DSI,DSI5,SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ symbol
					+ "' and DSI<>0 and DSI5<>0 AND SEQINDEX >"
					+ maxPreIndex + " order by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);

			int loopCount = 0;
			while (rs1.next()) {
				float dsi = rs1.getFloat(1);
				float dsi5 = rs1.getFloat(2);
				int index = rs1.getInt(3);

				String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET DSI3= "
						+ (dsi + dsi5) + " WHERE SYMBOL ='" + symbol
						+ "' and SEQINDEX = " + index;

				if (index > maxPreIndex)
					stmt2.executeUpdate(sql2);

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static boolean calculateDSI24HistorySP(String symbol,
			long maxPreIndex) {
		boolean success = false;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			cstmt = con
					.prepareCall("CALL DB2ADMIN.CALCULATEDSI24HISTORY(?,?,?)");

			cstmt.setString(1, symbol);
			cstmt.setLong(2, maxPreIndex);
			cstmt.registerOutParameter(3, Types.INTEGER);

			cstmt.execute();

		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
		}

		return success;

	}

	public static boolean calculatePTMAVSumHistorySP(String symbol,
			long maxPreIndex) {
		boolean success = false;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			cstmt = con.prepareCall("CALL DB2ADMIN.SUMPTMAVHISTORY(?,?,?)");

			cstmt.setString(1, symbol);
			cstmt.setLong(2, maxPreIndex);
			cstmt.registerOutParameter(3, Types.INTEGER);

			cstmt.execute();

		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			// e.printStackTrace();
		}

		return success;

	}

	public static boolean calculateDSI5SumHistorySP(String symbol,
			long maxPreIndex) {
		boolean success = false;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			cstmt = con.prepareCall("CALL DB2ADMIN.SUMDSI5HISTROY(?,?,?)");

			cstmt.setString(1, symbol);
			cstmt.setLong(2, maxPreIndex);
			cstmt.registerOutParameter(3, Types.INTEGER);

			cstmt.execute();

		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			// e.printStackTrace();
		}

		return success;

	}

	public static boolean isRealUpStartSP(String symbol, int seqIndex) {
		boolean success = false;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			if (cstmt == null) {
				// InsertDailyRecord (IN seqIndex INTEGER,IN symbol VARCHAR(10),
				// IN cdate DATE, IN openPrice REAL, IN lowPrice REAL, IN
				// highPrice REAL,
				// IN finalPrice REAL, IN adjustedPrice REAL, IN volume REAL,
				// OUT VARCOUNT INTEGER)
				cstmt = con
						.prepareCall("CALL BPMADMIN.FIRSTUPINDICATOR(?,?,?)");
			}

			cstmt.setString(1, symbol);
			cstmt.setInt(2, seqIndex);
			cstmt.registerOutParameter(3, Types.INTEGER);

			cstmt.execute();

			int result = cstmt.getInt(3);

			System.out.println(symbol + " Days is " + result);

			if (result >= 100) {
				success = true;
				System.out.print("Days " + result + " ");
			}

		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			// e.printStackTrace();
		}

		return success;

	}

	public static boolean calculateSurgeHistorySP(String symbol,
			long maxPreIndex) {
		boolean success = false;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			cstmt = con
					.prepareCall("CALL DB2ADMIN.CALCULATESURGEHISTORY(?,?,?)");

			cstmt.setString(1, symbol);
			cstmt.setLong(2, maxPreIndex);
			cstmt.registerOutParameter(3, Types.INTEGER);

			cstmt.execute();

		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
		}

		return success;

	}

	public static boolean findDSIStableSP(String symbol, int days,
			float avgSUMDSI, float avgSUMDSIRange, float avgDSI,
			float avgDSIRange, float avgDSI5, float avgDSI5Range) {
		boolean success = false;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			if (cstmt == null) {
				// IN symbolIn VARCHAR(28),IN CYCLEDAYS INTEGER,IN avgSUMDSI
				// FLOAT,
				// IN avgSUMDSIRange FLOAT,IN avgDSI FLOAT,IN avgDSIRange Float,
				// IN avgDSI5 FLOAT,IN avgDSI5Range FLOAT, OUT VARCOUNT INTEGER)

				cstmt = con
						.prepareCall("CALL BPMADMIN.DSISTABLEWIN(?,?,?,?,?,?,?,?,?)");
			}

			cstmt.setString(1, symbol);
			cstmt.setInt(2, days);
			cstmt.setFloat(3, avgSUMDSI);
			cstmt.setFloat(4, avgSUMDSIRange);
			cstmt.setFloat(5, avgDSI);
			cstmt.setFloat(6, avgDSIRange);
			cstmt.setFloat(7, avgDSI5);
			cstmt.setFloat(8, avgDSI5Range);
			cstmt.registerOutParameter(9, Types.INTEGER);

			cstmt.execute();

		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			// e.printStackTrace();
		}

		return success;

	}

	// symbolIn , seqIndexIn , lastOnly BOOLEAN, OUT VARCOUNT
	public static boolean calculateIndexSP(String symbol) {
		boolean success = false;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			cstmt = con.prepareCall("CALL BPMADMIN.IndexCal(?,?,?,?)");

			cstmt.setString(1, symbol);
			cstmt.setInt(2, 0);
			cstmt.setBoolean(3, false);
			cstmt.registerOutParameter(4, Types.INTEGER);

			cstmt.execute();

		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
		}

		return success;

	}

	public static boolean calculateIndexTMASP(String symbol) {
		boolean success = false;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			cstmt = con.prepareCall("CALL BPMADMIN.INDEX_TMA_CAL(?,?,?,?)");

			cstmt.setString(1, symbol);
			cstmt.setInt(2, 0);
			cstmt.setBoolean(3, false);
			cstmt.registerOutParameter(4, Types.INTEGER);

			cstmt.execute();

		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
		}

		return success;

	}

	// INDEX_PTMAV_CAL
	public static boolean calculateIndexPTMAVSP(String symbol) {
		boolean success = false;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			cstmt = con.prepareCall("CALL BPMADMIN.INDEX_PTMAV_CAL(?,?,?,?)");

			cstmt.setString(1, symbol);
			cstmt.setInt(2, 0);
			cstmt.setBoolean(3, false);
			cstmt.registerOutParameter(4, Types.INTEGER);

			cstmt.execute();

		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
		}

		return success;

	}

	public static boolean calculateIndexPTMASP(String symbol) {
		boolean success = false;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			cstmt = con.prepareCall("CALL BPMADMIN.INDEX_PTMA_CAL(?,?,?,?)");

			cstmt.setString(1, symbol);
			cstmt.setInt(2, 0);
			cstmt.setBoolean(3, false);
			cstmt.registerOutParameter(4, Types.INTEGER);

			cstmt.execute();

		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
		}

		return success;

	}

	public static boolean calculateIndexTMAVSP(String symbol) {
		boolean success = false;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			cstmt = con.prepareCall("CALL BPMADMIN.INDEX_TMAV_CAL(?,?,?,?)");

			cstmt.setString(1, symbol);
			cstmt.setInt(2, 0);
			cstmt.setBoolean(3, false);
			cstmt.registerOutParameter(4, Types.INTEGER);

			cstmt.execute();

		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
		}

		return success;

	}

	public static boolean calculateIndexDSISP(String symbol, int days) {
		boolean success = false;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			// currently up to 24 days
			cstmt = con.prepareCall("CALL BPMADMIN.INDEX_DSI24_CAL(?,?,?)");

			cstmt.setString(1, symbol);
			cstmt.setInt(2, days);
			;
			cstmt.registerOutParameter(3, Types.INTEGER);

			cstmt.execute();

		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
		}

		return success;

	}

	public static boolean findDSISyncSP(String symbol, int days, int dsiMin,
			int dsiDeltaMin, int dsi5Min, int dsi5DeltaMin) {
		boolean success = false;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			if (cstmt == null) {
				// InsertDailyRecord (IN seqIndex INTEGER,IN symbol VARCHAR(10),
				// IN cdate DATE, IN openPrice REAL, IN lowPrice REAL, IN
				// highPrice REAL,
				// IN finalPrice REAL, IN adjustedPrice REAL, IN volume REAL,
				// OUT VARCOUNT INTEGER)
				// DSISyncWing (IN symbolIn,IN CYCLEDAYS ,IN dsiMin ,IN dsiDelta
				// ,IN dsi5Min ,IN dsi5Delta , OUT VARCOUNT INTEGER)
				cstmt = con
						.prepareCall("CALL BPMADMIN.DSISyncWing(?,?,?,?,?,?,?)");
			}

			cstmt.setString(1, symbol);
			cstmt.setInt(2, days);
			cstmt.setInt(3, dsiMin);
			cstmt.setInt(4, dsiDeltaMin);
			cstmt.setInt(5, dsi5Min);
			cstmt.setInt(6, dsi5DeltaMin);
			cstmt.registerOutParameter(7, Types.INTEGER);

			cstmt.execute();

		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			// e.printStackTrace();
		}

		return success;

	}

	public static boolean insertRecordSP(int SEQINDEX, YStock vstock,
			java.sql.Date cdate) {
		boolean success = false;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			if (cstmt == null) {
				// InsertDailyRecord (IN seqIndex INTEGER,IN symbol VARCHAR(10),
				// IN cdate DATE, IN openPrice REAL, IN lowPrice REAL, IN
				// highPrice REAL,
				// IN finalPrice REAL, IN adjustedPrice REAL, IN volume REAL,
				// OUT VARCOUNT INTEGER)
				cstmt = con
						.prepareCall("CALL DB2ADMIN.INSERTINTRADAYRECORD(?,?,?,?,?,?,?,?,?,?,?,?)");
			}

			cstmt.setInt(1, SEQINDEX);
			cstmt.setString(2, vstock.getSymbol());
			cstmt.setDate(3, cdate);
			cstmt.setInt(4, vstock.getChour());
			cstmt.setInt(5, vstock.getCminute());
			cstmt.setFloat(6, vstock.getOpenPrice());
			cstmt.setFloat(7, vstock.getLowPrice());
			cstmt.setFloat(8, vstock.getHighPrice());
			cstmt.setFloat(9, vstock.getFinalPrice());
			cstmt.setFloat(10, vstock.getAdjustedPrice());
			cstmt.setFloat(11, vstock.getVolume());
			cstmt.registerOutParameter(12, Types.INTEGER);

			cstmt.execute();

		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
		}

		return success;

	}

	public static boolean insertRecord(long SEQINDEX, YStock vstock) {
		boolean success = false;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			if (currentMaxIndex == 0L) {
				getCurrentMaxIndex(vstock.getSymbol());
			}

			if (SEQINDEX > currentMaxIndex) {

				if (pstmt == null) {
					String SQL = "Insert into BPMADMIN.INTRADAYSTOCKS (SEQINDEX, SYMBOL, CURRENTDATE,CHOUR,CMINUTE,OPENPRICE,LOWPRICE,HIGHPRICE,FINALPRICE,ADJUSTEDPRICE,VOLUME)"
							+ "values(?,?,?,?,?,?,?,?,?,?,?)";

					// stmt.execute(SQL);

					System.out.println(SQL);
					pstmt = con.prepareStatement(SQL);
				}

				pstmt.setLong(1, SEQINDEX);
				pstmt.setString(2, vstock.getSymbol());
				pstmt.setString(3, vstock.getDate());
				pstmt.setInt(4, vstock.getChour());
				pstmt.setInt(5, vstock.getCminute());
				pstmt.setFloat(6, vstock.getOpenPrice());
				pstmt.setFloat(7, vstock.getLowPrice());
				pstmt.setFloat(8, vstock.getHighPrice());
				pstmt.setFloat(9, vstock.getFinalPrice());
				pstmt.setFloat(10, vstock.getAdjustedPrice());
				pstmt.setFloat(11, vstock.getVolume());

				pstmt.execute();

				// String date =
				// StaticData.dateMap().get(""+SEQINDEX).toString();
				String date = vstock.getDate();

				String SQL = "update  BPMADMIN.INTRADAYSTOCKS  set  currentdate=DATE('"
						+ date
						+ "') where seqIndex="
						+ SEQINDEX
						+ " and symbol='" + vstock.getSymbol() + "'";
				stmt1.executeUpdate(SQL);
				success = true;
			} else {
				System.out.println(SEQINDEX + " is less than currentMaxIndex "
						+ currentMaxIndex);
				System.out
						.println("Records already exists, skipping insertion.");
			}

		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
		}

		return success;

	}

	public static void markMonthEnd(String symbol, int counts, long preMaxIndex) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			long bottomIndex = 0;

			if (preMaxIndex > counts) {
				SQL = "select SEQINDEX from BPMADMIN.INTRADAYSTOCKS where symbol='"
						+ symbol
						+ "'  and TMAI = 1 and SEQINDEX>"
						+ (preMaxIndex - 2 * counts)
						+ " ORDER BY SEQINDEX ASC";
				rs1 = stmt1.executeQuery(SQL);

				if (rs1.next()) {
					bottomIndex = rs1.getLong(1);
				}

			}

			if (counts > 0) {
				SQL = "select SEQINDEX from BPMADMIN.INTRADAYSTOCKS where symbol='"
						+ symbol
						+ "' and SEQINDEX>"
						+ bottomIndex
						+ " order by SEQINDEX ASC";
				rs1 = stmt1.executeQuery(SQL);
				int loopCount = 0;
				while (rs1.next()) {
					bottomIndex = rs1.getInt(1);
					loopCount++;
					if (loopCount % counts == 0) {
						SQL = "update BPMADMIN.INTRADAYSTOCKS set TMAI = 1 where symbol='"
								+ symbol + "'  and seqIndex = " + bottomIndex;

						if (disLog)
							System.out.println(SQL);
						if (bottomIndex > preMaxIndex) {
							stmt2.executeUpdate(SQL);
						}
					}

				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateEntireBBPointHistory(int seqIndex,
			boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			if (disLog)
				System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				// int sumDays = 7;
				markBBEndPoint(nextStock, seqIndex, lastOnly);
				long t2 = System.currentTimeMillis();

				if (disLog)
					System.out.println(nextStock
							+ " Stock BBDI Score calculation done "
							+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;

				if (!lastOnly && loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(5000);
				} else if (loopCount % 1000 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(10000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void repairDCPIPHistory(int seqIndex, boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Thread.sleep(5000);
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				repairDCPIP(nextStock, seqIndex, lastOnly);
				// getDS3PerTrendHistoryByPercentageStep3(nextStock, seqIndex,
				// lastOnly);
				long t2 = System.currentTimeMillis();
				System.out.println(nextStock
						+ " Stock UTI History calculation done "
						+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;

				if (!lastOnly && loopCount % 3000 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(5000);
				} else if (loopCount % 1000 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(10000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void repairDCPIP(String symbol, int seqIndex, boolean lastOnly) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			int bottomIndex = 0;

			SQL = "select SEQINDEX, CURRENTDATE,BBDI,FINALPRICE from BPMADMIN.INTRADAYSTOCKS where symbol='"
					+ symbol
					+ "' and SEQINDEX<= "
					+ seqIndex
					+ " AND SEQINDEX>="
					+ (seqIndex - 700)
					+ " AND TWA<>0 order by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);
			int index1 = 0;
			int index2 = 0;
			float price1 = 0.0f;
			float price2 = 0.0f;

			int days = 0;
			while (rs1.next()) {
				int tempIndex = rs1.getInt(1);
				float tempPrice = rs1.getFloat(4);

				if (index1 == 0) {
					index1 = tempIndex;
					price1 = tempPrice;
				} else if (index2 == 0) {
					index2 = tempIndex;
					price2 = tempPrice;
				} else {
					if (index1 < (seqIndex - 10)) {
						// we only missed last 5 days
						break;
					}

					float dcpip = 100.0f * (price1 - price2) / price2;
					SQL = "update BPMADMIN.INTRADAYSTOCKS set DCPIP = " + dcpip
							+ " where symbol='" + symbol + "'  and seqIndex = "
							+ index1;

					System.out.println(SQL);

					stmt2.executeUpdate(SQL);
					index1 = index2;
					price1 = price2;
					index2 = tempIndex;
					price2 = tempPrice;
				}

				days++;
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateAverageDCPIP(int seqIndex, boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Thread.sleep(5000);
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				// resetDCPT(nextStock);
				// getDCPTrendHistoryByPercentageStep1(nextStock,(long)seqIndex);
				calculateAverageDCPIPforStock(nextStock, seqIndex, lastOnly);
				long t2 = System.currentTimeMillis();
				System.out.println(nextStock
						+ " Stock DCPIP History calculation done "
						+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;

				if (!lastOnly && loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					System.out.println("totalGains " + totalGains
							+ ", totalDays " + totalDays);
					System.out.println("Average DCPIP " + 100.0f * totalGains
							/ totalDays);

					System.out.println("totalGainsP " + totalGainsP
							+ ", totalDaysP " + totalDaysP);

					System.out.println("Average Pos DCPIP " + 100.0f
							* totalGainsP / totalDaysP);

					System.out.println("totalGainsN " + totalGainsN
							+ ", totalDaysN " + totalDaysN);

					System.out.println("Average Neg DCPIP " + 100.0f
							* totalGainsN / totalDaysN);

					Thread.sleep(1000);
				} else if (loopCount % 200 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(10000);
				}
			}

			System.out.println("Average PCPIP " + 100.0f * totalGains
					/ totalDays);

			System.out.println("Average Pos PCPIP " + 100.0f * totalGainsP
					/ totalDaysP);

			System.out.println("Average Neg PCPIP " + 100.0f * totalGainsN
					/ totalDaysN);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateAverageDS3IP(int seqIndex, boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Thread.sleep(5000);
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				calculateAverageDS3IPforStock(nextStock, seqIndex, lastOnly);
				long t2 = System.currentTimeMillis();
				System.out.println(nextStock
						+ " Stock DS3IP History calculation done "
						+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;

				if (!lastOnly && loopCount % 50 == 0) {
					System.out.println(loopCount + " stocks have been done");
					System.out.println("totalGains " + totalGains
							+ ", totalDays " + totalDays);
					System.out.println("Average DS3IP " + 100.0f * totalGains
							/ totalDays);

					System.out.println("totalGainsP " + totalGainsP
							+ ", totalDaysP " + totalDaysP);

					System.out.println("Average Pos DS3IP " + 100.0f
							* totalGainsP / totalDaysP);

					System.out.println("totalGainsN " + totalGainsN
							+ ", totalDaysN " + totalDaysN);

					System.out.println("Average Neg DS3IP " + 100.0f
							* totalGainsN / totalDaysN);

					Thread.sleep(5000);
				} else if (loopCount % 1000 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(10000);
				}
			}

			System.out.println("Average PS3IP " + 100.0f * totalGains
					/ totalDays);

			System.out.println("Average Pos PS3IP " + 100.0f * totalGainsP
					/ totalDaysP);

			System.out.println("Average Neg PS3IP " + 100.0f * totalGainsN
					/ totalDaysN);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	private static double totalGains = 0.0f;
	private static long totalDays = 0;

	private static double totalGainsP = 0.0f;
	private static long totalDaysP = 0;

	private static double totalGainsN = 0.0f;
	private static long totalDaysN = 0;

	public static void calculateAverageDCPIPforStock(String symbol,
			int seqIndex, boolean lastOnly) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			int bottomIndex = 0;

			SQL = "select TWA, DCPIP from BPMADMIN.INTRADAYSTOCKS where symbol='"
					+ symbol
					+ "' and SEQINDEX<= "
					+ seqIndex
					+ " AND (TWA>2 OR TWA<-2)  order by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				int tempTWA = rs1.getInt(1);
				float dcpip = rs1.getFloat(2);

				if (tempTWA > 0 && dcpip < 40 && dcpip > -40) {
					totalGainsP = totalGainsP + dcpip;
					totalDaysP = totalDaysP + tempTWA;
					totalGains = totalGains + dcpip;
					totalDays = totalDays + tempTWA;
				} else if (tempTWA < 0 && dcpip < 40 && dcpip > -40) {
					totalGainsN = totalGainsN + dcpip;
					totalDaysN = totalDaysN + tempTWA;
					totalGains = totalGains + dcpip;
					totalDays = totalDays - tempTWA;
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateAverageDS3IPforStock(String symbol,
			int seqIndex, boolean lastOnly) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			int bottomIndex = 0;

			SQL = "select DS3PIPDAYS, DS3PIP from BPMADMIN.INTRADAYSTOCKS where symbol='"
					+ symbol
					+ "' and SEQINDEX<= "
					+ seqIndex
					+ " AND (DS3PIPDAYS>2)  order by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				int tempTWA = rs1.getInt(1);
				float dcpip = rs1.getFloat(2);

				if (dcpip < 40 && dcpip > 0) {
					totalGainsP = totalGainsP + dcpip;
					totalDaysP = totalDaysP + tempTWA;
					totalGains = totalGains + dcpip;
					totalDays = totalDays + tempTWA;
				} else if (dcpip < 0 && dcpip > -40) {
					totalGainsN = totalGainsN + dcpip;
					totalDaysN = totalDaysN + tempTWA;
					totalGains = totalGains + dcpip;
					totalDays = totalDays + tempTWA;
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void repairBBScoreEndHistory(int seqIndex, boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex + " AND (BBDI>=200 OR BBDI<=-100)";

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Thread.sleep(5000);
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				repairBBEndPoint(nextStock, seqIndex, lastOnly);
				long t2 = System.currentTimeMillis();
				System.out.println(nextStock
						+ " Stock UTI History calculation done "
						+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;

				if (!lastOnly && loopCount % 3000 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(5000);
				} else if (loopCount % 1000 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(10000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	
	public static void renameRecords(String symbol, long lastIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			String SQL2 = "UPDATE BPMADMIN.INTRADAYSTOCKS SET SYMBOL='"
					+ symbol + "_ABX_' where symbol='" + symbol
					+ "'  AND SEQINDEX>" + lastIndex;

			stmt1.executeUpdate(SQL2);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}
	
	public static void repairBBEndPoint(String symbol, int seqIndex,
			boolean lastOnly) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			int bottomIndex = 0;

			SQL = "select SEQINDEX, CURRENTDATE,BBDI,FINALPRICE from BPMADMIN.INTRADAYSTOCKS where symbol='"
					+ symbol
					+ "' and SEQINDEX<= "
					+ seqIndex
					+ " AND SEQINDEX>"
					+ (seqIndex - 700)
					+ "  order by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);
			int currentIndex = 0;
			boolean bullStart = false;
			boolean bearStart = false;
			boolean update = false;
			float startPrice = 0.0f;
			float ePrice = 0.0f;
			int score = 0;
			int days = 0;
			while (rs1.next()) {
				currentIndex = rs1.getInt(1);
				int cBBDI = rs1.getInt(3);

				if (days == 0) {

					if (cBBDI > 0) {
						bullStart = true;
					} else {
						bearStart = true;
					}
				} else {
					if (bullStart && cBBDI >= 200) {
						update = false;
						break;
					} else if (bullStart && cBBDI <= -100) {
						update = true;
						score = 1;
						break;
					} else if (bearStart && cBBDI < -100) {
						update = false;
						break;
					} else if (bearStart && cBBDI >= 200) {
						update = true;
						score = -1;
						break;
					}

				}

				days++;
			}

			if (update) {
				SQL = "SELECT BBSCORE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol + "'  and seqIndex = " + seqIndex;
				rs2 = stmt2.executeQuery(SQL);

				int bbscore = 0;
				if (rs2.next()) {
					bbscore = rs2.getInt(1);
				}

				if (score > bbscore || score < bbscore) {
					System.out.println("update " + bbscore + " to " + score);
					SQL = "update BPMADMIN.INTRADAYSTOCKS set BBSCORE = "
							+ score + " where symbol='" + symbol
							+ "'  and seqIndex = " + seqIndex;

					System.out.println(SQL);

					stmt2.executeUpdate(SQL);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void markBBEndPoint(String symbol, long maxPreIndex,
			boolean lastOnly) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			int bottomIndex = 0;

			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 3/1/2018
			long tempIndex = 0;
			boolean bullStart = false;
			boolean bearStart = false;

			if (maxPreIndex > 0) {
				// this is where trend broke, so a new starting point going
				// forward
				SQL = "select  SEQINDEX,BBSCORE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and SEQINDEX<="
						+ maxPreIndex
						+ " AND (BBSCORE=1 OR BBSCORE=-1)  ORDER BY SEQINDEX DESC";

				if (disLog)
					System.out.println(SQL);

				rs1 = stmt1.executeQuery(SQL);

				if (rs1.next()) {
					tempIndex = rs1.getLong(1);

					if (disLog)
						System.out.println("tempIndex " + tempIndex);
					if (rs1.getInt(2) > 0) {
						bullStart = true;
					} else {
						bearStart = true;
					}
				}

			}
			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 3/1/2018

			SQL = "select SEQINDEX, CURRENTDATE,BBDI,FINALPRICE from BPMADMIN.INTRADAYSTOCKS where symbol='"
					+ symbol
					// must skip the index above
					+ "' AND SEQINDEX>" + tempIndex + " order by SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);
			int currentIndex = 0;
			boolean update = false;
			float startPrice = 0.0f;
			float ePrice = 0.0f;
			int score = 0;
			int days = 0;
			while (rs1.next()) {
				currentIndex = rs1.getInt(1);
				int cBBDI = rs1.getInt(3);

				if (cBBDI >= 200 && bearStart) {
					score = 1;
					update = true;
					bearStart = false;
					bullStart = true;

					if (disLog)
						System.out.println("bear days " + days);
					if (disLog)
						System.out.println("StartPrice " + startPrice
								+ " ePrice " + ePrice);
					days = 1;
					startPrice = rs1.getFloat(4);
					ePrice = rs1.getFloat(4);

				} else if (cBBDI >= 200 && !bearStart && !bullStart) {
					score = 1;
					update = true;
					bearStart = false;
					bullStart = true;
					days = 1;
					startPrice = rs1.getFloat(4);
					ePrice = rs1.getFloat(4);

				} else if (cBBDI <= -100 && bullStart) {
					score = -1;
					update = true;
					bearStart = true;
					bullStart = false;

					if (disLog)
						System.out.println("bull days " + days);
					if (disLog)
						System.out.println("StartPrice " + startPrice
								+ " ePrice " + ePrice);
					startPrice = rs1.getFloat(4);
					ePrice = rs1.getFloat(4);
					days = 1;

				} else if (cBBDI <= -100 && !bearStart && !bullStart) {
					score = -1;
					update = true;
					bearStart = true;
					bullStart = false;
					days = 1;
					startPrice = rs1.getFloat(4);
					ePrice = rs1.getFloat(4);
				}

				if (bearStart && rs1.getFloat(4) < ePrice) {
					ePrice = rs1.getFloat(4);
				} else if (bullStart && rs1.getFloat(4) > ePrice) {
					ePrice = rs1.getFloat(4);
				}

				if (update) {
					// modified such that the bull BBSCORE counting
					// starting from the last -100 or -200 BBDI instead
					// of the first BBDI 200, 9/20/2017
					if (score > 0) {
						SQL = "select SEQINDEX, BBDI from BPMADMIN.INTRADAYSTOCKS where symbol='"
								+ symbol
								+ "'  AND BBDI<=-100 and SEQINDEX<"
								+ currentIndex + " order by SEQINDEX DESC";
						rs2 = stmt2.executeQuery(SQL);
						int pIndex = 0;

						if (rs2.next()) {
							pIndex = rs2.getInt(1);
						}

						if (pIndex > 0) {
							SQL = "select SEQINDEX, BBSCORE from BPMADMIN.INTRADAYSTOCKS where symbol='"
									+ symbol
									+ "'  AND SEQINDEX>="
									+ pIndex
									+ " and SEQINDEX<"
									+ (pIndex + 10)
									+ " order by SEQINDEX ASC";
							rs2 = stmt2.executeQuery(SQL);

							if (disLog)
								System.out.println(SQL);
							pIndex = 0;
							int bbscore = 0;

							// added on 2/20/2018 to avoid duplicated BBSCORE=1
							// after multiple calculation
							boolean bbscore1exists = false;
							// added on 2/20/2018 to avoid duplicated BBSCORE=1
							// after multiple calculation

							while (rs2.next()) {
								pIndex = rs2.getInt(1);
								bbscore = rs2.getInt(2);
								if (bbscore == 1) {
									bbscore1exists = true;
									break;
								} else if (bbscore == 0) {
									break;
								}

							}
							SQL = "update BPMADMIN.INTRADAYSTOCKS set BBSCORE = "
									+ score
									+ " where symbol='"
									+ symbol
									+ "'  and seqIndex = " + pIndex;

							if (disLog)
								System.out.println(SQL);

							if (!bbscore1exists && pIndex >= tempIndex) {
								stmt2.executeUpdate(SQL);
							}
						}
					} else {

						SQL = "update BPMADMIN.INTRADAYSTOCKS set BBSCORE = "
								+ score + " where symbol='" + symbol
								+ "'  and seqIndex = " + currentIndex;

						if (disLog)
							System.out.println(SQL);

						if (currentIndex >= tempIndex)
							stmt2.executeUpdate(SQL);
					}
					update = false;
				}
				days++;

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateTMAV(String symbol, boolean lastOnly,
			long maxPreIndex) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018
			long tempIndex = 0;
			if (maxPreIndex > 0) {
				SQL = "select  seqindex from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and TMAI>0 AND SEQINDEX<="
						+ maxPreIndex
						+ " ORDER BY SEQINDEX DESC";
				rs1 = stmt1.executeQuery(SQL);

				int lc = 0;
				while (rs1.next()) {
					tempIndex = rs1.getLong(1);
					if (lc >= 9) { // we only need previous 9 month data
						break;
					}
				}

			}
			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018

			SQL = "select SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' and TMAI>0 and seqIndex>="
					+ tempIndex
					+ " order by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);
			int[] seqIndex = new int[2000];

			int monthCount = 0;
			while (rs1.next()) {

				seqIndex[monthCount] = rs1.getInt(1);

				if (disLog)
					System.out.println(monthCount + " " + rs1.getInt(1) + " "
							+ seqIndex[monthCount]);
				monthCount++;

			}

			for (int k = 0; k < monthCount - 10; k++) {

				float avgVolume = 0.0f;
				SQL = "select AVG(VOLUME) from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and  SEQINDEX <="
						+ seqIndex[k]
						+ " and SEQINDEX>" + seqIndex[k + 10];
				if (disLog)
					System.out.println("Ten month average volume sql is: "
							+ SQL);
				rs1 = stmt1.executeQuery(SQL);
				rs1.next();
				avgVolume = rs1.getFloat(1);

				SQL = "update  BPMADMIN.INTRADAYSTOCKS   set TMAV = "
						+ avgVolume + "  where seqIndex = " + seqIndex[k]
						+ " and SYMBOL='" + symbol + "'";

				if (seqIndex[k] >= maxPreIndex) {
					stmt1.executeUpdate(SQL);
				}

				if (lastOnly)
					break;

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateTMA(String symbol, boolean lastOnly,
			long maxPreIndex) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018
			long tempIndex = 0;
			if (maxPreIndex > 0) {
				SQL = "select SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and TMAI>0 AND SEQINDEX<="
						+ maxPreIndex
						+ " ORDER BY SEQINDEX DESC";
				rs1 = stmt1.executeQuery(SQL);

				int lc = 0;
				while (rs1.next()) {
					tempIndex = rs1.getLong(1);
					lc++;
					if (lc >= 9) { // we only need previous 9 month value
						break;
					}
				}

			}
			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018

			SQL = "select SEQINDEX, adjustedprice from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' and SEQINDEX>="
					// make sure backdated more than 24*10 months,LOL,
					// actually 24*5*10 =20 hours
					+ tempIndex + " and TMAI>0 order by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);
			int[] seqIndex = new int[2000];
			float[] priceFinal = new float[2000];

			int monthCount = 0;
			while (rs1.next()) {

				seqIndex[monthCount] = rs1.getInt(1);
				priceFinal[monthCount] = rs1.getFloat(2);

				if (disLog)
					System.out.println(monthCount + " " + rs1.getInt(1) + " "
							+ seqIndex[monthCount]);
				monthCount++;

			}

			for (int k = 0; k < monthCount - 9; k++) {

				float sumPrice = 0.0f;
				for (int w = k; w < k + 10; w++) {
					sumPrice = sumPrice + priceFinal[w];
				}
				float tmaPrice = sumPrice / 10.0f;

				SQL = "update  BPMADMIN.INTRADAYSTOCKS   set TMA = " + tmaPrice
						+ "  where seqIndex = " + seqIndex[k] + " and SYMBOL='"
						+ symbol + "'";

				if (seqIndex[k] >= maxPreIndex)
					stmt1.executeUpdate(SQL);

				// if (lastOnly)
				// break;

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateTWA(String symbol, int count, boolean lastOnly) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select SEQINDEX, adjustedprice from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' and WWI<>0  order by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);
			int[] seqIndex = new int[80000];
			float[] priceFinal = new float[80000];

			int weekCount = 0;
			while (rs1.next()) {

				seqIndex[weekCount] = rs1.getInt(1);
				priceFinal[weekCount] = rs1.getFloat(2);

				if (disLog)
					System.out.println(weekCount + " " + rs1.getInt(1) + " "
							+ seqIndex[weekCount]);
				weekCount++;

				if (weekCount > count && count > 0)
					break;

			}

			for (int k = 0; k < weekCount - 9; k++) {

				float sumPrice = 0.0f;
				for (int w = k; w < k + 10; w++) {
					sumPrice = sumPrice + priceFinal[w];
				}
				float twaPrice = sumPrice / 10.0f;

				SQL = "update  BPMADMIN.INTRADAYSTOCKS   set TWA = " + twaPrice
						+ "  where seqIndex = " + seqIndex[k] + " and SYMBOL='"
						+ symbol + "'";

				stmt1.executeUpdate(SQL);

				if (lastOnly)
					break;

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculatePTWAHistory(String symbol, int counts) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  ADJUSTEDPRICE,seqindex from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' ORDER BY SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);

			SQL = "select  TWA,seqindex from  BPMADMIN.INTRADAYSTOCKS   where symbol='"
					+ symbol + "' and TWA>0.001 order by seqIndex desc";

			rs2 = stmt2.executeQuery(SQL);

			float priceFinal = 0.0f;
			float lastTWA = 0.0f;
			int seqIndex = 0;
			int seqTWA = 0;
			int loopCount = 0;

			while (rs1.next()) {
				loopCount++;

				if (loopCount > counts && counts > 0)
					break;

				priceFinal = rs1.getFloat(1);
				seqIndex = rs1.getInt(2);

				if (seqIndex <= seqTWA || seqTWA == 0) {
					if (rs2.next()) {
						seqTWA = rs2.getInt(2);
						lastTWA = rs2.getFloat(1);
					} else {
						break;
					}

				}

				if (lastTWA > 0.001f && priceFinal > 0.001f) {
					float PTWA = 100.0f * (priceFinal - lastTWA) / lastTWA;
					SQL = "update BPMADMIN.INTRADAYSTOCKS  set DCP = " + PTWA
							+ "  where seqIndex = " + seqIndex
							+ " and SYMBOL='" + symbol + "'";

					stmt3.executeUpdate(SQL);
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateTodayACPTMA(int seqIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE GR100=1 and SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			Enumeration en = results.keys();

			while (en.hasMoreElements()) {
				String nextStock = en.nextElement().toString();
				calculateLastPTMA(nextStock, seqIndex);
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	// PAD RECORDS
	public static void padIntradayRecords(String symbol, long seqIndex) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  seqindex,openprice,highprice,lowprice,finalprice,adjustedprice,volume, VARCHAR_FORMAT(CURRENTDATE,'MM-DD-YYYY') AS VDATE  from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' and  SEQINDEX >="
					+ seqIndex+" AND SEQINDEX<="+seqIndex+StaticData.getDownloadDays()*79
					+ " order by seqIndex asc";


			rs2 = stmt2.executeQuery(SQL);

			float oprice = 0.0f;
			float hprice = 0.0f;
			float lprice = 0.0f;
			float fprice = 0.0f;
			float aprice = 0.0f;
			float volume = 0.0f;
			String cdate = "";
			long preIndex = 0;
			long currentIndex = 0;

			while (rs2.next()) {
				if (preIndex == 0) {
					preIndex = rs2.getInt(1);
					oprice = rs2.getFloat(2);
					hprice = rs2.getFloat(3);
					lprice = rs2.getFloat(4);
					fprice = rs2.getFloat(5);
					aprice = rs2.getFloat(6);
					volume = rs2.getFloat(7);
					cdate = rs2.getString(8);
				} else {
					currentIndex = rs2.getInt(1);
					if (currentIndex - preIndex > 1) {
						for (long w = preIndex + 1; w < currentIndex; w++) {
							// ***********
							YStock record = new YStock();
							record.setAdjustedPrice(aprice);
							record.setFinalPrice(fprice);
							record.setHighPrice(hprice);
							record.setLowPrice(lprice);
							record.setOpenPrice(oprice);
							record.setSeqIndex(w);
							record.setSymbol(symbol);
							record.setVolume(0.0f);

							Hashtable timeinfo = getTimeInfo(w);

							record.setDate((String) timeinfo.get("date"));
							record.setChour((int) timeinfo.get("hours"));
							record.setCminute((int) timeinfo.get("minutes"));

							SQLOperation3.insertRecord(
									(long) record.getSeqIndex(), record);

							// ***********
						}

					}

					preIndex = rs2.getInt(1);
					oprice = rs2.getFloat(2);
					hprice = rs2.getFloat(3);
					lprice = rs2.getFloat(4);
					fprice = rs2.getFloat(5);
					aprice = rs2.getFloat(6);
					volume = rs2.getFloat(7);
					cdate = rs2.getString(8);

				}

			}

			// pad end of day
			String sql2 = "select max(SEQINDEX) from BPMADMIN.TRADINGTIME WHERE CDATE='"
					+ cdate + "'";
			rs1 = stmt1.executeQuery(sql2);
			if (rs1.next()) {
				long maxIndex = rs1.getLong(1);
				for (long w = preIndex + 1; w <= maxIndex; w++) {
					YStock record = new YStock();
					record.setAdjustedPrice(aprice);
					record.setFinalPrice(fprice);
					record.setHighPrice(hprice);
					record.setLowPrice(lprice);
					record.setOpenPrice(oprice);
					record.setSeqIndex(w);
					record.setSymbol(symbol);
					record.setVolume(0.0f);

					Hashtable timeinfo = getTimeInfo(w);

					record.setDate((String) timeinfo.get("date"));
					record.setChour((int) timeinfo.get("hours"));
					record.setCminute((int) timeinfo.get("minutes"));

					SQLOperation3.insertRecord((long) record.getSeqIndex(),
							record);

				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	// PAD RECORDS

	public static void calculateLastPTMA(String symbol, int seqIndex1) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  seqindex from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' and GR100=-1 and SEQINDEX <"
					+ seqIndex1
					+ " order by seqIndex desc";

			rs1 = stmt1.executeQuery(SQL);

			int seqIndex2 = 0;
			float sumPTMA = 0.0f;
			float avgPTMA = 0.0f;

			if (rs1.next()) {

				seqIndex2 = rs1.getInt(1);

				SQL = "select  SUM(PTMA) from  BPMADMIN.INTRADAYSTOCKS   where symbol='"
						+ symbol
						+ "' and SEQINDEX>= "
						+ seqIndex2
						+ " and SEQINDEX<" + seqIndex1;
				// System.out.println(SQL);
				rs2 = stmt2.executeQuery(SQL);

				if (rs2.next()) {

					sumPTMA = rs2.getFloat(1);
					avgPTMA = sumPTMA / (1.0f * (seqIndex1 - seqIndex2));
					SQL = "update BPMADMIN.INTRADAYSTOCKS  set ACPTMA = "
							+ avgPTMA + "  where seqIndex = " + (seqIndex1 - 1)
							+ " and SYMBOL='" + symbol + "'";

					stmt3.executeUpdate(SQL);
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void findDSIDiscrepencySP(String symbol, boolean lastOnly) {
		// findDSIDiscrepency
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			cstmt = con.prepareCall("CALL BPMADMIN.findDSIDiscrepency(?,?,?)");

			cstmt.setString(1, symbol);
			cstmt.setBoolean(2, lastOnly);
			cstmt.registerOutParameter(3, Types.INTEGER);

			cstmt.execute();

		}

		// Handle any errors that may have occurred.
		catch (Exception e) {
			// e.printStackTrace();
		}

	}

	public static void markUpTrendHistory(String symbol) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();
			if (stmt3 == null)
				stmt3 = con.createStatement();

			SQL = "select SEQINDEX, SYMBOL, CURRENTDATE,  ADJUSTEDPRICE,  PTMAV, DSI5, UPTRENDSTART from BPMADMIN.INTRADAYSTOCKS  where  symbol='"
					+ symbol + "'  ORDER BY SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);

			int seqIndex = 0;
			int loopCount = 0;
			float sumDIS5 = 0.0f;
			float maxClosePrice = 0.0f;
			int ptmavPositiveCount = 0;
			int aboveFiftyCount = 0;
			float closePrice = 0.0f;
			float dis5 = 0.0f;
			float ptmav = 0.0f;

			while (rs1.next()) {

				if (loopCount == 6 && aboveFiftyCount == 6
						&& sumDIS5 / 6 >= 80.0f) {
					if (ptmavPositiveCount > 0) {
						SQL = "update BPMADMIN.INTRADAYSTOCKS  set UPTRENDSTART = 1  where seqIndex = "
								+ seqIndex + " and SYMBOL='" + symbol + "'";

						stmt3.executeUpdate(SQL);
					} else {
						SQL = "update BPMADMIN.INTRADAYSTOCKS  set UPTRENDSTART = -1  where seqIndex = "
								+ seqIndex + " and SYMBOL='" + symbol + "'";

						stmt3.executeUpdate(SQL);
						loopCount = 0;
						sumDIS5 = 0.0f;
						aboveFiftyCount = 0;
						maxClosePrice = 0.0f;
						closePrice = 0.0f;
						aboveFiftyCount = 0;
						ptmavPositiveCount = 0;

						while (rs1.next()) {
							seqIndex = rs1.getInt(1);
							closePrice = rs1.getFloat(4);
							if (closePrice > 1.01f * maxClosePrice) {
								SQL = "update BPMADMIN.INTRADAYSTOCKS  set UPTRENDSTART = 1  where seqIndex = "
										+ seqIndex
										+ " and SYMBOL='"
										+ symbol
										+ "'";

								stmt3.executeUpdate(SQL);
								break;
							}

							ptmav = rs1.getFloat(5);
							if (ptmav > 0.0f) {
								ptmavPositiveCount++;
							}

							closePrice = rs1.getFloat(4);

							if (closePrice > maxClosePrice) {
								maxClosePrice = closePrice;
							}

							loopCount++;

							dis5 = rs1.getFloat(6);
							sumDIS5 = sumDIS5 + dis5;

							if (dis5 > 50.0f) {
								aboveFiftyCount++;
							} else {
								loopCount = 0;
								sumDIS5 = 0.0f;
								aboveFiftyCount = 0;
								maxClosePrice = 0.0f;
								closePrice = 0.0f;
								aboveFiftyCount = 0;
								ptmavPositiveCount = 0;
							}

							if (loopCount == 6 && aboveFiftyCount == 6
									&& sumDIS5 / 6 >= 80.0f) {
								if (ptmavPositiveCount > 0) {
									SQL = "update BPMADMIN.INTRADAYSTOCKS  set UPTRENDSTART = 1  where seqIndex = "
											+ seqIndex
											+ " and SYMBOL='"
											+ symbol + "'";

									stmt3.executeUpdate(SQL);
									break;
								}
							}
						}

					}
					loopCount = 0;
					sumDIS5 = 0.0f;
					aboveFiftyCount = 0;
					maxClosePrice = 0.0f;
					closePrice = 0.0f;
					aboveFiftyCount = 0;
					ptmavPositiveCount = 0;

				}

				seqIndex = rs1.getInt(1);

				ptmav = rs1.getFloat(5);
				if (ptmav > 0.0f) {
					ptmavPositiveCount++;
				}

				closePrice = rs1.getFloat(4);

				if (closePrice > maxClosePrice) {
					maxClosePrice = closePrice;
				}

				loopCount++;

				dis5 = rs1.getFloat(6);
				sumDIS5 = sumDIS5 + dis5;

				if (dis5 > 50.0f) {
					aboveFiftyCount++;
				} else {
					loopCount = 0;
					sumDIS5 = 0.0f;
					aboveFiftyCount = 0;
					maxClosePrice = 0.0f;
					closePrice = 0.0f;
					aboveFiftyCount = 0;
					ptmavPositiveCount = 0;
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	// calculate Average Correction PTMA history
	// basically when GR100 is in negative territory, then consider an
	// correction
	// if the when GR100 becomes positive, then we look back at the entire
	// correction
	// period, if the Average Correction PTMA is positive, then consider a
	// shallow correction
	// the stock is primed for further uptrend, buy 6 month to one year out deep
	// call
	// option with less than 5% premium
	public static void calculateACPTMAHistory(String symbol, int counts) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select seqindex from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' and GR100 = 1 ORDER BY SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);

			int seqIndex1 = 0;
			int seqIndex2 = 0;
			float sumPTMA = 0.0f;
			float avgPTMA = 0.0f;
			int loopCount = 0;

			while (rs1.next()) {
				loopCount++;

				if (loopCount > counts && counts > 0)
					break;

				seqIndex1 = rs1.getInt(1);

				SQL = "select  seqindex from  BPMADMIN.INTRADAYSTOCKS   where symbol='"
						+ symbol
						+ "' and seqindex<"
						+ seqIndex1
						+ " and GR100= -1 order by seqIndex desc";

				rs2 = stmt2.executeQuery(SQL);

				if (rs2.next()) {
					seqIndex2 = rs2.getInt(1);
				} else {
					break;
				}

				SQL = "select  SUM(PTMA) from  BPMADMIN.INTRADAYSTOCKS   where symbol='"
						+ symbol
						+ "' and seqindex>="
						+ seqIndex2
						+ " and seqindex<" + seqIndex1;

				rs2 = stmt2.executeQuery(SQL);

				if (rs2.next()) {
					sumPTMA = rs2.getFloat(1);
					if ((seqIndex1 - seqIndex2) > 0) {
						avgPTMA = sumPTMA / (1.0f * (seqIndex1 - seqIndex2));

						SQL = "update BPMADMIN.INTRADAYSTOCKS  set ACPTMA = "
								+ avgPTMA + "  where seqIndex = "
								+ (seqIndex1 - 1) + " and SYMBOL='" + symbol
								+ "'";

						stmt3.executeUpdate(SQL);
					}
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculatePTMAVHistory(String symbol, long maxPreIndex) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018
			long tempIndex = 0;
			if (maxPreIndex > 0) {
				SQL = "select  seqindex from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and TMAI>0 AND SEQINDEX<="
						+ maxPreIndex
						+ " ORDER BY SEQINDEX DESC";
				rs1 = stmt1.executeQuery(SQL);

				int lc = 0;
				if (rs1.next()) {
					tempIndex = rs1.getLong(1);

				}

			}
			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018

			SQL = "select  VOLUME,seqindex from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' AND SEQINDEX>="
					+ maxPreIndex
					+ " ORDER BY SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);

			SQL = "select  TMAV,seqindex from  BPMADMIN.INTRADAYSTOCKS   where symbol='"
					+ symbol
					+ "' AND SEQINDEX>="
					+ tempIndex
					+ " and TMAI>0 order by seqIndex desc";

			rs2 = stmt2.executeQuery(SQL);

			float volume = 0.0f;
			float lastTMAV = 0.0f;
			int seqIndex = 0;
			int seqTMAV = 0;

			while (rs1.next()) {

				volume = rs1.getFloat(1);
				seqIndex = rs1.getInt(2);

				if (seqIndex <= seqTMAV || seqTMAV == 0) {
					if (rs2.next()) {
						seqTMAV = rs2.getInt(2);
						lastTMAV = rs2.getFloat(1);
					} else {
						break;
					}

				}

				if (seqIndex > maxPreIndex && lastTMAV > 0.001f
						&& volume > 0.001f) {
					float PTMAV = 100.0f * (volume - lastTMAV) / lastTMAV;
					SQL = "update BPMADMIN.INTRADAYSTOCKS  set PTMAV = "
							+ PTMAV + "  where seqIndex = " + seqIndex
							+ " and SYMBOL='" + symbol + "'";

					stmt3.executeUpdate(SQL);
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculatePTMAHistory(String symbol, long maxPreIndex) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  ADJUSTEDPRICE,seqindex from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' and SEQINDEX >="
					+ maxPreIndex
					+ " ORDER BY SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);

			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018
			long tempIndex = 0;
			if (maxPreIndex > 0) {
				SQL = "select  seqindex from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and TMAI>0 AND SEQINDEX<="
						+ maxPreIndex
						+ " ORDER BY SEQINDEX DESC";
				rs2 = stmt2.executeQuery(SQL);

				int lc = 0;
				if (rs2.next()) {
					tempIndex = rs2.getLong(1);
				}

			}
			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018

			SQL = "select  TMA,seqindex from  BPMADMIN.INTRADAYSTOCKS   where symbol='"
					+ symbol
					+ "' and SEQINDEX >="
					+ tempIndex
					+ " and TMA>0 order by seqIndex desc";

			rs2 = stmt2.executeQuery(SQL);

			float priceFinal = 0.0f;
			float lastTMA = 0.0f;
			int seqIndex = 0;
			int seqTMA = 0;

			while (rs1.next()) {

				priceFinal = rs1.getFloat(1);
				seqIndex = rs1.getInt(2);

				if (seqIndex <= seqTMA || seqTMA == 0) {
					if (rs2.next()) {
						seqTMA = rs2.getInt(2);
						lastTMA = rs2.getFloat(1);
					} else {
						break;
					}

				}

				if (seqIndex > maxPreIndex && lastTMA > 0.001f
						&& priceFinal > 0.001f) {
					float PTMA = 100.0f * (priceFinal - lastTMA) / lastTMA;
					SQL = "update BPMADMIN.INTRADAYSTOCKS  set PTMA = " + PTMA
							+ "  where seqIndex = " + seqIndex
							+ " and SYMBOL='" + symbol + "'";

					stmt3.executeUpdate(SQL);
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void printOutDSIAvgTrend(String symbol) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  seqIndex, VARCHAR_FORMAT(currentdate,'YYYY-MM-DD') as cdate  from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' and WWI<>0 order by seqIndex asc";

			rs1 = stmt1.executeQuery(SQL);

			int seqIndex1 = 0;
			int seqIndex2 = 0;
			String date1 = "";
			String date2 = "";

			while (rs1.next()) {
				if (seqIndex1 == 0) {
					seqIndex1 = rs1.getInt(1);
					date1 = rs1.getString(2);
				} else if (seqIndex2 == 0) {
					seqIndex2 = rs1.getInt(1);
					date2 = rs1.getString(2);
				} else {
					float avg = calculateAvgDSI(symbol, seqIndex1, seqIndex2);
					seqIndex1 = seqIndex2;
					date1 = date2;
					seqIndex2 = rs1.getInt(1);
					date2 = rs1.getString(2);

					System.out.println(symbol + " between " + date1 + " and "
							+ date2 + " avg dsi is " + avg);
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static float calculateAvgDSI(String symbol, int startIndex,
			int endIndex) {
		float avg = 0.0f;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt2 == null)
				stmt2 = con.createStatement();

			SQL = "select  AVG(DSI5)  from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' and SEQINDEX >"
					+ startIndex
					+ " and  SEQINDEX<=" + endIndex;

			rs2 = stmt2.executeQuery(SQL);

			if (rs2.next()) {

				avg = rs2.getFloat(1);

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return avg;
	}

	public static void calculatePTMA(String symbol, int seqIndex) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  ADJUSTEDPRICE,seqindex from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' and SEQINDEX =" + seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			SQL = "select  TMA,seqindex from  BPMADMIN.INTRADAYSTOCKS   where symbol='"
					+ symbol
					+ "' and TMA>0.001 AND SEQINDEX<="
					+ seqIndex
					+ " order by seqIndex desc";

			rs2 = stmt2.executeQuery(SQL);

			float priceFinal = 0.0f;
			float lastTMA = 0.0f;
			int seqTMA = 0;

			if (rs1.next()) {

				priceFinal = rs1.getFloat(1);
				seqIndex = rs1.getInt(2);

				if (rs2.next()) {
					seqTMA = rs2.getInt(2);
					lastTMA = rs2.getFloat(1);
				}

				if (lastTMA > 0.001f && priceFinal > 0.001f) {
					float PTMA = 100.0f * (priceFinal - lastTMA) / lastTMA;
					SQL = "update BPMADMIN.INTRADAYSTOCKS  set PTMA = " + PTMA
							+ "  where seqIndex = " + seqIndex
							+ " and SYMBOL='" + symbol + "'";

					stmt3.executeUpdate(SQL);
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static int[] findStableIndex(String symbol, String mode,
			float maxChange, int durationDays, int endIndex) {
		int[] qindexes = new int[2000];
		int[] rindexes = null;
		int days = 30;
		float[] finalprices = new float[days];
		float[] acptmas = new float[days];
		float[] ptmas = new float[days];
		float[] dsis = new float[days];
		int[] indexes = new int[days];
		float[] dsi5s = new float[days];
		float[] dsi3s = new float[days];
		String[] dates = new String[days];
		float[] highs = new float[days];
		float[] lows = new float[days];
		float[] opens = new float[days];
		int qc = 0;
		int nextIndexStart = 0;
		int totalCount = 0;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();
			SQL = "Update BPMADMIN.INTRADAYSTOCKS SET BBGO=0 where symbol='"
					+ symbol + "'";
			stmt2.executeUpdate(SQL);

			SQL = "select seqindex,ACPTMA,PTMA,DSI,DSI5,FINALPRICE,DSI3,VARCHAR_FORMAT(CURRENTDATE,'MM-DD-YYYY') AS VCHAR_FORMATED,HIGHPRICE,LOWPRICE,OPENPRICE  from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "'  order by seqIndex asc";

			rs1 = stmt1.executeQuery(SQL);

			int loopCount = 0;
			int trackCount = 1;
			// while loop 1 start
			while (rs1.next()) {
				totalCount++;
				if (loopCount < days) {
					indexes[loopCount] = rs1.getInt(1);
					acptmas[loopCount] = rs1.getFloat(2);
					ptmas[loopCount] = rs1.getFloat(3);
					dsis[loopCount] = rs1.getFloat(4);
					dsi5s[loopCount] = rs1.getFloat(5);
					finalprices[loopCount] = rs1.getFloat(6);
					dsi3s[loopCount] = rs1.getFloat(7);
					dates[loopCount] = rs1.getString(8);
					highs[loopCount] = rs1.getFloat(9);
					lows[loopCount] = rs1.getFloat(10);
					opens[loopCount] = rs1.getFloat(11);
				} else {
					boolean qualified = false;

					float maxPrice = 0.0f;
					float minPrice = 0.0f;

					for (int w = nextIndexStart; w < nextIndexStart
							+ durationDays; w++) {
						if (mode.equalsIgnoreCase("final")) {
							if (w == nextIndexStart) {
								maxPrice = finalprices[w % days];
								minPrice = finalprices[w % days];
							}

							if (finalprices[w % days] > (maxPrice + 0.0001)) {
								maxPrice = finalprices[w % days];
							}

							if (finalprices[w % days] < (minPrice - 0.0001)) {
								minPrice = finalprices[w % days];
							}
						}

					}

					float changeP = 100.0f * (maxPrice - minPrice) / minPrice;

					if (changeP < maxChange) {
						qualified = true;
					}

					if (qualified) {
						qindexes[qc] = indexes[nextIndexStart];
						qc++;
						String SQL2 = "";
						int loop = 0;
						for (int z = nextIndexStart; z < nextIndexStart
								+ durationDays; z++) {
							loop++;
							SQL2 = "Update BPMADMIN.INTRADAYSTOCKS SET BBGO = "
									+ loop + " where symbol='" + symbol
									+ "'  and seqIndex = " + indexes[z % days];

							stmt2.executeUpdate(SQL2);
						}

					}

					indexes[nextIndexStart] = rs1.getInt(1);
					acptmas[nextIndexStart] = rs1.getFloat(2);
					ptmas[nextIndexStart] = rs1.getFloat(3);
					dsis[nextIndexStart] = rs1.getFloat(4);
					dsi5s[nextIndexStart] = rs1.getFloat(5);
					finalprices[nextIndexStart] = rs1.getFloat(6);
					dsi3s[nextIndexStart] = rs1.getFloat(7);
					dates[nextIndexStart] = rs1.getString(8);
					highs[nextIndexStart] = rs1.getFloat(9);
					lows[nextIndexStart] = rs1.getFloat(10);
					opens[nextIndexStart] = rs1.getFloat(11);

					nextIndexStart = (nextIndexStart + 1) % 30;
				}
				loopCount++;
			}

			System.out.println(" out of  " + totalCount + " has " + qc
					+ " qualified for " + durationDays
					+ " days with price fluctuation less than " + maxChange
					+ "%");
			rindexes = new int[qc];
			for (int k = 0; k < qc; k++) {
				rindexes[k] = qindexes[k];
			}
		} catch (Exception ex) {

		}

		return rindexes;
	}

	public static int[] multiQualificationIndex(String symbol) {
		int[] qindexes = new int[2000];
		int[] rindexes = null;
		int days = 30;
		float[] finalprices = new float[days];
		float[] acptmas = new float[days];
		float[] ptmas = new float[days];
		float[] dsis = new float[days];
		int[] indexes = new int[days];
		float[] dsi5s = new float[days];
		float[] dsi3s = new float[days];
		String[] dates = new String[days];
		float[] delta1s = new float[days];
		float[] delta2s = new float[days];
		float[] delta3s = new float[days];
		float[] delta4s = new float[days];
		float[] delta5s = new float[days];
		int qc = 0;
		int nextIndexStart = 0;
		int totalCount = 0;
		float avgDelat1, avgDelat2, avgDelat3, avgDelat4, avgDelat5, deltaSum1, deltaSum2;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select seqindex,ACPTMA,PTMA,DSI,DSI5,FINALPRICE,DSI3,VARCHAR_FORMAT(CURRENTDATE,'MM-DD-YYYY') AS VCHAR_FORMATED,DELTA1,DELTA2,DELTA3,DELTA4,DELTA5  from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "'  order by seqIndex desc";

			rs1 = stmt1.executeQuery(SQL);

			int loopCount = 0;
			int trackCount = 1;
			// while loop 1 start
			while (rs1.next()) {
				totalCount++;
				if (loopCount < days) {
					indexes[loopCount] = rs1.getInt(1);
					acptmas[loopCount] = rs1.getFloat(2);
					ptmas[loopCount] = rs1.getFloat(3);
					dsis[loopCount] = rs1.getFloat(4);
					dsi5s[loopCount] = rs1.getFloat(5);
					finalprices[loopCount] = rs1.getFloat(6);
					dsi3s[loopCount] = rs1.getFloat(7);
					dates[loopCount] = rs1.getString(8);
					delta1s[loopCount] = rs1.getFloat(9);
					delta2s[loopCount] = rs1.getFloat(10);
					delta3s[loopCount] = rs1.getFloat(11);
					delta4s[loopCount] = rs1.getFloat(12);
					delta5s[loopCount] = rs1.getFloat(13);
				} else {
					boolean qualified = false;
					avgDelat1 = 0.0f;
					avgDelat2 = 0.0f;
					avgDelat3 = 0.0f;
					avgDelat4 = 0.0f;
					avgDelat5 = 0.0f;
					for (int w = 0; w < 5; w++) {
						int ni = (nextIndexStart + w) % days;
						avgDelat1 = avgDelat1 + delta1s[ni];
						avgDelat2 = avgDelat2 + delta1s[ni];
						avgDelat3 = avgDelat3 + delta1s[ni];
						avgDelat4 = avgDelat4 + delta1s[ni];
						avgDelat5 = avgDelat5 + delta1s[ni];
					}

					avgDelat1 = avgDelat1 / 5.0f;
					avgDelat2 = avgDelat2 / 5.0f;
					avgDelat3 = avgDelat3 / 5.0f;
					avgDelat4 = avgDelat4 / 5.0f;
					avgDelat5 = avgDelat5 / 5.0f;

					deltaSum1 = 0.0f;
					deltaSum2 = 0.0f;
					for (int w = 0; w < 2; w++) {
						int ni = (nextIndexStart + w) % days;
						if (w == 0) {
							deltaSum1 = delta1s[ni] + delta2s[ni] + delta3s[ni]
									+ delta4s[ni] + delta5s[ni];
						} else if (w == 1) {
							deltaSum2 = delta1s[ni] + delta2s[ni] + delta3s[ni]
									+ delta4s[ni] + delta5s[ni];
						}
					}

					if (avgDelat1 < 0.77f && avgDelat2 < 1.0f
							&& deltaSum1 < 5.5f && deltaSum2 < 5.5f) {
						qualified = true;
					}

					if (qualified) {
						qindexes[qc] = indexes[nextIndexStart];
						qc++;
					}

					indexes[nextIndexStart] = rs1.getInt(1);
					acptmas[nextIndexStart] = rs1.getFloat(2);
					ptmas[nextIndexStart] = rs1.getFloat(3);
					dsis[nextIndexStart] = rs1.getFloat(4);
					dsi5s[nextIndexStart] = rs1.getFloat(5);
					finalprices[nextIndexStart] = rs1.getFloat(6);
					dsi3s[nextIndexStart] = rs1.getFloat(7);
					dates[nextIndexStart] = rs1.getString(8);
					delta1s[nextIndexStart] = rs1.getFloat(9);
					delta2s[nextIndexStart] = rs1.getFloat(10);
					delta3s[nextIndexStart] = rs1.getFloat(11);
					delta4s[nextIndexStart] = rs1.getFloat(12);
					delta5s[nextIndexStart] = rs1.getFloat(13);

					nextIndexStart = (nextIndexStart + 1) % 30;
				}
				loopCount++;
			}

			System.out.println(" out of  " + totalCount + " has " + qc
					+ " double qualified");
			rindexes = new int[qc];
			for (int k = 0; k < qc; k++) {
				rindexes[k] = qindexes[k];
			}
		} catch (Exception ex) {

		}

		return rindexes;
	}

	public static void calculateDeltaStableStatistics(String symbol) {

		int days = 30;
		int watchStep = 5;
		int totalCount = 0;
		float targetNeg = -0.6f;
		float targetPos = 0.6f;
		float[] finalprices = new float[days];
		float[] acptmas = new float[days];
		float[] ptmas = new float[days];
		int[] indexes = new int[days];
		float[] dsis = new float[days];
		float[] dsi5s = new float[days];
		String[] dates = new String[days];
		float day5Yield = 1.0f;
		float day10Yield = 1.0f;
		float day15Yield = 1.0f;
		float grandYield = 1.0f;
		int grandTradeCount = 0;
		int mildTotal5 = 0;
		int mildTotal10 = 0;
		int mildTotal15 = 0;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select seqindex from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' and FFP=1 order by seqIndex asc";
			rs2 = stmt2.executeQuery(SQL);

			// int[] rindexes = multiQualificationIndex("SPY");

			// while loop 2 start
			while (rs2.next()) {
				// for (int z = 0; z < rindexes.length; z++) {
				int nextSeqIndex = rs2.getInt(1);
				// int nextSeqIndex = rindexes[z];

				SQL = "select seqindex,ACPTMA,PTMA,DSI,DSI5,FINALPRICE,VARCHAR_FORMAT(CURRENTDATE,'MM-DD-YYYY') AS VCHAR_FORMATED from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and  seqIndex >="
						+ nextSeqIndex
						+ " and seqIndex<"
						+ (nextSeqIndex + 50)
						+ " order by seqIndex asc";

				rs1 = stmt1.executeQuery(SQL);

				int loopCount = 0;
				int trackCount = 1;
				// while loop 1 start
				while (rs1.next()) {

					// IF loop 3 start
					if (loopCount < days) {
						indexes[loopCount] = rs1.getInt(1);
						acptmas[loopCount] = rs1.getFloat(2);
						ptmas[loopCount] = rs1.getFloat(3);
						dsis[loopCount] = rs1.getFloat(4);
						dsi5s[loopCount] = rs1.getFloat(5);
						finalprices[loopCount] = rs1.getFloat(6);
						dates[loopCount] = rs1.getString(7);
					} else {
						totalCount++;

						boolean calculateYield = true;
						// for now commented out this filter by ptma or acptma
						// condition
						// looks like the stable filter pretty much takes care
						// of it anyway

						/*
						 * int backDayCount = 20; for (int w = 0; w <
						 * backDayCount; w++) { int nextIndex = trackCount - w;
						 * if (nextIndex < 0) nextIndex = nextIndex + days; if
						 * (acptmas[nextIndex] < 2.0f) { calculateYield = false;
						 * break; } if (ptmas[nextIndex] < 4.0f) {
						 * calculateYield = false; break; } } if
						 * (calculateYield) { tradeCount++; grandTradeCount++; }
						 */
						float day5Delta = 100.0f
								* (finalprices[(watchStep - 1 + trackCount)
										% days] - finalprices[trackCount])
								/ finalprices[trackCount];
						if (day5Delta > targetNeg && day5Delta < targetPos) {
							System.out
									.println("index "
											+ indexes[trackCount]
											+ dates[trackCount]
											+ " to "
											+ indexes[(watchStep - 1 + trackCount)
													% days]
											+ " "
											+ dates[(watchStep - 1 + trackCount)
													% days]
											+ " 5 days price change is "
											+ day5Delta);
							if (day5Delta > 0.1f * targetNeg
									&& day5Delta < 0.1f * targetPos) {
								day5Yield = day5Yield * 1.40f;
							} else if (day5Delta > 0.2f * targetNeg
									&& day5Delta < 0.2f * targetPos) {
								day5Yield = day5Yield * 1.30f;
							} else if (day5Delta > 0.5f * targetNeg
									&& day5Delta < 0.5f * targetPos) {
								day5Yield = day5Yield * 1.2f;
							} else if (day5Delta > targetNeg
									&& day5Delta < targetPos) {
								day5Yield = day5Yield * 1.05f;
							}
							mildTotal5++;
						} else {
							if (calculateYield)
								day5Yield = day5Yield * 0.75f;
							System.out
									.println("index "
											+ indexes[trackCount]
											+ dates[trackCount]
											+ " to "
											+ indexes[(watchStep - 1 + trackCount)
													% days]
											+ " "
											+ dates[(watchStep - 1 + trackCount)
													% days]
											+ " 5 days price change is "
											+ day5Delta);
						}

						float day10Delta = 100.0f
								* (finalprices[(watchStep * 2 - 1 + trackCount)
										% days] - finalprices[trackCount])
								/ finalprices[trackCount];
						if (day10Delta > targetNeg && day10Delta < targetPos) {
							mildTotal10++;
							System.out.println("index "
									+ indexes[trackCount]
									+ " "
									+ dates[trackCount]
									+ " to "
									+ indexes[(watchStep * 2 - 1 + trackCount)
											% days]
									+ " "
									+ dates[(watchStep * 2 - 1 + trackCount)
											% days]
									+ " 10 days price change is " + day10Delta);

							day10Yield = day10Yield * 1.15f;
						} else {
							day10Yield = day10Yield * 0.71f;
							System.out.println("index "
									+ indexes[trackCount]
									+ dates[trackCount]
									+ " to "
									+ indexes[(watchStep * 2 - 1 + trackCount)
											% days]
									+ " "
									+ dates[(watchStep * 2 - 1 + trackCount)
											% days]
									+ " 10 days price change is " + day10Delta);
						}

						float day15Delta = 100.0f
								* (finalprices[(watchStep * 3 - 1 + trackCount)
										% days] - finalprices[trackCount])
								/ finalprices[trackCount];
						if (day15Delta > targetNeg && day15Delta < targetPos) {
							mildTotal15++;
							day15Yield = day15Yield * 1.15f;
							System.out.println("index "
									+ indexes[trackCount]
									+ " "
									+ dates[trackCount]
									+ " to "
									+ indexes[(watchStep * 3 - 1 + trackCount)
											% days]
									+ " "
									+ dates[(watchStep * 3 - 1 + trackCount)
											% days]
									+ " 15 days price change is " + day15Delta);

						} else {
							day15Yield = day15Yield * 0.71f;
							System.out.println("index "
									+ indexes[trackCount]
									+ dates[trackCount]
									+ " to "
									+ indexes[(watchStep * 3 - 1 + trackCount)
											% days]
									+ " "
									+ dates[(watchStep * 3 - 1 + trackCount)
											% days]
									+ " 15 days price change is " + day15Delta);
						}
						break;
					}// IF loop 3 end

					loopCount++;
				}// while loop 1 end
			}// while loop 2 end

			System.out.println("mildTotal5 = " + mildTotal5
					+ " grand trade count " + totalCount + " ratio "
					+ (100.0f * mildTotal5) / (1.0f * totalCount));
			System.out.println("day5Yield = " + day5Yield);
			System.out.println("mildTotal0 = " + mildTotal10
					+ " grand trade count " + totalCount + " ratio "
					+ (100.0f * mildTotal10) / (1.0f * totalCount));
			System.out.println("day10Yield = " + day10Yield);
			System.out.println("mildTotal15 = " + mildTotal15
					+ " grand trade count " + totalCount + " ratio "
					+ (100.0f * mildTotal15) / (1.0f * totalCount));

			System.out.println("day15Yield = " + day15Yield);
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateEntireAWSStatistics(int seqIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			/*
			 * results = new Hashtable(); results.put("BZUN", "BZUN");
			 * results.put("PBYI", "PBYI"); results.put("AAOI", "AAOI");
			 * results.put("AKAO", "AKAO"); results.put("ACH", "ACH");
			 * results.put("CUTR", "CUTR"); results.put("KRA", "KRA");
			 * results.put("AEIS", "AEIS"); results.put("ANET", "ANET");
			 */
			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				calculateAWSStatistics(nextStock);
				long t2 = System.currentTimeMillis();
				System.out.println(nextStock
						+ " Stock AWSStatistics calculation done "
						+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;

				if (loopCount % 101 == 0) {
					System.out.println(loopCount + " stocks have been done");
					printAWSStatisticsAverage();
					Thread.sleep(25000);
				} else if (loopCount % 1000 == 0) {
					printAWSStatisticsAverage();
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(120000);
				}
			}
			printAWSStatisticsAverage();

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	private static void printAWSStatisticsAverage() {
		System.out.println("awsdLength: " + awsdLength + ", awsdCount:"
				+ awsdCount);
		System.out.println("Average awsdLength " + awsdLength / awsdCount);

		System.out.println("awsuLength: " + awsuLength + ", awsuCount:"
				+ awsuCount);
		System.out.println("Average awsuLength " + awsuLength / awsuCount);

		System.out.println("awsduLength: " + awsduLength + ", awsduCount:"
				+ awsduCount + ", awsduYield: " + awsduYield);
		System.out.println("Average awsduLength " + awsduLength / awsduCount);
		System.out.println("Average awsduYield " + awsduYield / awsduCount);

		System.out.println("awsudLength: " + awsudLength + ", awsudCount:"
				+ awsudCount + ", awsudYield: " + awsudYield);
		System.out.println("Average awsudLength " + awsudLength / awsudCount);
		System.out.println("Average awsudYield " + awsudYield / awsudCount);

		System.out.println("awsuuLength: " + awsuuLength + ", awsuuCount:"
				+ awsuuCount + ", awsuuYield: " + awsuuYield);
		System.out.println("Average awsuuLength " + awsuuLength / awsuuCount);
		System.out.println("Average awsuuYield " + awsuuYield / awsuuCount);

		System.out.println("awsddLength: " + awsddLength + ", awsddCount:"
				+ awsddCount + ", awsddYield: " + awsddYield);
		System.out.println("Average awsddLength " + awsddLength / awsddCount);
		System.out.println("Average awsddYield " + awsddYield / awsddCount);

	}

	public static void calculateAWSStatistics(String symbol) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select SEQINDEX,PTS,FINALPRICE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' AND PTS<>0 order by seqIndex asc";
			rs1 = stmt1.executeQuery(SQL);

			int index1 = 0;
			int pts1 = 0;
			float price1 = 0.0f;
			int index2 = 0;
			int pts2 = 0;
			float price2 = 0.0f;
			int index3 = 0;
			int pts3 = 0;
			float price3 = 0.0f;
			int index4 = 0;
			int pts4 = 0;
			float price4 = 0.0f;

			while (rs1.next()) {
				int nextSeqIndex = rs1.getInt(1);
				int nextPTS = rs1.getInt(2);
				float price = rs1.getFloat(3);

				if (index1 == 0) {
					index1 = nextSeqIndex;
					pts1 = nextPTS;
					price1 = price;
				} else if (index2 == 0) {
					index2 = nextSeqIndex;
					pts2 = nextPTS;
					price2 = price;
				} else if (index3 == 0) {
					index3 = nextSeqIndex;
					pts3 = nextPTS;
					price3 = price;
				} else if (index4 == 0) {
					index4 = nextSeqIndex;
					pts4 = nextPTS;
					price4 = price;
				} else {
					SQL = "select COUNT(*) from BPMADMIN.INTRADAYSTOCKS  where symbol='"
							+ symbol
							+ "' AND  seqIndex >= "
							+ index1
							+ " AND seqIndex <= " + index2;
					System.out.println(SQL);
					rs2 = stmt2.executeQuery(SQL);
					int tempDays1 = 0;

					if (rs2.next()) {
						tempDays1 = rs2.getInt(1);
						System.out.println("tempDays1 " + tempDays1);
					}

					SQL = "select COUNT(*) from BPMADMIN.INTRADAYSTOCKS  where symbol='"
							+ symbol
							+ "' AND  seqIndex >= "
							+ index1
							+ " AND seqIndex <= " + index3;
					System.out.println(SQL);
					rs2 = stmt2.executeQuery(SQL);
					int tempDays2 = 0;

					if (rs2.next()) {
						tempDays2 = rs2.getInt(1);
						System.out.println("tempDays2 " + tempDays2);
					}

					if (pts1 > 0) {
						System.out.println(index1 + " pst:" + pts1);
						awsuLength = awsuLength + pts1 % 10000;
						System.out.println("awsuLength " + awsuLength);
						awsuCount = awsuCount + 1;
						System.out.println("awsuCount " + awsuCount);
						awsudLength = awsudLength + tempDays1;
						System.out.println("awsudLength " + awsudLength);
						awsudCount = awsudCount + 1;
						System.out.println("awsudCount " + awsudCount);
						float yield1 = 100.0f * (price2 - price1) / price1;
						System.out.println("yield1 " + yield1);
						awsudYield = awsudYield + yield1;
						System.out.println("awsudYield " + awsudYield);
						awsuuLength = awsuuLength + tempDays2;
						System.out.println("awsuuLength " + awsuuLength);
						awsuuCount = awsuuCount + 1;
						System.out.println("awsuuCount " + awsuuCount);
						float yield2 = 100.0f * (price3 - price1) / price1;
						System.out.println("yield2 " + yield2);
						awsuuYield = awsuuYield + yield2;
						System.out.println("awsuuYield " + awsuuYield);

					} else if (pts1 < 0) {
						System.out.println(index1 + " pst:" + pts1);
						awsdLength = awsdLength - pts1 % 10000;
						System.out.println("awsdLength " + awsdLength);
						awsdCount = awsdCount + 1;
						System.out.println("awsdCount " + awsdCount);
						awsduLength = awsduLength + tempDays1;
						System.out.println("awsduLength " + awsduLength);
						awsduCount = awsduCount + 1;
						System.out.println("awsduCount " + awsduCount);
						float yield1 = 100.0f * (price2 - price1) / price1;
						System.out.println("yield1 " + yield1);
						awsduYield = awsduYield + yield1;
						System.out.println("awsduYield " + awsduYield);
						awsddLength = awsddLength + tempDays2;
						System.out.println("awsddLength " + awsddLength);
						awsddCount = awsddCount + 1;
						System.out.println("awsddCount " + awsddCount);
						float yield2 = 100.0f * (price3 - price1) / price1;
						System.out.println("yield2 " + yield2);
						awsddYield = awsddYield + yield2;
						System.out.println("awsddYield " + awsddYield);
					}

					index1 = index2;
					pts1 = pts2;
					price1 = price2;
					index2 = index3;
					pts2 = pts3;
					price2 = price3;
					index3 = index4;
					pts3 = pts4;
					price3 = price4;
					index4 = nextSeqIndex;
					pts4 = nextPTS;
					price4 = price;

				}
			}

			SQL = "select COUNT(*) from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' AND  seqIndex >= "
					+ index1
					+ " AND seqIndex <= " + index2;
			System.out.println(SQL);
			rs2 = stmt2.executeQuery(SQL);
			int tempDays1 = 0;

			if (rs2.next()) {
				tempDays1 = rs2.getInt(1);
				System.out.println("tempDays1 " + tempDays1);
			}

			SQL = "select COUNT(*) from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' AND  seqIndex >= "
					+ index1
					+ " AND seqIndex <= " + index3;
			System.out.println(SQL);
			rs2 = stmt2.executeQuery(SQL);
			int tempDays2 = 0;

			if (rs2.next()) {
				tempDays2 = rs2.getInt(1);
				System.out.println("tempDays2 " + tempDays2);
			}

			if (pts1 > 0) {
				System.out.println(index1 + " pst:" + pts1);
				awsuLength = awsuLength + pts1 % 10000;
				System.out.println("awsuLength " + awsuLength);
				awsuCount = awsuCount + 1;
				System.out.println("awsuCount " + awsuCount);
				awsudLength = awsudLength + tempDays1;
				System.out.println("awsudLength " + awsudLength);
				awsudCount = awsudCount + 1;
				System.out.println("awsudCount " + awsudCount);
				float yield1 = 100.0f * (price2 - price1) / price1;
				System.out.println("yield1 " + yield1);
				awsudYield = awsudYield + yield1;
				System.out.println("awsudYield " + awsudYield);
				awsuuLength = awsuuLength + tempDays2;
				System.out.println("awsuuLength " + awsuuLength);
				awsuuCount = awsuuCount + 1;
				System.out.println("awsuuCount " + awsuuCount);
				float yield2 = 100.0f * (price3 - price1) / price1;
				System.out.println("yield2 " + yield2);
				awsuuYield = awsuuYield + yield2;
				System.out.println("awsuuYield " + awsuuYield);

			} else if (pts1 < 0) {
				System.out.println(index1 + " pst:" + pts1);
				awsdLength = awsdLength - pts1 % 10000;
				System.out.println("awsdLength " + awsdLength);
				awsdCount = awsdCount + 1;
				System.out.println("awsdCount " + awsdCount);
				awsduLength = awsduLength + tempDays1;
				System.out.println("awsduLength " + awsduLength);
				awsduCount = awsduCount + 1;
				System.out.println("awsduCount " + awsduCount);
				float yield1 = 100.0f * (price2 - price1) / price1;
				System.out.println("yield1 " + yield1);
				awsduYield = awsduYield + yield1;
				System.out.println("awsduYield " + awsduYield);
				awsddLength = awsddLength + tempDays2;
				System.out.println("awsddLength " + awsddLength);
				awsddCount = awsddCount + 1;
				System.out.println("awsddCount " + awsddCount);
				float yield2 = 100.0f * (price3 - price1) / price1;
				System.out.println("yield2 " + yield2);
				awsddYield = awsddYield + yield2;
				System.out.println("awsddYield " + awsddYield);
			}

			SQL = "select COUNT(*) from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' AND  seqIndex >= "
					+ index2
					+ " AND seqIndex <= " + index3;
			System.out.println(SQL);
			rs2 = stmt2.executeQuery(SQL);
			tempDays1 = 0;

			if (rs2.next()) {
				tempDays1 = rs2.getInt(1);
				System.out.println("tempDays1 " + tempDays1);
			}

			SQL = "select COUNT(*) from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' AND  seqIndex >= "
					+ index2
					+ " AND seqIndex <= " + index4;
			System.out.println(SQL);
			rs2 = stmt2.executeQuery(SQL);
			tempDays2 = 0;

			if (rs2.next()) {
				tempDays2 = rs2.getInt(1);
				System.out.println("tempDays2 " + tempDays2);
			}

			if (pts2 > 0) {
				System.out.println(index2 + " pst:" + pts2);
				awsuLength = awsuLength + pts2 % 10000;
				System.out.println("awsuLength " + awsuLength);
				awsuCount = awsuCount + 1;
				System.out.println("awsuCount " + awsuCount);
				awsudLength = awsudLength + tempDays1;
				System.out.println("awsudLength " + awsudLength);
				awsudCount = awsudCount + 1;
				System.out.println("awsudCount " + awsudCount);
				float yield1 = 100.0f * (price3 - price2) / price2;
				System.out.println("yield1 " + yield1);
				awsudYield = awsudYield + yield1;
				System.out.println("awsudYield " + awsudYield);
				awsuuLength = awsuuLength + tempDays2;
				System.out.println("awsuuLength " + awsuuLength);
				awsuuCount = awsuuCount + 1;
				System.out.println("awsuuCount " + awsuuCount);
				float yield2 = 100.0f * (price4 - price2) / price2;
				System.out.println("yield2 " + yield2);
				awsuuYield = awsuuYield + yield2;
				System.out.println("awsuuYield " + awsuuYield);

			} else if (pts2 < 0) {
				System.out.println(index2 + " pst:" + pts2);
				awsdLength = awsdLength - pts2 % 10000;
				System.out.println("awsdLength " + awsdLength);
				awsdCount = awsdCount + 1;
				System.out.println("awsdCount " + awsdCount);
				awsduLength = awsduLength + tempDays1;
				System.out.println("awsduLength " + awsduLength);
				awsduCount = awsduCount + 1;
				System.out.println("awsduCount " + awsduCount);
				float yield1 = 100.0f * (price3 - price2) / price2;
				System.out.println("yield1 " + yield1);
				awsduYield = awsduYield + yield1;
				System.out.println("awsduYield " + awsduYield);
				awsddLength = awsddLength + tempDays2;
				System.out.println("awsddLength " + awsddLength);
				awsddCount = awsddCount + 1;
				System.out.println("awsddCount " + awsddCount);
				float yield2 = 100.0f * (price4 - price2) / price2;
				System.out.println("yield2 " + yield2);
				awsddYield = awsddYield + yield2;
				System.out.println("awsddYield " + awsddYield);
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}

	public static void calculateSPYDSIStableStatistics(String symbol) {

		int days = 30;
		int watchStep = 5;
		int totalCount = 0;
		float targetNeg = -1.0f;
		float targetPos = 1.0f;
		float[] finalprices = new float[days];
		float[] acptmas = new float[days];
		float[] ptmas = new float[days];
		int[] indexes = new int[days];
		float[] dsis = new float[days];
		float[] dsi5s = new float[days];
		String[] dates = new String[days];
		float day5Yield = 1.0f;
		float day10Yield = 1.0f;
		float day15Yield = 1.0f;
		float grandYield = 1.0f;
		int grandTradeCount = 0;
		int mildTotal5 = 0;
		int mildTotal10 = 0;
		int mildTotal15 = 0;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select seqindex from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' and FFP=1 order by seqIndex asc";
			rs2 = stmt2.executeQuery(SQL);

			// while loop 2 start
			while (rs2.next()) {
				int nextSeqIndex = rs2.getInt(1);

				SQL = "select seqindex,ACPTMA,PTMA,DSI,DSI5,FINALPRICE,VARCHAR_FORMAT(CURRENTDATE,'MM-DD-YYYY') AS VCHAR_FORMATED from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and  seqIndex >="
						+ nextSeqIndex
						+ " and seqIndex<"
						+ (nextSeqIndex + 50)
						+ " order by seqIndex asc";

				rs1 = stmt1.executeQuery(SQL);

				int loopCount = 0;
				int trackCount = 1;
				// while loop 1 start
				while (rs1.next()) {

					// IF loop 3 start
					if (loopCount < days) {
						indexes[loopCount] = rs1.getInt(1);
						acptmas[loopCount] = rs1.getFloat(2);
						ptmas[loopCount] = rs1.getFloat(3);
						dsis[loopCount] = rs1.getFloat(4);
						dsi5s[loopCount] = rs1.getFloat(5);
						finalprices[loopCount] = rs1.getFloat(6);
						dates[loopCount] = rs1.getString(7);
					} else {
						totalCount++;

						boolean calculateYield = true;
						// for now commented out this filter by ptma or acptma
						// condition
						// looks like the stable filter pretty much takes care
						// of it anyway

						/*
						 * int backDayCount = 20; for (int w = 0; w <
						 * backDayCount; w++) { int nextIndex = trackCount - w;
						 * if (nextIndex < 0) nextIndex = nextIndex + days; if
						 * (acptmas[nextIndex] < 2.0f) { calculateYield = false;
						 * break; } if (ptmas[nextIndex] < 4.0f) {
						 * calculateYield = false; break; } } if
						 * (calculateYield) { tradeCount++; grandTradeCount++; }
						 */
						float day5Delta = 100.0f
								* (finalprices[(watchStep - 1 + trackCount)
										% days] - finalprices[trackCount])
								/ finalprices[trackCount];
						if (day5Delta > targetNeg && day5Delta < targetPos) {
							System.out
									.println("index "
											+ indexes[trackCount]
											+ dates[trackCount]
											+ " to "
											+ indexes[(watchStep - 1 + trackCount)
													% days]
											+ " "
											+ dates[(watchStep - 1 + trackCount)
													% days]
											+ " 5 days price change is "
											+ day5Delta);
							if (day5Delta > 0.1f * targetNeg
									&& day5Delta < 0.1f * targetPos) {
								day5Yield = day5Yield * 1.40f;
							} else if (day5Delta > 0.2f * targetNeg
									&& day5Delta < 0.2f * targetPos) {
								day5Yield = day5Yield * 1.30f;
							} else if (day5Delta > 0.5f * targetNeg
									&& day5Delta < 0.5f * targetPos) {
								day5Yield = day5Yield * 1.2f;
							} else if (day5Delta > targetNeg
									&& day5Delta < targetPos) {
								day5Yield = day5Yield * 1.05f;
							}
							mildTotal5++;
						} else {
							if (calculateYield)
								day5Yield = day5Yield * 0.75f;
							System.out
									.println("index "
											+ indexes[trackCount]
											+ dates[trackCount]
											+ " to "
											+ indexes[(watchStep - 1 + trackCount)
													% days]
											+ " "
											+ dates[(watchStep - 1 + trackCount)
													% days]
											+ " 5 days price change is "
											+ day5Delta);
						}

						float day10Delta = 100.0f
								* (finalprices[(watchStep * 2 - 1 + trackCount)
										% days] - finalprices[trackCount])
								/ finalprices[trackCount];
						if (day10Delta > targetNeg && day10Delta < targetPos) {
							mildTotal10++;
							System.out.println("index "
									+ indexes[trackCount]
									+ " "
									+ dates[trackCount]
									+ " to "
									+ indexes[(watchStep * 2 - 1 + trackCount)
											% days]
									+ " "
									+ dates[(watchStep * 2 - 1 + trackCount)
											% days]
									+ " 10 days price change is " + day10Delta);

							day10Yield = day10Yield * 1.15f;
						} else {
							day10Yield = day10Yield * 0.71f;
							System.out.println("index "
									+ indexes[trackCount]
									+ dates[trackCount]
									+ " to "
									+ indexes[(watchStep * 2 - 1 + trackCount)
											% days]
									+ " "
									+ dates[(watchStep * 2 - 1 + trackCount)
											% days]
									+ " 10 days price change is " + day10Delta);
						}

						float day15Delta = 100.0f
								* (finalprices[(watchStep * 3 - 1 + trackCount)
										% days] - finalprices[trackCount])
								/ finalprices[trackCount];
						if (day15Delta > targetNeg && day15Delta < targetPos) {
							mildTotal15++;
							day15Yield = day15Yield * 1.15f;
							System.out.println("index "
									+ indexes[trackCount]
									+ " "
									+ dates[trackCount]
									+ " to "
									+ indexes[(watchStep * 3 - 1 + trackCount)
											% days]
									+ " "
									+ dates[(watchStep * 3 - 1 + trackCount)
											% days]
									+ " 15 days price change is " + day15Delta);

						} else {
							day15Yield = day15Yield * 0.71f;
							System.out.println("index "
									+ indexes[trackCount]
									+ dates[trackCount]
									+ " to "
									+ indexes[(watchStep * 3 - 1 + trackCount)
											% days]
									+ " "
									+ dates[(watchStep * 3 - 1 + trackCount)
											% days]
									+ " 15 days price change is " + day15Delta);
						}
						break;
					}// IF loop 3 end

					loopCount++;
				}// while loop 1 end
			}// while loop 2 end

			System.out.println("mildTotal5 = " + mildTotal5
					+ " grand trade count " + totalCount + " ratio "
					+ (100.0f * mildTotal5) / (1.0f * totalCount));
			System.out.println("day5Yield = " + day5Yield);
			System.out.println("mildTotal0 = " + mildTotal10
					+ " grand trade count " + totalCount + " ratio "
					+ (100.0f * mildTotal10) / (1.0f * totalCount));
			System.out.println("day10Yield = " + day10Yield);
			System.out.println("mildTotal15 = " + mildTotal15
					+ " grand trade count " + totalCount + " ratio "
					+ (100.0f * mildTotal15) / (1.0f * totalCount));

			System.out.println("day15Yield = " + day15Yield);
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	// SELECT SEQINDEX,VARCHAR_FORMAT(CURRENTDATE,'MM-DD-YYYY') AS
	// VCHAR_FORMATED,FINALPRICE,DELTA1,DELTA2,DELTA3,DELTA4,DELTA5,DSI,DSi5,PTMAV,PTMA
	// FROM BPMADMIN.INTRADAYSTOCKS where symbol='SPY' and seqindex<=44037 order
	// by
	// seqIndex DEsc;

	// for symbol and how many count cDays ending endIndex
	// we try to use a combination of many factors like DELTA,DSI, DSI5, PTMAV,
	// PTMA etc to
	// match with the closest historical record sets and thus forecast its near
	// 5 day price
	// based on that historical record, kind like old recording machine
	// potentially we need to design some skipping factor and records skipping
	// capability
	public static void patternMatching(String symbol, int cDays, int endIndex,
			String method) {

		int days = 50;
		// int days = 10;
		int[] indexesP = new int[cDays];
		String[] datesP = new String[cDays];
		float[] finalpricesP = new float[cDays];
		float[] lowpricesP = new float[cDays];
		float[] highpricesP = new float[cDays];
		float[] openpricesP = new float[cDays];
		float[] delta1sP = new float[cDays];
		float[] delta2sP = new float[cDays];
		float[] delta3sP = new float[cDays];
		float[] delta4sP = new float[cDays];
		float[] delta5sP = new float[cDays];
		float[] dsisP = new float[cDays];
		float[] dsi5sP = new float[cDays];
		float[] ptmavsP = new float[cDays];
		float[] ptmasP = new float[cDays];

		int[] indexes = new int[days];
		String[] dates = new String[days];
		float[] finalprices = new float[days];
		float[] lowprices = new float[days];
		float[] highprices = new float[days];
		float[] openprices = new float[days];
		float[] delta1s = new float[days];
		float[] delta2s = new float[days];
		float[] delta3s = new float[days];
		float[] delta4s = new float[days];
		float[] delta5s = new float[days];
		float[] dsis = new float[days];
		float[] dsi5s = new float[days];
		float[] ptmavs = new float[days];
		float[] ptmas = new float[days];

		int loopIndexStart = 0;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			SQL = "UPDATE  BPMADMIN.INTRADAYSTOCKS SET PLPERCENT=0 where symbol='"
					+ symbol + "'";

			stmt2.executeUpdate(SQL);

			SQL = "SELECT SEQINDEX,VARCHAR_FORMAT(CURRENTDATE,'MM-DD-YYYY') AS VCHAR_FORMATED,FINALPRICE,DELTA1,DELTA2,DELTA3,DELTA4,DELTA5,DSI,DSI5,PTMAV,PTMA,LOWPRICE,HIGHPRICE,OPENPRICE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' and seqIndex<= "
					+ endIndex
					+ " order by seqIndex desc";

			rs1 = stmt1.executeQuery(SQL);

			int loopCount = 0;
			double priceSumD, sumDelta1D, sumDelta2D, sumDelta3D, sumDelta4D, sumDelta5D, sumDSID, sumDSI5D, sumPTMAVD, sumPTMAD;

			while (rs1.next()) {
				if (loopCount < (days + cDays)) {
					if (loopCount < cDays) {
						indexesP[loopCount] = rs1.getInt(1);
						datesP[loopCount] = rs1.getString(2);
						finalpricesP[loopCount] = rs1.getFloat(3);
						delta1sP[loopCount] = rs1.getFloat(4);
						delta2sP[loopCount] = rs1.getFloat(5);
						delta3sP[loopCount] = rs1.getFloat(6);
						delta4sP[loopCount] = rs1.getFloat(7);
						delta5sP[loopCount] = rs1.getFloat(8);
						dsisP[loopCount] = rs1.getFloat(9);
						dsi5sP[loopCount] = rs1.getFloat(10);
						ptmavsP[loopCount] = rs1.getFloat(11);
						ptmasP[loopCount] = rs1.getFloat(12);
						lowpricesP[loopCount] = rs1.getFloat(13);
						highpricesP[loopCount] = rs1.getFloat(14);
						openpricesP[loopCount] = rs1.getFloat(15);
					} else {
						indexes[loopCount - cDays] = rs1.getInt(1);
						dates[loopCount - cDays] = rs1.getString(2);
						finalprices[loopCount - cDays] = rs1.getFloat(3);
						delta1s[loopCount - cDays] = rs1.getFloat(4);
						delta2s[loopCount - cDays] = rs1.getFloat(5);
						delta3s[loopCount - cDays] = rs1.getFloat(6);
						delta4s[loopCount - cDays] = rs1.getFloat(7);
						delta5s[loopCount - cDays] = rs1.getFloat(8);
						dsis[loopCount - cDays] = rs1.getFloat(9);
						dsi5s[loopCount - cDays] = rs1.getFloat(10);
						ptmavs[loopCount - cDays] = rs1.getFloat(11);
						ptmas[loopCount - cDays] = rs1.getFloat(12);
						lowprices[loopCount - cDays] = rs1.getFloat(13);
						highprices[loopCount - cDays] = rs1.getFloat(14);
						openprices[loopCount - cDays] = rs1.getFloat(15);
					}

				} else if (loopCount >= (days + cDays)) {
					int end = loopIndexStart % days;
					// int start = (loopIndexStart + cDays) % days;
					priceSumD = 0.0d;
					sumDelta1D = 0.0d;
					sumDelta2D = 0.0d;
					sumDelta3D = 0.0d;
					sumDelta4D = 0.0d;
					sumDelta5D = 0.0d;
					sumDSID = 0.0d;
					sumDSI5D = 0.0d;
					sumPTMAVD = 0.0d;
					sumPTMAD = 0.0d;
					long t1 = System.currentTimeMillis();
					if (method.equalsIgnoreCase("DSI5")
							|| method.equalsIgnoreCase("both")
							|| method.equalsIgnoreCase("DSI"))
						for (int w = 0; w < cDays; w++) {
							System.out.println("Invoking old...");
							int id = (end + w) % days;
							// System.out.println("Compare records of " +
							// indexesP[w]
							// + " " + datesP[w] + " with records of "
							// + indexes[id] + " " + dates[id]);
							// priceSumD = priceSumD
							// + Math.pow(100.0d
							// * (finalpricesP[w] - finalprices[id])
							// / finalprices[id], 2);
							// sumDelta1D = sumDelta1D
							// + Math.pow((delta1sP[w] - delta1s[id]), 2);
							// sumDelta2D = sumDelta2D
							// + Math.pow((delta2sP[w] - delta2s[id]), 2);
							// sumDelta3D = sumDelta3D
							// + Math.pow((delta3sP[w] - delta3s[id]), 2);
							// sumDelta4D = sumDelta4D
							// + Math.pow((delta4sP[w] - delta4s[id]), 2);
							// sumDelta5D = sumDelta5D
							// + Math.pow((delta5sP[w] - delta5s[id]), 2);
							if (method.equalsIgnoreCase("DSI")
									|| method.equalsIgnoreCase("both")) {
								sumDSID = sumDSID
										+ Math.pow((dsisP[w] - dsis[id]), 2);
							}
							if (method.equalsIgnoreCase("DSI5")
									|| method.equalsIgnoreCase("both")) {

								sumDSI5D = sumDSI5D
										+ Math.pow((dsi5sP[w] - dsi5s[id]), 2);
							}
							// sumPTMAVD = sumPTMAVD
							// + Math.pow((ptmavsP[w] - ptmavs[id]), 2);
							// sumPTMAD = sumPTMAD
							// + Math.pow((ptmasP[w] - ptmas[id]), 2);
						}
					if (method.equalsIgnoreCase("DSI5")
							|| method.equalsIgnoreCase("both")
							|| method.equalsIgnoreCase("DSI")) {
						double deltasqrt = Math.sqrt(priceSumD + sumDelta1D
								+ sumDelta2D + sumDelta3D + sumDelta4D
								+ sumDelta5D + sumDSID + sumDSI5D + sumPTMAVD
								+ sumPTMAD);

						String SQL2 = "UPDATE BPMADMIN.INTRADAYSTOCKS SET PLPERCENT = "
								+ deltasqrt
								+ " where symbol='"
								+ symbol
								+ "' and seqIndex =" + indexes[end];

						stmt2.executeUpdate(SQL2);
						System.out.println("Invoking old...");
					}

					if (method.equalsIgnoreCase("finalprice")) {
						// System.out.println("Invoking new...");
						priceChangePatternMatching(symbol, cDays, days, end,
								indexesP, finalpricesP, indexes, finalprices);
					} else if (method.equalsIgnoreCase("lowprice")) {
						// System.out.println("Invoking new...");
						priceChangePatternMatching(symbol, cDays, days, end,
								indexesP, lowpricesP, indexes, lowprices);
					} else if (method.equalsIgnoreCase("highprice")) {
						// System.out.println("Invoking new...");
						priceChangePatternMatching(symbol, cDays, days, end,
								indexesP, highpricesP, indexes, highprices);
					} else if (method.equalsIgnoreCase("openprice")) {
						// System.out.println("Invoking new...");
						priceChangePatternMatching(symbol, cDays, days, end,
								indexesP, openpricesP, indexes, openprices);
					}

					long t2 = System.currentTimeMillis();
					// System.out.println("Update PLPERCENT of " + indexes[end]
					// + " to " + deltasqrt);
					// System.out.println("Cost time is second " + (t2 - t1)
					// / 1000);
					indexes[end] = rs1.getInt(1);
					dates[end] = rs1.getString(2);
					finalprices[end] = rs1.getFloat(3);
					delta1s[end] = rs1.getFloat(4);
					delta2s[end] = rs1.getFloat(5);
					delta3s[end] = rs1.getFloat(6);
					delta4s[end] = rs1.getFloat(7);
					delta5s[end] = rs1.getFloat(8);
					dsis[end] = rs1.getFloat(9);
					dsi5s[end] = rs1.getFloat(10);
					ptmavs[end] = rs1.getFloat(11);
					ptmas[end] = rs1.getFloat(12);
					lowprices[end] = rs1.getFloat(13);
					highprices[end] = rs1.getFloat(14);
					openprices[end] = rs1.getFloat(15);
					loopIndexStart++;

				}

				loopCount++;

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void priceChangePatternMatching(String symbol, int cDays,
			int days, int end, int[] pIndex, float[] priceP, int[] index,
			float[] price) {
		float pStartPrice = 0.0f;
		float startPrice = 0.0f;
		int pStartIndex = 0;
		int startIndex = 0;
		int endIndex = 0;
		int pEndIndex = 0;
		float endPrice = 0.0f;
		float pEndPrice = 0.0f;
		float pPriceChange = 0.0f;
		float priceChange = 0.0f;
		float priceSumChange = 0.0f;
		for (int w = 0; w < cDays; w++) {

			int id = (end + w) % days;
			if (w == 0) {
				pEndPrice = priceP[w];
				pEndIndex = pIndex[w];
				endPrice = price[(w + id) % days];
				endIndex = index[(w + id) % days];
			} else {
				pStartIndex = pIndex[w];
				startIndex = index[id];
				pStartPrice = priceP[w];
				startPrice = price[id];
				pPriceChange = 100.0f * (pEndPrice - pStartPrice) / pStartPrice;
				priceChange = 100.0f * (endPrice - startPrice) / startPrice;
				// System.out.println("Pattern From "+pStartIndex+" to "+pEndIndex+" price change from "+pStartPrice+" to "+pEndPrice+" or "+pPriceChange+"%");
				// System.out.println("Change From "+startIndex+" to "+endIndex+" price change from "+startPrice+" to "+endPrice+" or "+priceChange+"%");

				priceSumChange = priceSumChange
						+ (float) Math.pow((priceChange - pPriceChange), 2);
				// System.out.println("priceSumChange is now "+priceSumChange);
			}

		}
		float deltasqrt = (float) Math.sqrt(priceSumChange);
		try {
			if (con == null)
				con = getConnection();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			String SQL2 = "UPDATE BPMADMIN.INTRADAYSTOCKS SET PLPERCENT = "
					+ deltasqrt + " where symbol='" + symbol
					+ "' and seqIndex =" + endIndex;
			// System.out.println("SQL2 "+SQL2);
			stmt2.executeUpdate(SQL2);
			// System.out.println("Update PLPERCENT at index "+
			// endIndex+" with value "+deltasqrt);
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}

	public static void findTopMatching(String symbol, int[] tops1, int[] tops2,
			int[] tops3, int[] tops4, int topNum, int endIndex) {
		int[] tops1Score = new int[tops1.length];
		int[] tops2Score = new int[tops2.length];
		int[] tops3Score = new int[tops3.length];
		int[] tops4Score = new int[tops4.length];

		// init array
		for (int k = 0; k < tops1.length; k++) {
			tops1Score[k] = 0;
			tops2Score[k] = 0;
			tops3Score[k] = 0;
			tops4Score[k] = 0;
		}

		// $$$$$$$$$ step1 start score tops1 index
		for (int a = 0; a < tops1.length; a++) { // first for loop
			for (int b = 0; b < tops1.length; b++) { // second for loop
				if (a != b) {
					if (Math.abs(tops1[a] - tops1[b]) < 7) {
						int distance = checkDistance(symbol, tops1[a], tops1[b]);
						if (distance == 1)
							tops1Score[a] = tops1Score[a] + 3;

						if (distance == 2)
							tops1Score[a] = tops1Score[a] + 2;

						if (distance == 3)
							tops1Score[a] = tops1Score[a] + 1;

					}
				}
			}
		}

		for (int a = 0; a < tops1.length; a++) { // first for loop
			for (int b = 0; b < tops2.length; b++) { // second for loop
				// compare top1s and tops2
				if (tops1[a] == tops2[b]) {
					tops1Score[a] = tops1Score[a] + 6;
				} else if (Math.abs(tops1[a] - tops2[b]) < 7) {
					int distance = checkDistance(symbol, tops1[a], tops2[b]);
					if (distance == 1)
						tops1Score[a] = tops1Score[a] + 3;

					if (distance == 2)
						tops1Score[a] = tops1Score[a] + 2;

					if (distance == 3)
						tops1Score[a] = tops1Score[a] + 1;

				}

				// compare top1s and tops3
				if (tops1[a] == tops3[b]) {
					tops1Score[a] = tops1Score[a] + 6;
				} else if (Math.abs(tops1[a] - tops3[b]) < 7) {
					int distance = checkDistance(symbol, tops1[a], tops3[b]);
					if (distance == 1)
						tops1Score[a] = tops1Score[a] + 3;

					if (distance == 2)
						tops1Score[a] = tops1Score[a] + 2;

					if (distance == 3)
						tops1Score[a] = tops1Score[a] + 1;

				}

				// compare top1s and tops4
				if (tops1[a] == tops4[b]) {
					tops1Score[a] = tops1Score[a] + 6;
				} else if (Math.abs(tops1[a] - tops4[b]) < 7) {
					int distance = checkDistance(symbol, tops1[a], tops4[b]);
					if (distance == 1)
						tops1Score[a] = tops1Score[a] + 3;

					if (distance == 2)
						tops1Score[a] = tops1Score[a] + 2;

					if (distance == 3)
						tops1Score[a] = tops1Score[a] + 1;

				}

			}// second for loop

		}// first for loop
			// $$$$$$$$$ step1 end score tops1 index
		// display result
		// System.out.println("Tops 1 list: ");
		// for (int a = 0; a < tops1.length; a++) {
		// System.out.println(tops1[a] + " score " + tops1Score[a]);
		// }

		// $$$$$$$$$ step2 start score tops2 index
		for (int a = 0; a < tops2.length; a++) { // first for loop
			for (int b = 0; b < tops2.length; b++) { // second for loop
				if (a != b) {
					if (Math.abs(tops2[a] - tops2[b]) < 7) {
						int distance = checkDistance(symbol, tops2[a], tops2[b]);
						if (distance == 1)
							tops2Score[a] = tops2Score[a] + 3;

						if (distance == 2)
							tops2Score[a] = tops2Score[a] + 2;

						if (distance == 3)
							tops2Score[a] = tops2Score[a] + 1;

					}
				}
			}
		}
		for (int a = 0; a < tops2.length; a++) { // first for loop
			for (int b = 0; b < tops2.length; b++) { // second for loop
				// compare top2s and tops1
				if (tops2[a] == tops1[b]) {
					tops2Score[a] = tops2Score[a] + 6;
				} else if (Math.abs(tops2[a] - tops1[b]) < 7) {
					int distance = checkDistance(symbol, tops2[a], tops1[b]);
					if (distance == 1)
						tops2Score[a] = tops2Score[a] + 3;

					if (distance == 2)
						tops2Score[a] = tops2Score[a] + 2;

					if (distance == 3)
						tops2Score[a] = tops2Score[a] + 1;

				}

				// compare top2s and tops3
				if (tops2[a] == tops3[b]) {
					tops2Score[a] = tops2Score[a] + 6;
				} else if (Math.abs(tops2[a] - tops3[b]) < 7) {
					int distance = checkDistance(symbol, tops2[a], tops3[b]);
					if (distance == 1)
						tops2Score[a] = tops2Score[a] + 3;

					if (distance == 2)
						tops2Score[a] = tops2Score[a] + 2;

					if (distance == 3)
						tops2Score[a] = tops2Score[a] + 1;

				}

				// compare top2s and tops4
				if (tops2[a] == tops4[b]) {
					tops2Score[a] = tops2Score[a] + 6;
				} else if (Math.abs(tops2[a] - tops4[b]) < 7) {
					int distance = checkDistance(symbol, tops2[a], tops4[b]);
					if (distance == 1)
						tops2Score[a] = tops2Score[a] + 3;

					if (distance == 2)
						tops2Score[a] = tops2Score[a] + 2;

					if (distance == 3)
						tops2Score[a] = tops2Score[a] + 1;

				}

			}// second for loop

		}// first for loop
			// $$$$$$$$$ step2 end score tops2 index
			// display result
		// System.out.println("Tops 2 list: ");
		// for (int a = 0; a < tops2.length; a++) {
		// System.out.println(tops2[a] + " score " + tops2Score[a]);
		// }

		// $$$$$$$$$ step3 start score tops3 index
		for (int a = 0; a < tops3.length; a++) { // first for loop
			for (int b = 0; b < tops3.length; b++) { // second for loop
				if (a != b) {
					if (Math.abs(tops3[a] - tops3[b]) < 7) {
						int distance = checkDistance(symbol, tops3[a], tops3[b]);
						if (distance == 1)
							tops3Score[a] = tops3Score[a] + 3;

						if (distance == 2)
							tops3Score[a] = tops3Score[a] + 2;

						if (distance == 3)
							tops3Score[a] = tops3Score[a] + 1;

					}
				}
			}
		}
		for (int a = 0; a < tops3.length; a++) { // first for loop
			for (int b = 0; b < tops3.length; b++) { // second for loop
				// compare top3s and tops1
				if (tops3[a] == tops1[b]) {
					tops3Score[a] = tops3Score[a] + 6;
				} else if (Math.abs(tops3[a] - tops1[b]) < 7) {
					int distance = checkDistance(symbol, tops3[a], tops1[b]);
					if (distance == 1)
						tops3Score[a] = tops3Score[a] + 3;

					if (distance == 2)
						tops3Score[a] = tops3Score[a] + 2;

					if (distance == 3)
						tops3Score[a] = tops3Score[a] + 1;

				}

				// compare top3s and tops2
				if (tops3[a] == tops2[b]) {
					tops3Score[a] = tops3Score[a] + 6;
				} else if (Math.abs(tops3[a] - tops2[b]) < 7) {
					int distance = checkDistance(symbol, tops3[a], tops2[b]);
					if (distance == 1)
						tops3Score[a] = tops3Score[a] + 3;

					if (distance == 2)
						tops3Score[a] = tops3Score[a] + 2;

					if (distance == 3)
						tops3Score[a] = tops3Score[a] + 1;

				}

				// compare top3s and tops4
				if (tops3[a] == tops4[b]) {
					tops3Score[a] = tops3Score[a] + 6;
				} else if (Math.abs(tops3[a] - tops4[b]) < 7) {
					int distance = checkDistance(symbol, tops3[a], tops4[b]);
					if (distance == 1)
						tops3Score[a] = tops3Score[a] + 3;

					if (distance == 2)
						tops3Score[a] = tops3Score[a] + 2;

					if (distance == 3)
						tops3Score[a] = tops3Score[a] + 1;

				}

			}// second for loop

		}// first for loop
			// $$$$$$$$$ step3 end score tops3 index
			// display result
		// System.out.println("Tops 3 list: ");
		// for (int a = 0; a < tops3.length; a++) {
		// System.out.println(tops3[a] + " score " + tops3Score[a]);
		// }

		// $$$$$$$$$ step4 start score tops3 index
		for (int a = 0; a < tops4.length; a++) { // first for loop
			for (int b = 0; b < tops4.length; b++) { // second for loop
				if (a != b) {
					if (Math.abs(tops4[a] - tops4[b]) < 7) {
						int distance = checkDistance(symbol, tops4[a], tops4[b]);
						if (distance == 1)
							tops4Score[a] = tops4Score[a] + 3;

						if (distance == 2)
							tops4Score[a] = tops4Score[a] + 2;

						if (distance == 3)
							tops4Score[a] = tops4Score[a] + 1;

					}
				}
			}
		}
		for (int a = 0; a < tops4.length; a++) { // first for loop
			for (int b = 0; b < tops4.length; b++) { // second for loop
				// compare top4s and tops1
				if (tops4[a] == tops1[b]) {
					tops4Score[a] = tops4Score[a] + 6;
				} else if (Math.abs(tops4[a] - tops1[b]) < 7) {
					int distance = checkDistance(symbol, tops4[a], tops1[b]);
					if (distance == 1)
						tops4Score[a] = tops4Score[a] + 3;

					if (distance == 2)
						tops4Score[a] = tops4Score[a] + 2;

					if (distance == 3)
						tops4Score[a] = tops4Score[a] + 1;

				}

				// compare top4s and tops2
				if (tops4[a] == tops2[b]) {
					tops4Score[a] = tops4Score[a] + 6;
				} else if (Math.abs(tops4[a] - tops2[b]) < 7) {
					int distance = checkDistance(symbol, tops4[a], tops2[b]);
					if (distance == 1)
						tops4Score[a] = tops4Score[a] + 3;

					if (distance == 2)
						tops4Score[a] = tops4Score[a] + 2;

					if (distance == 3)
						tops4Score[a] = tops4Score[a] + 1;

				}

				// compare top4s and tops3
				if (tops4[a] == tops3[b]) {
					tops4Score[a] = tops4Score[a] + 6;
				} else if (Math.abs(tops4[a] - tops3[b]) < 7) {
					int distance = checkDistance(symbol, tops4[a], tops3[b]);
					if (distance == 1)
						tops4Score[a] = tops4Score[a] + 3;

					if (distance == 2)
						tops4Score[a] = tops4Score[a] + 2;

					if (distance == 3)
						tops4Score[a] = tops4Score[a] + 1;

				}

			}// second for loop

		}// first for loop
			// $$$$$$$$$ step4 end score tops4 index
			// display result
		// System.out.println("Tops 4 list: ");
		// for (int a = 0; a < tops4.length; a++) {
		// System.out.println(tops4[a] + " score " + tops4Score[a]);
		// }

		// Sort out top score index
		int[] topIndex = new int[topNum];
		int[] topScores = new int[topNum];
		for (int w = 0; w < topNum; w++) {
			topScores[w] = 1000000;
			topIndex[w] = 0;
		}

		for (int i = 0; i < tops1.length; i++) {
			// System.out.println("tops1[" + i + "]" + tops1[i] + "  score: "
			// + tops1Score[i]);
			if (!checkExisting(topIndex, tops1[i])) {
				int rIndex = replaceBottomScore(topScores, tops1Score[i]);
				if (rIndex > -1)
					topIndex[rIndex] = tops1[i];
			}
			// System.out.println("tops2[" + i + "]" + tops2[i] + "  score: "
			// + tops2Score[i]);
			if (!checkExisting(topIndex, tops2[i])) {
				int rIndex = replaceBottomScore(topScores, tops2Score[i]);
				if (rIndex > -1)
					topIndex[rIndex] = tops2[i];
			}
			// System.out.println("tops3[" + i + "]" + tops3[i] + "  score: "
			// + tops3Score[i]);
			if (!checkExisting(topIndex, tops3[i])) {
				int rIndex = replaceBottomScore(topScores, tops3Score[i]);
				if (rIndex > -1)
					topIndex[rIndex] = tops3[i];
			}
			// System.out.println("tops4[" + i + "]" + tops4[i] + "  score: "
			// + tops4Score[i]);
			if (!checkExisting(topIndex, tops4[i])) {
				int rIndex = replaceBottomScore(topScores, tops4Score[i]);
				if (rIndex > -1)
					topIndex[rIndex] = tops4[i];
			}

		}

		System.out.println("Final pattern matching score top list: ");
		for (int k = 0; k < topIndex.length; k++) {
			System.out.println("topIndex[" + k + "]" + topIndex[k]
					+ "  score: " + topScores[k]);

		}

		displayPatternResultMatchingCondition(symbol, tops4, topNum, endIndex);
		displayPatternResult(symbol, tops4, topNum, endIndex);
		// displayPatternResultMatchingCondition(symbol, topIndex, endIndex);
		// displayPatternResult(symbol, topIndex, endIndex);
	}

	public static void displayPatternResultMatchingCondition(String symbol,
			int[] topIndex, int topNum, int seqIndex) {
		int[] index = new int[topIndex.length + 1];
		index[0] = seqIndex;
		for (int n = 0; n < topIndex.length; n++) {
			index[n + 1] = topIndex[n];
		}
		float[] dsi = new float[(topIndex.length + 1) * 44];
		float[] dsi5 = new float[(topIndex.length + 1) * 44];
		float[] price = new float[(topIndex.length + 1) * 44];

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			for (int k = 0; k < index.length; k++) {

				SQL = "SELECT  DSI,DSI5,FINALPRICE FROM BPMADMIN.INTRADAYSTOCKS where symbol='"
						+ symbol
						+ "' and seqIndex<= "
						+ index[k]
						+ "  order by seqIndex desc";

				rs1 = stmt1.executeQuery(SQL);

				int loopCount = 0;
				boolean cont = true;
				while (rs1.next() && cont) {
					dsi[loopCount + 44 * k] = rs1.getFloat(1);
					dsi5[loopCount + 44 * k] = rs1.getFloat(2);
					price[loopCount + 44 * k] = rs1.getFloat(3);

					loopCount++;
					if (loopCount % 44 == 0)
						cont = false;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}
		/*
		 * System.out.println("Pattern matching condition comparision...");
		 * String headline = "Index end position "; for (int k = 0; k <
		 * index.length; k++) { headline = headline + " " + index[k] +
		 * "(price,DSI,DSI5) "; } System.out.println(headline); for (int k = 0;
		 * k < 44; k++) { String nextline = "      "; for (int w = 0; w <
		 * index.length; w++) { nextline = nextline + " (" + price[k + w * 44] +
		 * ", " + (int) (dsi[k + w * 44]) + ", " + (int) (dsi5[k + w * 44]) +
		 * ")  "; } System.out.println(nextline); }
		 */
		// calculate price change percentage compared to start point
		float startPrice = 0.0f;
		for (int w = 0; w < index.length; w++) {
			startPrice = 0.0f;
			for (int k = 43; k >= 0; k--) {
				if (k > 0 && k % 43 == 0) {
					startPrice = price[k + w * 44];
				} else {
					price[k + w * 44] = 100.0f * ((price[k + w * 44] - startPrice) / startPrice);
				}
			}
		}

		String headline = "";
		System.out.println("Pattern matching condition comparision...");
		headline = "";
		for (int k = 0; k < index.length; k++) {
			headline = headline + " " + index[k] + "(p%) ";
		}
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		System.out.println(headline);
		for (int k = 0; k < 44; k++) {
			String nextline = "";
			if (k > 0 && k % 43 == 0) {
				for (int w = 0; w < index.length; w++) {
					nextline = nextline + "(" + df.format(price[k + w * 44])
							+ ") ";
				}
			} else if (k >= 0) {
				for (int w = 0; w < index.length; w++) {
					nextline = nextline + " (" + df.format(price[k + w * 44])
							+ "%) ";
				}
			}
			System.out.println(nextline);
		}

		// compare direction score (number +- matching) and absolute score
		// (percentage change sum delta)
		int baselineCount = 0;
		float baselineSum = 0.0f;
		int currentPosCount = 0;
		float currentSum = 0.0f;
		int synCount = 0;

		// use 43 instead of 44 since the last one is real price
		for (int w = 0; w < 43; w++) {
			if (price[w] >= 0) {
				baselineCount++;
			}
			baselineSum = baselineSum + price[w];
		}

		for (int k = 1; k < index.length; k++) {
			// use 43 instead of 44 since the last one is real price
			synCount = 0;
			currentSum = 0;
			currentPosCount = 0;
			for (int w = 0; w < 43; w++) {
				if (price[w] * price[k * 44 + w] >= 0) {
					synCount++;
				}
				currentSum = currentSum + price[k * 44 + w];
				if (price[k * 44 + w] > 0)
					currentPosCount++;
			}

			System.out.println("for records ending at " + index[k]
					+ " the sync count is " + synCount + " postive count "
					+ currentPosCount + " vs pattern " + baselineCount
					+ " current percentage sum is " + currentSum
					+ " vs. pattern percentage sum " + baselineSum);
		}

	}

	public static void displayPatternResult(String symbol, int[] topIndex,
			int topNum, int seqIndex) {
		int[] index = new int[topIndex.length + 1];
		index[0] = seqIndex;
		for (int n = 0; n < topIndex.length; n++) {
			index[n + 1] = topIndex[n];
		}
		float[] price = new float[(topIndex.length + 1) * 15];

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			for (int k = 0; k < index.length; k++) {

				SQL = "SELECT  FINALPRICE FROM BPMADMIN.INTRADAYSTOCKS where symbol='"
						+ symbol
						+ "' and seqIndex>= "
						+ index[k]
						+ "  order by seqIndex asc";

				rs1 = stmt1.executeQuery(SQL);

				int loopCount = 0;
				boolean cont = true;
				while (rs1.next() && cont) {
					price[loopCount + 15 * k] = rs1.getFloat(1);

					loopCount++;
					if (loopCount % 15 == 0)
						cont = false;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

		/*
		 * System.out.println("Pattern matching condition comparision...");
		 * String headline = "Index end position "; for (int k = 0; k <
		 * index.length; k++) { headline = headline + " " + index[k] +
		 * "(price) "; }
		 * 
		 * System.out.println(headline); for (int k = 0; k < 15; k++) { String
		 * nextline = "      "; for (int w = 0; w < index.length; w++) {
		 * nextline = nextline + " (" + price[k + w * 15] + ") "; }
		 * System.out.println(nextline); }
		 */
		// calculate price change percentage compared to start point
		float startPrice = 0.0f;
		for (int w = 0; w < index.length; w++) {
			startPrice = 0.0f;
			for (int k = 0; k < 15; k++) {
				if (k == 0) {
					startPrice = price[k + w * 15];
				} else {
					price[k + w * 15] = 100.0f * ((price[k + w * 15] - startPrice) / startPrice);
				}
			}
		}

		System.out
				.println("Pattern matching result comparision...each future day against mark change%");
		String headline = "";
		for (int k = 0; k < index.length; k++) {
			headline = headline + " " + index[k] + "(p%) ";
		}
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		System.out.println(headline);
		for (int k = 0; k < 15; k++) {
			String nextline = "";
			if (k == 0) {
				for (int w = 0; w < index.length; w++) {
					nextline = nextline + "(" + df.format(price[k + w * 15])
							+ ") ";
				}
			} else {
				for (int w = 0; w < index.length; w++) {
					nextline = nextline + "(" + df.format(price[k + w * 15])
							+ "%) ";
				}
			}
			System.out.println(nextline);
		}

	}

	public static boolean checkExisting(int[] topIndex, int index) {
		boolean exist = false;

		for (int w = 0; w < topIndex.length; w++) {
			if (topIndex[w] == index) {
				exist = true;
			}

		}

		return exist;
	}

	public static int replaceBottomScore(int[] topScores, int score) {
		int index = -1;
		boolean placed = false;

		if (score > 0) {
			for (int w = 0; w < topScores.length; w++) {
				if (topScores[w] == 1000000) {
					index = w;
					topScores[w] = score;
					placed = true;
					break;
				}

			}
			if (!placed) {
				int minScore = 100000000;
				int minScoreIndex = -1;

				for (int w = 0; w < topScores.length; w++) {
					if (topScores[w] < minScore) {
						minScore = topScores[w];
						minScoreIndex = w;
						index = w;
					}
				}

				if (score > minScore && score > 0) {
					topScores[minScoreIndex] = score;
				} else {
					index = -1;
				}

			}
		}
		return index;
	}

	public static int checkDistance(String symbol, int seq1, int seq2) {
		int distance = 0;
		int endIndex = seq1;
		int startIndex = seq2;

		if (seq1 < seq2) {
			endIndex = seq2;
			startIndex = seq1;
		}

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "SELECT count(*) FROM BPMADMIN.INTRADAYSTOCKS where symbol='"
					+ symbol + "' and seqIndex< " + endIndex
					+ "  and seqIndex>= " + startIndex;

			rs1 = stmt1.executeQuery(SQL);

			if (rs1.next()) {
				distance = rs1.getInt(1);
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return distance;
	}

	public static int[] patternMatchingTops(String symbol, int endIndex,
			int count) {

		int[] indexesP = new int[count];
		String[] datesP = new String[count];

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "SELECT SEQINDEX,VARCHAR_FORMAT(CURRENTDATE,'MM-DD-YYYY') AS VCHAR_FORMATED, PLPERCENT "
					+ " from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' and seqIndex< "
					+ endIndex
					+ "  AND PLPERCENT<>0 order by PLPERCENT asc";

			rs1 = stmt1.executeQuery(SQL);

			int loopCount = 0;

			while (rs1.next()) {
				if (loopCount < (count)) {
					indexesP[loopCount] = rs1.getInt(1);
					datesP[loopCount] = rs1.getString(2);
					System.out.println("index " + indexesP[loopCount]
							+ " date " + datesP[loopCount] + " PLPERCENT "
							+ rs1.getFloat(3));

				} else if (loopCount >= count) {
					break;

				}
				if (loopCount == 0) {
					System.out.println("Min selected PLPERCENT is "
							+ rs1.getFloat(3) + " at " + rs1.getString(2));
				} else if (loopCount == count - 1) {
					System.out.println("Max selected PLPERCENT is "
							+ rs1.getFloat(3) + " at " + rs1.getString(2));
				}
				loopCount++;
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return indexesP;
	}

	public static void calIndexSqrtDelta(String symbol, int dayGap) {

		int days = 30;
		int watchStep = 5;
		int totalCount = 0;
		float[] finalprices = new float[days];
		float[] highs = new float[days];
		float[] lows = new float[days];
		int[] indexes = new int[days];
		String[] dates = new String[days];
		int loopIndexStart = 0;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			SQL = "select seqindex,FINALPRICE, LOWPRICE,HIGHPRICE,VARCHAR_FORMAT(CURRENTDATE,'MM-DD-YYYY') AS VCHAR_FORMATED from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' order by seqIndex desc";

			rs1 = stmt1.executeQuery(SQL);

			int loopCount = 0;
			int nextIndex1, nextIndex2;
			float price1, price2, low1, low2, high1, high2;
			price1 = 0.0f;
			price2 = 0.0f;
			low1 = 0.0f;
			low2 = 0.0f;
			high1 = 0.0f;
			high2 = 0.0f;
			nextIndex1 = 0;
			nextIndex2 = 0;
			while (rs1.next()) {
				if (loopCount < days) {
					indexes[loopCount] = rs1.getInt(1);
					finalprices[loopCount] = rs1.getFloat(2);
					lows[loopCount] = rs1.getFloat(3);
					highs[loopCount] = rs1.getFloat(4);
					dates[loopCount] = rs1.getString(5);
				} else if (loopCount >= days) {
					int end = loopIndexStart % 30;
					int start = (loopIndexStart + dayGap) % days;
					high1 = highs[end];
					high2 = highs[start];
					price1 = finalprices[end];
					price2 = finalprices[start];
					low1 = lows[end];
					low2 = lows[start];
					nextIndex1 = indexes[end];
					nextIndex2 = indexes[start];

					double v1 = Math.pow(100.0f * (high1 - high2) / high2, 2);
					double v2 = Math
							.pow(100.0f * (price1 - price2) / price2, 2);
					double v3 = Math.pow(100.0f * (low1 - low2) / low2, 2);
					double deltasqrt = Math.sqrt(v1 + v2 + v3);

					String SQL2 = "UPDATE BPMADMIN.INTRADAYSTOCKS SET DELTA1="
							+ deltasqrt + " where symbol='" + symbol
							+ "' and seqIndex =" + nextIndex1;

					if (dayGap == 2) {
						SQL2 = "UPDATE BPMADMIN.INTRADAYSTOCKS SET DELTA2="
								+ deltasqrt + " where symbol='" + symbol
								+ "' and seqIndex =" + nextIndex1;
					} else if (dayGap == 3) {
						SQL2 = "UPDATE BPMADMIN.INTRADAYSTOCKS SET DELTA3="
								+ deltasqrt + " where symbol='" + symbol
								+ "' and seqIndex =" + nextIndex1;
					} else if (dayGap == 4) {
						SQL2 = "UPDATE BPMADMIN.INTRADAYSTOCKS SET DELTA4="
								+ deltasqrt + " where symbol='" + symbol
								+ "' and seqIndex =" + nextIndex1;
					} else if (dayGap >= 5) {
						SQL2 = "UPDATE BPMADMIN.INTRADAYSTOCKS SET DELTA5="
								+ deltasqrt + " where symbol='" + symbol
								+ "' and seqIndex =" + nextIndex1;
					}

					stmt2.executeUpdate(SQL2);
					System.out.println("Update DELTA of " + nextIndex1);

					indexes[loopIndexStart % days] = rs1.getInt(1);
					finalprices[loopIndexStart % days] = rs1.getFloat(2);
					lows[loopIndexStart % days] = rs1.getFloat(3);
					highs[loopIndexStart % days] = rs1.getFloat(4);
					dates[loopIndexStart % days] = rs1.getString(5);
					loopIndexStart++;

				}

				loopCount++;

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	// It looks like DSI3 which is calculated as
	// adjacent day sqrt(sqr(delta(final price)%)+sqr(delta(low
	// price)%)+sqr(delta(high price)%))
	// to measure price change day to day, filter 5 days, no single day greater
	// 0.7, AVG 5 days less than 0.4
	// DSI which measure 20 days trend would allow you to add or subtract half
	// to one percentage to predict target
	// DSI stable seems also important, avgDSi>120, might be a good predictor to
	// add 1% for 5 days out for example
	// As for AVG DSI around 60 might be a status quo indicator for short term
	// DSI3 is the major filter, supplemented by DSI filter

	public static void qualifyIndexStatistics(String symbol, int wdays,
			float maxIncrease, float maxDecrease) {

		int days = 30;
		int totalCount = 0;
		float[] finalprices = new float[days];
		float[] acptmas = new float[days];
		float[] ptmas = new float[days];
		int[] indexes = new int[days];
		float[] dsis = new float[days];
		float[] dsi3s = new float[days];
		float[] dsi5s = new float[days];
		float[] highs = new float[days];
		float[] lows = new float[days];
		String[] dates = new String[days];
		float[] maxDsi3s = new float[days];
		int wildTotal5 = 0;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select seqindex,ACPTMA,PTMA,DSI,DSI5,FINALPRICE,VARCHAR_FORMAT(CURRENTDATE,'MM-DD-YYYY') AS VCHAR_FORMATED, LOWPRICE,HIGHPRICE, DSI3 from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' order by seqIndex asc";

			rs1 = stmt1.executeQuery(SQL);

			int loopCount = 0;
			int trackCount = 21;
			while (rs1.next()) {
				if (loopCount < days) {
					indexes[loopCount] = rs1.getInt(1);
					acptmas[loopCount] = rs1.getFloat(2);
					ptmas[loopCount] = rs1.getFloat(3);
					dsis[loopCount] = rs1.getFloat(4);
					dsi5s[loopCount] = rs1.getFloat(5);
					finalprices[loopCount] = rs1.getFloat(6);
					dates[loopCount] = rs1.getString(7);
					lows[loopCount] = rs1.getFloat(8);
					highs[loopCount] = rs1.getFloat(9);
					dsi3s[loopCount] = rs1.getFloat(10);
					maxDsi3s[loopCount] = 0.0f;
				} else {
					// how many in total without any, any filter
					// totalCount++;

					// float dayWDelta = 100.0f
					// * (finalprices[(wdays + trackCount - 1) % days] -
					// finalprices[trackCount])
					// / finalprices[trackCount];
					// if (dayWDelta > maxDecrease && dayWDelta < maxIncrease) {

					// System.out.println("End date "+dates[(wdays + trackCount
					// - 1) % days]+" price "+finalprices[(wdays + trackCount -
					// 1) % days]);
					// System.out.println("Start date "+dates[trackCount]+" price "+finalprices[trackCount]);
					String msg1 = "End date "
							+ dates[(wdays + trackCount - 1) % days]
							+ " price "
							+ finalprices[(wdays + trackCount - 1) % days];
					String msg2 = "Start date " + dates[trackCount] + " price "
							+ finalprices[trackCount];
					// System.out
					// .println("Qualified date found, the previous 5 day records are: ");

					float avgDSI3 = 0.0f;
					float avgDSI = 0.0f;
					for (int w = 1; w < 6; w++) {
						int nextIndex = (trackCount - w) % 30;
						if (nextIndex < 0)
							nextIndex = nextIndex + 30;
						// System.out.println("nextIndex "+nextIndex);

						avgDSI3 = avgDSI3 + dsi3s[nextIndex];
						avgDSI = avgDSI + dsis[nextIndex];

					}

					if (avgDSI3 < 3.4 * 5.0f) {
						// how many in total with filter avgDSI3 < 0.7 * 5.0f
						totalCount++;
						// if (avgDSI < 600 && avgDSI > 400) {

						float dayWDelta = 100.0f
								* (finalprices[(wdays + trackCount - 1) % days] - finalprices[trackCount])
								/ finalprices[trackCount];

						if (dayWDelta > maxDecrease && dayWDelta < maxIncrease) {
							wildTotal5++;
							System.out.println(msg1);
							System.out.println(msg2);
							// System.out.println(indexes[nextIndex] + " "
							// + dates[nextIndex] + " price: "
							// + finalprices[nextIndex] + " dsi: "
							// + dsis[nextIndex] + " dsi5: "
							// + dsi5s[nextIndex] + " dsi3: "
							// + dsi3s[nextIndex]);
							System.out.println("dayWDelta " + dayWDelta);

							System.out
									.println("Qualified date found, the previous 5 day records are: ");

							System.out.println("AVG DSI3 is "
									+ (avgDSI3 / 5.0f) + " AVG DSI is "
									+ (avgDSI / 5.0f));

						} else {
							System.out.println("Disq dayWDelta " + dayWDelta);

							System.out
									.println("DisQualified date found, the previous 5 day records are: ");

							System.out.println("Dis AVG DSI3 is "
									+ (avgDSI3 / 5.0f) + " AVG DSI is "
									+ (avgDSI / 5.0f));
						}
						// }
					}
					// }

					trackCount++;
					trackCount = trackCount % days;

					indexes[loopCount % days] = rs1.getInt(1);
					acptmas[loopCount % days] = rs1.getFloat(2);
					ptmas[loopCount % days] = rs1.getFloat(3);
					dsis[loopCount % days] = rs1.getFloat(4);
					dsi5s[loopCount % days] = rs1.getFloat(5);
					finalprices[loopCount % days] = rs1.getFloat(6);
					dates[loopCount % days] = rs1.getString(7);
					lows[loopCount % days] = rs1.getFloat(8);
					highs[loopCount % days] = rs1.getFloat(9);
					dsi3s[loopCount % days] = rs1.getFloat(10);
				}

				loopCount++;
			}
			System.out.println("Total count " + totalCount + " and "
					+ wildTotal5 + " qualified... ");

			System.out.println((100.0f * wildTotal5) / (1.0f * totalCount)
					+ "%  ...delta is between  " + maxDecrease + " and "
					+ maxIncrease);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateALLSPYStatistics(String symbol) {

		int days = 30;
		int watchStep = 5;
		int totalCount = 0;
		float targetNeg = -0.6f;
		float targetPos = 0.6f;
		int less2DropCount5Days = 0;
		int exception5Days = 0;
		int less2DropCount10Days = 0;
		int exception10Days = 0;
		int less2DropCount15Days = 0;
		int exception15Days = 0;
		int less2IncreaseCount5Days = 0;
		int less2IncreaseCount10Days = 0;
		int less2IncreaseCount15Days = 0;
		float[] finalprices = new float[days];
		float[] acptmas = new float[days];
		float[] ptmas = new float[days];
		int[] indexes = new int[days];
		float[] dsis = new float[days];
		float[] dsi5s = new float[days];
		String[] dates = new String[days];
		float day5Yield = 1.0f;
		float day10Yield = 1.0f;
		float day15Yield = 1.0f;
		float grandYield = 1.0f;
		int grandTradeCount = 0;
		int mildTotal5 = 0;
		int mildTotal10 = 0;
		int mildTotal15 = 0;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select seqindex,ACPTMA,PTMA,DSI,DSI5,FINALPRICE,VARCHAR_FORMAT(CURRENTDATE,'MM-DD-YYYY') AS VCHAR_FORMATED from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' order by seqIndex asc";

			rs1 = stmt1.executeQuery(SQL);

			int loopCount = 0;
			int trackCount = 21;
			int conCount = 0;
			int tradeCount = 0;
			while (rs1.next()) {
				conCount++;
				if (loopCount < days) {
					indexes[loopCount] = rs1.getInt(1);
					acptmas[loopCount] = rs1.getFloat(2);
					ptmas[loopCount] = rs1.getFloat(3);
					dsis[loopCount] = rs1.getFloat(4);
					dsi5s[loopCount] = rs1.getFloat(5);
					finalprices[loopCount] = rs1.getFloat(6);
					dates[loopCount] = rs1.getString(7);
				} else {
					totalCount++;

					boolean calculateYield = true;
					int backDayCount = 20;
					for (int w = 0; w < backDayCount; w++) {
						int nextIndex = trackCount - w;
						if (nextIndex < 0)
							nextIndex = nextIndex + days;
						if (acptmas[nextIndex] < 2.0f) {
							calculateYield = false;
							break;
						}
						if (ptmas[nextIndex] < 4.0f) {
							calculateYield = false;
							break;
						}
					}
					if (calculateYield) {
						tradeCount++;
						grandTradeCount++;
					}
					float day5Delta = 100.0f
							* (finalprices[(watchStep + trackCount) % days] - finalprices[trackCount])
							/ finalprices[trackCount];
					if (day5Delta > targetNeg) {
						less2DropCount5Days++;
						// System.out.println("index " + indexes[trackCount]
						// + " to " + indexes[(watchStep + trackCount) % days]
						// + " 5 days price change greater than "+targetNeg+"% "
						// + day5Delta);
						if (calculateYield)
							day5Yield = day5Yield * 1.14f;
						calculateYield = false;
					} else {
						if (calculateYield)
							day5Yield = day5Yield * 0.75f;
						calculateYield = false;
					}
					if (day5Delta > targetNeg && day5Delta < targetPos) {
						mildTotal5++;
					}
					if (conCount % 200 == 1) {
						System.out.println("Day 5 200 start Index "
								+ indexes[trackCount] + " date "
								+ dates[trackCount]);
					} else if (conCount % 200 == 0) {
						System.out.println("Day 5 200 End Index "
								+ indexes[trackCount] + " date "
								+ dates[trackCount]);
						System.out
								.println("Day 5 200 count yield " + day5Yield);
						System.out.println("Total trade count " + tradeCount);
						tradeCount = 0;
						grandYield = grandYield * day5Yield;
						day5Yield = 1.0f;
					}
					if (day5Delta < targetPos) {
						less2IncreaseCount5Days++;
						// System.out.println("index " + indexes[trackCount]
						// + " to " + indexes[(watchStep + trackCount) % days]
						// + " 5 days price change less than "+targetPos+"% "
						// + day5Delta);

					}

					// System.out.println("index " + indexes[trackCount] +
					// " to "
					// + indexes[(watchStep + trackCount) % days]
					// + " 5 days price change " + day5Delta);

					float day10Delta = 100.0f
							* (finalprices[(watchStep * 2 + trackCount) % days] - finalprices[trackCount])
							/ finalprices[trackCount];
					if (day10Delta > targetNeg && day10Delta < targetPos) {
						mildTotal10++;
					}
					if (day10Delta > targetNeg) {
						less2DropCount10Days++;
						// System.out.println("index " + indexes[trackCount]
						// + " to " + indexes[(watchStep*2 + trackCount) % days]
						// +
						// " 10 days price change greater than "+targetNeg+"% "
						// + day10Delta);
						day10Yield = day10Yield * 1.04f;
					} else {
						day10Yield = day10Yield * 0.71f;
					}
					if (day10Delta < targetPos) {
						less2IncreaseCount10Days++;
						// System.out.println("index " + indexes[trackCount]
						// + " to " + indexes[(watchStep*2 + trackCount) % days]
						// + " 10 days price change less than "+targetPos+"% "
						// + day10Delta);

					}

					// System.out.println("index " + indexes[trackCount] +
					// " to "
					// + indexes[(watchStep*2 + trackCount) % days]
					// + " 10 days price change " + day10Delta);

					float day15Delta = 100.0f
							* (finalprices[(watchStep * 3 + trackCount) % days] - finalprices[trackCount])
							/ finalprices[trackCount];
					if (day15Delta > targetNeg && day15Delta < targetPos) {
						mildTotal15++;
					}
					if (day15Delta > targetNeg) {
						less2DropCount15Days++;
						// System.out.println("index " + indexes[trackCount]
						// + " to " + indexes[(watchStep*3 + trackCount) % days]
						// +
						// " 15 days price change greater than "+targetNeg+"% "
						// + day15Delta);
						day15Yield = day15Yield * 1.04f;

					} else {
						day15Yield = day15Yield * 0.71f;
					}
					if (day15Delta < targetPos) {
						less2IncreaseCount15Days++;
						// System.out.println("index " + indexes[trackCount]
						// + " to " + indexes[(watchStep*3 + trackCount) % days]
						// + " 15 days price change less than  "+targetPos+"% "
						// + day15Delta);

					}

					// System.out.println("index " + indexes[trackCount] +
					// " to "
					// + indexes[(watchStep*3 + trackCount) % days]
					// + " price change " + day15Delta);

					trackCount++;
					trackCount = trackCount % days;

					indexes[loopCount % days] = rs1.getInt(1);
					acptmas[loopCount % days] = rs1.getFloat(2);
					ptmas[loopCount % days] = rs1.getFloat(3);
					dsis[loopCount % days] = rs1.getFloat(4);
					dsi5s[loopCount % days] = rs1.getFloat(5);
					finalprices[loopCount % days] = rs1.getFloat(6);
					dates[loopCount % days] = rs1.getString(7);
				}

				loopCount++;
			}
			System.out.println("Total count " + totalCount + " less than "
					+ targetNeg + "% drop in 5 days " + less2DropCount5Days
					+ " rate " + (100.0f * less2DropCount5Days)
					/ (1.0f * totalCount));
			System.out.println("Total count " + totalCount + " less than "
					+ targetPos + "% increase in 5 days "
					+ less2IncreaseCount5Days + " rate "
					+ (100.0f * less2IncreaseCount5Days) / (1.0f * totalCount));
			System.out.println("5 days exceptions " + exception5Days + " rate "
					+ (100.0f * exception5Days) / (1.0f * totalCount));
			System.out.println("grandYield = " + grandYield
					+ " grand trade count " + grandTradeCount);
			System.out.println("mildTotal5 = " + mildTotal5
					+ " grand trade count " + conCount + " ratio "
					+ (100.0f * mildTotal5) / (1.0f * conCount));
			System.out.println("mildTotal0 = " + mildTotal10
					+ " grand trade count " + conCount + " ratio "
					+ (100.0f * mildTotal10) / (1.0f * conCount));

			System.out.println("mildTotal15 = " + mildTotal15
					+ " grand trade count " + conCount + " ratio "
					+ (100.0f * mildTotal15) / (1.0f * conCount));

			System.out.println("Total count " + totalCount + " less than "
					+ targetNeg + "%  drop in 10 days " + less2DropCount5Days
					+ " rate " + (100.0f * less2DropCount10Days)
					/ (1.0f * totalCount));
			System.out
					.println("Total count " + totalCount + " less than "
							+ targetPos + "% increase in 10 days "
							+ less2IncreaseCount5Days + " rate "
							+ (100.0f * less2IncreaseCount10Days)
							/ (1.0f * totalCount));
			System.out.println("day10Yield = " + day10Yield);
			System.out.println("10 days exceptions " + exception10Days
					+ " rate " + (100.0f * exception10Days)
					/ (1.0f * totalCount));

			System.out.println("Total count " + totalCount + " less than "
					+ targetNeg + "% drop in 15 days " + less2DropCount5Days
					+ " rate " + (100.0f * less2DropCount15Days)
					/ (1.0f * totalCount));
			System.out
					.println("Total count " + totalCount + " less than "
							+ targetPos + "% increase in 15 days "
							+ less2IncreaseCount5Days + " rate "
							+ (100.0f * less2IncreaseCount15Days)
							/ (1.0f * totalCount));
			System.out.println("day15Yield = " + day15Yield);
			System.out.println("15 days exceptions " + exception15Days
					+ " rate " + (100.0f * exception15Days)
					/ (1.0f * totalCount));

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateDelta(String symbol, int counts) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  PTMA, seqindex from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' order by seqIndex desc";

			rs1 = stmt1.executeQuery(SQL);

			float[] ptma = new float[6];
			int[] seqIndex = new int[6];
			int loopCount = 0;

			while (rs1.next()) {
				if (loopCount > counts && counts > 0)
					break;

				if (loopCount == 0) {
					for (int k = 0; k < 6; k++) {
						if (k > 0)
							rs1.next();
						ptma[k] = rs1.getFloat(1);
						seqIndex[k] = rs1.getInt(2);
					}
				} else {
					for (int k = 0; k < 5; k++) {

						ptma[k] = ptma[k + 1];
						seqIndex[k] = seqIndex[k + 1];
					}
					ptma[5] = rs1.getFloat(1);
					seqIndex[5] = rs1.getInt(2);

				}

				/*
				 * SQL = "update BPMADMIN.INTRADAYSTOCKS  set delta1 = " +
				 * (ptma[0] - ptma[1]) + "  where seqIndex = " + seqIndex[0] +
				 * " and SYMBOL='" + symbol + "'"; stmt3.executeUpdate(SQL);
				 * 
				 * SQL = "update BPMADMIN.INTRADAYSTOCKS  set delta2 = " +
				 * (ptma[0] - ptma[2]) + "  where seqIndex = " + seqIndex[0] +
				 * " and SYMBOL='" + symbol + "'"; stmt3.executeUpdate(SQL); SQL
				 * = "update BPMADMIN.INTRADAYSTOCKS  set delta3 = " + (ptma[0]
				 * - ptma[3]) + "  where seqIndex = " + seqIndex[0] +
				 * " and SYMBOL='" + symbol + "'"; stmt3.executeUpdate(SQL); SQL
				 * = "update BPMADMIN.INTRADAYSTOCKS  set delta4 = " + (ptma[0]
				 * - ptma[4]) + "  where seqIndex = " + seqIndex[0] +
				 * " and SYMBOL='" + symbol + "'"; stmt3.executeUpdate(SQL); SQL
				 * = "update BPMADMIN.INTRADAYSTOCKS  set delta5 = " + (ptma[0]
				 * - ptma[5]) + "  where seqIndex = " + seqIndex[0] +
				 * " and SYMBOL='" + symbol + "'"; stmt3.executeUpdate(SQL);
				 */
				// ONLY DELTASUM SEEMS A GOOD VERY SHORT TERM FLUCTUATION
				// INDICATOR
				SQL = "update BPMADMIN.INTRADAYSTOCKS  set deltasum = "
						+ (4 * ptma[0] - ptma[4] - ptma[3] - ptma[2] - ptma[1])
						+ "  where seqIndex = " + seqIndex[0] + " and SYMBOL='"
						+ symbol + "'";
				stmt3.executeUpdate(SQL);

				loopCount++;
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void updateLatestDS3PerTrend(String nextStock, int seqIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			SQL = "select  DS3PER,FINALPRICE,SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "' and ( DS3PER>0.0000001 or DS3PER<-0.0000001) AND SEQINDEX>="
					+ (seqIndex - backCount) + " order by SEQINDEX ASC";

			if (seqIndex < 0) {
				getBothEnds(nextStock);
				SQL = "select  DS3PER,FINALPRICE,SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
						+ nextStock
						+ "' and ( DS3PER>0.0000001 or DS3PER<-0.0000001) AND SEQINDEX>="
						+ (endIndexLast - backCount) + " order by SEQINDEX ASC";

			}

			rs1 = stmt1.executeQuery(SQL);
			int loopCount = 0;
			int index1 = 0;
			int index2 = 0;
			float price1 = 0.0f;
			float price2 = 0.0f;
			float minPrice = 0.0f;
			float maxPrice = 0.0f;
			int recordNum = 0;
			boolean posBegin = false;
			boolean negBegin = false;
			boolean trackMinBegin = false;
			boolean trackMaxBegin = false;
			Hashtable priceRecords = new Hashtable();
			Hashtable maxMinRecords = new Hashtable();
			int maxMinRecordNum = 0;
			Hashtable indexRecords = new Hashtable();

			while (rs1.next()) {
				if (loopCount == 0 && rs1.getFloat(1) > 0) {
					posBegin = true;
					price1 = rs1.getFloat(2);
					index1 = rs1.getInt(3);
				} else if (loopCount == 0 && rs1.getFloat(1) < 0) {
					negBegin = true;
					price1 = rs1.getFloat(2);
					index1 = rs1.getInt(3);
				}

				if (loopCount > 0 && rs1.getFloat(1) > 0 && posBegin) {
					price1 = rs1.getFloat(2);
					index1 = rs1.getInt(3);
					if (trackMaxBegin && price1 > maxPrice) {
						maxPrice = price1;
					}
				} else if (loopCount > 0 && rs1.getFloat(1) < 0 && posBegin) {
					price2 = rs1.getFloat(2);
					index2 = rs1.getInt(3);
					if (trackMaxBegin) {
						maxMinRecordNum++;
						maxMinRecords.put("" + maxMinRecordNum, "" + maxPrice);
						trackMinBegin = true;
						minPrice = price2;
						trackMaxBegin = false;
					}
					recordNum++;
					if (recordNum == 1) {
						trackMinBegin = true;
						minPrice = price2;
					}
					priceRecords.put("" + recordNum, "" + price1);
					indexRecords.put("" + recordNum, "" + index1);
					recordNum++;
					priceRecords.put("" + recordNum, "" + price2);
					indexRecords.put("" + recordNum, "" + index2);

					posBegin = false;
					negBegin = true;
				}

				if (loopCount > 0 && rs1.getFloat(1) < 0 && negBegin) {
					price1 = rs1.getFloat(2);
					index1 = rs1.getInt(3);
					if (trackMinBegin && price1 < minPrice) {
						minPrice = price1;
					}
				} else if (loopCount > 0 && rs1.getFloat(1) > 0 && negBegin) {
					price2 = rs1.getFloat(2);
					index2 = rs1.getInt(3);
					if (trackMinBegin) {
						maxMinRecordNum++;
						maxMinRecords.put("" + maxMinRecordNum, "" + minPrice);
						trackMinBegin = false;
						trackMaxBegin = true;
						maxPrice = price2;
					}
					recordNum++;
					if (recordNum == 1) {
						trackMaxBegin = true;
						maxPrice = price2;
					}

					priceRecords.put("" + recordNum, "" + price1);
					indexRecords.put("" + recordNum, "" + index1);
					recordNum++;
					priceRecords.put("" + recordNum, "" + price2);
					indexRecords.put("" + recordNum, "" + index2);

					posBegin = true;
					negBegin = false;
				}

				loopCount++;

			}

			int maxRecordNum = priceRecords.size();
			int edgeTrendCount = 0;
			int minMaxCount = 0;
			float p1 = 0;
			float p2 = 0;
			float p3 = 0;
			float p4 = 0;
			int maxMinTrendCount = 0;
			for (int k = 1; k < maxRecordNum; k++) {
				if (k % 2 == 1) {
					int start = k;
					int end = k + 3;
					minMaxCount++;
					if (end <= maxRecordNum) {
						float startPrice = Float.parseFloat(priceRecords.get(
								"" + start).toString());
						float endPrice = Float.parseFloat(priceRecords.get(
								"" + end).toString());
						int startIndex = Integer.parseInt(indexRecords.get(
								"" + start).toString());
						int endIndex = Integer.parseInt(indexRecords.get(
								"" + end).toString());
						int minMaxStartIndex = Integer.parseInt(indexRecords
								.get("" + (start + 1)).toString());
						int minMaxEndIndex = Integer.parseInt(indexRecords.get(
								"" + (end - 1)).toString());
						float minMaxPrice = Float.parseFloat(maxMinRecords.get(
								"" + minMaxCount).toString());
						if (p1 < 0.00001) {
							p1 = minMaxPrice;
						} else if (p2 < 0.00001) {
							p2 = minMaxPrice;
						} else if (p3 < 0.00001) {
							p3 = minMaxPrice;
						} else if (p4 < 0.00001) {
							p4 = minMaxPrice;
						} else {
							p1 = p2;
							p2 = p3;
							p3 = p4;
							p4 = minMaxPrice;
						}

						// System.out.println("start index "+startIndex+" start price "+startPrice);
						// System.out.println("end index "+endIndex+" end price "+endPrice);
						// System.out.println("minmax start index "+minMaxStartIndex+"minmax end index "+minMaxEndIndex+" minmax price "+minMaxPrice);

						if (edgeTrendCount >= 0 && endPrice > startPrice) {
							// if(edgeTrendCount==0)
							// System.out.println("Positive trend starts at "+startIndex);

							edgeTrendCount++;
							// System.out.println("edgeTrendCount is now "+edgeTrendCount);
							if (edgeTrendCount >= 4) {
								// System.out.println("Edge Trend count "+edgeTrendCount+" at "+
								// endIndex);
								if (p1 < p3 && p2 < p4) {
									maxMinTrendCount++;
									/*
									 * String sql1 =
									 * "select FFP from  BPMADMIN.INTRADAYSTOCKS  WHERE SYMBOL ='"
									 * + nextStock + "' and SEQINDEX = " +
									 * endIndex;
									 * 
									 * rs1 = stmt1.executeQuery(sql1); int ffp =
									 * 0; if (rs1.next()) { ffp = rs1.getInt(1);
									 * //
									 * System.out.println("Current ffp "+ffp); }
									 */
									int ds3ptScore = 10000 + edgeTrendCount;
									String sql1 = "Update BPMADMIN.INTRADAYSTOCKS SET DS3PT = "
											+ ds3ptScore
											+ " WHERE SYMBOL ='"
											+ nextStock
											+ "' and SEQINDEX = "
											+ endIndex;

									if (endIndex == seqIndex) {
										System.out.println(sql1);
										// System.out
										// .println("Query update executed !");

										stmt2.executeUpdate(sql1);
									}
									// System.out.println("maxMinTrendCount "+maxMinTrendCount);
									// System.out.println("price trend validated "+p1+" to "+p3+", "+p2+" to "+p4);
								} else {
									// SKIP UPDATE THIS CONDITION FOR NOW
									// 8/3/2017
									// MIGHT BE IMPORTANT TO FOLLOW ON LATER

									// System.out.println("maxMinTrendCount "+maxMinTrendCount);
									// System.out.println("up trend divergent signal: edgeTrendCount "+edgeTrendCount+" at "+endIndex);
									// System.out.println("price trend not validated "+p1+" to "+p3+", "+p2+" to "+p4);
								}
								// update records FFP
							}
						} else if (edgeTrendCount > 0 && endPrice < startPrice) {
							// System.out.println("Reset values, Positive trend ends at "+endIndex);

							edgeTrendCount = -1;
							maxMinTrendCount = 0;
							p1 = minMaxPrice;
							p2 = 0;
							p3 = 0;
							p4 = 0;
							// System.out.println("edgeTrendCount is now "+edgeTrendCount);
							// System.out.println("Negative trend starts at "+startIndex);
						} else if (edgeTrendCount <= 0 && endPrice < startPrice) {
							// if(edgeTrendCount==0)
							// System.out.println("Negative trend starts at "+startIndex);

							edgeTrendCount--;
							// System.out.println("edgeTrendCount is now "+edgeTrendCount);
							if (edgeTrendCount <= -4) {
								// System.out.println("Edge Trend count "
								// + edgeTrendCount + " at " + endIndex);
								if (p1 > p3 && p2 > p4) {
									maxMinTrendCount--;
									// System.out.println("maxMinTrendCount "+maxMinTrendCount);
									// System.out.println("price trend validated "+p1+" to "+p3+", "+p2+" to "+p4);
									/*
									 * String sql1 =
									 * "select FFP from  BPMADMIN.INTRADAYSTOCKS  WHERE SYMBOL ='"
									 * + nextStock + "' and SEQINDEX = " +
									 * endIndex;
									 * 
									 * rs1 = stmt1.executeQuery(sql1); int ffp =
									 * 0; if (rs1.next()) { ffp = rs1.getInt(1);
									 * //
									 * System.out.println("Current ffp "+ffp); }
									 */
									// ffp = -10000 + edgeTrendCount * 1000 +
									// ffp;
									int ds3ptScore = -10000 + edgeTrendCount;
									String sql1 = "Update BPMADMIN.INTRADAYSTOCKS SET DS3PT = "
											+ ds3ptScore
											+ " WHERE SYMBOL ='"
											+ nextStock
											+ "' and SEQINDEX = "
											+ endIndex;

									if (endIndex == seqIndex) {
										System.out.println(sql1);
										// System.out
										// .println("Query update executed !");
										stmt2.executeUpdate(sql1);
									}
								} else {
									// SKIP UPDATE THIS CONDITION FOR NOW
									// 8/3/2017
									// MIGHT BE IMPORTANT TO FOLLOW ON LATER
									// System.out.println("down trend divergent signal: edgeTrendCount "+edgeTrendCount+" at "+endIndex);
									// System.out.println("price trend not validated "+p1+" to "+p3+", "+p2+" to "+p4);

									// System.out.println("maxMinTrendCount "+maxMinTrendCount);
									// System.out.println("price trend not validated "+p1+" to "+p3+", "+p2+" to "+p4);
								}
								// update records FFP
							}
						} else if (edgeTrendCount < 0 && endPrice > startPrice) {
							// System.out.println("Reset values, Negative trend ends at "+endIndex);
							edgeTrendCount = 1;
							maxMinTrendCount = 0;
							// System.out.println("edgeTrendCount is now "+edgeTrendCount);
							// System.out.println("Positive trend starts at "+startIndex);
							p1 = minMaxPrice;
							p2 = 0;
							p3 = 0;
							p4 = 0;
						}

					} else {
						break;
					}
				}
				k++;

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void getDCPTrendHistoryByPercentageStep1(String nextStock,
			long maxPreIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/27/2018
			long tempIndex = 0;
			if (maxPreIndex > 0) {
				SQL = "select SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ nextStock
						+ "' and ( TWA>1 or TWA<-1) AND SEQINDEX<"
						+ maxPreIndex + " ORDER BY SEQINDEX DESC";
				rs1 = stmt1.executeQuery(SQL);

				int lc = 0;
				while (rs1.next()) {
					tempIndex = rs1.getLong(1);
					lc++;
					if (lc >= 1) { // we only need previous 1 record value
						break;
					}
				}

			}
			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/27/2018

			if (stmt2 == null)
				stmt2 = con.createStatement();

			SQL = "select  FINALPRICE,SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "' and ( TWA>1 or TWA<-1) and SEQINDEX>="
					+ tempIndex + " order by SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);

			int index1 = 0;
			int index2 = 0;
			float price1 = 0.0f;
			float price2 = 0.0f;
			float dcpip = 0.0f;

			while (rs1.next()) {
				if (index1 == 0) {
					price1 = rs1.getFloat(1);
					index1 = rs1.getInt(2);
				} else if (index2 == 0) {
					price2 = rs1.getFloat(1);
					index2 = rs1.getInt(2);
				} else {

					dcpip = (100.0f * (price2 - price1)) / price1;
					String sql1 = "Update BPMADMIN.INTRADAYSTOCKS SET DCPIP = "
							+ dcpip + "  WHERE SYMBOL ='" + nextStock
							+ "' and SEQINDEX = " + index2;

					if (disLog)
						System.out.println(sql1);

					if (index2 >= maxPreIndex)
						stmt2.executeUpdate(sql1);

					price1 = price2;
					index1 = index2;

					price2 = rs1.getFloat(1);
					index2 = rs1.getInt(2);

				}

			}

			dcpip = (100.0f * (price2 - price1)) / price1;
			String sql1 = "Update BPMADMIN.INTRADAYSTOCKS SET DCPIP = " + dcpip
					+ "  WHERE SYMBOL ='" + nextStock + "' and SEQINDEX = "
					+ index2;

			if (disLog)
				System.out.println(sql1);

			if (index2 >= maxPreIndex)
				stmt2.executeUpdate(sql1);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void updateLatestDS3PerTrendByPercentageStep1(
			String nextStock, int seqIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			SQL = "select  DS3PER,FINALPRICE,SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "' and SEQINDEX>"
					+ (seqIndex - 10)
					+ " and SEQINDEX<="
					+ seqIndex
					+ " and ( DS3PER>0.0000001 or DS3PER<-0.0000001) order by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);
			int[] index = new int[4];
			float[] price = new float[4];
			float[] ds3per = new float[4];
			float price1 = 0.0f;
			float price2 = 0;
			int index1 = 0;
			int index2 = 0;
			float ds3per1 = 0.0f;
			float ds3per2 = 0.0f;
			int arrayIndex = 0;
			int days = 0;

			while (rs1.next()) {
				if (index1 == 0) {
					ds3per1 = rs1.getFloat(1);
					price1 = rs1.getFloat(2);
					index1 = rs1.getInt(3);
				} else if (index2 == 0) {
					ds3per2 = rs1.getFloat(1);
					price2 = rs1.getFloat(2);
					index2 = rs1.getInt(3);
				} else {
					if ((ds3per1 > 0 && ds3per2 < 0)
							|| (ds3per1 < 0 && ds3per2 > 0)) {
						index[arrayIndex] = index1;
						price[arrayIndex] = price1;
						ds3per[arrayIndex] = ds3per1;
						arrayIndex = (arrayIndex + 1) % 4;
						index[arrayIndex] = index2;
						price[arrayIndex] = price2;
						ds3per[arrayIndex] = ds3per2;
						days++;
						if (arrayIndex == 3)// array full
						{
							// calculation
							float per = 100.0f * (price[3] - price[0])
									/ price[0];

							if (disLog)
								System.out.println(index[3] + "  " + per
										+ " days: " + days);

							String sql1 = "Update BPMADMIN.INTRADAYSTOCKS SET DS3PIPDAYS = "
									+ (days - 1)
									+ ", DS3PIP = "
									+ per
									+ "  WHERE SYMBOL ='"
									+ nextStock
									+ "' and SEQINDEX = " + index[3];

							if (disLog)
								System.out.println(sql1);

							stmt2.executeUpdate(sql1);
							// array shift
							index[0] = index[2];
							price[0] = price[2];
							ds3per[0] = ds3per[2];

							index[1] = index[3];
							price[1] = price[3];
							ds3per[1] = ds3per[3];

							arrayIndex = 2;
							days = 1;
						}

						ds3per1 = ds3per2;
						price1 = price2;
						index1 = index2;
						ds3per2 = rs1.getFloat(1);
						price2 = rs1.getFloat(2);
						index2 = rs1.getInt(3);

					} else {
						ds3per1 = ds3per2;
						price1 = price2;
						index1 = index2;
						ds3per2 = rs1.getFloat(1);
						price2 = rs1.getFloat(2);
						index2 = rs1.getInt(3);
						days++;
					}
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void getDS3PerTrendHistoryByPercentageStep1(String nextStock,
			long maxPreIndex, boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			SQL = "select  DS3PER,FINALPRICE,SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "' and ( DS3PER>0.0000001 or DS3PER<-0.0000001) and SEQINDEX>"
					+ (maxPreIndex - 2) + " order by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);

			float price1 = 0.0f;
			float price2 = 0;
			int index1 = 0;
			int index2 = 0;
			float ds3per1 = 0.0f;
			float ds3per2 = 0.0f;

			while (rs1.next()) {
				if (index1 == 0) {
					ds3per1 = rs1.getFloat(1);
					price1 = rs1.getFloat(2);
					index1 = rs1.getInt(3);
				} else if (index2 == 0) {
					ds3per2 = rs1.getFloat(1);
					price2 = rs1.getFloat(2);
					index2 = rs1.getInt(3);
				} else {
					if (ds3per1 > 0 && ds3per2 < 0) {

						int days = 0;
						/*
						 * String sql2 =
						 * "SELECT DS3PIPW from BPMADMIN.INTRADAYSTOCKS  WHERE SYMBOL ='"
						 * + nextStock + "' and SEQINDEX=" + index1; rs2 =
						 * stmt2.executeQuery(sql2);
						 * 
						 * if (rs2.next()) { days = rs2.getInt(1); }
						 */

						if (days == 0) {
							String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET DS3PIPW = 1"
									+ "  WHERE SYMBOL ='"
									+ nextStock
									+ "' and SEQINDEX=" + index1;
							if (index1 >= maxPreIndex)
								stmt2.executeUpdate(sql2);
						}

						/*
						 * days = 0;
						 * 
						 * sql2 =
						 * "SELECT DS3PIPW from BPMADMIN.INTRADAYSTOCKS  WHERE SYMBOL ='"
						 * + nextStock + "' and SEQINDEX=" + index2; rs2 =
						 * stmt2.executeQuery(sql2);
						 * 
						 * if (rs2.next()) { days = rs2.getInt(1); }
						 */

						if (days == 0) {
							String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET DS3PIPW = -1"
									+ "  WHERE SYMBOL ='"
									+ nextStock
									+ "' and SEQINDEX=" + index2;
							if (index2 >= maxPreIndex)
								stmt2.executeUpdate(sql2);
						}

						if (lastOnly) {
							break;
						}

					} else if (ds3per1 < 0 && ds3per2 > 0) {
						int days = 0;
						/*
						 * String sql2 =
						 * "SELECT DS3PIPDAYS from BPMADMIN.INTRADAYSTOCKS  WHERE SYMBOL ='"
						 * + nextStock + "' and SEQINDEX=" + index1; rs2 =
						 * stmt2.executeQuery(sql2);
						 * 
						 * if (rs2.next()) { days = rs2.getInt(1); }
						 */
						if (days == 0) {
							String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET DS3PIPW = -1"
									+ "  WHERE SYMBOL ='"
									+ nextStock
									+ "' and SEQINDEX=" + index1;
							if (index1 >= maxPreIndex)
								stmt2.executeUpdate(sql2);
						}

						/*
						 * days = 0; sql2 =
						 * "SELECT DS3PIPDAYS  from BPMADMIN.INTRADAYSTOCKS  WHERE SYMBOL ='"
						 * + nextStock + "' and SEQINDEX=" + index2; rs2 =
						 * stmt2.executeQuery(sql2);
						 * 
						 * if (rs2.next()) { days = rs2.getInt(1); }
						 */
						if (days == 0) {
							String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET DS3PIPW = 1"
									+ "  WHERE SYMBOL ='"
									+ nextStock
									+ "' and SEQINDEX=" + index2;
							if (index2 >= maxPreIndex)
								stmt2.executeUpdate(sql2);
						}

						if (lastOnly) {
							break;
						}
					}

					ds3per1 = ds3per2;
					price1 = price2;
					index1 = index2;
					ds3per2 = rs1.getFloat(1);
					price2 = rs1.getFloat(2);
					index2 = rs1.getInt(3);

					if (index1 < maxPreIndex - 1) {
						break;
					}

				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void getDS3PerTrendHistoryByPercentageStep2(String nextStock,
			long maxPreIndex, boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018
			long tempIndex2 = 0;
			if (maxPreIndex > 0) {
				SQL = "select  seqindex from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ nextStock
						+ "' and (DS3PIPW=1 or DS3PIPW=-1) AND SEQINDEX<="
						+ maxPreIndex + " ORDER BY SEQINDEX DESC";
				rs1 = stmt1.executeQuery(SQL);

				int lc = 0;
				while (rs1.next()) {
					tempIndex2 = rs1.getLong(1);
					lc++;
					if (lc >= 3) { // only needs last 3 DS3PIPDAYS change
						break;
					}

				}

			}
			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018

			SQL = "select DS3PER, FINALPRICE,SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "' and (DS3PIPW=1 OR DS3PIPW=-1)  and SEQINDEX>="
					+ tempIndex2 + " order by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);

			float price1p = 0.0f;
			float price2p = 0;
			int index1p = 0;
			int index2p = 0;
			float ds3per1p = 0.0f;
			float ds3per2p = 0.0f;

			float price1n = 0.0f;
			float price2n = 0;
			int index1n = 0;
			int index2n = 0;
			float ds3per1n = 0.0f;
			float ds3per2n = 0.0f;
			int loop = 0;
			boolean trackNeg = false;
			boolean trackPos = false;
			boolean trackSecondValp = false;
			boolean trackSecondValn = false;

			while (rs1.next()) {
				float tempDS3PER = rs1.getFloat(1);
				float temPrice = rs1.getFloat(2);
				int tempIndex = rs1.getInt(3);

				if (loop == 0 && tempDS3PER > 0) {
					trackPos = true;
					trackNeg = false;
				} else if (loop == 0 && tempDS3PER < 0) {
					trackPos = false;
					trackNeg = true;
				}

				loop++;

				if (trackPos && !trackSecondValp && tempDS3PER > 0) {
					price1p = temPrice;
					index1p = tempIndex;
				} else if (trackPos && trackSecondValp && tempDS3PER > 0) {
					price2p = temPrice;
					index2p = tempIndex;

					// calculation
					float per = 100.0f * (price1p - price2p) / price2p;
					String sql1 = "Select count(*) from BPMADMIN.INTRADAYSTOCKS  where SYMBOL ='"
							+ nextStock
							+ "' and SEQINDEX>"
							+ index2p
							+ " AND SEQINDEX<=" + index1p;
					System.out.println(sql1);

					rs2 = stmt2.executeQuery(sql1);

					int days = 0;

					if (rs2.next()) {
						days = rs2.getInt(1);
					}
					System.out.println("days " + days);
					sql1 = "Update BPMADMIN.INTRADAYSTOCKS SET DS3PIP= " + per
							+ ",  DS3PIPDAYS = " + days + " WHERE SYMBOL ='"
							+ nextStock + "' and SEQINDEX = " + index1p;
					System.out.println(sql1);

					if (index1p >= maxPreIndex)
						stmt2.executeUpdate(sql1);

					// reset values
					trackPos = false;
					trackNeg = true;
					trackSecondValp = false;

					if (lastOnly) {
						break;
					}
				} else if (trackNeg && !trackSecondValn && tempDS3PER < 0) {
					price1n = temPrice;
					index1n = tempIndex;
				} else if (trackNeg && trackSecondValn && tempDS3PER < 0) {
					price2n = temPrice;
					index2n = tempIndex;

					// calculation
					float per = 100.0f * (price1n - price2n) / price2n;
					String sql1 = "Select count(*) from BPMADMIN.INTRADAYSTOCKS  where SYMBOL ='"
							+ nextStock
							+ "' and SEQINDEX>"
							+ index2n
							+ " AND SEQINDEX<=" + index1n;
					System.out.println(sql1);

					rs2 = stmt2.executeQuery(sql1);

					int days = 0;

					if (rs2.next()) {
						days = rs2.getInt(1);
					}

					sql1 = "Update BPMADMIN.INTRADAYSTOCKS SET DS3PIP= " + per
							+ ",  DS3PIPDAYS = " + days + " WHERE SYMBOL ='"
							+ nextStock + "' and SEQINDEX = " + index1n;
					System.out.println(sql1);

					if (index1n >= maxPreIndex)
						stmt2.executeUpdate(sql1);

					// reset values
					trackPos = true;
					trackNeg = false;
					trackSecondValn = false;

					if (lastOnly) {
						break;
					}
				}

				if (trackPos && tempDS3PER < 0) {
					trackSecondValp = true;
					price1n = temPrice;
					index1n = tempIndex;
				} else if (trackNeg && tempDS3PER > 0) {
					trackSecondValn = true;
					price1p = temPrice;
					index1p = tempIndex;
				}

			}

			/*
			 * String sql1 =
			 * "Update BPMADMIN.INTRADAYSTOCKS SET DS3PIPDAYS = 0 WHERE SYMBOL ='"
			 * + nextStock + "' and (DS3PIPDAYS  = -1 OR DS3PIPDAYS =1)";
			 * System.out.println(sql1);
			 * 
			 * stmt2.executeUpdate(sql1);
			 */

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void getDS3PerTrendHistoryByPercentageStep3(String nextStock,
			long maxPreIndex, boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 3/1/2018
			long tempIndex = 0;
			float tempDs3per = 0.0f;
			if (maxPreIndex > 0) {
				// this is where trend broke, so a new starting point going
				// forward
				SQL = "select  SEQINDEX,DS3PER from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ nextStock
						+ "' and SEQINDEX<="
						+ maxPreIndex
						+ " AND (DS3PT=10804 OR DS3PT=-10804 OR DS3PT=10004 OR DS3PT=-10004) ORDER BY SEQINDEX DESC";
				rs1 = stmt1.executeQuery(SQL);

				if (rs1.next()) {
					tempDs3per = rs1.getFloat(1);
					tempIndex = rs1.getLong(2);
				}

			}
			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 3/1/2018

			SQL = "select  DS3PIP ,DS3PIPDAYS,SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "' and DS3PIPDAYS>2 and SEQINDEX>="
					+ (tempIndex - 1) + " order by SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);
			int size = 4;
			int[] index = new int[size];
			float[] ds3pip = new float[size];
			int[] ds3pidDays = new int[size];
			int days = 0;
			int loop = 0;
			float sumDs3pip = 0.0f;
			int contCount = 0;
			// NEED TO USE STATISTICS DATA, avg 0.6496/day FOR +, -0.63/DAY FOR
			// -
			// use ALL POSITIVE VALUE AVG AS IF ALL POSITIVE
			float threshHoldIncrease = 0.051f;// every 5 minutes gap value
			// float threshHoldIncrease = 0.66f;//every day time gap value

			while (rs1.next()) {
				if (loop < size) {
					ds3pip[loop] = rs1.getFloat(1);
					ds3pidDays[loop] = rs1.getInt(2);
					index[loop] = rs1.getInt(3);

				} else {
					days = 0;
					sumDs3pip = 0.0f;
					for (int k = 0; k < size; k++) {
						days = days + ds3pidDays[k];
						sumDs3pip = sumDs3pip + ds3pip[k];
					}

					if (sumDs3pip < (1.0f * days) * threshHoldIncrease) {
						sumDs3pip = 0.0f;
						days = 0;
						contCount = 0;

					} else {
						contCount++;
						if (contCount >= 1) {
							int ds3ptScore = 10800 + contCount + 3;
							String sql2 = "Select DS3PT from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
									+ nextStock
									+ "' and SEQINDEX = "
									+ index[3];

							rs3 = stmt3.executeQuery(sql2);

							int val = 0;
							if (rs3.next()) {
								val = rs3.getInt(1);
							}

							if (val == 0 && ds3ptScore > val) {
								String sql1 = "Update BPMADMIN.INTRADAYSTOCKS SET DS3PT = "
										+ ds3ptScore
										+ " WHERE SYMBOL ='"
										+ nextStock
										+ "' and SEQINDEX = "
										+ index[3];

								System.out.println("sumDs3pip " + sumDs3pip);
								System.out.println(sql1);

								if (index[3] >= maxPreIndex)
									stmt2.executeUpdate(sql1);
							}

						}
					}

					for (int k = 0; k < size - 1; k++) {
						index[k] = index[k + 1];
						ds3pip[k] = ds3pip[k + 1];
						ds3pidDays[k] = ds3pidDays[k + 1];
					}
					ds3pip[size - 1] = rs1.getFloat(1);
					ds3pidDays[size - 1] = rs1.getInt(2);
					index[size - 1] = rs1.getInt(3);

				}

				loop++;

			}

			days = 0;
			sumDs3pip = 0.0f;
			for (int k = 0; k < size; k++) {
				days = days + ds3pidDays[k];
				sumDs3pip = sumDs3pip + ds3pip[k];
			}

			if (sumDs3pip < (1.0f * days) * threshHoldIncrease) {
				sumDs3pip = 0.0f;
				days = 0;
				contCount = 0;
			} else {
				contCount++;
				if (contCount >= 1) {
					int ds3ptScore = 10800 + contCount + 3;
					String sql2 = "Select DS3PT from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
							+ nextStock + "' and SEQINDEX = " + index[3];

					rs3 = stmt3.executeQuery(sql2);

					int val = 0;
					if (rs3.next()) {
						val = rs3.getInt(1);
					}

					if (ds3ptScore > val && val == 0) {
						String sql1 = "Update BPMADMIN.INTRADAYSTOCKS SET DS3PT = "
								+ ds3ptScore
								+ " WHERE SYMBOL ='"
								+ nextStock
								+ "' and SEQINDEX = " + index[3];
						System.out.println(sql1);
						System.out.println("sumDs3pip " + sumDs3pip);

						if (index[3] >= maxPreIndex)
							stmt2.executeUpdate(sql1);
					}

				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void getDS3PerTrendHistory(String nextStock, long maxPreIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 3/1/2018
			long tempIndex = 0;
			float tempDs3per = 0.0f;
			if (maxPreIndex > 0) {
				// this is where trend broke, so a new starting point going
				// forward
				SQL = "select  SEQINDEX,DS3PER from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ nextStock
						+ "' and SEQINDEX<="
						+ maxPreIndex
						+ " AND (DS3PT=10804 OR DS3PT=-10804 OR DS3PT=10004 OR DS3PT=-10004)  ORDER BY SEQINDEX DESC";
				rs1 = stmt1.executeQuery(SQL);

				if (rs1.next()) {
					tempDs3per = rs1.getFloat(1);
					tempIndex = rs1.getLong(2);
				}

			}
			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 3/1/2018

			System.out.println("tempIndex " + tempIndex);
			SQL = "select  DS3PER,FINALPRICE,SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "' and ( DS3PER>0.0000001 or DS3PER<-0.0000001) AND SEQINDEX>="
					+ (tempIndex - 1) + " order by SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);
			int loopCount = 0;
			long index1 = 0;
			long index2 = 0;
			float price1 = 0.0f;
			float price2 = 0.0f;
			float minPrice = 0.0f;
			float maxPrice = 0.0f;
			int recordNum = 0;
			boolean posBegin = false;
			boolean negBegin = false;
			boolean trackMinBegin = false;
			boolean trackMaxBegin = false;
			Hashtable priceRecords = new Hashtable();
			Hashtable maxMinRecords = new Hashtable();
			int maxMinRecordNum = 0;
			Hashtable indexRecords = new Hashtable();
			float DS3PER1 = 0.0f;
			float DS3PER2 = 0.0f;
			float DS3PER3 = 0.0f;

			while (rs1.next()) {
				// no need to set boundary to zero of DS3PER continues trend
				// like TWA
				if (loopCount == 0 && rs1.getFloat(1) > 0) {
					posBegin = true;
					price1 = rs1.getFloat(2);
					index1 = rs1.getLong(3);
				} else if (loopCount == 0 && rs1.getFloat(1) < 0) {
					negBegin = true;
					price1 = rs1.getFloat(2);
					index1 = rs1.getLong(3);
				}

				if (loopCount > 0 && rs1.getFloat(1) > 0 && posBegin) {
					price1 = rs1.getFloat(2);
					index1 = rs1.getInt(3);
					if (trackMaxBegin && price1 > maxPrice) {
						maxPrice = price1;
					}
				} else if (loopCount > 0 && rs1.getFloat(1) < 0 && posBegin) {
					price2 = rs1.getFloat(2);
					index2 = rs1.getInt(3);
					if (trackMaxBegin) {
						maxMinRecordNum++;
						maxMinRecords.put("" + maxMinRecordNum, "" + maxPrice);
						System.out.println("Put maxMinRecordNum:"
								+ maxMinRecordNum + ", maxPrice:" + maxPrice);
						trackMinBegin = true;
						minPrice = price2;
						trackMaxBegin = false;
					}
					recordNum++;
					if (recordNum == 1) {
						trackMinBegin = true;
						minPrice = price2;
					}
					priceRecords.put("" + recordNum, "" + price1);
					System.out.println("Put recordNum:" + recordNum
							+ ", price:" + price1);

					indexRecords.put("" + recordNum, "" + index1);
					System.out.println("Put recordNum:" + recordNum
							+ ", index:" + index1);

					recordNum++;
					priceRecords.put("" + recordNum, "" + price2);
					System.out.println("Put recordNum:" + recordNum
							+ ", price:" + price2);

					indexRecords.put("" + recordNum, "" + index2);
					System.out.println("Put recordNum:" + recordNum
							+ ", index:" + index2);

					posBegin = false;
					negBegin = true;
				}

				if (loopCount > 0 && rs1.getFloat(1) < 0 && negBegin) {
					price1 = rs1.getFloat(2);
					index1 = rs1.getLong(3);
					if (trackMinBegin && price1 < minPrice) {
						minPrice = price1;
					}
				} else if (loopCount > 0 && rs1.getFloat(1) > 0 && negBegin) {
					price2 = rs1.getFloat(2);
					index2 = rs1.getLong(3);
					if (trackMinBegin) {
						maxMinRecordNum++;
						maxMinRecords.put("" + maxMinRecordNum, "" + minPrice);
						System.out.println("Put maxMinRecordNum:"
								+ maxMinRecordNum + ", minPrice:" + minPrice);

						trackMinBegin = false;
						trackMaxBegin = true;
						maxPrice = price2;
					}
					recordNum++;
					if (recordNum == 1) {
						trackMaxBegin = true;
						maxPrice = price2;
					}

					priceRecords.put("" + recordNum, "" + price1);
					System.out.println("Put recordNum:" + recordNum
							+ ", price:" + price1);

					indexRecords.put("" + recordNum, "" + index1);
					System.out.println("Put recordNum:" + recordNum
							+ ", index:" + index1);

					recordNum++;
					priceRecords.put("" + recordNum, "" + price2);
					System.out.println("Put recordNum:" + recordNum
							+ ", price:" + price2);

					indexRecords.put("" + recordNum, "" + index2);
					System.out.println("Put recordNum:" + recordNum
							+ ", index:" + index2);

					posBegin = true;
					negBegin = false;
				}

				loopCount++;

			}

			int maxRecordNum = priceRecords.size();
			int edgeTrendCount = 0;
			int minMaxCount = 0;
			float p1 = 0;
			float p2 = 0;
			float p3 = 0;
			float p4 = 0;
			int maxMinTrendCount = 0;
			for (int k = 1; k < maxRecordNum; k++) {
				if (k % 2 == 1) {
					int start = k;
					int end = k + 3;
					minMaxCount++;
					if (end <= maxRecordNum) {
						float startPrice = Float.parseFloat(priceRecords.get(
								"" + start).toString());
						System.out.println("startPrice :" + startPrice);
						float endPrice = Float.parseFloat(priceRecords.get(
								"" + end).toString());
						System.out.println("endPrice :" + endPrice);
						int startIndex = Integer.parseInt(indexRecords.get(
								"" + start).toString());
						System.out.println("startIndex :" + startIndex);
						int endIndex = Integer.parseInt(indexRecords.get(
								"" + end).toString());
						System.out.println("endIndex :" + endIndex);
						int minMaxStartIndex = Integer.parseInt(indexRecords
								.get("" + (start + 1)).toString());
						System.out.println("minMaxStartIndex :"
								+ minMaxStartIndex);
						int minMaxEndIndex = Integer.parseInt(indexRecords.get(
								"" + (end - 1)).toString());
						System.out.println("minMaxEndIndex :" + minMaxEndIndex);
						float minMaxPrice = Float.parseFloat(maxMinRecords.get(
								"" + minMaxCount).toString());
						System.out.println("minMaxPrice :" + minMaxPrice);

						if (p1 < 0.00001) {
							p1 = minMaxPrice;
						} else if (p2 < 0.00001) {
							p2 = minMaxPrice;
						} else if (p3 < 0.00001) {
							p3 = minMaxPrice;
						} else if (p4 < 0.00001) {
							p4 = minMaxPrice;
						} else {
							p1 = p2;
							p2 = p3;
							p3 = p4;
							p4 = minMaxPrice;
						}

						// System.out.println("start index "+startIndex+" start price "+startPrice);
						// System.out.println("end index "+endIndex+" end price "+endPrice);
						// System.out.println("minmax start index "+minMaxStartIndex+"minmax end index "+minMaxEndIndex+" minmax price "+minMaxPrice);

						if (edgeTrendCount >= 0 && endPrice > startPrice) {
							// if(edgeTrendCount==0)
							// System.out.println("Positive trend starts at "+startIndex);

							edgeTrendCount++;
							// System.out.println("edgeTrendCount is now "+edgeTrendCount);
							if (edgeTrendCount >= 4) {
								// System.out.println("Edge Trend count "+edgeTrendCount+" at "+
								// endIndex);
								if (p1 < p3 && p2 < p4) {
									maxMinTrendCount++;
									/*
									 * String sql1 =
									 * "select FFP from  BPMADMIN.INTRADAYSTOCKS  WHERE SYMBOL ='"
									 * + nextStock + "' and SEQINDEX = " +
									 * endIndex;
									 * 
									 * rs1 = stmt1.executeQuery(sql1); int ffp =
									 * 0; if (rs1.next()) { ffp = rs1.getInt(1);
									 * //
									 * System.out.println("Current ffp "+ffp); }
									 */
									int ds3ptScore = 10000 + edgeTrendCount;
									String sql1 = "Update BPMADMIN.INTRADAYSTOCKS SET DS3PT = "
											+ ds3ptScore
											+ " WHERE SYMBOL ='"
											+ nextStock
											+ "' and SEQINDEX = "
											+ endIndex;
									System.out.println(sql1);

									if (endIndex >= maxPreIndex)
										stmt2.executeUpdate(sql1);
									// System.out.println("maxMinTrendCount "+maxMinTrendCount);
									// System.out.println("price trend validated "+p1+" to "+p3+", "+p2+" to "+p4);
								} else {
									// SKIP UPDATE THIS CONDITION FOR NOW
									// 8/3/2017
									// MIGHT BE IMPORTANT TO FOLLOW ON LATER

									// System.out.println("maxMinTrendCount "+maxMinTrendCount);
									// System.out.println("up trend divergent signal: edgeTrendCount "+edgeTrendCount+" at "+endIndex);
									// System.out.println("price trend not validated "+p1+" to "+p3+", "+p2+" to "+p4);
								}
								// update records FFP
							}
						} else if (edgeTrendCount > 0 && endPrice < startPrice) {
							// System.out.println("Reset values, Positive trend ends at "+endIndex);

							edgeTrendCount = -1;
							maxMinTrendCount = 0;
							p1 = minMaxPrice;
							p2 = 0;
							p3 = 0;
							p4 = 0;
							// System.out.println("edgeTrendCount is now "+edgeTrendCount);
							// System.out.println("Negative trend starts at "+startIndex);
						} else if (edgeTrendCount <= 0 && endPrice < startPrice) {
							// if(edgeTrendCount==0)
							// System.out.println("Negative trend starts at "+startIndex);

							edgeTrendCount--;
							// System.out.println("edgeTrendCount is now "+edgeTrendCount);
							if (edgeTrendCount <= -4) {
								System.out.println("Edge Trend count "
										+ edgeTrendCount + " at " + endIndex);
								if (p1 > p3 && p2 > p4) {
									maxMinTrendCount--;
									// System.out.println("maxMinTrendCount "+maxMinTrendCount);
									// System.out.println("price trend validated "+p1+" to "+p3+", "+p2+" to "+p4);
									/*
									 * String sql1 =
									 * "select FFP from  BPMADMIN.INTRADAYSTOCKS  WHERE SYMBOL ='"
									 * + nextStock + "' and SEQINDEX = " +
									 * endIndex;
									 * 
									 * rs1 = stmt1.executeQuery(sql1); int ffp =
									 * 0; if (rs1.next()) { ffp = rs1.getInt(1);
									 * //
									 * System.out.println("Current ffp "+ffp); }
									 */
									int ds3ptScore = -10000 + edgeTrendCount;
									String sql1 = "Update BPMADMIN.INTRADAYSTOCKS SET DS3PT = "
											+ ds3ptScore
											+ " WHERE SYMBOL ='"
											+ nextStock
											+ "' and SEQINDEX = "
											+ endIndex;
									System.out.println(sql1);

									if (endIndex >= maxPreIndex)
										stmt2.executeUpdate(sql1);
								} else {
									// SKIP UPDATE THIS CONDITION FOR NOW
									// 8/3/2017
									// MIGHT BE IMPORTANT TO FOLLOW ON LATER
									// System.out.println("down trend divergent signal: edgeTrendCount "+edgeTrendCount+" at "+endIndex);
									// System.out.println("price trend not validated "+p1+" to "+p3+", "+p2+" to "+p4);

									// System.out.println("maxMinTrendCount "+maxMinTrendCount);
									// System.out.println("price trend not validated "+p1+" to "+p3+", "+p2+" to "+p4);
								}
								// update records FFP
							}
						} else if (edgeTrendCount < 0 && endPrice > startPrice) {
							// System.out.println("Reset values, Negative trend ends at "+endIndex);
							edgeTrendCount = 1;
							maxMinTrendCount = 0;
							// System.out.println("edgeTrendCount is now "+edgeTrendCount);
							// System.out.println("Positive trend starts at "+startIndex);
							p1 = minMaxPrice;
							p2 = 0;
							p3 = 0;
							p4 = 0;
						}

					} else {
						break;
					}
				}
				k++;

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void updateLatestDCPIPStep1(String nextStock, long maxPreIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			SQL = "select  TWA,DCPIP,SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "'  AND SEQINDEX>"
					+ (maxPreIndex - backCountNeeded / 30)
					+ " ORDER by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);

			int days = 0;
			float dcpip1 = 0.0f;
			int twa1 = 0;
			int seqIndex1 = 0;

			float dcpip2 = 0.0f;
			int twa2 = 0;
			int seqIndex2 = 0;

			while (rs1.next()) {
				if (days == 0) {
					twa1 = rs1.getInt(1);
					dcpip1 = rs1.getFloat(2);
					seqIndex1 = rs1.getInt(3);

				} else if (days == 1) {
					twa2 = rs1.getInt(1);
					dcpip2 = rs1.getFloat(2);
					seqIndex2 = rs1.getInt(3);

					if (twa2 == 0 && (dcpip2 < 0 || dcpip2 > 0)) {
						String sql1 = "Update BPMADMIN.INTRADAYSTOCKS SET DCPIP = 0  WHERE SYMBOL ='"
								+ nextStock + "' and SEQINDEX = " + seqIndex2;

						if (disLog)
							System.out.println(sql1);

						stmt2.executeUpdate(sql1);
					}
					break;

				}
				days++;

			}

			SQL = "select  TWA,FINALPRICE,SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "' AND (TWA>1 OR TWA<-1) AND SEQINDEX>"
					+ (maxPreIndex - backCountNeeded)
					+ " ORDER by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);
			seqIndex1 = 0;
			float price1 = 0;
			seqIndex2 = 0;
			float price2 = 0;
			days = 0;

			while (rs1.next()) {
				if (days == 0) {
					seqIndex1 = rs1.getInt(3);
					price1 = rs1.getFloat(2);
					if (seqIndex1 < maxPreIndex) {
						// only update the lastest
						break;
					}

				} else if (days == 1) {
					seqIndex2 = rs1.getInt(3);
					price2 = rs1.getFloat(2);
					float dcpip = 100.0f * (price1 - price2) / price2;

					String sql1 = "Update BPMADMIN.INTRADAYSTOCKS SET DCPIP = "
							+ dcpip + " WHERE SYMBOL ='" + nextStock
							+ "' and SEQINDEX = " + seqIndex;

					if (disLog)
						System.out.println(sql1);

					stmt2.executeUpdate(sql1);

					break;
				}
				days++;
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void updateLatestDCPIPStep2(String nextStock, long maxPreIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			// *** new logic to take care add data the in-between 2 section data
			// calculation, added 3/4/2018
			long tempIndex = 0;
			int tempDCPT = 0;
			if (maxPreIndex > 0) {
				SQL = "select SEQINDEX,DCPT from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ nextStock
						+ "' and ( DCPT>1000 OR DCPT<-1000) AND SEQINDEX<="
						+ maxPreIndex + " ORDER BY SEQINDEX DESC";
				rs1 = stmt1.executeQuery(SQL);

				int lc = 0;
				while (rs1.next()) {
					tempIndex = rs1.getLong(1);
					tempDCPT = rs1.getInt(2);

					// find last counting start ending, // we need X0804, not
					// X0004
					if (tempDCPT % 1000 == 804 || tempDCPT % 1000 == -804) {
						lc++;
						if (lc == 1) {
							break;
						}
					}
				}

				SQL = "select SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ nextStock
						+ "' and ( TWA>2 OR TWA<-2) AND SEQINDEX<="
						+ tempIndex + " ORDER BY SEQINDEX DESC";
				rs1 = stmt1.executeQuery(SQL);

				lc = 0;
				while (rs1.next()) {
					tempIndex = rs1.getLong(1);
					lc++;
					// find the beginning of last ending start
					if (lc == 4) {
						break;
					}
				}
			}
			// *** new logic to take care add data the in-between 2 section data
			// calculation , // calculation, added 3/4/2018

			// SKIP -1,1,2 CASES AS 1,-1 USED AS BOUNDRY INDICATOR, 2 IS JUST
			// TOO CLOSE
			SQL = "select  DCPT, DCPIP, TWA, SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "' and (TWA>2 OR TWA<-2)  and SEQINDEX>="
					+ tempIndex + " order by SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);
			int size = 4;
			int[] index = new int[size];
			float[] dcpip = new float[size];
			float[] dcpt = new float[size];
			int[] dcptDays = new int[size];
			int days = 0;
			int loop = 0;
			float sumDcpip = 0.0f;
			int contCount = 0;

			// average days delta percentage needs to be determined
			// by historical data... 0.41 IS THE AVG FOR ALL POSITIVE
			// HERE WE TREAT ALL DAYS POSITIVE, ALTERNATIVELY WE COULD USE 0.88F
			// float threshHoldIncrease = 0.44f; //DAILY GAP VALUE
			float threshHoldIncrease = 0.036f; // 5 MIN GAP VALUE, CONSIDER
												// USING 0.04F
			while (rs1.next()) {
				if (loop < size) {
					dcpt[loop] = rs1.getFloat(1);
					dcpip[loop] = rs1.getFloat(2);
					dcptDays[loop] = rs1.getInt(3);
					index[loop] = rs1.getInt(4);

				} else {
					days = 0;
					sumDcpip = 0.0f;
					for (int k = 0; k < size; k++) {
						if (dcptDays[k] > 0) {
							days = days + dcptDays[k];
						} else {
							days = days - dcptDays[k];
						}
						sumDcpip = sumDcpip + dcpip[k];
					}

					if (sumDcpip < (1.0f * days) * threshHoldIncrease) {
						sumDcpip = 0.0f;
						days = 0;
						contCount = 0;

					} else {
						contCount++;
						if (contCount >= 1) {
							int dcptScore = 20800 + contCount + 3;
							String sql2 = "Select DCPT from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
									+ nextStock
									+ "' and SEQINDEX = "
									+ index[3];

							if (disLog)
								System.out.println(sql2);

							rs3 = stmt3.executeQuery(sql2);

							int val = 0;
							if (rs3.next()) {
								val = rs3.getInt(1);
							}

							if (val == 0 && dcptScore > val) {
								String sql1 = "SELECT DCPT FROM BPMADMIN.INTRADAYSTOCKS  WHERE SYMBOL ='"
										+ nextStock
										+ "' and SEQINDEX = "
										+ index[3];

								rs2 = stmt2.executeQuery(sql1);
								if (index[3] > maxPreIndex
										|| (rs2.next() && rs2.getInt(1) == 0)
										|| (!rs2.next())) {

									sql1 = "Update BPMADMIN.INTRADAYSTOCKS SET DCPT = "
											+ dcptScore
											+ " WHERE SYMBOL ='"
											+ nextStock
											+ "' and SEQINDEX = "
											+ index[3];

									if (disLog)
										System.out.println(sql1);

									stmt2.executeUpdate(sql1);
								}
							}

						}
					}

					for (int k = 0; k < size - 1; k++) {
						index[k] = index[k + 1];
						dcpip[k] = dcpip[k + 1];
						dcptDays[k] = dcptDays[k + 1];
					}

					dcpt[size - 1] = rs1.getFloat(1);
					dcpip[size - 1] = rs1.getFloat(2);
					dcptDays[size - 1] = rs1.getInt(3);
					index[size - 1] = rs1.getInt(4);

				}

				loop++;

			}

			days = 0;
			sumDcpip = 0.0f;
			for (int k = 0; k < size; k++) {
				if (dcptDays[k] > 0) {
					days = days + dcptDays[k];
				} else {
					days = days - dcptDays[k];
				}
				sumDcpip = sumDcpip + dcpip[k];
			}

			if (sumDcpip < (1.0f * days) * threshHoldIncrease) {
				sumDcpip = 0.0f;
				days = 0;
				contCount = 0;

			} else {
				contCount++;
				if (contCount >= 1) {
					int dcptScore = 20800 + contCount + 3;
					String sql2 = "Select DCPT from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
							+ nextStock + "' and SEQINDEX = " + index[3];

					if (disLog)
						System.out.println(sql2);

					rs3 = stmt3.executeQuery(sql2);

					int val = 0;
					if (rs3.next()) {
						val = rs3.getInt(1);
					}

					if (val == 0 && dcptScore > val) {
						String sql1 = "SELECT DCPT FROM BPMADMIN.INTRADAYSTOCKS  WHERE SYMBOL ='"
								+ nextStock + "' and SEQINDEX = " + index[3];

						rs2 = stmt2.executeQuery(sql1);
						if (index[3] > maxPreIndex
								|| (rs2.next() && rs2.getInt(1) == 0)
								|| (!rs2.next())) {

							sql1 = "Update BPMADMIN.INTRADAYSTOCKS SET DCPT = "
									+ dcptScore + " WHERE SYMBOL ='"
									+ nextStock + "' and SEQINDEX = "
									+ index[3];

							if (disLog)
								System.out.println(sql1);

							stmt2.executeUpdate(sql1);
						}
					}

				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}
	}

	public static void updateLatestTWATrendHistory(String nextStock,
			int seqIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			SQL = "select  TWA,FINALPRICE,SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "' AND (TWA>1 OR TWA<-1) AND SEQINDEX>"
					+ (seqIndex - 400) + " ORDER by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);
			int loopCount = 0;
			int score = 0;
			float previousPrice = 0;
			int nextIndex = 0;
			boolean ffp2Adjust = false;

			while (rs1.next()) {

				float twa = rs1.getFloat(1);
				float price = rs1.getFloat(2);
				int cIndex = rs1.getInt(3);
				if (loopCount == 0) {
					previousPrice = price;
					if (cIndex != seqIndex) {
						break;
					}
				} else {
					if (price <= previousPrice && score >= 0) {
						score++;
					} else if (price < previousPrice && score < 0) {
						break;
					}

					if (price >= previousPrice && score <= 0) {
						score--;
					} else if (price > previousPrice && score > 0) {
						break;
					}
					previousPrice = price;
				}
				loopCount++;
			}
			// System.out.println("Score "+score+" at "+cIndex);

			if (score >= 4 || score <= -4) {
				/*
				 * String sql1 =
				 * "select FFP,SEQINDEX from  BPMADMIN.INTRADAYSTOCKS  WHERE SYMBOL ='"
				 * + nextStock + "' and SEQINDEX<= " + seqIndex +
				 * " and SEQINDEX>=" + (seqIndex - 10) +
				 * " ORDER BY SEQINDEX DESC";
				 * 
				 * rs2 = stmt2.executeQuery(sql1); int ffp1 = 0; int ffp2 = 0;
				 * int loop = 0; while (rs2.next()) { if (loop == 0) { ffp1 =
				 * rs2.getInt(1); System.out.println("Current ffp " + ffp1); }
				 * else if (loop == 1) { ffp2 = rs2.getInt(1); nextIndex =
				 * rs2.getInt(2);
				 * 
				 * } else { break; } loop++; }
				 * 
				 * if (score >= 4) { if (ffp1 > 10000) { ffp1 = score * 1000 +
				 * ffp1; while (ffp1 < 30000) { ffp1 = 10000 + ffp1; } } else {
				 * ffp1 = 20000 + score * 1000 + ffp1; if (ffp2 > 20000 && ffp2
				 * < 30000) { ffp2 = ffp2 - ffp1; ffp2Adjust = true; } } } else
				 * if (score <= -4) { if (ffp1 < -10000) { ffp1 = score * 1000 +
				 * ffp1; while (ffp1 > -30000) { ffp1 = -10000 + ffp1; } } else
				 * { ffp1 = -20000 + score * 1000 + ffp1; if (ffp2 < -20000 &&
				 * ffp2 > -30000) { ffp2 = ffp2 - ffp1; ffp2Adjust = true; } }
				 * 
				 * }
				 * 
				 * while (ffp1 > 32760) { ffp1 = ffp1 - 1000; }
				 * 
				 * while (ffp1 < -32760) { ffp1 = ffp1 + 1000; }
				 */
				int dcptScore = 20000 + score;
				if (score < 0)
					dcptScore = -20000 + score;

				String sql1 = "Update BPMADMIN.INTRADAYSTOCKS SET DCPT = "
						+ dcptScore + " WHERE SYMBOL ='" + nextStock
						+ "' and SEQINDEX = " + seqIndex;

				if (disLog)
					System.out.println(sql1);

				stmt2.executeUpdate(sql1);

				/*
				 * if (ffp2Adjust) { sql1 =
				 * "Update BPMADMIN.INTRADAYSTOCKS SET FFP = " + ffp2 +
				 * " WHERE SYMBOL ='" + nextStock + "' and SEQINDEX = " +
				 * nextIndex; stmt2.executeUpdate(sql1);
				 * System.out.println(sql1); }
				 */
			}

			/*
			 * if (!ffp2Adjust) { String sql3 =
			 * "select FFP,TWA from  BPMADMIN.INTRADAYSTOCKS  WHERE SYMBOL ='" +
			 * nextStock + "' and SEQINDEX = " + nextIndex +
			 * " AND (FFP>20000 OR FFP<-20000) AND (FFP<30000 OR FFP>-30000)";
			 * rs1 = stmt1.executeQuery(sql3); if (rs1.next()) { int cFFP =
			 * rs1.getInt(1); float cTWA = rs1.getFloat(2); if (cTWA < 0.00001
			 * && cTWA > -0.00001) { cFFP = cFFP % 1000; String sql4 =
			 * "Update BPMADMIN.INTRADAYSTOCKS SET FFP = " + cFFP +
			 * " WHERE SYMBOL ='" + nextStock + "' and SEQINDEX = " + nextIndex;
			 * stmt2.executeUpdate(sql4);
			 * System.out.println("EXTRA UPDATES>>>>");
			 * System.out.println(sql4);
			 * 
			 * }
			 * 
			 * } }
			 */

			updateLatestDCPIPStep1(nextStock, seqIndex);
			// NEED TO REMOVE IT UNTIL PARAMETER MEASURED
			updateLatestDCPIPStep2(nextStock, (long) seqIndex);
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void AAA_resetFFPHistoryToOriginal(String nextStock) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			SQL = "select  FFP,SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "' AND (FFP>=1000 OR FFP<=-1000) ORDER by SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);
			int loopCount = 0;
			int score = 0;

			while (rs1.next()) {

				score = rs1.getInt(1);
				int cIndex = rs1.getInt(2);

				score = score % 1000;
				String sql1 = "Update BPMADMIN.INTRADAYSTOCKS SET FFP = "
						+ score + " WHERE SYMBOL ='" + nextStock
						+ "' and SEQINDEX = " + cIndex;
				System.out.println(sql1);

				stmt2.executeUpdate(sql1);

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void populateGSEQTable() {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			int gseq = 1512743400;
			Calendar cal1 = Calendar.getInstance();
			cal1.set(Calendar.YEAR, 2017);
			cal1.set(Calendar.MONTH, 11);
			cal1.set(Calendar.DAY_OF_MONTH, 8);
			PreparedStatement p1 = con
					.prepareStatement("insert into BPMADMIN.GSEQ values(?,?,?)");
			for (int k = 0; k < 10000; k++) {
				gseq = gseq + 86400;
				cal1.add(Calendar.HOUR, 24);
				if (cal1.get(Calendar.YEAR) <= 2037) {
					String y = "" + cal1.get(Calendar.YEAR);
					String m = "" + (cal1.get(Calendar.MONTH) + 1);
					String d = "" + cal1.get(Calendar.DATE);
					System.out.println((y + "-" + m + "-" + d));
					java.sql.Date d1 = java.sql.Date.valueOf(y + "-" + m + "-"
							+ d);
					// java.sql.Date d1 = java.sql.Date.valueOf("2017-12-08");
					java.sql.Time t1 = new java.sql.Time(9, 30, 0);

					p1.setInt(1, gseq);
					p1.setDate(2, d1);
					p1.setTime(3, t1);
					p1.execute();
				}

			}

			gseq = 1512743400;
			Calendar cal2 = Calendar.getInstance();
			cal2.set(Calendar.YEAR, 2017);
			cal2.set(Calendar.MONTH, 11);
			cal2.set(Calendar.DAY_OF_MONTH, 8);
			for (int k = 0; k > -17500; k--) {
				String y = "" + cal2.get(Calendar.YEAR);
				String m = "" + (cal2.get(Calendar.MONTH) + 1);
				String d = "" + cal2.get(Calendar.DATE);

				java.sql.Date d1 = java.sql.Date.valueOf(y + "-" + m + "-" + d);
				System.out.println((y + "-" + m + "-" + d));
				// java.sql.Date d1 = java.sql.Date.valueOf("2017-12-08");
				java.sql.Time t1 = new java.sql.Time(9, 30, 0);
				p1.setInt(1, gseq);
				p1.setDate(2, d1);
				p1.setTime(3, t1);
				p1.execute();
				gseq = gseq - 86400;

				cal2.add(Calendar.HOUR, -24);

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	private static int getTradingEndingHour(Calendar cdate) {
		int endingHour = 16;
		int year = cdate.get(Calendar.YEAR);
		int month = cdate.get(Calendar.MONTH) + 1;
		int date = cdate.get(Calendar.DAY_OF_MONTH);

		int weekDate = cdate.get(Calendar.DAY_OF_WEEK);

		if (weekDate < 2 || weekDate > 6) {
			endingHour = 0; // no trading on Sunday 1 and Saturday 7)
		} else if (year == 2017 && month == 1 && date == 2) {
			endingHour = 0; // no trading on Jan 2nd.
		} else if (year == 2017 && month == 1 && date == 16) {
			endingHour = 0; // no trading on Jan 16th. Martin Luther King
		} else if (year == 2017 && month == 2 && date == 20) {
			endingHour = 0; // no trading on Jan 20th. Washingon Birthday
		} else if (year == 2017 && month == 4 && date == 14) {
			endingHour = 0; // no trading on April 14th. Good Friday
		} else if (year == 2017 && month == 5 && date == 29) {
			endingHour = 0; // no trading on May 29th. Memorial Friday
		} else if (year == 2017 && month == 7 && date == 4) {
			endingHour = 0; // no trading on July 4th. Independence Day
		} else if (year == 2017 && month == 7 && date == 3) {
			endingHour = 13; // partial trading on July 3rd. date before
								// Independence Day
		} else if (year == 2017 && month == 9 && date == 4) {
			endingHour = 0; // no trading on Sept 4th. Labor Day
		} else if (year == 2017 && month == 11 && date == 23) {
			endingHour = 0; // no trading on Nov. 23rd. Thanksgiving Day
		} else if (year == 2017 && month == 11 && date == 24) {
			endingHour = 13; // partial trading on Nov. 24th. The day after
								// Thanksgiving Day
		} else if (year == 2017 && month == 12 && date == 25) {
			endingHour = 0; // no trading on Dec. 25th. Christmas Day
		} else if (year == 2018 && month == 1 && date == 1) {
			endingHour = 0; // no trading on Jan 1st.
		} else if (year == 2018 && month == 1 && date == 15) {
			endingHour = 0; // no trading on Jan 15th. Martin Luther King
		} else if (year == 2018 && month == 2 && date == 19) {
			endingHour = 0; // no trading on Jan 19th. Washingon Birthday
		} else if (year == 2018 && month == 3 && date == 30) {
			endingHour = 0; // no trading on March 30th. Good Friday
		} else if (year == 2018 && month == 5 && date == 28) {
			endingHour = 0; // no trading on May 28th. Memorial Friday
		} else if (year == 2018 && month == 7 && date == 4) {
			endingHour = 0; // no trading on July 4th. Independence Day
		} else if (year == 2018 && month == 7 && date == 3) {
			endingHour = 13; // partial trading on July 3rd. date before
								// Independence Day
		} else if (year == 2018 && month == 9 && date == 3) {
			endingHour = 0; // no trading on Sept 3rd. Labor Day
		} else if (year == 2018 && month == 11 && date == 22) {
			endingHour = 0; // no trading on Nov. 22nd. Thanksgiving Day
		} else if (year == 2018 && month == 11 && date == 23) {
			endingHour = 13; // partial trading on Nov. 23rd. The day after
								// Thanksgiving Day
		} else if (year == 2018 && month == 12 && date == 24) {
			endingHour = 13; // partial trading on Dec. 24th. The day before
								// Christmas Day
		} else if (year == 2018 && month == 12 && date == 25) {
			endingHour = 0; // no trading on Dec. 25th. Christmas Day
		} else if (year == 2019 && month == 1 && date == 1) {
			endingHour = 0; // no trading on Jan 1st.
		} else if (year == 2019 && month == 1 && date == 21) {
			endingHour = 0; // no trading on Jan 15th. Martin Luther King
		} else if (year == 2019 && month == 2 && date == 18) {
			endingHour = 0; // no trading on Jan 19th. Washingon Birthday
		} else if (year == 2019 && month == 4 && date == 19) {
			endingHour = 0; // no trading on March 30th. Good Friday
		} else if (year == 2019 && month == 5 && date == 27) {
			endingHour = 0; // no trading on May 28th. Memorial Friday
		} else if (year == 2019 && month == 7 && date == 4) {
			endingHour = 0; // no trading on July 4th. Independence Day
		} else if (year == 2019 && month == 7 && date == 3) {
			endingHour = 13; // partial trading on July 3rd. date before
								// Independence Day
		} else if (year == 2019 && month == 9 && date == 2) {
			endingHour = 0; // no trading on Sept 3rd. Labor Day
		} else if (year == 2019 && month == 11 && date == 28) {
			endingHour = 0; // no trading on Nov. 22nd. Thanksgiving Day
		} else if (year == 2019 && month == 11 && date == 29) {
			endingHour = 13; // partial trading on Nov. 23rd. The day after
								// Thanksgiving Day
		} else if (year == 2019 && month == 12 && date == 24) {
			endingHour = 13; // partial trading on Dec. 24th. The day before
								// Christmas Day
		} else if (year == 2019 && month == 12 && date == 25) {
			endingHour = 0; // no trading on Dec. 25th. Christmas Day
		}

		return endingHour;
	}

	public static void populateTradingTimeTable() {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			Calendar calEnd = Calendar.getInstance();
			calEnd.set(Calendar.YEAR, 2020);
			calEnd.set(Calendar.MONTH, 0);
			calEnd.set(Calendar.DAY_OF_MONTH, 1);

			Calendar cal1 = Calendar.getInstance();
			cal1.set(Calendar.YEAR, 2017);
			cal1.set(Calendar.MONTH, 0);
			cal1.set(Calendar.DAY_OF_MONTH, 1);
			PreparedStatement p1 = con
					.prepareStatement("insert into BPMADMIN.TRADINGTIME values(?,?,?,?)");
			for (int k = 1; k < 200000 && cal1.before(calEnd); k++) {
				int endHours = getTradingEndingHour(cal1);
				if (endHours > 0) {
					String y = "" + cal1.get(Calendar.YEAR);
					String m = "" + (cal1.get(Calendar.MONTH) + 1);
					String d = "" + cal1.get(Calendar.DATE);
					System.out.println((y + "-" + m + "-" + d));
					java.sql.Date d1 = java.sql.Date.valueOf(y + "-" + m + "-"
							+ d);
					// java.sql.Date d1 =
					// java.sql.Date.valueOf("2017-12-08");
					int startMinutes = 570; // 9:30 am
					for (int w = startMinutes; w <= endHours * 60; w = w + 5) {
						p1.setInt(1, k);
						p1.setDate(2, d1);
						p1.setInt(3, w / 60);
						p1.setInt(4, w % 60);
						p1.execute();
						k++;
					}
					k--;

				} else {
					k--;
				}
				cal1.add(Calendar.HOUR, 24);
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void AAA_resetALLLastTwoTWA(int seqIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "SELECT distinct(SYMBOL) FROM BPMADMIN.INTRADAYSTOCKS where seqIndex="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();

				AAA_resetLastTwoTWA(nextStock);
				System.out.println(nextStock + " AAA_resetLastTwoTWA done ");

				loopCount++;
				if (loopCount % 100 == 0) {
					long t2 = System.currentTimeMillis();
					System.out.println(nextStock + " Stock FFP Reset done "
							+ " cost time seconds " + (t2 - t1) / 1000);

					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(8000);
				} else if (loopCount % 1000 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(3000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void AAA_resetLastTwoTWA(String nextStock) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			SQL = "select  TWA,SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "'  AND (TWA<0 or TWA>0) ORDER by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);
			int seq1 = 0;
			int seq2 = 0;
			int twa1 = 0;
			int twa2 = 0;
			int twa = 0;
			int dcp1 = 0;
			int dcp2 = 0;
			int dcp0;
			int seq = 0;
			int loop = 0;

			if (rs1.next()) {
				twa = rs1.getInt(1);
				seq = rs1.getInt(2);

			}

			if (twa > 0 || twa < 0) {
				SQL = "select  DCP,SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
						+ nextStock
						+ "'  AND  SEQINDEX >="
						+ seq
						+ " ORDER BY SEQINDEX ASC";

				rs1 = stmt1.executeQuery(SQL);
				boolean update = true;

				while (rs1.next()) {
					if (loop == 0) {
						dcp1 = rs1.getInt(1);
						seq1 = rs1.getInt(2);
					} else if (loop == 1) {
						dcp2 = rs1.getInt(1);
						seq2 = rs1.getInt(2);
						if (twa > 0 && dcp1 >= dcp2 && twa1 <= 0) {
							twa1 = twa1 - 1;
						} else if (twa < 0 && dcp1 <= dcp2 && twa1 >= 0) {
							twa1 = twa1 + 1;
						} else {
							System.out.println("Crazy scenario for "
									+ nextStock);
							update = false;
							break;
						}

					} else {
						if (twa > 0 && dcp1 >= dcp2 && twa1 <= 0) {
							twa1 = twa1 - 1;
							dcp1 = dcp2;
							seq1 = seq2;
							dcp2 = rs1.getInt(1);
							seq2 = rs1.getInt(2);
						} else if (twa < 0 && dcp1 <= dcp2 && twa1 >= 0) {
							twa1 = twa1 + 1;
							dcp1 = dcp2;
							seq1 = seq2;
							dcp2 = rs1.getInt(1);
							seq2 = rs1.getInt(2);
						} else {
							System.out.println("Crazy scenario for "
									+ nextStock);
							update = false;
							break;
						}

					}
					loop++;

				}

				if (update && (twa1 > 0 || twa1 < 0)) {
					String sql1 = "Update BPMADMIN.INTRADAYSTOCKS SET TWA = "
							+ twa1 + " WHERE SYMBOL ='" + nextStock
							+ "' and SEQINDEX = " + seq2;
					System.out.println(sql1);

					stmt2.executeUpdate(sql1);
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void getTWATrendHistory(String nextStock, long maxPreIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018
			long tempIndex2 = 0;
			int score = 0;

			if (maxPreIndex > 0) {
				SQL = "select  seqindex,DCPT from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ nextStock
						+ "' and (DCPT>10000 OR DCPT<-10000)  AND SEQINDEX<"
						+ maxPreIndex + " ORDER BY SEQINDEX DESC";
				rs1 = stmt1.executeQuery(SQL);

				while (rs1.next()) { // we need X0004, not X0804
					tempIndex2 = rs1.getLong(1);
					score = rs1.getInt(2);
					if ((score % 1000 != 804 || score % 1000 != -804)
							&& (score % 10 == 4 || score % 10 == -4)) {
						score = score % 10;
						if (score > 0) {
							score = score - 4;
						} else {
							score = score + 4;
						}
						break;
					}

				}

				SQL = "select  seqindex from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ nextStock
						+ "' and (TWA>1 OR TWA<-1)  AND SEQINDEX<"
						+ tempIndex2 + " ORDER BY SEQINDEX DESC";
				rs1 = stmt1.executeQuery(SQL);

				int lc = 0;
				while (rs1.next()) {
					tempIndex2 = rs1.getLong(1);
					lc++;
					if (lc >= 4) { // only needs last 4 TWA change
						break;
					}

				}

			}
			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018

			// need to address TWA at the last date overwritten issue
			// if DCP keep up/down then need to reset to zero
			SQL = "select  TWA,FINALPRICE,SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "' AND (TWA>1 OR TWA<-1) AND SEQINDEX>="
					+ tempIndex2 + " ORDER by SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);
			int loopCount = 0;
			float previousPrice = 0;

			while (rs1.next()) {

				float twa = rs1.getFloat(1);
				float price = rs1.getFloat(2);
				int cIndex = rs1.getInt(3);
				if (loopCount == 0) {
					previousPrice = price;
				} else {
					if (price >= previousPrice && score >= 0) {
						score++;
					} else if (price > previousPrice && score < 0) {
						score = 1;
					} else if (price <= previousPrice && score <= 0) {
						score--;
					} else if (price < previousPrice && score > 0) {
						score = -1;
					}
					// System.out.println("Score "+score+" at "+cIndex);
					previousPrice = price;
					if (score >= 4 || score <= -4) {
						/*
						 * String sql1 =
						 * "select FFP from  BPMADMIN.INTRADAYSTOCKS  WHERE SYMBOL ='"
						 * + nextStock + "' and SEQINDEX = " + cIndex;
						 * 
						 * rs2 = stmt2.executeQuery(sql1); int ffp = 0; if
						 * (rs2.next()) { ffp = rs2.getInt(1);
						 * System.out.println("Current ffp " + ffp); }
						 * 
						 * if (score >= 4) { if (ffp > 10000) { ffp = score *
						 * 1000 + ffp; while (ffp < 30000) { ffp = 10000 + ffp;
						 * } } else { ffp = 20000 + score * 1000 + ffp; } } else
						 * if (score <= -4) { if (ffp < -10000) { ffp = score *
						 * 1000 + ffp; while (ffp > -30000) { ffp = -10000 +
						 * ffp; } } else { ffp = -20000 + score * 1000 + ffp; }
						 * 
						 * }
						 * 
						 * while (ffp > 32760) { ffp = ffp - 1000; }
						 * 
						 * while (ffp < -32760) { ffp = ffp + 1000; }
						 */
						int dcptScore = 20000 + score;
						if (score < 0)
							dcptScore = -20000 + score;
						String sql1 = "Update BPMADMIN.INTRADAYSTOCKS SET DCPT = "
								+ dcptScore
								+ " WHERE SYMBOL ='"
								+ nextStock
								+ "' and SEQINDEX = " + cIndex;
						System.out.println(sql1);

						if (cIndex > tempIndex2)
							stmt2.executeUpdate(sql1);

					}
				}

				loopCount++;

			}

			int dcptScore = 20000 + score;
			if (score < 0)
				dcptScore = -20000 + score;
			String sql1 = "Update BPMADMIN.INTRADAYSTOCKS SET DCPT = "
					+ dcptScore + " WHERE SYMBOL ='" + nextStock
					+ "' and SEQINDEX = " + cIndex;
			System.out.println(sql1);

			if (cIndex >= maxPreIndex)
				stmt2.executeUpdate(sql1);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void getDS3PerSum(String nextStock, long maxPreIndex,
			int sumDays, boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			SQL = "select  DS3PER,SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "' and SEQINDEX>"
					+ (maxPreIndex - sumDays)
					+ " order by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);
			int loopCount = 0;
			float[] ds3s = new float[sumDays];
			int[] indexes = new int[sumDays];

			while (rs1.next()) {
				if (loopCount < sumDays) {
					ds3s[loopCount] = rs1.getFloat(1);
					indexes[loopCount] = rs1.getInt(2);

				} else {

					float sumScore = 0.0f;
					int seqIndexNext = indexes[0];
					for (int i = 0; i < sumDays; i++) {
						sumScore = sumScore + ds3s[i];
					}

					String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET SUMDS3P = "
							+ sumScore
							+ " WHERE SYMBOL ='"
							+ nextStock
							+ "' and SEQINDEX = " + seqIndexNext;

					if (seqIndexNext > maxPreIndex) {
						stmt2.executeUpdate(sql2);
					}

					if (lastOnly) {
						break;
					}

					for (int i = 0; i < sumDays - 1; i++) {
						ds3s[i] = ds3s[i + 1];
						indexes[i] = indexes[i + 1];
					}

					ds3s[sumDays - 1] = rs1.getFloat(1);
					indexes[sumDays - 1] = rs1.getInt(2);
				}
				loopCount++;

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void getBBDIScore(String nextStock, long maxPreIndex,
			boolean lastOnly) {
		float DSI3BullI = 300;
		float DSI3BearI = -300;
		float SUMDS3PBullI = 300;
		float SUMDS3PBearI = -300;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			SQL = "select  DSI3,SUMDS3P, SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "' and SEQINDEX>"
					+ maxPreIndex
					+ " order by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);
			int score = 0;
			int nextIndex = 0;
			float cDSI3 = 0.0f;
			float cSUMDS3p = 0.0f;

			while (rs1.next()) {

				score = 0;
				nextIndex = rs1.getInt(3);
				cDSI3 = rs1.getFloat(1);
				cSUMDS3p = rs1.getFloat(2);

				// the upper value is calculated as 1.4X average value, the low
				// point 0.7X of average value
				/*
				 * Interday
				 * 
				 * 1. 5000 stocks have been done
				 * 
				 * totalDSISUM 0.0, totalDays 0 Average DSISUM NaN totalDSISUMP
				 * 1719596.8372625536, totalDaysP 11910932 Average Pos DSISUM
				 * 14.437130841335955 totalGainsN -120891.8332494343, totalDaysN
				 * 2131318 Average Neg DSISUM -5.67216310515063
				 * 
				 * Average Pos DSISUM 144.37 (DSI3) Average Neg DSISUM -5.672
				 * 
				 * 
				 * 2. Average SUMDS3P 36.213730437555096 Average Pos SUMDS3P
				 * 138.28076071501233 Average Neg SUMDS3P -95.3303205029011 Done
				 * 
				 * 5000 stocks have been done totalSUMDS3P 519121.87168815057,
				 * totalDays 14333906 Average SUMDS3P 3.6216358031659377
				 * totalSUMDS3P 1116157.1395620594, totalDaysP 8071365 Average
				 * Pos SUMDS3P 13.82860444995437 totalGainsN -597035.2678739103,
				 * totalDaysN 6262541 Average Neg SUMDS3P -9.53343487689598
				 * 
				 * 
				 * 3. 5000 stocks have been done totalDIPSCORE
				 * 223374.40382342308, totalDays 14094437 Average DIPSCORE
				 * 15.848409115129826 totalDIPSCOREP 223718.51383330778,
				 * totalDaysP 13939373 Average Pos DIPSCORE 16.049395753547003
				 * totalGainsN -344.1100098846946, totalDaysN 155064 Average Neg
				 * DIPSCORE -2.2191482864152517
				 * 
				 * Average SUMDS3P 1.584819521858026 Average Pos SUMDS3P
				 * 16.049185589017625 Average Neg SUMDS3P -2.2192782287886523
				 * 
				 * The following are intraday
				 * 
				 * 4. 4200 stocks have been done totalDIPSCORE
				 * 139876.97376521898, totalDays 15667472 Average DIPSCORE
				 * 8.927858544455606 totalDIPSCOREP 141005.8727958292,
				 * totalDaysP 15198614 Average Pos DIPSCORE 9.277548123521607
				 * totalGainsN -1128.8990306102205, totalDaysN 468858 Average
				 * Neg DIPSCORE -0.2407763183331031
				 * 
				 * Average SUMDS3P 8.946462834167007 Average Pos SUMDS3P
				 * 9.29176408663565 Average Neg SUMDS3P -0.23998906091730637
				 * Done...
				 * 
				 * 
				 * 5. DSI3
				 * 
				 * 3800 stocks have been done total DSI3 1251550.2304680804,
				 * totalDays 12450857 Average DSI3 100.51920365546566 total DSI3
				 * 1255314.548524844, totalDaysP 11568490 Average Pos DSI3
				 * 108.51152989930786 totalGainsN -3764.318053391995, totalDaysN
				 * 882367 Average Neg DSI3 -4.266159153041756
				 * 
				 * 
				 * MTUM Stock DIPSCORE History calculation done cost time
				 * seconds 0 Average DSI3 100.52898589784701 Average Pos DSI3
				 * 108.5633662152027 Average Neg DSI3 -4.272930792987301 Done...
				 * Done
				 * 
				 * 
				 * 6. SUMDS3P 3000 stocks have been done total SUMDS3P
				 * 1688.8410070033278, totalDays 8660932 Average SUMDS3P
				 * 0.19499529692685819 total POS SUMDS3P 38159.48627540015,
				 * totalDaysP 4367439 Average Pos SUMDS3P 8.73726828821196
				 * totalGainsN -36470.645268396824, totalDaysN 4293493 Average
				 * Neg SUMDS3P -8.494399610852241
				 * 
				 * 
				 * calculateEntireSUMDS3PHistory --> getDS3PerSum() sum of last
				 * 20 DS3PER
				 * 
				 * 
				 * Average SUMDS3P 0.2225471755993458 Average Pos SUMDS3P
				 * 8.738019830031629 Average Neg SUMDS3P -8.442955493680941
				 */

				// if ((cDSI3 + cSUMDS3p) > 150.0f) {
				if (cDSI3 > 150 && cSUMDS3p > 12) {
					score = 400;
				} else if (cDSI3 > 150 && cSUMDS3p > 6 && cSUMDS3p < 12) {
					score = 301;
				} else if (cSUMDS3p > 12 && cDSI3 > 75 && cDSI3 < 150) {
					score = 302;
				} else if ((cDSI3 + cSUMDS3p) > 100.0f && cSUMDS3p > 6) {
					score = 200;
				}
				// } else if ((cDSI3 + cSUMDS3p) < -150.0f) {
				if (cDSI3 < -6 && cSUMDS3p < -12) {
					score = -400;
				} else if (cDSI3 < -6 && cSUMDS3p < -9 && cSUMDS3p > -12) {
					score = -301;
				} else if (cSUMDS3p < -12 && cDSI3 < -4 && cDSI3 > -6) {
					score = -302;
				} else if ((cDSI3 + cSUMDS3p) < -10.0f && cDSI3 < -4) {
					score = -200;
				} else if ((cDSI3 + cSUMDS3p) < -10.0f) {
					score = -100;
				} else if ((cDSI3 + cSUMDS3p) < -1.0f) {
					score = -1;
				}

				String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET BBDI = "
						+ score + " WHERE SYMBOL ='" + nextStock
						+ "' and SEQINDEX = " + nextIndex;

				if (nextIndex > maxPreIndex)
					stmt2.executeUpdate(sql2);

				if (lastOnly) {
					break;
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void getDipscoreSum(String nextStock, long maxPreIndex,
			int sumDays, boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			SQL = "select  DIPSCORE,SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "' and SEQINDEX>="
					+ (maxPreIndex - sumDays * 2) + " order by SEQINDEX DESC";

			// (maxPreIndex-backCountNeeded/30)>sumDays

			rs1 = stmt1.executeQuery(SQL);
			int loopCount = 0;
			int[] dipscores = new int[sumDays];
			int[] indexes = new int[sumDays];

			while (rs1.next()) {
				if (loopCount < sumDays) {
					dipscores[loopCount] = rs1.getInt(1);
					indexes[loopCount] = rs1.getInt(2);

				} else {

					int sumScore = 0;
					int seqIndexNext = indexes[0];
					for (int i = 0; i < sumDays; i++) {
						sumScore = sumScore + dipscores[i];
					}

					String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET DCP = "
							+ sumScore + " WHERE SYMBOL ='" + nextStock
							+ "' and SEQINDEX = " + seqIndexNext;

					if (seqIndexNext >= maxPreIndex) {
						stmt2.executeUpdate(sql2);
					} else {
						break;
					}

					if (lastOnly) {
						break;
					}

					for (int i = 0; i < sumDays - 1; i++) {
						dipscores[i] = dipscores[i + 1];
						indexes[i] = indexes[i + 1];
					}

					dipscores[sumDays - 1] = rs1.getInt(1);
					indexes[sumDays - 1] = rs1.getInt(2);
				}
				loopCount++;

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	// // rewrite on 2/27/2018 to simplify logic and code to set TWA based DCP
	// peak and trough
	public static void getDipSumGradient(String nextStock, long maxPreIndex,
			boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018
			long tempIndex2 = 0;
			if (maxPreIndex > 0) {
				SQL = "select  seqindex from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ nextStock
						+ "' and TWA<>0  AND SEQINDEX<"
						+ maxPreIndex + " ORDER BY SEQINDEX DESC";
				rs1 = stmt1.executeQuery(SQL);

				int lc = 0;
				while (rs1.next()) {
					tempIndex2 = rs1.getLong(1);
					lc++;
					if (lc >= 1) { // only needs last 1 DS3PIPDAYS change
						break;
					}

				}

			}
			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018

			SQL = "select  DCP, SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "' and SEQINDEX>="
					+ tempIndex2
					+ "  order by SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);
			// (maxPreIndex-backCountNeeded/6)>days
			boolean nonZeroStart = false;
			int loopCount = 0;
			boolean needFillUp = false;
			int trendDays = 0;
			float dcp1 = 0;
			float dcp2 = 0;
			float dcp3 = 0;
			long seq1 = 0;
			long seq2 = 0;
			long seq3 = 0;
			int ndays = 0;
			boolean increase = false;
			boolean decrease = false;
			boolean neutral = false;
			boolean trendStart = false;

			while (rs1.next()) {
				long seqCurrent = rs1.getLong(2);
				if (dcp1 == 0) {
					dcp1 = rs1.getFloat(1);
					seq1 = rs1.getLong(2);
				} else if (dcp2 == 0) {
					dcp2 = rs1.getFloat(1);
					seq2 = rs1.getLong(2);
				} else if (dcp3 == 0) {
					dcp3 = rs1.getFloat(1);
					seq3 = rs1.getLong(2);
				} else {
					if (dcp1 > dcp2 && !trendStart) {
						trendStart = true;
						decrease = true;
						neutral = false;
						trendDays = trendDays - 1 - ndays;
						ndays = 0;
					} else if (dcp1 < dcp2 && !trendStart) {
						trendStart = true;
						increase = true;
						neutral = false;
						trendDays = trendDays + 1 + ndays;
						ndays = 0;
					} else if (dcp1 == dcp2 && !trendStart) {
						neutral = true;
						decrease = false;
						increase = false;
						ndays++;
					} else if (dcp1 >= dcp2 && trendStart && decrease) {
						neutral = false;
						decrease = true;
						increase = false;
						trendDays = trendDays - 1 - ndays;
						ndays = 0;
					} else if (dcp1 <= dcp2 && trendStart && increase) {
						neutral = false;
						increase = true;
						decrease = false;
						trendDays = trendDays + 1 + ndays;
						ndays = 0;
					} else if (dcp1 == dcp2 && trendStart) {
						if (increase) {
							trendDays++;
						} else if (decrease) {
							trendDays--;
						}
					} else if (trendStart && increase && dcp1 > dcp2) {
						String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET TWA = "
								+ trendDays
								+ " WHERE SYMBOL ='"
								+ nextStock
								+ "' and SEQINDEX = " + seq1;
						stmt2.executeUpdate(sql2);
						trendDays = -1;
						neutral = false;
						decrease = true;
						increase = false;
					} else if (trendStart && decrease && dcp1 < dcp2) {
						String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET TWA = "
								+ trendDays
								+ " WHERE SYMBOL ='"
								+ nextStock
								+ "' and SEQINDEX = " + seq1;
						stmt2.executeUpdate(sql2);
						trendDays = 1;
						neutral = false;
						decrease = false;
						increase = true;
					}

					// this takes care of boundry data add issue, so erase
					// temporay TWA &DCP
					// if the trend (DCP increase or descrease) continues
					if (seq3 == (maxPreIndex + 1) && dcp1 != 0 && dcp2 != 0
							&& dcp3 != 0) {
						if ((dcp1 >= dcp2 && dcp2 >= dcp3)
								|| (dcp1 <= dcp2 && dcp2 <= dcp3)) {
							String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET TWA = 0,DCPIP=0 WHERE SYMBOL ='"
									+ nextStock
									+ "' and SEQINDEX = "
									+ maxPreIndex;
							stmt2.executeUpdate(sql2);
						}
					}

					//
					dcp1 = dcp2;
					dcp2 = dcp3;
					dcp3 = rs1.getFloat(1);
					seq1 = seq2;
					seq2 = seq3;
					seq3 = rs1.getLong(2);
					;
				}

			}

			// add logic on 3/2/2018 to consider last update outside the loop

			if (dcp1 > dcp2 && !trendStart) {
				trendStart = true;
				decrease = true;
				neutral = false;
				trendDays = trendDays - 1 - ndays;
				ndays = 0;
			} else if (dcp1 < dcp2 && !trendStart) {
				trendStart = true;
				increase = true;
				neutral = false;
				trendDays = trendDays + 1 + ndays;
				ndays = 0;
			} else if (dcp1 == dcp2 && !trendStart) {
				neutral = true;
				decrease = false;
				increase = false;
				ndays++;
			} else if (dcp1 >= dcp2 && trendStart && decrease) {
				neutral = false;
				decrease = true;
				increase = false;
				trendDays = trendDays - 1 - ndays;
				ndays = 0;
			} else if (dcp1 <= dcp2 && trendStart && increase) {
				neutral = false;
				increase = true;
				decrease = false;
				trendDays = trendDays + 1 + ndays;
				ndays = 0;
			} else if (dcp1 == dcp2 && trendStart) {
				if (increase) {
					trendDays++;
				} else if (decrease) {
					trendDays--;
				}
			} else if (trendStart && increase && dcp1 > dcp2) {
				String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET TWA = "
						+ trendDays + " WHERE SYMBOL ='" + nextStock
						+ "' and SEQINDEX = " + seq1;
				stmt2.executeUpdate(sql2);
				trendDays = -1;
				neutral = false;
				decrease = true;
				increase = false;
			} else if (trendStart && decrease && dcp1 < dcp2) {
				String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET TWA = "
						+ trendDays + " WHERE SYMBOL ='" + nextStock
						+ "' and SEQINDEX = " + seq1;
				stmt2.executeUpdate(sql2);
				trendDays = 1;
				neutral = false;
				decrease = false;
				increase = true;
			}

			// add logic on 3/2/2018 to consider last update outside the loop

			// just in case some latest TWA not updated
			// getDipSumGradientLimited(nextStock, seqIndex);
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	// /

	public static void getDipSumGradientOLDNOTUSED(String nextStock,
			long maxPreIndex, boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018
			long tempIndex2 = 0;
			if (maxPreIndex > 0) {
				SQL = "select  seqindex from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ nextStock
						+ "' and TWA<>0  AND SEQINDEX<"
						+ maxPreIndex + " ORDER BY SEQINDEX DESC";
				rs1 = stmt1.executeQuery(SQL);

				int lc = 0;
				while (rs1.next()) {
					tempIndex2 = rs1.getLong(1);
					lc++;
					if (lc >= 1) { // only needs last 1 DS3PIPDAYS change
						break;
					}

				}

			}
			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018

			int days = 40;

			SQL = "select  DCP, SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "' and SEQINDEX>="
					+ (tempIndex2 - days)
					+ "  order by SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);
			// (maxPreIndex-backCountNeeded/6)>days
			boolean nonZeroStart = false;
			int loopCount = 0;
			float[] dcpscores = new float[days];
			long[] indexes = new long[days];
			boolean needFillUp = false;
			int trendDays = 0;
			float dcp1 = 0;
			float dcp2 = 0;
			float dcp3 = 0;
			long seq1 = 0;
			long seq2 = 0;
			long seq3 = 0;
			int ndays = 0;

			while (rs1.next()) {
				long seqCurrent = rs1.getLong(2);
				if (seqCurrent == maxPreIndex - 1) {
					dcp1 = rs1.getFloat(1);
				} else if (seqCurrent == maxPreIndex) {
					dcp2 = rs1.getFloat(1);
				} else if (seqCurrent == maxPreIndex + 1) {
					dcp3 = rs1.getFloat(1);
				}

				if (!nonZeroStart && rs1.getFloat(1) != 0)
					nonZeroStart = true;

				if (!needFillUp && nonZeroStart && loopCount < days) {
					dcpscores[loopCount] = rs1.getFloat(1);
					indexes[loopCount] = rs1.getLong(2);

				} else if (!needFillUp && nonZeroStart) {

					boolean increase = false;
					boolean decrease = false;
					boolean neutral = false;
					trendDays = 0;

					for (int i = 0; i < days - 1; i++) {
						if (i == 0) {
							if (dcpscores[0] > dcpscores[1]) {
								decrease = true;
								trendDays--;

							} else if (dcpscores[0] < dcpscores[1]) {
								increase = true;
								trendDays++;
							} else if (dcpscores[0] == dcpscores[1]) {
								neutral = true;
								ndays = 1;
							}
						}

						if (i > 0) {
							if (decrease && dcpscores[i] >= dcpscores[i + 1]) {
								trendDays--;
							} else if (neutral
									&& dcpscores[i] > dcpscores[i + 1]) {
								trendDays = trendDays - ndays - 1;
								neutral = false;
								decrease = true;
							} else if (neutral
									&& dcpscores[i] == dcpscores[i + 1]) {
								ndays++;
							} else if (decrease
									&& dcpscores[i] < dcpscores[i + 1]) {
								String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET TWA = "
										+ trendDays
										+ " WHERE SYMBOL ='"
										+ nextStock
										+ "' and SEQINDEX = "
										+ indexes[i];

								if (indexes[i] >= maxPreIndex)
									stmt2.executeUpdate(sql2);
								needFillUp = true;
								break;
							}

							if (increase && dcpscores[i] <= dcpscores[i + 1]) {
								trendDays++;
							} else if (neutral
									&& dcpscores[i] < dcpscores[i + 1]) {
								trendDays = trendDays + ndays + 1;
								neutral = false;
								increase = true;
							} else if (increase
									&& dcpscores[i] >= dcpscores[i + 1]) {
								String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET TWA = "
										+ trendDays
										+ " WHERE SYMBOL ='"
										+ nextStock
										+ "' and SEQINDEX = "
										+ indexes[i];

								if (indexes[i] >= maxPreIndex)
									stmt2.executeUpdate(sql2);
								needFillUp = true;
								break;
							}

						} // if>0

					} // for loop

				}// else close

				if (trendDays < 0)
					trendDays = -trendDays;

				if (needFillUp) {
					for (int i = 0; i < days - 1; i++) {
						dcpscores[i] = dcpscores[i + 1];
						indexes[i] = indexes[i + 1];
					}

					dcpscores[days - 1] = rs1.getFloat(1);
					indexes[days - 1] = rs1.getInt(2);
					trendDays--;

					if (trendDays == 0)
						needFillUp = false;
				}

				if (nonZeroStart)
					loopCount++;
			}

			boolean increase = false;
			boolean decrease = false;
			boolean justChanged = false;
			int trendDays2 = 0;
			if (needFillUp && trendDays > 0) {

				for (int i = trendDays + 1; i < days - 1; i++) {

					if (!increase && !decrease) {
						if (dcpscores[i] >= dcpscores[i + 1]) {
							decrease = true;
							trendDays2--;
						} else {
							increase = true;
							trendDays2++;
						}
					}

					if (increase && dcpscores[i] <= dcpscores[i + 1]) {
						trendDays2++;
					} else if (increase && dcpscores[i] >= dcpscores[i + 1]) {
						String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET TWA = "
								+ trendDays2
								+ " WHERE SYMBOL ='"
								+ nextStock
								+ "' and SEQINDEX = " + indexes[i];

						if (indexes[i] >= maxPreIndex)
							stmt2.executeUpdate(sql2);
						increase = false;
						decrease = true;
						justChanged = true;
						trendDays2 = -1;
					}

					if (!justChanged) {
						if (decrease && dcpscores[i] > dcpscores[i + 1]) {
							trendDays2--;
						} else if (decrease && dcpscores[i] <= dcpscores[i + 1]) {
							String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET TWA = "
									+ trendDays2
									+ " WHERE SYMBOL ='"
									+ nextStock
									+ "' and SEQINDEX = "
									+ indexes[i];

							if (indexes[i] >= maxPreIndex)
								stmt2.executeUpdate(sql2);
							increase = true;
							decrease = false;
							trendDays2 = 1;
						}
					} else {
						justChanged = false;
					}

				}

			}

			String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET TWA = "
					+ trendDays2 + " WHERE SYMBOL ='" + nextStock
					+ "' and SEQINDEX = " + indexes[days - 1];

			if (indexes[days - 1] >= maxPreIndex)
				stmt2.executeUpdate(sql2);

			// this takes care of boundry data add issue, so erase temporay TWA
			// &DCP
			// if the trend (DCP increase or descrease) continues
			if (dcp1 != 0 && dcp2 != 0 && dcp3 != 0) {
				if ((dcp1 >= dcp2 && dcp2 >= dcp3)
						|| (dcp1 <= dcp2 && dcp2 <= dcp3)) {
					sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET TWA = 0,DCPIP=0 WHERE SYMBOL ='"
							+ nextStock + "' and SEQINDEX = " + maxPreIndex;
					stmt2.executeUpdate(sql2);
				}
			}

			// just in case some latest TWA not updated
			// getDipSumGradientLimited(nextStock, seqIndex);
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void updateLatestDipSumGradient(String nextStock, int seqIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			SQL = "select DCP,TWA, SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "' and SEQINDEX<="
					+ seqIndex
					+ "  order by SEQINDEX DESC";

			if (seqIndex < 0)
				SQL = "select DCP,TWA, SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
						+ nextStock + "'  order by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);

			int loopCount = 0;
			float dcp1 = 0;
			float twa1 = 0;
			float dcp2 = 0;
			float twa2 = 0;
			int seq1 = 0;
			int seq2 = 0;
			while (rs1.next()) {
				if (loopCount == 0) {
					dcp1 = rs1.getFloat(1);
					twa1 = rs1.getFloat(2);
					seq1 = rs1.getInt(3);
				} else if (loopCount == 1) {
					dcp2 = rs1.getFloat(1);
					twa2 = rs1.getFloat(2);
					seq2 = rs1.getInt(3);

					if (dcp1 <= dcp2 && twa2 < 0) {
						String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET TWA = 0,DCPT=0 WHERE SYMBOL ='"
								+ nextStock + "' and SEQINDEX = " + seq2;
						stmt2.executeUpdate(sql2);
						sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET TWA = "
								+ (twa2 - 1) + " WHERE SYMBOL ='" + nextStock
								+ "' and SEQINDEX = " + seq1;
						stmt2.executeUpdate(sql2);
					} else if (dcp1 < dcp2 && twa2 > 0) {
						String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET TWA = -1 WHERE SYMBOL ='"
								+ nextStock + "' and SEQINDEX = " + seq1;
						stmt2.executeUpdate(sql2);

					} else if (dcp1 >= dcp2 && twa2 > 0) {
						String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET TWA = 0, DCPT=0 WHERE SYMBOL ='"
								+ nextStock + "' and SEQINDEX = " + seq2;
						stmt2.executeUpdate(sql2);
						sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET TWA = "
								+ (twa2 + 1) + " WHERE SYMBOL ='" + nextStock
								+ "' and SEQINDEX = " + seq1;
						stmt2.executeUpdate(sql2);
					} else if (dcp1 > dcp2 && twa2 < 0) {
						String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET TWA = 1 WHERE SYMBOL ='"
								+ nextStock + "' and SEQINDEX = " + seq1;
						stmt2.executeUpdate(sql2);

					}

				}

				loopCount++;
				if (loopCount > 1) {
					break;
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	// we only validate BBSCORE>=1000 case here, as DCPT value moves
	// as new day comes in thus the previous day BBSCORE may change
	// or disappear if its value>=1000
	public static void validateBBScore(String nextStock, int seqIndex) {
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
			int lnSeqIndex = 0;

			SQL = "select  SEQINDEX,BBSCORE from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "' and SEQINDEX<="
					+ seqIndex
					+ "  and  (BBSCORE<0 OR BBSCORE>0) and BBSCORE<1000 ORDER BY SEQINDEX DESC";
			rs1 = stmt1.executeQuery(SQL);

			int lastBBSCORE = 0;
			if (rs1.next()) {
				lnSeqIndex = rs1.getInt(1);
				lastBBSCORE = rs1.getInt(2);
			}

			/*
			 * // No longer needed as we mark the last -100 or -200 BBDI as the
			 * bull start
			 * 
			 * // modification made on 9/17/2017,9/19/2017(REMOVE
			 * lastBBSCORE<1000) // in case the calculation just // started //
			 * the time could be ahead of BBSCORE starting point(1,81,881 etc)
			 * // we could trace back to the last BBDI<=-100 end if (lnSeqIndex
			 * > 0 && lastBBSCORE > 0) { SQL =
			 * "select  SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='" +
			 * nextStock + "' and SEQINDEX<=" + lnSeqIndex + " and SEQINDEX>" +
			 * (lnSeqIndex - 700) + "  and  BBDI<=-100 ORDER BY SEQINDEX DESC";
			 * rs1 = stmt1.executeQuery(SQL); if (rs1.next()) { lnSeqIndex =
			 * rs1.getInt(1); } } // modification made on 9/17/2017 in case the
			 * calculation just // started // No longer needed as we mark the
			 * last -100 or -200 BBDI as the bull start
			 */

			if (lnSeqIndex > 0 && lastBBSCORE > 0) {
				/***
				 * BEFORE 9/17/2017 CHANGE **** SINCE WE ADD UTIS=3, DCPT, DS3PT
				 * CONDITION, FFP IS TOO STRINGENT CONDITION, DP4S IS GOOD
				 * ENOUGH SQL =
				 * "select BBSCORE,FFP,DS3PT,DCPT, SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
				 * + nextStock + "' and SEQINDEX<=" + seqIndex +
				 * " AND SEQINDEX>=" + lnSeqIndex +
				 * " and (BBSCORE>0 OR BBSCORE<0 OR FFP>0 OR DCPT>0 OR DCPT<0 OR DS3PT>0 OR DS3PT<0) ORDER BY SEQINDEX DESC"
				 * ;
				 */
				SQL = "select BBSCORE,FFP,DS3PT,DCPT, SEQINDEX,DPS4 from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
						+ nextStock
						+ "' and SEQINDEX<="
						+ seqIndex
						+ " AND SEQINDEX>="
						+ lnSeqIndex
						+ " and (DPS4>0 OR BBSCORE>0 OR BBSCORE<0 OR FFP>0 OR DCPT>0 OR DCPT<0 OR DS3PT>0 OR DS3PT<0) ORDER BY SEQINDEX DESC";

				rs1 = stmt1.executeQuery(SQL);

				int bbscore1 = 0;
				int ffp1 = 0;
				int dps4 = 0;
				int ds3pt1 = 0;
				int dcpt1 = 0;
				int cIndex = 0;
				int cBBScore = 0;
				int ffpCount = 0;
				int dcptCount = 0;
				int ds3ptCount = 0;
				boolean ffpFound = false;
				boolean dcptFound = false;
				boolean ds3ptFound = false;
				int loop = 0;
				int totalScore = 0;
				boolean updateNeed = true;

				while (rs1.next()) {
					if (loop == 0) {
						cBBScore = rs1.getInt(1);
						ffp1 = rs1.getInt(2);
						ds3pt1 = rs1.getInt(3);
						dcpt1 = rs1.getInt(4);
						cIndex = rs1.getInt(5);
						dps4 = rs1.getInt(6);
						if ((dps4 == 0 && ffp1 == 0 && ds3pt1 <= 0 && dcpt1 <= 0)
								&& !(lastBBSCORE > 0 && lastBBSCORE < 1000)) {
							updateNeed = true;
							// cBBScore = 0;
							System.out.println("Zero Break off at " + cIndex);
							break;
						}
					}

					bbscore1 = rs1.getInt(1);
					ffp1 = rs1.getInt(2);
					ds3pt1 = rs1.getInt(3);
					dcpt1 = rs1.getInt(4);
					cIndex = rs1.getInt(5);
					dps4 = rs1.getInt(6);

					// dps4>0 is a change added on 9/17/2017
					if (ffp1 > 0 || dps4 > 0) {
						ffpFound = true;
						ffpCount++;
						if (ds3ptFound && dcptFound) {
							totalScore = 3000;
						} else if (ds3ptFound || dcptFound) {
							totalScore = 2000;
						} else {
							totalScore = 1000;
						}

						if (cBBScore == totalScore) {
							updateNeed = false;
							System.out.println("Break off at " + cIndex);
							break;
						}

					} else if (ffp1 < 0) {
						ffpCount--;
					}

					if (ds3pt1 > 0) {
						ds3ptFound = true;
						ds3ptCount++;

						if (ffpFound && dcptFound) {
							totalScore = 3000;
						} else if (ffpFound || dcptFound) {
							totalScore = 2000;
						} else {
							totalScore = 1000;
						}

						if (cBBScore == totalScore) {
							updateNeed = false;
							System.out.println("Break off at " + cIndex);

							break;
						}

					} else if (ds3pt1 < 0) {
						ds3ptCount--;
					}

					if (dcpt1 > 0) {
						dcptFound = true;
						dcptCount++;

						if (ffpFound && ds3ptFound) {
							totalScore = 3000;
						} else if (ffpFound || ds3ptFound) {
							totalScore = 2000;
						} else {
							totalScore = 1000;
						}

						if (cBBScore == totalScore) {
							updateNeed = false;
							System.out.println("Break off at " + cIndex);

							break;
						}

					} else if (dcpt1 < 0) {
						dcptCount--;
					}

					loop++;
				}

				// no need to update BBSCORE if <=1000 or 0, 0 case is used for
				// updating
				// latest value
				System.out.println("totalScore " + totalScore
						+ " vs. cBBScore " + cBBScore);
				if (updateNeed && (cBBScore >= 1000 || cBBScore == 0)
						&& cBBScore != totalScore) {
					String sql = "Update BPMADMIN.INTRADAYSTOCKS SET BBSCORE="
							+ totalScore + " where symbol='" + nextStock
							+ "' AND  seqIndex=" + seqIndex;
					System.out.println(sql);
					stmt3.executeUpdate(sql);
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void updateDistroCode(String symbol, long seqIndex, float code) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set PREDICTOPEN ="+code+" where  symbol='"
					+ symbol + "' and SEQINDEX="+seqIndex;
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void updateEntireLatestBBScore(int seqIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			SQL = "select SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL='SPY' AND SEQINDEX <"
					+ seqIndex
					+ " and SEQINDEX>"
					+ (seqIndex - 10)
					+ " ORDER BY SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);
			int nextIndex = 0;
			if (rs1.next()) {
				nextIndex = rs1.getInt(1);
			}

			// ADDED ON 9/19/2017 TO UPDATE THE PREVIOUS BBSCORE =0
			// IF NO CONDITION MET FOR BBSCORE>0
			SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET BBSCORE=0 WHERE SEQINDEX ="
					+ nextIndex
					+ " and DPS4 = 0 AND FFP = 0 AND DS3PT = 0 AND DCPT = 0 AND BBSCORE>=1000";
			stmt1.executeUpdate(SQL);
			// SET BBSCORE = 0 IF NO CONDITION MET

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				int sumDays = 7;
				updateLatestBBScore(nextStock, seqIndex);
				validateBBScore(nextStock, nextIndex);
				long t2 = System.currentTimeMillis();
				loopCount++;

				if (loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(5000);
				} else if (loopCount % 1000 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(10000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void updateLatestBBScore(String nextStock, int seqIndex) {
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

			SQL = "select BBDI, SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "' and (BBDI>=200 OR BBDI<=-100) and SEQINDEX<="
					+ seqIndex
					+ " and seqIndex>"
					+ (seqIndex - 700)
					+ " ORDER BY SEQINDEX DESC";
			rs1 = stmt1.executeQuery(SQL);

			int loop = 0;
			boolean bullCase = false;
			while (rs1.next()) {
				if (loop == 0) {
					cBBDI = rs1.getInt(1);
					cIndex = rs1.getInt(2);
					if (cIndex < seqIndex) {
						// not possible a begin/end of bull or bear peroid
						System.out.println("No case for " + nextStock);
						break;
					}
				} else {
					nSeq = rs1.getInt(2);
					nBBDI = rs1.getInt(1);
					if (cBBDI >= 200 && nBBDI >= 200) {
						// couldn't be the beginning of bull as precursor did it
						System.out.println("Bull case " + nextStock);
						bullCase = true;
						break;
					} else if (cBBDI <= -100 && nBBDI <= -100) {
						System.out.println("Con Bear case " + nextStock);

						// couldn't be the beginning of bear as precursor did it
						break;
					} else if (cBBDI <= -100 && nBBDI >= 200) {
						// begining of bear
						SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET BBSCORE=-1 WHERE SYMBOL ='"
								+ nextStock + "' AND SEQINDEX= " + seqIndex;
						stmt2.executeUpdate(SQL);
						System.out.println(SQL);
						break;
					} else if (cBBDI >= 200 && nBBDI <= -100) {
						// begining of bull
						SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET BBSCORE=1 WHERE SYMBOL ='"
								+ nextStock + "' AND SEQINDEX= " + nSeq;
						stmt2.executeUpdate(SQL);
						System.out.println(SQL);
						// beginning of bull, need check 881 or 81 case,
						// although
						// this value has not been fully utilized for reference
						// System.out.println("Find V Bend " + nextStock);
						// findBBScoreVBend(nextStock, seqIndex);
						bullCase = true;
						break;
					}

				}

				loop++;
			}

			if (bullCase) {
				validateBBScore(nextStock, seqIndex);
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void getDipSumGradientLimited(String nextStock, int seqIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (stmt2 == null)
				stmt2 = con.createStatement();

			SQL = "select  SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "' and SEQINDEX<="
					+ seqIndex
					+ "  and (TWA>0.1 OR TWA<-0.1) order by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);
			int sIndex = 0;

			if (rs1.next())
				sIndex = rs1.getInt(1);

			if (sIndex == 0)
				return;

			SQL = "select  DCP,TWA,SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ nextStock
					+ "' and SEQINDEX>="
					+ sIndex
					+ " Order by SEQINDEX ASC";
			rs1 = stmt1.executeQuery(SQL);

			int trendDays2 = 0;
			int loopCount = 0;
			float dcp1 = 0;
			float twa1 = 0;
			float dcp2 = 0;
			float twa2 = 0;
			int seq1 = 0;
			int seq2 = 0;
			while (rs1.next()) {
				if (loopCount == 0) {
					dcp1 = rs1.getFloat(1);
					twa1 = rs1.getFloat(2);
					seq1 = rs1.getInt(3);
				} else {
					dcp2 = rs1.getFloat(1);
					twa2 = rs1.getFloat(2);
					seq2 = rs1.getInt(3);

					if (twa1 < 0 && dcp1 > dcp2 && loopCount == 1) {
						System.out.println("Impossible for " + nextStock
								+ " at " + rs1.getInt(3));
						return;
					}

					if (twa1 > 0 && dcp1 < dcp2 && loopCount == 1) {
						System.out.println("Impossible for " + nextStock
								+ " at " + rs1.getInt(3));
						return;
					}

					if (dcp1 > dcp2 && trendDays2 <= 0) {
						trendDays2--;
						dcp1 = dcp2;
						seq1 = seq2;
						dcp2 = rs1.getFloat(1);
						seq2 = rs1.getInt(3);
					} else if (dcp1 > dcp2 && trendDays2 > 0) {
						String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET TWA = "
								+ trendDays2
								+ " WHERE SYMBOL ='"
								+ nextStock
								+ "' and SEQINDEX = " + seq1;
						stmt2.executeUpdate(sql2);
						trendDays2 = -1;
						dcp1 = dcp2;
						seq1 = seq2;
						dcp2 = rs1.getFloat(1);
						seq2 = rs1.getInt(3);
					}

					if (dcp1 < dcp2 && trendDays2 >= 0) {
						trendDays2++;
						dcp1 = dcp2;
						seq1 = seq2;
						dcp2 = rs1.getFloat(1);
						seq2 = rs1.getInt(3);
					} else if (dcp1 < dcp2 && trendDays2 < 0) {
						String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET TWA = "
								+ trendDays2
								+ " WHERE SYMBOL ='"
								+ nextStock
								+ "' and SEQINDEX = " + seq1;
						stmt2.executeUpdate(sql2);
						trendDays2 = 1;
						dcp1 = dcp2;
						seq1 = seq2;
						dcp2 = rs1.getFloat(1);
						seq2 = rs1.getInt(3);
					}
				}
				loopCount++;

			}

			if (trendDays2 != 0) {
				String sql2 = "Update BPMADMIN.INTRADAYSTOCKS SET TWA = "
						+ trendDays2 + " WHERE SYMBOL ='" + nextStock
						+ "' and SEQINDEX = " + seqIndex;
				stmt2.executeUpdate(sql2);
			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateDipSum7GradientLimitedHistory(int seqIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex + " and TWA>-0.0001 AND TWA<0.0001";

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				System.out.println(nextStock);
				getDipSumGradientLimited(nextStock, seqIndex);
				long t2 = System.currentTimeMillis();
				System.out.println(nextStock + " DipSumGradientLimited done "
						+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;
				// if (!lastOnly && loopCount % 5 == 0) {
				// resetConnection();
				// Thread.sleep(1000);
				if (loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(5000);
				} else if (loopCount % 1000 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(10000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateDipSum7GradientHistory(int seqIndex,
			boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				System.out.println(nextStock);
				getDipSumGradient(nextStock, seqIndex, lastOnly);
				long t2 = System.currentTimeMillis();
				System.out.println(nextStock
						+ " Stock Dip Score calculation done "
						+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;
				// if (!lastOnly && loopCount % 5 == 0) {
				// resetConnection();
				// Thread.sleep(1000);
				if (!lastOnly && loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(5000);
				} else if (loopCount % 1000 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(10000);
				}
			}

			calculateDipSum7GradientLimitedHistory(seqIndex);
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void updateEntireLatestDipscoreSumGradient(int seqIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				int sumDays = 7;
				updateLatestDipSumGradient(nextStock, seqIndex);
				long t2 = System.currentTimeMillis();
				loopCount++;

				if (loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(5000);
				} else if (loopCount % 1000 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(10000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateEntireBBScoreHistory(int seqIndex,
			boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			/*
			 * results = new Hashtable(); results.put("BZUN", "BZUN");
			 * results.put("PBYI", "PBYI"); results.put("AAOI", "AAOI");
			 * results.put("AKAO", "AKAO"); results.put("ACH", "ACH");
			 * results.put("CUTR", "CUTR"); results.put("KRA", "KRA");
			 * results.put("AEIS", "AEIS"); results.put("ANET", "ANET");
			 */
			if (disLog)
				System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				// int sumDays = 7;
				calculateBBScore(nextStock, seqIndex, lastOnly);
				long t2 = System.currentTimeMillis();

				if (disLog)
					System.out.println(nextStock
							+ " Stock BBScore calculation done "
							+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;

				if (!lastOnly && loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(5000);
				} else if (loopCount % 1000 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(10000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateEntireBBDIHistory(int seqIndex, boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				// int sumDays = 7;
				getBBDIScore(nextStock, seqIndex, lastOnly);
				long t2 = System.currentTimeMillis();
				if (disLog)
					System.out.println(nextStock
							+ " Stock BBDI Score calculation done "
							+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;

				if (!lastOnly && loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(5000);
				} else if (loopCount % 1000 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(10000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateEntireDipscoreSum7History(int seqIndex,
			boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				int sumDays = 7;
				getDipscoreSum(nextStock, seqIndex, sumDays, lastOnly);
				long t2 = System.currentTimeMillis();
				System.out.println(nextStock
						+ " Stock Dip Score calculation done "
						+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;

				if (!lastOnly && loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(5000);
				} else if (loopCount % 1000 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(10000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateEntireSUMDS3PHistory(int seqIndex,
			boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				int sumDays = 20;
				if (lastOnly) {
					getDS3PerSum(nextStock, seqIndex, 20, true);
				} else {
					getDS3PerSum(nextStock, -1, 20, false);
				}
				long t2 = System.currentTimeMillis();
				System.out.println(nextStock
						+ " Stock DS3PerSum calculation done "
						+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;

				if (!lastOnly && loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(5000);
				} else if (loopCount % 1000 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(10000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void AAA_resetEntireFFPHistory(int seqIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "SELECT distinct(SYMBOL) FROM BPMADMIN.INTRADAYSTOCKS where FFP>10000 or FFP<-10000";

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();

				getDS3PerTrendHistory(nextStock, 0l);
				getTWATrendHistory(nextStock, 0l);
				AAA_resetFFPHistoryToOriginal(nextStock);

				loopCount++;
				if (loopCount % 100 == 0) {
					long t2 = System.currentTimeMillis();
					System.out.println(nextStock + " Stock FFP Reset done "
							+ " cost time seconds " + (t2 - t1) / 1000);

					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(8000);
				} else if (loopCount % 1000 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(3000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	// getTWATrendHistory("CMG");
	public static void calculateEntireTWATrendHistory(int seqIndex,
			boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				if (lastOnly) {
					updateLatestTWATrendHistory(nextStock, seqIndex);
				} else {
					getTWATrendHistory(nextStock, 0l);
				}
				loopCount++;
				if (!lastOnly && loopCount % 100 == 0) {
					long t2 = System.currentTimeMillis();
					System.out.println(nextStock
							+ " Stock DS3PerTrend calculation done "
							+ " cost time seconds " + (t2 - t1) / 1000);

					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(8000);
				} else if (loopCount % 1000 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(3000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateEntireDS3PerTrendHistory(int seqIndex,
			boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();

				if (lastOnly) {
					updateLatestDS3PerTrend(nextStock, seqIndex);
				} else {
					getDS3PerTrendHistory(nextStock, 0l);
				}

				loopCount++;
				if (!lastOnly && loopCount % 100 == 0) {
					long t2 = System.currentTimeMillis();
					System.out.println(nextStock
							+ " Stock DS3PerTrend calculation done "
							+ " cost time seconds " + (t2 - t1) / 1000);

					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(8000);
				} else if (loopCount % 1000 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(3000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateEntireDCPTrendByPercentageHistory(int seqIndex,
			boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			/*
			 * results = new Hashtable(); results.put("BZUN", "BZUN");
			 * results.put("PBYI", "PBYI"); results.put("AAOI", "AAOI");
			 * results.put("AKAO", "AKAO"); results.put("ACH", "ACH");
			 * results.put("CUTR", "CUTR"); results.put("KRA", "KRA");
			 * results.put("AEIS", "AEIS"); results.put("ANET", "ANET");
			 */
			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();

				if (lastOnly) {
					updateLatestDS3PerTrend(nextStock, seqIndex);
				} else {
					getDCPTrendHistoryByPercentageStep1(nextStock,
							(long) seqIndex);
					updateLatestDCPIPStep2(nextStock, 0l);

				}

				loopCount++;
				if (!lastOnly && loopCount % 100 == 0) {
					long t2 = System.currentTimeMillis();
					System.out.println(nextStock
							+ " Stock DS3PerTrend calculation done "
							+ " cost time seconds " + (t2 - t1) / 1000);

					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(8000);
				} else if (loopCount % 1000 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(3000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateEntireDS3PerTrendByPercentageHistory(
			int seqIndex, boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			/*
			 * results = new Hashtable(); results.put("BZUN", "BZUN");
			 * results.put("PBYI", "PBYI"); results.put("AAOI", "AAOI");
			 * results.put("AKAO", "AKAO"); results.put("ACH", "ACH");
			 * results.put("CUTR", "CUTR"); results.put("KRA", "KRA");
			 * results.put("AEIS", "AEIS"); results.put("ANET", "ANET");
			 */
			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();

				if (lastOnly) {
					getDS3PerTrendHistoryByPercentageStep1(nextStock, seqIndex,
							true);
					getDS3PerTrendHistoryByPercentageStep2(nextStock, seqIndex,
							true);
					getDS3PerTrendHistoryByPercentageStep3(nextStock, seqIndex,
							true);

				} else {
					getDS3PerTrendHistoryByPercentageStep1(nextStock, -1, false);
					getDS3PerTrendHistoryByPercentageStep2(nextStock, -1, false);
					getDS3PerTrendHistoryByPercentageStep3(nextStock, -1, false);
				}

				loopCount++;
				if (!lastOnly && loopCount % 50 == 0) {
					long t2 = System.currentTimeMillis();
					System.out.println(nextStock
							+ " Stock DS3PerTrend calculation done "
							+ " cost time seconds " + (t2 - t1) / 1000);

					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(8000);
				} else if (loopCount % 1000 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(3000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateEntireDipScoreHistory(int seqIndex,
			boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;
			// use DCINCR>0 as mark for interruptable calculation
			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex + " and DCINCR=0";

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				scoreDipOp(nextStock, seqIndex, lastOnly);
				long t2 = System.currentTimeMillis();
				System.out.println(nextStock
						+ " Stock Dip Score calculation done "
						+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;
				if (!lastOnly && loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(8000);
				} else if (loopCount % 1000 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(3000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateEntireDPS4History(int seqIndex, boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex + " AND DCINCR=0 ";

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				getDPS4History(nextStock, seqIndex, lastOnly);
				long t2 = System.currentTimeMillis();
				System.out.println(nextStock + " Stock DPS4 calculation done "
						+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;
				if (!lastOnly && loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(8000);
				} else if (lastOnly && loopCount % 1000 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(3000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}
	
	
	public static void updateDistroCode(String symbol, long start, long end, float code) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set PREDICTOPEN =" + code
					+ " where  symbol='" + symbol + "' and SEQINDEX>="
					+ start+" AND SEQINDEX<="+end;
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void setDistroCode(String symbol, long index, float code) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set PREDICTOPEN =" + code
					+ " where  symbol='" + symbol + "' and SEQINDEX="
					+ index;
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}
	public static Hashtable getDistroList(long start, long end, float code) {

		Hashtable stocks = new Hashtable();
		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			String SQL3 = "SELECT DISTINCT(SYMBOL) FROM  BPMADMIN.INTRADAYSTOCKS  WHERE PREDICTOPEN >"
					+ (code - 0.1)
					+ " AND PREDICTOPEN<"
					+ (code + 0.1)
					+ " and SEQINDEX>=" + start + " AND SEQINDEX<="+end;
			System.out.println(SQL3);
			rs1 = stmt1.executeQuery(SQL3);

			while (rs1.next()) {
				String nstk = rs1.getString(1);
				stocks.put(nstk, nstk);

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return stocks;
	}

	public static void resetDistroList(long start, long end) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			String SQL3 = "SELECT DISTINCT(SYMBOL) FROM  BPMADMIN.INTRADAYSTOCKS  WHERE  SEQINDEX>="
					+ start + " AND SEQINDEX<="+end;
			System.out.println(SQL3);
			rs1 = stmt1.executeQuery(SQL3);
			int count = 0;
			while (rs1.next()) {
				String nstk = rs1.getString(1);
				count++;
				float code = count % 3 + 1.0f;
				SQL3 = "UPDATE BPMADMIN.INTRADAYSTOCKS SET  PREDICTOPEN = "
						+ code + " WHERE SYMBOL='" + nstk + "' AND SEQINDEX>="
						+ start + " AND SEQINDEX<="+end;
				stmt2.executeUpdate(SQL3);
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void getDPS4History(String symbol, long maxPreIndex,
			boolean lastOnly) {
		/*
		 * 6. Interday 5000 stocks have been done totalGains 2229884.0200099866,
		 * totalDays 14078133 Average DIPSCORE 15.839344748412211 totalGainsP
		 * 2233326.930011303, totalDaysP 13923011 Average Pos DIPSCORE
		 * 16.040545611946317 totalGainsN -3442.9100000062117, totalDaysN 155122
		 * Average Neg DIPSCORE -2.219485308341958
		 * 
		 * Average DIPSCORE 15.83709979360277 Average Pos DIPSCORE
		 * 16.038210870956323 Average Neg DIPSCORE -2.2190753638327325 Done
		 * 
		 * Intraday: 4300 stocks have been done totalGains 2226213.6300524403,
		 * totalDays 15884460 Average DIPSCORE 14.015041304850405 totalGainsP
		 * 2238574.4900574926, totalDaysP 15411941 Average Pos DIPSCORE
		 * 14.524935503305473 totalGainsN -12360.860000076944, totalDaysN 472519
		 * Average Neg DIPSCORE -2.615949834837741
		 * 
		 * Average DIPSCORE 14.013896332249214 Average Pos DIPSCORE
		 * 14.523201644754813 Average Neg DIPSCORE -2.6119732301743697
		 */
		int days = 5;
		int count = 4;
		// int minScore = 30;
		// IT SHOULD BE 26 OR 27 BASED ON AVERAGE CALCULATION
		/*
		 * -- INTERDAY DIPSCORE>=28 COUNT 1134519, total 15731000 count 9.66%;
		 * -- INTERDAY DIPSCORE>=29 COUNT 1316166, total 15731000 count 8.37%;
		 * -- INTERDAY DIPSCORE>=30 COUNT 1134519, total 15731000 count 7.21%;
		 * -- INTERDAY DIPSCORE>=31 COUNT 1134519, total 15731000 count 6.19%;
		 * -- INTERDAY DIPSCORE>=32 COUNT 1316166, total 15731000 count 5.28%;
		 * -- INTERDAY DIPSCORE>=33 COUNT 1134519, total 15731000 count 4.48%;
		 * 
		 * -- INTRADAY DIPSCORE>=24 COUNT 2328203, total 17273646 count 13.48%;
		 * -- INTRADAY DIPSCORE>=25 COUNT 2056459, total 17273646 count 11.91%;
		 * -- INTRADAY DIPSCORE>=26 COUNT 1816453, total 17273646 count 10.52%;
		 * -- INTRADAY DIPSCORE>=27 COUNT 1602873, total 17273646 count 9.28%;
		 * -- INTRADAY DIPSCORE>=28 COUNT 1316166, total 17273646 count 8.18%;
		 * -- INTRADAY DIPSCORE>=29 COUNT 1134519, total 17273646 count 7.21%;
		 * -- INTRADAY DIPSCORE>=30 COUNT 1097388, total 17273646 count 6.35%;
		 */
		// BASED ON QUERY STATISTICS WE SHOULD USE 29 INSTEAD
		int minScore = 29;

		int bsz = 4 * days;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select SEQINDEX,DIPSCORE,CURRENTDATE,FINALPRICE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' AND  seqIndex>"
					+ (maxPreIndex - backCountNeeded / 10)
					+ " ORDER BY SEQINDEX DESC";

			if (seqIndex < 0) {
				SQL = "select SEQINDEX,DIPSCORE,CURRENTDATE,FINALPRICE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol + "'  ORDER BY SEQINDEX DESC";

			}

			if (lastOnly)
				SQL = "select SEQINDEX,DIPSCORE,CURRENTDATE,FINALPRICE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' AND  seqIndex<="
						+ seqIndex
						+ " and seqIndex>"
						+ (seqIndex - 8 * days)
						+ " ORDER BY SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);

			int[] indexes = new int[bsz];
			int[] dipscores = new int[bsz];
			String[] dates = new String[bsz];
			float[] prices = new float[bsz];

			int loopCount = 0;

			while (rs1.next()) {
				if (loopCount < bsz) {
					indexes[loopCount] = rs1.getInt(1);
					dipscores[loopCount] = rs1.getInt(2);
					dates[loopCount] = rs1.getString(3);
					prices[loopCount] = rs1.getFloat(4);
				} else {
					int tempScore = 0;
					for (int i = 0; i < days; i++) {
						if (dipscores[i] >= minScore) {
							tempScore = tempScore + 1;
						}
					}

					if (stmt2 == null)
						stmt2 = con.createStatement();

					if (tempScore >= count) {
						System.out.println(symbol + " at " + indexes[0] + " "
								+ dates[0] + " price: " + prices[0]
								+ " Total score " + tempScore);

						String sqlUpdate = "Update BPMADMIN.INTRADAYSTOCKS  SET DPS4 = "
								+ tempScore
								+ " WHERE SEQINDEX = "
								+ indexes[0]
								+ " AND SYMBOL='" + symbol + "'";

						// TEST ONLY
						/*
						 * String sqlUpdate =
						 * "Update BPMADMIN.INTRADAYSTOCKS  SET BBGO= " +
						 * tempScore + " WHERE SEQINDEX = " + indexes[0] +
						 * " AND SYMBOL='" + symbol + "'";
						 */
						if (indexes[0] > maxPreIndex) {
							stmt2.executeUpdate(sqlUpdate);
						}
					} else {
						String sqlUpdate = "Update BPMADMIN.INTRADAYSTOCKS  SET  DPS4 = 0 "
								+ " WHERE SEQINDEX = "
								+ indexes[0]
								+ " AND SYMBOL='" + symbol + "'";

						if (indexes[0] > maxPreIndex) {
							stmt2.executeUpdate(sqlUpdate);
						}
					}

					if (lastOnly) {
						break;
					}
					// rules

					for (int k = 0; k < bsz - 1; k++) {
						indexes[k] = indexes[k + 1];
						dipscores[k] = dipscores[k + 1];
						dates[k] = dates[k + 1];
						prices[k] = prices[k + 1];
					}
					indexes[bsz - 1] = rs1.getInt(1);
					dipscores[bsz - 1] = rs1.getInt(2);
					dates[bsz - 1] = rs1.getString(3);
					prices[bsz - 1] = rs1.getFloat(4);
				}
				loopCount++;
				if (indexes[0] < maxPreIndex) {
					break;
				}
			}

			// set up tag for interruptable calculation
			/*
			 * String sqlUpdate =
			 * "Update BPMADMIN.INTRADAYSTOCKS  SET DCINCR =1000 " +
			 * " WHERE SEQINDEX = " + seqIndex + " AND SYMBOL='" + symbol + "'";
			 * 
			 * stmt2.executeUpdate(sqlUpdate);
			 */
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	public static void calculateMissingHistory(int seqIndex, boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = " SELECT symbol FROM BPMADMIN.INTRADAYSTOCKS where seqindex=42904 AND DSI<0.0001 AND DSI>-0.0001";

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String symbol = en.nextElement().toString();
				SQLOperation3.calculateDSI24HistorySP(symbol, 0L);
				// NOT WORKING
				// SQLOperation.calculateDSI(symbol, 24, -1);
				// SQLOperation.calculateAVGLMDRHistory(symbol, false);
				SQLOperation3.calculateTrendPointsHistory(symbol, -1, -1);
				// SQLOperation.calculateIncDescTrendDays(symbol, -1, false,
				// 40);
				// SQLOperation.calculateDeltaAgainstSPY(symbol, -1, false, 40);
				SQLOperation3.calculateDSISumHistory(symbol, 0L);
				SQLOperation3.calculateSurgeHistorySP(symbol, 0L);
				SQLOperation3.calculateDSI5SumHistorySP(symbol, 0L);

				SQLOperation3.calculatePTMAVSumHistorySP(symbol, 0L);

				SQLOperation3.scoreDipOp(symbol, -1, false);
				SQLOperation3.getDPS4History(symbol, -1, false);
				SQLOperation3.getTurnPointScore(symbol, -1, false);
				SQLOperation3.UpdateRecordAge(symbol);
				long t2 = System.currentTimeMillis();
				System.out.println(symbol
						+ " Stock turn point calculation done "
						+ " cost time seconds " + (t2 - t1) / 1000);

				loopCount++;
				if (loopCount % 10 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(8000);
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateEntireTotalBuyPointsHistory(int seqIndex,
			boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex + " AND DCINCR = 0";

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				// getTurnPointScore(nextStock, seqIndex, lastOnly);
				getTotalBuyScore(nextStock, seqIndex, lastOnly);
				long t2 = System.currentTimeMillis();
				System.out.println(nextStock
						+ " Stock turn point calculation done "
						+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;
				if (loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(8000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateEntireTurnPointsHistory(int seqIndex,
			boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex + " AND DCINCR = 0";

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				getTurnPointScore(nextStock, seqIndex, lastOnly);
				long t2 = System.currentTimeMillis();
				System.out.println(nextStock
						+ " Stock turn point calculation done "
						+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;
				if (loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(8000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void getTotalBuyScore(String symbol, long maxPreIndex,
			boolean lastOnly) {
		// max days for turning point consideration
		int days = 11;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select SEQINDEX,DPS4,BBGO,DELTASUM AS VOLS,PTMA,CURRENTDATE,FINALPRICE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' AND  seqIndex>"
					+ (maxPreIndex - backCountNeeded / 15)
					+ " ORDER BY SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);

			int[] indexes = new int[days];
			int[] dps4s = new int[days];
			int[] bbgos = new int[days];
			int[] volumes = new int[days];
			float[] ptmas = new float[days];
			String[] dates = new String[days];
			float[] prices = new float[days];

			int loopCount = 0;

			int turningValue = 0;

			while (rs1.next()) {
				if (loopCount < days) {
					indexes[loopCount] = rs1.getInt(1);
					dps4s[loopCount] = rs1.getInt(2);
					bbgos[loopCount] = rs1.getInt(3);
					volumes[loopCount] = (int) rs1.getFloat(4);
					ptmas[loopCount] = rs1.getFloat(5);
					dates[loopCount] = rs1.getString(6);
					prices[loopCount] = rs1.getFloat(7);
					if (seqIndex < 0) {
						seqIndex = indexes[0];
					}
				} else {

					turningValue = 0;
					if (ptmas[0] > 0) {

						// logic in this else section not done yet!!!
						// 6/22/2017, need to be completed!
						boolean lessThan0 = false;
						int daysToZero = 0;
						for (int k = 1; k < days; k++) {
							daysToZero++;
							if (ptmas[k] < 0) {
								lessThan0 = true;
								break;
							}

						}

						// do we need this adjustment???
						boolean lessThan5 = false;
						int daysToLeast = 0;
						float ptmamin = 10000.0f;
						if (!lessThan0)
							for (int k = days - 1; k >= 1; k--) {
								if (ptmas[k] < 5) {
									lessThan5 = true;
								}
								if (ptmas[k] < ptmamin) {
									ptmamin = ptmas[k];
									daysToLeast = k;
								}
							}

						if (dps4s[0] > 0 && bbgos[0] > 0) {
							turningValue = dps4s[0] + bbgos[0] + volumes[0];
						} else if (dps4s[0] > 0 && volumes[0] > 0
								&& bbgos[0] == 0) {
							turningValue = dps4s[0] + bbgos[0] + volumes[0];
						} else if (bbgos[0] > 0 && volumes[0] > 0
								&& dps4s[0] == 0) {
							turningValue = dps4s[0] + bbgos[0] + volumes[0];
						}

						if (lessThan0 && turningValue > 0) {
							turningValue = turningValue + (10 - daysToZero + 1);
						} else if (lessThan0 && turningValue == 0) {
							if (dps4s[0] > 0)
								turningValue = dps4s[0] + (10 - daysToZero + 1);
							if (bbgos[0] > 0)
								turningValue = bbgos[0] + (10 - daysToZero + 1);
						} else if (!lessThan0 && lessThan5 && turningValue > 0) {
							turningValue = turningValue
									+ (10 - daysToLeast - 1);
						} else if (!lessThan0 && lessThan5 && turningValue == 0) {
							if (dps4s[0] > 0)
								turningValue = dps4s[0]
										+ (10 - daysToLeast - 1);
							if (bbgos[0] > 0)
								turningValue = bbgos[0]
										+ (10 - daysToLeast - 1);
						}

					}

					if (stmt2 == null) {
						stmt2 = con.createStatement();

					}

					String sqlUpdate = "Update BPMADMIN.INTRADAYSTOCKS  SET FFP = "
							+ turningValue
							+ " WHERE SEQINDEX = "
							+ indexes[0]
							+ " AND SYMBOL='" + symbol + "'";

					if (indexes[0] > maxPreIndex) {
						stmt2.executeUpdate(sqlUpdate);
					}

					if (lastOnly) {
						break;
					}

					for (int k = 0; k < days - 1; k++) {
						indexes[k] = indexes[k + 1];
						dps4s[k] = dps4s[k + 1];
						bbgos[k] = bbgos[k + 1];
						volumes[k] = volumes[k + 1];
						ptmas[k] = ptmas[k + 1];
						dates[k] = dates[k + 1];
						prices[k] = prices[k + 1];
					}

					indexes[days - 1] = rs1.getInt(1);
					dps4s[days - 1] = rs1.getInt(2);
					bbgos[days - 1] = rs1.getInt(3);
					volumes[days - 1] = (int) rs1.getFloat(4);
					ptmas[days - 1] = rs1.getFloat(5);
					dates[days - 1] = rs1.getString(6);
					prices[days - 1] = rs1.getFloat(7);

					if (indexes[0] < maxPreIndex) {
						break;
					}
				}
				loopCount++;
			}

			// String sqlUpdate =
			// "Update BPMADMIN.INTRADAYSTOCKS  SET DCINCR =1000 "
			// + " WHERE SEQINDEX = " + seqIndex + " AND SYMBOL='"
			// + symbol + "'";

			// stmt2.executeUpdate(sqlUpdate);

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	public static void getTurnPointScore(String symbol, long maxPreIndex,
			boolean lastOnly) {
		// max days for turning point consideration
		int days = 20;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select SEQINDEX,SATCOUNT,DSI3,CURRENTDATE,FINALPRICE,PTMA from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' AND  seqIndex>"
					+ (maxPreIndex - backCountNeeded / 6)
					+ " ORDER BY SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);

			int[] indexes = new int[days];
			int[] sats = new int[days];
			float[] dsisum = new float[days];
			String[] dates = new String[days];
			float[] prices = new float[days];
			float[] ptmas = new float[days];

			int loopCount = 0;

			int turningValue = 0;

			while (rs1.next()) {
				if (loopCount < days) {
					indexes[loopCount] = rs1.getInt(1);
					sats[loopCount] = rs1.getInt(2);
					dsisum[loopCount] = rs1.getFloat(3);
					dates[loopCount] = rs1.getString(4);
					prices[loopCount] = rs1.getFloat(5);
					ptmas[loopCount] = rs1.getFloat(6);
					if (seqIndex < 0) {
						seqIndex = indexes[0];
					}
				} else {

					boolean turningPointFound = false;
					turningValue = 0;
					int loop = 0;

					while (!turningPointFound && loop < 16) {
						int totalRecords = 4 + loop;
						int satNegCount = 0;
						int[] satNegIndex = new int[totalRecords];
						int satPosCount = 0;
						int[] satPosIndex = new int[totalRecords];
						float maxDSISum = -10000.0f;
						float minDSISum = 10000.0f;
						int maxDSIIndex = 0;
						int minDSIIndex = 0;

						for (int w = 0; w < (4 + loop); w++) {
							if (sats[w] < 0) {
								satNegIndex[satNegCount] = indexes[w];
								satNegCount++;

							} else if (sats[w] > 0) {
								satPosIndex[satPosCount] = indexes[w];
								satPosCount++;

							}

							if (dsisum[w] > maxDSISum) {
								maxDSISum = dsisum[w];
								maxDSIIndex = indexes[w];
							}

							if (dsisum[w] < minDSISum) {
								minDSISum = dsisum[w];
								minDSIIndex = indexes[w];
							}

						}

						// if (satPosCount >= 2 && satNegCount >= 2
						// && (maxDSISum - minDSISum) > 200.0f) {
						// 1.4x of average DSI3, so 150 instead of 200.0f

						// MAYBE IN THE FUTURE USE PROGRAM AND PERCENTAGE AS
						// STANDARD
						// TO RETUNE THE VALUES
						if (satPosCount >= 2 && satNegCount >= 2
								&& (maxDSISum - minDSISum) > 150.0f) {
							// first positive up-trend
							// the last SATCOUNT Positive index compared to the
							// first
							// SATCOUNT Negative index (index in descending
							// order)
							// max DSI is later than min DSI
							if ((satPosIndex[satPosCount - 1] > satNegIndex[0])
									&& (maxDSIIndex > minDSIIndex)
									// 110 instead of 150 for day gap
									&& minDSISum < 5.0f && maxDSISum > 110) {
								turningPointFound = true;
								// add 1 point for each additional SATCOUNT<>0
								turningValue = 20 - (4 + loop);
							}

							// second negative down-trend
							// the last SATCOUNT Negative index compared to the
							// first
							// SATCOUNT Positive index (index in descending
							// order)
							// max DSI is earlier than min DSI
							// MAY BE SEPARATE ALGORITHM TO BE PRECISE
							/*
							 * if ((satNegIndex[satNegCount - 1] >
							 * satPosIndex[0]) && (maxDSIIndex < minDSIIndex)) {
							 * if (minDSISum < 10) { turningPointFound = true;
							 * // add 1 point for each additional // SATCOUNT<>0
							 * // make it negative in total to denote //
							 * negative downtrend turningValue = -(20 - (4 +
							 * loop)); } }
							 */
						}

						// found the smallest days to satify the condition, no
						// need to go to 20 days
						if (turningPointFound) {
							break;
						}

						loop++;
					}

					if (stmt2 == null) {
						stmt2 = con.createStatement();

					}

					// System.out.println(symbol + " at " + indexes[0] + " "
					// + dates[0] + " price: " + prices[0]
					// + " Total score " + turningValue);

					String sqlUpdate = "Update BPMADMIN.INTRADAYSTOCKS  SET BBGO = "
							+ turningValue
							+ " WHERE SEQINDEX = "
							+ indexes[0]
							+ " AND SYMBOL='" + symbol + "'";

					if (indexes[0] > maxPreIndex)
						stmt2.executeUpdate(sqlUpdate);

					// if (lastOnly) {
					// break;
					// }
					// rules

					for (int k = 0; k < days - 1; k++) {
						indexes[k] = indexes[k + 1];
						sats[k] = sats[k + 1];
						dsisum[k] = dsisum[k + 1];
						dates[k] = dates[k + 1];
						prices[k] = prices[k + 1];
						ptmas[k] = ptmas[k + 1];
					}
					indexes[days - 1] = rs1.getInt(1);
					sats[days - 1] = rs1.getInt(2);
					dsisum[days - 1] = rs1.getFloat(3);
					dates[days - 1] = rs1.getString(4);
					prices[days - 1] = rs1.getFloat(5);
					ptmas[days - 1] = rs1.getFloat(6);
					if (indexes[0] < maxPreIndex) {
						break;
					}
				}
				loopCount++;
			}

			// String sqlUpdate =
			// "Update BPMADMIN.INTRADAYSTOCKS  SET DCINCR =1000 "
			// + " WHERE SEQINDEX = " + seqIndex + " AND SYMBOL='"
			// + symbol + "'";

			// stmt2.executeUpdate(sqlUpdate);

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	public static void calculateBBScore(String symbol, long maxPreIndex,
			boolean lastOnly) {
		// int days = 50;
		// int vDays = 10;

		// rules:
		// BABA,AMZN,FB,GRUB,GOOG,ACH,DQ,PCLN,SHOP,BZUN,WB,TSLA,NVDA,NOC,MCD,
		// BA,YRD,CTRP,JOBS,BITA,JD,MOMO,NMM,PBYI,YY,CLVS,V,NFLX,AAL,MSFT,BAC,
		// NEM,ABX,MTL,AMD,HTHT,JD,DB,VRTX,CSX,NRG,LRCX,IDXX(PRCEEDING -, LONG
		// WAITING),WYNN, EGRX,ALXN,BIIB,VRX,
		// 1. (>=7 POSITIVE) 10 DAYS HAS >=2 DAYS FFP>0, EACH FFP>0 AND
		// FFP<1000 ADD 1 POINT
		// EACH PS3PT 10000 ADD 3 POINTS, EACH DCPT 20000 ADD 3 POINTS,
		// 2. BEAR TYPE, CHANGE ABOVE TO OPPOSITE
		// RATHER THAN LONG TERM BEAR, THE SUM PONITS<-7 USUALLY INDICATING
		// OVERSOLD REACTION, NEAR TERM QUICK BOUND INDICATED, ESPECIALLY FOR
		// LARGE 'STABLE' CAP STOCKS, LIKE VZ, T, IBM ETC
		// .BIG STABLE CAP COMPANY >4 <-10000 WITH 10 DAYS, TEMP BOTTOM,
		// SHORT TERM UP, EXAMPLE, VZ, T, IBM, TM, ATVI, WAIT DS3PER>0 BUY

		// 3. LONG TERM BEAR INDICATOR: SUM(DS3PER) OVER 10??(12,13,15??) DAY
		// PEROID
		// CONTINOUS NEGATIVE ABOVE 20(??) DAYS INDICATING BEAR??
		// CMG,GPRO,UA,DDD,AMBA,SQM,TWTR,

		// 4. -- ++ TYPE, WYNN???
		// 5. ++ -- TYPE JD

		// 6. -- ++ -- ++ DQ, TM???
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			/*
			 * SQL =
			 * "UPDATE BPMADMIN.INTRADAYSTOCKS   SET BBSCORE=0 where symbol='" +
			 * symbol + "' AND (BBSCORE>0 OR BBSCORE<0)";
			 * stmt1.executeUpdate(SQL);
			 */
			// markBBEndPoint(symbol, maxPreIndex, false);
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null) {
				stmt1 = con.createStatement();
			}

			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018
			long tempIndex = 0;
			if (maxPreIndex > 0) {
				SQL = "select SEQINDEX,BBSCORE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and (BBSCORE=1 OR BBSCORE=-1) AND SEQINDEX<="
						+ maxPreIndex + " ORDER BY SEQINDEX DESC";
				rs1 = stmt1.executeQuery(SQL);

				if (rs1.next()) {
					tempIndex = rs1.getLong(1);
				}

			}
			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018

			// change seqIndex condition from maxPreIndex-backcountNeeded to
			// tempIndex
			// take out of the guess work, be more precise
			SQL = "select SEQINDEX,BBSCORE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' and (BBSCORE=1 OR BBSCORE=-1) AND SEQINDEX>="
					+ tempIndex + " ORDER BY SEQINDEX ASC";

			int seqIndex1 = 0;
			int bbscore1 = 0;
			int seqIndex2 = 0;
			int bbscore2 = 0;
			boolean ffpFound = false;
			int ffpCount = 0;
			boolean ds3ptFound = false;
			int ds3ptCount = 0;
			boolean dcptFound = false;
			int dcptCount = 0;
			boolean bullStart = false;
			int count = 0;

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				if (seqIndex1 == 0) {
					seqIndex1 = rs1.getInt(1);
					bbscore1 = rs1.getInt(2);
				} else if (seqIndex2 == 0) {
					seqIndex2 = rs1.getInt(1);
					bbscore2 = rs1.getInt(2);
					if (bbscore1 > 0) {
						findBBScoreBetween(symbol, seqIndex1, seqIndex2);
					}
				} else {
					// findBBScoreBetween(symbol,seqIndex1,seqIndex2);

					if (bbscore1 < 0) {
						seqIndex1 = seqIndex2;
						bbscore1 = bbscore2;
						seqIndex2 = rs1.getInt(1);
						bbscore2 = rs1.getInt(2);
					} else if (bbscore1 > 0) {
						findBBScoreBetween(symbol, seqIndex1, seqIndex2);
						seqIndex1 = seqIndex2;
						bbscore1 = bbscore2;
						seqIndex2 = rs1.getInt(1);
						bbscore2 = rs1.getInt(2);
					}

				}

			}

			if (bbscore1 > 0 && seqIndex2 > 0) {
				findBBScoreBetween(symbol, seqIndex1, seqIndex2);
			} else if (bbscore2 > 0) {
				SQL = "select SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and SEQINDEX >"
						+ maxPreIndex
						+ " ORDER BY SEQINDEX DESC";

				rs1 = stmt1.executeQuery(SQL);

				long topIndex = 0L;
				if (rs1.next()) {
					topIndex = rs1.getLong(1);
				}
				findBBScoreBetween(symbol, seqIndex2, topIndex);
			}

			if (bbscore1 > 0 && bbscore2 == 0 && seqIndex1 > 0
					&& seqIndex2 == 0) {
				SQL = "select SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and SEQINDEX >"
						+ maxPreIndex
						+ " ORDER BY SEQINDEX DESC";

				rs1 = stmt1.executeQuery(SQL);

				long topIndex = 0L;
				if (rs1.next()) {
					topIndex = rs1.getLong(1);
				}

				findBBScoreBetween(symbol, seqIndex1, topIndex);
			}
			// set up a tag
			/*
			 * String sqlUpdate =
			 * "Update BPMADMIN.INTRADAYSTOCKS  SET DCINCR =1000 " +
			 * " WHERE SEQINDEX = " + seqIndex + " AND SYMBOL='" + symbol + "'";
			 * 
			 * stmt2.executeUpdate(sqlUpdate);
			 */
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	private static void findBBScoreBetween(String symbol, long seqIndex1,
			long seqIndex2) {
		System.out.println("Find between " + seqIndex1 + "  " + seqIndex2);
		// findBBScoreVBend(symbol, seqIndex1);
		findPositiveBBScoreBetween(symbol, seqIndex1, seqIndex2);

	}

	private static void NoLongerUsed_findBBScoreVBend(String symbol,
			int seqIndex1) {
		// System.out.println("Find between " + seqIndex1 + " " + seqIndex2);
		int ffpCount = 0;
		int dcptCount = 0;
		int ds3ptCount = 0;
		boolean ffpFound = false;
		boolean dcptFound = false;
		boolean ds3ptFound = false;
		try {
			SQL = "select SEQINDEX,BBDI,FFP,DS3PT,DCPT,DPS4,FINALPRICE,PTMA,BBSCORE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' AND  seqIndex<="
					+ seqIndex1
					+ " AND seqIndex>="
					+ (seqIndex1 - 20)
					+ " Order by seqIndex DESC";

			rs2 = stmt2.executeQuery(SQL);

			int tCount = 0;
			int cBBDI = 0;
			int _100Count = 0;
			int _200Count = 0;
			boolean updateNeeded = true;

			while (rs2.next() && tCount <= 10) {
				if (tCount == 0) {
					cBBDI = rs2.getInt(2);
					if (cBBDI < 200) {
						updateNeeded = false;
						break;
					}
				}
				tCount++;
				if (rs2.getInt(2) == -100) {
					_100Count++;
				}
				if (rs2.getInt(2) == -200) {
					_200Count++;
				}
			}

			if (_200Count >= 1 || _100Count >= 2) {
				int totalScore = 881;
				String sql = "Update BPMADMIN.INTRADAYSTOCKS SET BBSCORE="
						+ totalScore + " where symbol='" + symbol
						+ "' AND  seqIndex=" + seqIndex1;

				if (disLog)
					System.out.println(tCount + " " + sql);

				stmt3.executeUpdate(sql);

			} else if (_100Count == 1) {
				int totalScore = 81;
				String sql = "Update BPMADMIN.INTRADAYSTOCKS SET BBSCORE="
						+ totalScore + " where symbol='" + symbol
						+ "' AND  seqIndex=" + seqIndex1;

				if (disLog)
					System.out.println(tCount + " " + sql);

				stmt3.executeUpdate(sql);

			}
			/*
			 * no longer needed as we mark the last -100/-200 BBDI as the bull
			 * start else if (updateNeeded) { // same update int totalScore = 1;
			 * String sql = "Update BPMADMIN.INTRADAYSTOCKS SET BBSCORE=" +
			 * totalScore + " where symbol='" + symbol + "' AND  seqIndex=" +
			 * seqIndex1; System.out.println(tCount + " " + sql);
			 * stmt3.executeUpdate(sql);
			 * 
			 * }
			 */
		} catch (Exception ex) {

		}
	}

	private static void findPositiveBBScoreBetween(String symbol,
			long seqIndex1, long seqIndex2) {

		if (disLog)
			System.out.println("Find between " + seqIndex1 + " " + seqIndex2);

		int ffpCount = 0;
		int dcptCount = 0;
		int ds3ptCount = 0;
		boolean ffpFound = false;
		boolean dcptFound = false;
		boolean ds3ptFound = false;
		try {

			SQL = "select SEQINDEX,FFP,DS3PT,DCPT,DPS4,FINALPRICE,PTMA,BBSCORE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' AND  seqIndex>="
					+ seqIndex1
					+ " AND seqIndex<="
					+ seqIndex2
					+ " AND (DPS4>0 OR FFP>0 OR FFP<0 OR DS3PT>0 OR DS3PT<0 OR DCPT>0 OR DCPT<0) ORDER BY SEQINDEX ASC";

			rs2 = stmt2.executeQuery(SQL);

			while (rs2.next()) {
				int cIndex = rs2.getInt(1);
				int cFfp = rs2.getInt(2);
				int cDs3pt = rs2.getInt(3);
				int cDcpt = rs2.getInt(4);
				int DPS4 = rs2.getInt(5);
				float cPtma = rs2.getFloat(7);
				int cBBScore = rs2.getInt(8);

				if (cFfp > 0 || DPS4 > 0) {
					ffpFound = true;
					ffpCount++;
					int totalScore = 0;
					if (ds3ptFound && dcptFound) {
						totalScore = 3000;
					} else if (ds3ptFound || dcptFound) {
						totalScore = 2000;
					} else {
						totalScore = 1000;
					}

					if (cBBScore == 0) {
						String sql = "Update BPMADMIN.INTRADAYSTOCKS SET BBSCORE="
								+ totalScore
								+ " where symbol='"
								+ symbol
								+ "' AND  seqIndex=" + cIndex;

						if (disLog)
							System.out.println(sql);

						stmt3.executeUpdate(sql);
					}

				}

				if (cDs3pt > 0) {
					ds3ptFound = true;
					ds3ptCount++;

					int totalScore = 0;
					if (ffpFound && dcptFound) {
						totalScore = 3000;
					} else if (ffpFound || dcptFound) {
						totalScore = 2000;
					} else {
						totalScore = 1000;
					}

					if (cBBScore == 0) {
						String sql = "Update BPMADMIN.INTRADAYSTOCKS SET BBSCORE="
								+ totalScore
								+ " where symbol='"
								+ symbol
								+ "' AND  seqIndex=" + cIndex;
						stmt3.executeUpdate(sql);

						if (disLog)
							System.out.println(sql);
					}

				} else if (cDs3pt < 0) {
					ds3ptCount--;
				}

				if (cDcpt > 0) {
					dcptFound = true;
					dcptCount++;

					int totalScore = 0;
					if (ffpFound && ds3ptFound) {
						totalScore = 3000;
					} else if (ffpFound || ds3ptFound) {
						totalScore = 2000;
					} else {
						totalScore = 1000;
					}

					if (cBBScore == 0) {
						String sql = "Update BPMADMIN.INTRADAYSTOCKS SET BBSCORE="
								+ totalScore
								+ " where symbol='"
								+ symbol
								+ "' AND  seqIndex=" + cIndex;

						if (disLog)
							System.out.println(sql);

						stmt3.executeUpdate(sql);
					}

				} else if (cDcpt < 0) {
					dcptCount--;
				}
			}
		} catch (Exception ex) {

		}
	}

	public static void calculateEntireSellingScoreHistory(int seqIndex,
			boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				// int sumDays = 7;
				calculateSellingScores(nextStock, seqIndex, lastOnly);
				long t2 = System.currentTimeMillis();

				if (disLog)
					System.out.println(nextStock
							+ " Stock SellingScore calculation done "
							+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;

				if (!lastOnly && loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(5000);
				} else if (loopCount % 1000 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(10000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateLBBIHistory(String symbol, long maxPreIndex,
			boolean lastOnly) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select SEQINDEX, BBSCORE, BBDI,UTIS from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ symbol
					+ "' AND SEQINDEX>"
					+ (maxPreIndex - backCountNeeded)
					+ " AND ((BBSCORE>0 OR BBSCORE<0) OR (BBDI<=-100 OR BBDI>=200) OR UTIS>0) ORDER BY SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);

			int index2 = 0;
			int bbscore2 = 0;
			int bbdi2 = 0;
			int utis2 = 0;
			boolean trackCorrectionUp = false;
			boolean checkBBDI = false;
			boolean trackBearStart = false;
			boolean bearStart = false;
			int bearCount = 0;
			int previousBBScore = 0;
			boolean trackUTIS = false;
			int maxUTIS = 0;

			while (rs1.next()) {

				index2 = rs1.getInt(1);
				bbscore2 = rs1.getInt(2);
				bbdi2 = rs1.getInt(3);
				utis2 = rs1.getInt(4);

				if (trackUTIS && utis2 > maxUTIS) {
					maxUTIS = utis2;
					System.out.println("maxUTIS " + maxUTIS + " at " + index2);
				}

				if (bbscore2 == 3000) {
					trackCorrectionUp = true;
					trackBearStart = false;
					checkBBDI = false;
					bearCount = 0;
				} else if (bbscore2 == 2000) {
					trackBearStart = true;
					trackCorrectionUp = false;
					checkBBDI = false;
				} else if (bbscore2 == 1000) {
					trackBearStart = true;
					trackCorrectionUp = false;
					checkBBDI = false;
				} else if (bbscore2 <= -1 && bbscore2 > -1000) {
					if (trackCorrectionUp) {
						checkBBDI = true;
					} else if (trackBearStart) {
						bearCount++;
					}
					if (!trackUTIS) {
						trackUTIS = true;
					}
				}

				if (checkBBDI && bbdi2 >= 200) {
					trackCorrectionUp = false;
					checkBBDI = false;
					maxUTIS = 0;
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET LBBI = 2 WHERE SYMBOL ='"
							+ symbol + "' AND SEQINDEX = " + index2;
					System.out.println(SQL);
					stmt2.executeUpdate(SQL);

				}

				if (trackBearStart && bearCount >= 2) {
					trackCorrectionUp = false;
					trackBearStart = false;
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET LBBI = "
							+ bearCount * (-4) + " WHERE SYMBOL ='" + symbol
							+ "' AND SEQINDEX = " + index2;
					System.out.println("2..." + SQL);
					stmt2.executeUpdate(SQL);
					bearCount = 0;
					maxUTIS = 0;

				}

				if (trackBearStart && bearCount >= 1 && maxUTIS <= 1) {
					trackCorrectionUp = false;
					trackBearStart = false;
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET LBBI = "
							+ bearCount * (-5) + " WHERE SYMBOL ='" + symbol
							+ "' AND SEQINDEX = " + index2;
					System.out.println("2..." + SQL);
					stmt2.executeUpdate(SQL);
					bearCount = 0;
					maxUTIS = 0;

				} else if (trackBearStart && bearCount >= 1 && maxUTIS > 1) {
					trackCorrectionUp = false;
					trackBearStart = false;
					bearCount = 0;
					maxUTIS = 0;
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	public static void calculateEntirePTSHistory(int seqIndex, boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				if (lastOnly) {
					calculateLastPTS(nextStock, seqIndex);
					calculateTruePTS(nextStock, seqIndex, true);
					calculateATT(nextStock, seqIndex, true);
				} else {
					calculateEntirePTS(nextStock, seqIndex);
					calculateTruePTS(nextStock, seqIndex, false);
					calculateATT(nextStock, seqIndex, false);
				}
				long t2 = System.currentTimeMillis();
				loopCount++;
				System.out.println(nextStock + " Stock PTS calculation done "
						+ loopCount + " cost time seconds " + (t2 - t1) / 1000);

				if (!lastOnly && loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(5000);
				} else if (loopCount % 1000 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(10000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateEntirePTS(String symbol, long maxPreIndex) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			// *** added 3/2/2018 to figure out where to start appropriately
			long tempIndex = 0L;
			int tempPTS = 0;
			SQL = "select SEQINDEX, PTS from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ symbol
					+ "'  AND PTS<>0 AND SEQINDEX<="
					+ maxPreIndex
					+ " ORDER BY SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);
			if (rs1.next()) {
				tempIndex = rs1.getLong(1);
				tempPTS = rs1.getInt(2);
			}

			if (tempPTS < 0) {
				SQL = "select SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
						+ symbol
						+ "'  AND AWS>0 AND SEQINDEX>"
						+ tempIndex
						+ " ORDER BY SEQINDEX ASC";
			} else {
				SQL = "select SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
						+ symbol
						+ "'  AND AWS<0 AND SEQINDEX>"
						+ tempIndex
						+ " ORDER BY SEQINDEX ASC";
			}

			rs1 = stmt1.executeQuery(SQL);
			if (rs1.next()) {

				tempIndex = rs1.getLong(1);
			}
			// **** added 3/2/2018 to figure out where to start appropriately

			SQL = "select SEQINDEX, AWS,FINALPRICE from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ symbol
					+ "'  AND SEQINDEX>="
					+ tempIndex
					+ " ORDER BY SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);

			int indexMin = 0;
			int indexMax = 0;
			int index = 0;
			float price = 0.0f;
			int aws = 0;
			float priceMin = 100000.0f;
			float priceMax = 0.0f;
			boolean start = false;
			int pCount = 0;
			int nCount = 0;
			int switchCount = 0;
			// awsTS aws trend switch between -aws and +aws
			boolean awsTS = false;
			boolean awsP = false;
			boolean awsN = false;
			int nCountMin = 1;
			int pCountMin = 1;

			while (rs1.next()) {
				index = rs1.getInt(1);
				aws = rs1.getInt(2);
				price = rs1.getFloat(3);
				if (!start && (aws > 0 || aws < 0)) {
					start = true;
					if (aws > 0) {
						awsP = true;
					}

					if (aws < 0) {
						awsN = true;
					}
					switchCount++;
				}

				if (start) {

					if (aws == 0) {
						while (rs1.next()) {
							int indexNext = rs1.getInt(1);
							int awsNext = rs1.getInt(2);
							float priceNext = rs1.getFloat(3);

							if (awsP && awsNext >= 0) {
								if (price > priceMax) {
									priceMax = price;
									indexMax = index;
								}

								pCount++;
							} else if (awsN && awsNext <= 0) {
								if (price < priceMin) {
									priceMin = price;
									indexMin = index;
								}
								nCount++;
							}
							index = indexNext;
							aws = awsNext;
							price = priceNext;

							if (aws > 0 || aws < 0) {
								break;
							}
						}
					}

					if (aws > 0 && awsN) {
						switchCount++;
						awsN = false;
						awsP = true;
					} else if (aws < 0 && awsP) {
						switchCount++;
						awsP = false;
						awsN = true;
					}

					if (aws > 0) {
						if (awsP) {
							if (price > priceMax) {
								priceMax = price;
								indexMax = index;
							}
							pCount++;
						}

					}

					if (aws < 0) {
						if (awsN) {
							if (price < priceMin) {
								priceMin = price;
								indexMin = index;
							}
							nCount++;
						}
					}
					if (switchCount >= 2 && awsP) {
						if (nCount >= nCountMin) {
							int pts = -10000 - nCount;
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET PTS ="
									+ pts + " WHERE SYMBOL ='" + symbol
									+ "' AND SEQINDEX=" + indexMin;
							System.out.println(SQL);
							stmt2.executeUpdate(SQL);
						}
						indexMin = 0;
						switchCount = 1;
						priceMin = 100000;
						nCount = 0;
					} else if (switchCount >= 2 && awsN) {
						if (pCount >= pCountMin) {
							int pts = 10000 + pCount;
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET PTS ="
									+ pts + " WHERE SYMBOL ='" + symbol
									+ "' AND SEQINDEX=" + indexMax;
							System.out.println(SQL);
							stmt2.executeUpdate(SQL);
						}
						indexMax = 0;
						switchCount = 1;
						priceMax = 0;
						pCount = 0;
					}

				}

			}

			if (awsP) {
				int pts = 10000 + pCount;
				SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET PTS =" + pts
						+ " WHERE SYMBOL ='" + symbol + "' AND SEQINDEX="
						+ indexMax;
				System.out.println(SQL);
				stmt2.executeUpdate(SQL);
			} else if (awsN) {
				int pts = -10000 - nCount;
				SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET PTS =" + pts
						+ " WHERE SYMBOL ='" + symbol + "' AND SEQINDEX="
						+ indexMin;
				System.out.println(SQL);
				stmt2.executeUpdate(SQL);
			}

		} catch (Exception ex) {

		}

	}

	public static void calculateTruePTS(String symbol, int seqIndex,
			boolean lastOnly) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select SEQINDEX, PTS,FINALPRICE from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ symbol + "' AND PTS<>0 ORDER BY SEQINDEX ASC";

			if (lastOnly) {
				SQL = "select SEQINDEX, PTS,FINALPRICE from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
						+ symbol
						+ "' AND PTS<>0 AND SEQINDEX>"
						+ (seqIndex - backCount) + " ORDER BY SEQINDEX ASC";
			}
			rs1 = stmt1.executeQuery(SQL);

			int index1 = 0;
			int index2 = 0;
			int index = 0;
			float price = 0.0f;
			int pts = 0;
			float price1 = 100000.0f;
			float price2 = 0.0f;
			int pts1 = 0;
			int pts2 = 0;

			while (rs1.next()) {
				index = rs1.getInt(1);
				pts = rs1.getInt(2);
				price = rs1.getFloat(3);
				if (index1 == 0) {
					index1 = index;
					pts1 = pts;
					price1 = price;
				} else if (index2 == 0) {
					index2 = index;
					pts2 = pts;
					price2 = price;
				} else {
					if (pts1 > 0 && pts2 < 0) {
						if (((price2 - price1) / price2) <= -0.05f) {
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET TPTS = -4 WHERE SYMBOL ='"
									+ symbol + "' AND SEQINDEX=" + index2;
							System.out.println(SQL);
							stmt2.executeUpdate(SQL);
						} else if (((price2 - price1) / price2) > -0.05f) {
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET TPTS = -1 WHERE SYMBOL ='"
									+ symbol + "' AND SEQINDEX=" + index2;
							System.out.println(SQL);
							stmt2.executeUpdate(SQL);
						}

						if (index2 == seqIndex) {
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET TPTS = -2 WHERE SYMBOL ='"
									+ symbol + "' AND SEQINDEX=" + index2;
							System.out.println(SQL);
							stmt2.executeUpdate(SQL);
						}
					} else if (pts1 < 0 && pts2 > 0) {
						if (((price2 - price1) / price2) >= 0.05f) {
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET TPTS = 4 WHERE SYMBOL ='"
									+ symbol + "' AND SEQINDEX=" + index2;
							System.out.println(SQL);
							stmt2.executeUpdate(SQL);
						} else if (((price2 - price1) / price2) < 0.05f) {
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET TPTS = 1 WHERE SYMBOL ='"
									+ symbol + "' AND SEQINDEX=" + index2;
							System.out.println(SQL);
							stmt2.executeUpdate(SQL);
						}

						if (index2 == seqIndex) {
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET TPTS = 2 WHERE SYMBOL ='"
									+ symbol + "' AND SEQINDEX=" + index2;
							System.out.println(SQL);
							stmt2.executeUpdate(SQL);
						}
					} else {
						System.out.println("" + index1 + ":" + pts1 + ":"
								+ price1 + "," + index2 + ":" + pts2 + ":"
								+ price2);
						throw new Exception("Impossible data " + symbol
								+ " at " + index2);

					}

					index1 = index2;
					price1 = price2;
					pts1 = pts2;

					index2 = index;
					pts2 = pts;
					price2 = price;
				}

			}

			if (pts1 > 0 && pts2 < 0) {
				if (((price2 - price1) / price2) <= -0.05f) {
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET TPTS = -4 WHERE SYMBOL ='"
							+ symbol + "' AND SEQINDEX=" + index2;
					System.out.println(SQL);
					stmt2.executeUpdate(SQL);
				} else if (((price2 - price1) / price2) > -0.05f) {
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET TPTS = -1 WHERE SYMBOL ='"
							+ symbol + "' AND SEQINDEX=" + index2;
					System.out.println(SQL);
					stmt2.executeUpdate(SQL);
				}

				if (index2 == seqIndex) {
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET TPTS = -2 WHERE SYMBOL ='"
							+ symbol + "' AND SEQINDEX=" + index2;
					System.out.println(SQL);
					stmt2.executeUpdate(SQL);
				}
			} else if (pts1 < 0 && pts2 > 0) {
				if (((price2 - price1) / price2) >= 0.05f) {
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET TPTS = 4 WHERE SYMBOL ='"
							+ symbol + "' AND SEQINDEX=" + index2;
					System.out.println(SQL);
					stmt2.executeUpdate(SQL);
				} else if (((price2 - price1) / price2) < 0.05f) {
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET TPTS = 1 WHERE SYMBOL ='"
							+ symbol + "' AND SEQINDEX=" + index2;
					System.out.println(SQL);
					stmt2.executeUpdate(SQL);
				}

				if (index2 == seqIndex) {
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET TPTS = 2 WHERE SYMBOL ='"
							+ symbol + "' AND SEQINDEX=" + index2;
					System.out.println(SQL);
					stmt2.executeUpdate(SQL);
				}
			} else {
				System.out.println("" + index1 + ":" + pts1 + ":" + price1
						+ "," + index2 + ":" + pts2 + ":" + price2);
				throw new Exception("Impossible data " + symbol + " at "
						+ index2);

			}

			SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET TPTS=0 where PTS=0 AND TPTS<>0 AND SYMBOL ='"
					+ symbol + "'";
			System.out.println(SQL);
			stmt2.executeUpdate(SQL);

		} catch (Exception ex) {

			ex.printStackTrace(System.out);
		}

	}

	// CALCULATION BASED ON TPTS, ONLY TPTS +4,-4 IS CONSIDERED
	// CLASSICAL HIGHER HIGH, HIGHER LOW, OR STRAIGHT 50 DAYS ONE DIRECTION
	public static void calculateATT(String symbol, int seqIndex,
			boolean lastOnly) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (!lastOnly) {
				SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET ATT =0 WHERE SYMBOL ='"
						+ symbol + "' AND ATT<>0";
				stmt1.executeUpdate(SQL);
			}

			// +1 AND -1 ARE IGNORED, +2, -2 ARE USED AS LASTEST TPTS POINTS
			// SO WE NEED TO CONSIDER THEM
			SQL = "select SEQINDEX, TPTS, FINALPRICE from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ symbol
					+ "' AND (TPTS>=2 OR TPTS<=-2) ORDER BY SEQINDEX ASC";

			if (lastOnly) {
				SQL = "select SEQINDEX, TPTS, FINALPRICE from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
						+ symbol
						+ "' AND (TPTS>=2 OR TPTS<=-2) AND SEQINDEX>"
						+ (seqIndex - backCount) + " ORDER BY SEQINDEX ASC";

			}
			rs1 = stmt1.executeQuery(SQL);

			int index1 = 0;
			int index2 = 0;
			int index3 = 0;
			int index4 = 0;
			int index = 0;
			float price = 0.0f;
			int tpts = 0;
			float price1 = 0.0f;
			float price2 = 0.0f;
			float price3 = 0.0f;
			float price4 = 0.0f;
			int tpts1 = 0;
			int tpts2 = 0;
			int tpts3 = 0;
			int tpts4 = 0;

			while (rs1.next()) {
				index = rs1.getInt(1);
				tpts = rs1.getInt(2);
				price = rs1.getFloat(3);
				if (index1 == 0) {
					index1 = index;
					tpts1 = tpts;
					price1 = price;
				} else if (index2 == 0) {
					index2 = index;
					tpts2 = tpts;
					price2 = price;
				} else if (index3 == 0) {
					index3 = index;
					tpts3 = tpts;
					price3 = price;
				} else if (index4 == 0) {
					index4 = index;
					tpts4 = tpts;
					price4 = price;
				} else {
					if (price1 < price3 && price2 < price4) {
						// higher high and higher low case
						if (tpts1 > 0 && tpts2 < 0 && tpts3 > 0 && tpts4 < 0) {
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET ATT = 8 WHERE SYMBOL ='"
									+ symbol + "' AND SEQINDEX=" + index4;
							System.out.println(SQL);
							stmt2.executeUpdate(SQL);
						} else if (tpts1 < 0 && tpts2 > 0 && tpts3 < 0
								&& tpts4 > 0) {
							// higher low and higher high case
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET ATT = 8 WHERE SYMBOL ='"
									+ symbol + "' AND SEQINDEX=" + index4;
							System.out.println(SQL);
							stmt2.executeUpdate(SQL);
						}

					} else if (price1 > price3 && price2 > price4) {
						// lower high and lower low case
						if (tpts1 > 0 && tpts2 < 0 && tpts3 > 0 && tpts4 < 0) {
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET ATT = -8 WHERE SYMBOL ='"
									+ symbol + "' AND SEQINDEX=" + index4;
							System.out.println(SQL);
							stmt2.executeUpdate(SQL);
						} else if (tpts1 < 0 && tpts2 > 0 && tpts3 < 0
								&& tpts4 > 0) {
							// lower low and lower high case
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET ATT = -8 WHERE SYMBOL ='"
									+ symbol + "' AND SEQINDEX=" + index4;
							System.out.println(SQL);
							stmt2.executeUpdate(SQL);
						}
					}

					int days1 = findDaysBetween(symbol, index1, index2);
					int days2 = findDaysBetween(symbol, index2, index3);
					int days3 = findDaysBetween(symbol, index3, index4);

					if (days1 >= 50) {
						if ((tpts1 > 0 && tpts2 < 0)
								|| (tpts1 < 0 && tpts2 < 0)) { // 50 days
																// straight down
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET ATT = -50 WHERE SYMBOL ='"
									+ symbol + "' AND SEQINDEX=" + index2;
							System.out.println(SQL);
							stmt2.executeUpdate(SQL);
						} else if ((tpts1 < 0 && tpts2 > 0)
								|| (tpts1 > 0 && tpts2 > 0)) { // 50 days
																// straight up
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET ATT = 50 WHERE SYMBOL ='"
									+ symbol + "' AND SEQINDEX=" + index2;
							System.out.println(SQL);
							stmt2.executeUpdate(SQL);
						}

					} else if ((days1 + days2) >= 50) { // need check tpts
														// values
						if (tpts2 < 0 && tpts3 < 0) { // 50 days straight down
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET ATT = -50 WHERE SYMBOL ='"
									+ symbol + "' AND SEQINDEX=" + index3;
							System.out.println(SQL);
							stmt2.executeUpdate(SQL);
						} else if (tpts2 > 0 && tpts3 > 0) { // 50 days straight
																// up
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET ATT = 50 WHERE SYMBOL ='"
									+ symbol + "' AND SEQINDEX=" + index3;
							System.out.println(SQL);
							stmt2.executeUpdate(SQL);
						}

					} else if ((days1 + days2 + days3) >= 50) {// need check
																// tpts values
						if (tpts2 < 0 && tpts3 < 0 && tpts4 < 0) { // 50 days
																	// straight
																	// down
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET ATT = -50 WHERE SYMBOL ='"
									+ symbol + "' AND SEQINDEX=" + index4;
							System.out.println(SQL);
							stmt2.executeUpdate(SQL);
						} else if (tpts2 > 0 && tpts3 > 0 && tpts4 > 0) { // 50
																			// days
																			// straight
																			// up
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET ATT = 50 WHERE SYMBOL ='"
									+ symbol + "' AND SEQINDEX=" + index4;
							System.out.println(SQL);
							stmt2.executeUpdate(SQL);
						}
					}

					index1 = index2;
					price1 = price2;
					tpts1 = tpts2;

					index2 = index3;
					price2 = price3;
					tpts2 = tpts3;

					index3 = index4;
					price3 = price4;
					tpts3 = tpts4;

					index4 = index;
					price4 = price;
					tpts4 = tpts;

				}

			}

			if (price1 < price3 && price2 < price4) {
				// higher high and higher low case
				if (tpts1 > 0 && tpts2 < 0 && tpts3 > 0 && tpts4 < 0) {
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET ATT = 8 WHERE SYMBOL ='"
							+ symbol + "' AND SEQINDEX=" + index4;
					System.out.println(SQL);
					stmt2.executeUpdate(SQL);
				} else if (tpts1 < 0 && tpts2 > 0 && tpts3 < 0 && tpts4 > 0) {
					// higher low and higher high case
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET ATT = 8 WHERE SYMBOL ='"
							+ symbol + "' AND SEQINDEX=" + index4;
					System.out.println(SQL);
					stmt2.executeUpdate(SQL);
				}

			} else if (price1 > price3 && price2 > price4) {
				// lower high and lower low case
				if (tpts1 > 0 && tpts2 < 0 && tpts3 > 0 && tpts4 < 0) {
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET ATT = -8 WHERE SYMBOL ='"
							+ symbol + "' AND SEQINDEX=" + index4;
					System.out.println(SQL);
					stmt2.executeUpdate(SQL);
				} else if (tpts1 < 0 && tpts2 > 0 && tpts3 < 0 && tpts4 > 0) {
					// lower low and lower high case
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET ATT = -8 WHERE SYMBOL ='"
							+ symbol + "' AND SEQINDEX=" + index4;
					System.out.println(SQL);
					stmt2.executeUpdate(SQL);
				}
			}

			int days1 = findDaysBetween(symbol, index1, index2);
			int days2 = findDaysBetween(symbol, index2, index3);
			int days3 = findDaysBetween(symbol, index3, index4);

			if (days1 >= 50) {
				if ((tpts1 > 0 && tpts2 < 0) || (tpts1 < 0 && tpts2 < 0)) { // 50
																			// days
																			// straight
																			// down
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET ATT = -50 WHERE SYMBOL ='"
							+ symbol + "' AND SEQINDEX=" + index2;
					System.out.println(SQL);
					stmt2.executeUpdate(SQL);
				} else if ((tpts1 < 0 && tpts2 > 0) || (tpts1 > 0 && tpts2 > 0)) { // 50
																					// days
																					// straight
																					// up
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET ATT = 50 WHERE SYMBOL ='"
							+ symbol + "' AND SEQINDEX=" + index2;
					System.out.println(SQL);
					stmt2.executeUpdate(SQL);
				}

			} else if ((days1 + days2) >= 50) { // need check tpts values
				if (tpts2 < 0 && tpts3 < 0) { // 50 days straight down
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET ATT = -50 WHERE SYMBOL ='"
							+ symbol + "' AND SEQINDEX=" + index3;
					System.out.println(SQL);
					stmt2.executeUpdate(SQL);
				} else if (tpts2 > 0 && tpts3 > 0) { // 50 days straight up
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET ATT = 50 WHERE SYMBOL ='"
							+ symbol + "' AND SEQINDEX=" + index3;
					System.out.println(SQL);
					stmt2.executeUpdate(SQL);
				}

			} else if ((days1 + days2 + days3) >= 50) {// need check tpts values
				if (tpts2 < 0 && tpts3 < 0 && tpts4 < 0) { // 50 days straight
															// down
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET ATT = -50 WHERE SYMBOL ='"
							+ symbol + "' AND SEQINDEX=" + index4;
					System.out.println(SQL);
					stmt2.executeUpdate(SQL);
				} else if (tpts2 > 0 && tpts3 > 0 && tpts4 > 0) { // 50 days
																	// straight
																	// up
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET ATT = 50 WHERE SYMBOL ='"
							+ symbol + "' AND SEQINDEX=" + index4;
					System.out.println(SQL);
					stmt2.executeUpdate(SQL);
				}
			}

		} catch (Exception ex) {

			ex.printStackTrace(System.out);
		}

	}

	private static int findDaysBetween(String symbol, int lowIndex,
			int highIndex) {
		int days = 0;
		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt3 == null)
				stmt3 = con.createStatement();

			SQL = "SELECT COUNT(*) FROM BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ symbol
					+ "' AND SEQINDEX>"
					+ lowIndex
					+ " AND SEQINDEX<="
					+ highIndex;
			System.out.println(SQL);

			rs3 = stmt3.executeQuery(SQL);

			if (rs3.next()) {
				days = rs3.getInt(1);
			}

		} catch (Exception ex) {

			ex.printStackTrace(System.out);
		}

		return days;
	}

	public static void calculateLastPTS(String symbol, int seqIndex) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select SEQINDEX, AWS,FINALPRICE from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ symbol + "' AND  SEQINDEX=" + seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			int index = 0;
			float price = 0.0f;
			int aws = 0;

			if (rs1.next()) {
				index = rs1.getInt(1);
				aws = rs1.getInt(2);
				price = rs1.getFloat(3);

				SQL = "select SEQINDEX, AWS,FINALPRICE, PTS from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
						+ symbol
						+ "'AND  PTS<>0 AND  SEQINDEX<"
						+ seqIndex
						+ " AND SEQINDEX>"
						+ (seqIndex - backCount)
						+ " ORDER BY SEQINDEX DESC";

				rs2 = stmt2.executeQuery(SQL);

				if (rs2.next()) {
					int index2 = rs2.getInt(1);
					int aws2 = rs2.getInt(2);
					float price2 = rs2.getFloat(3);
					int pts2 = rs2.getInt(4);

					if (pts2 > 0 && aws <= 0) { // first aws negative
						int ptsVal = -10001;
						SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET PTS ="
								+ ptsVal + " WHERE SYMBOL ='" + symbol
								+ "' AND SEQINDEX=" + seqIndex;
						System.out.println(SQL);
						stmt3.executeUpdate(SQL);
					} else if (pts2 > 0 && aws > 0) { // need to compare
						if (price <= price2) {// simply add one more day
							int ptsVal = pts2 + 1;
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET PTS ="
									+ ptsVal + " WHERE SYMBOL ='" + symbol
									+ "' AND SEQINDEX=" + index2;
							System.out.println(SQL);
							stmt3.executeUpdate(SQL);
						} else if (price > price2) {// move the marker
							int ptsVal = pts2 + 1;
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET PTS ="
									+ ptsVal + " WHERE SYMBOL ='" + symbol
									+ "' AND SEQINDEX=" + seqIndex;
							System.out.println(SQL);
							stmt3.executeUpdate(SQL);
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET PTS = 0 WHERE SYMBOL ='"
									+ symbol + "' AND SEQINDEX=" + index2;
							System.out.println(SQL);
							stmt3.executeUpdate(SQL);
						}

					} else if (pts2 < 0 && aws > 0) { // first aws positive
						int ptsVal = 10001;
						SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET PTS ="
								+ ptsVal + " WHERE SYMBOL ='" + symbol
								+ "' AND SEQINDEX=" + seqIndex;
						System.out.println(SQL);
						stmt3.executeUpdate(SQL);
					} else if (pts2 < 0 && aws <= 0) { // need to compare
						if (price >= price2) {// simply add one more day
							int ptsVal = pts2 - 1;
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET PTS ="
									+ ptsVal + " WHERE SYMBOL ='" + symbol
									+ "' AND SEQINDEX=" + index2;
							System.out.println(SQL);
							stmt3.executeUpdate(SQL);
						} else if (price < price2) {// move the marker
							int ptsVal = pts2 - 1;
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET PTS ="
									+ ptsVal + " WHERE SYMBOL ='" + symbol
									+ "' AND SEQINDEX=" + seqIndex;
							System.out.println(SQL);
							stmt3.executeUpdate(SQL);
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET PTS = 0 WHERE SYMBOL ='"
									+ symbol + "' AND SEQINDEX=" + index2;
							System.out.println(SQL);
							stmt3.executeUpdate(SQL);
						}

					}
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	// NTES,
	public static void calculateSellingScores(String symbol, long maxPreIndex,
			boolean lastOnly) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018
			long tempIndex = 0;
			/*
			 * if (maxPreIndex > 0) { SQL =
			 * "select SEQINDEX,BBSCORE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
			 * + symbol +
			 * "' and (BBSCORE=-1 OR (BBSCORE>0 AND BBSCORE<1000)) AND SEQINDEX<="
			 * + maxPreIndex + " ORDER BY SEQINDEX DESC"; rs1 =
			 * stmt1.executeQuery(SQL);
			 * 
			 * if (rs1.next()) { tempIndex = rs1.getLong(1); }
			 * 
			 * }
			 */

			// 3/30/2018 changed to BBSCORE query to be more precise
			if (maxPreIndex > 0) {
				SQL = "select SEQINDEX,BBSCORE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and (BBSCORE=-1 OR (BBSCORE>0 AND BBSCORE<1000)) AND SEQINDEX<="
						+ maxPreIndex + " ORDER BY SEQINDEX DESC";
				rs1 = stmt1.executeQuery(SQL);

				if (rs1.next()) {
					tempIndex = rs1.getLong(1);
				}

			}
			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018

			/*
			 * if (!lastOnly) { SQL =
			 * "UPDATE BPMADMIN.INTRADAYSTOCKS SET SELLINGSCORE=0 WHERE SYMBOL ='"
			 * + symbol + "' AND SELLINGSCORE<>0"; stmt1.executeUpdate(SQL); }
			 */

			SQL = "select SEQINDEX, BBSCORE from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ symbol
					+ "' AND (BBSCORE=-1 OR (BBSCORE>0 AND BBSCORE<1000)) AND SEQINDEX>="
					+ tempIndex + " ORDER BY SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);

			int index1 = 0;
			int index2 = 0;
			int bbscore1 = 0;
			int bbscore2 = 0;
			int loop = 0;

			while (rs1.next()) {
				if (loop == 0) {
					index1 = rs1.getInt(1);
					bbscore1 = rs1.getInt(2);
					/*
					 * findSellingScoreBend(symbol, index1);
					 * findSellingScoreBetween(symbol, index1, seqIndex); if
					 * (lastOnly) { break; }
					 */
				} else if (loop >= 1) {
					index2 = rs1.getInt(1);
					bbscore2 = rs1.getInt(2);
					// findSellingScoreBend(symbol, index2);
					// findSellingScoreBetween(symbol, index2, index1);
					findSellingScoreBetween(symbol, index1, index2);
					index1 = index2;
					bbscore1 = bbscore2;

				}

				loop++;

			}
		} catch (Exception ex) {

		}

	}

	// NTES,
	public static void calculateSellingScoresNotGood(String symbol,
			long maxPreIndex, boolean lastOnly) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018
			long tempIndex = 0;
			/*
			 * if (maxPreIndex > 0) { SQL =
			 * "select SEQINDEX,BBSCORE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
			 * + symbol +
			 * "' and (BBSCORE=-1 OR (BBSCORE>0 AND BBSCORE<1000)) AND SEQINDEX<="
			 * + maxPreIndex + " ORDER BY SEQINDEX DESC"; rs1 =
			 * stmt1.executeQuery(SQL);
			 * 
			 * if (rs1.next()) { tempIndex = rs1.getLong(1); }
			 * 
			 * }
			 */

			// 3/3/2018 changed to Sellingscore query to be more precise
			if (maxPreIndex > 0) {
				SQL = "select SEQINDEX,BBSCORE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and SELLINGSCORE<>0 AND SEQINDEX<="
						+ maxPreIndex + " ORDER BY SEQINDEX DESC";
				rs1 = stmt1.executeQuery(SQL);

				if (rs1.next()) {
					tempIndex = rs1.getLong(1);
				}

			}
			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018

			/*
			 * if (!lastOnly) { SQL =
			 * "UPDATE BPMADMIN.INTRADAYSTOCKS SET SELLINGSCORE=0 WHERE SYMBOL ='"
			 * + symbol + "' AND SELLINGSCORE<>0"; stmt1.executeUpdate(SQL); }
			 */

			SQL = "select SEQINDEX, BBSCORE from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ symbol
					+ "' AND (BBSCORE=-1 OR (BBSCORE>0 AND BBSCORE<1000)) AND SEQINDEX>="
					+ tempIndex + " ORDER BY SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);

			int index1 = 0;
			int index2 = 0;
			int bbscore1 = 0;
			int bbscore2 = 0;
			int loop = 0;

			while (rs1.next()) {
				if (loop == 0) {
					index1 = rs1.getInt(1);
					bbscore1 = rs1.getInt(2);
					/*
					 * findSellingScoreBend(symbol, index1);
					 * findSellingScoreBetween(symbol, index1, seqIndex); if
					 * (lastOnly) { break; }
					 */
				} else if (loop >= 1) {
					index2 = rs1.getInt(1);
					bbscore2 = rs1.getInt(2);
					// findSellingScoreBend(symbol, index2);
					// findSellingScoreBetween(symbol, index2, index1);
					findSellingScoreBetween(symbol, index1, index2);
					index1 = index2;
					bbscore1 = bbscore2;

				}

				loop++;

			}
		} catch (Exception ex) {

		}

	}

	// seqIndex1 must have BBSCORE=-1 as the bear starting point
	private static void findSellingScoreBend(String symbol, int seqIndex1) {
		// System.out.println("Find between " + seqIndex1 + " " + seqIndex2);

		try {

			// seqIndex1 must have BBSCORE=-1 as the bear starting point

			SQL = "select SEQINDEX,BBDI,FFP,DS3PT,DCPT,DPS4,FINALPRICE,PTMA,BBSCORE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' AND  seqIndex<="
					+ seqIndex1
					+ " AND seqIndex>="
					+ (seqIndex1 - 20)
					+ " Order by seqIndex DESC";

			rs2 = stmt2.executeQuery(SQL);

			int tCount = 0;
			int cBBDI = 0;
			int p100Count = 0;
			int p200Count = 0;
			int cBBScore = 0;
			boolean updateNeeded = true;
			int totalScore = -1;

			while (rs2.next() && tCount <= 10) {
				if (tCount == 0) {
					cBBDI = rs2.getInt(2);
					cBBScore = rs2.getInt(9);
					if (cBBDI > -100) {
						if (cBBScore == 81) {
							updateNeeded = true;
							totalScore = 4;
						} else if (cBBScore == 881) {
							updateNeeded = true;
							totalScore = 5;
						} else if (cBBScore == 1) {
							updateNeeded = true;
							totalScore = 1;
						}
						break;
					}
				}
				tCount++;
				if (rs2.getInt(2) == 100) {
					p100Count++;
				}
				if (rs2.getInt(2) == 200) {
					p200Count++;
				}
			}

			if (p200Count >= 1 || p100Count >= 2) {
				totalScore = -5;

			} else if (p100Count == 1) {
				totalScore = -4;

			}

			String sql = "Update BPMADMIN.INTRADAYSTOCKS SET SELLINGSCORE="
					+ totalScore + " where symbol='" + symbol
					+ "' AND  seqIndex=" + seqIndex1;
			System.out.println(tCount + " " + sql);
			stmt3.executeUpdate(sql);

		} catch (Exception ex) {

		}
	}

	private static void findSellingScoreBetween(String symbol, int seqIndex1,
			int seqIndex2) {
		// BBDI AT SEQINDEX1 should be the first of less or equal than -100
		// after BBDI>=200
		// BBDI AT SEQINDEX2 should be the first of greater or equal than
		// 1(81,0r 881) but less than 1000 after BBDI<=-00

		System.out.println("Find between " + seqIndex1 + " " + seqIndex2);
		int totalScore = 0;
		try {

			SQL = "select SEQINDEX,BBDI,DS3PT,DCPT,DPS4,FINALPRICE,PTMA, SELLINGSCORE,BBSCORE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' AND  seqIndex>="
					+ seqIndex1
					+ " AND seqIndex<="
					+ seqIndex2
					+ " AND ( DS3PT<>0 OR BBDI<=-100 OR BBDI>=200 OR DCPT<>0) ORDER BY SEQINDEX ASC";

			if (seqIndex2 < 0) {
				SQL = "select SEQINDEX,BBDI,DS3PT,DCPT,DPS4,FINALPRICE,PTMA, SELLINGSCORE,BBSCORE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' AND  seqIndex>="
						+ seqIndex1
						+ " AND ( DS3PT<>0 OR BBDI<=-100 OR BBDI>=200 OR DCPT<>0) ORDER BY SEQINDEX ASC";

			}
			rs2 = stmt2.executeQuery(SQL);
			int loop = 0;
			boolean bullCase = false;
			boolean bearCase = false;
			boolean update = true;
			// almost unlimit
			int maxCount = 5000;

			while (rs2.next() && loop < maxCount) {
				int cIndex = rs2.getInt(1);
				int cBBDI = rs2.getInt(2);
				int cDs3pt = rs2.getInt(3);
				int cDcpt = rs2.getInt(4);
				int DPS4 = rs2.getInt(5);
				float cPtma = rs2.getFloat(7);
				int cSSScore = rs2.getInt(8);
				int cBBScore = rs2.getInt(9);
				update = true;
				// score card: cBBDI recorded as surface value
				// each dcpt or ds3pt minus score as -600 or 3 cBBDI(-200)
				// cBBScore -81 worth -800, cBBScore -881 worth -1000,
				if (loop == 0) {
					if (cSSScore == -5) {
						totalScore = totalScore - 1000;
						bearCase = true;
					} else if (cSSScore == -4) {
						totalScore = totalScore - 800;
						bearCase = true;
					} else if (cSSScore == 4) {
						totalScore = totalScore + 800;
						bullCase = true;
					} else if (cSSScore == 5) {
						totalScore = totalScore + 1000;
						bullCase = true;
					}
				}
				if (loop > 0 && cBBDI <= -100) {
					if (!bearCase && !bullCase) {
						bearCase = true;
					}
					if (bearCase || (cIndex < seqIndex2 && bullCase)) {
						totalScore = totalScore + cBBDI;
					}

				} else if (loop > 0 && cBBDI >= 200) {
					if (!bearCase && !bullCase) {
						bullCase = true;
					}
					if ((bearCase && cIndex < seqIndex2) || bullCase) {
						totalScore = totalScore + cBBDI;
					}
				}
				if (loop > 0 && cDs3pt < -1000) {
					if (!bearCase && !bullCase) {
						bearCase = true;
					}
					if (bearCase || (cIndex < seqIndex2 && bullCase)) {
						totalScore = totalScore - 600;
					}
				} else if (loop > 0 && cDs3pt > 1000) {
					if (!bearCase && !bullCase) {
						bullCase = true;
					}
					if ((bearCase && cIndex < seqIndex2) || bullCase) {
						totalScore = totalScore + 600;
					}
				}
				if (loop > 0 && cDcpt < -1000) {
					if (!bearCase && !bullCase) {
						bearCase = true;
					}
					if (bearCase || (cIndex < seqIndex2 && bullCase)) {
						totalScore = totalScore - 600; // --Dcpt is a gliding
														// value with the
														// latest, so skip this
					}
				} else if (loop > 0 && cDcpt > 1000) {
					if (!bearCase && !bullCase) {
						bullCase = true;
					}

					if ((bearCase && cIndex < seqIndex2) || bullCase) {
						totalScore = totalScore + 600; // --Dcpt is a gliding
														// value with the
														// latest, so skip this
					}

				}

				loop++;
				// or 30(best?) -200 continuously within 40(what is the best
				// value???) days

				if (cIndex == seqIndex2) {
					if (bullCase && cBBScore < 0) {
						update = false;
					} else if (bearCase && cBBScore > 0) {
						update = false;
					}
				}
				if ((totalScore <= -200 || totalScore >= 200)
						&& loop <= maxCount) {
					if (update) {
						String sql = "Update BPMADMIN.INTRADAYSTOCKS SET SELLINGSCORE="
								+ (totalScore / 200)
								+ " where symbol='"
								+ symbol + "' AND  seqIndex=" + cIndex;

						System.out.println(sql);
						stmt3.executeUpdate(sql);
					}
				}

			}
		} catch (Exception ex) {

		}
	}

	public static void calculateBBScoreOld(String symbol, int seqIndex,
			boolean lastOnly) {
		int days = 50;
		int vDays = 10;

		// rules:
		// BABA,AMZN,FB,GRUB,GOOG,ACH,DQ,PCLN,SHOP,BZUN,WB,TSLA,NVDA,NOC,MCD,
		// BA,YRD,CTRP,JOBS,BITA,JD,MOMO,NMM,PBYI,YY,CLVS,V,NFLX,AAL,MSFT,BAC,
		// NEM,ABX,MTL,AMD,HTHT,JD,DB,VRTX,CSX,NRG,LRCX,IDXX(PRCEEDING -, LONG
		// WAITING),WYNN, EGRX,ALXN,BIIB,VRX,
		// 1. (>=7 POSITIVE) 10 DAYS HAS >=2 DAYS FFP>0, EACH FFP>0 AND
		// FFP<1000 ADD 1 POINT
		// EACH PS3PT 10000 ADD 3 POINTS, EACH DCPT 20000 ADD 3 POINTS,
		// 2. BEAR TYPE, CHANGE ABOVE TO OPPOSITE
		// RATHER THAN LONG TERM BEAR, THE SUM PONITS<-7 USUALLY INDICATING
		// OVERSOLD REACTION, NEAR TERM QUICK BOUND INDICATED, ESPECIALLY FOR
		// LARGE 'STABLE' CAP STOCKS, LIKE VZ, T, IBM ETC
		// .BIG STABLE CAP COMPANY >4 <-10000 WITH 10 DAYS, TEMP BOTTOM,
		// SHORT TERM UP, EXAMPLE, VZ, T, IBM, TM, ATVI, WAIT DS3PER>0 BUY

		// 3. LONG TERM BEAR INDICATOR: SUM(DS3PER) OVER 10??(12,13,15??) DAY
		// PEROID
		// CONTINOUS NEGATIVE ABOVE 20(??) DAYS INDICATING BEAR??
		// CMG,GPRO,UA,DDD,AMBA,SQM,TWTR,

		// 4. -- ++ TYPE, WYNN???
		// 5. ++ -- TYPE JD

		// 6. -- ++ -- ++ DQ, TM???
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select SEQINDEX,BBDI,FFP,DS3PT,DCPT,DPS4,FINALPRICE,PTMA from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' AND  seqIndex<="
					+ seqIndex
					+ " ORDER BY SEQINDEX DESC";

			if (seqIndex < 0) {
				SQL = "select SEQINDEX,BBDI,FFP,DS3PT,DCPT,DPS4,FINALPRICE,PTMA from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol + "'  ORDER BY SEQINDEX DESC";
			}

			if (lastOnly)
				SQL = "select SEQINDEX,BBDI,FFP,DS3PT,DCPT,DPS4,FINALPRICE,PTMA from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' AND  seqIndex<="
						+ seqIndex
						+ " and seqIndex>"
						+ (seqIndex - 80)
						+ " ORDER BY SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);

			int[] index = new int[days];
			int[] bbdi = new int[days];
			int[] ffp = new int[days];
			int[] ds3pt = new int[days];
			int[] dcpt = new int[days];
			int[] dps4 = new int[days];
			float[] prices = new float[days];
			float[] ptmas = new float[days];

			int loopCount = 0;

			while (rs1.next()) {
				if (loopCount < days) {
					index[loopCount] = rs1.getInt(1);
					bbdi[loopCount] = rs1.getInt(2);
					ffp[loopCount] = rs1.getInt(3);
					ds3pt[loopCount] = rs1.getInt(4);
					dcpt[loopCount] = rs1.getInt(5);
					dps4[loopCount] = rs1.getInt(6);
					prices[loopCount] = rs1.getFloat(7);
					ptmas[loopCount] = rs1.getFloat(8);
				} else {
					int totalscores = 0;

					// rule 1, vDay sharp turn around case
					// 10 trading days, BBDI change from -100
					// best -200 to 200 above, and price increase
					// at least 10%, then sharp turn around
					// not necessary a long term bull indicator
					// but at least short term days to weeks bull
					// potential, nothing is 100% ;)

					int last200Index = 0;
					float last200Price = 0.0f;
					int last_200Index1 = 0;
					float last_200Price1 = 0.0f;
					int last_200Index2 = 0;
					float last_200Price2 = 0.0f;
					boolean _100Found = false;
					boolean _200Found = false;
					boolean Found200 = false;
					int bbdi1 = 0;
					int bbdi2 = 0;

					for (int w = 0; w < vDays; w++) {
						if (bbdi[0] >= 200 && bbdi[1] <= 0) {

						} else {
							break;
						}

						if (bbdi[w] >= 200) {
							last200Index = index[w];
							last200Price = prices[w];
							Found200 = true;
						}

						if (bbdi[w] <= -200) {
							_200Found = true;
							if (last_200Index1 == 0) {
								last_200Index1 = index[w];
								last_200Price1 = prices[w];
								bbdi1 = bbdi[w];

							} else if (last_200Index2 == 0) {
								last_200Index2 = index[w];
								last_200Price2 = prices[w];
								bbdi2 = bbdi[w];

							}
						} else if (bbdi[w] <= -100) {
							_100Found = true;
							if (last_200Index1 == 0) {
								last_200Index1 = index[w];
								last_200Price1 = prices[w];
								bbdi1 = bbdi[w];

							} else if (last_200Index2 == 0) {
								last_200Index2 = index[w];
								last_200Price2 = prices[w];
								bbdi2 = bbdi[w];

							}
						}
					}

					if ((_100Found || _200Found) && Found200) {
						if (last200Index > last_200Index1
								&& last200Index > last_200Index2) {
							if ((last_200Price1 * 1.1f) < last200Price) {
								if (bbdi1 == -200 || bbdi2 == -200
										|| (bbdi1 + bbdi2) == -200) {
									totalscores = 888;
								} else {
									totalscores = 88;
								}

								String sqlUpdate = "Update BPMADMIN.INTRADAYSTOCKS  SET BBSCORE = "
										+ totalscores
										+ " WHERE SEQINDEX = "
										+ last200Index
										+ " AND SYMBOL='"
										+ symbol + "'";
								System.out.println(sqlUpdate);
								// stmt2.executeUpdate(sqlUpdate);

							}
						}
					}

					if (lastOnly) {
						break;
					}
					// rules

					for (int k = 0; k < days - 1; k++) {
						index[k] = index[k + 1];
						bbdi[k] = bbdi[k + 1];
						ffp[k] = ffp[k + 1];
						ds3pt[k] = ds3pt[k + 1];
						dcpt[k] = dcpt[k + 1];
						dps4[k] = dps4[k + 1];
						prices[k] = prices[k + 1];
						ptmas[k] = ptmas[k + 1];
					}
					index[days - 1] = rs1.getInt(1);
					bbdi[days - 1] = rs1.getInt(2);
					ffp[days - 1] = rs1.getInt(3);
					ds3pt[days - 1] = rs1.getInt(4);
					dcpt[days - 1] = rs1.getInt(5);
					dps4[days - 1] = rs1.getInt(6);
					prices[days - 1] = rs1.getFloat(7);
					ptmas[days - 1] = rs1.getFloat(8);
				}
				loopCount++;
			}

			// set up a tag
			/*
			 * String sqlUpdate =
			 * "Update BPMADMIN.INTRADAYSTOCKS  SET DCINCR =1000 " +
			 * " WHERE SEQINDEX = " + seqIndex + " AND SYMBOL='" + symbol + "'";
			 * 
			 * stmt2.executeUpdate(sqlUpdate);
			 */
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	public static void scoreDipOp(String symbol, long maxPreIndex,
			boolean lastOnly) {
		int days = 50;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select SEQINDEX,SATCOUNT,DSI5,DSI,DSI3, DCPERCENT,DSPERCENT, DS3PER,DS4PER,DS15PER,DELTA5,CURRENTDATE,FINALPRICE,PTMA from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' AND  seqIndex>"
					+ (maxPreIndex - backCountNeeded / 5)
					+ " ORDER BY SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);

			int[] indexes = new int[days];
			int[] sats = new int[days];
			float[] dsi5s = new float[days];
			float[] dsi24s = new float[days];
			float[] dsisum = new float[days];
			float[] dc = new float[days];
			float[] ds = new float[days];
			float[] ds3per = new float[days];
			float[] ds4per = new float[days];
			float[] ds15per = new float[days];
			float[] dds15 = new float[days];
			String[] dates = new String[days];
			float[] prices = new float[days];
			float[] ptmas = new float[days];

			int loopCount = 0;

			while (rs1.next()) {
				if (loopCount < days) {
					indexes[loopCount] = rs1.getInt(1);
					sats[loopCount] = rs1.getInt(2);
					dsi5s[loopCount] = rs1.getFloat(3);
					dsi24s[loopCount] = rs1.getFloat(4);
					dsisum[loopCount] = rs1.getFloat(5);
					dc[loopCount] = rs1.getFloat(6);
					ds[loopCount] = rs1.getFloat(7);
					ds3per[loopCount] = rs1.getFloat(8);
					ds4per[loopCount] = rs1.getFloat(9);
					ds15per[loopCount] = rs1.getFloat(10);
					dds15[loopCount] = rs1.getFloat(11);
					dates[loopCount] = rs1.getString(12);
					prices[loopCount] = rs1.getFloat(13);
					ptmas[loopCount] = rs1.getFloat(14);
				} else {
					int totalscores = 0;

					// rules
					// new logic --
					// ADD A FEW REULS
					// 17. THE NEXT 5 DAYS AFTER PTMA TURNS POSITIVTIVE FROM
					// NEGATIVE (OR LESS THAN 1)
					// EACH DAY ADD 1 POINTS TO THE DIPSCORE, DISCA
					// 18. (DS3PER-10)%10, (DS4PER-10)%10, ADD 1 PONIT EACH
					// FOR EACH MODULE LEFT SCORE
					// RESOLVE BABA, NVDA
					// ADD A FEW RULES,

					// ********* STATISTICS
					/*
					 * INTRADAY DATA -- TOTAL COUNT 17273646, DS3PER>1 11.49%,
					 * COUNT 1985996, DS3PER>0.8 14.02%, COUNT 2421819;
					 * DS3PER>0.6 17.43%, COUNT 3008349, -- TOTAL COUNT
					 * 17273646, DS4PER>1 13.51%, COUNT 2334894, DS4PER>0.8
					 * 16.20%, COUNT 2421819; DS4PER>0.6 19.72%, COUNT 3407442,
					 * 
					 * -- INTERDAY DAYA -- DS3PER>10 COUNT 2931640, total
					 * 15731000 count 18.63%; DS3PER>12 COUNT 2441165, total
					 * 15731000 count 15.5% -- DS4PER>10 COUNT 3435387, total
					 * 15731000 count 21.84%; DS4PER>12 COUNT 2927843, total
					 * 15731000 count 18.6%
					 * 
					 * SO WE PICK DS3PER 0.6 (INTRADAY) AS 10 (INTERDAY), 0.8 AS
					 * 12
					 */

					// ********** STATISTICS
					boolean lessThan1Found = false;
					for (int w = 0; w < 6; w++) {
						if (w == 0) {
							int score3 = (int) ((ds3per[w] - 0.6) / 0.6);
							if (score3 > 0) {
								totalscores = totalscores + score3;
							}

							int score4 = (int) ((ds4per[w] - 0.6) / 0.6);
							if (score4 > 0) {
								totalscores = totalscores + score4;
							}
						} else if (ptmas[w] < 0.05f && ptmas[0] > 0) {
							/*
							 * ---INTERDAY: PTMA>0.00000000001 AND PTMA<1.0,
							 * 2.62%, COUNT 411619 OF 15731000 -- INTRADAY:
							 * PTMA>0.00000000001 AND PTMA<0.06, 3.24%, COUNT
							 * 559363 OF 17273646 -- INTRADAY:
							 * PTMA>0.00000000001 AND PTMA<0.05, 2.72%, COUNT
							 * 477183 OF 17273646
							 */
							lessThan1Found = true;
						}

					}
					if (lessThan1Found) {
						totalscores = totalscores + 1;
					}
					// new rule 17 ,18
					// new logic --
					// rule 1, IN LAST 2 DAYS,
					// EACH 1 POINT FOR DSISUM IS GREATER THAN 200

					/**
					 * -- INTERDAY DSI3>200 COUNT 3045933, total 15731000 count
					 * 19.36%; -- INTERDAY DSI3>210 COUNT 2573164, total
					 * 15731000 count 16.36%; -- INTERDAY DSI3>190 COUNT
					 * 3524044, total 15731000 count 22.40%;
					 * 
					 * --INTRADAY: DSI3>150 COUNT 3749239, total 17273646 count
					 * 23.83%; --INTRADAY: DSI3>160 COUNT 3172466, total
					 * 17273646 count 18.36%; --INTRADAY: DSI3>155 COUNT
					 * 3452039, total 17273646 count 19.98%;
					 */
					for (int i = 0; i < 2; i++) {
						// if (dsisum[i] > 200) {
						// if (dsisum[i] > 150) { //1.4x of average dsi3, so 150
						// instead of 200
						if (dsisum[i] > 155) { // USE STATISTIC FINE TUNE TO 155
							totalscores = totalscores + 1;
						}
					}
					// EACH 1 POINT FOR DSISUM IS GREATER THAN 200

					// rule 2,3,4,5,6,7
					boolean allDsiSumGT100 = true;
					boolean allDSGTDC = true;
					boolean ds3perGT0 = true;
					boolean ds4perGT0 = true;
					for (int i = 0; i < 4; i++) {
						if (ds[i] < 0) { // rule 2. each ds<0, total score minus
											// 1
							totalscores = totalscores - 1;
						}
						if (dc[i] < 0) { // rule 3. each dc<0, total score minus
											// 1
							totalscores = totalscores - 1;
						}
						// if (dsisum[i] < 100) { // rule 4. all dsisum>100,
						// total
						// score add 2
						if (dsisum[i] < 70) { // 0.7x of average dsi3, so 70
												// instead of 100
							/*
							 * MATCHING STATISTIC DATA -- INTERDAY DSI3<100
							 * COUNT 7512822, total 15731000 count 44.75%; --
							 * INTERDAY DSI3<110 COUNT 8043532, total 15731000
							 * count 51.13%; -- INTERDAY DSI3<90 COUNT 6992401,
							 * total 15731000 count 44.49%;
							 * 
							 * --INTRADAY: DSI3<70 COUNT 7703667, total 17273646
							 * count 44.59%; --INTRADAY: DSI3<77 COUNT 3172466,
							 * total 17273646 count 52.17%; --INTRADAY: DSI3<63
							 * COUNT 7194946, total 17273646 count 41.65%%;
							 */
							allDsiSumGT100 = false;
						}

						if (ds[i] < dc[i]) { // rule 5. all ds > dc, total score
												// add 2
							allDSGTDC = false;
						}
						// rule 6. each ds3per >previous ds3per or>12, add 1
						// need to calculate ds3per[i] > 12 average to determine
						// value
						if (ds3per[i] > ds3per[i + 1] || ds3per[i] > 0.8) {
							totalscores = totalscores + 1;
						}
						if (ds3per[i] < 0) {
							ds3perGT0 = false;
						}
						if (ds4per[i] < 0) {
							ds4perGT0 = false;
						}
						// rule 7. each ds4per >previous ds4per or>12, add 1
						// need to calculate ds4per[i] > 12 average to determine
						// value
						if (ds4per[i] > ds4per[i + 1] || ds4per[i] > 0.8) {
							totalscores = totalscores + 1;
						}
					}

					// rule 4. all dsisum>100, total score add 2
					if (allDsiSumGT100) {
						totalscores = totalscores + 2;
					}

					// rule 5. all ds>dc, total score add 2
					if (allDSGTDC) {
						totalscores = totalscores + 2;
					}
					// rule 2,3,4,5,6,7

					// add day 5 to 10, each day ds3>12 or ds4>12 , add one
					for (int i = 4; i < 10; i++) {
						if (ds3perGT0 && ds3per[i] > 0.8) {
							totalscores = totalscores + 1;
						}

						if (ds3per[i] < 0) {
							ds3perGT0 = false;
						}

						if (ds4perGT0 && ds4per[i] > 0.8) {
							totalscores = totalscores + 1;
						}

						if (ds4per[i] < 0) {
							ds3perGT0 = false;
						}

					}

					// add day 5 to 10, each day ds3>12 or ds4>12 , add one

					// rule 8, ds15per<0 today, minus 2 from score
					if (ds15per[0] < 0 && symbol.equalsIgnoreCase("SPY")) {
						totalscores = totalscores - 4;
					} else if (ds15per[0] < 0
							&& !symbol.equalsIgnoreCase("SPY")) {
						totalscores = totalscores - 2;
					}

					// rule 9, dds15<0 today, minus 2 from score
					if (dds15[0] < 0) {
						totalscores = totalscores - 2;
					}

					boolean dsi5pos = false;
					boolean dsi5transdone = false;
					boolean dsi24pos = false;
					boolean dsi24transdone = false;
					boolean dds15pos = false;
					boolean dds15transdone = false;
					boolean ds15perpos = false;
					boolean ds15pertransdone = false;
					boolean ds3perpos = false;
					boolean ds3pertransdone = false;
					boolean ds4perpos = false;
					boolean ds4pertransdone = false;
					boolean satpos = false;
					boolean sattransdone = false;
					int maxSatPos = 0;
					int maxSatNeg = 0;
					int[] sat6 = new int[6];
					for (int i = 0; i < 24; i++) {
						if (!dsi5transdone) {
							if (dsi5s[i] > 0) {
								dsi5pos = true;
							}
							if (dsi5s[i] < 0 && dsi5pos) { // rule 10, dsi5
															// trans from - to
															// +, add 1
								totalscores = totalscores + 1;
								dsi5transdone = true;
							}
						}
						if (!dsi24transdone) {
							if (dsi24s[i] > 0) {
								dsi24pos = true;
							}
							if (dsi24s[i] < 0 && dsi24pos) { // rule 11, dsi24
																// trans from -
																// to +, add 1
								totalscores = totalscores + 1;
								dsi24transdone = true;
							}
						}
						if (!dds15transdone) {
							if (dds15[i] > 0) {
								dds15pos = true;
							}
							if (dds15[i] < 0 && dds15pos) { // rule 12, dds15
															// trans from - to
															// +, add 2
								totalscores = totalscores + 2;
								dds15transdone = true;
							}
						}
						if (!ds15pertransdone) {
							if (ds15per[i] > 0) {
								ds15perpos = true;
							}
							if (ds15per[i] < 0 && ds15perpos) { // rule 13,
																// ds15per trans
																// from - to +,
																// add 2
								if (symbol.equals("SPY")) {
									totalscores = totalscores + 4;
								} else {
									totalscores = totalscores + 2;
								}
								ds15pertransdone = true;
							}
						}

						if (!ds3pertransdone) {
							if (ds3per[i] > 0.8) {
								ds3perpos = true;
							}
							if (ds3per[i] < 0 && ds3perpos) { // rule 14, ds3per
																// trans from -
																// to >12, add 2
								totalscores = totalscores + 2;
								ds3pertransdone = true;
							}
						}

						if (!ds4pertransdone) {
							if (ds4per[i] > 0.8) {
								ds4perpos = true;
							}
							if (ds4per[i] < 0 && ds4perpos) { // rule 15, ds4per
																// trans from -
																// to >12, add 2
								totalscores = totalscores + 2;
								ds4pertransdone = true;
							}
						}

						if (!sattransdone) {
							if (sats[i] > 0) {
								satpos = true;
							}
							if (sats[i] < 0 && satpos) { // rule 16, sat trans
															// from - to +, add
															// 2
								totalscores = totalscores + 2;
								sattransdone = true;
							}
						}

						// rule 19 add max SATCOUNT POSITIVE/NEG within 6 days
						// to total score
						if (i < 6) {
							sat6[i] = sats[i];
						} else {
							int tempNegMax = 0;
							int tempPosMax = 0;
							for (int w = 0; w < 6; w++) {
								if (sat6[w] > 0) {
									tempPosMax++;
								} else if (sat6[w] < 0) {
									tempNegMax++;
								}
							}

							if (tempNegMax > maxSatNeg) {
								maxSatNeg = tempNegMax;
							}
							if (tempPosMax > maxSatPos) {
								maxSatPos = tempPosMax;
							}

							for (int w = 0; w < 5; w++) {
								sat6[w] = sat6[w + 1];
							}
							sat6[5] = sats[i];
						}
					}

					totalscores = totalscores + maxSatPos;
					totalscores = totalscores + maxSatNeg;

					if (stmt2 == null)
						stmt2 = con.createStatement();

					System.out.println(symbol + " at " + indexes[0] + " "
							+ dates[0] + " price: " + prices[0]
							+ " Total score " + totalscores);
					if (totalscores > 1024)
						totalscores = 1024;

					String sqlUpdate = "Update BPMADMIN.INTRADAYSTOCKS  SET DIPSCORE = "
							+ totalscores
							+ " WHERE SEQINDEX = "
							+ indexes[0]
							+ " AND SYMBOL='" + symbol + "'";

					// test mode code
					/*
					 * String sqlUpdate =
					 * "Update BPMADMIN.INTRADAYSTOCKS  SET BBGO = " +
					 * totalscores + " WHERE SEQINDEX = " + indexes[0] +
					 * " AND SYMBOL='" + symbol + "'"; //
					 */
					// System.out.println(sqlUpdate);
					stmt2.executeUpdate(sqlUpdate);

					if (lastOnly) {
						break;
					}
					// rules

					for (int k = 0; k < days - 1; k++) {
						indexes[k] = indexes[k + 1];
						sats[k] = sats[k + 1];
						dsi5s[k] = dsi5s[k + 1];
						dsi24s[k] = dsi24s[k + 1];
						dsisum[k] = dsisum[k + 1];
						dc[k] = dc[k + 1];
						ds[k] = ds[k + 1];
						ds3per[k] = ds3per[k + 1];
						ds4per[k] = ds4per[k + 1];
						ds15per[k] = ds15per[k + 1];
						dds15[k] = dds15[k + 1];
						dates[k] = dates[k + 1];
						prices[k] = prices[k + 1];
						ptmas[k] = ptmas[k + 1];
						;
					}
					indexes[days - 1] = rs1.getInt(1);
					sats[days - 1] = rs1.getInt(2);
					dsi5s[days - 1] = rs1.getFloat(3);
					dsi24s[days - 1] = rs1.getFloat(4);
					dsisum[days - 1] = rs1.getFloat(5);
					dc[days - 1] = rs1.getFloat(6);
					ds[days - 1] = rs1.getFloat(7);
					ds3per[days - 1] = rs1.getFloat(8);
					ds4per[days - 1] = rs1.getFloat(9);
					ds15per[days - 1] = rs1.getFloat(10);
					dds15[days - 1] = rs1.getFloat(11);
					dates[days - 1] = rs1.getString(12);
					prices[days - 1] = rs1.getFloat(13);
					ptmas[days - 1] = rs1.getFloat(14);
				}
				loopCount++;
			}

			// set up a tag
			/*
			 * String sqlUpdate =
			 * "Update BPMADMIN.INTRADAYSTOCKS  SET DCINCR =1000 " +
			 * " WHERE SEQINDEX = " + seqIndex + " AND SYMBOL='" + symbol + "'";
			 * 
			 * stmt2.executeUpdate(sqlUpdate);
			 */
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	// add more negative to detec UA< GPRO<CMG like stocks, not working
	// as it impacts bull score, rather use other approach
	public static void scoreDipOpTest1(String symbol, int seqIndex,
			boolean lastOnly) {
		int days = 50;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select SEQINDEX,SATCOUNT,DSI5,DSI,DSI3, DCPERCENT,DSPERCENT, DS3PER,DS4PER,DS15PER,DELTA5,CURRENTDATE,FINALPRICE,PTMA from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' AND  seqIndex<="
					+ seqIndex
					+ " ORDER BY SEQINDEX DESC";

			if (seqIndex < 0) {
				SQL = "select SEQINDEX,SATCOUNT,DSI5,DSI,DSI3, DCPERCENT,DSPERCENT, DS3PER,DS4PER,DS15PER,DELTA5,CURRENTDATE,FINALPRICE,PTMA from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol + "'  ORDER BY SEQINDEX DESC";
			}

			if (lastOnly)
				SQL = "select SEQINDEX,SATCOUNT,DSI5,DSI,DSI3, DCPERCENT,DSPERCENT, DS3PER,DS4PER,DS15PER,DELTA5,CURRENTDATE,FINALPRICE,PTMA from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' AND  seqIndex<="
						+ seqIndex
						+ " and seqIndex>"
						+ (seqIndex - 80)
						+ " ORDER BY SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);

			int[] indexes = new int[days];
			int[] sats = new int[days];
			float[] dsi5s = new float[days];
			float[] dsi24s = new float[days];
			float[] dsisum = new float[days];
			float[] dc = new float[days];
			float[] ds = new float[days];
			float[] ds3per = new float[days];
			float[] ds4per = new float[days];
			float[] ds15per = new float[days];
			float[] dds15 = new float[days];
			String[] dates = new String[days];
			float[] prices = new float[days];
			float[] ptmas = new float[days];

			int loopCount = 0;

			while (rs1.next()) {
				if (loopCount < days) {
					indexes[loopCount] = rs1.getInt(1);
					sats[loopCount] = rs1.getInt(2);
					dsi5s[loopCount] = rs1.getFloat(3);
					dsi24s[loopCount] = rs1.getFloat(4);
					dsisum[loopCount] = rs1.getFloat(5);
					dc[loopCount] = rs1.getFloat(6);
					ds[loopCount] = rs1.getFloat(7);
					ds3per[loopCount] = rs1.getFloat(8);
					ds4per[loopCount] = rs1.getFloat(9);
					ds15per[loopCount] = rs1.getFloat(10);
					dds15[loopCount] = rs1.getFloat(11);
					dates[loopCount] = rs1.getString(12);
					prices[loopCount] = rs1.getFloat(13);
					ptmas[loopCount] = rs1.getFloat(14);
				} else {
					int totalscores = 0;

					// rules
					// new logic --
					// ADD A FEW REULS
					// 17. THE NEXT 5 DAYS AFTER PTMA TURNS POSITIVTIVE FROM
					// NEGATIVE (OR LESS THAN 1)
					// EACH DAY ADD 1 POINTS TO THE DIPSCORE, DISCA
					// 18. (DS3PER-10)%10, (DS4PER-10)%10, ADD 1 PONIT EACH
					// FOR EACH MODULE LEFT SCORE
					// RESOLVE BABA, NVDA
					// ADD A FEW RULES,
					boolean lessThan1Found = false;
					for (int w = 0; w < 6; w++) {
						if (w == 0) {
							if (ds3per[w] > 0) {
								int score3 = ((int) (ds3per[w] - 10)) / 10;
								if (score3 > 0) {
									totalscores = totalscores + score3;
								}
							} else if (ds3per[w] < 0) {
								int score3 = ((int) ds3per[w]) / 10;
								totalscores = totalscores + score3;
							}

							if (ds4per[w] > 0) {
								int score4 = ((int) (ds4per[w] - 10)) / 10;
								if (score4 > 0) {
									totalscores = totalscores + score4;
								}
							} else if (ds4per[w] < 0) {
								int score4 = ((int) ds4per[w]) / 10;
								totalscores = totalscores + score4;
							}
						} else if (ptmas[w] < 1.0f && ptmas[0] > 0) {
							lessThan1Found = true;
						}

					}
					if (lessThan1Found) {
						totalscores = totalscores + 1;
					}
					// new rule 17 ,18
					// new logic --
					// rule 1, IN LAST 2 DAYS,
					// EACH 1 POINT FOR DSISUM IS GREATER THAN 200

					for (int i = 0; i < 2; i++) {
						if (dsisum[i] > 200) {
							totalscores = totalscores + 1;
						}

						if (dsisum[i] < -100) {
							totalscores = totalscores - 1;
						}
					}
					// EACH 1 POINT FOR DSISUM IS GREATER THAN 200

					// rule 2,3,4,5,6,7
					boolean allDsiSumGT100 = true;
					boolean allDsiSumLT_100 = true;
					boolean allDSGTDC = true;
					boolean ds3perGT0 = true;
					boolean ds4perGT0 = true;
					for (int i = 0; i < 4; i++) {
						if (ds[i] < 0) { // rule 2. each ds<0, total score minus
											// 1
							totalscores = totalscores - 1;
						}
						if (dc[i] < 0) { // rule 3. each dc<0, total score minus
											// 1
							totalscores = totalscores - 1;
						}
						if (dsisum[i] < 100) { // rule 4. all dsisum>100, total
												// score add 2
							allDsiSumGT100 = false;
						}

						if (dsisum[i] < -100) { // rule 4. all dsisum>100, total
							// score add 2
							allDsiSumLT_100 = false;
						}

						if (ds[i] < dc[i]) { // rule 5. all ds > dc, total score
												// add 2
							allDSGTDC = false;
						}
						// rule 6. each ds3per >previous ds3per or>12, add 1
						if (ds3per[i] > ds3per[i + 1] || ds3per[i] > 12) {
							totalscores = totalscores + 1;
						}
						if (ds3per[i] < 0) {
							ds3perGT0 = false;
						}
						if (ds4per[i] < 0) {
							ds4perGT0 = false;
						}
						// rule 7. each ds4per >previous ds4per or>12, add 1
						if (ds4per[i] > ds4per[i + 1] || ds4per[i] > 12) {
							totalscores = totalscores + 1;
						}
					}

					// rule 4. all dsisum>100, total score add 2
					if (allDsiSumGT100) {
						totalscores = totalscores + 2;
					}
					if (allDsiSumLT_100) {
						totalscores = totalscores - 2;
					}

					// rule 5. all ds>dc, total score add 2
					if (allDSGTDC) {
						totalscores = totalscores + 2;
					}
					// rule 2,3,4,5,6,7

					// add day 5 to 10, each day ds3>12 or ds4>12 , add one
					for (int i = 4; i < 10; i++) {
						if (ds3perGT0 && ds3per[i] > 12) {
							totalscores = totalscores + 1;
						}

						if (ds3per[i] < 0) {
							ds3perGT0 = false;
						}

						if (ds4perGT0 && ds4per[i] > 12) {
							totalscores = totalscores + 1;
						}

						if (ds4per[i] < 0) {
							ds3perGT0 = false;
						}

					}

					// add day 5 to 10, each day ds3>12 or ds4>12 , add one

					// rule 8, ds15per<0 today, minus 2 from score
					if (ds15per[0] < 0 && symbol.equalsIgnoreCase("SPY")) {
						totalscores = totalscores - 4;
					} else if (ds15per[0] < 0
							&& !symbol.equalsIgnoreCase("SPY")) {
						totalscores = totalscores - 2;
					}

					// rule 9, dds15<0 today, minus 2 from score
					if (dds15[0] < 0) {
						totalscores = totalscores - 2;
					}

					boolean dsi5pos = false;
					boolean dsi5transdone = false;
					boolean dsi5pos1 = false;
					boolean dsi5transdone1 = false;
					boolean dsi24pos = false;
					boolean dsi24transdone = false;
					boolean dsi24pos1 = false;
					boolean dsi24transdone1 = false;
					boolean dds15pos = false;
					boolean dds15transdone = false;
					boolean dds15pos1 = false;
					boolean dds15transdone1 = false;
					boolean ds15perpos = false;
					boolean ds15pertransdone = false;
					boolean ds15perpos1 = false;
					boolean ds15pertransdone1 = false;
					boolean ds3perpos = false;
					boolean ds3pertransdone = false;
					boolean ds3perpos1 = false;
					boolean ds3pertransdone1 = false;
					boolean ds4perpos = false;
					boolean ds4pertransdone = false;
					boolean ds4perpos1 = false;
					boolean ds4pertransdone1 = false;
					boolean satpos = false;
					boolean sattransdone = false;
					boolean satpos1 = false;
					boolean sattransdone1 = false;
					int maxSatPos = 0;
					int maxSatNeg = 0;
					int[] sat6 = new int[6];
					for (int i = 0; i < 24; i++) {
						if (!dsi5transdone) {
							if (dsi5s[i] > 0) {
								dsi5pos = true;
							}
							if (dsi5s[i] < 0 && dsi5pos) { // rule 10, dsi5
															// trans from - to
															// +, add 1
								totalscores = totalscores + 1;
								dsi5transdone = true;
							}
						}

						if (!dsi5transdone1) {
							if (dsi5s[i] < 0) {
								dsi5pos1 = true;
							}
							if (dsi5s[i] > 0 && dsi5pos1) { // rule 10, dsi5
															// trans from + to
															// -, MINUS 1
								totalscores = totalscores - 1;
								dsi5transdone1 = true;
							}
						}

						if (!dsi24transdone) {
							if (dsi24s[i] > 0) {
								dsi24pos = true;
							}
							if (dsi24s[i] < 0 && dsi24pos) { // rule 11, dsi24
																// trans from -
																// to +, add 1
								totalscores = totalscores + 1;
								dsi24transdone = true;
							}
						}

						if (!dsi24transdone1) {
							if (dsi24s[i] < 0) {
								dsi24pos1 = true;
							}
							if (dsi24s[i] > 0 && dsi24pos1) { // rule 11, dsi24
																// trans from +
																// to -, MINUS 1
								totalscores = totalscores - 1;
								dsi24transdone1 = true;
							}
						}

						if (!dds15transdone) {
							if (dds15[i] > 0) {
								dds15pos = true;
							}
							if (dds15[i] < 0 && dds15pos) { // rule 12, dds15
															// trans from - to
															// +, add 2
								totalscores = totalscores + 2;
								dds15transdone = true;
							}
						}

						if (!dds15transdone1) {
							if (dds15[i] < 0) {
								dds15pos1 = true;
							}
							if (dds15[i] > 0 && dds15pos1) { // rule 12, dds15
																// trans from +
																// to
																// -, MINUS 2
								totalscores = totalscores - 2;
								dds15transdone1 = true;
							}
						}

						if (!ds15pertransdone) {
							if (ds15per[i] > 0) {
								ds15perpos = true;
							}
							if (ds15per[i] < 0 && ds15perpos) { // rule 13,
																// ds15per trans
																// from - to +,
																// add 2
								if (symbol.equals("SPY")) {
									totalscores = totalscores + 4;
								} else {
									totalscores = totalscores + 2;
								}
								ds15pertransdone = true;
							}
						}

						if (!ds15pertransdone1) {
							if (ds15per[i] < 0) {
								ds15perpos1 = true;
							}
							if (ds15per[i] < 0 && ds15perpos1) { // rule 13,
																	// ds15per
																	// trans
																	// from + to
																	// -,
																	// MINUS 2
								if (symbol.equals("SPY")) {
									totalscores = totalscores - 4;
								} else {
									totalscores = totalscores - 2;
								}
								ds15pertransdone1 = true;
							}
						}

						if (!ds3pertransdone) {
							if (ds3per[i] > 12) {
								ds3perpos = true;
							}
							if (ds3per[i] < 0 && ds3perpos) { // rule 14, ds3per
																// trans from -
																// to >12, add 2
								totalscores = totalscores + 2;
								ds3pertransdone = true;
							}
						}

						if (!ds3pertransdone1) {
							if (ds3per[i] < 0) {
								ds3perpos1 = true;
							}
							if (ds3per[i] > 12 && ds3perpos1) { // rule 14,
																// ds3per
																// trans from
																// >12
																// to -, MINUS 2
								totalscores = totalscores - 2;
								ds3pertransdone1 = true;
							}
						}

						if (!ds4pertransdone) {
							if (ds4per[i] > 12) {
								ds4perpos = true;
							}
							if (ds4per[i] < 0 && ds4perpos) { // rule 15, ds4per
																// trans from -
																// to >12, add 2
								totalscores = totalscores + 2;
								ds4pertransdone = true;
							}
						}

						if (!ds4pertransdone1) {
							if (ds4per[i] < 12) {
								ds4perpos1 = true;
							}
							if (ds4per[i] > 12 && ds4perpos1) { // rule 15,
																// ds4per
																// trans from
																// >12
																// to -, MINUS 2
								totalscores = totalscores - 2;
								ds4pertransdone1 = true;
							}
						}

						if (!sattransdone) {
							if (sats[i] > 0) {
								satpos = true;
							}
							if (sats[i] < 0 && satpos) { // rule 16, sat trans
															// from - to +, add
															// 2
								totalscores = totalscores + 2;
								sattransdone = true;
							}
						}

						if (!sattransdone1) {
							if (sats[i] < 0) {
								satpos1 = true;
							}
							if (sats[i] > 0 && satpos1) { // rule 16, sat trans
															// from + to -,
															// MINUS
															// 2
								totalscores = totalscores - 2;
								sattransdone1 = true;
							}
						}

						// rule 19 add max SATCOUNT POSITIVE/NEG within 6 days
						// to total score
						if (i < 6) {
							sat6[i] = sats[i];
						} else {
							int tempNegMax = 0;
							int tempPosMax = 0;
							for (int w = 0; w < 6; w++) {
								if (sat6[w] > 0) {
									tempPosMax++;
								} else if (sat6[w] < 0) {
									tempNegMax++;
								}
							}

							if (tempNegMax > maxSatNeg) {
								maxSatNeg = tempNegMax;
							}
							if (tempPosMax > maxSatPos) {
								maxSatPos = tempPosMax;
							}

							for (int w = 0; w < 5; w++) {
								sat6[w] = sat6[w + 1];
							}
							sat6[5] = sats[i];
						}
					}

					totalscores = totalscores + maxSatPos;
					totalscores = totalscores + maxSatNeg;

					if (stmt2 == null)
						stmt2 = con.createStatement();

					System.out.println(symbol + " at " + indexes[0] + " "
							+ dates[0] + " price: " + prices[0]
							+ " Total score " + totalscores);
					if (totalscores > 1024)
						totalscores = 1024;

					String sqlUpdate = "Update BPMADMIN.INTRADAYSTOCKS  SET DIPSCORE = "
							+ totalscores
							+ " WHERE SEQINDEX = "
							+ indexes[0]
							+ " AND SYMBOL='" + symbol + "'";

					// test mode code
					/*
					 * String sqlUpdate =
					 * "Update BPMADMIN.INTRADAYSTOCKS  SET BBGO = " +
					 * totalscores + " WHERE SEQINDEX = " + indexes[0] +
					 * " AND SYMBOL='" + symbol + "'"; //
					 */
					// System.out.println(sqlUpdate);
					stmt2.executeUpdate(sqlUpdate);

					if (lastOnly) {
						break;
					}
					// rules

					for (int k = 0; k < days - 1; k++) {
						indexes[k] = indexes[k + 1];
						sats[k] = sats[k + 1];
						dsi5s[k] = dsi5s[k + 1];
						dsi24s[k] = dsi24s[k + 1];
						dsisum[k] = dsisum[k + 1];
						dc[k] = dc[k + 1];
						ds[k] = ds[k + 1];
						ds3per[k] = ds3per[k + 1];
						ds4per[k] = ds4per[k + 1];
						ds15per[k] = ds15per[k + 1];
						dds15[k] = dds15[k + 1];
						dates[k] = dates[k + 1];
						prices[k] = prices[k + 1];
						ptmas[k] = ptmas[k + 1];
						;
					}
					indexes[days - 1] = rs1.getInt(1);
					sats[days - 1] = rs1.getInt(2);
					dsi5s[days - 1] = rs1.getFloat(3);
					dsi24s[days - 1] = rs1.getFloat(4);
					dsisum[days - 1] = rs1.getFloat(5);
					dc[days - 1] = rs1.getFloat(6);
					ds[days - 1] = rs1.getFloat(7);
					ds3per[days - 1] = rs1.getFloat(8);
					ds4per[days - 1] = rs1.getFloat(9);
					ds15per[days - 1] = rs1.getFloat(10);
					dds15[days - 1] = rs1.getFloat(11);
					dates[days - 1] = rs1.getString(12);
					prices[days - 1] = rs1.getFloat(13);
					ptmas[days - 1] = rs1.getFloat(14);
				}
				loopCount++;
			}

			// set up a tag
			/*
			 * String sqlUpdate =
			 * "Update BPMADMIN.INTRADAYSTOCKS  SET DCINCR =1000 " +
			 * " WHERE SEQINDEX = " + seqIndex + " AND SYMBOL='" + symbol + "'";
			 * 
			 * stmt2.executeUpdate(sqlUpdate);
			 */
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	public static void calculateEntireUTIStepsHistory(int seqIndex,
			boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				calculateUTIStep1(nextStock, seqIndex, lastOnly);
				calculateUTISteps23(nextStock, seqIndex, lastOnly);
				long t2 = System.currentTimeMillis();
				System.out.println(nextStock
						+ " Stock UTI Steps History calculation done "
						+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;

				if (!lastOnly && loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(5000);
				} else if (loopCount % 1000 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(10000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateEntireAWSHistory(int seqIndex, boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				calculateSumAWS(nextStock, seqIndex, lastOnly);
				// calculateAWS(nextStock, seqIndex, lastOnly);
				long t2 = System.currentTimeMillis();
				System.out.println(nextStock
						+ " Stock UTI History calculation done "
						+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;

				if (!lastOnly && loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(5000);
				} else if (loopCount % 1000 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(10000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateAWS(String symbol, int seqIndex,
			boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			if (!lastOnly) {
				SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET AWS =0 WHERE SYMBOL ='"
						+ symbol + "' and AWS<>0";
				stmt1.executeUpdate(SQL);
			}

			SQL = "select SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ symbol
					+ "' and DCPERCENT>0 AND DSPERCENT>0 AND DS3PER>0 "
					+ " AND DS4PER>0 AND DS8PER>0 AND DS9PER>0 AND DS10PER>0 AND DS11PER>0 AND DS12PER>0 "
					+ " AND DS13PER>0 AND DS14PER>0 AND DS15PER>0 ORDER BY SEQINDEX DESC";

			if (lastOnly) {
				SQL = "select SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
						+ symbol
						+ "' and DCPERCENT>0 AND DSPERCENT>0 AND DS3PER>0 "
						+ " AND DS4PER>0 AND DS8PER>0 AND DS9PER>0 AND DS10PER>0 AND DS11PER>0 AND DS12PER>0 "
						+ " AND DS13PER>0 AND DS14PER>0 AND DS15PER>0 AND SEQINDEX>"
						+ (seqIndex - 10) + " ORDER BY SEQINDEX DESC";

			}

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				int nextIndex = rs1.getInt(1);
				String uSQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET AWS =12 WHERE SYMBOL ='"
						+ symbol + "' and SEQINDEX = " + nextIndex;

				if (disLog)
					System.out.println(uSQL);

				stmt2.executeUpdate(uSQL);
				if (lastOnly) {
					break;
				}
			}

			SQL = "select SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ symbol
					+ "' and DCPERCENT<0 AND DSPERCENT<0 AND DS3PER<0 "
					+ " AND DS4PER<0 AND DS8PER<0 AND DS9PER<0 AND DS10PER<0 AND DS11PER<0 AND DS12PER<0 "
					+ " AND DS13PER<0 AND DS14PER<0 AND DS15PER<0 ORDER BY SEQINDEX ASC";

			if (lastOnly) {
				SQL = "select SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
						+ symbol
						+ "' and DCPERCENT<0 AND DSPERCENT<0 AND DS3PER<0 "
						+ " AND DS4PER<0 AND DS8PER<0 AND DS9PER<0 AND DS10PER<0 AND DS11PER<0 AND DS12PER<0 "
						+ " AND DS13PER<0 AND DS14PER<0 AND DS15PER<0 AND SEQINDEX>"
						+ (seqIndex - 10) + " ORDER BY SEQINDEX ASC";

			}

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				int nextIndex = rs1.getInt(1);
				String uSQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET AWS =-12 WHERE SYMBOL ='"
						+ symbol + "' and SEQINDEX = " + nextIndex;

				if (disLog)
					System.out.println(uSQL);

				stmt2.executeUpdate(uSQL);
				if (lastOnly) {
					break;
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateSumAWS(String symbol, long maxPreIndex,
			boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();
			/*
			 * if (!lastOnly) { SQL =
			 * "UPDATE BPMADMIN.INTRADAYSTOCKS SET AWS =0 WHERE SYMBOL ='" +
			 * symbol + "' and AWS<>0"; stmt1.executeUpdate(SQL); }
			 */
			SQL = "select SEQINDEX,DCPERCENT,DSPERCENT,DS3PER,DS4PER,DS8PER,DS9PER,DS10PER,"
					+ "DS11PER,DS12PER,DS13PER,DS14PER,DS15PER from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL ='"
					+ symbol
					+ "'  and SEQINDEX > "
					+ maxPreIndex
					+ " ORDER BY SEQINDEX DESC";

			int nextIndex = 0;
			float dcp = 0.0f;
			float dsp = 0.0f;
			float ds3p = 0.0f;
			float ds4p = 0.0f;
			float ds8p = 0.0f;
			float ds9p = 0.0f;
			float ds10p = 0.0f;
			float ds11p = 0.0f;
			float ds12p = 0.0f;
			float ds13p = 0.0f;
			float ds14p = 0.0f;
			float ds15p = 0.0f;
			int sum = 0;

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				sum = 0;
				nextIndex = rs1.getInt(1);

				dcp = rs1.getFloat(2);
				if (dcp > 0) {
					sum++;
				} else if (dcp < 0) {
					sum--;
				}

				dsp = rs1.getFloat(3);
				if (dsp > 0) {
					sum++;
				} else if (dsp < 0) {
					sum--;
				}

				ds3p = rs1.getFloat(4);
				if (ds3p > 0) {
					sum++;
				} else if (ds3p < 0) {
					sum--;
				}

				ds4p = rs1.getFloat(5);
				if (ds4p > 0) {
					sum++;
				} else if (ds4p < 0) {
					sum--;
				}

				ds8p = rs1.getFloat(6);
				if (ds8p > 0) {
					sum++;
				} else if (ds8p < 0) {
					sum--;
				}

				ds9p = rs1.getFloat(7);
				if (ds9p > 0) {
					sum++;
				} else if (ds9p < 0) {
					sum--;
				}

				ds10p = rs1.getFloat(8);
				if (ds10p > 0) {
					sum++;
				} else if (ds10p < 0) {
					sum--;
				}

				ds11p = rs1.getFloat(9);
				if (ds11p > 0) {
					sum++;
				} else if (ds11p < 0) {
					sum--;
				}

				ds12p = rs1.getFloat(10);
				if (ds12p > 0) {
					sum++;
				} else if (ds12p < 0) {
					sum--;
				}

				ds13p = rs1.getFloat(11);
				if (ds13p > 0) {
					sum++;
				} else if (ds13p < 0) {
					sum--;
				}

				ds14p = rs1.getFloat(12);
				if (ds14p > 0) {
					sum++;
				} else if (ds14p < 0) {
					sum--;
				}

				ds15p = rs1.getFloat(13);
				if (ds15p > 0) {
					sum++;
				} else if (ds15p < 0) {
					sum--;
				}

				String uSQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET AWS =" + sum
						+ " WHERE SYMBOL ='" + symbol + "' and SEQINDEX = "
						+ nextIndex;

				if (disLog)
					System.out.println(uSQL);

				stmt2.executeUpdate(uSQL);
				// if (lastOnly) {
				// break;
				// }
			}

			// String uSQL =
			// "UPDATE BPMADMIN.INTRADAYSTOCKS SET AWS =1000 WHERE SYMBOL ='"+symbol+"' and SEQINDEX = "+seqIndex;
			// System.out.println(uSQL);
			// stmt2.executeUpdate(uSQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateEntireLBBIHistory(int seqIndex, boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				calculateLBBIHistory(nextStock, seqIndex, lastOnly);
				long t2 = System.currentTimeMillis();
				System.out.println(nextStock
						+ " Stock UTI History calculation done "
						+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;

				if (!lastOnly && loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(5000);
				} else if (loopCount % 1000 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(10000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateEntireUTIHistory(int seqIndex, boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				calculateUTI(nextStock, seqIndex, lastOnly);
				long t2 = System.currentTimeMillis();
				System.out.println(nextStock
						+ " Stock UTI History calculation done "
						+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;

				if (!lastOnly && loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(5000);
				} else if (loopCount % 1000 == 0 && lastOnly) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(10000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateUTI(String symbol, long maxPreIndex,
			boolean lastOnly) {

		boolean reset = false;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 3/3/2018
			long tempIndex = 0;

			ResultSet rs1 = null;

			if (maxPreIndex > 0) {
				SQL = "select SEQINDEX,BBSCORE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and UTI<>0 AND SEQINDEX<="
						+ maxPreIndex
						+ " ORDER BY SEQINDEX DESC";
				rs1 = stmt1.executeQuery(SQL);

				if (rs1.next()) {
					tempIndex = rs1.getLong(1);
				}

			}
			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 3/3/2018

			// add one more 300 to start fresh calculation
			SQL = "select    FINALPRICE, SEQINDEX, SELLINGSCORE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' and seqIndex> " + tempIndex // what is this
																// should
																// be?????Not
																// done yet
					+ " order by seqIndex desc";

			rs1 = stmt1.executeQuery(SQL);

			// int daysAfterLastNeg = 21; interday value, 1 month
			int daysAfterLastNeg = 24; // intraday ne value
			int daysToSkipFromBegin = 0;

			float[] finalPrice = new float[daysAfterLastNeg];
			long[] index = new long[daysAfterLastNeg];
			int[] sellingscores = new int[daysAfterLastNeg];
			int loopCount = 0;
			boolean negFound = false;
			int negIndex = 0;
			int negCount = 0;

			while (rs1.next()) {
				if (loopCount < daysAfterLastNeg) {
					finalPrice[loopCount] = rs1.getFloat(1);
					index[loopCount] = rs1.getLong(2);
					sellingscores[loopCount] = rs1.getInt(3);

					if (sellingscores[loopCount] < 0) {
						negFound = true;
						negIndex = loopCount;
						negCount++;
					}

				} else {

					if (!negFound) {
						if (lastOnly) {
							break;
						}
						// no neg case, then simply shift to next row
						int k = 0;
						for (k = 0; k < daysAfterLastNeg - 1; k++) {
							finalPrice[k] = finalPrice[k + 1];
							index[k] = index[k + 1];
							sellingscores[k] = sellingscores[k + 1];
						}
						finalPrice[k] = rs1.getFloat(1);
						index[k] = rs1.getInt(2);
						sellingscores[k] = rs1.getInt(3);
						if (sellingscores[k] < 0) {
							negFound = true;
							negIndex = k;
							negCount++;
						}
					} else if (negFound && negCount == 1
							&& negIndex == (daysAfterLastNeg - 1)) {
						// the last record is neg case...
						// first calculation only if selling score<-4
						if (sellingscores[negIndex] <= -1) {
							float basePrice = finalPrice[daysAfterLastNeg - 1];
							float totalDelta = 0.0f;
							for (int u = 0; u < daysAfterLastNeg - 1
									- daysToSkipFromBegin; u++) {
								totalDelta = totalDelta + 100.0f
										* (finalPrice[u] - basePrice)
										/ basePrice;

							}

							if (index[0] > tempIndex) {
								SQL = "update BPMADMIN.INTRADAYSTOCKS  set UTI = "
										+ (int) totalDelta
										+ "  where seqIndex = "
										+ index[0]
										+ " and SYMBOL='" + symbol + "'";
								stmt2.executeUpdate(SQL);
							}

						}

						if (lastOnly) {
							break;
						}
						// reset tags
						negFound = false;
						negIndex = 0;
						negCount = 0;

						// then replace whole array
						int k = 0;
						for (k = 0; k < daysAfterLastNeg; k++) {
							if (k > 0) {
								if (!rs1.next()) {
									break;
								}
							}
							finalPrice[k] = rs1.getFloat(1);
							index[k] = rs1.getLong(2);
							sellingscores[k] = rs1.getInt(3);
							if (sellingscores[k] < 0) {
								negFound = true;
								negIndex = k;
								negCount++;
							}

						}

					} else if ((negFound && negCount > 1)
							|| (negFound && negCount == 1 && negIndex < (daysAfterLastNeg - 1))) {
						if (lastOnly) {
							break;
						}
						// multiple records are neg or neg not the last one...

						// replace the array from the last neg index+1
						// first get the sellingscore non-negative records
						int lc = 0;
						for (int k = negIndex + 1; k < daysAfterLastNeg; k++) {
							finalPrice[lc] = finalPrice[k];
							index[lc] = index[k];
							sellingscores[lc] = sellingscores[k];
							lc++;
						}

						// reset tags
						negFound = false;
						negIndex = 0;
						negCount = 0;

						// then fill the rest of array
						for (int w = lc; w < daysAfterLastNeg; w++) {
							if (w > lc) {
								rs1.next();
							}
							finalPrice[w] = rs1.getFloat(1);
							index[w] = rs1.getLong(2);
							sellingscores[w] = rs1.getInt(3);
							if (sellingscores[w] < 0) {
								negFound = true;
								negIndex = w;
								negCount++;
							}
						}
					}
				}

				loopCount++;

			}

			/*
			 * reset at begining, no more needed if (maxPreIndex > 0) {
			 * resetUnqualifiedUTI(symbol, maxPreIndex); reset = true; }
			 */
		} catch (Exception ex) {
			ex.printStackTrace();
			/*
			 * reset at begining, no more needed if (!reset && maxPreIndex > 0)
			 * { resetUnqualifiedUTI(symbol, maxPreIndex); }
			 */
		}

	}

	// no longer needed as we reset up to 2*backcountNeeded (600 previous
	// records)
	public static void resetUnqualifiedUTI(String symbol, long maxPreIndex) {
		// some UTI was set in previous calculation became invalid as new
		// negative
		// selling score was create and the distance between UTI and neg
		// sellingscore
		// is less than 24

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();
			if (stmt2 == null)
				stmt2 = con.createStatement();

			/*
			 * if (!lastOnly) { SQL =
			 * "UPDATE BPMADMIN.INTRADAYSTOCKS  set UTI = 0 where symbol='" +
			 * symbol + "' and UTI<>0"; stmt1.executeUpdate(SQL); }
			 */

			SQL = "select   SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' and seqIndex>= "
					// + (maxPreIndex - backCountNeeded/ 3)
					+ (maxPreIndex - backCountNeeded * 2)
					+ " AND SEQINDEX<="
					+ maxPreIndex + " AND UTI<>0" + " order by seqIndex desc";

			rs1 = stmt1.executeQuery(SQL);

			// int daysAfterLastNeg = 21; interday value, 1 month
			int daysAfterLastNeg = 24; // intraday ne value
			int daysToSkipFromBegin = 0;

			while (rs1.next()) {
				long index = rs1.getLong(1);
				SQL = "select   SEQINDEX, SELLINGSCORE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and seqIndex>= "
						// + (maxPreIndex - backCountNeeded/ 3)
						+ (maxPreIndex - 3 * backCountNeeded)
						+ " AND SEQINDEX<"
						+ index
						+ " AND SELLINGSCORE<0 "
						+ " order by seqIndex desc";
				rs2 = stmt2.executeQuery(SQL);
				if (rs2.next()) {
					long index2 = rs2.getLong(1);
					SQL = "select   count(*) from BPMADMIN.INTRADAYSTOCKS  where symbol='"
							+ symbol
							+ "' and seqIndex>= "
							+ index2
							+ " AND SEQINDEX<=" + index;
					rs2 = stmt2.executeQuery(SQL);
					if (rs2.next() && rs2.getInt(1) < daysAfterLastNeg) {
						SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET UTI=0 where symbol='"
								+ symbol + "' and  SEQINDEX=" + index;
						stmt2.executeUpdate(SQL);

					}
				}

			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateUTIStep1(String symbol, long maxPreIndex,
			boolean lastOnly) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 3/3/2018
			long tempIndex = 0;

			if (maxPreIndex > 0) {
				SQL = "select SEQINDEX,BBSCORE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and UTIS=1 AND SEQINDEX<="
						+ maxPreIndex
						+ " ORDER BY SEQINDEX DESC";
				rs1 = stmt1.executeQuery(SQL);

				if (rs1.next()) {
					tempIndex = rs1.getLong(1);
				}

			}
			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 3/3/2018

			SQL = "select FINALPRICE, SEQINDEX, UTI from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' and UTI <>0 and seqIndex> " + tempIndex // not
																			// include
																			// tempIndex
																			// on
																			// purpose
					+ " order by seqIndex desc"; // desc or asc???

			rs1 = stmt1.executeQuery(SQL);

			int index1 = 0;
			int index2 = 0;
			float maxPrice = 0;
			int maxIndex = 0;

			while (rs1.next()) {
				index1 = rs1.getInt(2);

				String SQL2 = "select    FINALPRICE, SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and seqIndex <="
						+ index1
						+ " and SELLINGSCORE<0 order by seqIndex desc";

				rs2 = stmt2.executeQuery(SQL2);
				if (rs2.next()) {
					index2 = rs2.getInt(2);

					String SQL3 = "select    FINALPRICE, SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
							+ symbol
							+ "' and seqIndex >"
							+ index2
							+ " and seqIndex <="
							+ index1
							+ " order by seqIndex asc";

					rs3 = stmt3.executeQuery(SQL3);

					maxIndex = 0;
					maxPrice = 0.0f;
					while (rs3.next()) {
						float priceTemp = rs3.getFloat(1);
						int indexTemp = rs3.getInt(2);

						if (priceTemp > maxPrice) {
							maxPrice = priceTemp;
							maxIndex = indexTemp;
						}

					}

					if (maxIndex > 0) {
						String SQL4 = "UPDATE BPMADMIN.INTRADAYSTOCKS SET UTIS=1 where symbol='"
								+ symbol + "' and seqIndex =" + maxIndex;
						stmt3.executeUpdate(SQL4);

						if (disLog)
							System.out.println(SQL4);
					}
				}

				if (lastOnly) {
					break;
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateUTISteps23(String symbol, long maxPreIndex,
			boolean lastOnly) {

		/*
		 * -- intradays--- --totalGainsP: 51.54745892022038 ,totalDaysP:2001
		 * --totalGainsN: -56.331468263670104 ,totalDaysN:1911 --Average SPY
		 * YIELD -0.0012229062738879665 --Average Pos SPY YIELD
		 * 0.025760849035592392 --Average Neg SPY YIELD -0.02947748208459974
		 * 
		 * --interdays --- --totalGainsP: 2370.3605513875373 ,totalDaysP:2951
		 * --totalGainsN: -2511.0169267300516 ,totalDaysN:3348 --Average SPY
		 * YIELD -0.022329953221545382 --Average Pos SPY YIELD
		 * 0.8032397666511478 ---Average Neg SPY YIELD -0.7500050557736117
		 */
		try {
			// float utis2pc = 1.05f; -- interday, abou 6x avg pos gain
			// float utis3pc = 1.01f; -- interday, 1/5 of above vlue

			float utis2pc = 1.015f; // intraday, abou 6x avg pos gain
			float utis3pc = 1.01f; // intraday, 1/5 of above vlue

			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 3/3/2018
			long tempIndex = 0;

			if (maxPreIndex > 0) {
				SQL = "select SEQINDEX,BBSCORE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and UTIS=1 AND SEQINDEX<="
						+ maxPreIndex
						+ " ORDER BY SEQINDEX DESC";

				if (disLog)
					System.out.println(SQL);
				rs1 = stmt1.executeQuery(SQL);

				int lc = 0;
				while (rs1.next()) {
					tempIndex = rs1.getLong(1);
					lc++;
					if (lc >= 4) { // one UTIS seems not far enough, sometimes
									// 2nd UTIS=1
						// may have UTIS=3 later, we will stop at 2 count to
						// save time
						break;
					}
				}

			}
			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 3/3/2018

			SQL = "select    FINALPRICE, SEQINDEX, UTIS from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' and UTIS = 1 and seqIndex>= "
					+ tempIndex
					+ " order by seqIndex asc";

			if (disLog)
				System.out.println(SQL);
			rs1 = stmt1.executeQuery(SQL);

			int loopCount = 0;
			boolean negFound = false;
			int negIndex = 0;
			int negCount = 0;
			int index1 = 0;
			int index2 = 0;
			int utis1 = 0;
			int utis2 = 0;
			float price1 = 0.0f;
			float price2 = 0.0f;

			while (rs1.next()) {
				if (loopCount >= 0) {
					price1 = rs1.getFloat(1);
					index1 = rs1.getInt(2);
					utis1 = rs1.getInt(3);
					if (utis1 == 1) {
						SQL = "select    FINALPRICE, SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
								+ symbol
								+ "' and seqIndex >"
								+ index1
								+ " and FINALPRICE>="
								+ utis2pc
								* price1
								+ " order by seqIndex asc";

						if (disLog)
							System.out.println(SQL);
						rs2 = stmt2.executeQuery(SQL);
						if (rs2.next()) {
							int s2Index = rs2.getInt(2);
							float s2price = rs2.getFloat(1);
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET UTIS=2 where symbol='"
									+ symbol + "' and seqIndex =" + s2Index;

							if (disLog)
								System.out.println(SQL);

							if (s2Index >= maxPreIndex) {
								// anything up to maxPreIndex should be updated
								// already
								stmt2.executeUpdate(SQL);
							}

							SQL = "select    FINALPRICE, SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
									+ symbol
									+ "' and seqIndex >"
									+ s2Index
									+ " and FINALPRICE>="
									+ utis3pc
									* s2price
									+ " order by seqIndex asc";

							if (disLog)
								System.out.println(SQL);

							rs2 = stmt2.executeQuery(SQL);

							if (rs2.next()) {
								int s3Index = rs2.getInt(2);
								SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET UTIS=3 where symbol='"
										+ symbol + "' and seqIndex =" + s3Index;

								if (disLog)
									System.out.println(SQL);

								if (s3Index >= maxPreIndex) {
									// anything up to maxPreIndex should be
									// updated already

									stmt2.executeUpdate(SQL);
								}

							}
						}

					} else if (utis1 == 2) {
						SQL = "select    FINALPRICE, SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
								+ symbol
								+ "' and seqIndex >"
								+ index1
								+ " and FINALPRICE>="
								+ utis3pc
								* price1
								+ " order by seqIndex asc";

						if (disLog)
							System.out.println(SQL);
						rs2 = stmt2.executeQuery(SQL);
						if (rs2.next()) {
							int s3Index = rs2.getInt(2);
							float s3price = rs2.getFloat(1);
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET UTIS=3 where symbol='"
									+ symbol + "' and seqIndex =" + s3Index;

							if (disLog)
								System.out.println(SQL);

							if (s3Index >= maxPreIndex) {
								// anything up to maxPreIndex should be updated
								// already

								stmt2.executeUpdate(SQL);
							}

						}

					}

					if (lastOnly) {
						break;
					}
				}
				/*
				 * else { if (loopCount == 1) { price2 = rs1.getFloat(1); index2
				 * = rs1.getInt(2); utis2 = rs1.getInt(3); } else if (loopCount
				 * > 1) { price1 = price2; index1 = index2; utis1 = utis2;
				 * 
				 * price2 = rs1.getFloat(1); index2 = rs1.getInt(2); utis2 =
				 * rs1.getInt(3);
				 * 
				 * }
				 * 
				 * SQL =
				 * "select    FINALPRICE, SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
				 * + symbol + "' and seqIndex >" + index1 + " and FINALPRICE>="
				 * + utis2pc price2 + " and seqIndex<" + index2 +
				 * " order by seqIndex asc"; System.out.println(SQL); rs2 =
				 * stmt2.executeQuery(SQL); if (rs2.next()) { int s2Index =
				 * rs2.getInt(2); float s2price = rs2.getFloat(1); SQL =
				 * "UPDATE BPMADMIN.INTRADAYSTOCKS SET UTIS=2 where symbol='" +
				 * symbol + "' and seqIndex =" + s2Index;
				 * System.out.println(SQL); stmt2.executeUpdate(SQL); SQL =
				 * "select    FINALPRICE, SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
				 * + symbol + "' and seqIndex >" + s2Index + " and FINALPRICE>="
				 * + utis3pc s2price + " and seqIndex<" + index2 +
				 * " order by seqIndex asc"; System.out.println(SQL); rs2 =
				 * stmt2.executeQuery(SQL); if (rs2.next()) { int s3Index =
				 * rs2.getInt(2); SQL =
				 * "UPDATE BPMADMIN.INTRADAYSTOCKS SET UTIS=3 where symbol='" +
				 * symbol + "' and seqIndex =" + s3Index;
				 * System.out.println(SQL); stmt2.executeUpdate(SQL);
				 * 
				 * } }
				 * 
				 * }
				 */

				loopCount++;

			}

			if (utis1 == 1) {
				SQL = "select    FINALPRICE, SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and seqIndex >"
						+ index1
						+ " and FINALPRICE>="
						+ utis2pc
						* price1
						+ " order by seqIndex asc";

				if (disLog)
					System.out.println(SQL);

				rs2 = stmt2.executeQuery(SQL);
				if (rs2.next()) {
					int s2Index = rs2.getInt(2);
					float s2price = rs2.getFloat(1);
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET UTIS=2 where symbol='"
							+ symbol + "' and seqIndex =" + s2Index;

					if (disLog)
						System.out.println(SQL);

					if (s2Index >= maxPreIndex) {
						// anything up to maxPreIndex should be updated already

						stmt2.executeUpdate(SQL);
					}

					SQL = "select    FINALPRICE, SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
							+ symbol
							+ "' and seqIndex >"
							+ s2Index
							+ " and FINALPRICE>="
							+ utis3pc
							* s2price
							+ " order by seqIndex asc";

					if (disLog)
						System.out.println(SQL);

					rs2 = stmt2.executeQuery(SQL);
					if (rs2.next()) {
						int s3Index = rs2.getInt(2);
						SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET UTIS=3 where symbol='"
								+ symbol + "' and seqIndex =" + s3Index;

						if (disLog)
							System.out.println(SQL);

						if (s3Index >= maxPreIndex) {
							// anything up to maxPreIndex should be updated
							// already

							stmt2.executeUpdate(SQL);
						}

					}
				}

			} else if (utis1 == 2) {
				SQL = "select    FINALPRICE, SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and seqIndex >"
						+ index1
						+ " and FINALPRICE>="
						+ utis3pc
						* price1
						+ " order by seqIndex asc";

				if (disLog)
					System.out.println(SQL);

				rs2 = stmt2.executeQuery(SQL);
				if (rs2.next()) {
					int s3Index = rs2.getInt(2);
					float s3price = rs2.getFloat(1);
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET UTIS=3 where symbol='"
							+ symbol + "' and seqIndex =" + s3Index;

					if (disLog)
						System.out.println(SQL);

					if (s3Index >= maxPreIndex) {
						// anything up to maxPreIndex should be updated already

						stmt2.executeUpdate(SQL);
					}

				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			ex.printStackTrace(System.out);

		}

	}

	private static int utis1Count = 0;
	private static int utis2Count = 0;
	private static int utis3Count = 0;

	public static void calculateUTISteps23(String symbol, long maxPreIndex,
			boolean lastOnly, float utis2pc, float utis3pc) {

		try {
			// float utis2pc = 1.05f;
			// float utis3pc = 1.01f;

			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select    FINALPRICE, SEQINDEX, UTIS from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' and UTIS = 1 and seqIndex>= "
					+ (maxPreIndex - backCount) + " order by seqIndex desc";

			rs1 = stmt1.executeQuery(SQL);

			int loopCount = 0;
			boolean negFound = false;
			int negIndex = 0;
			int negCount = 0;
			int index1 = 0;
			int index2 = 0;
			int utis1 = 0;
			int utis2 = 0;
			float price1 = 0.0f;
			float price2 = 0.0f;

			while (rs1.next()) {
				utis1Count++;

				if (loopCount == 0) {
					price1 = rs1.getFloat(1);
					index1 = rs1.getInt(2);
					utis1 = rs1.getInt(3);
					if (utis1 == 1) {
						SQL = "select    FINALPRICE, SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
								+ symbol
								+ "' and seqIndex >"
								+ index1
								+ " and FINALPRICE>="
								+ utis2pc
								* price1
								+ " order by seqIndex asc";
						rs2 = stmt2.executeQuery(SQL);
						if (rs2.next()) {
							utis2Count++;

							int s2Index = rs2.getInt(2);
							float s2price = rs2.getFloat(1);
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET UTIS=2 where symbol='"
									+ symbol + "' and seqIndex =" + s2Index;
							// stmt2.executeUpdate(SQL);
							SQL = "select    FINALPRICE, SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
									+ symbol
									+ "' and seqIndex >"
									+ s2Index
									+ " and FINALPRICE>="
									+ utis3pc
									* s2price
									+ " order by seqIndex asc";
							rs2 = stmt2.executeQuery(SQL);
							if (rs2.next()) {
								int s3Index = rs2.getInt(2);
								SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET UTIS=3 where symbol='"
										+ symbol + "' and seqIndex =" + s3Index;
								// stmt2.executeUpdate(SQL);
								utis3Count++;

							}
						}

					} else if (utis1 == 2) {
						SQL = "select    FINALPRICE, SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
								+ symbol
								+ "' and seqIndex >"
								+ index1
								+ " and FINALPRICE>="
								+ utis3pc
								* price1
								+ " order by seqIndex asc";
						rs2 = stmt2.executeQuery(SQL);
						utis2Count++;

						if (rs2.next()) {
							int s3Index = rs2.getInt(2);
							float s3price = rs2.getFloat(1);
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET UTIS=3 where symbol='"
									+ symbol + "' and seqIndex =" + s3Index;
							// stmt2.executeUpdate(SQL);
							utis3Count++;

						}

					}

					if (lastOnly) {
						break;
					}
				} else {
					if (loopCount == 1) {
						price2 = rs1.getFloat(1);
						index2 = rs1.getInt(2);
						utis2 = rs1.getInt(3);
					} else if (loopCount > 1) {
						price1 = price2;
						index1 = index2;
						utis1 = utis2;

						price2 = rs1.getFloat(1);
						index2 = rs1.getInt(2);
						utis2 = rs1.getInt(3);

					}

					SQL = "select    FINALPRICE, SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
							+ symbol
							+ "' and seqIndex >"
							+ index2
							+ " and FINALPRICE>="
							+ utis2pc
							* price2
							+ " and seqIndex<"
							+ index1
							+ " order by seqIndex asc";
					rs2 = stmt2.executeQuery(SQL);
					if (rs2.next()) {
						int s2Index = rs2.getInt(2);
						float s2price = rs2.getFloat(1);
						SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET UTIS=2 where symbol='"
								+ symbol + "' and seqIndex =" + s2Index;
						// stmt2.executeUpdate(SQL);
						utis2Count++;

						SQL = "select    FINALPRICE, SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
								+ symbol
								+ "' and seqIndex >"
								+ s2Index
								+ " and FINALPRICE>="
								+ utis3pc
								* s2price
								+ " and seqIndex<"
								+ index1
								+ " order by seqIndex asc";
						rs2 = stmt2.executeQuery(SQL);
						if (rs2.next()) {
							int s3Index = rs2.getInt(2);
							SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET UTIS=3 where symbol='"
									+ symbol + "' and seqIndex =" + s3Index;
							// stmt2.executeUpdate(SQL);
							utis3Count++;

						}
					}

				}

				loopCount++;

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateDSI(String symbol, int delta, long maxPreIndex) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select   LOWPRICE, HIGHPRICE, FINALPRICE, seqindex from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' and SEQINDEX>="
					// since array is based on 10, so we have to have extra 10
					// records
					// of previous preMaxIndex
					+ (maxPreIndex - backCountNeeded / 30)
					+ " order by seqIndex desc";

			rs1 = stmt1.executeQuery(SQL);

			float[] lowPrice = new float[10];
			float[] highPrice = new float[10];
			float[] finalPrice = new float[10];
			int[] seqIndex = new int[10];
			int loopCount = 0;

			while (rs1.next()) {

				if (loopCount == 0) {
					for (int k = 0; k < 10; k++) {
						lowPrice[k] = rs1.getFloat(1);
						highPrice[k] = rs1.getFloat(2);
						finalPrice[k] = rs1.getFloat(3);
						seqIndex[k] = rs1.getInt(4);
					}
				} else {
					int k = 0;
					for (k = 0; k < 9; k++) {
						lowPrice[k] = lowPrice[k + 1];
						highPrice[k] = highPrice[k + 1];
						finalPrice[k] = finalPrice[k + 1];
						seqIndex[k] = seqIndex[k + 1];
					}
					lowPrice[k] = rs1.getFloat(1);
					highPrice[k] = rs1.getFloat(2);
					finalPrice[k] = rs1.getFloat(3);
					seqIndex[k] = rs1.getInt(4);
				}

				loopCount++;
				try {
					float dsi = 0.0f;
					if (delta == 1) {
						if (highPrice[0] - lowPrice[0] > 0) {
							dsi = 100.0f * (finalPrice[0] - lowPrice[0])
									/ (highPrice[0] - lowPrice[0]);
							SQL = "update BPMADMIN.INTRADAYSTOCKS  set dsi = "
									+ dsi + "  where seqIndex = " + seqIndex[0]
									+ " and SYMBOL='" + symbol + "'";
							if (seqIndex[0] > maxPreIndex) {
								stmt3.executeUpdate(SQL);
							}
						}

					} else if (delta == 3) {
						float max = 0.0f;
						for (int w = 0; w < 3; w++) {
							if (highPrice[w] > max)
								max = highPrice[w];
						}
						float min = 10000000000000.0f;
						for (int w = 0; w < 3; w++) {
							if (lowPrice[w] < min)
								min = lowPrice[w];
						}

						dsi = 100.0f * (finalPrice[0] - min) / (max - min);
						SQL = "update BPMADMIN.INTRADAYSTOCKS  set dsi3 = "
								+ dsi + "  where seqIndex = " + seqIndex[0]
								+ " and SYMBOL='" + symbol + "'";
						if (seqIndex[0] > maxPreIndex) {
							stmt3.executeUpdate(SQL);
						}

					} else if (delta == 5) {
						float max = 0.0f;
						for (int w = 0; w < 5; w++) {
							if (highPrice[w] > max)
								max = highPrice[w];
						}
						float min = 10000000000000.0f;
						for (int w = 0; w < 5; w++) {
							if (lowPrice[w] < min)
								min = lowPrice[w];
						}

						dsi = 100.0f * (finalPrice[0] - min) / (max - min);
						SQL = "update BPMADMIN.INTRADAYSTOCKS  set dsi5 = "
								+ dsi + "  where seqIndex = " + seqIndex[0]
								+ " and SYMBOL='" + symbol + "'";
						if (seqIndex[0] > maxPreIndex) {
							stmt3.executeUpdate(SQL);
						}

					}
				} catch (Exception ex) {

				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void compareRecords(String symbol1, String symbol2,
			long maxIndex) {

		boolean perfectMatch = true;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null) {
				stmt1 = con.createStatement();
				stmt2 = con.createStatement();
			}

			SQL = "select SEQINDEX,VARCHAR_FORMAT(CURRENTDATE,'MM-DD-YYYY') AS VDATE,chour,cminute,SYMBOL, "
					+ "OPENPRICE,LOWPRICE,HIGHPRICE,FINALPRICE,ADJUSTEDPRICE,VOLUME,TMAI,TMA,PTMA,"
					+ "GX100,RX100,GR100,FFP,ACPTMA,BBGO,WWI,DCP,TWA,PREDICTHIGH,PREDICTADJUSTED,"
					+ "PREDICTOPEN,PREDICTLOW,PAPERCENT,PHPERCENT, POPERCENT,PLPERCENT,DELTA1,DELTA2"
					+ ",DELTA3,DELTA4,DELTA5,DELTASUM,TMAV,PTMAV,DSI,DSi3,DSI5,UPTRENDSTART,DCPERCENT,"
					+ "DSPERCENT,DS3PER,DS4PER,DS8PER,DS9PER,DS10PER,DS11PER,DS12PER,DS13PER,DS14PER,"
					+ "DS15PER,AVGLMDR,DSNOTINCR,DCINCR,DSNOTDECR,DCDECR,SATCOUNT,DIPSCORE,DPS4,"
					+ "BBSCORE,DS3PT,DCPT,SUMDS3P,BBDI,SELLINGSCORE,UTI,UTIS,DS3PIP,DS3PIPDAYS,DCPIP,"
					+ "AWS,LBBI,PTS,TPTS,ATT,IBBS,DS3PIPW FROM BPMADMIN.INTRADAYSTOCKS where symbol='"
					+ symbol1 + "' ORDER BY SEQINDEX ASC";

			if (maxIndex > 0) {
				SQL = "select SEQINDEX,VARCHAR_FORMAT(CURRENTDATE,'MM-DD-YYYY') AS VDATE,chour,cminute,SYMBOL, "
						+ "OPENPRICE,LOWPRICE,HIGHPRICE,FINALPRICE,ADJUSTEDPRICE,VOLUME,TMAI,TMA,PTMA,"
						+ "GX100,RX100,GR100,FFP,ACPTMA,BBGO,WWI,DCP,TWA,PREDICTHIGH,PREDICTADJUSTED,"
						+ "PREDICTOPEN,PREDICTLOW,PAPERCENT,PHPERCENT, POPERCENT,PLPERCENT,DELTA1,DELTA2"
						+ ",DELTA3,DELTA4,DELTA5,DELTASUM,TMAV,PTMAV,DSI,DSi3,DSI5,UPTRENDSTART,DCPERCENT,"
						+ "DSPERCENT,DS3PER,DS4PER,DS8PER,DS9PER,DS10PER,DS11PER,DS12PER,DS13PER,DS14PER,"
						+ "DS15PER,AVGLMDR,DSNOTINCR,DCINCR,DSNOTDECR,DCDECR,SATCOUNT,DIPSCORE,DPS4,"
						+ "BBSCORE,DS3PT,DCPT,SUMDS3P,BBDI,SELLINGSCORE,UTI,UTIS,DS3PIP,DS3PIPDAYS,DCPIP,"
						+ "AWS,LBBI,PTS,TPTS,ATT,IBBS,DS3PIPW FROM BPMADMIN.INTRADAYSTOCKS where symbol='"
						+ symbol1
						+ "' AND SEQINDEX<="
						+ maxIndex
						+ " ORDER BY SEQINDEX ASC";
			}
			rs1 = stmt1.executeQuery(SQL);

			SQL = "select SEQINDEX,VARCHAR_FORMAT(CURRENTDATE,'MM-DD-YYYY') AS VDATE,chour,cminute,SYMBOL, "// 5
					+ "OPENPRICE,LOWPRICE,HIGHPRICE,FINALPRICE,ADJUSTEDPRICE,VOLUME,TMAI,TMA,PTMA,"// 14
					+ "GX100,RX100,GR100,FFP,ACPTMA,BBGO,WWI,DCP,TWA,PREDICTHIGH,PREDICTADJUSTED,"// 25
					+ "PREDICTOPEN,PREDICTLOW,PAPERCENT,PHPERCENT, POPERCENT,PLPERCENT,DELTA1,DELTA2"// 33
					+ ",DELTA3,DELTA4,DELTA5,DELTASUM,TMAV,PTMAV,DSI,DSi3,DSI5,UPTRENDSTART,DCPERCENT,"// 44
					+ "DSPERCENT,DS3PER,DS4PER,DS8PER,DS9PER,DS10PER,DS11PER,DS12PER,DS13PER,DS14PER,"// 54
					+ "DS15PER,AVGLMDR,DSNOTINCR,DCINCR,DSNOTDECR,DCDECR,SATCOUNT,DIPSCORE,DPS4,"// 63
					+ "BBSCORE,DS3PT,DCPT,SUMDS3P,BBDI,SELLINGSCORE,UTI,UTIS,DS3PIP,DS3PIPDAYS,DCPIP,"// 74
					+ "AWS,LBBI,PTS,TPTS,ATT,IBBS,DS3PIPW FROM BPMADMIN.INTRADAYSTOCKS where symbol='"// 81
					+ symbol2 + "' ORDER BY SEQINDEX ASC";

			if (maxIndex > 0) {
				SQL = "select SEQINDEX,VARCHAR_FORMAT(CURRENTDATE,'MM-DD-YYYY') AS VDATE,chour,cminute,SYMBOL, "// 5
						+ "OPENPRICE,LOWPRICE,HIGHPRICE,FINALPRICE,ADJUSTEDPRICE,VOLUME,TMAI,TMA,PTMA,"// 14
						+ "GX100,RX100,GR100,FFP,ACPTMA,BBGO,WWI,DCP,TWA,PREDICTHIGH,PREDICTADJUSTED,"// 25
						+ "PREDICTOPEN,PREDICTLOW,PAPERCENT,PHPERCENT, POPERCENT,PLPERCENT,DELTA1,DELTA2"// 33
						+ ",DELTA3,DELTA4,DELTA5,DELTASUM,TMAV,PTMAV,DSI,DSi3,DSI5,UPTRENDSTART,DCPERCENT,"// 44
						+ "DSPERCENT,DS3PER,DS4PER,DS8PER,DS9PER,DS10PER,DS11PER,DS12PER,DS13PER,DS14PER,"// 54
						+ "DS15PER,AVGLMDR,DSNOTINCR,DCINCR,DSNOTDECR,DCDECR,SATCOUNT,DIPSCORE,DPS4,"// 63
						+ "BBSCORE,DS3PT,DCPT,SUMDS3P,BBDI,SELLINGSCORE,UTI,UTIS,DS3PIP,DS3PIPDAYS,DCPIP,"// 74
						+ "AWS,LBBI,PTS,TPTS,ATT,IBBS,DS3PIPW FROM BPMADMIN.INTRADAYSTOCKS where symbol='"// 81
						+ symbol2
						+ "' AND SEQINDEX<="
						+ maxIndex
						+ " ORDER BY SEQINDEX ASC";

			}

			rs2 = stmt2.executeQuery(SQL);
			int lc = 0;
			while (rs1.next()) {
				lc++;
				// System.out.println("row "+lc);
				if (rs2.next()) {
					long seqDiff = rs1.getLong(1) - rs2.getLong(1);
					boolean dateSame = rs1.getString(2).equalsIgnoreCase(
							rs1.getString(2));
					int hourDiff = rs1.getInt(3) - rs2.getInt(3);
					int minuteDiff = rs1.getInt(4) - rs2.getInt(4);
					float openPricediff = rs1.getFloat(6) - rs2.getFloat(6);
					float lowPricediff = rs1.getFloat(7) - rs2.getFloat(7);
					float highPricediff = rs1.getFloat(8) - rs2.getFloat(8);
					float finalPricediff = rs1.getFloat(9) - rs2.getFloat(9);
					float adjPricediff = rs1.getFloat(10) - rs2.getFloat(10);
					float voldiff = rs1.getFloat(11) - rs2.getFloat(11);
					int tmaiDiff = rs1.getInt(12) - rs2.getInt(12);
					float tmadiff = rs1.getFloat(13) - rs2.getFloat(13);
					float ptmadiff = rs1.getFloat(14) - rs2.getFloat(14);
					int gx100Diff = rs1.getInt(15) - rs2.getInt(15);
					int rx100Diff = rs1.getInt(16) - rs2.getInt(16);
					int gr100Diff = rs1.getInt(17) - rs2.getInt(17);
					int ffpDiff = rs1.getInt(18) - rs2.getInt(18);
					float acpptmadiff = rs1.getFloat(19) - rs2.getFloat(19);
					int bbgoDiff = rs1.getInt(20) - rs2.getInt(20);
					int wwiDiff = rs1.getInt(21) - rs2.getInt(21);
					float dcpdiff = rs1.getFloat(22) - rs2.getFloat(22);
					int twaDiff = rs1.getInt(23) - rs2.getInt(23);
					float predHdiff = rs1.getFloat(24) - rs2.getFloat(24);
					float predAdiff = rs1.getFloat(25) - rs2.getFloat(25);
					float predOdiff = rs1.getFloat(26) - rs2.getFloat(26);
					float predLdiff = rs1.getFloat(27) - rs2.getFloat(27);
					float paPerdiff = rs1.getFloat(28) - rs2.getFloat(28);
					float phPerdiff = rs1.getFloat(29) - rs2.getFloat(29);
					float poPerdiff = rs1.getFloat(30) - rs2.getFloat(30);
					float plPerdiff = rs1.getFloat(31) - rs2.getFloat(31);
					float delat1diff = rs1.getFloat(32) - rs2.getFloat(32);
					float delat2diff = rs1.getFloat(33) - rs2.getFloat(33);
					float delat3diff = rs1.getFloat(34) - rs2.getFloat(34);
					float delat4diff = rs1.getFloat(35) - rs2.getFloat(35);
					float delat5diff = rs1.getFloat(36) - rs2.getFloat(36);
					float delatSumdiff = rs1.getFloat(37) - rs2.getFloat(37);
					float tmavdiff = rs1.getFloat(38) - rs2.getFloat(38);
					float ptmavdiff = rs1.getFloat(39) - rs2.getFloat(39);
					float dsidiff = rs1.getFloat(40) - rs2.getFloat(40);
					float dsi3diff = rs1.getFloat(41) - rs2.getFloat(41);
					float dsi5diff = rs1.getFloat(42) - rs2.getFloat(42);
					int upStartDiff = rs1.getInt(43) - rs2.getInt(43);
					float dcPerdiff = rs1.getFloat(44) - rs2.getFloat(44);
					float dsPerdiff = rs1.getFloat(45) - rs2.getFloat(45);
					float ds3Perdiff = rs1.getFloat(46) - rs2.getFloat(46);
					float ds4Perdiff = rs1.getFloat(47) - rs2.getFloat(47);
					float ds8Perdiff = rs1.getFloat(48) - rs2.getFloat(48);
					float ds9Perdiff = rs1.getFloat(49) - rs2.getFloat(49);
					float ds10Perdiff = rs1.getFloat(50) - rs2.getFloat(50);
					float ds11Perdiff = rs1.getFloat(51) - rs2.getFloat(51);
					float ds12Perdiff = rs1.getFloat(52) - rs2.getFloat(52);
					float ds13Perdiff = rs1.getFloat(53) - rs2.getFloat(53);
					float ds14Perdiff = rs1.getFloat(54) - rs2.getFloat(54);
					float ds15Perdiff = rs1.getFloat(55) - rs2.getFloat(55);
					float avgDrdiff = rs1.getFloat(56) - rs2.getFloat(56);
					int dsNotIncrDiff = rs1.getInt(57) - rs2.getInt(57);
					int dcIncrDiff = rs1.getInt(58) - rs2.getInt(58);
					int dsNotDecrDiff = rs1.getInt(59) - rs2.getInt(59);
					int dcDecrDiff = rs1.getInt(60) - rs2.getInt(60);
					int satCountDiff = rs1.getInt(61) - rs2.getInt(61);
					int dipScoreDiff = rs1.getInt(62) - rs2.getInt(62);
					int dps4Diff = rs1.getInt(63) - rs2.getInt(63);
					int bbscoreDiff = rs1.getInt(64) - rs2.getInt(64);
					float ds3ptdiff = rs1.getFloat(65) - rs2.getFloat(65);
					float dcptdiff = rs1.getFloat(66) - rs2.getFloat(66);
					float sumds3Pdiff = rs1.getFloat(67) - rs2.getFloat(67);
					int bbdiDiff = rs1.getInt(68) - rs2.getInt(68);
					int sellingscoreDiff = rs1.getInt(69) - rs2.getInt(69);
					int utiDiff = rs1.getInt(70) - rs2.getInt(70);
					int utisDiff = rs1.getInt(71) - rs2.getInt(71);
					float ds3pIPdiff = rs1.getFloat(72) - rs2.getFloat(72);
					int ds3pIPDaysDiff = rs1.getInt(73) - rs2.getInt(73);
					float dcpIPdiff = rs1.getFloat(74) - rs2.getFloat(74);
					int awsDiff = rs1.getInt(75) - rs2.getInt(75);
					int lbbiDiff = rs1.getInt(76) - rs2.getInt(76);
					int ptsDiff = rs1.getInt(77) - rs2.getInt(77);
					int tptsDiff = rs1.getInt(78) - rs2.getInt(78);
					int attDiff = rs1.getInt(79) - rs2.getInt(79);
					int ibbsDiff = rs1.getInt(80) - rs2.getInt(80);
					int ds3pipwDiff = rs1.getInt(81) - rs2.getInt(81);

					float d1 = 0.000000001f;
					float d2 = -0.000000001f;
					if (seqDiff != 0 || !dateSame || hourDiff != 0
							|| minuteDiff != 0 || openPricediff > d1
							|| openPricediff < d2) {
						System.out
								.println(symbol2
										+ " differs record at SEQINDEX "
										+ rs1.getLong(1) + ", date "
										+ rs1.getString(2));
						perfectMatch = false;
					}

					if (lowPricediff > d1 || lowPricediff < d2
							|| highPricediff > d1 || highPricediff < d2
							|| finalPricediff > d1 || finalPricediff < d2
							|| adjPricediff > d1 || adjPricediff < d2
							|| voldiff > d1 || voldiff < d2) {
						System.out
								.println(symbol2
										+ " differs record at SEQINDEX "
										+ rs1.getLong(1) + ", date "
										+ rs1.getString(2));
						perfectMatch = false;
					}

					if (tmaiDiff != 0 || tmadiff > d1 || tmadiff < d2
							|| ptmadiff > d1 || ptmadiff < d2 || gx100Diff != 0
							|| rx100Diff != 0 || gr100Diff != 0) {
						// System.out.println(symbol2+" differs record at SEQINDEX "+rs1.getLong(1)+", date "+rs1.getString(2));
						perfectMatch = false;
						if (tmaiDiff != 0) {
							System.out.println("tmaiDiff!=0");
						}
						if (tmadiff > d1 || tmadiff < d2) {
							System.out.println("tmadiff>d1||tmadiff<d2");
						}
						if (ptmadiff > d1 || ptmadiff < d2) {
							System.out.println("ptmadiff>d1||ptmadiff<d2");
						}
						if (gx100Diff != 0) {
							System.out.println("gx100Diff!=0");
						}
						if (rx100Diff != 0) {
							System.out.println("rx100Diff!=0");
						}
						if (gr100Diff != 0) {
							System.out.println("gr100Diff!=0");
						}
					}

					if (ffpDiff != 0 || acpptmadiff > d1 || acpptmadiff < d2
							|| dcpdiff > d1 || dcpdiff < d2 || bbgoDiff != 0
							|| wwiDiff != 0 || twaDiff != 0) {
						System.out
								.println(symbol2
										+ " differs record at SEQINDEX "
										+ rs1.getLong(1) + ", date "
										+ rs1.getString(2));
						perfectMatch = false;
						if (ffpDiff != 0) {
							System.out.println("ffpDiff!=0");
						}
						if (acpptmadiff > d1 || acpptmadiff < d2) {
							System.out
									.println("acpptmadiff>d1||acpptmadiff<d2");
						}
						if (dcpdiff > d1 || dcpdiff < d2) {
							System.out.println("dcpdiff>d1||dcpdiff<d2");
						}
						if (bbgoDiff != 0) {
							System.out.println("bbgoDiff!=0");
						}
						if (wwiDiff != 0) {
							System.out.println("|wwiDiff!=0");
						}
						if (twaDiff != 0) {
							System.out.println("twaDiff!=0");
						}
					}

					if (predHdiff > d1 || predHdiff < d2 || predAdiff > d1
							|| predAdiff < d2 || predOdiff > d1
							|| predOdiff < d2 || predLdiff > d1
							|| predLdiff < d2 || paPerdiff > d1
							|| paPerdiff < d2) {
						System.out
								.println(symbol2
										+ " differs record at SEQINDEX "
										+ rs1.getLong(1) + ", date "
										+ rs1.getString(2));
						perfectMatch = false;
						if (predHdiff > d1 || predHdiff < d2) {
							System.out.println("predHdiff>d1||predHdiff<d2");
						}
						if (predAdiff > d1 || predAdiff < d2) {
							System.out.println("predAdiff>d1||predAdiff<d2");
						}
						if (predOdiff > d1 || predOdiff < d2) {
							System.out.println("predOdiff>d1||predOdiff<d2");
						}
						if (predLdiff > d1 || predLdiff < d2) {
							System.out.println("predLdiff>d1||predLdiff<d2");
						}
						if (paPerdiff > d1 || paPerdiff < d2) {
							System.out.println("paPerdiff>d1||paPerdiff<d2");
						}
					}

					if (phPerdiff > d1 || phPerdiff < d2 || poPerdiff > d1
							|| poPerdiff < d2 || plPerdiff > d1
							|| plPerdiff < d2 || delat1diff > d1
							|| delat1diff < d2 || delat2diff > d1
							|| delat2diff < d2) {
						System.out
								.println(symbol2
										+ " differs record at SEQINDEX "
										+ rs1.getLong(1) + ", date "
										+ rs1.getString(2));
						perfectMatch = false;
						if (phPerdiff > d1 || phPerdiff < d2) {
							System.out.println("phPerdiff>d1||phPerdiff<d2");
						}
						if (poPerdiff > d1 || poPerdiff < d2) {
							System.out.println("poPerdiff>d1||poPerdiff<d2");
						}
						if (plPerdiff > d1 || plPerdiff < d2) {
							System.out.println("plPerdiff>d1||plPerdiff<d2");
						}
						if (delat1diff > d1 || delat1diff < d2) {
							System.out.println("delat1diff>d1||delat1diff<d2");
						}
						if (delat2diff > d1 || delat2diff < d2) {
							System.out.println("delat2diff>d1||delat2diff<d2");
						}
					}

					if (delat3diff > d1 || delat3diff < d2 || delat4diff > d1
							|| delat4diff < d2 || delat5diff > d1
							|| delat5diff < d2 || delatSumdiff > d1
							|| delatSumdiff < d2 || tmavdiff > d1
							|| tmavdiff < d2) {
						System.out
								.println(symbol2
										+ " differs record at SEQINDEX "
										+ rs1.getLong(1) + ", date "
										+ rs1.getString(2));
						perfectMatch = false;
						if (delat3diff > d1 || delat3diff < d2) {
							System.out.println("delat3diff>d1||delat3diff<d2");
						}
						if (delat4diff > d1 || delat4diff < d2) {
							System.out.println("delat4diff>d1||delat4diff<d2");
						}
						if (delat5diff > d1 || delat5diff < d2) {
							System.out.println("delat5diff>d1||delat5diff<d2");
						}
						if (delatSumdiff > d1 || delatSumdiff < d2) {
							System.out
									.println("delatSumdiff>d1||delatSumdiff<d2");
						}
						if (tmavdiff > d1 || tmavdiff < d2) {
							System.out.println("tmavdiff>d1||tmavdiff<d2");
						}
					}

					if (ptmavdiff > d1 || ptmavdiff < d2 || dsidiff > d1
							|| dsidiff < d2 || dsi3diff > d1 || dsi3diff < d2
							|| dsi5diff > d1 || dsi5diff < d2
							|| upStartDiff != 0) {
						System.out
								.println(symbol2
										+ " differs record at SEQINDEX "
										+ rs1.getLong(1) + ", date "
										+ rs1.getString(2));
						perfectMatch = false;
						if (ptmavdiff > d1 || ptmavdiff < d2) {
							System.out.println("ptmavdiff>d1||ptmavdiff<d2");
						}
						if (dsidiff > d1 || dsidiff < d2) {
							System.out.println("dsidiff>d1||dsidiff<d2");
						}
						if (dsi3diff > d1 || dsi3diff < d2) {
							System.out.println("dsi3diff>d1||dsi3diff<d2");
						}
						if (dsi5diff > d1 || dsi5diff < d2) {
							System.out.println("dsi5diff>d1||dsi5diff<d2");
						}
						if (upStartDiff != 0) {
							System.out.println("upStartDiff!=0");
						}
					}

					if (dcPerdiff > d1 || dcPerdiff < d2 || dsPerdiff > d1
							|| dsPerdiff < d2 || ds3Perdiff > d1
							|| ds3Perdiff < d2 || ds4Perdiff > d1
							|| ds4Perdiff < d2 || ds8Perdiff > d1
							|| ds8Perdiff < d2) {
						System.out
								.println(symbol2
										+ " differs record at SEQINDEX "
										+ rs1.getLong(1) + ", date "
										+ rs1.getString(2));
						perfectMatch = false;
						if (dcPerdiff > d1 || dcPerdiff < d2) {
							System.out.println("dcPerdiff>d1||dcPerdiff<d2");
						}
						if (dsPerdiff > d1 || dsPerdiff < d2) {
							System.out.println("dsPerdiff>d1||dsPerdiff<d2");
						}
						if (ds3Perdiff > d1 || ds3Perdiff < d2) {
							System.out.println("ds3Perdiff>d1||ds3Perdiff<d2");
						}
						if (ds4Perdiff > d1 || ds4Perdiff < d2) {
							System.out.println("ds4Perdiff>d1||ds4Perdiff<d2");
						}
						if (ds8Perdiff > d1 || ds8Perdiff < d2) {
							System.out.println("ds8Perdiff>d1||ds8Perdiff<d2");
						}

					}

					if (ds9Perdiff > d1 || ds9Perdiff < d2 || ds10Perdiff > d1
							|| ds10Perdiff < d2 || ds11Perdiff > d1
							|| ds11Perdiff < d2 || ds12Perdiff > d1
							|| ds12Perdiff < d2 || ds13Perdiff > d1
							|| ds13Perdiff < d2) {
						System.out
								.println(symbol2
										+ " differs record at SEQINDEX "
										+ rs1.getLong(1) + ", date "
										+ rs1.getString(2));
						perfectMatch = false;
						if (ds9Perdiff > d1 || ds9Perdiff < d2) {
							System.out.println("ds9Perdiff>d1||ds9Perdiff<d2");
						}
						if (ds10Perdiff > d1 || ds10Perdiff < d2) {
							System.out
									.println("ds10Perdiff>d1||ds10Perdiff<d2");
						}
						if (ds11Perdiff > d1 || ds11Perdiff < d2) {
							System.out
									.println("ds11Perdiff>d1||ds11Perdiff<d2");
						}
						if (ds12Perdiff > d1 || ds12Perdiff < d2) {
							System.out
									.println("ds12Perdiff>d1||ds12Perdiff<d2");
						}
						if (ds13Perdiff > d1 || ds13Perdiff < d2) {
							System.out
									.println("ds13Perdiff>d1||ds13Perdiff<d2");
						}

					}

					if (ds14Perdiff > d1 || ds14Perdiff < d2
							|| ds15Perdiff > d1 || ds15Perdiff < d2
							|| dsNotIncrDiff != 0 || dcIncrDiff != 0
							|| dsNotDecrDiff != 0) {
						System.out
								.println(symbol2
										+ " differs record at SEQINDEX "
										+ rs1.getLong(1) + ", date "
										+ rs1.getString(2));
						perfectMatch = false;
						if (ds14Perdiff > d1 || ds14Perdiff < d2) {
							System.out
									.println("ds14Perdiff>d1||ds14Perdiff<d2");
						}
						if (ds15Perdiff > d1 || ds15Perdiff < d2) {
							System.out
									.println("ds15Perdiff>d1||ds15Perdiff<d2");
						}
						if (dsNotIncrDiff != 0) {
							System.out.println("dsNotIncrDiff!=0");
						}
						if (dcIncrDiff != 0) {
							System.out.println("dcIncrDiff!=0");
						}
						if (dsNotDecrDiff != 0) {
							System.out.println("dsNotDecrDiff!=0");
						}

					}

					if (dcDecrDiff != 0 || satCountDiff != 0
							|| dipScoreDiff != 0 || dps4Diff != 0
							|| bbscoreDiff != 0 || attDiff != 0) {
						System.out
								.println(symbol2
										+ " differs record at SEQINDEX "
										+ rs1.getLong(1) + ", date "
										+ rs1.getString(2));
						perfectMatch = false;
						if (dcDecrDiff != 0) {
							System.out.println("dcDecrDiff!=0");
						}
						if (satCountDiff != 0) {
							System.out.println("satCountDiff!=0");
						}
						if (dipScoreDiff != 0) {
							System.out.println("dipScoreDiff!=0");
						}
						if (dps4Diff != 0) {
							System.out.println("dps4Diff!=0");
						}
						if (bbscoreDiff != 0) {
							System.out.println("bbscoreDiff!=0");
						}
						if (attDiff != 0) {
							System.out.println("attDiff!=0");
						}
					}

					if (ds3ptdiff > d1 || ds3ptdiff < d2 || dcptdiff > d1
							|| dcptdiff < d2 || sumds3Pdiff > d1
							|| sumds3Pdiff < d2 || bbdiDiff != 0
							|| sellingscoreDiff != 0 || utiDiff != 0
							|| utisDiff != 0) {
						System.out
								.println(symbol2
										+ " differs record at SEQINDEX "
										+ rs1.getLong(1) + ", date "
										+ rs1.getString(2));
						perfectMatch = false;
						if (ds3ptdiff > d1 || ds3ptdiff < d2) {
							System.out.println("ds3ptdiff>d1||ds3ptdiff<d2");
						}
						if (dcptdiff > d1 || dcptdiff < d2) {
							System.out.println("dcptdiff>d1||dcptdiff<d2");
						}
						if (sumds3Pdiff > d1 || sumds3Pdiff < d2) {
							System.out
									.println("sumds3Pdiff>d1||sumds3Pdiff<d2");
						}
						if (bbdiDiff != 0) {
							System.out.println("bbdiDiff!=0");
						}
						if (bbscoreDiff != 0) {
							System.out.println("bbscoreDiff!=0");
						}
						if (sellingscoreDiff != 0) {
							System.out.println("sellingscoreDiff!=0");
						}
						if (utiDiff != 0) {
							System.out.println("utiDiff!=0");
						}
						if (utisDiff != 0) {
							System.out.println("utisDiff!=0");
						}
					}

					if (ds3pIPdiff > d1 || ds3pIPdiff < d2
							|| ds3pIPDaysDiff != 0 || dcpIPdiff > d1
							|| dcpIPdiff < d2 || awsDiff != 0 || lbbiDiff != 0
							|| ptsDiff != 0 || tptsDiff != 0 || ibbsDiff != 0
							|| ds3pipwDiff != 0) {
						System.out
								.println(symbol2
										+ " differs record at SEQINDEX "
										+ rs1.getLong(1) + ", date "
										+ rs1.getString(2));
						perfectMatch = false;
						if (ds3pIPdiff > d1 || ds3pIPdiff < d2) {
							System.out.println("ds3pIPdiff>d1||ds3pIPdiff<d2");
						}
						if (ds3pIPDaysDiff != 0) {
							System.out.println("ds3pIPDaysDiff!=0");
						}
						if (dcpIPdiff > d1 || dcpIPdiff < d2) {
							System.out.println("dcpIPdiff>d1||dcpIPdiff<d2");
						}
						if (awsDiff != 0) {
							System.out.println("awsDiff!=0");
						}
						if (lbbiDiff != 0) {
							System.out.println("lbbiDiff!=0");
						}
						if (ptsDiff != 0) {
							System.out.println("ptsDiff!=0");
						}
						if (tptsDiff != 0) {
							System.out.println("tptsDiff!=0");
						}
						if (ibbsDiff != 0) {
							System.out.println("ibbsDiff!=0");
						}
						if (ds3pipwDiff != 0) {
							System.out.println("ds3pipwDiff!=0");
						}
					}

				} else {
					System.out.println(symbol2 + " missing record at SEQINDEX "
							+ rs1.getLong(1) + ", date " + rs1.getString(2));
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		if (perfectMatch) {
			System.out.println(symbol2 + " records perfectly matches "
					+ symbol1 + " records");
		}

	}

	public static void calculatePTWA(String symbol, int seqIndex) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  ADJUSTEDPRICE,seqindex from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' and SEQINDEX =" + seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			SQL = "select  TWA,seqindex from  BPMADMIN.INTRADAYSTOCKS   where symbol='"
					+ symbol
					+ "' and TWA>0.001 AND SEQINDEX<="
					+ seqIndex
					+ " order by seqIndex desc";

			rs2 = stmt2.executeQuery(SQL);

			float priceFinal = 0.0f;
			float lastTWA = 0.0f;
			int seqTWA = 0;

			if (rs1.next()) {

				priceFinal = rs1.getFloat(1);
				seqIndex = rs1.getInt(2);

				if (rs2.next()) {
					seqTWA = rs2.getInt(2);
					lastTWA = rs2.getFloat(1);
				}

				if (lastTWA > 0.001f && priceFinal > 0.001f) {
					float PTWA = 100.0f * (priceFinal - lastTWA) / lastTWA;
					SQL = "update BPMADMIN.INTRADAYSTOCKS  set DCP = " + PTWA
							+ "  where seqIndex = " + seqIndex
							+ " and SYMBOL='" + symbol + "'";

					stmt3.executeUpdate(SQL);
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateDeltaAgainstSPYHistory(long seqIndex,
			boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				String nextStock = en.nextElement().toString();
				long t1 = System.currentTimeMillis();
				if (!SQLOperation3.isCalculationDone(nextStock)) {
					calculateDeltaAgainstSPY(nextStock, seqIndex);
					SQLOperation3.setCalculationDoneTag(nextStock);
				}
				long t2 = System.currentTimeMillis();
				System.out.println("Time cost for processing " + nextStock
						+ " in seconds is " + (t2 - t1) / 1000);
				loopCount++;
				if (loopCount % 300 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(5000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateDeltaAgainstSPY(String symbol, long preMaxIndex) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();
			/*
			 * if (!lastOnly) { String SQL4 =
			 * "update BPMADMIN.INTRADAYSTOCKS  SET DELTA1=0.0, DELTA2=0.0, DELTA3=0.0,DELTA4=0.0,DELTA5=0.0 where symbol='"
			 * + symbol + "' AND  SEQINDEX=" + seqIndex;
			 * stmt3.executeUpdate(SQL4); }
			 */
			SQL = "select SEQINDEX,DCPERCENT,DSPERCENT,DS4PER,DS10PER, DS15PER from BPMADMIN.INTRADAYSTOCKS  where symbol='SPY'  AND SEQINDEX>="
					+ preMaxIndex + " ORDER BY SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				int index1 = rs1.getInt(1);
				float dcp = rs1.getFloat(2);
				float dsp = rs1.getFloat(3);
				float dsp4 = rs1.getFloat(4);
				float dsp10 = rs1.getFloat(5);
				float dsp15 = rs1.getFloat(6);

				String SQL2 = "select SEQINDEX,DCPERCENT,DSPERCENT,DS4PER,DS10PER, DS15PER from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol + "'  AND SEQINDEX=" + index1;
				// System.out.println(SQL2);
				rs2 = stmt2.executeQuery(SQL2);

				if (rs2.next()) {

					float deltaDC = rs2.getFloat(2) - dcp;
					float deltaDS = rs2.getFloat(3) - dsp;
					float deltaDS4 = rs2.getFloat(4) - dsp4;
					float deltaDS10 = rs2.getFloat(5) - dsp10;
					float deltaDS15 = rs2.getFloat(6) - dsp15;

					SQL2 = "update BPMADMIN.INTRADAYSTOCKS  SET DELTA1="
							+ deltaDC + ",DELTA2=" + deltaDS + ",DELTA3="
							+ deltaDS4 + ",DELTA4=" + deltaDS10 + ",DELTA5="
							+ deltaDS15 + " where symbol='" + symbol
							+ "' AND  SEQINDEX=" + index1;
					// System.out.println(SQL2);
					stmt3.executeUpdate(SQL2);

					// if (lastOnly) {// no more calculation needed
					// break;
					// }

				}

			}

			// FINSIH THE ARRAY CALCULATION SO THAT WE HAVE LATEST RECORDS
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateIncDescTrendDaysHistory(int seqIndex,
			boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex + " and DCINCR=0";
			System.out.println(SQL);
			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				String nextStock = en.nextElement().toString();
				if (lastOnly) {
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET DCINCR=0,DSNOTDECR=0, DCDECR=0,DSNOTINCR=0,SATCOUNT=0 WHERE SEQINDEX ="
							+ seqIndex + " AND SYMBOL='" + nextStock + "'";
				} else {
					SQL = "UPDATE BPMADMIN.INTRADAYSTOCKS SET DCINCR=0,DSNOTDECR=0, DCDECR=0,DSNOTINCR=0,SATCOUNT=0 WHERE  SYMBOL='"
							+ nextStock + "'";

				}
				// for saftey commented it, the latest don't need to update
				// anyway
				// stmt1.executeUpdate(SQL);
				long t1 = System.currentTimeMillis();
				if (lastOnly) {
					calculateLastIncDescTrendDays(nextStock, seqIndex, 40);
				} else {
					calculateIncDescTrendDays(nextStock, seqIndex, lastOnly, 40);

				}
				long t2 = System.currentTimeMillis();
				System.out.println("Time cost for processing " + nextStock
						+ " in seconds is " + (t2 - t1) / 1000);
				loopCount++;
				if (!lastOnly && loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(5000);
				} else if (lastOnly && loopCount % 20 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(8000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateLastIncDescTrendDays(String symbol,
			long preMaxIndex, int strechDays) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select   SEQINDEX, AVGLMDR from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' AND TMAI=1  AND seqIndex>="
					+ (preMaxIndex - 240) + " ORDER BY SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);

			// last 10 months should be enough
			int[] indexes = new int[10];
			float[] avglmdrs = new float[10];
			int loopCount = 0;

			while (rs1.next()) {
				indexes[loopCount] = rs1.getInt(1);
				avglmdrs[loopCount] = rs1.getFloat(2);
				loopCount++;
			}

			String SQL2 = "select SEQINDEX, FINALPRICE, HIGHPRICE,LOWPRICE,DCINCR,DCDECR,DSNOTINCR,DSNOTDECR from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' AND  SEQINDEX> "
					+ indexes[0]
					+ " ORDER BY SEQINDEX DESC";
			// System.out.println(SQL2);
			rs2 = stmt2.executeQuery(SQL2);
			int[] cindexes = new int[strechDays * 2];
			float[] fprices = new float[strechDays * 2];
			float[] hprices = new float[strechDays * 2];
			float[] lprices = new float[strechDays * 2];
			int[] incrDays = new int[strechDays * 2];
			int[] decrDays = new int[strechDays * 2];
			int[] daysNotIncr = new int[strechDays * 2];
			int[] daysNotDecr = new int[strechDays * 2];
			int count = 0;
			int compareCount = 0;
			float avglmdr = 0.0f;
			int lastIncrDays = 0;
			int lastDecrDays = 0;

			// only take the top 2*strechDays records
			while (rs2.next()) {
				if (count < strechDays * 2) {
					cindexes[count] = rs2.getInt(1);
					fprices[count] = rs2.getFloat(2);
					hprices[count] = rs2.getFloat(3);
					lprices[count] = rs2.getFloat(4);
					incrDays[count] = rs2.getInt(5);
					decrDays[count] = rs2.getInt(6);
					daysNotIncr[count] = rs2.getInt(7);
					daysNotDecr[count] = rs2.getInt(8);
				}
				count++;
			}

			// 333333333
			for (int w = strechDays * 2 - 1; w > 0; w--) {
				int cIndex = cindexes[w];
				float fprice = fprices[w];
				lastIncrDays = 0;
				lastDecrDays = 0;
				lastIncrDays = incrDays[w];
				lastDecrDays = decrDays[w];

				try {
					while (!(cIndex > indexes[compareCount] && cIndex <= indexes[compareCount + 1])) {
						compareCount++;
						if (compareCount >= loopCount) {
							compareCount--;
							break;
						}
						System.out.println("loopCount " + loopCount
								+ " Compare Count " + compareCount);
					}
				} catch (Exception ex) {
					System.out.println("Execption " + ex.toString());
					System.out.println("loopCount " + loopCount
							+ " Compare Count " + compareCount);

				}

				avglmdr = avglmdrs[compareCount];
				System.out.println(cIndex + " compared to "
						+ indexes[compareCount] + " avg " + avglmdr);
				boolean incDayFound = false;
				boolean decDayFound = false;
				boolean notDecrDayChanged = false;
				boolean notIncrDayChanged = false;
				int incFoundIndex = 0;
				int decFoundIndex = 0;
				int notIncDayCount = daysNotIncr[w];
				int notDecDayCount = daysNotDecr[w];
				int incDayCount = 0;
				int decDayCount = 0;
				// for (int k = w - 1; k>=0; k--) {
				System.out.println("Check record at " + cindexes[0]);
				if (lastIncrDays < 1 && !incDayFound
						&& (fprices[0] > (avglmdr + fprice))) {
					incDayCount = w;
					incFoundIndex = cindexes[0];
					incDayFound = true;
				}
				if (lastIncrDays < 1 && !incDayFound
						&& (fprices[0] < (avglmdr + fprice))) {
					notIncDayCount = notIncDayCount + 1;
					notIncrDayChanged = true;
				}

				if (lastDecrDays < 1 && !decDayFound
						&& (fprices[0] < (fprice - avglmdr))) {
					decDayCount = w;
					decFoundIndex = cindexes[0];
					decDayFound = true;
				}

				if (lastDecrDays < 1 && !decDayFound
						&& (fprices[0] > (fprice - avglmdr))) {
					notDecDayCount = notDecDayCount + 1;
					notDecrDayChanged = true;
				}

				if (incDayFound) {
					SQL2 = "update BPMADMIN.INTRADAYSTOCKS  SET DCINCR="
							+ incDayCount + " where symbol='" + symbol
							+ "' AND  SEQINDEX=" + cIndex;
					System.out.println("Inc day found for " + cIndex);
					stmt3.executeUpdate(SQL2);
				}

				if (notDecrDayChanged) {
					SQL2 = "update BPMADMIN.INTRADAYSTOCKS  SET DSNOTDECR="
							+ notDecDayCount + " where symbol='" + symbol
							+ "' AND  SEQINDEX=" + cIndex;
					stmt3.executeUpdate(SQL2);
					System.out.println("no decrease day found for " + cIndex);
				}

				if (decDayFound) {
					SQL2 = "update BPMADMIN.INTRADAYSTOCKS  SET DCDECR="
							+ decDayCount + " where symbol='" + symbol
							+ "' AND  SEQINDEX=" + cIndex;
					stmt3.executeUpdate(SQL2);
					System.out.println("decrease day found for " + cIndex);
				}

				if (notIncrDayChanged) {
					SQL2 = "update BPMADMIN.INTRADAYSTOCKS  SET DSNOTINCR="
							+ notIncDayCount + " where symbol='" + symbol
							+ "' AND  SEQINDEX=" + cIndex;
					stmt3.executeUpdate(SQL2);
					System.out.println("not increase day found for " + cIndex);
				}

				if (incFoundIndex > 0) {
					SQL2 = "SELECT SATCOUNT FROM BPMADMIN.INTRADAYSTOCKS  where symbol='"
							+ symbol + "' AND  SEQINDEX=" + incFoundIndex;
					rs3 = stmt3.executeQuery(SQL2);
					int satCount = 0;
					if (rs3.next()) {
						satCount = rs3.getInt(1);
					}
					if (satCount < 0) {
						System.out.println("Negative SATCOUNT Serve error at "
								+ incFoundIndex + " for symbol " + symbol);
					}
					satCount = satCount + 1;
					SQL2 = "update BPMADMIN.INTRADAYSTOCKS  SET SATCOUNT="
							+ satCount + " where symbol='" + symbol
							+ "' AND  SEQINDEX=" + incFoundIndex;
					stmt3.executeUpdate(SQL2);
					System.out.println("increase day found for "
							+ incFoundIndex);

				}

				if (decFoundIndex > 0) {
					System.out.println("decrease day found for "
							+ decFoundIndex);
					SQL2 = "SELECT SATCOUNT FROM BPMADMIN.INTRADAYSTOCKS  where symbol='"
							+ symbol + "' AND  SEQINDEX=" + decFoundIndex;
					rs3 = stmt3.executeQuery(SQL2);
					int satCount = 0;
					if (rs3.next()) {
						satCount = rs3.getInt(1);
					}
					if (satCount > 0) {
						System.out.println("Positive SATCOUNT Serve error at "
								+ decFoundIndex + " for symbol " + symbol);
					}
					satCount = satCount - 1;
					SQL2 = "update BPMADMIN.INTRADAYSTOCKS  SET SATCOUNT="
							+ satCount + " where symbol='" + symbol
							+ "' AND  SEQINDEX=" + decFoundIndex;
					stmt3.executeUpdate(SQL2);
				}
			}

			// 333333333333/

			// FINSIH THE ARRAY CALCULATION SO THAT WE HAVE LATEST RECORDS
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateIncDescTrendDays(String symbol,
			long maxPreIndex, boolean lastOnly, int strechDays) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018
			long tempIndex = 0;
			if (maxPreIndex > 0) {
				SQL = "select  seqindex from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and TMAI>0 AND SEQINDEX<="
						+ maxPreIndex
						+ " ORDER BY SEQINDEX DESC";
				rs1 = stmt1.executeQuery(SQL);

				int lc = 0;
				while (rs1.next()) {
					tempIndex = rs1.getLong(1);
					lc++;
					if (lc >= 20) { // only needs last 10 month or 10x24
									// =240>2x80
									// records
						// as this is the least value for 40 days x 2 in the
						// following calculation
						break;
					}

				}

			}
			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018

			SQL = "select   SEQINDEX, AVGLMDR from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' AND TMAI=1 AND SEQINDEX>="
					+ tempIndex
					+ " ORDER BY SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);

			// 1000 months or 83 years should be enough for all stocks
			// record length
			int maxArraySize = 5000;
			int[] indexes = new int[maxArraySize];
			float[] avglmdrs = new float[maxArraySize];
			int loopCount = 0;

			while (rs1.next()) {
				indexes[loopCount] = rs1.getInt(1);
				avglmdrs[loopCount] = rs1.getFloat(2);
				loopCount++;
			}

			String SQL2 = "select SEQINDEX, FINALPRICE, HIGHPRICE,LOWPRICE,DCINCR,DCDECR from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "'  AND SEQINDEX> "
					+ indexes[0]
					+ " ORDER BY SEQINDEX ASC";

			// System.out.println(SQL2);
			rs2 = stmt2.executeQuery(SQL2);
			int[] cindexes = new int[strechDays * 2];
			float[] fprices = new float[strechDays * 2];
			float[] hprices = new float[strechDays * 2];
			float[] lprices = new float[strechDays * 2];
			int[] incrDays = new int[strechDays * 2];
			int[] decrDays = new int[strechDays * 2];
			int count = 0;
			int compareCount = 0;
			float avglmdr = 0.0f;
			int workCount = 0;
			int lastIncrDays = 0;
			int lastDecrDays = 0;

			while (rs2.next()) {
				if (count < strechDays * 2) {
					cindexes[count] = rs2.getInt(1);
					fprices[count] = rs2.getFloat(2);
					hprices[count] = rs2.getFloat(3);
					lprices[count] = rs2.getFloat(4);
					incrDays[count] = rs2.getInt(5);
					decrDays[count] = rs2.getInt(6);
				} else {
					System.out.println("workCount " + workCount);
					System.out.println("index " + workCount % (strechDays * 2));
					int cIndex = cindexes[workCount % (strechDays * 2)];
					float fprice = fprices[workCount % (strechDays * 2)];
					while (compareCount < (maxArraySize - 1)
							&& !(cIndex > indexes[compareCount] && cIndex <= indexes[compareCount + 1])) {
						compareCount++;
					}

					// if(compareCount==(maxArraySize-1)){
					// break;
					// }

					avglmdr = avglmdrs[compareCount];

					System.out.println("avglmdr " + avglmdrs[compareCount]);
					System.out.println("index " + indexes[compareCount]);

					boolean cont = true;
					boolean incDayFound = false;
					boolean decDayFound = false;
					int incFoundIndex = 0;
					int decFoundIndex = 0;
					int notIncDayCount = 0;
					int notDecDayCount = 0;
					int incDayCount = 0;
					int decDayCount = 0;
					int lc = 0;

					// start for loop
					for (int k = workCount + 1; cont; k++) {
						// !incDayFound start
						if (!incDayFound
								&& (fprices[k % (strechDays * 2)] > (avglmdr + fprice))) {
							incDayCount = k - workCount;
							incFoundIndex = cindexes[k % (strechDays * 2)];
							// need to make sure this is a new record with no
							// increase found
							lastIncrDays = incrDays[workCount
									% (strechDays * 2)];
							incDayFound = true;
						}// !incDayFound end

						// !incDayFound start
						if (!incDayFound
								&& (fprices[k % (strechDays * 2)] < (avglmdr + fprice))) {
							notIncDayCount = notIncDayCount + 1;
						}
						// !incDayFound end

						// !decDayFound start
						if (!decDayFound
								&& (fprices[k % (strechDays * 2)] < (fprice - avglmdr))) {
							decDayCount = k - workCount;
							decFoundIndex = cindexes[k % (strechDays * 2)];
							// need to make sure this is a new record with no
							// increase found
							lastDecrDays = decrDays[workCount
									% (strechDays * 2)];
							decDayFound = true;
						}
						// !decDayFound end

						// !decDayFound start
						if (!decDayFound
								&& (fprices[k % (strechDays * 2)] > (fprice - avglmdr))) {
							notDecDayCount = notDecDayCount + 1;
						}
						// !decDayFound end

						lc++;
						if (lc >= (strechDays * 2 - 1)) {
							cont = false;
						}
						if (incDayFound && decDayFound) {
							cont = false;
						}
					}
					// end for loop

					if (incDayFound) {
						SQL2 = "update BPMADMIN.INTRADAYSTOCKS  SET DCINCR="
								+ incDayCount + " where symbol='" + symbol
								+ "' AND  SEQINDEX=" + cIndex;
						stmt3.executeUpdate(SQL2);
					}

					if (notDecDayCount > 0) {
						SQL2 = "update BPMADMIN.INTRADAYSTOCKS  SET DSNOTDECR="
								+ notDecDayCount + " where symbol='" + symbol
								+ "' AND  SEQINDEX=" + cIndex;
						stmt3.executeUpdate(SQL2);
					}

					if (decDayFound) {
						SQL2 = "update BPMADMIN.INTRADAYSTOCKS  SET DCDECR="
								+ decDayCount + " where symbol='" + symbol
								+ "' AND  SEQINDEX=" + cIndex;
						stmt3.executeUpdate(SQL2);
					}

					if (notIncDayCount > 0) {
						SQL2 = "update BPMADMIN.INTRADAYSTOCKS  SET DSNOTINCR="
								+ notIncDayCount + " where symbol='" + symbol
								+ "' AND  SEQINDEX=" + cIndex;
						stmt3.executeUpdate(SQL2);
					}

					if (incFoundIndex > 0 && lastIncrDays == 0) {
						SQL2 = "SELECT SATCOUNT FROM BPMADMIN.INTRADAYSTOCKS  where symbol='"
								+ symbol + "' AND  SEQINDEX=" + incFoundIndex;
						rs3 = stmt3.executeQuery(SQL2);
						int satCount = 0;
						if (rs3.next()) {
							satCount = rs3.getInt(1);
						}
						if (satCount < 0) {
							System.out
									.println("Negative SATCOUNT Serve error at "
											+ incFoundIndex
											+ " for symbol "
											+ symbol);
						}
						satCount = satCount + 1;
						SQL2 = "update BPMADMIN.INTRADAYSTOCKS  SET SATCOUNT="
								+ satCount + " where symbol='" + symbol
								+ "' AND  SEQINDEX=" + incFoundIndex;
						stmt3.executeUpdate(SQL2);
					}

					if (decFoundIndex > 0 && lastDecrDays == 0) {
						SQL2 = "SELECT SATCOUNT FROM BPMADMIN.INTRADAYSTOCKS  where symbol='"
								+ symbol + "' AND  SEQINDEX=" + decFoundIndex;
						rs3 = stmt3.executeQuery(SQL2);
						int satCount = 0;
						if (rs3.next()) {
							satCount = rs3.getInt(1);
						}
						if (satCount > 0) {
							System.out
									.println("Positive SATCOUNT Serve error at "
											+ decFoundIndex
											+ " for symbol "
											+ symbol);
						}
						satCount = satCount - 1;
						SQL2 = "update BPMADMIN.INTRADAYSTOCKS  SET SATCOUNT="
								+ satCount + " where symbol='" + symbol
								+ "' AND  SEQINDEX=" + decFoundIndex;
						stmt3.executeUpdate(SQL2);
					}

					// System.out.println("Last index update is at " + cIndex);

					cindexes[workCount % (strechDays * 2)] = rs2.getInt(1);
					fprices[workCount % (strechDays * 2)] = rs2.getFloat(2);
					hprices[workCount % (strechDays * 2)] = rs2.getFloat(3);
					lprices[workCount % (strechDays * 2)] = rs2.getFloat(4);
					incrDays[workCount % (strechDays * 2)] = rs2.getInt(5);
					decrDays[workCount % (strechDays * 2)] = rs2.getInt(6);

					workCount++;
				} // else end

				count++;

			}// while loop end
			/*
			 * System.out.println("Last Index is " + cindexes[(workCount - 1) %
			 * (strechDays * 2)] + " at array index " + (workCount - 1) %
			 * (strechDays * 2));
			 * 
			 * System.out.println("Last processed Index is " +
			 * cindexes[(workCount) % (strechDays * 2)] + " at array index " +
			 * (workCount) % (strechDays * 2));
			 */
			// FINISH THE ARRAY CALCULATION SO THAT WE HAVE LATEST RECORDS

			// System.out.println("loopCount " + loopCount + " Compare Count "
			// + compareCount);
			for (int w = 0; w < strechDays * 2 - 1; w++) {
				int cIndex = cindexes[workCount % (strechDays * 2)];
				float fprice = fprices[workCount % (strechDays * 2)];

				try {
					while (!(cIndex > indexes[compareCount] && cIndex <= indexes[compareCount + 1])) {
						compareCount++;
						if (compareCount >= loopCount) {
							compareCount--;
							break;
						}
						// System.out.println("loopCount " + loopCount
						// + " Compare Count " + compareCount);
					}
				} catch (Exception ex) {
					System.out.println("Execption " + ex.toString());
					System.out.println("loopCount " + loopCount
							+ " Compare Count " + compareCount);

				}

				avglmdr = avglmdrs[compareCount];
				// System.out.println(cIndex + " compared to "
				// + indexes[compareCount] + " avg " + avglmdr);
				boolean incDayFound = false;
				boolean decDayFound = false;
				boolean cont = true;
				int incFoundIndex = 0;
				int decFoundIndex = 0;
				int notIncDayCount = 0;
				int notDecDayCount = 0;
				int incDayCount = 0;
				int decDayCount = 0;
				int lc = 0;
				for (int k = workCount + 1; cont; k++) {
					// System.out.println("Check record at "
					// + cindexes[k % (strechDays * 2)]);
					if (!incDayFound
							&& (fprices[k % (strechDays * 2)] > (avglmdr + fprice))) {
						incDayCount = k - workCount;
						incFoundIndex = cindexes[k % (strechDays * 2)];
						incDayFound = true;
					}
					if (!incDayFound
							&& (fprices[k % (strechDays * 2)] < (avglmdr + fprice))) {
						notIncDayCount = notIncDayCount + 1;
					}

					if (!decDayFound
							&& (fprices[k % (strechDays * 2)] < (fprice - avglmdr))) {
						decDayCount = k - workCount;
						decFoundIndex = cindexes[k % (strechDays * 2)];
						decDayFound = true;
					}

					if (!decDayFound
							&& (fprices[k % (strechDays * 2)] > (fprice - avglmdr))) {
						notDecDayCount = notDecDayCount + 1;
					}

					lc++;
					if (lc >= (strechDays * 2 - 1 - w)) {
						cont = false;
					}
					if (incDayFound && decDayFound) {
						cont = false;
					}
				}

				if (incDayFound) {
					SQL2 = "update BPMADMIN.INTRADAYSTOCKS  SET DCINCR="
							+ incDayCount + " where symbol='" + symbol
							+ "' AND  SEQINDEX=" + cIndex;
					// System.out.println("Inc day found for " + cIndex);
					stmt3.executeUpdate(SQL2);
				}

				if (notDecDayCount > 0) {
					SQL2 = "update BPMADMIN.INTRADAYSTOCKS  SET DSNOTDECR="
							+ notDecDayCount + " where symbol='" + symbol
							+ "' AND  SEQINDEX=" + cIndex;
					stmt3.executeUpdate(SQL2);
					// System.out.println("no decrease day found for " +
					// cIndex);
				}

				if (decDayFound) {
					SQL2 = "update BPMADMIN.INTRADAYSTOCKS  SET DCDECR="
							+ decDayCount + " where symbol='" + symbol
							+ "' AND  SEQINDEX=" + cIndex;
					stmt3.executeUpdate(SQL2);
					// System.out.println("decrease day found for " + cIndex);
				}

				if (notIncDayCount > 0) {
					SQL2 = "update BPMADMIN.INTRADAYSTOCKS  SET DSNOTINCR="
							+ notIncDayCount + " where symbol='" + symbol
							+ "' AND  SEQINDEX=" + cIndex;
					stmt3.executeUpdate(SQL2);
					// System.out.println("not increase day found for " +
					// cIndex);
				}

				if (incFoundIndex > 0) {
					SQL2 = "SELECT SATCOUNT FROM BPMADMIN.INTRADAYSTOCKS  where symbol='"
							+ symbol + "' AND  SEQINDEX=" + incFoundIndex;
					rs3 = stmt3.executeQuery(SQL2);
					int satCount = 0;
					if (rs3.next()) {
						satCount = rs3.getInt(1);
					}
					if (satCount < 0) {
						System.out.println("Negative SATCOUNT Serve error at "
								+ incFoundIndex + " for symbol " + symbol);
					}
					satCount = satCount + 1;
					SQL2 = "update BPMADMIN.INTRADAYSTOCKS  SET SATCOUNT="
							+ satCount + " where symbol='" + symbol
							+ "' AND  SEQINDEX=" + incFoundIndex;
					stmt3.executeUpdate(SQL2);
					// System.out.println("increase day found for "
					// + incFoundIndex);

				}

				if (decFoundIndex > 0) {
					// System.out.println("decrease day found for "
					// + decFoundIndex);
					SQL2 = "SELECT SATCOUNT FROM BPMADMIN.INTRADAYSTOCKS  where symbol='"
							+ symbol + "' AND  SEQINDEX=" + decFoundIndex;
					rs3 = stmt3.executeQuery(SQL2);
					int satCount = 0;
					if (rs3.next()) {
						satCount = rs3.getInt(1);
					}
					if (satCount > 0) {
						System.out.println("Positive SATCOUNT Serve error at "
								+ decFoundIndex + " for symbol " + symbol);
					}
					satCount = satCount - 1;
					SQL2 = "update BPMADMIN.INTRADAYSTOCKS  SET SATCOUNT="
							+ satCount + " where symbol='" + symbol
							+ "' AND  SEQINDEX=" + decFoundIndex;
					stmt3.executeUpdate(SQL2);
				}
				workCount++;
			}

			// no longer needs this tag for interruptable execution
			// SQL =
			// "UPDATE BPMADMIN.INTRADAYSTOCKS SET DCINCR=1000 WHERE SEQINDEX ="
			// + seqIndex + " AND SYMBOL='" + symbol + "'";
			// stmt3.executeUpdate(SQL);
			// FINSIH THE ARRAY CALCULATION SO THAT WE HAVE LATEST RECORDS
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateEntireAVGLMDRHistory(int seqIndex,
			boolean lastOnly) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			while (en.hasMoreElements()) {
				long t1 = System.currentTimeMillis();
				String nextStock = en.nextElement().toString();
				calculateAVGLMDRHistory(nextStock, lastOnly, 0l);
				long t2 = System.currentTimeMillis();
				System.out.println(nextStock
						+ " Stock AVG LMDR calculation done "
						+ " cost time seconds " + (t2 - t1) / 1000);
				loopCount++;
				if (loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(8000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateAVGLMDRHistory(String symbol, boolean lastOnly,
			long maxPreIndex) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018
			long tempIndex = 0;
			if (maxPreIndex > 0) {
				SQL = "select  seqindex from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and TMAI>0 AND SEQINDEX<="
						+ maxPreIndex
						+ " ORDER BY SEQINDEX DESC";
				rs1 = stmt1.executeQuery(SQL);

				int lc = 0;
				while (rs1.next()) {
					tempIndex = rs1.getLong(1);
					lc++;
					if (lc >= 2) {
						break;
					}

				}

			}
			// *** new logic to take care add data the in-between 2 section data
			// calculation , see AAPL before 12/22/2017 and after data combined
			// added 2/24/2018

			SQL = "select   SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' AND TMAI=1 AND SEQINDEX >="
					+ tempIndex
					+ " ORDER BY SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);

			int seqIndex1 = 0;
			int seqIndex2 = 0;
			int loopCount = 0;

			while (rs1.next()) {
				if (loopCount == 0) {
					seqIndex1 = rs1.getInt(1);
				} else if (loopCount == 1) {
					seqIndex2 = rs1.getInt(1);
				} else {
					String SQL2 = "select  count(*), SUM(HIGHPRICE),SUM(LOWPRICE) from BPMADMIN.INTRADAYSTOCKS  where symbol='"
							+ symbol
							+ "' AND  SEQINDEX>"
							+ seqIndex2
							+ " AND SEQINDEX<=" + seqIndex1;
					// System.out.println(SQL2);
					rs2 = stmt2.executeQuery(SQL2);
					if (rs2.next()) {
						int count = rs2.getInt(1);
						float highs = rs2.getFloat(2);
						float lows = rs2.getFloat(3);
						float avgDR = (highs - lows) / (1.0f * count);
						SQL2 = "update BPMADMIN.INTRADAYSTOCKS  SET AVGLMDR="
								+ avgDR + " where symbol='" + symbol
								+ "' AND  SEQINDEX=" + seqIndex1;

						if (seqIndex1 >= maxPreIndex) {
							stmt2.executeUpdate(SQL2);
						}
						if (lastOnly) {// no more calculation needed
							break;
						}

					}
					seqIndex1 = seqIndex2;
					seqIndex2 = rs1.getInt(1);
				}

				loopCount++;
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void NotUsedcalculateFFPHistory(String symbol) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  PTMA, SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' ORDER BY SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);

			float lastTMA = 0.0f;
			int seqIndex = 0;
			int ffpCount = 0;

			while (rs1.next()) {

				lastTMA = rs1.getFloat(1);
				seqIndex = rs1.getInt(2);

				if (lastTMA >= 15.0f) {
					ffpCount++;

					if (ffpCount >= 15) {
						SQL = "update BPMADMIN.INTRADAYSTOCKS  set FFP="
								+ (ffpCount - 14) + "  where seqIndex = "
								+ seqIndex + " and SYMBOL='" + symbol + "'";

						stmt3.executeUpdate(SQL);
					}
				} else {
					ffpCount = 0;
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static boolean recordsExist(String symbol) {
		boolean exits = false;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  count(*) from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' ";

			rs1 = stmt1.executeQuery(SQL);

			int count = 0;

			if (rs1.next()) {

				count = rs1.getInt(1);

			}
			if (count > 0)
				exits = true;

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return exits;
	}

	public static void NotUsedcalculateBBGOHistory2(String symbol) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  PTMA, SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' ORDER BY SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);

			float lastTMA = 0.0f;
			int seqIndex = 0;
			int ffpCount = 0;

			while (rs1.next()) {

				lastTMA = rs1.getFloat(1);
				seqIndex = rs1.getInt(2);

				if (lastTMA <= -30.0f) {
					ffpCount--;

					if (ffpCount <= -15) {
						SQL = "update BPMADMIN.INTRADAYSTOCKS  set BBGO ="
								+ (ffpCount + 14) + "  where seqIndex = "
								+ seqIndex + " and SYMBOL='" + symbol + "'";

						stmt3.executeUpdate(SQL);
					}
				} else {
					ffpCount = 0;
				}

			}

			getBothEnds(symbol);

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set BBGO = -1  where seqIndex = "
					+ startIndexLast + " and SYMBOL='" + symbol + "'";

			stmt3.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void NotUsedcalculateBBGOHistory1(String symbol) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  PTMA, SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' ORDER BY SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);

			float lastTMA = 0.0f;
			int seqIndex = 0;
			int ffpCount = 0;

			while (rs1.next()) {

				lastTMA = rs1.getFloat(1);
				seqIndex = rs1.getInt(2);

				if (lastTMA >= 30.0f) {
					ffpCount++;

					if (ffpCount >= 15) {
						SQL = "update BPMADMIN.INTRADAYSTOCKS  set BBGO ="
								+ (ffpCount - 14) + "  where seqIndex = "
								+ seqIndex + " and SYMBOL='" + symbol + "'";

						stmt3.executeUpdate(SQL);
					}
				} else {
					ffpCount = 0;
				}

			}

			getBothEnds(symbol);

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set BBGO = -1  where seqIndex = "
					+ startIndexLast + " and SYMBOL='" + symbol + "'";

			stmt3.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateTodayBBGO(String symbol, int seqIndex) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  PTMA, SEQINDEX,BBGO from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' AND SEQINDEX>="
					+ (seqIndex - 15)
					+ " AND SEQINDEX<=" + seqIndex + " ORDER BY SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);

			float lastTMA = 0.0f;
			int lastIndex = 0;
			int bbgoCount = 0;
			int previousBBGO = 0;

			while (rs1.next()) {

				lastTMA = rs1.getFloat(1);
				lastIndex = rs1.getInt(2);

				if (lastTMA >= 30.0f) {
					bbgoCount++;

					if (bbgoCount >= 15) {
						SQL = "update BPMADMIN.INTRADAYSTOCKS  set BBGO="
								+ (previousBBGO + 1) + "  where seqIndex = "
								+ seqIndex + " and SYMBOL='" + symbol + "'";

						stmt3.executeUpdate(SQL);
					}
				} else if (lastTMA <= -30.0f) {
					bbgoCount--;

					if (bbgoCount <= -15) {
						SQL = "update BPMADMIN.INTRADAYSTOCKS  set BBGO="
								+ (previousBBGO - 1) + "  where seqIndex = "
								+ seqIndex + " and SYMBOL='" + symbol + "'";

						stmt3.executeUpdate(SQL);
					}
				} else {
					bbgoCount = 0;
				}

				previousBBGO = rs1.getInt(3);
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateTodayFFP(String symbol, int seqIndex) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  PTMA, SEQINDEX,FFP from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' AND SEQINDEX>="
					+ (seqIndex - 15)
					+ " AND SEQINDEX<=" + seqIndex + " ORDER BY SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);

			float lastTMA = 0.0f;
			int lastIndex = 0;
			int ffpCount = 0;
			int previousFFP = 0;

			while (rs1.next()) {

				lastTMA = rs1.getFloat(1);
				lastIndex = rs1.getInt(2);

				if (lastTMA >= 15.0f) {
					ffpCount++;

					if (ffpCount >= 15) {
						int FPP = rs1.getInt(3);
						SQL = "update BPMADMIN.INTRADAYSTOCKS  set FFP="
								+ (previousFFP + 1) + "  where seqIndex = "
								+ seqIndex + " and SYMBOL='" + symbol + "'";

						stmt3.executeUpdate(SQL);
					}
				} else {
					ffpCount = 0;
				}

				previousFFP = rs1.getInt(3);
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void findGRXPointsHistory(String symbol, int counts) {
		getBothEnds(symbol);

		if (counts > 0)
			startIndexLast = endIndexLast - 100 - counts;

		try {
			int minSeqIndex = 10000;
			int maxSeqIndex = 10000;
			float minPrice = 0.0f;
			float maxPrice = 0.0f;
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			for (long k = startIndexLast + 100; k <= endIndexLast; k++) {
				SQL = "SELECT min(ADJUSTEDPRICE),max(ADJUSTEDPRICE) from BPMADMIN.INTRADAYSTOCKS  where   symbol='"
						+ symbol
						+ "' and seqIndex<="
						+ k
						+ " and seqIndex>"
						+ (k - 100);
				rs1 = stmt1.executeQuery(SQL);
				if (rs1.next()) {
					minPrice = rs1.getFloat(1);
					maxPrice = rs1.getFloat(2);
				}

				if (minPrice >= 0.01f) {
					SQL = "SELECT seqIndex from BPMADMIN.INTRADAYSTOCKS where   symbol='"
							+ symbol
							+ "' and seqIndex<="
							+ k
							+ " and seqIndex>"
							+ (k - 100)
							+ " and ADJUSTEDPRICE>"
							+ (minPrice - 0.001)
							+ " and ADJUSTEDPRICE<"
							+ (minPrice + 0.001)
							+ " order by seqIndex desc";
					rs1 = stmt1.executeQuery(SQL);
					if (rs1.next()) {
						minSeqIndex = rs1.getInt(1);
					}

					SQL = "SELECT seqIndex from BPMADMIN.INTRADAYSTOCKS  where   symbol='"
							+ symbol
							+ "' and seqIndex<="
							+ k
							+ " and seqIndex>"
							+ (k - 100)
							+ " and ADJUSTEDPRICE>"
							+ (maxPrice - 0.001)
							+ " and ADJUSTEDPRICE<"
							+ (maxPrice + 0.001)
							+ " order by seqIndex desc";
					rs1 = stmt1.executeQuery(SQL);
					if (rs1.next()) {
						maxSeqIndex = rs1.getInt(1);
					}

					SQL = "update BPMADMIN.INTRADAYSTOCKS  set GX100="
							+ (100 - (k - maxSeqIndex)) + ",RX100="
							+ (100 - (k - minSeqIndex)) + " where  seqIndex="
							+ k + " and symbol='" + symbol + "'";
					stmt2.executeUpdate(SQL);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateTodayGRXPoints(String symbol, int seqIndex) {
		getBothEnds(symbol);

		try {
			int minSeqIndex = 10000;
			int maxSeqIndex = 10000;
			float minPrice = 0.0f;
			float maxPrice = 0.0f;
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "SELECT min(ADJUSTEDPRICE),max(ADJUSTEDPRICE) from BPMADMIN.INTRADAYSTOCKS  where   symbol='"
					+ symbol
					+ "' and seqIndex<="
					+ seqIndex
					+ " and seqIndex>"
					+ (seqIndex - 100);
			rs1 = stmt1.executeQuery(SQL);
			if (rs1.next()) {
				minPrice = rs1.getFloat(1);
				maxPrice = rs1.getFloat(2);
			}

			if (minPrice >= 0.01f) {
				SQL = "SELECT seqIndex from BPMADMIN.INTRADAYSTOCKS where   symbol='"
						+ symbol
						+ "' and seqIndex<="
						+ seqIndex
						+ " and seqIndex>"
						+ (seqIndex - 100)
						+ " and ADJUSTEDPRICE>"
						+ (minPrice - 0.001)
						+ " and ADJUSTEDPRICE<"
						+ (minPrice + 0.001)
						+ " order by seqIndex desc";
				rs1 = stmt1.executeQuery(SQL);
				if (rs1.next()) {
					minSeqIndex = rs1.getInt(1);
				}

				SQL = "SELECT seqIndex from BPMADMIN.INTRADAYSTOCKS  where   symbol='"
						+ symbol
						+ "' and seqIndex<="
						+ seqIndex
						+ " and seqIndex>"
						+ (seqIndex - 100)
						+ " and ADJUSTEDPRICE>"
						+ (maxPrice - 0.001)
						+ " and ADJUSTEDPRICE<"
						+ (maxPrice + 0.001)
						+ " order by seqIndex desc";
				rs1 = stmt1.executeQuery(SQL);
				if (rs1.next()) {
					maxSeqIndex = rs1.getInt(1);
				}

				SQL = "update BPMADMIN.INTRADAYSTOCKS  set GX100="
						+ (100 - (seqIndex - maxSeqIndex)) + ",RX100="
						+ (100 - (seqIndex - minSeqIndex))
						+ " where  seqIndex=" + seqIndex + " and symbol='"
						+ symbol + "'";
				stmt2.executeUpdate(SQL);
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void doGR100Calculation(String symbol, int counts) {

		getBothEnds(symbol);

		if (counts > 0)
			endIndexLast = endIndexLast - counts;

		try {

			int countGR1001 = 0;
			int GX1001 = 0;
			int RX1001 = 0;
			int seqIndex1 = 0;

			int countGR1002 = 0;
			int GX1002 = 0;
			int RX1002 = 0;
			int seqIndex2 = 0;

			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "SELECT GX100, RX100, GR100,seqIndex from BPMADMIN.INTRADAYSTOCKS  where   symbol='"
					+ symbol + "' and GX100>0 ORDER by SEQINDEX ASC";

			if (counts > 0)
				SQL = "SELECT GX100, RX100, GR100,seqIndex from BPMADMIN.INTRADAYSTOCKS  where   symbol='"
						+ symbol
						+ "' and GX100>0 and SEQINDEX> "
						+ endIndexLast + " ORDER by SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);
			while (rs1.next()) {

				if (countGR1001 == 0) {
					countGR1001 = rs1.getInt(3);
					GX1001 = rs1.getInt(1);
					RX1001 = rs1.getInt(2);
					seqIndex1 = rs1.getInt(4);

					if (GX1001 > RX1001) {
						countGR1001 = 1;
						SQL = "update BPMADMIN.INTRADAYSTOCKS set GR100 =1 where  seqIndex="
								+ seqIndex1 + " and symbol='" + symbol + "'";
						stmt2.executeUpdate(SQL);

					} else {
						countGR1001 = -1;
						SQL = "update BPMADMIN.INTRADAYSTOCKS set GR100 =-1 where  seqIndex="
								+ seqIndex1 + " and symbol='" + symbol + "'";
						stmt2.executeUpdate(SQL);
					}
				} else if (countGR1002 == 0) {
					countGR1002 = rs1.getInt(3);
					GX1002 = rs1.getInt(1);
					RX1002 = rs1.getInt(2);
					seqIndex2 = rs1.getInt(4);

					if (GX1002 > RX1002 && countGR1001 > 0) {
						countGR1002 = countGR1001 + 1;
						SQL = "update BPMADMIN.INTRADAYSTOCKS set GR100 ="
								+ countGR1002 + " where  seqIndex=" + seqIndex2
								+ " and symbol='" + symbol + "'";
						stmt2.executeUpdate(SQL);

					} else if (GX1002 > RX1002 && countGR1001 < 0) {
						countGR1002 = 1;
						SQL = "update BPMADMIN.INTRADAYSTOCKS set GR100 ="
								+ countGR1002 + " where  seqIndex=" + seqIndex2
								+ " and symbol='" + symbol + "'";
						stmt2.executeUpdate(SQL);

					} else if (GX1002 < RX1002 && countGR1001 > 0) {
						countGR1002 = -1;
						SQL = "update BPMADMIN.INTRADAYSTOCKS set GR100 ="
								+ countGR1002 + " where  seqIndex=" + seqIndex2
								+ " and symbol='" + symbol + "'";
						stmt2.executeUpdate(SQL);

					} else if (GX1002 < RX1002 && countGR1001 < 0) {
						countGR1002 = countGR1001 - 1;
						SQL = "update BPMADMIN.INTRADAYSTOCKS set GR100 ="
								+ countGR1002 + " where  seqIndex=" + seqIndex2
								+ " and symbol='" + symbol + "'";
						stmt2.executeUpdate(SQL);

					}

				} else {
					countGR1001 = countGR1002;
					GX1001 = GX1002;
					RX1001 = RX1002;
					seqIndex1 = seqIndex2;

					countGR1002 = rs1.getInt(3);
					GX1002 = rs1.getInt(1);
					RX1002 = rs1.getInt(2);
					seqIndex2 = rs1.getInt(4);

					if (GX1002 > RX1002 && countGR1001 > 0) {
						countGR1002 = countGR1001 + 1;
						SQL = "update BPMADMIN.INTRADAYSTOCKS set GR100 ="
								+ countGR1002 + " where  seqIndex=" + seqIndex2
								+ " and symbol='" + symbol + "'";
						stmt2.executeUpdate(SQL);

					} else if (GX1002 > RX1002 && countGR1001 < 0) {
						countGR1002 = 1;
						SQL = "update BPMADMIN.INTRADAYSTOCKS set GR100 ="
								+ countGR1002 + " where  seqIndex=" + seqIndex2
								+ " and symbol='" + symbol + "'";
						stmt2.executeUpdate(SQL);

					} else if (GX1002 < RX1002 && countGR1001 > 0) {
						countGR1002 = -1;
						SQL = "update BPMADMIN.INTRADAYSTOCKS set GR100 ="
								+ countGR1002 + " where  seqIndex=" + seqIndex2
								+ " and symbol='" + symbol + "'";
						stmt2.executeUpdate(SQL);

					} else if (GX1002 < RX1002 && countGR1001 < 0) {
						countGR1002 = countGR1001 - 1;
						SQL = "update BPMADMIN.INTRADAYSTOCKS set GR100 ="
								+ countGR1002 + " where  seqIndex=" + seqIndex2
								+ " and symbol='" + symbol + "'";
						stmt2.executeUpdate(SQL);

					}

				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateTodayGR100(String symbol, int seqIndex) {
		try {

			int countGR1001 = 0;
			int GX1001 = 0;
			int RX1001 = 0;
			int seqIndex1 = 0;

			int countGR1002 = 0;
			int GX1002 = 0;
			int RX1002 = 0;
			int seqIndex2 = 0;

			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "SELECT GX100, RX100, GR100,seqIndex from BPMADMIN.INTRADAYSTOCKS  where   symbol='"
					+ symbol
					+ "' and seqIndex<="
					+ seqIndex
					+ " and seqIndex>=" + (seqIndex - 5) // in case index skip
					+ " ORDER by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);
			while (rs1.next()) {

				if (GX1001 == 0) {
					countGR1001 = rs1.getInt(3);
					GX1001 = rs1.getInt(1);
					RX1001 = rs1.getInt(2);
					seqIndex1 = rs1.getInt(4);

				} else if (GX1002 == 0) {
					countGR1002 = rs1.getInt(3);
					GX1002 = rs1.getInt(1);
					RX1002 = rs1.getInt(2);
					seqIndex2 = rs1.getInt(4);

					if (GX1001 > RX1001 && countGR1002 > 0) {
						countGR1001 = countGR1002 + 1;
						SQL = "update BPMADMIN.INTRADAYSTOCKS set GR100 ="
								+ countGR1001 + " where  seqIndex=" + seqIndex1
								+ " and symbol='" + symbol + "'";
						stmt2.executeUpdate(SQL);

					} else if (GX1001 > RX1001 && countGR1002 < 0) {
						countGR1001 = 1;
						SQL = "update BPMADMIN.INTRADAYSTOCKS set GR100 ="
								+ countGR1001 + " where  seqIndex=" + seqIndex1
								+ " and symbol='" + symbol + "'";
						stmt2.executeUpdate(SQL);

					} else if (GX1001 < RX1001 && countGR1002 > 0) {
						countGR1001 = -1;
						SQL = "update BPMADMIN.INTRADAYSTOCKS set GR100 ="
								+ countGR1001 + " where  seqIndex=" + seqIndex1
								+ " and symbol='" + symbol + "'";
						stmt2.executeUpdate(SQL);

					} else if (GX1001 < RX1001 && countGR1002 < 0) {
						countGR1001 = countGR1002 - 1;
						SQL = "update BPMADMIN.INTRADAYSTOCKS set GR100 ="
								+ countGR1001 + " where  seqIndex=" + seqIndex1
								+ " and symbol='" + symbol + "'";
						stmt2.executeUpdate(SQL);

					}

				} else {
					break;// we have done here
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static boolean getTrendDSIStable(String symbol, int maxIndex) {
		boolean result = false;
		getBothEnds(symbol);
		int maxFCount = 3;
		// success mean above the target closing price
		int successCount51 = 0;
		// failed mean below the target closing price 1%
		int failedCount51 = 0;
		int successCount52 = 0;
		int failedCount52 = 0;
		int successCount101 = 0;
		int failedCount101 = 0;
		int successCount102 = 0;
		int failedCount102 = 0;
		int totalCount = 0;
		float totalReturn = 1.0f;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "SELECT SEQINDEX, SYMBOL, VARCHAR_FORMAT(CURRENTDATE,'MM-DD-YYYY') AS VCHAR_FORMATED, "
					+ "PTMA,DSI,FINALPRICE,HIGHPRICE,LOWPRICE,OPENPRICE,PTMAV,ADJUSTEDPRICE "
					+ "DSI5,ACPTMA FROM BPMADMIN.INTRADAYSTOCKS where symbol='"
					+ symbol
					+ "'  and seqindex<="
					+ maxIndex
					+ " and DSI<>0 and ptma<>0 order by seqIndex asc";
			rs1 = stmt1.executeQuery(SQL);
			int days = 44;
			int[] index = new int[days];
			String[] date = new String[days];
			float[] ptma = new float[days];
			float[] dsi = new float[days];
			float dsiAvg = 0.0f, dsiMin = 0.0f, dsiMax = 0.0f;
			float[] finalPrice = new float[days];
			float finalPriceAvg = 0.0f, finalPriceMin = 0.0f, finalPriceMax = 0.0f;
			float[] highPrice = new float[days];
			float highPriceAvg = 0.0f, highPriceMin = 0.0f, highPriceMax = 0.0f;
			float[] lowPrice = new float[days];
			float lowPriceAvg = 0.0f, lowPriceMin = 0.0f, lowPriceMax = 0.0f;
			float[] openPrice = new float[days];
			float openPriceAvg = 0.0f, openPriceMin = 0.0f, openPriceMax = 0.0f;
			float[] ptmav = new float[days];
			float[] dsi5 = new float[days];
			float[] acptmav = new float[days];
			float ptmavAvg = 0.0f, ptmavMin = 0.0f, ptmavMax = 0.0f;
			float ptmaAvg = 0.0f, ptmaMin = 0.0f, ptmaMax = 0.0f;

			int loopCount = 0;
			System.out
					.println("Index   date   ptma  dsi  final high  low  open  ptmav");
			boolean start = false;
			int aboveMin = 0;
			// days stay above (DSI) dsiLine value
			int aboveMinCount = 5;
			// this is the value we start consider count aboveMinCount
			float dsiLine = 80.0f;
			// value below which is considered a dip
			float diValue = 60.0f;
			// max of minimum value for dip
			float minValue = 30.0f;
			boolean divStart = false;
			boolean minStart = false;
			boolean stopCounting = false;
			float minDSI = 10000.0f;
			int minDSIIndex = 0;
			String minDSIDate = "";
			int daysSinceDSIMin = 0;
			float fAvgDSI = 0.0f;
			float fMinDSI = 0.0f;
			float fMaxDSI = 0.0f;
			float maxDSI = 0.0f;
			int fIndex = 0;
			String fDate = "";
			float fPrice = 0.0f;
			float fNPrice = 0.0f;
			int divStartIndex = 0;
			boolean fTrue = false;
			int fCount = 0;
			int abovePriceCount1 = 0;
			int abovePriceCount2 = 0;
			float minPriceF1 = 10000000.0f;
			float maxPriceF1 = 0.0f;
			float minPriceF2 = 10000000.0f;
			float maxPriceF2 = 0.0f;
			int checkDateCount5 = 5;
			int checkDateCount10 = 10;
			int fCountTrack = 0;

			while (rs1.next()) {

				if (fTrue) {
					fCount++;
				}

				int i = loopCount % days;
				index[i] = rs1.getInt(1);
				date[i] = rs1.getString(3);
				ptma[i] = rs1.getFloat(4);
				dsi[i] = rs1.getFloat(5);
				finalPrice[i] = rs1.getFloat(6);
				highPrice[i] = rs1.getFloat(7);
				lowPrice[i] = rs1.getFloat(8);
				openPrice[i] = rs1.getFloat(9);
				ptmav[i] = rs1.getFloat(10);
				dsi5[i] = rs1.getFloat(11);
				acptmav[i] = rs1.getFloat(12);

				if (index[i] == 34353 || index[i] == 34470) {
					System.out.println("First positive");
				}

				if (fCount == 1) {
					fNPrice = finalPrice[i];
					fCountTrack = fCountTrack + 1;
				}
				if (fTrue && (finalPrice[i] < minPriceF1)) {
					minPriceF1 = finalPrice[i];
				}

				if (fTrue && (finalPrice[i] > maxPriceF1)) {
					maxPriceF1 = finalPrice[i];
				}

				if (fCount > 1 && (finalPrice[i] < minPriceF2)) {
					minPriceF2 = finalPrice[i];
				}

				if (fCount > 1 && (finalPrice[i] > maxPriceF2)) {
					maxPriceF2 = finalPrice[i];
				}

				if (fTrue && (finalPrice[i] > fPrice)) {
					abovePriceCount1++;
				}
				if (fTrue && (finalPrice[i] > fNPrice && fNPrice > 0.01f)) {
					abovePriceCount2++;
				}

				// ptmaAvg>0 let us avoid high risk market adjustment period
				if (fCount == checkDateCount5 && ptmaAvg > 0) {
					boolean aboveStart = false;
					if (finalPrice[i] > fPrice)
						aboveStart = true;

					float cRate = 100.0f * (finalPrice[i] - fPrice) / fPrice;
					float maxLoss = 100.0f * (minPriceF1 - fPrice) / fPrice;

					totalCount++;
					if (cRate > -1.0f) {
						successCount51++;
						totalReturn = totalReturn * 1.06f;
					} else {
						totalReturn = totalReturn * 0.7f;
					}

					if (maxLoss < -1.0f)
						failedCount51++;

					float maxGain = 100.0f * (maxPriceF1 - fPrice) / fPrice;
					System.out.println(fIndex + "(startIndex) " + fDate
							+ "(date) " + " " + daysSinceDSIMin
							+ "(daysSinceMin) " + minDSI + "(minDSI) "
							+ minDSIDate + "(minDSIDate) " + minDSIIndex
							+ "(minDSIIndex) " + checkDateCount5
							+ "days AVGDSI " + fAvgDSI / aboveMinCount
							+ " DSI min " + fMinDSI + " maxDSI " + fMaxDSI
							+ " day " + checkDateCount5 + ">start "
							+ aboveStart + "(" + cRate + ") avg PTMA "
							+ ptmaAvg + "(" + ptmaMin + "," + ptmaMax + ") "
							+ " aboveStartCount " + abovePriceCount1
							+ " maxLoss " + maxLoss + " maxGain " + maxGain);
				}

				if (fTrue && ptmaAvg > 0) {
					float cRate = 100.0f * (finalPrice[i] - fPrice) / fPrice;
					System.out.println("Change1 at " + fCount + " is " + cRate);
				}
				if (fTrue && fCount > 1 && ptmaAvg > 0) {
					float cRate = 100.0f * (finalPrice[i] - fNPrice) / fNPrice;
					System.out.println("Change2 at " + fCount + " is " + cRate);
				}
				// ptmaAvg>0 (sum) let us avoid high risk market adjustment
				// period
				if (fCount == checkDateCount5 + 1 && ptmaAvg > 0) {
					boolean aboveStart = false;
					if (finalPrice[i] > fNPrice)
						aboveStart = true;
					float maxLoss = 100.0f * (minPriceF2 - fNPrice) / fNPrice;
					float maxGain = 100.0f * (maxPriceF2 - fNPrice) / fNPrice;
					float cRate = 100.0f * (finalPrice[i] - fNPrice) / fNPrice;

					if (cRate > -1.0f)
						successCount52++;

					if (maxLoss < -1.0f)
						failedCount52++;
					System.out.println(fIndex + "(startIndex) " + fDate
							+ "(date) " + " " + daysSinceDSIMin
							+ "(daysSinceMin) " + minDSI + "(minDSI) "
							+ minDSIDate + "(minDSIDate) " + minDSIIndex
							+ "(minDSIIndex) " + checkDateCount5
							+ "days AVGDSI " + fAvgDSI / aboveMinCount
							+ " DSI min " + fMinDSI + " maxDSI " + fMaxDSI
							+ " day " + checkDateCount5 + ">start "
							+ aboveStart + "(" + cRate + ") avg PTMA "
							+ ptmaAvg + "(" + ptmaMin + "," + ptmaMax + ") "
							+ " aboveStartCount " + abovePriceCount2
							+ " maxLoss " + maxLoss + " maxGain " + maxGain);
				}

				// ptmaAvg>0 let us avoid high risk market adjustment period
				if (fCount == checkDateCount10 && ptmaAvg > 0) {
					boolean aboveStart = false;
					if (finalPrice[i] > fPrice)
						aboveStart = true;
					float maxLoss = 100.0f * (minPriceF1 - fPrice) / fPrice;
					float maxGain = 100.0f * (maxPriceF1 - fPrice) / fPrice;
					float cRate = 100.0f * (finalPrice[i] - fPrice) / fPrice;

					if (cRate > -1.0f)
						successCount101++;

					if (maxLoss < -1.0f)
						failedCount101++;
					System.out.println(fIndex + "(startIndex) " + fDate
							+ "(date) " + " " + daysSinceDSIMin
							+ "(daysSinceMin) " + minDSI + "(minDSI) "
							+ minDSIDate + "(minDSIDate) " + minDSIIndex
							+ "(minDSIIndex) " + checkDateCount5
							+ "days AVGDSI " + fAvgDSI / aboveMinCount
							+ " DSI min " + fMinDSI + " maxDSI " + fMaxDSI
							+ " day " + checkDateCount10 + ">start "
							+ aboveStart + "(" + cRate + ") avg PTMA "
							+ ptmaAvg + "(" + ptmaMin + "," + ptmaMax + ") "
							+ " aboveStartCount " + abovePriceCount1
							+ " maxLoss " + maxLoss + " maxGain " + maxGain);

				}

				// ptmaAvg>0 let us avoid high risk market adjustment period
				if (fCount == checkDateCount10 + 1 && ptmaAvg > 0) {
					boolean aboveStart = false;
					if (finalPrice[i] > fNPrice)
						aboveStart = true;
					float maxLoss = 100.0f * (minPriceF2 - fNPrice) / fNPrice;
					float maxGain = 100.0f * (maxPriceF2 - fNPrice) / fNPrice;

					float cRate = 100.0f * (finalPrice[i] - fNPrice) / fNPrice;

					if (cRate > -1.0f)
						successCount102++;

					if (maxLoss < -1.0f)
						failedCount102++;
					System.out.println(fIndex + "(startIndex) " + fDate
							+ "(date) " + " " + daysSinceDSIMin
							+ "(daysSinceMin) " + minDSI + "(minDSI) "
							+ minDSIDate + "(minDSIDate) " + minDSIIndex
							+ "(minDSIIndex) " + checkDateCount5
							+ "days AVGDSI " + fAvgDSI / aboveMinCount
							+ " DSI min " + fMinDSI + " maxDSI " + fMaxDSI
							+ " day " + checkDateCount10 + ">start "
							+ aboveStart + "(" + cRate + ")" + ") avg PTMA "
							+ ptmaAvg + "(" + ptmaMin + "," + ptmaMax + ") "
							+ " aboveStartCount " + abovePriceCount2
							+ " maxLoss " + maxLoss + " maxGain " + maxGain);

					// reset
					divStart = false;
					minStart = false;
					stopCounting = false;
					minDSI = 10000.0f;
					minDSIIndex = 0;
					minDSIDate = "";
					daysSinceDSIMin = 0;
					fAvgDSI = 0.0f;
					fMinDSI = 0.0f;
					fMaxDSI = 0.0f;
					maxDSI = 0.0f;
					fIndex = 0;
					fDate = "";
					fPrice = 0.0f;
					fNPrice = 0.0f;
					divStartIndex = 0;
					fTrue = false;
					abovePriceCount1 = 0;
					abovePriceCount2 = 0;
					minPriceF1 = 0.0f;
					minPriceF2 = 0.0f;
					maxPriceF1 = 0.0f;
					maxPriceF2 = 0.0f;
					fCount = 0;
					aboveMin = 0;
					ptmaAvg = 0.0f;
					ptmaMin = 0.0f;
					ptmaMax = 0.0f;

					if (fCountTrack % 10 == 0) {
						System.out.println("Temp stop");
					}
					if (fCountTrack > 100) {
						System.out.println("Target 51 success count "
								+ successCount51 + " out of " + totalCount
								+ " success rate " + 100.0f
								* (successCount51 * 1.0f) / (totalCount * 1.0f)
								+ " totalReturn " + totalReturn);

						System.out.println("Target 51 failed count "
								+ failedCount51 + " out of " + totalCount
								+ " success rate " + 100.0f
								* (failedCount51 * 1.0f) / (totalCount * 1.0f));

						System.out
								.println("Target 52 success count "
										+ successCount52 + " out of "
										+ totalCount + " success rate "
										+ 100.0f * (successCount52 * 1.0f)
										/ (totalCount * 1.0f));

						System.out.println("Target 52 failed count "
								+ failedCount52 + " out of " + totalCount
								+ " success rate " + 100.0f
								* (failedCount52 * 1.0f) / (totalCount * 1.0f));

						System.out.println("Target 101 success count "
								+ successCount101 + " out of " + totalCount
								+ " success rate " + 100.0f
								* (successCount101 * 1.0f)
								/ (totalCount * 1.0f));

						System.out
								.println("Target 101 failed count "
										+ failedCount101 + " out of "
										+ totalCount + " success rate "
										+ 100.0f * (failedCount101 * 1.0f)
										/ (totalCount * 1.0f));

						System.out.println("Target 102 success count "
								+ successCount102 + " out of " + totalCount
								+ " success rate " + 100.0f
								* (successCount102 * 1.0f)
								/ (totalCount * 1.0f));

						System.out
								.println("Target 102 failed count "
										+ failedCount102 + " out of "
										+ totalCount + " success rate "
										+ 100.0f * (failedCount102 * 1.0f)
										/ (totalCount * 1.0f));

						break;
					}
				}
				if (fCount == checkDateCount10 + 1) {
					System.out.println("ptmaAvg<0 case");

					// reset
					divStart = false;
					minStart = false;
					stopCounting = false;
					minDSI = 10000.0f;
					minDSIIndex = 0;
					minDSIDate = "";
					daysSinceDSIMin = 0;
					fAvgDSI = 0.0f;
					fMinDSI = 0.0f;
					fMaxDSI = 0.0f;
					maxDSI = 0.0f;
					fIndex = 0;
					fDate = "";
					fPrice = 0.0f;
					fNPrice = 0.0f;
					divStartIndex = 0;
					fTrue = false;
					abovePriceCount1 = 0;
					abovePriceCount2 = 0;
					minPriceF1 = 0.0f;
					minPriceF2 = 0.0f;
					maxPriceF1 = 0.0f;
					maxPriceF2 = 0.0f;
					fCount = 0;
					aboveMin = 0;
					ptmaAvg = 0.0f;
					ptmaMin = 0.0f;
					ptmaMax = 0.0f;
				}

				if (loopCount == 0) {
					ptmaMin = ptma[i];
					ptmaMax = ptma[i];
					dsiMin = dsi[i];
					dsiMax = dsi[i];
					finalPriceMin = finalPrice[i];
					finalPriceMax = finalPrice[i];
					highPriceMin = highPrice[i];
					highPriceMax = highPrice[i];
					lowPriceMin = lowPrice[i];
					lowPriceMax = lowPrice[i];
					openPriceMin = openPrice[i];
					openPriceMax = openPrice[i];
					ptmavMin = ptmav[i];
					ptmavMax = ptmav[i];
				}

				if (!fTrue) {
					if (dsi[i] < diValue) {
						divStart = true;
						divStartIndex = index[i];
					}
					if (divStart && dsi[i] < minValue) {
						minStart = true;

					}
					if (minStart && dsi[i] < minDSI) {
						if (minDSI < 9999.0f)
							daysSinceDSIMin = 0;

						minDSI = dsi[i];
						minDSIIndex = index[i];
						minDSIDate = date[i];

					}
					daysSinceDSIMin = daysSinceDSIMin + 1;

					if (minStart && dsi[i] > dsiLine) {
						aboveMin++;
					} else if (minStart && dsi[i] < dsiLine) {
						aboveMin = 0;
						abovePriceCount1 = 0;
						abovePriceCount2 = 0;
						minPriceF1 = 100000.0f;
						minPriceF2 = 100000.0f;
						maxPriceF1 = 0.0f;
						maxPriceF2 = 0.0f;

					}

					if ((aboveMin == aboveMinCount) && loopCount >= days - 1) {
						fIndex = index[i];
						fPrice = finalPrice[i];
						fDate = date[i];
						int count = 0;
						int arrayIndex = 0;
						fMinDSI = 1000.0f;
						fMaxDSI = 0.0f;
						for (count = 0; count < aboveMinCount; count++) {
							arrayIndex = i - count;
							if (arrayIndex < 0)
								arrayIndex = arrayIndex + days;

							fAvgDSI = fAvgDSI + dsi[arrayIndex];
							if (dsi[arrayIndex] < fMinDSI)
								fMinDSI = dsi[arrayIndex];
							if (dsi[arrayIndex] > fMaxDSI)
								fMaxDSI = dsi[arrayIndex];
						}
						ptmaAvg = 0.0f;
						ptmaMin = 0.0f;
						ptmaMax = 0.0f;
						for (int z = 0; z < days; z++) {
							if (z == 0) {
								ptmaMin = ptma[z];
								ptmaMax = ptma[z];
							}
							ptmaAvg = ptmaAvg + ptma[z];
							if (ptma[z] < ptmaMin) {
								ptmaMin = ptma[z];
							}
							if (ptma[z] > ptmaMax) {
								ptmaMax = ptma[z];
							}
						}
						fTrue = true;
					} else if ((aboveMin == aboveMinCount)
							&& loopCount < days - 1) {
						divStart = false;
						minStart = false;
						stopCounting = false;
						minDSI = 10000.0f;
						minDSIIndex = 0;
						minDSIDate = "";
						daysSinceDSIMin = 0;
						daysSinceDSIMin = 0;
						fAvgDSI = 0.0f;
						fMinDSI = 0.0f;
						fMaxDSI = 0.0f;
						maxDSI = 0.0f;
						fIndex = 0;
						fDate = "";
						fPrice = 0.0f;
						fNPrice = 0.0f;
						divStartIndex = 0;
						fTrue = false;
						abovePriceCount1 = 0;
						abovePriceCount2 = 0;
						minPriceF1 = 0.0f;
						minPriceF2 = 0.0f;
						maxPriceF1 = 0.0f;
						maxPriceF2 = 0.0f;
						fCount = 0;
						aboveMin = 0;
						ptmaAvg = 0.0f;
						ptmaMin = 0.0f;
						ptmaMax = 0.0f;
					}
				}

				// System.out.println(index[i]+" "+date[i]+" "+ptma[i]+" "+dsi[i]+" "+finalPrice[i]
				// +" "+highPrice[i]+" "+lowPrice[i]+" "+openPrice[i]+" "+ptmav[i]);
				loopCount++;

			}

			// SQL =
			// "update BPMADMIN.INTRADAYSTOCKS  set GX100=1 where  seqIndex="
			// + startIndexLast + " and symbol='" + symbol + "'";
			// stmt2.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		System.out.println("Target 51 success count " + successCount51
				+ " out of " + totalCount + " success rate " + 100.0f
				* (successCount51 * 1.0f) / (totalCount * 1.0f)
				+ " totalReturn " + totalReturn);

		System.out.println("Target 51 failed count " + failedCount51
				+ " out of " + totalCount + " failed rate " + 100.0f
				* (failedCount51 * 1.0f) / (totalCount * 1.0f));

		System.out.println("Target 52 success count " + successCount52
				+ " out of " + totalCount + " success rate " + 100.0f
				* (successCount52 * 1.0f) / (totalCount * 1.0f));

		System.out.println("Target 52 failed count " + failedCount52
				+ " out of " + totalCount + " failed rate " + 100.0f
				* (failedCount52 * 1.0f) / (totalCount * 1.0f));

		System.out.println("Target 101 success count " + successCount101
				+ " out of " + totalCount + " success rate " + 100.0f
				* (successCount101 * 1.0f) / (totalCount * 1.0f));

		System.out.println("Target 101 failed count " + failedCount101
				+ " out of " + totalCount + " failed rate " + 100.0f
				* (failedCount101 * 1.0f) / (totalCount * 1.0f));

		System.out.println("Target 102 success count " + successCount102
				+ " out of " + totalCount + " success rate " + 100.0f
				* (successCount102 * 1.0f) / (totalCount * 1.0f));

		System.out.println("Target 102 failed count " + failedCount102
				+ " out of " + totalCount + " failed rate " + 100.0f
				* (failedCount102 * 1.0f) / (totalCount * 1.0f));

		return result;
	}

	public static boolean getTrendBasedonDSINew(String symbol, int maxIndex) {
		boolean result = false;
		getBothEnds(symbol);
		int maxFCount = 3;
		// success mean above the target closing price
		int successCount51 = 0;
		// failed mean below the target closing price 1%
		int failedCount51 = 0;
		int successCount52 = 0;
		int failedCount52 = 0;
		int successCount101 = 0;
		int failedCount101 = 0;
		int successCount102 = 0;
		int failedCount102 = 0;
		int totalCount = 0;
		float totalReturn = 1.0f;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "SELECT SEQINDEX, SYMBOL, VARCHAR_FORMAT(CURRENTDATE,'MM-DD-YYYY') AS VCHAR_FORMATED, "
					+ "PTMA,DSI,FINALPRICE,HIGHPRICE,LOWPRICE,OPENPRICE,PTMAV,ADJUSTEDPRICE "
					+ "FROM BPMADMIN.INTRADAYSTOCKS where symbol='"
					+ symbol
					+ "'  and seqindex<="
					+ maxIndex
					+ " and DSI<>0 and ptma<>0 order by seqIndex asc";
			rs1 = stmt1.executeQuery(SQL);
			int days = 44;
			int[] index = new int[days];
			String[] date = new String[days];
			float[] ptma = new float[days];
			float[] dsi = new float[days];
			float dsiAvg = 0.0f, dsiMin = 0.0f, dsiMax = 0.0f;
			float[] finalPrice = new float[days];
			float finalPriceAvg = 0.0f, finalPriceMin = 0.0f, finalPriceMax = 0.0f;
			float[] highPrice = new float[days];
			float highPriceAvg = 0.0f, highPriceMin = 0.0f, highPriceMax = 0.0f;
			float[] lowPrice = new float[days];
			float lowPriceAvg = 0.0f, lowPriceMin = 0.0f, lowPriceMax = 0.0f;
			float[] openPrice = new float[days];
			float openPriceAvg = 0.0f, openPriceMin = 0.0f, openPriceMax = 0.0f;
			float[] ptmav = new float[days];
			float ptmavAvg = 0.0f, ptmavMin = 0.0f, ptmavMax = 0.0f;
			float ptmaAvg = 0.0f, ptmaMin = 0.0f, ptmaMax = 0.0f;

			int loopCount = 0;
			System.out
					.println("Index   date   ptma  dsi  final high  low  open  ptmav");
			boolean start = false;
			int aboveMin = 0;
			// days stay above (DSI) dsiLine value
			int aboveMinCount = 5;
			// this is the value we start consider count aboveMinCount
			float dsiLine = 80.0f;
			// value below which is considered a dip
			float diValue = 60.0f;
			// max of minimum value for dip
			float minValue = 30.0f;
			boolean divStart = false;
			boolean minStart = false;
			boolean stopCounting = false;
			float minDSI = 10000.0f;
			int minDSIIndex = 0;
			String minDSIDate = "";
			int daysSinceDSIMin = 0;
			float fAvgDSI = 0.0f;
			float fMinDSI = 0.0f;
			float fMaxDSI = 0.0f;
			float maxDSI = 0.0f;
			int fIndex = 0;
			String fDate = "";
			float fPrice = 0.0f;
			float fNPrice = 0.0f;
			int divStartIndex = 0;
			boolean fTrue = false;
			int fCount = 0;
			int abovePriceCount1 = 0;
			int abovePriceCount2 = 0;
			float minPriceF1 = 10000000.0f;
			float maxPriceF1 = 0.0f;
			float minPriceF2 = 10000000.0f;
			float maxPriceF2 = 0.0f;
			int checkDateCount5 = 5;
			int checkDateCount10 = 10;
			int fCountTrack = 0;

			while (rs1.next()) {

				if (fTrue) {
					fCount++;
				}

				int i = loopCount % days;
				index[i] = rs1.getInt(1);
				date[i] = rs1.getString(3);
				ptma[i] = rs1.getFloat(4);
				dsi[i] = rs1.getFloat(5);
				finalPrice[i] = rs1.getFloat(6);
				highPrice[i] = rs1.getFloat(7);
				lowPrice[i] = rs1.getFloat(8);
				openPrice[i] = rs1.getFloat(9);
				ptmav[i] = rs1.getFloat(10);

				if (index[i] == 34353 || index[i] == 34470) {
					System.out.println("First positive");
				}

				if (fCount == 1) {
					fNPrice = finalPrice[i];
					fCountTrack = fCountTrack + 1;
				}
				if (fTrue && (finalPrice[i] < minPriceF1)) {
					minPriceF1 = finalPrice[i];
				}

				if (fTrue && (finalPrice[i] > maxPriceF1)) {
					maxPriceF1 = finalPrice[i];
				}

				if (fCount > 1 && (finalPrice[i] < minPriceF2)) {
					minPriceF2 = finalPrice[i];
				}

				if (fCount > 1 && (finalPrice[i] > maxPriceF2)) {
					maxPriceF2 = finalPrice[i];
				}

				if (fTrue && (finalPrice[i] > fPrice)) {
					abovePriceCount1++;
				}
				if (fTrue && (finalPrice[i] > fNPrice && fNPrice > 0.01f)) {
					abovePriceCount2++;
				}

				// ptmaAvg>0 let us avoid high risk market adjustment period
				if (fCount == checkDateCount5 && ptmaAvg > 0) {
					boolean aboveStart = false;
					if (finalPrice[i] > fPrice)
						aboveStart = true;

					float cRate = 100.0f * (finalPrice[i] - fPrice) / fPrice;
					float maxLoss = 100.0f * (minPriceF1 - fPrice) / fPrice;

					totalCount++;
					if (cRate > -1.0f) {
						successCount51++;
						totalReturn = totalReturn * 1.06f;
					} else {
						totalReturn = totalReturn * 0.7f;
					}

					if (maxLoss < -1.0f)
						failedCount51++;

					float maxGain = 100.0f * (maxPriceF1 - fPrice) / fPrice;
					System.out.println(fIndex + "(startIndex) " + fDate
							+ "(date) " + " " + daysSinceDSIMin
							+ "(daysSinceMin) " + minDSI + "(minDSI) "
							+ minDSIDate + "(minDSIDate) " + minDSIIndex
							+ "(minDSIIndex) " + checkDateCount5
							+ "days AVGDSI " + fAvgDSI / aboveMinCount
							+ " DSI min " + fMinDSI + " maxDSI " + fMaxDSI
							+ " day " + checkDateCount5 + ">start "
							+ aboveStart + "(" + cRate + ") avg PTMA "
							+ ptmaAvg + "(" + ptmaMin + "," + ptmaMax + ") "
							+ " aboveStartCount " + abovePriceCount1
							+ " maxLoss " + maxLoss + " maxGain " + maxGain);
				}

				if (fTrue && ptmaAvg > 0) {
					float cRate = 100.0f * (finalPrice[i] - fPrice) / fPrice;
					System.out.println("Change1 at " + fCount + " is " + cRate);
				}
				if (fTrue && fCount > 1 && ptmaAvg > 0) {
					float cRate = 100.0f * (finalPrice[i] - fNPrice) / fNPrice;
					System.out.println("Change2 at " + fCount + " is " + cRate);
				}
				// ptmaAvg>0 (sum) let us avoid high risk market adjustment
				// period
				if (fCount == checkDateCount5 + 1 && ptmaAvg > 0) {
					boolean aboveStart = false;
					if (finalPrice[i] > fNPrice)
						aboveStart = true;
					float maxLoss = 100.0f * (minPriceF2 - fNPrice) / fNPrice;
					float maxGain = 100.0f * (maxPriceF2 - fNPrice) / fNPrice;
					float cRate = 100.0f * (finalPrice[i] - fNPrice) / fNPrice;

					if (cRate > -1.0f)
						successCount52++;

					if (maxLoss < -1.0f)
						failedCount52++;
					System.out.println(fIndex + "(startIndex) " + fDate
							+ "(date) " + " " + daysSinceDSIMin
							+ "(daysSinceMin) " + minDSI + "(minDSI) "
							+ minDSIDate + "(minDSIDate) " + minDSIIndex
							+ "(minDSIIndex) " + checkDateCount5
							+ "days AVGDSI " + fAvgDSI / aboveMinCount
							+ " DSI min " + fMinDSI + " maxDSI " + fMaxDSI
							+ " day " + checkDateCount5 + ">start "
							+ aboveStart + "(" + cRate + ") avg PTMA "
							+ ptmaAvg + "(" + ptmaMin + "," + ptmaMax + ") "
							+ " aboveStartCount " + abovePriceCount2
							+ " maxLoss " + maxLoss + " maxGain " + maxGain);
				}

				// ptmaAvg>0 let us avoid high risk market adjustment period
				if (fCount == checkDateCount10 && ptmaAvg > 0) {
					boolean aboveStart = false;
					if (finalPrice[i] > fPrice)
						aboveStart = true;
					float maxLoss = 100.0f * (minPriceF1 - fPrice) / fPrice;
					float maxGain = 100.0f * (maxPriceF1 - fPrice) / fPrice;
					float cRate = 100.0f * (finalPrice[i] - fPrice) / fPrice;

					if (cRate > -1.0f)
						successCount101++;

					if (maxLoss < -1.0f)
						failedCount101++;
					System.out.println(fIndex + "(startIndex) " + fDate
							+ "(date) " + " " + daysSinceDSIMin
							+ "(daysSinceMin) " + minDSI + "(minDSI) "
							+ minDSIDate + "(minDSIDate) " + minDSIIndex
							+ "(minDSIIndex) " + checkDateCount5
							+ "days AVGDSI " + fAvgDSI / aboveMinCount
							+ " DSI min " + fMinDSI + " maxDSI " + fMaxDSI
							+ " day " + checkDateCount10 + ">start "
							+ aboveStart + "(" + cRate + ") avg PTMA "
							+ ptmaAvg + "(" + ptmaMin + "," + ptmaMax + ") "
							+ " aboveStartCount " + abovePriceCount1
							+ " maxLoss " + maxLoss + " maxGain " + maxGain);

				}

				// ptmaAvg>0 let us avoid high risk market adjustment period
				if (fCount == checkDateCount10 + 1 && ptmaAvg > 0) {
					boolean aboveStart = false;
					if (finalPrice[i] > fNPrice)
						aboveStart = true;
					float maxLoss = 100.0f * (minPriceF2 - fNPrice) / fNPrice;
					float maxGain = 100.0f * (maxPriceF2 - fNPrice) / fNPrice;

					float cRate = 100.0f * (finalPrice[i] - fNPrice) / fNPrice;

					if (cRate > -1.0f)
						successCount102++;

					if (maxLoss < -1.0f)
						failedCount102++;
					System.out.println(fIndex + "(startIndex) " + fDate
							+ "(date) " + " " + daysSinceDSIMin
							+ "(daysSinceMin) " + minDSI + "(minDSI) "
							+ minDSIDate + "(minDSIDate) " + minDSIIndex
							+ "(minDSIIndex) " + checkDateCount5
							+ "days AVGDSI " + fAvgDSI / aboveMinCount
							+ " DSI min " + fMinDSI + " maxDSI " + fMaxDSI
							+ " day " + checkDateCount10 + ">start "
							+ aboveStart + "(" + cRate + ")" + ") avg PTMA "
							+ ptmaAvg + "(" + ptmaMin + "," + ptmaMax + ") "
							+ " aboveStartCount " + abovePriceCount2
							+ " maxLoss " + maxLoss + " maxGain " + maxGain);

					// reset
					divStart = false;
					minStart = false;
					stopCounting = false;
					minDSI = 10000.0f;
					minDSIIndex = 0;
					minDSIDate = "";
					daysSinceDSIMin = 0;
					fAvgDSI = 0.0f;
					fMinDSI = 0.0f;
					fMaxDSI = 0.0f;
					maxDSI = 0.0f;
					fIndex = 0;
					fDate = "";
					fPrice = 0.0f;
					fNPrice = 0.0f;
					divStartIndex = 0;
					fTrue = false;
					abovePriceCount1 = 0;
					abovePriceCount2 = 0;
					minPriceF1 = 0.0f;
					minPriceF2 = 0.0f;
					maxPriceF1 = 0.0f;
					maxPriceF2 = 0.0f;
					fCount = 0;
					aboveMin = 0;
					ptmaAvg = 0.0f;
					ptmaMin = 0.0f;
					ptmaMax = 0.0f;

					if (fCountTrack % 10 == 0) {
						System.out.println("Temp stop");
					}
					if (fCountTrack > 100) {
						System.out.println("Target 51 success count "
								+ successCount51 + " out of " + totalCount
								+ " success rate " + 100.0f
								* (successCount51 * 1.0f) / (totalCount * 1.0f)
								+ " totalReturn " + totalReturn);

						System.out.println("Target 51 failed count "
								+ failedCount51 + " out of " + totalCount
								+ " success rate " + 100.0f
								* (failedCount51 * 1.0f) / (totalCount * 1.0f));

						System.out
								.println("Target 52 success count "
										+ successCount52 + " out of "
										+ totalCount + " success rate "
										+ 100.0f * (successCount52 * 1.0f)
										/ (totalCount * 1.0f));

						System.out.println("Target 52 failed count "
								+ failedCount52 + " out of " + totalCount
								+ " success rate " + 100.0f
								* (failedCount52 * 1.0f) / (totalCount * 1.0f));

						System.out.println("Target 101 success count "
								+ successCount101 + " out of " + totalCount
								+ " success rate " + 100.0f
								* (successCount101 * 1.0f)
								/ (totalCount * 1.0f));

						System.out
								.println("Target 101 failed count "
										+ failedCount101 + " out of "
										+ totalCount + " success rate "
										+ 100.0f * (failedCount101 * 1.0f)
										/ (totalCount * 1.0f));

						System.out.println("Target 102 success count "
								+ successCount102 + " out of " + totalCount
								+ " success rate " + 100.0f
								* (successCount102 * 1.0f)
								/ (totalCount * 1.0f));

						System.out
								.println("Target 102 failed count "
										+ failedCount102 + " out of "
										+ totalCount + " success rate "
										+ 100.0f * (failedCount102 * 1.0f)
										/ (totalCount * 1.0f));

						break;
					}
				}
				if (fCount == checkDateCount10 + 1) {
					System.out.println("ptmaAvg<0 case");

					// reset
					divStart = false;
					minStart = false;
					stopCounting = false;
					minDSI = 10000.0f;
					minDSIIndex = 0;
					minDSIDate = "";
					daysSinceDSIMin = 0;
					fAvgDSI = 0.0f;
					fMinDSI = 0.0f;
					fMaxDSI = 0.0f;
					maxDSI = 0.0f;
					fIndex = 0;
					fDate = "";
					fPrice = 0.0f;
					fNPrice = 0.0f;
					divStartIndex = 0;
					fTrue = false;
					abovePriceCount1 = 0;
					abovePriceCount2 = 0;
					minPriceF1 = 0.0f;
					minPriceF2 = 0.0f;
					maxPriceF1 = 0.0f;
					maxPriceF2 = 0.0f;
					fCount = 0;
					aboveMin = 0;
					ptmaAvg = 0.0f;
					ptmaMin = 0.0f;
					ptmaMax = 0.0f;
				}

				if (loopCount == 0) {
					ptmaMin = ptma[i];
					ptmaMax = ptma[i];
					dsiMin = dsi[i];
					dsiMax = dsi[i];
					finalPriceMin = finalPrice[i];
					finalPriceMax = finalPrice[i];
					highPriceMin = highPrice[i];
					highPriceMax = highPrice[i];
					lowPriceMin = lowPrice[i];
					lowPriceMax = lowPrice[i];
					openPriceMin = openPrice[i];
					openPriceMax = openPrice[i];
					ptmavMin = ptmav[i];
					ptmavMax = ptmav[i];
				}

				if (!fTrue) {
					if (dsi[i] < diValue) {
						divStart = true;
						divStartIndex = index[i];
					}
					if (divStart && dsi[i] < minValue) {
						minStart = true;

					}
					if (minStart && dsi[i] < minDSI) {
						if (minDSI < 9999.0f)
							daysSinceDSIMin = 0;

						minDSI = dsi[i];
						minDSIIndex = index[i];
						minDSIDate = date[i];

					}
					daysSinceDSIMin = daysSinceDSIMin + 1;

					if (minStart && dsi[i] > dsiLine) {
						aboveMin++;
					} else if (minStart && dsi[i] < dsiLine) {
						aboveMin = 0;
						abovePriceCount1 = 0;
						abovePriceCount2 = 0;
						minPriceF1 = 100000.0f;
						minPriceF2 = 100000.0f;
						maxPriceF1 = 0.0f;
						maxPriceF2 = 0.0f;

					}

					if ((aboveMin == aboveMinCount) && loopCount >= days - 1) {
						fIndex = index[i];
						fPrice = finalPrice[i];
						fDate = date[i];
						int count = 0;
						int arrayIndex = 0;
						fMinDSI = 1000.0f;
						fMaxDSI = 0.0f;
						for (count = 0; count < aboveMinCount; count++) {
							arrayIndex = i - count;
							if (arrayIndex < 0)
								arrayIndex = arrayIndex + days;

							fAvgDSI = fAvgDSI + dsi[arrayIndex];
							if (dsi[arrayIndex] < fMinDSI)
								fMinDSI = dsi[arrayIndex];
							if (dsi[arrayIndex] > fMaxDSI)
								fMaxDSI = dsi[arrayIndex];
						}
						ptmaAvg = 0.0f;
						ptmaMin = 0.0f;
						ptmaMax = 0.0f;
						for (int z = 0; z < days; z++) {
							if (z == 0) {
								ptmaMin = ptma[z];
								ptmaMax = ptma[z];
							}
							ptmaAvg = ptmaAvg + ptma[z];
							if (ptma[z] < ptmaMin) {
								ptmaMin = ptma[z];
							}
							if (ptma[z] > ptmaMax) {
								ptmaMax = ptma[z];
							}
						}
						fTrue = true;
					} else if ((aboveMin == aboveMinCount)
							&& loopCount < days - 1) {
						divStart = false;
						minStart = false;
						stopCounting = false;
						minDSI = 10000.0f;
						minDSIIndex = 0;
						minDSIDate = "";
						daysSinceDSIMin = 0;
						daysSinceDSIMin = 0;
						fAvgDSI = 0.0f;
						fMinDSI = 0.0f;
						fMaxDSI = 0.0f;
						maxDSI = 0.0f;
						fIndex = 0;
						fDate = "";
						fPrice = 0.0f;
						fNPrice = 0.0f;
						divStartIndex = 0;
						fTrue = false;
						abovePriceCount1 = 0;
						abovePriceCount2 = 0;
						minPriceF1 = 0.0f;
						minPriceF2 = 0.0f;
						maxPriceF1 = 0.0f;
						maxPriceF2 = 0.0f;
						fCount = 0;
						aboveMin = 0;
						ptmaAvg = 0.0f;
						ptmaMin = 0.0f;
						ptmaMax = 0.0f;
					}
				}

				// System.out.println(index[i]+" "+date[i]+" "+ptma[i]+" "+dsi[i]+" "+finalPrice[i]
				// +" "+highPrice[i]+" "+lowPrice[i]+" "+openPrice[i]+" "+ptmav[i]);
				loopCount++;

			}

			// SQL =
			// "update BPMADMIN.INTRADAYSTOCKS  set GX100=1 where  seqIndex="
			// + startIndexLast + " and symbol='" + symbol + "'";
			// stmt2.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		System.out.println("Target 51 success count " + successCount51
				+ " out of " + totalCount + " success rate " + 100.0f
				* (successCount51 * 1.0f) / (totalCount * 1.0f)
				+ " totalReturn " + totalReturn);

		System.out.println("Target 51 failed count " + failedCount51
				+ " out of " + totalCount + " failed rate " + 100.0f
				* (failedCount51 * 1.0f) / (totalCount * 1.0f));

		System.out.println("Target 52 success count " + successCount52
				+ " out of " + totalCount + " success rate " + 100.0f
				* (successCount52 * 1.0f) / (totalCount * 1.0f));

		System.out.println("Target 52 failed count " + failedCount52
				+ " out of " + totalCount + " failed rate " + 100.0f
				* (failedCount52 * 1.0f) / (totalCount * 1.0f));

		System.out.println("Target 101 success count " + successCount101
				+ " out of " + totalCount + " success rate " + 100.0f
				* (successCount101 * 1.0f) / (totalCount * 1.0f));

		System.out.println("Target 101 failed count " + failedCount101
				+ " out of " + totalCount + " failed rate " + 100.0f
				* (failedCount101 * 1.0f) / (totalCount * 1.0f));

		System.out.println("Target 102 success count " + successCount102
				+ " out of " + totalCount + " success rate " + 100.0f
				* (successCount102 * 1.0f) / (totalCount * 1.0f));

		System.out.println("Target 102 failed count " + failedCount102
				+ " out of " + totalCount + " failed rate " + 100.0f
				* (failedCount102 * 1.0f) / (totalCount * 1.0f));

		return result;
	}

	public static boolean isStockSplitted(String symbol) {
		boolean result = false;
		getBothEnds(symbol);

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select GX100 from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' and  SEQINDEX =" + (startIndexLast + 1);

			rs1 = stmt1.executeQuery(SQL);

			int gx100t = 0;
			if (rs1.next()) {

				gx100t = rs1.getInt(1);
			}

			SQL = "select GX100 from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' and  SEQINDEX =" + startIndexLast;

			rs1 = stmt1.executeQuery(SQL);

			int gx100 = 0;
			float p1 = 0.0f;
			float p2 = 0.0f;

			if (rs1.next()) {

				gx100 = rs1.getInt(1);

				if (gx100 > 20) {

					SQL = "select  ADJUSTEDPRICE,SEQINDEX from  BPMADMIN.INTRADAYSTOCKS   where symbol='"
							+ symbol
							+ "' and seqindex<="
							+ endIndexLast
							+ " and seqindex>= "
							+ (endIndexLast - gx100)
							+ " order by seqIndex asc";

					rs2 = stmt2.executeQuery(SQL);

					while (rs2.next()) {
						if (p1 < 0.001f) {
							p1 = rs2.getFloat(1);
						} else if (p2 < 0.001f) {
							p2 = rs2.getFloat(1);
						} else {
							if (p1 > 1.3f * p2 || p2 > 1.3f * p1) {
								if (p1 > 10.0f || p2 > 10.0f) {
									result = true;
									System.out.println(symbol
											+ " Split found around "
											+ (rs2.getInt(2) - 1));
									break;
								}
							} else if (p1 > 1.8f * p2 || p2 > 1.8f * p1) {
								if (p1 > 1.0f || p2 > 1.0f) {
									System.out.println(symbol
											+ " Split found around "
											+ (rs2.getInt(2) - 1));
									result = true;
									break;
								}
							} else {
								p1 = p2;
								p2 = rs2.getFloat(1);
							}

						}
					}

					if (!result && gx100 > 20) {
						SQL = "update BPMADMIN.INTRADAYSTOCKS  set GX100=1 where  seqIndex="
								+ startIndexLast
								+ " and symbol='"
								+ symbol
								+ "'";
						stmt2.executeUpdate(SQL);

						SQL = "update BPMADMIN.INTRADAYSTOCKS  set GX100="
								+ (gx100t + gx100) + " where  seqIndex="
								+ (startIndexLast + 1) + " and symbol='"
								+ symbol + "'";
						stmt2.executeUpdate(SQL);
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return result;
	}

	public static boolean verifyStockForSplit(String symbol) {
		int lastCheckedSeqIndex = 10;
		// int lastCheckedSeqIndex = 42080;
		if (hasBeenVerified(symbol)) {
			System.out.println("Stock split has been verified...");
			return false;
		}
		boolean result = false;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null) {
				stmt1 = con.createStatement();
				stmt2 = con.createStatement();
			}

			HttpDownload.downLoadStockHistory(symbol, 1, -1,993);
			Hashtable results = Files.getIndexHistory(symbol);

			if (results == null) {
				return false;
			}

			SQL = "select VARCHAR_FORMAT(CURRENTDATE, 'MM-DD-YYYY'),ADJUSTEDPRICE, seqindex from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' ORDER BY SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);
			int loopCount = 1;
			String date1 = "ABC";
			String date2 = "EFG";
			YStock ys = null;
			float adjustedPrice1 = 0.0f;
			float finalPrice2 = 0.0f;
			boolean cont = true;

			while (cont && rs1.next()) {
				date1 = rs1.getString(1);
				ys = (YStock) results.get("" + loopCount);

				date2 = ys.getDate();

				while (!date1.equalsIgnoreCase(date2) && loopCount < 10) {
					loopCount++;
					ys = (YStock) results.get("" + loopCount);
					date2 = ys.getDate();
				}

				if (date1.equalsIgnoreCase(date2)) {
					adjustedPrice1 = rs1.getFloat(2);
					finalPrice2 = ys.getFinalPrice();
					// System.out.println(date1+" adjusted: "+adjustedPrice1+" finalPrice2: "+finalPrice2);

					if (adjustedPrice1 > 1.01f * finalPrice2
							|| finalPrice2 > 1.01f * adjustedPrice1) {
						if (rs1.getInt(3) > lastCheckedSeqIndex) {
							System.out.println(symbol + " Real split at"
									+ rs1.getInt(3));
							SQL = "DELETE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
									+ symbol + "'";

							stmt1.execute(SQL);
							result = true;

							insertHistoryRecord(results);
							cont = false;

						}
					} else {
						loopCount++;
						// System.out.println("date1 "+date1+" Compared...");
					}

				}

			}

			getBothEnds(symbol);
			SQL = "update BPMADMIN.INTRADAYSTOCKS  set GX100=-999 where  seqIndex="
					+ startIndexLast + " and symbol='" + symbol + "'";
			System.out.println(SQL);
			stmt2.executeUpdate(SQL);
			if (!result) {
				System.out.println(symbol + " no split");
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return result;
	}

	public static void deletePartialRecord(String symbol, long topIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();
			SQL = "delete from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "' and SEQINDEX>" + topIndex;

			stmt1.execute(SQL);

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}

	public static void deleteLastDayRecords(String symbol, long lastIndex) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			String SQL2 = "DELETE from BPMADMIN.INTRADAYSTOCKS WHERE ( SYMBOL='"
					+ symbol + "' OR SYMBOL='"+symbol+"_ABX_' )   AND SEQINDEX>" + lastIndex;

			stmt1.executeUpdate(SQL2);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}
	
	public static void deleteAllRecords(String symbol) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();
			SQL = "delete from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "'";

			stmt1.execute(SQL);

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}

	public static long getCurrentMaxIndex(String symbol) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();
			SQL = "select  max(SeqIndex),min(SeqIndex) from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "'";

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {

				// startIndexLast = rs1.getInt(2);
				currentMaxIndex = rs1.getLong(1);

			}
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
		return currentMaxIndex;
	}

	public static Hashtable getAllCurrentStocksFromEOD() {
		Hashtable allstocks = new Hashtable();
		try {
			if (conEOD == null)
				conEOD = getEODConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1EOD == null)
				stmt1EOD = conEOD.createStatement();
			String SQL2 = "select  max(SeqIndex),min(SeqIndex) from BPMADMIN.YAHOODB   where symbol='AMZN'";

			rs1EOD = stmt1EOD.executeQuery(SQL2);

			int lastIndex = 0;
			while (rs1EOD.next()) {
				lastIndex = rs1EOD.getInt(1);
			}

			SQL2 = "select  SYMBOL from BPMADMIN.YAHOODB where SEQINDEX="
					+ lastIndex;

			rs1EOD = stmt1EOD.executeQuery(SQL2);

			while (rs1EOD.next()) {
				String symnext = rs1EOD.getString(1);
				allstocks.put(symnext, symnext);
			}

			rs1EOD.close();
			rs1EOD = null;
			stmt1EOD.close();
			stmt1EOD = null;
			conEOD.close();
			conEOD = null;

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
		return allstocks;
	}

	public static void getBothEnds(String symbol) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();
			SQL = "select  max(SeqIndex),min(SeqIndex) from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol + "'";

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {

				startIndexLast = rs1.getLong(2);
				endIndexLast = rs1.getLong(1);

			}
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}

	public static void resetStats(int index) {
		maxGainTotal = 0.0f;
		maxLossTotal = 0.0f;
		stockCount = 0;
		seqIndex = index;
	}

	public static void evaluateDSIDiscrepancyNew(String symbol) {

		int days = 30;
		float[] ptma = new float[days];
		float[] dsi = new float[days];
		float[] finalprice = new float[days];
		int[] seqIndex = new int[days];
		String[] date = new String[days];
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "SELECT seqIndex from BPMADMIN.INTRADAYSTOCKS  where   symbol='"
					+ symbol + "' and BBGO<>0 order by seqIndex desc";
			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				int nextSeqIndex = rs1.getInt(1);

				SQL = "SELECT SEQINDEX,VARCHAR_FORMAT(CURRENTDATE,'MM-DD-YYYY') AS VCHAR_FORMATED,PTMA,DSI,FINALPRICE from BPMADMIN.INTRADAYSTOCKS  where   symbol='"
						+ symbol
						+ "' and  SEQINDEX>="
						+ nextSeqIndex
						+ " and SEQINDEX<"
						+ (nextSeqIndex + days * 7 / 5)
						+ " order by seqIndex asc";
				rs2 = stmt2.executeQuery(SQL);
				int loopCount = 0;
				while (rs2.next()) {
					if (loopCount < days) {
						seqIndex[loopCount] = rs2.getInt(1);
						date[loopCount] = rs2.getString(2);
						ptma[loopCount] = rs2.getFloat(3);
						dsi[loopCount] = rs2.getFloat(4);
						finalprice[loopCount] = rs2.getFloat(5);
					} else {
						break;
					}
					loopCount++;
				}

				float ratio = (dsi[1] - dsi[0])
						/ (finalprice[1] - finalprice[0]);

				System.out.println("Start at " + seqIndex[1] + " " + date[1]
						+ " delta(DSI)/delta(price)= " + ratio);
				int startIndex = 2, daysWatched = 10;
				for (int w = startIndex + 1; w <= daysWatched + startIndex; w++) {
					float change = 100.0f
							* (finalprice[w] - finalprice[startIndex])
							/ finalprice[startIndex];
					System.out.println("Day " + (w - startIndex) + " At "
							+ seqIndex[w] + " " + date[w] + " pmta " + ptma[w]
							+ " price change percentage " + change);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void evaluateGain(String symbol, int seqIndex) {
		System.out.println("Evaluting " + symbol + " at " + seqIndex);
		getBothEnds(symbol);

		if ((seqIndex + 250) > endIndexLast)
			return;

		float maxPrice = 0.0f;
		float minPrice = 0.0f;
		float startPrice = 0.0f;
		float oneYearPrice = 0.0f;
		try {
			int count = 0;
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "SELECT MAX(ADJUSTEDPRICE),MIN(ADJUSTEDPRICE) from BPMADMIN.INTRADAYSTOCKS  where   symbol='"
					+ symbol
					+ "' and  SEQINDEX>="
					+ seqIndex
					+ " AND SEQINDEX<=" + (seqIndex + 250);
			rs1 = stmt1.executeQuery(SQL);

			if (rs1.next()) {
				maxPrice = rs1.getFloat(1);
				minPrice = rs1.getFloat(2);
			}

			SQL = "SELECT ADJUSTEDPRICE from BPMADMIN.INTRADAYSTOCKS  where   symbol='"
					+ symbol + "' and  SEQINDEX=" + seqIndex;
			rs1 = stmt1.executeQuery(SQL);

			if (rs1.next()) {
				startPrice = rs1.getFloat(1);
			}

			SQL = "SELECT ADJUSTEDPRICE from BPMADMIN.INTRADAYSTOCKS  where   symbol='"
					+ symbol + "' and  SEQINDEX=" + (seqIndex + 250);
			rs1 = stmt1.executeQuery(SQL);

			if (rs1.next()) {
				oneYearPrice = rs1.getFloat(1);
			}

			if (startPrice > 0.00001f && oneYearPrice > 0.0001f) {
				System.out.println("Hold for one year gain " + 100.0f
						* (oneYearPrice - startPrice) / startPrice);
				oneYearGainTotal = oneYearGainTotal + 100.0f
						* (oneYearPrice - startPrice) / startPrice;
				stockCount = stockCount + 1;
			}

			if (startPrice > 0.00001f && maxPrice > 0.0001f) {
				float mGain = 100.0f * (maxPrice - startPrice) / startPrice;

				if (mGain > 10.0f) {
					aboveTenPercentage++;
				}
				if (mGain > 20.0f) {
					aboveTwentyPercentage++;
				}
				if (mGain > 30.0f) {
					aboveThirtyPercentage++;
				}
				if (mGain > 40.0f) {
					aboveFourtyPercentage++;
				}
				if (mGain > 50.0f) {
					aboveFiftyPercentage++;
				}
				if (mGain > 60.0f) {
					aboveSixtyPercentage++;
				}
				if (mGain > 70.0f) {
					aboveSeventyPercentage++;
				}
				if (mGain > 80.0f) {
					aboveEightyPercentage++;
				}
				if (mGain > 90.0f) {
					aboveNintyPercentage++;
				}
				if (mGain > 100.0f) {
					aboveOnehundrePercentage++;
				}
				System.out.println("Max gain within one year " + mGain);
				maxGainTotal = maxGainTotal + mGain;

			}

			if (startPrice > 0.00001f && minPrice > 0.0001f) {
				float mLoss = 100.0f * (minPrice - startPrice) / startPrice;

				if (mLoss < -10.0f) {
					belowMinusTenPercentage++;
				}
				if (mLoss < -20.0f) {
					belowMinusTwentyPercentage++;
				}
				if (mLoss < -30.0f) {
					belowMinusThirtyPercentage++;
				}
				if (mLoss < -40.0f) {
					belowMinusFourtyPercentage++;
				}
				if (mLoss < -50.0f) {
					belowMinusFiftyPercentage++;
				}
				if (mLoss < -60.0f) {
					belowMinusSixtyPercentage++;
				}
				if (mLoss < -70.0f) {
					belowMinusSeventyPercentage++;
				}
				if (mLoss < -80.0f) {
					belowMinusEightyPercentage++;
				}
				if (mLoss < -90.0f) {
					belowMinusNintyPercentage++;
				}
				if (mLoss < -100.0f) {
					belowMinusOnehundrePercentage++;
				}
				System.out.println("Max loss within one year " + mLoss);
				maxLossTotal = maxLossTotal + mLoss;
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void evaluateShortGain(String symbol, int seqIndex) {
		System.out.println("Evaluting " + symbol + " at " + seqIndex);
		getBothEnds(symbol);

		if ((seqIndex + 25) > endIndexLast)
			return;

		float maxPrice = 0.0f;
		float minPrice = 0.0f;
		float startPrice = 0.0f;
		float oneYearPrice = 0.0f;
		try {
			int count = 0;
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "SELECT MAX(ADJUSTEDPRICE),MIN(ADJUSTEDPRICE) from BPMADMIN.INTRADAYSTOCKS  where   symbol='"
					+ symbol
					+ "' and  SEQINDEX>="
					+ seqIndex
					+ " AND SEQINDEX<=" + (seqIndex + 25);
			rs1 = stmt1.executeQuery(SQL);

			if (rs1.next()) {
				maxPrice = rs1.getFloat(1);
				minPrice = rs1.getFloat(2);
			}

			SQL = "SELECT ADJUSTEDPRICE from BPMADMIN.INTRADAYSTOCKS  where   symbol='"
					+ symbol + "' and  SEQINDEX=" + seqIndex;
			rs1 = stmt1.executeQuery(SQL);

			if (rs1.next()) {
				startPrice = rs1.getFloat(1);
			}

			SQL = "SELECT ADJUSTEDPRICE from BPMADMIN.INTRADAYSTOCKS  where   symbol='"
					+ symbol + "' and  SEQINDEX=" + (seqIndex + 25);
			rs1 = stmt1.executeQuery(SQL);

			if (rs1.next()) {
				oneYearPrice = rs1.getFloat(1);
			}

			if (startPrice > 0.00001f && oneYearPrice > 0.0001f) {
				System.out.println("Hold for three month gain " + 100.0f
						* (oneYearPrice - startPrice) / startPrice);
				oneYearGainTotal = oneYearGainTotal + 100.0f
						* (oneYearPrice - startPrice) / startPrice;
				stockCount = stockCount + 1;
			}

			if (startPrice > 0.00001f && maxPrice > 0.0001f) {
				float mGain = 100.0f * (maxPrice - startPrice) / startPrice;

				if (mGain > 3.0f) {
					aboveTenPercentage++;
				}
				if (mGain > 4.0f) {
					aboveTwentyPercentage++;
				}
				if (mGain > 5.0f) {
					aboveThirtyPercentage++;
				}
				if (mGain > 6.0f) {
					aboveFourtyPercentage++;
				}
				if (mGain > 7.0f) {
					aboveFiftyPercentage++;
				}
				if (mGain > 8.0f) {
					aboveSixtyPercentage++;
				}
				if (mGain > 10.0f) {
					aboveSeventyPercentage++;
				}
				if (mGain > 20.0f) {
					aboveEightyPercentage++;
				}
				if (mGain > 30.0f) {
					aboveNintyPercentage++;
				}
				if (mGain > 40.0f) {
					aboveOnehundrePercentage++;
				}
				System.out.println("Max gain within three month " + mGain);
				maxGainTotal = maxGainTotal + mGain;

			}

			if (startPrice > 0.00001f && minPrice > 0.0001f) {
				float mLoss = 100.0f * (minPrice - startPrice) / startPrice;

				if (mLoss < -3.0f) {
					belowMinusTenPercentage++;
				}
				if (mLoss < -4.0f) {
					belowMinusTwentyPercentage++;
				}
				if (mLoss < -5.0f) {
					belowMinusThirtyPercentage++;
				}
				if (mLoss < -6.0f) {
					belowMinusFourtyPercentage++;
				}
				if (mLoss < -7.0f) {
					belowMinusFiftyPercentage++;
				}
				if (mLoss < -8.0f) {
					belowMinusSixtyPercentage++;
				}
				if (mLoss < -10.0f) {
					belowMinusSeventyPercentage++;
				}
				if (mLoss < -20.0f) {
					belowMinusEightyPercentage++;
				}
				if (mLoss < -30.0f) {
					belowMinusNintyPercentage++;
				}
				if (mLoss < -40.0f) {
					belowMinusOnehundrePercentage++;
				}
				System.out.println("Max loss within three month " + mLoss);
				maxLossTotal = maxLossTotal + mLoss;
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void printAverageSummary() {
		// stock count is not accurate, it is the times of such occurence
		System.out.println("Total occurence " + stockCount);
		System.out.println("Total  aboveTenPercentage number "
				+ aboveTenPercentage + " percentage " + 100.0f
				* (aboveTenPercentage * 1.0f) / (stockCount * 1.0f));
		System.out.println("Total  aboveTwentyPercentage  number "
				+ aboveTwentyPercentage + " percentage " + 100.0f
				* (aboveTwentyPercentage * 1.0f) / (stockCount * 1.0f));
		System.out.println("Total  aboveThirtyPercentage number "
				+ aboveThirtyPercentage + " percentage " + 100.0f
				* (aboveThirtyPercentage * 1.0f) / (stockCount * 1.0f));
		System.out.println("Total  aboveFourtyPercentage number "
				+ aboveFourtyPercentage + " percentage " + 100.0f
				* (aboveFourtyPercentage * 1.0f) / (stockCount * 1.0f));
		System.out.println("Total  aboveFiftyPercentage number "
				+ aboveFiftyPercentage + " percentage " + 100.0f
				* (aboveFiftyPercentage * 1.0f) / (stockCount * 1.0f));
		System.out.println("Total  aboveSixtyPercentage number "
				+ aboveSixtyPercentage + " percentage " + 100.0f
				* (aboveSixtyPercentage * 1.0f) / (stockCount * 1.0f));
		System.out.println("Total  aboveSeventyPercentage number "
				+ aboveSeventyPercentage + " percentage " + 100.0f
				* (aboveSeventyPercentage * 1.0f) / (stockCount * 1.0f));
		System.out.println("Total  aboveEightyPercentage number "
				+ aboveEightyPercentage + " percentage " + 100.0f
				* (aboveEightyPercentage * 1.0f) / (stockCount * 1.0f));
		System.out.println("Total  aboveNintyPercentage number "
				+ aboveNintyPercentage + " percentage " + 100.0f
				* (aboveNintyPercentage * 1.0f) / (stockCount * 1.0f));
		System.out.println("Total  aboveOnehundrePercentage  number "
				+ aboveOnehundrePercentage + " percentage " + 100.0f
				* (aboveOnehundrePercentage * 1.0f) / (stockCount * 1.0f));
		System.out.println("Average one year gain " + oneYearGainTotal
				/ (1.0f * stockCount));
		System.out.println("Average max gain " + maxGainTotal
				/ (1.0f * stockCount));

		System.out.println(100.0f * (aboveTenPercentage * 1.0f)
				/ (stockCount * 1.0f) + "\t" + 100.0f
				* (aboveTwentyPercentage * 1.0f) / (stockCount * 1.0f) + "\t"
				+ 100.0f * (aboveThirtyPercentage * 1.0f) / (stockCount * 1.0f)
				+ "\t" + 100.0f * (aboveFourtyPercentage * 1.0f)
				/ (stockCount * 1.0f) + "\t" + 100.0f
				* (aboveFiftyPercentage * 1.0f) / (stockCount * 1.0f) + "\t"
				+ 100.0f * (aboveSixtyPercentage * 1.0f) / (stockCount * 1.0f)
				+ "\t" + 100.0f * (aboveSeventyPercentage * 1.0f)
				/ (stockCount * 1.0f) + "\t" + 100.0f
				* (aboveEightyPercentage * 1.0f) / (stockCount * 1.0f) + "\t"
				+ 100.0f * (aboveNintyPercentage * 1.0f) / (stockCount * 1.0f)
				+ "\t" + 100.0f * (aboveOnehundrePercentage * 1.0f)
				/ (stockCount * 1.0f));

		System.out.println("Total occurence " + stockCount);
		System.out.println("Total  belowMinusTenPercentage number "
				+ belowMinusTenPercentage + " percentage " + 100.0f
				* (belowMinusTenPercentage * 1.0f) / (stockCount * 1.0f));
		System.out.println("Total  belowMinusTwentyPercentage  number "
				+ belowMinusTwentyPercentage + " percentage " + 100.0f
				* (belowMinusTwentyPercentage * 1.0f) / (stockCount * 1.0f));
		System.out.println("Total  belowMinusThirtyPercentage number "
				+ belowMinusThirtyPercentage + " percentage " + 100.0f
				* (belowMinusThirtyPercentage * 1.0f) / (stockCount * 1.0f));
		System.out.println("Total  belowMinusFourtyPercentage number "
				+ belowMinusFourtyPercentage + " percentage " + 100.0f
				* (belowMinusFourtyPercentage * 1.0f) / (stockCount * 1.0f));
		System.out.println("Total  belowMinusFiftyPercentage number "
				+ belowMinusFiftyPercentage + " percentage " + 100.0f
				* (belowMinusFiftyPercentage * 1.0f) / (stockCount * 1.0f));
		System.out.println("Total  belowMinusSixtyPercentage number "
				+ belowMinusSixtyPercentage + " percentage " + 100.0f
				* (belowMinusSixtyPercentage * 1.0f) / (stockCount * 1.0f));
		System.out.println("Total  belowMinusSeventyPercentage number "
				+ belowMinusSeventyPercentage + " percentage " + 100.0f
				* (belowMinusSeventyPercentage * 1.0f) / (stockCount * 1.0f));
		System.out.println("Total  belowMinusEightyPercentage number "
				+ belowMinusEightyPercentage + " percentage " + 100.0f
				* (belowMinusEightyPercentage * 1.0f) / (stockCount * 1.0f));
		System.out.println("Total  belowMinusNintyPercentage number "
				+ belowMinusNintyPercentage + " percentage " + 100.0f
				* (belowMinusNintyPercentage * 1.0f) / (stockCount * 1.0f));
		System.out.println("Total  belowMinusOnehundrePercentage  number "
				+ belowMinusOnehundrePercentage + " percentage " + 100.0f
				* (belowMinusOnehundrePercentage * 1.0f) / (stockCount * 1.0f));

		System.out.println(100.0f * (belowMinusTenPercentage * 1.0f)
				/ (stockCount * 1.0f) + "\t" + 100.0f
				* (belowMinusTwentyPercentage * 1.0f) / (stockCount * 1.0f)
				+ "\t" + 100.0f * (belowMinusThirtyPercentage * 1.0f)
				/ (stockCount * 1.0f) + "\t" + 100.0f
				* (belowMinusFourtyPercentage * 1.0f) / (stockCount * 1.0f)
				+ "\t" + 100.0f * (belowMinusFiftyPercentage * 1.0f)
				/ (stockCount * 1.0f) + "\t" + 100.0f
				* (belowMinusSixtyPercentage * 1.0f) / (stockCount * 1.0f)
				+ "\t" + 100.0f * (belowMinusSeventyPercentage * 1.0f)
				/ (stockCount * 1.0f) + "\t" + 100.0f
				* (belowMinusEightyPercentage * 1.0f) / (stockCount * 1.0f)
				+ "\t" + 100.0f * (belowMinusNintyPercentage * 1.0f)
				/ (stockCount * 1.0f) + "\t" + 100.0f
				* (belowMinusOnehundrePercentage * 1.0f) / (stockCount * 1.0f));

		System.out.println("Average max loss " + maxLossTotal
				/ (1.0f * stockCount));

	}

	public static void evaluateConditions(String symbol, int[] seqIndexPairs) {
		try {
			int count = 0;
			// Establish the connection.
			if (con == null)
				con = getConnection();

			for (int k = 0; k < 50000; k++) {
				int seqIndexLow = seqIndexPairs[k];
				int seqIndexHigh = seqIndexPairs[++k];

				if (seqIndexLow == -1 || seqIndexHigh == -1)
					break;

				SQL = "SELECT MIN(PTMA),SUM(PTMA) from INTRADAYSTOCKS  where   symbol='"
						+ symbol
						+ "' and  SEQINDEX>="
						+ seqIndexLow
						+ " AND SEQINDEX<=" + seqIndexHigh;
				rs1 = stmt1.executeQuery(SQL);

				boolean allAboveZero = false;

				if (rs1.next()) {
					// remove >0 condition
					if (rs1.getFloat(1) > 0.01f) {
						float average = rs1.getFloat(2)
								/ ((seqIndexHigh + 1 - seqIndexLow) * 1.0f);
						// if (average > 5.0f) {
						allAboveZero = true;
						// }
					}

				}

				if (allAboveZero) {
					if ((seqIndexHigh - seqIndexLow) > 18) {
						// REMOVE CHECK FFP CONDITION, INSTEAD JUST BUY NEXT DAY
						// AFTER GR100=1
						// int buyIndex = checkFFCondition(symbol,
						// seqIndexHigh);
						int buyIndex = seqIndexHigh + 2;
						if (buyIndex > 0) {
							findCurrentCap(symbol, buyIndex);
							System.out
									.println("Stock "
											+ symbol
											+ " PTMA all above zero correction qualified from "
											+ seqIndexLow + " to "
											+ seqIndexHigh + " buy at "
											+ buyIndex);

							evaluateGain(symbol, buyIndex);
						}
					}
				} else {
					/*
					 * SQL =
					 * "SELECT SUM(PTMA) from INTRADAYSTOCKS  where   symbol='"
					 * + symbol + "' and  SEQINDEX>=" + seqIndexLow +
					 * " AND SEQINDEX<=" + seqIndexHigh; rs1 =
					 * stmt1.executeQuery(SQL);
					 * 
					 * if (rs1.next()) { float sumPTMA = rs1.getFloat(1); float
					 * averagePTMA = sumPTMA / ((seqIndexHigh - seqIndexLow + 1)
					 * * 1.0f);
					 * 
					 * if (averagePTMA > 5.0f) { if ((seqIndexHigh -
					 * seqIndexLow) > 20) { int buyIndex =
					 * checkFFCondition(symbol, seqIndexHigh); if (buyIndex > 0)
					 * { System.out.println("Stock " + symbol +
					 * " Average PTMA >5.0 correction " + seqIndexLow + " to " +
					 * seqIndexHigh); evaluateGain(symbol, buyIndex); } } } }
					 */
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	// if FFP>0 happens after at least 100 days continuous FFP>0 but within 200
	// days
	// of such incidents, the idea is that a hot stock goes into correction,
	// then
	// pick up steam again. Additional to consider, the previous 100 could be
	// broken down
	// into two session within 300 days, the previous rise must be above certain
	// level,
	// and the lastest FFP>0 may be above previous rise high etc
	// we could also calculate the time to reach max gain, or loss etec
	public static int[] findBullPointAfterRiseCorrection(String symbol) {
		int[] results = new int[5000];
		int count = 0;
		getBothEnds(symbol);

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "SELECT SEQINDEX, FFP from INTRADAYSTOCKS  where   symbol='"
					+ symbol + "' and FFP=1 ORDER BY SEQINDEX DESC";
			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				int nextIndex = rs1.getInt(1);

				SQL = "SELECT SEQINDEX, FFP from INTRADAYSTOCKS  where   symbol='"
						+ symbol
						+ "' and FFP>0 and SEQINDEX<"
						+ nextIndex
						+ " ORDER BY SEQINDEX DESC";
				rs2 = stmt2.executeQuery(SQL);

				if (rs2.next()) {
					int index = rs2.getInt(1);
					int ffp = rs2.getInt(2);
					if (ffp > 100 && (nextIndex - index) < 200) {
						results[count++] = nextIndex;

					}
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		results[count] = -20000;
		return results;

	}

	// The best selection criteria might be this
	// the gap between FFP is at least 190 days (gaps to be tested) (SKX) and
	// the SUM(PTMA) between
	// the FFP =0 period is great than zero
	// preferably the average PTMA (i.e. sum/days) is great than 1%
	// sometimes, the sum(PTMA) is less than zero, even greatly, then the next
	// gap between FFP positive doesn't need to meet
	// the greater than 190 criteria, you only need the sum(PTMA) during that
	// period > 0
	// IF THE NEXT GAP SUM(PTMA) STILL <0, THEN NEXT GAP, DON'T NEED TO BE 190
	// DAYS, JUST SUM(PTMA)>0,
	// PREFERABLY AVERAGE(PTMA)>10
	// such stock one year return should be great!
	// A back test program needs to be written and run against historical data
	// to verify this theory
	// observed by many manual verification samples.
	// FOR NEWLY PUBLIC OFFERED STOCKS, YOU MAY NOT HAVE SO LONG HISTORY FOR 190
	// DAYS WAIT
	// THEN WE JUST NEED FFP=1 POINT TO BEGINING >190 DAYS AND SUM(PT MA)>0 FOR
	// WHATEVER IS AVAILABLE,
	// ASSUMMING THE FFP=1 POINT IS SUCH OCCURRENCE AFTER IPO //CHECK THE BITA
	// EXCEPTION
	public static int[] findBullPointAfterProperLongCorrection(String symbol,
			int gaps) {
		System.out.println("Start checking " + symbol);

		int[] results = new int[5000];
		int count = 0;
		getBothEnds(symbol);

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "SELECT SEQINDEX, FFP from INTRADAYSTOCKS  where   symbol='"
					+ symbol + "' and FFP=1 ORDER BY SEQINDEX ASC";
			rs1 = stmt1.executeQuery(SQL);
			boolean noGapRequired = false;
			int previousStart = 0;
			int previousEnd = 0;

			while (rs1.next()) {
				int nextIndex = rs1.getInt(1);

				SQL = "SELECT SEQINDEX, FFP from INTRADAYSTOCKS  where   symbol='"
						+ symbol
						+ "' and FFP>0 and SEQINDEX<"
						+ nextIndex
						+ " ORDER BY SEQINDEX DESC";
				rs2 = stmt2.executeQuery(SQL);

				if (rs2.next()) {
					int index = rs2.getInt(1);
					// if((nextIndex-index)>150){
					if (noGapRequired && checkSumPTA(symbol, index, nextIndex)) {
						if (checkPreviousGain(symbol, previousStart, index)) {
							results[count++] = nextIndex;
						}
						noGapRequired = false;
						previousStart = 0;

					} else if ((nextIndex - index) >= gaps
							&& checkSumPTA(symbol, index, nextIndex)) {
						results[count++] = nextIndex;

					} else if ((nextIndex - index) >= gaps
							&& !checkSumPTA(symbol, index, nextIndex)) {
						noGapRequired = true;
						previousStart = nextIndex;

					}
				} else {
					if ((nextIndex - startIndexLast) >= gaps
							&& checkSumPTA(symbol, startIndexLast, nextIndex)) {
						results[count++] = nextIndex;
					} else if ((nextIndex - startIndexLast) >= gaps
							&& !checkSumPTA(symbol, startIndexLast, nextIndex)) {
						// not to muddle the water, maybe we skip this case for
						// now
						// noGapRequired = true;
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		results[count] = -20000;
		return results;

	}

	private static boolean checkPreviousGain(String symbol, int previousStart,
			int previousEnd) {
		boolean result = false;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "SELECT max(ADJUSTEDPRICE) from INTRADAYSTOCKS  where   symbol='"
					+ symbol
					+ "' and SEQINDEX<="
					+ previousEnd
					+ " and SEQINDEX>=" + previousStart;
			rs3 = stmt3.executeQuery(SQL);
			float maxPrice = 0.0f;
			float startPrice = 0.0f;

			if (rs3.next()) {
				maxPrice = rs3.getFloat(1);
			}

			SQL = "SELECT ADJUSTEDPRICE from INTRADAYSTOCKS  where   symbol='"
					+ symbol + "' and SEQINDEX=" + previousStart;
			rs3 = stmt3.executeQuery(SQL);

			if (rs3.next()) {
				startPrice = rs3.getFloat(1);
			}

			if (maxPrice > 1.0f && startPrice > 0.01f) {
				if ((100.0f * (maxPrice - startPrice)) / startPrice < 50.0f) {
					result = true;
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return result;

	}

	public static int checkRecordCount( long lastIndex) {
		int result = 0;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "SELECT count(*) from BPMADMIN.INTRADAYSTOCKS  where  SEQINDEX=" + lastIndex;
			;
			rs3 = stmt3.executeQuery(SQL);

			if (rs3.next()) {
					result = rs3.getInt(1);

			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return result;
	}
	
	public static boolean checkRecordAt(String symbol, long lastIndex) {
		boolean result = false;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "SELECT count(*) from BPMADMIN.INTRADAYSTOCKS  where   symbol='"
					+ symbol + "' AND SEQINDEX=" + lastIndex;
			;
			rs3 = stmt3.executeQuery(SQL);

			if (rs3.next()) {
				if (rs3.getInt(1) > 20)
					result = true;

			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return result;
	}

	public static boolean checkRecordExist(String symbol, long lastIndex) {
		boolean result = false;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			String SQL = "SELECT count(*) from BPMADMIN.INTRADAYSTOCKS  where   symbol='"
					+ symbol + "' AND SEQINDEX>" + lastIndex;
			;
			rs3 = stmt3.executeQuery(SQL);

			if (rs3.next()) {
				if (rs3.getInt(1) > 20)
					result = true;

			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return result;
	}

	private static boolean checkSumPTA(String symbol, long startIndex,
			long endIndex) {
		boolean result = false;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "SELECT sum(PTMA) from INTRADAYSTOCKS  where   symbol='"
					+ symbol + "' and SEQINDEX<" + endIndex + " and SEQINDEX>"
					+ startIndex;
			rs3 = stmt3.executeQuery(SQL);

			if (rs3.next()) {
				if (rs3.getFloat(1) > 0.01f)
					result = true;

			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return result;
	}

	
	//IF NO TMAV CALCULATED USED JAVA INSERT, OTHERWISE USE STORED PROCEDURE
	//TO INSERT, TOO SHORT RECORD, STORED PROCEDURE INSERT WILL FAIL
	public static int checkTMAV(String symbol) {

		int size = 0;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select count(*) from  BPMADMIN.YAHOODB  where symbol='"
					+ symbol + "' AND TMAV>1";

			rs1 = stmt1.executeQuery(SQL);

			if (rs1.next()) {
				size = rs1.getInt(1);
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return size;
	}
	
	// if FFP>0 happens waiting at least 150 (0r 200) trading days
	// then go ahead and buy stocks
	public static int[] findBullPointAfterLongCorrection(String symbol) {
		int[] results = new int[5000];
		int count = 0;
		getBothEnds(symbol);

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "SELECT SEQINDEX, FFP from INTRADAYSTOCKS  where   symbol='"
					+ symbol + "' and FFP=1 ORDER BY SEQINDEX DESC";
			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				int nextIndex = rs1.getInt(1);

				SQL = "SELECT SEQINDEX, FFP from INTRADAYSTOCKS  where   symbol='"
						+ symbol
						+ "' and FFP>0 and SEQINDEX<"
						+ nextIndex
						+ " ORDER BY SEQINDEX DESC";
				rs2 = stmt2.executeQuery(SQL);

				if (rs2.next()) {
					int index = rs2.getInt(1);
					// if((nextIndex-index)>150){
					if ((nextIndex - index) >= 200) {
						results[count++] = nextIndex;

					}
				} else if ((nextIndex - startIndexLast) >= 200) {
					results[count++] = nextIndex;
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		results[count] = -20000;
		return results;

	}

	public static int[] findBullEntryPointAfterRise(String symbol) {
		int[] results = new int[5000];
		getBothEnds(symbol);
		int count = 0;

		try {
			int previousIndex = 0;
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "SELECT SEQINDEX from INTRADAYSTOCKS  where   symbol='"
					+ symbol + "' and PTMA>100 ORDER BY SEQINDEX ASC";
			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				int cIndex = rs1.getInt(1);
				if (count == 0) {
					if ((cIndex - startIndexLast) > 200) {
						results[count] = cIndex;
						previousIndex = cIndex;
						count++;
					}

				} else {
					if ((cIndex - previousIndex) > 200) {
						results[count] = cIndex;
						previousIndex = cIndex;
						count++;
					}

				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		results[count++] = -20000;
		return results;

	}

	public static int[] findBullEntryPoint(String symbol) {
		int[] results = new int[5000];
		int count = 0;

		try {
			int minSeqIndex = 0;
			int maxSeqIndex = 0;
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "SELECT SEQINDEX,GR100 from INTRADAYSTOCKS  where   symbol='"
					+ symbol
					+ "' and (GR100=1 OR GR100=-1) ORDER BY SEQINDEX ASC";
			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				int GR100 = rs1.getInt(2);
				if (minSeqIndex == 0 && GR100 == -1) {
					minSeqIndex = rs1.getInt(1);
				} else if (maxSeqIndex == 0 && GR100 == 1) {
					maxSeqIndex = rs1.getInt(1);
					if (minSeqIndex < maxSeqIndex) {
						results[count++] = minSeqIndex;
						results[count++] = maxSeqIndex - 1;
						// System.out.println("Entry pair [" + minSeqIndex + ","
						// + (maxSeqIndex - 1) + "]");
					}
				} else if (minSeqIndex > 0 && maxSeqIndex > 0) {
					if (minSeqIndex < maxSeqIndex) {
						if (GR100 == -1) {
							minSeqIndex = rs1.getInt(1);
							maxSeqIndex = 0;
						} else {
							System.out
									.println("Impossible error for " + symbol);
						}
					} else if (minSeqIndex > maxSeqIndex) {
						if (GR100 == 1) {
							maxSeqIndex = rs1.getInt(1);
						} else {
							System.out
									.println("Impossible error for " + symbol);
						}
					}

				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		results[count++] = -1;
		return results;

	}

	public boolean checkExistance(String symbol, int seqIndex) {
		boolean exist = false;
		try {
			if (con == null)
				con = getConnection();

			SQL = "SELECT count(*) from FROM BPMADMIN.INTRADAYSTOCKS  where   symbol='"
					+ symbol + "'";
			rs1 = stmt1.executeQuery(SQL);

			int recordNum = 0;
			if (rs1.next()) {
				recordNum = rs1.getInt(1);

			}
			if (recordNum > 0) {
				exist = true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return exist;

	}

	public static int NotUsedcheckFFCondition(String symbol, int seqIndex) {
		int buyIndex = 0;
		try {
			int count = 0;
			// Establish the connection.
			if (con == null)
				con = getConnection();

			boolean continueCheck = true;
			// SINGLE POINT APPROACH OR CHECK VVREC=5 IF AVAILABLE
			/*
			 * for (int k = 0; k < 30; k++) { SQL =
			 * "SELECT COUNT(*) from INTRADAYSTOCKS  where   symbol='" + symbol
			 * + "' and PTMA>50 AND SEQINDEX =" + (seqIndex + 1 + k); rs1 =
			 * stmt1.executeQuery(SQL);
			 * 
			 * if (rs1.next()) { count = rs1.getInt(1); if (count == 1) {
			 * buyIndex = (seqIndex + 2 + k); continueCheck = false; //
			 * System.out // .println("Single point PTMA>20 qualification ");
			 * break; } } }
			 */

			SQL = "SELECT SEQINDEX from INTRADAYSTOCKS  where   symbol='"
					+ symbol + "' and FFP>0 AND SEQINDEX >=" + seqIndex
					+ " AND SEQINDEX <=" + (seqIndex + 30)
					+ " ORDER BY SEQINDEX ASC";
			rs1 = stmt1.executeQuery(SQL);

			if (rs1.next()) {
				buyIndex = rs1.getInt(1) + 1;

			}

			// SUMMARY APPROACH

			/*
			 * if (continueCheck) for (int k = 0; k < 30; k++) { SQL =
			 * "SELECT COUNT(*) from INTRADAYSTOCKS  where   symbol='" + symbol
			 * + "' and PTMA>15 AND SEQINDEX<=" + (seqIndex + 1 + k) +
			 * " AND SEQINDEX>" + (seqIndex - 14 + k); rs1 =
			 * stmt1.executeQuery(SQL);
			 * 
			 * if (rs1.next()) { count = rs1.getInt(1); if (count == 15) {
			 * buyIndex = (seqIndex + 2 + k); // System.out //
			 * .println("Summary qualification PTMA>15 for 15 days at " // + k);
			 * break; } } }
			 */
		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return buyIndex;

	}

	public static boolean isTodayWeekEnd(int seqIndexToday) {

		boolean result = false;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null) {
				stmt1 = con.createStatement();
			}
			if (stmt2 == null) {
				stmt2 = con.createStatement();
			}

			SQL = "select SEQINDEX, ADJUSTEDPRICE, CURRENTDATE from INTRADAYSTOCKS   where symbol='AAPL' and seqIndex <="
					+ seqIndexToday + " order by SEQINDEX DESC";
			System.out.println(SQL);
			rs1 = stmt1.executeQuery(SQL);

			int[] seqIndex = new int[5000];
			java.util.Date previousDate = null;
			java.util.Date currentDate = null;
			int previousWeekDate = 0;
			int currentWeekDate = 0;
			int previousIndex = 0;
			int currentIndex = 0;
			float previousPrice = 0.0f;
			float currentPrice = 0.0f;

			int weekCount = 0;
			int loopCount = 0;
			while (rs1.next() && loopCount < 2) {

				loopCount++;
				if (currentDate == null) {
					currentDate = rs1.getDate(3);
					currentWeekDate = currentDate.getDay();
					currentIndex = rs1.getInt(1);
				} else if (previousDate == null) {
					previousDate = rs1.getDate(3);
					previousWeekDate = previousDate.getDay();
					previousIndex = rs1.getInt(1);

					if ((currentWeekDate == 5)) {
						seqIndex[weekCount] = currentIndex;

						System.out.println(weekCount + " " + rs1.getInt(1)
								+ " " + seqIndex[weekCount]);
						weekCount++;
					} else if (currentWeekDate <= 3 && previousWeekDate < 5
							&& previousWeekDate >= 3) {
						seqIndex[weekCount] = previousIndex;

						System.out.println(weekCount + " " + rs1.getInt(1)
								+ " " + seqIndex[weekCount]);
						weekCount++;
					}

					if (weekCount > 0) {
						SQL = "update INTRADAYSTOCKS   set WWI = -1 where seqIndex = "
								+ seqIndexToday;

						stmt2.executeUpdate(SQL);
						result = true;
					}
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return result;
	}

	public static boolean isTodayMonthEnd(int seqIndex) {

		boolean result = false;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null) {
				stmt1 = con.createStatement();
			}
			if (stmt2 == null) {
				stmt2 = con.createStatement();
			}

			SQL = "select SEQINDEX,  CURRENTDATE from INTRADAYSTOCKS where symbol='AAPL' and seqIndex ="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			java.util.Date currentDate = null;

			int monthCount = 0;
			if (rs1.next()) {

				if (currentDate == null) {
					currentDate = rs1.getDate(2);

				}

				if (currentDate != null) {
					int month = currentDate.getMonth();
					int date = currentDate.getDate();

					if (month == 0 && date == 31) {
						result = true;
					} else if (month == 1 && date == 28) {
						result = true;
					} else if (month == 2 && date == 31) {
						result = true;
					} else if (month == 3 && date == 30) {
						result = true;
					} else if (month == 4 && date == 31) {
						result = true;
					} else if (month == 5 && date == 30) {
						result = true;
					} else if (month == 6 && date == 31) {
						result = true;
					} else if (month == 7 && date == 31) {
						result = true;
					} else if (month == 8 && date == 30) {
						result = true;
					} else if (month == 9 && date == 31) {
						result = true;
					} else if (month == 10 && date == 30) {
						result = true;
					} else if (month == 11 && date == 31) {
						result = true;
					}

					if (result) {
						SQL = "update INTRADAYSTOCKS set TMAI = 1 where seqIndex = "
								+ seqIndex;

						System.out.println(SQL);
						stmt2.executeUpdate(SQL);
					}

				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return result;
	}

	public static boolean isPreviousDayMonthEnd(int seqIndex) {

		boolean result = false;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null) {
				stmt1 = con.createStatement();
			}
			if (stmt2 == null) {
				stmt2 = con.createStatement();
			}

			SQL = "select SEQINDEX,  CURRENTDATE from INTRADAYSTOCKS where symbol='AAPL' and seqIndex <="
					+ seqIndex
					+ " and seqIndex>="
					+ (seqIndex - 1)
					+ " order by SEQINDEX DESC";

			rs1 = stmt1.executeQuery(SQL);
			java.util.Date previousDate = null;
			java.util.Date currentDate = null;
			int previousIndex = 0;
			int currentIndex = 0;

			int monthCount = 0;
			while (rs1.next()) {

				if (currentDate == null) {
					currentDate = rs1.getDate(2);
					currentIndex = rs1.getInt(1);
				} else if (previousDate == null) {
					previousDate = rs1.getDate(2);
					previousIndex = rs1.getInt(1);
				}

				if (currentDate != null
						&& previousDate != null
						&& (currentDate.getMonth() - previousDate.getMonth()) != 0) {
					SQL = "update INTRADAYSTOCKS set TMAI = 1 where seqIndex = "
							+ previousIndex;

					System.out.println(SQL);
					stmt2.executeUpdate(SQL);
					result = true;
				}

				if (currentDate != null && previousDate != null) {
					break;
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return result;
	}

	public static Hashtable getNotCalculatedStocks(int seqIndex) {
		Hashtable stocks = new Hashtable();

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			SQL = "select distinct(symbol) from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX>="
					+ (seqIndex - 20) + " AND DS3PER IS NULL";

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {

				stocks.put(rs1.getString(1), rs1.getString(1));

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return stocks;

	}

	public static Hashtable getCurrentVSTStocks(int sortOrder, int seqIndex) {
		Hashtable stocks = new Hashtable();

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			if (sortOrder == sortedByPrice) {
				SQL = "select SYMBOL from FULLVSTRANK where SEQINDEX="
						+ seqIndex + "  order by PRICEFINAL DESC";
			} else if (sortOrder == sortedByMarketcap) {
				// SQL = "select SYMBOL from FULLVSTRANK where SEQINDEX="
				// + seqIndex + "  order by marketcap DESC";
				SQL = "select SYMBOL from FULLVSTRANK where SEQINDEX="
						+ seqIndex + "   order by marketcap DESC";
			}

			rs1 = stmt1.executeQuery(SQL);

			int count = 1;
			while (rs1.next()) {

				stocks.put("" + count, rs1.getString(1));
				count++;

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return stocks;

	}

	public static void getPTMAWAVEHISTORY(long seqIndex, boolean lastOnly) {
		Hashtable stocks = getCurrentAllStocks(seqIndex);
		System.out.println("Unique Uptrend at index " + seqIndex);
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			long startTime = System.currentTimeMillis();
			Enumeration en = stocks.keys();
			int loopCount = 0;
			while (en.hasMoreElements()) {
				String symbol = en.nextElement().toString();
				System.out.println("Processing " + symbol);
				loopCount++;
				if (con == null)
					con = getConnection();

				if (cstmt == null) {
					cstmt = con
							.prepareCall("CALL BPMADMIN.PTMAWHISTORY(?,?,?)");
				}

				cstmt.setString(1, symbol);
				cstmt.setBoolean(2, lastOnly);
				cstmt.registerOutParameter(3, Types.INTEGER);

				cstmt.execute();

				int result = cstmt.getInt(3);

				if (loopCount % 10 == 0) {
					long endTime = System.currentTimeMillis();
					System.out.println(loopCount
							+ " Another 10 records processed. Cost seconds "
							+ (endTime - startTime) / 1000);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void calculateCurrentAVGPTMA(long seqIndex, boolean lastOnly) {
		Hashtable stocks = getCurrentAllStocks(seqIndex);
		System.out.println("Current AVG PTMA for each stock at index "
				+ seqIndex);
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			long startTime = System.currentTimeMillis();
			Enumeration en = stocks.keys();
			int loopCount = 0;
			while (en.hasMoreElements()) {
				String symbol = en.nextElement().toString();
				System.out.println("Processing " + symbol);
				loopCount++;
				if (con == null)
					con = getConnection();

				SQL = "select AVG(GR100) from BPMADMIN.INTRADAYSTOCKS where GR100<>0 AND SYMBOL='"
						+ symbol + "'";
				stmt1 = con.createStatement();
				rs1 = stmt1.executeQuery(SQL);

				int avgPTMA = 0;
				if (rs1.next()) {
					avgPTMA = rs1.getInt(1);
					SQL = "update BPMADMIN.INTRADAYSTOCKS  set RX100 = "
							+ avgPTMA + " where  SYMBOL='" + symbol
							+ "' and seqindex = " + seqIndex;
					stmt1 = con.createStatement();
					stmt1.executeUpdate(SQL);
				}

				if (loopCount % 10 == 0) {
					long endTime = System.currentTimeMillis();
					System.out.println(loopCount
							+ " Another 10 records processed. Cost seconds "
							+ (endTime - startTime) / 1000);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void getUniqueFirstUpStocks(long seqIndex, boolean lastOnly) {
		Hashtable stocks = getCurrentAllStocks(seqIndex);
		System.out.println("Unique Uptrend at index " + seqIndex);
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			long startTime = System.currentTimeMillis();
			Enumeration en = stocks.keys();
			int loopCount = 0;
			while (en.hasMoreElements()) {
				String symbol = en.nextElement().toString();
				System.out.println("Processing " + symbol);
				loopCount++;
				if (con == null)
					con = getConnection();

				// SQL =
				// "select count(*) from BPMADMIN.INTRADAYSTOCKS where RX100>0 AND SYMBOL='"
				// + symbol + "'";
				// stmt1 = con.createStatement();
				// rs1 = stmt1.executeQuery(SQL);

				int count = 0;
				// if (rs1.next())
				// count = rs1.getInt(1);

				if (cstmt == null) {
					// InsertDailyRecord (IN seqIndex INTEGER,IN symbol
					// VARCHAR(10),
					// IN cdate DATE, IN openPrice REAL, IN lowPrice REAL, IN
					// highPrice REAL,
					// IN finalPrice REAL, IN adjustedPrice REAL, IN volume
					// REAL,
					// OUT VARCOUNT INTEGER)
					cstmt = con
							.prepareCall("CALL BPMADMIN.FIRSTSURGEHISTORY_NEW(?,?,?,?)");
				}

				if (count == 0) {
					cstmt.setString(1, symbol);
					cstmt.setLong(2, seqIndex);
					cstmt.setBoolean(3, lastOnly);
					cstmt.registerOutParameter(4, Types.INTEGER);

					cstmt.execute();

					int result = cstmt.getInt(4);
				} else {
					System.out.println("Skipped Processing " + symbol);
				}

				if (loopCount % 10 == 0) {
					long endTime = System.currentTimeMillis();
					System.out
							.println("Another 10 records processed. Cost seconds "
									+ (endTime - startTime) / 1000);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static Hashtable getTimeInfo(long seqIndex) {
		Hashtable info = new Hashtable();
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select VARCHAR_FORMAT(CDATE,'MM-DD-YYYY') AS cudate,chour,cminute from BPMADMIN.TRADINGTIME where seqindex="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			if (rs1.next()) {
				String date = rs1.getString(1);
				int hours = rs1.getInt(2);
				int mins = rs1.getInt(3);
				info.put("date", date);
				info.put("hours", hours);
				info.put("minutes", mins);

			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return info;
	}

	public static long getSeqIndex(String date, int hours, int minutes) {
		long seqIndex = 0L;
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select SEQINDEX FROM BPMADMIN.TRADINGTIME where CDATE='"
					+ date + "' and CHOUR=" + hours + " and CMINUTE=" + minutes;

			rs1 = stmt1.executeQuery(SQL);

			if (rs1.next()) {
				seqIndex = rs1.getLong(1);
				System.out.println(date + " and CHOUR=" + hours
						+ " and CMINUTE=" + minutes + " seqIndex  is "
						+ seqIndex);
			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return seqIndex;
	}

	public static String getDateFromReference(int utime) {
		String dateString = "";
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select UTIME FROM BPMADMIN.GSEQ where UTIME>=" + utime
					+ "   order by UTIME asc";

			rs1 = stmt1.executeQuery(SQL);

			int t1up = 0;
			if (rs1.next()) {
				t1up = rs1.getInt(1);
				System.out.println("t1up is " + t1up);
			}

			SQL = "select UTIME FROM BPMADMIN.GSEQ where UTIME<=" + utime
					+ "   order by UTIME desc";

			rs1 = stmt1.executeQuery(SQL);

			int t2low = 0;
			if (rs1.next()) {
				t2low = rs1.getInt(1);
				System.out.println("t2low is " + t2low);
			}

			int tdiff = 0;
			int t_selected = 0;

			if ((t1up - utime) <= (utime - t2low)) {
				t_selected = t1up;
				tdiff = t1up - utime;

			} else if ((t1up - utime) > (utime - t2low)) {
				t_selected = t2low;
				tdiff = utime - t2low;
			}

			System.out.println("t selected is " + t_selected + " t diff is "
					+ tdiff);

			if (tdiff > 3600) {
				System.out.println("Error time difference more than one hour");
			}

			SQL = "Select VARCHAR_FORMAT(CDATE,'MM-DD-YYYY') AS VDATE  from BPMADMIN.GSEQ where UTIME="
					+ t_selected;
			rs1 = stmt1.executeQuery(SQL);
			if (rs1.next()) {
				dateString = rs1.getString(1);
				System.out.println("dateString is " + dateString);
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return dateString;

	}

	public static Hashtable getCurrentFirstUpStocks(int seqIndex) {
		Hashtable stocks = new Hashtable();
		System.out.println("Uptrend at index " + seqIndex);
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select SYMBOL, PTMA,UPTRENDSTART FROM BPMADMIN.INTRADAYSTOCKS where SEQINDEX="
					+ seqIndex
					+ "   and (UPTRENDSTART>=1 AND  DELTASUM>0) AND FINALPRICE>2 AND PTMA>-35 order by UPTRENDSTART desc";

			rs1 = stmt1.executeQuery(SQL);

			int count = 1;
			while (rs1.next()) {
				String symbol = rs1.getString(1);
				System.out.println("Add stock " + symbol);
				stocks.put(symbol, symbol);
			}

			System.out.println("Candidate size " + stocks.size());
			Enumeration en = stocks.keys();
			while (en.hasMoreElements()) {
				String symbol = en.nextElement().toString();
				System.out.println("Check stock " + symbol);
				if (isRealUpStartSP(symbol, seqIndex)) {
					System.out.println(" Uptrend 1st Start Point " + symbol);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return stocks;

	}

	public static void findLatestFFP(int seqIndexIn) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			if (seqIndexIn < 0) {
				getBothEnds("SPY");
			} else {
				endIndexLast = seqIndexIn;
			}

			SQL = "SELECT SYMBOL,FFP,FINALPRICE from BPMADMIN.INTRADAYSTOCKS  where FFP>0 and SEQINDEX="
					+ endIndexLast;

			System.out.println(SQL);

			rs1 = stmt1.executeQuery(SQL);

			float marketCap = 0.0f;
			int qualifiedCount = 0;
			int totalCount = 0;

			while (rs1.next()) {
				String symbol = rs1.getString(1);
				int ffp = rs1.getInt(2);
				float price = rs1.getFloat(3);
				int ffpPosCount1 = 0;
				int ffpPosCount2 = 0;
				boolean goodChoice = false;
				SQL = "SELECT SYMBOL,FFP,PTMA from BPMADMIN.INTRADAYSTOCKS  where  symbol='"
						+ symbol
						+ "' and SEQINDEX<="
						+ endIndexLast
						+ " order by seqIndex desc";
				rs2 = stmt2.executeQuery(SQL);
				int loopCount = 0;
				while (rs2.next() && loopCount < 15) {
					if (rs2.getInt(2) > 0)
						ffpPosCount1++;

					if (rs2.getInt(2) > 0 && loopCount < 2)
						ffpPosCount2++;

					if (rs2.getFloat(3) < 5.0f) {
						goodChoice = true;
						break;
					}
					loopCount++;
				}
				totalCount++;
				if (goodChoice && ffpPosCount1 == 2 && ffpPosCount2 == 2) {
					System.out.println(symbol + "   PRICE: " + price
							+ " within " + totalCount
							+ " days of PTMA<5.0f and FFP>0 count "
							+ ffpPosCount1);
					qualifiedCount++;
				} else {
					// System.out.println(symbol+" too late???");
				}
			}

			System.out.println("Total qualified stocks " + qualifiedCount
					+ " OUT OF " + totalCount);
			resetConnection();

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	private static float initMarketCapLowLimit(String marketCapSize) {
		float pvol1 = 0.0f;
		float pvol2 = 0.0f;

		if (marketCapSize.equalsIgnoreCase("microcap")) {
			pvol1 = 100000.0f;
			pvol2 = 1000000.0f;
		} else if (marketCapSize.equalsIgnoreCase("midcap")) {
			pvol1 = 5000000.0f;
			pvol2 = 100000000.0f;
		} else if (marketCapSize.equalsIgnoreCase("megacap")) {
			pvol1 = 500000000.0f;
			pvol2 = 5000000000.0f;
		} else {
			pvol1 = 1000.0f;
			pvol2 = 500000000000.0f;
		}
		return pvol1;
	}

	private static float initMarketCapUpperLimit(String marketCapSize) {
		float pvol1 = 0.0f;
		float pvol2 = 0.0f;

		if (marketCapSize.equalsIgnoreCase("microcap")) {
			pvol1 = 100000.0f;
			pvol2 = 1000000.0f;
		} else if (marketCapSize.equalsIgnoreCase("midcap")) {
			pvol1 = 5000000.0f;
			pvol2 = 100000000.0f;
		} else if (marketCapSize.equalsIgnoreCase("megacap")) {
			pvol1 = 500000000.0f;
			pvol2 = 5000000000.0f;
		} else {
			pvol1 = 1000.0f;
			pvol2 = 500000000000.0f;
		}
		return pvol2;
	}

	public static void findLFBB300SCORE(int seqIndexIn, String marketCapSize) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			if (seqIndexIn < 0) {
				getBothEnds("SPY");
			} else {
				endIndexLast = seqIndexIn;
			}

			SQL = "SELECT SYMBOL,BBSCORE,FINALPRICE,VOLUME from BPMADMIN.INTRADAYSTOCKS  where BBSCORE=3000 and SEQINDEX="
					+ endIndexLast;

			System.out.println(SQL);

			rs1 = stmt1.executeQuery(SQL);

			float marketCap = 0.0f;
			int qualifiedCount = 0;
			int totalCount = 0;
			float pvol1 = initMarketCapLowLimit(marketCapSize);
			float pvol2 = initMarketCapUpperLimit(marketCapSize);

			System.out.println(marketCapSize + " category output...");
			while (rs1.next()) {
				String symbol = rs1.getString(1);
				float price = rs1.getFloat(3);
				float vol = rs1.getFloat(4);
				if (vol * price >= pvol1 && vol * price <= pvol2) {
					SQL = "SELECT BBSCORE from BPMADMIN.INTRADAYSTOCKS  where  symbol='"
							+ symbol
							+ "' and SEQINDEX<"
							+ endIndexLast
							+ " and SEQINDEX>"
							+ (endIndexLast - 700)
							+ " and BBSCORE<>0 order by seqIndex desc";
					rs2 = stmt2.executeQuery(SQL);
					if (rs2.next()) {
						if (rs2.getInt(1) == 2000) {
							System.out.println(symbol + "   PRICE: " + price
									+ " has first BBSCORE=3000 today");
							qualifiedCount++;

						}
					}
				}
				totalCount++;
			}

			System.out.println("Total qualified stocks " + qualifiedCount
					+ " OUT OF " + totalCount);
			System.out.println("  ");
			resetConnection();

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void findDense300SCORE(int seqIndexIn) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			if (seqIndexIn < 0) {
				getBothEnds("SPY");
			} else {
				endIndexLast = seqIndexIn;
			}

			SQL = "SELECT SYMBOL,BBSCORE,FINALPRICE,VOLUME from BPMADMIN.INTRADAYSTOCKS  where BBSCORE=3000 and SEQINDEX="
					+ endIndexLast;

			System.out.println(SQL);

			rs1 = stmt1.executeQuery(SQL);

			float marketCap = 0.0f;
			int qualifiedCount = 0;
			int totalCount = 0;

			while (rs1.next()) {
				String symbol = rs1.getString(1);
				float price = rs1.getFloat(3);
				float vol = rs1.getFloat(4);
				if (vol * price > 5000000.0f) {
					SQL = "SELECT BBSCORE from BPMADMIN.INTRADAYSTOCKS  where  symbol='"
							+ symbol
							+ "' and SEQINDEX<"
							+ endIndexLast
							+ " and SEQINDEX>"
							+ (endIndexLast - 30)
							+ "  order by seqIndex desc";
					rs2 = stmt2.executeQuery(SQL);
					int loopCount = 0;
					int total3000 = 0;
					while (rs2.next()) {

						if (loopCount < 10 && rs2.getInt(1) == 3000) {
							total3000++;
						} else if (loopCount < 10 && rs2.getInt(1) < 3000
								&& rs2.getInt(1) != 0) {
							if (total3000 >= 3) {
								qualifiedCount++;
								System.out
										.println(symbol
												+ "   PRICE: "
												+ price
												+ " has more than 3 BBSCORE=3000 in last 10 days");
							}
							break;
						} else if (loopCount >= 10) {
							if (total3000 >= 3) {
								qualifiedCount++;
								System.out
										.println(symbol
												+ "   PRICE: "
												+ price
												+ " has more than 3 BBSCORE=3000 in last 10 days");
							}
							break;
						}

						loopCount++;
					}
				}
				totalCount++;
			}

			System.out.println("Total qualified stocks " + qualifiedCount
					+ " OUT OF " + totalCount);
			resetConnection();

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void findTodayDBCases(int seqIndexIn, String marketCapSize) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			if (seqIndexIn < 0) {
				getBothEnds("SPY");
			} else {
				endIndexLast = seqIndexIn;
			}

			SQL = "SELECT SYMBOL,UTIS,SELLINGSCORE,BBSCORE,FINALPRICE,VOLUME,PTMA from BPMADMIN.INTRADAYSTOCKS  where SEQINDEX="
					+ endIndexLast
					+ " AND (BBSCORE=3000 or UTIS=3) ORDER BY FINALPRICE DESC";

			System.out.println(SQL);

			rs1 = stmt1.executeQuery(SQL);

			float marketCap = 0.0f;
			int qualifiedCount = 0;
			int totalCount = 0;
			float pvol1 = initMarketCapLowLimit(marketCapSize);
			float pvol2 = initMarketCapUpperLimit(marketCapSize);

			System.out.println("Found UTIS = 3 OR BBSCORE=3000 stocks today: ");
			while (rs1.next()) {
				String symbol = rs1.getString(1);
				int utis = rs1.getInt(2);
				int sellingScore = rs1.getInt(3);
				int bbscore = rs1.getInt(4);
				float price = rs1.getFloat(5);
				float vol = rs1.getFloat(6);
				float ptma = rs1.getFloat(7);

				String SQL2 = null;

				if (vol * price >= pvol1 && vol * price <= pvol2) {
					if (utis == 3 && bbscore == 3000) {
						totalCount++;
						System.out.println("Qualified double bull case1: "
								+ symbol + "  at 0 days, Price: " + price
								+ ", ptma: " + ptma + ", sellingscore:"
								+ sellingScore + ", vol:" + vol);
					} else if (utis == 3 && bbscore < 3000) {
						SQL2 = "SELECT SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where SYMBOL = '"
								+ symbol
								+ "' and BBSCORE=3000 and SEQINDEX<="
								+ endIndexLast + " ORDER BY SEQINDEX DESC";

						// System.out.println(SQL2);

						rs2 = stmt2.executeQuery(SQL2);
						if (rs2.next()) {
							int indexNext = rs2.getInt(1);

							String SQL3 = "SELECT count(*) from BPMADMIN.INTRADAYSTOCKS  where SYMBOL = '"
									+ symbol
									+ "' and SEQINDEX>="
									+ indexNext
									+ " and SEQINDEX<="
									+ endIndexLast
									+ " AND SELLINGSCORE<0";

							rs3 = stmt3.executeQuery(SQL3);

							int negCount = 0;
							if (rs3.next()) {
								negCount = rs3.getInt(1);
							}

							if (negCount == 0) {
								SQL3 = "SELECT count(*) from BPMADMIN.INTRADAYSTOCKS  where SYMBOL = '"
										+ symbol
										+ "' and SEQINDEX>="
										+ indexNext
										+ " and SEQINDEX<="
										+ endIndexLast;

								rs3 = stmt3.executeQuery(SQL3);

								int daysCount = 0;
								if (rs3.next()) {
									totalCount++;
									daysCount = rs3.getInt(1) - 1;
									System.out
											.println("Qualified double bull case2: "
													+ symbol
													+ "  at "
													+ daysCount
													+ " days,"
													+ " Price: "
													+ price
													+ ", ptma: "
													+ ptma
													+ ", sellingscore:"
													+ sellingScore
													+ ", vol:"
													+ vol);

								}

							}

						}

					} else if (utis < 3 && bbscore == 3000) {
						SQL2 = "SELECT SEQINDEX from BPMADMIN.INTRADAYSTOCKS  where SYMBOL = '"
								+ symbol
								+ "' and UTIS=3 and SEQINDEX<="
								+ endIndexLast + " ORDER BY SEQINDEX DESC";

						// System.out.println(SQL2);

						rs2 = stmt2.executeQuery(SQL2);
						if (rs2.next()) {
							int indexNext = rs2.getInt(1);

							String SQL3 = "SELECT count(*) from BPMADMIN.INTRADAYSTOCKS  where SYMBOL = '"
									+ symbol
									+ "' and SEQINDEX>="
									+ indexNext
									+ " and SEQINDEX<="
									+ endIndexLast
									+ " AND BBSCORE=3000";

							rs3 = stmt3.executeQuery(SQL3);

							int BB3000Count = 0;
							if (rs3.next()) {
								BB3000Count = rs3.getInt(1);
							}

							if (BB3000Count == 1) {

								SQL3 = "SELECT count(*) from BPMADMIN.INTRADAYSTOCKS  where SYMBOL = '"
										+ symbol
										+ "' and SEQINDEX>="
										+ indexNext
										+ " and SEQINDEX<="
										+ endIndexLast + " AND SELLINGSCORE<0";

								rs3 = stmt3.executeQuery(SQL3);

								int negCount = 0;
								if (rs3.next()) {
									negCount = rs3.getInt(1);
								}

								if (negCount == 0) {
									totalCount++;
									SQL3 = "SELECT count(*) from BPMADMIN.INTRADAYSTOCKS  where SYMBOL = '"
											+ symbol
											+ "' and SEQINDEX>="
											+ indexNext
											+ " and SEQINDEX<="
											+ endIndexLast;

									rs3 = stmt3.executeQuery(SQL3);

									int daysCount = 0;
									if (rs3.next()) {
										daysCount = rs3.getInt(1) - 1;
										System.out
												.println("Qualified double bull case3: "
														+ symbol
														+ "  at "
														+ daysCount
														+ " days, Price: "
														+ price
														+ ", ptma: "
														+ ptma
														+ ", sellingscore:"
														+ sellingScore
														+ ", vol:" + vol);

									}
								} else {
									// System.out
									// .println("Not Qualified double bull case3: "
									// + symbol+" NegaCount>0");

								}
							} else {
								// System.out
								// .println("Not Qualified double bull case3: "
								// + symbol+" BBSCORE3000 not first");

							}

						}

					}
				}

			}

			System.out.println("Total qualified stocks " + totalCount);
			System.out.println(" ");
			resetConnection();

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void findTodayUTIS3(int seqIndexIn) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			if (seqIndexIn < 0) {
				getBothEnds("SPY");
			} else {
				endIndexLast = seqIndexIn;
			}

			SQL = "SELECT SYMBOL,SELLINGSCORE,FINALPRICE,VOLUME,PTMA from BPMADMIN.INTRADAYSTOCKS  where UTIS = 3 and SEQINDEX="
					+ endIndexLast + " ORDER BY FINALPRICE ASC";

			System.out.println(SQL);

			rs1 = stmt1.executeQuery(SQL);

			float marketCap = 0.0f;
			int qualifiedCount = 0;
			int totalCount = 0;

			System.out.println("Found UTIS = 3 stocks today: ");
			while (rs1.next()) {
				String symbol = rs1.getString(1);
				int sellingScore = rs1.getInt(2);
				float price = rs1.getFloat(3);
				float vol = rs1.getFloat(4);
				float ptma = rs1.getFloat(5);

				if (vol * price > 5000000.0f) {
					String SQL2 = "SELECT UTIS from BPMADMIN.INTRADAYSTOCKS  where SYMBOL = '"
							+ symbol
							+ "' and SEQINDEX<="
							+ endIndexLast
							+ " ORDER BY SEQINDEX DESC";

					// System.out.println(SQL2);

					rs2 = stmt2.executeQuery(SQL2);

					int lcount = 0;
					boolean nextUTISNot3 = true;

					while (rs2.next()) {
						int utisNext = rs2.getInt(1);
						if (lcount == 1 && utisNext >= 2) {
							nextUTISNot3 = false;
							/*
							 * System.out .println(
							 * "Not qualified for continuous UTIS 3 values: " +
							 * symbol + "  price: " + price + ", UTIS: " +
							 * utisNext + ", ptma: " + ptma + " ,volume" + vol);
							 */
							break;
						} else if (lcount >= 1) {
							break;
						}
						lcount++;
					}

					if (nextUTISNot3) {
						qualifiedCount++;
						System.out.println(symbol + "  price: " + price
								+ ", ptma: " + ptma + " ,volume" + vol);
					}

				}
				totalCount++;
			}

			System.out.println("Total qualified stocks " + qualifiedCount
					+ " OUT OF " + totalCount);
			System.out.println(" ");
			resetConnection();

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void findTodayAWS10(int seqIndexIn, String marketCapSize) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			if (seqIndexIn < 0) {
				getBothEnds("SPY");
			} else {
				endIndexLast = seqIndexIn;
			}

			SQL = "SELECT SYMBOL,SELLINGSCORE,FINALPRICE,VOLUME,PTMA, AWS from BPMADMIN.INTRADAYSTOCKS  where  SEQINDEX="
					+ endIndexLast;

			System.out.println(SQL);

			rs1 = stmt1.executeQuery(SQL);

			float marketCap = 0.0f;
			int qualifiedCount = 0;
			int totalCount = 0;
			int lcount = 0;
			float pvol1 = initMarketCapLowLimit(marketCapSize);
			float pvol2 = initMarketCapUpperLimit(marketCapSize);

			while (rs1.next()) {
				String symbol = rs1.getString(1);
				int sellingScore = rs1.getInt(2);
				float price = rs1.getFloat(3);
				float vol = rs1.getFloat(4);
				float ptma = rs1.getFloat(5);
				int aws = rs1.getInt(6);

				if (vol * price >= pvol1 && vol * price <= pvol2) {
					String SQL2 = "SELECT AWS,FINALPRICE from BPMADMIN.INTRADAYSTOCKS  where SYMBOL = '"
							+ symbol
							+ "' and SEQINDEX<"
							+ endIndexLast
							+ " AND SEQINDEX>"
							+ (endIndexLast - 10)
							+ " ORDER BY SEQINDEX DESC";

					// System.out.println(SQL2);

					rs2 = stmt2.executeQuery(SQL2);

					if (rs2.next()) {
						int awsNext = rs2.getInt(1);
						float priceNext = rs2.getFloat(2);
						float change = 100.0f * (price - priceNext) / priceNext;
						if (((aws - awsNext) >= 10) && change < 3.0f
								&& sellingScore > 10) {
							System.out.println("Find AWS delta "
									+ (aws - awsNext) + " for " + symbol
									+ ", price: " + price + ", selling score:"
									+ sellingScore + ", ptma:" + ptma);
							totalCount++;

						}

					}

					lcount++;
				}

			}

			System.out.println("Total qualified stocks " + totalCount
					+ " OUT OF " + lcount);
			resetConnection();

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void findToday2_5Points(int seqIndexIn, String marketCapSize) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			if (seqIndexIn < 0) {
				getBothEnds("SPY");
			} else {
				endIndexLast = seqIndexIn;
			}

			SQL = "SELECT SYMBOL,SELLINGSCORE,FINALPRICE,VOLUME,PTMA, AWS, LBBI from BPMADMIN.INTRADAYSTOCKS  where  SEQINDEX="
					+ endIndexLast + " and LBBI = -5 ";

			System.out.println(SQL);

			rs1 = stmt1.executeQuery(SQL);

			float marketCap = 0.0f;
			int qualifiedCount = 0;
			int totalCount = 0;
			int lcount = 0;
			int lbbi = 0;
			float pvol1 = initMarketCapLowLimit(marketCapSize);
			float pvol2 = initMarketCapUpperLimit(marketCapSize);

			while (rs1.next()) {
				String symbol = rs1.getString(1);
				int sellingScore = rs1.getInt(2);
				float price = rs1.getFloat(3);
				float vol = rs1.getFloat(4);
				float ptma = rs1.getFloat(5);
				int aws = rs1.getInt(6);
				lbbi = rs1.getInt(7);

				if (vol * price >= pvol1 && vol * price <= pvol2) {
					String SQL2 = "SELECT LBBI,FINALPRICE from BPMADMIN.INTRADAYSTOCKS  where SYMBOL = '"
							+ symbol
							+ "' and SEQINDEX<"
							+ endIndexLast
							+ " AND SEQINDEX>"
							+ (endIndexLast - 300)
							+ " AND LBBI<>0 ORDER BY SEQINDEX DESC";

					// System.out.println(SQL2);

					rs2 = stmt2.executeQuery(SQL2);

					if (rs2.next()) {
						int lbbiNext = rs2.getInt(1);
						float priceNext = rs2.getFloat(2);
						if (lbbiNext == 2 && lbbi == -5) {
							System.out
									.println("Find LBBI dipping buy point  for "
											+ symbol
											+ ", price: "
											+ price
											+ ", selling score:"
											+ sellingScore
											+ ", ptma:" + ptma);
							totalCount++;

						}

					}

					lcount++;
				}

			}

			System.out.println("Total qualified stocks " + totalCount
					+ " OUT OF " + lcount);
			resetConnection();

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void findTodayBBSCOREExtreme(int seqIndexIn) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			if (seqIndexIn < 0) {
				getBothEnds("SPY");
			} else {
				endIndexLast = seqIndexIn;
			}

			SQL = "SELECT SYMBOL,SELLINGSCORE,FINALPRICE,VOLUME from BPMADMIN.INTRADAYSTOCKS  where (SELLINGSCORE>=60 OR SELLINGSCORE<=-40) and SEQINDEX="
					+ endIndexLast;

			System.out.println(SQL);

			rs1 = stmt1.executeQuery(SQL);

			float marketCap = 0.0f;
			int qualifiedCount = 0;
			int totalCount = 0;

			while (rs1.next()) {
				String symbol = rs1.getString(1);
				float price = rs1.getFloat(3);
				float vol = rs1.getFloat(4);
				if (vol * price > 5000000.0f) {
					SQL = "SELECT SELLINGSCORE from BPMADMIN.INTRADAYSTOCKS  where  symbol='"
							+ symbol
							+ "' and SEQINDEX<"
							+ endIndexLast
							+ " and SEQINDEX>"
							+ (endIndexLast - 10)
							+ " and SELLINGSCORE<>0 order by seqIndex desc";
					rs2 = stmt2.executeQuery(SQL);
					if (rs2.next()
							&& ((rs2.getInt(1) < 60 && rs2.getInt(1) > 50) || (rs2
									.getInt(1) > -40 && rs2.getInt(1) < -30))) {
						if (rs2.getInt(1) > 0) {
							System.out.println(symbol + "   PRICE: " + price
									+ " has first SELLINGSCORE>=60 today");
							qualifiedCount++;
						} else if (rs2.getInt(1) < 0) {
							System.out.println(symbol + "   PRICE: " + price
									+ " has first SELLINGSCORE<=-40 today");
							qualifiedCount++;
						}
					}
				}
				totalCount++;
			}

			System.out.println("Total qualified stocks " + qualifiedCount
					+ " OUT OF " + totalCount);
			resetConnection();
			System.out.println(" ");

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static Hashtable getUnCalculationReadyStocks(long start, long end) {

		Hashtable result = new Hashtable();
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			// the parallel thread sorted by DESC, so this one ASC

			String SQL = "select distinct(SYMBOL), sum(DSI3) FROM BPMADMIN.INTRADAYSTOCKS where seqindex<="
					+ end
					+ " and seqindex>="
					+ start
					+ " group by symbol having sum(DSI3)<0.001 and  sum(DSI3)>-0.001";

			System.out.println(SQL);

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				// System.out.println(rs1.getString(1) + "   DCP: "
				// + rs1.getInt(2) + "  Price: " + rs1.getFloat(3));
				String stock = rs1.getString(1);
				result.put(stock, stock);

			}

			// resetConnection();

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return result;

	}

	public static void updateNotDownloadedStocks(int folder) {

		Hashtable result = new Hashtable();
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			// the parallel thread sorted by DESC, so this one ASC

			String SQL = "SELECT distinct(SYMBOL) from BPMADMIN.INTRADAYSTOCKS  where uptrendstart=99 Order by SYMBOL ASC";

			System.out.println(SQL);

			rs1 = stmt1.executeQuery(SQL);

			Hashtable all = new Hashtable();

			while (rs1.next()) {
				// System.out.println(rs1.getString(1) + "   DCP: "
				// + rs1.getInt(2) + "  Price: " + rs1.getFloat(3));
				String stock = rs1.getString(1);
				all.put(stock, stock);

			}

			Enumeration en = all.keys();

			while (en.hasMoreElements()) {
				String nextStock = en.nextElement().toString();

				boolean exist = Files
						.checkIntradayFileExists(nextStock, folder);

				if (!exist) {
					setCalculationDoneTag(nextStock);
				}
			}

			// resetConnection();

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static Hashtable getCalculationReadyStocks() {

		Hashtable result = new Hashtable();
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			// the parallel thread sorted by DESC, so this one ASC

			String SQL = "SELECT distinct(SYMBOL) from BPMADMIN.INTRADAYSTOCKS  where uptrendstart=99 Order by SYMBOL ASC";

			System.out.println(SQL);

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				// System.out.println(rs1.getString(1) + "   DCP: "
				// + rs1.getInt(2) + "  Price: " + rs1.getFloat(3));
				String stock = rs1.getString(1);
				result.put(stock, stock);

			}

			// resetConnection();

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return result;

	}

	public static Hashtable getMyStocks(long seqIndex, int filter) {

		Hashtable result = new Hashtable();
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			// the parallel thread sorted by DESC, so this one ASC

			String SQL = "SELECT distinct(SYMBOL) from BPMADMIN.INTRADAYSTOCKS  where uptrendstart="
					+ filter + " AND SEQINDEX=" + seqIndex;

			System.out.println(SQL);

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				// System.out.println(rs1.getString(1) + "   DCP: "
				// + rs1.getInt(2) + "  Price: " + rs1.getFloat(3));
				String stock = rs1.getString(1);
				result.put(stock, stock);

			}

			// resetConnection();

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return result;

	}

	public static Hashtable getAllStocks(long seqIndex) {

		Hashtable result = new Hashtable();
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			// the parallel thread sorted by DESC, so this one ASC

			String SQL = "SELECT distinct(SYMBOL) from BPMADMIN.INTRADAYSTOCKS  where (uptrendstart=993 OR uptrendstart=996 OR uptrendstart=999) "
					+ " AND SEQINDEX=" + seqIndex;

			System.out.println(SQL);

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				// System.out.println(rs1.getString(1) + "   DCP: "
				// + rs1.getInt(2) + "  Price: " + rs1.getFloat(3));
				String stock = rs1.getString(1);
				result.put(stock, stock);

			}

			// resetConnection();

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return result;

	}

	public static void findLatest7DCP(int seqIndexIn) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			if (seqIndexIn < 0) {
				getBothEnds("SPY");
			} else {
				endIndexLast = seqIndexIn;
			}

			SQL = "SELECT SYMBOL,DCP,FINALPRICE from BPMADMIN.INTRADAYSTOCKS  where DCP<7 and (DCP>0.1 or DCP<-0.1) AND (DSI3>0.00001 OR DSI3<-0.00001) and SEQINDEX="
					+ endIndexLast + " ORDER BY FINALPRICE DESC";

			System.out.println(SQL);

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				System.out.println(rs1.getString(1) + "   DCP: "
						+ rs1.getInt(2) + "  Price: " + rs1.getFloat(3));

			}

			resetConnection();

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void findCurrentCap(String symbol, int currentIndex) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select marketcap from FULLVSTRANK where SEQINDEX="
					+ seqIndex + " and  symbol='" + symbol + "'";

			rs1 = stmt1.executeQuery(SQL);

			float marketCap = 0.0f;

			if (rs1.next()) {
				marketCap = rs1.getFloat(1);
			}

			SQL = "select ADJUSTEDPRICE from INTRADAYSTOCKS where SEQINDEX="
					+ seqIndex + " and  symbol='" + symbol + "'";

			rs1 = stmt1.executeQuery(SQL);

			float todayPrice = 0.0f;

			if (rs1.next()) {
				todayPrice = rs1.getFloat(1);
			}

			SQL = "select ADJUSTEDPRICE from INTRADAYSTOCKS where SEQINDEX="
					+ currentIndex + " and  symbol='" + symbol + "'";

			rs1 = stmt1.executeQuery(SQL);

			float thatPrice = 0.0f;

			if (rs1.next()) {
				thatPrice = rs1.getFloat(1);
			}

			if (thatPrice > 0.01f && todayPrice > 0.01f && marketCap > 10) {
				System.out.println("MarketCap at that time is " + thatPrice
						* marketCap / todayPrice);
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	/*
	 * public static void findCurrentCap(String symbol, int currentIndex) {
	 * 
	 * try { // Establish the connection. if (con == null) con =
	 * getConnection();
	 * 
	 * SQL = "select marketcap from FULLVSTRANK where SEQINDEX=" + seqIndex +
	 * " and  symbol='" + symbol + "'";
	 * 
	 * rs1 = stmt1.executeQuery(SQL);
	 * 
	 * float marketCap = 0.0f;
	 * 
	 * if (rs1.next()) { marketCap = rs1.getFloat(1); }
	 * 
	 * SQL = "select ADJUSTEDPRICE from INTRADAYSTOCKS where SEQINDEX=" +
	 * seqIndex + " and  symbol='" + symbol + "'";
	 * 
	 * rs1 = stmt1.executeQuery(SQL);
	 * 
	 * float todayPrice = 0.0f;
	 * 
	 * if (rs1.next()) { todayPrice = rs1.getFloat(1); }
	 * 
	 * SQL = "select ADJUSTEDPRICE from INTRADAYSTOCKS where SEQINDEX=" +
	 * currentIndex + " and  symbol='" + symbol + "'";
	 * 
	 * rs1 = stmt1.executeQuery(SQL);
	 * 
	 * float thatPrice = 0.0f;
	 * 
	 * if (rs1.next()) { thatPrice = rs1.getFloat(1); }
	 * 
	 * if (thatPrice > 0.01f && todayPrice > 0.01f && marketCap > 10) {
	 * System.out.println("MarketCap at that time is " + thatPrice marketCap /
	 * todayPrice); }
	 * 
	 * } catch (Exception ex) { ex.printStackTrace();
	 * 
	 * }
	 * 
	 * }
	 */
	public static Hashtable getCurrentVSTBigCapStocks(int seqIndex,
			float marketCap) {
		Hashtable stocks = new Hashtable();

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select SYMBOL from FULLVSTRANK where SEQINDEX=" + seqIndex
					+ "  and marketcap>=" + marketCap
					+ " order by marketcap DESC";

			rs1 = stmt1.executeQuery(SQL);

			int count = 1;
			while (rs1.next()) {
				String symbol = rs1.getString(1);
				stocks.put(symbol, symbol);

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return stocks;

	}

	public static Hashtable getProperCorrectionStocks(int seqIndex) {
		Hashtable stocks = new Hashtable();

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select SYMBOL from INTRADAYSTOCKS where ACPTMA>0 AND SEQINDEX="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			int count = 1;
			while (rs1.next()) {
				String symbol = rs1.getString(1);
				stocks.put(symbol, symbol);

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return stocks;

	}

	public static Hashtable getPCUStocks(int seqIndex) {
		Hashtable stocks = new Hashtable();
		Hashtable stockResults = new Hashtable();

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select SYMBOL, ACPTMA from INTRADAYSTOCKS where ACPTMA>0.1 AND SEQINDEX="
					+ seqIndex
					+ " AND ADJUSTEDPRICE>10 AND VOLUME>500000 AND GR100<-29";

			rs1 = stmt1.executeQuery(SQL);

			int count = 1;
			while (rs1.next()) {
				String symbol = rs1.getString(1);
				float acptma = rs1.getFloat(2);
				stocks.put(symbol, symbol);

			}

			Enumeration en = stocks.keys();

			while (en.hasMoreElements()) {
				try {
					String sym = en.nextElement().toString();

					SQL = "select PTMA, ADJUSTEDPRICE from INTRADAYSTOCKS where  SEQINDEX="
							+ (seqIndex + 1);

					rs1 = stmt1.executeQuery(SQL);
					if (rs1.next()) {
						float ptma = rs1.getFloat(1);
						float price = rs1.getFloat(2);

						if (ptma > 8.0f) {
							if (getStockCap(sym, price) > 1000) {
								stockResults.put(sym, sym);
							}

						}

					}
				} catch (Exception ex) {

				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return stockResults;

	}

	public static Hashtable NotUsedgetFFPBULLStocks(int seqIndex) {
		Hashtable stocks = new Hashtable();

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select SYMBOL from INTRADAYSTOCKS where FFP=100 AND SEQINDEX="
					+ seqIndex + " AND ADJUSTEDPRICE>20 AND VOLUME>500000 ";

			rs1 = stmt1.executeQuery(SQL);

			int count = 1;
			while (rs1.next()) {
				String symbol = rs1.getString(1);
				stocks.put(symbol, symbol);

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return stocks;

	}

	public static Hashtable NotUsedgetGOGOBULLStocks(int seqIndex) {
		Hashtable stocks = new Hashtable();
		Hashtable stockResults = new Hashtable();

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select SYMBOL, BBGO from INTRADAYSTOCKS where BBGO=100 AND SEQINDEX="
					+ seqIndex + " AND ADJUSTEDPRICE>5 AND VOLUME>500000 ";

			rs1 = stmt1.executeQuery(SQL);

			int count = 1;
			while (rs1.next()) {
				String symbol = rs1.getString(1);
				float acptma = rs1.getFloat(2);
				stocks.put(symbol, symbol);

			}

			Enumeration en = stocks.keys();

			while (en.hasMoreElements()) {
				try {
					String sym = en.nextElement().toString();

					if (sym.equalsIgnoreCase("FOLD")) {
						int k = 0;
						k++;
					}

					SQL = "select BBGO, SEQINDEX,  ADJUSTEDPRICE from INTRADAYSTOCKS where  SYMBOL ='"
							+ sym
							+ "' AND BBGO<>0 AND SEQINDEX<"
							+ seqIndex
							+ " ORDER BY SEQINDEX DESC";

					rs1 = stmt1.executeQuery(SQL);

					if (rs1.next()) {
						int bbgo = rs1.getInt(1);
						int index = rs1.getInt(2);
						float lastPrice = rs1.getFloat(3);

						// if (bbgo > 0 && (seqIndex - index) > 60) {

						SQL = "select PTMA, ADJUSTEDPRICE from INTRADAYSTOCKS where  SEQINDEX="
								+ seqIndex + " and SYMBOL ='" + sym + "'";

						rs1 = stmt1.executeQuery(SQL);
						if (rs1.next()) {
							float ptma = rs1.getFloat(1);
							float price = rs1.getFloat(2);

							if (price > lastPrice
									&& getStockCap(sym, price) > 1000) {
								stockResults.put(sym, sym);
							}

						}
						// }
					}

				} catch (Exception ex) {

				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return stockResults;

	}

	public static Hashtable NotUSedgetGOGOBEARStocks(int seqIndex) {
		Hashtable stocks = new Hashtable();
		Hashtable stockResults = new Hashtable();

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select SYMBOL, BBGO from INTRADAYSTOCKS where BBGO=-1 AND SEQINDEX="
					+ seqIndex + " AND ADJUSTEDPRICE>5 AND VOLUME>500000 ";

			rs1 = stmt1.executeQuery(SQL);

			int count = 1;
			while (rs1.next()) {
				String symbol = rs1.getString(1);
				float acptma = rs1.getFloat(2);
				stocks.put(symbol, symbol);

			}

			Enumeration en = stocks.keys();

			while (en.hasMoreElements()) {
				try {
					String sym = en.nextElement().toString();

					if (sym.equalsIgnoreCase("FOLD")) {
						int k = 0;
						k++;
					}

					SQL = "select FFP, SEQINDEX,  ADJUSTEDPRICE from INTRADAYSTOCKS where  SYMBOL ='"
							+ sym
							+ "' AND FFP>0 AND SEQINDEX<"
							+ seqIndex
							+ " ORDER BY SEQINDEX DESC";

					rs1 = stmt1.executeQuery(SQL);

					if (rs1.next()) {
						int FFP = rs1.getInt(1);
						int index = rs1.getInt(2);
						float lastPrice = rs1.getFloat(3);

						if (FFP > 100) {

							SQL = "select COUNT(*) from INTRADAYSTOCKS where  BBGO<0 AND SEQINDEX<"
									+ seqIndex
									+ " AND SEQINDEX> "
									+ index
									+ " and SYMBOL ='" + sym + "'";

							rs1 = stmt1.executeQuery(SQL);
							if (rs1.next()) {
								int countGo = rs1.getInt(1);

								// if (price > lastPrice
								// && getStockCap(sym, price) > 1000) {
								// if (getStockCap(sym, price) > 1000) {
								if (countGo == 0) {
									stockResults.put(sym, sym);
								}
								// }

							}
						}
					}
				} catch (Exception ex) {

				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return stockResults;

	}

	public static Hashtable getUpUpStocks(int seqIndex) {
		Hashtable stocks = new Hashtable();

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select SYMBOL, SEQINDEX from INTRADAYSTOCKS where FFP=1 AND SEQINDEX="
					+ seqIndex + " AND ADJUSTEDPRICE>10";

			rs1 = stmt1.executeQuery(SQL);

			int count = 1;
			while (rs1.next()) {
				String symbol = rs1.getString(1);
				int index = rs1.getInt(2);
				SQL = "SELECT SEQINDEX, ACPTMA from INTRADAYSTOCKS  where   symbol='"
						+ symbol
						+ "' and ACPTMA<>0 and SEQINDEX<"
						+ index
						+ " ORDER BY SEQINDEX DESC";
				rs2 = stmt2.executeQuery(SQL);

				if (rs2.next()) {
					int index2 = rs2.getInt(1);
					float acptma = rs2.getFloat(2);
					if ((index - index2) < 80 && acptma < 5) {
						stocks.put(symbol, symbol);
					}

				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return stocks;

	}

	public static Hashtable getUberBullStocks(int seqIndex) {
		Hashtable stocks = new Hashtable();

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select SYMBOL, SEQINDEX, ADJUSTEDPRICE from INTRADAYSTOCKS where PTMA>50.0 AND SEQINDEX="
					+ seqIndex
					+ " AND ADJUSTEDPRICE>10 AND GR100<60 AND VOLUME>500000";

			rs1 = stmt1.executeQuery(SQL);

			int count = 1;
			while (rs1.next()) {
				String symbol = rs1.getString(1);
				int index = rs1.getInt(2);
				float price = rs1.getFloat(3);
				SQL = "SELECT count(*) from INTRADAYSTOCKS  where   symbol='"
						+ symbol + "' and PTMA>50.0  and SEQINDEX<" + index
						+ " AND SEQINDEX>" + (index - 120);
				rs2 = stmt2.executeQuery(SQL);

				if (rs2.next()) {
					int num = rs2.getInt(1);

					if (num == 0) {
						if (getStockCap(symbol, price) > 20000) {
							stocks.put(symbol, symbol);
						}
					}

				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return stocks;

	}

	public static Hashtable getUberBearStocks(int seqIndex) {
		Hashtable stocks = new Hashtable();

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select SYMBOL, SEQINDEX, ADJUSTEDPRICE from INTRADAYSTOCKS where PTMA<-50.0 AND SEQINDEX="
					+ seqIndex
					+ " AND ADJUSTEDPRICE>10 AND VOLUME>500000 AND GR100>-60";

			rs1 = stmt1.executeQuery(SQL);

			int count = 1;
			while (rs1.next()) {
				String symbol = rs1.getString(1);
				int index = rs1.getInt(2);
				float price = rs1.getFloat(3);
				SQL = "SELECT count(*) from INTRADAYSTOCKS  where   symbol='"
						+ symbol + "' and PTMA<-50.0  and SEQINDEX<" + index
						+ " AND SEQINDEX>" + (index - 300);
				rs2 = stmt2.executeQuery(SQL);

				if (rs2.next()) {
					int num = rs2.getInt(1);

					if (num == 0) {
						if (getStockCap(symbol, price) > 5000) {
							stocks.put(symbol, symbol);
						}
					}

				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return stocks;

	}

	public static Hashtable getQuickTurnStocks(int seqIndex) {
		Hashtable stocks = new Hashtable();

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select SYMBOL, SEQINDEX from INTRADAYSTOCKS where FFP=1 AND SEQINDEX="
					+ seqIndex + " AND ADJUSTEDPRICE>10";

			rs1 = stmt1.executeQuery(SQL);

			int count = 1;
			while (rs1.next()) {
				String symbol = rs1.getString(1);
				int index = rs1.getInt(2);
				SQL = "SELECT SEQINDEX, ACPTMA, ADJUSTEDPRICE from INTRADAYSTOCKS  where   symbol='"
						+ symbol
						+ "' and ACPTMA<>0 and SEQINDEX<"
						+ index
						+ " ORDER BY SEQINDEX DESC";
				rs2 = stmt2.executeQuery(SQL);

				if (rs2.next()) {
					int index2 = rs2.getInt(1);
					float acptma = rs2.getFloat(2);
					float price = rs2.getFloat(3);
					if ((index - index2) < 50) {
						SQL = "SELECT COUNT(*) from INTRADAYSTOCKS  where   symbol='"
								+ symbol
								+ "' and FFP=1 and SEQINDEX<"
								+ index
								+ " and SEQINDEX> " + index2;
						rs2 = stmt2.executeQuery(SQL);
						if (rs2.next()) {
							if (rs2.getInt(1) == 0) {
								stocks.put(symbol, symbol);
							}
						}
					}

				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return stocks;

	}

	/*
	 * public static void getIndexChangePercentage(int seqIndex) {
	 * 
	 * try { // Establish the connection. if (con == null) con =
	 * getConnection();
	 * 
	 * SQL =
	 * "SELECT SEQINDEX,SYMBOL,CURRENTDATE,ADJUSTEDPRICE,GR100,FFP,dcp,acptma FROM INTRADAYSTOCKS where (SYMBOL LIKE '^%' or SYMBOL='SPY') and seqIndex<="
	 * + seqIndex + "  ORDER BY SEQINDEX DESC, SYMBOL DESC";
	 * 
	 * rs1 = stmt1.executeQuery(SQL);
	 * 
	 * int count = 0; StringBuffer heading = new StringBuffer(); StringBuffer
	 * nextLine = new StringBuffer(); int seqIndex1 = 0; int seqIndex2 = 0;
	 * float sum = 0.0f; int rowCount = 0;
	 * 
	 * while (rs1.next()) {
	 * 
	 * if (seqIndex1 == 0) { count = 1; seqIndex1 = rs1.getInt(1);
	 * heading.append("Date").append(',');
	 * nextLine.append(rs1.getString(3)).append(','); if
	 * (!rs1.getString(2).equalsIgnoreCase("^MERV") &&
	 * !rs1.getString(2).equalsIgnoreCase("^VIX") &&
	 * !rs1.getString(2).equalsIgnoreCase("^XOI") &&
	 * !rs1.getString(2).equalsIgnoreCase("^XAU")) {
	 * heading.append(rs1.getString(2)).append(',');
	 * nextLine.append(rs1.getFloat(7)).append(','); rowCount++; sum =
	 * rs1.getFloat(7) + sum; } } else seqIndex2 = rs1.getInt(1);
	 * 
	 * if (seqIndex1 == seqIndex2) { if
	 * (!rs1.getString(2).equalsIgnoreCase("^MERV") &&
	 * !rs1.getString(2).equalsIgnoreCase("^VIX") &&
	 * !rs1.getString(2).equalsIgnoreCase("^XOI") &&
	 * !rs1.getString(2).equalsIgnoreCase("^XAU")) {
	 * 
	 * if (count == 1) {
	 * 
	 * heading.append(rs1.getString(2)).append(',');
	 * 
	 * } nextLine.append(rs1.getFloat(7)).append(','); rowCount++; sum =
	 * rs1.getFloat(7) + sum; } } else if (seqIndex2 > 0) { if
	 * (!rs1.getString(2).equalsIgnoreCase("^MERV") &&
	 * !rs1.getString(2).equalsIgnoreCase("^VIX") &&
	 * !rs1.getString(2).equalsIgnoreCase("^XOI") &&
	 * !rs1.getString(2).equalsIgnoreCase("^XAU")) {
	 * 
	 * if (count == 1) { Files.appendToFile("C:\\stocks\\Book19.csv",
	 * heading.append("SUM").toString()); System.out.println("Heading " +
	 * heading.toString()); }
	 * 
	 * if (rowCount == 38) { Files.appendToFile("C:\\stocks\\Book19.csv",
	 * nextLine.append(sum).toString()); }
	 * 
	 * System.out.println("Next " + nextLine.toString()); nextLine = new
	 * StringBuffer(); nextLine.append(rs1.getString(3)).append(',');
	 * nextLine.append(rs1.getFloat(7)).append(','); rowCount = 1; sum =
	 * rs1.getFloat(7); seqIndex1 = seqIndex2; count++; }
	 * 
	 * }
	 * 
	 * } } catch (Exception ex) { ex.printStackTrace();
	 * 
	 * }
	 * 
	 * }
	 */

	public static void getIndexStatus(int seqIndex, String mode) {
		Hashtable indexes = IndexHistory.initIndexTable();
		// indexes.put("SPY", "SPY");
		Hashtable excludedSymbols = new Hashtable();
		// excludedSymbols.put("^MERV", "^MERV");
		// excludedSymbols.put("^VIX", "^VIX");
		// excludedSymbols.put("^XOI", "^XOI");
		// excludedSymbols.put("^XAU", "^XAU");
		boolean useExcluded = true;

		Hashtable includedSymbols = new Hashtable();
		includedSymbols.put("XAO", "XAO");
		includedSymbols.put("OMX", "OMX");
		includedSymbols.put("HSI", "HSI");
		includedSymbols.put("KLCI", "KLCI");
		includedSymbols.put("BVSP", "BVSP");
		includedSymbols.put("AEX", "AEX");
		// includedSymbols.put("^HSCE", "^HSCE");//?? MAP TO WHAT
		includedSymbols.put("BSESN", "BSESN");
		includedSymbols.put("NI225", "NI225");
		includedSymbols.put("ATX", "ATX");
		// includedSymbols.put("^STI", "^STI");
		includedSymbols.put("SSMI", "SSMI");
		includedSymbols.put("KS11", "KS11");
		includedSymbols.put("DAX", "DAX");
		includedSymbols.put("CAC40", "CAC40");
		includedSymbols.put("SP500", "SP500");
		includedSymbols.put("JKSE", "JKSE");
		includedSymbols.put("NZ50", "NZ50");
		includedSymbols.put("MERV", "MERV");
		// includedSymbols.put("^IBEX", "^IBEX");//?? MAP TO WHAT
		includedSymbols.put("TWII", "TWII");
		includedSymbols.put("MXX", "MXX");
		includedSymbols.put("NSEI", "NSEI");
		// includedSymbols.put("^OSEAX", "^OSEAX");//?? MAP TO WHAT
		// includedSymbols.put("^NSEI", "^NSEI");
		// includedSymbols.put("^N100", "^N100");//?? MAP TO WHAT
		// includedSymbols.put("^ISEQ", "^ISEQ");//?? MAP TO WHAT

		Hashtable allRecords = new Hashtable();
		Hashtable records = null;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			if (mode != null && mode.equalsIgnoreCase("PTMA")) {
				SQL = "SELECT SEQINDEX,SYMBOL,CURRENTDATE,ADJUSTEDPRICE,GR100,FFP,ptma,acptma FROM BPMADMIN.INTRADAYSTOCKS where (SYMBOL LIKE '^%' or SYMBOL='SPY') and seqIndex<="
						+ seqIndex + " ORDER BY SEQINDEX ASC, SYMBOL DESC";
			} else if (mode != null && mode.equalsIgnoreCase("DCP")) {
				SQL = "SELECT SEQINDEX,SYMBOL,CURRENTDATE,ADJUSTEDPRICE,GR100,FFP,dcp,acptma FROM BPMADMIN.INTRADAYSTOCKS where (SYMBOL LIKE '^%' or SYMBOL='SPY') and seqIndex<="
						+ seqIndex + " ORDER BY SEQINDEX DESC, SYMBOL DESC";

			} else {
				SQL = "SELECT SEQINDEX,SYMBOL,CURRENTDATE,ADJUSTEDPRICE,GR100,FFP,ptma,acptma FROM BPMADMIN.INTRADAYSTOCKS where (SYMBOL LIKE '^%' or SYMBOL='SPY') and seqIndex<="
						+ seqIndex + " ORDER BY SEQINDEX DESC, SYMBOL DESC";

			}

			rs1 = stmt1.executeQuery(SQL);

			int seqIndex1 = 0;
			int seqIndex2 = 0;
			String symbol = "";
			String date = "";
			float percentage = 0.0f;
			float sum = 0.0f;
			float spy = 0.0f;
			float price = 0.0f;
			int count = 0;
			Hashtable previousRecords = new Hashtable();
			Hashtable newRecords = new Hashtable();
			Hashtable tempRecords = new Hashtable();

			while (rs1.next()) {
				if (seqIndex1 == 0) {

					seqIndex1 = rs1.getInt(1);
					seqIndex = seqIndex1;
					symbol = rs1.getString(2);
					date = rs1.getString(3);
					percentage = rs1.getFloat(7);
					if (symbol.equalsIgnoreCase("^GSPC")) {
						spy = percentage;
						price = rs1.getFloat(4);
					}
					sum = sum + percentage;
					count++;

					records = new Hashtable();
					records.put(symbol, "" + percentage);
					records.put("date", date);
					if (!previousRecords.containsKey(symbol)) {
						previousRecords.put(symbol, "" + percentage);
					}
					newRecords.put(symbol, "" + percentage);

				} else
					seqIndex2 = rs1.getInt(1);

				if (seqIndex1 == seqIndex2) {
					symbol = rs1.getString(2);
					percentage = rs1.getFloat(7);
					if (symbol.equalsIgnoreCase("^GSPC")) {
						spy = percentage;
						price = rs1.getFloat(4);
					}
					sum = sum + percentage;
					count++;

					records.put(symbol, "" + percentage);
					if (!previousRecords.containsKey(symbol)) {
						previousRecords.put(symbol, "" + percentage);
					}
					newRecords.put(symbol, "" + percentage);

				} else if (seqIndex2 > 0 && seqIndex1 != seqIndex2) {
					Enumeration en = previousRecords.keys();
					while (en.hasMoreElements()) {
						String sym = en.nextElement().toString();
						float perc = Float.parseFloat(previousRecords.get(sym)
								.toString());
						if (!newRecords.containsKey(sym)) {
							sum = sum + perc;
							count++;
							tempRecords.put(sym, "" + perc);
						} else {
							perc = Float.parseFloat(newRecords.get(sym)
									.toString());
							tempRecords.put(sym, "" + perc);

						}
					}

					Enumeration en1 = newRecords.keys();
					while (en1.hasMoreElements()) {
						String sym1 = en1.nextElement().toString();
						float perc1 = Float.parseFloat(newRecords.get(sym1)
								.toString());
						if (!tempRecords.containsKey(sym1)) {

							tempRecords.put(sym1, "" + perc1);
						}
					}

					previousRecords = tempRecords;
					newRecords = new Hashtable();

					String nextline = seqIndex + " ," + date + ", sum: ," + sum
							+ ", ^GSPC: ," + spy + ",  price: ," + price
							+ ", total count " + count + ",  " + (sum / count);
					if (seqIndex > 42194)
						System.out.println(nextline);
					allRecords.put("" + seqIndex1, records);
					String sql2 = "Update BPMADMIN.INTRADAYSTOCKS set ACPTMA="
							+ (sum / count * 1.0f)
							+ " where SYMBOL='SPY' and SEQINDEX = " + seqIndex;
					stmt2.executeUpdate(sql2);
					Files.appendToFile("C:\\stock\\Book88.csv", nextline);
					symbol = rs1.getString(2);
					percentage = rs1.getFloat(7);
					sum = percentage;
					count = 0;
					if (symbol.equalsIgnoreCase("^GSPC")) {
						spy = percentage;
						price = rs1.getFloat(4);
					}

					seqIndex1 = seqIndex2;
					seqIndex = seqIndex1;
					date = rs1.getString(3);

					records = new Hashtable();
					records.put(symbol, "" + percentage);
					records.put("date", date);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	// BPMADMIN.INTRADAYSTOCKS set DS15PER=0.0 where seqindex=42869;
	public static void calculateTrendPointsHistoryForAllStocks(int seqIndex,
			int ndays) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
					+ seqIndex
					+ " AND DS15PER>-0.0000001 AND DS15PER<0.0000001";

			if (seqIndex < 0) {
				SQL = "select  SEQINDEX from BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL='SPY' ORDER by SEQINDEX DESC";

				rs1 = stmt1.executeQuery(SQL);
				rs1.next();

				seqIndex = rs1.getInt(1);

			}

			if (ndays > 0) {// then we only calculate recent ndays records
				SQL = "select  SYMBOL from BPMADMIN.INTRADAYSTOCKS WHERE SEQINDEX ="
						+ seqIndex;
			}

			rs1 = stmt1.executeQuery(SQL);

			Hashtable results = new Hashtable();

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				results.put(nextStock, nextStock);
			}

			System.out.println(" Stocks left " + results.size());
			Enumeration en = results.keys();

			int loopCount = 0;
			long t1 = System.currentTimeMillis();
			while (en.hasMoreElements()) {
				String nextStock = en.nextElement().toString();

				calculateTrendPointsHistory(nextStock, seqIndex, ndays);
				long t2 = System.currentTimeMillis();
				loopCount++;
				System.out.println("Process stock " + nextStock + " "
						+ loopCount + " stocks took " + (t2 - t1) / 1000
						+ " seconds.");

				if (loopCount % 100 == 0) {
					System.out.println(loopCount + " stocks have been done");
					Thread.sleep(8000);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void displayYieldsFittingCriteria(String symbol) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = " SELECT SEQINDEX, CURRENTDATE FROM BPMADMIN.INTRADAYSTOCKS  where  symbol='"
					+ symbol
					+ "' "
					+ " and (DS3PER>12 or DS4PER>12) AND seqindex<=44780    order by seqIndex desc; ";

			rs1 = stmt1.executeQuery(SQL);

			int days = 500;
			int[] seqIndexArray = new int[days];
			String[] dates = new String[days];
			int[] countArray = new int[days];

			float[] DS3Array = new float[days];
			float[] DS4Array = new float[days];
			float[] DS8Array = new float[days];
			float[] DS9Array = new float[days];
			float[] DS10Array = new float[days];
			float[] DS11Array = new float[days];
			float[] DS12Array = new float[days];
			float[] DS13Array = new float[days];
			float[] DS14Array = new float[days];
			float[] DS15Array = new float[days];

			float[] finalpriceArray = new float[days];
			float[] highpriceArray = new float[days];
			float[] lowpriceArray = new float[days];

			int loopCount = 0;
			int index = 0;
			while (rs1.next()) {
				if (loopCount < days) {
					seqIndexArray[loopCount] = rs1.getInt(1);
					dates[loopCount] = rs1.getString(2);

				} else {
					System.out.println("Too many records for array ");

				}
				loopCount++;
			}

			for (int k = 0; k < loopCount; k++) {

				String SQL2 = " SELECT FINALPRICE,HIGHPRICE,LOWPRICE,OPENPRICE  "
						+ " FROM BPMADMIN.INTRADAYSTOCKS where symbol='"
						+ symbol
						+ "'   and seqindex>="
						+ seqIndexArray[k]
						+ " AND seqIndex<"
						+ (seqIndexArray[k] + 60)
						+ " order by seqIndex asc";

				rs2 = stmt2.executeQuery(SQL2);
				int count = 0;
				float y1 = 0.0f;
				float y2 = 0.0f;
				float y3 = 0.0f;
				float y4 = 0.0f;
				float y5 = 0.0f;
				float y6 = 0.0f;
				float y7 = 0.0f;
				float y8 = 0.0f;
				float y9 = 0.0f;
				float y10 = 0.0f;
				float y11 = 0.0f;
				float y12 = 0.0f;
				float y13 = 0.0f;
				float y14 = 0.0f;
				float y15 = 0.0f;
				float start = 0.0f;
				while (rs2.next() && count < 16) {
					if (count == 0) {
						start = rs2.getFloat(1);
					}
					float end = rs2.getFloat(1);

					if (count == 1) {
						y1 = 100.0f * (end - start) / start;
					} else if (count == 2) {
						y2 = 100.0f * (end - start) / start;
					} else if (count == 3) {
						y3 = 100.0f * (end - start) / start;
					} else if (count == 4) {
						y4 = 100.0f * (end - start) / start;
					} else if (count == 5) {
						y5 = 100.0f * (end - start) / start;
					} else if (count == 6) {
						y6 = 100.0f * (end - start) / start;
					} else if (count == 7) {
						y7 = 100.0f * (end - start) / start;
					} else if (count == 8) {
						y8 = 100.0f * (end - start) / start;
					} else if (count == 9) {
						y9 = 100.0f * (end - start) / start;
					} else if (count == 10) {
						y10 = 100.0f * (end - start) / start;
					} else if (count == 11) {
						y11 = 100.0f * (end - start) / start;
					} else if (count == 12) {
						y12 = 100.0f * (end - start) / start;
					} else if (count == 13) {
						y13 = 100.0f * (end - start) / start;
					} else if (count == 14) {
						y14 = 100.0f * (end - start) / start;
					} else if (count == 15) {
						y15 = 100.0f * (end - start) / start;
					}

					count++;
				}

				String nextline = seqIndexArray[k] + " , date: " + dates[k]
						+ ", y1: " + y1 + ", y2: " + y2 + ", y3: " + y3
						+ ", y4: " + y4 + ",y5: " + y5 + ", y6: " + y6
						+ ",y7: " + y7 + ", y8:" + y8 + ", y9:" + y9 + ", y10:"
						+ y10 + ", y11: " + y11 + ", y12:" + y12 + ", y13:"
						+ y13 + ", y14:" + y14 + ", y15:" + y15;
				Files.appendToFile("C:\\stock\\" + symbol + "Yields.csv",
						nextline);

			}
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	public static void displayMulQueryInSingleRow(String symbol1, String symbol2) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = " SELECT SEQINDEX,CURRENTDATE,finalprice,DSPERCENT,DS3PER,DS4PER,DS8PER,DS9PER,DS10PER,DS11PER,DS12PER,DS13PER,DS14PER,DS15PER FROM BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL = '"
					+ symbol1 + "' ORDER BY SEQINDEX DESC;";

			rs1 = stmt1.executeQuery(SQL);

			int days = 20;
			int[] seqIndexArray = new int[days];
			String[] dates = new String[days];
			int[] countArray = new int[days];

			float[] DSArray1 = new float[days];
			float[] DS3Array1 = new float[days];
			float[] DS4Array1 = new float[days];
			float[] DS8Array1 = new float[days];
			float[] DS9Array1 = new float[days];
			float[] DS10Array1 = new float[days];
			float[] DS11Array1 = new float[days];
			float[] DS12Array1 = new float[days];
			float[] DS13Array1 = new float[days];
			float[] DS14Array1 = new float[days];
			float[] DS15Array1 = new float[days];

			float[] DSArray2 = new float[days];
			float[] DS3Array2 = new float[days];
			float[] DS4Array2 = new float[days];
			float[] DS8Array2 = new float[days];
			float[] DS9Array2 = new float[days];
			float[] DS10Array2 = new float[days];
			float[] DS11Array2 = new float[days];
			float[] DS12Array2 = new float[days];
			float[] DS13Array2 = new float[days];
			float[] DS14Array2 = new float[days];
			float[] DS15Array2 = new float[days];

			float[] finalpriceArray1 = new float[days];
			float[] finalpriceArray2 = new float[days];
			float[] highpriceArray = new float[days];
			float[] lowpriceArray = new float[days];

			int loopCount = 0;
			int index = 0;
			while (rs1.next()) {
				if (loopCount < days) {
					seqIndexArray[loopCount] = rs1.getInt(1);
					dates[loopCount] = rs1.getString(2);
					finalpriceArray1[loopCount] = rs1.getFloat(3);
					DSArray1[loopCount] = rs1.getFloat(4);
					DS3Array1[loopCount] = rs1.getFloat(5);
					DS4Array1[loopCount] = rs1.getFloat(6);
					DS8Array1[loopCount] = rs1.getFloat(7);
					DS9Array1[loopCount] = rs1.getFloat(8);
					DS10Array1[loopCount] = rs1.getFloat(9);
					DS11Array1[loopCount] = rs1.getFloat(10);
					DS12Array1[loopCount] = rs1.getFloat(11);
					DS13Array1[loopCount] = rs1.getFloat(12);
					DS14Array1[loopCount] = rs1.getFloat(13);
					DS15Array1[loopCount] = rs1.getFloat(14);

				} else {

					String SQL2 = " SELECT SEQINDEX,CURRENTDATE,finalprice,DSPERCENT,DS3PER,DS4PER,DS8PER,DS9PER,DS10PER,DS11PER,DS12PER,DS13PER,DS14PER,DS15PER FROM BPMADMIN.INTRADAYSTOCKS WHERE SYMBOL = '"
							+ symbol2 + "'   and seqindex=" + seqIndexArray[0];

					rs2 = stmt2.executeQuery(SQL2);
					if (rs2.next()) {

						String nextline = seqIndexArray[0] + " , date: "
								+ dates[0] + ", DS: " + DSArray1[0]
								+ ", SPY DS: " + rs2.getFloat(4) + ", DS3: "
								+ DS3Array1[0] + ", SPY DS3:  "
								+ rs2.getFloat(5) + ",price: "
								+ finalpriceArray1[0] + ", SPY Price: "
								+ rs2.getFloat(3) + ",DS4: " + DS4Array1[0]
								+ ", SPY DS4: " + rs2.getFloat(6) + ",DS10: "
								+ DS10Array1[0] + ", SPY DS10: "
								+ rs2.getFloat(9) + ",DS15: " + DS15Array1[0]
								+ ", SPY DS15: " + rs2.getFloat(14);
						Files.appendToFile("C:\\stock\\" + symbol1 + symbol2
								+ "MuQuery.csv", nextline);
					}

					for (int w = 0; w < days - 1; w++) {
						seqIndexArray[w] = seqIndexArray[w + 1];
						dates[w] = dates[w + 1];
						finalpriceArray1[w] = finalpriceArray1[w + 1];
						DSArray1[w] = DSArray1[w + 1];
						DS3Array1[w] = DS3Array1[w + 1];
						DS4Array1[w] = DS4Array1[w + 1];
						DS8Array1[w] = DS8Array1[w + 1];
						DS9Array1[w] = DS9Array1[w + 1];
						DS10Array1[w] = DS10Array1[w + 1];
						DS11Array1[w] = DS11Array1[w + 1];
						DS12Array1[w] = DS12Array1[w + 1];
						DS13Array1[w] = DS13Array1[w + 1];
						DS14Array1[w] = DS14Array1[w + 1];
						DS15Array1[w] = DS15Array1[w + 1];
					}

					seqIndexArray[days - 1] = rs1.getInt(1);
					dates[days - 1] = rs1.getString(2);
					finalpriceArray1[days - 1] = rs1.getFloat(3);
					DSArray1[days - 1] = rs1.getFloat(4);
					DS3Array1[days - 1] = rs1.getFloat(5);
					DS4Array1[days - 1] = rs1.getFloat(6);
					DS8Array1[days - 1] = rs1.getFloat(7);
					DS9Array1[days - 1] = rs1.getFloat(8);
					DS10Array1[days - 1] = rs1.getFloat(9);
					DS11Array1[days - 1] = rs1.getFloat(10);
					DS12Array1[days - 1] = rs1.getFloat(11);
					DS13Array1[days - 1] = rs1.getFloat(12);
					DS14Array1[days - 1] = rs1.getFloat(13);
					DS15Array1[days - 1] = rs1.getFloat(14);

				}
				loopCount++;

			}

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	public static void displayMulQueryInSingleRow(String symbol) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = " SELECT SEQINDEX,CURRENTDATE,SUM(DS3PER),SUM(DS4PER),SUM(DS8PER),SUM(DS9PER),SUM(DS10PER),SUM(DS11PER),SUM(DS12PER),SUM(DS13PER),SUM(DS14PER),SUM(DS15PER) FROM BPMADMIN.INTRADAYSTOCKS  GROUP BY SEQINDEX,CURRENTDATE ORDER BY SEQINDEX DESC;";

			rs1 = stmt1.executeQuery(SQL);

			int days = 20;
			int[] seqIndexArray = new int[days];
			String[] dates = new String[days];
			int[] countArray = new int[days];

			float[] DS3Array = new float[days];
			float[] DS4Array = new float[days];
			float[] DS8Array = new float[days];
			float[] DS9Array = new float[days];
			float[] DS10Array = new float[days];
			float[] DS11Array = new float[days];
			float[] DS12Array = new float[days];
			float[] DS13Array = new float[days];
			float[] DS14Array = new float[days];
			float[] DS15Array = new float[days];

			float[] finalpriceArray = new float[days];
			float[] highpriceArray = new float[days];
			float[] lowpriceArray = new float[days];

			int loopCount = 0;
			int index = 0;
			while (rs1.next()) {
				if (loopCount < days) {
					seqIndexArray[loopCount] = rs1.getInt(1);
					dates[loopCount] = rs1.getString(2);
					DS3Array[loopCount] = rs1.getFloat(3);
					DS4Array[loopCount] = rs1.getFloat(4);
					DS8Array[loopCount] = rs1.getFloat(5);
					DS9Array[loopCount] = rs1.getFloat(6);
					DS10Array[loopCount] = rs1.getFloat(7);
					DS11Array[loopCount] = rs1.getFloat(8);
					DS12Array[loopCount] = rs1.getFloat(9);
					DS13Array[loopCount] = rs1.getFloat(10);
					DS14Array[loopCount] = rs1.getFloat(11);
					DS15Array[loopCount] = rs1.getFloat(12);

				} else {

					String SQL2 = " SELECT DSI5,DSI,DSI3,PTMA,FINALPRICE,HIGHPRICE,LOWPRICE,OPENPRICE,DCPERCENT,DSPERCENT  "
							+ " FROM BPMADMIN.INTRADAYSTOCKS where symbol='SPY'  and seqindex="
							+ seqIndexArray[0];

					rs2 = stmt2.executeQuery(SQL2);
					if (rs2.next()) {

						String nextline = seqIndexArray[0] + " , date: "
								+ dates[0] + ", DS3SUM: " + DS3Array[0]
								+ ", DS4SUM: " + DS4Array[0] + ", DS8SUM: "
								+ DS8Array[0] + ", DS15SUM: " + DS15Array[0]
								+ ",price: " + rs2.getFloat(5) + ", change%: "
								+ rs2.getFloat(9) + ",allChanges%: "
								+ rs2.getFloat(10) + ", DSI5:"
								+ rs2.getFloat(1) + ", DSI:" + rs2.getFloat(2)
								+ ", DSI3:" + rs2.getFloat(3) + ", PTMA: "
								+ rs2.getFloat(4);
						Files.appendToFile("C:\\stock\\" + symbol
								+ "MuQuery.csv", nextline);
					}

					for (int w = 0; w < days - 1; w++) {
						seqIndexArray[w] = seqIndexArray[w + 1];
						dates[w] = dates[w + 1];
						DS3Array[w] = DS3Array[w + 1];
						DS4Array[w] = DS4Array[w + 1];
						DS8Array[w] = DS8Array[w + 1];
						DS9Array[w] = DS9Array[w + 1];
						DS10Array[w] = DS10Array[w + 1];
						DS11Array[w] = DS11Array[w + 1];
						DS12Array[w] = DS12Array[w + 1];
						DS13Array[w] = DS13Array[w + 1];
						DS14Array[w] = DS14Array[w + 1];
						DS15Array[w] = DS15Array[w + 1];
					}

					seqIndexArray[days - 1] = rs1.getInt(1);
					dates[days - 1] = rs1.getString(2);
					DS3Array[days - 1] = rs1.getFloat(3);
					DS4Array[days - 1] = rs1.getFloat(4);
					DS8Array[days - 1] = rs1.getFloat(5);
					DS9Array[days - 1] = rs1.getFloat(6);
					DS10Array[days - 1] = rs1.getFloat(7);
					DS11Array[days - 1] = rs1.getFloat(8);
					DS12Array[days - 1] = rs1.getFloat(9);
					DS13Array[days - 1] = rs1.getFloat(10);
					DS14Array[days - 1] = rs1.getFloat(11);
					DS15Array[days - 1] = rs1.getFloat(12);

				}
				loopCount++;

			}

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	public static void calculateTrendPointsHistory(String symbol,
			long maxPreIndex, int ndays) {
		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			// if (ndays < 0) {
			// SQL =
			// "Update BPMADMIN.INTRADAYSTOCKS where SET DCPERCENT=0.0,DSPERCENT=0.0,DS3PER=0.0,DS4PER=0.0,DS8PER=0.0,DS9PER=0.0,DS10PER=0.0,DS11PER=0.0,DS12PER=0.0,DS13PER=0.0,DS14PER=0.0,DS15PER=0.0 WHERE  Symbol='"
			// + symbol + "'";

			// stmt2.executeUpdate(SQL);
			// }

			int days = 20;
			SQL = "SELECT SEQINDEX,CURRENTDATE,OPENPRICE,FINALPRICE,HIGHPRICE,LOWPRICE, DSI5, DSI "
					+ " FROM BPMADMIN.INTRADAYSTOCKS where   Symbol='"
					+ symbol
					+ "'   AND SEQINDEX>"
					+ (maxPreIndex - days)
					+ " ORDER BY SEQINDEX ASC";

			// backCountNeeded/10 should be greater than days=20
			if (ndays > 0) {
				SQL = "SELECT SEQINDEX,CURRENTDATE,OPENPRICE,FINALPRICE,HIGHPRICE,LOWPRICE, DSI5, DSI "
						+ " FROM BPMADMIN.INTRADAYSTOCKS where   Symbol='"
						+ symbol
						+ "'   and SEQINDEX >"
						+ (maxPreIndex - 7 * ndays) + " ORDER BY SEQINDEX ASC";

			}

			rs1 = stmt1.executeQuery(SQL);

			int[] seqIndexArray = new int[days];
			String[] dates = new String[days];
			float[] openpriceArray = new float[days];
			float[] finalpriceArray = new float[days];
			float[] highpriceArray = new float[days];
			float[] lowpriceArray = new float[days];
			float[] dsi5Array = new float[days];
			float[] dsiArray = new float[days];
			float[] changeP = new float[days];
			float[] changeSum = new float[days];

			int loopCount = 0;
			int index = 0;
			while (rs1.next()) {
				if (loopCount < days) {
					seqIndexArray[loopCount] = rs1.getInt(1);
					dates[loopCount] = rs1.getString(2);
					openpriceArray[loopCount] = rs1.getFloat(3);
					finalpriceArray[loopCount] = rs1.getFloat(4);
					highpriceArray[loopCount] = rs1.getFloat(5);
					lowpriceArray[loopCount] = rs1.getFloat(6);
					dsi5Array[loopCount] = rs1.getFloat(7);
					dsiArray[loopCount] = rs1.getFloat(8);

				} else {
					for (int w = 0; w < days - 1; w++) {
						int seq1 = seqIndexArray[w];
						float open1 = openpriceArray[w];
						float final1 = finalpriceArray[w];
						float high1 = highpriceArray[w];
						float low1 = lowpriceArray[w];

						int seq2 = seqIndexArray[(w + 1) % days];
						String date2 = dates[(w + 1) % days];
						float open2 = openpriceArray[(w + 1) % days];
						float final2 = finalpriceArray[(w + 1) % days];
						float high2 = highpriceArray[(w + 1) % days];
						float low2 = lowpriceArray[(w + 1) % days];
						index = (w + 1) % days;

						float changePer = 100.0f * (final2 - final1) / final1;
						float changeO = 100.0f * (open2 - open1) / open1;
						float changeH = 100.0f * (high2 - high1) / high1;
						float changeL = 100.0f * (low2 - low1) / low1;
						float changeSumPer = changePer + changeO + changeH
								+ changeL;
						changeP[(w + 1) % days] = changePer;
						changeSum[(w + 1) % days] = changeSumPer;
					}

					int lastSeq = seqIndexArray[days - 1];
					String lastDate = dates[days - 1];
					float fprice = finalpriceArray[days - 1];
					float lchange = changeP[days - 1];
					float lsum = changeSum[days - 1];
					float sum3 = changeSum[days - 1] + changeSum[days - 2]
							+ changeSum[days - 3];
					float sum4 = sum3 + changeSum[days - 4];
					float sum8 = sum4 + changeSum[days - 5]
							+ changeSum[days - 6] + changeSum[days - 7]
							+ changeSum[days - 8];
					float sum9 = sum8 + changeSum[days - 9];
					float sum10 = sum9 + changeSum[days - 10];
					float sum11 = sum10 + changeSum[days - 11];
					float sum12 = sum11 + changeSum[days - 12];
					float sum13 = sum12 + changeSum[days - 13];
					float sum14 = sum13 + changeSum[days - 14];
					float sum15 = sum14 + changeSum[days - 15];
					/*
					 * String nextline = lastSeq + " , date: ," + lastDate +
					 * ",price: " + fprice + ", change%," + lchange +
					 * ",allChanges%:," + lsum + ", s3:" + sum3 + ", s4:" + sum4
					 * + ", s8:" + sum8 + ", s9:" + sum9 + ", s10:" + sum10 +
					 * ", s11:" + sum11 + ", s12:" + sum12 + ", s13:" + sum13 +
					 * ", s14:" + sum14 + ", s15:" + sum15;
					 * Files.appendToFile("C:\\stock\\" + symbol +
					 * "TrendPoints.csv", nextline);
					 */
					SQL = "Update BPMADMIN.INTRADAYSTOCKS where SET DCPERCENT="
							+ lchange + ",DSPERCENT=" + lsum + ",DS3PER="
							+ sum3 + ",DS4PER=" + sum4 + ",DS8PER=" + sum8
							+ ",DS9PER=" + sum9 + ", DS10PER=" + sum10
							+ ",DS11PER=" + sum11 + ",DS12PER=" + sum12
							+ ",DS13PER=" + sum13 + ",DS14PER=" + sum14
							+ ",DS15PER=" + sum15 + " WHERE  Symbol='" + symbol
							+ "' and SEQINDEX = " + lastSeq;

					if (lastSeq > maxPreIndex)
						stmt2.executeUpdate(SQL);

					for (int w = 0; w < days - 1; w++) {
						seqIndexArray[w] = seqIndexArray[w + 1];
						openpriceArray[w] = openpriceArray[w + 1];
						finalpriceArray[w] = finalpriceArray[w + 1];
						highpriceArray[w] = highpriceArray[w + 1];
						lowpriceArray[w] = lowpriceArray[w + 1];
						changeP[w] = changeP[w + 1];
						changeSum[w] = changeSum[w + 1];
					}

					seqIndexArray[days - 1] = rs1.getInt(1);
					dates[days - 1] = rs1.getString(2);
					openpriceArray[days - 1] = rs1.getFloat(3);
					finalpriceArray[days - 1] = rs1.getFloat(4);
					highpriceArray[days - 1] = rs1.getFloat(5);
					lowpriceArray[days - 1] = rs1.getFloat(6);
					dsi5Array[days - 1] = rs1.getFloat(7);
					dsiArray[days - 1] = rs1.getFloat(8);
				}
				loopCount++;

			}

			int seq1 = seqIndexArray[days - 2];
			float open1 = openpriceArray[days - 2];
			float final1 = finalpriceArray[days - 2];
			float high1 = highpriceArray[days - 2];
			float low1 = lowpriceArray[days - 2];

			int seq2 = seqIndexArray[days - 1];
			String date2 = dates[days - 1];
			float open2 = openpriceArray[days - 1];
			float final2 = finalpriceArray[days - 1];
			float high2 = highpriceArray[days - 1];
			float low2 = lowpriceArray[days - 1];

			float changePer = 100.0f * (final2 - final1) / final1;
			float changeO = 100.0f * (open2 - open1) / open1;
			float changeH = 100.0f * (high2 - high1) / high1;
			float changeL = 100.0f * (low2 - low1) / low1;
			float changeSumPer = changePer + changeO + changeH + changeL;
			changeP[days - 1] = changePer;
			changeSum[days - 1] = changeSumPer;

			int lastSeq = seqIndexArray[days - 1];
			String lastDate = dates[days - 1];
			float fprice = finalpriceArray[days - 1];
			float lchange = changeP[days - 1];
			float lsum = changeSum[days - 1];
			float sum3 = changeSum[days - 1] + changeSum[days - 2]
					+ changeSum[days - 3];
			float sum4 = sum3 + changeSum[days - 4];
			float sum8 = sum4 + changeSum[days - 5] + changeSum[days - 6]
					+ changeSum[days - 7] + changeSum[days - 8];
			float sum9 = sum8 + changeSum[days - 9];
			float sum10 = sum9 + changeSum[days - 10];
			float sum11 = sum10 + changeSum[days - 11];
			float sum12 = sum11 + changeSum[days - 12];
			float sum13 = sum12 + changeSum[days - 13];
			float sum14 = sum13 + changeSum[days - 14];
			float sum15 = sum14 + changeSum[days - 15];

			SQL = "Update BPMADMIN.INTRADAYSTOCKS where SET DCPERCENT="
					+ lchange + ",DSPERCENT=" + lsum + ",DS3PER=" + sum3
					+ ",DS4PER=" + sum4 + ",DS8PER=" + sum8 + ",DS9PER=" + sum9
					+ ", DS10PER=" + sum10 + ",DS11PER=" + sum11 + ",DS12PER="
					+ sum12 + ",DS13PER=" + sum13 + ",DS14PER=" + sum14
					+ ",DS15PER=" + sum15 + " WHERE  Symbol='" + symbol
					+ "' and SEQINDEX = " + lastSeq;

			if (lastSeq > maxPreIndex)
				stmt2.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	public static void discoverTrendPoints(String symbol) {

		Hashtable allRecords = new Hashtable();
		Hashtable records = null;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "SELECT SEQINDEX,CURRENTDATE,OPENPRICE,FINALPRICE,HIGHPRICE,LOWPRICE, DSI5, DSI "
					+ " FROM BPMADMIN.INTRADAYSTOCKS where   Symbol='"
					+ symbol
					+ "'   ORDER BY SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);

			int days = 20;
			int[] seqIndexArray = new int[days];
			String[] dates = new String[days];
			float[] openpriceArray = new float[days];
			float[] finalpriceArray = new float[days];
			float[] highpriceArray = new float[days];
			float[] lowpriceArray = new float[days];
			float[] dsi5Array = new float[days];
			float[] dsiArray = new float[days];
			float[] changeP = new float[days];
			float[] changeSum = new float[days];

			int loopCount = 0;
			int index = 0;
			while (rs1.next()) {
				if (loopCount < days) {
					seqIndexArray[loopCount] = rs1.getInt(1);
					dates[loopCount] = rs1.getString(2);
					openpriceArray[loopCount] = rs1.getFloat(3);
					finalpriceArray[loopCount] = rs1.getFloat(4);
					highpriceArray[loopCount] = rs1.getFloat(5);
					lowpriceArray[loopCount] = rs1.getFloat(6);
					dsi5Array[loopCount] = rs1.getFloat(7);
					dsiArray[loopCount] = rs1.getFloat(8);

				} else {
					for (int w = 0; w < days - 1; w++) {
						int seq1 = seqIndexArray[w];
						float open1 = openpriceArray[w];
						float final1 = finalpriceArray[w];
						float high1 = highpriceArray[w];
						float low1 = lowpriceArray[w];

						int seq2 = seqIndexArray[(w + 1) % days];
						String date2 = dates[(w + 1) % days];
						float open2 = openpriceArray[(w + 1) % days];
						float final2 = finalpriceArray[(w + 1) % days];
						float high2 = highpriceArray[(w + 1) % days];
						float low2 = lowpriceArray[(w + 1) % days];
						index = (w + 1) % days;

						float changePer = 100.0f * (final2 - final1) / final1;
						float changeO = 100.0f * (open2 - open1) / open1;
						float changeH = 100.0f * (high2 - high1) / high1;
						float changeL = 100.0f * (low2 - low1) / low1;
						float changeSumPer = changePer + changeO + changeH
								+ changeL;
						changeP[(w + 1) % days] = changePer;
						changeSum[(w + 1) % days] = changeSumPer;
					}

					int lastSeq = seqIndexArray[days - 1];
					String lastDate = dates[days - 1];
					float fprice = finalpriceArray[days - 1];
					float lchange = changeP[days - 1];
					float lsum = changeSum[days - 1];
					float sum3 = changeSum[days - 1] + changeSum[days - 2]
							+ changeSum[days - 3];
					float sum4 = sum3 + changeSum[days - 4];
					float sum8 = sum4 + changeSum[days - 5]
							+ changeSum[days - 6] + changeSum[days - 7]
							+ changeSum[days - 8];
					float sum9 = sum8 + changeSum[days - 9];
					float sum10 = sum9 + changeSum[days - 10];
					float sum11 = sum10 + changeSum[days - 11];
					float sum12 = sum11 + changeSum[days - 12];
					float sum13 = sum12 + changeSum[days - 13];
					float sum14 = sum13 + changeSum[days - 14];
					float sum15 = sum14 + changeSum[days - 15];

					String nextline = lastSeq + " , date: ," + lastDate
							+ ",price: " + fprice + ", change%," + lchange
							+ ",allChanges%:," + lsum + ", s3:" + sum3
							+ ", s4:" + sum4 + ", s8:" + sum8 + ", s9:" + sum9
							+ ", s10:" + sum10 + ", s11:" + sum11 + ", s12:"
							+ sum12 + ", s13:" + sum13 + ", s14:" + sum14
							+ ", s15:" + sum15;
					Files.appendToFile("C:\\stock\\" + symbol
							+ "TrendPoints.csv", nextline);

					for (int w = 0; w < days - 1; w++) {
						seqIndexArray[w] = seqIndexArray[w + 1];
						openpriceArray[w] = openpriceArray[w + 1];
						finalpriceArray[w] = finalpriceArray[w + 1];
						highpriceArray[w] = highpriceArray[w + 1];
						lowpriceArray[w] = lowpriceArray[w + 1];
						changeP[w] = changeP[w + 1];
						changeSum[w] = changeSum[w + 1];
					}

					seqIndexArray[days - 1] = rs1.getInt(1);
					dates[days - 1] = rs1.getString(2);
					openpriceArray[days - 1] = rs1.getFloat(3);
					finalpriceArray[days - 1] = rs1.getFloat(4);
					highpriceArray[days - 1] = rs1.getFloat(5);
					lowpriceArray[days - 1] = rs1.getFloat(6);
					dsi5Array[days - 1] = rs1.getFloat(7);
					dsiArray[days - 1] = rs1.getFloat(8);
				}
				loopCount++;

			}

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	public static void evaluateBuyPoint(String symbol) {

		Hashtable allRecords = new Hashtable();
		Hashtable records = null;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "SELECT SEQINDEX,CURRENTDATE,DSI5,DSI,FINALPRICE,HIGHPRICE,LOWPRICE,OPENPRICE "
					+ " FROM BPMADMIN.INTRADAYSTOCKS where   Symbol='"
					+ symbol
					+ "' AND DSI<>0 AND DSI5<>0  ORDER BY SEQINDEX ASC";

			rs1 = stmt1.executeQuery(SQL);

			int seqIndex1 = 0;
			String date = "";
			float dsi5 = 0.0f;
			float dsi = 0.0f;
			float finalprice = 0.0f;
			float highprice = 0.0f;
			float lowprice = 0.0f;
			float openprice = 0.0f;
			boolean dsi5Neg = false;
			boolean dsiNeg = false;
			int purchaseIndex = 0;
			float highyield = 0.0f;
			float lowyield = 0.0f;
			float closeyield = 0.0f;
			float openyield = 0.0f;
			int dsi5Count = 0;
			int dsiCount = 0;
			float dsiBullLine = 100.0f;
			boolean nextStart = false;
			boolean newCycle = false;
			int totalCount = 0;
			int[] posArray = new int[100];
			for (int k = 0; k < 100; k++) {
				posArray[k] = 0;
			}

			int[] GT4Array = new int[100];
			for (int k = 0; k < 100; k++) {
				GT4Array[k] = 0;
			}

			int[] DM2Array = new int[100];
			for (int k = 0; k < 100; k++) {
				DM2Array[k] = 0;
			}

			while (rs1.next()) {

				seqIndex1 = rs1.getInt(1);
				date = rs1.getString(2);
				dsi5 = rs1.getFloat(3);
				dsi = rs1.getFloat(4);
				finalprice = rs1.getFloat(5);
				highprice = rs1.getFloat(6);
				lowprice = rs1.getFloat(7);
				openprice = rs1.getFloat(8);

				if (dsi5Neg) {
					dsi5Count = dsi5Count + 1;
				}

				if (dsi5 < 0 && dsi5Neg) {
					dsi5Count = 0;
				} else if (dsi5 < 0 && !dsi5Neg) {
					dsi5Neg = true;
				}

				if (dsiNeg) {
					dsiCount = dsiCount + 1;
				}

				if (dsi < 0 && dsiNeg) {
					dsiCount = 0;
				} else if (dsi < 0 && !dsiNeg) {
					dsiNeg = true;
				}

				if (nextStart) {
					System.out.println("Processing at " + seqIndex1
							+ " for symbol " + symbol);
					purchaseIndex = seqIndex1;

					String Sql2 = " SELECT SEQINDEX,CURRENTDATE,FINALPRICE,HIGHPRICE,LOWPRICE,OPENPRICE,PTMA,PTMAV "
							+ " FROM BPMADMIN.INTRADAYSTOCKS where symbol='"
							+ symbol
							+ "'  and seqindex>="
							+ purchaseIndex
							+ " order by seqindex asc";

					rs2 = stmt2.executeQuery(Sql2);
					int loopCount = 0;
					int indexStart = 0;
					String dateStart = "";
					float priceStart = 0.0f;
					float highStart = 0.0f;
					float lowStart = 0.0f;
					float openStart = 0.0f;
					float ptmaStart = 0.0f;
					float ptmavStart = 0.0f;
					int countDays = 100;
					while (rs2.next() && loopCount < countDays) {
						if (loopCount == 0) {
							totalCount++;
							indexStart = rs2.getInt(1);
							dateStart = rs2.getString(2);
							priceStart = rs2.getFloat(3);
							highStart = rs2.getFloat(4);
							lowStart = rs2.getFloat(5);
							openStart = rs2.getFloat(6);
							ptmaStart = rs2.getFloat(7);
							ptmavStart = rs2.getFloat(8);
							String nextline = indexStart + " , dateStart: ,"
									+ dateStart + ",   priceStart,"
									+ priceStart + ",highStart:," + highStart
									+ ", lowStart:," + lowStart + ",openStart,"
									+ openStart + ",ptma," + ptmaStart
									+ ",ptmav," + ptmavStart;
							Files.appendToFile("C:\\stock\\SPYEval.csv",
									nextline);
						} else {
							int index = rs2.getInt(1);
							String dateN = rs2.getString(2);
							float price = rs2.getFloat(3);
							float high = rs2.getFloat(4);
							float low = rs2.getFloat(5);
							float open = rs2.getFloat(6);
							float ptma = rs2.getFloat(7);
							float ptmav = rs2.getFloat(8);
							if (price > priceStart) {
								posArray[loopCount] = posArray[loopCount] + 1;
							}
							if (price > 1.11f * priceStart) {
								GT4Array[loopCount] = GT4Array[loopCount] + 1;
							}
							if (1.02 * price < priceStart) {
								DM2Array[loopCount] = DM2Array[loopCount] + 1;
							}
							String nextline = index + " , date: ," + dateN
									+ ",   priceChange," + 100.0f
									* (price - priceStart) / priceStart
									+ ",highChange:," + 100.0f
									* (high - priceStart) / priceStart
									+ ", lowChange:," + 100.0f
									* (low - priceStart) / priceStart
									+ ",openChange," + 100.0f
									* (open - priceStart) / priceStart
									+ ",ptma," + ptma + ",ptmav," + ptmav;
							Files.appendToFile("C:\\stock\\SPYEval.csv",
									nextline);
						}
						loopCount++;
					}

					// reset values
					seqIndex1 = 0;
					date = "";
					dsi5 = 0.0f;
					dsi = 0.0f;
					finalprice = 0.0f;
					highprice = 0.0f;
					lowprice = 0.0f;
					openprice = 0.0f;
					dsi5Neg = false;
					dsiNeg = false;
					purchaseIndex = 0;
					highyield = 0.0f;
					lowyield = 0.0f;
					closeyield = 0.0f;
					openyield = 0.0f;
					dsi5 = 0;
					dsiCount = 0;
					// dsiBullLine = 100.0f;
					nextStart = false;
					newCycle = false;

					// reset values
				}
				// could do same day trading,for now use next day
				if ((dsiNeg && dsi5Neg) && dsi > dsiBullLine && !nextStart) {
					nextStart = true;
				}

			}

			System.out.println("Total count " + totalCount);
			for (int w = 0; w < 100; w++) {
				System.out.println("Day " + (w + 1) + " postive count "
						+ posArray[w]);
			}
			for (int w = 0; w < 100; w++) {
				System.out.println("Day " + (w + 1)
						+ " greater than 11% count " + GT4Array[w]);
			}
			for (int w = 0; w < 100; w++) {
				System.out.println("Day " + (w + 1) + " drop than 2% count "
						+ DM2Array[w]);
			}

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	public static void getSPYManyIndicators(int seqIndex, String mode) {

		Hashtable allRecords = new Hashtable();
		Hashtable records = null;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			if (mode != null && mode.equalsIgnoreCase("PTMA")) {
				SQL = "SELECT SEQINDEX,CURRENTDATE, count(*),SUM(UPTRENDSTART),SUM(DELTASUM),SUM(GX100)/SUM(UPTRENDSTART)"
						+ " FROM BPMADMIN.INTRADAYSTOCKS where  UPTRENDSTART>0 AND DELTASUM>0 AND SEQINDEX<="
						+ seqIndex
						+ " GROUP BY SEQINDEX,CURRENTDATE ORDER BY SEQINDEX DESC";
			} else if (mode != null && mode.equalsIgnoreCase("DCP")) {
				SQL = "SELECT SEQINDEX,CURRENTDATE, count(*),SUM(UPTRENDSTART),SUM(DELTASUM),SUM(GX100)"
						+ " FROM BPMADMIN.INTRADAYSTOCKS where   SEQINDEX<="
						+ seqIndex
						+ " GROUP BY SEQINDEX,CURRENTDATE ORDER BY SEQINDEX DESC";

			} else {
				SQL = "SELECT SEQINDEX,CURRENTDATE, count(*),SUM(UPTRENDSTART),SUM(DELTASUM),SUM(GX100)"
						+ " FROM BPMADMIN.INTRADAYSTOCKS where   SEQINDEX<="
						+ seqIndex
						+ " GROUP BY SEQINDEX,CURRENTDATE ORDER BY SEQINDEX DESC";

			}

			rs1 = stmt1.executeQuery(SQL);

			int seqIndex1 = 0;
			String date = "";
			int totalCount = 0;
			int upPriceCount = 0;
			int upVolumeCount = 0;
			int GX100Sum = 0;

			while (rs1.next()) {

				seqIndex1 = rs1.getInt(1);
				date = rs1.getString(2);
				totalCount = rs1.getInt(3);
				upPriceCount = rs1.getInt(4);
				upVolumeCount = rs1.getInt(5);
				GX100Sum = rs1.getInt(6);

				String Sql2 = " SELECT DSI5,DSI,DSI3 AS DSISUM,FINALPRICE,HIGHPRICE,LOWPRICE,PTMA,PTMAV "
						+ " FROM BPMADMIN.INTRADAYSTOCKS where symbol='SPY'  and seqindex="
						+ seqIndex1;

				rs2 = stmt2.executeQuery(Sql2);
				if (rs2.next()) {
					float dsi5 = rs2.getFloat(1);
					float dsi24 = rs2.getFloat(2);
					float dsisum = rs2.getFloat(3);
					float finalPrice = rs2.getFloat(4);
					float highPrice = rs2.getFloat(5);
					float lowPrice = rs2.getFloat(6);
					float ptma = rs2.getFloat(7);
					float ptmav = rs2.getFloat(8);

					String nextline = seqIndex1 + " ," + date + ", count: ,"
							+ totalCount + ", upPriceCount: ," + upPriceCount
							+ ",  upVolumeCount: ," + upVolumeCount
							+ ", GX100Sum:, " + GX100Sum + ",  finalPrice,"
							+ finalPrice + ",dsi5:," + dsi5 + ", dsi24:,"
							+ dsi24 + ",dsisum," + dsisum + ",ptma," + ptma
							+ ",ptmav," + ptmav;
					nextline = seqIndex1 + " ," + date + ", count: ,"
							+ totalCount + ",   finalPrice," + finalPrice
							+ ",dsi5:," + dsi5 + ", dsi24:," + dsi24
							+ ",dsisum," + dsisum + ",ptma," + ptma + ",ptmav,"
							+ ptmav;
					nextline = seqIndex1 + " ," + date + ", count: ,"
							+ totalCount + ",dsi5:," + dsi5 + ", dsi24:,"
							+ dsi24 + ",   finalPrice," + finalPrice + ","
							+ ",dsisum," + dsisum + ",ptma," + ptma + ",ptmav,"
							+ ptmav;
					// allRecords.put("" + seqIndex1, records);

					Files.appendToFile("C:\\stock\\SPYIndicators.csv", nextline);
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	public static void getIndexStatusOld(int seqIndex, String mode) {
		Hashtable indexes = IndexHistory.initIndexTable();
		// indexes.put("SPY", "SPY");
		Hashtable excludedSymbols = new Hashtable();
		// excludedSymbols.put("^MERV", "^MERV");
		// excludedSymbols.put("^VIX", "^VIX");
		// excludedSymbols.put("^XOI", "^XOI");
		// excludedSymbols.put("^XAU", "^XAU");
		boolean useExcluded = true;

		Hashtable includedSymbols = new Hashtable();
		includedSymbols.put("^AORD", "^AORD");
		includedSymbols.put("^OMX", "^OMX");
		includedSymbols.put("^HSI", "^HSI");
		includedSymbols.put("^KLSE", "^KLSE");
		includedSymbols.put("^BVSP", "^BVSP");
		includedSymbols.put("^AEX", "^AEX");
		includedSymbols.put("^HSCE", "^HSCE");
		includedSymbols.put("^BSESN", "^BSESN");
		includedSymbols.put("^N225", "^N225");
		includedSymbols.put("^ATX", "^ATX");
		// includedSymbols.put("^STI", "^STI");
		includedSymbols.put("^SSMI", "^SSMI");
		includedSymbols.put("^KS11", "^KS11");
		includedSymbols.put("^GDAXI", "^GDAXI");
		includedSymbols.put("^FCHI", "^FCHI");
		includedSymbols.put("^GSPC", "^GSPC");
		includedSymbols.put("^JKSE", "^JKSE");
		includedSymbols.put("^NZ50", "^NZ50");
		includedSymbols.put("^MERV", "^MERV");
		includedSymbols.put("^IBEX", "^IBEX");
		includedSymbols.put("^TWII", "^TWII");
		includedSymbols.put("^MXX", "^MXX");
		includedSymbols.put("^NSEI", "^NSEI");
		includedSymbols.put("^OSEAX", "^OSEAX");
		// includedSymbols.put("^NSEI", "^NSEI");
		includedSymbols.put("^N100", "^N100");
		includedSymbols.put("^ISEQ", "^ISEQ");
		boolean useIncluded = false;

		// includedSymbols.put("SPY", "SPY");
		// includedSymbols.put("^XAL", "^XAL");
		// includedSymbols.put("^VIX","");

		// includedSymbols.put("^XOI", "^XOI");
		// includedSymbols.put("^DJI", "^DJI");
		// includedSymbols.put("^DJT", "^DJT");
		// includedSymbols.put("^IXIC", "^IXIC");
		// includedSymbols.put("^NDX", "^NDX");
		// includedSymbols.put("^UTY", "^UTY");
		// includedSymbols.put("^FTSE", "^FTSE");
		// includedSymbols.put("^RUT", "^RUT");

		Hashtable allRecords = new Hashtable();
		Hashtable records = null;

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			if (mode != null && mode.equalsIgnoreCase("PTMA")) {
				SQL = "SELECT SEQINDEX,SYMBOL,CURRENTDATE,ADJUSTEDPRICE,GR100,FFP,ptma,acptma FROM BPMADMIN.INTRADAYSTOCKS where (SYMBOL LIKE '^%' or SYMBOL='SPY') and seqIndex<="
						+ seqIndex + " ORDER BY SEQINDEX DESC, SYMBOL DESC";
			} else if (mode != null && mode.equalsIgnoreCase("DCP")) {
				SQL = "SELECT SEQINDEX,SYMBOL,CURRENTDATE,ADJUSTEDPRICE,GR100,FFP,dcp,acptma FROM BPMADMIN.INTRADAYSTOCKS where (SYMBOL LIKE '^%' or SYMBOL='SPY') and seqIndex<="
						+ seqIndex + " ORDER BY SEQINDEX DESC, SYMBOL DESC";

			} else {
				SQL = "SELECT SEQINDEX,SYMBOL,CURRENTDATE,ADJUSTEDPRICE,GR100,FFP,ptma,acptma FROM BPMADMIN.INTRADAYSTOCKS where (SYMBOL LIKE '^%' or SYMBOL='SPY') and seqIndex<="
						+ seqIndex + " ORDER BY SEQINDEX DESC, SYMBOL DESC";

			}

			rs1 = stmt1.executeQuery(SQL);

			int seqIndex1 = 0;
			int seqIndex2 = 0;
			String symbol = "";
			String date = "";
			float percentage = 0.0f;

			while (rs1.next()) {
				if (seqIndex1 == 0) {

					seqIndex1 = rs1.getInt(1);
					seqIndex = seqIndex1;
					symbol = rs1.getString(2);
					date = rs1.getString(3);
					percentage = rs1.getFloat(7);

					records = new Hashtable();
					records.put(symbol, "" + percentage);
					records.put("date", date);

				} else
					seqIndex2 = rs1.getInt(1);

				if (seqIndex1 == seqIndex2) {
					symbol = rs1.getString(2);
					percentage = rs1.getFloat(7);
					records.put(symbol, "" + percentage);

				} else if (seqIndex2 > 0 && seqIndex1 != seqIndex2) {
					allRecords.put("" + seqIndex1, records);

					symbol = rs1.getString(2);
					percentage = rs1.getFloat(7);
					seqIndex1 = seqIndex2;
					date = rs1.getString(3);

					records = new Hashtable();
					records.put(symbol, "" + percentage);
					records.put("date", date);
				}
			}

			for (int k = seqIndex; k > seqIndex - allRecords.size(); k--) {

				Hashtable dailyRecords = (Hashtable) allRecords.get("" + k);
				System.out.println("K " + k);
				if (dailyRecords != null) {
					System.out.println("dailyRecords is null");

					String dateString = dailyRecords.get("date").toString();

					Enumeration en = indexes.keys();
					StringBuffer headline = new StringBuffer();
					float sum = 0.0f;

					if (k == seqIndex) {
						headline.append("seqIndex").append(',');
						headline.append("Date").append(',');
					}

					StringBuffer nextline = new StringBuffer();
					nextline.append("" + k).append(',');
					nextline.append(dateString).append(',');

					while (en.hasMoreElements()) { // we rely enumeration return
													// the
													// same
						// order for different loop iteration to align index and
						String nextSymbol = en.nextElement().toString();
						boolean add = false;

						if (useIncluded
								&& includedSymbols.containsKey(nextSymbol)) {
							add = true;
						}

						if (useExcluded
								&& !excludedSymbols.containsKey(nextSymbol)) {
							add = true;
						}
						if (!useIncluded && !useExcluded) {
							add = true;
						}

						if (add) {
							if (k == seqIndex) {
								headline.append(nextSymbol).append(',');
							}

							if (dailyRecords.containsKey(nextSymbol)) {
								float cpp = Float.parseFloat(dailyRecords.get(
										nextSymbol).toString());
								nextline.append(cpp).append(',');
								sum = sum + cpp;
							} else {
								for (int w = k - 1; w > k - 8; w--) {
									Hashtable dailyRecordsNext = (Hashtable) allRecords
											.get("" + w);
									if (dailyRecordsNext != null
											&& dailyRecordsNext
													.containsKey(nextSymbol)) {
										float cpp = Float
												.parseFloat(dailyRecordsNext
														.get(nextSymbol)
														.toString());
										nextline.append(cpp).append(',');
										sum = sum + cpp;
										System.out.println("For " + nextSymbol
												+ " no value at " + k
												+ " using instead value at "
												+ w);
										break;
									} else if (w == k - 7) {
										nextline.append(" ").append(',');
										sum = sum;
									}

								}

							}
						}
					}

					if (mode != null && mode.equalsIgnoreCase("DCP")) {

						if (k == seqIndex) {
							Files.appendToFile("C:\\stock\\Book88.csv",
									headline.append("SUM").toString());
							System.out
									.println("Heading " + headline.toString());

						}

						Files.appendToFile("C:\\stock\\Book88.csv", nextline
								.append(sum).toString());
					} else {

						if (k == seqIndex) {
							Files.appendToFile("C:\\stock\\Book85.csv",
									headline.append("SUM").toString());
							System.out
									.println("Heading " + headline.toString());

						}

						Files.appendToFile("C:\\stock\\Book85.csv", nextline
								.append(sum).toString());

					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static Hashtable getUturnStocks(int seqIndex) {
		Hashtable stocks = new Hashtable();

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select SYMBOL, SEQINDEX from BPMADMIN.INTRADAYSTOCKS where FFP=1 AND SEQINDEX="
					+ seqIndex + " AND ADJUSTEDPRICE>10 and volume>350000";

			rs1 = stmt1.executeQuery(SQL);

			int count = 1;
			while (rs1.next()) {
				String symbol = rs1.getString(1);
				int index = rs1.getInt(2);
				SQL = "SELECT SEQINDEX, ACPTMA, ADJUSTEDPRICE from BPMADMIN.INTRADAYSTOCKS  where   symbol='"
						+ symbol
						+ "' and ACPTMA<>0 and SEQINDEX<"
						+ index
						+ " ORDER BY SEQINDEX DESC";
				rs2 = stmt2.executeQuery(SQL);

				if (rs2.next()) {
					int index2 = rs2.getInt(1);
					float acptma = rs2.getFloat(2);
					float price = rs2.getFloat(3);
					if ((index - index2) < 50 && acptma < -5
							&& getStockCap(symbol, price) > 1000.0f
							&& getStockCap(symbol, price) < 5000.0f) {
						SQL = "SELECT COUNT(*) from BPMADMIN.INTRADAYSTOCKS  where   symbol='"
								+ symbol
								+ "' and FFP=1 and SEQINDEX<"
								+ index
								+ " and SEQINDEX> " + index2;
						rs2 = stmt2.executeQuery(SQL);
						if (rs2.next()) {
							if (rs2.getInt(1) == 0) {
								stocks.put(symbol, symbol);
							}
						}
					}

				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return stocks;

	}

	public static float getStockCap(String symbol, float price) {
		float marketCap = 0.0f;

		if (symbol.equalsIgnoreCase("VXX")) {
			marketCap = 0.0f;
		}

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select marketcap, pricefinal from FULLVSTRANK where SEQINDEX=10606 and symbol='"
					+ symbol + "'";

			rs1 = stmt1.executeQuery(SQL);

			float marketCapS = 0.0F;
			float priceS = 0.0F;

			if (rs1.next()) {
				marketCapS = rs1.getFloat(1);
				priceS = rs1.getFloat(2);

			}

			SQL = "select   ADJUSTEDPRICE from BPMADMIN.INTRADAYSTOCKS  where SEQINDEX=10606 and symbol='"
					+ symbol + "'";

			rs1 = stmt1.executeQuery(SQL);
			float priceN = 0.0f;

			if (rs1.next()) {

				priceN = rs1.getFloat(1);

			}

			marketCap = marketCapS * (price / priceN);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return marketCap;

	}

	public static Hashtable getNotUpdatedStocks(long seqIndex) {
		Hashtable stocks = new Hashtable();

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select SYMBOL from BPMADMIN.INTRADAYSTOCKS where SEQINDEX="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			Hashtable tempstocks = new Hashtable();
			int count = 1;
			while (rs1.next()) {
				String symbol = rs1.getString(1);
				tempstocks.put(symbol, symbol);

			}

			Enumeration en = tempstocks.keys();

			while (en.hasMoreElements()) {
				String symbol = en.nextElement().toString();

				if (!isCalculationDone(symbol)) {
					stocks.put(symbol, symbol);
					System.out.println("Stock not updated " + symbol);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return stocks;

	}

	public static Hashtable getLeftStocks() {
		Hashtable stocks = new Hashtable();

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "SELECT distinct(SYMBOL) from BPMADMIN.INTRADAYSTOCKS  where uptrendstart=99 Order by SYMBOL DESC";

			rs1 = stmt1.executeQuery(SQL);

			int count = 1;
			while (rs1.next()) {
				String symbol = rs1.getString(1);
				stocks.put(symbol, symbol);

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return stocks;

	}

	public static Hashtable getCurrentEODStocks() {
		Hashtable stocks = new Hashtable();

		try {
			// Establish the connection.
			if (conEOD == null) {
				conEOD = getEODConnection();
			}

			String SQL3 = "select SEQINDEX from BPMADMIN.YAHOODB where SYMBOL='AMZN' OR SYMBOL='FB' OR SYMBOL='WMT' OR SYMBOL='AAPL' ORDER BY SEQINDEX DESC";

			rs1EOD = stmt1EOD.executeQuery(SQL3);

			int index = 0;
			if (rs1EOD.next()) {
				index = rs1EOD.getInt(1);
			}

			SQL3 = "select SYMBOL from BPMADMIN.YAHOODB where  SEQINDEX = "
					+ index;

			rs1EOD = stmt1EOD.executeQuery(SQL3);
			while (rs1EOD.next()) {
				String ns = rs1EOD.getString(1);
				stocks.put(ns, ns);
			}
			
			rs1EOD.close();
			rs1EOD = null;
			stmt1EOD.close();
			stmt1EOD = null;
			conEOD.close();
			conEOD = null;

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return stocks;

	}

	public static Hashtable getCurrentAllStocks(long seqIndex) {
		Hashtable stocks = new Hashtable();

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();

			SQL = "select SYMBOL from BPMADMIN.INTRADAYSTOCKS where uptrendstart<993 AND SEQINDEX="
					+ seqIndex;

			rs1 = stmt1.executeQuery(SQL);

			int count = 1;
			while (rs1.next()) {
				String symbol = rs1.getString(1);
				stocks.put(symbol, symbol);

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

		return stocks;

	}

	public static void transferRecords(long seqIndex) {
		Hashtable stocks = getCurrentAllStocks(seqIndex - 1);
		Enumeration en = stocks.keys();
		while (en.hasMoreElements()) {
			try {
				String symbol = en.nextElement().toString();

				String sql = "SELECT SEQINDEX, SYMBOL,VARCHAR_FORMAT(CURRENTDATE, 'MM-DD-YYYY'),PRICEOPEN,PRICELOW,PRICEHIGH,PRICEFINAL, VOLUME "
						+ " FROM FULLVSTRANK WHERE SYMBOL='"
						+ symbol
						+ "' AND SEQINDEX=" + seqIndex;

				rs3 = stmt3.executeQuery(sql);

				if (rs3.next()) {
					YStock stock = new YStock();
					stock.setSymbol(symbol);
					stock.setDate(rs3.getString(3));
					stock.setOpenPrice(rs3.getFloat(4));
					stock.setLowPrice(rs3.getFloat(5));
					stock.setHighPrice(rs3.getFloat(6));
					stock.setFinalPrice(rs3.getFloat(7));
					stock.setAdjustedPrice(rs3.getFloat(7));
					stock.setVolume(rs3.getFloat(8));
					insertRecord(seqIndex, stock);
				}

			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}

		}

	}

	public static boolean hasBeenVerified(String symbol) {
		boolean result = false;
		getBothEnds(symbol);

		try {
			int previousAge = 0;
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "SELECT GX100 from BPMADMIN.INTRADAYSTOCKS  where   symbol='"
					+ symbol + "' and seqIndex=" + startIndexLast;
			rs1 = stmt1.executeQuery(SQL);

			if (rs1.next()) {
				previousAge = rs1.getInt(1);
			}

			if (previousAge == -999)
				result = true;

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return result;
	}

	public static void resetRecords(String symbol) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set DSI5=0, UPTRENDSTART=0,DELTASUM=0 where  symbol='"
					+ symbol + "'";
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void resetDPS4(String symbol) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set DPS4=0 where  symbol='"
					+ symbol + "'";
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void resetSATCount(String symbol) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set SATCOUNT=0,DCINCR=0,DSNOTDECR=0,DCDECR=0,DSNOTINCR=0 where  symbol='"
					+ symbol + "'";
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void resetFFP(String symbol) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set FFP=0 where  symbol='"
					+ symbol + "'";
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void resetBBGO(String symbol) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set BBGO=0 where  symbol='"
					+ symbol + "'";
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void resetLBBI(String symbol) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set LBBI =0 where  symbol='"
					+ symbol + "'";
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void resetIBBS(String symbol) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set IBBS =0 where  symbol='"
					+ symbol + "' and IBBS<>0";
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void resetDCPIP(String symbol) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set DCPIP=0 where  symbol='"
					+ symbol + "'";
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void resetUTIS(String symbol) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set UTI=0, UTIS=0 where  symbol='"
					+ symbol + "'";
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void resetDS3PT(String symbol) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set DS3PT=0 where  symbol='"
					+ symbol + "'";
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void resetDCPT(String symbol) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set DCPT=0 where  symbol='"
					+ symbol + "'";
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void resetDIPSCORE(String symbol) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set DIPSCORE=0 where  symbol='"
					+ symbol + "'";
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void resetDIPSUMGradientTWA(String symbol) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set TWA=0 where  symbol='"
					+ symbol + "'";
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void resetDIPSCORESUM(String symbol) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set DCP=0 where  symbol='"
					+ symbol + "'";
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void AAA_ZZresetCalculationNotDoneTagZZ(long supposedIndex) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			Hashtable stocks = SQLOperation3.getCurrentAllStocks(15528L);
			System.out.println("Stocks has " + stocks.size());

			SQL = "SELECT SYMBOL FROM BPMADMIN.INTRADAYSTOCKS  WHERE SEQINDEX = "
					+ supposedIndex;

			rs1 = stmt1.executeQuery(SQL);

			while (rs1.next()) {
				String nextStock = rs1.getString(1);
				if (stocks.containsKey(nextStock)) {
					stocks.remove(nextStock);
				}
			}

			System.out.println("Stocks size is now " + stocks.size());

			Enumeration en2 = stocks.keys();

			while (en2.hasMoreElements()) {
				String ns = en2.nextElement().toString();
				//setDownloadCompleteTag(ns);
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void resetAllCalculationDoneTag() {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set uptrendstart=0 where  uptrendstart>=99 and uptrendstart<=100";
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void resetCalculationDoneTag(String symbol) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set uptrendstart=0 where  symbol='"
					+ symbol + "' and uptrendstart=100";
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void splitCalculationTag(String symbol, long seqIndex, int tag) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			getBothEnds(symbol);

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set uptrendstart=" + tag
					+ " where  symbol='" + symbol + "' and SEQINDEX="
					+ seqIndex;
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void setDownloadCompleteTag(String symbol, long index, int tag) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			//getBothEnds(symbol);

			String SQL2 = "update BPMADMIN.INTRADAYSTOCKS  set uptrendstart="+tag+" where  symbol='"
					+ symbol + "' and SEQINDEX=" + index;
			stmt1.executeUpdate(SQL2);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void setCalculationDoneTag(String symbol) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			getBothEnds(symbol);

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set uptrendstart=100 where  symbol='"
					+ symbol + "' and SEQINDEX=" + startIndexLast;
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void setReCalculationDoneTag(String symbol) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			getBothEnds(symbol);

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set uptrendstart=1000 where  symbol='"
					+ symbol
					+ "' and SEQINDEX>"
					+ startIndexLast
					+ " and SEQINDEX<" + (startIndexLast + 5);
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static boolean isReCalculationDone(String symbol) {

		boolean done = false;
		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select count(*) from BPMADMIN.INTRADAYSTOCKS  where  symbol='"
					+ symbol + "' and uptrendstart=1000";
			rs1 = stmt1.executeQuery(SQL);
			if (rs1.next()) {
				if (rs1.getInt(1) > 0) {
					done = true;
				}
			}

			if (!done) {
				SQL = "select count(*) from BPMADMIN.INTRADAYSTOCKS  where  symbol='"
						+ symbol + "'";
				rs1 = stmt1.executeQuery(SQL);
				if (rs1.next()) {
					if (rs1.getInt(1) == 0) {
						done = true;
					}
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return done;

	}

	public static boolean isInsertionDone(String symbol) {

		boolean done = false;
		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			/*
			 * SQL =
			 * "select count(*) from BPMADMIN.INTRADAYSTOCKS  where  symbol='" +
			 * symbol + "' and uptrendstart=100"; rs1 = stmt1.executeQuery(SQL);
			 * if (rs1.next()) { if (rs1.getInt(1) > 0) { done = true; } }
			 */

			// use inserted records as more direct evidence
			// change to top most record as mau have less records
			// if brand new stocks
			int recordCount = 79 * StaticData.getDownloadDays();
			long lastIndex = StaticData.getIntradayLastIndex();
			String SQL2 = "select count(*) from BPMADMIN.INTRADAYSTOCKS  where  symbol='"
					+ symbol + "' and SEQINDEX=" + (lastIndex + recordCount);
			rs1 = stmt1.executeQuery(SQL2);
			if (rs1.next()) {
				if (rs1.getInt(1) > 0) {
					done = true;
				}
			}
			/*
			 * if (!done) { SQL =
			 * "select count(*) from BPMADMIN.INTRADAYSTOCKS  where  symbol='" +
			 * symbol + "'"; rs1 = stmt1.executeQuery(SQL); if (rs1.next()) { if
			 * (rs1.getInt(1) == 0) { done = true; } } }
			 */

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return done;

	}

	public static boolean isCalculationDone(String symbol,long lastIndex, int days) {

		boolean done = false;
		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select count(*) from BPMADMIN.INTRADAYSTOCKS  where  symbol='"
					+ symbol + "' and SEQINDEX>"+lastIndex;
			rs1 = stmt1.executeQuery(SQL);
			if (rs1.next()) {
				if (rs1.getInt(1) ==79*days) {
				//if (rs1.getInt(1) ==43) {
				
					done = true;
				}
			}

			/*
			 * if (!done) { SQL =
			 * "select count(*) from BPMADMIN.INTRADAYSTOCKS  where  symbol='" +
			 * symbol + "'"; rs1 = stmt1.executeQuery(SQL); if (rs1.next()) { if
			 * (rs1.getInt(1) == 0) { done = true; } } }
			 */

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return done;

	}

	
	public static boolean isCalculationDone(String symbol) {

		boolean done = false;
		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "select count(*) from BPMADMIN.INTRADAYSTOCKS  where  symbol='"
					+ symbol + "' and uptrendstart=100";
			rs1 = stmt1.executeQuery(SQL);
			if (rs1.next()) {
				if (rs1.getInt(1) > 0) {
					done = true;
				}
			}

			/*
			 * if (!done) { SQL =
			 * "select count(*) from BPMADMIN.INTRADAYSTOCKS  where  symbol='" +
			 * symbol + "'"; rs1 = stmt1.executeQuery(SQL); if (rs1.next()) { if
			 * (rs1.getInt(1) == 0) { done = true; } } }
			 */

		} catch (Exception ex) {
			ex.printStackTrace();

		}
		return done;

	}
	public static void resetBBDI(String symbol) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set BBDI=0 where  symbol='"
					+ symbol + "' and BBDI<>0";
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void resetBBScore(String symbol) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set BBSCORE=0 where  symbol='"
					+ symbol + "' and BBSCORE<>0 ";
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void resetSellingScore(String symbol) {

		try {
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set SELLINGSCORE=0 where  symbol='"
					+ symbol + "' and SELLINGSCORE<>0";
			stmt1.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void UpdateRecordAge(String symbol) {
		getBothEnds(symbol);

		try {
			int previousAge = 0;
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			SQL = "SELECT GX100 from BPMADMIN.INTRADAYSTOCKS  where   symbol='"
					+ symbol + "' and seqIndex=" + startIndexLast;
			rs1 = stmt1.executeQuery(SQL);

			if (rs1.next()) {
				previousAge = rs1.getInt(1);
			}

			SQL = "update BPMADMIN.INTRADAYSTOCKS  set GX100="
					+ (previousAge + 1) + " where  seqIndex=" + startIndexLast
					+ " and symbol='" + symbol + "'";
			SQL = "update BPMADMIN.INTRADAYSTOCKS  set GX100=998 where  seqIndex="
					+ startIndexLast + " and symbol='" + symbol + "'";
			stmt2.executeUpdate(SQL);

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void markWeekEnds(String symbol, boolean thisStockOnly,
			int counts) {

		getBothEnds(symbol);

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null)
				stmt1 = con.createStatement();

			int bottomIndex = 0;

			if (counts > 0) {
				SQL = "select SEQINDEX from BPMADMIN.INTRADAYSTOCKS where symbol='"
						+ symbol + "'  order by SEQINDEX DESC";
				rs1 = stmt1.executeQuery(SQL);
				int loopCount = 0;
				while (rs1.next()) {
					bottomIndex = rs1.getInt(1);
					loopCount++;
					if (loopCount > counts)
						break;

				}
			}

			SQL = "select SEQINDEX, ADJUSTEDPRICE, CURRENTDATE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
					+ symbol
					+ "' and seqIndex <="
					+ endIndexLast
					+ " and seqIndex>="
					+ startIndexLast
					+ " order by SEQINDEX ASC";

			if (bottomIndex > 0)
				SQL = "select SEQINDEX, ADJUSTEDPRICE, CURRENTDATE from BPMADMIN.INTRADAYSTOCKS  where symbol='"
						+ symbol
						+ "' and seqIndex <="
						+ endIndexLast
						+ " and seqIndex>="
						+ bottomIndex
						+ " order by SEQINDEX ASC";

			System.out.println(SQL);
			rs1 = stmt1.executeQuery(SQL);
			int[] seqIndex = new int[5000];
			java.util.Date previousDate = null;
			java.util.Date currentDate = null;
			int previousWeekDate = 0;
			int currentWeekDate = 0;
			int previousIndex = 0;
			int currentIndex = 0;
			float previousPrice = 0.0f;
			float currentPrice = 0.0f;

			int weekCount = 0;
			while (rs1.next()) {

				if (previousDate == null) {
					previousDate = rs1.getDate(3);
					previousWeekDate = previousDate.getDay();
					previousIndex = rs1.getInt(1);
				} else if (currentDate == null) {
					currentDate = rs1.getDate(3);
					currentWeekDate = currentDate.getDay();
					currentIndex = rs1.getInt(1);
				} else {
					previousDate = currentDate;
					currentDate = rs1.getDate(3);
					previousIndex = currentIndex;
					currentIndex = rs1.getInt(1);
					previousWeekDate = previousDate.getDay();
					currentWeekDate = currentDate.getDay();
					// System.out.println(currentDate.getMonth());
					// System.out.println(previousDate.getMonth());

				}

				if ((currentWeekDate == 5)) {
					seqIndex[weekCount] = currentIndex;

					System.out.println(weekCount + " " + rs1.getInt(1) + " "
							+ seqIndex[weekCount]);
					weekCount++;
				} else if (currentWeekDate <= 3 && previousWeekDate < 5
						&& previousWeekDate >= 3) {
					seqIndex[weekCount] = previousIndex;

					System.out.println(weekCount + " " + rs1.getInt(1) + " "
							+ seqIndex[weekCount]);
					weekCount++;
				}

			}

			for (int k = 0; k < weekCount; k++) {
				SQL = "update BPMADMIN.INTRADAYSTOCKS   set WWI = -1 where seqIndex = "
						+ seqIndex[k];

				if (thisStockOnly)
					SQL = "update BPMADMIN.INTRADAYSTOCKS   set WWI = -1 where SYMBOL='"
							+ symbol + "' and seqIndex = " + seqIndex[k];

				System.out.println(weekCount + " " + SQL + " " + seqIndex[k]);
				stmt1.executeUpdate(SQL);

			}

		} catch (Exception ex) {
			ex.printStackTrace();

		}

	}

	public static void findSpyWeeklyStatus(int endIndex, int startIndex) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null) {
				stmt1 = con.createStatement();
				stmt2 = con.createStatement();

			}

			con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			SQL = "Select seqIndex,finalprice,currentdate FROM BPMADMIN.INTRADAYSTOCKS   where  seqIndex<="
					+ endIndex
					+ "  and seqIndex>="
					+ startIndex
					+ " and WWI<>0 and symbol='SPY' order by seqIndex desc";

			rs1 = stmt1.executeQuery(SQL);
			int seqIndexFriday1 = 0;
			float finalPriceFriday1 = 0.0f;
			String FDate1 = "";
			int seqIndexFriday2 = 0;
			float finalPriceFriday2 = 0.0f;
			String FDate2 = "";
			int seqIndexMonday = 0;
			float finalPriceMonday = 0.0f;
			String MDate1 = "";
			float percentageChange = 0.0f;
			int lttpc = 0;
			int gttpc = 0;

			while (rs1.next()) {
				if (seqIndexFriday1 == 0) {
					seqIndexFriday1 = rs1.getInt(1);
					finalPriceFriday1 = rs1.getFloat(2);
					FDate1 = rs1.getString(3);
					// /System.out.println(seqIndexFriday1+": "+FDate1);

				} else if (seqIndexFriday2 == 0) {
					seqIndexFriday2 = rs1.getInt(1);
					finalPriceFriday2 = rs1.getFloat(2);
					FDate2 = rs1.getString(3);

				} else {
					seqIndexMonday = seqIndexFriday2 + 1;
					SQL = "Select seqIndex,finalprice,currentDate FROM BPMADMIN.INTRADAYSTOCKS  where  seqIndex="
							+ seqIndexMonday + "  and  symbol='SPY' ";

					rs2 = stmt2.executeQuery(SQL);
					if (rs2.next()) {
						finalPriceMonday = rs2.getFloat(2);
						MDate1 = rs2.getString(3);

						percentageChange = 100.0f
								* (finalPriceFriday1 - finalPriceMonday)
								/ finalPriceMonday;

						System.out.println(MDate1 + "(" + seqIndexMonday + ":"
								+ finalPriceMonday + ") to " + FDate1 + "("
								+ seqIndexFriday1 + ":" + finalPriceFriday1
								+ ") percentageChange  " + percentageChange);
						// totalWeeklyGain = totalWeeklyGain + percentageChange;

						if (percentageChange > 2.5f || percentageChange < -2.5f) {
							gttpc++;
							// System.out.println("seqIndexMonday "+seqIndexMonday);
						} else {
							lttpc++;
						}

					}

					seqIndexFriday1 = seqIndexFriday2;
					finalPriceFriday1 = finalPriceFriday2;
					FDate1 = FDate2;

					seqIndexFriday2 = rs1.getInt(1);
					finalPriceFriday2 = rs1.getFloat(2);
					FDate2 = rs1.getString(3);
				}

			}

			System.out.println("lttpc  " + lttpc);
			System.out.println("gttpc  " + gttpc);
			// System.out.println("totalWeeklyGain "+totalWeeklyGain);

		}
		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			// closeConnection();
		}

	}

	public static void findSpyStatusByDays(int endIndex, int startIndex,
			int days) {

		try {
			// Establish the connection.
			if (con == null)
				con = getConnection();
			// Create and execute an SQL statement that returns some data.
			if (stmt1 == null) {
				stmt1 = con.createStatement();
				stmt2 = con.createStatement();

			}

			con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			SQL = "Select seqIndex,finalprice,currentdate FROM BPMADMIN.INTRADAYSTOCKS   where  seqIndex<="
					+ endIndex
					+ "  and seqIndex>="
					+ startIndex
					+ "  and symbol='SPY' order by seqIndex desc";

			rs1 = stmt1.executeQuery(SQL);
			int seqIndexFriday1 = 0;
			float finalPriceFriday1 = 0.0f;
			String FDate1 = "";
			int seqIndexFriday2 = 0;
			float finalPriceFriday2 = 0.0f;
			String FDate2 = "";
			int seqIndexMonday = 0;
			float finalPriceMonday = 0.0f;
			String MDate1 = "";
			float percentageChange = 0.0f;
			int lttpc = 0;
			int gttpc = 0;
			int loopCount = 1;

			while (rs1.next()) {
				if (seqIndexFriday1 == 0) {
					seqIndexFriday1 = rs1.getInt(1);
					finalPriceFriday1 = rs1.getFloat(2);
					FDate1 = rs1.getString(3);
					// /System.out.println(seqIndexFriday1+": "+FDate1);

				} else if (loopCount % days == 0) {
					seqIndexFriday2 = rs1.getInt(1);
					finalPriceFriday2 = rs1.getFloat(2);
					FDate2 = rs1.getString(3);

					percentageChange = 100.0f
							* (finalPriceFriday1 - finalPriceFriday2)
							/ finalPriceFriday2;

					// totalWeeklyGain = totalWeeklyGain + percentageChange;

					if (percentageChange > 6f || percentageChange < -6f) {
						gttpc++;
						// System.out.println("seqIndexMonday "+seqIndexMonday);
					} else {
						lttpc++;
					}

					seqIndexFriday1 = seqIndexFriday2;
					finalPriceFriday1 = finalPriceFriday2;
					FDate1 = FDate2;

				}
				loopCount++;
			}

			System.out.println("lttpc  " + lttpc);
			System.out.println("gttpc  " + gttpc);
			// System.out.println("totalWeeklyGain "+totalWeeklyGain);

		}
		// Handle any errors that may have occurred.
		catch (Exception e) {
			e.printStackTrace();
		}

		finally {
			// closeConnection();
		}

	}

	public static int getUtis1Count() {
		return utis1Count;
	}

	public static void setUtis1Count(int utis1Count) {
		SQLOperation3.utis1Count = utis1Count;
	}

	public static int getUtis2Count() {
		return utis2Count;
	}

	public static void setUtis2Count(int utis2Count) {
		SQLOperation3.utis2Count = utis2Count;
	}

	public static int getUtis3Count() {
		return utis3Count;
	}

	public static void setUtis3Count(int utis3Count) {
		SQLOperation3.utis3Count = utis3Count;
	}

	public static long getStartIndexLast() {
		return startIndexLast;
	}

	public static void setStartIndexLast(long startIndexLast) {
		SQLOperation3.startIndexLast = startIndexLast;
	}

	public static long getEndIndexLast() {
		return endIndexLast;
	}

	public static void setEndIndexLast(long endIndexLast) {
		SQLOperation3.endIndexLast = endIndexLast;
	}

}
