package cn.donting.web.os.core.util;

public class StringUtil {

    public static String toUnderscore(String str){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (i == 0) {
                builder.append(Character.toLowerCase(c));
                continue;
            }
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    builder.append('_');
                }
                builder.append(Character.toLowerCase(c));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }
}
