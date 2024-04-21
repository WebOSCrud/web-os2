package cn.donting.web.os.core.repository;

import cn.donting.web.os.core.entity.OsWapInstall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OsWapInstallRepository extends JpaRepository<OsWapInstall,String> {
}
