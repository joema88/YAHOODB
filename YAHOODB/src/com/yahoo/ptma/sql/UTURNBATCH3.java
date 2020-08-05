package com.yahoo.ptma.sql;

import com.yahoo.ptma.helper.Files;

public class UTURNBATCH3 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Files.createEODTagFile("UTurnBatch3Start.txt");
		SQLOperationUT3.getMegaUTurnBatch(43390);
		Files.createEODTagFile("UTurnBatch3End.txt");
	}

}
