package com.sda.automation;

import java.io.*;

public class StockFileRename {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String fileLoc = "C:\\Users\\bpmadmin\\Documents\\";
		File f = new File(fileLoc);

		// array of files and directory

		String[] fnames = f.list();
		// for each name in the path array
		for (String fname : fnames) {

			// prints filename and directory name
			System.out.println(fname);
			
			if(fname.toLowerCase().contains("yellow")) {
				int pos1= fname.toLowerCase().indexOf("yellow");
				String part1 =fname.substring(0,pos1);
				String fileName = part1+"Yellow.csv";
				File oldfile = new File(fileLoc+fname);
				File newfile = new File(fileLoc+fileName);

				if (oldfile.renameTo(newfile)) {
					System.out.println("File name changed succesful ");
				} else {
					System.out.println("Rename failed");
				}
			}else if(fname.toLowerCase().contains("teal")) {
				int pos1= fname.toLowerCase().indexOf("teal");
				String part1 =fname.substring(0,pos1);
				String fileName = part1+"Teal.csv";
				File oldfile = new File(fileLoc+fname);
				File newfile = new File(fileLoc+fileName);

				if (oldfile.renameTo(newfile)) {
					System.out.println("File name changed succesful ");
				} else {
					System.out.println("Rename failed");
				}
			}else if(fname.toLowerCase().contains("pink")) {
				int pos1= fname.toLowerCase().indexOf("pink");
				String part1 =fname.substring(0,pos1);
				String fileName = part1+"Pink.csv";
				File oldfile = new File(fileLoc+fname);
				File newfile = new File(fileLoc+fileName);

				if (oldfile.renameTo(newfile)) {
					System.out.println("File name changed succesful ");
				} else {
					System.out.println("Rename failed");
				}
			}
		}
		
	}

}
