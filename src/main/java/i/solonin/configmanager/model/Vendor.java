package i.solonin.configmanager.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Entity(name = "vendor")
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
})
public class Vendor extends DBEntity {
    @NotNull
    private String name;
}
