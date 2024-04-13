package cn.donting.web.os.api.wap;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class WapInstallInfo extends WapInfo {
    /**
     * 安装时间
     */
    private long installTime;
    /**
     * 更新时间
     */
    private long updateTime;
    /**
     * 更新人
     */
    private String updateUser;
    /**
     * 安装人
     */
    private String installUser;
}
