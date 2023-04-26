package judgels.persistence;

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

    QueryBuilder<M> query();

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
}
