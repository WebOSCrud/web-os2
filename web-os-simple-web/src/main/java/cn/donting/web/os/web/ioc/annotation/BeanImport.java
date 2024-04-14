package cn.donting.web.os.web.ioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public interface BeanImport {
    Map<String,Object> importBeans(List<Class> packageScanClass) throws Exception;
}
