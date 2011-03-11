package adsevertest;

import net.rim.device.api.ui.UiApplication;

/**
 * This class extends the UiApplication class, providing a
 * graphical user interface.
 */
public class AdserverApp extends UiApplication
{
    /**
     * Entry point for application
     * @param args Command line arguments (not used)
     */ 
    public static void main(String[] args)
    {
        AdserverApp theApp = new AdserverApp();       
        theApp.enterEventDispatcher();
    }
    

    /**
     * Creates a new AdserverApp object
     */
    public AdserverApp()
    {        
        pushScreen(new AdserverScreen());
    }    
}
