package com.intraday.ptma.helper;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import java.util.Calendar;
import com.intraday.ptma.sql.SQLOperation;
import com.intraday.ptma.sql.SQLOperation2;
import com.intraday.ptma.sql.SQLOperation3;

import java.util.Hashtable;
import java.util.Enumeration;
import java.net.*;

public class HttpDownload implements Runnable {

	private static Hashtable myStocks = null;
	private static int sfn = 1;

	// use an array to buffer the write and read time to
	// avoid file not complete write before read
	private static int fbuff = 100;
	private static String[] files = new String[fbuff];
	private static int fileIndex = 0;
	private static Hashtable folder1Stocks = null;
	private static Hashtable folder2Stocks = null;
	private static Hashtable folder3Stocks = null;
	private static CookieManager cookieManager = null;

	public void run() {
		// do nothing
		System.out.println("HttpDownload.java runned");
		try {

			Files.createIntradyTagFile(System.currentTimeMillis() / 1000
					+ "StartDownnload.txt");
			long lastIndex = StaticData.getIntradayLastIndex();
			System.out.println("lastIndex " + lastIndex);

			SQLOperation.resetAllCalculationDoneTag();
			SQLOperation.clearDownloadCompleteTag();
			int folderNumber = StaticData.getIntradayFolderEnd();
			int sfn = StaticData.getSubFolderNumbers();
			downLoadAllStocks(lastIndex, StaticData.getDownloadDays(),
					folderNumber, sfn);

			Files.createIntradyTagFile("EndDownloadCalculation.txt");
			System.out.println("Start sleeping...****");
			Thread.sleep(100000);

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// 4603 STOCKS AFTER CLEANUP 5/11/2018, EACH OF THESE
		// STOCK TRADED AT 40% TIMES OF 5 MINUTES INTERVAL OVER 10 WEEKS
		// FROM 3/2/2018 TO 5/11/2018 PEROID
		// UKX --> FTSE 100
		//
		try {
			// downLoadStockHistory("AMZN", 1);
			// downLoadAllStocks(19441L,2); //*** download last n days daily
			// stock trade info
			// addRecordToStock("DEO", "DEO_A",12,12, 0L);
			// at 5 min interval ****//
			// System.out.println("Intraday download complete.****");
			// downLoadStockHistory("ME", 5, 46,1);
			// Thread.sleep(20000);
			// makeupCalculation(19441L) ;
			// insertAllStocks(23470L, 13, 13); //*** insert daily stock trade
			// info from folder to folder
			// downLoadAllStocks(k,n); //download n day intraday record
			// use all stock symbols at index k

			// sxe SQLOperation.getNotUpdatedStocks(24260L);
			// System.out.println("Start sleeping...****");
			// Thread.sleep(1000000000); Thread.sleep(1000000000);
			// Thread.sleep(1000000000); Thread.sleep(1000000000);
			// Thread.sleep(1000000000); Thread.sleep(1000000000);

			// findMissingRecords(StaticData.getIntradayLastIndex(),
			// StaticData.getIntradayFolderEnd());
			// int folderNumber1 = StaticData.getIntradayFolderEnd();
			// downLoadAllStocks(StaticData.getIntradayLastIndex(),
			// StaticData.getDownloadDays(), folderNumber1);
			/*
			 * Files.createIntradyTagFile("IntradayMissingStart.txt");
			 * 
			 * insertAllStocks(StaticData.getIntradayLastIndex(), 37, 37, false,
			 * true); Files.createIntradyTagFile("IntradayMissingDone.txt");
			 * 
			 * System.out.println("Done"); Thread.sleep(1000000000);
			 * Thread.sleep(1000000000); Thread.sleep(1000000000);
			 * Thread.sleep(1000000000); Thread.sleep(1000000000);
			 * Thread.sleep(1000000000); Thread.sleep(1000000000);
			 * Thread.sleep(1000000000);
			 */
			// splitCalculationStocks();
			// SQLOperation.updateNotDownloadedStocks(20);

			// testSingleStock("BABA");
			// addRecordToStock("AMZN", "AMZN", 35, 35,
			// StaticData.getIntradayLastIndex());
			// checkEmptyRecords(35);
			// findMissingRecords(StaticData.getIntradayLastIndex(), 36);
			// insertAllStocks(StaticData.getIntradayLastIndex(), 36, 36, false,
			// true);
			// System.out.println("Start sleeping...****");
			// Thread.sleep(1000000000);
			// Thread.sleep(1000000000);
			// Thread.sleep(1000000000);
			// Thread.sleep(1000000000);

			Files.createIntradyTagFile("StartDownnload.txt");
			// SQLOperation.getBothEnds("AMZN");
			long lastIndex = StaticData.getIntradayLastIndex();// SQLOperation.getEndIndexLast();
			// long lastIndex = 24339L;//SQLOperation2.getEndIndexLast();

			System.out.println("lastIndex " + lastIndex);

			//SQLOperation.resetAllCalculationDoneTag();
			//SQLOperation.clearDownloadCompleteTag();
			int folderNumber = StaticData.getIntradayFolderEnd();
			int sfn = StaticData.getSubFolderNumbers();
			downLoadAllStocks(lastIndex, StaticData.getDownloadDays(),
					folderNumber, sfn);
			// download
			// n
			// day
			// intraday
			// record

			// no need to calculate here as we have 3 threads running
			// CalculatedDownloadedStocks(lastIndex, folderNumber, folderNumber,
			// true, true);
			// int folderNumber = 14;
			// long lastIndex = 24260L;
			// insertAllStocks(lastIndex, folderNumber, folderNumber, true,
			// true); // ***
			// insert
			// daily
			// stock trade info
			// from folder to
			// folder
			Files.createIntradyTagFile("EndDownloadCalculation.txt");
			// makeupCalculation(23470L);
			// compareRecords(24023L, 11);
			// makeupCalculation(23470L);
			// downLoadStockHistory("ANTH");
			// addRecordToStock("AMZN","AMZN_Z");
			// SQLOperation.compareRecords("BIIB_A", "BIIB_B", -19441L);
			System.out.println("Start sleeping...****");
			Thread.sleep(100000);

			/*
			 * Thread.sleep(1000000000); Thread.sleep(1000000000);
			 * Thread.sleep(1000000000); Thread.sleep(1000000000);
			 * Thread.sleep(1000000000); //
			 * SQLOperation.padIntradayRecords("OSK", 0); //
			 * makeupCalculation(19441); // testSingleStock("OSK");
			 * System.out.println("Start sleeping...");
			 * Thread.sleep(1000000000); String folder = "2";
			 * 
			 * // Hashtable symbols = SQLOperation.getSymbolsFromYahooDB(43076);
			 * Hashtable symbols = SQLOperation.getStocks(); Hashtable symbols2
			 * = Files .getIntradayStocks("C:\\stock\\intraday\\" + folder +
			 * "\\"); // Hashtable symbols = SQLOperation.getStocks();
			 * Enumeration en = symbols2.keys();
			 * System.out.println("Total symbol count " + symbols.size()); int
			 * count = 0; long t1 = System.currentTimeMillis(); while
			 * (en.hasMoreElements()) { String symbol =
			 * en.nextElement().toString(); System.out.println("Processing " +
			 * symbol);
			 * 
			 * if (!symbols.containsKey(symbol)) { //
			 * downLoadStockHistory(symbol); // count++; //
			 * downLoadStockHistory("AAPL"); // System.out.println(count + //
			 * " Download complete for symbol " // + symbol); // // int seconds
			 * = (int)(10000*Math.random()); // Thread.sleep(seconds); // } long
			 * currentMaxIndex = SQLOperation .getCurrentMaxIndex(symbol);
			 * 
			 * if (IndexHistory.insertGoogleIntradayData(symbol, 2, symbol, 0l))
			 * { count++; System.out.println(count +
			 * " insert complete for symbol " + symbol);
			 * SQLOperation.padIntradayRecords(symbol, 0);
			 * IndexHistory.SingleStockRecalulation(symbol, currentMaxIndex); //
			 * Files.deleteGoogleIntradayHistory(symbol); if (count % 50 == 0) {
			 * long t2 = System.currentTimeMillis(); System.out
			 * .println("########## " + count +
			 * " *****************time cost seconds ############# " + (t2 - t1)
			 * / 1000); Thread.sleep(10000); } else if (count % 5 == 0) { long
			 * t2 = System.currentTimeMillis(); System.out
			 * .println("########## " + count +
			 * " ****************time cost seconds ##################" + (t2 -
			 * t1) / 1000); Thread.sleep(5000); } else if (count % 2 == 11) {
			 * long t2 = System.currentTimeMillis(); System.out
			 * .println("########## " + count +
			 * " ****************time cost seconds ##############" + (t2 - t1) /
			 * 1000); Thread.sleep(5000); } } } }
			 */
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}

	public static boolean checkExistence(String symbol, int folder) {
		boolean result = false;

		if (folder >= 1) {
			String filePath = "C:\\stock\\intraday\\" + folder + "\\" + symbol
					+ ".csv";
			File file = new File(filePath);
			if (file.exists()) {
				result = true;
			}
		}

		return result;
	}

	public static boolean checkExistence(String symbol, int folder, int sfn) {
		boolean result = false;

		if (folder >= 1) {
			String filePath = "C:\\stock\\intraday\\" + folder + "\\" + sfn
					+ "\\" + symbol + ".csv";
			File file = new File(filePath);
			if (file.exists()) {
				result = true;
			}
		}

		if (!result) {
			if (sfn == 1) {
				if (folder1Stocks == null) {
					folder1Stocks = Files.listAllFiles(folder, sfn);
				}

				if (folder1Stocks != null && folder1Stocks.containsKey(symbol)) {
					result = true;
				}
			} else if (sfn == 2) {
				if (folder2Stocks == null) {
					folder2Stocks = Files.listAllFiles(folder, sfn);
				}

				if (folder2Stocks != null && folder2Stocks.containsKey(symbol)) {
					result = true;
				}
			} else if (sfn == 3) {
				if (folder3Stocks == null) {
					folder3Stocks = Files.listAllFiles(folder, sfn);
				}

				if (folder3Stocks != null && folder3Stocks.containsKey(symbol)) {
					result = true;
				}
			}

		}

		return result;
	}

	public static void checkEmptyRecords(int folder) {
		Hashtable stocks = SQLOperation.getCurrentEODStocks(-1);
		Enumeration en = stocks.keys();
		int emptyCount = 0;
		int fullCount = 0;
		int partialCount = 0;
		int totalCount = stocks.size();
		int lc = 0;

		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			lc++;

			Hashtable stocks1 = Files.getRealGoogleIntradayHistory(symbol,
					folder, symbol);
			if (stocks1.size() == 79) {
				fullCount++;
			} else if (stocks1.size() > 0) {
				partialCount++;
			} else if (stocks1.size() == 0) {
				emptyCount++;
				System.out.println(symbol + " record is empty!");
			}

			System.out.println(lc + " out of " + totalCount + " processed");

			if (lc % 100 == 0) {
				try {
					Thread.sleep(5000);

				} catch (Exception ex) {

				}
			}

			System.out.println("Total count fullCount " + fullCount);
			System.out.println("Total count partialCount " + partialCount);
			System.out.println("Total count emptyCount " + emptyCount);

		}

	}

	public static void makeupCalculation(long lastIndex) {
		Hashtable results = SQLOperation.getCurrentAllStocks(lastIndex);

		Enumeration en = results.keys();
		System.out.println(results.size() + " stocks left");
		try {

			Thread.sleep(5000);
		} catch (Exception ex) {

		}
		long t1 = System.currentTimeMillis();
		int count = 0;
		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			if (!SQLOperation.isCalculationDone(symbol)) {
				SQLOperation.padIntradayRecords(symbol, lastIndex);
				IndexHistory.SingleStockRecalulation(symbol, lastIndex);
				SQLOperation.setCalculationDoneTag(symbol);
				System.out.println("Processing done for " + symbol);
				count++;
				long t2 = System.currentTimeMillis();
				try {
					System.out.println(count + " cost time seconds "
							+ (t2 - t1) / 1000);
					Thread.sleep(1000);
				} catch (Exception ex) {

				}
			}
			if (count % 20 == 0 && count > 0) {
				long t2 = System.currentTimeMillis();
				try {
					System.out.println(count + " cost time seconds "
							+ (t2 - t1) / 1000);
					Thread.sleep(3000);
				} catch (Exception ex) {

				}
			}
		}

	}

	public static void addRecordToStock(String symbol, String insertSymbol,
			int startFolder, int endFolder, long lastIndex) throws Exception {
		int folder = 1;// downLoadStockHistory(symbol);
		// IndexHistory.insertGoogleIntradayData(symbol,folder,insertSymbol);
		folder = 3;
		long currentMaxIndex = lastIndex;

		for (folder = startFolder; folder <= endFolder; folder++) {
			int rs = IndexHistory.insertGoogleIntradayData(symbol, folder, sfn,
					insertSymbol, currentMaxIndex);
			if (rs > 0) {
				SQLOperation.padIntradayRecords(insertSymbol, currentMaxIndex);
				currentMaxIndex = SQLOperation.getCurrentMaxIndex(insertSymbol);
			}
		}
		// Hashtable stocks = Files.getRealGoogleIntradayHistory(symbol);
		// IndexHistory.insertGoogleIntradayData(symbol);
		// System.out.println("Sleeping,,,");
		// Thread.sleep(100000);

		// IndexHistory.SingleStockRecalulation(insertSymbol, currentMaxIndex);

	}

	public static void addWithoutDownload(String symbol, int folder, int tag) {

		try {
			addRecordToStock(symbol, symbol, folder, folder,
					StaticData.getIntradayLastIndex());
			SQLOperation.setDownloadCompleteTag(symbol,
					StaticData.getIntradayLastIndex(), tag);
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	public static void addRecordToStock(String symbol, String insertSymbol,
			long lastIndex) throws Exception {
		int folder = 1;// downLoadStockHistory(symbol);
		long currentMaxIndex = SQLOperation.getCurrentMaxIndex(insertSymbol);
		// IndexHistory.insertGoogleIntradayData(symbol,folder,insertSymbol);
		folder = 3;
		IndexHistory.insertGoogleIntradayData(symbol, folder, sfn,
				insertSymbol, lastIndex);
		// Hashtable stocks = Files.getRealGoogleIntradayHistory(symbol);
		// IndexHistory.insertGoogleIntradayData(symbol);
		// System.out.println("Sleeping,,,");
		// Thread.sleep(100000);

		SQLOperation.padIntradayRecords(insertSymbol, currentMaxIndex);
		IndexHistory.SingleStockRecalulation(insertSymbol, currentMaxIndex);

	}

	public static void compareRecords(long index, int folder) {
		Hashtable stocks = Files.getRealGoogleSymbols(11);

		Enumeration en = stocks.keys();

		int lc = 0;
		long t1 = System.currentTimeMillis();

		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			try {
				boolean exists = SQLOperation.checkRecordAt(symbol, index);
				if (!exists) {
					System.out.println(" Symbol record not inserted " + symbol
							+ " at " + index);
				}
			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}

		}

	}

	public static int downLoadAllStocks(long lastIndex, int days, int folder,
			int sfn) {
		// Hashtable stocks = SQLOperation.getCurrentAllStocks(lastIndex);
		// use this so that we always download regardless previous date trade or
		// not

		Files.checkIntradayFolderExists(folder, sfn);

		// Hashtable stocks = SQLOperation.getCurrentAllStocks(15528L);

		// dynamic list of stocks from EOD table 1 day prior
		// as the Intraday program runs before EOD program
		// EOD program add fresh stocks if price*vlume>=200,000 and appear first
		Hashtable stocks = SQLOperation.getCurrentEODStocks(-1);
		// Hashtable stocks = new Hashtable();
		// stocks.put("AMZN", "AMZN");
		// stocks.put("DAUD", "DAUD");
		// stocks.put("EXPI", "EXPI");
		// stocks.put("INDS", "INDS");
		// stocks.put("SRTSW", "SRTSW");
		// switch over once EOD records sorted out
		// Hashtable stocks = SQLOperation.getAllCurrentStocksFromEOD();
		System.out.println("Stock size " + stocks.size());
		try {
			Thread.sleep(1000 + (int) (Math.random() * 2000));
		} catch (Exception ex) {

		}
		Enumeration en = stocks.keys();

		int lc = 0;
		long t1 = System.currentTimeMillis();
		int folderNumber = 0;
		int subfolder = 1;
		Files.checkIntradayFolderExists(folder);
		boolean downloadOne = false;

		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			// lc++;

			if (lc % 3 == 0) {
				subfolder = 1;
			} else if (lc % 3 == 1) {
				subfolder = 2;
			} else if (lc % 3 == 2) {
				subfolder = 3;
			}

			if (!checkExistence(symbol, folder, 1)
					&& !checkExistence(symbol, folder, 2)
					&& !checkExistence(symbol, folder, 3)) {
				try {
					downloadOne = true;
					if (folder <= 0 && folderNumber == 0) {
						folderNumber = downLoadStockHistory(symbol, days, -1,
								subfolder);
						lc++;
					} else {
						if (folder > 0) {
							// comment this out and uncomment next line when
							// done
							// addWithoutDownload(symbol, folder, tag);
							downLoadStockHistory(symbol, days, folder,
									subfolder);
							lc++;
						} else {
							downLoadStockHistory(symbol, days, folderNumber,
									subfolder);
							lc++;
						}
					}
					System.out.println("download history done for stock "
							+ symbol);

					long t2 = System.currentTimeMillis();
					try {
						Thread.sleep(4000 + (int) (Math.random() * 2000));

					} catch (Exception ex) {

					}
					System.out.println("Total cost time for " + lc
							+ " stock is seconds: " + (t2 - t1) / 1000);

				} catch (Exception ex) {
					ex.printStackTrace(System.out);
				}

			} else {
				System.out.println(symbol + " already downloaded...");
				downloadOne = false;
			}

			if (lc % 100 == 0 && downloadOne) {
				long t2 = System.currentTimeMillis();
				try {
					Thread.sleep(30000 + (int) (Math.random() * 10000));
				} catch (Exception ex) {

				}
				System.out.println("Total cost time for " + lc
						+ " stock is seconds: " + (t2 - t1) / 1000);
			}
		}

		return folderNumber;
	}

	public static void splitCalculationStocks() {

		Hashtable stocks = new Hashtable();
		int count = 0;
		int lc = 0;

		try {

			// 15528L is the position to download each day stocks
			// total count 4722
			stocks = SQLOperation.getCurrentAllStocks(16476L);

			// stocks = SQLOperation.getLeftStocks();

			Enumeration en = stocks.keys();

			while (en.hasMoreElements()) { // inner while

				lc++;
				count++;
				// loop
				// start
				String symbol = en.nextElement().toString();
				// use position 16528 uptrendstar value to split calculation
				// among three different work threads: CalculationThread,
				// CalculationThread3
				// and HttpDownload calculation
				if (count < 6000 && lc % 3 == 0) {
					SQLOperation.splitCalculationTag(symbol, 16476L, 993);
				} else if (count < 6000 && lc % 3 == 1) {
					// if(count%2==0){
					// SQLOperation.splitCalculationTag(symbol, 16476L, 993);
					// }else{
					SQLOperation.splitCalculationTag(symbol, 16476L, 996);
					// }
				} else if (count < 6000 && lc % 3 == 2) {
					SQLOperation.splitCalculationTag(symbol, 16476L, 999);
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	public static void CalculatedDownloadedStocks(long lastIndex,
			int startFolder, int endFolder, boolean withCalculation,
			boolean deleteSomeRecords) {

		if (myStocks == null) {
			// tag position and value, count = 499
			myStocks = SQLOperation.getMyStocks(16476L, 999);
		}

		long t1 = System.currentTimeMillis();
		int maxLoad = 4500; // calculat 2500 stocks then stop;

		Hashtable stocks = new Hashtable();
		int count = 0;
		int zeroCount = 0;

		try {
			while (stocks.size() < 5 && count < maxLoad) { // outer while loop
															// start
				try {

					Thread.sleep(12000); // sleep 2 min
				} catch (Exception ex) {

				}
				// stocks = SQLOperation.getCalculationReadyStocks();

				Hashtable stocksTemp = SQLOperation.getCalculationReadyStocks();

				Enumeration ent = stocksTemp.keys();

				while (ent.hasMoreElements()) {
					String nextStock = ent.nextElement().toString();
					if (myStocks.containsKey(nextStock)) {
						stocks.put(nextStock, nextStock);
					}
				}

				if (stocks.size() == 0) {
					zeroCount++;
				}

				if (zeroCount > 2) {
					break;
				}

				Enumeration en = stocks.keys();

				while (en.hasMoreElements() && count < maxLoad) { // inner while
																	// loop
																	// start
					String symbol = en.nextElement().toString();
					boolean done = SQLOperation.isCalculationDone(symbol);
					try {
						if (!done) { // if(!done) start
							if (deleteSomeRecords) {
								SQLOperation.deletePartialRecord(symbol,
										lastIndex);
							}
							for (int folder = startFolder; folder <= endFolder; folder++) { // for
																							// loop
																							// start
								// downLoadStockHistory(symbol);
								int rs = IndexHistory.insertGoogleIntradayData(
										symbol, folder, sfn, symbol, lastIndex);
								if (rs > 0) {
									SQLOperation.getBothEnds(symbol);
									SQLOperation.padIntradayRecords(symbol,
											SQLOperation.getEndIndexLast());
									System.out
											.println(count
													+ " insert history done for stock done "
													+ symbol);
									count++;

									if (withCalculation
											&& !SQLOperation
													.isCalculationDone(symbol)) {
										// SQLOperation.padIntradayRecords(symbol,
										// currentMaxIndex);
										IndexHistory.SingleStockRecalulation(
												symbol, lastIndex);
										SQLOperation
												.setCalculationDoneTag(symbol);
									}
								} else {
									SQLOperation.setCalculationDoneTag(symbol);
									count++;
								}

								if (count >= maxLoad) {
									break;
								}
								if (count % 50 == 0) {
									long t2 = System.currentTimeMillis();
									System.out.println("Total cost time for "
											+ count + " stock is seconds: "
											+ (t2 - t1) / 1000);

									try {
										Thread.sleep(5000);
									} catch (Exception ex) {

									}

								}
							}// for loop end
						} // if(!done) end
					} catch (Exception ex) {
						ex.printStackTrace(System.out);
					}

				}// inner while loop end

				stocks = new Hashtable();
				stocks.put("FindNewStockAgain", "NextLoop");
			}// outer while loop end

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

		Files.createIntradyTagFile("intradayDone.txt");
		long t2 = System.currentTimeMillis();
		System.out.println("Total cost time for " + count
				+ " stock is seconds: " + (t2 - t1) / 1000);
	}

	public static Hashtable findMissingRecords(long lastIndex, int folder) {
		Hashtable mstocks = new Hashtable();
		Hashtable stocks = SQLOperation.getCurrentEODStocks(43220);

		Enumeration en = stocks.keys();

		int lc = 0;

		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			Hashtable stock = Files.getRealGoogleIntradayHistory(symbol,
					folder, symbol);

			if (stock.size() > 0) {
				if (!SQLOperation.checkRecordAt(symbol, lastIndex)) {
					System.out.println("Stock missing " + symbol);
					mstocks.put(symbol, symbol);
					lc++;
				}

			}
		}

		System.out.println("Total missing stocks " + lc);
		return mstocks;
	}

	public static void insertAllStocks(long lastIndex, int startFolder,
			int endFolder, boolean withCalculation, boolean deleteSomeRecords) {
		// Hashtable stocks = SQLOperation.getCurrentEODStocks(43219);
		Hashtable stocks = findMissingRecords(
				StaticData.getIntradayLastIndex(), 37);
		Enumeration en = stocks.keys();

		System.out.println("Missing stocks size " + stocks.size());

		int lc = 0;
		int tag = 993;
		long t1 = System.currentTimeMillis();

		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			boolean done = SQLOperation.isCalculationDone(symbol);

			if (!done) {
				System.out.println(lc + " start insert " + symbol);
			} else {
				System.out.println(lc + " already inserted " + symbol);
			}

			try {
				if (!done) {
					if (deleteSomeRecords) {
						SQLOperation.deletePartialRecord(symbol, lastIndex);
					}
					for (int folder = startFolder; folder <= endFolder; folder++) {
						// downLoadStockHistory(symbol);
						long currentMaxIndex = SQLOperation
								.getCurrentMaxIndex(symbol);
						int rs = IndexHistory.insertGoogleIntradayData(symbol,
								folder, sfn, symbol, currentMaxIndex);
						if (rs > 0) {
							SQLOperation.padIntradayRecords(symbol,
									currentMaxIndex);
							System.out.println(lc
									+ " insert history done for stock done "
									+ symbol);
							lc++;
							if (lc % 3 == 0) {
								tag = 993;
							} else if (lc % 3 == 1) {
								tag = 996;
							} else if (lc % 3 == 2) {
								tag = 999;
							}
							try {// sleep 2 seconds before update to make DB
									// records inserted

								Thread.sleep(2000);
							} catch (Exception ex) {

							}
							SQLOperation.setDownloadCompleteTag(symbol,
									StaticData.getIntradayLastIndex() + 1, tag);
							if (withCalculation
									&& !SQLOperation.isCalculationDone(symbol)) {
								// SQLOperation.padIntradayRecords(symbol,
								// currentMaxIndex);
								IndexHistory.SingleStockRecalulation(symbol,
										currentMaxIndex);
								SQLOperation.setCalculationDoneTag(symbol);
							}
						}
						if (lc % 50 == 0) {
							long t2 = System.currentTimeMillis();
							System.out.println("Total cost time for " + lc
									+ " stock is seconds: " + (t2 - t1) / 1000);

							try {
								Thread.sleep(5000);
							} catch (Exception ex) {

							}

						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}

			// break;
		}
		Files.createIntradyTagFile("intradayDone.txt");
		long t2 = System.currentTimeMillis();
		System.out.println("Total cost time for " + lc + " stock is seconds: "
				+ (t2 - t1) / 1000);

	}

	public static void testSingleStock(String symbol) throws Exception {
		// downLoadStockHistory(symbol);
		// IndexHistory.insertGoogleIntradayData(symbol);
		// Hashtable stocks = Files.getRealGoogleIntradayHistory(symbol);
		// IndexHistory.insertGoogleIntradayData(symbol);
		SQLOperation.padIntradayRecords(symbol, 0);
		IndexHistory.SingleStockRecalulation(symbol, 0l);

	}

	public static int downLoadStockHistory(String symbol, int days, int folder,
			int subfolder) throws Exception {

		CloseableHttpClient httpclient = HttpClients.createDefault();
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH);
		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		// String url =
		// "http://real-chart.finance.yahoo.com/table.csv?s="+symbol+"&d="+month+"&e="+day+"&f="+year+"&g=d&a=0&b=01&c=1900&ignore=.csv";
		// String url = "http://finance.google.com/finance/getprices?&q=" +
		// symbol
		// + "&i=300&p=90d&f=d,o,h,l,c,v";

		// String url = "http://finance.google.com/finance/getprices?&q=" +
		// symbol
		// + "&i=300&p=" + days + "d&f=d,o,h,l,c,v";
		// https://www.google.com/finance/getprices?i=60&p=10d&f=d,o,h,l,c,v&df=cpct&q=IBM
		String url = "http://finance.google.com/finance/getprices?i=300&p="
				+ days + "d&f=d,o,h,l,c,v&df=cpct&q=" + symbol;
		System.out.println(url);
		long t1 = System.currentTimeMillis();
		// HttpGet httpGet = new HttpGet(url);
		// CloseableHttpResponse response1 = null;
		URLConnection connection = null;
		try {
			// response1 = httpclient.execute(httpGet);
			if (cookieManager == null) {
				cookieManager = new CookieManager();
				System.out.println("cookieManager  is null");
			}

			CookieHandler.setDefault(cookieManager);

			URL url3 = new URL(url);

			connection = url3.openConnection();
			System.out.println("Open connection...");
			connection.addRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64; rv:28.0) Gecko/20100101  Firefox/28.0");
			

			// connection.setRequestMethod("GET");
			// connection.setRequestProperty("Connection", "Keep-Alive");
			// connection.setRequestProperty("NID",
			// "135=lUXFyBZ2WPCOiPwU4hcUb2JGAJd5ui3WeKIB6TjENPoHSv6MU6CE6AdGNSei9S-Wf8NLZK0-lknIqawOy-_F8aWDHepwaYN-zHdCw6t9iNJhs50b4__Q2TdtgUaY1KRC");
			// connection.connect();

			connection.getContent();

			CookieStore cookieStore = cookieManager.getCookieStore();

			List<HttpCookie> cookieList = cookieStore.getCookies();

			// iterate HttpCookie object

			for (HttpCookie cookie : cookieList) {
				// gets domain set for the cookie
				System.out.println("Domain: " + cookie.getDomain());

				// gets max age of the cookie
				System.out.println("max age: " + cookie.getMaxAge());

				// gets name cookie
				System.out.println("name of cookie: " + cookie.getName());

				// gets path of the server
				System.out.println("server path: " + cookie.getPath());

				// gets boolean if cookie is being sent with secure protocol
				System.out.println("is cookie secure: " + cookie.getSecure());

				// gets the value of the cookie
				System.out.println("value of cookie: " + cookie.getValue());

				// gets the version of the protocol with which the given cookie
				// is related.
				System.out.println("value of cookie: " + cookie.getVersion());
			}

		} catch (Exception ex) {
			// kill the program if no connection, otherwise extra thread
			ex.printStackTrace(System.out);
			Files.createIntradyTagFile("StartDownnloadKilled.txt");
			Files.createIntradyTagFile("StartDownnloadKilled.txt"+System.currentTimeMillis()%1000);
			System.exit(1);
		}
		// The underlying HTTP connection is still held by the response object
		// to allow the response content to be streamed directly from the
		// network socket.
		// In order to ensure correct deallocation of system resources
		// the user MUST call CloseableHttpResponse#close() from a finally
		// clause.
		// Please note that if response content is not fully consumed the
		// underlying
		// connection cannot be safely re-used and will be shut down and
		// discarded
		// by the connection manager.
		int k = 1;
		try {

			// System.out.println(response1.getStatusLine());
			// HttpEntity entity1 = response1.getEntity();
			// do something useful with the response body
			// and ensure it is fully consumed
			// EntityUtils.consume(entity1);
			// InputStream is = entity1.getContent();
			InputStream is = connection.getInputStream();
			long t2 = System.currentTimeMillis();
			String filePath = "C:\\stock\\intraday\\1\\" + symbol + ".csv";

			if (folder <= 0) {
				for (k = 1; k < 100; k++) {
					filePath = "C:\\stock\\intraday\\" + k;
					File fileFolder = new File(filePath);
					if (!fileFolder.exists()) {
						fileFolder.mkdirs();
					}

					filePath = "C:\\stock\\intraday\\" + k + "\\" + symbol
							+ ".csv";
					File fileStock = new File(filePath);
					if (!fileStock.exists()) {
						fileStock.createNewFile();
						break;
					}
				}
			} else if (folder > 0) {

				filePath = "C:\\stock\\intraday\\" + folder + "\\" + subfolder
						+ "\\" + symbol + ".csv";
				File fileStock = new File(filePath);
				if (!fileStock.exists()) {
					fileStock.createNewFile();
				}
			}

			FileOutputStream fos = new FileOutputStream(new File(filePath));
			int inByte;
			while ((inByte = is.read()) != -1)
				fos.write(inByte);
			is.close();
			fos.close();
			long t3 = System.currentTimeMillis();
			System.out.println("Initial response takes milliseconds "
					+ (t2 - t1));
			System.out.println("Process response takes milliseconds "
					+ (t3 - t2));
		} finally {
			// response1.close();
		}

		return k--;

	}

	public static int downLoadStockHistoryBK(String symbol, int days,
			int folder, int subfolder) throws Exception {

		CloseableHttpClient httpclient = HttpClients.createDefault();
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH);
		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		// String url =
		// "http://real-chart.finance.yahoo.com/table.csv?s="+symbol+"&d="+month+"&e="+day+"&f="+year+"&g=d&a=0&b=01&c=1900&ignore=.csv";
		// String url = "http://finance.google.com/finance/getprices?&q=" +
		// symbol
		// + "&i=300&p=90d&f=d,o,h,l,c,v";

		// String url = "http://finance.google.com/finance/getprices?&q=" +
		// symbol
		// + "&i=300&p=" + days + "d&f=d,o,h,l,c,v";
		// https://www.google.com/finance/getprices?i=60&p=10d&f=d,o,h,l,c,v&df=cpct&q=IBM
		String url = "http://finance.google.com/finance/getprices?i=300&p="
				+ days + "d&f=d,o,h,l,c,v&df=cpct&q=" + symbol;
		System.out.println(url);
		long t1 = System.currentTimeMillis();
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response1 = null;

		try {
			response1 = httpclient.execute(httpGet);
		} catch (Exception ex) {
			// kill the program if no connection, otherwise extra thread
			System.exit(-1);
		}
		// The underlying HTTP connection is still held by the response object
		// to allow the response content to be streamed directly from the
		// network socket.
		// In order to ensure correct deallocation of system resources
		// the user MUST call CloseableHttpResponse#close() from a finally
		// clause.
		// Please note that if response content is not fully consumed the
		// underlying
		// connection cannot be safely re-used and will be shut down and
		// discarded
		// by the connection manager.
		int k = 1;
		try {

			System.out.println(response1.getStatusLine());
			HttpEntity entity1 = response1.getEntity();
			// do something useful with the response body
			// and ensure it is fully consumed
			// EntityUtils.consume(entity1);
			InputStream is = entity1.getContent();
			long t2 = System.currentTimeMillis();
			String filePath = "C:\\stock\\intraday\\1\\" + symbol + ".csv";

			if (folder <= 0) {
				for (k = 1; k < 100; k++) {
					filePath = "C:\\stock\\intraday\\" + k;
					File fileFolder = new File(filePath);
					if (!fileFolder.exists()) {
						fileFolder.mkdirs();
					}

					filePath = "C:\\stock\\intraday\\" + k + "\\" + symbol
							+ ".csv";
					File fileStock = new File(filePath);
					if (!fileStock.exists()) {
						fileStock.createNewFile();
						break;
					}
				}
			} else if (folder > 0) {

				filePath = "C:\\stock\\intraday\\" + folder + "\\" + subfolder
						+ "\\" + symbol + ".csv";
				File fileStock = new File(filePath);
				if (!fileStock.exists()) {
					fileStock.createNewFile();
				}
			}

			FileOutputStream fos = new FileOutputStream(new File(filePath));
			int inByte;
			while ((inByte = is.read()) != -1)
				fos.write(inByte);
			is.close();
			fos.close();
			long t3 = System.currentTimeMillis();
			System.out.println("Initial response takes milliseconds "
					+ (t2 - t1));
			System.out.println("Process response takes milliseconds "
					+ (t3 - t2));
		} finally {
			response1.close();
		}

		return k--;

	}

	public static void downLoadStockHistory(String downLoadSymbol, String symbol)
			throws Exception {

		CloseableHttpClient httpclient = HttpClients.createDefault();
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH);
		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		String url = "http://chart.finance.yahoo.com/table.csv?s="
				+ downLoadSymbol + "&d=" + month + "&e=" + day + "&f=" + year
				+ "&g=d&a=0&b=01&c=1900&ignore=.csv";
		System.out.println(url);
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response1 = httpclient.execute(httpGet);
		// The underlying HTTP connection is still held by the response object
		// to allow the response content to be streamed directly from the
		// network socket.
		// In order to ensure correct deallocation of system resources
		// the user MUST call CloseableHttpResponse#close() from a finally
		// clause.
		// Please note that if response content is not fully consumed the
		// underlying
		// connection cannot be safely re-used and will be shut down and
		// discarded
		// by the connection manager.
		try {
			System.out.println(response1.getStatusLine());
			HttpEntity entity1 = response1.getEntity();
			// do something useful with the response body
			// and ensure it is fully consumed
			// EntityUtils.consume(entity1);
			InputStream is = entity1.getContent();
			String filePath = "C:\\stock\\yahoo\\" + symbol + ".csv";
			FileOutputStream fos = new FileOutputStream(new File(filePath));
			int inByte;
			while ((inByte = is.read()) != -1)
				fos.write(inByte);
			is.close();
			fos.close();
		} finally {
			response1.close();
		}

		/*
		 * HttpPost httpPost = new HttpPost("http://targethost/login"); List
		 * <NameValuePair> nvps = new ArrayList <NameValuePair>(); nvps.add(new
		 * BasicNameValuePair("username", "vip")); nvps.add(new
		 * BasicNameValuePair("password", "secret")); httpPost.setEntity(new
		 * UrlEncodedFormEntity(nvps)); CloseableHttpResponse response2 =
		 * httpclient.execute(httpPost);
		 * 
		 * try { System.out.println(response2.getStatusLine()); HttpEntity
		 * entity2 = response2.getEntity(); // do something useful with the
		 * response body // and ensure it is fully consumed
		 * EntityUtils.consume(entity2);
		 * 
		 * } finally { response2.close(); }
		 */
	}

}
