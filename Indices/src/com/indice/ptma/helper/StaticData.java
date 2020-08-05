package com.indice.ptma.helper;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Date;

public class StaticData {
	
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
			boolean stop = false;
			int pastCount = 0;
			while (!stop) {
				
				if(!past.before(now)){
					pastCount++;
				}
				
				if(pastCount>3){
					stop =  true;
				}
				
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

}
