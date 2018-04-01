package judgels.service.api;

import java.util.Optional;
import javax.ws.rs.QueryParam;
import judgels.persistence.api.OrderDir;
import judgels.persistence.api.SelectionOptions;
import org.immutables.value.Value;

@Value.Immutable
public interface SelectionParams {
    SelectionParams DEFAULT = new Builder().build();

    @QueryParam("page")
    Optional<Integer> getPage();

    @QueryParam("pageSize")
    Optional<Integer> getPageSize();

    @QueryParam("orderBy")
    Optional<String> getOrderBy();

    @QueryParam("orderDir")
    Optional<OrderDir> getOrderDir();

    default SelectionOptions toOptions() {
        SelectionOptions.Builder options = new SelectionOptions.Builder();
        getPage().ifPresent(options::page);
        getPageSize().ifPresent(options::pageSize);
        getOrderBy().ifPresent(options::orderBy);
        getOrderDir().ifPresent(options::orderDir);
        return options.build();
    }

    class Builder extends ImmutableSelectionParams.Builder {}
}
