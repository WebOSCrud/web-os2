package cn.donting.web.os.web.log;


public class LoggerConfig {

    public static void load(){
        //启用彩色日志,抄的sprinboot  looggin
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
//        try {
//            URL resource = Thread.currentThread().getContextClassLoader().getResource("logging.properties");
//            InputStream inputStream = resource.openStream();
//            LogManager.getLogManager().readConfiguration(inputStream);
//        }
//        catch (Exception ex){
//            throw new RuntimeException(ex);
//        }
    }
}
