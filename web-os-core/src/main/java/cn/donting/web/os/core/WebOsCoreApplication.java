package cn.donting.web.os.core;

import cn.donting.web.os.web.ioc.SimpleWebIocApplication;

public class WebOsCoreApplication  {
   private static SimpleWebIocApplication simpleIocApplication;
   public static void main(String[] args) throws Exception {
       simpleIocApplication=new SimpleWebIocApplication();
       simpleIocApplication.run(WebOsCoreApplication.class,args);
   }

}
