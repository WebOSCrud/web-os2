package cn.donting.web.os.core.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * os 设置
 */
@Entity
@Data
public class OsSetting {

    /**
     * 默认桌面
     */
    public static final String DEF_DESKTOP_WAP_ID="def_desktop_wap_id";

    @Id
    private String key;

    private String wapId;

    @Column(length = 512)
    private String value;
}
