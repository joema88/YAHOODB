package com.yahoo.ptma.sql;

import com.yahoo.ptma.helper.Files;

public class UTURNBATCH1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Files.createEODTagFile("UTurnBatch1Start.txt");
		SQLOperationUT1.getMegaUTurnBatch(43390);
		Files.createEODTagFile("UTurnBatch1End.txt");
	}

}
