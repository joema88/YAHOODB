package com.yahoo.ptma.helper;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;

import com.intraday.ptma.status.SQLOperationStatus;
import com.yahoo.ptma.sql.SQLOperation;

//the SELLINGSCORE SSC Calculation algorithm needs to be looked at
//seems not correct in some instances, maybe should look strickly 
//at BBDI values instead 10/03/2017 comments
//  -- 30 DAYS AWS<0, 0 OKAY, COULD HAVE 2 DAY AWS +, SUM LESS THAN 12, BEAR STARTS
//10/04/2017 comments,for example sina, may be should be lower low, lower high using aws +- to select peak and trough
//each negative -aws must be at least 10 to be meaningful, AZO,LVS,MGM,SINA,MYL,GALE,VRX,FSLR,TRIP,AGN, UA,FEYE,DATA,FOSL,CMG,GPRO,BAC (GOOD EXAMPLE AROUND 2008)(CALM NOT SO PERFECT),
//ANOTHER TYPE LIKE TWTR,TEVA, WITH 1 OR 2 +AWS,30 PLUS -AWS, STRAIGHT DOWN BEAR, NO STRUGGLE
public class DailStockHistory {
	private static String operationMode = "SECTOR";
	private static Hashtable nyseStocks = null;
	private static Hashtable amxStocks = null;
	private static Hashtable nasStocks = null;
	private static Hashtable eodIndexes = null;
	private static Hashtable stocks = new Hashtable();
	private static Hashtable currentExistingStocks = null;
	private static Hashtable previousExistingStocks = null;
	private static int amxCount = 0;
	private static int nasCount = 0;
	private static int nyseCount = 0;
	private static int indexCount = 0;

	/**
	 * @param args
	 */
	// 2017 GOLDEN RULE
	// 1.PTMA POSITIVE --LONG NEGATIVE --THEN POSITIVE + DSI5 (6 DAYS, AVG>80) +
	// MAYBE PTMAV SURGE INDICATING BEAR OVER
	// 2.PTMA POSITIVE --LONG NEGATIVE --THEN POSITIVE+DCP(TWAP% POSITIVE FOR
	// FIVE DAYS) + DSI5 (6 DAYS, AVG>80, MIN 75) + MAYBE PTMAV SURGE INDICATING
	// BEAR OVER
	// 3.OR DSI5 (6 OR 7 DAYS, AVG>80, MIN 75) + MAYBE PTMAV SURGE (>100 OR SUM
	// 3 DAYS PTMAV>150) WITHIN 60 DAYS OR PTMA START INDICATING BEAR OVER IS
	// ENOUGH
	// 4$.OR DSI5 (6 OR 7 OR 8 DAYS, AVG>85, MIN 50) + MAYBE PTMAV SURGE
	// (SINGLE>100 OR SUM 3 DAYS PTMAV>150) WITHIN 60 DAYS OR PTMA START
	// INDICATING BEAR OVER IS ENOUGH
	// ALL SEEMS A TYPICAL STUDY EXAMPLE, WPRT AS NEGATIVE EXAMPLE
	// TOP OR DESCEND INDICATOR: 6 DAYS OF DSI5<25 CONTINUOUSLY, CHECK CGI, OR 8
	// DAYS OF AVG DSI5<20 CONTINOUSLY, OR 4 DAYS OF AVG DSI5<20 CONTINOUSLY
	// (TWICE WITHIN SHORT RANGE, SECOND MAX <FIRST MAX, THEN DOWNTREND STARTS),
	// FOR EXAMPLE CGI,AGN,AAPL
	// BIG BEND: CONTINOUS 4 OR 5 DAYS SUM DSI5/CONTINOUS PRIOR 4 OR 5 DAYS SUM
	// DSI5>7 OR 4? THEN BOTTOM UP
	// 5. 10 DAYS OR 12 DAYS (PTMAV>65%) PTMAV ABOVE CERTAIN POSITIVE VALUE
	// SEEMS A BULL INDICATOR AS WELL
	// 6. 6 DAYS AVG DSI5>85, MIN 75, SUM(PTMAV)>200, DSI5 CALCULATION CHANGED
	// AS :CURRENT DSI5 +200*(END PRICE-START PRICE)/START PRICE

	// UPTREND >=1 AND DELTASUM>0 , PTMA>0 (?) FIRST OF THIS KIND IN 90 DAYS GAP
	// OR UPTREN>=7 AND WITHIN SHORT PERIOD PRIOR OR AFTER, DELTASUM>=12 MANY
	// DAYS
	// 50 DAYS DOWN TPTS STARTING 4, -1,1,-4 , 0 CONTINUE
	// OR 4,-4,4,-4 OR -4,4,-4,4, LOWER LOW, LOWER HIGH, BEAR

	/*
	 * SELECT SEQINDEX AS SEQ, SYMBOL AS
	 * SYM,VARCHAR_FORMAT(CURRENTDATE,'MM-DD-YYYY') AS VDATE, DSI3 AS
	 * DSISUM,SUMDS3P
	 * ,DIPSCORE,DPS4,BBDI,FFP,DS3PER,DS3PT,DCPT,DS3PIP,DS3PIPDAYS,
	 * ATT,TPTS,PTS,AWS,FINALPRICE AS PRICE,LBBI,UTI,UTIS,BBSCORE,SELLINGSCORE
	 * AS SSC,DCP,DCPIP,TWA AS DCPTDAYS,TMAI,TMA,PTMA, UPTRENDSTART AS UPS FROM
	 * BPMADMIN.YAHOODB where symbol='AMPE' AND SEQINDEX<43600 order by seqIndex
	 * DESC;
	 */
	// for 1/4/2018, 43103
	public static void makeUpCal(int k) {
		/*
		 * SQLOperation.calculateEntireDS3PerTrendByPercentageHistory( k, true);
		 * SQLOperation.updateEntireLatestBBScore(k);
		 * SQLOperation.calculateEntireSellingScoreHistory(k, true);
		 * SQLOperation.getUniqueFirstUpStocks(k, true); long t18 =
		 * System.currentTimeMillis(); // SQLOperation.findLatestFFP(-1000);
		 * SQLOperation.calculateEntireUTIHistory(k, true);
		 * SQLOperation.calculateEntireUTIStepsHistory(k, true);
		 * SQLOperation.calculateEntireLBBIHistory(k, true);
		 */
		SQLOperation.calculateEntirePTSHistory(k, true);

	}

	public static void main(String[] args) {
		long t1 = System.currentTimeMillis();
		System.out.println("Start");
		stocks.put("AA", "AA");
		stocks.put("AAC", "AAC");
		stocks.put("AAL", "AAL");
		stocks.put("AAN", "AAN");
		stocks.put("AAP", "AAP");
		stocks.put("AAT", "AAT");
		stocks.put("AAU", "AAU");

		// afterOperationNew(43271);
		// sleep750Minutes();
		// sleep750Minutes();
		/*
		 * SQLOperation.overlayAllBBScore(43184, true); try{
		 * System.out.println("Start sleeping...."); Thread.sleep(1000000);
		 * }catch(Exception ex){
		 * 
		 * }
		 */
		// recalculateALLFreshStocks();
		// System.out.println("Start sleeping....");
		// sleep750Minutes();
		// sleep750Minutes();
		// sleep750Minutes();
		// UA GPRO BABA
		// SQLOperation.scoreDipOpTest1(symbol, 42946, false);
		// SQLOperation.scoreDipOp(symbol, 42946, false);
		// SQLOperation.getDipscoreSum(symbol, 42946, 7,false);
		// SQLOperation.calculateDSI24HistorySP("SPY",24);
		// SQLOperation.getTrendBasedonDSINew("SPY",42487);//--90% prediction
		// success!
		// SQLOperation.findDSIDiscrepencySP("SPY",false);--not useful at all
		// SQLOperation.evaluateDSIDiscrepancyNew("SPY");--not useful at all
		// SQLOperation.calculateDSI24History(42387);
		// shortTermUniqueUpHistoryNew(42836);
		// getPTMAWAVEHISTORYNew(42836);
		// findDSISyncSP(String symbol,int days, int dsiMin, int dsiDeltaMin,
		// int dsi5Min, int dsi5DeltaMin)
		// SQLOperation.findDSISyncSP("SPY",5,50,40,85,40);--may be significant
		// DSISTABLEWIN (symbolIn,DAYS,avgSUMDSI,avgSUMDSIRange,avgDSI
		// avgDSIRange,avgDSI5,avgDSI5Range, OUT VARCOUNT INTEGER)
		// $$$$$ this is for searching stable SPY period for the next 5 to 15
		// days
		// so that we could sell time spread on options, the following find
		// candidates
		// SQLOperation.findDSIStableSP("SPY", 5, 200.0f,10.0f, 100.0f, 10.0f,
		// 100.0f, 20.0f);
		// this one calculate gain according to real SPY data
		// SQLOperation.calculateSPYDSIStableStatistics("SPY");
		// System.out.println("Unique Up history done");
		// sleep5Minutes();
		// TODO Auto-generated method stub
		// AVNR, PBYI EXAMPLE
		// runDailyIndex();
		// SingleStockRecalulation("A");

		// SHOULD BE RUN PEROIDICALLY, ESPECIALLY AFTER MONTH END
		// TO GET MORE STOCK USING STORED PROCEDURE TO INSERT
		// Files.createEODTagFile("ERC_CalculationStart.txt");

		// recalculateALLFreshStocks();
		// System.out.println("RECALCULATION done");
		// Files.createEODTagFile("ERC_CalculationEnd.txt");

		// fillMissingRecordsForDate("20180529");
		// sleep750Minutes();
		// addStocksRecords();
		// doCompleteRun();
		// skip index daily pull from yahoo
		// SQLOperation.calculateTrendPointsHistoryForAllStocks(-1,6);
		// true means ignore index updates
		// 5:30 pm
		boolean skipIndex = true;
		// SQLOperation.calculateEntirePTSHistory(43012,false);
		// SQLOperation.getDS3PerSum("CTRP", -1,20, false);
		// SQLOperation.AAA_resetALLLastTwoTWA(42954);
		// SQLOperation.calculateEntireTWATrendHistory(42954,true);
		// AZO,FB,MSFT,BIIB,CMG,UA,TWTR,AMZN,TSLA
		String symbol = "FB";
		// SQLOperation.calculateATT(symbol, 40319);
		// SQLOperation.calculateEntirePTSHistory(43020,false);//
		// SQLOperation.calculateAWSStatistics(symbol);
		// SQLOperation.calculateEntireAWSStatistics(43013);
		// SQLOperation.calculateEntirePTS(symbol, 43013);
		// SQLOperation.calculateEntirePTSHistory(43013,false);
		System.out.println("Start time "
				+ Calendar.getInstance().getTime().toLocaleString());
		// /SQLOperation.findTodayDBCases(43003);
		// IPGP -- SSC CALCULATION IS A PROBLEM
		// makeUpCal(43103);
		Files.createEODTagFile("EOD CalculationStart.txt");
		// doAllEODRun("20180420");
		// SQLOperation.filterAllIBSPTrend(43291);
		// SQLOperation.sumIBSPTrendHistory(43291,43291, null);
		// System.out.println("---Calculation done---");
		// SQLOperation.sumIBSPTrendHistory(43292,43292, null);
		// SQLOperation.recalTwoItems(43292, -1);
		// SQLOperation.calculateEntireCBull(43292, true);
		// SQLOperation.recalTwoItems(43291, -1);
		// System.out.println("---Calculation done---");
		// sleep750Minutes();
		// recalculateALLFreshStocks();
		boolean calAVGlmdr = false; // set it to true the day after month end
		
		System.out.println("calculateEntireAVGLMDRHistory last only..");
		SQLOperation.calculateEntireAVGLMDRHistory(43555,
				true);
		Files.createEODTagFile("calculateEntireAVGLMDRHistory.DONE");
		
		doEODRunNew("20190403", skipIndex, calAVGlmdr);
		// fillMissingRecords(42771);
		// recalculateALLFreshStocks();
		System.out.println("End time "
				+ Calendar.getInstance().getTime().toLocaleString());
		Files.createEODTagFile("EOD CalculationDone.txt");

		// makeupSteps(43017);
		// SQLOperation.calculateEntirePTSHistory(43018, false);
		// SQLOperation.calculateEntirePTS(symbol, 43018);
		// SQLOperation.calculateTruePTS(symbol,43018);
		// printTodayResult(43019);
		// SQLOperation.calculateLBBIHistory(symbol, 43006,false);
		// SQLOperation.calculateEntireLBBIHistory(43006,false);

		// SQLOperation.findLFBB300SCORE(-1000);//66,48,31
		// SQLOperation.findDense300SCORE(-1000);//179,134,95
		// SQLOperation.findTodayBBSCOREExtreme(-1000);//12,8
		// SQLOperation.findTodayUTIS3(-1000);//14,12,12
		// SQLOperation.findTodayDBCases(43006);//34,27,19

		// sleep5Minutes();
		// SQLOperation.calculateEntireAWSHistory(43002, true);
		// SQLOperation.findTodayAWS10(43002);
		// SQLOperation.calculateAWS(symbol,43002, false);
		// SQLOperation.calculateSumAWS(symbol,43002, false);
		// SQLOperation.calculateAverageDCPIP(42995,false);
		// SQLOperation.calculateAverageDS3IP(42995,false);
		// SQLOperation.repairDCPIPHistory(42995,true);
		// SQLOperation.validateBBScore(symbol, 42626);
		// SQLOperation.getDS3PerTrendHistoryByPercentageStep1(symbol);
		// SQLOperation.getDS3PerTrendHistoryByPercentageStep2(symbol);
		// SQLOperation.getDS3PerTrendHistoryByPercentageStep3(symbol);
		// SQLOperation.calculateEntireDCPTrendByPercentageHistory(42991,false);

		// Need many hours to rerun entire BBScore according to new Percentage
		// rule plus the regular positive rules
		// RECALCULATE ALL DS3PerTrendByPercentageHistory for all stocks
		// int lastIndex = 42996;
		// SQLOperation.calculateEntireDS3PerTrendByPercentageHistory(lastIndex,false);
		// System.out.println("step 1 done...");
		// sleep5Minutes();
		// RECALCULATE ALL DCPTrendByPercentageHistory for all stocks
		// SQLOperation.calculateEntireDCPTrendByPercentageHistory(lastIndex,false);
		// System.out.println("step 2 done...");
		// sleep5Minutes();
		// Recalculate all BBScore
		// SQLOperation.calculateEntireBBScoreHistory(lastIndex, false);
		// System.out.println("step 3 done...");
		// sleep5Minutes();
		// Need many hours to rerun entire BBScore according to new Percentage
		// rule plus the regular positive rules

		// SQLOperation.getDS3PerTrendHistoryByPercentageStep2(symbol);
		// SQLOperation.getDS3PerTrendHistoryByPercentageStep1(symbol);
		// doEODRunNew("20170925", skipIndex, calAVGlmdr);
		// SQLOperation.validateBBScore(symbol, 42992);
		// SQLOperation.repairBBScoreEndHistory(42992,false);
		// SQLOperation.calculateDipSum7GradientHistory(42991,false);
		// SQLOperation.calculateEntireTWATrendHistory(42991,false);

		// SQLOperation.getDS3PerTrendHistoryByPercentageStep2(symbol);
		// SQLOperation.repairDCPIPHistory(42995,false);
		/*
		 * SQLOperation.calculateDipSum7GradientHistory(42991,false);
		 * System.out.println("step 1 done..."); sleep5Minutes();
		 * 
		 * SQLOperation.calculateEntireTWATrendHistory(42991,false);
		 * System.out.println("step2  done..."); sleep5Minutes();
		 * 
		 * SQLOperation.calculateEntireDS3PerTrendHistory(42991,false);
		 * System.out.println("step 3 done..."); sleep5Minutes();
		 * 
		 * SQLOperation.calculateEntireDS3PerTrendByPercentageHistory(42991,false
		 * ); System.out.println("step 4 done..."); sleep5Minutes();
		 * 
		 * SQLOperation.calculateEntireBBScoreHistory(42991,false);
		 * System.out.println("step 5 done..."); sleep5Minutes();
		 */
		// CALCULATE A SYMBOL IF RECORD DELETED
		// SQLOperation.getDipSumGradient(symbol, 42992, false);
		// SQLOperation.getDipSumGradientLimited(symbol, 42992);
		// SQLOperation.getDS3PerTrendHistoryByPercentageStep1(symbol,-1,false);
		// SQLOperation.getDS3PerTrendHistoryByPercentageStep2(symbol,-1,false);

		// SQLOperation.getDS3PerTrendHistoryByPercentageStep3(symbol,-1,false);
		// SQLOperation.getTWATrendHistory(symbol);
		// SQLOperation.calculateBBScore(symbol, 42992, false);
		// SQLOperation.validateBBScore(symbol, 42992);
		// CALCULATE A SYMBOL IF RECORD DELETED

		// SQLOperation.findTodayDBCases(42596);

		// SQLOperation.calculateEntireUTIStepsHistory(42985,false);
		// SQLOperation.calculateUTIStep1(symbol, 42985,false);
		// SQLOperation.calculateUTISteps23(symbol, 42985,false);
		// SQLOperation.calculateUTISteps(symbol, 42985,false);
		// SQLOperation.calculateUTI(symbol, 42985,false);
		// SQLOperation.calculateEntireUTIHistory(42985,false);

		// SQLOperation.updateEntireLatestBBScore(42967);
		// SQLOperation.updateLatestBBScore(symbol,42967);
		// SQLOperation.validateBBScore(symbol,42964);
		// SQLOperation.getBBDIScore("BABA", -1, false);
		// SQLOperation.calculateEntireBBDIHistory(42961,false);
		// .calculateEntireSUMDS3PHistory(42956,false);
		// SQLOperation.calculateEntireSellingScoreHistory(42977,false);
		// SQLOperation.calculateSellingScores("BAC", 42977,false);
		// SQLOperation.calculateEntireUTIStepsHistory(42988,true);

		// SQLOperation.findLFBB300SCORE(42971);
		// SQLOperation.findTodayBBSCOREExtreme(42971);
		// SQLOperation.calculateEntireBBScoreHistory(42967, false);
		// SQLOperation.calculateBBScore("BAC",-1,false);
		// SQLOperation.markBBEndPoint("BABA",-1,false);
		// SQLOperation.calculateEntireBBPointHistory(42964,false);
		// SQLOperation.markBBEndPoint(symbol,-1,false);
		// SQLOperation.calculateBBScore(symbol,42967,false);
		// SQLOperation.validateBBScore(symbol,42964);
		// SQLOperation.updateLatestBBScore()
		// SQLOperation.calculateEntireBBScoreHistory(42964,false);
		// SQLOperation.calculateEntireDS3PerTrendHistory(42950,true);
		// SQLOperation.calculateEntireTWATrendHistory(42950,true);
		// System.out.println("Start calculateEntireDS3PerTrendHistory");
		// SQLOperation.calculateEntireDS3PerTrendHistory(42949,false);
		// SQLOperation.resetConnection();
		// System.out.println("Sleep 25 mins");
		// sleep25Minutes();
		// System.out.println("Start calculateEntireTWATrendHistory");
		// SQLOperation.calculateEntireTWATrendHistory(42949,false);
		// SQLOperation.updateEntireLatestDipscoreSumGradient(42946);
		// SQLOperation.findLatestFFP(42740);
		// SQLOperation.getDS3PerTrendHistory(symbol);
		// SQLOperation.getTWATrendHistory(symbol);
		// SQLOperation.AAA_resetFFPHistoryToOriginal(symbol);
		// SQLOperation.AAA_resetEntireFFPHistory(42953);

		// SQLOperation.calculateEntireDipscoreSum7History(42943, false);
		// SQLOperation.getDipSumGradient("WHR",42943 ,false);
		// SQLOperation.calculateDipSum7GradientHistory(42943 ,false);
		// SQLOperation.getDipSumGradientLimited("SGEN",42943);
		// SQLOperation.updateLatestDipSumGradient("JPS", 42946);
		// SQLOperation.findLatestFFP(-1000);
		// SQLOperation.calculateDipSum7GradientLimitedHistory(42943);

		// SQLOperation.calculateLastIncDescTrendDays("LPL",42893, 40);
		//
		// String symbol="LPL";
		// SingleStockInsert("20170502","HSI");
		// SingleStockRecalulation("HSI");
		// automateIndexOnly("AAAP", 2012, 1, 2);
		// SQLOperation.calculateEntireTurnPointsHistory(42905,true);
		// SQLOperation.getTurnPointScore("AMD", 42905,false);
		// automateNewStocks(2012,1,2,"20170623",42908);
		// automateStocks("NYSE", "DKS", 2012, 1,2);
		// SingleStockRecalulation(symbol);
		// automateStocks("NASDAQ", "MYOK", 2012, 1, 2);
		// SQLOperation.calculateMissingHistory(42906,false);
		// SQLOperation.calculateEntireTotalBuyPointsHistory(42906,false);
		// SQLOperation.getTurnPointScore("YHOO", 42906,false);
		// SQLOperation.getTotalBuyScore("YHOO", 42906,false);
		// automateStocks("AMEX", "AADR", 2012, 1, 2);

		// SQLOperation.calculateIncDescTrendDays(symbol, 42892,false, 40);
		// SQLOperation.calculateLastIncDescTrendDays(symbol, 42893, 40);
		// SQLOperation.resetConnection();
		// doEODRunNew("20170922",skipIndex,calAVGlmdr);
		// SQLOperation.calculateEntireDPS4History(42892, true);
		// SQLOperation.calculateEntireDipScoreHistory(42892, true);
		// SQLOperation.calculateDeltaAgainstSPYHistory(42892, true);
		// SQLOperation.calculateIncDescTrendDaysHistory(42893, false);

		// SQLOperation.calculateEntireDipScoreHistory(42893, false);
		// SQLOperation.calculateEntireDPS4History(42893, false);
		// SQLOperation.scoreDipOp("DISCA", 42893, false);
		// SQLOperation.getDPS4History("DISCA", 42893, false);

		// SQLOperation
		// .calculateIncDescTrendDaysHistory(42883, true);

		// SQLOperation.getSPYManyIndicators(42864, "PTMA");
		// SQLOperation.evaluateBuyPoint("SPY");
		// SQLOperation.calculateDSI24History(42852);
		// SQLOperation.calculateDSISumHistory(42852);
		// SQLOperation.calculateDSISumDeltaHistory(42855);
		// SQLOperation.calculateDeltaSumHistory(42855);

		// t1 = System.currentTimeMillis();

		// SQLOperation.getUniqueFirstUpStocks(42848,true);
		// SQLOperation.resetConnection();
		// long t2 = System.currentTimeMillis();
		// System.out.println("42848 Calculate done $$$$$$$: cost time "+(t2-t1)/1000);

		// sleep5Minutes();

		// SQLOperation.resetConnection();
		// SQLOperation.getUniqueFirstUpStocks(42837, true);
		// 42836,42835,42834,42831,42830,42829,42828,42827

		// index daily pull from yahoo
		// doEODRunNew("20170420",false);
		// System.out.println("20170410 done");
		// sleep5Minutes();
		// amxStocks = null;

		// validateSplits(null);
		// calculateSingleStockSP("SMTX",true);
		// calculateSingleStockSP("BPOP",true);
		// calculateSingleStockSP("DEO",false);
		// System.out.println("recalculation done");
		// sleep5Minutes();

		// SQLOperation.calculateDSIHistorySP("SPY");
		// SQLOperation.calculateSurgeHistorySP("SPY");
		// boolean result = SQLOperation.isRealUpStartSP("BAC",42687);
		// System.out.println("First start is "+result);
		// get first up history, set CBULL=1 if UPTRENDSTART>=1 AND DELTASUM>0
		// first time within 6 days
		// SQLOperation.getUniqueFirstUpStocks(42827,false);
		long t2 = System.currentTimeMillis();
		System.out.println("$$$$$$$$$$$$ done $$$$$ total cost seconds "
				+ (t2 - t1) / 1000);
		// sleep5Minutes();
		/*
		 * long startTime = System.currentTimeMillis(); long endTime = 0; int
		 * start = 34800; while (start > 30000) {
		 * SQLOperation.getCurrentFirstUpStocks(start);
		 * SQLOperation.resetConnection(); endTime = System.currentTimeMillis();
		 * System.out.println("$$$$$$$$$$$$ done $$$$$ " + start +
		 * " cost seconds " + (endTime - startTime) / 1000); start--; startTime
		 * = endTime; if (start % 100 == 0) sleep5Minutes(); }
		 */
		// recalculateDSIHistory(null);
		// System.out.println("DSI History done");
		// recalculateDSIHistory(stocks);
		// recalculateSurgeHistory(stocks);
		// recalculateDsi5SumHistory(stocks);
		// recalculatePTMAVSumHistory(stocks);
		// recalculatePTMAVSumHistory(stocks);
		// System.out.println("dal Surge History done");
		// sleep5Minutes();
		// recalculateSurgeHistory(null);
		// System.out.println("DIS Surge History done");
		// sleep5Minutes();
		// recalculateDsi5SumHistory(null);
		// System.out.println("DIS 5 SUM History done");
		// sleep5Minutes();
		// recalculatePTMAVSumHistory(null);
		// System.out.println("PTMAVSumHistory done");
		// recalculatePTMAVSumHistory(stocks);
		// recalculatePTMAVSumHistory(null);
		// doTopOffRun(null);
		// calculateSingleStock("DRR","DRR",0.0f,true);
		/*
		 * String symbol="SMRT"; SQLOperation.calculateTMA(symbol, false,-1);
		 * SQLOperation.calculatePTMAHistory(symbol, -1);
		 * SQLOperation.calculateTMAV(symbol, false, -1);
		 * SQLOperation.calculatePTMAVHistory(symbol, -1);
		 * SQLOperation.calculateDSI(symbol, 5, -1);
		 */
		// addAllStocksRecordsQuick();

		// runDailyETF();
		// weekendVVSectorCalculation("@FOOD");
		// doCompleteSectorCalculation();

		// calculateTWAPHistory();
		// calculateDSI("^GSPC");
		t2 = System.currentTimeMillis();
		System.out.println("End time cost seconds " + (t2 - t1) / 1000);
	}

	public static void sleep5Minutes() {
		try {
			Thread.sleep(300000);
		} catch (Exception ex) {

		}
	}

	public static void sleep750Minutes() {
		try {
			Thread.sleep(1500000 * 30);
		} catch (Exception ex) {

		}
	}

	public static void sleep25Minutes() {
		try {
			Thread.sleep(1500000);
		} catch (Exception ex) {

		}
	}

	public static void calculateDSI(String symbol) {

		SQLOperation.calculateDSI(symbol, 1, -1);
		SQLOperation.calculateDSI(symbol, 3, -1);
		SQLOperation.calculateDSI(symbol, 5, -1);

	}

	public static void calculateDSI() {
		Hashtable tables = initIndexTable();

		System.out.println("Size " + tables.size());
		Enumeration en = tables.keys();

		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();

			SQLOperation.calculateDSI(symbol, 1, -1);
			SQLOperation.calculateDSI(symbol, 3, -1);
			SQLOperation.calculateDSI(symbol, 5, -1);
		}

	}

	public static void downLoadInsertIndex() {
		Hashtable tables = initIndexTable();

		System.out.println("Size " + tables.size());
		Enumeration en = tables.keys();

		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			System.out.println("Start processing " + symbol);
			String downLoadSymbol = "%5E" + symbol.substring(1);

			if (symbol.equalsIgnoreCase("SPY"))
				downLoadSymbol = symbol;

			try {
				HttpDownload.downLoadStockHistory(downLoadSymbol, symbol);
				Hashtable results = Files.getIndexHistory(symbol);
				SQLOperation.insertHistoryRecord(results);

				Thread.sleep(20000);
			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}

			System.out.println("Done processing " + symbol);
		}
	}

	public static void calculateTWAPHistory() {
		Hashtable tables = initIndexTable();

		System.out.println("Size " + tables.size());
		Enumeration en = tables.keys();
		int loopCount = 0;

		while (en.hasMoreElements()) {

			String symbol = en.nextElement().toString();
			// SQLOperation.calculatePTWA(symbol, 10687);
			System.out.println(symbol + " start");
			if (loopCount == 0) {
				SQLOperation.markWeekEnds(symbol, false, -1);

				loopCount++;
			}
			SQLOperation.calculateTWA(symbol, -10000, false);
			SQLOperation.calculatePTWAHistory(symbol, -1);
			SQLOperation.calculatePTWAHistory(symbol, -1);
			System.out.println(symbol + " done");
		}
	}

	public static void addStocksRecords() {
		try {
			Hashtable stocks = Files
					.getOptionStocks("C:\\stock\\OptionSymbols.txt");

			Enumeration symbols = stocks.keys();
			while (symbols.hasMoreElements()) {
				String symbol = symbols.nextElement().toString();
				if (!SQLOperation.recordsExist(symbol)) {
					System.out.println("start processing " + symbol);
					calculateSingleStock(symbol, symbol, 40.0f, false);
					System.out.println("Done processing " + symbol);
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	public static void addAllStocksRecordsQuick() {
		try {
			Hashtable stocks = Files
					.getOptionStocks("C:\\stock\\OptionSymbols.txt");

			Enumeration symbols = stocks.keys();
			while (symbols.hasMoreElements()) {
				String symbol = symbols.nextElement().toString();
				System.out.println("Check..." + symbol);
				if (!SQLOperation.recordsExist(symbol)) {
					System.out.println("start processing " + symbol);
					calculateSingleStock(symbol, symbol, -1.0f, true);
					System.out.println("Done processing " + symbol);
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

	public static void initEODStocks(String date) {
		amxStocks = Files.getEODRecordsAMX(date);
		nyseStocks = Files.getEODRecordsNYSE(date);
		nasStocks = Files.getEODRecordsNASDAQ(date);
		eodIndexes = Files.getEODRecordsIndex(date);
	}

	public static YStock getRecord(String symbol, String date) {
		YStock result = null;
		if (amxStocks == null) {
			System.out.println("Init stocks list ...");
			initEODStocks(date);
		}
		if (amxStocks.containsKey(symbol)) {
			result = (YStock) amxStocks.get(symbol);
			amxStocks.remove(symbol);
			amxCount++;
		} else if (nasStocks.containsKey(symbol)) {
			result = (YStock) nasStocks.get(symbol);
			nasStocks.remove(symbol);
			nasCount++;
		} else if (nyseStocks.containsKey(symbol)) {
			result = (YStock) nyseStocks.get(symbol);
			nyseStocks.remove(symbol);
			nyseCount++;
		} else if (eodIndexes.containsKey(symbol)) {
			result = (YStock) eodIndexes.get(symbol);
			eodIndexes.remove(symbol);
			indexCount++;
		}

		return result;

	}

	public static void doEODStep3Only(int seqIndex) {
		long t1 = System.currentTimeMillis();
		SQLOperation.getUniqueFirstUpStocks(seqIndex, true);
		long t2 = System.currentTimeMillis();
		System.out.println("EOD $$$$$$$ Step 3 done cost seconds " + (t2 - t1)
				/ 1000);
	}

	public static void doEODRunNewOld(String date, boolean skipTopOff,
			boolean calculateAVGlmdr) {
		// at this step -- Insertion, TMAI, TMA, TMAV,PTMA,PTMAV,
		// CBULL, DSI5,DELTASUM,UPTRENDSTART (the last two Volume plus Price)
		// up swing constitutes surge indicator
		long t1 = System.currentTimeMillis();
		doEODRun(date, skipTopOff);
		long t2 = System.currentTimeMillis();
		SQLOperation.calculateDSI24HistorySP("SPY");
		long t3 = System.currentTimeMillis();
		System.out.println("calculateTrendPointsHistoryForAllStocks");
		SQLOperation.calculateTrendPointsHistoryForAllStocks(-1, 6);
		long t4 = System.currentTimeMillis();
		if (calculateAVGlmdr) {
			System.out.println("calculateEntireAVGLMDRHistory last only..");
			SQLOperation.calculateEntireAVGLMDRHistory(SQLOperation.cIndex,
					true);
		}
		System.out.println("calculateIncDescTrendDaysHistory last only..");
		SQLOperation
				.calculateIncDescTrendDaysHistory(SQLOperation.cIndex, true);
		long t5 = System.currentTimeMillis();

		System.out.println("calculateDeltaAgainstSPYHistory last only..");
		SQLOperation.calculateDeltaAgainstSPYHistory(SQLOperation.cIndex, true);
		long t6 = System.currentTimeMillis();
		SQLOperation.calculateEntireDipScoreHistory(SQLOperation.cIndex, true);
		long t7 = System.currentTimeMillis();
		SQLOperation.calculateEntireDipscoreSum7History(SQLOperation.cIndex,
				true);
		long t8 = System.currentTimeMillis();
		SQLOperation.calculateEntireDPS4History(SQLOperation.cIndex, true);
		long t9 = System.currentTimeMillis();
		SQLOperation
				.calculateEntireTurnPointsHistory(SQLOperation.cIndex, true);
		long t10 = System.currentTimeMillis();
		SQLOperation.calculateEntireTotalBuyPointsHistory(SQLOperation.cIndex,
				true);
		long t11 = System.currentTimeMillis();
		SQLOperation.calculateEntireDS3PerTrendHistory(SQLOperation.cIndex,
				true);
		long t12 = System.currentTimeMillis();
		SQLOperation.calculateEntireSUMDS3PHistory(SQLOperation.cIndex, true);
		long t13 = System.currentTimeMillis();
		SQLOperation.calculateEntireBBDIHistory(SQLOperation.cIndex, true);
		SQLOperation.calculateEntireAWSHistory(SQLOperation.cIndex, true);
		long t14 = System.currentTimeMillis();
		SQLOperation.resetConnection();
		try {
			Thread.sleep(8000);
		} catch (Exception ex) {

		}
		t1 = System.currentTimeMillis();
		// GX100 an indicator of how many days passed for the same stock
		// to see the Surge Indicator again (Volume plus Price Surge)
		// The bigger the GX100 is, the more likely the stock is set up
		// for another mid/long term leg up...
		// not seem a very useful indicator, may rename the field
		// for other purpose, LBS change pattern calculation result
		// for example could be stored here
		// SQLOperation.getCurrentFirstUpStocks(SQLOperation.cIndex);

		long t15 = System.currentTimeMillis();
		System.out.println("EOD $$$$$$$$$$ Step 2 done cost seconds "
				+ (t2 - t1) / 1000);
		SQLOperation.resetConnection();
		SQLOperation.updateEntireLatestDipscoreSumGradient(SQLOperation.cIndex);
		long t16 = System.currentTimeMillis();
		SQLOperation.calculateEntireTWATrendHistory(SQLOperation.cIndex, true);
		long t17 = System.currentTimeMillis();
		try {
			Thread.sleep(8000);
		} catch (Exception ex) {

		}
		t1 = System.currentTimeMillis();
		// This is the indicator (CHAT=1) to filter out duplicated surge
		// indicator
		// for stocks on continuous basis, only a gap of 6 days will make it
		// a fresh up indicator again, this useful for sum up stock for
		// aggregate
		// analysis, thus bring in some unique individual stock counts instead
		// of
		// a lot of duplicates around the few peak days, more clear a picture
		SQLOperation.calculateEntireDS3PerTrendByPercentageHistory(
				SQLOperation.cIndex, true);
		SQLOperation.updateEntireLatestBBScore(SQLOperation.cIndex);
		SQLOperation.calculateEntireSellingScoreHistory(SQLOperation.cIndex,
				true);
		SQLOperation.getUniqueFirstUpStocks(SQLOperation.cIndex, true);
		long t18 = System.currentTimeMillis();
		// SQLOperation.findLatestFFP(-1000);
		SQLOperation.calculateEntireUTIHistory(SQLOperation.cIndex, true);
		SQLOperation.calculateEntireUTIStepsHistory(SQLOperation.cIndex, true);
		SQLOperation.calculateEntireLBBIHistory(SQLOperation.cIndex, true);
		SQLOperation.calculateEntirePTSHistory(SQLOperation.cIndex, true);
		// SQLOperation.findDense300SCORE(-1000);

		// so far important patterns found: 1. Double Bull case: BBSCore=3000
		// and UTIS=3
		// a little variation of this pattern, (BBScore=3000 or UTIS=3) AND
		// SSC>=60
		// BBScore =3000 DENSE OR MORE THAN 3 TIMES WITHIN 10 DAYS
		// 2. LBBI 2_5 or +2 -5 pattern, look at BAC around Oct., 2017
		// NEED TO FURTHER INVESTIGATE AND REFINE UNDER WHAT CONDITION LBBI 2_5
		// PATTERN EEFECTIVE
		// 3. TPTS i.e. PTS Trend, PTS is calculated based on AWS PEAK AND
		// TROUGH POINTS
		// CLASSICAL HIGHER HIGH, HIGHER LOW (BULL CASE) OR LOWER HIGH, LOWER
		// LOW (BEAR CASE)
		// OR MORE THAN 40( OR 50?) DAYS FROM TPTS +4 WITHOUT SEEING ANOTHER +4
		// BEAR CASE
		// OR MORE THAN 40( OR 50?) DAYS FROM TPTS -4 WITHOUT SEEING ANOTHER -4
		// BULL CASE
		System.out.println("\n\n");
		System.out.println("ALL caps stock...");
		SQLOperation.findTodayBBSCOREExtreme(-1000);
		System.out.println("\n\n");
		System.out.println("ALL caps stock...");
		SQLOperation.findTodayUTIS3(-1000);
		System.out.println("\n\n");
		System.out.println("ALL caps stock...");
		SQLOperation.findLFBB300SCORE(-1000, "allcap");
		System.out.println("\n\n");
		System.out.println("ALL caps stock...");
		SQLOperation.findTodayDBCases(SQLOperation.cIndex, "allcap");
		System.out.println("\n\n");
		System.out.println("Mega caps stock...");
		long t19 = System.currentTimeMillis();
		SQLOperation.findLFBB300SCORE(-1000, "megacap");
		System.out.println("\n\n");
		System.out.println("Mega caps stock...");
		SQLOperation.findTodayDBCases(SQLOperation.cIndex, "megacap");
		System.out.println("\n\n");
		System.out.println("Mega caps stock...");
		SQLOperation.findTodayAWS10(SQLOperation.cIndex, "megacap");
		System.out.println("\n\n");
		System.out.println("Micro caps stock...");
		SQLOperation.findLFBB300SCORE(-1000, "microcap");
		System.out.println("\n\n");
		System.out.println("Micro caps stock...");
		SQLOperation.findTodayDBCases(SQLOperation.cIndex, "microcap");
		SQLOperation.findToday2_5Points(SQLOperation.cIndex, "allcap");
		// SQLOperation.findLatest7DCP(-1000);
		System.out.println("EOD $$$$$$$$$$ Step 1 done cost seconds (T2-T1) "
				+ (t2 - t1) / 1000);
		System.out.println("EOD $$$$$$$$$$ Step 2 done cost seconds (T3-T2) "
				+ (t3 - t2) / 1000);
		System.out.println("EOD $$$$$$$$$$ Step 3 done cost seconds (T4-T3) "
				+ (t4 - t3) / 1000);
		System.out.println("EOD $$$$$$$$$$ Step 4 done cost seconds (T5-T4) "
				+ (t5 - t4) / 1000);
		System.out.println("EOD $$$$$$$$$$ Step 5 done cost seconds (T6-T5) "
				+ (t6 - t5) / 1000);
		System.out.println("EOD $$$$$$$$$$ Step 6 done cost seconds (T7-T6) "
				+ (t7 - t6) / 1000);
		System.out.println("EOD $$$$$$$$$$ Step 7 done cost seconds (T8-T7) "
				+ (t8 - t7) / 1000);
		System.out.println("EOD $$$$$$$$$$ Step 8 done cost seconds (T9-T8) "
				+ (t9 - t8) / 1000);
		System.out.println("EOD $$$$$$$$$$ Step 9 done cost seconds (T10-T9) "
				+ (t10 - t9) / 1000);
		System.out
				.println("EOD $$$$$$$$$$ Step 10 done cost seconds (T11-T10) "
						+ (t11 - t10) / 1000);
		System.out
				.println("EOD $$$$$$$$$$ Step 11 done cost seconds (T12-T11) "
						+ (t12 - t11) / 1000);
		System.out
				.println("EOD $$$$$$$$$$ Step 12 done cost seconds (T13-T12) "
						+ (t13 - t12) / 1000);
		System.out
				.println("EOD $$$$$$$$$$ Step 13 done cost seconds (T14-T13) "
						+ (t14 - t13) / 1000);

		System.out.println("EOD $$$$$$$ Total done cost seconds (T19-T1) "
				+ (t19 - t1) / 1000);

		SQLOperation.getBothEnds("AMZN");
		int endIndex1 = SQLOperation.getEndIndexLast();

		if (Files.intradayDoneFile()) {
			SQLOperation.overlayAllBBScore(endIndex1, true);
			SQLOperation.updateLastIBSSum(endIndex1, true);
		}

		// Files.deleteIntradyDoneFile();

		System.out.println("EOD IBBS Transfered from intraday to EOD");
	}

	public static void doEODRunNew(String date, boolean skipTopOff,
			boolean calculateAVGlmdr) {
		int lastStep = 0;//29;
		//System.out.println(SQLOperation.cIndex);
		//SQLOperation.cIndex = 43550;
		System.out.println(SQLOperation.cIndex);
	
		// speedMode turn off some weekly/monthly calculation in order to catch
		// up
		// like step 45 weekly HLHH, LHLL calculation
		boolean speedMode = false;

		boolean weekend = false;
		Calendar cal = Calendar.getInstance();
		int a = cal.get(Calendar.DAY_OF_WEEK);
		if (a == Calendar.FRIDAY || a ==Calendar.SATURDAY|| a == Calendar.SUNDAY) {
			weekend = true;
		}
		//weekend = false;
		System.out.println("Weekend is "+weekend);
		// ADD 1 DAY TO PREVIOUS RECORD
		// USUALLY, THIS RUNS BEFORE INTRADAY PROCESSING FINISHING
		// RUN ON DAILY BASIS, IF NOT, MIGHT HAVE TO CHANGE LOGIC
		SQLOperationStatus.addWorkingDays(com.intraday.ptma.helper.StaticData
				.getDownloadDays());
		// SQLOperationStatus.addWorkingDays(1);

		// at this step -- Insertion, TMAI, TMA, TMAV,PTMA,PTMAV,
		// CBULL, DSI5,DELTASUM,UPTRENDSTART (the last two Volume plus Price)
		// up swing constitutes surge indicator
		long t1 = System.currentTimeMillis();
		// this is the procedure called before 4/21/2018 change, include
		// index around the world capability
		// doEODRun(date, skipTopOff);
		// change on 4/21/2018, feature, add new stocks automatically, and
		// when records size>= 50, use stored procedure, otherwise java insert
		// as too short record size failed stock proc

		// INSERT RECORDS, AND IF USE STORED PROC, THEN SET/CALCULATE MANY
		// THINGS
		// TMAI,TMA,TMAV,PTMA,PTMAV,DSI5,DSI(24),DSI3(SUM),DELTA1,DELTA2,DELTA3,DELTA4
		// DELTASUM,UPTRENDSTART (UPTRENDSTART -- BASED ON sum_ptmav>250 AND
		// sum_dsi5>570 ETC)
		if (lastStep == 0)
			fillMissingRecordsForDate(date);
		// change on 4/21/2018, feature, add new stocks automatically, and
		// when records size>= 50, use stored procedure, otherwise java insert
		// as too short record size failed stock proc

		long t2 = System.currentTimeMillis();
		if (lastStep < 1) {
			// UPDATE DSI FOR SPY ONLY
			SQLOperation.calculateDSI24HistorySP("SPY");
			Files.createEODTagFile("EOD_STEP1.txt");
		}

		long t3 = System.currentTimeMillis();
		if (lastStep < 2) {
			// Update BPMADMIN.YAHOODB
			// DCPERCENT,DSPERCENT,DS3PER,DS4PER,DS8PER,DS9PER,
			// DS10PER,DS11PER,DS12PER,DS13PER,DS14PER,DS15PER BASED ON DAILY
			// PRICES
			System.out.println("calculateTrendPointsHistoryForAllStocks");
			SQLOperation.calculateTrendPointsHistoryForAllStocks(-1, 6);
			Files.createEODTagFile("EOD_STEP2.txt");
		}

		long t4 = System.currentTimeMillis();

		// NEED TO MODIFY TO add CALCULATing TMA ETC FOR NEW STOCKS, RECALCULATE
		// ENTIRE STOCKS IF TMAV>1 COUNT = 0, before AVGlmdr still okay
		if (calculateAVGlmdr) {
			if (lastStep < 3) {
				// SET AVGLMDR ON MONTHLY BASIS BASED ON DAILY PRICES
				System.out.println("calculateEntireAVGLMDRHistory last only..");
				SQLOperation.calculateEntireAVGLMDRHistory(SQLOperation.cIndex,
						true);
				Files.createEODTagFile("EOD_STEP3.txt");
			}

		}

		if (lastStep < 4) {
			System.out.println("calculateIncDescTrendDaysHistory last only..");
			// set DCINCR,DSNOTDECR, DCDECR,DSNOTINCR,SATCOUNT BASED ON AVGLMDR
			// AND DAILY PRICES
			SQLOperation.calculateIncDescTrendDaysHistory(SQLOperation.cIndex,
					true);
			Files.createEODTagFile("EOD_STEP4.txt");
		}

		long t5 = System.currentTimeMillis();

		if (lastStep < 5) {
			System.out.println("calculateDeltaAgainstSPYHistory last only..");
			// SET SET DELTA1, DELTA2, DELTA3,DELTA4,DELTA5, MAYBE IN CONFLICT
			// WITH STORED PROC INSERTION CALCULATION OF DELTAS
			// BASED ON STOCK VS. SPY'S
			// DCPERCENT,DSPERCENT,DS3PER,DS4PER,DS8PER,DS9PER,
			// DS10PER,DS11PER,DS12PER,DS13PER,DS14PER,DS15PER BASED ON DAILY
			// PRICES

			SQLOperation.calculateDeltaAgainstSPYHistory(SQLOperation.cIndex,
					true);
			Files.createEODTagFile("EOD_STEP5.txt");
		}

		long t6 = System.currentTimeMillis();
		if (lastStep < 6) {
			// SET DIPSCORE BASED ON STOCK HISTORY OF SATCOUNT,DSI5,DSI,DSI3,
			// DCPERCENT,DSPERCENT, DS3PER,DS4PER,
			// DS15PER,DELTA5,CURRENTDATE,FINALPRICE,PTMA
			SQLOperation.calculateEntireDipScoreHistory(SQLOperation.cIndex,
					true);
			Files.createEODTagFile("EOD_STEP6.txt");
		}

		long t7 = System.currentTimeMillis();
		if (lastStep < 7) {
			// set DCP based on the last 7 days' DIPSCORE
			SQLOperation.calculateEntireDipscoreSum7History(
					SQLOperation.cIndex, true);
			Files.createEODTagFile("EOD_STEP7.txt");
		}

		long t8 = System.currentTimeMillis();
		if (lastStep < 8) {
			// set DPS4 based on last 5 days' DIPSCORE, the value
			// is set up based on certain threshhold, i.e., DIPSCORE>=30
			// for at least 4 days out of last 5 days
			SQLOperation.calculateEntireDPS4History(SQLOperation.cIndex, true);
			Files.createEODTagFile("EOD_STEP8.txt");
		}
		long t9 = System.currentTimeMillis();
		if (lastStep < 9) {
			// SET BBGO TurnPoints value based on last 20 days'
			// SATCOUNT,DSI3,FINALPRICE,PTMA
			SQLOperation.calculateEntireTurnPointsHistory(SQLOperation.cIndex,
					true);
			Files.createEODTagFile("EOD_STEP9.txt");
		}

		long t10 = System.currentTimeMillis();
		if (lastStep < 10) {
			// set FFP (total buy points) based on a score system of the last
			// 11 days' DPS4,BBGO,DELTASUM AS VOLS,PTMA
			SQLOperation.calculateEntireTotalBuyPointsHistory(
					SQLOperation.cIndex, true);
			Files.createEODTagFile("EOD_STEP10.txt");
		}

		long t11 = System.currentTimeMillis();
		if (lastStep < 11) {
			// SET DS3PT based on DS3PER,FINALPRICE history
			// DS3PT Rhythm set up
			SQLOperation.calculateEntireDS3PerTrendHistory(SQLOperation.cIndex,
					true);
			Files.createEODTagFile("EOD_STEP11.txt");
		}

		long t12 = System.currentTimeMillis();
		if (lastStep < 12) {
			// SET SUMDS3P based on the last 20 days' DS3PER
			SQLOperation.calculateEntireSUMDS3PHistory(SQLOperation.cIndex,
					true);
			Files.createEODTagFile("EOD_STEP12.txt");
		}

		long t13 = System.currentTimeMillis();
		if (lastStep < 13) {
			// SET BBDI score based on (cDSI3 + cSUMDS3p) result
			SQLOperation.calculateEntireBBDIHistory(SQLOperation.cIndex, true);
			Files.createEODTagFile("EOD_STEP13.txt");
		}

		if (lastStep < 14) {
			SQLOperation.calculateEntireAWSHistory(SQLOperation.cIndex, true);
			Files.createEODTagFile("EOD_STEP14.txt");
		}

		long t14 = System.currentTimeMillis();

		if (lastStep == 0) {
			SQLOperation.resetConnection();
			try {
				Thread.sleep(8000);
			} catch (Exception ex) {

			}
		}
		t1 = System.currentTimeMillis();
		// GX100 an indicator of how many days passed for the same stock
		// to see the Surge Indicator again (Volume plus Price Surge)
		// The bigger the GX100 is, the more likely the stock is set up
		// for another mid/long term leg up...
		if (lastStep < 15) {
			// not seem a very useful indicator, may rename the field
			// for other purpose, LBS change pattern calculation result
			// for example could be stored here
			// THIS MAY BE AN IMPORTANT LONG TERM INDICATOR AFTRE ALL
			// GOOD FOR GX100>100, LONG TERM CONSOLIDATION CASES
			SQLOperation.getCurrentFirstUpStocks(SQLOperation.cIndex);
			Files.createEODTagFile("EOD_STEP15.txt");
		}

		long t15 = System.currentTimeMillis();
		System.out.println("EOD $$$$$$$$$$ Step 2 done cost seconds "
				+ (t2 - t1) / 1000);

		if (lastStep < 16) {
			SQLOperation.resetConnection();
			SQLOperation
					.updateEntireLatestDipscoreSumGradient(SQLOperation.cIndex);
			Files.createEODTagFile("EOD_STEP16.txt");
		}

		long t16 = System.currentTimeMillis();
		if (lastStep < 17) {
			SQLOperation.calculateEntireTWATrendHistory(SQLOperation.cIndex,
					true);
			Files.createEODTagFile("EOD_STEP17.txt");
		}

		long t17 = System.currentTimeMillis();
		try {
			Thread.sleep(8000);
		} catch (Exception ex) {

		}
		t1 = System.currentTimeMillis();
		// This is the indicator (CHAT=1) to filter out duplicated surge
		// indicator
		// for stocks on continuous basis, only a gap of 6 days will make it
		// a fresh up indicator again, this useful for sum up stock for
		// aggregate
		// analysis, thus bring in some unique individual stock counts instead
		// of
		// a lot of duplicates around the few peak days, more clear a picture
		if (lastStep < 18) {
			SQLOperation.calculateEntireDS3PerTrendByPercentageHistory(
					SQLOperation.cIndex, true);
			Files.createEODTagFile("EOD_STEP18.txt");
		}

		if (lastStep < 19) {
			SQLOperation.updateEntireLatestBBScore(SQLOperation.cIndex);
			Files.createEODTagFile("EOD_STEP19.txt");
		}
		long t19 = System.currentTimeMillis();

		if (lastStep < 20) {
			SQLOperation.calculateEntireSellingScoreHistory(
					SQLOperation.cIndex, true);
			Files.createEODTagFile("EOD_STEP20.txt");
		}

		if (lastStep < 21) {
			// THIS MODIFY CHAT WHICH IS USED AS CHAT NOW, SO SHOULD BE
			// STOPPED, PREVIOUS CALCULATION JUST MARK IF THIS IS THE FIRST
			// DAY OF PRICE AND VOLUME SURGE ACTION FOR SHORT TERM BULL
			// SQLOperation.getUniqueFirstUpStocks(SQLOperation.cIndex, true);
			Files.createEODTagFile("EOD_STEP21.txt");
		}

		long t18 = System.currentTimeMillis();
		// SQLOperation.findLatestFFP(-1000);
		if (lastStep < 22) {
			SQLOperation.calculateEntireUTIHistory(SQLOperation.cIndex, true);
			Files.createEODTagFile("EOD_STEP22.txt");
		}

		if (lastStep < 23) {
			SQLOperation.calculateEntireUTIStepsHistory(SQLOperation.cIndex,
					true);
			Files.createEODTagFile("EOD_STEP23.txt");
		}

		if (lastStep < 24) {
			SQLOperation.calculateEntireLBBIHistory(SQLOperation.cIndex, true);
			Files.createEODTagFile("EOD_STEP24.txt");
		}

		if (lastStep < 25) {
			SQLOperation.calculateEntirePTSHistory(SQLOperation.cIndex, true);
			Files.createEODTagFile("EOD_STEP25.txt");
		}

		// SQLOperation.findDense300SCORE(-1000);

		// so far important patterns found: 1. Double Bull case: BBSCore=3000
		// and UTIS=3
		// a little variation of this pattern, (BBScore=3000 or UTIS=3) AND
		// SSC>=60
		// BBScore =3000 DENSE OR MORE THAN 3 TIMES WITHIN 10 DAYS
		// 2. LBBI 2_5 or +2 -5 pattern, look at BAC around Oct., 2017
		// NEED TO FURTHER INVESTIGATE AND REFINE UNDER WHAT CONDITION LBBI 2_5
		// PATTERN EEFECTIVE
		// 3. TPTS i.e. PTS Trend, PTS is calculated based on AWS PEAK AND
		// TROUGH POINTS
		// CLASSICAL HIGHER HIGH, HIGHER LOW (BULL CASE) OR LOWER HIGH, LOWER
		// LOW (BEAR CASE)
		// OR MORE THAN 40( OR 50?) DAYS FROM TPTS +4 WITHOUT SEEING ANOTHER +4
		// BEAR CASE
		// OR MORE THAN 40( OR 50?) DAYS FROM TPTS -4 WITHOUT SEEING ANOTHER -4
		// BULL CASE
		if (lastStep <= 25) {
			System.out.println("\n\n");
			System.out.println("ALL caps stock...");
			SQLOperation.findTodayBBSCOREExtreme(-1000);
			System.out.println("\n\n");
			System.out.println("ALL caps stock...");
			SQLOperation.findTodayUTIS3(-1000);
			System.out.println("\n\n");
			System.out.println("ALL caps stock...");
			SQLOperation.findLFBB300SCORE(-1000, "allcap");
			System.out.println("\n\n");
			System.out.println("ALL caps stock...");
			SQLOperation.findTodayDBCases(SQLOperation.cIndex, "allcap");
			System.out.println("\n\n");
			System.out.println("Mega caps stock...");
			// long t19 = System.currentTimeMillis();
			SQLOperation.findLFBB300SCORE(-1000, "megacap");
			System.out.println("\n\n");
			System.out.println("Mega caps stock...");
			SQLOperation.findTodayDBCases(SQLOperation.cIndex, "megacap");
			System.out.println("\n\n");
			System.out.println("Mega caps stock...");
			SQLOperation.findTodayAWS10(SQLOperation.cIndex, "megacap");
			System.out.println("\n\n");
			System.out.println("Micro caps stock...");
			SQLOperation.findLFBB300SCORE(-1000, "microcap");
			System.out.println("\n\n");
			System.out.println("Micro caps stock...");
		}

		if (lastStep < 26) {
			SQLOperation.findTodayDBCases(SQLOperation.cIndex, "microcap");
			SQLOperation.findToday2_5Points(SQLOperation.cIndex, "allcap");
			Files.createEODTagFile("EOD_STEP26.txt");
		}

		// SQLOperation.findLatest7DCP(-1000);
		System.out.println("EOD $$$$$$$$$$ Step 1 done cost seconds (T2-T1) "
				+ (t2 - t1) / 1000);
		System.out.println("EOD $$$$$$$$$$ Step 2 done cost seconds (T3-T2) "
				+ (t3 - t2) / 1000);
		System.out.println("EOD $$$$$$$$$$ Step 3 done cost seconds (T4-T3) "
				+ (t4 - t3) / 1000);
		System.out.println("EOD $$$$$$$$$$ Step 4 done cost seconds (T5-T4) "
				+ (t5 - t4) / 1000);
		System.out.println("EOD $$$$$$$$$$ Step 5 done cost seconds (T6-T5) "
				+ (t6 - t5) / 1000);
		System.out.println("EOD $$$$$$$$$$ Step 6 done cost seconds (T7-T6) "
				+ (t7 - t6) / 1000);
		System.out.println("EOD $$$$$$$$$$ Step 7 done cost seconds (T8-T7) "
				+ (t8 - t7) / 1000);
		System.out.println("EOD $$$$$$$$$$ Step 8 done cost seconds (T9-T8) "
				+ (t9 - t8) / 1000);
		System.out.println("EOD $$$$$$$$$$ Step 9 done cost seconds (T10-T9) "
				+ (t10 - t9) / 1000);
		System.out
				.println("EOD $$$$$$$$$$ Step 10 done cost seconds (T11-T10) "
						+ (t11 - t10) / 1000);
		System.out
				.println("EOD $$$$$$$$$$ Step 11 done cost seconds (T12-T11) "
						+ (t12 - t11) / 1000);
		System.out
				.println("EOD $$$$$$$$$$ Step 12 done cost seconds (T13-T12) "
						+ (t13 - t12) / 1000);
		System.out
				.println("EOD $$$$$$$$$$ Step 13 done cost seconds (T14-T13) "
						+ (t14 - t13) / 1000);

		System.out.println("EOD $$$$$$$ Total done cost seconds (T19-T1) "
				+ (t19 - t1) / 1000);

			if (lastStep < 27) {
			SQLOperation.calculateAllHATs(SQLOperation.cIndex, true);
			Files.createEODTagFile("EOD_STEP27.txt");
		}

		if (lastStep < 28) {
			// calculate HAT continuous + or - count from previous history
			SQLOperation.calculateEntireCHAT(SQLOperation.cIndex, true);
			Files.createEODTagFile("EOD_STEP28.txt");
		}

		// THIS CAN BE DONE FIRST
		if (lastStep < 29) {
			// SQLOperation.markAllDaysAfterBBScore3000(SQLOperation.cIndex,
			// true);
			Files.createEODTagFile("EOD_STEP29.txt");
			Files.createEODTagFile(SQLOperation.cIndex + "_EOD_STEP29.txt");
		}

		SQLOperation.getBothEnds("AMZN");
		// the following could be overridden to handle intraday and EOD
		// out of sync calculation
		int endIndex1 = SQLOperation.getEndIndexLast();
		// endIndex1 = 43550;

		 Files.createEODTagFile("waiting for intraday completes at "+endIndex1);
		if (Files.intradayDoneFile()) {
			// if(true){
			if (lastStep < 30) {
				SQLOperation.overlayAllBBScore(endIndex1, true);
				Files.createEODTagFile("EOD_STEP30.txt");
			}

			if (lastStep < 31) {
				SQLOperation.updateLastIBSSum(endIndex1, true);
				Files.createEODTagFile("EOD_STEP31.txt");
			}

			if (lastStep < 32) {
				SQLOperation.scoreAllStocksIBBS(endIndex1);
				Files.createEODTagFile("EOD_STEP32.txt");
			}

			if (lastStep < 33) {
				// Hashtable rstks =
				// SQLOperationStatus.getSparselyTradedStocks();
				// Files.spitNoNeededStocks(rstks);
				Files.createEODTagFile("EOD_STEP33.txt");
			}

			// need to go to file at "C:\\stock\\yahoo\\addRemovedStocks.remove"
			// cut and paste this content to
			// com.intraday.ptma.helper.StaticData.java
			// excluded stocks map

		}
		// what if more than one day intra day data down-loaded?? Manual
		// editing???
		// SCORE IBSP 40% BUMP, saved in PREDICTLP field, as LP
		if (lastStep < 34) {
			SQLOperation.scoreAllBullPoints(endIndex1, endIndex1);
			Files.createEODTagFile("EOD_STEP34.txt");
			Files.createEODTagFile(SQLOperation.cIndex + "_EOD_STEP34.txt");

		}
		// SCORE CONTINUOUS IBSP 40% BUMP PLUS MARKET CAP IMPACT, saved
		// in HP field (HP)
		if (lastStep < 35) {
			SQLOperation.filterAllIBSPTrend(endIndex1);
			Files.createEODTagFile("EOD_STEP35.txt");
		}
		// sum the occurrence times of LP (40% bump) within last 20 days, and
		// saved in
		// in SP field, used in conjunction with HP
		if (lastStep < 36) {
			SQLOperation.sumIBSPTrendHistory(endIndex1, endIndex1, null);
			Files.createEODTagFile("EOD_STEP36.txt");
		}
		// Files.deleteIntradyDoneFile();
		if (lastStep < 37) {
			SQLOperation.calculateAvgIBSP(endIndex1, true);
			Files.createEODTagFile("EOD_STEP37.txt");
		}

		if (lastStep < 38) {
			// calculate CHAT bull points based on HAT history, 1% above the
			// highest price of the HAT<=-9, only last 9 HAT neg values used
			SQLOperation.calculateEntireCBull(endIndex1, true);
			Files.createEODTagFile("EOD_STEP38.txt");
		}

		if (lastStep < 39) {
			// update LBS and AB3D, JUST DO LBS AS AB3D ALREADY DONE FIRST
			// SQLOperation.updateTwoItems(SQLOperation.cIndex, 0);
			SQLOperation.recalTwoItems(endIndex1, 0);
			Files.createEODTagFile("EOD_STEP39.txt");
			System.out.println("EOD IBBS Transfered from intraday to EOD");
		}

		if (lastStep < 40) {
			// update DBL strong bull indicator based on LBS history
			SQLOperation.calculateEntireDBLHistory(endIndex1, true);
			Files.createEODTagFile("EOD_STEP40.txt");
			System.out.println("CalculateEntireDBLHistory done");
		}

		if (lastStep < 41) {
			// update U-turn indicator based on LBS history
			SQLOperation.scoreAllStockUShape(endIndex1, true);
			Files.createEODTagFile("EOD_STEP41.txt");
			System.out.println("U-Turn Shape History done");
		}

		if (lastStep < 42) {
			// update U2 indicator based on BBSCORE3000 and BDL4
			SQLOperation.updateLastestU2(endIndex1);
			Files.createEODTagFile("EOD_STEP42.txt");
			System.out.println("U2 score update done");
		}

		if (lastStep < 43) {
			// update U2 milestone indicator based on BBSCORE3000 and BDL4
			// i.e., 5% drop or 10% increase when U2>1000 OR DBL4 happens
			SQLOperation.updateAllLastU2MileStone(endIndex1);
			Files.createEODTagFile("EOD_STEP43.txt");
			System.out.println("U2 score update done");
		}

		if (lastStep < 44) {
			SQLOperation.getIPOBulls(endIndex1);
			Files.createEODTagFile("EOD_STEP44.txt");
			System.out.println("IPO BULL update done");
		}

		if (lastStep < 45 && !speedMode && weekend) {

			// a=6, Friday, a= 7 Saturday, a=1 Monday
			SQLOperation.recalculateEntireHL4History(endIndex1);
			Files.createEODTagFile("EOD_STEP45.txt");
			System.out.println("HHHL LHLL update done");

		}

		// skip for now
		/*
		if (lastStep < 46 && !speedMode) {
			SQLOperation.getMegaUTurn(endIndex1);
			Files.createEODTagFile("EOD_STEP46.txt");
			System.out.println("Mega UTurn update done");
		}*/

	}

	public static void afterOperationNew(int seqIndex) {
		SQLOperation.overlayAllBBScore(seqIndex, true);
		SQLOperation.updateLastIBSSum(seqIndex, true);
		SQLOperation.scoreAllStocksIBBS(seqIndex);

		Hashtable rstks = SQLOperationStatus.getSparselyTradedStocks();
		Files.spitNoNeededStocks(rstks);

		// need to go to file at "C:\\stock\\yahoo\\addRemovedStocks.remove"
		// cut and paste this content to
		// com.intraday.ptma.helper.StaticData.java
		// excluded stocks map

		// what if more than one day intra day data down-loaded?? Manual
		// editing???
		// SCORE IBSP 40% BUMP, saved in PREDICTLP field, as LP
		SQLOperation.scoreAllBullPoints(seqIndex, seqIndex);
		// SCORE CONTINUOUS IBSP 40% BUMP PLUS MARKET CAP IMPACT, saved
		// in HP field (HP)
		SQLOperation.filterAllIBSPTrend(seqIndex);
		// sum the occurrence times of LP (40% bump) within last 20 days, and
		// saved in
		// in SP field, used in conjunction with HP
		SQLOperation.sumIBSPTrendHistory(seqIndex, seqIndex, null);
		// Files.deleteIntradyDoneFile();
		SQLOperation.calculateAvgIBSP(seqIndex, true);
		SQLOperation.calculateAllHATs(seqIndex, true);

		// calculate HAT continuous + or - count from previous history
		SQLOperation.calculateEntireCHAT(seqIndex, true);
		// calculate CHAT bull points based on HAT history, 1% above the
		// highest price of the HAT<=-9, only last 9 HAT neg values used
		SQLOperation.calculateEntireCBull(seqIndex, true);

		System.out.println("EOD IBBS Transfered from intraday to EOD");
	}

	public static void makeupSteps(int seqIndex) {
		SQLOperation.calculateEntireUTIStepsHistory(seqIndex, true);
		SQLOperation.calculateEntireLBBIHistory(seqIndex, true);
		// SQLOperation.findDense300SCORE(-1000);
		SQLOperation.findTodayBBSCOREExtreme(-1000);
		SQLOperation.findTodayUTIS3(-1000);
		SQLOperation.findLFBB300SCORE(-1000, "allcap");
		SQLOperation.findTodayDBCases(seqIndex, "allcap");
		long t19 = System.currentTimeMillis();
		SQLOperation.findLFBB300SCORE(-1000, "megacap");
		SQLOperation.findTodayDBCases(seqIndex, "megacap");
		SQLOperation.findTodayAWS10(seqIndex, "megacap");
		SQLOperation.findLFBB300SCORE(-1000, "microcap");
		SQLOperation.findTodayDBCases(seqIndex, "microcap");
		SQLOperation.findToday2_5Points(seqIndex, "allcap");
		SQLOperation.calculateEntirePTSHistory(seqIndex, true);

	}

	public static void printTodayResult(int seqIndex) {
		SQLOperation.findTodayBBSCOREExtreme(-1000);
		SQLOperation.findTodayUTIS3(-1000);
		SQLOperation.findLFBB300SCORE(-1000, "allcap");
		SQLOperation.findTodayDBCases(seqIndex, "allcap");
		long t19 = System.currentTimeMillis();
		SQLOperation.findLFBB300SCORE(-1000, "megacap");
		SQLOperation.findTodayDBCases(seqIndex, "megacap");
		SQLOperation.findTodayAWS10(seqIndex, "megacap");
		SQLOperation.findLFBB300SCORE(-1000, "microcap");
		SQLOperation.findTodayDBCases(seqIndex, "microcap");
		SQLOperation.findToday2_5Points(seqIndex, "allcap");

	}

	public static void shortTermUniqueUpHistoryNew(int currentIndex) {
		SQLOperation.getUniqueFirstUpStocks(currentIndex, false);
	}

	public static void getPTMAWAVEHISTORYNew(int currentIndex) {
		SQLOperation.getPTMAWAVEHISTORY(currentIndex, false);
	}

	//
	public static void automateNewStocks(int startYear, int startMonth,
			int startDays, String endDate, int lastIndex) {
		initEODStocks(endDate);
		Enumeration amex = amxStocks.keys();
		int count = 0;
		while (amex.hasMoreElements()) {
			String symbol = amex.nextElement().toString();
			if (!SQLOperation.recordsExist(symbol)) {
				automateStocks("AMEX", symbol, startYear, startMonth, startDays);
				System.out.println(symbol + " AMX new records processed ");
				count++;
				if (count % 10 == 0) {
					try {
						Thread.sleep(10000);
					} catch (Exception ex) {

					}
				}
			} else {
				System.out.println(symbol + " AMX exists ");

			}
		}

		Enumeration nystocks = nyseStocks.keys();
		while (nystocks.hasMoreElements()) {
			String symbol = nystocks.nextElement().toString();
			if (!SQLOperation.recordsExist(symbol)) {
				automateStocks("NYSE", symbol, startYear, startMonth, startDays);
				System.out.println(symbol + " NYSE new records processed ");
				count++;
				if (count % 10 == 0) {
					try {
						Thread.sleep(10000);
					} catch (Exception ex) {

					}
				}
			} else {
				System.out.println(symbol + " NYSE exists ");

			}
		}

		Enumeration nas = nasStocks.keys();
		while (nas.hasMoreElements()) {
			String symbol = nas.nextElement().toString();
			if (!SQLOperation.recordsExist(symbol)) {
				automateStocks("NASDAQ", symbol, startYear, startMonth,
						startDays);
				System.out.println(symbol + " NASDAQ new records processed ");
				count++;
				if (count % 10 == 0) {
					try {
						Thread.sleep(10000);
					} catch (Exception ex) {

					}
				}
			} else {
				System.out.println(symbol + " NASDAQ exists ");

			}
		}
	}

	public static void automateStocks(String exchange, String symbol,
			int startYear, int startMonth, int startDays) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new java.util.Date());
		int cYear = cal.getTime().getYear() + 1900;
		int cMonth = cal.getTime().getMonth() + 1;
		int cDay = cal.getTime().getDate();
		System.out.println("Current year " + cYear + ", month " + cMonth
				+ ", date " + cDay);
		try {
			Thread.sleep(100);
		} catch (Exception ex) {

		}
		Hashtable dateSeqMap = StaticData.dateMap(false);
		int insertCount = 0;

		for (int j = startYear; j <= cYear; j++) {// year
			for (int i = startMonth; i <= cMonth; i++) { // month
				if (i > startMonth)
					startDays = 1;
				for (int w = startDays; w < 32; w++) { // day
					String date = "" + j;
					if (i < 10) {
						date = date + "0" + i;
					} else {
						date = date + i;
					}

					if (w < 10) {
						date = date + "0" + w;
					} else {
						date = date + w;
					}

					Hashtable stocks = null;
					if (exchange.equalsIgnoreCase("AMEX")) {
						stocks = Files.getEODRecordsAMX(date);
					} else if (exchange.equalsIgnoreCase("NYSE")) {
						stocks = Files.getEODRecordsNYSE(date);
					} else if (exchange.equalsIgnoreCase("NASDAQ")) {
						stocks = Files.getEODRecordsNASDAQ(date);
					}

					String y = date.substring(0, 4);
					String m = date.substring(4, 6);
					String d = date.substring(6, 8);
					java.sql.Date cdate = java.sql.Date.valueOf(y + "-" + m
							+ "-" + d);
					YStock record = (YStock) stocks.get(symbol);

					try {
						if (record != null) {
							String dateString = record.getDate();

							if (dateSeqMap.containsKey(dateString)) {
								String indexString = dateSeqMap.get(dateString)
										.toString();
								int seqIndex = Integer.parseInt(indexString);
								try {
									// if (insertCount >= 300) {
									// SQLOperation.insertRecordSP(seqIndex,
									// record, cdate);
									// } else {
									SQLOperation.insertRecord(seqIndex, record);
									// }
									// insertCount++;
								} catch (Exception ex) {
									ex.printStackTrace(System.out);
								}
							}

						}
					} catch (Exception ex) {

					}
					System.out.println("Process done for " + symbol
							+ " at date " + date);
				}
			}
		}

		SingleStockRecalulation(symbol);

	}

	public static void automateIndexOnly(String symbol, int startYear,
			int startMonth, int startDays) {
		int cYear = 2017;
		int cMonth = 6;
		int cDay = 19;

		for (int j = startYear; j <= cYear; j++) {// year
			for (int i = startMonth; i <= cMonth; i++) { // month
				if (i > startMonth)
					startDays = 1;
				for (int w = startDays; w < 32; w++) { // day
					String date = "" + j;
					if (i < 10) {
						date = date + "0" + i;
					} else {
						date = date + i;
					}

					if (w < 10) {
						date = date + "0" + w;
					} else {
						date = date + w;
					}
					SingleStockInsert(date, symbol);
					amxStocks = null;
					System.out.println("Process done for " + symbol
							+ " at date " + date);
				}
			}
		}

		SingleStockRecalulation(symbol);

	}

	public static void SingleStockInsert(String date, String symbol) {
		String y = date.substring(0, 4);
		String m = date.substring(4, 6);
		String d = date.substring(6, 8);
		java.sql.Date cdate = java.sql.Date.valueOf(y + "-" + m + "-" + d);
		YStock record = getRecord(symbol, date);

		try {
			if (record != null) {
				SQLOperation.insertEODRecord(record, cdate);

			}
		} catch (Exception ex) {

		}

	}

	// NEED TO RUN MONTHLY TO GET TMAI ETC
	public static void recalculateALLFreshStocks() {
		// Hashtable stocks = SQLOperation.getFreshYahooStocks();
		Hashtable stocks = SQLOperation.getCalReadyNewStocks();

		Enumeration en = stocks.keys();
		int size = stocks.size();
		int count = 0;
		System.out.println("Total stocks needs to be processed..." + size);
		try {
			Thread.sleep(7000);
		} catch (Exception ex) {

		}
		while (en.hasMoreElements()) {
			count++;
			String symbol = en.nextElement().toString();
			System.out.println("Start processing " + symbol);
			try {
				SingleStockRecalulation(symbol);
				System.out.println("Done processing " + symbol + " " + count
						+ " out of " + size);
				System.out.println("Total stocks needs to be processed..."
						+ size);
				try {
					Thread.sleep(4000);
				} catch (Exception ex) {

				}

			} catch (Exception ex) {

			}

		}
	}

	public static void SingleStockRecalulation(String symbol) {
		long t1 = System.currentTimeMillis();
		SQLOperation.markMonthEnd(symbol, -1);
		SQLOperation.markWeekEnds(symbol, true, -1);
		SQLOperation.calculateTMA(symbol, false, -1);
		SQLOperation.calculatePTMAHistory(symbol, -1);
		SQLOperation.calculateTMAV(symbol, false, -1);
		SQLOperation.calculatePTMAVHistory(symbol, -1);
		SQLOperation.calculateDSI(symbol, 5, -1);
		SQLOperation.calculateDSI24HistorySP(symbol);
		// NOT WORKING
		// SQLOperation.calculateDSI(symbol, 24, -1);
		SQLOperation.calculateAVGLMDRHistory(symbol, false);
		SQLOperation.calculateTrendPointsHistory(symbol, -1, -1);
		SQLOperation.calculateIncDescTrendDays(symbol, -1, false, 40);
		SQLOperation.calculateDeltaAgainstSPY(symbol, -1, false, 40);
		SQLOperation.calculateDSISumHistory(symbol);
		SQLOperation.calculateSurgeHistorySP(symbol);
		SQLOperation.calculateDSI5SumHistorySP(symbol);
		SQLOperation.calculatePTMAVSumHistorySP(symbol);
		SQLOperation.scoreDipOp(symbol, -1, false);
		SQLOperation.getDPS4History(symbol, -1, false);
		SQLOperation.getTurnPointScore(symbol, -1, false);
		SQLOperation.getTotalBuyScore(symbol, -1, false);
		SQLOperation.getDipscoreSum(symbol, -1, 7, false);
		SQLOperation.getDipSumGradient(symbol, -1, false);
		SQLOperation.getDS3PerTrendHistory(symbol);
		SQLOperation.getDS3PerSum(symbol, -1, 20, false);
		SQLOperation.getTWATrendHistory(symbol);
		SQLOperation.getBBDIScore(symbol, -1, false);
		SQLOperation.markBBEndPoint(symbol, -1, false);
		// SQLOperation.calculateBBScore(symbol, -1, false);
		SQLOperation.calculateSellingScores(symbol, -1, false);
		SQLOperation.calculateUTI(symbol, -1, false);
		SQLOperation.calculateUTIStep1(symbol, -1, false);
		SQLOperation.calculateUTISteps23(symbol, -1, false);
		// SQLOperation.validateBBScore(symbol,42964);
		SQLOperation.getDS3PerTrendHistoryByPercentageStep1(symbol, -1, false);
		SQLOperation.getDS3PerTrendHistoryByPercentageStep2(symbol, -1, false);
		SQLOperation.getDS3PerTrendHistoryByPercentageStep3(symbol, -1, false);
		SQLOperation.calculateBBScore(symbol, -1, false);
		SQLOperation.calculateSumAWS(symbol, -1, false);
		SQLOperation.calculateLBBIHistory(symbol, -1, false);
		SQLOperation.calculateEntirePTS(symbol, -1);

		// SQLOperation.overlayAllBBScore(endIndex1, true);
		// SQLOperation.updateLastIBSSum(endIndex1, true);
		// SQLOperation.scoreAllStocksIBBS(endIndex1);

		try {
			Thread.sleep(3000);
		} catch (Exception ex) {

		}

		long t2 = System.currentTimeMillis();
		System.out.println("EOD $$$$$$$ Step 3 done cost seconds " + (t2 - t1)
				/ 1000);

	}

	/***
	 * records start 2017-02-06 , pricefinal*volume = 200,000; check records, if
	 * exist, not insert, if not insert; then check previous records start
	 * 2017-02-6, if exist, always find record then insert, regardless
	 * pricefinal*volume result if record number>240,then store proc, otherwise
	 * sql insert Initial records tagged and recaulculated completely
	 * 
	 * @param date
	 */
	public static void fillMissingRecords(int startIndex) {
		Hashtable dates = SQLOperation.fillMissingRecords(startIndex);
		for (int w = 1; w <= dates.size(); w++) {
			String ndate = dates.get("" + w).toString();
			System.out.println("Start fill missing records for date " + ndate);
			fillMissingRecordsForDate(ndate);
			System.out.println("Done fill missing records for date " + ndate);
			try {
				Thread.sleep(5000);
			} catch (Exception ex) {

			}
		}
	}

	public static void fillMissingRecordsForDate(String date) {

		initEODStocks(date);
		Hashtable indexTable = StaticData.dateMap(true);
		long start = System.currentTimeMillis();
		int count = 0;

		String y = date.substring(0, 4);
		String m = date.substring(4, 6);
		String d = date.substring(6, 8);
		String dateString = m + "-" + d + "-" + y;
		int seqIndex = Integer.parseInt(indexTable.get(dateString).toString());
		System.out.println("Seqindex is " + seqIndex);

		// get all non index stocks
		currentExistingStocks = SQLOperation.getStocks();

		java.sql.Date cdate = java.sql.Date.valueOf(y + "-" + m + "-" + d);

		Enumeration ennyse = nyseStocks.keys();
		while (ennyse.hasMoreElements()) {
			count++;
			String symbol = ennyse.nextElement().toString();
			// if(symbol.equalsIgnoreCase("DKS")){

			System.out.println("Processing... " + symbol);
			YStock record = getRecord(symbol, date);

			try {
				if (record != null) {
					// month end, PTMA, PTMAV, DSI5 have been
					// done inside stored proc now, so no longer needed
					// to be called in Java
					// SQLOperation.insertEODRecord(record, cdate);
					checkAndInsertRecord(seqIndex, symbol, record, cdate);
				}

			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}
		}

		Enumeration ennas = nasStocks.keys();
		while (ennas.hasMoreElements()) {
			count++;
			String symbol = ennas.nextElement().toString();

			System.out.println("Processing... " + symbol);
			YStock record = getRecord(symbol, date);

			try {
				if (record != null) {
					// month end, PTMA, PTMAV, DSI5 have been
					// done inside stored proc now, so no longer needed
					// to be called in Java
					// SQLOperation.insertEODRecord(record, cdate);
					checkAndInsertRecord(seqIndex, symbol, record, cdate);
				}

			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}

		}

		Enumeration enamx = amxStocks.keys();
		while (enamx.hasMoreElements()) {
			count++;
			String symbol = enamx.nextElement().toString();
			System.out.println("Processing... " + symbol);
			YStock record = getRecord(symbol, date);

			try {
				if (record != null) {
					// month end, PTMA, PTMAV, DSI5 have been
					// done inside stored proc now, so no longer needed
					// to be called in Java
					// SQLOperation.insertEODRecord(record, cdate);
					checkAndInsertRecord(seqIndex, symbol, record, cdate);
				}

			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}
		}

		Enumeration enindex = eodIndexes.keys();
		while (enindex.hasMoreElements()) {
			count++;
			String symbol = enindex.nextElement().toString();
			System.out.println("Processing... " + symbol);
			YStock record = getRecord(symbol, date);

			try {
				if (record != null) {
					// month end, PTMA, PTMAV, DSI5 have been
					// done inside stored proc now, so no longer needed
					// to be called in Java
					// SQLOperation.insertEODRecord(record, cdate);
					checkAndInsertRecord(seqIndex, symbol, record, cdate);
				}

			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}

		}

		// System.out.println("Done processing " + symbol);
		System.out.println("AMX count " + amxCount);
		System.out.println("NASDQ count " + nasCount);
		System.out.println("NYSE count " + nyseCount);
		System.out.println("Index count " + indexCount);

		// Enumeration enKeys = leftStocks.keys();
		// while (enKeys.hasMoreElements()) {
		// String sym = enKeys.nextElement().toString();
		// System.out.println("Top off symbol: " + sym);
		// }

	}

	private static void checkAndInsertRecord(int seqIndex, String symbol,
			YStock record, java.sql.Date cdate) {
		boolean needInsert = false;
		boolean freshStock = false;
		float cutOffPV = 200000f;

		if (!currentExistingStocks.containsKey(symbol)) {
			if (record.getFinalPrice() * record.getVolume() >= cutOffPV) {

				needInsert = true;
				freshStock = true;
				currentExistingStocks.put(symbol, symbol);
			}
		}

		if (currentExistingStocks.containsKey(symbol)) {
			needInsert = true;
		}

		// only need when partial records inserted for a given day
		boolean exist = false;// SQLOperation.checkRecordExisting(symbol,seqIndex);

		if (needInsert && !exist) {
			if (freshStock) {
				// regular Java insertion
				SQLOperation.insertRecord(seqIndex, record);
				SQLOperation.setNewSymbolTag(symbol, seqIndex, 1000008.0f);

			} else {
				// check existing record size
				// int recordSize = SQLOperation.checkRecordSize(record
				// .getSymbol());
				int recordSize = SQLOperation.checkTMAV(record.getSymbol(),
						seqIndex);
				// if (recordSize < 350) {
				if (recordSize < 1) {
					// regular Java insertion if size<240
					SQLOperation.insertRecord(seqIndex, record);
				} else {
					// otherwise stored proc will fail in calculating PTMAV
					// which needs 11 months data
					// stock proc insertion if size>=360
					SQLOperation.insertEODRecord(record, cdate);
				}

			}

		}
	}

	private static void checkAndInsertRecordOLD(int seqIndex, String symbol,
			YStock record) {
		boolean needInsert = false;
		boolean freshStock = false;
		float cutOffPV = 200000f;

		if (!currentExistingStocks.containsKey(symbol)) {
			if (record.getFinalPrice() * record.getVolume() >= cutOffPV) {

				needInsert = true;

				if (previousExistingStocks != null
						&& !previousExistingStocks.containsKey(symbol)) {
					freshStock = true;

				}
			}
		}

		if (previousExistingStocks != null
				&& previousExistingStocks.containsKey(symbol)) {
			needInsert = true;
		}

		if (needInsert) {
			if (freshStock) {
				SQLOperation.setNewSymbolTag(symbol, seqIndex, 1000000.0f);
				SQLOperation.insertRecord(seqIndex, record);
			} else {
				SQLOperation.insertRecord(seqIndex, record);

			}
			currentExistingStocks.put(symbol, symbol);

		}
	}

	public static void doAllEODRun(String date) {

		initEODStocks(date);
		Hashtable indexTable = StaticData.dateMap(true);
		long start = System.currentTimeMillis();
		int count = 0;

		String y = date.substring(0, 4);
		String m = date.substring(4, 6);
		String d = date.substring(6, 8);
		String dateString = m + "-" + d + "-" + y;
		int seqIndex = Integer.parseInt(indexTable.get(dateString).toString());
		System.out.println("Seqindex is " + seqIndex);

		java.sql.Date cdate = java.sql.Date.valueOf(y + "-" + m + "-" + d);

		Enumeration enamx = amxStocks.keys();
		while (enamx.hasMoreElements()) {
			count++;
			String symbol = enamx.nextElement().toString();
			System.out.println("Processing... " + symbol);
			YStock record = getRecord(symbol, date);

			try {
				if (record != null) {
					// month end, PTMA, PTMAV, DSI5 have been
					// done inside stored proc now, so no longer needed
					// to be called in Java
					// SQLOperation.insertEODRecord(record, cdate);
					SQLOperation.insertRecord(seqIndex, record);
					if (count % 500 == 0) {
						long end = System.currentTimeMillis();
						System.out
								.println("Processed another 500 records, cost Seconds "
										+ (end - start) / 1000);
						Thread.sleep(3000);

					}
				}

			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}
		}

		Enumeration ennas = nasStocks.keys();
		while (ennas.hasMoreElements()) {
			count++;
			String symbol = ennas.nextElement().toString();
			System.out.println("Processing... " + symbol);
			YStock record = getRecord(symbol, date);

			try {
				if (record != null) {
					// month end, PTMA, PTMAV, DSI5 have been
					// done inside stored proc now, so no longer needed
					// to be called in Java
					// SQLOperation.insertEODRecord(record, cdate);
					SQLOperation.insertRecord(seqIndex, record);

					if (count % 500 == 0) {
						long end = System.currentTimeMillis();
						System.out
								.println("Processed another 500 records, cost Seconds "
										+ (end - start) / 1000);
						Thread.sleep(3000);

					}
				}

			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}

		}

		Enumeration ennyse = nyseStocks.keys();
		while (ennyse.hasMoreElements()) {
			count++;
			String symbol = ennyse.nextElement().toString();
			System.out.println("Processing... " + symbol);
			YStock record = getRecord(symbol, date);

			try {
				if (record != null) {
					// month end, PTMA, PTMAV, DSI5 have been
					// done inside stored proc now, so no longer needed
					// to be called in Java
					// SQLOperation.insertEODRecord(record, cdate);
					SQLOperation.insertRecord(seqIndex, record);

					if (count % 500 == 0) {
						long end = System.currentTimeMillis();
						System.out
								.println("Processed another 500 records, cost Seconds "
										+ (end - start) / 1000);
						Thread.sleep(3000);

					}
				}

			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}

		}

		Enumeration enindex = eodIndexes.keys();
		while (enindex.hasMoreElements()) {
			count++;
			String symbol = enindex.nextElement().toString();
			System.out.println("Processing... " + symbol);
			YStock record = getRecord(symbol, date);

			try {
				if (record != null) {
					// month end, PTMA, PTMAV, DSI5 have been
					// done inside stored proc now, so no longer needed
					// to be called in Java
					// SQLOperation.insertEODRecord(record, cdate);
					SQLOperation.insertRecord(seqIndex, record);

					if (count % 500 == 0) {
						long end = System.currentTimeMillis();
						System.out
								.println("Processed another 500 records, cost Seconds "
										+ (end - start) / 1000);
						Thread.sleep(3000);

					}
				}

			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}

		}

		// System.out.println("Done processing " + symbol);
		System.out.println("AMX count " + amxCount);
		System.out.println("NASDQ count " + nasCount);
		System.out.println("NYSE count " + nyseCount);
		System.out.println("Index count " + indexCount);

		// Enumeration enKeys = leftStocks.keys();
		// while (enKeys.hasMoreElements()) {
		// String sym = enKeys.nextElement().toString();
		// System.out.println("Top off symbol: " + sym);
		// }

	}

	public static void doEODRun(String date, boolean skipTopOff) {

		Hashtable leftStocks = new Hashtable();
		Hashtable Indexes = SQLOperation.getIndexes();
		Hashtable stocks = SQLOperation.getStocks();

		Hashtable tables = new Hashtable();
		Enumeration enIndex = Indexes.keys();
		while (enIndex.hasMoreElements()) {
			String symbol = enIndex.nextElement().toString();
			tables.put(symbol, symbol);
		}
		Enumeration enStock = stocks.keys();
		while (enStock.hasMoreElements()) {
			String symbol = enStock.nextElement().toString();
			tables.put(symbol, symbol);
		}
		System.out.println("Size is " + tables.size());
		Enumeration en = tables.keys();

		long start = System.currentTimeMillis();
		int count = 0;

		String y = date.substring(0, 4);
		String m = date.substring(4, 6);
		String d = date.substring(6, 8);

		java.sql.Date cdate = java.sql.Date.valueOf(y + "-" + m + "-" + d);

		while (en.hasMoreElements()) {
			count++;
			String symbol = en.nextElement().toString();
			System.out.println("Processing... " + symbol);
			YStock record = getRecord(symbol, date);

			try {
				if (record != null) {
					// month end, PTMA, PTMAV, DSI5 have been
					// done inside stored proc now, so no longer needed
					// to be called in Java
					SQLOperation.insertEODRecord(record, cdate);

					if (count % 500 == 0) {
						long end = System.currentTimeMillis();
						System.out
								.println("Processed another 500 records, cost Seconds "
										+ (end - start) / 1000);
						Thread.sleep(3000);

					}

					/*
					 * SQLOperation.calculatePTMAHistory(symbol, 1); //
					 * calculate N day PTMA
					 * SQLOperation.calculatePTMAVHistory(symbol, 1); //
					 * calculate N day PTMAV SQLOperation.calculateDSI(symbol,
					 * 5, 1); // calculate N+1 Thread.sleep(3000);
					 */// day DSI 5
						// days
					// SQLOperation.calculateTWA(symbol, 20, false); //
					// SQLOperation.calculateDSI(symbol, 1, 50);
					// SQLOperation.calculateDSI(symbol, 3, 50);

					// SQLOperation.calculatePTWAHistory(symbol, 50); //
					// SQLOperation.calculateFFPHistory(symbol); //
					// Thread.sleep(2000); //
					// SQLOperation.findGRXPointsHistory(symbol, 50); //
					// SQLOperation.doCBULLCalculation(symbol, 50); //
					// Thread.sleep(2000); //
					// SQLOperation.UpdateRecordAge(symbol);
					// SQLOperation.calculateACPTMAHistory(symbol, 50); //
					// SQLOperation.calculateDelta(symbol, 50);

					// Thread.sleep(3000);
					// System.out.println("Record  find "+symbol);
				} else {
					leftStocks.put(symbol, symbol);
					System.out.println("Record not find " + symbol);
				}

			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}

		}

		if (!skipTopOff) {
			doTopOffRun(leftStocks);
		}
		// System.out.println("Done processing " + symbol);
		System.out.println("AMX count " + amxCount);
		System.out.println("NASDQ count " + nasCount);
		System.out.println("NYSE count " + nyseCount);
		System.out.println("Index count " + indexCount);

		// Enumeration enKeys = leftStocks.keys();
		// while (enKeys.hasMoreElements()) {
		// String sym = enKeys.nextElement().toString();
		// System.out.println("Top off symbol: " + sym);
		// }

	}

	public static void doEODRunOLD(String date, boolean skipTopOff) {

		Hashtable leftStocks = new Hashtable();
		Hashtable Indexes = SQLOperation.getIndexes();
		Hashtable stocks = SQLOperation.getStocks();

		Hashtable tables = new Hashtable();
		Enumeration enIndex = Indexes.keys();
		while (enIndex.hasMoreElements()) {
			String symbol = enIndex.nextElement().toString();
			tables.put(symbol, symbol);
		}
		Enumeration enStock = stocks.keys();
		while (enStock.hasMoreElements()) {
			String symbol = enStock.nextElement().toString();
			tables.put(symbol, symbol);
		}
		System.out.println("Size is " + tables.size());
		Enumeration en = tables.keys();

		long start = System.currentTimeMillis();
		int count = 0;

		String y = date.substring(0, 4);
		String m = date.substring(4, 6);
		String d = date.substring(6, 8);
		java.sql.Date cdate = java.sql.Date.valueOf(y + "-" + m + "-" + d);

		while (en.hasMoreElements()) {
			count++;
			String symbol = en.nextElement().toString();
			System.out.println("Processing... " + symbol);
			YStock record = getRecord(symbol, date);

			try {
				if (record != null) {
					// month end, PTMA, PTMAV, DSI5 have been
					// done inside stored proc now, so no longer needed
					// to be called in Java
					SQLOperation.insertEODRecord(record, cdate);

					if (count % 500 == 0) {
						long end = System.currentTimeMillis();
						System.out
								.println("Processed another 500 records, cost Seconds "
										+ (end - start) / 1000);
						Thread.sleep(3000);

					}

					/*
					 * SQLOperation.calculatePTMAHistory(symbol, 1); //
					 * calculate N day PTMA
					 * SQLOperation.calculatePTMAVHistory(symbol, 1); //
					 * calculate N day PTMAV SQLOperation.calculateDSI(symbol,
					 * 5, 1); // calculate N+1 Thread.sleep(3000);
					 */// day DSI 5
						// days
					// SQLOperation.calculateTWA(symbol, 20, false); //
					// SQLOperation.calculateDSI(symbol, 1, 50);
					// SQLOperation.calculateDSI(symbol, 3, 50);

					// SQLOperation.calculatePTWAHistory(symbol, 50); //
					// SQLOperation.calculateFFPHistory(symbol); //
					// Thread.sleep(2000); //
					// SQLOperation.findGRXPointsHistory(symbol, 50); //
					// SQLOperation.doCBULLCalculation(symbol, 50); //
					// Thread.sleep(2000); //
					// SQLOperation.UpdateRecordAge(symbol);
					// SQLOperation.calculateACPTMAHistory(symbol, 50); //
					// SQLOperation.calculateDelta(symbol, 50);

					// Thread.sleep(3000);
					// System.out.println("Record  find "+symbol);
				} else {
					leftStocks.put(symbol, symbol);
					System.out.println("Record not find " + symbol);
				}

			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}

		}

		if (!skipTopOff) {
			doTopOffRun(leftStocks);
		}
		// System.out.println("Done processing " + symbol);
		System.out.println("AMX count " + amxCount);
		System.out.println("NASDQ count " + nasCount);
		System.out.println("NYSE count " + nyseCount);
		System.out.println("Index count " + indexCount);

		// Enumeration enKeys = leftStocks.keys();
		// while (enKeys.hasMoreElements()) {
		// String sym = enKeys.nextElement().toString();
		// System.out.println("Top off symbol: " + sym);
		// }

	}

	public static void validateSplits(Hashtable tables) {
		Hashtable stocks = SQLOperation.getStocks();

		if (tables == null) {
			tables = new Hashtable();
			Enumeration enStock = stocks.keys();
			while (enStock.hasMoreElements()) {
				String symbol = enStock.nextElement().toString();
				tables.put(symbol, symbol);
			}
		}
		System.out.println("Size " + tables.size());
		Enumeration en = tables.keys();

		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();

			// SQLOperation.deleteRecord(symbol);
			// System.out.println("Start processing " + symbol);
			String downLoadSymbol = "%5E" + symbol.substring(1);

			if (stocks.containsKey(symbol))
				downLoadSymbol = symbol;

			try {

				boolean splitted = SQLOperation.verifyStockForSplit(symbol);

				if (splitted) {
					calculateSingleStockSP(symbol, false);
					System.out.println("Recalculation done");
					Thread.sleep(3000);
				}

			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}

			System.out.println("Done processing " + symbol);
		}
	}

	public static void doTopOffRun(Hashtable tables) {
		Hashtable Indexes = SQLOperation.getIndexes();
		Hashtable stocks = SQLOperation.getStocks();

		if (tables == null) {
			tables = new Hashtable();

			Enumeration enIndex = Indexes.keys();
			while (enIndex.hasMoreElements()) {
				String symbol = enIndex.nextElement().toString();
				tables.put(symbol, symbol);
			}
			Enumeration enStock = stocks.keys();
			while (enStock.hasMoreElements()) {
				String symbol = enStock.nextElement().toString();
				tables.put(symbol, symbol);
			}
		}
		System.out.println("Size " + tables.size());
		Enumeration en = tables.keys();

		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();

			// SQLOperation.deleteRecord(symbol);
			System.out.println("Start processing " + symbol);
			String downLoadSymbol = "%5E" + symbol.substring(1);

			if (stocks.containsKey(symbol))
				downLoadSymbol = symbol;

			try {
				HttpDownload.downLoadStockHistory(downLoadSymbol, symbol);
				Hashtable results = Files.getIndexHistory(symbol);
				SQLOperation.insertHistoryRecord(results);
				SQLOperation.markMonthEnd(symbol, 60);
				SQLOperation.markWeekEnds(symbol, true, 60);
				Thread.sleep(3000);
				SQLOperation.calculateTMA(symbol, false, 500);
				SQLOperation.calculatePTMAHistory(symbol, 50);
				Thread.sleep(3000);
				SQLOperation.calculateTMAV(symbol, false, 500);
				SQLOperation.calculatePTMAVHistory(symbol, 50);
				Thread.sleep(3000);
				// SQLOperation.calculateDSI(symbol, 1, 50);
				// SQLOperation.calculateDSI(symbol, 3, 50);
				Thread.sleep(2000);
				SQLOperation.calculateDSI(symbol, 5, 50);
				// SQLOperation.calculateTWA(symbol, 20, false);
				// Thread.sleep(2000);
				// SQLOperation.calculatePTWAHistory(symbol, 50);
				// SQLOperation.calculateFFPHistory(symbol);
				// Thread.sleep(2000);
				// SQLOperation.findGRXPointsHistory(symbol, 50);
				// SQLOperation.doCBULLCalculation(symbol, 50);
				// Thread.sleep(2000);
				// SQLOperation.UpdateRecordAge(symbol);
				// SQLOperation.calculateACPTMAHistory(symbol, 50);
				// SQLOperation.calculateDelta(symbol, 50);

				Thread.sleep(3000);
			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}

			System.out.println("Done processing " + symbol);
		}
	}

	public static void recalculatePTMAVSumHistory(Hashtable tables) {
		Hashtable Indexes = SQLOperation.getIndexes();
		Hashtable stocks = SQLOperation.getStocks();

		if (tables == null) {
			tables = new Hashtable();

			Enumeration enIndex = Indexes.keys();
			while (enIndex.hasMoreElements()) {
				String symbol = enIndex.nextElement().toString();
				tables.put(symbol, symbol);
			}
			Enumeration enStock = stocks.keys();
			while (enStock.hasMoreElements()) {
				String symbol = enStock.nextElement().toString();
				tables.put(symbol, symbol);
			}
		}
		System.out.println("Size " + tables.size());
		Enumeration en = tables.keys();

		int loop = 0;
		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();

			loop++;
			System.out.println("Processing " + symbol + " " + loop);

			try {
				SQLOperation.calculatePTMAVSumHistorySP(symbol);

				if (loop % 100 == 0) {
					Thread.sleep(8000);
				}

			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}

			System.out.println("Done processing " + symbol);
		}
	}

	public static void recalculateDsi5SumHistory(Hashtable tables) {
		Hashtable Indexes = SQLOperation.getIndexes();
		Hashtable stocks = SQLOperation.getStocks();

		if (tables == null) {
			tables = new Hashtable();

			Enumeration enIndex = Indexes.keys();
			while (enIndex.hasMoreElements()) {
				String symbol = enIndex.nextElement().toString();
				tables.put(symbol, symbol);
			}
			Enumeration enStock = stocks.keys();
			while (enStock.hasMoreElements()) {
				String symbol = enStock.nextElement().toString();
				tables.put(symbol, symbol);
			}
		}
		System.out.println("Size " + tables.size());
		Enumeration en = tables.keys();

		int loop = 0;
		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();

			loop++;
			System.out.println("Processing " + symbol + " " + loop);

			try {
				SQLOperation.calculateDSI5SumHistorySP(symbol);

				if (loop % 100 == 0) {
					Thread.sleep(5000);
				}

			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}

			System.out.println("Done processing " + symbol);
		}
	}

	public static void recalculateSurgeHistory(Hashtable tables) {
		Hashtable Indexes = SQLOperation.getIndexes();
		Hashtable stocks = SQLOperation.getStocks();

		if (tables == null) {
			tables = new Hashtable();

			Enumeration enIndex = Indexes.keys();
			while (enIndex.hasMoreElements()) {
				String symbol = enIndex.nextElement().toString();
				tables.put(symbol, symbol);
			}
			Enumeration enStock = stocks.keys();
			while (enStock.hasMoreElements()) {
				String symbol = enStock.nextElement().toString();
				tables.put(symbol, symbol);
			}
		}
		System.out.println("Size " + tables.size());
		Enumeration en = tables.keys();

		int loop = 0;
		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();

			loop++;
			System.out
					.println("SURGE HISTORYProcessing " + symbol + " " + loop);

			try {
				SQLOperation.calculateSurgeHistorySP(symbol);

				if (loop % 100 == 0) {
					Thread.sleep(9000);
				}

			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}

			System.out.println("Done processing " + symbol);
		}
	}

	public static void recalculateDSIHistory(Hashtable tables) {
		Hashtable Indexes = SQLOperation.getIndexes();
		Hashtable stocks = SQLOperation.getStocks();

		if (tables == null) {
			tables = new Hashtable();

			Enumeration enIndex = Indexes.keys();
			while (enIndex.hasMoreElements()) {
				String symbol = enIndex.nextElement().toString();
				tables.put(symbol, symbol);
			}
			Enumeration enStock = stocks.keys();
			while (enStock.hasMoreElements()) {
				String symbol = enStock.nextElement().toString();
				tables.put(symbol, symbol);
			}
		}
		System.out.println("Size " + tables.size());
		Enumeration en = tables.keys();

		int loop = 0;
		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();

			loop++;
			System.out.println("Processing " + symbol + " " + loop);

			try {
				SQLOperation.calculateDSIHistorySP(symbol);

				if (loop % 100 == 0) {
					Thread.sleep(5000);
				}

			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}

			System.out.println("Done processing " + symbol);
		}
	}

	public static void doCompleteRun() {
		Hashtable tables = initIndexTable();
		Hashtable stocks = initStockTable();

		System.out.println("Size " + tables.size());
		Enumeration en = tables.keys();

		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			System.out.println("Start processing " + symbol);
			String downLoadSymbol = "%5E" + symbol.substring(1);

			if (stocks.containsKey(symbol))
				downLoadSymbol = symbol;
			calculateSingleStock(downLoadSymbol, symbol, 0.0f, false);

			System.out.println("Done processing " + symbol);
		}
	}

	public static void calculateSingleStockSP(String symbol,
			boolean needDownload) {
		if (needDownload)
			try {
				HttpDownload.downLoadStockHistory(symbol, symbol);
				Hashtable results = Files.getIndexHistory(symbol);
				SQLOperation.insertHistoryRecord(results);
			} catch (Exception ex) {

			}
		Hashtable results = Files.getIndexHistory(symbol);
		Hashtable stocks = new Hashtable();
		stocks.put(symbol, symbol);
		SQLOperation.resetRecords(symbol);
		SQLOperation.markMonthEnd(symbol, -1);
		SQLOperation.calculateTMA(symbol, false, -1);
		SQLOperation.calculatePTMAHistory(symbol, -1);
		SQLOperation.calculateTMAV(symbol, false, -1);
		SQLOperation.calculatePTMAVHistory(symbol, -1);

		recalculateDSIHistory(stocks);
		recalculateSurgeHistory(stocks);
		recalculateDsi5SumHistory(stocks);
		recalculatePTMAVSumHistory(stocks);

	}

	public static void calculateSingleStock(String downLoadSymbol,
			String symbol, float lastPriceMin, boolean quickMode) {

		SQLOperation.deleteRecord(symbol);
		boolean cont = true;

		try {

			HttpDownload.downLoadStockHistory(downLoadSymbol, symbol);
			Hashtable results = Files.getIndexHistory(symbol);
			if (lastPriceMin > 0.1 && !quickMode) {

				YStock stock = (YStock) results.get("" + 1);
				if (stock.getFinalPrice() < lastPriceMin)
					cont = false;

				if (!cont) {
					System.out.println("Skip processing " + symbol
							+ " last price too small " + stock.getFinalPrice());
				}

			}

			if (cont && !quickMode) {
				SQLOperation.insertHistoryRecord(results);
				SQLOperation.markMonthEnd(symbol, -1);
				SQLOperation.markWeekEnds(symbol, true, -1);
				Thread.sleep(8000);
				SQLOperation.calculateTMA(symbol, false, -1);
				SQLOperation.calculatePTMAHistory(symbol, -1);
				Thread.sleep(8000);
				SQLOperation.calculateTMAV(symbol, false, -1);
				SQLOperation.calculatePTMAVHistory(symbol, -1);
				Thread.sleep(8000);
				// SQLOperation.calculateDSI(symbol, 1, -1);
				// SQLOperation.calculateDSI(symbol, 3, -1);
				Thread.sleep(8000);
				SQLOperation.calculateDSI(symbol, 5, -1);
				// SQLOperation.calculateTWA(symbol, -1, false);
				// Thread.sleep(8000);
				// SQLOperation.calculatePTWAHistory(symbol, -1);
				// SQLOperation.calculateFFPHistory(symbol);
				// Thread.sleep(8000);
				// SQLOperation.findGRXPointsHistory(symbol, -1);
				// SQLOperation.doCBULLCalculation(symbol, -1);
				// Thread.sleep(8000);
				SQLOperation.UpdateRecordAge(symbol);
				// SQLOperation.calculateACPTMAHistory(symbol, -1);
				// SQLOperation.calculateDelta(symbol, -1);

				// Thread.sleep(20000);
			} else if (quickMode) {
				SQLOperation.insertHistoryRecordNoCheck(results, 1);
				SQLOperation.markMonthEnd(symbol, -1);
				Thread.sleep(8000);
				SQLOperation.calculateTMA(symbol, false, -1);
				SQLOperation.calculatePTMAHistory(symbol, -1);
				Thread.sleep(8000);
				SQLOperation.calculateTMAV(symbol, false, -1);
				SQLOperation.calculatePTMAVHistory(symbol, -1);

				SQLOperation.calculateDSI(symbol, 5, -1);
				Thread.sleep(8000);
			}
		} catch (Exception ex) {
			ex.printStackTrace(System.out);
		}
	}

	public static Hashtable initStockTable() {
		Hashtable tables = new Hashtable();
		tables.put("AMZN", "AMZN");
		// tables.put("NVDA", "NVDA");
		/*
		 * //tables.put("JNJ", "JNJ"); //tables.put("JPM", "JPM");
		 * //tables.put("WFC", "WFC"); //tables.put("CVX", "CVX");
		 * //tables.put("PG", "PG"); //tables.put("PFE", "PFE");
		 * //tables.put("T", "T"); //tables.put("BAC", "BAC");
		 * //tables.put("KO", "KO"); tables.put("C", "C"); //tables.put("DIS",
		 * "DIS"); //tables.put("MA", "MA"); //tables.put("V", "V");
		 * tables.put("QCOM", "QCOM"); //tables.put("CMCSA", "CMCSA");
		 * //tables.put("PM", "PM"); tables.put("SLB", "SLB"); tables.put("PEP",
		 * "PEP"); //tables.put("CSCO", "CSCO"); //tables.put("INTC", "INTC");
		 * //tables.put("GILD", "GILD"); //tables.put("UTX", "UTX");
		 * //tables.put("MCD", "MCD"); //tables.put("T", "T");
		 * //tables.put("ABT", "ABT"); //tables.put("VOD", "VOD");
		 * tables.put("TM", "TM"); //tables.put("HON", "HON"); tables.put("GSK",
		 * "GSK"); //tables.put("AZN", "AZN"); tables.put("BP", "BP");
		 * tables.put("SBUX", "SBUX"); //tables.put("RIO", "RIO");
		 * //tables.put("PRU", "PRU"); //tables.put("BCS", "BCS");
		 * tables.put("PEP", "PEP"); //tables.put("AMGN", "AMGN");
		 * //tables.put("UNH", "UNH"); //tables.put("MO", "MO");
		 * //tables.put("MDT", "MDT"); tables.put("UL", "UL"); tables.put("CL",
		 * "CL"); //tables.put("CLX", "CLX"); //tables.put("UPS", "UPS");
		 * //tables.put("FDX", "FDX"); //tables.put("ALL", "ALL");
		 * //tables.put("AGN", "AGN"); tables.put("LLY", "LLY");
		 * //tables.put("COST", "COST"); //tables.put("TGT", "TGT");
		 * //tables.put("UNP", "UNP"); //tables.put("GLD", "GLD");
		 * tables.put("BMY", "BMY"); //tables.put("USB", "USB");
		 * tables.put("CELG", "CELG"); tables.put("LFC", "LFC");
		 * //tables.put("PCLN", "PCLN"); //tables.put("AXP", "AXP");
		 * //tables.put("PBR", "PBR"); tables.put("ACN", "ACN");
		 * tables.put("TXN", "TXN"); //tables.put("BIIB", "BIIB");
		 * tables.put("TWX", "TWX"); //tables.put("MS", "MS");
		 * tables.put("BIDU", "BIDU"); //tables.put("BHP", "BHP");
		 * //tables.put("BLK", "BLK"); tables.put("BX", "BX");
		 * //tables.put("TRV", "TRV"); tables.put("AIG", "AIG");
		 * //tables.put("AAL", "AAL"); //tables.put("LUV", "LUV");
		 * //tables.put("DAL", "DAL"); //tables.put("TMO", "TMO");
		 * tables.put("DHR", "DHR"); //tables.put("KR", "KR"); tables.put("MET",
		 * "MET"); //tables.put("CAT", "CAT"); //tables.put("FOX", "FOX");
		 * //tables.put("CAT", "CAT"); //tables.put("TJX", "TJX");
		 * //tables.put("GD", "GD"); //tables.put("MON", "MON");
		 * //tables.put("F", "F"); //tables.put("PNC", "PNC");
		 * //tables.put("NKE", "NKE"); //tables.put("CRM", "CRM");
		 * //tables.put("MCK", "MCK"); tables.put("EBAY", "EBAY");
		 * //tables.put("AZN", "AZN"); //tables.put("VRX", "VRX");
		 * //tables.put("VLO", "VLO"); //tables.put("VALE", "VALE");
		 * //tables.put("GG", "GG"); //tables.put("STT", "STT");
		 * //tables.put("TROW", "TROW"); tables.put("ROST", "ROST");
		 * //tables.put("HPQ", "HPQ"); //tables.put("TROW", "TROW");
		 * //tables.put("BAX", "BAX"); //tables.put("DB", "DB");
		 * tables.put("IP", "IP"); /* tables.put("AAPL", "AAPL");
		 * tables.put("XOM", "XOM"); tables.put("VZ", "VZ"); tables.put("TSLA",
		 * "TSLA"); tables.put("SPY", "SPY Index"); tables.put("WMT", "WMT");
		 * tables.put("MRK", "MRK"); tables.put("MSFT", "MSFT");
		 * tables.put("AMZN", "AMZN"); tables.put("FB", "FB"); tables.put("MRK",
		 * "MRK"); tables.put("GOOG", "GOOG"); tables.put("WFM", "WFM");
		 * tables.put("GS", "GS"); tables.put("LMT", "LMT"); tables.put("BA",
		 * "BA"); tables.put("HUM", "HUM"); tables.put("APA", "APA");
		 * tables.put("MRK", "MRK"); tables.put("IBM", "IBM");
		 * tables.put("ORCL", "ORCL"); tables.put("WBA", "WBA");
		 * tables.put("HD", "HD"); tables.put("MMM", "MMM"); tables.put("GE",
		 * "GE");
		 */
		return tables;

	}

	public static Hashtable initIndexTable() {
		Hashtable tables = new Hashtable();
		tables.put("AMZN", "AMZN");
		// tables.put("NVDA", "NVDA");
		// tables.put("SPY", "SPY Index");
		// tables.put("JNJ", "JNJ");
		// tables.put("JPM", "JPM");
		// tables.put("WFC", "WFC");
		// tables.put("CVX", "CVX");
		// tables.put("PG", "PG");
		// tables.put("PFE", "PFE");
		// tables.put("T", "T");
		// tables.put("BAC", "BAC");
		// tables.put("KO", "KO");
		/*
		 * tables.put("C", "C"); //tables.put("DIS", "DIS"); //tables.put("MA",
		 * "MA"); //tables.put("V", "V"); tables.put("QCOM", "QCOM");
		 * //tables.put("CMCSA", "CMCSA"); //tables.put("PM", "PM");
		 * tables.put("SLB", "SLB"); tables.put("PEP", "PEP");
		 * //tables.put("CSCO", "CSCO"); //tables.put("INTC", "INTC");
		 * //tables.put("GILD", "GILD"); //tables.put("UTX", "UTX");
		 * //tables.put("MCD", "MCD"); //tables.put("T", "T");
		 * //tables.put("ABT", "ABT"); //tables.put("VOD", "VOD");
		 * tables.put("TM", "TM"); //tables.put("HON", "HON"); tables.put("GSK",
		 * "GSK"); //tables.put("AZN", "AZN"); tables.put("BP", "BP");
		 * tables.put("SBUX", "SBUX"); //tables.put("RIO", "RIO");
		 * //tables.put("PRU", "PRU"); //tables.put("BCS", "BCS");
		 * tables.put("PEP", "PEP"); //tables.put("AMGN", "AMGN");
		 * //tables.put("UNH", "UNH"); //tables.put("MO", "MO");
		 * //tables.put("MDT", "MDT"); tables.put("UL", "UL"); tables.put("CL",
		 * "CL"); //tables.put("CLX", "CLX"); //tables.put("UPS", "UPS");
		 * //tables.put("FDX", "FDX"); //tables.put("ALL", "ALL");
		 * //tables.put("AGN", "AGN"); tables.put("LLY", "LLY");
		 * //tables.put("COST", "COST"); //tables.put("TGT", "TGT");
		 * //tables.put("UNP", "UNP"); //tables.put("GLD", "GLD");
		 * tables.put("BMY", "BMY"); //tables.put("USB", "USB");
		 * tables.put("CELG", "CELG"); tables.put("LFC", "LFC");
		 * //tables.put("PCLN", "PCLN"); //tables.put("AXP", "AXP");
		 * //tables.put("PBR", "PBR"); tables.put("ACN", "ACN");
		 * tables.put("TXN", "TXN"); //tables.put("BIIB", "BIIB");
		 * tables.put("TWX", "TWX"); //tables.put("MS", "MS");
		 * tables.put("BIDU", "BIDU"); //tables.put("BHP", "BHP");
		 * //tables.put("BLK", "BLK"); tables.put("BX", "BX");
		 * //tables.put("TRV", "TRV"); tables.put("AIG", "AIG");
		 * //tables.put("AAL", "AAL"); //tables.put("LUV", "LUV");
		 * //tables.put("DAL", "DAL"); //tables.put("TMO", "TMO");
		 * tables.put("DHR", "DHR"); //tables.put("KR", "KR"); tables.put("MET",
		 * "MET"); //tables.put("CAT", "CAT"); //tables.put("FOX", "FOX");
		 * //tables.put("CAT", "CAT"); //tables.put("TJX", "TJX");
		 * //tables.put("GD", "GD"); //tables.put("MON", "MON");
		 * //tables.put("F", "F"); //tables.put("PNC", "PNC");
		 * //tables.put("NKE", "NKE"); //tables.put("CRM", "CRM");
		 * //tables.put("MCK", "MCK"); tables.put("EBAY", "EBAY");
		 * //tables.put("AZN", "AZN"); //tables.put("VRX", "VRX");
		 * //tables.put("VLO", "VLO"); //tables.put("VALE", "VALE");
		 * //tables.put("GG", "GG"); //tables.put("STT", "STT");
		 * //tables.put("TROW", "TROW"); tables.put("ROST", "ROST");
		 * //tables.put("HPQ", "HPQ"); //tables.put("TROW", "TROW");
		 * //tables.put("BAX", "BAX"); //tables.put("DB", "DB");
		 * tables.put("IP", "IP");
		 */
		/*
		 * tables.put("AAPL", "AAPL"); tables.put("XOM", "XOM");
		 * tables.put("VZ", "VZ"); tables.put("TSLA", "TSLA"); tables.put("SPY",
		 * "SPY Index"); tables.put("WMT", "WMT"); tables.put("MRK", "MRK");
		 * tables.put("MSFT", "MSFT"); tables.put("AMZN", "AMZN");
		 * tables.put("FB", "FB"); tables.put("MRK", "MRK"); tables.put("GOOG",
		 * "GOOG"); tables.put("WFM", "WFM"); tables.put("GS", "GS");
		 * tables.put("LMT", "LMT"); tables.put("BA", "BA"); tables.put("HUM",
		 * "HUM"); tables.put("APA", "APA"); tables.put("MRK", "MRK");
		 * tables.put("IBM", "IBM"); tables.put("ORCL", "ORCL");
		 * tables.put("WBA", "WBA"); tables.put("HD", "HD"); tables.put("MMM",
		 * "MMM"); tables.put("GE", "GE");
		 */
		// tables.put("^FTAS", "^FTAS");
		// tables.put("^CCSI", "Egypt Cairo Stock Exchange");
		// tables.put("^FTAS", "FTSE ALL-SHARE");
		// tables.put("^TA100", "^TA100");

		// tables.put("^FTSE", "London FTSE 100");

		// tables.put("^GSPC", "S&P 500");
		// tables.put("SPY", "SPY Index");
		// tables.put("^JKSE", "Jakarta Composite Index");
		// tables.put("^BSESN", "Indian S&P BSE SENSEX");
		// tables.put("^N225", "Japan Nikkei 225");
		// tables.put("^OMX", "Stockholm 30 Exchange");
		// tables.put("^N100", "EURONEXT 100");

		// tables.put("^GDAXI", "Germany Index");
		// tables.put("^HSI", "Hongkong HANG SENG INDEX");
		// tables.put("^FCHI", "French CAC 40");
		// tables.put("^DJI", "Dow Jones Industrial Average");
		// tables.put("^IXIC", "NASDAQ Composite");
		// tables.put("^GSPC", "S&P 500");
		// tables.put("^NDX", "NASDAQ-100");
		// tables.put("^VIX", "VOLATILITY S&P 500");
		// tables.put("^STI", "Singapore Straits Times Index");
		// tables.put("^RUT", "Russell 2000");
		// tables.put("^BVSP", "Brasil Index");
		// tables.put("^NZ50", "New Zealand 50 INDEX GROSS");
		// tables.put("^KLSE", "FTSE Bursa Malaysia KLCI");
		// tables.put("^SSMI", "Switzland Market Index");
		// tables.put("^NBI", "NASDAQ Biotechnology");
		// tables.put("^MERV", "Argetina Indexx");
		// tables.put("^IBEX", "Brussell 35 Index");
		// tables.put("^ATX", "Austrian Traded Index");
		// tables.put("^AEX", "Amsterdam Exchange index");
		// tables.put("^DJT", "Dow Jones Transportation Averag");
		// tables.put("^TWII", "Taiwan weighted index");
		// tables.put("^AORD", "Austrialia ALL ORDINARIES");
		// tables.put("^KS11", "Korean KOSPI Composite Index");
		// tables.put("^MXX", "Mexico Exchange Index");
		// tables.put("^NSEI", "India CNX NIFTY");
		// tables.put("^XAU", "PHLX Gold/Silver Sector");
		// tables.put("^BFX", "Belgium BEL-20");
		// tables.put("^OSEAX", "Norway Stock Index");
		// tables.put("^SOX", "PHLX Semiconductor");
		// tables.put("^XAL", "NYSE ARCA AIRLINE INDEX");
		// tables.put("^HSCE", "HANG SENG CHINA ENTERPRISES IND ");
		// tables.put("^XOI", "NYSE ARCA OIL & GAS INDEX");
		// tables.put("^UTY", "PHLX Utility Sector");
		// tables.put("^JKPROP", "Construction, Property & Real E");
		// tables.put("^ISEQ", "Irish Stock Exchange Overall Index");
		// tables.put("^IXHC", "NASDAQ Health Care");

		/*
		 * Hashtable stocks = initStockTable(); Enumeration en = stocks.keys();
		 * while (en.hasMoreElements()) { String symbol =
		 * en.nextElement().toString(); tables.put(symbol, symbol); }
		 */
		return tables;

	}

	public static void runDailyIndex() {

		Enumeration en = initIndexTable().keys();

		while (en.hasMoreElements()) {
			String symbol = en.nextElement().toString();
			System.out.println("Start processing " + symbol);

			// if(symbol.equalsIgnoreCase("^N225"))
			try {
				HttpDownload.downLoadStockHistory(symbol);

				Thread.sleep(5000);
			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}

			System.out.println("Done processing " + symbol);
		}
	}

	/*
	 * public static void doLastTenRecalculation(String symbol) {
	 * SQLOperation.getBothEnds(symbol); updateIndexRecordsOnItself(symbol); //
	 * updateBasedOnOlderDailyRecords(symbol);
	 * SQLOperation.markMonthEnds(symbol, true);
	 * SQLOperation.calculateTMA(symbol, 10345, false);
	 * SQLOperation.calculateTMAI(10345, false, symbol);
	 * SQLOperation.getBothEnds(symbol);
	 * 
	 * for (int k = SQLOperation.getEndIndexLast() - 10; k <= SQLOperation
	 * .getEndIndexLast(); k++) { SQLOperation.calculatePTMA(symbol, k); }
	 * 
	 * SQLOperation.markWeekEnds(symbol, true);
	 * SQLOperation.recalculateAllTWA(10345, false, symbol);
	 * SQLOperation.calculateTWAI(10345, false, symbol);
	 * SQLOperation.calculateWWIndicator(symbol,
	 * SQLOperation.getStartIndexLast(), SQLOperation.getEndIndexLast());
	 * 
	 * }
	 * 
	 * public static void doCompleteRecalculation(String symbol) {
	 * SQLOperation.getBothEnds(symbol); updateAllIndexRecords(symbol); //
	 * updateBasedOnOlderDailyRecords(symbol);
	 * SQLOperation.markMonthEnds(symbol, true);
	 * SQLOperation.calculateTMA(symbol, 10345, false);
	 * SQLOperation.calculateTMAI(10345, false, symbol);
	 * SQLOperation.getBothEnds(symbol);
	 * 
	 * for (int k = SQLOperation.getStartIndexLast(); k <= SQLOperation
	 * .getEndIndexLast(); k++) { SQLOperation.calculatePTMA(symbol, k); }
	 * 
	 * SQLOperation.markWeekEnds(symbol, true);
	 * SQLOperation.recalculateAllTWA(10345, false, symbol);
	 * SQLOperation.calculateTWAI(10345, false, symbol);
	 * SQLOperation.calculateWWIndicator(symbol,
	 * SQLOperation.getStartIndexLast(), SQLOperation.getEndIndexLast());
	 * 
	 * SQLOperation.markRegionHighLows(SQLOperation.getStartIndexLast(),
	 * SQLOperation.getEndIndexLast(), symbol); SQLOperation
	 * .RecalculateEntireResetCorrectBullByDoubleHigherHistory(symbol);
	 * 
	 * // SINCE THERE IS NO RANKING INFO, THIS SEEMS NOT NEEDED //
	 * calculateVV(symbol);
	 * 
	 * }
	 * 
	 * 
	 * public static void updateIndexRecordsOnItself(String symbol) { Hashtable
	 * records = Files.getIndexHistory(symbol); int startSeqIndex = 0; int
	 * recordCount = 0; boolean cont = true; boolean start = false;
	 * 
	 * for (int j = 0; cont && j < 3; j++) { VVStock vsExisting =
	 * SQLOperation.getLatestRecord(symbol, 2015); if (j == 1) vsExisting =
	 * SQLOperation.getLatestRecord("^FTSE", 2015); if (j == 2) vsExisting =
	 * SQLOperation.getLatestRecord("^DJI", 2015);
	 * 
	 * int existingIndex = vsExisting.getSeqIndex();
	 * 
	 * // we start last 30 records, which should be enough, // you should have
	 * done update in 30 days! for (int k = 20; k >= 1; k--) {
	 * 
	 * String key = "" + k; VVStock vstock = (VVStock) records.get(key);
	 * 
	 * try { while (!start && !vstock.getTradingDate().equals(
	 * vsExisting.getTradingDate())) { k--; key = "" + k;
	 * 
	 * vstock = (VVStock) records.get(key);
	 * 
	 * }
	 * 
	 * } catch (Exception ex) {
	 * 
	 * }
	 * 
	 * if ((start || vstock.getTradingDate().equals(
	 * vsExisting.getTradingDate()))) {
	 * 
	 * if (startSeqIndex == 0) { startSeqIndex = vsExisting.getSeqIndex(); start
	 * = true; cont = false; } int seqIndex = startSeqIndex + recordCount;
	 * vstock.setSeqIndex(seqIndex);
	 * vstock.setIndustrySector(vstock.getIndustrySector());
	 * vstock.setSector(vstock.getSector());
	 * SQLOperation.insertOrUpdateRecord(vstock); recordCount++; }
	 * 
	 * } }
	 * 
	 * }
	 * 
	 * public static void updateAllIndexRecords(String symbol) { Hashtable
	 * records = null; if (operationMode.equalsIgnoreCase("INDEX")) { records =
	 * Files.getIndexHistory(symbol); } else if
	 * (operationMode.equalsIgnoreCase("ETF")) { records =
	 * Files.getIndexHistory(symbol); } else if
	 * (operationMode.equalsIgnoreCase("SECTOR")) { records =
	 * Files.getVVSectorHistory(symbol); }
	 * 
	 * int startSeqIndex = 0; int recordCount = 0;
	 * 
	 * VVStock vs = SQLOperation.getLatestRecord("SPY", 2015); VVStock
	 * vsExisting = SQLOperation.getLatestRecord(symbol, 2015); int
	 * existingIndex = vsExisting.getSeqIndex();
	 * 
	 * boolean start = false; boolean oldRecord = false; for (int k = 1; k <=
	 * records.size(); k++) {
	 * 
	 * String key = "" + k; VVStock vstock = (VVStock) records.get(key);
	 * 
	 * try { while (!start &&
	 * !vstock.getTradingDate().equals(vs.getTradingDate())) { k++; key = "" +
	 * k;
	 * 
	 * vstock = (VVStock) records.get(key);
	 * 
	 * }
	 * 
	 * } catch (Exception ex) { oldRecord = true; }
	 * 
	 * if (!oldRecord && (start || vstock.getTradingDate().equals(
	 * vs.getTradingDate()))) {
	 * 
	 * if (startSeqIndex == 0) { startSeqIndex = vs.getSeqIndex(); start = true;
	 * } int seqIndex = startSeqIndex - recordCount;
	 * vstock.setSeqIndex(seqIndex);
	 * vstock.setIndustrySector(vstock.getIndustrySector());
	 * vstock.setSector(vstock.getSector());
	 * SQLOperation.insertOrUpdateRecord(vstock); recordCount++; }
	 * 
	 * }
	 * 
	 * if (oldRecord && !start) for (int k = 1; k <= records.size(); k++) {
	 * 
	 * String key = "" + k; VVStock vstock = (VVStock) records.get(key);
	 * 
	 * while (!start && !vstock.getTradingDate().equals(vs.getTradingDate())) {
	 * vs = SQLOperation.getRecord("SPY", vs.getSeqIndex() - 1, 2015); }
	 * 
	 * if (start || vstock.getTradingDate().equals(vs.getTradingDate())) {
	 * 
	 * if (startSeqIndex == 0) { startSeqIndex = vs.getSeqIndex(); start = true;
	 * } int seqIndex = startSeqIndex - recordCount;
	 * vstock.setSeqIndex(seqIndex);
	 * vstock.setIndustrySector(vstock.getIndustrySector());
	 * vstock.setSector(vstock.getSector());
	 * SQLOperation.insertOrUpdateRecord(vstock); recordCount++; }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * public static void updateBasedOnOlderDailyRecords(String symbol) {
	 * Hashtable records = Files.getStockHistory(symbol); int startSeqIndex = 0;
	 * int recordCount = 0;
	 * 
	 * VVStock vs = SQLOperation.getLatestRecord(symbol, 2015);
	 * 
	 * boolean start = false; for (int k = 1; k <= records.size(); k++) {
	 * 
	 * String key = "" + k; VVStock vstock = (VVStock) records.get(key);
	 * 
	 * while (!start && !vstock.getTradingDate().equals(vs.getTradingDate())) {
	 * vs = SQLOperation.getRecord(symbol, vs.getSeqIndex() - 1, 2014); }
	 * 
	 * if (start || vstock.getTradingDate().equals(vs.getTradingDate())) {
	 * 
	 * if (startSeqIndex == 0) { startSeqIndex = vs.getSeqIndex(); start = true;
	 * } int seqIndex = startSeqIndex - recordCount;
	 * vstock.setSeqIndex(seqIndex);
	 * vstock.setIndustrySector(vstock.getIndustrySector());
	 * vstock.setSector(vstock.getSector());
	 * SQLOperation.insertOrUpdateRecord(vstock); recordCount++; }
	 * 
	 * }
	 * 
	 * }
	 */
}
