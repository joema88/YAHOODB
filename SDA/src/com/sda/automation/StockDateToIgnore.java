package com.sda.automation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.*;

public class StockDateToIgnore {

	public static void main(String[] args) {

		String fileLoc = "C:\\Users\\bpmadmin\\Documents\\";
		String dateToIgnore="6/29/20";
		File f = new File(fileLoc);

		// array of files and directory

		String[] fnames = f.list();
		// for each name in the path array
		for (String fname : fnames) {

			// prints filename and directory name
			System.out.println(fname);

			if (fname.toLowerCase().contains("yellow.csv") || fname.toLowerCase().contains("pink.csv")
					|| fname.toLowerCase().contains("teal.csv") || fname.toLowerCase().contains("base.csv")
					|| fname.toLowerCase().contains("cx520.csv")) {
				BufferedReader reader;
				File fout = new File(fileLoc + fname + ".bk");

				try {
					if (!fout.exists())
						fout.createNewFile();

					FileWriter fw = new FileWriter(fileLoc + fname + ".bk", true);
					BufferedWriter bw = new BufferedWriter(fw);
					PrintWriter out = new PrintWriter(bw);

					reader = new BufferedReader(new FileReader(fileLoc + fname));
					String line = reader.readLine();
					out.write(line);
					while (line != null) {
						// System.out.println(line);
						// read next line
						try {
							line = reader.readLine();
							if (line.indexOf(dateToIgnore) < 0) {
								out.write(line);
								out.write("\n");
								System.out.println("New line " + line);
							}
						} catch (Exception ex) {
							System.out.println("Caught error...");
							ex.printStackTrace(System.out);
						}

					}
					reader.close();
					out.close();
					
					File oFile = new File(fileLoc + fname);
					oFile.delete();
					fout.renameTo(oFile);
					
				} catch (IOException e) {
					e.printStackTrace();
				}

				//break;
			}
		}
	}

}
