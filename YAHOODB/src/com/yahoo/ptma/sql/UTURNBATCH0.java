package com.yahoo.ptma.sql;

import com.yahoo.ptma.helper.Files;

public class UTURNBATCH0 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Files.createEODTagFile("UTurnBatch0Start.txt");
		
		SQLOperation.getMegaUTurnBatch(43390);
		
		Files.createEODTagFile("UTurnBatch0End.txt");
	}

}
