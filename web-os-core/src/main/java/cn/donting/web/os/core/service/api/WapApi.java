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
import cn.donting.web.os.core.repository.OsOsWapInstallRepository;
import cn.donting.web.os.core.wap.WapLoader;
import cn.donting.web.os.core.wap.loader.WapClassLoader;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.URLUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
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
    OsOsWapInstallRepository osOsWapInstallRepository;
    @Autowired
    UserApi userApi;
    @Autowired
    OsFileTypeRepository osFileTypeRepository;

    @Override
    @Transactional
    public synchronized WapInstallInfo install(File file) {
        log.info("install wap file:{}", file);
        WapLoader wapLoader = WapLoader.getWapLoader(file);
        WapInfo wapInfo = wapLoader.getWapInfo();
        Optional<OsWapInstall> wapInstallOptional = osOsWapInstallRepository.findById(wapInfo.getId());
        WapInstallInfo wapInstallInfo;
        if (wapInstallOptional.isPresent()) {
            wapInstallInfo = update(wapLoader);
        } else {
            wapInstallInfo = firstInstall(wapLoader);
        }
        updateFileType(wapLoader);
        //复制wap 文件到wap 安装目录
        FileUtil.copy(file, new File(FileSpaceApi.wapDir, file.getName()), true);
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
    private synchronized WapInstallInfo update(WapLoader wapLoader) {
        log.info("update wap {}", wapLoader.getWapInfo().getId());
        WapInfo wapInfo = wapLoader.getWapInfo();

        OsWapInstall osWapInstall = osOsWapInstallRepository.findById(wapInfo.getId()).get();
        osWapInstall.setWapInstallInfo(wapInfo);
        osWapInstall.setUpdateTime(System.currentTimeMillis());
        User user = userApi.currentLoginUser();
        osWapInstall.setUpdateUser(user == null ? "sys" : user.getUsername());

        osOsWapInstallRepository.save(osWapInstall);
        return osWapInstall.getWapInstallInfo();
    }

    /**
     * 第一次安装
     *
     * @param wapLoader
     */
    private synchronized WapInstallInfo firstInstall(WapLoader wapLoader) {
        log.info("firstInstall wap :{}", wapLoader.getWapInfo().getId());

        WapInfo wapInfo = wapLoader.getWapInfo();
        OsWapInstall osWapInstall = new OsWapInstall();
        osWapInstall.setWapId(wapInfo.getId());

        User user = userApi.currentLoginUser();
        osWapInstall.setInstallUser(user == null ? "sys" : user.getUsername());
        osWapInstall.setWapInstallInfo(wapInfo);
        osWapInstall.setInstallTime(System.currentTimeMillis());

        osOsWapInstallRepository.save(osWapInstall);

        return osWapInstall.getWapInstallInfo();
    }


    private void updateFileType(WapLoader wapLoader) {
        WapInfo wapInfo = wapLoader.getWapInfo();
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
