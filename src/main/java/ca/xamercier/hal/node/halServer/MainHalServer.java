package ca.xamercier.hal.node.halServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * This file is a part of the HAL project
 *
 * @author Xavier Mercier | xamercier
 */
public class MainHalServer  extends Thread {

	private static ArrayList<ServerClient> clientList = new ArrayList<ServerClient>();

	private ServerSocket listenSocket = null;
	Socket clientSocket = null;
	BufferedReader in = null;

	public void send(String msg) {
		if(msg.equalsIgnoreCase("null")) {
			return;
		}
		for (ServerClient client : clientList) {
			client.send(msg);
		}
	}

	public void sendOnlyToClient(String msg) {
		for (ServerClient client : clientList) {
			client.send(msg);
		}
	}

	public void init(int port) {
		try {
			listenSocket = new ServerSocket(port);
			System.out.println("Le serveur HAL est bien demarrer");
		} catch (IOException e1) {
			System.out.println("Le serveur HAL n'as pu etre demarrer !" + e1);
		}
		start();
	}

	public void run() {
		while (true) {
			try {
				clientSocket = listenSocket.accept();
			} catch (IOException e) {
				return;
			}
			ServerClient client = new ServerClient(clientSocket, this);
			clientList.add(client);
			client.start();
		}
	}

	public static ArrayList<ServerClient> getClientList() {
		return clientList;
	}

	public void stopServer() {
		try {
			listenSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (ServerClient serverClient : clientList) {
			serverClient.stopServerClient();
		}
	}
}
