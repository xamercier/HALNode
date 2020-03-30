package ca.xamercier.hal.node.config;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import java.io.*;
import java.net.URL;

/**
 * This file is a part of the HAL project
 *
 * @author Xavier Mercier | xamercier
 */
public class ConfigurationManager {
	private JSONObject servers;
	private JSONObject halServer;
	private File serversDirectory;

	/**
	 * Creating new instance of the ConfigurationManager
	 *
	 * @throws IOException
	 *             if config parser don't finhish with success
	 */
	public ConfigurationManager() throws IOException {
		loadConfigurationFile();
	}

	/**
	 * Loading the config.json file and parse it
	 *
	 * @throws IOException
	 *             if config parser don't finish with success
	 */
	private void loadConfigurationFile() throws IOException {
		URL defaultFile = getClass().getClassLoader().getResource("config.default.json");
		File configFile = new File("config.json");
		if (!configFile.exists()) {
			if (defaultFile == null) {
				throw new FileNotFoundException("Unable to create default config file");
			}
			FileUtils.copyURLToFile(defaultFile, configFile);
		}

		BufferedReader reader = new BufferedReader(new FileReader(configFile));

		StringBuilder builder = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			builder.append(line.trim());
		}
		reader.close();
		String content = builder.toString();

		JSONObject globalConfiguration = new JSONObject(content);

		servers = globalConfiguration.getJSONObject("servers");
		halServer = globalConfiguration.getJSONObject("HALServer");

		this.serversDirectory = new File(globalConfiguration.getString("serverDirectory"));
		if (!this.serversDirectory.exists()) {
			if (!this.serversDirectory.mkdirs()) {
				System.err.println("Unable to create " + this.serversDirectory.getPath() + " directory !");
				System.exit(1);
			}
		}
	}

	/**
	 * Get servers type with all the needed information
	 *
	 * @return the server JSON object
	 */
	public JSONObject getServers() {
		return servers;
	}

	public int getHalPort() {
		return this.halServer.getInt("port");
	}

	/**
	 * Get directory where all the server are stored
	 *
	 * @return the directory
	 */
	public File getServersDirectory() {
		return serversDirectory;
	}
}
