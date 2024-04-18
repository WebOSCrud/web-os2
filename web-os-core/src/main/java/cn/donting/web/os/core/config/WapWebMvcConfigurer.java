package cn.donting.web.os.core.config;

import cn.donting.web.os.core.WebOsCoreApplication;
import cn.donting.web.os.core.service.api.FileSpaceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web mvc 配置
 */
@Configuration
public class WapWebMvcConfigurer implements WebMvcConfigurer {

    public static final String STATIC_PATH = "/" + WebOsCoreApplication.OS_ID + "/static";

    @Autowired
    WebProperties webProperties;

    /**
     * 设置 静态资源 。添加前缀 wapId
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        ///用户头像
        registry.addResourceHandler("/static/**").addResourceLocations("file:" + FileSpaceApi.staticDir.getPath() + "/");
        //主程序 修改静态资源
        if (webProperties.getResources().getStaticLocations() != null) {
            String[] staticLocations = webProperties.getResources().getStaticLocations();
            //注册 设置的 staticLocations
            registry.addResourceHandler("/**").addResourceLocations(staticLocations);
        }

    }



}
