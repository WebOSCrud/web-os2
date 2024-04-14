package cn.donting.web.os.core.util;

import java.util.List;

public class Lists {

    public static String toString(List list) {
        if (list.size() == 0) {
            return "";
        }
        StringBuilder str = new StringBuilder();
        str.append(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            str.append(",").append(list.get(i));
        }
        return str.toString();
    }
}
