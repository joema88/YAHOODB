package com.intraday.ptma.helper;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Date;

public class StaticData {
	private static int intradayFolderStart=92;
	private static int intradayFolderEnd=92;
	private static int subFolderNumbers = 3;
	private static int downloadDays =1;
	//whatever in intraday DB when the program satrts, ie, yesterday max
	//private static long intradayLastIndex=26709+79*0; //+79 each day
	//last index of 3/1/2018, back track 10 weeks to do continuous calculation
	//rename stock ABC to ABC_X of previous records greater than this index
	//need to remove the rename function after this try...
	//private static long intradayLastIndex=22996; //+79 each day
	private static long intradayLastIndex=40505+ 79*49; 
	private static int trueRangeDays = 14;
	private static int fdata = -1000000000;
	// private static String tableName="OptionStocksDaily";
	private static String tableName = "FULLVSTRANK";

	private static String sourceTable = "OptionStocksDailyNew";
	private static String targetTable = "WEALTHOPTION";
	private static int obvSteps = 14;
	private static int obvRSITriggerDays = 20;
	private static int countDuringObvRSITriggerDays = 2;

	// private static int baseIndex = 10000;
	private static int baseIndex = 10026;
	private static Hashtable dateMap = new Hashtable();
	private static Hashtable excludedStocks = null;

	// cwei--sample
	// vst rank IS FIRST SORTED BY RT DESC, THEN RV DESC
	public static Hashtable dateMap(boolean recentDays) {

		if (dateMap == null || dateMap.size() == 0) {

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, 1900);
			cal.set(Calendar.MONTH, 0);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.HOUR_OF_DAY, 19);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			Date past = cal.getTime();

			Date now = new Date();
			int k = 0;
			String dateString = "";
			while (past.before(now)) {
				k++;
				dateString = "";
				int month = cal.get(Calendar.MONTH) + 1;
				if (month < 10) {
					dateString = dateString + "0" + month;
				} else {
					dateString = dateString + month;
				}

				int day = cal.get(Calendar.DAY_OF_MONTH);
				if (day < 10) {
					dateString = dateString + "-0" + day;
				} else {
					dateString = dateString + "-" + day;
				}

				int year = cal.get(Calendar.YEAR);
				dateString = dateString + "-" + year;
                boolean add = true;
				if(recentDays&&year<2017){
					add = false;
				}
				if(add){
				dateMap.put("" + k, dateString);
				dateMap.put(dateString,"" + k);
				}
				System.out.println("Index "+k+" :"+dateString);
				if(k%1000==0){
					System.out.println("Date string "+dateString);
				}
				cal.add(Calendar.DAY_OF_MONTH, 1);
				past = cal.getTime();

			}

			return dateMap;

		}
		/*
		 * dateMap.put("" + (baseIndex + 675), "01-29-2016");
		 * 
		 * dateMap.put("" + (baseIndex + 753), "05-20-2016"); dateMap.put("" +
		 * (baseIndex + 752), "05-19-2016"); dateMap.put("" + (baseIndex + 751),
		 * "05-18-2016"); dateMap.put("" + (baseIndex + 750), "05-17-2016");
		 * dateMap.put("" + (baseIndex + 749), "05-16-2016"); dateMap.put("" +
		 * (baseIndex + 748), "05-13-2016"); dateMap.put("" + (baseIndex + 747),
		 * "05-12-2016"); dateMap.put("" + (baseIndex + 746), "05-11-2016");
		 * dateMap.put("" + (baseIndex + 745), "05-10-2016"); dateMap.put("" +
		 * (baseIndex + 744), "05-09-2016"); dateMap.put("" + (baseIndex + 743),
		 * "05-06-2016"); dateMap.put("" + (baseIndex + 742), "05-05-2016");
		 * dateMap.put("" + (baseIndex + 741), "05-04-2016"); dateMap.put("" +
		 * (baseIndex + 740), "05-03-2016"); dateMap.put("" + (baseIndex + 739),
		 * "05-02-2016"); dateMap.put("" + (baseIndex + 738), "04-29-2016");
		 * dateMap.put("" + (baseIndex + 737), "04-28-2016"); dateMap.put("" +
		 * (baseIndex + 736), "04-27-2016"); dateMap.put("" + (baseIndex + 735),
		 * "04-26-2016"); dateMap.put("" + (baseIndex + 734), "04-25-2016");
		 * dateMap.put("" + (baseIndex + 733), "04-22-2016"); dateMap.put("" +
		 * (baseIndex + 732), "04-21-2016"); dateMap.put("" + (baseIndex + 731),
		 * "04-20-2016"); dateMap.put("" + (baseIndex + 730), "04-19-2016");
		 * dateMap.put("" + (baseIndex + 729), "04-18-2016"); dateMap.put("" +
		 * (baseIndex + 728), "04-15-2016"); dateMap.put("" + (baseIndex + 727),
		 * "04-14-2016"); dateMap.put("" + (baseIndex + 726), "04-13-2016");
		 * dateMap.put("" + (baseIndex + 725), "04-12-2016"); dateMap.put("" +
		 * (baseIndex + 724), "04-11-2016"); dateMap.put("" + (baseIndex + 723),
		 * "04-08-2016"); dateMap.put("" + (baseIndex + 722), "04-07-2016");
		 * dateMap.put("" + (baseIndex + 721), "04-06-2016"); dateMap.put("" +
		 * (baseIndex + 720), "04-05-2016"); dateMap.put("" + (baseIndex + 719),
		 * "04-04-2016"); dateMap.put("" + (baseIndex + 718), "04-01-2016");
		 * dateMap.put("" + (baseIndex + 717), "03-31-2016"); dateMap.put("" +
		 * (baseIndex + 716), "03-30-2016"); dateMap.put("" + (baseIndex + 715),
		 * "03-29-2016"); dateMap.put("" + (baseIndex + 714), "03-28-2016");
		 * dateMap.put("" + (baseIndex + 713), "03-24-2016"); dateMap.put("" +
		 * (baseIndex + 712), "03-23-2016"); dateMap.put("" + (baseIndex + 711),
		 * "03-22-2016"); dateMap.put("" + (baseIndex + 710), "03-21-2016");
		 * dateMap.put("" + (baseIndex + 709), "03-18-2016"); dateMap.put("" +
		 * (baseIndex + 708), "03-17-2016"); dateMap.put("" + (baseIndex + 707),
		 * "03-16-2016"); dateMap.put("" + (baseIndex + 706), "03-15-2016");
		 * dateMap.put("" + (baseIndex + 705), "03-14-2016"); dateMap.put("" +
		 * (baseIndex + 704), "03-11-2016"); dateMap.put("" + (baseIndex + 703),
		 * "03-10-2016"); dateMap.put("" + (baseIndex + 702), "03-09-2016");
		 * dateMap.put("" + (baseIndex + 701), "03-08-2016"); dateMap.put("" +
		 * (baseIndex + 700), "03-07-2016"); dateMap.put("" + (baseIndex + 699),
		 * "03-04-2016"); dateMap.put("" + (baseIndex + 698), "03-03-2016");
		 * dateMap.put("" + (baseIndex + 697), "03-02-2016"); dateMap.put("" +
		 * (baseIndex + 696), "03-01-2016"); dateMap.put("" + (baseIndex + 695),
		 * "02-29-2016"); dateMap.put("" + (baseIndex + 694), "02-26-2016");
		 * dateMap.put("" + (baseIndex + 693), "02-25-2016"); dateMap.put("" +
		 * (baseIndex + 692), "02-24-2016"); dateMap.put("" + (baseIndex + 691),
		 * "02-23-2016"); dateMap.put("" + (baseIndex + 690), "02-22-2016");
		 * dateMap.put("" + (baseIndex + 689), "02-19-2016"); dateMap.put("" +
		 * (baseIndex + 688), "02-18-2016"); dateMap.put("" + (baseIndex + 687),
		 * "02-17-2016"); dateMap.put("" + (baseIndex + 686), "02-16-2016");
		 * dateMap.put("" + (baseIndex + 685), "02-12-2016"); dateMap.put("" +
		 * (baseIndex + 684), "02-11-2016"); dateMap.put("" + (baseIndex + 683),
		 * "02-10-2016"); dateMap.put("" + (baseIndex + 682), "02-09-2016");
		 * dateMap.put("" + (baseIndex + 681), "02-08-2016"); dateMap.put("" +
		 * (baseIndex + 680), "02-05-2016"); dateMap.put("" + (baseIndex + 679),
		 * "02-04-2016"); dateMap.put("" + (baseIndex + 678), "02-03-2016");
		 * dateMap.put("" + (baseIndex + 677), "02-02-2016"); dateMap.put("" +
		 * (baseIndex + 676), "02-01-2016"); dateMap.put("" + (baseIndex + 675),
		 * "01-29-2016"); dateMap.put("" + (baseIndex + 674), "01-28-2016");
		 * dateMap.put("" + (baseIndex + 673), "01-27-2016"); dateMap.put("" +
		 * (baseIndex + 672), "01-26-2016"); dateMap.put("" + (baseIndex + 671),
		 * "01-25-2016"); dateMap.put("" + (baseIndex + 670), "01-22-2016");
		 * dateMap.put("" + (baseIndex + 669), "01-21-2016"); dateMap.put("" +
		 * (baseIndex + 668), "01-20-2016"); dateMap.put("" + (baseIndex + 667),
		 * "01-19-2016"); dateMap.put("" + (baseIndex + 666), "01-18-2016");
		 * dateMap.put("" + (baseIndex + 665), "01-15-2016"); dateMap.put("" +
		 * (baseIndex + 664), "01-14-2016"); dateMap.put("" + (baseIndex + 663),
		 * "01-13-2016"); dateMap.put("" + (baseIndex + 662), "01-12-2016");
		 * dateMap.put("" + (baseIndex + 661), "01-11-2016"); dateMap.put("" +
		 * (baseIndex + 660), "01-08-2016"); dateMap.put("" + (baseIndex + 659),
		 * "01-07-2016"); dateMap.put("" + (baseIndex + 658), "01-06-2016");
		 * dateMap.put("" + (baseIndex + 657), "01-05-2016"); dateMap.put("" +
		 * (baseIndex + 656), "01-04-2016"); dateMap.put("" + (baseIndex + 655),
		 * "12-31-2015"); dateMap.put("" + (baseIndex + 654), "12-30-2015");
		 * dateMap.put("" + (baseIndex + 653), "12-29-2015"); dateMap.put("" +
		 * (baseIndex + 652), "12-28-2015"); dateMap.put("" + (baseIndex + 651),
		 * "12-24-2015"); dateMap.put("" + (baseIndex + 650), "12-23-2015");
		 * dateMap.put("" + (baseIndex + 649), "12-22-2015"); dateMap.put("" +
		 * (baseIndex + 648), "12-21-2015"); dateMap.put("" + (baseIndex + 647),
		 * "12-18-2015"); dateMap.put("" + (baseIndex + 646), "12-17-2015");
		 * dateMap.put("" + (baseIndex + 645), "12-16-2015"); dateMap.put("" +
		 * (baseIndex + 644), "12-15-2015"); dateMap.put("" + (baseIndex + 643),
		 * "12-14-2015"); dateMap.put("" + (baseIndex + 642), "12-11-2015");
		 * dateMap.put("" + (baseIndex + 641), "12-10-2015"); dateMap.put("" +
		 * (baseIndex + 640), "12-09-2015"); dateMap.put("" + (baseIndex + 639),
		 * "12-08-2015"); dateMap.put("" + (baseIndex + 638), "12-07-2015");
		 * dateMap.put("" + (baseIndex + 637), "12-04-2015"); dateMap.put("" +
		 * (baseIndex + 636), "12-03-2015"); dateMap.put("" + (baseIndex + 635),
		 * "12-02-2015"); dateMap.put("" + (baseIndex + 634), "12-01-2015");
		 * dateMap.put("" + (baseIndex + 633), "11-30-2015"); dateMap.put("" +
		 * (baseIndex + 632), "11-27-2015");; dateMap.put("" + (baseIndex +
		 * 631), "11-25-2015"); dateMap.put("" + (baseIndex + 630),
		 * "11-24-2015"); dateMap.put("" + (baseIndex + 629), "11-23-2015");
		 * dateMap.put("" + (baseIndex + 628), "11-20-2015"); dateMap.put("" +
		 * (baseIndex + 627), "11-19-2015"); dateMap.put("" + (baseIndex + 626),
		 * "11-18-2015"); dateMap.put("" + (baseIndex + 625), "11-17-2015");
		 * dateMap.put("" + (baseIndex + 624), "11-16-2015"); dateMap.put("" +
		 * (baseIndex + 623), "11-13-2015"); dateMap.put("" + (baseIndex + 622),
		 * "11-12-2015"); dateMap.put("" + (baseIndex + 621), "11-11-2015");
		 * dateMap.put("" + (baseIndex + 620), "11-10-2015"); dateMap.put("" +
		 * (baseIndex + 619), "11-09-2015"); dateMap.put("" + (baseIndex + 618),
		 * "11-06-2015"); dateMap.put("" + (baseIndex + 617), "11-05-2015");
		 * dateMap.put("" + (baseIndex + 616), "11-04-2015"); dateMap.put("" +
		 * (baseIndex + 615), "11-03-2015"); dateMap.put("" + (baseIndex + 614),
		 * "11-02-2015"); dateMap.put("" + (baseIndex + 613), "10-30-2015");
		 * dateMap.put("" + (baseIndex + 612), "10-29-2015"); dateMap.put("" +
		 * (baseIndex + 611), "10-28-2015"); dateMap.put("" + (baseIndex + 610),
		 * "10-27-2015"); dateMap.put("" + (baseIndex + 609), "10-26-2015");
		 * dateMap.put("" + (baseIndex + 608), "10-23-2015"); dateMap.put("" +
		 * (baseIndex + 607), "10-22-2015"); dateMap.put("" + (baseIndex + 606),
		 * "10-21-2015"); dateMap.put("" + (baseIndex + 605), "10-20-2015");
		 * dateMap.put("" + (baseIndex + 604), "10-19-2015"); dateMap.put("" +
		 * (baseIndex + 603), "10-16-2015"); dateMap.put("" + (baseIndex + 602),
		 * "10-15-2015"); dateMap.put("" + (baseIndex + 601), "10-14-2015");
		 * dateMap.put("" + (baseIndex + 600), "10-13-2015"); dateMap.put("" +
		 * (baseIndex + 599), "10-12-2015"); dateMap.put("" + (baseIndex + 598),
		 * "10-09-2015"); dateMap.put("" + (baseIndex + 597), "10-08-2015");
		 * dateMap.put("" + (baseIndex + 596), "10-07-2015"); dateMap.put("" +
		 * (baseIndex + 595), "10-06-2015"); dateMap.put("" + (baseIndex + 594),
		 * "10-05-2015"); dateMap.put("" + (baseIndex + 593), "10-02-2015");
		 * dateMap.put("" + (baseIndex + 592), "10-01-2015"); dateMap.put("" +
		 * (baseIndex + 591), "09-30-2015"); dateMap.put("" + (baseIndex + 590),
		 * "09-29-2015"); dateMap.put("" + (baseIndex + 589), "09-28-2015");
		 * dateMap.put("" + (baseIndex + 588), "09-25-2015"); dateMap.put("" +
		 * (baseIndex + 587), "09-24-2015"); dateMap.put("" + (baseIndex + 586),
		 * "09-23-2015"); dateMap.put("" + (baseIndex + 585), "09-22-2015");
		 * dateMap.put("" + (baseIndex + 584), "09-21-2015"); dateMap.put("" +
		 * (baseIndex + 583), "09-18-2015"); dateMap.put("" + (baseIndex + 582),
		 * "09-17-2015"); dateMap.put("" + (baseIndex + 581), "09-16-2015");
		 * dateMap.put("" + (baseIndex + 580), "09-15-2015"); dateMap.put("" +
		 * (baseIndex + 579), "09-14-2015"); dateMap.put("" + (baseIndex + 578),
		 * "09-11-2015"); dateMap.put("" + (baseIndex + 577), "09-10-2015");
		 * dateMap.put("" + (baseIndex + 576), "09-09-2015"); dateMap.put("" +
		 * (baseIndex + 575), "09-08-2015"); dateMap.put("" + (baseIndex + 574),
		 * "09-04-2015"); dateMap.put("" + (baseIndex + 573), "09-03-2015");
		 * dateMap.put("" + (baseIndex + 572), "09-02-2015"); dateMap.put("" +
		 * (baseIndex + 571), "09-01-2015"); dateMap.put("" + (baseIndex + 570),
		 * "08-31-2015"); dateMap.put("" + (baseIndex + 569), "08-28-2015");
		 * dateMap.put("" + (baseIndex + 568), "08-27-2015"); dateMap.put("" +
		 * (baseIndex + 567), "08-26-2015"); dateMap.put("" + (baseIndex + 566),
		 * "08-25-2015"); dateMap.put("" + (baseIndex + 565), "08-24-2015");
		 * dateMap.put("" + (baseIndex + 564), "08-21-2015"); dateMap.put("" +
		 * (baseIndex + 563), "08-20-2015"); dateMap.put("" + (baseIndex + 562),
		 * "08-19-2015"); dateMap.put("" + (baseIndex + 561), "08-18-2015");
		 * dateMap.put("" + (baseIndex + 560), "08-17-2015"); dateMap.put("" +
		 * (baseIndex + 559), "08-14-2015"); dateMap.put("" + (baseIndex + 558),
		 * "08-13-2015"); dateMap.put("" + (baseIndex + 557), "08-12-2015");
		 * dateMap.put("" + (baseIndex + 556), "08-11-2015"); dateMap.put("" +
		 * (baseIndex + 555), "08-10-2015"); dateMap.put("" + (baseIndex + 554),
		 * "08-07-2015"); dateMap.put("" + (baseIndex + 553), "08-06-2015");
		 * dateMap.put("" + (baseIndex + 552), "08-05-2015"); dateMap.put("" +
		 * (baseIndex + 551), "08-04-2015"); dateMap.put("" + (baseIndex + 550),
		 * "08-03-2015"); dateMap.put("" + (baseIndex + 549), "07-31-2015");
		 * dateMap.put("" + (baseIndex + 548), "07-30-2015"); dateMap.put("" +
		 * (baseIndex + 547), "07-29-2015"); dateMap.put("" + (baseIndex + 546),
		 * "07-28-2015"); dateMap.put("" + (baseIndex + 545), "07-27-2015");
		 * dateMap.put("" + (baseIndex + 544), "07-24-2015"); dateMap.put("" +
		 * (baseIndex + 543), "07-23-2015"); dateMap.put("" + (baseIndex + 542),
		 * "07-22-2015"); dateMap.put("" + (baseIndex + 541), "07-21-2015");
		 * dateMap.put("" + (baseIndex + 540), "07-20-2015"); dateMap.put("" +
		 * (baseIndex + 539), "07-17-2015"); dateMap.put("" + (baseIndex + 538),
		 * "07-16-2015"); dateMap.put("" + (baseIndex + 537), "07-15-2015");
		 * dateMap.put("" + (baseIndex + 536), "07-14-2015"); dateMap.put("" +
		 * (baseIndex + 535), "07-13-2015"); dateMap.put("" + (baseIndex + 534),
		 * "07-10-2015"); dateMap.put("" + (baseIndex + 533), "07-09-2015");
		 * dateMap.put("" + (baseIndex + 532), "07-08-2015"); dateMap.put("" +
		 * (baseIndex + 531), "07-07-2015"); dateMap.put("" + (baseIndex + 530),
		 * "07-06-2015");
		 * 
		 * dateMap.put("" + (baseIndex + 529), "07-02-2015"); dateMap.put("" +
		 * (baseIndex + 528), "07-01-2015"); dateMap.put("" + (baseIndex + 527),
		 * "06-30-2015"); dateMap.put("" + (baseIndex + 526), "06-29-2015");
		 * 
		 * dateMap.put("" + (baseIndex + 525), "06-26-2015"); dateMap.put("" +
		 * (baseIndex + 524), "06-25-2015"); dateMap.put("" + (baseIndex + 523),
		 * "06-24-2015"); dateMap.put("" + (baseIndex + 522), "06-23-2015");
		 * dateMap.put("" + (baseIndex + 521), "06-22-2015");
		 * 
		 * dateMap.put("" + (baseIndex + 520), "06-19-2015"); dateMap.put("" +
		 * (baseIndex + 519), "06-18-2015"); dateMap.put("" + (baseIndex + 518),
		 * "06-17-2015"); dateMap.put("" + (baseIndex + 517), "06-16-2015");
		 * dateMap.put("" + (baseIndex + 516), "06-15-2015");
		 * 
		 * dateMap.put("" + (baseIndex + 515), "06-12-2015"); dateMap.put("" +
		 * (baseIndex + 514), "06-11-2015"); dateMap.put("" + (baseIndex + 513),
		 * "06-10-2015"); dateMap.put("" + (baseIndex + 512), "06-09-2015");
		 * dateMap.put("" + (baseIndex + 511), "06-08-2015");
		 * 
		 * dateMap.put("" + (baseIndex + 510), "06-05-2015"); dateMap.put("" +
		 * (baseIndex + 509), "06-04-2015"); dateMap.put("" + (baseIndex + 508),
		 * "06-03-2015"); dateMap.put("" + (baseIndex + 507), "06-02-2015");
		 * dateMap.put("" + (baseIndex + 506), "06-01-2015");
		 * 
		 * dateMap.put("" + (baseIndex + 505), "05-29-2015"); dateMap.put("" +
		 * (baseIndex + 504), "05-28-2015"); dateMap.put("" + (baseIndex + 503),
		 * "05-27-2015"); dateMap.put("" + (baseIndex + 502), "05-26-2015");
		 * 
		 * dateMap.put("" + (baseIndex + 501), "05-22-2015"); dateMap.put("" +
		 * (baseIndex + 500), "05-21-2015"); dateMap.put("" + (baseIndex + 499),
		 * "05-20-2015"); dateMap.put("" + (baseIndex + 498), "05-19-2015");
		 * dateMap.put("" + (baseIndex + 497), "05-18-2015");
		 * 
		 * dateMap.put("" + (baseIndex + 496), "05-15-2015"); dateMap.put("" +
		 * (baseIndex + 495), "05-14-2015"); dateMap.put("" + (baseIndex + 494),
		 * "05-13-2015"); dateMap.put("" + (baseIndex + 493), "05-12-2015");
		 * dateMap.put("" + (baseIndex + 492), "05-11-2015");
		 * 
		 * dateMap.put("" + (baseIndex + 491), "05-08-2015"); dateMap.put("" +
		 * (baseIndex + 490), "05-07-2015"); dateMap.put("" + (baseIndex + 489),
		 * "05-06-2015"); dateMap.put("" + (baseIndex + 488), "05-05-2015");
		 * dateMap.put("" + (baseIndex + 487), "05-04-2015");
		 * 
		 * dateMap.put("" + (baseIndex + 486), "05-01-2015"); dateMap.put("" +
		 * (baseIndex + 485), "04-30-2015"); dateMap.put("" + (baseIndex + 484),
		 * "04-29-2015"); dateMap.put("" + (baseIndex + 483), "04-28-2015");
		 * dateMap.put("" + (baseIndex + 482), "04-27-2015");
		 * 
		 * 
		 * dateMap.put("" + (baseIndex + 481), "04-24-2015"); dateMap.put("" +
		 * (baseIndex + 480), "04-23-2015"); dateMap.put("" + (baseIndex + 479),
		 * "04-22-2015"); dateMap.put("" + (baseIndex + 478), "04-21-2015");
		 * dateMap.put("" + (baseIndex + 477), "04-20-2015");
		 * 
		 * dateMap.put("" + (baseIndex + 476), "04-17-2015"); dateMap.put("" +
		 * (baseIndex + 475), "04-16-2015"); dateMap.put("" + (baseIndex + 474),
		 * "04-15-2015"); dateMap.put("" + (baseIndex + 473), "04-14-2015");
		 * dateMap.put("" + (baseIndex + 472), "04-13-2015");
		 * 
		 * dateMap.put("" + (baseIndex + 471), "04-10-2015"); dateMap.put("" +
		 * (baseIndex + 470), "04-09-2015"); dateMap.put("" + (baseIndex + 469),
		 * "04-08-2015"); dateMap.put("" + (baseIndex + 468), "04-07-2015");
		 * dateMap.put("" + (baseIndex + 467), "04-06-2015"); dateMap.put("" +
		 * (baseIndex + 466), "04-02-2015"); dateMap.put("" + (baseIndex + 465),
		 * "04-01-2015"); dateMap.put("" + (baseIndex + 464), "03-31-2015");
		 * dateMap.put("" + (baseIndex + 463), "03-30-2015");
		 * 
		 * dateMap.put("" + (baseIndex + 462), "03-27-2015"); dateMap.put("" +
		 * (baseIndex + 461), "03-26-2015"); dateMap.put("" + (baseIndex + 460),
		 * "03-25-2015"); dateMap.put("" + (baseIndex + 459), "03-24-2015");
		 * dateMap.put("" + (baseIndex + 458), "03-23-2015");
		 * 
		 * dateMap.put("" + (baseIndex + 457), "03-20-2015"); dateMap.put("" +
		 * (baseIndex + 456), "03-19-2015"); dateMap.put("" + (baseIndex + 455),
		 * "03-18-2015"); dateMap.put("" + (baseIndex + 454), "03-17-2015");
		 * dateMap.put("" + (baseIndex + 453), "03-16-2015");
		 * 
		 * dateMap.put("" + (baseIndex + 452), "03-13-2015"); dateMap.put("" +
		 * (baseIndex + 451), "03-12-2015"); dateMap.put("" + (baseIndex + 450),
		 * "03-11-2015"); dateMap.put("" + (baseIndex + 449), "03-10-2015");
		 * dateMap.put("" + (baseIndex + 448), "03-09-2015");
		 * 
		 * dateMap.put("" + (baseIndex + 447), "03-06-2015"); dateMap.put("" +
		 * (baseIndex + 446), "03-05-2015"); dateMap.put("" + (baseIndex + 445),
		 * "03-04-2015"); dateMap.put("" + (baseIndex + 444), "03-03-2015");
		 * dateMap.put("" + (baseIndex + 443), "03-02-2015");
		 * 
		 * dateMap.put("" + (baseIndex + 442), "02-27-2015"); dateMap.put("" +
		 * (baseIndex + 441), "02-26-2015"); dateMap.put("" + (baseIndex + 440),
		 * "02-25-2015"); dateMap.put("" + (baseIndex + 439), "02-24-2015");
		 * dateMap.put("" + (baseIndex + 438), "02-23-2015");
		 * 
		 * dateMap.put("" + (baseIndex + 437), "02-20-2015"); dateMap.put("" +
		 * (baseIndex + 436), "02-19-2015"); dateMap.put("" + (baseIndex + 435),
		 * "02-18-2015"); dateMap.put("" + (baseIndex + 434), "02-17-2015");
		 * 
		 * dateMap.put("" + (baseIndex + 433), "02-13-2015"); dateMap.put("" +
		 * (baseIndex + 432), "02-12-2015"); dateMap.put("" + (baseIndex + 431),
		 * "02-11-2015"); dateMap.put("" + (baseIndex + 430), "02-10-2015");
		 * dateMap.put("" + (baseIndex + 429), "02-09-2015");
		 * 
		 * dateMap.put("" + (baseIndex + 428), "02-06-2015"); dateMap.put("" +
		 * (baseIndex + 427), "02-05-2015"); dateMap.put("" + (baseIndex + 426),
		 * "02-04-2015"); dateMap.put("" + (baseIndex + 425), "02-03-2015");
		 * dateMap.put("" + (baseIndex + 424), "02-02-2015"); dateMap.put("" +
		 * (baseIndex + 423), "01-30-2015"); dateMap.put("" + (baseIndex + 422),
		 * "01-29-2015"); dateMap.put("" + (baseIndex + 421), "01-28-2015");
		 * dateMap.put("" + (baseIndex + 420), "01-27-2015"); dateMap.put("" +
		 * (baseIndex + 419), "01-26-2015");
		 * 
		 * dateMap.put("" + (baseIndex + 418), "01-23-2015"); dateMap.put("" +
		 * (baseIndex + 417), "01-22-2015"); dateMap.put("" + (baseIndex + 416),
		 * "01-21-2015"); dateMap.put("" + (baseIndex + 415), "01-20-2015");
		 * dateMap.put("" + (baseIndex + 414), "01-16-2015"); dateMap.put("" +
		 * (baseIndex + 413), "01-15-2015"); dateMap.put("" + (baseIndex + 412),
		 * "01-14-2015"); dateMap.put("" + (baseIndex + 411), "01-13-2015");
		 * dateMap.put("" + (baseIndex + 410), "01-12-2015"); dateMap.put("" +
		 * (baseIndex + 409), "01-09-2015"); dateMap.put("" + (baseIndex + 408),
		 * "01-08-2015"); dateMap.put("" + (baseIndex + 407), "01-07-2015");
		 * dateMap.put("" + (baseIndex + 406), "01-06-2015"); dateMap.put("" +
		 * (baseIndex + 405), "01-05-2015"); dateMap.put("" + (baseIndex + 404),
		 * "01-02-2015"); dateMap.put("" + (baseIndex + 403), "12-31-2014");
		 * dateMap.put("" + (baseIndex + 402), "12-30-2014"); dateMap.put("" +
		 * (baseIndex + 401), "12-29-2014"); dateMap.put("" + (baseIndex + 400),
		 * "12-26-2014"); dateMap.put("" + (baseIndex + 399), "12-24-2014");
		 * dateMap.put("" + (baseIndex + 398), "12-23-2014"); dateMap.put("" +
		 * (baseIndex + 397), "12-22-2014"); dateMap.put("" + (baseIndex + 396),
		 * "12-19-2014"); dateMap.put("" + (baseIndex + 395), "12-18-2014");
		 * dateMap.put("" + (baseIndex + 394), "12-17-2014"); dateMap.put("" +
		 * (baseIndex + 393), "12-16-2014"); dateMap.put("" + (baseIndex + 392),
		 * "12-15-2014"); dateMap.put("" + (baseIndex + 391), "12-12-2014");
		 * dateMap.put("" + (baseIndex + 390), "12-11-2014"); dateMap.put("" +
		 * (baseIndex + 389), "12-10-2014"); dateMap.put("" + (baseIndex + 388),
		 * "12-09-2014"); dateMap.put("" + (baseIndex + 387), "12-08-2014");
		 * dateMap.put("" + (baseIndex + 386), "12-05-2014"); dateMap.put("" +
		 * (baseIndex + 385), "12-04-2014"); dateMap.put("" + (baseIndex + 384),
		 * "12-03-2014"); dateMap.put("" + (baseIndex + 383), "12-02-2014");
		 * dateMap.put("" + (baseIndex + 382), "12-01-2014"); dateMap.put("" +
		 * (baseIndex + 381), "11-28-2014"); dateMap.put("" + (baseIndex + 380),
		 * "11-26-2014"); dateMap.put("" + (baseIndex + 379), "11-25-2014");
		 * dateMap.put("" + (baseIndex + 378), "11-24-2014"); dateMap.put("" +
		 * (baseIndex + 377), "11-21-2014"); dateMap.put("" + (baseIndex + 376),
		 * "11-20-2014"); dateMap.put("" + (baseIndex + 375), "11-19-2014");
		 * dateMap.put("" + (baseIndex + 374), "11-18-2014"); dateMap.put("" +
		 * (baseIndex + 373), "11-17-2014"); dateMap.put("" + (baseIndex + 372),
		 * "11-14-2014"); dateMap.put("" + (baseIndex + 371), "11-13-2014");
		 * dateMap.put("" + (baseIndex + 370), "11-12-2014"); dateMap.put("" +
		 * (baseIndex + 369), "11-11-2014"); dateMap.put("" + (baseIndex + 368),
		 * "11-10-2014"); dateMap.put("" + (baseIndex + 367), "11-07-2014");
		 * dateMap.put("" + (baseIndex + 366), "11-06-2014"); dateMap.put("" +
		 * (baseIndex + 365), "11-05-2014"); dateMap.put("" + (baseIndex + 364),
		 * "11-04-2014"); dateMap.put("" + (baseIndex + 363), "11-03-2014");
		 * dateMap.put("" + (baseIndex + 362), "10-31-2014"); dateMap.put("" +
		 * (baseIndex + 361), "10-30-2014"); dateMap.put("" + (baseIndex + 360),
		 * "10-29-2014"); dateMap.put("" + (baseIndex + 359), "10-28-2014");
		 * dateMap.put("" + (baseIndex + 358), "10-27-2014"); dateMap.put("" +
		 * (baseIndex + 357), "10-24-2014"); dateMap.put("" + (baseIndex + 356),
		 * "10-23-2014"); dateMap.put("" + (baseIndex + 355), "10-22-2014");
		 * dateMap.put("" + (baseIndex + 354), "10-21-2014"); dateMap.put("" +
		 * (baseIndex + 353), "10-20-2014"); dateMap.put("" + (baseIndex + 352),
		 * "10-17-2014"); dateMap.put("" + (baseIndex + 351), "10-16-2014");
		 * dateMap.put("" + (baseIndex + 350), "10-15-2014"); dateMap.put("" +
		 * (baseIndex + 349), "10-14-2014"); dateMap.put("" + (baseIndex + 348),
		 * "10-13-2014"); dateMap.put("" + (baseIndex + 347), "10-10-2014");
		 * dateMap.put("" + (baseIndex + 346), "10-09-2014"); dateMap.put("" +
		 * (baseIndex + 345), "10-08-2014"); dateMap.put("" + (baseIndex + 344),
		 * "10-07-2014"); dateMap.put("" + (baseIndex + 343), "10-06-2014");
		 * dateMap.put("" + (baseIndex + 342), "10-03-2014"); dateMap.put("" +
		 * (baseIndex + 341), "10-02-2014"); dateMap.put("" + (baseIndex + 340),
		 * "10-01-2014"); dateMap.put("" + (baseIndex + 339), "09-30-2014");
		 * dateMap.put("" + (baseIndex + 338), "09-29-2014"); dateMap.put("" +
		 * (baseIndex + 337), "09-26-2014"); dateMap.put("" + (baseIndex + 336),
		 * "09-25-2014"); dateMap.put("" + (baseIndex + 335), "09-24-2014");
		 * dateMap.put("" + (baseIndex + 334), "09-23-2014"); dateMap.put("" +
		 * (baseIndex + 333), "09-22-2014"); dateMap.put("" + (baseIndex + 332),
		 * "09-19-2014"); dateMap.put("" + (baseIndex + 331), "09-18-2014");
		 * dateMap.put("" + (baseIndex + 330), "09-17-2014"); dateMap.put("" +
		 * (baseIndex + 329), "09-16-2014"); dateMap.put("" + (baseIndex + 328),
		 * "09-15-2014"); dateMap.put("" + (baseIndex + 327), "09-12-2014");
		 * dateMap.put("" + (baseIndex + 326), "09-11-2014"); dateMap.put("" +
		 * (baseIndex + 325), "09-10-2014"); dateMap.put("" + (baseIndex + 324),
		 * "09-09-2014"); dateMap.put("" + (baseIndex + 323), "09-08-2014");
		 * dateMap.put("" + (baseIndex + 322), "09-05-2014"); dateMap.put("" +
		 * (baseIndex + 321), "09-04-2014"); dateMap.put("" + (baseIndex + 320),
		 * "09-03-2014"); dateMap.put("" + (baseIndex + 319), "09-02-2014");
		 * dateMap.put("" + (baseIndex + 318), "08-29-2014"); dateMap.put("" +
		 * (baseIndex + 317), "08-28-2014"); dateMap.put("" + (baseIndex + 316),
		 * "08-27-2014"); dateMap.put("" + (baseIndex + 315), "08-26-2014");
		 * dateMap.put("" + (baseIndex + 314), "08-25-2014"); dateMap.put("" +
		 * (baseIndex + 313), "08-22-2014"); dateMap.put("" + (baseIndex + 312),
		 * "08-21-2014"); dateMap.put("" + (baseIndex + 311), "08-20-2014");
		 * dateMap.put("" + (baseIndex + 310), "08-19-2014"); dateMap.put("" +
		 * (baseIndex + 309), "08-18-2014"); dateMap.put("" + (baseIndex + 308),
		 * "08-15-2014"); dateMap.put("" + (baseIndex + 307), "08-14-2014");
		 * dateMap.put("" + (baseIndex + 306), "08-13-2014"); dateMap.put("" +
		 * (baseIndex + 305), "08-12-2014"); dateMap.put("" + (baseIndex + 304),
		 * "08-11-2014"); dateMap.put("" + (baseIndex + 303), "08-08-2014");
		 * dateMap.put("" + (baseIndex + 302), "08-07-2014"); dateMap.put("" +
		 * (baseIndex + 301), "08-06-2014"); dateMap.put("" + (baseIndex + 300),
		 * "08-05-2014"); dateMap.put("" + (baseIndex + 299), "08-04-2014");
		 * dateMap.put("" + (baseIndex + 298), "08-01-2014"); dateMap.put("" +
		 * (baseIndex + 297), "07-31-2014"); dateMap.put("" + (baseIndex + 296),
		 * "07-30-2014"); dateMap.put("" + (baseIndex + 295), "07-29-2014");
		 * dateMap.put("" + (baseIndex + 294), "07-28-2014"); dateMap.put("" +
		 * (baseIndex + 293), "07-25-2014"); dateMap.put("" + (baseIndex + 292),
		 * "07-24-2014"); dateMap.put("" + (baseIndex + 291), "07-23-2014");
		 * dateMap.put("" + (baseIndex + 290), "07-22-2014"); dateMap.put("" +
		 * (baseIndex + 289), "07-21-2014"); dateMap.put("" + (baseIndex + 288),
		 * "07-18-2014"); dateMap.put("" + (baseIndex + 287), "07-17-2014");
		 * dateMap.put("" + (baseIndex + 286), "07-16-2014"); dateMap.put("" +
		 * (baseIndex + 285), "07-15-2014"); dateMap.put("" + (baseIndex + 284),
		 * "07-14-2014"); dateMap.put("" + (baseIndex + 283), "07-11-2014");
		 * dateMap.put("" + (baseIndex + 282), "07-10-2014"); dateMap.put("" +
		 * (baseIndex + 281), "07-09-2014"); dateMap.put("" + (baseIndex + 280),
		 * "07-08-2014"); dateMap.put("" + (baseIndex + 279), "07-07-2014");
		 * dateMap.put("" + (baseIndex + 278), "07-03-2014"); dateMap.put("" +
		 * (baseIndex + 277), "07-02-2014"); dateMap.put("" + (baseIndex + 276),
		 * "07-01-2014"); dateMap.put("" + (baseIndex + 275), "06-30-2014");
		 * dateMap.put("" + (baseIndex + 274), "06-27-2014"); dateMap.put("" +
		 * (baseIndex + 273), "06-26-2014"); dateMap.put("" + (baseIndex + 272),
		 * "06-25-2014"); dateMap.put("" + (baseIndex + 271), "06-24-2014");
		 * dateMap.put("" + (baseIndex + 270), "06-23-2014"); dateMap.put("" +
		 * (baseIndex + 269), "06-20-2014"); dateMap.put("" + (baseIndex + 268),
		 * "06-19-2014"); dateMap.put("" + (baseIndex + 267), "06-18-2014");
		 * dateMap.put("" + (baseIndex + 266), "06-17-2014"); dateMap.put("" +
		 * (baseIndex + 265), "06-16-2014"); dateMap.put("" + (baseIndex + 264),
		 * "06-13-2014"); dateMap.put("" + (baseIndex + 263), "06-12-2014");
		 * dateMap.put("" + (baseIndex + 262), "06-11-2014"); dateMap.put("" +
		 * (baseIndex + 261), "06-10-2014"); dateMap.put("" + (baseIndex + 260),
		 * "06-09-2014"); dateMap.put("" + (baseIndex + 259), "06-06-2014");
		 * dateMap.put("" + (baseIndex + 258), "06-05-2014"); dateMap.put("" +
		 * (baseIndex + 257), "06-04-2014"); dateMap.put("" + (baseIndex + 256),
		 * "06-03-2014"); dateMap.put("" + (baseIndex + 255), "06-02-2014");
		 * dateMap.put("" + (baseIndex + 254), "05-30-2014"); dateMap.put("" +
		 * (baseIndex + 253), "05-29-2014"); dateMap.put("" + (baseIndex + 252),
		 * "05-28-2014"); dateMap.put("" + (baseIndex + 251), "05-27-2014");
		 * dateMap.put("" + (baseIndex + 250), "05-23-2014"); dateMap.put("" +
		 * (baseIndex + 249), "05-22-2014"); dateMap.put("" + (baseIndex + 248),
		 * "05-21-2014"); dateMap.put("" + (baseIndex + 247), "05-20-2014");
		 * dateMap.put("" + (baseIndex + 246), "05-19-2014"); dateMap.put("" +
		 * (baseIndex + 245), "05-16-2014"); dateMap.put("" + (baseIndex + 244),
		 * "05-15-2014"); dateMap.put("" + (baseIndex + 243), "05-14-2014");
		 * dateMap.put("" + (baseIndex + 242), "05-13-2014"); dateMap.put("" +
		 * (baseIndex + 241), "05-12-2014"); dateMap.put("" + (baseIndex + 240),
		 * "05-09-2014");
		 * 
		 * dateMap.put("" + (baseIndex + 239), "05-08-2014"); dateMap.put("" +
		 * (baseIndex + 238), "05-07-2014"); dateMap.put("" + (baseIndex + 237),
		 * "05-06-2014"); dateMap.put("" + (baseIndex + 236), "05-05-2014");
		 * dateMap.put("" + (baseIndex + 235), "05-02-2014"); dateMap.put("" +
		 * (baseIndex + 234), "05-01-2014"); dateMap.put("" + (baseIndex + 233),
		 * "04-30-2014"); dateMap.put("" + (baseIndex + 232), "04-29-2014");
		 * 
		 * dateMap.put("" + (baseIndex + 231), "04-28-2014"); dateMap.put("" +
		 * (baseIndex + 230), "04-25-2014"); dateMap.put("" + (baseIndex + 229),
		 * "04-24-2014"); dateMap.put("" + (baseIndex + 228), "04-23-2014");
		 * dateMap.put("" + (baseIndex + 227), "04-22-2014"); dateMap.put("" +
		 * (baseIndex + 226), "04-21-2014"); dateMap.put("" + (baseIndex + 225),
		 * "04-17-2014"); dateMap.put("" + (baseIndex + 224), "04-16-2014");
		 * dateMap.put("" + (baseIndex + 223), "04-15-2014"); dateMap.put("" +
		 * (baseIndex + 222), "04-14-2014"); dateMap.put("" + (baseIndex + 221),
		 * "04-11-2014"); dateMap.put("" + (baseIndex + 220), "04-10-2014");
		 * dateMap.put("" + (baseIndex + 219), "04-09-2014"); dateMap.put("" +
		 * (baseIndex + 218), "04-08-2014"); dateMap.put("" + (baseIndex + 217),
		 * "04-07-2014"); dateMap.put("" + (baseIndex + 216), "04-04-2014");
		 * dateMap.put("" + (baseIndex + 215), "04-03-2014"); dateMap.put("" +
		 * (baseIndex + 214), "04-02-2014"); dateMap.put("" + (baseIndex + 213),
		 * "04-01-2014"); dateMap.put("" + (baseIndex + 212), "03-31-2014");
		 * dateMap.put("" + (baseIndex + 211), "03-28-2014"); dateMap.put("" +
		 * (baseIndex + 210), "03-27-2014"); dateMap.put("" + (baseIndex + 209),
		 * "03-26-2014"); dateMap.put("" + (baseIndex + 208), "03-25-2014");
		 * dateMap.put("" + (baseIndex + 207), "03-24-2014"); dateMap.put("" +
		 * (baseIndex + 206), "03-21-2014"); dateMap.put("" + (baseIndex + 205),
		 * "03-20-2014"); dateMap.put("" + (baseIndex + 204), "03-19-2014");
		 * dateMap.put("" + (baseIndex + 203), "03-18-2014"); dateMap.put("" +
		 * (baseIndex + 202), "03-17-2014"); dateMap.put("" + (baseIndex + 201),
		 * "03-14-2014"); dateMap.put("" + (baseIndex + 200), "03-13-2014");
		 * dateMap.put("" + (baseIndex + 199), "03-12-2014"); dateMap.put("" +
		 * (baseIndex + 198), "03-11-2014"); dateMap.put("" + (baseIndex + 197),
		 * "03-10-2014"); dateMap.put("" + (baseIndex + 196), "03-07-2014");
		 * dateMap.put("" + (baseIndex + 195), "03-06-2014"); dateMap.put("" +
		 * (baseIndex + 194), "03-05-2014"); dateMap.put("" + (baseIndex + 193),
		 * "03-04-2014"); dateMap.put("" + (baseIndex + 192), "03-03-2014");
		 * dateMap.put("" + (baseIndex + 191), "02-28-2014"); dateMap.put("" +
		 * (baseIndex + 190), "02-27-2014"); dateMap.put("" + (baseIndex + 189),
		 * "02-26-2014"); dateMap.put("" + (baseIndex + 188), "02-25-2014");
		 * dateMap.put("" + (baseIndex + 187), "02-24-2014"); dateMap.put("" +
		 * (baseIndex + 186), "02-21-2014"); dateMap.put("" + (baseIndex + 185),
		 * "02-20-2014"); dateMap.put("" + (baseIndex + 184), "02-19-2014");
		 * dateMap.put("" + (baseIndex + 183), "02-18-2014"); dateMap.put("" +
		 * (baseIndex + 182), "02-14-2014"); dateMap.put("" + (baseIndex + 181),
		 * "02-13-2014"); dateMap.put("" + (baseIndex + 180), "02-12-2014");
		 * dateMap.put("" + (baseIndex + 179), "02-11-2014"); dateMap.put("" +
		 * (baseIndex + 178), "02-10-2014"); dateMap.put("" + (baseIndex + 177),
		 * "02-07-2014"); dateMap.put("" + (baseIndex + 176), "02-06-2014");
		 * dateMap.put("" + (baseIndex + 175), "02-05-2014"); dateMap.put("" +
		 * (baseIndex + 174), "02-04-2014"); dateMap.put("" + (baseIndex + 173),
		 * "02-03-2014"); dateMap.put("" + (baseIndex + 172), "01-31-2014");
		 * dateMap.put("" + (baseIndex + 171), "01-30-2014"); dateMap.put("" +
		 * (baseIndex + 170), "01-29-2014"); dateMap.put("" + (baseIndex + 169),
		 * "01-28-2014"); dateMap.put("" + (baseIndex + 168), "01-27-2014");
		 * dateMap.put("" + (baseIndex + 167), "01-24-2014"); dateMap.put("" +
		 * (baseIndex + 166), "01-23-2014"); dateMap.put("" + (baseIndex + 165),
		 * "01-22-2014"); dateMap.put("" + (baseIndex + 164), "01-21-2014");
		 * dateMap.put("" + (baseIndex + 163), "01-17-2014"); dateMap.put("" +
		 * (baseIndex + 162), "01-16-2014"); dateMap.put("" + (baseIndex + 161),
		 * "01-15-2014"); dateMap.put("" + (baseIndex + 160), "01-14-2014");
		 * dateMap.put("" + (baseIndex + 159), "01-13-2014"); dateMap.put("" +
		 * (baseIndex + 158), "01-10-2014"); dateMap.put("" + (baseIndex + 157),
		 * "01-09-2014"); dateMap.put("" + (baseIndex + 156), "01-08-2014");
		 * //10182 dateMap.put("" + (baseIndex + 155), "01-07-2014");
		 * dateMap.put("" + (baseIndex + 154), "01-06-2014"); dateMap.put("" +
		 * (baseIndex + 153), "01-03-2014"); dateMap.put("" + (baseIndex + 152),
		 * "01-02-2014"); dateMap.put("" + (baseIndex + 151), "12-31-2013");
		 * dateMap.put("" + (baseIndex + 150), "12-30-2013"); dateMap.put("" +
		 * (baseIndex + 149), "12-27-2013"); dateMap.put("" + (baseIndex + 148),
		 * "12-26-2013"); dateMap.put("" + (baseIndex + 147), "12-24-2013");
		 * dateMap.put("" + (baseIndex + 146), "12-23-2013"); dateMap.put("" +
		 * (baseIndex + 145), "12-20-2013"); dateMap.put("" + (baseIndex + 144),
		 * "12-19-2013"); dateMap.put("" + (baseIndex + 143), "12-18-2013");
		 * dateMap.put("" + (baseIndex + 142), "12-17-2013"); dateMap.put("" +
		 * (baseIndex + 141), "12-16-2013"); dateMap.put("" + (baseIndex + 140),
		 * "12-13-2013"); dateMap.put("" + (baseIndex + 139), "12-12-2013");
		 * dateMap.put("" + (baseIndex + 138), "12-11-2013"); dateMap.put("" +
		 * (baseIndex + 137), "12-10-2013"); dateMap.put("" + (baseIndex + 136),
		 * "12-09-2013"); dateMap.put("" + (baseIndex + 135), "12-06-2013");
		 * dateMap.put("" + (baseIndex + 134), "12-05-2013"); dateMap.put("" +
		 * (baseIndex + 133), "12-04-2013"); dateMap.put("" + (baseIndex + 132),
		 * "12-03-2013"); dateMap.put("" + (baseIndex + 131), "12-02-2013");
		 * dateMap.put("" + (baseIndex + 130), "11-29-2013"); dateMap.put("" +
		 * (baseIndex + 129), "11-27-2013"); dateMap.put("" + (baseIndex + 128),
		 * "11-26-2013"); dateMap.put("" + (baseIndex + 127), "11-25-2013");
		 * dateMap.put("" + (baseIndex + 126), "11-22-2013"); dateMap.put("" +
		 * (baseIndex + 125), "11-21-2013"); dateMap.put("" + (baseIndex + 124),
		 * "11-20-2013"); dateMap.put("" + (baseIndex + 123), "11-19-2013");
		 * dateMap.put("" + (baseIndex + 122), "11-18-2013"); dateMap.put("" +
		 * (baseIndex + 121), "11-15-2013"); dateMap.put("" + (baseIndex + 120),
		 * "11-14-2013"); dateMap.put("" + (baseIndex + 119), "11-13-2013");
		 * dateMap.put("" + (baseIndex + 118), "11-12-2013"); dateMap.put("" +
		 * (baseIndex + 117), "11-11-2013"); dateMap.put("" + (baseIndex + 116),
		 * "11-08-2013"); dateMap.put("" + (baseIndex + 115), "11-07-2013");
		 * dateMap.put("" + (baseIndex + 114), "11-06-2013"); dateMap.put("" +
		 * (baseIndex + 113), "11-05-2013"); dateMap.put("" + (baseIndex + 112),
		 * "11-04-2013"); dateMap.put("" + (baseIndex + 111), "11-01-2013");
		 * dateMap.put("" + (baseIndex + 110), "10-31-2013"); dateMap.put("" +
		 * (baseIndex + 109), "10-30-2013"); dateMap.put("" + (baseIndex + 108),
		 * "10-29-2013"); dateMap.put("" + (baseIndex + 107), "10-28-2013");
		 * dateMap.put("" + (baseIndex + 106), "10-25-2013"); dateMap.put("" +
		 * (baseIndex + 105), "10-24-2013"); dateMap.put("" + (baseIndex + 104),
		 * "10-23-2013"); dateMap.put("" + (baseIndex + 103), "10-22-2013");
		 * dateMap.put("" + (baseIndex + 102), "10-21-2013"); dateMap.put("" +
		 * (baseIndex + 101), "10-18-2013"); dateMap.put("" + (baseIndex + 100),
		 * "10-17-2013"); dateMap.put("" + (baseIndex + 99), "10-16-2013");
		 * dateMap.put("" + (baseIndex + 98), "10-15-2013"); dateMap.put("" +
		 * (baseIndex + 97), "10-14-2013"); dateMap.put("" + (baseIndex + 96),
		 * "10-11-2013"); dateMap.put("" + (baseIndex + 95), "10-10-2013");
		 * dateMap.put("" + (baseIndex + 94), "10-09-2013"); dateMap.put("" +
		 * (baseIndex + 93), "10-08-2013"); dateMap.put("" + (baseIndex + 92),
		 * "10-07-2013"); dateMap.put("" + (baseIndex + 91), "10-04-2013");
		 * dateMap.put("" + (baseIndex + 90), "10-03-2013"); dateMap.put("" +
		 * (baseIndex + 89), "10-02-2013"); dateMap.put("" + (baseIndex + 88),
		 * "10-01-2013"); dateMap.put("" + (baseIndex + 87), "09-30-2013");
		 * dateMap.put("" + (baseIndex + 86), "09-27-2013"); dateMap.put("" +
		 * (baseIndex + 85), "09-26-2013"); dateMap.put("" + (baseIndex + 84),
		 * "09-25-2013"); dateMap.put("" + (baseIndex + 83), "09-24-2013");
		 * dateMap.put("" + (baseIndex + 82), "09-23-2013"); dateMap.put("" +
		 * (baseIndex + 81), "09-20-2013"); dateMap.put("" + (baseIndex + 80),
		 * "09-19-2013"); dateMap.put("" + (baseIndex + 79), "09-18-2013");
		 * dateMap.put("" + (baseIndex + 78), "09-17-2013"); dateMap.put("" +
		 * (baseIndex + 77), "09-16-2013"); dateMap.put("" + (baseIndex + 76),
		 * "09-13-2013"); dateMap.put("" + (baseIndex + 75), "09-12-2013");
		 * dateMap.put("" + (baseIndex + 74), "09-11-2013"); dateMap.put("" +
		 * (baseIndex + 73), "09-10-2013"); dateMap.put("" + (baseIndex + 72),
		 * "09-09-2013"); dateMap.put("" + (baseIndex + 71), "09-06-2013");
		 * dateMap.put("" + (baseIndex + 70), "09-05-2013"); dateMap.put("" +
		 * (baseIndex + 69), "09-04-2013"); dateMap.put("" + (baseIndex + 68),
		 * "09-03-2013"); dateMap.put("" + (baseIndex + 67), "08-30-2013");
		 * dateMap.put("" + (baseIndex + 66), "08-29-2013"); dateMap.put("" +
		 * (baseIndex + 65), "08-28-2013"); dateMap.put("" + (baseIndex + 64),
		 * "08-27-2013"); dateMap.put("" + (baseIndex + 63), "08-26-2013");
		 * dateMap.put("" + (baseIndex + 62), "08-23-2013"); dateMap.put("" +
		 * (baseIndex + 61), "08-22-2013"); dateMap.put("" + (baseIndex + 60),
		 * "08-21-2013"); dateMap.put("" + (baseIndex + 59), "08-20-2013");
		 * dateMap.put("" + (baseIndex + 58), "08-19-2013"); dateMap.put("" +
		 * (baseIndex + 57), "08-16-2013"); dateMap.put("" + (baseIndex + 56),
		 * "08-15-2013"); dateMap.put("" + (baseIndex + 55), "08-14-2013");
		 * dateMap.put("" + (baseIndex + 54), "08-13-2013"); dateMap.put("" +
		 * (baseIndex + 53), "08-12-2013"); dateMap.put("" + (baseIndex + 52),
		 * "08-09-2013"); dateMap.put("" + (baseIndex + 51), "08-08-2013");
		 * dateMap.put("" + (baseIndex + 50), "08-07-2013"); dateMap.put("" +
		 * (baseIndex + 49), "08-06-2013"); dateMap.put("" + (baseIndex + 48),
		 * "08-05-2013"); dateMap.put("" + (baseIndex + 47), "08-02-2013");
		 * dateMap.put("" + (baseIndex + 46), "08-01-2013"); dateMap.put("" +
		 * (baseIndex + 45), "07-31-2013"); dateMap.put("" + (baseIndex + 44),
		 * "07-30-2013"); dateMap.put("" + (baseIndex + 43), "07-29-2013");
		 * dateMap.put("" + (baseIndex + 42), "07-26-2013"); dateMap.put("" +
		 * (baseIndex + 41), "07-25-2013"); dateMap.put("" + (baseIndex + 40),
		 * "07-24-2013"); dateMap.put("" + (baseIndex + 39), "07-23-2013");
		 * dateMap.put("" + (baseIndex + 38), "07-22-2013"); dateMap.put("" +
		 * (baseIndex + 37), "07-19-2013"); dateMap.put("" + (baseIndex + 36),
		 * "07-18-2013"); dateMap.put("" + (baseIndex + 35), "07-17-2013");
		 * dateMap.put("" + (baseIndex + 34), "07-16-2013"); dateMap.put("" +
		 * (baseIndex + 33), "07-15-2013"); dateMap.put("" + (baseIndex + 32),
		 * "07-12-2013"); dateMap.put("" + (baseIndex + 31), "07-11-2013");
		 * dateMap.put("" + (baseIndex + 30), "07-10-2013"); dateMap.put("" +
		 * (baseIndex + 29), "07-09-2013"); dateMap.put("" + (baseIndex + 28),
		 * "07-08-2013"); dateMap.put("" + (baseIndex + 27), "07-05-2013");
		 * dateMap.put("" + (baseIndex + 26), "07-03-2013"); dateMap.put("" +
		 * (baseIndex + 25), "07-02-2013"); dateMap.put("" + (baseIndex + 24),
		 * "07-01-2013"); dateMap.put("" + (baseIndex + 23), "06-28-2013");
		 * dateMap.put("" + (baseIndex + 22), "06-27-2013"); dateMap.put("" +
		 * (baseIndex + 21), "06-26-2013"); dateMap.put("" + (baseIndex + 20),
		 * "06-25-2013"); dateMap.put("" + (baseIndex + 19), "06-24-2013");
		 * dateMap.put("" + (baseIndex + 18), "06-21-2013"); dateMap.put("" +
		 * (baseIndex + 17), "06-20-2013"); dateMap.put("" + (baseIndex + 16),
		 * "06-19-2013"); dateMap.put("" + (baseIndex + 15), "06-18-2013");
		 * dateMap.put("" + (baseIndex + 14), "06-17-2013"); dateMap.put("" +
		 * (baseIndex + 13), "06-14-2013"); dateMap.put("" + (baseIndex + 12),
		 * "06-13-2013"); dateMap.put("" + (baseIndex + 11), "06-12-2013");
		 * dateMap.put("" + (baseIndex + 10), "06-11-2013"); dateMap.put("" +
		 * (baseIndex + 9), "06-10-2013"); dateMap.put("" + (baseIndex + 8),
		 * "06-07-2013"); dateMap.put("" + (baseIndex + 7), "06-06-2013");
		 * dateMap.put("" + (baseIndex + 6), "06-05-2013"); dateMap.put("" +
		 * (baseIndex + 5), "06-04-2013"); dateMap.put("" + (baseIndex + 4),
		 * "06-03-2013"); dateMap.put("" + (baseIndex + 3), "05-31-2013");
		 * dateMap.put("" + (baseIndex + 2), "05-30-2013"); dateMap.put("" +
		 * (baseIndex + 1), "05-29-2013"); dateMap.put("" + baseIndex,
		 * "05-28-2013"); dateMap.put("" + (baseIndex - 1), "05-24-2013");
		 * dateMap.put("" + (baseIndex - 2), "05-23-2013"); dateMap.put("" +
		 * (baseIndex - 3), "05-22-2013"); dateMap.put("" + (baseIndex - 4),
		 * "05-21-2013"); dateMap.put("" + (baseIndex - 5), "05-20-2013");
		 * dateMap.put("" + (baseIndex - 6), "05-17-2013"); dateMap.put("" +
		 * (baseIndex - 7), "05-16-2013"); dateMap.put("" + (baseIndex - 8),
		 * "05-15-2013"); dateMap.put("" + (baseIndex - 9), "05-14-2013");
		 * dateMap.put("" + (baseIndex - 10), "05-13-2013"); dateMap.put("" +
		 * (baseIndex - 11), "05-10-2013"); dateMap.put("" + (baseIndex - 12),
		 * "05-09-2013"); dateMap.put("" + (baseIndex - 13), "05-08-2013");
		 * dateMap.put("" + (baseIndex - 14), "05-07-2013"); dateMap.put("" +
		 * (baseIndex - 15), "05-06-2013"); dateMap.put("" + (baseIndex - 16),
		 * "05-03-2013"); dateMap.put("" + (baseIndex - 17), "05-02-2013");
		 * dateMap.put("" + (baseIndex - 18), "05-01-2013"); dateMap.put("" +
		 * (baseIndex - 19), "04-30-2013"); dateMap.put("" + (baseIndex - 20),
		 * "04-29-2013"); dateMap.put("" + (baseIndex - 21), "04-26-2013");
		 * dateMap.put("" + (baseIndex - 22), "04-25-2013"); dateMap.put("" +
		 * (baseIndex - 23), "04-24-2013"); dateMap.put("" + (baseIndex - 24),
		 * "04-23-2013"); dateMap.put("" + (baseIndex - 25), "04-22-2013");
		 * dateMap.put("" + (baseIndex - 26), "04-19-2013"); dateMap.put("" +
		 * (baseIndex - 27), "04-18-2013"); dateMap.put("" + (baseIndex - 28),
		 * "04-17-2013"); dateMap.put("" + (baseIndex - 29), "04-16-2013");
		 * dateMap.put("" + (baseIndex - 30), "04-15-2013"); dateMap.put("" +
		 * (baseIndex - 31), "04-12-2013"); dateMap.put("" + (baseIndex - 32),
		 * "04-11-2013"); dateMap.put("" + (baseIndex - 33), "04-10-2013");
		 * dateMap.put("" + (baseIndex - 34), "04-09-2013"); dateMap.put("" +
		 * (baseIndex - 35), "04-08-2013"); dateMap.put("" + (baseIndex - 36),
		 * "04-05-2013"); dateMap.put("" + (baseIndex - 37), "04-04-2013");
		 * dateMap.put("" + (baseIndex - 38), "04-03-2013"); dateMap.put("" +
		 * (baseIndex - 39), "04-02-2013"); dateMap.put("" + (baseIndex - 40),
		 * "04-01-2013"); dateMap.put("" + (baseIndex - 41), "03-28-2013");
		 * dateMap.put("" + (baseIndex - 42), "03-27-2013"); dateMap.put("" +
		 * (baseIndex - 43), "03-26-2013"); dateMap.put("" + (baseIndex - 44),
		 * "03-25-2013"); dateMap.put("" + (baseIndex - 45), "03-22-2013");
		 * dateMap.put("" + (baseIndex - 46), "03-21-2013"); dateMap.put("" +
		 * (baseIndex - 47), "03-20-2013"); dateMap.put("" + (baseIndex - 48),
		 * "03-19-2013"); dateMap.put("" + (baseIndex - 49), "03-18-2013");
		 * dateMap.put("" + (baseIndex - 50), "03-15-2013"); dateMap.put("" +
		 * (baseIndex - 51), "03-14-2013"); dateMap.put("" + (baseIndex - 52),
		 * "03-13-2013"); dateMap.put("" + (baseIndex - 53), "03-12-2013");
		 * dateMap.put("" + (baseIndex - 54), "03-11-2013"); dateMap.put("" +
		 * (baseIndex - 55), "03-08-2013"); dateMap.put("" + (baseIndex - 56),
		 * "03-07-2013"); dateMap.put("" + (baseIndex - 57), "03-06-2013");
		 * dateMap.put("" + (baseIndex - 58), "03-05-2013"); dateMap.put("" +
		 * (baseIndex - 59), "03-04-2013"); dateMap.put("" + (baseIndex - 60),
		 * "03-01-2013"); dateMap.put("" + (baseIndex - 61), "02-28-2013");
		 * dateMap.put("" + (baseIndex - 62), "02-27-2013"); dateMap.put("" +
		 * (baseIndex - 63), "02-26-2013"); dateMap.put("" + (baseIndex - 64),
		 * "02-25-2013"); dateMap.put("" + (baseIndex - 65), "02-22-2013");
		 * dateMap.put("" + (baseIndex - 66), "02-21-2013"); dateMap.put("" +
		 * (baseIndex - 67), "02-20-2013"); dateMap.put("" + (baseIndex - 68),
		 * "02-19-2013"); dateMap.put("" + (baseIndex - 69), "02-15-2013");
		 * dateMap.put("" + (baseIndex - 70), "02-14-2013"); dateMap.put("" +
		 * (baseIndex - 71), "02-13-2013"); dateMap.put("" + (baseIndex - 72),
		 * "02-12-2013"); dateMap.put("" + (baseIndex - 73), "02-11-2013");
		 * dateMap.put("" + (baseIndex - 74), "02-08-2013"); dateMap.put("" +
		 * (baseIndex - 75), "02-07-2013"); dateMap.put("" + (baseIndex - 76),
		 * "02-06-2013"); dateMap.put("" + (baseIndex - 77), "02-05-2013");
		 * dateMap.put("" + (baseIndex - 78), "02-04-2013"); dateMap.put("" +
		 * (baseIndex - 79), "02-01-2013"); dateMap.put("" + (baseIndex - 80),
		 * "01-31-2013"); dateMap.put("" + (baseIndex - 81), "01-30-2013");
		 * dateMap.put("" + (baseIndex - 82), "01-29-2013"); dateMap.put("" +
		 * (baseIndex - 83), "01-28-2013"); dateMap.put("" + (baseIndex - 84),
		 * "01-25-2013"); dateMap.put("" + (baseIndex - 85), "01-24-2013");
		 * dateMap.put("" + (baseIndex - 86), "01-23-2013"); dateMap.put("" +
		 * (baseIndex - 87), "01-22-2013"); dateMap.put("" + (baseIndex - 88),
		 * "01-18-2013"); dateMap.put("" + (baseIndex - 89), "01-17-2013");
		 * dateMap.put("" + (baseIndex - 90), "01-16-2013"); dateMap.put("" +
		 * (baseIndex - 91), "01-15-2013"); dateMap.put("" + (baseIndex - 92),
		 * "01-14-2013"); dateMap.put("" + (baseIndex - 93), "01-11-2013");
		 * dateMap.put("" + (baseIndex - 94), "01-10-2013"); dateMap.put("" +
		 * (baseIndex - 95), "01-09-2013"); dateMap.put("" + (baseIndex - 96),
		 * "01-08-2013"); dateMap.put("" + (baseIndex - 97), "01-07-2013");
		 * dateMap.put("" + (baseIndex - 98), "01-04-2013"); dateMap.put("" +
		 * (baseIndex - 99), "01-03-2013"); dateMap.put("" + (baseIndex - 100),
		 * "01-02-2013"); dateMap.put("" + (baseIndex - 101), "12-31-2012");
		 * dateMap.put("" + (baseIndex - 102), "12-28-2012"); dateMap.put("" +
		 * (baseIndex - 103), "12-27-2012"); dateMap.put("" + (baseIndex - 104),
		 * "12-26-2012"); dateMap.put("" + (baseIndex - 105), "12-24-2012");
		 * dateMap.put("" + (baseIndex - 106), "12-21-2012"); dateMap.put("" +
		 * (baseIndex - 107), "12-20-2012"); dateMap.put("" + (baseIndex - 108),
		 * "12-19-2012"); dateMap.put("" + (baseIndex - 109), "12-18-2012");
		 * dateMap.put("" + (baseIndex - 110), "12-17-2012"); dateMap.put("" +
		 * (baseIndex - 111), "12-14-2012"); dateMap.put("" + (baseIndex - 112),
		 * "12-13-2012"); dateMap.put("" + (baseIndex - 113), "12-12-2012");
		 * dateMap.put("" + (baseIndex - 114), "12-11-2012"); dateMap.put("" +
		 * (baseIndex - 115), "12-10-2012"); dateMap.put("" + (baseIndex - 116),
		 * "12-07-2012"); dateMap.put("" + (baseIndex - 117), "12-06-2012");
		 * dateMap.put("" + (baseIndex - 118), "12-05-2012"); dateMap.put("" +
		 * (baseIndex - 119), "12-04-2012"); dateMap.put("" + (baseIndex - 120),
		 * "12-03-2012"); dateMap.put("" + (baseIndex - 121), "11-30-2012");
		 * dateMap.put("" + (baseIndex - 122), "11-29-2012"); dateMap.put("" +
		 * (baseIndex - 123), "11-28-2012"); dateMap.put("" + (baseIndex - 124),
		 * "11-27-2012"); dateMap.put("" + (baseIndex - 125), "11-26-2012");
		 * dateMap.put("" + (baseIndex - 126), "11-23-2012"); dateMap.put("" +
		 * (baseIndex - 127), "11-21-2012"); dateMap.put("" + (baseIndex - 128),
		 * "11-20-2012"); dateMap.put("" + (baseIndex - 129), "11-19-2012");
		 * dateMap.put("" + (baseIndex - 130), "11-16-2012"); dateMap.put("" +
		 * (baseIndex - 131), "11-15-2012"); dateMap.put("" + (baseIndex - 132),
		 * "11-14-2012"); dateMap.put("" + (baseIndex - 133), "11-13-2012");
		 * dateMap.put("" + (baseIndex - 134), "11-12-2012"); dateMap.put("" +
		 * (baseIndex - 135), "11-09-2012"); dateMap.put("" + (baseIndex - 136),
		 * "11-08-2012"); dateMap.put("" + (baseIndex - 137), "11-07-2012");
		 * dateMap.put("" + (baseIndex - 138), "11-06-2012"); dateMap.put("" +
		 * (baseIndex - 139), "11-05-2012"); dateMap.put("" + (baseIndex - 140),
		 * "11-02-2012"); dateMap.put("" + (baseIndex - 141), "11-01-2012");
		 * dateMap.put("" + (baseIndex - 142), "10-31-2012"); dateMap.put("" +
		 * (baseIndex - 143), "10-26-2012"); dateMap.put("" + (baseIndex - 144),
		 * "10-25-2012"); dateMap.put("" + (baseIndex - 145), "10-24-2012");
		 * dateMap.put("" + (baseIndex - 146), "10-23-2012"); dateMap.put("" +
		 * (baseIndex - 147), "10-22-2012"); dateMap.put("" + (baseIndex - 148),
		 * "10-19-2012"); dateMap.put("" + (baseIndex - 149), "10-18-2012");
		 * dateMap.put("" + (baseIndex - 150), "10-17-2012"); dateMap.put("" +
		 * (baseIndex - 151), "10-16-2012"); dateMap.put("" + (baseIndex - 152),
		 * "10-15-2012"); dateMap.put("" + (baseIndex - 153), "10-12-2012");
		 * dateMap.put("" + (baseIndex - 154), "10-11-2012"); dateMap.put("" +
		 * (baseIndex - 155), "10-10-2012"); dateMap.put("" + (baseIndex - 156),
		 * "10-09-2012"); dateMap.put("" + (baseIndex - 157), "10-08-2012");
		 * dateMap.put("" + (baseIndex - 158), "10-05-2012"); dateMap.put("" +
		 * (baseIndex - 159), "10-04-2012"); dateMap.put("" + (baseIndex - 160),
		 * "10-03-2012"); dateMap.put("" + (baseIndex - 161), "10-02-2012");
		 * dateMap.put("" + (baseIndex - 162), "10-01-2012"); dateMap.put("" +
		 * (baseIndex - 163), "09-28-2012"); dateMap.put("" + (baseIndex - 164),
		 * "09-27-2012"); dateMap.put("" + (baseIndex - 165), "09-26-2012");
		 * dateMap.put("" + (baseIndex - 166), "09-25-2012"); dateMap.put("" +
		 * (baseIndex - 167), "09-24-2012"); dateMap.put("" + (baseIndex - 168),
		 * "09-21-2012"); dateMap.put("" + (baseIndex - 169), "09-20-2012");
		 * dateMap.put("" + (baseIndex - 170), "09-19-2012"); dateMap.put("" +
		 * (baseIndex - 171), "09-18-2012"); dateMap.put("" + (baseIndex - 172),
		 * "09-17-2012"); dateMap.put("" + (baseIndex - 173), "09-14-2012");
		 * dateMap.put("" + (baseIndex - 174), "09-13-2012"); dateMap.put("" +
		 * (baseIndex - 175), "09-12-2012"); dateMap.put("" + (baseIndex - 176),
		 * "09-11-2012"); dateMap.put("" + (baseIndex - 177), "09-10-2012");
		 * dateMap.put("" + (baseIndex - 178), "09-07-2012"); dateMap.put("" +
		 * (baseIndex - 179), "09-06-2012"); dateMap.put("" + (baseIndex - 180),
		 * "09-05-2012"); dateMap.put("" + (baseIndex - 181), "09-04-2012");
		 * dateMap.put("" + (baseIndex - 182), "08-31-2012"); dateMap.put("" +
		 * (baseIndex - 183), "08-30-2012"); dateMap.put("" + (baseIndex - 184),
		 * "08-29-2012"); dateMap.put("" + (baseIndex - 185), "08-28-2012");
		 * dateMap.put("" + (baseIndex - 186), "08-27-2012"); dateMap.put("" +
		 * (baseIndex - 187), "08-24-2012"); dateMap.put("" + (baseIndex - 188),
		 * "08-23-2012"); dateMap.put("" + (baseIndex - 189), "08-22-2012");
		 * dateMap.put("" + (baseIndex - 190), "08-21-2012"); dateMap.put("" +
		 * (baseIndex - 191), "08-20-2012"); dateMap.put("" + (baseIndex - 192),
		 * "08-17-2012"); dateMap.put("" + (baseIndex - 193), "08-16-2012");
		 * dateMap.put("" + (baseIndex - 194), "08-15-2012"); dateMap.put("" +
		 * (baseIndex - 195), "08-14-2012"); dateMap.put("" + (baseIndex - 196),
		 * "08-13-2012"); dateMap.put("" + (baseIndex - 197), "08-10-2012");
		 * dateMap.put("" + (baseIndex - 198), "08-09-2012"); dateMap.put("" +
		 * (baseIndex - 199), "08-08-2012"); dateMap.put("" + (baseIndex - 200),
		 * "08-07-2012"); dateMap.put("" + (baseIndex - 201), "08-06-2012");
		 * dateMap.put("" + (baseIndex - 202), "08-03-2012"); dateMap.put("" +
		 * (baseIndex - 203), "08-02-2012"); dateMap.put("" + (baseIndex - 204),
		 * "08-01-2012"); dateMap.put("" + (baseIndex - 205), "07-31-2012");
		 * dateMap.put("" + (baseIndex - 206), "07-30-2012"); dateMap.put("" +
		 * (baseIndex - 207), "07-27-2012"); dateMap.put("" + (baseIndex - 208),
		 * "07-26-2012"); dateMap.put("" + (baseIndex - 209), "07-25-2012");
		 * dateMap.put("" + (baseIndex - 210), "07-24-2012"); dateMap.put("" +
		 * (baseIndex - 211), "07-23-2012"); dateMap.put("" + (baseIndex - 212),
		 * "07-20-2012"); dateMap.put("" + (baseIndex - 213), "07-19-2012");
		 * dateMap.put("" + (baseIndex - 214), "07-18-2012"); dateMap.put("" +
		 * (baseIndex - 215), "07-17-2012"); dateMap.put("" + (baseIndex - 216),
		 * "07-16-2012"); dateMap.put("" + (baseIndex - 217), "07-13-2012");
		 * dateMap.put("" + (baseIndex - 218), "07-12-2012"); dateMap.put("" +
		 * (baseIndex - 219), "07-11-2012"); dateMap.put("" + (baseIndex - 220),
		 * "07-10-2012"); dateMap.put("" + (baseIndex - 221), "07-09-2012");
		 * dateMap.put("" + (baseIndex - 222), "07-06-2012"); dateMap.put("" +
		 * (baseIndex - 223), "07-05-2012"); dateMap.put("" + (baseIndex - 224),
		 * "07-03-2012"); dateMap.put("" + (baseIndex - 225), "07-02-2012");
		 * dateMap.put("" + (baseIndex - 226), "06-29-2012"); dateMap.put("" +
		 * (baseIndex - 227), "06-28-2012"); dateMap.put("" + (baseIndex - 228),
		 * "06-27-2012"); dateMap.put("" + (baseIndex - 229), "06-26-2012");
		 * dateMap.put("" + (baseIndex - 230), "06-25-2012"); dateMap.put("" +
		 * (baseIndex - 231), "06-22-2012"); dateMap.put("" + (baseIndex - 232),
		 * "06-21-2012"); dateMap.put("" + (baseIndex - 233), "06-20-2012");
		 * dateMap.put("" + (baseIndex - 234), "06-19-2012"); dateMap.put("" +
		 * (baseIndex - 235), "06-18-2012"); dateMap.put("" + (baseIndex - 236),
		 * "06-15-2012"); dateMap.put("" + (baseIndex - 237), "06-14-2012");
		 * dateMap.put("" + (baseIndex - 238), "06-13-2012"); dateMap.put("" +
		 * (baseIndex - 239), "06-12-2012"); dateMap.put("" + (baseIndex - 240),
		 * "06-11-2012"); dateMap.put("" + (baseIndex - 241), "06-08-2012");
		 * dateMap.put("" + (baseIndex - 242), "06-07-2012"); dateMap.put("" +
		 * (baseIndex - 243), "06-06-2012"); dateMap.put("" + (baseIndex - 244),
		 * "06-05-2012"); dateMap.put("" + (baseIndex - 245), "06-04-2012");
		 * dateMap.put("" + (baseIndex - 246), "06-01-2012"); dateMap.put("" +
		 * (baseIndex - 247), "05-31-2012"); dateMap.put("" + (baseIndex - 248),
		 * "05-30-2012"); dateMap.put("" + (baseIndex - 249), "05-29-2012");
		 * dateMap.put("" + (baseIndex - 250), "05-25-2012"); dateMap.put("" +
		 * (baseIndex - 251), "05-24-2012"); dateMap.put("" + (baseIndex - 252),
		 * "05-23-2012"); dateMap.put("" + (baseIndex - 253), "05-22-2012");
		 * dateMap.put("" + (baseIndex - 254), "05-21-2012"); dateMap.put("" +
		 * (baseIndex - 255), "05-18-2012"); dateMap.put("" + (baseIndex - 256),
		 * "05-17-2012"); dateMap.put("" + (baseIndex - 257), "05-16-2012");
		 * dateMap.put("" + (baseIndex - 258), "05-15-2012"); dateMap.put("" +
		 * (baseIndex - 259), "05-14-2012"); dateMap.put("" + (baseIndex - 260),
		 * "05-11-2012"); dateMap.put("" + (baseIndex - 261), "05-10-2012");
		 * dateMap.put("" + (baseIndex - 262), "05-09-2012"); dateMap.put("" +
		 * (baseIndex - 263), "05-08-2012"); dateMap.put("" + (baseIndex - 264),
		 * "05-07-2012"); dateMap.put("" + (baseIndex - 265), "05-04-2012");
		 * dateMap.put("" + (baseIndex - 266), "05-03-2012"); dateMap.put("" +
		 * (baseIndex - 267), "05-02-2012"); dateMap.put("" + (baseIndex - 268),
		 * "05-01-2012"); dateMap.put("" + (baseIndex - 269), "04-30-2012");
		 * dateMap.put("" + (baseIndex - 270), "04-27-2012"); dateMap.put("" +
		 * (baseIndex - 271), "04-26-2012"); dateMap.put("" + (baseIndex - 272),
		 * "04-25-2012"); dateMap.put("" + (baseIndex - 273), "04-24-2012");
		 * dateMap.put("" + (baseIndex - 274), "04-23-2012"); dateMap.put("" +
		 * (baseIndex - 275), "04-20-2012"); dateMap.put("" + (baseIndex - 276),
		 * "04-19-2012"); dateMap.put("" + (baseIndex - 277), "04-18-2012");
		 * dateMap.put("" + (baseIndex - 278), "04-17-2012"); dateMap.put("" +
		 * (baseIndex - 279), "04-16-2012"); dateMap.put("" + (baseIndex - 280),
		 * "04-13-2012"); dateMap.put("" + (baseIndex - 281), "04-12-2012");
		 * dateMap.put("" + (baseIndex - 282), "04-11-2012"); dateMap.put("" +
		 * (baseIndex - 283), "04-10-2012"); dateMap.put("" + (baseIndex - 284),
		 * "04-09-2012"); dateMap.put("" + (baseIndex - 285), "04-05-2012");
		 * dateMap.put("" + (baseIndex - 286), "04-04-2012"); dateMap.put("" +
		 * (baseIndex - 287), "04-03-2012"); dateMap.put("" + (baseIndex - 288),
		 * "04-02-2012"); dateMap.put("" + (baseIndex - 289), "03-30-2012");
		 * dateMap.put("" + (baseIndex - 290), "03-29-2012"); dateMap.put("" +
		 * (baseIndex - 291), "03-28-2012"); dateMap.put("" + (baseIndex - 292),
		 * "03-27-2012"); dateMap.put("" + (baseIndex - 293), "03-26-2012");
		 * dateMap.put("" + (baseIndex - 294), "03-23-2012"); dateMap.put("" +
		 * (baseIndex - 295), "03-22-2012"); dateMap.put("" + (baseIndex - 296),
		 * "03-21-2012"); dateMap.put("" + (baseIndex - 297), "03-20-2012");
		 * dateMap.put("" + (baseIndex - 298), "03-19-2012"); dateMap.put("" +
		 * (baseIndex - 299), "03-16-2012"); dateMap.put("" + (baseIndex - 300),
		 * "03-15-2012"); dateMap.put("" + (baseIndex - 301), "03-14-2012");
		 * dateMap.put("" + (baseIndex - 302), "03-13-2012"); dateMap.put("" +
		 * (baseIndex - 303), "03-12-2012"); dateMap.put("" + (baseIndex - 304),
		 * "03-09-2012"); dateMap.put("" + (baseIndex - 305), "03-08-2012");
		 * dateMap.put("" + (baseIndex - 306), "03-07-2012"); dateMap.put("" +
		 * (baseIndex - 307), "03-06-2012"); dateMap.put("" + (baseIndex - 308),
		 * "03-05-2012"); dateMap.put("" + (baseIndex - 309), "03-02-2012");
		 * dateMap.put("" + (baseIndex - 310), "03-01-2012"); dateMap.put("" +
		 * (baseIndex - 311), "02-29-2012"); dateMap.put("" + (baseIndex - 312),
		 * "02-28-2012"); dateMap.put("" + (baseIndex - 313), "02-27-2012");
		 * dateMap.put("" + (baseIndex - 314), "02-24-2012"); dateMap.put("" +
		 * (baseIndex - 315), "02-23-2012"); dateMap.put("" + (baseIndex - 316),
		 * "02-22-2012"); dateMap.put("" + (baseIndex - 317), "02-21-2012");
		 * dateMap.put("" + (baseIndex - 318), "02-17-2012"); dateMap.put("" +
		 * (baseIndex - 319), "02-16-2012"); dateMap.put("" + (baseIndex - 320),
		 * "02-15-2012"); dateMap.put("" + (baseIndex - 321), "02-14-2012");
		 * dateMap.put("" + (baseIndex - 322), "02-13-2012"); dateMap.put("" +
		 * (baseIndex - 323), "02-10-2012"); dateMap.put("" + (baseIndex - 324),
		 * "02-09-2012"); dateMap.put("" + (baseIndex - 325), "02-08-2012");
		 * dateMap.put("" + (baseIndex - 326), "02-07-2012"); dateMap.put("" +
		 * (baseIndex - 327), "02-06-2012"); dateMap.put("" + (baseIndex - 328),
		 * "02-03-2012"); dateMap.put("" + (baseIndex - 329), "02-02-2012");
		 * dateMap.put("" + (baseIndex - 330), "02-01-2012"); dateMap.put("" +
		 * (baseIndex - 331), "01-31-2012"); dateMap.put("" + (baseIndex - 332),
		 * "01-30-2012"); dateMap.put("" + (baseIndex - 333), "01-27-2012");
		 * dateMap.put("" + (baseIndex - 334), "01-26-2012"); dateMap.put("" +
		 * (baseIndex - 335), "01-25-2012"); dateMap.put("" + (baseIndex - 336),
		 * "01-24-2012"); dateMap.put("" + (baseIndex - 337), "01-23-2012");
		 * dateMap.put("" + (baseIndex - 338), "01-20-2012"); dateMap.put("" +
		 * (baseIndex - 339), "01-19-2012"); dateMap.put("" + (baseIndex - 340),
		 * "01-18-2012"); dateMap.put("" + (baseIndex - 341), "01-17-2012");
		 * dateMap.put("" + (baseIndex - 342), "01-13-2012"); dateMap.put("" +
		 * (baseIndex - 343), "01-12-2012"); dateMap.put("" + (baseIndex - 344),
		 * "01-11-2012"); dateMap.put("" + (baseIndex - 345), "01-10-2012");
		 * dateMap.put("" + (baseIndex - 346), "01-09-2012"); dateMap.put("" +
		 * (baseIndex - 347), "01-06-2012"); dateMap.put("" + (baseIndex - 348),
		 * "01-05-2012"); dateMap.put("" + (baseIndex - 349), "01-04-2012");
		 * dateMap.put("" + (baseIndex - 350), "01-03-2012");
		 */
		return dateMap;

	}

	public static int getTrueRangeDays() {
		return trueRangeDays;
	}

	public static void setTrueRangeDays(int trueRangeDays) {
		StaticData.trueRangeDays = trueRangeDays;
	}

	public static int getFdata() {
		return fdata;
	}

	public static void setFdata(int fdata) {
		StaticData.fdata = fdata;
	}

	public static String getTableName() {
		return tableName;
	}

	public static void setTableName(String tableName) {
		StaticData.tableName = tableName;
	}

	public static String getSourceTable() {
		return sourceTable;
	}

	public static void setSourceTable(String sourceTable) {
		StaticData.sourceTable = sourceTable;
	}

	public static String getTargetTable() {
		return targetTable;
	}

	public static void setTargetTable(String targetTable) {
		StaticData.targetTable = targetTable;
	}

	public static int getObvSteps() {
		return obvSteps;
	}

	public static void setObvSteps(int obvSteps) {
		StaticData.obvSteps = obvSteps;
	}

	public static int getObvRSITriggerDays() {
		return obvRSITriggerDays;
	}

	public static void setObvRSITriggerDays(int obvRSITriggerDays) {
		StaticData.obvRSITriggerDays = obvRSITriggerDays;
	}

	public static int getCountDuringObvRSITriggerDays() {
		return countDuringObvRSITriggerDays;
	}

	public static void setCountDuringObvRSITriggerDays(
			int countDuringObvRSITriggerDays) {
		StaticData.countDuringObvRSITriggerDays = countDuringObvRSITriggerDays;
	}

	public static int getIntradayFolderStart() {
		return intradayFolderStart;
	}

	public static void setIntradayFolderStart(int intradayFolderStart) {
		StaticData.intradayFolderStart = intradayFolderStart;
	}

	public static int getIntradayFolderEnd() {
		return intradayFolderEnd;
	}

	public static void setIntradayFolderEnd(int intradayFolderEnd) {
		StaticData.intradayFolderEnd = intradayFolderEnd;
	}

	public static long getIntradayLastIndex() {
		return intradayLastIndex;
	}

	public static void setIntradayLastIndex(long intradayLastIndex) {
		StaticData.intradayLastIndex = intradayLastIndex;
	}

	public static int getDownloadDays() {
		return downloadDays;
	}

	public static void setDownloadDays(int downloadDays) {
		StaticData.downloadDays = downloadDays;
	}

	public static int getBaseIndex() {
		return baseIndex;
	}

	public static void setBaseIndex(int baseIndex) {
		StaticData.baseIndex = baseIndex;
	}

	public static int getSubFolderNumbers() {
		return subFolderNumbers;
	}

	public static void setSubFolderNumbers(int subFolderNumbers) {
		StaticData.subFolderNumbers = subFolderNumbers;
	}

	public static Hashtable getExcludedStocks() {
		if(excludedStocks==null){
			excludedStocks = new Hashtable();
			
			/**New exclused 7/2/2018*/
			excludedStocks.put("TMCXU","0");
			excludedStocks.put("ADVT","10-7:156");
			excludedStocks.put("VTIQU","10-7:0");
			excludedStocks.put("LCAHW","10-8:0");
			excludedStocks.put("MGC","10-10:215");
			excludedStocks.put("NBR-A","10-7:64");
			excludedStocks.put("VXZ","10-10:185");
			excludedStocks.put("ANGL","10-8:133");
			excludedStocks.put("GXC","10-10:139");
			excludedStocks.put("CVM.W","10-10:0");
			excludedStocks.put("FLJP","10-7:11");
			excludedStocks.put("RUSS","10-7:134");
			excludedStocks.put("GXC","11-10:139");
			excludedStocks.put("PACQU","10-7:0");
			excludedStocks.put("VXZ","12-13:223");
			excludedStocks.put("RYT","11-7:134");
			excludedStocks.put("DTJ","10-9:0");
			excludedStocks.put("TMK-C","10-8:0");
			excludedStocks.put("GOVNI","11-9:0");
			excludedStocks.put("PHYS","10-7:137");
			excludedStocks.put("FG.W","10-7:0");
			excludedStocks.put("PACQU","11-7:0");
			excludedStocks.put("VXZ","13-13:223");
			excludedStocks.put("MGC","13-12:250");
			excludedStocks.put("PSI","13-8:148");
			excludedStocks.put("MACQW","13-8:0");
			excludedStocks.put("IYH","10-7:127");
			excludedStocks.put("MYDU","10-11:0");
			excludedStocks.put("M5DL","10-10:0");
			excludedStocks.put("PACQ","10-9:64");
			excludedStocks.put("DRN","13-8:153");
			excludedStocks.put("M6DL","10-10:0");
			excludedStocks.put("M1DL","10-10:0");
			excludedStocks.put("FXP","13-8:97");
			excludedStocks.put("MYDL","10-11:0");
			excludedStocks.put("M3DL","10-10:0");
			excludedStocks.put("M5DU","12-11:0");
			excludedStocks.put("FBT","18-11:223");
			excludedStocks.put("BSIG","16-10:0");
			excludedStocks.put("M5DU","15-14:0");
			excludedStocks.put("TDACU","10-8:0");
			excludedStocks.put("TMCXW","10-10:0");
			excludedStocks.put("TMCX","10-8:36");
			excludedStocks.put("LTSF","10-7:0");
			excludedStocks.put("COWNL","10-9:0");
			excludedStocks.put("JUST","11-7:85");
			excludedStocks.put("PBY","10-7:113");
			/**New exclused 7/2/2018*/
			
			excludedStocks.put("AFST","0");
			excludedStocks.put("AIQ","0");
			excludedStocks.put("STA-I","0");
			excludedStocks.put("CNACR","0");
			excludedStocks.put("PUK.P","0");
			excludedStocks.put("N3HJ","0");
			excludedStocks.put("GM.B","0");
			excludedStocks.put("CLN-J","0");
			excludedStocks.put("OIB.C","0");
			excludedStocks.put("AFGH","0");
			excludedStocks.put("CNACR","0");
			excludedStocks.put("SLDA","0");
			//excludedStocks.put("PRPLW","0");
			excludedStocks.put("VONV","0");
			//excludedStocks.put("BRACW","0");
	
			
			
			excludedStocks.put("ABEOW","0");
			excludedStocks.put("ADVE","0");
			excludedStocks.put("ADVV","0");
			excludedStocks.put("AFS-A","0");
			excludedStocks.put("AFS-E","0");
			excludedStocks.put("AFS-F","0");
			excludedStocks.put("ALTS","0");
			excludedStocks.put("AMRHW","0");
			excludedStocks.put("AMRWW","0");
			excludedStocks.put("APOPW","0");
			excludedStocks.put("ARE-A","0");
			excludedStocks.put("CETXP","8");
			excludedStocks.put("CIS.W","0");
			excludedStocks.put("COMG","1");
			excludedStocks.put("COR-Z","0");
			excludedStocks.put("COWNZ","0");
			excludedStocks.put("CPHC","3");
			excludedStocks.put("CPI","9");
			excludedStocks.put("CTAA","0");
			excludedStocks.put("CWBC","7");
			excludedStocks.put("DALT","0");
			excludedStocks.put("DBES","1");
			excludedStocks.put("DECD","0");
			excludedStocks.put("DECS","0");
			excludedStocks.put("DECT","0");
			excludedStocks.put("DECV","0");
			excludedStocks.put("DEWJ","0");
			excludedStocks.put("DINT","0");
			excludedStocks.put("EEMD","0");
			excludedStocks.put("EMFM","4");
			excludedStocks.put("EQWL","1");
			excludedStocks.put("ESGL","0");
			excludedStocks.put("ESGS","0");
			excludedStocks.put("FFBCW","0");
			excludedStocks.put("FLCA","5");
			excludedStocks.put("FLEH","1");
			excludedStocks.put("FLGB","0");
			excludedStocks.put("FLIO","0");
			excludedStocks.put("FLMI","2");
			excludedStocks.put("FLQM","0");
			excludedStocks.put("FLQS","0");
			excludedStocks.put("FLRU","0");
			excludedStocks.put("FNCF","3");
			excludedStocks.put("FSACU","0");
			excludedStocks.put("GARD","1");
			excludedStocks.put("GPAQU","0");
			excludedStocks.put("GRMY","1");
			excludedStocks.put("GRU","2");
			excludedStocks.put("GSEW","4");
			excludedStocks.put("GSJY","3");
			excludedStocks.put("HBK","4");
			excludedStocks.put("HECO","0");
			excludedStocks.put("HEFV","2");
			excludedStocks.put("HGI","0");
			excludedStocks.put("HMG","3");
			excludedStocks.put("HOVNP","8");
			excludedStocks.put("HSB-A","0");
			excludedStocks.put("IBO","0");
			excludedStocks.put("IGVT","0");
			excludedStocks.put("IIP-A","0");
			excludedStocks.put("IMLP","3");
			excludedStocks.put("IRE-C","0");
			excludedStocks.put("JJSB","1");
			excludedStocks.put("JJUB","5");
			excludedStocks.put("JPGB","0");
			excludedStocks.put("KCAPL","0");
			excludedStocks.put("LEGR","0");
			excludedStocks.put("LGC.W","0");
			excludedStocks.put("LMHA","0");
			excludedStocks.put("LMLP","0");
			excludedStocks.put("LRET","1");
			excludedStocks.put("LSVX","2");
			excludedStocks.put("LTL","4");
			excludedStocks.put("LTN","5");
			excludedStocks.put("M3DA","0");
			excludedStocks.put("M3DE","0");
			excludedStocks.put("M3DI","0");
			excludedStocks.put("M3DQ","0");
			excludedStocks.put("M3DS","0");
			excludedStocks.put("M3DX","0");
			excludedStocks.put("M3HE","0");
			excludedStocks.put("M3HI","0");
			excludedStocks.put("M3HL","0");
			excludedStocks.put("M3HO","0");
			excludedStocks.put("M3HS","0");
			excludedStocks.put("M3HX","0");
			excludedStocks.put("M3HY","0");
			excludedStocks.put("M3LG","0");
			excludedStocks.put("M3LH","0");
			excludedStocks.put("M3LL","0");
			excludedStocks.put("M3LO","0");
			excludedStocks.put("M3LX","0");
			excludedStocks.put("MADA","0");
			excludedStocks.put("MADE","0");
			excludedStocks.put("MADH","0");
			excludedStocks.put("MADL","0");
			excludedStocks.put("MADO","0");
			excludedStocks.put("MADQ","0");
			excludedStocks.put("MADV","0");
			excludedStocks.put("MADX","0");
			excludedStocks.put("MAHE","0");
			excludedStocks.put("MAHH","0");
			excludedStocks.put("MAHL","0");
			excludedStocks.put("MAHP","0");
			excludedStocks.put("MAHU","0");
			excludedStocks.put("MAHX","0");
			excludedStocks.put("MAHZ","0");
			excludedStocks.put("MALC","0");
			excludedStocks.put("MALG","0");
			excludedStocks.put("MALP","0");
			excludedStocks.put("MALV","0");
			excludedStocks.put("MALX","0");
			excludedStocks.put("MALZ","0");
			excludedStocks.put("MATF","0");
			excludedStocks.put("MCRO","2");
			excludedStocks.put("MCV","0");
			excludedStocks.put("MIT-A","0");
			excludedStocks.put("MIT-B","0");
			excludedStocks.put("MLPS","0");
			excludedStocks.put("MLPZ","1");
			excludedStocks.put("MMDM","5");
			excludedStocks.put("MTB.P","0");
			excludedStocks.put("MTB.W","0");
			excludedStocks.put("MYDE","0");
			excludedStocks.put("MYDK","0");
			excludedStocks.put("MYDO","0");
			excludedStocks.put("MYDY","0");
			excludedStocks.put("MYHA","0");
			excludedStocks.put("MYHG","0");
			excludedStocks.put("MYHL","0");
			excludedStocks.put("MYHQ","0");
			excludedStocks.put("MYHV","0");
			excludedStocks.put("MYHX","0");
			excludedStocks.put("MYHY","0");
			excludedStocks.put("MYLE","0");
			excludedStocks.put("MYLG","0");
			excludedStocks.put("MYLK","0");
			excludedStocks.put("MYLP","0");
			excludedStocks.put("MYLS","0");
			excludedStocks.put("MYLW","0");
			excludedStocks.put("MYLZ","0");
			excludedStocks.put("N1HC","0");
			excludedStocks.put("N1HH","0");
			excludedStocks.put("N1LB","0");
			excludedStocks.put("N1LE","0");
			excludedStocks.put("N1LJ","0");
			excludedStocks.put("N5DB","0");
			excludedStocks.put("N5DE","0");
			excludedStocks.put("N5DH","0");
			excludedStocks.put("N5HB","0");
			excludedStocks.put("N5LH","0");
			excludedStocks.put("NEE-I","0");
			excludedStocks.put("NUDM","1");
			excludedStocks.put("NULV","4");
			excludedStocks.put("NURE","1");
			excludedStocks.put("OBAS","9");
			excludedStocks.put("OCIO","0");
			excludedStocks.put("OCSLL","0");
			excludedStocks.put("OFED","3");
			excludedStocks.put("OILU","0");
			excludedStocks.put("PBI-B","0");
			excludedStocks.put("PDN","1");
			excludedStocks.put("PEX","0");
			excludedStocks.put("PJH","0");
			excludedStocks.put("PMR","0");
			excludedStocks.put("PNC.W","0");
			excludedStocks.put("PPX","9");
			excludedStocks.put("PSC","2");
			excludedStocks.put("PTM","1");
			excludedStocks.put("PUW","0");
			excludedStocks.put("PWS","0");
			excludedStocks.put("PXQ","3");
			excludedStocks.put("PZI","3");
			excludedStocks.put("QXRR","0");
			excludedStocks.put("RFUN","1");
			excludedStocks.put("RILYG","0");
			excludedStocks.put("RORE","1");
			excludedStocks.put("SDYL","2");
			excludedStocks.put("SMLL","1");
			excludedStocks.put("SSWA","0");
			excludedStocks.put("TCBIL","0");
			excludedStocks.put("TIG","7");
			excludedStocks.put("TPVY","0");
			excludedStocks.put("TUZ","10");
			excludedStocks.put("TXSC","0");
			excludedStocks.put("TXSX","0");
			excludedStocks.put("TYD","3");
			excludedStocks.put("UBM","0");
			excludedStocks.put("UCC","1");
			excludedStocks.put("UGE","1");
			excludedStocks.put("ULBR","2");
			excludedStocks.put("USHY","1");
			excludedStocks.put("USTB","1");
			excludedStocks.put("VIIZ","6");
			excludedStocks.put("VLU","3");
			excludedStocks.put("VQT","2");
			excludedStocks.put("WDRW","0");
			excludedStocks.put("WEAR","0");
			excludedStocks.put("WINS","3");
			excludedStocks.put("WRB-B","0");
			excludedStocks.put("WRB-C","0");
			excludedStocks.put("YCL","0");
			excludedStocks.put("YLD","1");
			excludedStocks.put("ZIONW","0");
			excludedStocks.put("ZMLP","0");
			excludedStocks.put("ADVD","0");
			excludedStocks.put("ADVN","4");
			excludedStocks.put("ADVQ","0");
			excludedStocks.put("ADVS","0");
			excludedStocks.put("ADVX","0");
			excludedStocks.put("AFS-D","0");
			excludedStocks.put("AGM-B","1");
			excludedStocks.put("ALL-B","0");
			excludedStocks.put("ALL-Y","0");
			excludedStocks.put("ASRVP","1");
			excludedStocks.put("BIBL","0");
			excludedStocks.put("BOSS","1");
			excludedStocks.put("BTAL","4");
			excludedStocks.put("CBND","1");
			excludedStocks.put("CBON","1");
			excludedStocks.put("CERCW","0");
			excludedStocks.put("CMS-B","3");
			excludedStocks.put("CNYA","8");
			excludedStocks.put("COD-A","0");
			excludedStocks.put("COD-B","0");
			excludedStocks.put("COM","2");
			excludedStocks.put("CPER","5");
			excludedStocks.put("CTX","1");
			excludedStocks.put("CTZ","6");
			excludedStocks.put("CYHHZ","0");
			excludedStocks.put("DBAP","4");
			excludedStocks.put("DBGR","2");
			excludedStocks.put("DBKO","8");
			excludedStocks.put("DECA","0");
			excludedStocks.put("DECE","0");
			excludedStocks.put("DECQ","0");
			excludedStocks.put("DECX","0");
			excludedStocks.put("DEUS","4");
			excludedStocks.put("DEW","7");
			excludedStocks.put("DGT","1");
			excludedStocks.put("DGZ","2");
			excludedStocks.put("DIVA","0");
			excludedStocks.put("DJCI","0");
			excludedStocks.put("DRUA","0");
			excludedStocks.put("DTL.P","0");
			excludedStocks.put("EDOM","0");
			excludedStocks.put("EFAX","0");
			excludedStocks.put("EFBI","6");
			excludedStocks.put("EMBH","1");
			excludedStocks.put("EQLT","3");
			excludedStocks.put("ESGF","0");
			excludedStocks.put("ESTRW","0");
			excludedStocks.put("EURZ","0");
			excludedStocks.put("FFTG","0");
			excludedStocks.put("FLAG","1");
			excludedStocks.put("FLAU","2");
			excludedStocks.put("FLFR","1");
			excludedStocks.put("FLMB","0");
			excludedStocks.put("FLRT","3");
			excludedStocks.put("FYLD","1");
			excludedStocks.put("GCVRZ","0");
			excludedStocks.put("GMR-A","0");
			excludedStocks.put("GRNB","0");
			excludedStocks.put("GTN.A","4");
			excludedStocks.put("GWGH","10");
			excludedStocks.put("GXF","2");
			excludedStocks.put("GYB","10");
			excludedStocks.put("HAP","5");
			excludedStocks.put("HEMV","1");
			excludedStocks.put("HILO","0");
			excludedStocks.put("HML-A","0");
			excludedStocks.put("HOML","1");
			excludedStocks.put("IDHD","0");
			excludedStocks.put("IEAWW","0");
			excludedStocks.put("IGZ","0");
			excludedStocks.put("ILPT","0");
			excludedStocks.put("INR","3");
			excludedStocks.put("IQDY","10");
			excludedStocks.put("ITA","0");
			excludedStocks.put("IVENC","0");
			excludedStocks.put("JKJ","4");
			excludedStocks.put("JPED","0");
			excludedStocks.put("JPM.W","0");
			excludedStocks.put("JPMF","0");
			excludedStocks.put("JPN","5");
			excludedStocks.put("LEAD","1");
			excludedStocks.put("LLQD","0");
			excludedStocks.put("LMHB","0");
			excludedStocks.put("LSBK","7");
			excludedStocks.put("LVIN","0");
			excludedStocks.put("M3DG","0");
			excludedStocks.put("M3DO","0");
			excludedStocks.put("M3DP","0");
			excludedStocks.put("M3DV","0");
			excludedStocks.put("M3DZ","0");
			excludedStocks.put("M3HA","0");
			excludedStocks.put("M3HG","0");
			excludedStocks.put("M3HK","0");
			excludedStocks.put("M3HN","0");
			excludedStocks.put("M3HP","0");
			excludedStocks.put("M3HU","0");
			excludedStocks.put("M3HZ","0");
			excludedStocks.put("M3LE","0");
			excludedStocks.put("M3LP","0");
			excludedStocks.put("M3LV","0");
			excludedStocks.put("M3LY","0");
			excludedStocks.put("MADC","0");
			excludedStocks.put("MADF","0");
			excludedStocks.put("MADI","0");
			excludedStocks.put("MADY","0");
			excludedStocks.put("MAHC","0");
			excludedStocks.put("MAHF","0");
			excludedStocks.put("MAHN","0");
			excludedStocks.put("MAHQ","0");
			excludedStocks.put("MAHV","0");
			excludedStocks.put("MAHY","0");
			excludedStocks.put("MALE","0");
			excludedStocks.put("MALH","0");
			excludedStocks.put("MALL","0");
			excludedStocks.put("MALQ","0");
			excludedStocks.put("MALU","0");
			excludedStocks.put("MAMS","7");
			excludedStocks.put("MDLQ","0");
			excludedStocks.put("MLPG","2");
			excludedStocks.put("MLQD","0");
			excludedStocks.put("MTB-C","1");
			excludedStocks.put("MYDA","0");
			excludedStocks.put("MYDF","0");
			excludedStocks.put("MYDH","0");
			excludedStocks.put("MYDQ","0");
			excludedStocks.put("MYHE","0");
			excludedStocks.put("MYHK","0");
			excludedStocks.put("MYHN","0");
			excludedStocks.put("MYHS","0");
			excludedStocks.put("MYHW","0");
			excludedStocks.put("MYHZ","0");
			excludedStocks.put("MYLA","0");
			excludedStocks.put("MYLO","0");
			excludedStocks.put("MYLQ","0");
			excludedStocks.put("MYLV","0");
			excludedStocks.put("MYLX","0");
			excludedStocks.put("N1HA","0");
			excludedStocks.put("N1HJ","0");
			excludedStocks.put("N1LC","0");
			excludedStocks.put("N1LH","0");
			excludedStocks.put("N5DC","0");
			excludedStocks.put("N5HC","0");
			excludedStocks.put("N5HE","0");
			excludedStocks.put("N5HI","0");
			excludedStocks.put("N5LA","0");
			excludedStocks.put("N5LB","0");
			excludedStocks.put("N5LE","0");
			excludedStocks.put("N5LJ","0");
			excludedStocks.put("NEE-J","0");
			excludedStocks.put("NEE-Q","7");
			excludedStocks.put("NGL-A","0");
			excludedStocks.put("NUEM","4");
			excludedStocks.put("NUSA","3");
			excludedStocks.put("OASI","2");
			excludedStocks.put("ONEY","0");
			excludedStocks.put("OSBCP","4");
			excludedStocks.put("OXSQL","0");
			excludedStocks.put("PPLN","3");
			excludedStocks.put("PPMC","0");
			excludedStocks.put("PSR","1");
			excludedStocks.put("PVI","2");
			excludedStocks.put("PXR","3");
			excludedStocks.put("QDEU","1");
			excludedStocks.put("QDYN","1");
			excludedStocks.put("RFDA","3");
			excludedStocks.put("RILYZ","0");
			excludedStocks.put("RPT-D","4");
			excludedStocks.put("RWG.U","0");
			excludedStocks.put("SAGG","2");
			excludedStocks.put("SCACU","0");
			excludedStocks.put("SECT","6");
			excludedStocks.put("SLMBP","6");
			excludedStocks.put("SNOAW","0");
			excludedStocks.put("SQZZ","2");
			excludedStocks.put("SSWN","0");
			excludedStocks.put("TLEH","3");
			excludedStocks.put("TTEN","5");
			excludedStocks.put("TWNKW","0");
			excludedStocks.put("TXTW","0");
			excludedStocks.put("TYBS","1");
			excludedStocks.put("TYO","6");
			excludedStocks.put("UBOH","7");
			excludedStocks.put("UHN","2");
			excludedStocks.put("USDY","0");
			excludedStocks.put("USMV","0");
			excludedStocks.put("UZC","0");
			excludedStocks.put("VFQY","1");
			excludedStocks.put("VSMV","8");
			excludedStocks.put("VZA","0");
			excludedStocks.put("WALA","0");
			excludedStocks.put("WFC.W","0");
			excludedStocks.put("WHLRW","0");
			excludedStocks.put("WRB-D","0");
			excludedStocks.put("WVFC","8");
			excludedStocks.put("XCEM","3");
			excludedStocks.put("XKFS","6");
			excludedStocks.put("XUSA","1");
			excludedStocks.put("YAO","2");
			excludedStocks.put("ADVA","0");
			excludedStocks.put("AFS-B","0");
			excludedStocks.put("AFS-C","0");
			excludedStocks.put("AGM-A","9");
			excludedStocks.put("AIG.W","0");
			excludedStocks.put("BCOM","8");
			excludedStocks.put("BOFIL","0");
			excludedStocks.put("BVAL","1");
			excludedStocks.put("CMA.W","0");
			excludedStocks.put("CNSF","0");
			excludedStocks.put("CPAC","1");
			excludedStocks.put("CSA","0");
			excludedStocks.put("CSD","1");
			excludedStocks.put("CTBB","0");
			excludedStocks.put("CTDD","0");
			excludedStocks.put("CTV","0");
			excludedStocks.put("CUO","4");
			excludedStocks.put("CWAI","1");
			excludedStocks.put("DESC","3");
			excludedStocks.put("DIVB","4");
			excludedStocks.put("DIVO","0");
			excludedStocks.put("DJCO","2");
			excludedStocks.put("DVHL","3");
			excludedStocks.put("DVYA","1");
			excludedStocks.put("DVYL","4");
			excludedStocks.put("EEMX","6");
			excludedStocks.put("EMBU","0");
			excludedStocks.put("EMDV","5");
			excludedStocks.put("EMIH","2");
			excludedStocks.put("EQWM","0");
			excludedStocks.put("EQWS","0");
			excludedStocks.put("EUXL","3");
			excludedStocks.put("EVIX","0");
			excludedStocks.put("EVSTC","1");
			excludedStocks.put("FLAX","1");
			excludedStocks.put("FLEE","1");
			excludedStocks.put("FLEU","3");
			excludedStocks.put("FLGR","0");
			excludedStocks.put("FLHK","1");
			excludedStocks.put("FLIY","2");
			excludedStocks.put("FLLV","2");
			excludedStocks.put("FLMX","1");
			excludedStocks.put("FLQL","2");
			excludedStocks.put("FLSW","2");
			excludedStocks.put("FLTW","0");
			excludedStocks.put("GRI","5");
			excludedStocks.put("GSD","1");
			excludedStocks.put("GSHT","2");
			excludedStocks.put("GSP","3");
			excludedStocks.put("GUDB","0");
			excludedStocks.put("GYC","3");
			excludedStocks.put("HBM.W","0");
			excludedStocks.put("HCAPZ","0");
			excludedStocks.put("HDG","5");
			excludedStocks.put("HEWC","0");
			excludedStocks.put("HEWP","4");
			excludedStocks.put("HGH","0");
			excludedStocks.put("IBD","1");
			excludedStocks.put("IDHQ","3");
			excludedStocks.put("IG","0");
			excludedStocks.put("IGV","3");
			excludedStocks.put("IMRNW","0");
			excludedStocks.put("IOR","5");
			excludedStocks.put("IPB","2");
			excludedStocks.put("JBK","2");
			excludedStocks.put("JHDG","1");
			excludedStocks.put("JPEH","0");
			excludedStocks.put("JPST","0");
			excludedStocks.put("KOD.W","0");
			excludedStocks.put("KOIN","1");
			excludedStocks.put("LGC.U","0");
			excludedStocks.put("LVHE","4");
			excludedStocks.put("LVHI","0");
			excludedStocks.put("LVL","1");
			excludedStocks.put("LVUS","0");
			excludedStocks.put("M3DC","0");
			excludedStocks.put("M3DF","0");
			excludedStocks.put("M3DH","0");
			excludedStocks.put("M3DK","0");
			excludedStocks.put("M3DN","0");
			excludedStocks.put("M3DU","0");
			excludedStocks.put("M3DY","0");
			excludedStocks.put("M3HC","0");
			excludedStocks.put("M3HF","0");
			excludedStocks.put("M3HH","0");
			excludedStocks.put("M3HQ","0");
			excludedStocks.put("M3HV","0");
			excludedStocks.put("M3LA","0");
			excludedStocks.put("M3LC","0");
			excludedStocks.put("M3LN","0");
			excludedStocks.put("M3LQ","0");
			excludedStocks.put("M3LU","0");
			excludedStocks.put("M3LZ","0");
			excludedStocks.put("MADG","0");
			excludedStocks.put("MADK","0");
			excludedStocks.put("MADN","0");
			excludedStocks.put("MADP","0");
			excludedStocks.put("MADU","0");
			excludedStocks.put("MADZ","0");
			excludedStocks.put("MAHA","0");
			excludedStocks.put("MAHG","0");
			excludedStocks.put("MAHI","0");
			excludedStocks.put("MAHK","0");
			excludedStocks.put("MAHO","0");
			excludedStocks.put("MALA","0");
			excludedStocks.put("MALN","0");
			excludedStocks.put("MDLX","0");
			excludedStocks.put("MLPC","0");
			excludedStocks.put("MLPY","6");
			excludedStocks.put("MTFB","5");
			excludedStocks.put("MUDSU","0");
			excludedStocks.put("MYDC","0");
			excludedStocks.put("MYDG","0");
			excludedStocks.put("MYDI","0");
			excludedStocks.put("MYDN","0");
			excludedStocks.put("MYDP","2");
			excludedStocks.put("MYDS","0");
			excludedStocks.put("MYDV","0");
			excludedStocks.put("MYDZ","0");
			excludedStocks.put("MYHC","0");
			excludedStocks.put("MYHF","0");
			excludedStocks.put("MYHH","0");
			excludedStocks.put("MYHO","0");
			excludedStocks.put("MYHP","0");
			excludedStocks.put("MYHU","0");
			excludedStocks.put("MYLC","0");
			excludedStocks.put("MYLF","0");
			excludedStocks.put("MYLH","0");
			excludedStocks.put("MYLL","0");
			excludedStocks.put("MYLN","0");
			excludedStocks.put("MYLU","0");
			excludedStocks.put("MYLY","0");
			excludedStocks.put("N1HB","0");
			excludedStocks.put("N1HE","0");
			excludedStocks.put("N1LA","0");
			excludedStocks.put("N1LI","0");
			excludedStocks.put("N5DA","0");
			excludedStocks.put("N5DI","0");
			excludedStocks.put("N5HA","0");
			excludedStocks.put("N5HH","0");
			excludedStocks.put("N5HJ","0");
			excludedStocks.put("N5LC","0");
			excludedStocks.put("N5LI","0");
			excludedStocks.put("NEE-K","0");
			excludedStocks.put("NEWTI","0");
			excludedStocks.put("NSEC","6");
			excludedStocks.put("NUBD","3");
			excludedStocks.put("NUMG","2");
			excludedStocks.put("NXTDW","0");
			excludedStocks.put("NYC-A","0");
			excludedStocks.put("NYC-U","0");
			excludedStocks.put("OAK-A","0");
			excludedStocks.put("OILBR","0");
			excludedStocks.put("OILD","0");
			excludedStocks.put("OILK","1");
			excludedStocks.put("OILSW","0");
			excludedStocks.put("ONEV","0");
			excludedStocks.put("ONTL","1");
			excludedStocks.put("OPBK","0");
			excludedStocks.put("OSLE","0");
			excludedStocks.put("PAF","1");
			excludedStocks.put("PBJ","8");
			excludedStocks.put("PILL","4");
			excludedStocks.put("PLTM","0");
			excludedStocks.put("PPIH","6");
			excludedStocks.put("PPSC","0");
			excludedStocks.put("PRH","0");
			excludedStocks.put("PULS","0");
			excludedStocks.put("PVAL","1");
			excludedStocks.put("PYT","6");
			excludedStocks.put("QCAN","3");
			excludedStocks.put("QEFA","7");
			excludedStocks.put("QQQX","0");
			excludedStocks.put("QXGG","0");
			excludedStocks.put("QXMI","0");
			excludedStocks.put("RFCI","0");
			excludedStocks.put("RFFC","2");
			excludedStocks.put("RFTA","0");
			excludedStocks.put("RILYL","0");
			excludedStocks.put("ROGS","10");
			excludedStocks.put("RVRS","2");
			excludedStocks.put("SCE-B","2");
			excludedStocks.put("SDVY","5");
			excludedStocks.put("SFHY","0");
			excludedStocks.put("SIZE","4");
			excludedStocks.put("SMCP","10");
			excludedStocks.put("SNHNI","0");
			excludedStocks.put("SNHNL","0");
			excludedStocks.put("SOHOK","0");
			excludedStocks.put("SSNI","0");
			excludedStocks.put("STI.A","0");
			excludedStocks.put("STNLU","0");
			excludedStocks.put("TDW.A","0");
			excludedStocks.put("THGA","0");
			excludedStocks.put("UNL","2");
			excludedStocks.put("UTLF","6");
			excludedStocks.put("UZB","0");
			excludedStocks.put("VALQ","0");
			excludedStocks.put("VALX","3");
			excludedStocks.put("VBND","2");
			excludedStocks.put("VFLQ","0");
			excludedStocks.put("VFVA","4");
			excludedStocks.put("WEL-I","0");
			excludedStocks.put("WSCWW","0");
			excludedStocks.put("XKII","1");
			excludedStocks.put("ACIM","2");
			excludedStocks.put("ACV","0");
			excludedStocks.put("AEK","0");
			excludedStocks.put("AGGY","8");
			excludedStocks.put("AIZP","19");
			excludedStocks.put("BAC-E","24");
			excludedStocks.put("BCI","23");
			excludedStocks.put("BGH","0");
			excludedStocks.put("BLNKW","0");
			excludedStocks.put("BRF","21");
			excludedStocks.put("BSCM","19");
			excludedStocks.put("BSJI","19");
			excludedStocks.put("BWZ","5");
			excludedStocks.put("C.A","0");
			excludedStocks.put("CHIQ","18");
			excludedStocks.put("CLN-B","0");
			excludedStocks.put("CLN-I","0");
			excludedStocks.put("CMR-C","0");
			excludedStocks.put("CMSA","0");
			excludedStocks.put("COPX","16");
			excludedStocks.put("CORP","12");
			excludedStocks.put("CRAK","21");
			excludedStocks.put("CRBN","6");
			excludedStocks.put("CSM","4");
			excludedStocks.put("CTO","24");
			excludedStocks.put("CVCY","24");
			excludedStocks.put("DBB","22");
			excludedStocks.put("DBE","18");
			excludedStocks.put("DBP","9");
			excludedStocks.put("DDWM","8");
			excludedStocks.put("DHS","20");
			excludedStocks.put("DLR-G","29");
			excludedStocks.put("DLR-I","24");
			excludedStocks.put("DLS","21");
			excludedStocks.put("DOL","6");
			excludedStocks.put("DSAF","0");
			excludedStocks.put("DSAI","0");
			excludedStocks.put("DSAL","0");
			excludedStocks.put("DSAP","0");
			excludedStocks.put("DSAS","0");
			excludedStocks.put("DSBD","0");
			excludedStocks.put("DSBK","0");
			excludedStocks.put("DSBM","0");
			excludedStocks.put("DSCA","0");
			excludedStocks.put("DSCF","0");
			excludedStocks.put("DSCL","0");
			excludedStocks.put("DSCP","0");
			excludedStocks.put("DSCS","0");
			excludedStocks.put("DSCX","0");
			excludedStocks.put("DSDB","0");
			excludedStocks.put("DSDR","0");
			excludedStocks.put("DSES","0");
			excludedStocks.put("DSEU","0");
			excludedStocks.put("DSFB","0");
			excludedStocks.put("DSFD","0");
			excludedStocks.put("DSFO","0");
			excludedStocks.put("DSGU","0");
			excludedStocks.put("DSHC","0");
			excludedStocks.put("DSHG","0");
			excludedStocks.put("DSHL","0");
			excludedStocks.put("DSHR","0");
			excludedStocks.put("DSIM","0");
			excludedStocks.put("DSIR","0");
			excludedStocks.put("DSIU","0");
			excludedStocks.put("DSLE","0");
			excludedStocks.put("DSME","0");
			excludedStocks.put("DSMT","0");
			excludedStocks.put("DSNC","0");
			excludedStocks.put("DSPC","0");
			excludedStocks.put("DSPM","0");
			excludedStocks.put("DSPP","0");
			excludedStocks.put("DSRB","0");
			excludedStocks.put("DSRE","0");
			excludedStocks.put("DSRL","0");
			excludedStocks.put("DSRN","0");
			excludedStocks.put("DSRS","0");
			excludedStocks.put("DSSB","0");
			excludedStocks.put("DSSR","0");
			excludedStocks.put("DSSW","0");
			excludedStocks.put("DSTB","0");
			excludedStocks.put("DSTY","0");
			excludedStocks.put("DSUT","0");
			excludedStocks.put("DTD","7");
			excludedStocks.put("DWM","10");
			excludedStocks.put("DXGE","21");
			excludedStocks.put("EBAYL","0");
			excludedStocks.put("EDEN","16");
			excludedStocks.put("EET","8");
			excludedStocks.put("EFG","25");
			excludedStocks.put("ELECW","0");
			excludedStocks.put("EMGF","18");
			excludedStocks.put("ENBA","0");
			excludedStocks.put("EPS","2");
			excludedStocks.put("EUSC","17");
			excludedStocks.put("EWO","20");
			excludedStocks.put("FAN","11");
			excludedStocks.put("FBGX","2");
			excludedStocks.put("FDMO","12");
			excludedStocks.put("FDRR","10");
			excludedStocks.put("FEEU","12");
			excludedStocks.put("FIGY","5");
			excludedStocks.put("FINU","18");
			excludedStocks.put("FLOT","21");
			excludedStocks.put("FNDB","11");
			excludedStocks.put("FNDX","28");
			excludedStocks.put("FNI","9");
			excludedStocks.put("FOX","0");
			excludedStocks.put("FQAL","6");
			excludedStocks.put("FRAK","11");
			excludedStocks.put("FRI","16");
			excludedStocks.put("FRN","20");
			excludedStocks.put("FSTA","21");
			excludedStocks.put("FVAL","13");
			excludedStocks.put("FXC","24");
			excludedStocks.put("FXF","11");
			excludedStocks.put("FYT","22");
			excludedStocks.put("GAA","0");
			excludedStocks.put("GAMR","12");
			excludedStocks.put("GIG","5");
			excludedStocks.put("GLL","19");
			excludedStocks.put("GLTR","15");
			excludedStocks.put("GOVT","0");
			excludedStocks.put("GRIF","13");
			excludedStocks.put("GVAL","15");
			excludedStocks.put("HCA.W","0");
			excludedStocks.put("HUSE","8");
			excludedStocks.put("HYACW","0");
			excludedStocks.put("HYMB","23");
			excludedStocks.put("HYXU","8");
			excludedStocks.put("IBDM","14");
			excludedStocks.put("IBND","15");
			excludedStocks.put("ICSH","2");
			excludedStocks.put("IDLV","12");
			excludedStocks.put("IHE","16");
			excludedStocks.put("IHY","9");
			excludedStocks.put("IPAC","11");
			excludedStocks.put("ITE","12");
			excludedStocks.put("IVAL","15");
			excludedStocks.put("IVLU","9");
			excludedStocks.put("IVOG","20");
			excludedStocks.put("IYJ","19");
			excludedStocks.put("IYLD","18");
			excludedStocks.put("IYY","23");
			excludedStocks.put("JETS","10");
			excludedStocks.put("JHMT","5");
			excludedStocks.put("JHSC","4");
			excludedStocks.put("JKE","27");
			excludedStocks.put("JKG","8");
			excludedStocks.put("JPHY","3");
			excludedStocks.put("JPNL","5");
			excludedStocks.put("KBWR","27");
			excludedStocks.put("LQDH","8");
			excludedStocks.put("M1DC","0");
			excludedStocks.put("M1DE","0");
			excludedStocks.put("M1DH","0");
			excludedStocks.put("M1DZ","0");
			excludedStocks.put("M1HA","0");
			excludedStocks.put("M1HE","0");
			excludedStocks.put("M1HL","0");
			excludedStocks.put("M1HQ","0");
			excludedStocks.put("M1HV","0");
			excludedStocks.put("M1LE","0");
			excludedStocks.put("M1LL","0");
			excludedStocks.put("M1LU","0");
			excludedStocks.put("M5DC","0");
			excludedStocks.put("M5DZ","0");
			excludedStocks.put("M5HH","0");
			excludedStocks.put("M5HL","0");
			excludedStocks.put("M5HO","0");
			excludedStocks.put("M5HQ","0");
			excludedStocks.put("M5HV","0");
			excludedStocks.put("M5LE","0");
			excludedStocks.put("M5LG","0");
			excludedStocks.put("M5LN","0");
			excludedStocks.put("M5LQ","0");
			excludedStocks.put("M5LV","0");
			excludedStocks.put("M5LZ","0");
			excludedStocks.put("M6DC","0");
			excludedStocks.put("M6DH","0");
			excludedStocks.put("M6DQ","0");
			excludedStocks.put("M6DX","0");
			excludedStocks.put("M6HG","0");
			excludedStocks.put("M6HL","0");
			excludedStocks.put("M6HQ","0");
			excludedStocks.put("M6HV","0");
			excludedStocks.put("M6HX","0");
			excludedStocks.put("M6LG","0");
			excludedStocks.put("M6LL","0");
			excludedStocks.put("M6LV","0");
			excludedStocks.put("M6LZ","0");
			excludedStocks.put("MGIC","22");
			excludedStocks.put("MINC","4");
			excludedStocks.put("MLN","10");
			excludedStocks.put("MLPQ","12");
			excludedStocks.put("MMDMR","0");
			excludedStocks.put("MMDMW","0");
			excludedStocks.put("MNA","10");
			excludedStocks.put("MVV","16");
			excludedStocks.put("MXI","6");
			excludedStocks.put("N3DA","0");
			excludedStocks.put("N3HB","0");
			excludedStocks.put("N3LB","0");
			excludedStocks.put("N6DA","0");
			excludedStocks.put("N6HB","0");
			excludedStocks.put("NADB","0");
			excludedStocks.put("NAHB","0");
			excludedStocks.put("NALA","0");
			excludedStocks.put("NFRA","22");
			excludedStocks.put("NYDB","0");
			excludedStocks.put("NYHA","0");
			excludedStocks.put("OEUR","17");
			excludedStocks.put("PALL","11");
			excludedStocks.put("PBBI","12");
			excludedStocks.put("PBP","9");
			excludedStocks.put("PFIG","0");
			excludedStocks.put("PGHY","17");
			excludedStocks.put("PKB","22");
			excludedStocks.put("PLW","20");
			excludedStocks.put("PPTY","10");
			excludedStocks.put("PSTG","0");
			excludedStocks.put("PTMC","14");
			excludedStocks.put("PTNQ","19");
			excludedStocks.put("PUTW","7");
			excludedStocks.put("PXLG","5");
			excludedStocks.put("PXMG","8");
			excludedStocks.put("PZA","25");
			excludedStocks.put("QQQE","12");
			excludedStocks.put("QUAL","22");
			excludedStocks.put("QVAL","6");
			excludedStocks.put("RGI","10");
			excludedStocks.put("RISE","8");
			excludedStocks.put("RLY","5");
			excludedStocks.put("RODM","24");
			excludedStocks.put("ROOF","17");
			excludedStocks.put("RRR","0");
			excludedStocks.put("RWGE","9");
			excludedStocks.put("RWJ","10");
			excludedStocks.put("RYF","26");
			excludedStocks.put("RYU","6");
			excludedStocks.put("SCIF","23");
			excludedStocks.put("SCJ","27");
			excludedStocks.put("SIJ","5");
			excludedStocks.put("SKF","24");
			excludedStocks.put("SLYG","20");
			excludedStocks.put("SOJB","0");
			excludedStocks.put("SP500","0");
			excludedStocks.put("SPTS","7");
			excludedStocks.put("SPYX","6");
			excludedStocks.put("SUSA","10");
			excludedStocks.put("TAO","7");
			excludedStocks.put("TIBRW","0");
			excludedStocks.put("TIPX","5");
			excludedStocks.put("TLH","14");
			excludedStocks.put("TLTE","9");
			excludedStocks.put("TMFC","16");
			excludedStocks.put("TPG.F","0");
			excludedStocks.put("TPG.I","0");
			excludedStocks.put("TTCS","0");
			excludedStocks.put("TWN","22");
			excludedStocks.put("TXEW","0");
			excludedStocks.put("TXFO","0");
			excludedStocks.put("UEVM","16");
			excludedStocks.put("UGA","20");
			excludedStocks.put("UIVM","21");
			excludedStocks.put("UNCA","0");
			excludedStocks.put("UNCE","0");
			excludedStocks.put("UNCQ","0");
			excludedStocks.put("UNCS","0");
			excludedStocks.put("UNCV","0");
			excludedStocks.put("URE","27");
			excludedStocks.put("USCI","29");
			excludedStocks.put("UTSL","19");
			excludedStocks.put("VEGI","3");
			excludedStocks.put("VIOG","8");
			excludedStocks.put("VIXM","22");
			excludedStocks.put("VNLA","19");
			excludedStocks.put("VNO-K","27");
			excludedStocks.put("WHLRP","26");
			excludedStocks.put("WPG-H","29");
			excludedStocks.put("XNTK","15");
			excludedStocks.put("XRLV","8");
			excludedStocks.put("XTN","17");
			excludedStocks.put("ZSL","17");
			excludedStocks.put("AADR","20");
			excludedStocks.put("AHT-H","18");
			excludedStocks.put("AIY","7");
			excludedStocks.put("AOA","19");
			excludedStocks.put("AOK","10");
			excludedStocks.put("ARKQ","14");
			excludedStocks.put("BAC.A","0");
			excludedStocks.put("BFZ","0");
			excludedStocks.put("BIL","25");
			excludedStocks.put("BIZD","20");
			excludedStocks.put("BLV","25");
			excludedStocks.put("BPRN","28");
			excludedStocks.put("BSCL","14");
			excludedStocks.put("BSCN","11");
			excludedStocks.put("BSCP","3");
			excludedStocks.put("CANE","17");
			excludedStocks.put("CHSCO","25");
			excludedStocks.put("CLN-E","0");
			excludedStocks.put("CLY","7");
			excludedStocks.put("CMR-E","0");
			excludedStocks.put("DGRE","29");
			excludedStocks.put("DJI","23");
			excludedStocks.put("DRW","10");
			excludedStocks.put("DSAE","0");
			excludedStocks.put("DSAG","0");
			excludedStocks.put("DSAU","0");
			excludedStocks.put("DSBE","0");
			excludedStocks.put("DSBT","0");
			excludedStocks.put("DSCC","0");
			excludedStocks.put("DSCH","0");
			excludedStocks.put("DSCM","0");
			excludedStocks.put("DSCY","0");
			excludedStocks.put("DSDS","0");
			excludedStocks.put("DSDV","0");
			excludedStocks.put("DSEC","0");
			excludedStocks.put("DSEN","0");
			excludedStocks.put("DSFE","0");
			excludedStocks.put("DSFH","0");
			excludedStocks.put("DSFP","0");
			excludedStocks.put("DSFT","0");
			excludedStocks.put("DSGF","0");
			excludedStocks.put("DSHN","0");
			excludedStocks.put("DSHV","0");
			excludedStocks.put("DSID","0");
			excludedStocks.put("DSIG","0");
			excludedStocks.put("DSIN","0");
			excludedStocks.put("DSIP","0");
			excludedStocks.put("DSIS","0");
			excludedStocks.put("DSIX","0");
			excludedStocks.put("DSKEW","0");
			excludedStocks.put("DSLG","0");
			excludedStocks.put("DSMF","0");
			excludedStocks.put("DSMR","0");
			excludedStocks.put("DSNS","0");
			excludedStocks.put("DSOG","0");
			excludedStocks.put("DSOQ","0");
			excludedStocks.put("DSPN","0");
			excludedStocks.put("DSRH","0");
			excludedStocks.put("DSRQ","0");
			excludedStocks.put("DSRU","0");
			excludedStocks.put("DSSD","0");
			excludedStocks.put("DSSP","0");
			excludedStocks.put("DSTC","0");
			excludedStocks.put("DSTK","0");
			excludedStocks.put("DSTL","0");
			excludedStocks.put("DSTQ","0");
			excludedStocks.put("DSTS","0");
			excludedStocks.put("DSUO","0");
			excludedStocks.put("DSVN","0");
			excludedStocks.put("DSWC","0");
			excludedStocks.put("DTO","17");
			excludedStocks.put("DUKH","0");
			excludedStocks.put("DYLS","5");
			excludedStocks.put("EDOG","15");
			excludedStocks.put("EES","17");
			excludedStocks.put("EMHY","26");
			excludedStocks.put("EPR-C","20");
			excludedStocks.put("EPR-E","15");
			excludedStocks.put("EURL","13");
			excludedStocks.put("EWX","28");
			excludedStocks.put("EZM","19");
			excludedStocks.put("FBND","24");
			excludedStocks.put("FCOM","16");
			excludedStocks.put("FDVV","8");
			excludedStocks.put("FFEU","11");
			excludedStocks.put("FIW","13");
			excludedStocks.put("FIYY","10");
			excludedStocks.put("FLL","0");
			excludedStocks.put("FLTR","16");
			excludedStocks.put("FNDC","6");
			excludedStocks.put("FNDF","28");
			excludedStocks.put("FTSD","4");
			excludedStocks.put("FXG","22");
			excludedStocks.put("FXR","29");
			excludedStocks.put("GBIL","28");
			excludedStocks.put("GCOW","8");
			excludedStocks.put("GMF","13");
			excludedStocks.put("GMTA","0");
			excludedStocks.put("GRID","11");
			excludedStocks.put("GSY","26");
			excludedStocks.put("GWX","18");
			excludedStocks.put("HAO","23");
			excludedStocks.put("HEEM","19");
			excludedStocks.put("HLTH","0");
			excludedStocks.put("HYAC","10");
			excludedStocks.put("IAGG","13");
			excludedStocks.put("IAK","5");
			excludedStocks.put("IBDH","16");
			excludedStocks.put("IBDP","6");
			excludedStocks.put("IBMG","8");
			excludedStocks.put("IBMJ","0");
			excludedStocks.put("ICOW","3");
			excludedStocks.put("IHDG","11");
			excludedStocks.put("IHF","23");
			excludedStocks.put("IHTA","27");
			excludedStocks.put("IJJ","27");
			excludedStocks.put("INCO","7");
			excludedStocks.put("INDL","26");
			excludedStocks.put("INTF","26");
			excludedStocks.put("IQDE","14");
			excludedStocks.put("IQLT","5");
			excludedStocks.put("IVOV","9");
			excludedStocks.put("IWC","24");
			excludedStocks.put("JHML","3");
			excludedStocks.put("JKF","5");
			excludedStocks.put("JKI","20");
			excludedStocks.put("JTPY","21");
			excludedStocks.put("JXI","3");
			excludedStocks.put("KXI","14");
			excludedStocks.put("LDUR","7");
			excludedStocks.put("LGC","5");
			excludedStocks.put("M1DQ","0");
			excludedStocks.put("M1DV","0");
			excludedStocks.put("M1HC","0");
			excludedStocks.put("M1HG","0");
			excludedStocks.put("M1HN","0");
			excludedStocks.put("M1HU","0");
			excludedStocks.put("M1HX","0");
			excludedStocks.put("M1LA","0");
			excludedStocks.put("M1LG","0");
			excludedStocks.put("M1LN","0");
			excludedStocks.put("M1LV","0");
			excludedStocks.put("M5DE","0");
			excludedStocks.put("M5DH","0");
			excludedStocks.put("M5DO","0");
			excludedStocks.put("M5DX","0");
			excludedStocks.put("M5HA","0");
			excludedStocks.put("M5HE","0");
			excludedStocks.put("M5HK","0");
			excludedStocks.put("M5HP","0");
			excludedStocks.put("M5HX","0");
			excludedStocks.put("M5LH","0");
			excludedStocks.put("M5LP","0");
			excludedStocks.put("M5LX","0");
			excludedStocks.put("M6DE","0");
			excludedStocks.put("M6DU","0");
			excludedStocks.put("M6HC","0");
			excludedStocks.put("M6HH","0");
			excludedStocks.put("M6HN","0");
			excludedStocks.put("M6HZ","0");
			excludedStocks.put("M6LH","0");
			excludedStocks.put("M6LP","0");
			excludedStocks.put("M6LU","0");
			excludedStocks.put("MBG","7");
			excludedStocks.put("MDYG","20");
			excludedStocks.put("MDYV","21");
			excludedStocks.put("MKC.V","18");
			excludedStocks.put("MOTI","4");
			excludedStocks.put("MTP","0");
			excludedStocks.put("N1DB","0");
			excludedStocks.put("N1DH","0");
			excludedStocks.put("N3DB","0");
			excludedStocks.put("N3LJ","0");
			excludedStocks.put("N6DB","0");
			excludedStocks.put("N6LA","0");
			excludedStocks.put("NADA","0");
			excludedStocks.put("NEE-R","13");
			excludedStocks.put("NSS","0");
			excludedStocks.put("NUSC","10");
			excludedStocks.put("NWLI","17");
			excludedStocks.put("NXR","29");
			excludedStocks.put("PBE","10");
			excludedStocks.put("PCEF","29");
			excludedStocks.put("PEB-C","5");
			excludedStocks.put("PIO","26");
			excludedStocks.put("PRIM","0");
			excludedStocks.put("PSCC","11");
			excludedStocks.put("PSCF","27");
			excludedStocks.put("PSJ","20");
			excludedStocks.put("PSP","16");
			excludedStocks.put("PTEU","6");
			excludedStocks.put("PWZ","7");
			excludedStocks.put("PXE","12");
			excludedStocks.put("PXJ","12");
			excludedStocks.put("PXLV","3");
			excludedStocks.put("QAI","14");
			excludedStocks.put("QLTA","10");
			excludedStocks.put("QTS-A","24");
			excludedStocks.put("REGL","11");
			excludedStocks.put("REZ","17");
			excludedStocks.put("RFEU","21");
			excludedStocks.put("RIGS","0");
			excludedStocks.put("RJI","17");
			excludedStocks.put("RWK","10");
			excludedStocks.put("RXI","6");
			excludedStocks.put("RYH","17");
			excludedStocks.put("RZB","0");
			excludedStocks.put("RZG","3");
			excludedStocks.put("SBIO","18");
			excludedStocks.put("SCH-C","0");
			excludedStocks.put("SCH-D","0");
			excludedStocks.put("SDCI","6");
			excludedStocks.put("SGLB","28");
			excludedStocks.put("SJIU","4");
			excludedStocks.put("SLY","18");
			excludedStocks.put("SMLF","6");
			excludedStocks.put("SPFF","10");
			excludedStocks.put("SPMD","0");
			excludedStocks.put("STNC","0");
			excludedStocks.put("STNU","0");
			excludedStocks.put("SUB","21");
			excludedStocks.put("SWJ","0");
			excludedStocks.put("SWP","12");
			excludedStocks.put("TBB","0");
			excludedStocks.put("TBX","16");
			excludedStocks.put("TLTD","8");
			excludedStocks.put("TOOC","0");
			excludedStocks.put("TOTL","25");
			excludedStocks.put("TXDC","0");
			excludedStocks.put("UAVS","0");
			excludedStocks.put("UGL","9");
			excludedStocks.put("UMDD","8");
			excludedStocks.put("UNCN","0");
			excludedStocks.put("UNCT","0");
			excludedStocks.put("UNCX","0");
			excludedStocks.put("URTH","7");
			excludedStocks.put("USFR","18");
			excludedStocks.put("USL","14");
			excludedStocks.put("VFMF","5");
			excludedStocks.put("WBIH","5");
			excludedStocks.put("WFC-L","7");
			excludedStocks.put("WPG-I","21");
			excludedStocks.put("WPXP","12");
			excludedStocks.put("WRLS","5");
			excludedStocks.put("WTIU","23");
			excludedStocks.put("XHE","17");
			excludedStocks.put("XHS","14");
			excludedStocks.put("XLG","15");
			excludedStocks.put("XMLV","13");
			excludedStocks.put("XSLV","27");
			excludedStocks.put("XWEB","4");
			excludedStocks.put("YYY","13");
			excludedStocks.put("ZROZ","9");
			excludedStocks.put("AGGP","4");
			excludedStocks.put("AGND","7");
			excludedStocks.put("AGZ","6");
			excludedStocks.put("ALX","16");
			excludedStocks.put("AMNB","29");
			excludedStocks.put("AOM","23");
			excludedStocks.put("BAB","25");
			excludedStocks.put("BAC-Y","0");
			excludedStocks.put("BAC.B","0");
			excludedStocks.put("BDCL","19");
			excludedStocks.put("BDJ","0");
			excludedStocks.put("BFOR","7");
			excludedStocks.put("BJO","12");
			excludedStocks.put("BKHU","4");
			excludedStocks.put("BSCI","22");
			excludedStocks.put("BSCO","11");
			excludedStocks.put("BSJJ","25");
			excludedStocks.put("BSJM","8");
			excludedStocks.put("CATH","21");
			excludedStocks.put("CGW","23");
			excludedStocks.put("CIC","0");
			excludedStocks.put("CMBS","8");
			excludedStocks.put("CMF","20");
			excludedStocks.put("CMSS","3");
			excludedStocks.put("CPR","27");
			excludedStocks.put("DBAW","8");
			excludedStocks.put("DBJP","26");
			excludedStocks.put("DD-B","10");
			excludedStocks.put("DECN","17");
			excludedStocks.put("DIVY","4");
			excludedStocks.put("DPST","12");
			excludedStocks.put("DSAM","0");
			excludedStocks.put("DSAR","0");
			excludedStocks.put("DSAT","0");
			excludedStocks.put("DSAV","0");
			excludedStocks.put("DSBC","0");
			excludedStocks.put("DSBS","0");
			excludedStocks.put("DSBV","0");
			excludedStocks.put("DSCG","0");
			excludedStocks.put("DSCN","0");
			excludedStocks.put("DSCR","11");
			excludedStocks.put("DSDN","0");
			excludedStocks.put("DSDT","0");
			excludedStocks.put("DSEE","0");
			excludedStocks.put("DSEH","0");
			excludedStocks.put("DSFA","0");
			excludedStocks.put("DSFC","0");
			excludedStocks.put("DSFN","2");
			excludedStocks.put("DSFR","0");
			excludedStocks.put("DSGI","0");
			excludedStocks.put("DSHB","0");
			excludedStocks.put("DSHD","0");
			excludedStocks.put("DSHI","0");
			excludedStocks.put("DSHP","0");
			excludedStocks.put("DSIB","0");
			excludedStocks.put("DSIF","0");
			excludedStocks.put("DSIL","0");
			excludedStocks.put("DSIO","0");
			excludedStocks.put("DSIQ","0");
			excludedStocks.put("DSIT","0");
			excludedStocks.put("DSIV","0");
			excludedStocks.put("DSMC","0");
			excludedStocks.put("DSMG","0");
			excludedStocks.put("DSMS","0");
			excludedStocks.put("DSMU","0");
			excludedStocks.put("DSNF","0");
			excludedStocks.put("DSOE","0");
			excludedStocks.put("DSOI","0");
			excludedStocks.put("DSOS","0");
			excludedStocks.put("DSPB","0");
			excludedStocks.put("DSPL","0");
			excludedStocks.put("DSPR","0");
			excludedStocks.put("DSRA","0");
			excludedStocks.put("DSRD","0");
			excludedStocks.put("DSRI","0");
			excludedStocks.put("DSRP","0");
			excludedStocks.put("DSRR","0");
			excludedStocks.put("DSRT","0");
			excludedStocks.put("DSSC","0");
			excludedStocks.put("DSSF","0");
			excludedStocks.put("DSST","0");
			excludedStocks.put("DSSV","0");
			excludedStocks.put("DSTR","0");
			excludedStocks.put("DSTT","0");
			excludedStocks.put("DSUM","22");
			excludedStocks.put("DSVE","0");
			excludedStocks.put("DSWU","0");
			excludedStocks.put("DTN","12");
			excludedStocks.put("DUC","29");
			excludedStocks.put("DYNC","9");
			excludedStocks.put("EBND","29");
			excludedStocks.put("ECCX","0");
			excludedStocks.put("EDIV","26");
			excludedStocks.put("EDV","23");
			excludedStocks.put("EEMS","12");
			excludedStocks.put("EGPT","24");
			excludedStocks.put("ESGU","28");
			excludedStocks.put("EUMV","5");
			excludedStocks.put("EUSA","5");
			excludedStocks.put("EWK","0");
			excludedStocks.put("FFSG","1");
			excludedStocks.put("FFTI","7");
			excludedStocks.put("FGBI","27");
			excludedStocks.put("FGD","22");
			excludedStocks.put("FIBR","3");
			excludedStocks.put("FIHD","8");
			excludedStocks.put("FNDA","27");
			excludedStocks.put("FNGD","20");
			excludedStocks.put("FRLG","15");
			excludedStocks.put("FRPH","26");
			excludedStocks.put("FSFG","17");
			excludedStocks.put("FXA","18");
			excludedStocks.put("FXU","23");
			excludedStocks.put("GII","10");
			excludedStocks.put("GLADN","8");
			excludedStocks.put("GLD","0");
			excludedStocks.put("GLIBA","0");
			excludedStocks.put("GLO-B","0");
			excludedStocks.put("GPJA","0");
			excludedStocks.put("GPT-A","9");
			excludedStocks.put("GQRE","18");
			excludedStocks.put("GRES","12");
			excludedStocks.put("GSIE","14");
			excludedStocks.put("GVI","22");
			excludedStocks.put("GVIP","1");
			excludedStocks.put("HSPX","2");
			excludedStocks.put("HTUS","12");
			excludedStocks.put("HYACU","0");
			excludedStocks.put("HYGH","3");
			excludedStocks.put("HYLD","10");
			excludedStocks.put("IAM","13");
			excludedStocks.put("IBDC","6");
			excludedStocks.put("IBDK","26");
			excludedStocks.put("IBDL","14");
			excludedStocks.put("IBDN","20");
			excludedStocks.put("IBDO","13");
			excludedStocks.put("IBDQ","14");
			excludedStocks.put("IBMH","7");
			excludedStocks.put("IBMK","4");
			excludedStocks.put("ICVT","27");
			excludedStocks.put("IDOG","9");
			excludedStocks.put("IDSY","27");
			excludedStocks.put("IGE","13");
			excludedStocks.put("IGHG","13");
			excludedStocks.put("ILTB","7");
			excludedStocks.put("INFU","20");
			excludedStocks.put("IOO","10");
			excludedStocks.put("IQDG","24");
			excludedStocks.put("ITM","22");
			excludedStocks.put("IWL","3");
			excludedStocks.put("IWX","5");
			excludedStocks.put("IXG","5");
			excludedStocks.put("IXP","10");
			excludedStocks.put("JHMM","13");
			excludedStocks.put("JKD","24");
			excludedStocks.put("JKK","13");
			excludedStocks.put("JPEM","14");
			excludedStocks.put("JPMV","3");
			excludedStocks.put("JPSE","4");
			excludedStocks.put("JPUS","11");
			excludedStocks.put("KAAC","2");
			excludedStocks.put("KCE","5");
			excludedStocks.put("KNOW","4");
			excludedStocks.put("LDRS","12");
			excludedStocks.put("LGLV","9");
			excludedStocks.put("LKOR","28");
			excludedStocks.put("LOWC","3");
			excludedStocks.put("LTPZ","8");
			excludedStocks.put("M1DG","0");
			excludedStocks.put("M1DN","0");
			excludedStocks.put("M1DP","0");
			excludedStocks.put("M1DU","0");
			excludedStocks.put("M1DX","0");
			excludedStocks.put("M1HH","0");
			excludedStocks.put("M1HP","0");
			excludedStocks.put("M1HZ","0");
			excludedStocks.put("M1LC","0");
			excludedStocks.put("M1LH","0");
			excludedStocks.put("M1LP","0");
			excludedStocks.put("M1LQ","0");
			excludedStocks.put("M1LX","0");
			excludedStocks.put("M1LZ","0");
			excludedStocks.put("M5DG","0");
			excludedStocks.put("M5DN","0");
			excludedStocks.put("M5DP","0");
			excludedStocks.put("M5DQ","0");
			excludedStocks.put("M5DV","0");
			excludedStocks.put("M5HC","0");
			excludedStocks.put("M5HG","0");
			excludedStocks.put("M5HN","0");
			excludedStocks.put("M5HU","0");
			excludedStocks.put("M5HZ","0");
			excludedStocks.put("M5LA","0");
			excludedStocks.put("M5LC","0");
			excludedStocks.put("M5LL","0");
			excludedStocks.put("M5LU","0");
			excludedStocks.put("M6DG","0");
			excludedStocks.put("M6DN","0");
			excludedStocks.put("M6DP","0");
			excludedStocks.put("M6DV","0");
			excludedStocks.put("M6DZ","0");
			excludedStocks.put("M6HE","0");
			excludedStocks.put("M6HP","0");
			excludedStocks.put("M6HU","0");
			excludedStocks.put("M6LC","0");
			excludedStocks.put("M6LE","0");
			excludedStocks.put("M6LN","0");
			excludedStocks.put("M6LQ","0");
			excludedStocks.put("M6LX","0");
			excludedStocks.put("MLAB","25");
			excludedStocks.put("MORT","14");
			excludedStocks.put("MOS.U","0");
			excludedStocks.put("MTGEP","20");
			excludedStocks.put("MUNI","4");
			excludedStocks.put("MYSZ","29");
			excludedStocks.put("N1DA","0");
			excludedStocks.put("N3DH","0");
			excludedStocks.put("N3HA","0");
			excludedStocks.put("N3HH","0");
			excludedStocks.put("N3LA","0");
			excludedStocks.put("N6HA","0");
			excludedStocks.put("N6HH","0");
			excludedStocks.put("NAHA","0");
			excludedStocks.put("NANR","11");
			excludedStocks.put("NKSH","17");
			excludedStocks.put("NXEOW","0");
			excludedStocks.put("NYDA","0");
			excludedStocks.put("NYF","8");
			excludedStocks.put("NYHB","0");
			excludedStocks.put("NYHH","0");
			excludedStocks.put("NYL.A","0");
			excludedStocks.put("NYLA","0");
			excludedStocks.put("OMAD","2");
			excludedStocks.put("OMFL","3");
			excludedStocks.put("OUSM","7");
			excludedStocks.put("PEBK","19");
			excludedStocks.put("PFXF","23");
			excludedStocks.put("PSK","21");
			excludedStocks.put("PTF","23");
			excludedStocks.put("PTLC","0");
			excludedStocks.put("PWB","12");
			excludedStocks.put("PXF","19");
			excludedStocks.put("PXSG","2");
			excludedStocks.put("QEMM","1");
			excludedStocks.put("RAVI","7");
			excludedStocks.put("RBCAA","18");
			excludedStocks.put("REET","25");
			excludedStocks.put("RFAP","19");
			excludedStocks.put("RFG","6");
			excludedStocks.put("RWL","15");
			excludedStocks.put("RXN-A","6");
			excludedStocks.put("RYE","21");
			excludedStocks.put("RZA","0");
			excludedStocks.put("SBUX","0");
			excludedStocks.put("SCHR","18");
			excludedStocks.put("SEA","10");
			excludedStocks.put("SEF","14");
			excludedStocks.put("SGDM","14");
			excludedStocks.put("SGOL","8");
			excludedStocks.put("SHYD","5");
			excludedStocks.put("SILJ","24");
			excludedStocks.put("SKOR","26");
			excludedStocks.put("SLDD","0");
			excludedStocks.put("SMDV","16");
			excludedStocks.put("SMLV","3");
			excludedStocks.put("SOJA","0");
			excludedStocks.put("SOJC","0");
			excludedStocks.put("SPDN","14");
			excludedStocks.put("SPL-A","0");
			excludedStocks.put("STIP","24");
			excludedStocks.put("TANNI","0");
			excludedStocks.put("TANNL","0");
			excludedStocks.put("TDTF","22");
			excludedStocks.put("TDTT","22");
			excludedStocks.put("TFLO","17");
			excludedStocks.put("TTAC","7");
			excludedStocks.put("TXCX","0");
			excludedStocks.put("TXEI","0");
			excludedStocks.put("UDN","7");
			excludedStocks.put("ULVM","2");
			excludedStocks.put("USDU","16");
			excludedStocks.put("USEQ","3");
			excludedStocks.put("USOU","10");
			excludedStocks.put("VIDI","2");
			excludedStocks.put("VIOV","8");
			excludedStocks.put("VOOV","11");
			excludedStocks.put("VRP","22");
			excludedStocks.put("VTHR","19");
			excludedStocks.put("VTRB","0");
			excludedStocks.put("VTWV","14");
			excludedStocks.put("VUSE","5");
			excludedStocks.put("WINA","22");
			excludedStocks.put("XITK","7");
			excludedStocks.put("XSOE","21");
			excludedStocks.put("XSW","5");
			
			excludedStocks.put("AADR","<0.4");
			excludedStocks.put("AAMC","<0.4");
			excludedStocks.put("ABAC","<0.4");
			excludedStocks.put("ABE","<0.4");
			excludedStocks.put("ABIL","<0.4");
			excludedStocks.put("ABLX","<0.4");
			excludedStocks.put("ABMD","<0.4");
			excludedStocks.put("ABR-B","<0.4");
			excludedStocks.put("ABR-C","<0.4");
			excludedStocks.put("ABUS","<0.4");
			excludedStocks.put("AC","<0.4");
			excludedStocks.put("ACER","<0.4");
			excludedStocks.put("ACFC","<0.4");
			excludedStocks.put("ACGLO","<0.4");
			excludedStocks.put("ACHV","<0.4");
			excludedStocks.put("ACIM","<0.4");
			excludedStocks.put("ACIU","<0.4");
			excludedStocks.put("ACMR","<0.4");
			excludedStocks.put("ACNB","<0.4");
			excludedStocks.put("ACSF","<0.4");
			excludedStocks.put("ACSI","<0.4");
			excludedStocks.put("ACT","<0.4");
			excludedStocks.put("ACU","<0.4");
			excludedStocks.put("ACV","<0.4");
			excludedStocks.put("ACWF","<0.4");
			excludedStocks.put("ACWV","<0.4");
			excludedStocks.put("ACY","<0.4");
			excludedStocks.put("ADMA","<0.4");
			excludedStocks.put("ADRA","<0.4");
			excludedStocks.put("ADRD","<0.4");
			excludedStocks.put("ADRE","<0.4");
			excludedStocks.put("ADRU","<0.4");
			excludedStocks.put("ADVN","<0.4");
			excludedStocks.put("AE","<0.4");
			excludedStocks.put("AEB","<0.4");
			excludedStocks.put("AED","<0.4");
			excludedStocks.put("AETI","<0.4");
			excludedStocks.put("AEUA","<0.4");
			excludedStocks.put("AEY","<0.4");
			excludedStocks.put("AFAM","<0.4");
			excludedStocks.put("AFC","<0.4");
			excludedStocks.put("AFK","<0.4");
			excludedStocks.put("AFTY","<0.4");
			excludedStocks.put("AGD","<0.4");
			excludedStocks.put("AGGE","<0.4");
			excludedStocks.put("AGGP","<0.4");
			excludedStocks.put("AGGY","<0.4");
			excludedStocks.put("AGII","<0.4");
			excludedStocks.put("AGM-A","<0.4");
			excludedStocks.put("AGM-B","<0.4");
			excludedStocks.put("AGM.A","<0.4");
			excludedStocks.put("AGN-A","<0.4");
			excludedStocks.put("AGNCB","<0.4");
			excludedStocks.put("AGNCN","<0.4");
			excludedStocks.put("AGND","<0.4");
			excludedStocks.put("AGO-B","<0.4");
			excludedStocks.put("AGO-E","<0.4");
			excludedStocks.put("AGO-F","<0.4");
			excludedStocks.put("AGQ","<0.4");
			excludedStocks.put("AGU","<0.4");
			excludedStocks.put("AGYS","<0.4");
			excludedStocks.put("AGZ","<0.4");
			excludedStocks.put("AHC","<0.4");
			excludedStocks.put("AHL-C","<0.4");
			excludedStocks.put("AHL-D","<0.4");
			excludedStocks.put("AHP","<0.4");
			excludedStocks.put("AHPI","<0.4");
			excludedStocks.put("AHT-D","<0.4");
			excludedStocks.put("AHT-F","<0.4");
			excludedStocks.put("AHT-G","<0.4");
			excludedStocks.put("AHT-H","<0.4");
			excludedStocks.put("AHT-I","<0.4");
			excludedStocks.put("AI-B","<0.4");
			excludedStocks.put("AIEQ","<0.4");
			excludedStocks.put("AINC","<0.4");
			excludedStocks.put("AIRI","<0.4");
			excludedStocks.put("AIRR","<0.4");
			excludedStocks.put("AIRT","<0.4");
			excludedStocks.put("AIV-A","<0.4");
			excludedStocks.put("AIY","<0.4");
			excludedStocks.put("AIZP","<0.4");
			excludedStocks.put("AJXA","<0.4");
			excludedStocks.put("AKCA","<0.4");
			excludedStocks.put("AKO","<0.4");
			excludedStocks.put("AKO.A","<0.4");
			excludedStocks.put("AKO.B","<0.4");
			excludedStocks.put("AKP","<0.4");
			excludedStocks.put("AKTX","<0.4");
			excludedStocks.put("ALCO","<0.4");
			excludedStocks.put("ALD","<0.4");
			excludedStocks.put("ALDW","<0.4");
			excludedStocks.put("ALFI","<0.4");
			excludedStocks.put("ALJJ","<0.4");
			excludedStocks.put("ALL-A","<0.4");
			excludedStocks.put("ALL-C","<0.4");
			excludedStocks.put("ALL-D","<0.4");
			excludedStocks.put("ALL-F","<0.4");
			excludedStocks.put("ALLE","<0.4");
			excludedStocks.put("ALLT","<0.4");
			excludedStocks.put("ALOT","<0.4");
			excludedStocks.put("ALP-Q","<0.4");
			excludedStocks.put("ALPN","<0.4");
			excludedStocks.put("ALQA","<0.4");
			excludedStocks.put("ALRN","<0.4");
			excludedStocks.put("ALTS","<0.4");
			excludedStocks.put("ALTY","<0.4");
			excludedStocks.put("ALX","<0.4");
			excludedStocks.put("AMCA","<0.4");
			excludedStocks.put("AMEH","<0.4");
			excludedStocks.put("AMH-C","<0.4");
			excludedStocks.put("AMH-D","<0.4");
			excludedStocks.put("AMH-E","<0.4");
			excludedStocks.put("AMH-F","<0.4");
			excludedStocks.put("AMH-G","<0.4");
			excludedStocks.put("AMJL","<0.4");
			excludedStocks.put("AMNB","<0.4");
			excludedStocks.put("AMOV","<0.4");
			excludedStocks.put("AMRB","<0.4");
			excludedStocks.put("AMRH","<0.4");
			excludedStocks.put("AMRK","<0.4");
			excludedStocks.put("AMS","<0.4");
			excludedStocks.put("AMT-B","<0.4");
			excludedStocks.put("AMU","<0.4");
			excludedStocks.put("AMZN_1","<0.4");
			excludedStocks.put("ANCB","<0.4");
			excludedStocks.put("ANH-A","<0.4");
			excludedStocks.put("ANH-B","<0.4");
			excludedStocks.put("ANH-C","<0.4");
			excludedStocks.put("ANTH","<0.4");
			excludedStocks.put("ANTX","<0.4");
			excludedStocks.put("AOA","<0.4");
			excludedStocks.put("AOK","<0.4");
			excludedStocks.put("AOM","<0.4");
			excludedStocks.put("AOR","<0.4");
			excludedStocks.put("AP","<0.4");
			excludedStocks.put("APB","<0.4");
			excludedStocks.put("APDN","<0.4");
			excludedStocks.put("APEN","<0.4");
			excludedStocks.put("APF","<0.4");
			excludedStocks.put("APOP","<0.4");
			excludedStocks.put("APT","<0.4");
			excludedStocks.put("APWC","<0.4");
			excludedStocks.put("ARCI","<0.4");
			excludedStocks.put("ARCM","<0.4");
			excludedStocks.put("ARCT","<0.4");
			excludedStocks.put("ARCW","<0.4");
			excludedStocks.put("ARCX","<0.4");
			excludedStocks.put("ARE-D","<0.4");
			excludedStocks.put("ARGS","<0.4");
			excludedStocks.put("ARGT","<0.4");
			excludedStocks.put("ARI-C","<0.4");
			excludedStocks.put("ARKG","<0.4");
			excludedStocks.put("ARKQ","<0.4");
			excludedStocks.put("ARKR","<0.4");
			excludedStocks.put("ARL","<0.4");
			excludedStocks.put("AROW","<0.4");
			excludedStocks.put("ARR-A","<0.4");
			excludedStocks.put("ARR-B","<0.4");
			excludedStocks.put("ARTNA","<0.4");
			excludedStocks.put("ARTW","<0.4");
			excludedStocks.put("ASB-C","<0.4");
			excludedStocks.put("ASB-D","<0.4");
			excludedStocks.put("ASEA","<0.4");
			excludedStocks.put("ASET","<0.4");
			excludedStocks.put("ASFI","<0.4");
			excludedStocks.put("ASHS","<0.4");
			excludedStocks.put("ASHX","<0.4");
			excludedStocks.put("ASPN","<0.4");
			excludedStocks.put("ASPU","<0.4");
			excludedStocks.put("ASRV","<0.4");
			excludedStocks.put("ASRVP","<0.4");
			excludedStocks.put("ASTC","<0.4");
			excludedStocks.put("ASV","<0.4");
			excludedStocks.put("ATAC","<0.4");
			excludedStocks.put("ATAX","<0.4");
			excludedStocks.put("ATLC","<0.4");
			excludedStocks.put("ATLO","<0.4");
			excludedStocks.put("ATMP","<0.4");
			excludedStocks.put("ATRI","<0.4");
			excludedStocks.put("ATV","<0.4");
			excludedStocks.put("ATXI","<0.4");
			excludedStocks.put("AUBN","<0.4");
			excludedStocks.put("AUDC","<0.4");
			excludedStocks.put("AUSE","<0.4");
			excludedStocks.put("AUTO","<0.4");
			excludedStocks.put("AVNW","<0.4");
			excludedStocks.put("AVT","<0.4");
			excludedStocks.put("AWRE","<0.4");
			excludedStocks.put("AWX","<0.4");
			excludedStocks.put("AXJL","<0.4");
			excludedStocks.put("AXJV","<0.4");
			excludedStocks.put("AXR","<0.4");
			excludedStocks.put("AXS-D","<0.4");
			excludedStocks.put("AYT","<0.4");
			excludedStocks.put("AZRE","<0.4");
			excludedStocks.put("AZRX","<0.4");
			excludedStocks.put("BAA","<0.4");
			excludedStocks.put("BAB","<0.4");
			excludedStocks.put("BAC-C","<0.4");
			excludedStocks.put("BAC-D","<0.4");
			excludedStocks.put("BAC-E","<0.4");
			excludedStocks.put("BAC-I","<0.4");
			excludedStocks.put("BAC-L","<0.4");
			excludedStocks.put("BAC-Y","<0.4");
			excludedStocks.put("BAF","<0.4");
			excludedStocks.put("BAL","<0.4");
			excludedStocks.put("BANFP","<0.4");
			excludedStocks.put("BANX","<0.4");
			excludedStocks.put("BAR","<0.4");
			excludedStocks.put("BASI","<0.4");
			excludedStocks.put("BBC","<0.4");
			excludedStocks.put("BBDO","<0.4");
			excludedStocks.put("BBF","<0.4");
			excludedStocks.put("BBG","<0.4");
			excludedStocks.put("BBGI","<0.4");
			excludedStocks.put("BBH","<0.4");
			excludedStocks.put("BBK","<0.4");
			excludedStocks.put("BBP","<0.4");
			excludedStocks.put("BBRC","<0.4");
			excludedStocks.put("BBRG","<0.4");
			excludedStocks.put("BBT-D","<0.4");
			excludedStocks.put("BBT-F","<0.4");
			excludedStocks.put("BBT-G","<0.4");
			excludedStocks.put("BBW","<0.4");
			excludedStocks.put("BCAC","<0.4");
			excludedStocks.put("BCBP","<0.4");
			excludedStocks.put("BCH","<0.4");
			excludedStocks.put("BCI","<0.4");
			excludedStocks.put("BCLI","<0.4");
			excludedStocks.put("BCM","<0.4");
			excludedStocks.put("BCOM","<0.4");
			excludedStocks.put("BCR","<0.4");
			excludedStocks.put("BCRH","<0.4");
			excludedStocks.put("BCTF","<0.4");
			excludedStocks.put("BCV","<0.4");
			excludedStocks.put("BCV-A","<0.4");
			excludedStocks.put("BDC-B","<0.4");
			excludedStocks.put("BDCL","<0.4");
			excludedStocks.put("BDCS","<0.4");
			excludedStocks.put("BDD","<0.4");
			excludedStocks.put("BDJ","<0.4");
			excludedStocks.put("BDL","<0.4");
			excludedStocks.put("BDRY","<0.4");
			excludedStocks.put("BDXA","<0.4");
			excludedStocks.put("BELFA","<0.4");
			excludedStocks.put("BELFB","<0.4");
			excludedStocks.put("BEMO","<0.4");
			excludedStocks.put("BETR","<0.4");
			excludedStocks.put("BF","<0.4");
			excludedStocks.put("BFO","<0.4");
			excludedStocks.put("BFOR","<0.4");
			excludedStocks.put("BFRA","<0.4");
			excludedStocks.put("BFS-C","<0.4");
			excludedStocks.put("BFS-D","<0.4");
			excludedStocks.put("BFST","<0.4");
			excludedStocks.put("BFY","<0.4");
			excludedStocks.put("BFZ","<0.4");
			excludedStocks.put("BGH","<0.4");
			excludedStocks.put("BGI","<0.4");
			excludedStocks.put("BGIO","<0.4");
			excludedStocks.put("BH.A","<0.4");
			excludedStocks.put("BHTG","<0.4");
			excludedStocks.put("BHV","<0.4");
			excludedStocks.put("BIBL","<0.4");
			excludedStocks.put("BICK","<0.4");
			excludedStocks.put("BIL","<0.4");
			excludedStocks.put("BIO.B","<0.4");
			excludedStocks.put("BIOA","<0.4");
			excludedStocks.put("BIP","<0.4");
			excludedStocks.put("BIZD","<0.4");
			excludedStocks.put("BJJN","<0.4");
			excludedStocks.put("BJK","<0.4");
			excludedStocks.put("BJO","<0.4");
			excludedStocks.put("BJZ","<0.4");
			excludedStocks.put("BKEP","<0.4");
			excludedStocks.put("BKEPP","<0.4");
			excludedStocks.put("BKF","<0.4");
			excludedStocks.put("BKHU","<0.4");
			excludedStocks.put("BKJ","<0.4");
			excludedStocks.put("BKK","<0.4");
			excludedStocks.put("BKMU","<0.4");
			excludedStocks.put("BKN","<0.4");
			excludedStocks.put("BKSC","<0.4");
			excludedStocks.put("BKYI","<0.4");
			excludedStocks.put("BLES","<0.4");
			excludedStocks.put("BLH","<0.4");
			excludedStocks.put("BLHY","<0.4");
			excludedStocks.put("BLIN","<0.4");
			excludedStocks.put("BLJ","<0.4");
			excludedStocks.put("BLMT","<0.4");
			excludedStocks.put("BLNK","<0.4");
			excludedStocks.put("BLOK","<0.4");
			excludedStocks.put("BLV","<0.4");
			excludedStocks.put("BML-G","<0.4");
			excludedStocks.put("BML-H","<0.4");
			excludedStocks.put("BML-I","<0.4");
			excludedStocks.put("BML-J","<0.4");
			excludedStocks.put("BML-L","<0.4");
			excludedStocks.put("BMRA","<0.4");
			excludedStocks.put("BMRC","<0.4");
			excludedStocks.put("BNDC","<0.4");
			excludedStocks.put("BNJ","<0.4");
			excludedStocks.put("BNSO","<0.4");
			excludedStocks.put("BNTC","<0.4");
			excludedStocks.put("BNY","<0.4");
			excludedStocks.put("BOBE","<0.4");
			excludedStocks.put("BOCH","<0.4");
			excludedStocks.put("BOND","<0.4");
			excludedStocks.put("BOON","<0.4");
			excludedStocks.put("BORN","<0.4");
			excludedStocks.put("BOSC","<0.4");
			excludedStocks.put("BOSS","<0.4");
			excludedStocks.put("BOXL","<0.4");
			excludedStocks.put("BPFHP","<0.4");
			excludedStocks.put("BPK","<0.4");
			excludedStocks.put("BPOPM","<0.4");
			excludedStocks.put("BPOPN","<0.4");
			excludedStocks.put("BPRN","<0.4");
			excludedStocks.put("BPTH","<0.4");
			excludedStocks.put("BQH","<0.4");
			excludedStocks.put("BRAC","<0.4");
			excludedStocks.put("BRF","<0.4");
			excludedStocks.put("BRG-A","<0.4");
			excludedStocks.put("BRG-C","<0.4");
			excludedStocks.put("BRG-D","<0.4");
			excludedStocks.put("BRID","<0.4");
			excludedStocks.put("BRK","<0.4");
			excludedStocks.put("BRN","<0.4");
			excludedStocks.put("BRQS","<0.4");
			excludedStocks.put("BRT","<0.4");
			excludedStocks.put("BSA","<0.4");
			excludedStocks.put("BSCH","<0.4");
			excludedStocks.put("BSCI","<0.4");
			excludedStocks.put("BSCJ","<0.4");
			excludedStocks.put("BSCK","<0.4");
			excludedStocks.put("BSCL","<0.4");
			excludedStocks.put("BSCM","<0.4");
			excludedStocks.put("BSCN","<0.4");
			excludedStocks.put("BSCO","<0.4");
			excludedStocks.put("BSCP","<0.4");
			excludedStocks.put("BSCQ","<0.4");
			excludedStocks.put("BSCR","<0.4");
			excludedStocks.put("BSD","<0.4");
			excludedStocks.put("BSE","<0.4");
			excludedStocks.put("BSFT","<0.4");
			excludedStocks.put("BSJH","<0.4");
			excludedStocks.put("BSJI","<0.4");
			excludedStocks.put("BSJJ","<0.4");
			excludedStocks.put("BSJK","<0.4");
			excludedStocks.put("BSJL","<0.4");
			excludedStocks.put("BSJM","<0.4");
			excludedStocks.put("BSJN","<0.4");
			excludedStocks.put("BSJO","<0.4");
			excludedStocks.put("BSJP","<0.4");
			excludedStocks.put("BSQR","<0.4");
			excludedStocks.put("BSRR","<0.4");
			excludedStocks.put("BSTC","<0.4");
			excludedStocks.put("BSWN","<0.4");
			excludedStocks.put("BTAL","<0.4");
			excludedStocks.put("BTEC","<0.4");
			excludedStocks.put("BTN","<0.4");
			excludedStocks.put("BUFF","<0.4");
			excludedStocks.put("BURG","<0.4");
			excludedStocks.put("BUZ","<0.4");
			excludedStocks.put("BV","<0.4");
			excludedStocks.put("BVSN","<0.4");
			excludedStocks.put("BVX","<0.4");
			excludedStocks.put("BVXV","<0.4");
			excludedStocks.put("BWFG","<0.4");
			excludedStocks.put("BWG","<0.4");
			excludedStocks.put("BWINB","<0.4");
			excludedStocks.put("BWL.A","<0.4");
			excludedStocks.put("BWZ","<0.4");
			excludedStocks.put("BXP-B","<0.4");
			excludedStocks.put("BYLD","<0.4");
			excludedStocks.put("BYM","<0.4");
			excludedStocks.put("BYSI","<0.4");
			excludedStocks.put("BZF","<0.4");
			excludedStocks.put("BZM","<0.4");
			excludedStocks.put("BZQ","<0.4");
			excludedStocks.put("C-C","<0.4");
			excludedStocks.put("C-P","<0.4");
			excludedStocks.put("CAA","<0.4");
			excludedStocks.put("CAAS","<0.4");
			excludedStocks.put("CAC","<0.4");
			excludedStocks.put("CACG","<0.4");
			excludedStocks.put("CADC","<0.4");
			excludedStocks.put("CAFE","<0.4");
			excludedStocks.put("CAI-A","<0.4");
			excludedStocks.put("CALD","<0.4");
			excludedStocks.put("CALF","<0.4");
			excludedStocks.put("CANE","<0.4");
			excludedStocks.put("CANF","<0.4");
			excludedStocks.put("CAPE","<0.4");
			excludedStocks.put("CART","<0.4");
			excludedStocks.put("CARV","<0.4");
			excludedStocks.put("CARZ","<0.4");
			excludedStocks.put("CASM","<0.4");
			excludedStocks.put("CASS","<0.4");
			excludedStocks.put("CATC","<0.4");
			excludedStocks.put("CATH","<0.4");
			excludedStocks.put("CATS","<0.4");
			excludedStocks.put("CAW","<0.4");
			excludedStocks.put("CBAK","<0.4");
			excludedStocks.put("CBAN","<0.4");
			excludedStocks.put("CBB-B","<0.4");
			excludedStocks.put("CBFV","<0.4");
			excludedStocks.put("CBG","<0.4");
			excludedStocks.put("CBH","<0.4");
			excludedStocks.put("CBK","<0.4");
			excludedStocks.put("CBL-E","<0.4");
			excludedStocks.put("CBLI","<0.4");
			excludedStocks.put("CBND","<0.4");
			excludedStocks.put("CBO","<0.4");
			excludedStocks.put("CBON","<0.4");
			excludedStocks.put("CBS.A","<0.4");
			excludedStocks.put("CBSHP","<0.4");
			excludedStocks.put("CCA","<0.4");
			excludedStocks.put("CCBG","<0.4");
			excludedStocks.put("CCC","<0.4");
			excludedStocks.put("CCCL","<0.4");
			excludedStocks.put("CCCR","<0.4");
			excludedStocks.put("CCF","<0.4");
			excludedStocks.put("CCI-A","<0.4");
			excludedStocks.put("CCM","<0.4");
			excludedStocks.put("CCNE","<0.4");
			excludedStocks.put("CCOR","<0.4");
			excludedStocks.put("CCR","<0.4");
			excludedStocks.put("CCUR","<0.4");
			excludedStocks.put("CDL","<0.4");
			excludedStocks.put("CDMOP","<0.4");
			excludedStocks.put("CDOR","<0.4");
			excludedStocks.put("CDR-B","<0.4");
			excludedStocks.put("CDR-C","<0.4");
			excludedStocks.put("CEE","<0.4");
			excludedStocks.put("CEFL","<0.4");
			excludedStocks.put("CEFS","<0.4");
			excludedStocks.put("CEL","<0.4");
			excludedStocks.put("CELC","<0.4");
			excludedStocks.put("CELP","<0.4");
			excludedStocks.put("CEMB","<0.4");
			excludedStocks.put("CEMI","<0.4");
			excludedStocks.put("CET","<0.4");
			excludedStocks.put("CETX","<0.4");
			excludedStocks.put("CETXP","<0.4");
			excludedStocks.put("CEV","<0.4");
			excludedStocks.put("CEW","<0.4");
			excludedStocks.put("CEY","<0.4");
			excludedStocks.put("CEZ","<0.4");
			excludedStocks.put("CFBI","<0.4");
			excludedStocks.put("CFBK","<0.4");
			excludedStocks.put("CFC-B","<0.4");
			excludedStocks.put("CFFI","<0.4");
			excludedStocks.put("CFR-A","<0.4");
			excludedStocks.put("CGA","<0.4");
			excludedStocks.put("CGG","<0.4");
			excludedStocks.put("CGI","<0.4");
			excludedStocks.put("CGO","<0.4");
			excludedStocks.put("CGW","<0.4");
			excludedStocks.put("CH","<0.4");
			excludedStocks.put("CHAD","<0.4");
			excludedStocks.put("CHAU","<0.4");
			excludedStocks.put("CHCI","<0.4");
			excludedStocks.put("CHGX","<0.4");
			excludedStocks.put("CHIE","<0.4");
			excludedStocks.put("CHII","<0.4");
			excludedStocks.put("CHIM","<0.4");
			excludedStocks.put("CHIQ","<0.4");
			excludedStocks.put("CHIX","<0.4");
			excludedStocks.put("CHK-D","<0.4");
			excludedStocks.put("CHKE","<0.4");
			excludedStocks.put("CHMA","<0.4");
			excludedStocks.put("CHMG","<0.4");
			excludedStocks.put("CHN","<0.4");
			excludedStocks.put("CHOC","<0.4");
			excludedStocks.put("CHSCL","<0.4");
			excludedStocks.put("CHSCM","<0.4");
			excludedStocks.put("CHSCN","<0.4");
			excludedStocks.put("CHSCO","<0.4");
			excludedStocks.put("CHSCP","<0.4");
			excludedStocks.put("CIC","<0.4");
			excludedStocks.put("CID","<0.4");
			excludedStocks.put("CIDM","<0.4");
			excludedStocks.put("CIG.C","<0.4");
			excludedStocks.put("CIL","<0.4");
			excludedStocks.put("CIM-A","<0.4");
			excludedStocks.put("CIM-B","<0.4");
			excludedStocks.put("CINR","<0.4");
			excludedStocks.put("CIO-A","<0.4");
			excludedStocks.put("CIX","<0.4");
			excludedStocks.put("CIZ","<0.4");
			excludedStocks.put("CIZN","<0.4");
			excludedStocks.put("CJJD","<0.4");
			excludedStocks.put("CJNK","<0.4");
			excludedStocks.put("CLDC","<0.4");
			excludedStocks.put("CLFD","<0.4");
			excludedStocks.put("CLIR","<0.4");
			excludedStocks.put("CLIX","<0.4");
			excludedStocks.put("CLPR","<0.4");
			excludedStocks.put("CLRG","<0.4");
			excludedStocks.put("CLTL","<0.4");
			excludedStocks.put("CLWT","<0.4");
			excludedStocks.put("CLY","<0.4");
			excludedStocks.put("CLYH","<0.4");
			excludedStocks.put("CMBS","<0.4");
			excludedStocks.put("CMCL","<0.4");
			excludedStocks.put("CMCT","<0.4");
			excludedStocks.put("CMDT","<0.4");
			excludedStocks.put("CMF","<0.4");
			excludedStocks.put("CMFN","<0.4");
			excludedStocks.put("CMO-E","<0.4");
			excludedStocks.put("CMS-B","<0.4");
			excludedStocks.put("CMSS","<0.4");
			excludedStocks.put("CMT","<0.4");
			excludedStocks.put("CN","<0.4");
			excludedStocks.put("CNAC","<0.4");
			excludedStocks.put("CNBKA","<0.4");
			excludedStocks.put("CNDA","<0.4");
			excludedStocks.put("CNDF","<0.4");
			excludedStocks.put("CNFR","<0.4");
			excludedStocks.put("CNIT","<0.4");
			excludedStocks.put("CNNX","<0.4");
			excludedStocks.put("CNTF","<0.4");
			excludedStocks.put("CNXT","<0.4");
			excludedStocks.put("CNY","<0.4");
			excludedStocks.put("CNYA","<0.4");
			excludedStocks.put("COCP","<0.4");
			excludedStocks.put("CODA","<0.4");
			excludedStocks.put("CODX","<0.4");
			excludedStocks.put("COE","<0.4");
			excludedStocks.put("COF-C","<0.4");
			excludedStocks.put("COF-F","<0.4");
			excludedStocks.put("COF-H","<0.4");
			excludedStocks.put("COM","<0.4");
			excludedStocks.put("COMB","<0.4");
			excludedStocks.put("COPX","<0.4");
			excludedStocks.put("CORN","<0.4");
			excludedStocks.put("CORP","<0.4");
			excludedStocks.put("COW","<0.4");
			excludedStocks.put("COWB","<0.4");
			excludedStocks.put("COWN","<0.4");
			excludedStocks.put("COWZ","<0.4");
			excludedStocks.put("CPAC","<0.4");
			excludedStocks.put("CPAH","<0.4");
			excludedStocks.put("CPE-A","<0.4");
			excludedStocks.put("CPER","<0.4");
			excludedStocks.put("CPHC","<0.4");
			excludedStocks.put("CPI","<0.4");
			excludedStocks.put("CPIX","<0.4");
			excludedStocks.put("CPN","<0.4");
			excludedStocks.put("CPR","<0.4");
			excludedStocks.put("CPSS","<0.4");
			excludedStocks.put("CQQQ","<0.4");
			excludedStocks.put("CRAK","<0.4");
			excludedStocks.put("CRBN","<0.4");
			excludedStocks.put("CRD","<0.4");
			excludedStocks.put("CRD.A","<0.4");
			excludedStocks.put("CRD.B","<0.4");
			excludedStocks.put("CRHM","<0.4");
			excludedStocks.put("CRME","<0.4");
			excludedStocks.put("CROC","<0.4");
			excludedStocks.put("CROP","<0.4");
			excludedStocks.put("CRT","<0.4");
			excludedStocks.put("CRVL","<0.4");
			excludedStocks.put("CRVP","<0.4");
			excludedStocks.put("CRWS","<0.4");
			excludedStocks.put("CSB","<0.4");
			excludedStocks.put("CSBK","<0.4");
			excludedStocks.put("CSBR","<0.4");
			excludedStocks.put("CSD","<0.4");
			excludedStocks.put("CSF","<0.4");
			excludedStocks.put("CSM","<0.4");
			excludedStocks.put("CSPI","<0.4");
			excludedStocks.put("CSRA","<0.4");
			excludedStocks.put("CSSE","<0.4");
			excludedStocks.put("CSTR","<0.4");
			excludedStocks.put("CSWC","<0.4");
			excludedStocks.put("CTEK","<0.4");
			excludedStocks.put("CTHR","<0.4");
			excludedStocks.put("CTIB","<0.4");
			excludedStocks.put("CTO","<0.4");
			excludedStocks.put("CTU","<0.4");
			excludedStocks.put("CTV","<0.4");
			excludedStocks.put("CTW","<0.4");
			excludedStocks.put("CTX","<0.4");
			excludedStocks.put("CTXR","<0.4");
			excludedStocks.put("CTZ","<0.4");
			excludedStocks.put("CUBA","<0.4");
			excludedStocks.put("CUDA","<0.4");
			excludedStocks.put("CUI","<0.4");
			excludedStocks.put("CULP","<0.4");
			excludedStocks.put("CUMB","<0.4");
			excludedStocks.put("CUO","<0.4");
			excludedStocks.put("CUT","<0.4");
			excludedStocks.put("CVCY","<0.4");
			excludedStocks.put("CVLY","<0.4");
			excludedStocks.put("CVO","<0.4");
			excludedStocks.put("CVR","<0.4");
			excludedStocks.put("CVU","<0.4");
			excludedStocks.put("CVV","<0.4");
			excludedStocks.put("CVY","<0.4");
			excludedStocks.put("CWAY","<0.4");
			excludedStocks.put("CWBC","<0.4");
			excludedStocks.put("CWBR","<0.4");
			excludedStocks.put("CWEB","<0.4");
			excludedStocks.put("CWI","<0.4");
			excludedStocks.put("CXDC","<0.4");
			excludedStocks.put("CXH","<0.4");
			excludedStocks.put("CYAN","<0.4");
			excludedStocks.put("CYB","<0.4");
			excludedStocks.put("CYCC","<0.4");
			excludedStocks.put("CYM","<0.4");
			excludedStocks.put("CYRN","<0.4");
			excludedStocks.put("CYS-A","<0.4");
			excludedStocks.put("CYS-B","<0.4");
			excludedStocks.put("CZA","<0.4");
			excludedStocks.put("CZFC","<0.4");
			excludedStocks.put("CZNC","<0.4");
			excludedStocks.put("CZWI","<0.4");
			excludedStocks.put("DAC","<0.4");
			excludedStocks.put("DAG","<0.4");
			excludedStocks.put("DAX","<0.4");
			excludedStocks.put("DBAP","<0.4");
			excludedStocks.put("DBAW","<0.4");
			excludedStocks.put("DBB","<0.4");
			excludedStocks.put("DBBR","<0.4");
			excludedStocks.put("DBE","<0.4");
			excludedStocks.put("DBEM","<0.4");
			excludedStocks.put("DBEZ","<0.4");
			excludedStocks.put("DBGR","<0.4");
			excludedStocks.put("DBJP","<0.4");
			excludedStocks.put("DBKO","<0.4");
			excludedStocks.put("DBP","<0.4");
			excludedStocks.put("DBS","<0.4");
			excludedStocks.put("DBV","<0.4");
			excludedStocks.put("DCF","<0.4");
			excludedStocks.put("DCM","<0.4");
			excludedStocks.put("DCTH","<0.4");
			excludedStocks.put("DCUD","<0.4");
			excludedStocks.put("DD-A","<0.4");
			excludedStocks.put("DD-B","<0.4");
			excludedStocks.put("DDBI","<0.4");
			excludedStocks.put("DDE","<0.4");
			excludedStocks.put("DDEZ","<0.4");
			excludedStocks.put("DDF","<0.4");
			excludedStocks.put("DDG","<0.4");
			excludedStocks.put("DDLS","<0.4");
			excludedStocks.put("DDR-A","<0.4");
			excludedStocks.put("DDR-J","<0.4");
			excludedStocks.put("DDR-K","<0.4");
			excludedStocks.put("DDT","<0.4");
			excludedStocks.put("DDWM","<0.4");
			excludedStocks.put("DECN","<0.4");
			excludedStocks.put("DEEF","<0.4");
			excludedStocks.put("DEF","<0.4");
			excludedStocks.put("DEFA","<0.4");
			excludedStocks.put("DEL","<0.4");
			excludedStocks.put("DEMG","<0.4");
			excludedStocks.put("DES","<0.4");
			excludedStocks.put("DESC","<0.4");
			excludedStocks.put("DEST","<0.4");
			excludedStocks.put("DEUR","<0.4");
			excludedStocks.put("DEUS","<0.4");
			excludedStocks.put("DEW","<0.4");
			excludedStocks.put("DEWJ","<0.4");
			excludedStocks.put("DEX","<0.4");
			excludedStocks.put("DEZU","<0.4");
			excludedStocks.put("DFBG","<0.4");
			excludedStocks.put("DFE","<0.4");
			excludedStocks.put("DFJ","<0.4");
			excludedStocks.put("DFND","<0.4");
			excludedStocks.put("DFNL","<0.4");
			excludedStocks.put("DFVS","<0.4");
			excludedStocks.put("DGICA","<0.4");
			excludedStocks.put("DGL","<0.4");
			excludedStocks.put("DGLY","<0.4");
			excludedStocks.put("DGP","<0.4");
			excludedStocks.put("DGRE","<0.4");
			excludedStocks.put("DGRS","<0.4");
			excludedStocks.put("DGS","<0.4");
			excludedStocks.put("DGSE","<0.4");
			excludedStocks.put("DGT","<0.4");
			excludedStocks.put("DGZ","<0.4");
			excludedStocks.put("DHCP","<0.4");
			excludedStocks.put("DHDG","<0.4");
			excludedStocks.put("DHIL","<0.4");
			excludedStocks.put("DHS","<0.4");
			excludedStocks.put("DHVW","<0.4");
			excludedStocks.put("DHXM","<0.4");
			excludedStocks.put("DIAL","<0.4");
			excludedStocks.put("DIM","<0.4");
			excludedStocks.put("DISCB","<0.4");
			excludedStocks.put("DIT","<0.4");
			excludedStocks.put("DIV","<0.4");
			excludedStocks.put("DIVA","<0.4");
			excludedStocks.put("DIVB","<0.4");
			excludedStocks.put("DIVY","<0.4");
			excludedStocks.put("DJC","<0.4");
			excludedStocks.put("DJCI","<0.4");
			excludedStocks.put("DJCO","<0.4");
			excludedStocks.put("DJD","<0.4");
			excludedStocks.put("DJI","<0.4");
			excludedStocks.put("DJT","<0.4");
			excludedStocks.put("DJU","<0.4");
			excludedStocks.put("DL","<0.4");
			excludedStocks.put("DLA","<0.4");
			excludedStocks.put("DLBS","<0.4");
			excludedStocks.put("DLHC","<0.4");
			excludedStocks.put("DLN","<0.4");
			excludedStocks.put("DLPN","<0.4");
			excludedStocks.put("DLR-C","<0.4");
			excludedStocks.put("DLR-G","<0.4");
			excludedStocks.put("DLR-H","<0.4");
			excludedStocks.put("DLR-I","<0.4");
			excludedStocks.put("DLR-J","<0.4");
			excludedStocks.put("DLS","<0.4");
			excludedStocks.put("DMF","<0.4");
			excludedStocks.put("DMRI","<0.4");
			excludedStocks.put("DMRL","<0.4");
			excludedStocks.put("DMRM","<0.4");
			excludedStocks.put("DMRS","<0.4");
			excludedStocks.put("DNBF","<0.4");
			excludedStocks.put("DNJR","<0.4");
			excludedStocks.put("DNL","<0.4");
			excludedStocks.put("DNO","<0.4");
			excludedStocks.put("DOD","<0.4");
			excludedStocks.put("DOGS","<0.4");
			excludedStocks.put("DOGZ","<0.4");
			excludedStocks.put("DOL","<0.4");
			excludedStocks.put("DON","<0.4");
			excludedStocks.put("DOO","<0.4");
			excludedStocks.put("DOTA","<0.4");
			excludedStocks.put("DPK","<0.4");
			excludedStocks.put("DPST","<0.4");
			excludedStocks.put("DRIO","<0.4");
			excludedStocks.put("DRIV","<0.4");
			excludedStocks.put("DRR","<0.4");
			excludedStocks.put("DRV","<0.4");
			excludedStocks.put("DRW","<0.4");
			excludedStocks.put("DS-C","<0.4");
			excludedStocks.put("DS-D","<0.4");
			excludedStocks.put("DSCR","<0.4");
			excludedStocks.put("DSFI","<0.4");
			excludedStocks.put("DSFN","<0.4");
			excludedStocks.put("DSI","<0.4");
			excludedStocks.put("DSNG","<0.4");
			excludedStocks.put("DSOL","<0.4");
			excludedStocks.put("DSS","<0.4");
			excludedStocks.put("DSUM","<0.4");
			excludedStocks.put("DSWL","<0.4");
			excludedStocks.put("DSX-B","<0.4");
			excludedStocks.put("DTD","<0.4");
			excludedStocks.put("DTEA","<0.4");
			excludedStocks.put("DTF","<0.4");
			excludedStocks.put("DTH","<0.4");
			excludedStocks.put("DTN","<0.4");
			excludedStocks.put("DTO","<0.4");
			excludedStocks.put("DTRM","<0.4");
			excludedStocks.put("DTUS","<0.4");
			excludedStocks.put("DTV","<0.4");
			excludedStocks.put("DTW","<0.4");
			excludedStocks.put("DTY","<0.4");
			excludedStocks.put("DTYS","<0.4");
			excludedStocks.put("DUC","<0.4");
			excludedStocks.put("DUSA","<0.4");
			excludedStocks.put("DUSL","<0.4");
			excludedStocks.put("DVD","<0.4");
			excludedStocks.put("DVEM","<0.4");
			excludedStocks.put("DVHL","<0.4");
			excludedStocks.put("DVP","<0.4");
			excludedStocks.put("DVYA","<0.4");
			excludedStocks.put("DVYE","<0.4");
			excludedStocks.put("DVYL","<0.4");
			excludedStocks.put("DWAQ","<0.4");
			excludedStocks.put("DWAS","<0.4");
			excludedStocks.put("DWAT","<0.4");
			excludedStocks.put("DWCR","<0.4");
			excludedStocks.put("DWFI","<0.4");
			excludedStocks.put("DWIN","<0.4");
			excludedStocks.put("DWLD","<0.4");
			excludedStocks.put("DWLV","<0.4");
			excludedStocks.put("DWM","<0.4");
			excludedStocks.put("DWPP","<0.4");
			excludedStocks.put("DWTR","<0.4");
			excludedStocks.put("DWX","<0.4");
			excludedStocks.put("DX-A","<0.4");
			excludedStocks.put("DX-B","<0.4");
			excludedStocks.put("DXB","<0.4");
			excludedStocks.put("DXGE","<0.4");
			excludedStocks.put("DXJC","<0.4");
			excludedStocks.put("DXJF","<0.4");
			excludedStocks.put("DXJH","<0.4");
			excludedStocks.put("DXJR","<0.4");
			excludedStocks.put("DXJS","<0.4");
			excludedStocks.put("DXR","<0.4");
			excludedStocks.put("DXTR","<0.4");
			excludedStocks.put("DXYN","<0.4");
			excludedStocks.put("DYB","<0.4");
			excludedStocks.put("DYLS","<0.4");
			excludedStocks.put("DYNC","<0.4");
			excludedStocks.put("DYNT","<0.4");
			excludedStocks.put("DYSL","<0.4");
			excludedStocks.put("DYY","<0.4");
			excludedStocks.put("DZK","<0.4");
			excludedStocks.put("DZSI","<0.4");
			excludedStocks.put("DZZ","<0.4");
			excludedStocks.put("EACQ","<0.4");
			excludedStocks.put("EAGL","<0.4");
			excludedStocks.put("EAI","<0.4");
			excludedStocks.put("EAST","<0.4");
			excludedStocks.put("EBMT","<0.4");
			excludedStocks.put("EBND","<0.4");
			excludedStocks.put("EBR.B","<0.4");
			excludedStocks.put("EBTC","<0.4");
			excludedStocks.put("ECCA","<0.4");
			excludedStocks.put("ECCB","<0.4");
			excludedStocks.put("ECF","<0.4");
			excludedStocks.put("ECF-A","<0.4");
			excludedStocks.put("ECNS","<0.4");
			excludedStocks.put("ECON","<0.4");
			excludedStocks.put("ECT","<0.4");
			excludedStocks.put("EDAP","<0.4");
			excludedStocks.put("EDBI","<0.4");
			excludedStocks.put("EDEN","<0.4");
			excludedStocks.put("EDGW","<0.4");
			excludedStocks.put("EDIV","<0.4");
			excludedStocks.put("EDOG","<0.4");
			excludedStocks.put("EDOW","<0.4");
			excludedStocks.put("EDV","<0.4");
			excludedStocks.put("EEA","<0.4");
			excludedStocks.put("EEB","<0.4");
			excludedStocks.put("EEI","<0.4");
			excludedStocks.put("EELV","<0.4");
			excludedStocks.put("EEMA","<0.4");
			excludedStocks.put("EEMD","<0.4");
			excludedStocks.put("EEMO","<0.4");
			excludedStocks.put("EEMS","<0.4");
			excludedStocks.put("EEMX","<0.4");
			excludedStocks.put("EES","<0.4");
			excludedStocks.put("EET","<0.4");
			excludedStocks.put("EEV","<0.4");
			excludedStocks.put("EFAD","<0.4");
			excludedStocks.put("EFAS","<0.4");
			excludedStocks.put("EFAX","<0.4");
			excludedStocks.put("EFBI","<0.4");
			excludedStocks.put("EFF","<0.4");
			excludedStocks.put("EFG","<0.4");
			excludedStocks.put("EFL","<0.4");
			excludedStocks.put("EFNL","<0.4");
			excludedStocks.put("EFO","<0.4");
			excludedStocks.put("EFOI","<0.4");
			excludedStocks.put("EFZ","<0.4");
			excludedStocks.put("EGF","<0.4");
			excludedStocks.put("EGI","<0.4");
			excludedStocks.put("EGIF","<0.4");
			excludedStocks.put("EGPT","<0.4");
			excludedStocks.put("EHT","<0.4");
			excludedStocks.put("EIA","<0.4");
			excludedStocks.put("EIO","<0.4");
			excludedStocks.put("EIP","<0.4");
			excludedStocks.put("EIRL","<0.4");
			excludedStocks.put("EIS","<0.4");
			excludedStocks.put("EIV","<0.4");
			excludedStocks.put("EKAR","<0.4");
			excludedStocks.put("ELC","<0.4");
			excludedStocks.put("ELD","<0.4");
			excludedStocks.put("ELMD","<0.4");
			excludedStocks.put("ELON","<0.4");
			excludedStocks.put("ELSE","<0.4");
			excludedStocks.put("ELTK","<0.4");
			excludedStocks.put("EMAG","<0.4");
			excludedStocks.put("EMBH","<0.4");
			excludedStocks.put("EMBU","<0.4");
			excludedStocks.put("EMCB","<0.4");
			excludedStocks.put("EMCF","<0.4");
			excludedStocks.put("EMCG","<0.4");
			excludedStocks.put("EMCI","<0.4");
			excludedStocks.put("EMDV","<0.4");
			excludedStocks.put("EMEM","<0.4");
			excludedStocks.put("EMFM","<0.4");
			excludedStocks.put("EMGF","<0.4");
			excludedStocks.put("EMHY","<0.4");
			excludedStocks.put("EMI","<0.4");
			excludedStocks.put("EMIF","<0.4");
			excludedStocks.put("EMIH","<0.4");
			excludedStocks.put("EMITF","<0.4");
			excludedStocks.put("EMJ","<0.4");
			excludedStocks.put("EML","<0.4");
			excludedStocks.put("EMMS","<0.4");
			excludedStocks.put("EMO","<0.4");
			excludedStocks.put("EMP","<0.4");
			excludedStocks.put("EMQQ","<0.4");
			excludedStocks.put("EMSD","<0.4");
			excludedStocks.put("EMSH","<0.4");
			excludedStocks.put("EMTL","<0.4");
			excludedStocks.put("EMTY","<0.4");
			excludedStocks.put("EMX","<0.4");
			excludedStocks.put("EMXC","<0.4");
			excludedStocks.put("ENFC","<0.4");
			excludedStocks.put("ENFR","<0.4");
			excludedStocks.put("ENG","<0.4");
			excludedStocks.put("ENO","<0.4");
			excludedStocks.put("ENOR","<0.4");
			excludedStocks.put("ENRJ","<0.4");
			excludedStocks.put("ENY","<0.4");
			excludedStocks.put("ENZL","<0.4");
			excludedStocks.put("EOT","<0.4");
			excludedStocks.put("EP-C","<0.4");
			excludedStocks.put("EPIX","<0.4");
			excludedStocks.put("EPR-C","<0.4");
			excludedStocks.put("EPR-E","<0.4");
			excludedStocks.put("EPR-G","<0.4");
			excludedStocks.put("EPRF","<0.4");
			excludedStocks.put("EPS","<0.4");
			excludedStocks.put("EPU","<0.4");
			excludedStocks.put("EPV","<0.4");
			excludedStocks.put("EQAL","<0.4");
			excludedStocks.put("EQC-D","<0.4");
			excludedStocks.put("EQFN","<0.4");
			excludedStocks.put("EQL","<0.4");
			excludedStocks.put("EQLT","<0.4");
			excludedStocks.put("EQRR","<0.4");
			excludedStocks.put("EQS","<0.4");
			excludedStocks.put("EQWL","<0.4");
			excludedStocks.put("EQWM","<0.4");
			excludedStocks.put("EQWS","<0.4");
			excludedStocks.put("ERH","<0.4");
			excludedStocks.put("ERM","<0.4");
			excludedStocks.put("ERN","<0.4");
			excludedStocks.put("ERYP","<0.4");
			excludedStocks.put("ESBA","<0.4");
			excludedStocks.put("ESBK","<0.4");
			excludedStocks.put("ESCA","<0.4");
			excludedStocks.put("ESEA","<0.4");
			excludedStocks.put("ESES","<0.4");
			excludedStocks.put("ESG","<0.4");
			excludedStocks.put("ESGD","<0.4");
			excludedStocks.put("ESGE","<0.4");
			excludedStocks.put("ESGG","<0.4");
			excludedStocks.put("ESGL","<0.4");
			excludedStocks.put("ESGU","<0.4");
			excludedStocks.put("ESML","<0.4");
			excludedStocks.put("ESP","<0.4");
			excludedStocks.put("ESQ","<0.4");
			excludedStocks.put("ESSA","<0.4");
			excludedStocks.put("ESTR","<0.4");
			excludedStocks.put("ESXB","<0.4");
			excludedStocks.put("ETHO","<0.4");
			excludedStocks.put("ETN","<0.4");
			excludedStocks.put("ETX","<0.4");
			excludedStocks.put("EUDG","<0.4");
			excludedStocks.put("EUDV","<0.4");
			excludedStocks.put("EUFL","<0.4");
			excludedStocks.put("EUFX","<0.4");
			excludedStocks.put("EUMV","<0.4");
			excludedStocks.put("EUO","<0.4");
			excludedStocks.put("EURL","<0.4");
			excludedStocks.put("EUSA","<0.4");
			excludedStocks.put("EUSC","<0.4");
			excludedStocks.put("EUXL","<0.4");
			excludedStocks.put("EVBN","<0.4");
			excludedStocks.put("EVFM","<0.4");
			excludedStocks.put("EVG","<0.4");
			excludedStocks.put("EVGN","<0.4");
			excludedStocks.put("EVI","<0.4");
			excludedStocks.put("EVIX","<0.4");
			excludedStocks.put("EVJ","<0.4");
			excludedStocks.put("EVK","<0.4");
			excludedStocks.put("EVM","<0.4");
			excludedStocks.put("EVO","<0.4");
			excludedStocks.put("EVOL","<0.4");
			excludedStocks.put("EVP","<0.4");
			excludedStocks.put("EVX","<0.4");
			excludedStocks.put("EVY","<0.4");
			excludedStocks.put("EWEM","<0.4");
			excludedStocks.put("EWGS","<0.4");
			excludedStocks.put("EWK","<0.4");
			excludedStocks.put("EWMC","<0.4");
			excludedStocks.put("EWO","<0.4");
			excludedStocks.put("EWRE","<0.4");
			excludedStocks.put("EWSC","<0.4");
			excludedStocks.put("EWUS","<0.4");
			excludedStocks.put("EWV","<0.4");
			excludedStocks.put("EWX","<0.4");
			excludedStocks.put("EWZS","<0.4");
			excludedStocks.put("EXD","<0.4");
			excludedStocks.put("EXFO","<0.4");
			excludedStocks.put("EXI","<0.4");
			excludedStocks.put("EXIV","<0.4");
			excludedStocks.put("EXT","<0.4");
			excludedStocks.put("EYEN","<0.4");
			excludedStocks.put("EYLD","<0.4");
			excludedStocks.put("EZJ","<0.4");
			excludedStocks.put("EZM","<0.4");
			excludedStocks.put("EZY","<0.4");
			excludedStocks.put("FAAR","<0.4");
			excludedStocks.put("FAB","<0.4");
			excludedStocks.put("FAC","<0.4");
			excludedStocks.put("FAD","<0.4");
			excludedStocks.put("FALN","<0.4");
			excludedStocks.put("FAM","<0.4");
			excludedStocks.put("FAMI","<0.4");
			excludedStocks.put("FAN","<0.4");
			excludedStocks.put("FAT","<0.4");
			excludedStocks.put("FB_1","<0.4");
			excludedStocks.put("FBGX","<0.4");
			excludedStocks.put("FBIOP","<0.4");
			excludedStocks.put("FBIZ","<0.4");
			excludedStocks.put("FBND","<0.4");
			excludedStocks.put("FBNK","<0.4");
			excludedStocks.put("FBSS","<0.4");
			excludedStocks.put("FBZ","<0.4");
			excludedStocks.put("FCA","<0.4");
			excludedStocks.put("FCAL","<0.4");
			excludedStocks.put("FCAN","<0.4");
			excludedStocks.put("FCAP","<0.4");
			excludedStocks.put("FCBP","<0.4");
			excludedStocks.put("FCCO","<0.4");
			excludedStocks.put("FCCY","<0.4");
			excludedStocks.put("FCE","<0.4");
			excludedStocks.put("FCEF","<0.4");
			excludedStocks.put("FCNCA","<0.4");
			excludedStocks.put("FCO","<0.4");
			excludedStocks.put("FCOM","<0.4");
			excludedStocks.put("FCOR","<0.4");
			excludedStocks.put("FCRE","<0.4");
			excludedStocks.put("FDBC","<0.4");
			excludedStocks.put("FDD","<0.4");
			excludedStocks.put("FDIS","<0.4");
			excludedStocks.put("FDIV","<0.4");
			excludedStocks.put("FDL","<0.4");
			excludedStocks.put("FDLO","<0.4");
			excludedStocks.put("FDM","<0.4");
			excludedStocks.put("FDMO","<0.4");
			excludedStocks.put("FDRR","<0.4");
			excludedStocks.put("FDTS","<0.4");
			excludedStocks.put("FDVV","<0.4");
			excludedStocks.put("FEDU","<0.4");
			excludedStocks.put("FEEU","<0.4");
			excludedStocks.put("FEIM","<0.4");
			excludedStocks.put("FELP","<0.4");
			excludedStocks.put("FEMB","<0.4");
			excludedStocks.put("FEO","<0.4");
			excludedStocks.put("FEU","<0.4");
			excludedStocks.put("FEUZ","<0.4");
			excludedStocks.put("FFBW","<0.4");
			excludedStocks.put("FFEU","<0.4");
			excludedStocks.put("FFG","<0.4");
			excludedStocks.put("FFHG","<0.4");
			excludedStocks.put("FFIU","<0.4");
			excludedStocks.put("FFNW","<0.4");
			excludedStocks.put("FFR","<0.4");
			excludedStocks.put("FFSG","<0.4");
			excludedStocks.put("FFTI","<0.4");
			excludedStocks.put("FFTY","<0.4");
			excludedStocks.put("FGB","<0.4");
			excludedStocks.put("FGBI","<0.4");
			excludedStocks.put("FGD","<0.4");
			excludedStocks.put("FGM","<0.4");
			excludedStocks.put("FH","<0.4");
			excludedStocks.put("FHN-A","<0.4");
			excludedStocks.put("FHY","<0.4");
			excludedStocks.put("FIBR","<0.4");
			excludedStocks.put("FIDI","<0.4");
			excludedStocks.put("FIDU","<0.4");
			excludedStocks.put("FIEE","<0.4");
			excludedStocks.put("FIEU","<0.4");
			excludedStocks.put("FIG","<0.4");
			excludedStocks.put("FIGY","<0.4");
			excludedStocks.put("FIHD","<0.4");
			excludedStocks.put("FILL","<0.4");
			excludedStocks.put("FINU","<0.4");
			excludedStocks.put("FINZ","<0.4");
			excludedStocks.put("FITBI","<0.4");
			excludedStocks.put("FIVA","<0.4");
			excludedStocks.put("FIW","<0.4");
			excludedStocks.put("FIXD","<0.4");
			excludedStocks.put("FIYY","<0.4");
			excludedStocks.put("FJP","<0.4");
			excludedStocks.put("FKO","<0.4");
			excludedStocks.put("FKU","<0.4");
			excludedStocks.put("FLAG","<0.4");
			excludedStocks.put("FLAT","<0.4");
			excludedStocks.put("FLAU","<0.4");
			excludedStocks.put("FLBR","<0.4");
			excludedStocks.put("FLC","<0.4");
			excludedStocks.put("FLCH","<0.4");
			excludedStocks.put("FLCO","<0.4");
			excludedStocks.put("FLEE","<0.4");
			excludedStocks.put("FLEU","<0.4");
			excludedStocks.put("FLGB","<0.4");
			excludedStocks.put("FLGE","<0.4");
			excludedStocks.put("FLGT","<0.4");
			excludedStocks.put("FLIN","<0.4");
			excludedStocks.put("FLIY","<0.4");
			excludedStocks.put("FLKR","<0.4");
			excludedStocks.put("FLKS","<0.4");
			excludedStocks.put("FLL","<0.4");
			excludedStocks.put("FLM","<0.4");
			excludedStocks.put("FLMI","<0.4");
			excludedStocks.put("FLN","<0.4");
			excludedStocks.put("FLOT","<0.4");
			excludedStocks.put("FLQE","<0.4");
			excludedStocks.put("FLRN","<0.4");
			excludedStocks.put("FLRT","<0.4");
			excludedStocks.put("FLRU","<0.4");
			excludedStocks.put("FLSW","<0.4");
			excludedStocks.put("FLTB","<0.4");
			excludedStocks.put("FLTR","<0.4");
			excludedStocks.put("FLTW","<0.4");
			excludedStocks.put("FLXS","<0.4");
			excludedStocks.put("FM","<0.4");
			excludedStocks.put("FMAO","<0.4");
			excludedStocks.put("FMAT","<0.4");
			excludedStocks.put("FMBH","<0.4");
			excludedStocks.put("FMDG","<0.4");
			excludedStocks.put("FMF","<0.4");
			excludedStocks.put("FMHI","<0.4");
			excludedStocks.put("FMK","<0.4");
			excludedStocks.put("FMN","<0.4");
			excludedStocks.put("FMNB","<0.4");
			excludedStocks.put("FMY","<0.4");
			excludedStocks.put("FNB-E","<0.4");
			excludedStocks.put("FNBG","<0.4");
			excludedStocks.put("FNCB","<0.4");
			excludedStocks.put("FNCF","<0.4");
			excludedStocks.put("FNDA","<0.4");
			excludedStocks.put("FNDB","<0.4");
			excludedStocks.put("FNDC","<0.4");
			excludedStocks.put("FNDE","<0.4");
			excludedStocks.put("FNDF","<0.4");
			excludedStocks.put("FNDX","<0.4");
			excludedStocks.put("FNG","<0.4");
			excludedStocks.put("FNGD","<0.4");
			excludedStocks.put("FNGU","<0.4");
			excludedStocks.put("FNI","<0.4");
			excludedStocks.put("FNK","<0.4");
			excludedStocks.put("FNLC","<0.4");
			excludedStocks.put("FNTE","<0.4");
			excludedStocks.put("FNWB","<0.4");
			excludedStocks.put("FNY","<0.4");
			excludedStocks.put("FONE","<0.4");
			excludedStocks.put("FORK","<0.4");
			excludedStocks.put("FORTY","<0.4");
			excludedStocks.put("FOX","<0.4");
			excludedStocks.put("FPA","<0.4");
			excludedStocks.put("FPAY","<0.4");
			excludedStocks.put("FPEI","<0.4");
			excludedStocks.put("FPI-B","<0.4");
			excludedStocks.put("FPX","<0.4");
			excludedStocks.put("FPXI","<0.4");
			excludedStocks.put("FQAL","<0.4");
			excludedStocks.put("FRAK","<0.4");
			excludedStocks.put("FRBA","<0.4");
			excludedStocks.put("FRC-D","<0.4");
			excludedStocks.put("FRC-E","<0.4");
			excludedStocks.put("FRC-F","<0.4");
			excludedStocks.put("FRC-G","<0.4");
			excludedStocks.put("FRC-H","<0.4");
			excludedStocks.put("FRD","<0.4");
			excludedStocks.put("FREL","<0.4");
			excludedStocks.put("FRI","<0.4");
			excludedStocks.put("FRLG","<0.4");
			excludedStocks.put("FRN","<0.4");
			excludedStocks.put("FRPH","<0.4");
			excludedStocks.put("FRSX","<0.4");
			excludedStocks.put("FRT-C","<0.4");
			excludedStocks.put("FSAC","<0.4");
			excludedStocks.put("FSBC","<0.4");
			excludedStocks.put("FSBW","<0.4");
			excludedStocks.put("FSFG","<0.4");
			excludedStocks.put("FSI","<0.4");
			excludedStocks.put("FSTA","<0.4");
			excludedStocks.put("FSZ","<0.4");
			excludedStocks.put("FT","<0.4");
			excludedStocks.put("FTAG","<0.4");
			excludedStocks.put("FTEK","<0.4");
			excludedStocks.put("FTHI","<0.4");
			excludedStocks.put("FTLB","<0.4");
			excludedStocks.put("FTLS","<0.4");
			excludedStocks.put("FTNW","<0.4");
			excludedStocks.put("FTRI","<0.4");
			excludedStocks.put("FTSD","<0.4");
			excludedStocks.put("FTVA","<0.4");
			excludedStocks.put("FTXH","<0.4");
			excludedStocks.put("FTXL","<0.4");
			excludedStocks.put("FTXN","<0.4");
			excludedStocks.put("FTXR","<0.4");
			excludedStocks.put("FUD","<0.4");
			excludedStocks.put("FUNC","<0.4");
			excludedStocks.put("FUND","<0.4");
			excludedStocks.put("FUSB","<0.4");
			excludedStocks.put("FUTY","<0.4");
			excludedStocks.put("FUV","<0.4");
			excludedStocks.put("FVAL","<0.4");
			excludedStocks.put("FVD","<0.4");
			excludedStocks.put("FVE","<0.4");
			excludedStocks.put("FVL","<0.4");
			excludedStocks.put("FWDB","<0.4");
			excludedStocks.put("FWDD","<0.4");
			excludedStocks.put("FWDI","<0.4");
			excludedStocks.put("FWP","<0.4");
			excludedStocks.put("FXA","<0.4");
			excludedStocks.put("FXB","<0.4");
			excludedStocks.put("FXC","<0.4");
			excludedStocks.put("FXD","<0.4");
			excludedStocks.put("FXEU","<0.4");
			excludedStocks.put("FXF","<0.4");
			excludedStocks.put("FXG","<0.4");
			excludedStocks.put("FXH","<0.4");
			excludedStocks.put("FXO","<0.4");
			excludedStocks.put("FXR","<0.4");
			excludedStocks.put("FXS","<0.4");
			excludedStocks.put("FXU","<0.4");
			excludedStocks.put("FXZ","<0.4");
			excludedStocks.put("FYC","<0.4");
			excludedStocks.put("FYLD","<0.4");
			excludedStocks.put("FYT","<0.4");
			excludedStocks.put("GAB-G","<0.4");
			excludedStocks.put("GAB-H","<0.4");
			excludedStocks.put("GAB-J","<0.4");
			excludedStocks.put("GAINM","<0.4");
			excludedStocks.put("GAINN","<0.4");
			excludedStocks.put("GAINO","<0.4");
			excludedStocks.put("GAL","<0.4");
			excludedStocks.put("GALE","<0.4");
			excludedStocks.put("GAM-B","<0.4");
			excludedStocks.put("GAMR","<0.4");
			excludedStocks.put("GARS","<0.4");
			excludedStocks.put("GASS","<0.4");
			excludedStocks.put("GASX","<0.4");
			excludedStocks.put("GAZ","<0.4");
			excludedStocks.put("GBB","<0.4");
			excludedStocks.put("GBF","<0.4");
			excludedStocks.put("GBIL","<0.4");
			excludedStocks.put("GBL","<0.4");
			excludedStocks.put("GBLI","<0.4");
			excludedStocks.put("GBR","<0.4");
			excludedStocks.put("GCBC","<0.4");
			excludedStocks.put("GCC","<0.4");
			excludedStocks.put("GCE","<0.4");
			excludedStocks.put("GCH","<0.4");
			excludedStocks.put("GCOW","<0.4");
			excludedStocks.put("GCV","<0.4");
			excludedStocks.put("GDL","<0.4");
			excludedStocks.put("GDO","<0.4");
			excludedStocks.put("GDP","<0.4");
			excludedStocks.put("GDV-A","<0.4");
			excludedStocks.put("GDV-D","<0.4");
			excludedStocks.put("GDV-G","<0.4");
			excludedStocks.put("GDVD","<0.4");
			excludedStocks.put("GDXS","<0.4");
			excludedStocks.put("GDXX","<0.4");
			excludedStocks.put("GEC","<0.4");
			excludedStocks.put("GECC","<0.4");
			excludedStocks.put("GEF.B","<0.4");
			excludedStocks.put("GEH","<0.4");
			excludedStocks.put("GEM","<0.4");
			excludedStocks.put("GENC","<0.4");
			excludedStocks.put("GENY","<0.4");
			excludedStocks.put("GEO","<0.4");
			excludedStocks.put("GEX","<0.4");
			excludedStocks.put("GF","<0.4");
			excludedStocks.put("GFA","<0.4");
			excludedStocks.put("GFED","<0.4");
			excludedStocks.put("GFNCP","<0.4");
			excludedStocks.put("GFY","<0.4");
			excludedStocks.put("GGN-B","<0.4");
			excludedStocks.put("GGO","<0.4");
			excludedStocks.put("GGP-A","<0.4");
			excludedStocks.put("GGT","<0.4");
			excludedStocks.put("GGT-E","<0.4");
			excludedStocks.put("GGZ","<0.4");
			excludedStocks.put("GHC","<0.4");
			excludedStocks.put("GHII","<0.4");
			excludedStocks.put("GHYB","<0.4");
			excludedStocks.put("GHYG","<0.4");
			excludedStocks.put("GIG","<0.4");
			excludedStocks.put("GIGB","<0.4");
			excludedStocks.put("GIGM","<0.4");
			excludedStocks.put("GII","<0.4");
			excludedStocks.put("GILT","<0.4");
			excludedStocks.put("GIS","<0.4");
			excludedStocks.put("GJR","<0.4");
			excludedStocks.put("GJS","<0.4");
			excludedStocks.put("GJV","<0.4");
			excludedStocks.put("GLADN","<0.4");
			excludedStocks.put("GLBL","<0.4");
			excludedStocks.put("GLBZ","<0.4");
			excludedStocks.put("GLD","<0.4");
			excludedStocks.put("GLDI","<0.4");
			excludedStocks.put("GLF","<0.4");
			excludedStocks.put("GLIBA","<0.4");
			excludedStocks.put("GLL","<0.4");
			excludedStocks.put("GLMD","<0.4");
			excludedStocks.put("GLTR","<0.4");
			excludedStocks.put("GLU","<0.4");
			excludedStocks.put("GLV","<0.4");
			excludedStocks.put("GMF","<0.4");
			excludedStocks.put("GMLPP","<0.4");
			excludedStocks.put("GMOM","<0.4");
			excludedStocks.put("GNE-A","<0.4");
			excludedStocks.put("GNL-A","<0.4");
			excludedStocks.put("GNMA","<0.4");
			excludedStocks.put("GNMX","<0.4");
			excludedStocks.put("GNR","<0.4");
			excludedStocks.put("GNRX","<0.4");
			excludedStocks.put("GNT-A","<0.4");
			excludedStocks.put("GNTY","<0.4");
			excludedStocks.put("GNUS","<0.4");
			excludedStocks.put("GOAU","<0.4");
			excludedStocks.put("GOEX","<0.4");
			excludedStocks.put("GOODM","<0.4");
			excludedStocks.put("GOODO","<0.4");
			excludedStocks.put("GOODP","<0.4");
			excludedStocks.put("GOVT","<0.4");
			excludedStocks.put("GPIC","<0.4");
			excludedStocks.put("GPM","<0.4");
			excludedStocks.put("GPT-A","<0.4");
			excludedStocks.put("GQRE","<0.4");
			excludedStocks.put("GRBK","<0.4");
			excludedStocks.put("GRES","<0.4");
			excludedStocks.put("GRF","<0.4");
			excludedStocks.put("GRI","<0.4");
			excludedStocks.put("GRID","<0.4");
			excludedStocks.put("GRIF","<0.4");
			excludedStocks.put("GRN","<0.4");
			excludedStocks.put("GRR","<0.4");
			excludedStocks.put("GRU","<0.4");
			excludedStocks.put("GRVY","<0.4");
			excludedStocks.put("GRX","<0.4");
			excludedStocks.put("GRX-A","<0.4");
			excludedStocks.put("GRX-B","<0.4");
			excludedStocks.put("GS-B","<0.4");
			excludedStocks.put("GS-C","<0.4");
			excludedStocks.put("GSB","<0.4");
			excludedStocks.put("GSD","<0.4");
			excludedStocks.put("GSEU","<0.4");
			excludedStocks.put("GSEW","<0.4");
			excludedStocks.put("GSH","<0.4");
			excludedStocks.put("GSHT","<0.4");
			excludedStocks.put("GSIE","<0.4");
			excludedStocks.put("GSIT","<0.4");
			excludedStocks.put("GSJY","<0.4");
			excludedStocks.put("GSL-B","<0.4");
			excludedStocks.put("GSP","<0.4");
			excludedStocks.put("GSSC","<0.4");
			excludedStocks.put("GST-A","<0.4");
			excludedStocks.put("GST-B","<0.4");
			excludedStocks.put("GSY","<0.4");
			excludedStocks.put("GTIM","<0.4");
			excludedStocks.put("GTN.A","<0.4");
			excludedStocks.put("GTO","<0.4");
			excludedStocks.put("GTYH","<0.4");
			excludedStocks.put("GULF","<0.4");
			excludedStocks.put("GURE","<0.4");
			excludedStocks.put("GURU","<0.4");
			excludedStocks.put("GUT-A","<0.4");
			excludedStocks.put("GUT-C","<0.4");
			excludedStocks.put("GVAL","<0.4");
			excludedStocks.put("GVI","<0.4");
			excludedStocks.put("GVIP","<0.4");
			excludedStocks.put("GVP","<0.4");
			excludedStocks.put("GWGH","<0.4");
			excludedStocks.put("GWRS","<0.4");
			excludedStocks.put("GWX","<0.4");
			excludedStocks.put("GXF","<0.4");
			excludedStocks.put("GXG","<0.4");
			excludedStocks.put("GYB","<0.4");
			excludedStocks.put("GYC","<0.4");
			excludedStocks.put("GYLD","<0.4");
			excludedStocks.put("GYRO","<0.4");
			excludedStocks.put("GZT","<0.4");
			excludedStocks.put("HACW","<0.4");
			excludedStocks.put("HALL","<0.4");
			excludedStocks.put("HAO","<0.4");
			excludedStocks.put("HAP","<0.4");
			excludedStocks.put("HAWX","<0.4");
			excludedStocks.put("HBANN","<0.4");
			excludedStocks.put("HBCP","<0.4");
			excludedStocks.put("HBK","<0.4");
			excludedStocks.put("HBMD","<0.4");
			excludedStocks.put("HCAC","<0.4");
			excludedStocks.put("HCAP","<0.4");
			excludedStocks.put("HCN","<0.4");
			excludedStocks.put("HCN-I","<0.4");
			excludedStocks.put("HCOM","<0.4");
			excludedStocks.put("HCRF","<0.4");
			excludedStocks.put("HDAW","<0.4");
			excludedStocks.put("HDEF","<0.4");
			excludedStocks.put("HDEZ","<0.4");
			excludedStocks.put("HDG","<0.4");
			excludedStocks.put("HDGE","<0.4");
			excludedStocks.put("HDLV","<0.4");
			excludedStocks.put("HDMV","<0.4");
			excludedStocks.put("HDRW","<0.4");
			excludedStocks.put("HEBT","<0.4");
			excludedStocks.put("HECO","<0.4");
			excludedStocks.put("HEEM","<0.4");
			excludedStocks.put("HEFV","<0.4");
			excludedStocks.put("HEMV","<0.4");
			excludedStocks.put("HES-A","<0.4");
			excludedStocks.put("HEWC","<0.4");
			excludedStocks.put("HEWI","<0.4");
			excludedStocks.put("HEWL","<0.4");
			excludedStocks.put("HEWP","<0.4");
			excludedStocks.put("HEWU","<0.4");
			excludedStocks.put("HFBC","<0.4");
			excludedStocks.put("HFBL","<0.4");
			excludedStocks.put("HFXE","<0.4");
			excludedStocks.put("HFXI","<0.4");
			excludedStocks.put("HFXJ","<0.4");
			excludedStocks.put("HGI","<0.4");
			excludedStocks.put("HGSD","<0.4");
			excludedStocks.put("HGSH","<0.4");
			excludedStocks.put("HHS","<0.4");
			excludedStocks.put("HIFS","<0.4");
			excludedStocks.put("HIHO","<0.4");
			excludedStocks.put("HIPS","<0.4");
			excludedStocks.put("HJV","<0.4");
			excludedStocks.put("HLS","<0.4");
			excludedStocks.put("HLTH","<0.4");
			excludedStocks.put("HMG","<0.4");
			excludedStocks.put("HMI","<0.4");
			excludedStocks.put("HMNF","<0.4");
			excludedStocks.put("HMOP","<0.4");
			excludedStocks.put("HMTA","<0.4");
			excludedStocks.put("HNDL","<0.4");
			excludedStocks.put("HNNA","<0.4");
			excludedStocks.put("HNRG","<0.4");
			excludedStocks.put("HNW","<0.4");
			excludedStocks.put("HOLD","<0.4");
			excludedStocks.put("HOVNP","<0.4");
			excludedStocks.put("HPF","<0.4");
			excludedStocks.put("HPI","<0.4");
			excludedStocks.put("HPJ","<0.4");
			excludedStocks.put("HQCL","<0.4");
			excludedStocks.put("HSCZ","<0.4");
			excludedStocks.put("HSDT","<0.4");
			excludedStocks.put("HSEA","<0.4");
			excludedStocks.put("HSEB","<0.4");
			excludedStocks.put("HSGX","<0.4");
			excludedStocks.put("HSNI","<0.4");
			excludedStocks.put("HSON","<0.4");
			excludedStocks.put("HSPX","<0.4");
			excludedStocks.put("HT-C","<0.4");
			excludedStocks.put("HT-D","<0.4");
			excludedStocks.put("HT-E","<0.4");
			excludedStocks.put("HTM","<0.4");
			excludedStocks.put("HTUS","<0.4");
			excludedStocks.put("HTY","<0.4");
			excludedStocks.put("HUSE","<0.4");
			excludedStocks.put("HUSV","<0.4");
			excludedStocks.put("HVBC","<0.4");
			excludedStocks.put("HWBK","<0.4");
			excludedStocks.put("HX","<0.4");
			excludedStocks.put("HYAC","<0.4");
			excludedStocks.put("HYB","<0.4");
			excludedStocks.put("HYD","<0.4");
			excludedStocks.put("HYDD","<0.4");
			excludedStocks.put("HYEM","<0.4");
			excludedStocks.put("HYGH","<0.4");
			excludedStocks.put("HYGS","<0.4");
			excludedStocks.put("HYHG","<0.4");
			excludedStocks.put("HYIH","<0.4");
			excludedStocks.put("HYLB","<0.4");
			excludedStocks.put("HYLD","<0.4");
			excludedStocks.put("HYLV","<0.4");
			excludedStocks.put("HYMB","<0.4");
			excludedStocks.put("HYND","<0.4");
			excludedStocks.put("HYXE","<0.4");
			excludedStocks.put("HYXU","<0.4");
			excludedStocks.put("IAE","<0.4");
			excludedStocks.put("IAF","<0.4");
			excludedStocks.put("IAGG","<0.4");
			excludedStocks.put("IAI","<0.4");
			excludedStocks.put("IAK","<0.4");
			excludedStocks.put("IAM","<0.4");
			excludedStocks.put("IBA","<0.4");
			excludedStocks.put("IBCC","<0.4");
			excludedStocks.put("IBCD","<0.4");
			excludedStocks.put("IBCE","<0.4");
			excludedStocks.put("IBD","<0.4");
			excludedStocks.put("IBDB","<0.4");
			excludedStocks.put("IBDC","<0.4");
			excludedStocks.put("IBDD","<0.4");
			excludedStocks.put("IBDH","<0.4");
			excludedStocks.put("IBDJ","<0.4");
			excludedStocks.put("IBDK","<0.4");
			excludedStocks.put("IBDL","<0.4");
			excludedStocks.put("IBDM","<0.4");
			excludedStocks.put("IBDN","<0.4");
			excludedStocks.put("IBDO","<0.4");
			excludedStocks.put("IBDP","<0.4");
			excludedStocks.put("IBDQ","<0.4");
			excludedStocks.put("IBDR","<0.4");
			excludedStocks.put("IBDS","<0.4");
			excludedStocks.put("IBKCO","<0.4");
			excludedStocks.put("IBKCP","<0.4");
			excludedStocks.put("IBLN","<0.4");
			excludedStocks.put("IBMG","<0.4");
			excludedStocks.put("IBMH","<0.4");
			excludedStocks.put("IBMI","<0.4");
			excludedStocks.put("IBMJ","<0.4");
			excludedStocks.put("IBMK","<0.4");
			excludedStocks.put("IBML","<0.4");
			excludedStocks.put("IBMM","<0.4");
			excludedStocks.put("IBND","<0.4");
			excludedStocks.put("ICAD","<0.4");
			excludedStocks.put("ICAN","<0.4");
			excludedStocks.put("ICB","<0.4");
			excludedStocks.put("ICBK","<0.4");
			excludedStocks.put("ICCC","<0.4");
			excludedStocks.put("ICCH","<0.4");
			excludedStocks.put("ICOL","<0.4");
			excludedStocks.put("ICOW","<0.4");
			excludedStocks.put("ICSH","<0.4");
			excludedStocks.put("ICVT","<0.4");
			excludedStocks.put("IDEV","<0.4");
			excludedStocks.put("IDHQ","<0.4");
			excludedStocks.put("IDLB","<0.4");
			excludedStocks.put("IDLV","<0.4");
			excludedStocks.put("IDN","<0.4");
			excludedStocks.put("IDOG","<0.4");
			excludedStocks.put("IDSA","<0.4");
			excludedStocks.put("IDSY","<0.4");
			excludedStocks.put("IDU","<0.4");
			excludedStocks.put("IDX","<0.4");
			excludedStocks.put("IEA","<0.4");
			excludedStocks.put("IEC","<0.4");
			excludedStocks.put("IEO","<0.4");
			excludedStocks.put("IESC","<0.4");
			excludedStocks.put("IETC","<0.4");
			excludedStocks.put("IEUS","<0.4");
			excludedStocks.put("IEZ","<0.4");
			excludedStocks.put("IF","<0.4");
			excludedStocks.put("IFEU","<0.4");
			excludedStocks.put("IFLY","<0.4");
			excludedStocks.put("IFMK","<0.4");
			excludedStocks.put("IGE","<0.4");
			excludedStocks.put("IGHG","<0.4");
			excludedStocks.put("IGI","<0.4");
			excludedStocks.put("IGIH","<0.4");
			excludedStocks.put("IGLD","<0.4");
			excludedStocks.put("IGN","<0.4");
			excludedStocks.put("IGRO","<0.4");
			excludedStocks.put("IGV","<0.4");
			excludedStocks.put("IHC","<0.4");
			excludedStocks.put("IHDG","<0.4");
			excludedStocks.put("IHE","<0.4");
			excludedStocks.put("IHF","<0.4");
			excludedStocks.put("IHIT","<0.4");
			excludedStocks.put("IHTA","<0.4");
			excludedStocks.put("IHY","<0.4");
			excludedStocks.put("IID","<0.4");
			excludedStocks.put("IIF","<0.4");
			excludedStocks.put("IIJI","<0.4");
			excludedStocks.put("IJJ","<0.4");
			excludedStocks.put("IJK","<0.4");
			excludedStocks.put("IJS","<0.4");
			excludedStocks.put("IKNX","<0.4");
			excludedStocks.put("ILTB","<0.4");
			excludedStocks.put("IMH","<0.4");
			excludedStocks.put("IMI","<0.4");
			excludedStocks.put("IMLP","<0.4");
			excludedStocks.put("IMMP","<0.4");
			excludedStocks.put("IMOM","<0.4");
			excludedStocks.put("IMRN","<0.4");
			excludedStocks.put("IMTB","<0.4");
			excludedStocks.put("IMTE","<0.4");
			excludedStocks.put("IMTM","<0.4");
			excludedStocks.put("INCO","<0.4");
			excludedStocks.put("INDF","<0.4");
			excludedStocks.put("INDL","<0.4");
			excludedStocks.put("INDU","<0.4");
			excludedStocks.put("INFR","<0.4");
			excludedStocks.put("INFU","<0.4");
			excludedStocks.put("INKM","<0.4");
			excludedStocks.put("INN-C","<0.4");
			excludedStocks.put("INN-D","<0.4");
			excludedStocks.put("INN-E","<0.4");
			excludedStocks.put("INOD","<0.4");
			excludedStocks.put("INP","<0.4");
			excludedStocks.put("INR","<0.4");
			excludedStocks.put("INS","<0.4");
			excludedStocks.put("INSE","<0.4");
			excludedStocks.put("INSI","<0.4");
			excludedStocks.put("INTF","<0.4");
			excludedStocks.put("INTG","<0.4");
			excludedStocks.put("INTT","<0.4");
			excludedStocks.put("INTX","<0.4");
			excludedStocks.put("INUV","<0.4");
			excludedStocks.put("INVE","<0.4");
			excludedStocks.put("INXX","<0.4");
			excludedStocks.put("IOIL","<0.4");
			excludedStocks.put("IONS","<0.4");
			excludedStocks.put("IOO","<0.4");
			excludedStocks.put("IOR","<0.4");
			excludedStocks.put("IPAC","<0.4");
			excludedStocks.put("IPAY","<0.4");
			excludedStocks.put("IPB","<0.4");
			excludedStocks.put("IPDN","<0.4");
			excludedStocks.put("IPE","<0.4");
			excludedStocks.put("IPFF","<0.4");
			excludedStocks.put("IPGP_A","<0.4");
			excludedStocks.put("IPGP_B","<0.4");
			excludedStocks.put("IPIC","<0.4");
			excludedStocks.put("IPL-D","<0.4");
			excludedStocks.put("IPO","<0.4");
			excludedStocks.put("IPXL","<0.4");
			excludedStocks.put("IQDE","<0.4");
			excludedStocks.put("IQDF","<0.4");
			excludedStocks.put("IQDG","<0.4");
			excludedStocks.put("IQDY","<0.4");
			excludedStocks.put("IQLT","<0.4");
			excludedStocks.put("IRCP","<0.4");
			excludedStocks.put("IRIX","<0.4");
			excludedStocks.put("IRL","<0.4");
			excludedStocks.put("IRMD","<0.4");
			excludedStocks.put("IROQ","<0.4");
			excludedStocks.put("ISCF","<0.4");
			excludedStocks.put("ISDR","<0.4");
			excludedStocks.put("ISG","<0.4");
			excludedStocks.put("ISHG","<0.4");
			excludedStocks.put("ISIG","<0.4");
			excludedStocks.put("ISL","<0.4");
			excludedStocks.put("ISNS","<0.4");
			excludedStocks.put("ISRA","<0.4");
			excludedStocks.put("ISRL","<0.4");
			excludedStocks.put("ISSC","<0.4");
			excludedStocks.put("ISTR","<0.4");
			excludedStocks.put("ITCB","<0.4");
			excludedStocks.put("ITE","<0.4");
			excludedStocks.put("ITEK","<0.4");
			excludedStocks.put("ITEQ","<0.4");
			excludedStocks.put("ITIC","<0.4");
			excludedStocks.put("ITM","<0.4");
			excludedStocks.put("IVAL","<0.4");
			excludedStocks.put("IVLU","<0.4");
			excludedStocks.put("IVOG","<0.4");
			excludedStocks.put("IVOO","<0.4");
			excludedStocks.put("IVOV","<0.4");
			excludedStocks.put("IVR-A","<0.4");
			excludedStocks.put("IVR-B","<0.4");
			excludedStocks.put("IVR-C","<0.4");
			excludedStocks.put("IWC","<0.4");
			excludedStocks.put("IWL","<0.4");
			excludedStocks.put("IWX","<0.4");
			excludedStocks.put("IWY","<0.4");
			excludedStocks.put("IXG","<0.4");
			excludedStocks.put("IXJ","<0.4");
			excludedStocks.put("IXN","<0.4");
			excludedStocks.put("IXP","<0.4");
			excludedStocks.put("IXYS","<0.4");
			excludedStocks.put("IYC","<0.4");
			excludedStocks.put("IYJ","<0.4");
			excludedStocks.put("IYK","<0.4");
			excludedStocks.put("IYLD","<0.4");
			excludedStocks.put("IYY","<0.4");
			excludedStocks.put("IZRL","<0.4");
			excludedStocks.put("JASN","<0.4");
			excludedStocks.put("JBK","<0.4");
			excludedStocks.put("JBN","<0.4");
			excludedStocks.put("JBR","<0.4");
			excludedStocks.put("JBRI","<0.4");
			excludedStocks.put("JCO","<0.4");
			excludedStocks.put("JCS","<0.4");
			excludedStocks.put("JCTCF","<0.4");
			excludedStocks.put("JE-A","<0.4");
			excludedStocks.put("JEMD","<0.4");
			excludedStocks.put("JEQ","<0.4");
			excludedStocks.put("JETS","<0.4");
			excludedStocks.put("JHA","<0.4");
			excludedStocks.put("JHD","<0.4");
			excludedStocks.put("JHDG","<0.4");
			excludedStocks.put("JHI","<0.4");
			excludedStocks.put("JHMA","<0.4");
			excludedStocks.put("JHMC","<0.4");
			excludedStocks.put("JHMD","<0.4");
			excludedStocks.put("JHME","<0.4");
			excludedStocks.put("JHMF","<0.4");
			excludedStocks.put("JHMH","<0.4");
			excludedStocks.put("JHMI","<0.4");
			excludedStocks.put("JHML","<0.4");
			excludedStocks.put("JHMM","<0.4");
			excludedStocks.put("JHMT","<0.4");
			excludedStocks.put("JHS","<0.4");
			excludedStocks.put("JHSC","<0.4");
			excludedStocks.put("JHX","<0.4");
			excludedStocks.put("JHY","<0.4");
			excludedStocks.put("JJA","<0.4");
			excludedStocks.put("JJAB","<0.4");
			excludedStocks.put("JJC","<0.4");
			excludedStocks.put("JJE","<0.4");
			excludedStocks.put("JJG","<0.4");
			excludedStocks.put("JJGB","<0.4");
			excludedStocks.put("JJM","<0.4");
			excludedStocks.put("JJMB","<0.4");
			excludedStocks.put("JJN","<0.4");
			excludedStocks.put("JJSB","<0.4");
			excludedStocks.put("JJU","<0.4");
			excludedStocks.put("JJUB","<0.4");
			excludedStocks.put("JKD","<0.4");
			excludedStocks.put("JKE","<0.4");
			excludedStocks.put("JKF","<0.4");
			excludedStocks.put("JKG","<0.4");
			excludedStocks.put("JKH","<0.4");
			excludedStocks.put("JKI","<0.4");
			excludedStocks.put("JKJ","<0.4");
			excludedStocks.put("JKK","<0.4");
			excludedStocks.put("JKL","<0.4");
			excludedStocks.put("JLS","<0.4");
			excludedStocks.put("JMBA","<0.4");
			excludedStocks.put("JMLP","<0.4");
			excludedStocks.put("JMM","<0.4");
			excludedStocks.put("JMP","<0.4");
			excludedStocks.put("JMT","<0.4");
			excludedStocks.put("JMU","<0.4");
			excludedStocks.put("JO","<0.4");
			excludedStocks.put("JOB","<0.4");
			excludedStocks.put("JPEM","<0.4");
			excludedStocks.put("JPEU","<0.4");
			excludedStocks.put("JPGE","<0.4");
			excludedStocks.put("JPHF","<0.4");
			excludedStocks.put("JPHY","<0.4");
			excludedStocks.put("JPIN","<0.4");
			excludedStocks.put("JPME","<0.4");
			excludedStocks.put("JPMF","<0.4");
			excludedStocks.put("JPMV","<0.4");
			excludedStocks.put("JPN","<0.4");
			excludedStocks.put("JPNL","<0.4");
			excludedStocks.put("JPSE","<0.4");
			excludedStocks.put("JPT","<0.4");
			excludedStocks.put("JPUS","<0.4");
			excludedStocks.put("JPXN","<0.4");
			excludedStocks.put("JRJC","<0.4");
			excludedStocks.put("JRS","<0.4");
			excludedStocks.put("JSM","<0.4");
			excludedStocks.put("JSMD","<0.4");
			excludedStocks.put("JSML","<0.4");
			excludedStocks.put("JTD","<0.4");
			excludedStocks.put("JTPY","<0.4");
			excludedStocks.put("JUNO","<0.4");
			excludedStocks.put("JVA","<0.4");
			excludedStocks.put("JW","<0.4");
			excludedStocks.put("JX","<0.4");
			excludedStocks.put("JXI","<0.4");
			excludedStocks.put("JXSB","<0.4");
			excludedStocks.put("KAAC","<0.4");
			excludedStocks.put("KALV","<0.4");
			excludedStocks.put("KAP","<0.4");
			excludedStocks.put("KARS","<0.4");
			excludedStocks.put("KBA","<0.4");
			excludedStocks.put("KBLM","<0.4");
			excludedStocks.put("KBWP","<0.4");
			excludedStocks.put("KBWR","<0.4");
			excludedStocks.put("KCE","<0.4");
			excludedStocks.put("KCNY","<0.4");
			excludedStocks.put("KED","<0.4");
			excludedStocks.put("KEMQ","<0.4");
			excludedStocks.put("KEN","<0.4");
			excludedStocks.put("KEQU","<0.4");
			excludedStocks.put("KEY-I","<0.4");
			excludedStocks.put("KF","<0.4");
			excludedStocks.put("KFFB","<0.4");
			excludedStocks.put("KFS","<0.4");
			excludedStocks.put("KFYP","<0.4");
			excludedStocks.put("KGJI","<0.4");
			excludedStocks.put("KGRN","<0.4");
			excludedStocks.put("KIM-I","<0.4");
			excludedStocks.put("KIM-J","<0.4");
			excludedStocks.put("KIM-K","<0.4");
			excludedStocks.put("KIM-L","<0.4");
			excludedStocks.put("KINS","<0.4");
			excludedStocks.put("KIQ","<0.4");
			excludedStocks.put("KKR-A","<0.4");
			excludedStocks.put("KKR-B","<0.4");
			excludedStocks.put("KLDW","<0.4");
			excludedStocks.put("KMDA","<0.4");
			excludedStocks.put("KMM","<0.4");
			excludedStocks.put("KNG","<0.4");
			excludedStocks.put("KNOW","<0.4");
			excludedStocks.put("KOIN","<0.4");
			excludedStocks.put("KOL","<0.4");
			excludedStocks.put("KOLD","<0.4");
			excludedStocks.put("KONA","<0.4");
			excludedStocks.put("KONE","<0.4");
			excludedStocks.put("KOR","<0.4");
			excludedStocks.put("KORU","<0.4");
			excludedStocks.put("KOSS","<0.4");
			excludedStocks.put("KRMA","<0.4");
			excludedStocks.put("KROO","<0.4");
			excludedStocks.put("KRP","<0.4");
			excludedStocks.put("KRYS","<0.4");
			excludedStocks.put("KSA","<0.4");
			excludedStocks.put("KSM","<0.4");
			excludedStocks.put("KST","<0.4");
			excludedStocks.put("KTCC","<0.4");
			excludedStocks.put("KTH","<0.4");
			excludedStocks.put("KTN","<0.4");
			excludedStocks.put("KTP","<0.4");
			excludedStocks.put("KURE","<0.4");
			excludedStocks.put("KVHI","<0.4");
			excludedStocks.put("KXI","<0.4");
			excludedStocks.put("KYN-F","<0.4");
			excludedStocks.put("KZIA","<0.4");
			excludedStocks.put("LAKE","<0.4");
			excludedStocks.put("LANDP","<0.4");
			excludedStocks.put("LAQ","<0.4");
			excludedStocks.put("LARE","<0.4");
			excludedStocks.put("LARK","<0.4");
			excludedStocks.put("LAWS","<0.4");
			excludedStocks.put("LAZY","<0.4");
			excludedStocks.put("LBDC","<0.4");
			excludedStocks.put("LBIX","<0.4");
			excludedStocks.put("LBJ","<0.4");
			excludedStocks.put("LCA","<0.4");
			excludedStocks.put("LCM","<0.4");
			excludedStocks.put("LCNB","<0.4");
			excludedStocks.put("LDF","<0.4");
			excludedStocks.put("LDRI","<0.4");
			excludedStocks.put("LDRS","<0.4");
			excludedStocks.put("LDUR","<0.4");
			excludedStocks.put("LEAD","<0.4");
			excludedStocks.put("LEE","<0.4");
			excludedStocks.put("LEGR","<0.4");
			excludedStocks.put("LEMB","<0.4");
			excludedStocks.put("LENS","<0.4");
			excludedStocks.put("LEU","<0.4");
			excludedStocks.put("LEVB","<0.4");
			excludedStocks.put("LEVL","<0.4");
			excludedStocks.put("LFEQ","<0.4");
			excludedStocks.put("LFVN","<0.4");
			excludedStocks.put("LGC","<0.4");
			excludedStocks.put("LGCYP","<0.4");
			excludedStocks.put("LGF","<0.4");
			excludedStocks.put("LGI","<0.4");
			excludedStocks.put("LGL","<0.4");
			excludedStocks.put("LGLV","<0.4");
			excludedStocks.put("LHC","<0.4");
			excludedStocks.put("LHO-I","<0.4");
			excludedStocks.put("LHO-J","<0.4");
			excludedStocks.put("LIFE","<0.4");
			excludedStocks.put("LINC","<0.4");
			excludedStocks.put("LINK","<0.4");
			excludedStocks.put("LITB","<0.4");
			excludedStocks.put("LIVE","<0.4");
			excludedStocks.put("LIVX","<0.4");
			excludedStocks.put("LKOR","<0.4");
			excludedStocks.put("LLIT","<0.4");
			excludedStocks.put("LLSC","<0.4");
			excludedStocks.put("LLSP","<0.4");
			excludedStocks.put("LMB","<0.4");
			excludedStocks.put("LMNR","<0.4");
			excludedStocks.put("LMRKN","<0.4");
			excludedStocks.put("LMRKO","<0.4");
			excludedStocks.put("LMRKP","<0.4");
			excludedStocks.put("LND","<0.4");
			excludedStocks.put("LOAN","<0.4");
			excludedStocks.put("LOGO","<0.4");
			excludedStocks.put("LOOP","<0.4");
			excludedStocks.put("LOR","<0.4");
			excludedStocks.put("LOV","<0.4");
			excludedStocks.put("LOWC","<0.4");
			excludedStocks.put("LPTX","<0.4");
			excludedStocks.put("LQDH","<0.4");
			excludedStocks.put("LRAD","<0.4");
			excludedStocks.put("LRET","<0.4");
			excludedStocks.put("LRGF","<0.4");
			excludedStocks.put("LSBK","<0.4");
			excludedStocks.put("LSTK","<0.4");
			excludedStocks.put("LSVX","<0.4");
			excludedStocks.put("LTL","<0.4");
			excludedStocks.put("LTN","<0.4");
			excludedStocks.put("LTPZ","<0.4");
			excludedStocks.put("LTRX","<0.4");
			excludedStocks.put("LTS-A","<0.4");
			excludedStocks.put("LUB","<0.4");
			excludedStocks.put("LVHB","<0.4");
			excludedStocks.put("LVHE","<0.4");
			excludedStocks.put("LVL","<0.4");
			excludedStocks.put("LVNTA","<0.4");
			excludedStocks.put("LWAY","<0.4");
			excludedStocks.put("LXP-C","<0.4");
			excludedStocks.put("LYL","<0.4");
			excludedStocks.put("MAA-I","<0.4");
			excludedStocks.put("MAB","<0.4");
			excludedStocks.put("MACQ","<0.4");
			excludedStocks.put("MADI","<0.4");
			excludedStocks.put("MAGA","<0.4");
			excludedStocks.put("MAGS","<0.4");
			excludedStocks.put("MAMS","<0.4");
			excludedStocks.put("MANU","<0.4");
			excludedStocks.put("MBCN","<0.4");
			excludedStocks.put("MBFIO","<0.4");
			excludedStocks.put("MBG","<0.4");
			excludedStocks.put("MBSD","<0.4");
			excludedStocks.put("MBTF","<0.4");
			excludedStocks.put("MBVX","<0.4");
			excludedStocks.put("MCI","<0.4");
			excludedStocks.put("MCN","<0.4");
			excludedStocks.put("MCRO","<0.4");
			excludedStocks.put("MCX","<0.4");
			excludedStocks.put("MDGS","<0.4");
			excludedStocks.put("MDLY","<0.4");
			excludedStocks.put("MDWD","<0.4");
			excludedStocks.put("MDYG","<0.4");
			excludedStocks.put("MDYV","<0.4");
			excludedStocks.put("MEAR","<0.4");
			excludedStocks.put("MELR","<0.4");
			excludedStocks.put("MER-P","<0.4");
			excludedStocks.put("MESO","<0.4");
			excludedStocks.put("MET-A","<0.4");
			excludedStocks.put("METC","<0.4");
			excludedStocks.put("MEXX","<0.4");
			excludedStocks.put("MFA-B","<0.4");
			excludedStocks.put("MFCB","<0.4");
			excludedStocks.put("MFD","<0.4");
			excludedStocks.put("MFDX","<0.4");
			excludedStocks.put("MFEM","<0.4");
			excludedStocks.put("MFNC","<0.4");
			excludedStocks.put("MFO","<0.4");
			excludedStocks.put("MFSF","<0.4");
			excludedStocks.put("MFT","<0.4");
			excludedStocks.put("MFUS","<0.4");
			excludedStocks.put("MFV","<0.4");
			excludedStocks.put("MGF","<0.4");
			excludedStocks.put("MGIC","<0.4");
			excludedStocks.put("MGV","<0.4");
			excludedStocks.put("MGYR","<0.4");
			excludedStocks.put("MH-A","<0.4");
			excludedStocks.put("MH-C","<0.4");
			excludedStocks.put("MH-D","<0.4");
			excludedStocks.put("MHD","<0.4");
			excludedStocks.put("MHE","<0.4");
			excludedStocks.put("MHF","<0.4");
			excludedStocks.put("MHH","<0.4");
			excludedStocks.put("MICR","<0.4");
			excludedStocks.put("MICT","<0.4");
			excludedStocks.put("MIDU","<0.4");
			excludedStocks.put("MIDZ","<0.4");
			excludedStocks.put("MILN","<0.4");
			excludedStocks.put("MINC","<0.4");
			excludedStocks.put("MIND","<0.4");
			excludedStocks.put("MINDP","<0.4");
			excludedStocks.put("MINT","<0.4");
			excludedStocks.put("MIW","<0.4");
			excludedStocks.put("MIY","<0.4");
			excludedStocks.put("MJCO","<0.4");
			excludedStocks.put("MKC.V","<0.4");
			excludedStocks.put("MKGI","<0.4");
			excludedStocks.put("MLAB","<0.4");
			excludedStocks.put("MLN","<0.4");
			excludedStocks.put("MLP","<0.4");
			excludedStocks.put("MLPC","<0.4");
			excludedStocks.put("MLPG","<0.4");
			excludedStocks.put("MLPQ","<0.4");
			excludedStocks.put("MLPX","<0.4");
			excludedStocks.put("MLPY","<0.4");
			excludedStocks.put("MLSS","<0.4");
			excludedStocks.put("MLVF","<0.4");
			excludedStocks.put("MMAC","<0.4");
			excludedStocks.put("MMDM","<0.4");
			excludedStocks.put("MMTM","<0.4");
			excludedStocks.put("MMV","<0.4");
			excludedStocks.put("MN","<0.4");
			excludedStocks.put("MNA","<0.4");
			excludedStocks.put("MNE","<0.4");
			excludedStocks.put("MNI","<0.4");
			excludedStocks.put("MNP","<0.4");
			excludedStocks.put("MNR-C","<0.4");
			excludedStocks.put("MNTX","<0.4");
			excludedStocks.put("MOAT","<0.4");
			excludedStocks.put("MOC","<0.4");
			excludedStocks.put("MOFG","<0.4");
			excludedStocks.put("MOG","<0.4");
			excludedStocks.put("MOG.B","<0.4");
			excludedStocks.put("MOO","<0.4");
			excludedStocks.put("MOR","<0.4");
			excludedStocks.put("MORL","<0.4");
			excludedStocks.put("MORT","<0.4");
			excludedStocks.put("MOSC","<0.4");
			excludedStocks.put("MOTI","<0.4");
			excludedStocks.put("MOTS","<0.4");
			excludedStocks.put("MOXC","<0.4");
			excludedStocks.put("MP-D","<0.4");
			excludedStocks.put("MPA","<0.4");
			excludedStocks.put("MPAC","<0.4");
			excludedStocks.put("MPB","<0.4");
			excludedStocks.put("MPCT","<0.4");
			excludedStocks.put("MPV","<0.4");
			excludedStocks.put("MPVD","<0.4");
			excludedStocks.put("MPX","<0.4");
			excludedStocks.put("MRBK","<0.4");
			excludedStocks.put("MRIN","<0.4");
			excludedStocks.put("MRRL","<0.4");
			excludedStocks.put("MRUS","<0.4");
			excludedStocks.put("MS-F","<0.4");
			excludedStocks.put("MS-G","<0.4");
			excludedStocks.put("MSBF","<0.4");
			excludedStocks.put("MSF","<0.4");
			excludedStocks.put("MSL","<0.4");
			excludedStocks.put("MSN","<0.4");
			excludedStocks.put("MSON","<0.4");
			excludedStocks.put("MSP","<0.4");
			excludedStocks.put("MTBCP","<0.4");
			excludedStocks.put("MTEC","<0.4");
			excludedStocks.put("MTEM","<0.4");
			excludedStocks.put("MTEX","<0.4");
			excludedStocks.put("MTFB","<0.4");
			excludedStocks.put("MTGEP","<0.4");
			excludedStocks.put("MTL","<0.4");
			excludedStocks.put("MTR","<0.4");
			excludedStocks.put("MTSL","<0.4");
			excludedStocks.put("MTT","<0.4");
			excludedStocks.put("MTU","<0.4");
			excludedStocks.put("MUDS","<0.4");
			excludedStocks.put("MUH","<0.4");
			excludedStocks.put("MULE","<0.4");
			excludedStocks.put("MUNI","<0.4");
			excludedStocks.put("MUS","<0.4");
			excludedStocks.put("MVBF","<0.4");
			excludedStocks.put("MVC","<0.4");
			excludedStocks.put("MVO","<0.4");
			excludedStocks.put("MVT","<0.4");
			excludedStocks.put("MVV","<0.4");
			excludedStocks.put("MXC","<0.4");
			excludedStocks.put("MXDU","<0.4");
			excludedStocks.put("MXE","<0.4");
			excludedStocks.put("MXF","<0.4");
			excludedStocks.put("MXI","<0.4");
			excludedStocks.put("MYDP","<0.4");
			excludedStocks.put("MYDX","<0.4");
			excludedStocks.put("MYF","<0.4");
			excludedStocks.put("MYHI","<0.4");
			excludedStocks.put("MYJ","<0.4");
			excludedStocks.put("MYLI","<0.4");
			excludedStocks.put("MYOS","<0.4");
			excludedStocks.put("MYSZ","<0.4");
			excludedStocks.put("MYY","<0.4");
			excludedStocks.put("MZA","<0.4");
			excludedStocks.put("MZF","<0.4");
			excludedStocks.put("MZZ","<0.4");
			excludedStocks.put("NAII","<0.4");
			excludedStocks.put("NAIL","<0.4");
			excludedStocks.put("NAKD","<0.4");
			excludedStocks.put("NANR","<0.4");
			excludedStocks.put("NAOV","<0.4");
			excludedStocks.put("NASH","<0.4");
			excludedStocks.put("NATH","<0.4");
			excludedStocks.put("NATR","<0.4");
			excludedStocks.put("NAUH","<0.4");
			excludedStocks.put("NAZ","<0.4");
			excludedStocks.put("NBD","<0.4");
			excludedStocks.put("NBH","<0.4");
			excludedStocks.put("NBN","<0.4");
			excludedStocks.put("NBO","<0.4");
			excludedStocks.put("NBW","<0.4");
			excludedStocks.put("NBY","<0.4");
			excludedStocks.put("NC","<0.4");
			excludedStocks.put("NCA","<0.4");
			excludedStocks.put("NCB","<0.4");
			excludedStocks.put("NCBS","<0.4");
			excludedStocks.put("NDRA","<0.4");
			excludedStocks.put("NEAR","<0.4");
			excludedStocks.put("NEBU","<0.4");
			excludedStocks.put("NEE-Q","<0.4");
			excludedStocks.put("NEE-R","<0.4");
			excludedStocks.put("NEN","<0.4");
			excludedStocks.put("NES","<0.4");
			excludedStocks.put("NESR","<0.4");
			excludedStocks.put("NETS","<0.4");
			excludedStocks.put("NEXT","<0.4");
			excludedStocks.put("NFLT","<0.4");
			excludedStocks.put("NFO","<0.4");
			excludedStocks.put("NFRA","<0.4");
			excludedStocks.put("NGE","<0.4");
			excludedStocks.put("NGHCN","<0.4");
			excludedStocks.put("NGHCO","<0.4");
			excludedStocks.put("NGHCP","<0.4");
			excludedStocks.put("NGL-B","<0.4");
			excludedStocks.put("NH","<0.4");
			excludedStocks.put("NHA","<0.4");
			excludedStocks.put("NHLD","<0.4");
			excludedStocks.put("NHS","<0.4");
			excludedStocks.put("NIB","<0.4");
			excludedStocks.put("NICK","<0.4");
			excludedStocks.put("NIM","<0.4");
			excludedStocks.put("NINI","<0.4");
			excludedStocks.put("NIQ","<0.4");
			excludedStocks.put("NITE","<0.4");
			excludedStocks.put("NJV","<0.4");
			excludedStocks.put("NKG","<0.4");
			excludedStocks.put("NKSH","<0.4");
			excludedStocks.put("NLR","<0.4");
			excludedStocks.put("NLY-C","<0.4");
			excludedStocks.put("NLY-D","<0.4");
			excludedStocks.put("NM-G","<0.4");
			excludedStocks.put("NM-H","<0.4");
			excludedStocks.put("NMI","<0.4");
			excludedStocks.put("NMRD","<0.4");
			excludedStocks.put("NMS","<0.4");
			excludedStocks.put("NMT","<0.4");
			excludedStocks.put("NMY","<0.4");
			excludedStocks.put("NNC","<0.4");
			excludedStocks.put("NNDM","<0.4");
			excludedStocks.put("NNN-F","<0.4");
			excludedStocks.put("NNY","<0.4");
			excludedStocks.put("NODK","<0.4");
			excludedStocks.put("NORW","<0.4");
			excludedStocks.put("NOVN","<0.4");
			excludedStocks.put("NPV","<0.4");
			excludedStocks.put("NQ","<0.4");
			excludedStocks.put("NRIM","<0.4");
			excludedStocks.put("NRT","<0.4");
			excludedStocks.put("NS-A","<0.4");
			excludedStocks.put("NS-B","<0.4");
			excludedStocks.put("NS-C","<0.4");
			excludedStocks.put("NSA-A","<0.4");
			excludedStocks.put("NSEC","<0.4");
			excludedStocks.put("NSSC","<0.4");
			excludedStocks.put("NSYS","<0.4");
			excludedStocks.put("NTC","<0.4");
			excludedStocks.put("NTEC","<0.4");
			excludedStocks.put("NTES","<0.4");
			excludedStocks.put("NTIC","<0.4");
			excludedStocks.put("NTIP","<0.4");
			excludedStocks.put("NTL","<0.4");
			excludedStocks.put("NTN","<0.4");
			excludedStocks.put("NTP","<0.4");
			excludedStocks.put("NTRP","<0.4");
			excludedStocks.put("NTRSP","<0.4");
			excludedStocks.put("NTWK","<0.4");
			excludedStocks.put("NTX","<0.4");
			excludedStocks.put("NTZ","<0.4");
			excludedStocks.put("NUAG","<0.4");
			excludedStocks.put("NUBD","<0.4");
			excludedStocks.put("NUEM","<0.4");
			excludedStocks.put("NULG","<0.4");
			excludedStocks.put("NULV","<0.4");
			excludedStocks.put("NUM","<0.4");
			excludedStocks.put("NUMG","<0.4");
			excludedStocks.put("NUMV","<0.4");
			excludedStocks.put("NUO","<0.4");
			excludedStocks.put("NURE","<0.4");
			excludedStocks.put("NUSA","<0.4");
			excludedStocks.put("NUSC","<0.4");
			excludedStocks.put("NUW","<0.4");
			excludedStocks.put("NVEC","<0.4");
			excludedStocks.put("NVLN","<0.4");
			excludedStocks.put("NVMM","<0.4");
			excludedStocks.put("NVUS","<0.4");
			excludedStocks.put("NWFL","<0.4");
			excludedStocks.put("NWLI","<0.4");
			excludedStocks.put("NXC","<0.4");
			excludedStocks.put("NXN","<0.4");
			excludedStocks.put("NXP","<0.4");
			excludedStocks.put("NXQ","<0.4");
			excludedStocks.put("NXR","<0.4");
			excludedStocks.put("NYF","<0.4");
			excludedStocks.put("NYH","<0.4");
			excludedStocks.put("NYMTN","<0.4");
			excludedStocks.put("NYMTO","<0.4");
			excludedStocks.put("NYMTP","<0.4");
			excludedStocks.put("NYNY","<0.4");
			excludedStocks.put("NYV","<0.4");
			excludedStocks.put("OASI","<0.4");
			excludedStocks.put("OASM","<0.4");
			excludedStocks.put("OBAS","<0.4");
			excludedStocks.put("OBLN","<0.4");
			excludedStocks.put("OBOR","<0.4");
			excludedStocks.put("OBSV","<0.4");
			excludedStocks.put("OCC","<0.4");
			excludedStocks.put("OCIP","<0.4");
			excludedStocks.put("OCX","<0.4");
			excludedStocks.put("ODC","<0.4");
			excludedStocks.put("OESX","<0.4");
			excludedStocks.put("OEUR","<0.4");
			excludedStocks.put("OEX","<0.4");
			excludedStocks.put("OFED","<0.4");
			excludedStocks.put("OFG-B","<0.4");
			excludedStocks.put("OFG-D","<0.4");
			excludedStocks.put("OFLX","<0.4");
			excludedStocks.put("OGCP","<0.4");
			excludedStocks.put("OHAI","<0.4");
			excludedStocks.put("OIIM","<0.4");
			excludedStocks.put("OIL","<0.4");
			excludedStocks.put("OILB","<0.4");
			excludedStocks.put("OILK","<0.4");
			excludedStocks.put("OLEM","<0.4");
			excludedStocks.put("OLO","<0.4");
			excludedStocks.put("OMAD","<0.4");
			excludedStocks.put("OMAM","<0.4");
			excludedStocks.put("OME","<0.4");
			excludedStocks.put("OMFL","<0.4");
			excludedStocks.put("OMNT","<0.4");
			excludedStocks.put("ONEO","<0.4");
			excludedStocks.put("ONEV","<0.4");
			excludedStocks.put("ONEY","<0.4");
			excludedStocks.put("ONP","<0.4");
			excludedStocks.put("ONS","<0.4");
			excludedStocks.put("ONTL","<0.4");
			excludedStocks.put("OOMA","<0.4");
			excludedStocks.put("OPES","<0.4");
			excludedStocks.put("OPHC","<0.4");
			excludedStocks.put("OPNT","<0.4");
			excludedStocks.put("OPOF","<0.4");
			excludedStocks.put("OPP","<0.4");
			excludedStocks.put("OREX","<0.4");
			excludedStocks.put("ORG","<0.4");
			excludedStocks.put("ORGS","<0.4");
			excludedStocks.put("ORM","<0.4");
			excludedStocks.put("ORMP","<0.4");
			excludedStocks.put("ORPN","<0.4");
			excludedStocks.put("ORRF","<0.4");
			excludedStocks.put("OSBCP","<0.4");
			excludedStocks.put("OSN","<0.4");
			excludedStocks.put("OSPR","<0.4");
			excludedStocks.put("OSS","<0.4");
			excludedStocks.put("OTEL","<0.4");
			excludedStocks.put("OTTW","<0.4");
			excludedStocks.put("OUNZ","<0.4");
			excludedStocks.put("OUSA","<0.4");
			excludedStocks.put("OUSM","<0.4");
			excludedStocks.put("OVBC","<0.4");
			excludedStocks.put("OVID","<0.4");
			excludedStocks.put("OVLY","<0.4");
			excludedStocks.put("OXBR","<0.4");
			excludedStocks.put("OXLCM","<0.4");
			excludedStocks.put("PAF","<0.4");
			excludedStocks.put("PAGG","<0.4");
			excludedStocks.put("PAI","<0.4");
			excludedStocks.put("PAK","<0.4");
			excludedStocks.put("PALL","<0.4");
			excludedStocks.put("PANL","<0.4");
			excludedStocks.put("PATI","<0.4");
			excludedStocks.put("PAVE","<0.4");
			excludedStocks.put("PAVM","<0.4");
			excludedStocks.put("PBB","<0.4");
			excludedStocks.put("PBBI","<0.4");
			excludedStocks.put("PBCTP","<0.4");
			excludedStocks.put("PBD","<0.4");
			excludedStocks.put("PBE","<0.4");
			excludedStocks.put("PBHC","<0.4");
			excludedStocks.put("PBIB","<0.4");
			excludedStocks.put("PBIP","<0.4");
			excludedStocks.put("PBJ","<0.4");
			excludedStocks.put("PBP","<0.4");
			excludedStocks.put("PBS","<0.4");
			excludedStocks.put("PBSK","<0.4");
			excludedStocks.put("PBW","<0.4");
			excludedStocks.put("PCEF","<0.4");
			excludedStocks.put("PCF","<0.4");
			excludedStocks.put("PCG-A","<0.4");
			excludedStocks.put("PCG-B","<0.4");
			excludedStocks.put("PCG-C","<0.4");
			excludedStocks.put("PCG-D","<0.4");
			excludedStocks.put("PCG-E","<0.4");
			excludedStocks.put("PCG-G","<0.4");
			excludedStocks.put("PCG-H","<0.4");
			excludedStocks.put("PCG-I","<0.4");
			excludedStocks.put("PCK","<0.4");
			excludedStocks.put("PCLN","<0.4");
			excludedStocks.put("PCM","<0.4");
			excludedStocks.put("PCTI","<0.4");
			excludedStocks.put("PCYO","<0.4");
			excludedStocks.put("PDEX","<0.4");
			excludedStocks.put("PDLB","<0.4");
			excludedStocks.put("PDN","<0.4");
			excludedStocks.put("PDVW","<0.4");
			excludedStocks.put("PEB-C","<0.4");
			excludedStocks.put("PEB-D","<0.4");
			excludedStocks.put("PEBK","<0.4");
			excludedStocks.put("PED","<0.4");
			excludedStocks.put("PEI-B","<0.4");
			excludedStocks.put("PEI-C","<0.4");
			excludedStocks.put("PEI-D","<0.4");
			excludedStocks.put("PEJ","<0.4");
			excludedStocks.put("PEK","<0.4");
			excludedStocks.put("PESI","<0.4");
			excludedStocks.put("PETZ","<0.4");
			excludedStocks.put("PEX","<0.4");
			excludedStocks.put("PEZ","<0.4");
			excludedStocks.put("PFBI","<0.4");
			excludedStocks.put("PFD","<0.4");
			excludedStocks.put("PFFD","<0.4");
			excludedStocks.put("PFFR","<0.4");
			excludedStocks.put("PFH","<0.4");
			excludedStocks.put("PFI","<0.4");
			excludedStocks.put("PFIG","<0.4");
			excludedStocks.put("PFIN","<0.4");
			excludedStocks.put("PFIS","<0.4");
			excludedStocks.put("PFK","<0.4");
			excludedStocks.put("PFM","<0.4");
			excludedStocks.put("PFO","<0.4");
			excludedStocks.put("PFSW","<0.4");
			excludedStocks.put("PFXF","<0.4");
			excludedStocks.put("PGAL","<0.4");
			excludedStocks.put("PGEM","<0.4");
			excludedStocks.put("PGF","<0.4");
			excludedStocks.put("PGHY","<0.4");
			excludedStocks.put("PGJ","<0.4");
			excludedStocks.put("PGLC","<0.4");
			excludedStocks.put("PGM","<0.4");
			excludedStocks.put("PGZ","<0.4");
			excludedStocks.put("PHB","<0.4");
			excludedStocks.put("PHDG","<0.4");
			excludedStocks.put("PHII","<0.4");
			excludedStocks.put("PHIIK","<0.4");
			excludedStocks.put("PHX","<0.4");
			excludedStocks.put("PICB","<0.4");
			excludedStocks.put("PICK","<0.4");
			excludedStocks.put("PIH","<0.4");
			excludedStocks.put("PIHPP","<0.4");
			excludedStocks.put("PILL","<0.4");
			excludedStocks.put("PIN","<0.4");
			excludedStocks.put("PINC","<0.4");
			excludedStocks.put("PIO","<0.4");
			excludedStocks.put("PIY","<0.4");
			excludedStocks.put("PIZ","<0.4");
			excludedStocks.put("PJP","<0.4");
			excludedStocks.put("PKB","<0.4");
			excludedStocks.put("PKBK","<0.4");
			excludedStocks.put("PKOH","<0.4");
			excludedStocks.put("PLBC","<0.4");
			excludedStocks.put("PLND","<0.4");
			excludedStocks.put("PLPC","<0.4");
			excludedStocks.put("PLPM","<0.4");
			excludedStocks.put("PLW","<0.4");
			excludedStocks.put("PLXP","<0.4");
			excludedStocks.put("PLYM","<0.4");
			excludedStocks.put("PMBC","<0.4");
			excludedStocks.put("PMD","<0.4");
			excludedStocks.put("PMOM","<0.4");
			excludedStocks.put("PMPT","<0.4");
			excludedStocks.put("PMR","<0.4");
			excludedStocks.put("PMT-A","<0.4");
			excludedStocks.put("PMT-B","<0.4");
			excludedStocks.put("PMTS","<0.4");
			excludedStocks.put("PNBK","<0.4");
			excludedStocks.put("PNC-Q","<0.4");
			excludedStocks.put("PNF","<0.4");
			excludedStocks.put("PNI","<0.4");
			excludedStocks.put("PNRG","<0.4");
			excludedStocks.put("PNTR","<0.4");
			excludedStocks.put("POLA","<0.4");
			excludedStocks.put("POT","<0.4");
			excludedStocks.put("PPA","<0.4");
			excludedStocks.put("PPHM","<0.4");
			excludedStocks.put("PPIH","<0.4");
			excludedStocks.put("PPLC","<0.4");
			excludedStocks.put("PPLN","<0.4");
			excludedStocks.put("PPLT","<0.4");
			excludedStocks.put("PPSC","<0.4");
			excludedStocks.put("PPSI","<0.4");
			excludedStocks.put("PPTY","<0.4");
			excludedStocks.put("PPX","<0.4");
			excludedStocks.put("PQ","<0.4");
			excludedStocks.put("PRAN","<0.4");
			excludedStocks.put("PRCP","<0.4");
			excludedStocks.put("PRE-F","<0.4");
			excludedStocks.put("PRE-G","<0.4");
			excludedStocks.put("PRE-H","<0.4");
			excludedStocks.put("PRE-I","<0.4");
			excludedStocks.put("PREF","<0.4");
			excludedStocks.put("PRH","<0.4");
			excludedStocks.put("PRIM","<0.4");
			excludedStocks.put("PRKR","<0.4");
			excludedStocks.put("PRME","<0.4");
			excludedStocks.put("PRNT","<0.4");
			excludedStocks.put("PROV","<0.4");
			excludedStocks.put("PRPH","<0.4");
			excludedStocks.put("PRPL","<0.4");
			excludedStocks.put("PRQR","<0.4");
			excludedStocks.put("PRSS","<0.4");
			excludedStocks.put("PRTO","<0.4");
			excludedStocks.put("PSA-A","<0.4");
			excludedStocks.put("PSA-B","<0.4");
			excludedStocks.put("PSA-C","<0.4");
			excludedStocks.put("PSA-D","<0.4");
			excludedStocks.put("PSA-E","<0.4");
			excludedStocks.put("PSA-F","<0.4");
			excludedStocks.put("PSA-G","<0.4");
			excludedStocks.put("PSA-U","<0.4");
			excludedStocks.put("PSA-V","<0.4");
			excludedStocks.put("PSA-X","<0.4");
			excludedStocks.put("PSA-Y","<0.4");
			excludedStocks.put("PSA-Z","<0.4");
			excludedStocks.put("PSAU","<0.4");
			excludedStocks.put("PSB-U","<0.4");
			excludedStocks.put("PSB-V","<0.4");
			excludedStocks.put("PSB-W","<0.4");
			excludedStocks.put("PSB-X","<0.4");
			excludedStocks.put("PSB-Y","<0.4");
			excludedStocks.put("PSC","<0.4");
			excludedStocks.put("PSCC","<0.4");
			excludedStocks.put("PSCD","<0.4");
			excludedStocks.put("PSCE","<0.4");
			excludedStocks.put("PSCF","<0.4");
			excludedStocks.put("PSCI","<0.4");
			excludedStocks.put("PSCM","<0.4");
			excludedStocks.put("PSCT","<0.4");
			excludedStocks.put("PSCU","<0.4");
			excludedStocks.put("PSDV","<0.4");
			excludedStocks.put("PSJ","<0.4");
			excludedStocks.put("PSK","<0.4");
			excludedStocks.put("PSL","<0.4");
			excludedStocks.put("PSLV","<0.4");
			excludedStocks.put("PSMC","<0.4");
			excludedStocks.put("PSP","<0.4");
			excludedStocks.put("PSR","<0.4");
			excludedStocks.put("PST","<0.4");
			excludedStocks.put("PSTG","<0.4");
			excludedStocks.put("PTEU","<0.4");
			excludedStocks.put("PTF","<0.4");
			excludedStocks.put("PTH","<0.4");
			excludedStocks.put("PTIE","<0.4");
			excludedStocks.put("PTM","<0.4");
			excludedStocks.put("PTMC","<0.4");
			excludedStocks.put("PTNQ","<0.4");
			excludedStocks.put("PTNR","<0.4");
			excludedStocks.put("PTSI","<0.4");
			excludedStocks.put("PTX","<0.4");
			excludedStocks.put("PUI","<0.4");
			excludedStocks.put("PUK-A","<0.4");
			excludedStocks.put("PULS","<0.4");
			excludedStocks.put("PUTW","<0.4");
			excludedStocks.put("PUW","<0.4");
			excludedStocks.put("PVAL","<0.4");
			excludedStocks.put("PVBC","<0.4");
			excludedStocks.put("PVI","<0.4");
			excludedStocks.put("PW","<0.4");
			excludedStocks.put("PWB","<0.4");
			excludedStocks.put("PWC","<0.4");
			excludedStocks.put("PWOD","<0.4");
			excludedStocks.put("PWV","<0.4");
			excludedStocks.put("PWZ","<0.4");
			excludedStocks.put("PXE","<0.4");
			excludedStocks.put("PXF","<0.4");
			excludedStocks.put("PXH","<0.4");
			excludedStocks.put("PXI","<0.4");
			excludedStocks.put("PXJ","<0.4");
			excludedStocks.put("PXLG","<0.4");
			excludedStocks.put("PXLV","<0.4");
			excludedStocks.put("PXMG","<0.4");
			excludedStocks.put("PXMV","<0.4");
			excludedStocks.put("PXQ","<0.4");
			excludedStocks.put("PXR","<0.4");
			excludedStocks.put("PXSG","<0.4");
			excludedStocks.put("PXSV","<0.4");
			excludedStocks.put("PY","<0.4");
			excludedStocks.put("PYN","<0.4");
			excludedStocks.put("PYS","<0.4");
			excludedStocks.put("PYT","<0.4");
			excludedStocks.put("PYZ","<0.4");
			excludedStocks.put("PZA","<0.4");
			excludedStocks.put("PZC","<0.4");
			excludedStocks.put("PZD","<0.4");
			excludedStocks.put("PZE","<0.4");
			excludedStocks.put("PZG","<0.4");
			excludedStocks.put("PZI","<0.4");
			excludedStocks.put("PZN","<0.4");
			excludedStocks.put("PZT","<0.4");
			excludedStocks.put("QADB","<0.4");
			excludedStocks.put("QAI","<0.4");
			excludedStocks.put("QARP","<0.4");
			excludedStocks.put("QAT","<0.4");
			excludedStocks.put("QBAK","<0.4");
			excludedStocks.put("QCAN","<0.4");
			excludedStocks.put("QCLN","<0.4");
			excludedStocks.put("QDEF","<0.4");
			excludedStocks.put("QDEU","<0.4");
			excludedStocks.put("QDF","<0.4");
			excludedStocks.put("QDYN","<0.4");
			excludedStocks.put("QEFA","<0.4");
			excludedStocks.put("QEMM","<0.4");
			excludedStocks.put("QGBR","<0.4");
			excludedStocks.put("QINC","<0.4");
			excludedStocks.put("QJPN","<0.4");
			excludedStocks.put("QLC","<0.4");
			excludedStocks.put("QLS","<0.4");
			excludedStocks.put("QLTA","<0.4");
			excludedStocks.put("QMN","<0.4");
			excludedStocks.put("QMOM","<0.4");
			excludedStocks.put("QQQC","<0.4");
			excludedStocks.put("QQQE","<0.4");
			excludedStocks.put("QQXT","<0.4");
			excludedStocks.put("QRHC","<0.4");
			excludedStocks.put("QSY","<0.4");
			excludedStocks.put("QTS-A","<0.4");
			excludedStocks.put("QUAL","<0.4");
			excludedStocks.put("QUMU","<0.4");
			excludedStocks.put("QUS","<0.4");
			excludedStocks.put("QVAL","<0.4");
			excludedStocks.put("QVCA","<0.4");
			excludedStocks.put("QXGG","<0.4");
			excludedStocks.put("QXTR","<0.4");
			excludedStocks.put("RAAX","<0.4");
			excludedStocks.put("RALS","<0.4");
			excludedStocks.put("RAND","<0.4");
			excludedStocks.put("RAS-A","<0.4");
			excludedStocks.put("RAS-B","<0.4");
			excludedStocks.put("RAS-C","<0.4");
			excludedStocks.put("RAVE","<0.4");
			excludedStocks.put("RAVI","<0.4");
			excludedStocks.put("RBC","<0.4");
			excludedStocks.put("RBCAA","<0.4");
			excludedStocks.put("RBCN","<0.4");
			excludedStocks.put("RBNC","<0.4");
			excludedStocks.put("RBS-S","<0.4");
			excludedStocks.put("RCD","<0.4");
			excludedStocks.put("RCG","<0.4");
			excludedStocks.put("RCMT","<0.4");
			excludedStocks.put("RDCM","<0.4");
			excludedStocks.put("RDIV","<0.4");
			excludedStocks.put("RDS","<0.4");
			excludedStocks.put("REED","<0.4");
			excludedStocks.put("REET","<0.4");
			excludedStocks.put("REFA","<0.4");
			excludedStocks.put("REFR","<0.4");
			excludedStocks.put("REGL","<0.4");
			excludedStocks.put("REIS","<0.4");
			excludedStocks.put("REK","<0.4");
			excludedStocks.put("RELL","<0.4");
			excludedStocks.put("RELV","<0.4");
			excludedStocks.put("REML","<0.4");
			excludedStocks.put("REMX","<0.4");
			excludedStocks.put("RETL","<0.4");
			excludedStocks.put("REW","<0.4");
			excludedStocks.put("REXX","<0.4");
			excludedStocks.put("REZ","<0.4");
			excludedStocks.put("RF-A","<0.4");
			excludedStocks.put("RFAP","<0.4");
			excludedStocks.put("RFCI","<0.4");
			excludedStocks.put("RFDA","<0.4");
			excludedStocks.put("RFEM","<0.4");
			excludedStocks.put("RFEU","<0.4");
			excludedStocks.put("RFFC","<0.4");
			excludedStocks.put("RFG","<0.4");
			excludedStocks.put("RFI","<0.4");
			excludedStocks.put("RFIL","<0.4");
			excludedStocks.put("RFT","<0.4");
			excludedStocks.put("RFUN","<0.4");
			excludedStocks.put("RFV","<0.4");
			excludedStocks.put("RGC","<0.4");
			excludedStocks.put("RGCO","<0.4");
			excludedStocks.put("RGI","<0.4");
			excludedStocks.put("RGLB","<0.4");
			excludedStocks.put("RGT","<0.4");
			excludedStocks.put("RHE-A","<0.4");
			excludedStocks.put("RHS","<0.4");
			excludedStocks.put("RIBT","<0.4");
			excludedStocks.put("RIGS","<0.4");
			excludedStocks.put("RINF","<0.4");
			excludedStocks.put("RISE","<0.4");
			excludedStocks.put("RIV","<0.4");
			excludedStocks.put("RJA","<0.4");
			excludedStocks.put("RJI","<0.4");
			excludedStocks.put("RJN","<0.4");
			excludedStocks.put("RJZ","<0.4");
			excludedStocks.put("RLJ-A","<0.4");
			excludedStocks.put("RLJE","<0.4");
			excludedStocks.put("RLY","<0.4");
			excludedStocks.put("RMBL","<0.4");
			excludedStocks.put("RMCF","<0.4");
			excludedStocks.put("RMGN","<0.4");
			excludedStocks.put("RMNI","<0.4");
			excludedStocks.put("RNDM","<0.4");
			excludedStocks.put("RNDV","<0.4");
			excludedStocks.put("RNEM","<0.4");
			excludedStocks.put("RNET","<0.4");
			excludedStocks.put("RNG","<0.4");
			excludedStocks.put("RNLC","<0.4");
			excludedStocks.put("RNMC","<0.4");
			excludedStocks.put("RNR-C","<0.4");
			excludedStocks.put("RNR-E","<0.4");
			excludedStocks.put("RNSC","<0.4");
			excludedStocks.put("RNWK","<0.4");
			excludedStocks.put("ROAM","<0.4");
			excludedStocks.put("ROBT","<0.4");
			excludedStocks.put("RODM","<0.4");
			excludedStocks.put("ROGS","<0.4");
			excludedStocks.put("ROL","<0.4");
			excludedStocks.put("ROOF","<0.4");
			excludedStocks.put("RORE","<0.4");
			excludedStocks.put("ROSE","<0.4");
			excludedStocks.put("ROSG","<0.4");
			excludedStocks.put("ROUS","<0.4");
			excludedStocks.put("RPRX","<0.4");
			excludedStocks.put("RPT-D","<0.4");
			excludedStocks.put("RPUT","<0.4");
			excludedStocks.put("RPV","<0.4");
			excludedStocks.put("RSO-B","<0.4");
			excludedStocks.put("RSO-C","<0.4");
			excludedStocks.put("RSXJ","<0.4");
			excludedStocks.put("RT","<0.4");
			excludedStocks.put("RTCM","<0.4");
			excludedStocks.put("RTH","<0.4");
			excludedStocks.put("RTM","<0.4");
			excludedStocks.put("RTRE","<0.4");
			excludedStocks.put("RUSHB","<0.4");
			excludedStocks.put("RVEN","<0.4");
			excludedStocks.put("RVNU","<0.4");
			excludedStocks.put("RVP","<0.4");
			excludedStocks.put("RVRS","<0.4");
			excludedStocks.put("RWC","<0.4");
			excludedStocks.put("RWGE","<0.4");
			excludedStocks.put("RWJ","<0.4");
			excludedStocks.put("RWK","<0.4");
			excludedStocks.put("RWL","<0.4");
			excludedStocks.put("RWO","<0.4");
			excludedStocks.put("RWW","<0.4");
			excludedStocks.put("RXI","<0.4");
			excludedStocks.put("RXL","<0.4");
			excludedStocks.put("RXN-A","<0.4");
			excludedStocks.put("RYAM","<0.4");
			excludedStocks.put("RYE","<0.4");
			excludedStocks.put("RYF","<0.4");
			excludedStocks.put("RYH","<0.4");
			excludedStocks.put("RYJ","<0.4");
			excludedStocks.put("RYU","<0.4");
			excludedStocks.put("RZA","<0.4");
			excludedStocks.put("RZG","<0.4");
			excludedStocks.put("RZV","<0.4");
			excludedStocks.put("SAA","<0.4");
			excludedStocks.put("SACH","<0.4");
			excludedStocks.put("SAEX","<0.4");
			excludedStocks.put("SAGG","<0.4");
			excludedStocks.put("SAL","<0.4");
			excludedStocks.put("SAMG","<0.4");
			excludedStocks.put("SAN-A","<0.4");
			excludedStocks.put("SAN-B","<0.4");
			excludedStocks.put("SAN-C","<0.4");
			excludedStocks.put("SAN-I","<0.4");
			excludedStocks.put("SANW","<0.4");
			excludedStocks.put("SAR","<0.4");
			excludedStocks.put("SAUC","<0.4");
			excludedStocks.put("SB-C","<0.4");
			excludedStocks.put("SB-D","<0.4");
			excludedStocks.put("SBB","<0.4");
			excludedStocks.put("SBBX","<0.4");
			excludedStocks.put("SBFG","<0.4");
			excludedStocks.put("SBI","<0.4");
			excludedStocks.put("SBIO","<0.4");
			excludedStocks.put("SBOT","<0.4");
			excludedStocks.put("SBPH","<0.4");
			excludedStocks.put("SBR","<0.4");
			excludedStocks.put("SBRAP","<0.4");
			excludedStocks.put("SBUX","<0.4");
			excludedStocks.put("SCA","<0.4");
			excludedStocks.put("SCAC","<0.4");
			excludedStocks.put("SCD","<0.4");
			excludedStocks.put("SCE-B","<0.4");
			excludedStocks.put("SCE-C","<0.4");
			excludedStocks.put("SCE-D","<0.4");
			excludedStocks.put("SCE-E","<0.4");
			excludedStocks.put("SCE-H","<0.4");
			excludedStocks.put("SCE-J","<0.4");
			excludedStocks.put("SCE-K","<0.4");
			excludedStocks.put("SCE-L","<0.4");
			excludedStocks.put("SCHC","<0.4");
			excludedStocks.put("SCHK","<0.4");
			excludedStocks.put("SCHO","<0.4");
			excludedStocks.put("SCHP","<0.4");
			excludedStocks.put("SCHR","<0.4");
			excludedStocks.put("SCHZ","<0.4");
			excludedStocks.put("SCID","<0.4");
			excludedStocks.put("SCIF","<0.4");
			excludedStocks.put("SCIJ","<0.4");
			excludedStocks.put("SCIN","<0.4");
			excludedStocks.put("SCIU","<0.4");
			excludedStocks.put("SCIX","<0.4");
			excludedStocks.put("SCJ","<0.4");
			excludedStocks.put("SCKT","<0.4");
			excludedStocks.put("SCMP","<0.4");
			excludedStocks.put("SCX","<0.4");
			excludedStocks.put("SDCI","<0.4");
			excludedStocks.put("SDD","<0.4");
			excludedStocks.put("SDEM","<0.4");
			excludedStocks.put("SDIV","<0.4");
			excludedStocks.put("SDOG","<0.4");
			excludedStocks.put("SDP","<0.4");
			excludedStocks.put("SDPI","<0.4");
			excludedStocks.put("SDVY","<0.4");
			excludedStocks.put("SDYL","<0.4");
			excludedStocks.put("SEA","<0.4");
			excludedStocks.put("SEB","<0.4");
			excludedStocks.put("SECO","<0.4");
			excludedStocks.put("SECT","<0.4");
			excludedStocks.put("SEF","<0.4");
			excludedStocks.put("SEII","<0.4");
			excludedStocks.put("SELF","<0.4");
			excludedStocks.put("SENEA","<0.4");
			excludedStocks.put("SES","<0.4");
			excludedStocks.put("SF-A","<0.4");
			excludedStocks.put("SFBC","<0.4");
			excludedStocks.put("SFST","<0.4");
			excludedStocks.put("SGA","<0.4");
			excludedStocks.put("SGB","<0.4");
			excludedStocks.put("SGBX","<0.4");
			excludedStocks.put("SGC","<0.4");
			excludedStocks.put("SGDJ","<0.4");
			excludedStocks.put("SGDM","<0.4");
			excludedStocks.put("SGEN","<0.4");
			excludedStocks.put("SGF","<0.4");
			excludedStocks.put("SGG","<0.4");
			excludedStocks.put("SGGB","<0.4");
			excludedStocks.put("SGLB","<0.4");
			excludedStocks.put("SGMA","<0.4");
			excludedStocks.put("SGOC","<0.4");
			excludedStocks.put("SGOL","<0.4");
			excludedStocks.put("SGQI","<0.4");
			excludedStocks.put("SGRP","<0.4");
			excludedStocks.put("SGY","<0.4");
			excludedStocks.put("SHAG","<0.4");
			excludedStocks.put("SHBI","<0.4");
			excludedStocks.put("SHE","<0.4");
			excludedStocks.put("SHLO","<0.4");
			excludedStocks.put("SHM","<0.4");
			excludedStocks.put("SHNY","<0.4");
			excludedStocks.put("SHO-E","<0.4");
			excludedStocks.put("SHO-F","<0.4");
			excludedStocks.put("SHOS","<0.4");
			excludedStocks.put("SHSP","<0.4");
			excludedStocks.put("SHYD","<0.4");
			excludedStocks.put("SHYL","<0.4");
			excludedStocks.put("SIEB","<0.4");
			excludedStocks.put("SIF","<0.4");
			excludedStocks.put("SIFI","<0.4");
			excludedStocks.put("SIJ","<0.4");
			excludedStocks.put("SIL","<0.4");
			excludedStocks.put("SILJ","<0.4");
			excludedStocks.put("SIM","<0.4");
			excludedStocks.put("SIVR","<0.4");
			excludedStocks.put("SIZE","<0.4");
			excludedStocks.put("SJB","<0.4");
			excludedStocks.put("SJIU","<0.4");
			excludedStocks.put("SKF","<0.4");
			excludedStocks.put("SKIS","<0.4");
			excludedStocks.put("SKOR","<0.4");
			excludedStocks.put("SKYS","<0.4");
			excludedStocks.put("SLCT","<0.4");
			excludedStocks.put("SLG-I","<0.4");
			excludedStocks.put("SLGL","<0.4");
			excludedStocks.put("SLIM","<0.4");
			excludedStocks.put("SLMBP","<0.4");
			excludedStocks.put("SLNO","<0.4");
			excludedStocks.put("SLVO","<0.4");
			excludedStocks.put("SLVP","<0.4");
			excludedStocks.put("SLX","<0.4");
			excludedStocks.put("SLY","<0.4");
			excludedStocks.put("SLYG","<0.4");
			excludedStocks.put("SMB","<0.4");
			excludedStocks.put("SMBC","<0.4");
			excludedStocks.put("SMCP","<0.4");
			excludedStocks.put("SMDD","<0.4");
			excludedStocks.put("SMDV","<0.4");
			excludedStocks.put("SMED","<0.4");
			excludedStocks.put("SMEZ","<0.4");
			excludedStocks.put("SMHD","<0.4");
			excludedStocks.put("SMIT","<0.4");
			excludedStocks.put("SML","<0.4");
			excludedStocks.put("SMLF","<0.4");
			excludedStocks.put("SMLL","<0.4");
			excludedStocks.put("SMLV","<0.4");
			excludedStocks.put("SMMD","<0.4");
			excludedStocks.put("SMMF","<0.4");
			excludedStocks.put("SMMU","<0.4");
			excludedStocks.put("SMMV","<0.4");
			excludedStocks.put("SMN","<0.4");
			excludedStocks.put("SMTS","<0.4");
			excludedStocks.put("SMTX","<0.4");
			excludedStocks.put("SNDE","<0.4");
			excludedStocks.put("SNES","<0.4");
			excludedStocks.put("SNFCA","<0.4");
			excludedStocks.put("SNGX","<0.4");
			excludedStocks.put("SNI","<0.4");
			excludedStocks.put("SNMX","<0.4");
			excludedStocks.put("SNOA","<0.4");
			excludedStocks.put("SNSR","<0.4");
			excludedStocks.put("SNV-C","<0.4");
			excludedStocks.put("SOFO","<0.4");
			excludedStocks.put("SOHOB","<0.4");
			excludedStocks.put("SOHOO","<0.4");
			excludedStocks.put("SOIL","<0.4");
			excludedStocks.put("SOL","<0.4");
			excludedStocks.put("SOR","<0.4");
			excludedStocks.put("SOV-C","<0.4");
			excludedStocks.put("SOVB","<0.4");
			excludedStocks.put("SOYB","<0.4");
			excludedStocks.put("SPAB","<0.4");
			excludedStocks.put("SPCB","<0.4");
			excludedStocks.put("SPDN","<0.4");
			excludedStocks.put("SPDV","<0.4");
			excludedStocks.put("SPE","<0.4");
			excludedStocks.put("SPE-B","<0.4");
			excludedStocks.put("SPFF","<0.4");
			excludedStocks.put("SPG-J","<0.4");
			excludedStocks.put("SPHB","<0.4");
			excludedStocks.put("SPHQ","<0.4");
			excludedStocks.put("SPIB","<0.4");
			excludedStocks.put("SPIL","<0.4");
			excludedStocks.put("SPKEP","<0.4");
			excludedStocks.put("SPLB","<0.4");
			excludedStocks.put("SPLG","<0.4");
			excludedStocks.put("SPLP","<0.4");
			excludedStocks.put("SPLX","<0.4");
			excludedStocks.put("SPMO","<0.4");
			excludedStocks.put("SPNS","<0.4");
			excludedStocks.put("SPPP","<0.4");
			excludedStocks.put("SPRT","<0.4");
			excludedStocks.put("SPSB","<0.4");
			excludedStocks.put("SPTL","<0.4");
			excludedStocks.put("SPTS","<0.4");
			excludedStocks.put("SPUN","<0.4");
			excludedStocks.put("SPUU","<0.4");
			excludedStocks.put("SPVU","<0.4");
			excludedStocks.put("SPXE","<0.4");
			excludedStocks.put("SPXH","<0.4");
			excludedStocks.put("SPXT","<0.4");
			excludedStocks.put("SPYB","<0.4");
			excludedStocks.put("SPYX","<0.4");
			excludedStocks.put("SQZZ","<0.4");
			excludedStocks.put("SRC-A","<0.4");
			excludedStocks.put("SRCLP","<0.4");
			excludedStocks.put("SRDX","<0.4");
			excludedStocks.put("SRE-A","<0.4");
			excludedStocks.put("SRET","<0.4");
			excludedStocks.put("SRF","<0.4");
			excludedStocks.put("SRG-A","<0.4");
			excludedStocks.put("SRLN","<0.4");
			excludedStocks.put("SRS","<0.4");
			excludedStocks.put("SRTS","<0.4");
			excludedStocks.put("SRV","<0.4");
			excludedStocks.put("SSFN","<0.4");
			excludedStocks.put("SSG","<0.4");
			excludedStocks.put("SSKN","<0.4");
			excludedStocks.put("SSLJ","<0.4");
			excludedStocks.put("SSNI","<0.4");
			excludedStocks.put("SSNT","<0.4");
			excludedStocks.put("SSW-D","<0.4");
			excludedStocks.put("SSW-E","<0.4");
			excludedStocks.put("SSW-G","<0.4");
			excludedStocks.put("SSW-H","<0.4");
			excludedStocks.put("SSY","<0.4");
			excludedStocks.put("STAF","<0.4");
			excludedStocks.put("STB","<0.4");
			excludedStocks.put("STDY","<0.4");
			excludedStocks.put("STI-A","<0.4");
			excludedStocks.put("STIP","<0.4");
			excludedStocks.put("STKS","<0.4");
			excludedStocks.put("STL-A","<0.4");
			excludedStocks.put("STLR","<0.4");
			excludedStocks.put("STN","<0.4");
			excludedStocks.put("STOT","<0.4");
			excludedStocks.put("STPZ","<0.4");
			excludedStocks.put("STRM","<0.4");
			excludedStocks.put("STRP","<0.4");
			excludedStocks.put("STRS","<0.4");
			excludedStocks.put("STRT","<0.4");
			excludedStocks.put("STT-C","<0.4");
			excludedStocks.put("SUB","<0.4");
			excludedStocks.put("SUMR","<0.4");
			excludedStocks.put("SUSA","<0.4");
			excludedStocks.put("SUSB","<0.4");
			excludedStocks.put("SUSC","<0.4");
			excludedStocks.put("SVBI","<0.4");
			excludedStocks.put("SVT","<0.4");
			excludedStocks.put("SVVC","<0.4");
			excludedStocks.put("SWNC","<0.4");
			excludedStocks.put("SWP","<0.4");
			excludedStocks.put("SWZ","<0.4");
			excludedStocks.put("SYE","<0.4");
			excludedStocks.put("SYG","<0.4");
			excludedStocks.put("SYLD","<0.4");
			excludedStocks.put("SYNL","<0.4");
			excludedStocks.put("SYPR","<0.4");
			excludedStocks.put("SYT","<0.4");
			excludedStocks.put("SYV","<0.4");
			excludedStocks.put("TAC","<0.4");
			excludedStocks.put("TACT","<0.4");
			excludedStocks.put("TAIL","<0.4");
			excludedStocks.put("TAIT","<0.4");
			excludedStocks.put("TAN","<0.4");
			excludedStocks.put("TAO","<0.4");
			excludedStocks.put("TAP.A","<0.4");
			excludedStocks.put("TAPR","<0.4");
			//excludedStocks.put("TARO","<0.4");
			excludedStocks.put("TATT","<0.4");
			excludedStocks.put("TAYD","<0.4");
			excludedStocks.put("TBLU","<0.4");
			excludedStocks.put("TBNK","<0.4");
			excludedStocks.put("TBX","<0.4");
			excludedStocks.put("TCBIP","<0.4");
			excludedStocks.put("TCCO","<0.4");
			excludedStocks.put("TCF-D","<0.4");
			excludedStocks.put("TCFC","<0.4");
			excludedStocks.put("TCGP","<0.4");
			excludedStocks.put("TCHF","<0.4");
			excludedStocks.put("TCI","<0.4");
			excludedStocks.put("TCO-J","<0.4");
			excludedStocks.put("TCO-K","<0.4");
			excludedStocks.put("TCTL","<0.4");
			excludedStocks.put("TDE","<0.4");
			excludedStocks.put("TDI","<0.4");
			excludedStocks.put("TDJ","<0.4");
			excludedStocks.put("TDTF","<0.4");
			excludedStocks.put("TDTT","<0.4");
			excludedStocks.put("TERM","<0.4");
			excludedStocks.put("TESO","<0.4");
			excludedStocks.put("TETF","<0.4");
			excludedStocks.put("TFI","<0.4");
			excludedStocks.put("TFLO","<0.4");
			excludedStocks.put("TGC","<0.4");
			excludedStocks.put("TGEN","<0.4");
			excludedStocks.put("TGLS","<0.4");
			excludedStocks.put("TGP-A","<0.4");
			excludedStocks.put("TGP-B","<0.4");
			excludedStocks.put("THFF","<0.4");
			excludedStocks.put("THST","<0.4");
			excludedStocks.put("TI","<0.4");
			excludedStocks.put("TI.A","<0.4");
			excludedStocks.put("TIBR","<0.4");
			excludedStocks.put("TIG","<0.4");
			excludedStocks.put("TIK","<0.4");
			excludedStocks.put("TIL","<0.4");
			excludedStocks.put("TILT","<0.4");
			excludedStocks.put("TIME","<0.4");
			excludedStocks.put("TIPT","<0.4");
			excludedStocks.put("TIPX","<0.4");
			excludedStocks.put("TIPZ","<0.4");
			excludedStocks.put("TISA","<0.4");
			excludedStocks.put("TKAT","<0.4");
			excludedStocks.put("TKF","<0.4");
			excludedStocks.put("TLDH","<0.4");
			excludedStocks.put("TLEH","<0.4");
			excludedStocks.put("TLF","<0.4");
			excludedStocks.put("TLH","<0.4");
			excludedStocks.put("TLI","<0.4");
			excludedStocks.put("TLTD","<0.4");
			excludedStocks.put("TLTE","<0.4");
			excludedStocks.put("TMFC","<0.4");
			excludedStocks.put("TMHC","<0.4");
			excludedStocks.put("TNH","<0.4");
			excludedStocks.put("TNP-C","<0.4");
			excludedStocks.put("TNP-D","<0.4");
			excludedStocks.put("TNP-E","<0.4");
			excludedStocks.put("TOK","<0.4");
			excludedStocks.put("TOLZ","<0.4");
			excludedStocks.put("TOO-A","<0.4");
			excludedStocks.put("TOO-B","<0.4");
			excludedStocks.put("TOO-E","<0.4");
			excludedStocks.put("TOTL","<0.4");
			excludedStocks.put("TPGE","<0.4");
			excludedStocks.put("TPGH","<0.4");
			excludedStocks.put("TPHS","<0.4");
			excludedStocks.put("TPIV","<0.4");
			excludedStocks.put("TPL","<0.4");
			excludedStocks.put("TPOR","<0.4");
			excludedStocks.put("TPVG","<0.4");
			excludedStocks.put("TPYP","<0.4");
			excludedStocks.put("TPZ","<0.4");
			excludedStocks.put("TRCB","<0.4");
			excludedStocks.put("TRIB","<0.4");
			excludedStocks.put("TRIL","<0.4");
			excludedStocks.put("TRNS","<0.4");
			excludedStocks.put("TRPX","<0.4");
			excludedStocks.put("TRSK","<0.4");
			excludedStocks.put("TRT","<0.4");
			excludedStocks.put("TSBK","<0.4");
			excludedStocks.put("TSCAP","<0.4");
			excludedStocks.put("TSI","<0.4");
			excludedStocks.put("TSLF","<0.4");
			excludedStocks.put("TSQ","<0.4");
			excludedStocks.put("TSRI","<0.4");
			excludedStocks.put("TTAC","<0.4");
			excludedStocks.put("TTAI","<0.4");
			excludedStocks.put("TTCD","<0.4");
			excludedStocks.put("TTCS","<0.4");
			excludedStocks.put("TTEN","<0.4");
			excludedStocks.put("TTF","<0.4");
			excludedStocks.put("TTFS","<0.4");
			excludedStocks.put("TTGD","<0.4");
			excludedStocks.put("TTHC","<0.4");
			excludedStocks.put("TTIN","<0.4");
			excludedStocks.put("TTMT","<0.4");
			excludedStocks.put("TTRE","<0.4");
			excludedStocks.put("TTT","<0.4");
			excludedStocks.put("TTTK","<0.4");
			excludedStocks.put("TTTS","<0.4");
			excludedStocks.put("TTUT","<0.4");
			excludedStocks.put("TURN","<0.4");
			excludedStocks.put("TUSA","<0.4");
			excludedStocks.put("TUZ","<0.4");
			excludedStocks.put("TVC","<0.4");
			excludedStocks.put("TVE","<0.4");
			excludedStocks.put("TVIZ","<0.4");
			excludedStocks.put("TWMC","<0.4");
			excludedStocks.put("TWN","<0.4");
			excludedStocks.put("TWO-A","<0.4");
			excludedStocks.put("TWO-B","<0.4");
			excludedStocks.put("TWO-C","<0.4");
			excludedStocks.put("TXBE","<0.4");
			excludedStocks.put("TXBM","<0.4");
			excludedStocks.put("TXCT","<0.4");
			excludedStocks.put("TXDE","<0.4");
			excludedStocks.put("TXDV","<0.4");
			excludedStocks.put("TXGM","<0.4");
			excludedStocks.put("TXOE","<0.4");
			excludedStocks.put("TXPR","<0.4");
			excludedStocks.put("TXSI","<0.4");
			excludedStocks.put("TYBS","<0.4");
			excludedStocks.put("TYD","<0.4");
			excludedStocks.put("TYHT","<0.4");
			excludedStocks.put("TYO","<0.4");
			excludedStocks.put("UAE","<0.4");
			excludedStocks.put("UAMY","<0.4");
			excludedStocks.put("UBC","<0.4");
			excludedStocks.put("UBCP","<0.4");
			excludedStocks.put("UBFO","<0.4");
			excludedStocks.put("UBOH","<0.4");
			excludedStocks.put("UBOT","<0.4");
			excludedStocks.put("UBP","<0.4");
			excludedStocks.put("UBP-G","<0.4");
			excludedStocks.put("UBP-H","<0.4");
			excludedStocks.put("UBR","<0.4");
			excludedStocks.put("UBRT","<0.4");
			excludedStocks.put("UBT","<0.4");
			excludedStocks.put("UCBA","<0.4");
			excludedStocks.put("UCC","<0.4");
			excludedStocks.put("UCI","<0.4");
			excludedStocks.put("UDN","<0.4");
			excludedStocks.put("UEUR","<0.4");
			excludedStocks.put("UEVM","<0.4");
			excludedStocks.put("UFAB","<0.4");
			excludedStocks.put("UFPT","<0.4");
			excludedStocks.put("UGA","<0.4");
			excludedStocks.put("UGE","<0.4");
			excludedStocks.put("UGL","<0.4");
			excludedStocks.put("UHN","<0.4");
			excludedStocks.put("UITB","<0.4");
			excludedStocks.put("UIVM","<0.4");
			excludedStocks.put("ULBI","<0.4");
			excludedStocks.put("ULE","<0.4");
			excludedStocks.put("ULH","<0.4");
			excludedStocks.put("ULST","<0.4");
			excludedStocks.put("ULVM","<0.4");
			excludedStocks.put("UMDD","<0.4");
			excludedStocks.put("UMH-B","<0.4");
			excludedStocks.put("UMH-C","<0.4");
			excludedStocks.put("UMH-D","<0.4");
			excludedStocks.put("UNAM","<0.4");
			excludedStocks.put("UNB","<0.4");
			excludedStocks.put("UNL","<0.4");
			excludedStocks.put("UNTY","<0.4");
			excludedStocks.put("UONEK","<0.4");
			excludedStocks.put("UPV","<0.4");
			excludedStocks.put("UPW","<0.4");
			excludedStocks.put("URE","<0.4");
			excludedStocks.put("URR","<0.4");
			excludedStocks.put("URTH","<0.4");
			excludedStocks.put("USAI","<0.4");
			excludedStocks.put("USAS","<0.4");
			excludedStocks.put("USATP","<0.4");
			excludedStocks.put("USB-A","<0.4");
			excludedStocks.put("USB-O","<0.4");
			excludedStocks.put("USCI","<0.4");
			excludedStocks.put("USD","<0.4");
			excludedStocks.put("USDP","<0.4");
			excludedStocks.put("USDU","<0.4");
			excludedStocks.put("USEG","<0.4");
			excludedStocks.put("USEQ","<0.4");
			excludedStocks.put("USFR","<0.4");
			excludedStocks.put("USL","<0.4");
			excludedStocks.put("USLB","<0.4");
			excludedStocks.put("USLM","<0.4");
			excludedStocks.put("USMC","<0.4");
			excludedStocks.put("USMF","<0.4");
			excludedStocks.put("USMV","<0.4");
			excludedStocks.put("USOD","<0.4");
			excludedStocks.put("USOI","<0.4");
			excludedStocks.put("USOU","<0.4");
			excludedStocks.put("USRT","<0.4");
			excludedStocks.put("UST","<0.4");
			excludedStocks.put("USTB","<0.4");
			excludedStocks.put("USVM","<0.4");
			excludedStocks.put("UTI","<0.4");
			excludedStocks.put("UTLF","<0.4");
			excludedStocks.put("UTMD","<0.4");
			excludedStocks.put("UTSL","<0.4");
			excludedStocks.put("UUU","<0.4");
			excludedStocks.put("UWN","<0.4");
			excludedStocks.put("UXI","<0.4");
			excludedStocks.put("UYM","<0.4");
			excludedStocks.put("UZA","<0.4");
			excludedStocks.put("VALU","<0.4");
			excludedStocks.put("VALX","<0.4");
			excludedStocks.put("VAMO","<0.4");
			excludedStocks.put("VBF","<0.4");
			excludedStocks.put("VBK","<0.4");
			excludedStocks.put("VBND","<0.4");
			excludedStocks.put("VCF","<0.4");
			excludedStocks.put("VCO","<0.4");
			excludedStocks.put("VCRA","<0.4");
			excludedStocks.put("VEAC","<0.4");
			excludedStocks.put("VEGA","<0.4");
			excludedStocks.put("VEGI","<0.4");
			excludedStocks.put("VFL","<0.4");
			excludedStocks.put("VFLQ","<0.4");
			excludedStocks.put("VFMF","<0.4");
			excludedStocks.put("VFMO","<0.4");
			excludedStocks.put("VFMV","<0.4");
			excludedStocks.put("VFQY","<0.4");
			excludedStocks.put("VFVA","<0.4");
			excludedStocks.put("VIDI","<0.4");
			excludedStocks.put("VIIZ","<0.4");
			excludedStocks.put("VIOG","<0.4");
			excludedStocks.put("VIOO","<0.4");
			excludedStocks.put("VIOV","<0.4");
			excludedStocks.put("VIRC","<0.4");
			excludedStocks.put("VISI","<0.4");
			excludedStocks.put("VIXM","<0.4");
			excludedStocks.put("VJET","<0.4");
			excludedStocks.put("VLT","<0.4");
			excludedStocks.put("VLU","<0.4");
			excludedStocks.put("VLY-A","<0.4");
			excludedStocks.put("VLY-B","<0.4");
			excludedStocks.put("VMAX","<0.4");
			excludedStocks.put("VMIN","<0.4");
			excludedStocks.put("VMM","<0.4");
			excludedStocks.put("VMOT","<0.4");
			excludedStocks.put("VNCE","<0.4");
			excludedStocks.put("VNLA","<0.4");
			excludedStocks.put("VNO-K","<0.4");
			excludedStocks.put("VNO-L","<0.4");
			excludedStocks.put("VNTV","<0.4");
			excludedStocks.put("VOOG","<0.4");
			excludedStocks.put("VOOV","<0.4");
			excludedStocks.put("VOX","<0.4");
			excludedStocks.put("VQT","<0.4");
			excludedStocks.put("VR-A","<0.4");
			excludedStocks.put("VR-B","<0.4");
			excludedStocks.put("VRNA","<0.4");
			excludedStocks.put("VRP","<0.4");
			excludedStocks.put("VRTSP","<0.4");
			excludedStocks.put("VSEC","<0.4");
			excludedStocks.put("VSMV","<0.4");
			excludedStocks.put("VSS","<0.4");
			excludedStocks.put("VTC","<0.4");
			excludedStocks.put("VTEB","<0.4");
			excludedStocks.put("VTHR","<0.4");
			excludedStocks.put("VTNR","<0.4");
			excludedStocks.put("VTSI","<0.4");
			excludedStocks.put("VTWG","<0.4");
			excludedStocks.put("VTWV","<0.4");
			excludedStocks.put("VUSE","<0.4");
			excludedStocks.put("VVPR","<0.4");
			excludedStocks.put("VXRT","<0.4");
			excludedStocks.put("WAC","<0.4");
			excludedStocks.put("WBIA","<0.4");
			excludedStocks.put("WBIB","<0.4");
			excludedStocks.put("WBIC","<0.4");
			excludedStocks.put("WBID","<0.4");
			excludedStocks.put("WBIE","<0.4");
			excludedStocks.put("WBIF","<0.4");
			excludedStocks.put("WBIG","<0.4");
			excludedStocks.put("WBIH","<0.4");
			excludedStocks.put("WBII","<0.4");
			excludedStocks.put("WBIL","<0.4");
			excludedStocks.put("WBIY","<0.4");
			excludedStocks.put("WBS-F","<0.4");
			excludedStocks.put("WCC","<0.4");
			excludedStocks.put("WCFB","<0.4");
			excludedStocks.put("WCHN","<0.4");
			excludedStocks.put("WDIV","<0.4");
			excludedStocks.put("WDRW","<0.4");
			excludedStocks.put("WEA","<0.4");
			excludedStocks.put("WEAT","<0.4");
			excludedStocks.put("WEBK","<0.4");
			excludedStocks.put("WEYS","<0.4");
			excludedStocks.put("WF","<0.4");
			excludedStocks.put("WFC","<0.4");
			excludedStocks.put("WFC-L","<0.4");
			excludedStocks.put("WFC-O","<0.4");
			excludedStocks.put("WFC-P","<0.4");
			excludedStocks.put("WFE-A","<0.4");
			excludedStocks.put("WFT","<0.4");
			excludedStocks.put("WG","<0.4");
			excludedStocks.put("WHG","<0.4");
			excludedStocks.put("WHLM","<0.4");
			excludedStocks.put("WHLRD","<0.4");
			excludedStocks.put("WHLRP","<0.4");
			excludedStocks.put("WIA","<0.4");
			excludedStocks.put("WINA","<0.4");
			excludedStocks.put("WINS","<0.4");
			excludedStocks.put("WIP","<0.4");
			excludedStocks.put("WIX","<0.4");
			excludedStocks.put("WLFC","<0.4");
			excludedStocks.put("WMCR","<0.4");
			excludedStocks.put("WMLP","<0.4");
			excludedStocks.put("WMW","<0.4");
			excludedStocks.put("WNEB","<0.4");
			excludedStocks.put("WPG-H","<0.4");
			excludedStocks.put("WPG-I","<0.4");
			excludedStocks.put("WPS","<0.4");
			excludedStocks.put("WPXP","<0.4");
			excludedStocks.put("WREI","<0.4");
			excludedStocks.put("WRLS","<0.4");
			excludedStocks.put("WRN","<0.4");
			excludedStocks.put("WSCI","<0.4");
			excludedStocks.put("WSKY","<0.4");
			excludedStocks.put("WSTG","<0.4");
			excludedStocks.put("WSTL","<0.4");
			excludedStocks.put("WTBA","<0.4");
			excludedStocks.put("WTFCM","<0.4");
			excludedStocks.put("WTID","<0.4");
			excludedStocks.put("WTIU","<0.4");
			excludedStocks.put("WTMF","<0.4");
			excludedStocks.put("WTT","<0.4");
			excludedStocks.put("WVFC","<0.4");
			excludedStocks.put("WVVI","<0.4");
			excludedStocks.put("WYDE","<0.4");
			excludedStocks.put("XBIO","<0.4");
			excludedStocks.put("XCEM","<0.4");
			excludedStocks.put("XELB","<0.4");
			excludedStocks.put("XFLT","<0.4");
			excludedStocks.put("XHE","<0.4");
			excludedStocks.put("XHS","<0.4");
			excludedStocks.put("XITK","<0.4");
			excludedStocks.put("XIVH","<0.4");
			excludedStocks.put("XKFS","<0.4");
			excludedStocks.put("XKST","<0.4");
			excludedStocks.put("XLG","<0.4");
			excludedStocks.put("XMLV","<0.4");
			excludedStocks.put("XMPT","<0.4");
			excludedStocks.put("XMX","<0.4");
			excludedStocks.put("XNTK","<0.4");
			excludedStocks.put("XNY","<0.4");
			excludedStocks.put("XPH","<0.4");
			excludedStocks.put("XPL","<0.4");
			excludedStocks.put("XPLR","<0.4");
			excludedStocks.put("XPP","<0.4");
			excludedStocks.put("XRLV","<0.4");
			excludedStocks.put("XRM","<0.4");
			excludedStocks.put("XSHD","<0.4");
			excludedStocks.put("XSLV","<0.4");
			excludedStocks.put("XSOE","<0.4");
			excludedStocks.put("XSW","<0.4");
			excludedStocks.put("XTL","<0.4");
			excludedStocks.put("XTLB","<0.4");
			excludedStocks.put("XTN","<0.4");
			excludedStocks.put("XTNT","<0.4");
			excludedStocks.put("XUSA","<0.4");
			excludedStocks.put("XVZ","<0.4");
			excludedStocks.put("XWEB","<0.4");
			excludedStocks.put("YAO","<0.4");
			excludedStocks.put("YCL","<0.4");
			excludedStocks.put("YCS","<0.4");
			excludedStocks.put("YDIV","<0.4");
			excludedStocks.put("YECO","<0.4");
			excludedStocks.put("YGE","<0.4");
			excludedStocks.put("YGYI","<0.4");
			excludedStocks.put("YIN","<0.4");
			excludedStocks.put("YLCO","<0.4");
			excludedStocks.put("YLD","<0.4");
			excludedStocks.put("YMLI","<0.4");
			excludedStocks.put("YMLP","<0.4");
			excludedStocks.put("YOGA","<0.4");
			excludedStocks.put("YRIV","<0.4");
			excludedStocks.put("YUMA","<0.4");
			excludedStocks.put("YUME","<0.4");
			excludedStocks.put("YXI","<0.4");
			excludedStocks.put("YYY","<0.4");
			excludedStocks.put("ZAIS","<0.4");
			excludedStocks.put("ZB-A","<0.4");
			excludedStocks.put("ZB-G","<0.4");
			excludedStocks.put("ZB-H","<0.4");
			excludedStocks.put("ZBIO","<0.4");
			excludedStocks.put("ZBK","<0.4");
			excludedStocks.put("ZDGE","<0.4");
			excludedStocks.put("ZEAL","<0.4");
			excludedStocks.put("ZF","<0.4");
			excludedStocks.put("ZKIN","<0.4");
			excludedStocks.put("ZOM","<0.4");
			excludedStocks.put("ZROZ","<0.4");
			excludedStocks.put("ZSL","<0.4");
			excludedStocks.put("ZX","<0.4");
			excludedStocks.put("ZYME","<0.4");

		}
		return excludedStocks;
	}

	public static void setExcludedStocks(Hashtable excludedStocks) {
		StaticData.excludedStocks = excludedStocks;
	}

}
