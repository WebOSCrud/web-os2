package cn.donting.web.os.core.repository;

import cn.donting.web.os.core.entity.OsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface OsUserRepository extends JpaRepository<OsUser,String> {
    @Transactional
    @Modifying
    @Query("update OsUser set nonceExpiredTime=?1 where username=?2")
    int updateNonceExpiredTimeByUsername(Long nonceExpiredTime,String username);

}
