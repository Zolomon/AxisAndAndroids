package se.axisandandroids.client;

public class HostPort {
    private final String mHost;
    private final int mPort;

    /**
     * @param host
     * @param port
     */
    public HostPort(String host, int port) {
        mHost = host;
        mPort = port;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return mHost;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return mPort;
    }
    
    @Override
    public String toString() {
        /** @formatter:off */
        return mHost+":"+mPort;
        /** @formatter:on */
    }
}