package com.intraday.ptma.helper;

import java.util.Calendar;

import com.intraday.ptma.sql.*;

public class WWIndexSum {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//SQLOperation.getIndexStatus(10693, "DCP");
		//42691 should be the larget, current to date index
		//SQLOperation.getIndexStatus(42837, "PTMA");
		
		Calendar cal1 = Calendar.getInstance();
		cal1.set(Calendar.YEAR, 2017);
		cal1.set(Calendar.MONTH,0);
		cal1.set(Calendar.DAY_OF_MONTH, 1);
		//cal1.set(Calendar.HOUR_OF_DAY,10);
		
		for(int k=0; k<10;k++){
			
			System.out.println(cal1.get(Calendar.YEAR)+"-"+(cal1.get(Calendar.MONTH)+1)+"-"+cal1.get(Calendar.DAY_OF_MONTH));
		int weekDate = cal1.get(Calendar.DAY_OF_WEEK);
		System.out.println("week date is "+weekDate);
		cal1.add(Calendar.HOUR, 24);
		}
	}

}
