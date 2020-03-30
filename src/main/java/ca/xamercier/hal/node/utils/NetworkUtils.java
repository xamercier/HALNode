package ca.xamercier.hal.node.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This file is a part of the HAL project
 *
 * @author Xavier Mercier | xamercier
 */
public class NetworkUtils {

    public static final int MIN_PORT_NUMBER = 1024;
    public static final int MAX_PORT_NUMBER = 65535;

    /**
     * Create new instance of the NetworkUtils class
     */
    public NetworkUtils() {

    }

    /**
     * Get random port
     *
     * @return the random port
     */
    public int getRandomPort() {
        return ThreadLocalRandom.current().nextInt(MIN_PORT_NUMBER, MAX_PORT_NUMBER + 1);
    }

    /**
     * Get random port and check if this port is available
     *
     * @return the available port
     */
    public int getRandomAvailablePort() {
        int port = getRandomPort();
        while (!isPortAvailable(port)) {
            port = getRandomPort();
        }
        return port;
    }

    /**
     * Check if port is available
     *
     * @param port the port requested
     * @return true if the port is available, else false
     */
    public boolean isPortAvailable(int port) {
        try {
            ServerSocket srv = new ServerSocket(port);
            srv.close();
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    /**
     * Get host address of the current computer
     *
     * @return the host address (like 192.168.2.147)
     */
    public String getHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            ErrorCatcher.catchError(e);
        }
        return "localhost"; // Callback, I hope that the user is in local ;)
    }
}
