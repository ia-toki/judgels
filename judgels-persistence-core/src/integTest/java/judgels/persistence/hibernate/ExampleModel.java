package judgels.persistence.hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import judgels.persistence.Model;

@Entity
class ExampleModel extends Model {
    @Column
    String column;

    @Column(unique = true)
    String uniqueColumn;
}
