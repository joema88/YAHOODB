package com.yahoo.ptma.sql;

import com.yahoo.ptma.helper.Files;

public class UTURNBATCH2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Files.createEODTagFile("UTurnBatch2Start.txt");
		SQLOperationUT2.getMegaUTurnBatch(43390);
		Files.createEODTagFile("UTurnBatch2End.txt");
	}

}
