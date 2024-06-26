package cn.donting.web.os.core.entity;

import cn.donting.web.os.api.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
public class OsUser extends User{

    private String password;

    private String nonce;
    /**
     * nonce 过期时间
     */
    private long nonceExpiredTime;
    @Id
    @Override
    public String getUsername() {
        return super.getUsername();
    }
    @Column
    @Override
    public String getDescription() {
        return super.getDescription();
    }
    @Column
    @Override
    public Long getCreatTime() {
        return super.getCreatTime();
    }
}
