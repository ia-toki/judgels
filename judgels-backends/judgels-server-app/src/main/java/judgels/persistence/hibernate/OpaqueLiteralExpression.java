package judgels.persistence.hibernate;

import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.LiteralExpression;

public class OpaqueLiteralExpression extends LiteralExpression<Void> {
    private final String value;

    public OpaqueLiteralExpression(CriteriaBuilder cb, String value) {
        super((CriteriaBuilderImpl) cb, Void.class, null);
        this.value = value;
    }

    @Override
    public String render(RenderingContext renderingContext) {
        return this.value;
    }
}
