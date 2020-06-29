package i.solonin.configmanager.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity(name = "model")
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"vendor_id", "name"})
})
public class Model extends DBEntity {
    @NotNull
    @OneToOne
    private Vendor vendor;
    @NotNull
    private String name;
}
