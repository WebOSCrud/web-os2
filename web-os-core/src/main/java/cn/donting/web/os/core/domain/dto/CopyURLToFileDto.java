package cn.donting.web.os.core.domain.dto;

import lombok.Getter;

import java.io.File;
import java.net.URL;
@Getter
public class CopyURLToFileDto {
    private URL source;
    private File target;
    public CopyURLToFileDto(URL source, File target) {
        this.source = source;
        this.target = target;
    }
}
