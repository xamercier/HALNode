package ca.xamercier.hal.node.halServer.Thread;

import ca.xamercier.hal.node.HALNode;

/**
 * This file is a part of the HAL project
 *
 * @author Xavier Mercier | xamercier
 */
public class StayConnectedThread extends Thread{
	
	public void run() {
		while (true){
			HALNode.getInstance().getHalServer().send("");
			try {
				Thread.sleep(480000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
