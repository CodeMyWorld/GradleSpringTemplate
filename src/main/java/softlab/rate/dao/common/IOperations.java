package softlab.rate.dao.common;

import java.io.Serializable;
import java.util.List;

/**
 * Created by alex on 17-1-17.
 */
public interface IOperations <T extends Serializable>{
    T findOne(String id);

    List<T> findAll();

    void create(T entity);

    T update(T entity);

    void delete(T entity);

    void deleteById(String id);

    List<T> queryList(String para, String value);
}
