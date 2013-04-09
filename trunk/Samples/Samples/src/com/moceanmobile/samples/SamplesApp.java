package com.moceanmobile.samples;

import net.rim.device.api.ui.UiApplication;

import com.moceanmobile.mast.BackgroundQueue;


public class SamplesApp extends UiApplication
{
    public static void main(String[] args)
    {
        SamplesApp theApp = new SamplesApp();       
        theApp.enterEventDispatcher();
    }

    public SamplesApp()
    {        
        // Push a screen onto the UI stack for rendering.
        pushScreen(new SamplesScreen());
        
        //
        // Used for extra debug info from the MASTAdView internal thread pool.
        // This setting affects all MASTAdView instances as they all share the
        // same queue singleton instance.
        //
        BackgroundQueue.getInstance().setConsoleLog(false);
    }
}
