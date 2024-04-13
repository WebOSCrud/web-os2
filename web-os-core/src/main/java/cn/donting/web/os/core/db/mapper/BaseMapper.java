package cn.donting.web.os.core.db.mapper;

import java.util.List;

public interface BaseMapper<E,ID> {

    int save(E entity);
    int deleteById(ID id);
    List<E> findAll();
    E findById(ID id);
    int saveAll(List<E> e);

}
