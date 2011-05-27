package com.adserver.core;


public class AdserverState {
	private boolean isDefauitImageRequired = false;
	private boolean adserverAlive = true;
	private boolean skipBrowserPhase = false;
	private boolean isUpdate = false;
	private boolean isVisible = true;
	

	protected Object waitTillPageLoad = new Object();
	protected Object timerObject = new Object();
	
	private AdserverBase adserver;
	
	public AdserverState(AdserverBase adserver) {
		setAdserver(adserver);
	}

	/**
	 * @param set adserver object
	 */
	public void setAdserver(AdserverBase adserver) {
		this.adserver = adserver;
	}

	/**
	 * @param isDefauitImageRequired the isDefauitImageRequired to set
	 */
	public void setDefauitImageRequired(boolean isDefauitImageRequired) {
		this.isDefauitImageRequired = isDefauitImageRequired;
	}

	/**
	 * @return the isDefauitImageRequired
	 */
	public boolean isDefauitImageRequired() {
		return isDefauitImageRequired;
	}

	/**
	 * @param adserverAlive the adserverAlive to set
	 */
	public void setAdserverAlive(boolean adserverAlive) {
		this.adserverAlive = adserverAlive;
	}

	/**
	 * @return the adserverAlive
	 */
	public boolean isAdserverAlive() {
		return adserverAlive;
	}

	public void doIt() {

		Thread workerThread = new Thread() {
			public void run() {

				adserver.getLogger().info(" AdserverState :workerThread >>>>>>>>>> workerThread - started");
//				if (isDefauitImageRequired()) {
				if (adserver.defaultImageIsSet) {
					adserver.getLogger().info(" AdserverState :workerThread >>>>>>>>>> set default image");
					adserver.displayDefaultImage();
				}
				
				//run while adserverAlive
				while(isAdserverAlive()) {

					adserver.getLogger().info(" AdserverState :workerThread >>>>>>>>>> begin new cycle");
					adserver.fetchResource();
					
					//waiting for resourceThread
					synchronized (waitTillPageLoad) {
						try {
							waitTillPageLoad.wait();
							adserver.getLogger().info(" AdserverState :workerThread >>>>>>>>>> waitTillPageLoad released");
						} catch (InterruptedException e) {
						} 
					}
					adserver.getLogger().info(" AdserverState :workerThread >>>>>>>>>> - cycle ended");
				} //while alive
				adserver.getLogger().info(" AdserverState :workerThread >>>>>>>>>> - finished");
			};
		};
		workerThread.start();
	}
	
	public void releaseLatch() {
		
		Thread releaseLatchThread = new Thread() {
			public void run() {
				adserver.getLogger().info(" AdserverState :resourseThread >>>>>>>>>> - releaseLatch() - prepare to check adReloadPreiod");
				if (!isUpdate()) {
					//wait adReloadPeriod timer
					synchronized (timerObject) {
						if (adserver.adReloadPreiod > 0 ) {
							try {
								adserver.getLogger().info(" AdserverState :resourseThread >>>>>>>>>> - releaseLatch() - waiting time in ms = " + adserver.adReloadPreiod);
								timerObject.wait(adserver.adReloadPreiod);
								adserver.getLogger().info(" AdserverState :resourseThread >>>>>>>>>> - releaseLatch() - waiting finished");
							} catch (InterruptedException e) {
							} 
						}
					}
					
					//check visibility
					adserver.getLogger().info(" AdserverState :>>>>>>>>>> Form is visible = " + isVisible());
					adserver.getLogger().info(" AdserverState :>>>>>>>>>> isAdserverAlive() = " + isAdserverAlive());
					while (!isVisible() && isAdserverAlive()) {
						synchronized (timerObject) {
							try {
								adserver.getLogger().debug(" AdserverState :>>>>>>>>>>> wait !");
								timerObject.wait(1000);
							} catch (Exception e) {
							}
						}
					}
					
					//check adReloadPeriod 0
					synchronized (timerObject) {
						if (adserver.adReloadPreiod == 0) {
							try {
								adserver.getLogger().info(" AdserverState :resourseThread >>>>>>>>>> - releaseLatch() - waiting time in ms = " + adserver.adReloadPreiod);
								timerObject.wait();
								adserver.getLogger().info(" AdserverState :resourseThread >>>>>>>>>> - releaseLatch() - waiting finished");
							} catch (InterruptedException e) {
							} 
						}
					}
				}
				
				//release thewaitTillPageLoad object  
				synchronized (waitTillPageLoad) {
					adserver.getLogger().info(" AdserverState :resourseThread >>>>>>>>>> - releaseLatch() - releasing waitTillPageLoad object");
					waitTillPageLoad.notify();
				}				
			};
		};
		releaseLatchThread.start();
	}
	
	public void timerNotify() {
		synchronized (timerObject) {
			timerObject.notify();
		}
	}

	public void setSkipBrowserPhase(boolean skipBrowserPhase) {
		this.skipBrowserPhase = skipBrowserPhase;
	}
	
	public boolean isSkipBrowserPhase() {
		return skipBrowserPhase;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public boolean isUpdate() {
		return isUpdate;
	}
}


