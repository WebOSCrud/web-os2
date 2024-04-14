package cn.donting.web.os.core;

import cn.donting.web.os.core.db.MapperBeanImport;
import cn.donting.web.os.web.ioc.SimpleWebIocApplication;
import cn.donting.web.os.web.ioc.annotation.Import;

@Import(beanImport={MapperBeanImport.class})
public class WebOsCoreApplication  {
   private static SimpleWebIocApplication simpleIocApplication;
   public static void main(String[] args) throws Exception {
       simpleIocApplication=new SimpleWebIocApplication();
       simpleIocApplication.run(WebOsCoreApplication.class,args);
   }

}
