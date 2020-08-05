package com.yahoo.ptma.helper;

import com.yahoo.ptma.sql.SQLOperation;

public class WeeklyHL4Recal {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Files.createEODTagFile("WeeklyHL4RecalStart.txt");
		
		//Single Stock Recal
		//String symbol="TNDM";
		//int endIndex = 43363;
		//SQLOperation.calculateCBullHistoryNew(symbol, endIndex,true);
		//SQLOperation.calculateCBullHistoryNew(symbol, endIndex,false);
		//SQLOperation.calculateHL4History(symbol, endIndex);
		
		
		//entire stock recal
		int endIndex = 43363;
		SQLOperation.recalculateEntireHL4History(43363);
		Files.createEODTagFile("WeeklyHL4RecalDone.txt");
	}

}
