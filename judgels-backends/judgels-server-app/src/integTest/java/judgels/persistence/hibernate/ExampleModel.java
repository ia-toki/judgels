package judgels.persistence.hibernate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
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
