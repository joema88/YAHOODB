package com.sda.automation;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Screen;
import org.sikuli.script.Pattern;
import org.sikuli.script.*;
public class LoginGmail {

	public static void main(String[] args) {
		try {
		Screen screen = new Screen();
		//Pattern image1 = new Pattern("C:\\Users\\bpmadmin\\Desktop\\ThinkOrSwim\\pictures\\gmail.PNG");
		Pattern image2 = new Pattern("C:\\Users\\bpmadmin\\Desktop\\ThinkOrSwim\\pictures\\input.PNG");
		
		//relativeReg = find("iamge.png")
	//	Region	reg1 = screen.find(image2);
	//	Region	reg = new Region(reg1.x , reg1.y, reg1.w ,reg1.h );
		//screen.click(image1);
		//screen.doubleClick(image2, -20);
		//reg.getCenter().doubleClick();
		//screen.write("AMAZON");
		//screen.type(image2, "AMZON");
		//reg.doubleClick();
		 try
		    {
		      //  pattern = new Pattern(imageLocation);
		        screen.wait(image2 ,30);
		        screen.click(image2);
		        System.out.println("First Attempt To Find Image.");
		    }
		    catch(FindFailed f)
		    {
		        System.out.println("Exception In First Attempt: " +f.getMessage());
		      //  System.out.println("FindFailed Exception Handled By Method: ClickObjectUsingSikuli. Please check image being used to identify the webelement. supplied image: " +imageLocation);
		      //  Assert.fail("Image wasn't found. Please use correct image.");
		    }

		    Thread.sleep(1000);

		    //In case image/object wasn't clicked in first attempt and cursor stays in the same screen, then do second atempt.
		    if(screen.exists(image2) != null)
		    {
		        try
		        {
		            screen.getLastMatch().click(image2);
		            System.out.println("Second Attempt To Find Image.");
		         //   System.out.println("Object: " +imageLocation + " is clicked successfully.");
		        }
		        catch(FindFailed f)
		        {
		            System.out.println("Exception In Second Attempt: " +f.getMessage());
		         //   System.out.println("FindFailed Exception Handled By Method: ClickObjectUsingSikuli. Please check image being used to identify the webelement. supplied image: " +imageLocation);
		        }
		    }
		}catch(Exception ex) {
			ex.printStackTrace(System.out);
		}

	}

}
