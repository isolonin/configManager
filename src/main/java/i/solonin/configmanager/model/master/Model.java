package i.solonin.configmanager.model.master;

import i.solonin.configmanager.model.DBEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "model")
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"vendor_id", "name"})
}, indexes = {
        @Index(name = "idx_model_name", columnList = "name")
})
@NoArgsConstructor
public class Model extends DBEntity {
    @NotNull
    @ManyToOne
    private Vendor vendor;
    @NotNull
    private String name;
    @OneToMany(mappedBy = "model")
    private List<Device> devices = new ArrayList<>();
    @ManyToOne
    private Template template;

    public Model(@NotNull Vendor vendor, @NotNull String name) {
        this.vendor = vendor;
        this.name = name;
    }

    public String getFullName() {
        return String.format("%s / %s", vendor.getName(), this.getName());
    }
}
