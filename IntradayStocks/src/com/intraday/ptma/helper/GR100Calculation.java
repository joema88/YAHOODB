package com.intraday.ptma.helper;

import java.util.Enumeration;
import java.util.Hashtable;

import com.intraday.ptma.sql.*;

public class GR100Calculation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		redoCalculation(10693,10706);

	}

	public static void redoCalculation(long start, long end) {
		for (long w = start; w < end + 1; w++) {
			Hashtable allStocks =SQLOperation.getCurrentAllStocks(w);
			//allStocks.put("SPY", "SPY");
			System.out.println("Total stocks " + allStocks.size());
			int count = 0;
			Enumeration en = allStocks.keys();
			while (en.hasMoreElements()) {
				String symbol = en.nextElement().toString();

				count++;
				System.out.println(symbol + " history done ");
				SQLOperation.calculateTodayGR100(symbol, (int)w);

				
			}
		}
	}

}
