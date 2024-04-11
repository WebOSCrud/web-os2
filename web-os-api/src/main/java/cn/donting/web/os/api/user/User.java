package cn.donting.web.os.api.user;

import lombok.Data;

@Data
public class User {
    /**
     * 用户名
     */
    String username;
    /**
     * 用户描述
     */
    String description;
    /**
     * 创建时间
     */
    long creatTime;
}
