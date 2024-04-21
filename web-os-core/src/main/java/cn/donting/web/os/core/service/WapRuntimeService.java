package cn.donting.web.os.core.service;

import cn.donting.web.os.core.entity.OsWapInstall;
import cn.donting.web.os.core.repository.OsWapInstallRepository;
import cn.donting.web.os.core.service.api.FileSpaceApi;
import cn.donting.web.os.core.wap.WapLoader;
import cn.donting.web.os.core.wap.WapRuntime;
import cn.donting.web.os.core.wap.loader.WapClassLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Service
@Slf4j
public class WapRuntimeService {


    private final Map<String, WapRuntime> wapRuntimeMap = new ConcurrentHashMap<>();
    private final Set<String> updateLockWapIdSet = new CopyOnWriteArraySet<>();

    @Autowired
    OsWapInstallRepository osWapInstallRepository;


    public WapRuntime getWapRuntime(String wapId) {
        return wapRuntimeMap.get(wapId);
    }

    public WapRuntime getWapRuntimeAndLoad(String wapId) {
        WapRuntime wapRuntime = wapRuntimeMap.get(wapId);
        if (wapRuntime == null) {
            return loadWapRuntime(wapId);
        }
        return wapRuntime;
    }

    private synchronized WapRuntime loadWapRuntime(String wapId) {
        WapRuntime wapRuntime = wapRuntimeMap.get(wapId);
        if (wapRuntime != null) {
            return wapRuntime;
        }
        Optional<OsWapInstall> osWapInstallOptional = osWapInstallRepository.findById(wapId);
        if (!osWapInstallOptional.isPresent()) {
            throw new RuntimeException(wapId + " is not install");
        }
        String fileName = osWapInstallOptional.get().getFileName();
        File file = new File(FileSpaceApi.installDir, fileName);
        WapClassLoader wapClassLoader = WapLoader.getWapLoader(file).getWapClassLoader();
        wapRuntime = new WapRuntime(file, wapClassLoader);
        wapRuntimeMap.put(wapId, wapRuntime);
        return wapRuntime;
    }

    /**
     * 停止一个WapRuntime
     * @param wapId
     */
    public synchronized void stopWap(String wapId) {
        WapRuntime wapRuntime = getWapRuntime(wapId);
        if (wapRuntime!=null) {
            log.info("stopWap:[{}]",wapId);
            wapRuntime.stop();
            wapRuntimeMap.remove(wapId);
        }
    }

    /**
     * 锁定一个wapId,不允许加载。
     * 在执行更新wap 时需要
     * @param wapId
     */
    public synchronized void updateLockWap(String wapId){
        //先停止wapRuntime
        stopWap(wapId);
        updateLockWapIdSet.add(wapId);
        log.info("updateLockWap:[{}]",wapId);
    }

    /**
     * 解锁 wapId
     * @param wapId
     */
    public synchronized void updateUnlockWap(String wapId){
        updateLockWapIdSet.remove(wapId);
        log.info("updateUnlockWap:[{}]",wapId);
    }
}
