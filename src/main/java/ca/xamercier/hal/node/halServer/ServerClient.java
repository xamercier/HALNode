package ca.xamercier.hal.node.halServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import ca.xamercier.hal.node.HALServer;

/**
 * This file is a part of the HAL project
 *
 * @author Xavier Mercier | xamercier
 */
public class ServerClient extends Thread {

	Socket clientSocket = null;
	BufferedReader in = null;
	PrintWriter out = null;
	MainHalServer server = null;
	String ip = null;
	private Thread thread;

	ServerClient(Socket clientSocket, MainHalServer server) {
		this.clientSocket = clientSocket;
		this.server = server;
		this.ip = clientSocket.getInetAddress().getHostAddress();
		try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(String msg) {
		out.println(msg);
	}

	@Override
	public void run() {
		boolean run = true;
		out.println();
		out.println("");

		while (run) {
			try {
				String line = in.readLine();

				server.send(line);

				boolean isAPlayerRequest = false;
				JSONObject message = new JSONObject(line);
				System.out.print("[HalServer] read => " + message.toString());

				try {
					String player = message.getString("player");
					if (player != null) {
						isAPlayerRequest = true;
					} else {
						isAPlayerRequest = false;
					}
				} catch (JSONException e) {
					isAPlayerRequest = false;
				}

				if (!isAPlayerRequest == true) {

					String action = message.getString("action");

					switch (action.toLowerCase()) {
					case "start": {
						System.out.println(" ok.");
						thread = new Thread(() -> {
							String serverType = message.getString("serverType");
							HALServer s = new HALServer(serverType, thread);
							s.start();
						});
						thread.start();
						break;
					}

					default: {
						System.out.println(" skipped.");
					}
					}
				}

			} catch (IOException e) {
				run = false;
				MainHalServer.getClientList().remove(this);
			} catch (NullPointerException e) {
				run = false;
				MainHalServer.getClientList().remove(this);
			}
		}
		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void stopServerClient() {
		try {
			clientSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
