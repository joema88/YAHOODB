package com.intraday.ptma.helper;

import com.intraday.ptma.sql.*;

public class SPY {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//SQLOperation.findSpyWeeklyStatus(10708,4920);
		SQLOperation.findSpyStatusByDays(10708,4920,9);

	}

}
