package cn.donting.web.os.api.wap;

import cn.donting.web.os.api.annotation.NonNull;
import cn.donting.web.os.api.annotation.Nullable;
import lombok.Data;

@Data
public class WapInfo {
    /**
     * 唯一id
     */
    @NonNull
    private String id;
    /**
     * 图标
     * 相对于 resources 路径
     * static/icon.png
     */
    @Nullable
    private String iconResource;
    /**
     * 描述
     */
    @NonNull
    private String description;
    /**
     * 版本标识
     */
    @NonNull
    private String version;
    /**
     * 数字版本，用于比较升级版本大小
     * 将不允许 降版本
     */
    @NonNull
    private int numberVersion;
}
