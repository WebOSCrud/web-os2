package cn.donting.web.os.core.repository;

import cn.donting.web.os.core.entity.OsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OsUserRepository extends JpaRepository<OsUser,String> {
}
