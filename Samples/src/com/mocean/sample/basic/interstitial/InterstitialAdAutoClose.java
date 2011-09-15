package com.mocean.sample.basic.interstitial;

import com.adserver.core.AdserverInterstitial;

public class InterstitialAdAutoClose {
	
	public InterstitialAdAutoClose() {
		AdserverInterstitial interstitialControl = new AdserverInterstitial(8061, 16112);
		//set ad preferred size
		interstitialControl.setMinSizeX(320);
		interstitialControl.setMinSizeY(460);
		//set size
		interstitialControl.setSize(320, 460);
		//set close time = 10 sec
		interstitialControl.setAutoCloseInterstitialTime(10000);
		//Push Interstitial control to display stack
		interstitialControl.pushScreen();
		
	}

}
