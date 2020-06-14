package com.sda.automation;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Key;
import org.sikuli.script.Screen;

import com.sun.tools.javac.Main;

import org.sikuli.script.Pattern;
import java.util.*;

public class StockTeal {

	public static void main(String[] args) {
		Pattern input = new Pattern("C:\\Users\\bpmadmin\\Desktop\\ThinkOrSwim\\pictures\\input.PNG");
		Pattern highlight = new Pattern("C:\\Users\\bpmadmin\\Desktop\\ThinkOrSwim\\pictures\\highlight.PNG");
		Pattern highlight1 = new Pattern("C:\\Users\\bpmadmin\\Desktop\\ThinkOrSwim\\pictures\\highlight1.PNG");
		Pattern highlight2 = new Pattern("C:\\Users\\bpmadmin\\Desktop\\ThinkOrSwim\\pictures\\highlight2.PNG");
		Pattern highlight3 = new Pattern("C:\\Users\\bpmadmin\\Desktop\\ThinkOrSwim\\pictures\\highlight3.PNG");

		Pattern report = new Pattern("C:\\Users\\bpmadmin\\Desktop\\ThinkOrSwim\\pictures\\report.PNG");
		Pattern export = new Pattern("C:\\Users\\bpmadmin\\Desktop\\ThinkOrSwim\\pictures\\export.PNG");
		Pattern fileName = new Pattern("C:\\Users\\bpmadmin\\Desktop\\ThinkOrSwim\\pictures\\fileName.PNG");
		Pattern save = new Pattern("C:\\Users\\bpmadmin\\Desktop\\ThinkOrSwim\\pictures\\save.PNG");
		Pattern close = new Pattern("C:\\Users\\bpmadmin\\Desktop\\ThinkOrSwim\\pictures\\close.PNG");

		Hashtable symbols = new Hashtable();
		symbols.put("AAPL", "AAPL");
		symbols.put("GOOG", "AAPL");
		symbols.put("EBAY", "AAPL");
		symbols.put("FB", "AAPL");
		symbols.put("AMZN", "AAPL");
		symbols.put("WST", "AAPL");
		symbols.put("YRD", "AAPL");
		symbols.put("NBR", "AAPL");
		symbols.put("TQQQ", "AAPL");
		Enumeration en = symbols.keys();
		long t1 = System.currentTimeMillis();
		while (en.hasMoreElements()) {
			try {
				String symbol = en.nextElement().toString();
				Screen s = new Screen();
				s.find(input);
				s.type(input, symbol);
				s.keyDown(Key.ENTER);
				s.keyUp(Key.ENTER);
				for (int i = 0; i < symbol.length(); i++) {
					s.keyDown(Key.BACKSPACE);
					s.keyUp(Key.BACKSPACE);
				}
				boolean find = false;

				if (!find) {

					try {
						Thread.sleep(4000);
						s.wait(highlight.similar((double) 0.6), 2000);
						s.rightClick();
						find = true;
					} catch (Exception ex) {
						find = false;
					}
				}
				if (!find) {
					try {
						s.wait(highlight1.similar((double) 0.6), 2000);
						s.rightClick();
						find = true;
					} catch (Exception ex) {
						find = false;

					}
				}
				if (!find) {
					try {
						s.wait(highlight2.similar((double) 0.6), 2000);
						s.rightClick();
						find = true;
					} catch (Exception ex) {
						find = false;

					}
				}
				if (!find) {
					try {
						s.wait(highlight3.similar((double) 0.6), 2000);
						s.rightClick();
						find = true;
					} catch (Exception ex) {
						find = false;

					}
				}
				s.wait(report.similar((double) 0.9), 2000);
				// s.find(report.similar((double)0.9));
				s.click();
				s.wait(export.similar((double) 0.9), 2000);
				s.click();
				s.wait(fileName.similar((double) 0.9), 2000);
				s.click();
				for (int i = 0; i < 5; i++) {
					s.keyDown(Key.BACKSPACE);
					s.keyUp(Key.BACKSPACE);
				}
				s.write("Base");
				s.find(save).click();
				s.find(close).click();
			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}
		}
		long t2 = System.currentTimeMillis();
		System.out.println(symbols.size()+" stock download, Total time cost minutes:"+(t2-t1)/(1000*60));
	}

	
	public static void process(String symbol) {
		Pattern input = new Pattern("C:\\Users\\bpmadmin\\Desktop\\ThinkOrSwim\\pictures\\input.PNG");
		Pattern highlight1 = new Pattern("C:\\Users\\bpmadmin\\Desktop\\ThinkOrSwim\\pictures\\Teal1.PNG");
		Pattern highlight2 = new Pattern("C:\\Users\\bpmadmin\\Desktop\\ThinkOrSwim\\pictures\\Teal2.PNG");
		Pattern highlight3 = new Pattern("C:\\Users\\bpmadmin\\Desktop\\ThinkOrSwim\\pictures\\Teal3.PNG");

		Pattern report = new Pattern("C:\\Users\\bpmadmin\\Desktop\\ThinkOrSwim\\pictures\\report.PNG");
		Pattern export = new Pattern("C:\\Users\\bpmadmin\\Desktop\\ThinkOrSwim\\pictures\\export.PNG");
		Pattern fileName = new Pattern("C:\\Users\\bpmadmin\\Desktop\\ThinkOrSwim\\pictures\\fileName.PNG");
		Pattern save = new Pattern("C:\\Users\\bpmadmin\\Desktop\\ThinkOrSwim\\pictures\\save.PNG");
		Pattern close = new Pattern("C:\\Users\\bpmadmin\\Desktop\\ThinkOrSwim\\pictures\\close.PNG");

		
			try {
				//String symbol = en.nextElement().toString();
				Screen s = new Screen();
				s.find(input);
				s.type(input, symbol);
				s.keyDown(Key.ENTER);
				s.keyUp(Key.ENTER);
				for (int i = 0; i < symbol.length(); i++) {
					s.keyDown(Key.BACKSPACE);
					s.keyUp(Key.BACKSPACE);
				}
				boolean find = false;

				if (!find) {
					try {
						s.wait(highlight1.similar((double) 0.6), 2000);
						s.rightClick();
						find = true;
					} catch (Exception ex) {
						find = false;

					}
				}
				if (!find) {
					try {
						s.wait(highlight2.similar((double) 0.6), 2000);
						s.rightClick();
						find = true;
					} catch (Exception ex) {
						find = false;

					}
				}
				if (!find) {
					try {
						s.wait(highlight3.similar((double) 0.6), 2000);
						s.rightClick();
						find = true;
					} catch (Exception ex) {
						find = false;

					}
				}
				s.wait(report.similar((double) 0.9), 2000);
				// s.find(report.similar((double)0.9));
				s.click();
				s.wait(export.similar((double) 0.9), 2000);
				s.click();
				s.wait(fileName.similar((double) 0.9), 2000);
				s.click();
				for (int i = 0; i < 5; i++) {
					s.keyDown(Key.BACKSPACE);
					s.keyUp(Key.BACKSPACE);
				}
				s.write("Teal");
				s.find(save).click();
				s.find(close).click();
			} catch (Exception ex) {
				ex.printStackTrace(System.out);
			}
		
	}
}
