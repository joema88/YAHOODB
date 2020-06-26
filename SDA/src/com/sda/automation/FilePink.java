package com.sda.automation;

import java.io.*;

public class FilePink {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {

			String file = "C:\\Users\\Udemy\\dockerDevOps\\test\\stocks.txt";
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = null;
			long t1 = System.currentTimeMillis();
			int totalStocks = 0;

			while ((line = br.readLine()) != null) {
				String symbol = line.strip();
				if (symbol.length() >= 1) {

					if (!checkFileExist(symbol)) {
						totalStocks++;
						StockPink.process(symbol);
						System.out.println(totalStocks+" Processed stock "+symbol);
					}
				}

			}

			long t2 = System.currentTimeMillis();
			System.out.println(totalStocks + " stock download, Total time cost minutes:" + (t2 - t1) / (1000 * 60));

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	public static boolean checkFileExist(String symbol) {
		boolean exists = false;
		String fileBase = "C:\\Users\\bpmadmin\\Documents\\";
		String fullFileName = fileBase + "StrategyReports_" + symbol + "_Pink.csv";
		File file = new File(fullFileName);

		if (file.exists())
			exists = true;

		return exists;
	}

}
