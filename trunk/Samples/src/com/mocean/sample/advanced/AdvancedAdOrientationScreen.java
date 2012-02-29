package com.mocean.sample.advanced;

import com.MASTAdview.core.MASTAdview;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.MainScreen;

public class AdvancedAdOrientationScreen extends MainScreen{
	MASTAdview field;
	
	public AdvancedAdOrientationScreen() {
		super();
		setTitle("Advances Ad Orientation Sample");
		field = new MASTAdview(8061, 16741);
		field.setSize(320,50);
		field.setUpdateTime(5);
		add(field);
		add(new LabelField("Best view with phones resolution 360x480:"));
		add(new LabelField("Rotate phone to lanscape/portrate mode to reload ad with different site/zone "));
	}
	
	public void sublayout(int width, int height)
	   {
	       if(Display.getOrientation()== Display.ORIENTATION_LANDSCAPE)
		       {
		    	   field.setSite(8061);
		    	   field.setZone(16741);
		    	   int displayWidth = Display.getWidth();
		    	   field.setSize(displayWidth, 50);
//		    	   field.setSize(468, 60);
		    	   field.setMinSizeX(468);
		    	   field.setMinSizeY(60);
		    	   field.setMaxSizeX(468);
		    	   field.setMaxSizeY(60);
		    	   field.update();
		       }
	       else if(Display.getOrientation()== Display.ORIENTATION_PORTRAIT)
		       {
		    	   field.setSite(8061);
		    	   field.setZone(20249);
		    	   int displayWidth = Display.getWidth();
		    	   field.setSize(displayWidth, 50);
		    	   field.setMinSizeX(320);
		    	   field.setMinSizeY(50);
		    	   field.setMaxSizeX(320);
		    	   field.setMaxSizeY(50);
		    	   field.update();
		       }
	       super.sublayout(width, height);
	   }
}
