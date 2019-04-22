package LIC.UC04v1.services;

import java.util.List;
import java.util.Optional;

/**
 * Created by bingyang.wei on 5/9/2017.
 */
public interface CRUDService<T> {
    List<?> listAll();
    Optional<T> getById(Integer id);
    T saveOrUpdate(T domainObject);
    void delete(Integer id);
}
