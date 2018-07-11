package judgels.persistence.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import judgels.persistence.Model;

@Entity
@Table(indexes = {@Index(columnList = "uniqueColumn1,uniqueColumn2", unique = true)})
class ExampleModel extends Model {
    @Column
    String column1;

    @Column
    String column2;

    @Column(unique = true)
    String uniqueColumn;

    @Column
    String uniqueColumn1;

    @Column
    String uniqueColumn2;
}
