package cn.donting.web.os.core.entity;

import cn.donting.web.os.api.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
public class OsUser extends User{

    private String password;
    @Id
    @Override
    public String getUsername() {
        return super.getUsername();
    }
}
