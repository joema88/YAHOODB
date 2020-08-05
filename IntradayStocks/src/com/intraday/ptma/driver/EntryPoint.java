package com.intraday.ptma.driver;

import java.util.Hashtable;
import java.util.Enumeration;

import com.intraday.ptma.sql.SQLOperation;

public class EntryPoint {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// CASE1(10644);
		// CASE2(10626);
		// CASE3(10626);
		// CASE4(10626);
		// CASE5(10626);
		// CASE6(10676);
		// CASE7(10677);
		// CASE8(10677);
		// CASE9(10677);
		//CASE10(10677);
		// CASE11(10677);
		// CASE12(10677);
		 //CASE13(10677);
		//CASE14(10677);
		CASE15(10677);

	}

	// 20 billion
	/*
	 * Total occurence 71.0 Average one year gain 24.400406 Average max gain
	 * 54.716423 Average max loss -19.878767 Finish processing case 1, total
	 * time cost in hours 0.055221666
	 */

	// 50 BILLION
	/*
	 * Total occurence 54 Total aboveTenPercentage number 45 = 83.33% Total
	 * aboveTwentyPercentage number 39 = 72.2% Total aboveThirtyPercentage
	 * number 32 = 59.26% Total aboveFourtyPercentage number 27 = 50% Total
	 * aboveOnehundrePercentage number 8 = 14.8% Average one year gain 35.0696
	 * Average max gain 126.568756 Average max loss -17.886116 Finish processing
	 * case 1, total time cost in hours 0.052929446
	 */
	public static void CASE1(int seqIndex) {
		long t1 = System.currentTimeMillis();
		System.out.println("Start processing case 1");

		Hashtable ystocks1 = SQLOperation.getCurrentAllStocks(seqIndex);
		Hashtable ystocks = SQLOperation.getCurrentVSTBigCapStocks(seqIndex,
				50000);
		SQLOperation.resetStats(seqIndex);
		Enumeration en = ystocks.keys();

		// CASE 1 FOR 80 STOCKS DOWNLOADED 9/30/2015
		// RX100<0 AND PTMA>0 CORRECTION CASE, GAP>20 DAYS
		// Total occurence 63.0
		// Average one year gain 23.45348
		// Average max gain 58.208775
		// Average max loss -20.868399

		/*
		 * total occurence 2333.0 Average one year gain 20.514599 Average max
		 * gain 114.42618 Average max loss -19.10283
		 */

		// sample data 3027 at 10620, INTRADAYSTOCKS back to beginning of each stock
		/*
		 * Start processing case 1 Total occurence 2940.0 Average one year gain
		 * 20.581875 Average max gain 102.452805 Average max loss -19.995535
		 * Finish processing case 1, total time cost in hours 1.1976347
		 */

		// Start processing case 1
		// Total occurence 4253.0
		// Average one year gain 20.387403
		// Average max gain 90.37592
		// Average max loss -21.739223
		// Finish processing case 1, total time cost in hours 3.2809846
		// SEEMS MOST PROMISING ONE, LET'S DO MORE DETAILED ANALYSIS
		// AND USE SUM(PTMA) AVERAGE DURING GR100 NEGATIVE >5.0F AS STANDARD
		// INSTEAD OF ALL POSITIVE,GAP>=18?LNKD

		int loopCount = 0;
		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			if (ystocks1.containsKey(symbol)) {

				int[] pairs = SQLOperation.findBullEntryPoint(symbol);
				SQLOperation.evaluateConditions(symbol, pairs);
				System.out.println("********  done ***********");
				System.out.println("  ");

				loopCount++;
				if (loopCount % 100 == 0)
					try {
						break;
						// Thread.sleep(15000);
					} catch (Exception ex) {

					}
			}
		}

		SQLOperation.printAverageSummary();

		long t2 = System.currentTimeMillis();
		System.out
				.println("Finish processing case 1, total time cost in hours "
						+ ((t2 - t1) * 1.0f) / (1000 * 60 * 60 * 1.0f));

	}

	public static void CASE2(int seqIndex) {

		long t1 = System.currentTimeMillis();
		System.out.println("Start processing case 2");

		Hashtable ystocks = SQLOperation.getCurrentAllStocks(seqIndex);
		SQLOperation.resetStats(seqIndex);

		Enumeration en = ystocks.keys();

		// CASE 2, FFP>0 WAITING PEROID IN BETWEEN>150 (OR 200) TRADING DAYS
		// FOR 80 STOCKS DOWNLOADED 9/30/2015
		// Total occurence 605.0
		// Average one year gain 29.10255
		// Average max gain 53.10154
		// Average max loss -15.489833

		/*
		 * Total occurence 12991.0 Average one year gain 23.299597 Average max
		 * gain 46.02623 Average max loss -16.375248
		 */
		/*
		 * Start processing case 2 Total occurence 16226.0 Average one year gain
		 * 27.128828 Average max gain 48.093853 Average max loss -17.335989
		 * Finish processing case 2, total time cost in hours 6.821949
		 */
		int loopCount = 0;

		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			int[] options = SQLOperation
					.findBullPointAfterLongCorrection(symbol);

			for (int k = 0; k < options.length; k++) {
				int buyIndex = options[k];
				if (buyIndex == -20000) {
					break;
				} else {
					SQLOperation.evaluateGain(symbol, buyIndex);
				}
			}
			loopCount++;
			if (loopCount % 100 == 0)
				try {
					Thread.sleep(15000);
				} catch (Exception ex) {

				}
		}
		SQLOperation.printAverageSummary();

		long t2 = System.currentTimeMillis();
		System.out
				.println("Finish processing case 2, total time cost in hours "
						+ ((t2 - t1) * 1.0f) / (1000 * 60 * 60 * 1.0f));

	}

	public static void CASE3(int seqIndex) {
		long t1 = System.currentTimeMillis();
		System.out.println("Start processing case 3");

		Hashtable ystocks = SQLOperation.getCurrentAllStocks(seqIndex);
		SQLOperation.resetStats(seqIndex);

		Enumeration en = ystocks.keys();

		// Total occurence 2540.0
		/*
		 * Average one year gain 21.168674 Average max gain 46.39999 Average max
		 * loss -20.837873
		 */
		/*
		 * Start processing case 3 Total occurence 3148.0 Average one year gain
		 * 160.3951 Average max gain 47.099052 Average max loss -21.696144
		 * Finish processing case 3, total time cost in hours 6.7591944
		 */
		int loopCount = 0;
		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			int[] options = SQLOperation
					.findBullPointAfterRiseCorrection(symbol);

			for (int k = 0; k < options.length; k++) {
				int buyIndex = options[k];
				if (buyIndex == -20000) {
					break;
				} else {
					SQLOperation.evaluateGain(symbol, buyIndex);
				}
			}

			loopCount++;
			if (loopCount % 100 == 0)
				try {
					Thread.sleep(15000);
				} catch (Exception ex) {

				}
		}
		SQLOperation.printAverageSummary();

		long t2 = System.currentTimeMillis();
		System.out
				.println("Finish processing case 3, total time cost in hours "
						+ ((t2 - t1) * 1.0f) / (1000 * 60 * 60 * 1.0f));

	}

	// 10/15/2015
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
	public static void CASE4(int seqIndex) {

		/*
		 * big cap only Total occurence 508.0 Average one year gain 21.947952
		 * Average max gain 41.433254 Average max loss -15.0646 Finish
		 * processing case 2, total time cost in hours 0.4322311
		 */

		long t1 = System.currentTimeMillis();
		System.out.println("Start processing case 4...");

		Hashtable ystocks = SQLOperation.getCurrentAllStocks(seqIndex);
		Hashtable bigCaps = SQLOperation.getCurrentVSTBigCapStocks(seqIndex,
				2000);
		SQLOperation.resetStats(seqIndex);

		Enumeration en = ystocks.keys();

		int loopCount = 0;

		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			// if (symbol.equalsIgnoreCase("MNKD")) {
			if (bigCaps.contains(symbol)) {
				int[] options = SQLOperation
						.findBullPointAfterProperLongCorrection(symbol, 190);

				for (int k = 0; k < options.length; k++) {
					int buyIndex = options[k];
					if (buyIndex == -20000) {
						break;
					} else {
						SQLOperation.evaluateGain(symbol, buyIndex);
					}
				}
				loopCount++;
				if (loopCount % 100 == 0)
					try {
						Thread.sleep(15000);
						break;
					} catch (Exception ex) {

					}
			}

		}
		SQLOperation.printAverageSummary();

		long t2 = System.currentTimeMillis();
		System.out
				.println("Finish processing case 2, total time cost in hours "
						+ ((t2 - t1) * 1.0f) / (1000 * 60 * 60 * 1.0f));

	}

	// 10/16/2015, SAMPLE DATA
	// Total occurence 10012.0 ptma>50, ONE YEAR HOLD
	// Average one year gain 31.688412
	// Average max gain 83.075066
	// Average max loss -27.59807
	// Finish processing case 5, total time cost in hours 2.5564733
	// MUST BE ONE YEAR ENTIRE
	/*
	 * Total occurence 10012.0 Average one year gain 31.688412 Average max gain
	 * 81.578896 Average max loss -26.673582 Finish processing case 5, total
	 * time cost in hours 3.109056
	 */

	// PTMA>100 AT LEAST 200 DAYS GAP, BUY NEXT DAY,
	/*
	 * Total occurence 2509.0 Average one year gain 4619.6836 Average max gain
	 * 5240.7734 Average max loss -34.629063 Finish processing case 5, total
	 * time cost in hours 2.6295152
	 */
	public static void CASE5(int seqIndex) {
		long t1 = System.currentTimeMillis();
		System.out.println("Start processing case 5");

		Hashtable ystocks = SQLOperation.getCurrentAllStocks(seqIndex);
		SQLOperation.resetStats(seqIndex);
		Enumeration en = ystocks.keys();

		int loopCount = 0;
		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			// if (symbol.equalsIgnoreCase("JKS")) {
			int[] options = SQLOperation.findBullEntryPointAfterRise(symbol);

			for (int k = 0; k < options.length; k++) {
				int buyIndex = options[k];
				if (buyIndex == -20000) {
					break;
				} else {
					SQLOperation.evaluateGain(symbol, buyIndex + 1);
				}
			}
			loopCount++;
			if (loopCount % 100 == 0)
				try {
					Thread.sleep(15000);
					// break;
				} catch (Exception ex) {

				}
		}
		// }
		SQLOperation.printAverageSummary();

		long t2 = System.currentTimeMillis();
		System.out
				.println("Finish processing case 5, total time cost in hours "
						+ ((t2 - t1) * 1.0f) / (1000 * 60 * 60 * 1.0f));

	}

	// CASE 6: ACPTMA>0, OR PROPER CORRECTION MAY BE AN INDICATOR OF UPTREND
	// ON CONTRARY, ACPTMA<0 MAY BE A BEAR INDICATOR

	/*
	 * FROM 10676 TO 10300 RANGE Total occurence 1766 Total aboveTenPercentage
	 * number 1095 Total aboveTwentyPercentage number 649 Total
	 * aboveThirtyPercentage number 409 Total aboveFourtyPercentage number 283
	 * Total aboveOnehundrePercentage number 56 Average one year gain 5.088595
	 * Average max gain 48.498856 Average max loss -14.724503 Finish processing
	 * case 5, total time cost in hours 0.28318554
	 */

	// FFP=1 AT index (index-index2)<100 && acptma<-15, acptma AT INDEX2
	/*
	 * Total occurence 45 Total aboveTenPercentage number 29 Total
	 * aboveTwentyPercentage number 22 Total aboveThirtyPercentage number 19
	 * Total aboveFourtyPercentage number 14 Total aboveOnehundrePercentage
	 * number 4 Average one year gain -14.789428 Average max gain 73.73746
	 * Average max loss -40.13372 Finish processing case 7, total time cost in
	 * hours 0.36001
	 */
	// if((index-index2)<50&&acptma<-10){
	// if((index-index2)<50&&acptma<-10), INDEX>10300

	/*
	 * Total occurence 35 Total aboveTenPercentage number 26 Total
	 * aboveTwentyPercentage number 21 Total aboveThirtyPercentage number 18
	 * Total aboveFourtyPercentage number 14 Total aboveOnehundrePercentage
	 * number 5 Average one year gain -11.408409 Average max gain 106.59954
	 * Average max loss -35.086132 Finish processing case 7, total time cost in
	 * hours 0.36454472
	 */

	/*
	 * Total occurence 1877 if((index-index2)<50&&acptma<-15){, INDEX>8000 Total
	 * aboveTenPercentage number 1570 Total aboveTwentyPercentage number 1372
	 * Total aboveThirtyPercentage number 1153 Total aboveFourtyPercentage
	 * number 973 Total aboveOnehundrePercentage number 402 Average one year
	 * gain 25.514076 Average max gain 150.72252 Average max loss -28.693642
	 * Finish processing case 7, total time cost in hours 6.8625054
	 */

	// ADJUSTEDPRICE>10
	// if((index-index2)<120&&acptma<-15){
	// W>7000
	//
	/*
	 * Total occurence 2939 Total aboveTenPercentage number 2453 Total
	 * aboveTwentyPercentage number 1978 Total aboveThirtyPercentage number 1468
	 * Total aboveFourtyPercentage number 1066 Total aboveOnehundrePercentage
	 * number 213 Average one year gain 8.884389 Average max gain 84.86055
	 * Average max loss -23.179892 Finish processing case 7, total time cost in
	 * hours 5.6978326
	 */

	public static void CASE6(int seqIndex) {
		long t1 = System.currentTimeMillis();
		System.out.println("Start processing case 6");

		Hashtable ystocks = SQLOperation.getCurrentAllStocks(seqIndex);
		SQLOperation.resetStats(seqIndex);

		int loopCount = 0;
		for (int w = seqIndex - 251; w > 10300; w--) {
			Hashtable stocks = SQLOperation.getProperCorrectionStocks(w);
			Enumeration en = stocks.keys();
			while (en.hasMoreElements()) {
				String symbol = en.nextElement().toString();
				if (ystocks.containsKey(symbol)) {
					SQLOperation.evaluateGain(symbol, w + 1);
					loopCount++;

					if (loopCount % 100 == 0)
						try {
							Thread.sleep(15000);
							// break;
						} catch (Exception ex) {

						}
				}
			}
		}
		// }
		SQLOperation.printAverageSummary();

		long t2 = System.currentTimeMillis();
		System.out
				.println("Finish processing case 6, total time cost in hours "
						+ ((t2 - t1) * 1.0f) / (1000 * 60 * 60 * 1.0f));

	}

	// CASE 7 STUDY: ACPTMA<0 (OR ACPTMA<-10) AND FFP>0 within 100 days DOWN THE
	// ROAD
	// MAY BE A U-TURN INDICATOR

	// MARKET CAP>5000, W>7000
	// if ((index - index2) < 120 && acptma < -15
	// && getStockCap(symbol, price) > 5000.0f) {
	/*
	 * Total occurence 99 Total aboveTenPercentage number 73 Total
	 * aboveTwentyPercentage number 52 Total aboveThirtyPercentage number 37
	 * Total aboveFourtyPercentage number 28 Total aboveOnehundrePercentage
	 * number 7 Average one year gain 3.3018548 Average max gain 36.81 Average
	 * max loss -30.322983 Finish processing case 7, total time cost in hours
	 * 3.7809854
	 */

	/*
	 * if ((index - index2) < 120 && acptma < -15 && getStockCap(symbol, price)
	 * > 1000.0f && getStockCap(symbol, price) < 5000.0f) {
	 */
	/*
	 * Total occurence 226 Total aboveTenPercentage number 174 Total
	 * aboveTwentyPercentage number 137 Total aboveThirtyPercentage number 100
	 * Total aboveFourtyPercentage number 79 Total aboveOnehundrePercentage
	 * number 23 Average one year gain 2.7988772 Average max gain 44.465034
	 * Average max loss -30.942646 Finish processing case 7, total time cost in
	 * hours 3.8548095
	 */

	// ADJUSTEDPRICE>10 and volume>350000
	// (index - index2) < 50 && acptma < -5
	// && getStockCap(symbol, price) > 1000.0f
	// && getStockCap(symbol, price) < 5000.0f
	// Total occurence 302
	// Total aboveTenPercentage number 251
	// Total aboveTwentyPercentage number 198
	// Total aboveThirtyPercentage number 153
	// Total aboveFourtyPercentage number 124
	// Total aboveOnehundrePercentage number 34
	// Average one year gain 11.744455
	// Average max gain 46.923935
	// Average max loss -27.086569
	// Finish processing case 7, total time cost in hours 2.1253397

	public static void CASE7(int seqIndex) {
		long t1 = System.currentTimeMillis();
		System.out.println("Start processing case 7");

		// Hashtable ystocks = SQLOperation.getCurrentYahooStocks(seqIndex);
		SQLOperation.resetStats(seqIndex);

		int loopCount = 0;
		for (int w = seqIndex - 251; w > 5000; w--) {
			Hashtable stocks = SQLOperation.getUturnStocks(w);
			Enumeration en = stocks.keys();
			while (en.hasMoreElements()) {
				String symbol = en.nextElement().toString();
				// if (ystocks.containsKey(symbol)) {
				SQLOperation.evaluateGain(symbol, w + 1);
				loopCount++;

				if (loopCount % 100 == 0)
					try {
						Thread.sleep(15000);
						// break;
					} catch (Exception ex) {

					}
				// }
			}
		}
		// }
		SQLOperation.printAverageSummary();

		long t2 = System.currentTimeMillis();
		System.out
				.println("Finish processing case 7, total time cost in hours "
						+ ((t2 - t1) * 1.0f) / (1000 * 60 * 60 * 1.0f));

	}

	// ALTERNATIVELY,
	// ADJUSTEDPRICE>10,FFP=1 AT INDEX, ACPTMA>5 AT INDEX2
	// if((index-index2)<80&&acptma>5){, THEORY IS THAT AFTER PROPER CORRECTION
	// YOU SHOULD RAMP UP QUICK IN ORDER TO GO HIGHER
	// W>7000
	/*
	 * Total occurence 7689 Total aboveTenPercentage number 6036 Total
	 * aboveTwentyPercentage number 4629 Total aboveThirtyPercentage number 3387
	 * Total aboveFourtyPercentage number 2451 Total aboveOnehundrePercentage
	 * number 454 Average one year gain 7.462386 Average max gain 74.605835
	 * Average max loss -23.0863 Finish processing case 8, total time cost in
	 * hours 6.3261347
	 */
	public static void CASE8(int seqIndex) {
		long t1 = System.currentTimeMillis();
		System.out.println("Start processing case 8");

		Hashtable ystocks = SQLOperation.getCurrentAllStocks(seqIndex);
		SQLOperation.resetStats(seqIndex);

		int loopCount = 0;
		for (int w = seqIndex - 251; w > 7000; w--) {
			Hashtable stocks = SQLOperation.getUpUpStocks(w);
			Enumeration en = stocks.keys();
			while (en.hasMoreElements()) {
				String symbol = en.nextElement().toString();
				if (ystocks.containsKey(symbol)) {
					SQLOperation.evaluateGain(symbol, w + 1);
					loopCount++;

					if (loopCount % 100 == 0)
						try {
							Thread.sleep(15000);
							// break;
						} catch (Exception ex) {

						}
				}
			}
		}
		// }
		SQLOperation.printAverageSummary();

		long t2 = System.currentTimeMillis();
		System.out
				.println("Finish processing case 8, total time cost in hours "
						+ ((t2 - t1) * 1.0f) / (1000 * 60 * 60 * 1.0f));

	}

	// quick turn
	public static void CASE9(int seqIndex) {
		long t1 = System.currentTimeMillis();
		System.out.println("Start processing case 9");

		Hashtable ystocks = SQLOperation.getCurrentAllStocks(seqIndex);
		SQLOperation.resetStats(seqIndex);

		int loopCount = 0;
		for (int w = seqIndex - 251; w > 9000; w--) {
			Hashtable stocks = SQLOperation.getQuickTurnStocks(w);
			Enumeration en = stocks.keys();
			while (en.hasMoreElements()) {
				String symbol = en.nextElement().toString();
				if (ystocks.containsKey(symbol)) {
					SQLOperation.evaluateGain(symbol, w + 1);
					loopCount++;

					if (loopCount % 100 == 0)
						try {
							Thread.sleep(15000);
							// break;
						} catch (Exception ex) {

						}
				}
			}
		}
		// }
		SQLOperation.printAverageSummary();

		long t2 = System.currentTimeMillis();
		System.out
				.println("Finish processing case 9, total time cost in hours "
						+ ((t2 - t1) * 1.0f) / (1000 * 60 * 60 * 1.0f));

	}

	// GET UBER BULL, PTMA>50.0 &&ADJUSTEDPRICE>1, and count (PTMA>35%)==0 FOR
	// FIRST 120 DAYS (AT LEAST)
	/*
	 * Total occurence 807 Total aboveTenPercentage number 646 Total
	 * aboveTwentyPercentage number 588 Total aboveThirtyPercentage number 529
	 * Total aboveFourtyPercentage number 478 Total aboveOnehundrePercentage
	 * number 262 Average one year gain 71.46976 Average max gain 173.83783
	 * Average max loss -33.970364 Finish processing case 10, total time cost in
	 * hours 3.4841392
	 */

	// PTMA>50.0, 120 COUNT(PTMA>50.0)==0, MARKETCAP>5000
	/*
	 * Total occurence 236 Total aboveTenPercentage number 188 Total
	 * aboveTwentyPercentage number 161 Total aboveThirtyPercentage number 130
	 * Total aboveFourtyPercentage number 102 Total aboveOnehundrePercentage
	 * number 25 Average one year gain 10.271029 Average max gain 46.132927
	 * Average max loss -28.100632 Finish processing case 10, total time cost in
	 * hours 1.3098072
	 */

	// MARKETCAP 500M TO 5B
	// PTMA>50.0, 120 COUNT(PTMA>50.0)==0
	/*
	 * Total occurence 898 Total aboveTenPercentage number 728 Total
	 * aboveTwentyPercentage number 596 Total aboveThirtyPercentage number 492
	 * Total aboveFourtyPercentage number 405 Total aboveOnehundrePercentage
	 * number 140 Average one year gain 12.908201 Average max gain 57.439316
	 * Average max loss -30.6605 Finish processing case 10, total time cost in
	 * hours 1.3853619
	 */

	// GET UBER BULL, PTMA>50.0 &&ADJUSTEDPRICE>1, and count (PTMA>35%)==0 FOR
	// LAST 120 DAYS
	// TEST DATA RANGE W>5000 AND W<10677-250
	/*
	 * Total occurence 1310 Total aboveTenPercentage number 1046 Total
	 * aboveTwentyPercentage number 936 Total aboveThirtyPercentage number 841
	 * Total aboveFourtyPercentage number 757 Total aboveOnehundrePercentage
	 * number 409 Average one year gain 108.48518 Average max gain 275.47186
	 * Average max loss -36.47202 Finish processing case 10, total time cost in
	 * hours 4.1919785
	 */

	// repeat calculation,
	// GET UBER BULL, PTMA>50.0 &&ADJUSTEDPRICE>1, and count (PTMA>35%)==0 FOR
	// LAST 120 DAYS
	// TEST DATA RANGE W>5000 AND W<10677-250
	/*
	 * Total occurence 1307 Total aboveTenPercentage number 1043 Total
	 * aboveTwentyPercentage number 933 Total aboveThirtyPercentage number 838
	 * Total aboveFourtyPercentage number 754 Total aboveOnehundrePercentage
	 * number 406 Average one year gain 108.59111 Average max gain 269.8118
	 * Average max loss -36.382294 Finish processing case 10, total time cost in
	 * hours 4.246811
	 */

	// GET UBER BULL, PTMA>50.0 &&ADJUSTEDPRICE>1, and count (PTMA>50%)==0 FOR
	// LAST 120 DAYS
	// TEST DATA RANGE W>5000 AND W<10677-250
	/*
	 * Total occurence 10611 Total aboveTenPercentage number 8774 Total
	 * aboveTwentyPercentage number 7542 Total aboveThirtyPercentage number 6441
	 * Total aboveFourtyPercentage number 5500 Total aboveOnehundrePercentage
	 * number 2304 Average one year gain 33.981106 Average max gain 96.64638
	 * Average max loss -28.52711 Finish processing case 10, total time cost in
	 * hours 5.27265
	 */

	// PTMA>50.0,ADJUSTEDPRICE>10 AND VOLUME>350000
	// and count (PTMA>35%)==0 FOR LAST 120 DAYS, MARKET CAP >1000 M
	// TEST DATA RANGE W>5000 AND W<10677-250
	/*
	 * Total occurence 98 Total aboveTenPercentage number 78 Total
	 * aboveTwentyPercentage number 63 Total aboveThirtyPercentage number 56
	 * Total aboveFourtyPercentage number 49 Total aboveOnehundrePercentage
	 * number 21 Average one year gain 6.7118998 Average max gain 71.04941
	 * Average max loss -35.346104 Finish processing case 10, total time cost in
	 * hours 0.93224055
	 */

	// PTMA>50.0,ADJUSTEDPRICE>10 AND VOLUME>350000
	// and count (PTMA>50%)==0 FOR LAST 120 DAYS, MARKET CAP >1000 M
	// TEST DATA RANGE W>5000 AND W<10677-250
	// Total occurence 1099
	// Total aboveTenPercentage number 883
	// Total aboveTwentyPercentage number 715
	// Total aboveThirtyPercentage number 574
	// Total aboveFourtyPercentage number 474
	// Total aboveOnehundrePercentage number 133
	// Average one year gain 12.21213
	// Average max gain 52.889225
	// Average max loss -29.027029
	// Finish processing case 10, total time cost in hours 0.77515197

	// PTMA>40.0,ADJUSTEDPRICE>10 AND VOLUME>350000
	// and count (PTMA>40%)==0 FOR LAST 120 DAYS, MARKET CAP >1000 M
	// TEST DATA RANGE W>5000 AND W<10677-250
	/*
	 * Total occurence 1401 Total aboveTenPercentage number 1098 Total
	 * aboveTwentyPercentage number 886 Total aboveThirtyPercentage number 698
	 * Total aboveFourtyPercentage number 529 Total aboveOnehundrePercentage
	 * number 124 Average one year gain 9.942234 Average max gain 44.51905
	 * Average max loss -26.323097 Finish processing case 10, total time cost in
	 * hours 1.1799989
	 */

	// PTMA>30.0,ADJUSTEDPRICE>10 AND VOLUME>350000
	// and count (PTMA>30%)==0 FOR LAST 120 DAYS, MARKET CAP >1000 M
	// TEST DATA RANGE W>5000 AND W<10677-250
	/*
	 * Total occurence 1840 Total aboveTenPercentage number 1481 Total
	 * aboveTwentyPercentage number 1110 Total aboveThirtyPercentage number 846
	 * Total aboveFourtyPercentage number 626 Total aboveOnehundrePercentage
	 * number 125 Average one year gain 10.397049 Average max gain 39.95045
	 * Average max loss -23.006634 Finish processing case 10, total time cost in
	 * hours 1.7979028
	 */

	// PTMA>50.0,ADJUSTEDPRICE>10 AND VOLUME>500000
	// and count (PTMA>50%)==0 FOR LAST 120 DAYS, MARKET CAP >1000 M
	// TEST DATA RANGE W>5000 AND W<10677-250

	// Total occurence 1071
	// Total aboveTenPercentage number 862
	// Total aboveTwentyPercentage number 703
	// Total aboveThirtyPercentage number 562
	// Total aboveFourtyPercentage number 463
	// Total aboveOnehundrePercentage number 128
	// Average one year gain 12.29462
	// Average max gain 51.740047
	// Average max loss -28.707878
	// Finish processing case 10, total time cost in hours 0.69144696

	// PTMA>50.0,ADJUSTEDPRICE>10 AND VOLUME>500000 AND GR100<60
	// and count (PTMA>50%)==0 FOR LAST 120 DAYS, MARKET CAP >1000 M
	// TEST DATA RANGE W>5000 AND W<10677-250

	// Total occurence 474
	// Total aboveTenPercentage number 387
	// Total aboveTwentyPercentage number 314
	// Total aboveThirtyPercentage number 264
	// Total aboveFourtyPercentage number 216
	// Total aboveOnehundrePercentage number 64
	// Average one year gain 11.253316
	// Average max gain 60.438713
	// Average max loss -33.094093
	// Finish processing case 10, total time cost in hours 0.15022695

	// PTMA>50.0,ADJUSTEDPRICE>10 AND VOLUME>500000 AND GR100<60
	// and count (PTMA>50%)==0 FOR LAST 80 DAYS, MARKET CAP >1000 M
	// TEST DATA RANGE W>5000 AND W<10677-250

	// Total occurence 531
	// Total aboveTenPercentage number 430
	// Total aboveTwentyPercentage number 354
	// Total aboveThirtyPercentage number 292
	// Total aboveFourtyPercentage number 243
	// Total aboveOnehundrePercentage number 78
	// Average one year gain 9.535094
	// Average max gain 59.904625
	// Average max loss -34.01293
	// Finish processing case 10, total time cost in hours 0.12857139
	
	// PTMA>50.0,ADJUSTEDPRICE>10 AND VOLUME>500000 AND GR100<60
	// and count (PTMA>50%)==0 FOR LAST 120 DAYS, MARKET CAP >5000 M
	// TEST DATA RANGE W>5000 AND W<10677-250
	
//	Total occurence 147
//	Total  aboveTenPercentage number 121
//	Total  aboveTwentyPercentage  number 97
//	Total  aboveThirtyPercentage number 82
//	Total  aboveFourtyPercentage number 66
//	Total  aboveOnehundrePercentage  number 16
//	Average one year gain 9.771259
//	Average max gain 56.104095
//	Average max loss -31.995596
//	Finish processing case 10, total time cost in hours 0.11097861
	
	// PTMA>50.0,ADJUSTEDPRICE>10 AND VOLUME>500000 AND GR100<60
	// and count (PTMA>50%)==0 FOR LAST 120 DAYS, MARKET CAP >20000 M
	// TEST DATA RANGE W>5000 AND W<10677-250
	// Total occurence 43
	// Total aboveTenPercentage number 31
	// Total aboveTwentyPercentage number 24
	// Total aboveThirtyPercentage number 19
	// Total aboveFourtyPercentage number 14
	// Total aboveOnehundrePercentage number 5
	// Average one year gain -10.642749
	// Average max gain 44.74474
	// Average max loss -42.525063
	// Finish processing case 10, total time cost in hours 0.09816389

	public static void CASE10(int seqIndex) {
		long t1 = System.currentTimeMillis();
		System.out.println("Start processing case 10");

		// Hashtable ystocks = SQLOperation.getCurrentYahooStocks(seqIndex);
		SQLOperation.resetStats(seqIndex);

		int loopCount = 0;
		for (int w = seqIndex - 251; w > 8000; w--) {
			Hashtable stocks = SQLOperation.getUberBullStocks(w);
			Enumeration en = stocks.keys();
			while (en.hasMoreElements()) {
				String symbol = en.nextElement().toString();
				// if (ystocks.containsKey(symbol)) {
				SQLOperation.evaluateGain(symbol, w + 1);
				loopCount++;

				if (loopCount % 100 == 0)
					try {
						Thread.sleep(15000);
						// break;
					} catch (Exception ex) {

					}
			}
			// }
		}
		// }
		SQLOperation.printAverageSummary();

		long t2 = System.currentTimeMillis();
		System.out
				.println("Finish processing case 10, total time cost in hours "
						+ ((t2 - t1) * 1.0f) / (1000 * 60 * 60 * 1.0f));

	}

	// GET UBER Bear, PTMA<-50.0 &&ADJUSTEDPRICE>1, and count (PTMA<-50%)==0 FOR
	// FIRST 120 DAYS (AT LEAST)
	// W>7000
	/*
	 * Total occurence 4397 Total aboveTenPercentage number 4045 Total
	 * aboveTwentyPercentage number 3724 Total aboveThirtyPercentage number 3403
	 * Total aboveFourtyPercentage number 3047 Total aboveOnehundrePercentage
	 * number 1277 Average one year gain 37.00686 Average max gain 85.92807
	 * Average max loss -39.394295 Finish processing case 11, total time cost in
	 * hours 2.5704002
	 */

	// PTMA<-40 AND ADJUSTEDPRICE>10 AND VOLUME>350000
	// getStockCap(symbol, price) >1000
	// and count (PTMA<-40%)==0 FOR LAST 120 DAYS, MARKET CAP >1000 M
	// TEST DATA RANGE W>5000 AND W<10677-250
	/*
	 * Total occurence 507 Total aboveTenPercentage number 420 Total
	 * aboveTwentyPercentage number 348 Total aboveThirtyPercentage number 287
	 * Total aboveFourtyPercentage number 216 Total aboveOnehundrePercentage
	 * number 40 Average one year gain -6.0899696 Average max gain 44.690395
	 * Average max loss -42.55783 Finish processing case 11, total time cost in
	 * hours 0.45364556
	 */
	// PTMA<-50 AND ADJUSTEDPRICE>10 AND VOLUME>350000
	// getStockCap(symbol, price) >1000
	// and count (PTMA<-50%)==0 FOR LAST 120 DAYS, MARKET CAP >1000 M
	// TEST DATA RANGE W>5000 AND W<10677-250
	/*
	 * Total occurence 294 Total aboveTenPercentage number 254 Total
	 * aboveTwentyPercentage number 218 Total aboveThirtyPercentage number 176
	 * Total aboveFourtyPercentage number 141 Total aboveOnehundrePercentage
	 * number 46 Average one year gain -3.835619 Average max gain 51.29563
	 * Average max loss -47.10248 Finish processing case 11, total time cost in
	 * hours 0.21782556
	 */

	// PTMA<-60 AND ADJUSTEDPRICE>10 AND VOLUME>350000
	// getStockCap(symbol, price) >1000
	// and count (PTMA<-60%)==0 FOR LAST 120 DAYS, MARKET CAP >1000 M
	// TEST DATA RANGE W>5000 AND W<10677-250
	/*
	 * Total occurence 155 Total aboveTenPercentage number 135 Total
	 * aboveTwentyPercentage number 113 Total aboveThirtyPercentage number 100
	 * Total aboveFourtyPercentage number 86 Total aboveOnehundrePercentage
	 * number 30 Average one year gain -3.400117 Average max gain 59.852665
	 * Average max loss -53.971294 Finish processing case 11, total time cost in
	 * hours 0.09404305
	 */

	// PTMA<-70 AND ADJUSTEDPRICE>10 AND VOLUME>350000
	// getStockCap(symbol, price) >1000
	// and count (PTMA<-70%)==0 FOR LAST 120 DAYS, MARKET CAP >1000 M
	// TEST DATA RANGE W>5000 AND W<10677-250
	/*
	 * 
	 * Total aboveTenPercentage number 58 Total aboveTwentyPercentage number 51
	 * Total aboveThirtyPercentage number 48 Total aboveFourtyPercentage number
	 * 43 Total aboveOnehundrePercentage number 22 Average one year gain
	 * 8.233755 Average max gain 78.25182 Average max loss -55.564407 Finish
	 * processing case 11, total time cost in hours 0.034872223
	 */

	
	//PTMA<-50.0 AND SEQINDEX="
	//		+ seqIndex + " AND ADJUSTEDPRICE>10 AND VOLUME>500000 AND GR100>-60
	//PTMA<-50.0  and SEQINDEX<" + index + " AND SEQINDEX>" + (index - 120);
	// getStockCap(symbol, price) > 500
//	Total occurence 215
//	Total  aboveTenPercentage number 189
//	Total  aboveTwentyPercentage  number 170
//	Total  aboveThirtyPercentage number 147
//	Total  aboveFourtyPercentage number 126
//	Total  aboveOnehundrePercentage  number 33
//	Average one year gain 1.6549788
//	Average max gain 58.320274
//	Average max loss -45.293736
//	Finish processing case 11, total time cost in hours 0.06343833
	
	//PTMA<-50.0 AND SEQINDEX="
	//		+ seqIndex + " AND ADJUSTEDPRICE>10 AND VOLUME>500000 AND GR100>-60
	//PTMA<-50.0  and SEQINDEX<" + index + " AND SEQINDEX>" + (index - 120);
	// getStockCap(symbol, price) > 5000
//	Total occurence 45
//	Total  aboveTenPercentage number 39
//	Total  aboveTwentyPercentage  number 33
//	Total  aboveThirtyPercentage number 28
//	Total  aboveFourtyPercentage number 19
//	Total  aboveOnehundrePercentage  number 5
//	Average one year gain -16.174887
//	Average max gain 47.96267
//	Average max loss -55.56287
//	Finish processing case 11, total time cost in hours 0.038376667
	
	
	//PTMA<-50.0 AND SEQINDEX="
	//		+ seqIndex + " AND ADJUSTEDPRICE>10 AND VOLUME>500000 AND GR100>-60
	//PTMA<-50.0  and SEQINDEX<" + index + " AND SEQINDEX>" + (index - 200);
	// getStockCap(symbol, price) > 5000
//	Total occurence 44
//	Total  aboveTenPercentage number 38
//	Total  aboveTwentyPercentage  number 32
//	Total  aboveThirtyPercentage number 27
//	Total  aboveFourtyPercentage number 19
//	Total  aboveOnehundrePercentage  number 4
//	Average one year gain -12.710023
//	Average max gain 47.265785
//	Average max loss -55.47877
	
	//PTMA<-50.0 AND SEQINDEX="
	//		+ seqIndex + " AND ADJUSTEDPRICE>10 AND VOLUME>500000 AND GR100>-60
	//PTMA<-50.0  and SEQINDEX<" + index + " AND SEQINDEX>" + (index - 300);
	// getStockCap(symbol, price) > 5000	
	// Total occurence 43
	// Total aboveTenPercentage number 37
	// Total aboveTwentyPercentage number 31
	// Total aboveThirtyPercentage number 26
	// Total aboveFourtyPercentage number 19
	// Total aboveOnehundrePercentage number 4
	// Average one year gain -12.713586
	// Average max gain 47.504906
	// Average max loss -55.553394
	// Finish processing case 11, total time cost in hours 0.086006664

	public static void CASE11(int seqIndex) {
		long t1 = System.currentTimeMillis();
		System.out.println("Start processing case 11");

		// Hashtable ystocks = SQLOperation.getCurrentYahooStocks(seqIndex);
		SQLOperation.resetStats(seqIndex);

		int loopCount = 0;
		for (int w = seqIndex - 251; w > 5000; w--) {
			Hashtable stocks = SQLOperation.getUberBearStocks(w);
			Enumeration en = stocks.keys();
			while (en.hasMoreElements()) {
				String symbol = en.nextElement().toString();
				// if (ystocks.containsKey(symbol)) {
				SQLOperation.evaluateGain(symbol, w + 1);
				loopCount++;

				if (loopCount % 100 == 0)
					try {
						Thread.sleep(15000);
						// break;
					} catch (Exception ex) {

					}
				// }
			}
		}
		// }
		SQLOperation.printAverageSummary();

		long t2 = System.currentTimeMillis();
		System.out
				.println("Finish processing case 11, total time cost in hours "
						+ ((t2 - t1) * 1.0f) / (1000 * 60 * 60 * 1.0f));

	}

	// proper correction acptma>0.1 and price>10 and volume>500000 at index
	// and ptma>8 and index+1
	// ADJUSTEDPRICE>10 AND VOLUME>500000 AND GR100<-29, w > 5000
	// if (ptma > 8.0f) {
	// if (getStockCap(sym, price) > 5000) {

	// Total occurence 746
	// Total aboveTenPercentage number 580
	// Total aboveTwentyPercentage number 426
	// Total aboveThirtyPercentage number 286
	// Total aboveFourtyPercentage number 177
	// Total aboveOnehundrePercentage number 30
	// Average one year gain 7.3872595
	// Average max gain 32.395603
	// Average max loss -16.53664
	// Finish processing case 12, total time cost in hours 0.07562222

	// proper correction acptma>0.1 and price>10 and volume>500000 at index
	// and ptma>8 and index+1
	// ADJUSTEDPRICE>10 AND VOLUME>500000 AND GR100<-29, w > 5000
	// if (ptma > 8.0f) {
	// if (getStockCap(sym, price) < 20000) {
	// Total occurence 1920
	// Total aboveTenPercentage number 1496
	// Total aboveTwentyPercentage number 1114
	// Total aboveThirtyPercentage number 749
	// Total aboveFourtyPercentage number 513
	// Total aboveOnehundrePercentage number 79
	// Average one year gain 8.478154
	// Average max gain 33.15372
	// Average max loss -17.277891
	// Finish processing case 12, total time cost in hours 0.20804

	public static void CASE12(int seqIndex) {
		long t1 = System.currentTimeMillis();
		System.out.println("Start processing case 12");

		// Hashtable ystocks = SQLOperation.getCurrentYahooStocks(seqIndex);
		SQLOperation.resetStats(seqIndex);

		int loopCount = 0;
		for (int w = seqIndex - 81; w > 10000; w--) {
			Hashtable stocks = SQLOperation.getPCUStocks(w);
			Enumeration en = stocks.keys();
			while (en.hasMoreElements()) {
				String symbol = en.nextElement().toString();
				// if (ystocks.containsKey(symbol)) {
				//SQLOperation.evaluateGain(symbol, w + 2);
				SQLOperation.evaluateShortGain(symbol, w+2);
				loopCount++;

				if (loopCount % 100 == 0)
					try {
						Thread.sleep(15000);
						// break;
					} catch (Exception ex) {

					}
				// }
			}
		}
		// }
		SQLOperation.printAverageSummary();

		long t2 = System.currentTimeMillis();
		System.out
				.println("Finish processing case 12, total time cost in hours "
						+ ((t2 - t1) * 1.0f) / (1000 * 60 * 60 * 1.0f));

	}
	
	
	public static void CASE13(int seqIndex) {
		long t1 = System.currentTimeMillis();
		System.out.println("Start processing case 13");

		// Hashtable ystocks = SQLOperation.getCurrentYahooStocks(seqIndex);
		SQLOperation.resetStats(seqIndex);

		int loopCount = 0;
		for (int w = seqIndex - 126; w > 10000; w--) {
			Hashtable stocks =null;// SQLOperation.getGOGOBULLStocks(w);
			Enumeration en = stocks.keys();
			while (en.hasMoreElements()) {
				String symbol = en.nextElement().toString();
				// if (ystocks.containsKey(symbol)) {
				//SQLOperation.evaluateGain(symbol, w + 1);
				SQLOperation.evaluateShortGain(symbol, w+1);
				loopCount++;

				if (loopCount % 100 == 0)
					try {
						Thread.sleep(15000);
						// break;
					} catch (Exception ex) {

					}
				// }
			}
		}
		// }
		SQLOperation.printAverageSummary();

		long t2 = System.currentTimeMillis();
		System.out
				.println("Finish processing case 13, total time cost in hours "
						+ ((t2 - t1) * 1.0f) / (1000 * 60 * 60 * 1.0f));

	}
	
	
	public static void CASE14(int seqIndex) {
		long t1 = System.currentTimeMillis();
		System.out.println("Start processing case 13");

		// Hashtable ystocks = SQLOperation.getCurrentYahooStocks(seqIndex);
		SQLOperation.resetStats(seqIndex);

		int loopCount = 0;
		for (int w = seqIndex - 26; w > 10000; w--) {
			Hashtable stocks = null;//SQLOperation.getFFPBULLStocks(w);
			Enumeration en = stocks.keys();
			while (en.hasMoreElements()) {
				String symbol = en.nextElement().toString();
				// if (ystocks.containsKey(symbol)) {
				//SQLOperation.evaluateGain(symbol, w + 1);
				SQLOperation.evaluateShortGain(symbol, w+1);
				loopCount++;

				if (loopCount % 100 == 0)
					try {
						Thread.sleep(15000);
						// break;
					} catch (Exception ex) {

					}
				// }
			}
		}
		// }
		SQLOperation.printAverageSummary();

		long t2 = System.currentTimeMillis();
		System.out
				.println("Finish processing case 13, total time cost in hours "
						+ ((t2 - t1) * 1.0f) / (1000 * 60 * 60 * 1.0f));

	}
	
	
	public static void CASE15(int seqIndex) {
		long t1 = System.currentTimeMillis();
		System.out.println("Start processing case 15");

		// Hashtable ystocks = SQLOperation.getCurrentYahooStocks(seqIndex);
		SQLOperation.resetStats(seqIndex);

		int loopCount = 0;
		for (int w = seqIndex - 251; w > 2000; w--) {
			Hashtable stocks = null;//SQLOperation.getGOGOBEARStocks(w);
			Enumeration en = stocks.keys();
			while (en.hasMoreElements()) {
				String symbol = en.nextElement().toString();
				// if (ystocks.containsKey(symbol)) {
				SQLOperation.evaluateGain(symbol, w + 1);
				loopCount++;

				if (loopCount % 100 == 0)
					try {
						Thread.sleep(15000);
						// break;
					} catch (Exception ex) {

					}
				// }
			}
		}
		// }
		SQLOperation.printAverageSummary();

		long t2 = System.currentTimeMillis();
		System.out
				.println("Finish processing case 15, total time cost in hours "
						+ ((t2 - t1) * 1.0f) / (1000 * 60 * 60 * 1.0f));

	}

}
