package com.mocean.sample.basic.interstitial;

import com.adserver.core.AdserverInterstitial;

public class InterstitialAdCloseButtonDelayed {
	
	public InterstitialAdCloseButtonDelayed() {
		AdserverInterstitial interstitialControl = new AdserverInterstitial(8061, 16112);
		//set ad preferred size
		interstitialControl.setMinSizeX(320);
		interstitialControl.setMinSizeY(460);
		//set size
		interstitialControl.setSize(320, 460);
		//Close button delay = 5 sec
		interstitialControl.setShowCloseButtonTime(5000);
		//Push Interstitial control to display stack
		interstitialControl.pushScreen();
	}

}
