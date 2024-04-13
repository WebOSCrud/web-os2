package cn.donting.web.os.core.db.entity;


import cn.donting.web.os.api.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class OsUser extends User {
    /**
     * 密码
     */
    private String password;
    /**
     * 登陆 token
     */
    private String token;
}
