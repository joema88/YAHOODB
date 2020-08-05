package com.yahoo.ptma.helper;

import com.yahoo.ptma.sql.*;
// test weekly SPY Range

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
