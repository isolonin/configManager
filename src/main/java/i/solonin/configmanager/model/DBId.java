package i.solonin.configmanager.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
public class DBId implements WithId {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Access(AccessType.PROPERTY)
    public Long id;

    public boolean isNew() {
        return id == null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DBId dbEntity = (DBId) o;
        return Objects.equals(id, dbEntity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
