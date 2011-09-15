package com.mocean.sample.basic.interstitial;

import com.adserver.core.AdserverInterstitial;

public class InterstitialAdCloseButtonBottom {
	
	public InterstitialAdCloseButtonBottom() {
		AdserverInterstitial interstitialControl = new AdserverInterstitial(8061, 16112);
		//set ad preferred size
		interstitialControl.setMinSizeX(320);
		interstitialControl.setMinSizeY(460);
		//set close button position
		interstitialControl.setCloseButtonPosition(AdserverInterstitial.CLOSEBUTTONPOSITIONBOTTOM);
		//set size
		interstitialControl.setSize(320, 460);
		//Push Interstitial control to display stack
		interstitialControl.pushScreen();
	}

}
