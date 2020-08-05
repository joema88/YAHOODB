package com.intraday.index;

import com.intraday.ptma.helper.Files;
import com.intraday.ptma.helper.HttpDownload;
import com.intraday.ptma.helper.YStock;
import com.intraday.ptma.sql.SQLOperation;
import java.lang.String;
import java.math.*;

import java.util.*;


//ATX-->^ATX, AEX-->^AEX,
public class Index {

	/**
	 * @param args
	 */

	/***
	 * 
	 * It looks like DSI5 negative at least, DSI24 less than 20 then when low
	 * turn up, or DSI5 plus DSI24 turn above 80 within 2 days of dsi5 turns
	 * positive (include) DSI+DSI5 needs to be less than 120 for the previous 8
	 * days continuousely then SPY will shot up in the next 10 to 20 days. Sell
	 * 10 contract $1 above strike call 2 to 3 weeks out then buy 11 contracts
	 * $2 below strike call 3 to 4 weeks out for example 11-07-2016
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// recalculateIndexHistory("SPY", 7);

		// The next two steps need to be merged into EODRunNew
		// Or Run after that method
		// calculateIndexPriceSqrtDelta("SPY");
		// recalculateIndexHistory("SPY",7);
		// SQLOperation.patternMatching("SPY", 11, 42652, "both");
		// DSIStableStatistics("SPY");

		// markDSIStableStart("SPY");
		// this one calculate gain according to real SPY data
		// calculateSPYDSIStableStatistics("SPY");
		// evaluateIndexStableStatics("^AORD");
		// evaluateIndexStableStatics("^OMX");
		// evaluateIndexStableStatics("^HSI");
		// evaluateIndexStableStatics("^KLSE");
		// evaluateIndexStableStatics("^BVSP");
		// evaluateIndexStableStatics("^AEX");
		// evaluateIndexStableStatics("^HSCE");
		// evaluateIndexStableStatics("^BSESN");
		// evaluateIndexStableStatics("^N225");
		// evaluateIndexStableStatics("^ATX");
		// evaluateIndexStableStatics("^SSMI");
		// evaluateIndexStableStatics("^KS11");
		// evaluateIndexAllStatics("^GDAXI");
		// evaluateIndexStableStatics("^GDAXI");
		// evaluateIndexAllStatics("SPY");
		// evaluateIndexStableStatics("^FCHI");
		// evaluateIndexStableStatics("^JKSE");

		// $$$ 3 to 4 days sum sum>12, all sum>0 (sum minimum)
		// and all sum>change, and max 1 change neg (orderly)
		// all changes must be below 3.5% (no violent change)
		// previous 10 (???, dynamic??) days sum sum <-10 (dip history)
		// SQLOperation.discoverTrendPoints("VZ");
		// SQLOperation.calculateTrendPointsHistory("BAC");
		// SQLOperation.calculateTrendPointsHistoryForAllStocks(42869);
		// SQLOperation.displayMulQueryInSingleRow("SPY");
		// SQLOperation.displayYieldsFittingCriteria("SPY");
		// SQLOperation.calculateEntireAVGLMDRHistory(42873);
		long t1 = System.currentTimeMillis();
		// SQLOperation.calculateEntireAVGLMDRHistory(42880);
		// SQLOperation.calculateIncDescTrendDays("MELI", 42878,false, 40);
		// SQLOperation.resetConnection();
		//SQLOperation.calculateIncDescTrendDaysHistory(42887,false);
		//SQLOperation.calculateIncDescTrendDays("NVDA", 42887,false,40);
		// SQLOperation.calculateIncDescTrendDaysHistory(42880);
		// SQLOperation.calculateDeltaAgainstSPY("NOC",42880,false,10);
		//SQLOperation.calculateDeltaAgainstSPYHistory(42880,false);
		//SQLOperation.scoreDipOp("AMD", 42886,false);
		//SQLOperation.calculateEntireDipScoreHistory(42887,false);
		//SQLOperation.getDPS4History("PPC",42887,false);
		//SQLOperation.calculateEntireDPS4History(42887,false);
		long t2 = System.currentTimeMillis();
		System.out.println("Time cost in seconds : " + (t2 - t1) / 1000);
		// SQLOperation.displayMulQueryInSingleRow("AMZN", "SPY");
		// findQualifiedSmallChangeDuration("SPY", "final",1.0f,10, -1);
		// DSI3 or DELTA1 filter
		// SQLOperation.qualifyIndexStatistics("SPY",5,0.8f,-0.8f);
		// SQLOperation.calculateDeltaStableStatistics("SPY");

		// Calculate DELTA1, 2, 3, 4, 5 filters to get more days delta
		// conditions
		// calculateIndexPriceSqrtDelta("SPY");

		// SQLOperation.calIndexSqrtDelta("^GDAXI");
		// SQLOperation.calculateSPYDSIStableStatistics("SPY");
		// SQLOperation.doubleQualifiedIndex("SPY");
		// SQLOperation.calculateDSIPlusDSI3StableStatistics("SPY");

	}

	public static void DSISyncDip(String symbol) {
		// it looks like whenever dsi5 is below zero or less than 10
		// at the same time DSI -DSi5 is less than 10, then an absolute low
		// point reached for the next 5 to 10 days
		// sell near the price point bull
		// interesting idea, but may need a few days to confirm bottom,
		// and not several negative days
	}

	public static void DSIStableStatistics(String symbol) {
		// $$$$$ this is for searching stable SPY period for the next 5 to 15
		// days
		// so that we could sell time spread on options, the following find
		// candidates
		SQLOperation.findDSIStableSP("SPY", 5, 200.0f, 10.0f, 100.0f, 10.0f,
				100.0f, 20.0f);
		// this one calculate gain according to real SPY data
		SQLOperation.calculateSPYDSIStableStatistics("SPY");

	}

	public static void findQualifiedSmallChangeDuration(String symbol,
			String mode, float maxChange, int durationDays, int endIndex) {
		SQLOperation.findStableIndex(symbol, mode, maxChange, durationDays,
				endIndex);
	}

	public static void evaluateIndexStableStatics(String symbol) {
		recalculateIndexHistory(symbol, -1);
		markDSIStableStart(symbol);
		calculateSPYDSIStableStatistics(symbol);
	}

	public static void calculateIndexPriceSqrtDelta(String symbol) {
		System.out.println("Start processing " + symbol);
		long t1 = System.currentTimeMillis();
		SQLOperation.calIndexSqrtDelta(symbol, 1);
		long t2 = System.currentTimeMillis();
		System.out.println("SqrtDelta one day delta finished, costs second "
				+ (t2 - t1) / 1000);
		t1 = System.currentTimeMillis();
		SQLOperation.calIndexSqrtDelta(symbol, 2);
		t2 = System.currentTimeMillis();
		System.out.println("SqrtDelta two day delta finished, costs second "
				+ (t2 - t1) / 1000);
		t1 = System.currentTimeMillis();
		SQLOperation.calIndexSqrtDelta(symbol, 3);
		t2 = System.currentTimeMillis();
		System.out.println("SqrtDelta three day delta finished, costs second "
				+ (t2 - t1) / 1000);
		t1 = System.currentTimeMillis();
		SQLOperation.calIndexSqrtDelta(symbol, 4);
		t2 = System.currentTimeMillis();
		System.out.println("SqrtDelta four day delta finished, costs second "
				+ (t2 - t1) / 1000);
		t1 = System.currentTimeMillis();
		SQLOperation.calIndexSqrtDelta(symbol, 5);
		t2 = System.currentTimeMillis();
		System.out.println("SqrtDelta five day delta finished, costs second "
				+ (t2 - t1) / 1000);

	}

	public static void evaluateIndexAllStatics(String symbol) {
		SQLOperation.calculateALLSPYStatistics(symbol);
	}

	public static Hashtable initIndexes() {
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
		return includedSymbols;

	}

	public static void calculateSPYDSIStableStatistics(String symbol) {
		SQLOperation.calculateDeltaStableStatistics(symbol);
	}

	public static void markDSIStableStart(String symbol) {
		// $$$$$ this is for searching stable SPY period for the next 5 to 15
		// days
		// so that we could sell time spread on options, the following find
		// candidates

		// 5days peroid, DSI+DSI5 sum is around 200 with 20%+- range
		// then each day value is less than 10%+- from AVG
		// DSI with value around 100, with 20%+- range
		// then each day value is less than 10%+- from AVG
		// DSI5 around 100, with 20%+- range
		// then each day value is less than 20%+- from AVG
		SQLOperation.findDSIStableSP("SPY", 5, 200.0f, 10.0f, 100.0f, 10.0f,
				100.0f, 20.0f);
		System.out.println("DSI Stable points marked...");

	}

	public static void recalculateIndexHistory(String symbol, int step) {
		System.out.println("Start processing " + symbol);
		long t1 = System.currentTimeMillis();
		if (step == 1 || step < 0)
			SQLOperation.calculateIndexSP(symbol);
		long t2 = System.currentTimeMillis();
		System.out
				.println("Index " + symbol
						+ " history step1 done. Time cost seconds: "
						+ (t2 - t1) / 1000);
		t1 = System.currentTimeMillis();
		if (step == 2 || step < 0)
			SQLOperation.calculateIndexTMASP(symbol);
		t2 = System.currentTimeMillis();
		System.out
				.println("Index " + symbol
						+ " history step2 done. Time cost seconds: "
						+ (t2 - t1) / 1000);
		t1 = System.currentTimeMillis();
		if (step == 3 || step < 0)
			SQLOperation.calculateIndexTMAVSP(symbol);
		t2 = System.currentTimeMillis();
		System.out
				.println("Index " + symbol
						+ " history step3 done. Time cost seconds: "
						+ (t2 - t1) / 1000);
		t1 = System.currentTimeMillis();
		if (step == 4 || step < 0)
			SQLOperation.calculateIndexPTMASP(symbol);
		t2 = System.currentTimeMillis();
		System.out
				.println("Index " + symbol
						+ " history step4 done. Time cost seconds: "
						+ (t2 - t1) / 1000);
		if (step == 5 || step < 0)

			SQLOperation.calculateIndexPTMAVSP(symbol);
		t2 = System.currentTimeMillis();
		System.out
				.println("Index " + symbol
						+ " history step5 done. Time cost seconds: "
						+ (t2 - t1) / 1000);
		t1 = System.currentTimeMillis();
		if (step == 6 || step < 0)

			SQLOperation.calculateIndexDSISP(symbol, 5);
		t2 = System.currentTimeMillis();
		System.out
				.println("Index " + symbol
						+ " history step6 done. Time cost seconds: "
						+ (t2 - t1) / 1000);
		if (step == 7 || step < 0)
			t1 = System.currentTimeMillis();
		SQLOperation.calculateIndexDSISP(symbol, 24);
		t2 = System.currentTimeMillis();
		System.out
				.println("Index " + symbol
						+ " history step7 done. Time cost seconds: "
						+ (t2 - t1) / 1000);

	}
}
