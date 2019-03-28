package LIC.UC04v1.services;

import java.util.List;

/**
 * Created by bingyang.wei on 5/9/2017.
 */
public interface CRUDService<T> {
    List<?> listAll();
    T getById(Integer id);
    T saveOrUpdate(T domainObject);
    void delete(Integer id);
}
