package com.intraday.ptma.helper;

import java.util.StringTokenizer;
import java.sql.Date;


public class YStock {
	private String symbol;
	private String date;
	private String timestamp;
	private int chour;
	private int cminute;
	private float openPrice;
	private float lowPrice;
	private float highPrice;
	private float finalPrice;
	private float volume;
	private float adjustedPrice;
	private long seqIndex;
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}


	public float getOpenPrice() {
		return openPrice;
	}


	public void setOpenPrice(float openPrice) {
		this.openPrice = openPrice;
	}


	public float getLowPrice() {
		return lowPrice;
	}


	public void setLowPrice(float lowPrice) {
		this.lowPrice = lowPrice;
	}


	public float getHighPrice() {
		return highPrice;
	}


	public void setHighPrice(float highPrice) {
		this.highPrice = highPrice;
	}


	public float getFinalPrice() {
		return finalPrice;
	}


	public void setFinalPrice(float finalPrice) {
		this.finalPrice = finalPrice;
	}


	public float getAdjustedPrice() {
		return adjustedPrice;
	}


	public void setAdjustedPrice(float adjustedPrice) {
		this.adjustedPrice = adjustedPrice;
	}


	public String getSymbol() {
		return symbol;
	}


	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}


	public float getVolume() {
		return volume;
	}


	public void setVolume(float volume) {
		this.volume = volume;
	}


	public int getChour() {
		return chour;
	}


	public void setChour(int chour) {
		this.chour = chour;
	}


	public int getCminute() {
		return cminute;
	}


	public void setCminute(int cminute) {
		this.cminute = cminute;
	}


	public String getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
		try{
		StringTokenizer tok4 = new StringTokenizer(this.timestamp,":");
		String hour=tok4.nextToken();
		if(hour.equalsIgnoreCase("14")){
			setChour(9);
		}if(hour.equalsIgnoreCase("15")){
			setChour(10);
		}if(hour.equalsIgnoreCase("16")){
			setChour(11);
		}if(hour.equalsIgnoreCase("17")){
			setChour(12);
		}if(hour.equalsIgnoreCase("18")){
			setChour(13);
		}if(hour.equalsIgnoreCase("19")){
			setChour(14);
		}if(hour.equalsIgnoreCase("20")){
			setChour(15);
		}if(hour.equalsIgnoreCase("21")){
			setChour(16);
		}
		
		setCminute(Integer.parseInt(tok4.nextToken()));
		}catch(Exception ex){
			
		}
	}


	public long getSeqIndex() {
		return seqIndex;
	}


	public void setSeqIndex(long seqIndex) {
		this.seqIndex = seqIndex;
	}

}
