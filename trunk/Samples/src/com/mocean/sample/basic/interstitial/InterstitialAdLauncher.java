package com.mocean.sample.basic.interstitial;

import com.MASTAdview.core.MASTAdview;


public class InterstitialAdLauncher{
	
	public InterstitialAdLauncher() {
		MASTAdview interstitialControl = new MASTAdview(8061, 16112);
//		AdserverInterstitial interstitialControl = new AdserverInterstitial(8061, 16112);
//		AdserverInterstitial interstitialControl = new AdserverInterstitial(0, 0);
		//set ad preferred size
		interstitialControl.setMinSizeX(320);
		interstitialControl.setMinSizeY(460);
		interstitialControl.setUpdateTime(5);
		//set size
		interstitialControl.setSize(320, 460);
		//Push Interstitial control to display stack
		interstitialControl.show();
	}
}
