package com.mocean.sample.basic.interstitial;

import com.MASTAdview.core.AdserverInterstitial;
import com.MASTAdview.core.MASTAdview;

public class InterstitialAdAutoClose {
	
	public InterstitialAdAutoClose() {
		MASTAdview interstitialControl = new MASTAdview(8061, 16112);
		//set ad preferred size
		interstitialControl.setMinSizeX(320);
		interstitialControl.setMinSizeY(460);
		//set size
		interstitialControl.setSize(320, 460);
		//set close time = 10 sec
		interstitialControl.setAutoCloseInterstitialTime(10000);
		//Push Interstitial control to display stack
		interstitialControl.show();
		
	}

}
