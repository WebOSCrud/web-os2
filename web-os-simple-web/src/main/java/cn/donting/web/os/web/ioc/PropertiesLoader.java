package cn.donting.web.os.web.ioc;

import cn.donting.web.os.web.annotation.Properties;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.List;

public class PropertiesLoader {


    public static void loader(SimpleWebIocApplication simpleWebIocApplication) throws Exception {
        ClassLoader classLoader = simpleWebIocApplication.getClass().getClassLoader();
        URL resource = classLoader.getResource("application.properties");
        java.util.Properties properties = new java.util.Properties();
        try (InputStream inputStream = resource.openStream()) {
            properties.load(inputStream);
        }
        List allBeans = simpleWebIocApplication.getAllBeans();
        for (Object bean : allBeans) {
            Properties annotation = bean.getClass().getAnnotation(Properties.class);
            if (annotation == null) {
                continue;
            }
            String prefix = annotation.prefix();
            Method[] declaredMethods = bean.getClass().getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                if (Modifier.isPublic(declaredMethod.getModifiers()) && declaredMethod.getName().startsWith("set")) {
                    String propertiesKey = toPropertiesKey(declaredMethod,prefix);
                    Class<?> parameterType = declaredMethod.getParameterTypes()[0];
                    String value = properties.getProperty(propertiesKey);
                    if(value==null){
                        continue;
                    }
                    if (parameterType.equals(String.class)) {
                        declaredMethod.invoke(bean,value);
                        continue;
                    }
                    if (parameterType.equals(int.class) || parameterType.equals(Integer.class)) {
                        declaredMethod.invoke(bean,Integer.parseInt(value));
                        continue;
                    }
                    throw new RuntimeException(parameterType+" 不支持");
                }
            }
        }
    }

    private static String toPropertiesKey(Method columMethod,String prefix) {
        String name = columMethod.getName();
        //去掉set
        name = name.substring(3);
        StringBuilder builder = new StringBuilder();
        if(!prefix.equals("")){
             builder.append(prefix).append(".");
        }
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (i == 0) {
                builder.append(Character.toLowerCase(c));
                continue;
            }
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    builder.append('.');
                }
                builder.append(Character.toLowerCase(c));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }


}
