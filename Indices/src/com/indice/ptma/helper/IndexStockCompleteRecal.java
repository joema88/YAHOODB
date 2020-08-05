package com.indice.ptma.helper;

import java.util.Hashtable;
import java.util.Enumeration;

public class IndexStockCompleteRecal {

	
	/**
	 * @param args
	 */
	public static Hashtable getTrackedIndex() {
		// -- TOO SHORT: ^OMXC25,IMOEX.ME, WIG20,TASI.SR,^NZDOW,HNX30
		Hashtable indexes = new Hashtable();
		indexes.put("^DJI", "2^DJI");
		indexes.put("^GSPC", "2^GSPC");
		indexes.put("^IXIC", "2^IXIC");
		indexes.put("^RUT", "2^RUT");
		indexes.put("^VIX", "2^VIX");
		indexes.put("^GSPTSE", "2^GSPTSE");
		indexes.put("^BVSP", "2^BVSP");
		indexes.put("^MXX", "2^MXX");
		indexes.put("^GDAXI", "2^GDAXI");
		indexes.put("^FTSE", "2^FTSE");
		indexes.put("^FCHI", "2^FCHI");
		indexes.put("^STOXX50E", "2^STOXX50E");
		indexes.put("^AEX", "2^AEX");
		indexes.put("^IBEX", "2^IBEX");
		indexes.put("FTSEMIB.MI", "FTSEMIB.MX");
		indexes.put("^SSMI", "2^SSMI");
		indexes.put("PSI20.LS", "2PSI20.LS");
		indexes.put("^BFX", "2^BFX");
		indexes.put("^ATX", "2^ATX");
		indexes.put("^OMX", "2^OMX");
		//indexes.put("^OMXC25", "2^OMXC25");
		//indexes.put("^IMOEX.ME", "2IMOEX.ME");
		indexes.put("RTSI.ME", "2RTSI.ME");
		//indexes.put("WIG20", "2WIG20");
		indexes.put("^BUX", "2^BUX");
		indexes.put("XU100.IS", "2XU100.IS");
		indexes.put("TA35.TA", "2TA35.TA");
		//indexes.put("^TASI.SR", "2^TASI.SR");
		indexes.put("^N225", "2^N225");
		indexes.put("^AXJO", "2^AXJO");
		//indexes.put("^NZDOW", "2^NZDOW");
		indexes.put("^SSEC", "2^SSEC");
		indexes.put("^HSI", "2^HSI");
		indexes.put("^TWII", "2^TWII");
		indexes.put("^SET.BK", "2^SET.BK");
		indexes.put("^KS11", "2^KS11");
		indexes.put("^JKSE", "2^JKSE");
		indexes.put("^BSESN", "2^BSESN");
		indexes.put("PSEI.PS", "2PSEI.PS");
		indexes.put("^STI", "2^STI");
		indexes.put("^KSE", "2^KSE");
		//indexes.put("HNX30", "2HNX30");
		indexes.put("^CSE", "2^CSE");

		return indexes;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		IndiceHistory ih = new IndiceHistory();
		
		Hashtable allIndice = getTrackedIndex();
		
		Enumeration en = allIndice.keys();
		
		while(en.hasMoreElements()){
			String key = en.nextElement().toString();
			//String symbol = allIndice.get(key).toString();
			
			try{
		     ih.SingleStockRecalulation(key);
		     System.out.println(key+" recalculation done...");
			}catch(Exception ex){
				ex.printStackTrace();
				System.out.println(key+" not processed");
			}
		
		}

	}

}
