package com.intraday.patterns;

import com.intraday.ptma.sql.SQLOperation;

public class ComparePattern {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//SQLOperation.patternMatching("SPY",11,42652,"both");
		int k=42479;
		System.out.println("final price stats: ");
		runPatternMatchingStatistics("SPY",k,"finalprice");
		System.out.println("high price stats: ");
		runPatternMatchingStatistics("SPY",k,"highprice");
		System.out.println("low price stats: ");
		runPatternMatchingStatistics("SPY",k,"lowprice");
		System.out.println("open price stats: ");
		runPatternMatchingStatistics("SPY",k,"openprice");
		

	}

	
	public static void runPatternMatchingStatistics(String symbol, int endIndex,String method){
		long t1 = System.currentTimeMillis();
		/*	System.out.println("44 days cycle...");
		SQLOperation.patternMatching(symbol,44, endIndex,"finalprice");
		int[] tops1 = SQLOperation.patternMatchingTops(symbol,endIndex,5);
		SQLOperation.displayPatternResultMatchingCondition(symbol, tops1, 5,endIndex);
		SQLOperation.displayPatternResult(symbol, tops1,5,endIndex);
		
		System.out.println("33 days cycle...");
		SQLOperation.patternMatching("SPY",33, endIndex,"finalprice");
		int[] tops2 = SQLOperation.patternMatchingTops(symbol,endIndex,5);
		SQLOperation.displayPatternResultMatchingCondition(symbol, tops2, 5,endIndex);
		SQLOperation.displayPatternResult(symbol, tops2,5,endIndex);
		
		
		System.out.println("22 days cycle...");
		SQLOperation.patternMatching("SPY",22, endIndex,"finalprice");
		int[] tops3 = SQLOperation.patternMatchingTops(symbol,endIndex,5);
		SQLOperation.displayPatternResultMatchingCondition(symbol, tops3, 5,endIndex);
		SQLOperation.displayPatternResult(symbol, tops3,5,endIndex);
		*/
		System.out.println("10 days cycle...");
		//SQLOperation.patternMatching("SPY",10, endIndex,"finalprice");
		SQLOperation.patternMatching("SPY",6, endIndex,method);
		int[] tops4 = SQLOperation.patternMatchingTops(symbol,endIndex,5);
		//SQLOperation.findTopMatching(symbol,tops1,tops2,tops3,tops4,5,endIndex);
		SQLOperation.displayPatternResultMatchingCondition(symbol, tops4, 1,endIndex);
		SQLOperation.displayPatternResult(symbol, tops4,1,endIndex);
		
		long t2 = System.currentTimeMillis();
		System.out.println("Total cost time seconds "+(t2-t1)/1000);
	}
}
