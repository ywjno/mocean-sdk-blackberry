package com.mocean.sample.basic.interstitial;

import com.MASTAdview.core.AdserverInterstitial;
import com.MASTAdview.core.MASTAdview;

public class InterstitialAdCloseButtonDelayed {
	
	public InterstitialAdCloseButtonDelayed() {
		MASTAdview interstitialControl = new MASTAdview(8061, 16112);
		//set ad preferred size
		interstitialControl.setMinSizeX(320);
		interstitialControl.setMinSizeY(460);
		//set size
		interstitialControl.setSize(320, 460);
		//Close button delay = 5 sec
		interstitialControl.setShowCloseButtonTime(5000);
		//Push Interstitial control to display stack
		interstitialControl.show();
	}

}
