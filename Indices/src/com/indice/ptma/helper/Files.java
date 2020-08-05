package com.indice.ptma.helper;

/**
 * @author MaY
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */

import java.util.*;
import java.io.*;

import com.indice.ptma.sql.*;

public class Files {
	
	private static BufferedReader in = null;

	public static void main(String[] args) {

		parseWJ(2018);
		// System.out.println(getIndexHistory("^GDAXI").size());
		// getEODRecords("NASDAQ_20151113.csv");
		// writeToFile("C://stocks//process.txt","Daily Task finished at "+Calendar.getInstance().getTime().toLocaleString());

	}

	static public void spitNoNeededStocks(Hashtable stocks) {
		String path = "C:\\stock\\yahoo\\addRemovedStocks.remove";
		File f = new File(path);

		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (Exception ex) {

			}
		}

		Enumeration en = stocks.keys();

		while (en.hasMoreElements()) {
			String stk = en.nextElement().toString();
			String val = stocks.get(stk).toString();
			String dis = "excludedStocks.put(\"" + stk + "\"," + "\"" + val
					+ "\");";
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

	public static void appendToEODFile(String content) {
		try {
			String path = "C:\\stock\\EOD\\BBSCORE.txt";

			FileOutputStream out = new FileOutputStream(path, true);
			out.write(("\n" + content).getBytes());
			out.close();
		} catch (IOException e) {
		}

	}

	static public boolean intradayDoneFile() {
		boolean result = false;
		String path1 = "C:\\stock\\yahoo\\EndParallelCalculation1.txt";
		String path2 = "C:\\stock\\yahoo\\EndParallelCalculation2.txt";
		String path3 = "C:\\stock\\yahoo\\EndParallelCalculation3.txt";

		while (!result) {
			File file1 = new File(path1);
			File file2 = new File(path2);
			File file3 = new File(path3);
			while (!file1.exists() || !file2.exists() || !file3.exists()) {
				try {
					// sleep for 5 minutes
					Thread.sleep(300000);
				} catch (Exception ex) {

				}
			}

			if (file1.exists() && file2.exists() && file3.exists()) {
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
		String path = "C:\\stock\\yahoo\\indices\\" + symbol + ".csv";
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
					if (index.indexOf("null") < 0 && index.indexOf("Date") < 0
							&& index.length() > 10) {
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

						detail.setAdjustedPrice(Float.parseFloat(tok
								.nextToken().trim()));

						detail.setVolume(Float.parseFloat(tok.nextToken()
								.trim()));

						/*
						 * // deal with yahoo adjusted for dividens calculation
						 * // at least to keep most recent one correct if
						 * (detail.getAdjustedPrice() < detail.getFinalPrice())
						 * { if ((divs + detail.getAdjustedPrice()) * 1.10 >
						 * detail .getFinalPrice()) { divs = divs +
						 * detail.getFinalPrice() - detail.getAdjustedPrice();
						 * detail.setAdjustedPrice(detail.getFinalPrice()); } }
						 */
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

	public static Hashtable parseWJPage(int year, String fileName) {
		String path = "C:\\stock\\yahoo\\indices\\"+fileName;
		Hashtable result = new Hashtable();
		String dateString = "05-25-2018";
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
					boolean start = false;
					System.out.println(index);
					// need to be configurable on year
					if (index.indexOf(", " + year + " </span>") > 0) {
						// date section
						StringTokenizer tok = new StringTokenizer(index, ", \t");
						System.out.println(tok.nextToken());
						// System.out.println(tok.nextToken());
						String ms = tok.nextToken().toString();
						int month = 1;
						if (ms.equalsIgnoreCase("January")) {
							month = 1;
						} else if (ms.equalsIgnoreCase("February")) {
							month = 2;
						} else if (ms.equalsIgnoreCase("March")) {
							month = 3;
						} else if (ms.equalsIgnoreCase("April")) {
							month = 4;
						} else if (ms.equalsIgnoreCase("May")) {
							month = 5;
						} else if (ms.equalsIgnoreCase("June")) {
							month = 6;
						} else if (ms.equalsIgnoreCase("July")) {
							month = 7;
						} else if (ms.equalsIgnoreCase("August")) {
							month = 8;
						} else if (ms.equalsIgnoreCase("September")) {
							month = 9;
						} else if (ms.equalsIgnoreCase("October")) {
							month = 10;
						} else if (ms.equalsIgnoreCase("November")) {
							month = 11;
						} else if (ms.equalsIgnoreCase("December")) {
							month = 12;
						}

						int date = Integer.parseInt(tok.nextToken());
						System.out.println(month + "-" + date);

						String m = "";
						if (month > 9) {
							m = m + month;
						} else {
							m = "0" + month;
						}

						String d = "";
						if (date > 9) {
							d = "" + date;
						} else {
							d = "0" + date;
						}

						dateString = m + "-" + d + "-" + year;
						System.out.println(dateString);
					}

					String symbol = getSymbolName(index);
					if (index != null && symbol.length() > 3) {
						// System.out.println("I " + count);

						// StringTokenizer tok = new StringTokenizer(index,
						// ",\t");
						YStock detail = new YStock();
						detail.setSymbol(symbol.toUpperCase());
						detail.setOpenPrice(0);

						detail.setDate(dateString);

						index = Files.readLineFromFile(path);
						System.out.println(index);
						String tagEnd = "</td>";
						int end = index.indexOf(tagEnd);
						String tagStart = "<td class=\"num\">";

						String val = index.substring(tagStart.length(), end);

						if (!val.equalsIgnoreCase("...")) {
							count++;
							
							float high = Float.parseFloat(val);
							detail.setHighPrice(high);

							index = Files.readLineFromFile(path);
							end = index.indexOf(tagEnd);

							float low = Float.parseFloat(index.substring(
									tagStart.length(), end));
							detail.setLowPrice(low);

							index = Files.readLineFromFile(path);
							end = index.indexOf(tagEnd);

							tagStart = "<td style=\"font-weight:bold;\" class=\"pnum\">";
							float close = Float.parseFloat(index.substring(
									tagStart.length(), end));
							detail.setFinalPrice(close);
							detail.setAdjustedPrice(close);
							
							//tag from WSJ data
							detail.setFc(3);

							//need a fake value to avoid error?
							detail.setVolume(0);
							
							//how about open price??

							result.put("" + count, detail);
						}
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
	
	public static Hashtable parseWJ(int year) {
		String path = "C:\\stock\\yahoo\\indices\\SPY3.HTML";
		Hashtable result = new Hashtable();
		String dateString = "05-25-2018";
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
					boolean start = false;
					System.out.println(index);
					// need to be configurable on year
					if (index.indexOf(", " + year + " </span>") > 0) {
						// date section
						StringTokenizer tok = new StringTokenizer(index, ", \t");
						System.out.println(tok.nextToken());
						// System.out.println(tok.nextToken());
						String ms = tok.nextToken().toString();
						int month = 1;
						if (ms.equalsIgnoreCase("January")) {
							month = 1;
						} else if (ms.equalsIgnoreCase("February")) {
							month = 2;
						} else if (ms.equalsIgnoreCase("March")) {
							month = 3;
						} else if (ms.equalsIgnoreCase("April")) {
							month = 4;
						} else if (ms.equalsIgnoreCase("May")) {
							month = 5;
						} else if (ms.equalsIgnoreCase("June")) {
							month = 6;
						} else if (ms.equalsIgnoreCase("July")) {
							month = 7;
						} else if (ms.equalsIgnoreCase("August")) {
							month = 8;
						} else if (ms.equalsIgnoreCase("September")) {
							month = 9;
						} else if (ms.equalsIgnoreCase("October")) {
							month = 10;
						} else if (ms.equalsIgnoreCase("November")) {
							month = 11;
						} else if (ms.equalsIgnoreCase("December")) {
							month = 12;
						}

						int date = Integer.parseInt(tok.nextToken());
						System.out.println(month + "-" + date);

						String m = "";
						if (month > 9) {
							m = m + month;
						} else {
							m = "0" + month;
						}

						String d = "";
						if (date > 9) {
							d = "" + date;
						} else {
							d = "0" + date;
						}

						dateString = m + "-" + d + "-" + year;
						System.out.println(dateString);
					}

					String symbol = getSymbolName(index);
					if (index != null && symbol.length() > 3) {
						// System.out.println("I " + count);

						// StringTokenizer tok = new StringTokenizer(index,
						// ",\t");
						YStock detail = new YStock();
						detail.setSymbol(symbol.toUpperCase());
						detail.setOpenPrice(0);

						detail.setDate(dateString);

						index = Files.readLineFromFile(path);
						System.out.println(index);
						String tagEnd = "</td>";
						int end = index.indexOf(tagEnd);
						String tagStart = "<td class=\"num\">";

						String val = index.substring(tagStart.length(), end);

						if (!val.equalsIgnoreCase("...")) {
							count++;
							
							float high = Float.parseFloat(val);
							detail.setHighPrice(high);

							index = Files.readLineFromFile(path);
							end = index.indexOf(tagEnd);

							float low = Float.parseFloat(index.substring(
									tagStart.length(), end));
							detail.setLowPrice(low);

							index = Files.readLineFromFile(path);
							end = index.indexOf(tagEnd);

							tagStart = "<td style=\"font-weight:bold;\" class=\"pnum\">";
							float close = Float.parseFloat(index.substring(
									tagStart.length(), end));
							detail.setFinalPrice(close);
							detail.setAdjustedPrice(close);
							
							//tag from WSJ data
							detail.setFc(3);

							//need a fake value to avoid error?
							detail.setVolume(0);
							
							//how about open price??

							result.put("" + count, detail);
						}
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

	public static Hashtable getIndices() {
		Hashtable results = new Hashtable();
		results.put("^GDOW", "^GDOW");
		results.put("GDOWEU", "GDOWEU");
		results.put("^W1DOW", "^W1DOW");
		results.put("^W2DOW", "^W2DOW");
		results.put("^AORD", "^AORD");
		results.put("^AXJO", "^AXJO");
		results.put("^DJCN", "^DJCN");
		results.put("^SSEC", "^SSEC");
		results.put("^HSI", "^HSI");
		results.put("^BSESN", "^BSESN");
		results.put("^JKSE", "^JKSE");
		results.put("^N300", "^N300");
		results.put("^N225", "^N225");
		results.put("JPNT", "JPNT");
		results.put("^KLSE", "^KLSE");
		results.put("^NZ50", "^NZ50");
		results.put("PSEI.PS", "PSEI.PS");
		results.put("^STI", "^STI");
		results.put("^KS11", "^KS11");
		results.put("^CSE", "^CSE");
		results.put("^TWII", "^TWII");
		results.put("^SET.BK", "^SET.BK");
		results.put("^STOXX", "^STOXX");
		results.put("STO50", "STO50");
		results.put("^STOXX50E", "^STOXX50E");
		results.put("STOEU", "STOEU");
		results.put("^ATX", "^ATX");
		results.put("^BFX", "^BFX");
		results.put("^NQCZT", "^NQCZT");
		results.put("^OMXC20", "^OMXC20");
		results.put("^OMXHPI", "^OMXHPI");
		results.put("^FCHI", "^FCHI");
		results.put("^GDAXI", "^GDAXI");
		results.put("^BUX", "^BUX");
		results.put("FTSEMIB.MI", "FTSEMIB.MI");
		results.put("^AEX", "^AEX");
		results.put("^OSEAX", "^OSEAX");
		results.put("WIGP", "WIGP");
		results.put("PSI20.LS", "PSI20.LS");
		results.put("RTSI.ME", "RTSI.ME");
		results.put("^IBX", "^IBX");
		results.put("OMXSWED", "OMXSWED");
		results.put("^SSMI", "^SSMI");
		results.put("XU100.IS", "XU100.IS");
		results.put("^FTSE", "^FTSE");
		results.put("^FTMC", "^FTMC");
		results.put("^A1DOW", "^A1DOW");
		results.put("^MERV", "^MERV");
		results.put("^BVSP", "^BVSP");
		results.put("^GSPTSE", "^GSPTSE");
		results.put("^IPSA", "^IPSA");
		results.put("^MXX", "^MXX");
		results.put("^TA100", "^TA100");
		results.put("J203.L", "J203.L");

		return results;
	}

	public static String getSymbolName(String input) {
		String symbol = "";

		if (input.indexOf("The Global Dow</span> (World)") >= 0) {
			symbol = "^GDOW"; // YAHOO
		} else if (input.indexOf("The Global Dow Euro</span> (World)") >= 0) {
			// COULD BE IGNORED AS IT IS THE SAME AS ABOVE
			symbol = "GDOWEU"; // made up
		} else if (input.indexOf("DJ Global Index</span> (World)") >= 0) {
			symbol = "^W1DOW"; // YAHOO
		} else if (input.indexOf("DJ Global ex U.S.</span> (World)") >= 0) {
			symbol = "^W2DOW"; // YAHOO
		} else if (input.indexOf("All Ordinaries</span> (Australia)") >= 0) {
			symbol = "^AORD"; // YAHOO
		} else if (input.indexOf("S &amp; P/ASX 200</span> (Australia)") >= 0) {
			symbol = "^AXJO"; // YAHOO
		} else if (input.indexOf("Dow Jones China 88</span> (China)") >= 0) {
			symbol = "^DJCN"; // YAHOO
		} else if (input.indexOf("Shanghai Composite</span> (China)") >= 0) {
			symbol = "^SSEC"; // YAHOO
		} else if (input.indexOf("Hang Seng</span> (Hong Kong)") >= 0) {
			symbol = "^HSI"; // YAHOO
		} else if (input.indexOf("S &amp; P BSE Sensex</span> (India)") >= 0) {
			symbol = "^BSESN"; // YAHOO
		} else if (input.indexOf("Jakarta Composite</span> (Indonesia)") >= 0) {
			symbol = "^JKSE"; // YAHOO
		} else if (input.indexOf("Nikkei 300</span> (Japan)") >= 0) {
			// MAYBE IGNORED AS CLOSELY TRACKED BY ^N225
			symbol = "^N300"; // YAHOO
		} else if (input.indexOf("Nikkei Stock Avg</span> (Japan)") >= 0) {
			symbol = "^N225"; // YAHOO
		} else if (input.indexOf("Topix Index</span> (Japan)") >= 0) {
			// MAYBE IGNORED AS CLOSELY TRACKED BY ^N225
			symbol = "JPNT"; // made up
		} else if (input.indexOf("FTSE Bursa Malaysia KLCI</span> (Malaysia)") >= 0) {
			symbol = "^KLSE"; // YAHOO
		} else if (input.indexOf("S &amp; P/NZX 50</span> (New Zealand)") >= 0) {
			symbol = "^NZ50"; // YAHOO
		} else if (input.indexOf("PSEi</span> (Philippines)") >= 0) {
			symbol = "PSEI.PS"; // YAHOO
		} else if (input.indexOf("Straits Times</span> (Singapore)") >= 0) {
			symbol = "^STI"; // YAHOO
		} else if (input.indexOf("Kospi</span> (South Korea)") >= 0) {
			symbol = "^KS11"; // YAHOO
		} else if (input.indexOf("Colombo Stock Exchange</span> (Sri Lanka)") >= 0) {
			symbol = "^CSE"; // YAHOO, ??
		} else if (input.indexOf("Weighted</span> (Taiwan)") >= 0) {
			symbol = "^TWII"; // YAHOO
		} else if (input.indexOf("SET</span> (Thailand)") >= 0) {
			symbol = "^SET.BK"; // YAHOO
		} else if (input.indexOf("Stoxx Europe 600</span> (Europe)") >= 0) {
			symbol = "^STOXX"; // YAHOO
		} else if (input.indexOf("Stoxx Europe 50</span> (Europe)") >= 0) {
			symbol = "STO50"; // made up
		} else if (input.indexOf("Euro Stoxx 50</span> (Euro zone)") >= 0) {
			symbol = "^STOXX50E"; // YAHOO
		} else if (input.indexOf("Euro Stoxx</span> (Euro zone)") >= 0) {
			symbol = "STOEU"; // made up
		} else if (input.indexOf("ATX</span> (Austria)") >= 0) {
			symbol = "^ATX"; // YAHOO
		} else if (input.indexOf("Bel-20</span> (Belgium)") >= 0) {
			symbol = "^BFX"; // YAHOO
		} else if (input.indexOf("Prague PX</span> (Czech Republic)") >= 0) {
			symbol = "^NQCZT"; // YAHOO
		} else if (input.indexOf("OMX Copenhagen</span> (Denmark)") >= 0) {
			symbol = "^OMXC20"; // made up,?? RATIO??
		} else if (input.indexOf("OMX Helsinki</span> (Finland)") >= 0) {
			symbol = "^OMXHPI"; // YAHOO
		} else if (input.indexOf("CAC 40</span> (France)") >= 0) {
			symbol = "^FCHI"; // /YAHOO
		} else if (input.indexOf("DAX</span> (Germany)") >= 0) {
			symbol = "^GDAXI"; // YAHOO
		} else if (input.indexOf("BUX</span> (Hungary)") >= 0) {
			symbol = "^BUX"; // YAHOO
		} else if (input.indexOf("FTSE MIB</span> (Italy)") >= 0) {
			symbol = "FTSEMIB.MI"; // YAHOO
		} else if (input.indexOf("AEX</span> (Netherlands)") >= 0) {
			symbol = "^AEX"; // YAHOO
		} else if (input.indexOf("Oslo Bors All Share</span> (Norway)") >= 0) {
			symbol = "^OSEAX"; // YAHOO
		} else if (input.indexOf("WIG</span> (Poland)") >= 0) {
			symbol = "WIGP"; // made up
		} else if (input.indexOf("PSI 20</span> (Portugal)") >= 0) {
			symbol = "PSI20.LS"; // YAHOO
		} else if (input.indexOf("RTS Index</span> (Russia)") >= 0) {
			symbol = "RTSI.ME"; // YAHOO
		} else if (input.indexOf("IBEX 35</span> (Spain)") >= 0) {
			symbol = "^IBX"; // YAHOO
		} else if (input.indexOf("OMX Stockholm</span> (Sweden)") >= 0) {
			symbol = "OMXSWED"; // made up
		} else if (input.indexOf("Swiss Market</span> (Switzerland)") >= 0) {
			symbol = "^SSMI"; // YAHOO
		} else if (input.indexOf("BIST 100</span> (Turkey)") >= 0) {
			symbol = "XU100.IS"; // YAHOO
		} else if (input.indexOf("FTSE 100</span> (U.K.)") >= 0) {
			symbol = "^FTSE"; // YAHOO
		} else if (input.indexOf("FTSE 250</span> (U.K.)") >= 0) {
			symbol = "^FTMC"; // YAHOO
		} else if (input.indexOf("DJ Americas</span> (Americas)") >= 0) {
			symbol = "^A1DOW"; // YAHOO
		} else if (input.indexOf("Merval</span> (Argentina)") >= 0) {
			symbol = "^MERV"; // YAHOO
		} else if (input.indexOf("Sao Paulo Bovespa</span> (Brazil)") >= 0) {
			symbol = "^BVSP"; // YAHOO
		} else if (input.indexOf("S &amp; P/TSX Comp</span> (Canada)") >= 0) {
			symbol = "^GSPTSE"; // YAHOO
		} else if (input.indexOf("Santiago IPSA</span> (Chile)") >= 0) {
			symbol = "^IPSA"; // YAHOO-->made up, NEED CONVERT RATIO??
		} else if (input.indexOf("IPC All-Share</span> (Mexico)") >= 0) {
			symbol = "^MXX"; // YAHOO
		} else if (input.indexOf("Tel Aviv</span> (Israel)") >= 0) {
			symbol = "^TA100"; // YAHOO-->made up, NEED CONVERT RATIO??
		} else if (input.indexOf("FTSE/JSE All-Share</span> (South Africa)") >= 0) {
			symbol = "J203.L"; // YAHOO
		}

		return symbol;
	}

}