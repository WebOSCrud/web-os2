package cn.donting.web.os.core.db.entity;


import cn.donting.web.os.api.user.User;
import cn.donting.web.os.core.db.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Table(id = "username")
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
