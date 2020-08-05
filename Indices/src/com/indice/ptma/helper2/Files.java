package com.indice.ptma.helper2;

/**
 * @author MaY
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */

import java.util.*;
import java.io.*;

import com.indice.ptma.sql2.*;

public class Files {
	
	private static BufferedReader in = null;

	public static void main(String[] args) {
		// pad old records or create new records with different symobols
		// reprocessYahooIndexHistory();
		// pad old index records for missing days
		// padAllYahooIndexHistory();

		// parseWJ(2018);
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
		String path = "C:\\stock\\yahoo\\indices\\" + fileName;
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

							try {
								tagStart = "<td style=\"font-weight:bold;\" class=\"pnum\">";
								float close = Float.parseFloat(index.substring(
										tagStart.length(), end));
								detail.setFinalPrice(close);
								detail.setAdjustedPrice(close);
							} catch (Exception ex) {

							} finally {
								if ((detail.getLowPrice() == detail
										.getHighPrice())
										&& detail.getHighPrice() > 0.1) {
									detail.setFinalPrice(detail.getHighPrice());
									detail.setAdjustedPrice(detail
											.getHighPrice());
								}
							}

							// tag from WSJ data
							detail.setFc(3);

							// need a fake value to avoid error?
							detail.setVolume(0);

							// how about open price??

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

							// tag from WSJ data
							detail.setFc(3);

							// need a fake value to avoid error?
							detail.setVolume(0);

							// how about open price??

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

	public static String getSymbolFromFileName(String input) {
		String symbol = "";

		if (input.indexOf("Dow Jones Industrial Average") >= 0) {
			symbol = "^DJI"; // YAHOO, Dow 30
		} else if (input.indexOf("S&P 500") >= 0) {
			symbol = "^GSPC"; // YAHOO S&P 500
		} else if (input.indexOf("NASDAQ") >= 0) {
			symbol = "^IXIC"; // YAHOO, NASDAQ
		} else if (input.indexOf("US SmallCap") >= 0) {
			symbol = "^RUT"; // Russel 2000
		} else if (input.indexOf("CBOE") >= 0) {
			symbol = "^VIX"; // YAHOO, S&P 500 VIX
		} else if (input.indexOf("S&P_TSX") >= 0) {
			symbol = "^GSPTSE"; // Yahoo Canada Toronto index
		} else if (input.indexOf("Bovespa") >= 0) {
			symbol = "^BVSP"; // YAHOO, Brazil Index
		} else if (input.indexOf("S&P_BMV") >= 0) {
			symbol = "^MXX"; // YAHOO, Mexico Index
		} else if (input.indexOf("DAX") >= 0) {
			symbol = "^GDAXI"; // YAHOO, German Index
		} else if (input.indexOf("FTSE Data") >= 0) {
			symbol = "^FTSE"; // YAHOO, England index
		} else if (input.indexOf("CAC") >= 0) {
			symbol = "^FCHI"; // /YAHOO, French Index
		} else if (input.indexOf("Euro Stoxx 50") >= 0) {
			symbol = "^STOXX50E"; // YAHOO, EURO 50 stocks index
		} else if (input.indexOf("AEX") >= 0) {
			symbol = "^AEX"; // YAHOO, Amsterdam Exchange index, Holland
		} else if (input.indexOf("IBX 35") >= 0) {
			symbol = "^IBEX"; // YAHOO, index of the Bolsa de Madrid, Spain
		} else if (input.indexOf("FTSE MIB Data") >= 0) {
			symbol = "FTSEMIB.MI"; // YAHOO, Italy Index
		} else if (input.indexOf("SMI") >= 0) {
			symbol = "^SSMI"; // YAHOO Swiss index
		} else if (input.indexOf("PSI") >= 0) {
			symbol = "PSI20.LS"; // YAHOO, Lisbon, Portuguese ibex index
		} else if (input.indexOf("BEL 20") >= 0) {
			symbol = "^BFX"; // YAHOO, Euronext Brussels, Belgium BEL 20
		} else if (input.indexOf("ATX") >= 0) {
			symbol = "^ATX"; // YAHOO, Austrian Traded Index
		} else if (input.indexOf("OMX Stockholm 30") >= 0) {
			symbol = "^OMX"; // Yahoo market index for the Stockholm Stock
								// Exchange
		} else if (input.indexOf("OMX Copenhagen 25") >= 0) {
			symbol = "^OMXC25"; // Yahoo OMX Copenhagen 25 Index
		} else if (input.indexOf("MOEX Russia Historical Data") >= 0) {
			symbol = "IMOEX.ME"; // YAHOO, MOEX Russia Index
		} else if (input.indexOf("RTSI") >= 0) {
			symbol = "RTSI.ME"; // YAHOO, index of 50 Russian stocks traded on
			// the Moscow Exchange,
		} else if (input.indexOf("WIG20 Historical Data") >= 0) {
			symbol = "WIG20"; // Warsaw Stock Exchange, Polland, no Yahoo
		} else if (input.indexOf("Budapest") >= 0) {
			symbol = "^BUX"; // YAHOO, hungary budapest index
		} else if (input.indexOf("XU100") >= 0) {
			symbol = "BIST"; // YAHOO, Istanbul Index of Turkey
		} else if (input.indexOf("TA35") >= 0) {
			symbol = "TA 35 Historical Data"; // YAHOO-->Isreal stock index
		} else if (input.indexOf("Tadawul") >= 0) {// TASI
			symbol = "^TASI.SR"; // YAHOO,Saudi Stock Exchange
		} else if (input.indexOf("Nikkei 225") >= 0) {
			symbol = "^N225"; // YAHOO, Japan Index
		} else if (input.indexOf("S&P_ASX 200") >= 0) {
			symbol = "^AXJO"; // YAHOO, similar to All Ordinaries</span>
								// (Australia)
		} else if (input.indexOf("New Zealand") >= 0) {
			symbol = "^NZDOW"; // similar to ^NZ50 YAHOO New Zealand
		} else if (input.indexOf("Shanghai") >= 0) {
			symbol = "^SSEC"; // YAHOO, Shanghai index //
		} else if (input.indexOf("Hang Seng") >= 0) {
			symbol = "^HSI"; // YAHOO, HONGKONG Index
		} else if (input.indexOf("Taiwan") >= 0) {
			symbol = "^TWII"; // YAHOO, Taiwan index
		} else if (input.indexOf("SET Index") >= 0) {
			symbol = "^SET.BK"; // YAHOO, Thailand
		} else if (input.indexOf("KOSPI") >= 0) {
			symbol = "^KS11"; // YAHOO, South Korean
		} else if (input.indexOf("Jakarta") >= 0) {
			symbol = "^JKSE"; // YAHOO, Indonesia Jakarta Composite
		} else if (input.indexOf("BSE Sensex") >= 0) {
			symbol = "^BSESN"; // YAHOO, Indian stock index
		} else if (input.indexOf("PSEi Composite") >= 0) {
			symbol = "PSEI.PS"; // YAHOO, Philippine
		} else if (input.indexOf("STI") >= 0) {
			symbol = "^STI"; // YAHOO, Singapore stock market index
		} else if (input.indexOf("Karachi") >= 0) {
			symbol = "^KSE"; // YAHOO, Pakistan 100
		} else if (input.indexOf("HNX 30") >= 0) {
			symbol = "HNX30"; // YAHOO, Vietnam
		} else if (input.indexOf("CSE") >= 0) {
			symbol = "^CSE"; // YAHOO, sri lanka colombo stock inde
		} // from below are missing, but we covered every region , so probably
			// not matter much!
		else if (input.indexOf("Merval</span> (Argentina)") >= 0) {
			symbol = "^MERV"; // YAHOO
		} else if (input.indexOf("Santiago IPSA</span> (Chile)") >= 0) {
			symbol = "^IPSA"; // YAHOO-->made up, NEED CONVERT RATIO??
		} else if (input.indexOf("FTSE/JSE All-Share</span> (South Africa)") >= 0) {
			symbol = "J203.L"; // YAHOO
		} else if (input.indexOf("FTSE Bursa Malaysia KLCI</span> (Malaysia)") >= 0) {
			symbol = "^KLSE"; // YAHOO
		} else if (input.indexOf("Prague PX</span> (Czech Republic)") >= 0) {
			symbol = "^NQCZT"; // YAHOO
		} else if (input.indexOf("OMX Helsinki</span> (Finland)") >= 0) {
			symbol = "^OMXHPI"; // YAHOO
		} else if (input.indexOf("Oslo Bors All Share</span> (Norway)") >= 0) {
			symbol = "^OSEAX"; // YAHOO
		}

		return symbol;
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

	public static Hashtable getTrackedIndex() {
		Hashtable indexes = new Hashtable();
		indexes.put("^DJI", "2^DJI");
		indexes.put("^GSPC", "2^GSPC");
		indexes.put("^IXIC", "2^IXIC");
		indexes.put("^RUT", "2^RUT");
		indexes.put("^VIX", "2^VIX");
		indexes.put("^GSPTSE", "2^GSPTSE");
		indexes.put("^BVSP", "2^BVSP");
		indexes.put("^MXX", "2^MXX");
		indexes.put("^GDAXI", "2^GDAXI");
		indexes.put("^FTSE", "2^FTSE");
		indexes.put("^FCHI", "2^FCHI");
		indexes.put("^STOXX50E", "2^STOXX50E");
		indexes.put("^AEX", "2^AEX");
		indexes.put("^IBEX", "2^IBEX");
		indexes.put("FTSEMIB.MI", "FTSEMIB.MX");
		indexes.put("^SSMI", "2^SSMI");
		indexes.put("PSI20.LS", "2PSI20.LS");
		indexes.put("^BFX", "2^BFX");
		indexes.put("^ATX", "2^ATX");
		indexes.put("^OMX", "2^OMX");
		indexes.put("^OMXC25", "2^OMXC25");
		indexes.put("^IMOEX.ME", "2IMOEX.ME");
		indexes.put("RTSI.ME", "2RTSI.ME");
		indexes.put("WIG20", "2WIG20");
		indexes.put("^BUX", "2^BUX");
		indexes.put("XU100.IS", "2XU100.IS");
		indexes.put("TA35.TA", "2TA35.TA");
		indexes.put("^TASI.SR", "2^TASI.SR");
		indexes.put("^N225", "2^N225");
		indexes.put("^AXJO", "2^AXJO");
		indexes.put("^NZDOW", "2^NZDOW");
		indexes.put("^SSEC", "2^SSEC");
		indexes.put("^HSI", "2^HSI");
		indexes.put("^TWII", "2^TWII");
		indexes.put("^SET.BK", "2^SET.BK");
		indexes.put("^KS11", "2^KS11");
		indexes.put("^JKSE", "2^JKSE");
		indexes.put("^BSESN", "2^BSESN");
		indexes.put("PSEI.PS", "2PSEI.PS");
		indexes.put("^STI", "2^STI");
		indexes.put("^KSE", "2^KSE");
		indexes.put("HNX30", "2HNX30");
		indexes.put("^CSE", "2^CSE");

		return indexes;
	}

	public static Hashtable getEarlyFinishedIndex() {
		Hashtable indexes = new Hashtable();
		indexes.put("^DJI", "2^DJI");
		indexes.put("^GSPC", "2^GSPC");
		indexes.put("^IXIC", "2^IXIC");
		indexes.put("^RUT", "2^RUT");
		indexes.put("^VIX", "2^VIX");
		indexes.put("^GSPTSE", "2^GSPTSE");
		indexes.put("^BVSP", "2^BVSP");
		indexes.put("^MXX", "2^MXX");
		indexes.put("^GDAXI", "2^GDAXI");
		indexes.put("^FTSE", "2^FTSE");
		indexes.put("^FCHI", "2^FCHI");
		indexes.put("^STOXX50E", "2^STOXX50E");
		indexes.put("^AEX", "2^AEX");
		indexes.put("^IBEX", "2^IBEX");
		indexes.put("FTSEMIB.MI", "FTSEMIB.MX");
		indexes.put("^SSMI", "2^SSMI");
		indexes.put("PSI20.LS", "2PSI20.LS");
		indexes.put("^BFX", "2^BFX");
		indexes.put("^ATX", "2^ATX");
		indexes.put("^OMX", "2^OMX");
		indexes.put("^OMXC25", "2^OMXC25");
		indexes.put("^IMOEX.ME", "2IMOEX.ME");
		indexes.put("RTSI.ME", "2RTSI.ME");
		indexes.put("WIG20", "2WIG20");
		indexes.put("^BUX", "2^BUX");
		indexes.put("XU100.IS", "2XU100.IS");
		indexes.put("TA35.TA", "2TA35.TA");
		indexes.put("^TASI.SR", "2^TASI.SR");
		indexes.put("^N225", "2^N225");
		indexes.put("^AXJO", "2^AXJO");
		indexes.put("^NZDOW", "2^NZDOW");
		indexes.put("^SSEC", "2^SSEC");
		indexes.put("^HSI", "2^HSI");
		indexes.put("^TWII", "2^TWII");
		indexes.put("^SET.BK", "2^SET.BK");
		indexes.put("^KS11", "2^KS11");
		indexes.put("^JKSE", "2^JKSE");
		indexes.put("^BSESN", "2^BSESN");
		indexes.put("PSEI.PS", "2PSEI.PS");
		indexes.put("^STI", "2^STI");
		indexes.put("^KSE", "2^KSE");
		indexes.put("HNX30", "2HNX30");
		indexes.put("^CSE", "2^CSE");

		return indexes;
	}

	public static Hashtable getLateFishedIndex() {
		Hashtable indexes = new Hashtable();
		indexes.put("^DJI", "2^DJI");
		indexes.put("^GSPC", "2^GSPC");
		indexes.put("^IXIC", "2^IXIC");
		indexes.put("^RUT", "2^RUT");
		indexes.put("^VIX", "2^VIX");
		indexes.put("^GSPTSE", "2^GSPTSE");
		indexes.put("^BVSP", "2^BVSP");
		indexes.put("^MXX", "2^MXX");
		indexes.put("^GDAXI", "2^GDAXI");
		indexes.put("^FTSE", "2^FTSE");
		indexes.put("^FCHI", "2^FCHI");
		indexes.put("^STOXX50E", "2^STOXX50E");
		indexes.put("^AEX", "2^AEX");
		indexes.put("^IBEX", "2^IBEX");
		indexes.put("FTSEMIB.MI", "FTSEMIB.MX");
		indexes.put("^SSMI", "2^SSMI");
		indexes.put("PSI20.LS", "2PSI20.LS");
		indexes.put("^BFX", "2^BFX");
		indexes.put("^ATX", "2^ATX");
		indexes.put("^OMX", "2^OMX");
		indexes.put("^OMXC25", "2^OMXC25");
		indexes.put("^IMOEX.ME", "2IMOEX.ME");
		indexes.put("RTSI.ME", "2RTSI.ME");
		indexes.put("WIG20", "2WIG20");
		indexes.put("^BUX", "2^BUX");
		indexes.put("XU100.IS", "2XU100.IS");
		indexes.put("TA35.TA", "2TA35.TA");
		indexes.put("^TASI.SR", "2^TASI.SR");
		indexes.put("^N225", "2^N225");
		indexes.put("^AXJO", "2^AXJO");
		indexes.put("^NZDOW", "2^NZDOW");
		indexes.put("^SSEC", "2^SSEC");
		indexes.put("^HSI", "2^HSI");
		indexes.put("^TWII", "2^TWII");
		indexes.put("^SET.BK", "2^SET.BK");
		indexes.put("^KS11", "2^KS11");
		indexes.put("^JKSE", "2^JKSE");
		indexes.put("^BSESN", "2^BSESN");
		indexes.put("PSEI.PS", "2PSEI.PS");
		indexes.put("^STI", "2^STI");
		indexes.put("^KSE", "2^KSE");
		indexes.put("HNX30", "2HNX30");
		indexes.put("^CSE", "2^CSE");

		return indexes;
	}

	public static String getSymbolMapName(String input) {
		String symbol = "";

		if (input.indexOf("DJI") >= 0) {
			symbol = "^DJI"; // YAHOO, Dow 30
		} else if (input.indexOf("US500") >= 0) {
			symbol = "^GSPC"; // YAHOO S&P 500
		} else if (input.indexOf("IXIC") >= 0) {
			symbol = "^IXIC"; // YAHOO, NASDAQ
		} else if (input.indexOf("US2000") >= 0) {
			symbol = "^RUT"; // Russel 2000
		} else if (input.indexOf("VIX") >= 0) {
			symbol = "^VIX"; // YAHOO, S&P 500 VIX
		} else if (input.indexOf("TSX") >= 0) {
			symbol = "^GSPTSE"; // Yahoo Canada Toronto index
		} else if (input.indexOf("BVSP") >= 0) {
			symbol = "^BVSP"; // YAHOO, Brazil Index
		} else if (input.indexOf("MXX") >= 0) {
			symbol = "^MXX"; // YAHOO, Mexico Index
		} else if (input.indexOf("DE30") >= 0) {
			symbol = "^GDAXI"; // YAHOO, German Index
		} else if (input.indexOf("UK100") >= 0) {
			symbol = "^FTSE"; // YAHOO, England index
		} else if (input.indexOf("FCHI") >= 0) {
			symbol = "^FCHI"; // /YAHOO, French Index
		} else if (input.indexOf("STOXX50") >= 0) {
			symbol = "^STOXX50E"; // YAHOO, EURO 50 stocks index
		} else if (input.indexOf("AEX") >= 0) {
			symbol = "^AEX"; // YAHOO, Amsterdam Exchange index, Holland
		} else if (input.indexOf("ES35") >= 0) {
			symbol = "^IBEX"; // YAHOO, index of the Bolsa de Madrid, Spain
		} else if (input.indexOf("IT40") >= 0) {
			symbol = "FTSEMIB.MI"; // YAHOO, Italy Index
		} else if (input.indexOf("SWI20") >= 0) {
			symbol = "^SSMI"; // YAHOO Swiss index
		} else if (input.indexOf("PSI20") >= 0) {
			symbol = "PSI20.LS"; // YAHOO, Lisbon, Portuguese ibex index
		} else if (input.indexOf("BFX") >= 0) {
			symbol = "^BFX"; // YAHOO, Euronext Brussels, Belgium BEL 20
		} else if (input.indexOf("ATX") >= 0) {
			symbol = "^ATX"; // YAHOO, Austrian Traded Index
		} else if (input.indexOf("OMXS30") >= 0) {
			symbol = "^OMX"; // Yahoo market index for the Stockholm Stock
								// Exchange
		} else if (input.indexOf("OMXC25CAP") >= 0) {
			symbol = "^OMXC25"; // Yahoo OMX Copenhagen 25 Index
		} else if (input.indexOf("IMOEX") >= 0) {
			symbol = "IMOEX.ME"; // YAHOO, MOEX Russia Index
		} else if (input.indexOf("IRTS") >= 0) {
			symbol = "RTSI.ME"; // YAHOO, index of 50 Russian stocks traded on
			// the Moscow Exchange,
		} else if (input.indexOf("WIG20") >= 0) {
			symbol = "WIG20"; // Warsaw Stock Exchange, Polland, no Yahoo
		} else if (input.indexOf("BUX") >= 0) {
			symbol = "^BUX"; // YAHOO, hungary budapest index
		} else if (input.indexOf("XU100") >= 0) {
			symbol = "XU100.IS"; // YAHOO, Istanbul Index of Turkey
		} else if (input.indexOf("TA35") >= 0) {
			symbol = "TA35.TA"; // YAHOO-->Isreal stock index
		} else if (input.indexOf("TASI") >= 0) {// TASI
			symbol = "^TASI.SR"; // YAHOO,Saudi Stock Exchange
		} else if (input.indexOf("JP225") >= 0) {
			symbol = "^N225"; // YAHOO, Japan Index
		} else if (input.indexOf("AXJO") >= 0) {
			symbol = "^AXJO"; // YAHOO, similar to All Ordinaries</span>
								// (Australia)
		} else if (input.indexOf("NZDOW") >= 0) {
			symbol = "^NZDOW"; // similar to ^NZ50 YAHOO New Zealand
		} else if (input.indexOf("SSEC") >= 0) {
			symbol = "^SSEC"; // YAHOO, Shanghai index //
		} else if (input.indexOf("HK50") >= 0) {
			symbol = "^HSI"; // YAHOO, HONGKONG Index
		} else if (input.indexOf("TWII") >= 0) {
			symbol = "^TWII"; // YAHOO, Taiwan index
		} else if (input.indexOf("SETI") >= 0) {
			symbol = "^SET.BK"; // YAHOO, Thailand
		} else if (input.indexOf("KS11") >= 0) {
			symbol = "^KS11"; // YAHOO, South Korean
		} else if (input.indexOf("JKSE") >= 0) {
			symbol = "^JKSE"; // YAHOO, Indonesia Jakarta Composite
		} else if (input.indexOf("BSESN") >= 0) {
			symbol = "^BSESN"; // YAHOO, Indian stock index
		} else if (input.indexOf("PSI") >= 0) {
			symbol = "PSEI.PS"; // YAHOO, Philippine
		} else if (input.indexOf("STI") >= 0) {
			symbol = "^STI"; // YAHOO, Singapore stock market index
		} else if (input.indexOf("KSE") >= 0) {
			symbol = "^KSE"; // YAHOO, Pakistan 100
		} else if (input.indexOf("HNX30") >= 0) {
			symbol = "HNX30"; // YAHOO, Vietnam
		} else if (input.indexOf("CSE") >= 0) {
			symbol = "^CSE"; // YAHOO, sri lanka colombo stock inde
		} // from below are missing, but we covered every region , so probably
			// not matter much!
		else if (input.indexOf("Merval</span> (Argentina)") >= 0) {
			symbol = "^MERV"; // YAHOO
		} else if (input.indexOf("Santiago IPSA</span> (Chile)") >= 0) {
			symbol = "^IPSA"; // YAHOO-->made up, NEED CONVERT RATIO??
		} else if (input.indexOf("FTSE/JSE All-Share</span> (South Africa)") >= 0) {
			symbol = "J203.L"; // YAHOO
		} else if (input.indexOf("FTSE Bursa Malaysia KLCI</span> (Malaysia)") >= 0) {
			symbol = "^KLSE"; // YAHOO
		} else if (input.indexOf("Prague PX</span> (Czech Republic)") >= 0) {
			symbol = "^NQCZT"; // YAHOO
		} else if (input.indexOf("OMX Helsinki</span> (Finland)") >= 0) {
			symbol = "^OMXHPI"; // YAHOO
		} else if (input.indexOf("Oslo Bors All Share</span> (Norway)") >= 0) {
			symbol = "^OSEAX"; // YAHOO
		}

		return symbol;
	}

	public static Hashtable parseInvestingComHistory(String dateString){
		Hashtable results = new Hashtable();
		
		
			Hashtable dtmap = new Hashtable();
			 dtmap.put("07-20-2020", "Jul 20, 2020");
			 dtmap.put("07-21-2020", "Jul 21, 2020");
			 dtmap.put("07-22-2020", "Jul 22, 2020");
			 dtmap.put("07-23-2020", "Jul 23, 2020");
			 dtmap.put("07-24-2020", "Jul 24, 2020");
			 dtmap.put("07-27-2020", "Jul 27, 2020");
			 dtmap.put("07-28-2020", "Jul 28, 2020");
			 dtmap.put("07-29-2020", "Jul 29, 2020");
			 dtmap.put("07-30-2020", "Jul 30, 2020");
			 dtmap.put("07-31-2020", "Jul 31, 2020");
			 dtmap.put("08-03-2020","Aug 03, 2020");

		String path = "C:\\stock\\yahoo\\investing\\";
		File f = new File(path);

        // Populates the array with names of files and directories
       String[] pathnames = f.list();
       
       for (String pathname : pathnames) {
           // Print the names of files and directories
           System.out.println(pathname);
           if(pathname.indexOf(" Data.csv")>0){
        	  String symbol = getSymbolFromFileName(pathname);
        	  String date2=(String)dtmap.get(dateString);
        	  YStock stock = parseInvestingCom(symbol,dateString,pathname,date2);
        	  results.put(symbol, stock);
           }
       }
		
		return results;
	}

	// "Date","Price","Open","High","Low","Vol.","Change %"
	// "Index","Symbol","Last","High","Low","Chg.","Chg. %","Time"
	public static YStock parseInvestingCom(String symbol, String dateString,
			String fileName, String date2) {
		String path = "C:\\stock\\yahoo\\investing\\" + fileName;
		YStock detail = new YStock();
		detail.setDate(dateString);
		detail.setSymbol(symbol);

		// String dateString = "05-25-2018";
		try {
			int count = 0;
			String index = null;
			boolean read = true;
			String symb = "";
			float divs = 0.0f;
			boolean start = false;

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

					// need to be configurable on year
					if (index.indexOf(date2) >= 0) {
						// date section

						// System.out.println(index);
						// System.out.println("I " + count);
						// "Index","Symbol","Last","High","Low","Chg.","Chg. %","Time"
						// "Dow 30","DJI","25,828.36","25,884.89","25,517.39","-138.97","-0.54%","17:14:47"
						// "MOEX","IMOEX","2,544.43","2,564.38","2,544.43","-20.67","-0.81%","08/05"
						// count++;
						// tok = new StringTokenizer(index, " \t");
						String part1 = index
								.substring(index.indexOf("\",\"") + 3);
						// System.out.println("part1 " + part1);
						String lastPrice = part1.substring(0,
								part1.indexOf("\",\"")).replaceAll(",", "");
						// System.out.println("lastPrice " + lastPrice);
						String part2 = part1
								.substring(part1.indexOf("\",\"") + 3);
						// System.out.println("part2 " + part2);
						String openPrice = part2.substring(0,
								part2.indexOf("\",\"")).replaceAll(",", "");
						// System.out.println("openPrice " + openPrice);
						String part3 = part2
								.substring(part2.indexOf("\",\"") + 3);
						// System.out.println("part3 " + part3);
						String highPrice = part3.substring(0,
								part3.indexOf("\",\"")).replaceAll(",", "");
						// System.out.println("highPrice " + highPrice);
						String part4 = part3
								.substring(part3.indexOf("\",\"") + 3);
						// System.out.println("part4 " + part4);
						String lowPrice = part4.substring(0,
								part4.indexOf("\",\"")).replaceAll(",", "");
						// System.out.println("lowPrice " + lowPrice);
						String part5 = part4
								.substring(part4.indexOf("\",\"") + 3);
						// System.out.println("part5 " + part5);
						String volume = part5.substring(0,
								part5.indexOf("\",\"")).replaceAll(",", "");
						// System.out.println("lowPrice " + volume);

						String part6 = part5
								.substring(part5.indexOf("\",\"") + 3);
						// System.out.println("part6 " + part6);
						String changePercent = part6.replaceAll("\"", "");
						// System.out.println("changePercent " + changePercent);

						detail.setSymbol(symbol.toUpperCase());
						detail.setOpenPrice(0);
						detail.setDate(dateString);

						// System.out.println(highPrice + " " + symbol);
						float high = Float.parseFloat(highPrice);
						detail.setHighPrice(high);

						float low = Float.parseFloat(lowPrice);
						detail.setLowPrice(low);

						float close = Float.parseFloat(lastPrice);
						detail.setFinalPrice(close);
						detail.setAdjustedPrice(close);

						// need a fake value to avoid error?
						detail.setVolume(0);
						Files.in = null;
						break;

					}
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
		return detail;
	}

	// "Date","Price","Open","High","Low","Vol.","Change %"vc --history
	// "Index","Symbol","Last","High","Low","Chg.","Chg. %","Time" --daily
	public static Hashtable parseInvestingCom(String dateString) {
		String path = "C:\\stock\\yahoo\\investing\\Major World Indices.csv";
		Hashtable result = new Hashtable();
		// String dateString = "05-25-2018";
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

					// need to be configurable on year
					if (index.indexOf("Symbol") > 0) {
						// date section

					} else {
						System.out.println(index);
						// System.out.println("I " + count);
						// "Index","Symbol","Last","High","Low","Chg.","Chg. %","Time"
						// "Dow 30","DJI","25,828.36","25,884.89","25,517.39","-138.97","-0.54%","17:14:47"
						// "MOEX","IMOEX","2,544.43","2,564.38","2,544.43","-20.67","-0.81%","08/05"
						// count++;
						// tok = new StringTokenizer(index, " \t");
						String part1 = index
								.substring(index.indexOf("\",\"") + 3);
						System.out.println("Part1 " + part1);
						String symbol = getSymbolMapName(part1.substring(0,
								part1.indexOf("\",\"")));
						System.out.println("symbol " + symbol);

						String part2 = part1
								.substring(part1.indexOf("\",\"") + 3);
						System.out.println("part2 " + part2);
						String lastPrice = part2.substring(0,
								part2.indexOf("\",\"")).replaceAll(",", "");
						System.out.println("lastPrice " + lastPrice);
						String part3 = part2
								.substring(part2.indexOf("\",\"") + 3);
						System.out.println("part3 " + part3);
						String highPrice = part3.substring(0,
								part3.indexOf("\",\"")).replaceAll(",", "");
						System.out.println("highPrice " + highPrice);
						String part4 = part3
								.substring(part3.indexOf("\",\"") + 3);
						System.out.println("part4 " + part4);
						String lowPrice = part4.substring(0,
								part4.indexOf("\",\"")).replaceAll(",", "");
						System.out.println("lowPrice " + lowPrice);
						String part5 = part4
								.substring(part4.indexOf("\",\"") + 3);
						System.out.println("part5 " + part5);
						String changePrice = part5.substring(0,
								part5.indexOf("\",\"")).replaceAll(",", "");
						System.out.println("changePrice " + changePrice);
						String part6 = part5
								.substring(part5.indexOf("\",\"") + 3);
						System.out.println("part6 " + part6);
						String changePercent = part6.substring(0,
								part6.indexOf("\",\"")).replaceAll(",", "");
						System.out.println("changePercent " + changePercent);
						String part7 = part6
								.substring(part6.indexOf("\",\"") + 3);
						System.out.println("part7 " + part7);
						String time = part7.substring(0, part7.indexOf("\""));
						System.out.println("time " + time);

						YStock detail = new YStock();
						detail.setSymbol(symbol.toUpperCase());
						detail.setOpenPrice(0);
						detail.setDate(dateString);

						System.out.println(highPrice + " " + symbol);
						float high = Float.parseFloat(highPrice);
						detail.setHighPrice(high);

						float low = Float.parseFloat(lowPrice);
						detail.setLowPrice(low);

						float close = Float.parseFloat(lastPrice);
						detail.setFinalPrice(close);
						detail.setAdjustedPrice(close);

						// need a fake value to avoid error?
						detail.setVolume(0);
						if (symbol.length() > 1) {
							result.put(symbol, detail);
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

	//
	/*** NEW 5/10/2019 ***/
	public static void padAllYahooIndexHistory() {
		Hashtable allIndexes = getTrackedIndex();
		Enumeration en = allIndexes.keys();
		Hashtable alreadyProcessed = new Hashtable();
		/*
		 * alreadyProcessed.put("^MXX", "^MXX");
		 * alreadyProcessed.put("^STOXX50E", "^STOXX50E");
		 * alreadyProcessed.put("^TWII", "^TWII"); alreadyProcessed.put("^JKSE",
		 * "^JKSE"); alreadyProcessed.put("^IBEX", "^IBEX");
		 * alreadyProcessed.put("^BFX", "^BFX");
		 * alreadyProcessed.put("^PSI20.LS", "PSI20.LS");
		 * alreadyProcessed.put("^OMX", "^OMX"); alreadyProcessed.put("^HSI",
		 * "^HSI"); alreadyProcessed.put("^GSPTSE", "^GSPTSE");
		 * alreadyProcessed.put("^FTSE", "^FTSE"); alreadyProcessed.put("^RUT",
		 * "^RUT"); alreadyProcessed.put("^BVSP", "^BVSP");
		 * alreadyProcessed.put("^SSEC", "^SSEC"); alreadyProcessed.put("^IXIC",
		 * "^IXIC"); alreadyProcessed.put("^NZDOW", "^NZDOW");
		 * alreadyProcessed.put("^AEX", "^AEX"); alreadyProcessed.put("^BSESN",
		 * "^BSESN"); alreadyProcessed.put("^N225", "^N225");
		 * alreadyProcessed.put("RTSI.ME", "RTSI.ME");
		 * alreadyProcessed.put("^ATX", "^ATX"); alreadyProcessed.put("^AXJO",
		 * "^AXJO"); alreadyProcessed.put("^STI", "^STI");
		 * alreadyProcessed.put("^DJI", "^DJI"); alreadyProcessed.put("PSEI.PS",
		 * "PSEI.PS"); alreadyProcessed.put("^SSMI", "^SSMI");
		 * alreadyProcessed.put("^CSE", "^CSE"); alreadyProcessed.put("^KSE",
		 * "^KSE"); alreadyProcessed.put("^VIX", "^VIX");
		 * alreadyProcessed.put("XU100.IS", "XU100.IS");
		 * alreadyProcessed.put("^KS11", "^KS11");
		 * alreadyProcessed.put("^GDAXI", "^GDAXI");
		 * alreadyProcessed.put("^FCHI", "^FCHI");
		 * alreadyProcessed.put("^SET.BK", "^SET.BK");
		 * alreadyProcessed.put("^GSPC", "^GSPC");
		 * alreadyProcessed.put("TA35.TA", "TA35.TA");
		 * alreadyProcessed.put("^BUX", "^BUX");
		 * 
		 * alreadyProcessed.put("^TASI.SR", "^TASI.SR");
		 * alreadyProcessed.put("PSI20.LS", "PSI20.LS");
		 * alreadyProcessed.put("WIG20", "WIG20");
		 * alreadyProcessed.put("^OMXC25", "^OMXC25");
		 * alreadyProcessed.put("FTSEMIB.MI", "FTSEMIB.MX");
		 * alreadyProcessed.put("IMOEX.ME", "IMOEX.ME");
		 * alreadyProcessed.put("HNX30", "HNX30");
		 * alreadyProcessed.put("^IMOEX.ME", "^IMOEX.ME");
		 */

		Hashtable indexMap = SQLOperation.getIndexMap();
		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			if (!alreadyProcessed.containsKey(symbol)) {
				String insertSymbol = allIndexes.get(symbol).toString();
				try {
					// brand new insert with insertSymbol
					// parseInsertYahooIndexHistory(symbol, insertSymbol);
					// pad old records if no found

					SQLOperation.padIndiceRecordsHistory(symbol, indexMap);
					System.out.println(insertSymbol + " has been padded...");
				} catch (Exception ex) {

				}
			}
		}
	}

	/*** NEW 5/10/2019 ***/
	public static void reprocessYahooIndexHistory() {
		Hashtable allIndexes = getTrackedIndex();
		Enumeration en = allIndexes.keys();
		Hashtable alreadyProcessed = new Hashtable();
		/*
		 * alreadyProcessed.put("^MXX", "^MXX");
		 * alreadyProcessed.put("^STOXX50E", "^STOXX50E");
		 * alreadyProcessed.put("^TWII", "^TWII"); alreadyProcessed.put("^JKSE",
		 * "^JKSE"); alreadyProcessed.put("^IBEX", "^IBEX");
		 * alreadyProcessed.put("^BFX", "^BFX");
		 * alreadyProcessed.put("^PSI20.LS", "PSI20.LS");
		 * alreadyProcessed.put("^OMX", "^OMX"); alreadyProcessed.put("^HSI",
		 * "^HSI"); alreadyProcessed.put("^GSPTSE", "^GSPTSE");
		 * alreadyProcessed.put("^FTSE", "^FTSE"); alreadyProcessed.put("^RUT",
		 * "^RUT"); alreadyProcessed.put("^BVSP", "^BVSP");
		 * alreadyProcessed.put("^SSEC", "^SSEC"); alreadyProcessed.put("^IXIC",
		 * "^IXIC"); alreadyProcessed.put("^NZDOW", "^NZDOW");
		 * alreadyProcessed.put("^AEX", "^AEX"); alreadyProcessed.put("^BSESN",
		 * "^BSESN"); alreadyProcessed.put("^N225", "^N225");
		 * alreadyProcessed.put("RTSI.ME", "RTSI.ME");
		 * alreadyProcessed.put("^ATX", "^ATX"); alreadyProcessed.put("^AXJO",
		 * "^AXJO"); alreadyProcessed.put("^STI", "^STI");
		 * alreadyProcessed.put("^DJI", "^DJI"); alreadyProcessed.put("PSEI.PS",
		 * "PSEI.PS"); alreadyProcessed.put("^SSMI", "^SSMI");
		 * alreadyProcessed.put("^CSE", "^CSE"); alreadyProcessed.put("^KSE",
		 * "^KSE"); alreadyProcessed.put("^VIX", "^VIX");
		 * alreadyProcessed.put("XU100.IS", "XU100.IS");
		 * alreadyProcessed.put("^KS11", "^KS11");
		 * alreadyProcessed.put("^GDAXI", "^GDAXI");
		 * alreadyProcessed.put("^FCHI", "^FCHI");
		 * alreadyProcessed.put("^SET.BK", "^SET.BK");
		 * alreadyProcessed.put("^GSPC", "^GSPC");
		 * alreadyProcessed.put("TA35.TA", "TA35.TA");
		 * alreadyProcessed.put("^BUX", "^BUX");
		 * 
		 * alreadyProcessed.put("^TASI.SR", "^TASI.SR");
		 * alreadyProcessed.put("PSI20.LS", "PSI20.LS");
		 * alreadyProcessed.put("WIG20", "WIG20");
		 * alreadyProcessed.put("^OMXC25", "^OMXC25");
		 * alreadyProcessed.put("FTSEMIB.MI", "FTSEMIB.MX");
		 * alreadyProcessed.put("IMOEX.ME", "IMOEX.ME");
		 * alreadyProcessed.put("HNX30", "HNX30");
		 * alreadyProcessed.put("^IMOEX.ME", "^IMOEX.ME");
		 */
		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			if (!alreadyProcessed.containsKey(symbol)) {
				String insertSymbol = allIndexes.get(symbol).toString();
				try {
					// brand new insert with insertSymbol
					// parseInsertYahooIndexHistory(symbol, insertSymbol);
					// pad old records if no found
					parseInsertPadYahooIndexHistory(symbol);
					System.out.println(insertSymbol + " has been inserted...");
				} catch (Exception ex) {

				}
			}
		}
	}

	public static void parseInsertYahooIndexHistory(String symbol,
			String insertSymbol) {
		String path = "C:\\stock\\yahoo\\history\\" + symbol + ".csv";
		// String dateString = "05-25-2018";
		try {
			boolean read = true;
			String index = null;
			YStock previousDetail = null;
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
					if (index.indexOf("Volume") > 0) {
						// header

					} else {
						// System.out.println("I " + count);
						/*
						 * Date,Open,High,Low,Close,Adj Close,Volume
						 * 1997-02-03,257.369995
						 * ,265.880005,257.369995,265.630005,265.630005,0
						 * 1997-02
						 * -04,265.630005,271.980011,257.100006,261.239990
						 * ,261.239990,0
						 * 1997-02-05,261.239990,269.489990,261.239990
						 * ,269.010010,269.010010,0
						 * 1997-02-06,269.010010,269.010010
						 * ,265.369995,267.609985,267.609985,0
						 * 1997-02-09,null,null,null,null,null,null
						 */
						StringTokenizer tok = new StringTokenizer(index, ",\t");

						String dateString1 = tok.nextToken();
						String y = dateString1.substring(0, 4);
						String m = dateString1.substring(5, 7);
						String d = dateString1.substring(8, 10);
						String dateString = m + "-" + d + "-" + y;

						String openPrice = tok.nextToken();
						String highPrice = tok.nextToken();
						String lowPrice = tok.nextToken();
						String lastPrice = tok.nextToken();
						String adjustedPrice = tok.nextToken();
						String volume = tok.nextToken();

						if (!openPrice.equals("null")) {
							YStock detail = new YStock();
							detail.setSymbol(insertSymbol.toUpperCase());
							float open = Float.parseFloat(openPrice);
							detail.setOpenPrice(open);

							float high = Float.parseFloat(highPrice);
							detail.setHighPrice(high);

							float low = Float.parseFloat(lowPrice);
							detail.setLowPrice(low);

							float close = Float.parseFloat(lastPrice);
							detail.setFinalPrice(close);
							detail.setAdjustedPrice(close);

							float vol = Float.parseFloat(volume);
							detail.setVolume(vol);

							detail.setDate(dateString);
							int SEQINDEX = Integer.parseInt(StaticData
									.dateMap(true).get(dateString).toString());
							SQLOperation.insertRecord(SEQINDEX, detail);
							// SingleStockInsert(dateString1, insertSymbol,
							// detail);
							previousDetail = detail;
						} else {
							previousDetail.setDate(dateString);
							int SEQINDEX = Integer.parseInt(StaticData
									.dateMap(true).get(dateString).toString());
							SQLOperation.insertRecord(SEQINDEX, previousDetail);

						}

					}

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

	}

	public static void parseInsertPadYahooIndexHistory(String symbol) {
		String path = "C:\\stock\\yahoo\\history\\" + symbol + ".csv";
		// String dateString = "05-25-2018";
		try {
			boolean read = true;
			String index = null;
			YStock previousDetail = null;
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
					if (index.indexOf("Volume") > 0) {
						// header

					} else {
						// System.out.println("I " + count);
						/*
						 * Date,Open,High,Low,Close,Adj Close,Volume
						 * 1997-02-03,257.369995
						 * ,265.880005,257.369995,265.630005,265.630005,0
						 * 1997-02
						 * -04,265.630005,271.980011,257.100006,261.239990
						 * ,261.239990,0
						 * 1997-02-05,261.239990,269.489990,261.239990
						 * ,269.010010,269.010010,0
						 * 1997-02-06,269.010010,269.010010
						 * ,265.369995,267.609985,267.609985,0
						 * 1997-02-09,null,null,null,null,null,null
						 */
						StringTokenizer tok = new StringTokenizer(index, ",\t");

						String dateString1 = tok.nextToken();
						String y = dateString1.substring(0, 4);
						String m = dateString1.substring(5, 7);
						String d = dateString1.substring(8, 10);
						String dateString = m + "-" + d + "-" + y;

						String openPrice = tok.nextToken();
						String highPrice = tok.nextToken();
						String lowPrice = tok.nextToken();
						String lastPrice = tok.nextToken();
						String adjustedPrice = tok.nextToken();
						String volume = tok.nextToken();

						if (!openPrice.equals("null")) {
							YStock detail = new YStock();
							detail.setSymbol(symbol.toUpperCase());
							float open = Float.parseFloat(openPrice);
							detail.setOpenPrice(open);

							float high = Float.parseFloat(highPrice);
							detail.setHighPrice(high);

							float low = Float.parseFloat(lowPrice);
							detail.setLowPrice(low);

							float close = Float.parseFloat(lastPrice);
							detail.setFinalPrice(close);
							detail.setAdjustedPrice(close);

							float vol = Float.parseFloat(volume);
							detail.setVolume(vol);

							detail.setDate(dateString);
							int SEQINDEX = Integer.parseInt(StaticData
									.dateMap(true).get(dateString).toString());
							if (!SQLOperation.checkRecordExistance(SEQINDEX,
									detail)) {
								SQLOperation.insertRecord(SEQINDEX, detail);
							}
							// SingleStockInsert(dateString1, insertSymbol,
							// detail);
							previousDetail = detail;
						} else {
							previousDetail.setDate(dateString);
							int SEQINDEX = Integer.parseInt(StaticData
									.dateMap(true).get(dateString).toString());
							if (!SQLOperation.checkRecordExistance(SEQINDEX,
									previousDetail)) {

								SQLOperation.insertRecord(SEQINDEX,
										previousDetail);
							}

						}

					}

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

	}

}