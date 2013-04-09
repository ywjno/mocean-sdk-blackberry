package com.moceanmobile.mast;

import java.util.Enumeration;
import java.util.Vector;

public class BackgroundQueue
{
	private static BackgroundQueue instance = null;
	public static BackgroundQueue getInstance()
	{
		if (instance == null)
			instance = new BackgroundQueue(2);
		
		return instance;
	}
	
	private boolean consoleLog = false;
	
	private int maxIdle = 60000;
	private int maxThreads = 1;
	
	private Object workNotify = new Object();
	private Vector threads = new Vector(5);	
	private SynchronizedTaskQueue queue = new SynchronizedTaskQueue();
	
	public BackgroundQueue(int maxThreads)
	{
		this.maxThreads = maxThreads;
	}
	
	public void setConsoleLog(boolean consoleLog)
	{
		this.consoleLog = consoleLog;
	}
	
	public void terminateThreads()
	{
		synchronized(threads)
		{
			Enumeration e = threads.elements();
			while (e.hasMoreElements())
			{
				Thread t = (Thread)e.nextElement();
				
				if (consoleLog)
					System.out.println("MASTAdView:BackgroundQueue(" + this + "):terminateThreads thread:" + t);
				
				t.interrupt();
			}
			threads.removeAllElements();
		}
	}
	
	public void queueTask(Runnable task)
	{
		if (consoleLog)
			System.out.println("MASTAdView:BackgroundQueue(" + this + "):queuedTask task:" + task);

		queue.add(task);
		executeTasks();
	}
	
	private void executeTasks()
	{
		if (queue.hasTasks() == false)
			return;
		
		synchronized(workNotify)
		{
			workNotify.notifyAll();
		}
		
		synchronized(threads)
		{
			if (threads.isEmpty() || (queue.hasTasks() && (threads.size() < maxThreads)))
			{
				Thread t = new WorkerThread();
				threads.addElement(t);
				
				if (consoleLog)
					System.out.println("MASTAdView:BackgroundQueue(" + this + "):starting worker:" + t);
				
				t.start();
				return;
			}
		}
	}
	
	protected class WorkerThread extends Thread
	{
		public void run()
		{
			while (true)
			{
				Runnable task = queue.popFirst();
				if (task != null)
				{
					try
					{
						if (consoleLog)
							System.out.println("MASTAdView:BackgroundQueue(" + BackgroundQueue.this + "):worker task:" + task);

						task.run();
					}
					catch (Exception e)
					{
						if (consoleLog)
							System.out.println("MASTAdView:BackgroundQueue(" + BackgroundQueue.this + "):worker task exception:" + e);
					}
				}
				
				if (queue.hasTasks())
					continue;
				
				try
				{
					if (consoleLog)
						System.out.println("MASTAdView:BackgroundQueue(" + BackgroundQueue.this + "):worker waiting:" + this);

					synchronized(workNotify)
					{
						long preWait = System.currentTimeMillis();
						
						workNotify.wait(maxIdle);
						
						if (System.currentTimeMillis() - preWait > maxIdle)
							break;
					}
				}
				catch (InterruptedException e)
				{
					if (consoleLog)
						System.out.println("MASTAdView:BackgroundQueue(" + BackgroundQueue.this + "):worker interrupted:" + this);

					break;
				}
			}
			
			if (consoleLog)
				System.out.println("MASTAdView:BackgroundQueue(" + BackgroundQueue.this + "):worker terminating:" + this);
			
			threads.removeElement(this);
		}
	}
	
	protected class SynchronizedTaskQueue
	{
		private Vector queue = new Vector(5);
		
		public synchronized boolean hasTasks()
		{
			return queue.isEmpty() == false;
		}
		
		public synchronized void add(Runnable task)
		{
			queue.addElement(task);
		}
		
		public synchronized Runnable popFirst()
		{
			if (queue.isEmpty())
				return null;
			
			Runnable task = (Runnable) queue.elementAt(0);
			queue.removeElementAt(0);
			
			return task;
		}
	}
}
