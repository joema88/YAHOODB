package com.yahoo.ptma.sql;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Enumeration;

import com.yahoo.ptma.helper.Files;

public class TestFunction {
	// BUY ATEC,EBIO,AKG, TDW,SSW,IO
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Files.createEODTagFile("U2RecalStart.txt");
		// SQLOperation.recalAllU2History(43339);
		Calendar cal = Calendar.getInstance();
		int a = cal.get(Calendar.DAY_OF_WEEK);
	System.out.println("A is "+a);
		String symbol = "TNDM";
		int endIndex = 43377;
		
		for(int k=1; k<=6; k++){
			float rate = 1.0f+0.05f*k;
			float yield = 1.0f;
			for(int i=1; i<=12; i++){
				yield = yield*rate;
			}
			
			System.out.println("MONTHLY yeild "+5*k+"% then annaul yeild is "+yield);
		}
		//SQLOperation.getMegaUTurnBatch(43377);
		//SQLOperation.getMegaUTurn(endIndex);
			//TRY CBAY, LLY,BXC	
		//SQLOperation.distributeMegaUTurnBatch(endIndex);
		//System.out.println("Done");
	//	for (int w = 42028; w <= 43377; w++) {
	//	SQLOperation.getMegaUTurnGood1(w, "LLY");
		
		//SQLOperation.
			//SQLOperation.getIPOBulls(w);
			//SQLOperation.getMegaUTurn(w); 
	//		System.out.println(w+" done ");
	//	}
		// SQLOperation.markHL4Result(symbol, endIndex);
		// SQLOperation.calculateCBullHistoryNew(symbol, endIndex,true);
		// SQLOperation.calculateCBullHistoryNew(symbol, endIndex,false);
		// SQLOperation.calculateHL4History(symbol, endIndex);
		// SQLOperation.recalculateEntireHL4History(43363);
		Files.createEODTagFile("U2RecalDone.txt");

		/*
		 * SQLOperation.calculateU2History2("TNDM",43339);
		 * System.out.println("DOne"); try { Thread.sleep(500000000); } catch
		 * (Exception ex) {
		 * 
		 * } /* // SQLOperation.scoreBullPoint("AMZN", 43261, 43261); Hashtable
		 * allStocks = SQLOperation.getCurrentYahooStocks(43335); //
		 * allStocks.put("HTBX", "HTBX"); Enumeration en2 = allStocks.keys();
		 * int size = allStocks.size(); int lc = 0; while
		 * (en2.hasMoreElements()) { String stockSymbol =
		 * en2.nextElement().toString();
		 * SQLOperation.calculateU2MileStone(stockSymbol, 43335); lc++;
		 * System.out.println(lc+" out of "+size+" done..."+stockSymbol); }
		 * System.out.println("dONE...");
		 * 
		 * try { Thread.sleep(500000000); } catch (Exception ex) {
		 * 
		 * } // UPDATE 3/2/2018 TO 5/22/2018 RECORDS //
		 * updateEODIBBSetc(43249,43249);
		 * Files.createEODTagFile("IBSPStart.txt"); updateIBSPOnly(43023,
		 * 43251); Files.createEODTagFile("IBSPDone.txt");
		 */
	}

	public static void updateIBSPOnly(int startIndex, int endIndex) {
		Hashtable allStocks = SQLOperation.getIBBSExistStocks(startIndex,
				endIndex);
		// allStocks.put("HTBX", "HTBX");
		Enumeration en2 = allStocks.keys();
		System.out.println("Stock size " + allStocks.size());
		Files.appendToFile("C:\\stock\\yahoo\\IBSPStart.txt", "Stock size "
				+ allStocks.size());
		try {
			Thread.sleep(5000);
		} catch (Exception ex) {

		}

		int lc = 0;
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
				SQLOperation.overlayAllBBScore(k, true);

				SQLOperation.updateLastIBSSum(k, true);

				SQLOperation.scoreStockIBBS(symbol, k, true);
				System.out.println(k + " records done..." + symbol + " " + lc
						+ " out of " + allStocks.size());

			}
			try {
				Thread.sleep(5000);
			} catch (Exception ex) {

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
		SQLOperation.scoreBullPoint("AMZN", 43034, 43193);
		System.out.println("dONE...");

		try {
			Thread.sleep(50000000);
		} catch (Exception ex) {

		}
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
