package com.yahoo.ptma.sql;

import com.yahoo.ptma.helper.Files;

public class UTURNBATCH6 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Files.createEODTagFile("UTurnBatch6Start.txt");
		SQLOperationUT6.getMegaUTurnBatch(43390);
		Files.createEODTagFile("UTurnBatch6End.txt");
	}

}
