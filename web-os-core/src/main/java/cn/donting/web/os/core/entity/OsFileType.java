package cn.donting.web.os.core.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 注册到系统的文件类型
 */
@Entity
@Data
public class OsFileType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * wapId
     */
    @Column(nullable = false)
    private String wapId;
    /**
     * 扩展名
     */
    @Column(nullable = false)
    private String extName;
    /**
     * 描述
     */
    private String desc;
    /**
     * 图标访问名称
     * core 会提取出来到统一的路径下
     */
    @Column(nullable = false)
    private String iconName;
    /**
     * 是否默认
     */
    private boolean def;
}
