package ca.xamercier.hal.node;

import java.io.IOException;

import ca.xamercier.hal.node.config.ConfigurationManager;
import ca.xamercier.hal.node.halServer.MainHalServer;
import ca.xamercier.hal.node.utils.ErrorCatcher;
import ca.xamercier.hal.node.utils.NetworkUtils;

/**
 * This file is a part of the HAL project
 *
 * @author Xavier Mercier | xamercier
 */
public class HALNode {
    private static HALNode instance;
    private static MainHalServer server;
    private ConfigurationManager configurationManager;
    private NetworkUtils networkUtils;

    /**
     * Main method
     *
     * @param args the command line arguments
     * @throws Exception if some errors occurs
     */
    public static void main(String[] args) throws Exception {
        @SuppressWarnings("unused")
		HALNode node = new HALNode();
    }

    /**
     * Private constructor to don't create two instance
     *
     * @throws Exception if some errors occurs
     */
    private HALNode() throws Exception {
        startNode();
    }

    /**
     * Real start of the node
     */
    private void startNode() {
        instance = this;
        try {
            configurationManager = new ConfigurationManager();            networkUtils = new NetworkUtils();
        } catch (IOException e) {
            ErrorCatcher.catchError(e);
        }
        
		server = new MainHalServer();
		server.init(configurationManager.getHalPort());
        
    }

    /**
     * Get configuration manager ({@link ConfigurationManager})
     *
     * @return the configuration manager
     */
    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }
    
    public MainHalServer getHalServer() {
    	return server;
    }

    /**
     * Get node ({@link HALNode}) instance
     *
     * @return the {@link HALNode}
     */
    public static HALNode getInstance() {
        return instance;
    }

    /**
     * Get {@link NetworkUtils} instance class
     *
     * @return the {@link NetworkUtils} class
     */
    public NetworkUtils getNetworkUtils() {
        return networkUtils;
    }
}
