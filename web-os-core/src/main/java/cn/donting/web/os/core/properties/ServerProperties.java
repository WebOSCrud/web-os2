package cn.donting.web.os.core.properties;

import cn.donting.web.os.web.annotation.Properties;

@Properties(prefix = "server")
public class ServerProperties {
    private int port = 8081;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
