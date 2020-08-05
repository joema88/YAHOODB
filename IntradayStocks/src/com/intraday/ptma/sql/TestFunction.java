package com.intraday.ptma.sql;

import java.util.*;

public class TestFunction {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// SQLOperation sqlOperation = new SQLOperation();
		// SQLOperation.markUpTrendHistory("NVDA");
		// SQLOperation.printOutDSIAvgTrend("SPY");
		SQLOperation.resetDistroList(32125, 32203);
		System.out.println("Done ");
		// SQLOperation.calculateAverageDCPIP(19441,false);
		// recalculateDCPPerTrendByPercentage(19441l);

		// GET THE PARAMETERS
		// SQLOperation.calculateAverageDS3IP(19441,false);
		// RERUN THE CALCULATION
		// recalculateDS3PerTrendByPercentage(19441l);

		// recalculate BBDI based on statistic determined parameters
		// NEED TO RERUN TO COMPLETE IT
		// recalculateBBDI(19441l);

		// recalculateBBDI(19441l);
		// calculateAverageDS3PER(19441l);

		// recalculateDCPIP(19441L);
		// recalculateDCPT(19441L);
		// recalculateDCPT(19441L);
		// recalculateDIPSUMGradientTWA(19441L);
		// recalculateDIPSCORE(19441L);
		// SQLOperation.calculateAverageDIPSCORE(19441L, false);

		// recalculateDPS4(19441L);

		// recalculateBBSCORE(19441L);

		// recalculateUTIS(19441L);
		// recalculateSATCount(19441L);
		// recalculateBBGO(19441L);
		// recalculateFFP(19441L);
		// recalculateBBSCORE(19441L);

		// recalculateSellingScore(19441L);
		// recalculateLBBI(19441L);
		// recalculateIBBS(19441L);
		// findUTISPC(19441L);
		// SQLOperation.calculateAverageSPYYield("SPY",19441L);
		// recalculateUTIS(19441L);
		// SQLOperation.overlayAllBBScore(19441L);

	}

	public static void findUTISPC(long index) {
		Hashtable stocks = SQLOperation.getCurrentAllStocks(index);
		Enumeration en = stocks.keys();
		int loopCount = 0;
		long t1 = System.currentTimeMillis();

		while (en.hasMoreElements()) {
			String stock = en.nextElement().toString();
			SQLOperation.calculateUTISteps23(stock, 0l, false, 1.010f, 1.002f);

			loopCount++;
			System.out.println("Processing done " + loopCount
					+ " SellingScore for stock " + stock);
			if (loopCount % 100 == 0) {
				long t2 = System.currentTimeMillis();
				System.out.println(loopCount
						+ " calculation done time cost seconds: " + (t2 - t1)
						/ 1000);
				try {
					Thread.sleep(3000);
				} catch (Exception ex) {

				}
			}
		}

		int utis1c = SQLOperation.getUtis1Count();
		int utis2c = SQLOperation.getUtis2Count();
		int utis3c = SQLOperation.getUtis3Count();
		int total = 17273646;
		System.out.println("utis1c " + utis1c + ", utis2c " + utis2c
				+ ", utis3c " + utis3c);
		System.out.println("utis1c percentage " + 100.0f
				* (utis1c * 1.0f / total * 1.0f));
		System.out.println("utis2c percentage " + 100.0f
				* (utis2c * 1.0f / total * 1.0f));
		System.out.println("utis3c percentage " + 100.0f
				* (utis3c * 1.0f / total * 1.0f));
	}

	public static void recalculateSellingScore(long index) {
		Hashtable stocks = SQLOperation.getCurrentAllStocks(index);
		Enumeration en = stocks.keys();
		int loopCount = 0;
		long t1 = System.currentTimeMillis();

		while (en.hasMoreElements()) {
			String stock = en.nextElement().toString();
			if (!SQLOperation.isCalculationDone(stock)
					&& stock.indexOf("_") < 0) {
				SQLOperation.resetSellingScore(stock);
				SQLOperation.calculateSellingScores(stock, 0l, false);

				SQLOperation.setCalculationDoneTag(stock);
			}
			loopCount++;
			System.out.println("Processing done " + loopCount
					+ " SellingScore for stock " + stock);
			if (loopCount % 100 == 0) {
				long t2 = System.currentTimeMillis();
				System.out.println(loopCount
						+ " calculation done time cost seconds: " + (t2 - t1)
						/ 1000);
				try {
					Thread.sleep(3000);
				} catch (Exception ex) {

				}
			}
		}

	}

	public static void recalculateUTIS(long index) {
		Hashtable stocks = SQLOperation.getCurrentAllStocks(index);
		Enumeration en = stocks.keys();
		int loopCount = 0;
		long t1 = System.currentTimeMillis();

		while (en.hasMoreElements()) {
			String stock = en.nextElement().toString();
			if (!SQLOperation.isCalculationDone(stock)
					&& stock.indexOf("_") < 0) {
				SQLOperation.resetUTIS(stock);
				SQLOperation.calculateUTI(stock, 0l, false);
				SQLOperation.calculateUTIStep1(stock, 0l, false);
				SQLOperation.calculateUTISteps23(stock, 0l, false);

				SQLOperation.setCalculationDoneTag(stock);
			}
			loopCount++;
			System.out.println("Processing done " + loopCount
					+ " UTI,UTIS for stock " + stock);
			if (loopCount % 100 == 0) {
				long t2 = System.currentTimeMillis();
				System.out.println(loopCount
						+ " calculation done time cost seconds: " + (t2 - t1)
						/ 1000);
				try {
					Thread.sleep(3000);
				} catch (Exception ex) {

				}
			}
		}

	}

	public static void calculateAverageDS3PER(long index) {
		SQLOperation.calculateAverageDS3PER(index, false);
	}

	public static void recalculateDIPSCORE(long index) {
		Hashtable stocks = SQLOperation.getCurrentAllStocks(index);
		Enumeration en = stocks.keys();
		int loopCount = 0;
		long t1 = System.currentTimeMillis();

		while (en.hasMoreElements()) {
			String stock = en.nextElement().toString();
			if (!SQLOperation.isCalculationDone(stock)
					&& stock.indexOf("_") < 0) {
				SQLOperation.resetDIPSCORE(stock);
				SQLOperation.scoreDipOp(stock, 0l, false);
				SQLOperation.setCalculationDoneTag(stock);
			}
			loopCount++;
			System.out.println("Processing done " + loopCount
					+ " DIPSCORE for stock " + stock);
			if (loopCount % 100 == 0) {
				long t2 = System.currentTimeMillis();
				System.out.println(loopCount
						+ " calculation done time cost seconds: " + (t2 - t1)
						/ 1000);
				try {
					Thread.sleep(3000);
				} catch (Exception ex) {

				}
			}
		}

	}

	public static void recalculateDIPScoreSum(long index) {
		Hashtable stocks = SQLOperation.getCurrentAllStocks(index);
		Enumeration en = stocks.keys();
		int loopCount = 0;
		long t1 = System.currentTimeMillis();

		while (en.hasMoreElements()) {
			String stock = en.nextElement().toString();
			if (!SQLOperation.isCalculationDone(stock)
					&& stock.indexOf("_") < 0) {
				SQLOperation.resetDIPSCORESUM(stock);
				SQLOperation.getDipscoreSum(stock, 0l, 7, false);
				SQLOperation.setCalculationDoneTag(stock);
			}
			loopCount++;
			System.out.println("Processing done " + loopCount
					+ " DIPSCORE for stock " + stock);
			if (loopCount % 100 == 0) {
				long t2 = System.currentTimeMillis();
				System.out.println(loopCount
						+ " calculation done time cost seconds: " + (t2 - t1)
						/ 1000);
				try {
					Thread.sleep(3000);
				} catch (Exception ex) {

				}
			}
		}

	}

	public static void recalculateDIPSUMGradientTWA(long index) {
		Hashtable stocks = SQLOperation.getCurrentAllStocks(index);
		Enumeration en = stocks.keys();
		int loopCount = 0;
		long t1 = System.currentTimeMillis();

		while (en.hasMoreElements()) {
			String stock = en.nextElement().toString();
			if (!SQLOperation.isCalculationDone(stock)
					&& stock.indexOf("_") < 0) {
				SQLOperation.resetDIPSUMGradientTWA(stock);
				SQLOperation.getDipSumGradient(stock, 0l, false);
				SQLOperation.setCalculationDoneTag(stock);
			}
			loopCount++;
			System.out.println("Processing done " + loopCount
					+ " DIPSCORE for stock " + stock);
			if (loopCount % 100 == 0) {
				long t2 = System.currentTimeMillis();
				System.out.println(loopCount
						+ " calculation done time cost seconds: " + (t2 - t1)
						/ 1000);
				try {
					Thread.sleep(3000);
				} catch (Exception ex) {

				}
			}
		}

	}

	public static void recalculateDPS4(long index) {
		Hashtable stocks = SQLOperation.getCurrentAllStocks(index);
		Enumeration en = stocks.keys();
		int loopCount = 0;
		long t1 = System.currentTimeMillis();

		while (en.hasMoreElements()) {
			String stock = en.nextElement().toString();

			if (!SQLOperation.isCalculationDone(stock)
					&& stock.indexOf("_") < 0) {
				SQLOperation.resetDPS4(stock);
				SQLOperation.getDPS4History(stock, 0l, false);
				SQLOperation.setCalculationDoneTag(stock);
			}

			loopCount++;
			System.out.println("Processing done " + loopCount
					+ " DPS4 for stock " + stock);
			if (loopCount % 100 == 0) {
				long t2 = System.currentTimeMillis();
				System.out.println(loopCount
						+ " calculation done time cost seconds: " + (t2 - t1)
						/ 1000);
				try {
					Thread.sleep(3000);
				} catch (Exception ex) {

				}
			}
		}

	}

	public static void recalculateFFP(long index) {
		Hashtable stocks = SQLOperation.getCurrentAllStocks(index);
		Enumeration en = stocks.keys();
		int loopCount = 0;
		long t1 = System.currentTimeMillis();

		while (en.hasMoreElements()) {
			String stock = en.nextElement().toString();
			if (!SQLOperation.isCalculationDone(stock)
					&& stock.indexOf("_") < 0) {

				SQLOperation.resetFFP(stock);
				SQLOperation.getTotalBuyScore(stock, 0l, false);
				SQLOperation.setCalculationDoneTag(stock);

			}
			loopCount++;
			System.out.println("Processing done " + loopCount
					+ " FFP for stock " + stock);
			if (loopCount % 100 == 0) {
				long t2 = System.currentTimeMillis();
				System.out.println(loopCount
						+ " calculation done time cost seconds: " + (t2 - t1)
						/ 1000);
				try {
					Thread.sleep(3000);
				} catch (Exception ex) {

				}
			}
		}

	}

	public static void recalculateBBGO(long index) {
		Hashtable stocks = SQLOperation.getCurrentAllStocks(index);
		Enumeration en = stocks.keys();
		int loopCount = 0;
		long t1 = System.currentTimeMillis();

		while (en.hasMoreElements()) {
			String stock = en.nextElement().toString();
			if (!SQLOperation.isCalculationDone(stock)
					&& stock.indexOf("_") < 0) {

				SQLOperation.resetBBGO(stock);
				SQLOperation.getTurnPointScore(stock, 0l, false);
				SQLOperation.setCalculationDoneTag(stock);

			}
			loopCount++;
			System.out.println("Processing done " + loopCount
					+ " BBGO for stock " + stock);
			if (loopCount % 100 == 0) {
				long t2 = System.currentTimeMillis();
				System.out.println(loopCount
						+ " calculation done time cost seconds: " + (t2 - t1)
						/ 1000);
				try {
					Thread.sleep(3000);
				} catch (Exception ex) {

				}
			}
		}

	}

	public static void recalculateBBSCORE(long index) {
		Hashtable stocks = SQLOperation.getCurrentAllStocks(index);
		Enumeration en = stocks.keys();
		int loopCount = 0;
		long t1 = System.currentTimeMillis();

		while (en.hasMoreElements()) {
			String stock = en.nextElement().toString();
			if (!SQLOperation.isCalculationDone(stock)
					&& stock.indexOf("_") < 0) {

				SQLOperation.resetBBScore(stock);
				SQLOperation.markBBEndPoint(stock, 0l, false);
				SQLOperation.calculateBBScore(stock, 0l, false);
				SQLOperation.setCalculationDoneTag(stock);

			}
			loopCount++;
			System.out.println("Processing done " + loopCount
					+ " BBSCore for stock " + stock);
			if (loopCount % 100 == 0) {
				long t2 = System.currentTimeMillis();
				System.out.println(loopCount
						+ " calculation done time cost seconds: " + (t2 - t1)
						/ 1000);
				try {
					Thread.sleep(3000);
				} catch (Exception ex) {

				}
			}
		}

	}

	public static void recalculateSATCount(long index) {
		Hashtable stocks = SQLOperation.getCurrentAllStocks(index);
		Enumeration en = stocks.keys();
		int loopCount = 0;
		long t1 = System.currentTimeMillis();

		while (en.hasMoreElements()) {
			String stock = en.nextElement().toString();
			if (!SQLOperation.isCalculationDone(stock)
					&& stock.indexOf("_") < 0) {

				SQLOperation.resetSATCount(stock);
				SQLOperation.calculateIncDescTrendDays(stock, 0l, false, 40);
				loopCount++;
				SQLOperation.setCalculationDoneTag(stock);
			}
			System.out.println("Processing done " + loopCount
					+ " SATCOUNT for stock " + stock);
			if (loopCount % 100 == 0) {
				long t2 = System.currentTimeMillis();
				System.out.println(loopCount
						+ " calculation done time cost seconds: " + (t2 - t1)
						/ 1000);
				try {
					Thread.sleep(3000);
				} catch (Exception ex) {

				}
			}
		}

	}

	public static void recalculateLBBI(long index) {
		Hashtable stocks = SQLOperation.getCurrentAllStocks(index);
		Enumeration en = stocks.keys();
		int loopCount = 0;
		long t1 = System.currentTimeMillis();

		while (en.hasMoreElements()) {
			String stock = en.nextElement().toString();
			if (!SQLOperation.isCalculationDone(stock)
					&& stock.indexOf("_") < 0) {

				SQLOperation.resetLBBI(stock);
				SQLOperation.calculateLBBIHistory(stock, 0l, false);
				loopCount++;
				SQLOperation.setCalculationDoneTag(stock);
			}
			System.out.println("Processing done " + loopCount
					+ " BBDI for stock " + stock);
			if (loopCount % 100 == 0) {
				long t2 = System.currentTimeMillis();
				System.out.println(loopCount
						+ " calculation done time cost seconds: " + (t2 - t1)
						/ 1000);
				try {
					Thread.sleep(3000);
				} catch (Exception ex) {

				}
			}
		}

	}

	public static void recalculateIBBS(long index) {
		Hashtable stocks = SQLOperation.getCurrentAllStocks(index);
		Enumeration en = stocks.keys();
		int loopCount = 0;
		long t1 = System.currentTimeMillis();

		while (en.hasMoreElements()) {
			String stock = en.nextElement().toString();
			if (!SQLOperation.isCalculationDone(stock)
					&& stock.indexOf("_") < 0) {

				SQLOperation.resetIBBS(stock);
				SQLOperation.overlayBBScore(stock, 0l);
				loopCount++;
				SQLOperation.setCalculationDoneTag(stock);
			}
			System.out.println("Processing done " + loopCount
					+ " BBDI for stock " + stock);
			if (loopCount % 100 == 0) {
				long t2 = System.currentTimeMillis();
				System.out.println(loopCount
						+ " calculation done time cost seconds: " + (t2 - t1)
						/ 1000);
				try {
					Thread.sleep(3000);
				} catch (Exception ex) {

				}
			}
		}

	}

	public static void recalculateBBDI(long index) {
		Hashtable stocks = SQLOperation.getCurrentAllStocks(index);
		Enumeration en = stocks.keys();
		int loopCount = 0;
		long t1 = System.currentTimeMillis();

		while (en.hasMoreElements()) {
			String stock = en.nextElement().toString();
			if (!SQLOperation.isCalculationDone(stock)
					&& stock.indexOf("_") < 0) {

				SQLOperation.resetBBDI(stock);
				SQLOperation.getBBDIScore(stock, 0l, false);
				loopCount++;
				SQLOperation.setCalculationDoneTag(stock);
			}
			System.out.println("Processing done " + loopCount
					+ " BBDI for stock " + stock);
			if (loopCount % 100 == 0) {
				long t2 = System.currentTimeMillis();
				System.out.println(loopCount
						+ " calculation done time cost seconds: " + (t2 - t1)
						/ 1000);
				try {
					Thread.sleep(3000);
				} catch (Exception ex) {

				}
			}
		}

	}

	public static void recalculateDS3PerTrendByPercentage(long index) {
		Hashtable stocks = SQLOperation.getCurrentAllStocks(index);
		Enumeration en = stocks.keys();
		int loopCount = 0;
		long t1 = System.currentTimeMillis();

		while (en.hasMoreElements()) {
			String stock = en.nextElement().toString();
			if (!SQLOperation.isCalculationDone(stock)
					&& stock.indexOf("_") < 0) {

				SQLOperation.resetDS3PT(stock);
				SQLOperation.getDS3PerTrendHistory(stock, 0);
				SQLOperation.getDS3PerTrendHistoryByPercentageStep3(stock, 0,
						false);
				loopCount++;
				SQLOperation.setCalculationDoneTag(stock);
			}
			if (loopCount % 100 == 0) {
				long t2 = System.currentTimeMillis();
				System.out.println(loopCount
						+ " calculation done time cost seconds: " + (t2 - t1)
						/ 1000);
				try {
					Thread.sleep(3000);
				} catch (Exception ex) {

				}
			}
		}

	}

	public static void recalculateDeltaAgainstSPYHistory(long index) {

		SQLOperation.calculateDeltaAgainstSPYHistory(index, false);

	}

	public static void recalculateDCPIP(long index) {
		Hashtable stocks = SQLOperation.getCurrentAllStocks(index);
		Enumeration en = stocks.keys();
		int loopCount = 0;
		long t1 = System.currentTimeMillis();

		while (en.hasMoreElements()) {
			String stock = en.nextElement().toString();
			if (!SQLOperation.isCalculationDone(stock)
					&& stock.indexOf("_") < 0) {
				SQLOperation.resetDCPIP(stock);
				SQLOperation.getDCPTrendHistoryByPercentageStep1(stock, 0l);
				SQLOperation.setCalculationDoneTag(stock);
				loopCount++;
			}
			if (loopCount % 100 == 0) {
				long t2 = System.currentTimeMillis();
				System.out.println(loopCount
						+ " DCPIP calculation done time cost seconds: "
						+ (t2 - t1) / 1000);
				try {
					Thread.sleep(3000);
				} catch (Exception ex) {

				}
			}
		}

	}

	public static void recalculateDCPT(long index) {
		Hashtable stocks = SQLOperation.getCurrentAllStocks(index);
		Enumeration en = stocks.keys();
		int loopCount = 0;
		long t1 = System.currentTimeMillis();

		while (en.hasMoreElements()) {
			String stock = en.nextElement().toString();
			if (!SQLOperation.isCalculationDone(stock)
					&& stock.indexOf("_") < 0) {

				SQLOperation.resetDCPT(stock);
				SQLOperation.resetDCPIP(stock);
				// SET DCPT BASED ON TWA AND SIMPLE PRICE INCREASE/DECREASE
				SQLOperation.getTWATrendHistory(stock, 0l);
				// SET DCPIP
				SQLOperation.getDCPTrendHistoryByPercentageStep1(stock, 0l);
				// SET DCPT BASED ON DCPIP PERCENTAGE
				SQLOperation.updateLatestDCPIPStep2(stock, 0l);

				SQLOperation.setCalculationDoneTag(stock);
			}
			loopCount++;
			if (loopCount % 100 == 0) {
				long t2 = System.currentTimeMillis();
				System.out.println(loopCount
						+ " calculation done time cost seconds: " + (t2 - t1)
						/ 1000);
				try {
					Thread.sleep(3000);
				} catch (Exception ex) {

				}
			}
		}

	}

	public static void XXXX_recalculateDCPPerTrendByPercentage(long index) {
		Hashtable stocks = SQLOperation.getCurrentAllStocks(index);
		Enumeration en = stocks.keys();
		int loopCount = 0;
		long t1 = System.currentTimeMillis();

		while (en.hasMoreElements()) {
			String stock = en.nextElement().toString();
			SQLOperation.resetDCPT(stock);
			SQLOperation.getTWATrendHistory(stock, 0l);
			SQLOperation.updateLatestDCPIPStep2(stock, 0l);
			loopCount++;
			if (loopCount % 100 == 0) {
				long t2 = System.currentTimeMillis();
				System.out.println(loopCount
						+ " calculation done time cost seconds: " + (t2 - t1)
						/ 1000);
				try {
					Thread.sleep(3000);
				} catch (Exception ex) {

				}
			}
		}

	}
}
