package judgels.persistence;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.metamodel.SingularAttribute;
import judgels.persistence.api.Page;
import judgels.persistence.api.SelectionOptions;
import judgels.persistence.api.dump.UnmodifiableDump;

public interface UnmodifiableDao<M extends UnmodifiableModel> {
    void flush();
    void clear();
    M insert(M model);
    M persist(M model);
    List<M> insertAll(List<M> models);

    Optional<M> select(long id);
    Optional<M> selectByFilter(FilterOptions<M> filterOptions);
    Optional<M> selectByUniqueColumn(SingularAttribute<M, String> column, String value);
    Optional<M> selectByUniqueColumns(Map<SingularAttribute<M, ?>, ?> key);

    long selectCount(FilterOptions<M> filterOptions);

    Page<M> selectPaged(FilterOptions<M> filterOptions, SelectionOptions selectionOptions);

    default Page<M> selectPaged(FilterOptions<M> filterOptions) {
        return selectPaged(filterOptions, SelectionOptions.DEFAULT_PAGED);
    }

    default Page<M> selectPaged(SelectionOptions selectionOptions) {
        return selectPaged(new FilterOptions.Builder<M>().build(), selectionOptions);
    }

    List<M> selectAll(FilterOptions<M> filterOptions, SelectionOptions selectionOptions);

    default List<M> selectAll(FilterOptions<M> filterOptions) {
        return selectAll(filterOptions, SelectionOptions.DEFAULT_ALL);
    }

    default List<M> selectAll(SelectionOptions selectionOptions) {
        return selectAll(new FilterOptions.Builder<M>().build(), selectionOptions);
    }

    void delete(M model);

    void setModelMetadataFromDump(M model, UnmodifiableDump dump);

    @Deprecated M findById(long id);
    @Deprecated List<M> getAll();

    @Deprecated
    long countByFilters(String filterString);

    @Deprecated
    long countByFilters(
            String filterString,
            Map<SingularAttribute<? super M, ?>, ?> filterColumnsEq,
            Map<SingularAttribute<? super M, String>, ? extends Collection<String>> filterColumnsIn);

    @Deprecated
    long countByFiltersEq(String filterString, Map<SingularAttribute<? super M, ?>, ?> filterColumnsEq);

    @Deprecated
    long countByFiltersIn(
            String filterString,
            Map<SingularAttribute<? super M, String>, ? extends Collection<String>> filterColumnsIn);

    @Deprecated
    List<M> findSortedByFilters(String orderBy, String orderDir, String filterString, long offset, long limit);

    @Deprecated
    List<M> findSortedByFilters(
            String orderBy,
            String orderDir,
            String filterString,
            Map<SingularAttribute<? super M, ?>, ?> filterColumnsEq,
            Map<SingularAttribute<? super M, String>, ? extends Collection<String>> filterColumnsIn,
            long offset,
            long limit);

    @Deprecated
    List<M> findSortedByFiltersEq(
            String orderBy,
            String orderDir,
            String filterString,
            Map<SingularAttribute<? super M, ?>, ?> filterColumnsEq,
            long offset,
            long limit);

    @Deprecated
    List<M> findSortedByFiltersIn(
            String orderBy,
            String orderDir,
            String filterString,
            Map<SingularAttribute<? super M, String>, ? extends Collection<String>> filterColumnsIn,
            long offset,
            long limit);
}
