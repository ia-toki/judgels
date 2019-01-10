package org.iatoki.judgels.play.model;

import javax.persistence.metamodel.SingularAttribute;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Dao<K, M extends AbstractModel> {

    void persist(M model, String user, String ipAddress);

    M edit(M model, String user, String ipAddress);

    void flush();

    void remove(M model);

    boolean existsById(K id);

    M findById(K id);

    List<M> getAll();

    long countByFilters(String filterString);

    long countByFilters(String filterString, Map<SingularAttribute<? super M, ? extends Object>, ? extends Object> filterColumnsEq, Map<SingularAttribute<? super M, String>, ? extends Collection<String>> filterColumnsIn);

    long countByFiltersEq(String filterString, Map<SingularAttribute<? super M, ? extends Object>, ? extends Object> filterColumnsEq);

    long countByFiltersIn(String filterString, Map<SingularAttribute<? super M, String>, ? extends Collection<String>> filterColumnsIn);

    List<M> findSortedByFilters(String orderBy, String orderDir, String filterString, long offset, long limit);

    List<M> findSortedByFilters(String orderBy, String orderDir, String filterString, Map<SingularAttribute<? super M, ? extends Object>, ? extends Object> filterColumnsEq, Map<SingularAttribute<? super M, String>, ? extends Collection<String>> filterColumnsIn, long offset, long limit);

    List<M> findSortedByFiltersEq(String orderBy, String orderDir, String filterString, Map<SingularAttribute<? super M, ? extends Object>, ? extends Object> filterColumnsEq, long offset, long limit);

    List<M> findSortedByFiltersIn(String orderBy, String orderDir, String filterString, Map<SingularAttribute<? super M, String>, ? extends Collection<String>> filterColumnsIn, long offset, long limit);
}
