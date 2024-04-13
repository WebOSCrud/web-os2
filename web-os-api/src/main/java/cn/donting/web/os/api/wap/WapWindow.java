package cn.donting.web.os.api.wap;

import cn.donting.web.os.api.annotation.NonNull;
import cn.donting.web.os.api.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 * wap 窗口
 * 用于注册 窗口， 可直接打开 注册窗口，并可以从os 创建快方式
 * 并不是说一定要注册的 窗口才能打开，也可以通过 os.js.api 打开自定义的窗口
 * @author donting
 */
@Getter
@Setter
public class WapWindow {
    /**
     * 窗口名称
     */
    @NonNull
    private String name;

    /**
     * icon Resource 路径 {@link ClassLoader#getResource} 能够加载
     * @see ClassLoader#getResource(String)
     */
    @Nullable
    private String iconResource;

    /**
     * 描述
     */
    @Nullable
    private String description;
    /**
     * 窗口参数
     */
    @NonNull
    private WapWindowOption option;
    /**
     * 窗口类型
     */
    @NonNull
    private WapWindowType type;

}
