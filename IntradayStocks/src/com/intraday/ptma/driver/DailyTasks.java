package com.intraday.ptma.driver;

import com.intraday.ptma.helper.Files;
import com.intraday.ptma.helper.IndexHistory;
import com.intraday.ptma.helper.YStock;
import com.intraday.ptma.sql.SQLOperation;
import java.util.*;


public class DailyTasks {
	// The best selection criteria might be this
	// the gap between FFP is at least 190 days (SKX) and the SUM(PTMA) between
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int seqIndex = 10633;

		for (int w = 10778; w < 10779; w++) {
			// doTodayCalculation(w);
			Files.writeToFile("C://stocks//process.txt", w
					+ " Daily Task started at "
					+ Calendar.getInstance().getTime().toLocaleString());

			doIndexToday(w);
			calculateIndexPTMA(w);
			doTodayCalculationEOD(w, "20160519");
			Files.appendToFile("C://stocks//process.txt", w
					+ " Daily Task finished at "
					+ Calendar.getInstance().getTime().toLocaleString());

			try {
				Thread.sleep(90000);
			} catch (Exception ex) {

			}
		}
		
		

		// seqIndex = 10632;
		// doTodayCalculation(seqIndex);

		// getHistoryData(seqIndex, 1800);

	}

	public static void doIndexToday(int seqIndex) {
		IndexHistory.downLoadInsertIndex();

		boolean weekend = false;
		if (SQLOperation.isTodayWeekEnd(seqIndex)) {
			weekend = true;
		}

		Hashtable tables = IndexHistory.initIndexTable();
		System.out.println("Size " + tables.size());
		Enumeration en = tables.keys();

		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			if (weekend) {
				SQLOperation.calculateTWA(symbol,seqIndex, true);
			}
			SQLOperation.calculatePTWA(symbol, seqIndex);
		}

		System.out.println("PTWA Done for Index");
	}

	public static void doTodayCalculationEOD(int seqIndex, String date) {

		insertAMXEOD(date, seqIndex);
		insertNYSEOD(date, seqIndex);
		insertNASDQEOD(date, seqIndex);

		calculatePTMA_GRX_GR100(seqIndex);
		SQLOperation.calculateTodayACPTMA(seqIndex);

		// let's start tomorrow
		handleStockSplitRecords(seqIndex);

	}

	public static void handleStockSplitRecords(int seqIndex) {
		Hashtable allStocks = SQLOperation.getCurrentAllStocks((long)seqIndex);
		System.out.println("Total stocks " + allStocks.size());
		int loopCount = 0;
		Enumeration en = allStocks.keys();
		while (en.hasMoreElements()) {
			try {
				String symbol = en.nextElement().toString();
				loopCount++;
				if (SQLOperation.isStockSplitted(symbol)) {
					if (SQLOperation.verifyStockForSplit(symbol)) {
						//SQLOperation.deleteAllRecords(symbol);
						StockHistory.getStockHistory(symbol, seqIndex);
					}

				}
				if (loopCount % 100 == 0) {
					System.out.println(loopCount
							+ " stocks checked for split...");
				}
			} catch (Exception ex) {

			}
		}

	}

	public static void insertAMXEOD(String date, int seqIndex) {
		Hashtable result1 = Files.getEODRecordsAMX(date);
		Enumeration en = result1.keys();
		while (en.hasMoreElements()) {
			String key = en.nextElement().toString();
			YStock vstock = (YStock) result1.get(key);
			SQLOperation.insertRecord(seqIndex, vstock);
		}
	}

	public static void insertNYSEOD(String date, int seqIndex) {
		Hashtable result1 = Files.getEODRecordsNYSE(date);
		Enumeration en = result1.keys();
		while (en.hasMoreElements()) {
			String key = en.nextElement().toString();
			YStock vstock = (YStock) result1.get(key);
			SQLOperation.insertRecord(seqIndex, vstock);
		}
	}

	public static void insertNASDQEOD(String date, int seqIndex) {
		Hashtable result1 = Files.getEODRecordsNASDAQ(date);
		Enumeration en = result1.keys();
		while (en.hasMoreElements()) {
			String key = en.nextElement().toString();
			YStock vstock = (YStock) result1.get(key);
			SQLOperation.insertRecord(seqIndex, vstock);
		}
	}

	public static void doTodayCalculation(int seqIndex) {
		SQLOperation.transferRecords(seqIndex);
		calculatePTMA_GRX_GR100(seqIndex);
		SQLOperation.calculateTodayACPTMA(seqIndex);

	}

	public static void calculateIndexPTMA(int seqIndex) {
		int monthEndIndex = seqIndex;
		boolean isMonthEnd = false;

		if (SQLOperation.isTodayMonthEnd(seqIndex)) {
			isMonthEnd = true;
			monthEndIndex = seqIndex;
		} else if (SQLOperation.isPreviousDayMonthEnd(seqIndex)) {
			isMonthEnd = true;
			monthEndIndex = seqIndex - 1;
		}

		Hashtable tables = IndexHistory.initIndexTable();
		System.out.println("Size " + tables.size());
		Enumeration en = tables.keys();

		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			if (isMonthEnd) {
				SQLOperation.calculateTMA(symbol, true,500);
			}
			SQLOperation.calculatePTMA(symbol, monthEndIndex);
			if (monthEndIndex < seqIndex) {
				SQLOperation.calculatePTMA(symbol, seqIndex);
			}
			SQLOperation.UpdateRecordAge(symbol);
			System.out.println("Calculation done for " + symbol + " at "
					+ seqIndex);
		}
	}

	public static void calculatePTMA_GRX_GR100(int seqIndex) {
		int monthEndIndex = seqIndex;
		boolean isMonthEnd = false;

		if (SQLOperation.isTodayMonthEnd(seqIndex)) {
			isMonthEnd = true;
			monthEndIndex = seqIndex;
		} else if (SQLOperation.isPreviousDayMonthEnd(seqIndex)) {
			isMonthEnd = true;
			monthEndIndex = seqIndex - 1;
		}

		Hashtable stocks = SQLOperation.getCurrentAllStocks(seqIndex);
		Enumeration en = stocks.keys();

		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			if (isMonthEnd) {
				SQLOperation.calculateTMA(symbol, true,-1);
			}
			SQLOperation.calculatePTMA(symbol, monthEndIndex);
			if (monthEndIndex < seqIndex) {
				SQLOperation.calculatePTMA(symbol, seqIndex);
			}

			SQLOperation.calculateTodayGRXPoints(symbol, seqIndex);
			SQLOperation.calculateTodayGR100(symbol, seqIndex);
			SQLOperation.calculateTodayFFP(symbol, seqIndex);
			SQLOperation.calculateTodayBBGO(symbol, seqIndex);
			SQLOperation.UpdateRecordAge(symbol);
			System.out.println("Calculation done for " + symbol + " at "
					+ seqIndex);
		}
	}

	public static void getHistoryData(int seqIndex, int recordsNumber) {
		Hashtable stocks = SQLOperation.getCurrentVSTStocks(
				SQLOperation.sortedByMarketcap, seqIndex);
		Hashtable ystocks = SQLOperation.getCurrentAllStocks((long)seqIndex);
		int count = 0;
		for (int k = 1; k <= stocks.size(); k++) {
			String symbol = stocks.get("" + k).toString();
			System.out.println("Symbol " + symbol);
			if (symbol.startsWith("~")) {
				symbol = symbol.substring(1);
				if (!ystocks.containsKey(symbol)) {
					System.out.println("Retrieving history for " + symbol);
					StockHistory.getStockHistory(symbol, seqIndex);
					count++;
				}
			}

			if (count >= recordsNumber)
				break; // we got enough records
		}
	}
}
