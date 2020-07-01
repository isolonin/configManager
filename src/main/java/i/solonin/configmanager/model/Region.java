package i.solonin.configmanager.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity(name = "region")
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
})
@NoArgsConstructor
public class Region extends DBEntity {
    private String name;
    @OneToMany(mappedBy = "region")
    private List<Device> devices = new ArrayList<>();

    public Region(String name) {
        this.name = name;
    }
}
