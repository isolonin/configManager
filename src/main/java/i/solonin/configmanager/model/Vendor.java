package i.solonin.configmanager.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "vendor")
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
}, indexes = {
        @Index(name = "idx_vendor_name", columnList = "name")
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
