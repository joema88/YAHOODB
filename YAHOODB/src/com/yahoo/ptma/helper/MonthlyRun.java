package com.yahoo.ptma.helper;

public class MonthlyRun {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		boolean con = true;
		int k = 1;
		Files.createEODTagFile("MonthlyProcessStart"+k+".txt");
		while (con) {
			System.out.println("Loop " + k);
			if (Files.dailyCalDoneFile()) {
				Files.createEODTagFile("MonthlyStart"+k+".txt");
				DailStockHistory.recalculateALLFreshStocks();
				con = false;
				Files.createEODTagFile("MonthlyDone"+k+".txt");
			}

			try {
				Thread.sleep(10000);
				k++;
			} catch (Exception ex) {

			}
		}
	}

}
