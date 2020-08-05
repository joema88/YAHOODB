package com.indice.ptma.helper;

import java.util.Enumeration;
import java.util.Hashtable;

import com.indice.ptma.sql.*;

public class GR100Calculation {
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		redoCalculation(10693,10706);

	}

	public static void redoCalculation(int start, int end) {
		for (int w = start; w < end + 1; w++) {
			Hashtable allStocks =SQLOperation.getCurrentYahooStocks(w);
			//allStocks.put("SPY", "SPY");
			System.out.println("Total stocks " + allStocks.size());
			int count = 0;
			Enumeration en = allStocks.keys();
			while (en.hasMoreElements()) {
				String symbol = en.nextElement().toString();

				count++;
				System.out.println(symbol + " history done ");
				SQLOperation.calculateTodayGR100(symbol, w);

				
			}
		}
	}

}
