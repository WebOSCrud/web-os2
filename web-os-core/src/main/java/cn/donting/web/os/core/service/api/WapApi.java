package cn.donting.web.os.core.service.api;

import cn.donting.web.os.api.UserApi;
import cn.donting.web.os.api.user.User;
import cn.donting.web.os.api.wap.FileType;
import cn.donting.web.os.api.wap.WapInfo;
import cn.donting.web.os.api.wap.WapInstallInfo;
import cn.donting.web.os.core.domain.dto.CopyURLToFileDto;
import cn.donting.web.os.core.entity.OsFileType;
import cn.donting.web.os.core.entity.OsWapInstall;
import cn.donting.web.os.core.repository.OsFileTypeRepository;
import cn.donting.web.os.core.repository.OsWapInstallRepository;
import cn.donting.web.os.core.service.WapRuntimeService;
import cn.donting.web.os.core.service.WapService;
import cn.donting.web.os.core.wap.WapLoader;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WapApi implements cn.donting.web.os.api.WapApi {
    @Autowired
    OsWapInstallRepository osOsWapInstallRepository;
    @Autowired
    UserApi userApi;
    @Autowired
    OsFileTypeRepository osFileTypeRepository;
    @Autowired
    WapRuntimeService wapRuntimeService;

    @Override
    @Transactional
    public synchronized WapInstallInfo install(File file) {
        log.info("install wap file:{}", file);
        WapLoader wapLoader = WapLoader.getWapLoader(file);
        WapInfo wapInfo = wapLoader.getWapClassLoader().getWapInfo();
        Optional<OsWapInstall> wapInstallOptional = osOsWapInstallRepository.findById(wapInfo.getId());
        WapInstallInfo wapInstallInfo;

        String extName = FileUtil.extName(file.getName());
        File installFile=new File(FileSpaceApi.wapDir,UUID.randomUUID().toString()+"."+extName);

        if (wapInstallOptional.isPresent()) {
            wapInstallInfo = update(wapLoader,installFile);
        } else {
            wapInstallInfo = firstInstall(wapLoader,installFile);
        }
        updateFileType(wapLoader);

        //复制wap 文件到wap 安装目录
        FileUtil.copy(file, installFile, true);
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
     *
     * @param wapLoader
     */
    private synchronized WapInstallInfo update(WapLoader wapLoader,File installFile) {
        log.info("update wap {}", wapLoader.getWapClassLoader().getWapId());
        WapInfo wapInfo = wapLoader.getWapClassLoader().getWapInfo();
        wapRuntimeService.stopWap(wapInfo.getId());

        OsWapInstall osWapInstall = osOsWapInstallRepository.findById(wapInfo.getId()).get();
        osWapInstall.setWapInstallInfo(wapInfo);
        osWapInstall.setUpdateTime(System.currentTimeMillis());
        User user = userApi.currentLoginUser();
        osWapInstall.setUpdateUser(user == null ? "sys" : user.getUsername());
        String fileName = osWapInstall.getFileName();
        new File(FileSpaceApi.installDir,fileName).delete();
        osWapInstall.setFileName(installFile.getName());
        osOsWapInstallRepository.save(osWapInstall);
        return osWapInstall.getWapInstallInfo();
    }

    /**
     * 第一次安装
     *
     * @param wapLoader
     * @param installFile  安装的目标文件
     */
    private synchronized WapInstallInfo firstInstall(WapLoader wapLoader,File installFile) {
        log.info("firstInstall wap :{}", wapLoader.getWapClassLoader().getWapId());

        WapInfo wapInfo = wapLoader.getWapClassLoader().getWapInfo();
        OsWapInstall osWapInstall = new OsWapInstall();
        osWapInstall.setWapId(wapInfo.getId());

        User user = userApi.currentLoginUser();
        osWapInstall.setInstallUser(user == null ? "sys" : user.getUsername());
        osWapInstall.setWapInstallInfo(wapInfo);
        osWapInstall.setInstallTime(System.currentTimeMillis());
        osWapInstall.setFileName(installFile.getName());
        osOsWapInstallRepository.save(osWapInstall);

        return osWapInstall.getWapInstallInfo();
    }


    private void updateFileType(WapLoader wapLoader) {
        WapInfo wapInfo = wapLoader.getWapClassLoader().getWapInfo();
        String wapId = wapInfo.getId();
        List<OsFileType> osFileTypes = osFileTypeRepository.findAllByWapId(wapId);
        osFileTypeRepository.deleteAllByWapId(wapId);
        //extName,OsFileType
        Map<String, OsFileType> osFileTypeMap = osFileTypes.stream().collect(Collectors.toMap(OsFileType::getExtName, f -> f));

        List<FileType> fileTypes = wapInfo.getFileTypes();
        List<File> deleteFile=new ArrayList<>();

        List<CopyURLToFileDto> copyURLToFileDtos=new ArrayList<>();
        for (FileType fileType : fileTypes) {
            OsFileType osFileType = osFileTypeMap.get(fileType.getExtName());
            if(osFileType!=null){
                String iconName = osFileType.getIconName();
                deleteFile.add(new File(FileSpaceApi.fileTypeIconDir,iconName));
            }
            osFileType = new OsFileType();
            URL resource = wapLoader.getWapClassLoader().getResource(fileType.getIconResource());
            if(resource==null){
                throw new RuntimeException("file iconResource not found:"+fileType.getIconResource()+" ");
            }

            String resourceName = FileUtil.getName(resource.getFile());
            String extName = FileUtil.extName(resourceName);
            File file = new File(FileSpaceApi.fileTypeIconDir, UUID.randomUUID().toString() + "." + extName);

            osFileType.setIconName(file.getName());
            osFileType.setWapId(wapId);
            osFileType.setDesc(fileType.getDescription());
            osFileType.setExtName(fileType.getExtName());
            osFileTypeRepository.save(osFileType);

            CopyURLToFileDto copyURLToFileDto = new CopyURLToFileDto(resource, file);
            copyURLToFileDtos.add(copyURLToFileDto);
        }
        for (CopyURLToFileDto copyURLToFileDto : copyURLToFileDtos) {
            try(InputStream inputStream = copyURLToFileDto.getSource().openStream()){
                FileUtil.writeFromStream(inputStream,copyURLToFileDto.getTarget());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for (File file : deleteFile) {
            file.delete();
        }
    }

}
