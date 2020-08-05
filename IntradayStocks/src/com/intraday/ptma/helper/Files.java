package com.intraday.ptma.helper;

/**
 * @author MaY
 * 
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates. To enable and disable the creation of type
 * comments go to Window>Preferences>Java>Code Generation.
 */

import java.util.*;
import java.io.*;
import com.intraday.ptma.status.SQLOperationStatus;
import com.intraday.ptma.sql.SQLOperation;

import com.intraday.ptma.sql.*;

public class Files {
	/*
	 * private static BufferedReaderZZ in = null; private static int hoursZZ =
	 * 9; private static int minutesZZ = 30; private static String dateStringZZZ
	 * = ""; private static String tempdateStringZZZ = ""; private static int
	 * previousGapNumberZZ = 0; private static int currentGapNumberZZ = 0;
	 */
	
	private static Hashtable previousStocks = null;
	
	public static void main(String[] args) {

		// renameFile(38,1,"A",79);
		// listUnprocessedFiles(38, 1);
		// spitFailProcessedFiles(41, 3);
		// filterSparselyTradedStocks(46,1,1);
		// filterSparselyTradedStocks(46,2,1);
		// filterSparselyTradedStocks(46,3,1);
        //>200/869 5856 out of processed files 8607
		//>260/869 or latest>40, 5500 out of processed files 8607

		Files.createIntradyTagFile("BitBotStart.txt");
		String date = "04/02/2019";
		//unmarkFileProcessed();
		getBitBotIntradayData("DATA", date);
		Files.createIntradyTagFile("BitBotEnd.txt");
		/*
		 * 
		 * String date = "20180815";
		 * Files.createIntradyTagFile("NASDAQStart.txt");
		 * getEODIntradayData("93", "NASDAQ", date);
		 * Files.createIntradyTagFile("NASDAQEnd.txt");
		 * 
		 * Files.createIntradyTagFile("AMEXStart.txt"); getEODIntradayData("93",
		 * "AMEX", date); Files.createIntradyTagFile("AMEXStart.txt");
		 * 
		 * Files.createIntradyTagFile("AMEXStart.txt"); getEODIntradayData("93",
		 * "NYSE", date); Files.createIntradyTagFile("NYSEStart.txt");
		 */
		// reverseUnprocessedFiles(73, 1);
		// reverseUnprocessedFiles(73, 2);
		// reverseUnprocessedFiles(73, 3);

		// listFailProcessedFiles(40,1);
		// renameAllFiles(40, 3);
		// getRealGoogleSymbols(11);
		// getGoogleIntradayHistory("NVDA");
		// getIntradayStocks("ABC");
		// getIndexHistory("^NZ50");
		// getEODRecords("NASDAQ_20151113.csv");
		// writeToFile("C://stocks//process.txt","Daily Task finished at "+Calendar.getInstance().getTime().toLocaleString());

	}

	static public void deleteIntradyDoneFile() {
		String path = "C:\\stock\\yahoo\\intradayDone.txt";
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}

	static public void createIntradyTagFile(String doneFile) {
		String path = "C:\\stock\\yahoo\\" + doneFile;
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception ex) {

			}
		}
	}

	static public void createBitBotIntradyTagFile(String doneFile) {
		String path = "C:\\stock\\intraday\\Data\\Processed\\" + doneFile;
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (Exception ex) {

			}
		}
	}

	static public boolean checkFailedTagFile(String tagFile) {
		boolean exist = false;
		String path = "C:\\stock\\yahoo\\" + tagFile;
		File file = new File(path);
		if (file.exists()) {
			exist = true;

			file.delete();
		}

		return exist;
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

	static public boolean historyFileExists(String symbol) {
		boolean result = false;
		String path = "C:\\stock\\yahoo\\" + symbol + ".csv";
		File file = new File(path);
		if (file.exists())
			result = true;

		return result;

	}

	static public boolean checkIntradayFileExists(String symbol, int folder) {
		boolean result = false;
		String path = "C:\\stock\\yahoo\\intraday\\" + folder + "\\" + symbol
				+ ".csv";
		File file = new File(path);
		if (file.exists())
			result = true;

		return result;

	}

	static public void checkIntradayFolderExists(int folder) {
		boolean result = false;
		String path = "C:\\stock\\intraday\\" + folder;
		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();
		}

		String path1 = "C:\\stock\\intraday\\" + folder + "\\" + 1;
		File file1 = new File(path1);
		if (!file1.exists()) {
			file1.mkdir();
		}

		String path2 = "C:\\stock\\intraday\\" + folder + "\\" + 2;
		File file2 = new File(path2);
		if (!file2.exists()) {
			file2.mkdir();
		}

		String path3 = "C:\\stock\\intraday\\" + folder + "\\" + 3;
		File file3 = new File(path3);
		if (!file3.exists()) {
			file3.mkdir();
		}

		try {
			Thread.sleep(10000);
		} catch (Exception ex) {

		}
	}

	static public void checkIntradayFolderExists(int folder, int sfn) {
		boolean result = false;
		for (int w = 1; w <= sfn; w++) {
			String path = "C:\\stock\\intraday\\" + folder + "\\" + w;
			File file = new File(path);
			if (!file.exists()) {
				file.mkdir();
			}
		}

	}

	static void markFileProcessed(File file, int size) {

		try {
			String path2 = file.getCanonicalPath() + "_done";
			if (size > 0) {
				path2 = path2 + "_" + size;
			}
			System.out.println(path2);
			File f2 = new File(path2);

			file.renameTo(f2);
		} catch (Exception ex) {

		}

	}

	static void unmarkFileProcessed() {

		try {

			String path = "C:\\stock\\intraday\\Data\\";

			FilenameFilter doneFilter = new FilenameFilter() {
				public boolean accept(File dir, String name) {
					String lowercaseName = name.toLowerCase();
					if (lowercaseName.indexOf(".txt_done") > 0) {
						return true;
					} else {
						return false;
					}
				}
			};

			File tfile = new File(path);

			File[] files = tfile.listFiles(doneFilter);
			for (File file : files) {
				if (file.isDirectory()) {
					System.out.print("directory:");
				} else {
					System.out.print("     file:");
					String path2 = file.getCanonicalPath();
					System.out.println(path2);
					path2 = path2.substring(0, path2.indexOf("_done"));
					System.out.println(path2);
					File f2 = new File(path2);

					file.renameTo(f2);

					// break;
				}
				// System.out.println(file.getCanonicalPath());
			}

		} catch (Exception ex) {

		}

	}

	static void renameFile(int folder, int sfn, String symbol, int size) {
		String path1 = "C:\\stock\\intraday\\" + folder + "\\" + sfn + "\\"
				+ symbol + ".csv";
		File f1 = new File(path1);
		String path2 = "C:\\stock\\intraday\\" + folder + "\\" + sfn + "\\"
				+ symbol + ".csv_" + size;
		File f2 = new File(path2);

		if (f2.exists()) {
			f1.delete();
		} else {
			f1.renameTo(f2);
		}

	}

	static void markFileDone(int folder, int sfn, String symbol, int size) {
		String path1 = "C:\\stock\\intraday\\" + folder + "\\" + sfn + "\\"
				+ symbol + ".csv_" + (1000 + size);
		File f1 = new File(path1);
		String path2 = "C:\\stock\\intraday\\" + folder + "\\" + sfn + "\\"
				+ symbol + ".csv_" + size;
		File f2 = new File(path2);

		f1.renameTo(f2);

	}

	static void markFileReprocess(int folder, int sfn, String symbol, int size) {
		String path1 = "C:\\stock\\intraday\\" + folder + "\\" + sfn + "\\"
				+ symbol + ".csv_" + (1000 + size);
		File f1 = new File(path1);
		String path2 = "C:\\stock\\intraday\\" + folder + "\\" + sfn + "\\"
				+ symbol + ".csv";
		File f2 = new File(path2);

		f1.renameTo(f2);

	}

	static public int countAllFiles(int folder, int sfn) {
		Hashtable stocks = new Hashtable();
		String path = "C:\\stock\\intraday\\" + folder + "\\" + sfn + "\\";
		if (sfn < 0)
			path = "C:\\stock\\intraday\\" + folder + "\\";

		File f = new File(path);

		File[] files = f.listFiles();
		int count = files.length;

		return count;
	}

	static public Hashtable filterSparselyTradedStocks(int folder, int sfn,
			int days) {
		Hashtable stocks = new Hashtable();
		String path = "C:\\stock\\intraday\\" + folder + "\\" + sfn + "\\";
		File f = new File(path);

		File[] files = f.listFiles();
		int count = 0;
		for (File file : files) {

			String fname = file.getName();
			int pos = fname.indexOf(".csv_");
			if (pos > 0) {
				count++;
				String stk = fname.substring(0, pos);
				int rc = Integer.parseInt(fname.substring(pos + 5,
						fname.length()));

				if (rc == 1000) {
					stocks.put(stk, "" + (rc - 1000));
					System.out.println(stk + "  " + rc);
					SQLOperationStatus.addStockStatus(stk, days, 0);
				} else {
					if (rc < 24 * days) { // each days<30%
						SQLOperationStatus.addStockStatus(stk, days, rc);
					}
				}

				stocks.put(stk, stk);
			}
		}

		return stocks;
	}

	static public Hashtable listUnprocessedFiles(int folder, int sfn) {
		Hashtable stocks = new Hashtable();
		String path = "C:\\stock\\intraday\\" + folder + "\\" + sfn + "\\";
		File f = new File(path);

		File[] files = f.listFiles();
		int count = 0;
		for (File file : files) {

			String fname = file.getName();
			if (fname.endsWith(".csv")) {
				count++;
				String stk = fname.substring(0, fname.length() - 4);
				System.out.println(count + "     file=== " + fname + " -- "
						+ stk);
				stocks.put(stk, stk);
			}
		}

		return stocks;
	}

	static public Hashtable reverseUnprocessedFiles(int folder, int sfn) {
		Hashtable stocks = new Hashtable();
		String path = "C:\\stock\\intraday\\" + folder + "\\" + sfn + "\\";
		File f = new File(path);

		File[] files = f.listFiles();
		int count = 0;
		for (File file : files) {

			String fname = file.getName();
			if (fname.indexOf(".csv_") > 0) {
				count++;
				int pos = fname.indexOf(".csv_");
				String stk = fname.substring(0, pos);
				System.out.println(count + "     file=== " + fname + " -- "
						+ stk);
				String path3 = "C:\\stock\\intraday\\" + folder + "\\" + sfn
						+ "\\" + stk + ".csv";
				File f3 = new File(path3);

				file.renameTo(f3);
			}
		}

		return stocks;
	}

	static public Hashtable listFailProcessedFiles(int folder, int sfn) {
		Hashtable stocks = new Hashtable();
		String path = "C:\\stock\\intraday\\" + folder + "\\" + sfn + "\\";
		File f = new File(path);

		File[] files = f.listFiles();
		int count = 0;
		for (File file : files) {

			String fname = file.getName();
			int pos = fname.indexOf(".csv_");
			if (pos > 0) {
				count++;
				String stk = fname.substring(0, pos);
				int rc = Integer.parseInt(fname.substring(pos + 5,
						fname.length()));

				if (rc > 1000) {
					stocks.put(stk, "" + (rc - 1000));
					System.out.println(stk + "  " + rc);
				}
				// stocks.put(stk, stk);
			}
		}

		return stocks;
	}

	static public Hashtable listAllFiles(int folder, int sfn) {
		Hashtable stocks = new Hashtable();
		String path = "C:\\stock\\intraday\\" + folder + "\\" + sfn + "\\";
		File f = new File(path);

		File[] files = f.listFiles();
		int count = 0;
		for (File file : files) {

			String fname = file.getName();
			int pos = fname.indexOf(".csv");
			if (pos > 0) {
				count++;
				String stk = fname.substring(0, pos);

				stocks.put(stk, stk);
			}
		}

		return stocks;
	}

	static public Hashtable spitFailProcessedFiles(int folder, int sfn) {
		Hashtable stocks = new Hashtable();
		String path = "C:\\stock\\intraday\\" + folder + "\\" + sfn + "\\";
		File f = new File(path);

		File[] files = f.listFiles();
		int count = 0;
		System.out.println("Start...");
		for (File file : files) {

			String fname = file.getName();
			int pos = fname.indexOf(".csv_");
			if (pos > 0) {
				count++;
				String stk = fname.substring(0, pos);
				int rc = Integer.parseInt(fname.substring(pos + 5,
						fname.length()));

				if (rc < 30 || (rc >= 1000 && rc <= 1030)) {
					stocks.put(stk, "" + (rc - 1000));
					if (rc >= 1000) {
						String dis = "excludedStocks.put(\"" + stk + "\","
								+ "\"" + (rc - 1000) + "\");";
						System.out.println(dis);
					}
					if (rc < 30) {
						String dis = "excludedStocks.put(\"" + stk + "\","
								+ "\"" + (rc) + "\");";
						System.out.println(dis);
					}
				}

				// stocks.put(stk, stk);
			}
		}

		return stocks;
	}

	static public Hashtable renameAllFiles(int folder, int sfn) {
		Hashtable stocks = new Hashtable();
		String path = "C:\\stock\\intraday\\" + folder + "\\" + sfn + "\\";
		File f = new File(path);

		File[] files = f.listFiles();
		int count = 0;
		for (File file : files) {

			String fname = file.getName();
			String path2 = "C:\\stock\\intraday\\" + folder + "\\" + sfn + "\\"
					+ fname;
			File f2 = new File(path2);

			count++;
			String stk = fname.substring(0, fname.indexOf("."));
			String path3 = "C:\\stock\\intraday\\" + folder + "\\" + sfn + "\\"
					+ stk + ".csv";
			File f3 = new File(path3);

			f2.renameTo(f3);

			System.out.println(count + "     file=== " + fname + " -- " + stk);
			stocks.put(stk, stk);

		}

		return stocks;
	}

	static public void deleteGoogleIntradayHistory(String symbol) {
		try {
			String path = "C:\\stock\\intraday\\" + symbol.toLowerCase()
					+ ".csv";
			File file = new File(path);
			file.delete();
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	static public Hashtable getRealGoogleIntradayHistory(String symbol,
			int folder, String insertSymbol) {
		BufferedReader in = null;
		int hours = 9;
		int minutes = 30;
		int previousGapNumber = 0;
		int currentGapNumber = 0;
		String path = "C:\\stock\\intraday\\" + folder + "\\"
				+ symbol.toLowerCase() + ".csv";
		Hashtable result = new Hashtable();
		String myDateString = "";
		String tempdateString = "";
		int utime = 0;
		boolean read = true;
		try {
			in = new BufferedReader(new FileReader(path));
		} catch (Exception ex) {
			read = false;
		}

		try {
			int count = 0;
			String index = null;

			boolean recordStart = false;
			boolean newDay = false;
			String symb = "";
			float divs = 0.0f;
			while (read) {
				try {
					index = in.readLine();
				} catch (Exception ex) {
					ex.printStackTrace(System.out);
					// ingnore no file
				}
				if (index == null) {
					read = false;
				} else if (index.indexOf("TIMEZONE_OFFSET") >= 0) {
					recordStart = true;
				} else if (recordStart == true) {
					count++;
					// System.out.println("I " + count);

					StringTokenizer tok = new StringTokenizer(index, ",\t");
					YStock detail = new YStock();
					detail.setSymbol(symbol.toUpperCase());
					if (insertSymbol != null && insertSymbol.length() > 0) {
						detail.setSymbol(insertSymbol.toUpperCase());
					}
					String gapString = tok.nextToken().trim();
					if (gapString.charAt(0) == 'a') {
						utime = Integer.parseInt(gapString.substring(1,
								gapString.length()));
						System.out.println("Utime " + utime);
						// Select VARCHAR_FORMAT(CDATE,'MM-DD-YYYY') AS VDATE
						// from BPMADMIN.GSEQ where UTIME=2145882600;
						myDateString = SQLOperation.getDateFromReference(utime);

						detail.setDate(myDateString);
						detail.setChour(hours);
						detail.setCminute(minutes);
						long seqIndex = SQLOperation.getSeqIndex(myDateString,
								hours, minutes);
						if (seqIndex <= 0) {
							System.out
									.println("*******$$$$$$$$$$$$$$$$$$$****** No index found for "
											+ myDateString
											+ " at "
											+ hours
											+ ":" + minutes);
						}
						detail.setSeqIndex(seqIndex);
						previousGapNumber = 0;
						currentGapNumber = 0;
					} else {
						int gapNumber = Integer.parseInt(gapString);
						if (previousGapNumber == 0) {
							previousGapNumber = gapNumber;

							// 5 min gap
							int minutesTemp = minutes + gapNumber * 5;
							int temphours = 0;
							if (minutesTemp >= 60) {
								temphours = (hours + minutesTemp / 60) % 24;
								minutesTemp = minutesTemp % 60;
							} else {
								temphours = hours;
							}

							if (tempdateString.length() == 0) {
								detail.setDate(myDateString);
								long seqIndex = SQLOperation.getSeqIndex(
										myDateString, temphours, minutesTemp);
								if (seqIndex <= 0) {
									System.out
											.println("*******$$$$$$$$$$$$$$$$$$$****** No index found for "
													+ myDateString
													+ " at "
													+ temphours
													+ ":"
													+ minutesTemp);
								}
								detail.setSeqIndex(seqIndex);
							} else {
								detail.setDate(tempdateString);
								long seqIndex = SQLOperation.getSeqIndex(
										tempdateString, temphours, minutesTemp);
								if (seqIndex <= 0) {
									System.out
											.println("*******$$$$$$$$$$$$$$$$$$$****** No index found for "
													+ myDateString
													+ " at "
													+ temphours
													+ ":"
													+ minutesTemp);
								}
								detail.setSeqIndex(seqIndex);
							}

							detail.setChour(temphours);
							detail.setCminute(minutesTemp);
						} else if (currentGapNumber == 0) {
							currentGapNumber = gapNumber;
							// 5 min gap

							int minutesTemp = minutes + gapNumber * 5;
							int temphours = 0;
							if (minutesTemp >= 60) {
								temphours = (hours + minutesTemp / 60) % 24;
								minutesTemp = minutesTemp % 60;
							} else {
								temphours = hours;
							}

							if (tempdateString.length() == 0) {
								detail.setDate(myDateString);
								long seqIndex = SQLOperation.getSeqIndex(
										myDateString, temphours, minutesTemp);
								if (seqIndex <= 0) {
									System.out
											.println("*******$$$$$$$$$$$$$$$$$$$****** No index found for "
													+ myDateString
													+ " at "
													+ temphours
													+ ":"
													+ minutesTemp);
								}
								detail.setSeqIndex(seqIndex);
							} else {
								detail.setDate(tempdateString);
								long seqIndex = SQLOperation.getSeqIndex(
										tempdateString, temphours, minutesTemp);
								if (seqIndex <= 0) {
									System.out
											.println("*******$$$$$$$$$$$$$$$$$$$****** No index found for "
													+ myDateString
													+ " at "
													+ temphours
													+ ":"
													+ minutesTemp);
								}
								detail.setSeqIndex(seqIndex);
							}

							detail.setChour(temphours);
							detail.setCminute(minutesTemp);
						} else {
							previousGapNumber = currentGapNumber;
							currentGapNumber = gapNumber;

							if ((currentGapNumber - previousGapNumber) < 90) {
								int minutesTemp = minutes + gapNumber * 5;
								int hoursTemp = (hours + minutesTemp / 60) % 24;
								minutesTemp = minutesTemp % 60;

								if (tempdateString.length() == 0) {
									detail.setDate(myDateString);
									long seqIndex = SQLOperation.getSeqIndex(
											myDateString, hoursTemp,
											minutesTemp);
									if (seqIndex <= 0) {
										System.out
												.println("*******$$$$$$$$$$$$$$$$$$$****** No index found for "
														+ myDateString
														+ " at "
														+ hoursTemp
														+ ":"
														+ minutesTemp);
									}
									detail.setSeqIndex(seqIndex);
								} else {
									detail.setDate(tempdateString);
									long seqIndex = SQLOperation.getSeqIndex(
											tempdateString, hoursTemp,
											minutesTemp);
									if (seqIndex <= 0) {
										System.out
												.println("*******$$$$$$$$$$$$$$$$$$$****** No index found for "
														+ myDateString
														+ " at "
														+ hoursTemp
														+ ":"
														+ minutesTemp);
									}
									detail.setSeqIndex(seqIndex);
								}

								detail.setChour(hoursTemp);
								detail.setCminute(minutesTemp);
							} else {
								System.out.println("previousGapNumber is "
										+ previousGapNumber);
								System.out.println("currentGapNumber is "
										+ currentGapNumber);
								System.out.println("dateString is "
										+ myDateString);
								StringTokenizer toks = new StringTokenizer(
										myDateString, "-");
								Calendar cal1 = Calendar.getInstance();
								cal1.set(Calendar.MONTH,
										Integer.parseInt(toks.nextToken()) - 1);
								cal1.set(Calendar.DAY_OF_MONTH,
										Integer.parseInt(toks.nextToken()));
								cal1.set(Calendar.YEAR,
										Integer.parseInt(toks.nextToken()));
								cal1.set(Calendar.HOUR, hours);
								cal1.set(Calendar.MINUTE, minutes);

								cal1.add(Calendar.MINUTE, gapNumber * 5);
								tempdateString = ""
										+ (cal1.get(Calendar.MONTH) + 1) + "-"
										+ cal1.get(Calendar.DAY_OF_MONTH) + "-"
										+ cal1.get(Calendar.YEAR);
								System.out.println("--- Datestring is now "
										+ tempdateString);
								if (tempdateString.length() == 0) {
									detail.setDate(myDateString);
									long seqIndex = SQLOperation.getSeqIndex(
											myDateString, hours, minutes);
									if (seqIndex <= 0) {
										System.out
												.println("*******$$$$$$$$$$$$$$$$$$$****** No index found for "
														+ myDateString
														+ " at "
														+ hours + ":" + minutes);
									}
									detail.setSeqIndex(seqIndex);
								} else {
									detail.setDate(tempdateString);
									long seqIndex = SQLOperation.getSeqIndex(
											tempdateString, hours, minutes);
									if (seqIndex <= 0) {
										System.out
												.println("*******$$$$$$$$$$$$$$$$$$$****** No index found for "
														+ myDateString
														+ " at "
														+ hours + ":" + minutes);
									}
									detail.setSeqIndex(seqIndex);
								}

								int tempMinutes = cal1.get(Calendar.MINUTE) % 60;

								detail.setChour(hours);
								detail.setCminute(minutes);

								previousGapNumber = currentGapNumber;
								currentGapNumber = 0;

							}
						}
					}
					/*
					 * StringTokenizer tok2 = new StringTokenizer(dateString,
					 * "-/"); String y = tok2.nextToken(); String m =
					 * tok2.nextToken(); if (Integer.parseInt(m) > 9) { m = "" +
					 * Integer.parseInt(m); } else { m = "0" +
					 * Integer.parseInt(m); }
					 * 
					 * String d = tok2.nextToken(); if (Integer.parseInt(d) > 9)
					 * { d = "" + Integer.parseInt(d); } else { d = "0" +
					 * Integer.parseInt(d); }
					 * 
					 * detail.setDate(m + "-" + d + "-" + y); //
					 * detail.setTradingDate(dateString.replaceAll("/", //
					 * "-")); detail.setTimestamp(tok.nextToken() .trim());
					 */
					detail.setFinalPrice(Float.parseFloat(tok.nextToken()
							.trim()));
					detail.setAdjustedPrice(detail.getFinalPrice());
					detail.setHighPrice(Float
							.parseFloat(tok.nextToken().trim()));
					detail.setLowPrice(Float.parseFloat(tok.nextToken().trim()));

					detail.setOpenPrice(Float
							.parseFloat(tok.nextToken().trim()));
					detail.setVolume(Float.parseFloat(tok.nextToken().trim()));
					result.put("" + count, detail);
					/*
					 * System.out.println("" + count + " " + detail.getSymbol()
					 * + " date: " + detail.getDate() + " hour: " +
					 * detail.getChour() + " minuts: " + detail.getCminute() +
					 * " open: " + detail.getOpenPrice() + " high: " +
					 * detail.getHighPrice() + " low: " + detail.getLowPrice() +
					 * " close: " + detail.getFinalPrice() + " vol: " +
					 * detail.getVolume() + " adjusted: " +
					 * detail.getAdjustedPrice());
					 */

					/*
					 * for(int w=1; w<=result.size();w++){ YStock wdetail =
					 * (YStock)result.get(""+w);
					 * 
					 * System.out.println("*****" + w + " " +
					 * wdetail.getSymbol() + " date: " + wdetail.getDate() +
					 * " hour: " + wdetail.getChour() + " minuts: " +
					 * wdetail.getCminute() + " open: " + wdetail.getOpenPrice()
					 * + " high: " + wdetail.getHighPrice() + " low: " +
					 * wdetail.getLowPrice() + " close: " +
					 * wdetail.getFinalPrice() + " vol: " + wdetail.getVolume()
					 * + " adjusted: " + wdetail.getAdjustedPrice()); }
					 */

					// System.out.println(detail.getTimestamp()+","+(9+(count+6)/12));
					// System.out.println(detail.getTimestamp()+","+(9+(count+6)/12));
					// StringTokenizer tok4 = new
					// StringTokenizer(detail.getTimestamp(),":");
					// tok4.nextToken();
					// System.out.println("Minutes "+tok4.nextToken());

				}

			}

			in.close();
			in = null;
			// result = null;
			// System.out.println("total index data count: " + count);
		} catch (Exception ex) {
			try {
				in.close();
				in = null;
				// result = null;
				ex.printStackTrace(System.out);
			} catch (Exception ex2) {

			}

		}
		return result;
	}

	static private void insertRecords(int symCount, Hashtable stocks) {
		int size = stocks.size();
		long start = System.currentTimeMillis();
		for (int k = 1; k <= size; k++) {
			YStock record = (YStock) stocks.get("" + k);

			if (record != null) {
				String date = record.getDate();
				StringTokenizer toks = new StringTokenizer(date, "-");
				System.out.println("date is " + date);
				String y = toks.nextToken();
				String m = toks.nextToken();
				String d = toks.nextToken();
				String symbol = record.getSymbol();
				System.out
						.println(symbol + " " + k + "  m " + m + " d " + d
								+ " y " + y + " FINAL PRICE: "
								+ record.getFinalPrice());
				java.sql.Date cdate = java.sql.Date.valueOf(y + "-" + m + "-"
						+ d);

				// month end, PTMA, PTMAV, DSI5 have been
				// done inside stored proc now, so no longer needed
				// to be called in Java
				// SQLOperation.insertRecordSP(k, record, cdate);
				// SQLOperation.insertRecord((long) record.getSeqIndex(),
				// record);
				// if (recordSize < 350) {
				// if (recordSize < 1) {
				// regular Java insertion if size<240
				SQLOperation.insertRecord((long) record.getSeqIndex(), record);
				float code = 1.0f * (symCount % 6 + 1);

				// set up distro code
				SQLOperation.setDistroCode(record.getSymbol(),
						(long) record.getSeqIndex(), code);

				// } else {
				// otherwise stored proc will fail in calculating PTMAV which
				// needs 11 months data
				// stock proc insertion if size>=360
				// SQLOperation.insertEODRecord(record, cdate);
				// }
				/*
				 * if (k % 500 == 0) { long end = System.currentTimeMillis();
				 * System.out
				 * .println("Processed another 500 records, cost Seconds " +
				 * (end - start) / 1000); try { Thread.sleep(3000); } catch
				 * (Exception ex) {
				 * 
				 * }
				 * 
				 * }
				 */
			}

		}

	}

	static public Hashtable getEODIntradayData(String folder, String exchange,
			String date) {
		String path = "C:\\stock\\intraday\\" + folder + "\\" + exchange + "_"
				+ date + ".csv";
		boolean notExist = true;
		while (notExist) {
			File tfile = new File(path);
			if (!tfile.exists()) {
				System.out.println("Waiting for file at " + path);
				try {
					Thread.sleep(60000);
				} catch (Exception ex) {

				}
			} else {
				notExist = false;
			}
		}

		boolean read = true;
		BufferedReader in = null;

		int hours = 9;
		int minutes = 30;
		int preVolume = 0;
		float preOpen = 0.0f;
		float preMax = 0.0f;
		float preMin = 10000000000.0f;
		float afterOpen = 0.0f;
		int afterVolume = 0;
		float afterMax = 0.0f;
		float afterMin = 10000000000.0f;
		float afterFinal = 0.0f;
		Hashtable nstkData = null;
		int count = 0;
		String ccdate = null;
		int symCount = 0;

		try {
			in = new BufferedReader(new FileReader(path));
		} catch (Exception ex) {
			read = false;
		}

		try {
			String index = null;

			boolean recordStart = false;
			boolean newDay = false;
			String preSymbol = null;

			while (read) { // while loop
				try {
					index = in.readLine();
					System.out.println("index " + index);

				} catch (Exception ex) {
					ex.printStackTrace(System.out);
					// ingnore no file
				}
				if (index == null) {
					read = false;
					recordStart = false;
				} else {
					recordStart = true;
				}

				if (recordStart == true) { // if recordStart
					System.out.println("index " + index);

					StringTokenizer tok = new StringTokenizer(index, ",\t ");
					String symbol = tok.nextToken().toString();
					String dateString = tok.nextToken().toString();
					StringTokenizer tok2 = new StringTokenizer(dateString, "-");
					String dStr = tok2.nextToken().toString();
					String mStr = tok2.nextToken().toString();
					String yStr = tok2.nextToken().toString();

					String cdate = yStr + "-" + getMonth(mStr) + "-" + dStr;

					if (ccdate == null && cdate != null) {
						ccdate = cdate;
					}

					String timeString = tok.nextToken().toString();
					StringTokenizer tok3 = new StringTokenizer(timeString, ":");
					hours = Integer.parseInt(tok3.nextToken().toString());
					minutes = Integer.parseInt(tok3.nextToken().toString());

					long myseqIndex = SQLOperation.getSeqIndex(cdate, hours,
							minutes);
					String openString = tok.nextToken().toString();
					String highString = tok.nextToken().toString();
					String lowString = tok.nextToken().toString();
					String closeString = tok.nextToken().toString();
					String volumeString = tok.nextToken().toString();

					if (preSymbol == null) {
						preSymbol = symbol;
					}

					if (preSymbol.equalsIgnoreCase(symbol)) { // if
																// (preSymbol.equalsIgnoreCase(symbol))
						if (nstkData == null) {
							nstkData = new Hashtable();
						}

						// populate records of intraday for the same stokc

						if (hours < 9 || (hours == 9 && minutes < 30)) {
							// preHour
							if (preOpen < 0.00000001f) {
								preOpen = Float.parseFloat(openString);
							}

							if (preMax < Float.parseFloat(highString)) {
								preMax = Float.parseFloat(highString);
							}

							if (preMin > Float.parseFloat(lowString)) {
								preMin = Float.parseFloat(lowString);
							}
							preVolume = preVolume
									+ Integer.parseInt(volumeString);

						} else if ((hours == 9 && minutes >= 30)) {

							count++;
							YStock detail = new YStock();
							detail.setSymbol(symbol.toUpperCase());
							detail.setDate(cdate);
							detail.setChour(hours);
							detail.setCminute(minutes);
							detail.setSeqIndex(myseqIndex);
							detail.setFinalPrice(Float.parseFloat(closeString));
							detail.setAdjustedPrice(detail.getFinalPrice());

							if (preOpen > 0.001f) {
								// unlikely you have pre-market trading and 9:30
								// am has zero volume
								// or no trading
								if (preOpen < 0.00000001f) {
									preOpen = Float.parseFloat(openString);
								}

								if (preMax < Float.parseFloat(highString)) {
									preMax = Float.parseFloat(highString);
								}

								if (preMin > Float.parseFloat(lowString)) {
									preMin = Float.parseFloat(lowString);
								}
								preVolume = preVolume
										+ Integer.parseInt(volumeString);

								detail.setOpenPrice(preOpen);
								detail.setHighPrice(preMax);
								detail.setLowPrice(preMin);
								detail.setVolume(preVolume);
								preOpen = 0.0f;
								preMax = 0.0f;
								preMin = 10000000000.0f;
								preVolume = 0;
								afterOpen = 0.0f;
								afterVolume = 0;
								afterMax = 0.0f;
								afterMin = 10000000000.0f;
								afterFinal = 0.0f;

							} else {
								detail.setOpenPrice(Float
										.parseFloat(openString));
								detail.setHighPrice(Float
										.parseFloat(highString));
								detail.setLowPrice(Float.parseFloat(lowString));
								detail.setVolume(Float.parseFloat(volumeString));

							}

							nstkData.put("" + count, detail);

						} else if (hours > 9 && hours < 16) {
							count++;
							System.out.println("count " + count);
							System.out.println("symbol " + symbol);
							System.out.println("dateString " + dateString);
							System.out.println("dStr " + dStr);
							System.out.println("mStr " + mStr);
							System.out.println("yStr " + yStr);
							System.out.println("cDate " + cdate);
							System.out.println("timeString " + timeString);
							System.out.println("Hours " + hours);
							System.out.println("Minutes " + minutes);
							System.out.println(" myseqIndex  " + myseqIndex);
							System.out.println("openString " + openString);
							System.out.println("highString " + highString);
							System.out.println("lowString " + lowString);
							System.out.println("closeString " + closeString);
							System.out.println("volumeString " + volumeString);

							YStock detail = new YStock();
							detail.setSymbol(symbol.toUpperCase());
							detail.setDate(cdate);
							detail.setChour(hours);
							detail.setCminute(minutes);
							myseqIndex = SQLOperation.getSeqIndex(cdate, hours,
									minutes);
							detail.setSeqIndex(myseqIndex);
							detail.setFinalPrice(Float.parseFloat(closeString));
							detail.setAdjustedPrice(detail.getFinalPrice());
							detail.setHighPrice(Float.parseFloat(highString));
							detail.setLowPrice(Float.parseFloat(lowString));

							detail.setOpenPrice(Float.parseFloat(openString));
							detail.setVolume(Float.parseFloat(volumeString));
							nstkData.put("" + count, detail);

						} else if (hours >= 16) {
							// afterHour
							if (afterOpen < 0.00000001f) {
								afterOpen = Float.parseFloat(openString);
							}

							if (afterMax < Float.parseFloat(highString)) {
								afterMax = Float.parseFloat(highString);
							}

							if (afterMin > Float.parseFloat(lowString)) {
								afterMin = Float.parseFloat(lowString);
							}

							afterFinal = Float.parseFloat(closeString);
							afterVolume = afterVolume
									+ Integer.parseInt(volumeString);
						}

					} else if (!preSymbol.equalsIgnoreCase(symbol)) { // if
						System.out.println("Symbol changed from " + preSymbol
								+ " to " + symbol);
						// close the last leg of previous trading
						if (afterOpen > 0.001f) {
							count++;
							YStock detail = new YStock();
							detail.setSymbol(preSymbol.toUpperCase());
							detail.setDate(cdate);
							detail.setChour(16);
							detail.setCminute(0);
							myseqIndex = SQLOperation.getSeqIndex(cdate, 16, 0);
							detail.setSeqIndex(myseqIndex);
							detail.setFinalPrice(afterFinal);
							detail.setAdjustedPrice(afterFinal);
							detail.setHighPrice(afterMax);
							detail.setLowPrice(afterMin);

							detail.setOpenPrice(afterOpen);
							detail.setVolume(afterVolume);
							nstkData.put("" + count, detail);
						}

						symCount++; // (!preSymbol.equalsIgnoreCase(symbol))

						// DELETE EXISTING DATA
						// SQLOperation.deleteRecords(preSymbol.toUpperCase(),StaticData.getIntradayLastIndex()+1,StaticData.getIntradayLastIndex()+79);
						// then insert the records nstkData
						insertRecords(symCount, nstkData);
						// check inserted count matches size
						SQLOperation.compareRecordNumber(
								preSymbol.toUpperCase(),
								StaticData.getIntradayLastIndex() + 1,
								StaticData.getIntradayLastIndex() + 79,
								nstkData.size());

						// reinite nstkData
						nstkData = new Hashtable();
						count = 0;

						preOpen = 0.0f;
						preMax = 0.0f;
						preMin = 10000000000.0f;
						preVolume = 0;
						afterOpen = 0.0f;
						afterVolume = 0;
						afterMax = 0.0f;
						afterMin = 10000000000.0f;
						afterFinal = 0.0f;

						preSymbol = symbol;
						if (hours < 9 || (hours == 9 && minutes < 30)) {
							// preHour
							if (preOpen < 0.00000001f) {
								preOpen = Float.parseFloat(openString);
							}

							if (preMax < Float.parseFloat(highString)) {
								preMax = Float.parseFloat(highString);
							}

							if (preMin > Float.parseFloat(lowString)) {
								preMin = Float.parseFloat(lowString);
							}
							preVolume = preVolume
									+ Integer.parseInt(volumeString);

						} else if (hours == 9 && minutes >= 30) {

							count++;
							YStock detail2 = new YStock();
							detail2.setSymbol(symbol.toUpperCase());
							detail2.setDate(cdate);
							detail2.setChour(hours);
							detail2.setCminute(minutes);
							myseqIndex = SQLOperation.getSeqIndex(cdate, hours,
									minutes);
							detail2.setSeqIndex(myseqIndex);
							detail2.setFinalPrice(Float.parseFloat(closeString));
							detail2.setAdjustedPrice(detail2.getFinalPrice());

							if (preOpen > 0.001f) {
								// unlikely you have pre-market trading and 9:30
								// am has zero volume
								// or no trading
								detail2.setOpenPrice(preOpen);
								detail2.setHighPrice(preMax);
								detail2.setLowPrice(preMin);
								preVolume = preVolume
										+ Integer.parseInt(volumeString);
								detail2.setVolume(preVolume);
								preOpen = 0.0f;
								preMax = 0.0f;
								preMin = 10000000000.0f;
								preVolume = 0;

							} else {
								detail2.setOpenPrice(Float
										.parseFloat(openString));
								detail2.setHighPrice(Float
										.parseFloat(highString));
								detail2.setLowPrice(Float.parseFloat(lowString));
								detail2.setVolume(Float
										.parseFloat(volumeString));

							}

							nstkData.put("" + count, detail2);

						} else if (hours > 9 && hours < 16) {
							count++;
							YStock detail3 = new YStock();
							detail3.setSymbol(symbol.toUpperCase());
							detail3.setDate(cdate);
							detail3.setChour(hours);
							detail3.setCminute(minutes);
							myseqIndex = SQLOperation.getSeqIndex(cdate, hours,
									minutes);
							detail3.setSeqIndex(myseqIndex);
							detail3.setFinalPrice(Float.parseFloat(closeString));
							detail3.setAdjustedPrice(detail3.getFinalPrice());
							detail3.setHighPrice(Float.parseFloat(highString));
							detail3.setLowPrice(Float.parseFloat(lowString));
							detail3.setOpenPrice(Float.parseFloat(openString));
							detail3.setVolume(Float.parseFloat(volumeString));
							nstkData.put("" + count, detail3);

						} else if (hours >= 16) {
							// afterHour
							if (afterOpen < 0.00000001f) {
								afterOpen = Float.parseFloat(openString);
							}

							if (afterMax < Float.parseFloat(highString)) {
								afterMax = Float.parseFloat(highString);
							}

							if (afterMin > Float.parseFloat(lowString)) {
								afterMin = Float.parseFloat(lowString);
							}

							afterFinal = Float.parseFloat(closeString);
							afterVolume = afterVolume
									+ Integer.parseInt(volumeString);
						}

					}
				}

			}

			// close the last leg of previous trading
			// close the last leg of previous trading
			if (afterOpen > 0.001f) {

				count++;
				YStock detail = new YStock();
				detail.setSymbol(preSymbol.toUpperCase());
				detail.setDate(ccdate);
				detail.setChour(16);
				detail.setCminute(0);
				long myseqIndex = SQLOperation.getSeqIndex(ccdate, 16, 0);
				detail.setSeqIndex(myseqIndex);
				detail.setFinalPrice(afterFinal);
				detail.setAdjustedPrice(afterFinal);
				detail.setHighPrice(afterMax);
				detail.setLowPrice(afterMin);

				detail.setOpenPrice(afterOpen);
				detail.setVolume(afterVolume);
				nstkData.put("" + count, detail);
			}

			symCount++;

			// DELETE EXISTING DATA
			// SQLOperation.deleteRecords(preSymbol.toUpperCase(),StaticData.getIntradayLastIndex()+1,StaticData.getIntradayLastIndex()+79);
			// insert the records
			insertRecords(symCount, nstkData);
			// check inserted count matches size
			SQLOperation.compareRecordNumber(preSymbol.toUpperCase(),
					StaticData.getIntradayLastIndex() + 1,
					StaticData.getIntradayLastIndex() + 79, nstkData.size());

			in.close();
			in = null;

			System.out.println("Total symbol count " + symCount);

			// System.out.println("total index data count: " + count);
		} catch (Exception ex) {
			try {
				in.close();
				in = null;
				// result = null;
				ex.printStackTrace(System.out);
			} catch (Exception ex2) {

			}

		}
		return nstkData;
	}

	/************* NEW CODE 8/16/20218 *************/

	static public void getBitBotIntradayData(String folder, String date) {
		String path = "C:\\stock\\intraday\\" + folder + "\\";

		boolean notExist = true;
		
		previousStocks = SQLOperation.getCurrentAllStocks(StaticData.getIntradayLastIndex());

		int loop = 1;
		while(loop<4&&(previousStocks==null||previousStocks.size()<4000)){
			previousStocks = SQLOperation.getCurrentAllStocks(StaticData.getIntradayLastIndex()-79*loop);
			loop++;
		}
		
		FilenameFilter textFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".txt")) {
					return true;
				} else {
					return false;
				}
			}
		};

		File tfile = new File(path);
		int noFileTimes = 0;
		int processedFiles = 0;
         int totalInsertCount = 0;

		while (notExist) {
			File[] files = tfile.listFiles(textFilter);
			for (File file : files) {
				if (file.isDirectory()) {
					System.out.print("directory:");
				} else {
					System.out.print("     file:");
					processedFiles++;
					getBitBotStockData(file, date, processedFiles);

					
					// break;
				}
				// System.out.println(file.getCanonicalPath());
			}
			noFileTimes++;
			try { // sleep 20 minute for more files to be downloaded
				Thread.sleep(1200000);
			} catch (Exception ex) {

			}

			if (noFileTimes > 5 && processedFiles > 7000) {
				notExist = false;
				// stop process
				System.out.println(totalInsertCount+" out of processed files "+processedFiles);
			}
		}

	}

	private static boolean getBitBotStockData(File file, String date, int fileCount) {
		boolean read = true;
		BufferedReader in = null;
		boolean inserted = false;

		int hours = 9;
		int minutes = 30;
		int preVolume = 0;
		float preOpen = 0.0f;
		float preMax = 0.0f;
		float preMin = 10000000000.0f;
		float afterOpen = 0.0f;
		int afterVolume = 0;
		float afterMax = 0.0f;
		float afterMin = 10000000000.0f;
		float afterFinal = 0.0f;
		Hashtable nstkData = new Hashtable();

		int count = 0;
		String ccdate = null;
		int symCount = 0;
		String symbol = "";
		int totalRecordCount = 0;
		int todayRecordCount = 0;

		try {
			in = new BufferedReader(new FileReader(file));
			// System.out.println(file.getCanonicalPath());
			symbol = file.getName();
			symbol = symbol.substring(0, symbol.length() - 4);
			System.out.println("Stock symbol is " + symbol);
		} catch (Exception ex) {
			read = false;
		}

		try {
			String index = null;

			boolean recordStart = false;

			while (read) { // while loop
				try {
					index = in.readLine();
					System.out.println("index " + index);

				} catch (Exception ex) {
					ex.printStackTrace(System.out);
					// ingnore no file
				}

				if (index == null) {
					read = false;
				} else if (index != null) { // if recordStart
					System.out.println("index " + index);

					StringTokenizer tok = new StringTokenizer(index, ",\t ");
					String dateString = tok.nextToken().toString();
					totalRecordCount++;

					if (dateString.equalsIgnoreCase(date)) { // top if
						recordStart = true;
						todayRecordCount++;
						StringTokenizer tok2 = new StringTokenizer(dateString,
								"/");
						String mStr = tok2.nextToken().toString();

						String dStr = tok2.nextToken().toString();
						String yStr = tok2.nextToken().toString();

						String cdate = yStr + "-" + mStr + "-" + dStr;

						if (ccdate == null && cdate != null) {
							ccdate = cdate;
						}

						String timeString = tok.nextToken().toString();
						StringTokenizer tok3 = new StringTokenizer(timeString,
								":");
						hours = Integer.parseInt(tok3.nextToken().toString());
						minutes = Integer.parseInt(tok3.nextToken().toString());

						String openString = tok.nextToken().toString();
						String highString = tok.nextToken().toString();
						String lowString = tok.nextToken().toString();
						String closeString = tok.nextToken().toString();
						String volumeString = tok.nextToken().toString();

						// populate records of intraday for the same stokc

						if (hours < 9 || (hours == 9 && minutes < 30)) {
							// preHour
							if (preOpen < 0.00000001f) {
								preOpen = Float.parseFloat(openString);
							}

							if (preMax < Float.parseFloat(highString)) {
								preMax = Float.parseFloat(highString);
							}

							if (preMin > Float.parseFloat(lowString)) {
								preMin = Float.parseFloat(lowString);
							}
							preVolume = preVolume
									+ Integer.parseInt(volumeString);

						} else if ((hours == 9 && minutes >= 30)) {

							count++;
							long myseqIndex = SQLOperation.getSeqIndex(ccdate,
									hours, minutes);

							YStock detail = new YStock();
							detail.setSymbol(symbol.toUpperCase());
							detail.setDate(cdate);
							detail.setChour(hours);
							detail.setCminute(minutes);
							detail.setSeqIndex(myseqIndex);
							detail.setFinalPrice(Float.parseFloat(closeString));
							detail.setAdjustedPrice(detail.getFinalPrice());

							if (preOpen > 0.001f) {
								// unlikely you have pre-market trading and
								// 9:30
								// am has zero volume
								// or no trading
								if (preOpen < 0.00000001f) {
									preOpen = Float.parseFloat(openString);
								}

								if (preMax < Float.parseFloat(highString)) {
									preMax = Float.parseFloat(highString);
								}

								if (preMin > Float.parseFloat(lowString)) {
									preMin = Float.parseFloat(lowString);
								}
								preVolume = preVolume
										+ Integer.parseInt(volumeString);

								detail.setOpenPrice(preOpen);
								detail.setHighPrice(preMax);
								detail.setLowPrice(preMin);
								detail.setVolume(preVolume);
								preOpen = 0.0f;
								preMax = 0.0f;
								preMin = 10000000000.0f;
								preVolume = 0;
								afterOpen = 0.0f;
								afterVolume = 0;
								afterMax = 0.0f;
								afterMin = 10000000000.0f;
								afterFinal = 0.0f;

							} else {
								detail.setOpenPrice(Float
										.parseFloat(openString));
								detail.setHighPrice(Float
										.parseFloat(highString));
								detail.setLowPrice(Float.parseFloat(lowString));
								detail.setVolume(Float.parseFloat(volumeString));

							}

							nstkData.put("" + count, detail);

						} else if (hours > 9 && hours < 16) {
							count++;
							long myseqIndex = SQLOperation.getSeqIndex(cdate,
									hours, minutes);

							System.out.println("count " + count);
							System.out.println("symbol " + symbol);
							System.out.println("dateString " + dateString);
							System.out.println("dStr " + dStr);
							System.out.println("mStr " + mStr);
							System.out.println("yStr " + yStr);
							System.out.println("cDate " + cdate);
							System.out.println("timeString " + timeString);
							System.out.println("Hours " + hours);
							System.out.println("Minutes " + minutes);
							System.out.println(" myseqIndex  " + myseqIndex);
							System.out.println("openString " + openString);
							System.out.println("highString " + highString);
							System.out.println("lowString " + lowString);
							System.out.println("closeString " + closeString);
							System.out.println("volumeString " + volumeString);

							YStock detail = new YStock();
							detail.setSymbol(symbol.toUpperCase());
							detail.setDate(ccdate);
							detail.setChour(hours);
							detail.setCminute(minutes);
							detail.setSeqIndex(myseqIndex);
							detail.setFinalPrice(Float.parseFloat(closeString));
							detail.setAdjustedPrice(detail.getFinalPrice());
							detail.setHighPrice(Float.parseFloat(highString));
							detail.setLowPrice(Float.parseFloat(lowString));

							detail.setOpenPrice(Float.parseFloat(openString));
							detail.setVolume(Float.parseFloat(volumeString));
							nstkData.put("" + count, detail);

						} else if (hours >= 16) {
							// afterHour
							if (afterOpen < 0.00000001f) {
								afterOpen = Float.parseFloat(openString);
							}

							if (afterMax < Float.parseFloat(highString)) {
								afterMax = Float.parseFloat(highString);
							}

							if (afterMin > Float.parseFloat(lowString)) {
								afterMin = Float.parseFloat(lowString);
							}

							afterFinal = Float.parseFloat(closeString);
							afterVolume = afterVolume
									+ Integer.parseInt(volumeString);
						}

					}
				}

			} // while loop

			// close the last leg of previous trading
			if (recordStart && afterOpen > 0.001f) {
				count++;
				YStock detail = new YStock();
				detail.setSymbol(symbol);
				detail.setDate(ccdate);
				detail.setChour(16);
				detail.setCminute(0);
				long myseqIndex = SQLOperation.getSeqIndex(ccdate, 16, 0);
				detail.setSeqIndex(myseqIndex);
				detail.setFinalPrice(afterFinal);
				detail.setAdjustedPrice(afterFinal);
				detail.setHighPrice(afterMax);
				detail.setLowPrice(afterMin);

				detail.setOpenPrice(afterOpen);
				detail.setVolume(afterVolume);
				nstkData.put("" + count, detail);
			}
			symCount++; // (!preSymbol.equalsIgnoreCase(symbol))
			in.close();
			// DELETE EXISTING DATA
			// SQLOperation.deleteRecords(preSymbol.toUpperCase(),StaticData.getIntradayLastIndex()+1,StaticData.getIntradayLastIndex()+79);
			// then insert the records nstkData
			System.out.println("Total Record count " + totalRecordCount);
			System.out.println("Today Record count " + nstkData.size());

			//SQLOperation.deleteRecords(symbol, 32046, 32124);
             //EITHER MORE THAN 50 RECORDS THAT DAY OR HAS PREVIOUS RECORDS
			//IN DB ALREADY
			if (previousStocks.containsKey(symbol)||nstkData.size()>50) {
			//30% of the past 17 days AVG or 65% today, then insert
			//update this to new algorithm above once done today
			//if(totalRecordCount>=400||nstkData.size()>50){
				insertRecords(fileCount, nstkData);
				// check inserted count matches size
				SQLOperation
						.compareRecordNumber(symbol.toUpperCase(),
								StaticData.getIntradayLastIndex() + 1,
								StaticData.getIntradayLastIndex() + 79,
								nstkData.size());
				markFileProcessed(file, -1);
				inserted = true;
			} else {
				markFileProcessed(file, totalRecordCount);
			}

			// System.out.println("total index data count: " + count);
		} catch (Exception ex) {
			try {
				in.close();
				in = null;
				// result = null;
				ex.printStackTrace(System.out);
			} catch (Exception ex2) {

			}

		}
		return inserted;

	}

	/************ NEW CODE 8/16/2018 **************/

	static private String getMonth(String mStr) {
		String month = "01";

		if (mStr.equalsIgnoreCase("Jan")) {
			month = "01";
		} else if (mStr.equalsIgnoreCase("Feb")) {
			month = "02";
		} else if (mStr.equalsIgnoreCase("Mar")) {
			month = "03";
		} else if (mStr.equalsIgnoreCase("Apr")) {
			month = "04";
		} else if (mStr.equalsIgnoreCase("May")) {
			month = "05";
		} else if (mStr.equalsIgnoreCase("Jun")) {
			month = "06";
		} else if (mStr.equalsIgnoreCase("Jul")) {
			month = "07";
		} else if (mStr.equalsIgnoreCase("Aug")) {
			month = "08";
		} else if (mStr.equalsIgnoreCase("Sept")) {
			month = "09";
		} else if (mStr.equalsIgnoreCase("Oct")) {
			month = "10";
		} else if (mStr.equalsIgnoreCase("Nov")) {
			month = "11";
		} else if (mStr.equalsIgnoreCase("Dec")) {
			month = "12";
		}

		return month;
	}

	static public Hashtable getRealGoogleIntradayHistory(String symbol,
			int folder, int sfn, String insertSymbol) {
		String path = "C:\\stock\\intraday\\" + folder + "\\" + sfn + "\\"
				+ symbol.toLowerCase() + ".csv";
		Hashtable result = new Hashtable();
		boolean read = true;
		BufferedReader in = null;

		int hours = 9;
		int minutes = 30;
		// int previousGapNumber = 0;
		// int currentGapNumber = 0;
		try {
			in = new BufferedReader(new FileReader(path));
		} catch (Exception ex) {
			read = false;
		}

		try {
			int count = 0;
			String index = null;

			boolean recordStart = false;
			boolean newDay = false;
			String myDateString = "";
			String tempdateString = "";
			int utime = 0;
			String symb = "";
			float divs = 0.0f;
			long seqIndex = 0L;

			while (read) {
				try {
					index = in.readLine();
				} catch (Exception ex) {
					ex.printStackTrace(System.out);
					// ingnore no file
				}
				if (index == null) {
					read = false;
				} else if (index.indexOf("TIMEZONE_OFFSET") >= 0) {
					recordStart = true;
				} else if (recordStart == true) {
					count++;
					// System.out.println("I " + count);

					StringTokenizer tok = new StringTokenizer(index, ",\t");
					YStock detail = new YStock();
					detail.setSymbol(symbol.toUpperCase());
					if (insertSymbol != null && insertSymbol.length() > 0) {
						detail.setSymbol(insertSymbol.toUpperCase());
					}
					String gapString = tok.nextToken().trim();
					if (gapString.charAt(0) == 'a') {
						utime = Integer.parseInt(gapString.substring(1,
								gapString.length()));
						System.out.println("Utime " + utime);
						// Select VARCHAR_FORMAT(CDATE,'MM-DD-YYYY') AS VDATE
						// from BPMADMIN.GSEQ where UTIME=2145882600;
						myDateString = SQLOperation.getDateFromReference(utime);
						detail.setDate(myDateString);
						detail.setChour(hours);
						detail.setCminute(minutes);
						long myseqIndex = SQLOperation.getSeqIndex(
								myDateString, hours, minutes);

						detail.setSeqIndex(myseqIndex);
						seqIndex = myseqIndex;
					} else {
						int gapNumber = Integer.parseInt(gapString);

						int myutime = utime + gapNumber * 5 * 60; // utime is
																	// based on
																	// seconds
						System.out.println("Utime " + myutime);
						// Select VARCHAR_FORMAT(CDATE,'MM-DD-YYYY') AS VDATE
						// from BPMADMIN.GSEQ where UTIME=2145882600;
						myDateString = SQLOperation
								.getDateFromReference(myutime);

						// 5 min gap
						int minutesTemp = minutes + gapNumber * 5;
						int temphours = 0;
						if (minutesTemp >= 60) {
							temphours = (hours + minutesTemp / 60) % 24;
							minutesTemp = minutesTemp % 60;
						} else {
							temphours = hours;
						}

						detail.setDate(myDateString);
						long myseqIndex = SQLOperation.getSeqIndex(
								myDateString, temphours, minutesTemp);
						if (myseqIndex <= 0) {
							System.out
									.println("*******$$$$$$$$$$$$$$$$$$$****** No index found for "
											+ myDateString
											+ " at "
											+ temphours
											+ ":" + minutesTemp);
							seqIndex = seqIndex + 1;
							detail.setSeqIndex(seqIndex);

						} else {
							detail.setSeqIndex(myseqIndex);
							seqIndex = myseqIndex;
						}

						detail.setChour(temphours);
						detail.setCminute(minutesTemp);

					}
					detail.setFinalPrice(Float.parseFloat(tok.nextToken()
							.trim()));
					detail.setAdjustedPrice(detail.getFinalPrice());
					detail.setHighPrice(Float
							.parseFloat(tok.nextToken().trim()));
					detail.setLowPrice(Float.parseFloat(tok.nextToken().trim()));

					detail.setOpenPrice(Float
							.parseFloat(tok.nextToken().trim()));
					detail.setVolume(Float.parseFloat(tok.nextToken().trim()));
					result.put("" + count, detail);

				}
			}

			in.close();
			in = null;
			// result = null;
			// System.out.println("total index data count: " + count);
		} catch (Exception ex) {
			try {
				in.close();
				in = null;
				// result = null;
				ex.printStackTrace(System.out);
			} catch (Exception ex2) {

			}

		}
		return result;
	}

	static public Hashtable NotUsedOldgetRealGoogleIntradayHistory(
			String symbol, int folder, int sfn, String insertSymbol) {
		String path = "C:\\stock\\intraday\\" + folder + "\\" + sfn + "\\"
				+ symbol.toLowerCase() + ".csv";
		Hashtable result = new Hashtable();
		boolean read = true;
		BufferedReader in = null;

		int hours = 9;
		int minutes = 30;
		int previousGapNumber = 0;
		int currentGapNumber = 0;
		try {
			in = new BufferedReader(new FileReader(path));
		} catch (Exception ex) {
			read = false;
		}

		try {
			int count = 0;
			String index = null;

			boolean recordStart = false;
			boolean newDay = false;
			String myDateString = "";
			String tempdateString = "";
			int utime = 0;
			String symb = "";
			float divs = 0.0f;
			while (read) {
				try {
					index = in.readLine();
				} catch (Exception ex) {
					ex.printStackTrace(System.out);
					// ingnore no file
				}
				if (index == null) {
					read = false;
				} else if (index.indexOf("TIMEZONE_OFFSET") >= 0) {
					recordStart = true;
				} else if (recordStart == true) {
					count++;
					// System.out.println("I " + count);

					StringTokenizer tok = new StringTokenizer(index, ",\t");
					YStock detail = new YStock();
					detail.setSymbol(symbol.toUpperCase());
					if (insertSymbol != null && insertSymbol.length() > 0) {
						detail.setSymbol(insertSymbol.toUpperCase());
					}
					String gapString = tok.nextToken().trim();
					if (gapString.charAt(0) == 'a') {
						utime = Integer.parseInt(gapString.substring(1,
								gapString.length()));
						System.out.println("Utime " + utime);
						// Select VARCHAR_FORMAT(CDATE,'MM-DD-YYYY') AS VDATE
						// from BPMADMIN.GSEQ where UTIME=2145882600;
						myDateString = SQLOperation.getDateFromReference(utime);
						detail.setDate(myDateString);
						detail.setChour(hours);
						detail.setCminute(minutes);
						long seqIndex = SQLOperation.getSeqIndex(myDateString,
								hours, minutes);
						if (seqIndex <= 0) {
							System.out
									.println("*******$$$$$$$$$$$$$$$$$$$****** No index found for "
											+ myDateString
											+ " at "
											+ hours
											+ ":" + minutes);
						}
						detail.setSeqIndex(seqIndex);
						previousGapNumber = 0;
						currentGapNumber = 0;
					} else {
						int gapNumber = Integer.parseInt(gapString);
						if (previousGapNumber == 0) {
							previousGapNumber = gapNumber;
							int myutime = utime + gapNumber * 5;
							System.out.println("Utime " + myutime);
							// Select VARCHAR_FORMAT(CDATE,'MM-DD-YYYY') AS
							// VDATE
							// from BPMADMIN.GSEQ where UTIME=2145882600;
							myDateString = SQLOperation
									.getDateFromReference(utime);

							// 5 min gap
							int minutesTemp = minutes + gapNumber * 5;
							int temphours = 0;
							if (minutesTemp >= 60) {
								temphours = (hours + minutesTemp / 60) % 24;
								minutesTemp = minutesTemp % 60;
							} else {
								temphours = hours;
							}

							if (tempdateString.length() == 0) {
								detail.setDate(myDateString);
								long seqIndex = SQLOperation.getSeqIndex(
										myDateString, temphours, minutesTemp);
								if (seqIndex <= 0) {
									System.out
											.println("*******$$$$$$$$$$$$$$$$$$$****** No index found for "
													+ myDateString
													+ " at "
													+ temphours
													+ ":"
													+ minutesTemp);
								}
								detail.setSeqIndex(seqIndex);
							} else {
								detail.setDate(tempdateString);
								long seqIndex = SQLOperation.getSeqIndex(
										tempdateString, temphours, minutesTemp);
								if (seqIndex <= 0) {
									System.out
											.println("*******$$$$$$$$$$$$$$$$$$$****** No index found for "
													+ myDateString
													+ " at "
													+ temphours
													+ ":"
													+ minutesTemp);
								}
								detail.setSeqIndex(seqIndex);
							}

							detail.setChour(temphours);
							detail.setCminute(minutesTemp);
						} else if (currentGapNumber == 0) {
							currentGapNumber = gapNumber;
							// 5 min gap

							int minutesTemp = minutes + gapNumber * 5;
							int temphours = 0;
							if (minutesTemp >= 60) {
								temphours = (hours + minutesTemp / 60) % 24;
								minutesTemp = minutesTemp % 60;
							} else {
								temphours = hours;
							}

							if (tempdateString.length() == 0) {
								detail.setDate(myDateString);
								long seqIndex = SQLOperation.getSeqIndex(
										myDateString, temphours, minutesTemp);
								if (seqIndex <= 0) {
									System.out
											.println("*******$$$$$$$$$$$$$$$$$$$****** No index found for "
													+ myDateString
													+ " at "
													+ temphours
													+ ":"
													+ minutesTemp);
								}
								detail.setSeqIndex(seqIndex);
							} else {
								detail.setDate(tempdateString);
								long seqIndex = SQLOperation.getSeqIndex(
										tempdateString, temphours, minutesTemp);
								if (seqIndex <= 0) {
									System.out
											.println("*******$$$$$$$$$$$$$$$$$$$****** No index found for "
													+ myDateString
													+ " at "
													+ temphours
													+ ":"
													+ minutesTemp);
								}
								detail.setSeqIndex(seqIndex);
							}

							detail.setChour(temphours);
							detail.setCminute(minutesTemp);
						} else {
							previousGapNumber = currentGapNumber;
							currentGapNumber = gapNumber;

							if ((currentGapNumber - previousGapNumber) < 90) {
								int minutesTemp = minutes + gapNumber * 5;
								int hoursTemp = (hours + minutesTemp / 60) % 24;
								minutesTemp = minutesTemp % 60;

								if (tempdateString.length() == 0) {
									detail.setDate(myDateString);
									long seqIndex = SQLOperation.getSeqIndex(
											myDateString, hoursTemp,
											minutesTemp);
									if (seqIndex <= 0) {
										System.out
												.println("*******$$$$$$$$$$$$$$$$$$$****** No index found for "
														+ myDateString
														+ " at "
														+ hoursTemp
														+ ":"
														+ minutesTemp);
									}
									detail.setSeqIndex(seqIndex);
								} else {
									detail.setDate(tempdateString);
									long seqIndex = SQLOperation.getSeqIndex(
											tempdateString, hoursTemp,
											minutesTemp);
									if (seqIndex <= 0) {
										System.out
												.println("*******$$$$$$$$$$$$$$$$$$$****** No index found for "
														+ myDateString
														+ " at "
														+ hoursTemp
														+ ":"
														+ minutesTemp);
									}
									detail.setSeqIndex(seqIndex);
								}

								detail.setChour(hoursTemp);
								detail.setCminute(minutesTemp);
							} else {
								System.out.println("previousGapNumber is "
										+ previousGapNumber);
								System.out.println("currentGapNumber is "
										+ currentGapNumber);
								System.out.println("dateString is "
										+ myDateString);
								StringTokenizer toks = new StringTokenizer(
										myDateString, "-");
								Calendar cal1 = Calendar.getInstance();
								cal1.set(Calendar.MONTH,
										Integer.parseInt(toks.nextToken()) - 1);
								cal1.set(Calendar.DAY_OF_MONTH,
										Integer.parseInt(toks.nextToken()));
								cal1.set(Calendar.YEAR,
										Integer.parseInt(toks.nextToken()));
								cal1.set(Calendar.HOUR, hours);
								cal1.set(Calendar.MINUTE, minutes);

								cal1.add(Calendar.MINUTE, gapNumber * 5);
								tempdateString = ""
										+ (cal1.get(Calendar.MONTH) + 1) + "-"
										+ cal1.get(Calendar.DAY_OF_MONTH) + "-"
										+ cal1.get(Calendar.YEAR);
								System.out.println("--- Datestring is now "
										+ tempdateString);
								if (tempdateString.length() == 0) {
									detail.setDate(myDateString);
									long seqIndex = SQLOperation.getSeqIndex(
											myDateString, hours, minutes);
									if (seqIndex <= 0) {
										System.out
												.println("*******$$$$$$$$$$$$$$$$$$$****** No index found for "
														+ myDateString
														+ " at "
														+ hours + ":" + minutes);
									}
									detail.setSeqIndex(seqIndex);
								} else {
									detail.setDate(tempdateString);
									long seqIndex = SQLOperation.getSeqIndex(
											tempdateString, hours, minutes);
									if (seqIndex <= 0) {
										System.out
												.println("*******$$$$$$$$$$$$$$$$$$$****** No index found for "
														+ myDateString
														+ " at "
														+ hours + ":" + minutes);
									}
									detail.setSeqIndex(seqIndex);
								}

								int tempMinutes = cal1.get(Calendar.MINUTE) % 60;

								detail.setChour(hours);
								detail.setCminute(minutes);

								previousGapNumber = currentGapNumber;
								currentGapNumber = 0;

							}
						}
					}
					/*
					 * StringTokenizer tok2 = new StringTokenizer(dateString,
					 * "-/"); String y = tok2.nextToken(); String m =
					 * tok2.nextToken(); if (Integer.parseInt(m) > 9) { m = "" +
					 * Integer.parseInt(m); } else { m = "0" +
					 * Integer.parseInt(m); }
					 * 
					 * String d = tok2.nextToken(); if (Integer.parseInt(d) > 9)
					 * { d = "" + Integer.parseInt(d); } else { d = "0" +
					 * Integer.parseInt(d); }
					 * 
					 * detail.setDate(m + "-" + d + "-" + y); //
					 * detail.setTradingDate(dateString.replaceAll("/", //
					 * "-")); detail.setTimestamp(tok.nextToken() .trim());
					 */
					detail.setFinalPrice(Float.parseFloat(tok.nextToken()
							.trim()));
					detail.setAdjustedPrice(detail.getFinalPrice());
					detail.setHighPrice(Float
							.parseFloat(tok.nextToken().trim()));
					detail.setLowPrice(Float.parseFloat(tok.nextToken().trim()));

					detail.setOpenPrice(Float
							.parseFloat(tok.nextToken().trim()));
					detail.setVolume(Float.parseFloat(tok.nextToken().trim()));
					result.put("" + count, detail);
					/*
					 * System.out.println("" + count + " " + detail.getSymbol()
					 * + " date: " + detail.getDate() + " hour: " +
					 * detail.getChour() + " minuts: " + detail.getCminute() +
					 * " open: " + detail.getOpenPrice() + " high: " +
					 * detail.getHighPrice() + " low: " + detail.getLowPrice() +
					 * " close: " + detail.getFinalPrice() + " vol: " +
					 * detail.getVolume() + " adjusted: " +
					 * detail.getAdjustedPrice());
					 */

					/*
					 * for(int w=1; w<=result.size();w++){ YStock wdetail =
					 * (YStock)result.get(""+w);
					 * 
					 * System.out.println("*****" + w + " " +
					 * wdetail.getSymbol() + " date: " + wdetail.getDate() +
					 * " hour: " + wdetail.getChour() + " minuts: " +
					 * wdetail.getCminute() + " open: " + wdetail.getOpenPrice()
					 * + " high: " + wdetail.getHighPrice() + " low: " +
					 * wdetail.getLowPrice() + " close: " +
					 * wdetail.getFinalPrice() + " vol: " + wdetail.getVolume()
					 * + " adjusted: " + wdetail.getAdjustedPrice()); }
					 */

					// System.out.println(detail.getTimestamp()+","+(9+(count+6)/12));
					// System.out.println(detail.getTimestamp()+","+(9+(count+6)/12));
					// StringTokenizer tok4 = new
					// StringTokenizer(detail.getTimestamp(),":");
					// tok4.nextToken();
					// System.out.println("Minutes "+tok4.nextToken());

				}

			}

			in.close();
			in = null;
			// result = null;
			// System.out.println("total index data count: " + count);
		} catch (Exception ex) {
			try {
				in.close();
				in = null;
				// result = null;
				ex.printStackTrace(System.out);
			} catch (Exception ex2) {

			}

		}
		return result;
	}

	static public Hashtable getRealGoogleSymbols(int folder) {
		String path = "C:\\stock\\intraday\\" + folder + "\\";
		Hashtable result = new Hashtable();

		try {
			File dir = new File(path);
			File[] files = dir.listFiles();

			for (int k = 0; k < files.length; k++) {
				String name = files[k].getName();
				System.out.print(k + " file name is " + name + " ");

				if (name.indexOf(".csv") > 0) {
					String symbol = name.substring(0, name.indexOf(".csv"));
					System.out.println(k + " stock name is " + symbol);
					result.put(symbol, symbol);
				}
			}

		} catch (Exception ex2) {

		}

		return result;
	}

	static public Hashtable getIntradayStocks(String path) {
		Hashtable result = new Hashtable();
		path = "C:\\stock\\intraday\\";
		File file = new File(path);
		File[] files = file.listFiles();
		for (int k = 0; k < files.length; k++) {
			String name = files[k].getName();
			int pos = name.indexOf(".csv");
			name = name.substring(0, pos);
			result.put(name, name);

		}

		return result;
	}

	static public Hashtable getIndexHistory(String symbol) {
		BufferedReader in = null;

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
		BufferedReader in = null;

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
		BufferedReader in = null;

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
		BufferedReader in = null;

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
