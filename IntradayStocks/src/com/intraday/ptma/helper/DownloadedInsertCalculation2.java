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
import com.intraday.ptma.sql.SQLOperation2;

import java.util.Hashtable;
import java.util.Enumeration;

public class DownloadedInsertCalculation2 {
	private static Hashtable myStocks = null;
	private static int sfn = 3;


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			//testSingleStock("SPY");
			Files.createIntradyTagFile("DownloadedInsertCalculation2.txt");
			
			recalculateBBScore(15528L);
			// recalculateBBScore(24418L);
			Files.createIntradyTagFile("DownloadedInsertCalculation2Done.txt");
			
			System.out.println("BBScore calculation  finished...");
			Thread.sleep(1000000000);
			Thread.sleep(1000000000);
			Thread.sleep(1000000000);

			SQLOperation2.getBothEnds("FB_1");
			long lastIndex = SQLOperation2.getEndIndexLast();
			System.out.println("Last index " + lastIndex);
			addRecordToStock("FB", "FB_1", 1, 16, lastIndex);
			// IndexHistory.SingleStockRecalulation("AMZN_1", -1);
			SQLOperation2.compareRecords("FB", "FB_1", -1);

			System.out.println("Insert and calculation  finished...");
			Thread.sleep(1000000000);
			Thread.sleep(1000000000);
			Thread.sleep(1000000000);
			Thread.sleep(1000000000);
			Thread.sleep(1000000000);
			Thread.sleep(1000000000);
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

	public static void makeupCalculation(long lastIndex) {
		Hashtable results = SQLOperation2.getCurrentAllStocks(lastIndex);

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
			if (!SQLOperation2.isCalculationDone(symbol)) {
				SQLOperation2.padIntradayRecords(symbol, lastIndex);
				IndexHistory.SingleStockRecalulation(symbol, lastIndex);
				SQLOperation2.setCalculationDoneTag(symbol);
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
			IndexHistory.insertGoogleIntradayData(symbol, folder,sfn, insertSymbol,
					currentMaxIndex);
			SQLOperation2.padIntradayRecords(insertSymbol, currentMaxIndex);
			currentMaxIndex = SQLOperation2.getCurrentMaxIndex(insertSymbol);
		}
		IndexHistory.SingleStockRecalulation(insertSymbol, lastIndex);

	}

	public static void addRecordToStock(String symbol, String insertSymbol,
			long lastIndex) throws Exception {
		int folder = 1;// downLoadStockHistory(symbol);
		long currentMaxIndex = SQLOperation2.getCurrentMaxIndex(insertSymbol);
		// IndexHistory.insertGoogleIntradayData(symbol,folder,insertSymbol);
		folder = 3;
		IndexHistory.insertGoogleIntradayData(symbol, folder,sfn, insertSymbol,
				lastIndex);
		// Hashtable stocks = Files.getRealGoogleIntradayHistory(symbol);
		// IndexHistory.insertGoogleIntradayData(symbol);
		// System.out.println("Sleeping,,,");
		// Thread.sleep(100000);

		SQLOperation2.padIntradayRecords(insertSymbol, currentMaxIndex);
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
				boolean exists = SQLOperation2.checkRecordAt(symbol, index);
				if (!exists) {
					System.out.println(" Symbol record not inserted " + symbol
							+ " at " + index);
				}
			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}

		}

	}

	public static int downLoadAllStocks(long lastIndex, int days, int folder) {
		Hashtable stocks = SQLOperation2.getCurrentAllStocks(lastIndex);

		Enumeration en = stocks.keys();

		int lc = 0;
		long t1 = System.currentTimeMillis();
		int folderNumber = 0;

		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			if (!checkExistence(symbol, folder)) {
				try {
					if (folderNumber == 0) {
						folderNumber = downLoadStockHistory(symbol, days);
					} else {
						downLoadStockHistory(symbol, days);
					}
					System.out.println("download history done for stock "
							+ symbol);

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

			} else {
				System.out.println(symbol + " already downloaded...");
			}

			lc++;
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

	public static void CalculatedDownloadedStocks(long lastIndex,
			int startFolder, int endFolder, boolean withCalculation,
			boolean deleteSomeRecords) {

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
				stocks = SQLOperation2.getCalculationReadyStocks();

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
					boolean done = SQLOperation2.isCalculationDone(symbol);
					try {
						if (!done) { // if(!done) start
							if (deleteSomeRecords) {
								SQLOperation2.deletePartialRecord(symbol,
										lastIndex);
							}
							for (int folder = startFolder; folder <= endFolder; folder++) { // for
																							// loop
																							// start
								// downLoadStockHistory(symbol);
								int rs = IndexHistory
										.insertGoogleIntradayData(symbol,
												folder,sfn, symbol, lastIndex);
								if (rs>0) {
									SQLOperation2.getBothEnds(symbol);
									SQLOperation2.padIntradayRecords(symbol,
											SQLOperation2.getEndIndexLast());
									System.out
											.println(count
													+ " insert history done for stock done "
													+ symbol);
									count++;

									if (withCalculation
											&& !SQLOperation2
													.isCalculationDone(symbol)) {
										// SQLOperation.padIntradayRecords(symbol,
										// currentMaxIndex);
										IndexHistory.SingleStockRecalulation(
												symbol, lastIndex);
										SQLOperation2
												.setCalculationDoneTag(symbol);
									}
								} else {
									SQLOperation2.setCalculationDoneTag(symbol);
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

	public static void insertAllStocks(long lastIndex, int startFolder,
			int endFolder, boolean withCalculation, boolean deleteSomeRecords) {
		Hashtable stocks = SQLOperation2.getCurrentAllStocks(lastIndex);

		Enumeration en = stocks.keys();

		int lc = 1;
		long t1 = System.currentTimeMillis();

		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			boolean done = SQLOperation2.isCalculationDone(symbol);

			System.out.println(lc + " start insert " + symbol);
			try {
				if (!done) {
					if (deleteSomeRecords) {
						SQLOperation2.deletePartialRecord(symbol, lastIndex);
					}
					for (int folder = startFolder; folder <= endFolder; folder++) {
						// downLoadStockHistory(symbol);
						long currentMaxIndex = SQLOperation2
								.getCurrentMaxIndex(symbol);
						int rs = IndexHistory
								.insertGoogleIntradayData(symbol, folder,sfn,
										symbol, currentMaxIndex);
						if (rs>0) {
							SQLOperation2.padIntradayRecords(symbol,
									currentMaxIndex);
							System.out.println(lc
									+ " insert history done for stock done "
									+ symbol);
							lc++;

							if (withCalculation
									&& !SQLOperation2.isCalculationDone(symbol)) {
								// SQLOperation.padIntradayRecords(symbol,
								// currentMaxIndex);
								IndexHistory.SingleStockRecalulation(symbol,
										currentMaxIndex);
								SQLOperation2.setCalculationDoneTag(symbol);
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

	public static void recalculateBBScore(long lastIndex) {
		
		if(myStocks==null){
			//tag position and value, count = 1906
			myStocks= SQLOperation2.getMyStocks(16476L, 996);
		}
		Hashtable stocksTemp = SQLOperation2.getCurrentAllStocks(lastIndex);
		
		Enumeration ent = stocksTemp.keys();
		
		Hashtable stocks = new Hashtable();
		
		while(ent.hasMoreElements()){
			String nextStock = ent.nextElement().toString();
			if(myStocks!=null&&myStocks.containsKey(nextStock)){
				stocks.put(nextStock, nextStock);
			}
		}

		Enumeration en = stocks.keys();
		
		

		int lc = 0;
		long t1 = System.currentTimeMillis();
		int folderNumber = 0;

		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			// SQLOperation.calculateBBScore(symbol, lastIndex, false);
			// SQLOperation.overlayBBScore(symbol, lastIndex);

			if (!SQLOperation2.isReCalculationDone(symbol)) {
				IndexHistory.SingleStockRecalulation2(symbol, 0L);
				SQLOperation2.setReCalculationDoneTag(symbol);
			}

			lc++;
			System.out.println(lc + " stock OVERLAY BBSCore done out of "
					+ stocks.size() + " stock " + symbol);
			try{
				Thread.sleep(3000);
			}catch(Exception ex){
				
			}
		}

	}

	public static void testSingleStock(String symbol) throws Exception {
		// downLoadStockHistory(symbol);
		// IndexHistory.insertGoogleIntradayData(symbol);
		// Hashtable stocks = Files.getRealGoogleIntradayHistory(symbol);
		// IndexHistory.insertGoogleIntradayData(symbol);
		//SQLOperation.padIntradayRecords(symbol, 0);
		Hashtable stocks = SQLOperation2.getUnCalculationReadyStocks(24735,24813);

		Enumeration en = stocks.keys();

		while (en.hasMoreElements() ) {
			symbol = en.nextElement().toString();
			
		IndexHistory.SingleStockRecalulation(symbol, 0l);
		}

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

		//SQLOperation2.setDownloadCompleteTag(symbol);

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
