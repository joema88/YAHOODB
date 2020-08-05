package com.yahoo.ptma.helper;

/**
 * @author MaY
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */

import java.util.*;
import java.io.*;

import com.yahoo.ptma.sql.*;

public class Files {
	private static BufferedReader in = null;

	public static void main(String[] args) {

		getIndexHistory("^NZ50");
		// getEODRecords("NASDAQ_20151113.csv");
		// writeToFile("C://stocks//process.txt","Daily Task finished at "+Calendar.getInstance().getTime().toLocaleString());

	}

	static public void spitNoNeededStocks(Hashtable stocks) {
		String path = "C:\\stock\\yahoo\\addRemovedStocks.remove";
		File f = new File(path);
		
		if(!f.exists()){
			try{
			f.createNewFile();
			}catch(Exception ex){
				
			}
		}

		Enumeration en = stocks.keys();
		
		while(en.hasMoreElements()){
			String stk = en.nextElement().toString();
			String val = stocks.get(stk).toString();
		    String dis = "excludedStocks.put(\"" + stk + "\","
				+ "\"" + val+ "\");";
		    Files.appendToFile(path, dis);
		}
	}
	
	
	static public void createEODTagFile(String doneFile) {
		String path = "C:\\stock\\yahoo\\" + doneFile;
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception ex) {

			}
		}
	}

	public static void writeToFile(String path, String content) {
		try {
			FileOutputStream out = new FileOutputStream(path, false);
			out.write(("\n" + content).getBytes());
			out.close();
		} catch (IOException e) {
		}

	}

	public static void appendToFile(String path, String content) {
		try {
			FileOutputStream out = new FileOutputStream(path, true);
			out.write(("\n" + content).getBytes());
			out.close();
		} catch (IOException e) {
		}

	}
	
	public static void appendToEODFile( String content) {
		try {
			String path="C:\\stock\\EOD\\BBSCORE.txt";
			
			FileOutputStream out = new FileOutputStream(path, true);
			out.write(("\n" + content).getBytes());
			out.close();
		} catch (IOException e) {
		}

	}

	public static void appendToIPOBullFile( String content) {
		try {
			String path="C:\\stock\\YAHOO\\IPO.BULL";
			
			FileOutputStream out = new FileOutputStream(path, true);
			out.write(("\n" + content).getBytes());
			out.close();
		} catch (IOException e) {
		}

	}
	
	public static void appendToMegaUTurnFile( String content) {
		try {
			String path="C:\\stock\\YAHOO\\Mega.UTURN";
			
			FileOutputStream out = new FileOutputStream(path, true);
			out.write(("\n" + content).getBytes());
			out.close();
		} catch (IOException e) {
		}

	}
	//EOD_STEP44.txt
	static public boolean dailyCalDoneFile() {
		boolean result = false;
		String path1 = "C:\\stock\\yahoo\\EOD_STEP44.txt";
		

		while (!result) {
			File file1 = new File(path1);
			while (!file1.exists()) {
				try {
					// sleep for 5 minutes
					Thread.sleep(300000);
				} catch (Exception ex) {

				}
			}

			if (file1.exists()) {
				result = true;
			}

		}

		return result;
	}
	
	static public boolean intradayDoneFile() {
		boolean result = false;
		String path1 = "C:\\stock\\yahoo\\EndParallelCalculation1.txt";
		String path2 = "C:\\stock\\yahoo\\EndParallelCalculation2.txt";
		String path3 = "C:\\stock\\yahoo\\EndParallelCalculation3.txt";
		String path4 = "C:\\stock\\yahoo\\EndParallelCalculation4.txt";
		String path5 = "C:\\stock\\yahoo\\EndParallelCalculation5.txt";
		String path6 = "C:\\stock\\yahoo\\EndParallelCalculation6.txt";


		while (!result) {
			File file1 = new File(path1);
			File file2 = new File(path2);
			File file3 = new File(path3);
			File file4 = new File(path4);
			File file5 = new File(path5);
			File file6 = new File(path6);
			while (!file1.exists()||!file2.exists()||!file3.exists()||!file4.exists()||!file5.exists()||!file6.exists()) {
				try {
					// sleep for 5 minutes
					Thread.sleep(300000);
				} catch (Exception ex) {

				}
			}

			if (file1.exists()&&file2.exists()&&file3.exists()&&file4.exists()&&file5.exists()) {
				result = true;
			}

		}

		return result;

	}

	static public void deleteIntradyDoneFile() {
		String path = "C:\\stock\\yahoo\\intradayDone.txt";
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}

	static public boolean historyFileExists(String symbol) {
		boolean result = false;
		String path = "C:\\stock\\yahoo\\" + symbol + ".csv";
		File file = new File(path);
		if (file.exists())
			result = true;

		return result;

	}

	static public Hashtable getIndexHistory(String symbol) {
		String path = "C:\\stock\\yahoo\\" + symbol + ".csv";
		Hashtable result = new Hashtable();
		try {
			int count = 0;
			String index = null;
			boolean read = true;
			String symb = "";
			float divs = 0.0f;
			while (read) {
				try {
					index = Files.readLineFromFile(path);
				} catch (Exception ex) {
					ex.printStackTrace(System.out);
					// ingnore no file
				}
				if (index == null) {
					read = false;
				} else {
					if (index.indexOf("Date") < 0 && index.length() > 10) {
						count++;
						// System.out.println("I " + count);

						StringTokenizer tok = new StringTokenizer(index, ",\t");
						YStock detail = new YStock();
						detail.setSymbol(symbol.toUpperCase());
						String dateString = tok.nextToken().trim();
						StringTokenizer tok2 = new StringTokenizer(dateString,
								"-/");
						String y = tok2.nextToken();
						String m = tok2.nextToken();
						if (Integer.parseInt(m) > 9) {
							m = "" + Integer.parseInt(m);
						} else {
							m = "0" + Integer.parseInt(m);
						}

						String d = tok2.nextToken();
						if (Integer.parseInt(d) > 9) {
							d = "" + Integer.parseInt(d);
						} else {
							d = "0" + Integer.parseInt(d);
						}

						detail.setDate(m + "-" + d + "-" + y);
						// detail.setTradingDate(dateString.replaceAll("/",
						// "-"));
						detail.setOpenPrice(Float.parseFloat(tok.nextToken()
								.trim()));
						detail.setHighPrice(Float.parseFloat(tok.nextToken()
								.trim()));
						detail.setLowPrice(Float.parseFloat(tok.nextToken()
								.trim()));

						detail.setFinalPrice(Float.parseFloat(tok.nextToken()
								.trim()));

						detail.setVolume(Float.parseFloat(tok.nextToken()
								.trim()));

						detail.setAdjustedPrice(Float.parseFloat(tok
								.nextToken().trim()));

						// deal with yahoo adjusted for dividens calculation
						// at least to keep most recent one correct
						if (detail.getAdjustedPrice() < detail.getFinalPrice()) {
							if ((divs + detail.getAdjustedPrice()) * 1.10 > detail
									.getFinalPrice()) {
								divs = divs + detail.getFinalPrice()
										- detail.getAdjustedPrice();
								detail.setAdjustedPrice(detail.getFinalPrice());
							}
						}

						result.put("" + count, detail);
					}

				}
			}
			// System.out.println("total index data count: " + count);
		} catch (Exception ex) {
			try {
				in.close();
				in = null;
				result = null;
				ex.printStackTrace(System.out);
			} catch (Exception ex2) {

			}

		}
		return result;
	}

	static public Hashtable getEODRecordsIndex(String date) {
		String filePath = "INDEX_" + date + ".csv";

		return getEODRecords(filePath);
	}

	static public Hashtable getEODRecordsAMX(String date) {
		String filePath = "AMEX_" + date + ".csv";

		return getEODRecords(filePath);
	}

	static public Hashtable getEODRecordsNYSE(String date) {
		String filePath = "NYSE_" + date + ".csv";

		return getEODRecords(filePath);
	}

	static public Hashtable getEODRecordsNASDAQ(String date) {
		String filePath = "NASDAQ_" + date + ".csv";

		return getEODRecords(filePath);
	}

	static public Hashtable getEODRecords(String fileName) {
		String path = "C:\\stock\\eod\\" + fileName;
		Hashtable result = new Hashtable();
		try {
			int count = 0;
			String index = null;
			boolean read = true;
			String symb = "";
			String ds = fileName.substring(fileName.indexOf("_") + 1);
			if (ds.indexOf(".") > 0) {
				ds = ds.substring(0, ds.indexOf("."));
			}
			String y = ds.substring(0, 4);
			String m = ds.substring(4, 6);
			String d = ds.substring(6, 8);

			while (read) {
				try {
					index = Files.readLineFromFile(path);
				} catch (Exception ex) {
					ex.printStackTrace(System.out);
					// ingnore no file
				}
				if (index == null) {
					read = false;
				} else {
					count++;
					// System.out.println("I " + count);

					StringTokenizer tok = new StringTokenizer(index, ",\t");
					YStock detail = new YStock();
					String symbol = tok.nextToken().trim();
					detail.setSymbol(symbol.toUpperCase());
					String dateString = tok.nextToken().trim();

					// detail.setDate(dateString);
					detail.setDate(m + "-" + d + "-" + y);
					// detail.setTradingDate(dateString.replaceAll("/",
					// "-"));
					detail.setOpenPrice(Float
							.parseFloat(tok.nextToken().trim()));
					detail.setHighPrice(Float
							.parseFloat(tok.nextToken().trim()));
					detail.setLowPrice(Float.parseFloat(tok.nextToken().trim()));

					float finalPrice = Float.parseFloat(tok.nextToken().trim());
					detail.setFinalPrice(finalPrice);

					detail.setVolume(Float.parseFloat(tok.nextToken().trim()));

					detail.setAdjustedPrice(finalPrice);
					// SQLOperation.insertRecord(10650, detail);

					result.put(symbol, detail);
				}

			}

			// System.out.println("total index data count: " + count);
		} catch (Exception ex) {
			try {
				in.close();
				in = null;
				ex.printStackTrace(System.out);
			} catch (Exception ex2) {

			}

		}
		return result;
	}

	public static Hashtable getOptionStocks(String path) throws Exception {
		Hashtable symbols = new Hashtable();
		String result = null;

		if (in == null)
			in = new BufferedReader(new FileReader(path));
		boolean cont = true;

		while (cont) {
			result = in.readLine();
			if (result == null) {
				cont = false;
				in.close();
				in = null;
				break;
			} else {
				symbols.put(result, result);
			}

		}
		return symbols;
	}

	public static String readLineFromFile(String path) throws Exception {
		String result = null;

		if (in == null)
			in = new BufferedReader(new FileReader(path));
		result = in.readLine();

		if (result == null) {
			in.close();
			in = null;
		}

		return result;
	}

}
