package com.indice.ptma.helper2;

public class YStock {
	
	private String symbol;
	private String date;
		private float openPrice;
	private float lowPrice;
	private float highPrice;
	private float finalPrice;
	private float volume;
	private float adjustedPrice;
	private int fc;
	

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


	public int getFc() {
		return fc;
	}


	public void setFc(int fc) {
		this.fc = fc;
	}


	

}
