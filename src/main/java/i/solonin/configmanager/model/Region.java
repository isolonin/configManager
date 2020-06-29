package i.solonin.configmanager.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Getter
@Setter
@Entity(name = "region")
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
})
public class Region extends DBEntity {
    private String name;
}
