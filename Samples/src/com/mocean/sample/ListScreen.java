package com.mocean.sample;

import java.util.Vector;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

public class ListScreen extends MainScreen{
	protected ListField _listField;
	
	public ListScreen() {
		super(Manager.VERTICAL_SCROLL | Manager.VERTICAL_SCROLLBAR);

		setTitle("mOcean Samples");
	}

	public boolean onMenu(int instance){
        if( instance == Menu.INSTANCE_CONTEXT ){
            Menu mainMenu = this.getMenu( Menu.INSTANCE_DEFAULT );
            
            int numItems = mainMenu.getSize();
            for( int i = 0; i < numItems; i++ ){
                MenuItem curItem = mainMenu.getItem(i);
                String name = curItem.toString();
                if( name.equalsIgnoreCase( "click" ) ){                    
                    curItem.run();                    
                    break;
                }
            }
            return false;
        } else
            return super.onMenu(instance);
    }

	//	List Callback implementation
	public class ListCallBack implements ListFieldCallback {
		private Vector listElements = new Vector();

		public void drawListRow(ListField list, Graphics g, int index,
				int y, int w) {
			String text = (String) listElements.elementAt(index);
			g.drawText(text, 20, y, 0, w);
		}

		public Object get(ListField list, int index) {
			return listElements.elementAt(index);
		}

		public int getPreferredWidth(ListField list) {
			return Display.getWidth();
		}

		public void insert(String toInsert, int index) {
			listElements.insertElementAt(toInsert, index);
		}

		public void erase() {
			listElements.removeAllElements();
		}

		public int indexOfList(ListField listField, String prefix, int start) {
			return listElements.indexOf(listField);
		}
	}
}
