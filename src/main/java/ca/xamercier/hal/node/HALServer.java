package ca.xamercier.hal.node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import ca.xamercier.hal.node.utils.ErrorCatcher;

/**
 * This file is a part of the HAL project
 *
 * @author Xavier Mercier | xamercier
 */
public class HALServer {

	private Thread thread;
	private String type;
	private JSONObject infos;
	private Process process;
	private boolean typeExists;
	private boolean windows;
	private File file;
	private int port;
	private String name = "";

	/**
	 * Creating new {@link HALServer} instance
	 *
	 * @param type
	 *            type of the server to create
	 */
	public HALServer(String type, Thread thread) {
		this.thread = thread;
		this.type = type;
		windows = System.getProperty("os.name").startsWith("Windows");

		if (HALNode.getInstance().getConfigurationManager().getServers().has(this.type)) {
			typeExists = true;
			this.infos = HALNode.getInstance().getConfigurationManager().getServers().getJSONObject(this.type);
			this.port = HALNode.getInstance().getNetworkUtils().getRandomAvailablePort();
			this.file = new File(HALNode.getInstance().getConfigurationManager().getServersDirectory(),
					this.infos.getString("prefix") + port);
		} else {
			typeExists = false;
			System.out.println("!! - The type: " + this.type + " does not exist on the configuration file !");
		}
	}

	/**
	 * Get start script path executed later
	 *
	 * @return the start script path
	 */
	private String getStartScript() {
		return new File(this.file.getPath(), infos.getString("startScript")).getAbsolutePath();
	}

	/**
	 * Start the server
	 */
	@SuppressWarnings("unused")
	public void start() {
		try {
			if (this.typeExists) {
				/*
				 * 'a voir while (this.file.exists()) { this.file = getPath(); }
				 */

				if (!this.file.mkdirs()) {
					System.out.println("Unable to create server file !");
					return;
				}

				FileUtils.copyDirectory(new File(this.infos.getString("templateDir")), this.file);
				defineServerPort();

				File startScript = new File(getStartScript());
				boolean read = startScript.setReadable(true, false);
				boolean write = startScript.setWritable(true, false);
				boolean exec = startScript.setExecutable(true, false);

				ProcessBuilder pb;
				if (windows) {
					// Windows
					pb = new ProcessBuilder("cmd.exe", "/c", "start", "/w", "cmd", "/c", // open
																							// new
																							// cmd
							getStartScript() // Execution du script de
												// démarrage
					);
				} else {
					// linux
					pb = new ProcessBuilder("screen", "-dmS", this.file.getName(), // create
																					// screen
							getStartScript() // start server
					);
				}
				pb.directory(this.file);

				// Register server on BunggeeCord
				JSONObject registerOnBungee = new JSONObject();
				registerOnBungee.put("action", "register");
				registerOnBungee.put("name", this.file.getName());
				registerOnBungee.put("serverType", this.type);
				registerOnBungee.put("ip", getHost());
				registerOnBungee.put("port", getPort());
				HALNode.getInstance().getHalServer().send(registerOnBungee.toString());

				this.name = this.file.getName();

				this.process = pb.start();
				if (windows) {
					this.process.waitFor();
				} else {
					waitFor();
				}
				stop();
			} else {
			}
		} catch (IOException | InterruptedException e) {
			ErrorCatcher.catchError(e);
		}
	}

	/**
	 * ONLY FOR LINUX Wait for the minecraft server is power off and the screen
	 * is terminated, and continue program after
	 */
	private void waitFor() {
		String[] cmd = { "/bin/sh", "-c", "screen -ls | grep " + this.file.getName() };
		BufferedReader response;

		while (true) {
			try {
				Process p = Runtime.getRuntime().exec(cmd);
				response = new BufferedReader(new InputStreamReader(p.getInputStream()));

				String s;
				int i = 0;
				while ((s = response.readLine()) != null) {
					i++;

					String[] resps = s.trim().split("\\s+");
					String resp = resps[0];
					System.out.println("Found: " + resp);
					// if
					// (Pattern.compile("(^\\d+)(\\.)(\\w+)").matcher(resp).matches())
					// {
					// break waitFor;
					// }
				}

				if (i != 1) {
					break;
				}

				Thread.sleep(10000); // Check every 10 seconds
			} catch (Exception e) {
				ErrorCatcher.catchError(e);
			}
		}
	}

	private void defineServerPort() {
		File serverProperties = new File(this.file, "server.properties");

		try {
			FileOutputStream out = new FileOutputStream(serverProperties);
			FileInputStream in = new FileInputStream(serverProperties);

			Properties props = new Properties();
			props.load(in);
			props.setProperty("server-port", this.port + "");
			props.setProperty("online-mode", "false");
			props.store(out, null);

			out.close();
			in.close();
		} catch (IOException e) {
			ErrorCatcher.catchError(e);
		}
	}

	/**
	 * Force stop the server
	 */
	public void stop() {
		
		JSONObject unRegisterOnBungee = new JSONObject();
		unRegisterOnBungee.put("action", "unregister");
		unRegisterOnBungee.put("serverType", this.type);
		unRegisterOnBungee.put("ip", getHost());
		unRegisterOnBungee.put("port", getPort());
		unRegisterOnBungee.put("name", this.file.getName());
		 
		//String unregister = "{\"port\":"+getPort()+ "," +"\"serverType\":\"" +this.type+"\",\"ip\":\""+getPort()+"\",\"name\":\""+this.file.getName()+"\",\"action\":\"unregister\"}";
		HALNode.getInstance().getHalServer().send(unRegisterOnBungee.toString());

		if (this.process != null) {
			this.process.destroy();

			if (this.thread != null) {
				this.thread.interrupt();
			}

			try {
				FileUtils.deleteDirectory(this.file);
			} catch (IOException e) {
				ErrorCatcher.catchError(e);
			}
		}
	}

	/**
	 * Get host of the minecraft server
	 *
	 * @return host of the minecraft server
	 */
	public String getHost() {
		return HALNode.getInstance().getNetworkUtils().getHostAddress();
	}

	/**
	 * Get the port used by the minecraft server
	 *
	 * @return the port used by the server
	 */
	public int getPort() {
		return port;
	}

	public String getName() {
		return name;
	}
}
