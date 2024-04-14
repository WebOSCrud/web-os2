package cn.donting.web.os.api.user;

import lombok.Data;

@Data
public class User {
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户描述
     */
    private String description;
    /**
     * 创建时间
     */
    private Long creatTime;
}
