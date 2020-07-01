package i.solonin.configmanager.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "vendor")
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
})
public class Vendor extends DBEntity {
    @NotNull
    private String name;
    @OneToMany(mappedBy = "vendor")
    private List<Model> models = new ArrayList<>();

    public Vendor(@NotNull String name) {
        this.name = name;
    }
}
