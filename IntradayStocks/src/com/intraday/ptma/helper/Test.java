package com.intraday.ptma.helper;

import com.intraday.ptma.sql.SQLOperation;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
			//HttpDownload hd = new HttpDownload();
			//hd.downLoadStockHistory("FB", 1, 92,1);
			//hd.downLoadStockHistory("AAPL", 1, 92,1);
			SQLOperation.padIntradayRecords("A", 31177);
			
		}catch(Exception ex){
			ex.printStackTrace(System.out);
		}
	
	}

}
