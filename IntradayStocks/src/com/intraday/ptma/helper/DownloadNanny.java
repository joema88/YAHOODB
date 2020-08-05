package com.intraday.ptma.helper;

public class DownloadNanny {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		boolean cont = true;
		int preCount = 0;
		int steadyCount = 0;
		int nc = 0;
		Files.createIntradyTagFile("StartDownnloadNanny.txt");
		Files.checkIntradayFolderExists(StaticData.getIntradayFolderStart());
		long cw = 0;
		while (cont) {
/*
			int temp = Files.listAllFiles(StaticData.getIntradayFolderStart(),
					1).size();
			temp = temp
					+ Files.listAllFiles(StaticData.getIntradayFolderStart(), 2)
							.size();
			temp = temp
					+ Files.listAllFiles(StaticData.getIntradayFolderStart(), 3)
							.size();
			if (temp > preCount) {
				preCount = temp;
				steadyCount = 0;
			} else if (temp == preCount) {
				steadyCount++;
			}

		*/	boolean killed = Files
					.checkFailedTagFile("StartDownnloadKilled.txt");

			if (cw == 0) {
				Files.createIntradyTagFile("NannyStartDownnload.txt_" + nc);

				HttpDownload httpLoad = new HttpDownload();
				Thread thread1 = new Thread(httpLoad, "Thread 1");
				thread1.start();

				Files.createIntradyTagFile("StartDownnloadNanny.txt_" + nc);

			}

			cw++;

			if (killed) {
				steadyCount = 0;
				nc++;
				Files.createIntradyTagFile("NannyStartDownnload.txt_" + nc);

				HttpDownload httpLoad = new HttpDownload();
				Thread thread1 = new Thread(httpLoad, "Thread 1");
				thread1.start();

				Files.createIntradyTagFile("StartDownnloadNanny.txt_" + nc);

				try {
					// give the process 10 mins to start
					Thread.sleep(600000);
				} catch (Exception ex) {

				}

			} else if (cw>720) {
				cont = false;
				Files.createIntradyTagFile("NannyProcessDone.txt_" + nc);

			}

			try {
				Thread.sleep(60000);
				System.out.println("Waking up & check...");
			} catch (Exception ex) {

			}
		}

	}

}
