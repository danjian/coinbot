/*
 * Copyright (C) 2013 by danjian <josepwnz@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.coinbot.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase se usa para gestionar la cola de "workers"
 * @author danjian
 */
public class WorkerQueue implements Runnable {
	private List<WorkerThread> queue = new ArrayList<WorkerThread>();
	private Thread queueThread;
	private int maxWorkers = 4;
	
	public WorkerQueue() {
		queueThread = new Thread(this);
	}
	
	public synchronized void addWorker(WorkerThread worker) {
		queue.add(worker);
	}
	
	public synchronized void removeWorker(WorkerThread worker) {
		queue.remove(worker);
	}
	
	public List<WorkerThread> getWorkers() {
		return new ArrayList<WorkerThread>(queue);
	}
	
	public int countWorkers() {
		return getWorkers().size();
	}
	
	public void clearQueue() {
		queue.clear();
	}
	
	public void stop() {
		queueThread.interrupt();
		stopWorkers();
	}
	
	public void stopWorkers() {
		for (WorkerThread worker : getWorkers()) {
			worker.stop();
		}
		clearQueue();
	}
	
	public void start() {
		queueThread.start();
	}
	
	public boolean isQueueFull() {
		if(countWorkers() >= maxWorkers) {
			return true;
		}
		return false;
	}
	
	public void checkQueue() {
		for (WorkerThread worker : getWorkers()) {
			if(worker.hasFinished()) {
				removeWorker(worker);
			}
		}
	}
	
	@Override
	public void run() {
		
		while(!queueThread.isInterrupted()) {
			//checkQueue();
			
			// La cola no esta llena, nuevo worker ...
			if(!isQueueFull()) {
				System.out.println("Adding worker id: " + countWorkers());
				WorkerThread worker = new WorkerThread(countWorkers());
				worker.start();
				addWorker(worker);
			}
			
			
		}
		
		System.out.println("Queue thread stopped");
	}
	
	
}