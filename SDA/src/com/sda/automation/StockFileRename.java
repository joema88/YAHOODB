package com.sda.automation;

import java.io.*;

public class StockFileRename {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String fileLoc = "C:\\Users\\bpmadmin\\Documents\\";
	//	fileLoc = "C:\\Users\\Udemy\\dockerDevOps\\test\\simple\\";
		File f = new File(fileLoc);

		// array of files and directory

		String[] fnames = f.list();
		// for each name in the path array
		for (String fname : fnames) {

			// prints filename and directory name
			System.out.println(fname);

			if (fname.toLowerCase().contains("yellow")) {
				int pos1 = fname.toLowerCase().indexOf("yellow");
				String part1 = fname.substring(0, pos1);
				String fileName = part1 + "Yellow.csv";
				File oldfile = new File(fileLoc + fname);
				File newfile = new File(fileLoc + fileName);

				if (oldfile.renameTo(newfile)) {
					System.out.println("File name changed succesful ");
				} else {
					System.out.println("Rename failed");
				}
			} else if (fname.toLowerCase().contains("teal")) {
				int pos1 = fname.toLowerCase().indexOf("teal");
				String part1 = fname.substring(0, pos1);
				String fileName = part1 + "Teal.csv";
				File oldfile = new File(fileLoc + fname);
				File newfile = new File(fileLoc + fileName);

				if (oldfile.renameTo(newfile)) {
					System.out.println("File name changed succesful ");
				} else {
					System.out.println("Rename failed");
				}
			} else if (fname.toLowerCase().contains("pink")) {
				int pos1 = fname.toLowerCase().indexOf("pink");
				String part1 = fname.substring(0, pos1);
				String fileName = part1 + "Pink.csv";
				File oldfile = new File(fileLoc + fname);
				File newfile = new File(fileLoc + fileName);

				if (oldfile.renameTo(newfile)) {
					System.out.println("File name changed succesful ");
				} else {
					System.out.println("Rename failed");
				}
			} else if (fname.toLowerCase().contains("base")) {
				int pos1 = fname.toLowerCase().indexOf("base");
				String part1 = fname.substring(0, pos1);
				String fileName = part1 + "Base.csv";
				File oldfile = new File(fileLoc + fname);
				File newfile = new File(fileLoc + fileName);

				if (oldfile.renameTo(newfile)) {
					System.out.println("File name changed succesful ");
				} else {
					System.out.println("Rename failed");
				}
			} else if (fname.toLowerCase().contains("cx520")) {
				int pos1 = fname.toLowerCase().indexOf("cx520");
				String part1 = fname.substring(0, pos1);
				String fileName = part1 + "CX520.csv";
				File oldfile = new File(fileLoc + fname);
				File newfile = new File(fileLoc + fileName);

				if (oldfile.renameTo(newfile)) {
					System.out.println("File name changed succesful ");
				} else {
					System.out.println("Rename failed");
				}
			}
		}

	}

}
