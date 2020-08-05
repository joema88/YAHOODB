package com.indice.ptma.driver;

import com.indice.ptma.helper2.*;
import com.indice.ptma.sql2.*;

import java.util.Hashtable;

public class StockHistory {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String symbol = "^JKSE";// AVGO 10634, -1
		symbol = "^OMX";
		//symbol = "^MERV";
		//symbol = "^BVSP";
		//symbol = "^N225";
		//symbol = "^ATX";
		//symbol = "^OSEAX";
		//symbol = "^SSMI";
		//symbol = "^KS11";
		//symbol = "^GDAXI";
		//symbol = "^JKPROP";
		int lastIndex = 10682;

		//SQLOperation.deleteRecord(symbol);
		getStockHistory(symbol, lastIndex);

	}

	public static void getStockHistory(String symbol, int lastIndex) {
		try {
			//HttpDownload.downLoadStockHistory(symbol);
			//Hashtable results = Files.getIndexHistory(symbol);

			//SQLOperation.insertHistoryRecord(lastIndex, results);
			SQLOperation.isTodayMonthEnd(lastIndex);
			SQLOperation.markMonthEnd(symbol,-1);
			SQLOperation.calculateTMA(symbol, false,-1);
			SQLOperation.calculatePTMAHistory(symbol,-1);
			//SQLOperation.calculateFFPHistory(symbol);
			SQLOperation.findGRXPointsHistory(symbol,-1);
			SQLOperation.doGR100Calculation(symbol,-1);
			SQLOperation.UpdateRecordAge(symbol);
			SQLOperation.calculateACPTMAHistory(symbol,-1);
			System.out.println("Processing done for " + symbol);
			try {
				Thread.sleep(15000);
			} catch (Exception ex) {

			}

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}
}
