package com.yahoo.ptma.sql;

import com.yahoo.ptma.helper.Files;

public class UTURNBATCH4 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Files.createEODTagFile("UTurnBatch4Start.txt");
		SQLOperationUT4.getMegaUTurnBatch(43390);
		Files.createEODTagFile("UTurnBatch4End.txt");
	}

}
