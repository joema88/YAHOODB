package com.yahoo.ptma.sql;

import com.yahoo.ptma.helper.Files;

public class UTURNBATCH5 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Files.createEODTagFile("UTurnBatch5Start.txt");
		SQLOperationUT5.getMegaUTurnBatch(43390);
		Files.createEODTagFile("UTurnBatch5End.txt");
	}

}
