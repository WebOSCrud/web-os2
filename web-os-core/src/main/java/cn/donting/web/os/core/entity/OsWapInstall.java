package cn.donting.web.os.core.entity;

import cn.donting.web.os.api.wap.WapInfo;
import cn.donting.web.os.api.wap.WapInstallInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.data.annotation.Transient;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
@Data
@Entity
public class OsWapInstall {
    @Id
    private String wapId;

    /**
     * wap.json
     */
    private String wapInfoStr;

    private String fileName;

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
    @Transient
    public WapInstallInfo getWapInstallInfo() {
        try {
            WapInstallInfo wapInstallInfo = new ObjectMapper().readValue(wapInfoStr, WapInstallInfo.class);
            wapInstallInfo.setInstallTime(this.installTime);
            wapInstallInfo.setInstallUser(this.installUser);
            wapInstallInfo.setUpdateUser(this.updateUser);
            wapInstallInfo.setUpdateTime(this.updateTime);
            return wapInstallInfo;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public void setWapInstallInfo(WapInfo wapInfo) {
        try {
            this.wapInfoStr =new ObjectMapper().writeValueAsString(wapInfo);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
