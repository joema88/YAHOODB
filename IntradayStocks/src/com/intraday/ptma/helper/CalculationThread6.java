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
import com.intraday.ptma.sql.SQLOperation6;

import java.util.Hashtable;
import java.util.Enumeration;
import com.intraday.ptma.helper.StaticData;

public class CalculationThread6 {

	private static Hashtable myStocks = null;
	private static int sfn = 1;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			long t1 = System.currentTimeMillis();
			Files.createIntradyTagFile("ParallelCalculationStart6.txt");
			// SQLOperation.getBothEnds("AMZN");
			long lastIndex = StaticData.getIntradayLastIndex();// SQLOperation.getEndIndexLast();
			System.out.println("lastIndex " + lastIndex);

			// int folder = 19;
			int startFolder = StaticData.getIntradayFolderStart();
			int endFolder = StaticData.getIntradayFolderEnd();
			//google data method
			//CalculatedDownloadedStocks(lastIndex, startFolder, endFolder, true,
			//		true);
			CalculatedInsertedStocks(lastIndex);
			Files.createIntradyTagFile("EndParallelCalculation6.txt");
			long t2 = System.currentTimeMillis();
			long time = (t2-t1)/1000;
			Files.createIntradyTagFile("EndParallelCalculation6.txt"+time);
			
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
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
			IndexHistory.insertGoogleIntradayData(symbol, folder, sfn,
					insertSymbol, currentMaxIndex);
			SQLOperation.padIntradayRecords(insertSymbol, currentMaxIndex);
			currentMaxIndex = SQLOperation.getCurrentMaxIndex(insertSymbol);
		}
		// Hashtable stocks = Files.getRealGoogleIntradayHistory(symbol);
		// IndexHistory.insertGoogleIntradayData(symbol);
		// System.out.println("Sleeping,,,");
		// Thread.sleep(100000);

		// IndexHistory.SingleStockRecalulation(insertSymbol, currentMaxIndex);

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

	public static int downLoadAllStocks(long lastIndex, int days) {
		long t1 = System.currentTimeMillis();

		Hashtable stocks = SQLOperation.getCurrentAllStocks(lastIndex);

		Enumeration en = stocks.keys();

		int lc = 0;
		int folderNumber = 0;

		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			try {
				if (folderNumber == 0) {
					folderNumber = downLoadStockHistory(symbol, days);
				} else {
					downLoadStockHistory(symbol, days);
				}
				System.out.println("download history done for stock " + symbol);

				long t2 = System.currentTimeMillis();
				try {
					Thread.sleep(2000 + (int) (Math.random() * 2000));
				} catch (Exception ex) {

				}
				System.out.println("Total cost time for " + lc
						+ " stock is seconds: " + (t2 - t1) / 1000);
				lc++;
			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}

			if (lc % 100 == 0) {
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

	public static void doubleCheckFailedStocks(int folder) {
		Hashtable stocks = Files.listFailProcessedFiles(folder, sfn);
		Enumeration en = stocks.keys();

		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			int size = Integer.parseInt(stocks.get(symbol).toString());

			if (SQLOperation.isCalculationDone(symbol)) {
				SQLOperation.setCalculationDoneTag(symbol);
				Files.markFileDone(folder, sfn, symbol, size);
			} else {
				Files.markFileReprocess(folder, sfn, symbol, size);
			}
		}
	}

	public static void CalculatedDownloadedStocks(long lastIndex,
			int startFolder, int endFolder, boolean withCalculation,
			boolean deleteSomeRecords) {

		long t1 = System.currentTimeMillis();
		int lastCount = 0; // calculat 2500 stocks then stop;

		int zeroCount = 0;
		int lc = 0;
		int rs = 300;

		boolean con = true;
		boolean holidayTradingSchedule = false;
		try {
			// SQLOperation.resetAllCalculationDoneTag();
			// Files.createIntradyTagFile("CalculationThreadStart.txt");
			while (con) { // outer while loop start
				Hashtable stocks = Files.listUnprocessedFiles(endFolder, sfn);

				Enumeration en2 = stocks.keys();
				while (en2.hasMoreElements()) {
					String symbol2 = en2.nextElement().toString();
					// SQLOperation.deleteLastDayRecords(symbol2, lastIndex);

				}

				try {
					if (stocks.size() == 0) {
						Thread.sleep(60000); // sleep 1 mins
					}
				} catch (Exception ex) {

				}
				if (stocks.size() == 0) {
					int cc = Files.countAllFiles(endFolder, -1);
					if (!holidayTradingSchedule) {
						doubleCheckFailedStocks(endFolder);
					}

					if (cc > lastCount) {
						lastCount = cc;
					} else {
						// Thread.sleep(420000);
						Thread.sleep(30000);
						zeroCount++;

					}
				}

				if (zeroCount > 1) {
					con = false;
				}

				Enumeration en = stocks.keys();

				// while (stocks.size() > 5 && en.hasMoreElements()&& count <
				// maxLoad){
				while (en.hasMoreElements()) { // inner while
												// loop
												// start
					lc++;
					rs = 999;
					if (lc % 6 == 0) {
						System.out.println((lc - 1)
								+ " stocks has been calculated");
						Thread.sleep(1000);
					}
					String symbol = en.nextElement().toString();

					// need to remove after backcalculation done
					// SQLOperation.renameRecords(symbol, lastIndex);
					// NEED TO REVERT TO CHECK CODE ONCE DONE MAKE UP
					// CALCULATION
					// tag based method
					// boolean done = SQLOperation.isCalculationDone(symbol);
					// record number based method
					boolean done = SQLOperation.isCalculationDone(symbol,
							lastIndex, StaticData.getDownloadDays());

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
								rs = IndexHistory.insertGoogleIntradayData(
										symbol, folder, sfn, symbol, lastIndex);
								if (rs > 0) {
									SQLOperation.padIntradayRecords(symbol,
											lastIndex);
									lc++;
									if (withCalculation
											&& !SQLOperation
													.isCalculationDone(symbol)) {

										// IndexHistory.SingleStockRecalulation(
										// symbol, 26393);
										IndexHistory.SingleStockRecalulation(
												symbol, lastIndex);

									}
								}

								if (SQLOperation.isInsertionDone(symbol)) {
									SQLOperation.setCalculationDoneTag(symbol);
								} else {
									rs = 1000 + rs;
								}

							}// for loop end
						} // if(!done)
						Files.renameFile(endFolder, sfn, symbol, rs);
						// Thread.sleep(3000);
					} catch (Exception ex) {
						ex.printStackTrace(System.out);
					}

				}// inner while loop end

			}// outer while loop end

			Files.filterSparselyTradedStocks(endFolder, 1,
					StaticData.getDownloadDays());
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

		long t2 = System.currentTimeMillis();
	}

	public static void CalculatedInsertedStocks(long lastIndex) {

		long t1 = System.currentTimeMillis();
		int zeroCount = 0;
		int lc = 0;

		boolean con = true;
		boolean holidayTradingSchedule = false;
		long endIndex = lastIndex + StaticData.getDownloadDays() * 79;
		try {
			while (con) { // outer while loop start
				Hashtable stocks = SQLOperation6.getDistroList(lastIndex+1,
						endIndex, 6.0f);

				Enumeration en2 = stocks.keys();
				while (en2.hasMoreElements()) {
					String symbol2 = en2.nextElement().toString();
					// SQLOperation.deleteLastDayRecords(symbol2, lastIndex);

				}

				try {
					if (stocks.size() == 0) {
						Thread.sleep(60000); // sleep 5 mins
					}
				} catch (Exception ex) {

				}
				if (stocks.size() == 0) {
					zeroCount++;
				}

				if (zeroCount > 3) {
					con = false;
				}

				Enumeration en = stocks.keys();

				// while (stocks.size() > 5 && en.hasMoreElements()&& count <
				// maxLoad){
				while (en.hasMoreElements()) { // inner while
												// loop
												// start
					lc++;
					if (lc % 101 == 0) {
						System.out.println((lc - 1)
								+ " stocks has been calculated");
						Thread.sleep(1000);
					}
					String symbol = en.nextElement().toString();

					try {

						SQLOperation6.padIntradayRecords(symbol, lastIndex);
						lc++;
						IndexHistory.SingleStockRecalulation6(symbol, lastIndex);
						
						SQLOperation6.updateDistroCode(symbol, lastIndex+1,endIndex, 101.0f);

					} catch (Exception ex) {
						ex.printStackTrace(System.out);
					}

				}// inner while loop end

			}// outer while loop end

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

		long t2 = System.currentTimeMillis();
	}

	public static void testSingleStock(String symbol) throws Exception {
		// downLoadStockHistory(symbol);
		// IndexHistory.insertGoogleIntradayData(symbol);
		// Hashtable stocks = Files.getRealGoogleIntradayHistory(symbol);
		// IndexHistory.insertGoogleIntradayData(symbol);
		SQLOperation.padIntradayRecords(symbol, 0);
		IndexHistory.SingleStockRecalulation(symbol, 0l);

	}

	public static int downLoadStockHistory(String symbol, int days)
			throws Exception {

		CloseableHttpClient httpclient = HttpClients.createDefault();
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH);
		int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		// String url =
		// "http://real-chart.finance.yahoo.com/table.csv?s="+symbol+"&d="+month+"&e="+day+"&f="+year+"&g=d&a=0&b=01&c=1900&ignore=.csv";
		// String url = "http://finance.google.com/finance/getprices?&q=" +
		// symbol
		// + "&i=300&p=90d&f=d,o,h,l,c,v";

		String url = "http://finance.google.com/finance/getprices?&q=" + symbol
				+ "&i=300&p=" + days + "d&f=d,o,h,l,c,v";

		System.out.println(url);
		long t1 = System.currentTimeMillis();
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
			for (k = 1; k < 100; k++) {
				filePath = "C:\\stock\\intraday\\" + k;
				File fileFolder = new File(filePath);
				if (!fileFolder.exists()) {
					fileFolder.mkdirs();
				}

				filePath = "C:\\stock\\intraday\\" + k + "\\" + symbol + ".csv";
				File fileStock = new File(filePath);
				if (!fileStock.exists()) {
					fileStock.createNewFile();
					break;
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
