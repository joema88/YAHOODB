package com.indice.ptma.sql2;

import java.util.Hashtable;
import java.util.Enumeration;

public class TestFunction {
	
	// BUY ATEC,EBIO,AKG, TDW,SSW,IO
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//UPDATE 3/2/2018 TO 5/22/2018 RECORDS
		//updateEODIBBSetc(43160,43241);
		updateIBSPOnly(43160,43241);
	}
	
	public static void updateIBSPOnly(int startIndex, int endIndex) {
		Hashtable allStocks = SQLOperation.getIBBSExistStocks(startIndex, endIndex);
		allStocks.put("HTBX", "HTBX");
		Enumeration en2 = allStocks.keys();
		System.out.println("Stock size "+allStocks.size());
		
		try{
			Thread.sleep(5000);
		}catch(Exception ex){
			
		}
		
		int lc=0;
		while (en2.hasMoreElements()) {
			String symbol = en2.nextElement().toString();
			// SQLOperation.resetIBSPRecords(symbol);
			// CHECK CBIO REVISE ALGORITHM TO CATCH PTMA<0 --> PTMA>0 SCORE+40??
			// AWS FROM -12 TO +12 SCORE +20??
			// IBSP>=95, 100?? BUY SIGNAL??? BAC, NVDA, AMPE,IPGP,
			// +2% PBR, >=100? SELECT 1%, CHECK NSM WHAT LEARNED??BBSCORE
			// WEIGHT??
			// NO BBSCORE=3000, MORE DEDUCTION
			lc++;
			for (int k = endIndex; k >= startIndex; k--) {
				// for(int w=43191; w>=43026; w--){
				SQLOperation.scoreStockIBBS(symbol, k, true);
				System.out.println(k + " records done..." + symbol + " " + lc
						+ " out of " + allStocks.size());
			}
			// break;
		}

	}
	

	public static void updateEODIBBSetc(int startIndex, int endIndex) {

		for (int w = startIndex; w <= endIndex; w++) {
			Hashtable allStocks = SQLOperation.getCurrentYahooStocks(w);
			System.out.println("All stock size " + allStocks.size() + " at "
					+ w);

			try {
				Thread.sleep(5000);
			} catch (Exception ex) {

			}
			// Hashtable allStocks = new Hashtable();
			// allStocks.put("THO", "THO");
			Enumeration en = allStocks.keys();
			int size = allStocks.size();

			int lc = 0;

			// CHECK SOXL, IBSP>CERTAIN VALUE(SAY 150), AND COULD NO MORE MAKE
			// 1% TO 2% HIGHER, THEN, IBSP RETURNS TO 0, THEN POSITIVE NUMBER
			// AGAIN, NO MATTER WHAT THE NUMBER IS, AS LONG AS AFTER THAT
			// IT MAKES 1% TO 2% HIGHER, THEN BUY POINT FOR HOGHER!!!
			// EVEN THE FIRST BUMP MAKES ABOVE 2%, BUT NOT MUCH, THEN QUCIKLY
			// FADED INTO SERVE LOSS, THEN THE NEXT IBPS POSITIVE IS A STRONG
			// BUY
			// LOOK AT IPGP
			SQLOperation.overlayAllBBScore(w, true);

			SQLOperation.updateLastIBSSum(w, true);

			System.out.println("AA records done...at " + w);
		}

		
		Hashtable allStocks2 = SQLOperation.getCurrentYahooStocks(endIndex);
		System.out.println("All stock size " + allStocks2.size() + " at "
				+ endIndex);

		try {
			Thread.sleep(5000);
		} catch (Exception ex) {

		}
		// Hashtable allStocks = new Hashtable();
		// allStocks.put("THO", "THO");
		Enumeration en2 = allStocks2.keys();
		while (en2.hasMoreElements()) {
			String symbol = en2.nextElement().toString();
			// SQLOperation.resetIBSPRecords(symbol);
			// CHECK CBIO REVISE ALGORITHM TO CATCH PTMA<0 --> PTMA>0 SCORE+40??
			// AWS FROM -12 TO +12 SCORE +20??
			// IBSP>=95, 100?? BUY SIGNAL??? BAC, NVDA, AMPE,IPGP,
			// +2% PBR, >=100? SELECT 1%, CHECK NSM WHAT LEARNED??BBSCORE
			// WEIGHT??
			// NO BBSCORE=3000, MORE DEDUCTION
			for (int k = endIndex; k >= startIndex; k--) {
				// for(int w=43191; w>=43026; w--){
				SQLOperation.scoreStockIBBS(symbol, k, true);
				System.out.println(k + " records done..." + symbol + " " + k
						+ " out of " + allStocks2.size());
			}
			// break;
		}

	}

	public static void preMain() {
		// TODO Auto-generated method stub
		// SQLOperation sqlOperation = new SQLOperation();
		// SQLOperation.markUpTrendHistory("NVDA");
		// SQLOperation.printOutDSIAvgTrend("SPY");
		String symbol = "VZ";
		// Hashtable allStocks = SQLOperation.getUncalculatedYahooStocks(43212);
		int updateIndex = 43227;// 23,26,27
		Hashtable allStocks = SQLOperation.getCurrentYahooStocks(updateIndex);
		System.out.println("All stock size " + allStocks.size());

		try {
			Thread.sleep(5000);
		} catch (Exception ex) {

		}
		// Hashtable allStocks = new Hashtable();
		// allStocks.put("THO", "THO");
		Enumeration en = allStocks.keys();
		int size = allStocks.size();

		int lc = 0;

		// CHECK SOXL, IBSP>CERTAIN VALUE(SAY 150), AND COULD NO MORE MAKE
		// 1% TO 2% HIGHER, THEN, IBSP RETURNS TO 0, THEN POSITIVE NUMBER
		// AGAIN, NO MATTER WHAT THE NUMBER IS, AS LONG AS AFTER THAT
		// IT MAKES 1% TO 2% HIGHER, THEN BUY POINT FOR HOGHER!!!
		// EVEN THE FIRST BUMP MAKES ABOVE 2%, BUT NOT MUCH, THEN QUCIKLY
		// FADED INTO SERVE LOSS, THEN THE NEXT IBPS POSITIVE IS A STRONG BUY
		// LOOK AT IPGP
		SQLOperation.overlayAllBBScore(updateIndex, true);

		SQLOperation.updateLastIBSSum(updateIndex, true);

		while (en.hasMoreElements()) {
			lc++;
			symbol = en.nextElement().toString();
			// SQLOperation.resetIBSPRecords(symbol);
			// CHECK CBIO REVISE ALGORITHM TO CATCH PTMA<0 --> PTMA>0 SCORE+40??
			// AWS FROM -12 TO +12 SCORE +20??
			// IBSP>=95, 100?? BUY SIGNAL??? BAC, NVDA, AMPE,IPGP,
			// +2% PBR, >=100? SELECT 1%, CHECK NSM WHAT LEARNED??BBSCORE
			// WEIGHT??
			// NO BBSCORE=3000, MORE DEDUCTION
			for (int w = updateIndex; w >= updateIndex; w--) {
				// for(int w=43191; w>=43026; w--){
				SQLOperation.scoreStockIBBS(symbol, w, true);
				System.out.println(w + " records done..." + symbol + " " + w
						+ " out of " + size);
			}
			// break;
		}
		System.out.println("AA records done...");
	}

}
