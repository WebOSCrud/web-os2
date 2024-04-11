package cn.donting.web.os.launch;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class DevWebOsCoreLaunch extends WebOsCoreLaunch {

    public DevWebOsCoreLaunch(File file) {
        super(file);
    }

    @Override
    protected List<URL> getClasspathURL(File file) {
        try {
            List<URL> urls=new ArrayList<>();
            List<String> lines = Files.readAllLines(file.toPath());
            for (int i = 1; i < lines.size(); i++) {
                urls.add(new File(lines.get(i)).toURI().toURL());
            }
            return urls;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getMainClass(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            return lines.get(0);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
