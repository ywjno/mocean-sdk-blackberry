package com.mocean.sample.advanced;

import com.adserver.core.AdClickListener;
import com.adserver.core.Adserver;

import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

public class AdvancedAdConsumeMenuScreen extends MainScreen implements AdClickListener{
	Adserver field;
	
	public AdvancedAdConsumeMenuScreen() {
		super();
		setTitle("Advances Ad Orientation Sample");
		field = new Adserver(8061, 16741);
		field.setSize(320,50);
		field.setUpdateTime(5);
		field.setClickListener(this);
		add(field);
		add(new LabelField("Clicking Ad do not invoke menu"));
	}

	public boolean didAdClicked(String arg0) {
		return false;
	}
	public boolean onMenu(int instance){
        if( instance == Menu.INSTANCE_CONTEXT ){
            //Retrieve the main menu (for some reason, the context menu cannot handle image hyperlinks)
            Menu mainMenu = this.getMenu( Menu.INSTANCE_DEFAULT );
            
            int numItems = mainMenu.getSize();
            for( int i = 0; i < numItems; i++ ){
                MenuItem curItem = mainMenu.getItem(i);
                String name = curItem.toString();
                if( name.equalsIgnoreCase( "Open Link" ) ){                    
                    curItem.run();                    
                    break;
                }
            }
            return false;
        } else
            return super.onMenu(instance);
    }
}
