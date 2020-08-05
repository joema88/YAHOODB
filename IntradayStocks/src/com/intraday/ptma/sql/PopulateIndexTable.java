package com.intraday.ptma.sql;

public class PopulateIndexTable {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		//current records till 12/31/2019 only!!
		//need to consider market closing days (weekends and holidays)
		SQLOperation.populateTradingTimeTable();
	}

}
