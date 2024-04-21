package cn.donting.web.os.core.wap;

import cn.donting.web.os.core.wap.loader.WapClassLoader;
import lombok.Getter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.File;
@Getter
public class WapRuntime {

    private final File file;
    private final WapClassLoader wapClassLoader;
    private final String wapId;
    private  Long startTime;
    private  WapRuntimeStatus  wapRuntimeStatus;

    public WapRuntime(File file, WapClassLoader wapClassLoader) {
        this.file = file;
        this.wapClassLoader = wapClassLoader;
        this.wapId = wapClassLoader.getWapId();
        wapRuntimeStatus=WapRuntimeStatus.INIT;
    }


    public synchronized void start(String[] args){
        wapRuntimeStatus=WapRuntimeStatus.STARTING;
        startTime=System.currentTimeMillis();


        wapRuntimeStatus=WapRuntimeStatus.RUNTIME;
    }

    public synchronized void stop(){
        wapRuntimeStatus=WapRuntimeStatus.STOPPING;

        wapRuntimeStatus=WapRuntimeStatus.STOP;
    }


    /**
     * @ TODO::
     * @param req
     * @param res
     */
    public void doService(ServletRequest req, ServletResponse res){

    }
}
