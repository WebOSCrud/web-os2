package cn.donting.web.os.core.service.api;

import cn.donting.web.os.api.UserApi;
import cn.donting.web.os.api.user.User;
import cn.donting.web.os.api.wap.WapInfo;
import cn.donting.web.os.api.wap.WapInstallInfo;
import cn.donting.web.os.core.entity.OsWapInstall;
import cn.donting.web.os.core.repository.OsOsWapInstallRepository;
import cn.donting.web.os.core.wap.WapLoader;
import cn.donting.web.os.core.wap.loader.WapClassLoader;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WapApi implements cn.donting.web.os.api.WapApi {
    @Autowired
    OsOsWapInstallRepository osOsWapInstallRepository;
    @Autowired
    UserApi userApi;

    @Override
    @Transactional
    public synchronized WapInstallInfo install(File file) {
        log.info("install wap file:{}",file);
        WapLoader wapLoader = WapLoader.getWapLoader(file);
        WapInfo wapInfo = wapLoader.getWapInfo();
        Optional<OsWapInstall> wapInstallOptional = osOsWapInstallRepository.findById(wapInfo.getId());
        WapInstallInfo wapInstallInfo;
        if (wapInstallOptional.isPresent()) {
            wapInstallInfo=update(wapLoader);
        }else {
            wapInstallInfo=firstInstall(wapLoader);
        }
        //复制wap 文件到wap 安装目录
        FileUtil.copy(file,new File(FileSpaceApi.wapDir,file.getName()),true);
        file.delete();
        return wapInstallInfo;
    }

    @Override
    public void uninstall(String wapId) {

    }

    @Override
    public List<WapInstallInfo> getInstallList() {
        List<OsWapInstall> wapInstalls = osOsWapInstallRepository.findAll();
        List<WapInstallInfo> wapInstallInfos = wapInstalls.stream().map(OsWapInstall::getWapInstallInfo).collect(Collectors.toList());
        return wapInstallInfos;
    }

    /**
     * 更新wap
     * @param wapLoader
     */
    private synchronized WapInstallInfo update(WapLoader wapLoader){
        WapInfo wapInfo = wapLoader.getWapInfo();

        OsWapInstall osWapInstall = osOsWapInstallRepository.findById(wapInfo.getId()).get();
        osWapInstall.setWapInstallInfo(wapInfo);
        osWapInstall.setUpdateTime(System.currentTimeMillis());
        User user = userApi.currentLoginUser();
        osWapInstall.setUpdateUser(user==null ?"sys":user.getUsername());

        osOsWapInstallRepository.save(osWapInstall);
        return osWapInstall.getWapInstallInfo();
    }

    /**
     * 第一次安装
     * @param wapLoader
     */
    private synchronized WapInstallInfo firstInstall(WapLoader wapLoader){
        WapInfo wapInfo = wapLoader.getWapInfo();
        OsWapInstall osWapInstall = new OsWapInstall();
        osWapInstall.setWapId(wapInfo.getId());

        User user = userApi.currentLoginUser();
        osWapInstall.setInstallUser(user==null ?"sys":user.getUsername());
        osWapInstall.setWapInstallInfo(wapInfo);
        osWapInstall.setInstallTime(System.currentTimeMillis());

        osOsWapInstallRepository.save(osWapInstall);

        return osWapInstall.getWapInstallInfo();
    }


}
