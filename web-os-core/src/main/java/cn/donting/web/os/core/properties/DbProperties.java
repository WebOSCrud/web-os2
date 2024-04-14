package cn.donting.web.os.core.properties;

import cn.donting.web.os.web.annotation.Properties;
import lombok.Data;

/**
 * db 连接配置
 */
@Properties(prefix = "web.os.db")
@Data
public class DbProperties {
    private String url;
    private String username;
    private String password;
}
