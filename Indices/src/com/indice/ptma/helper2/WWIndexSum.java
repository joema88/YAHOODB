package com.indice.ptma.helper2;

import com.indice.ptma.sql2.*;

public class WWIndexSum {

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//SQLOperation.getIndexStatus(10693, "DCP");
		//42691 should be the larget, current to date index
		SQLOperation.getIndexStatus(42837, "PTMA");
	}

}
