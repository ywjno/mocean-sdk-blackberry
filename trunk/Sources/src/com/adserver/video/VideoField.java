package com.adserver.video;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VideoControl;
import javax.microedition.media.control.VolumeControl;

import com.adserver.core.AdClickListener;
import com.adserver.core.AdserverBase;

import net.rim.blackberry.api.browser.Browser;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.TouchEvent;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class VideoField extends VerticalFieldManager implements PlayerListener {
	private Player _player;
	private Field _videoField;
    private AdClickListener clickListener;
    private String url;
    private AdserverBase adserver;

    private int width = Display.getWidth();
	private int height = Display.getHeight();

	public VideoField(final String fileName, int width, int height, AdClickListener clickListener, final String url, AdserverBase adserver) {
		super(FOCUSABLE);
		this.width = width;
		this.height = height;
		this.clickListener = clickListener;
		this.url = url;
		this.adserver = adserver;

//		UiApplication.getUiApplication().invokeLater(new Runnable() {
//			public void run() {
				initializeMedia(fileName);

				// If initialization was successful...
				if (_videoField != null) {
					add(_videoField);
//					updateVideoSize();
//					Logger.debug(" VideoField >>>>>>>>>>> playerStart() TEST");
//					playerStart();
				} else {
					adserver.getLogger().debug(" VideoField : Error: Could not load media: " + fileName);
				}
//			}
//		});
	}

	// Start player
	public void playerStart() {
		Thread thread = new Thread() {
			public void run() {
				try {
				int playerState = _player.getState();
				if (playerState == Player.PREFETCHED
						|| playerState == Player.REALIZED) {
					try {
						_player.start();
					} catch (MediaException me) {
						adserver.getLogger().debug(" VideoField :Video Player threw exception on PLAYER_START()" + me.getMessage());
					}
				}
				} catch (Exception e) {
				}
			}
		};
		thread.start();
	}

	// Stop player
	public void playerStop() {
		Thread thread = new Thread() {
			public void run() {
			try {
				int playerState = _player.getState();
				if (playerState == Player.STARTED) {
					try {
						_player.stop();
					} catch (MediaException me) {
						adserver.getLogger().debug(" VideoField :Video Player threw exception on PLAYER_STOP()" + me.toString());
					}
				}
			} catch (Exception e) {
			}
			}
		};
		thread.start();
	}

	/**
	 * Creates a Player based on a specified URL and provides a VolumeControl
	 * object.
	 */

	private void initializeMedia(String fileNme) {
		try {
			
			FileConnection conn = null;
			conn = (FileConnection) Connector.open(fileNme, Connector.READ_WRITE);
			InputStream is = conn.openInputStream();
			
			//read content type
			DataInputStream dis = new DataInputStream(is);
			String contentType = dis.readUTF();
			adserver.getLogger().info("VideoField :video >>>>>>>>>> content-type :" + contentType);
			//need close streams!

			_player = javax.microedition.media.Manager.createPlayer(is,contentType);
			_player.addPlayerListener(this);
			_player.realize();

			VideoControl vc = (VideoControl) _player.getControl("VideoControl");
			if (vc != null) {
				_videoField = (Field) vc.initDisplayMode(VideoControl.USE_GUI_PRIMITIVE,"net.rim.device.api.ui.Field");
				vc.setVisible(true);
				vc.setDisplaySize(width, height);
				VolumeControl volume = (VolumeControl) _player.getControl("VolumeControl");
				volume.setLevel(100);
			}
		} catch (MediaException pe) {
			VideoField.errorDialog(pe.toString());
			adserver.getLogger().debug(" VideoField :Manager.createPlayer() threw "+ pe.toString());
		} catch (IOException ioe) {
			adserver.getLogger().debug(" VideoField :Manager.createPlayer() threw "+ ioe.toString());
		}
	}

//	/**
//	 * Updates the video size according to the current screen dimensions
//	 * 
//	 * @param screenWidth
//	 *            The screen's width.
//	 * @param screenHeight
//	 *            The screen's height.
//	 */
//	private void updateVideoSize() {
//		if (_player != null) {
//			try {
//
//				// VideoControl vc = (VideoControl)
//				// _player.getControl("VideoControl");
//				// if( vc != null )
//				// {
//				// net.rim.device.api.ui.Manager manager = getMainManager();
//				// vc.setDisplaySize( videoWidth, videoHeight);
//				// }
//			} catch (Exception e) {
//				// VideoScreen.errorDialog("VideoControl#setDisplayDize() threw "
//				// + e.toString());
//			}
//		}
//	}

	public void playerUpdate(Player player, final String event, Object eventData) {
		UiApplication.getUiApplication().invokeLater(new Runnable() {
			public void run() {

				if (event.equals(END_OF_MEDIA)) {
					adserver.getLogger().info (" VideoField :Video >>>>>>>>>>> playerUpdate -> END_OF_MEDIA");
					playerStart();
				}
			}
		});
	}

	/**
	 * Presents a dialog to the user with a given message
	 * 
	 * @param message
	 *            The text to display
	 */
	public static void errorDialog(final String message) {
		UiApplication.getUiApplication().invokeLater(new Runnable() {
			public void run() {
				Dialog.alert(message);
			}
		});
	}

	protected void onDisplay() {
		super.onDisplay();
		adserver.getLogger().debug(" VideoField :Video >>>>>>>>>>> onDisplay -> playerStart()");
		playerStart();
	}

	protected void onUndisplay() {
		super.onUndisplay();
		adserver.getLogger().debug(" VideoField :Video >>>>>>>>>>> onUndisplay -> playerStop()");
		playerStop();
		try {
			delete(_videoField);
			_player = null;
		} catch (Exception e) {
		}
	}

	protected void onVisibilityChange(boolean visible) {
		super.onVisibilityChange(visible);
		if (visible) {
			adserver.getLogger().debug(" VideoField :Video >>>>>>>>>>> onVisibilityChange -> visible -> playerStart()");
			playerStart();
		} else {
			adserver.getLogger().debug(" VideoField :Video >>>>>>>>>>> onVisibilityChange -> invisible -> playerStop()");
			playerStop();
		}
	}

	protected boolean touchEvent(TouchEvent message) {
		if (message.getEvent() == TouchEvent.CLICK) {

			adserver.getLogger().info(" VideoField : video ad clicked");
			if (null != clickListener) {
				Application.getApplication().invokeLater(new Runnable() {
					public void run() {
						if (!clickListener.didAdClicked(url)) {
							Browser.getDefaultSession().displayPage(url);
						}

					}
				});
			} else {
				Application.getApplication().invokeLater(new Runnable() {
					public void run() {
						Browser.getDefaultSession().displayPage(url);
					}
				});
			}

			
			return true;
		}
		return super.touchEvent(message);
	}
//	protected boolean navigationClick(int status, int time) {
//		//keyboards only
//		VideoField.errorDialog("Clicked");
//		return true;
//	}
}
