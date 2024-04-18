package cn.donting.web.os.core.repository;

import cn.donting.web.os.core.entity.OsFileType;
import cn.donting.web.os.core.entity.OsUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OsFileTypeRepository extends JpaRepository<OsFileType,Long> {
    int deleteAllByWapId(String wapId);
    List<OsFileType> findAllByWapId(String wapId);
}
