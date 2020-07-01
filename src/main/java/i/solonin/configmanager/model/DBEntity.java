package i.solonin.configmanager.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
public class DBEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    protected Long id;
    protected Date createAt;
    protected Date updateAt;

    public boolean isNew() {
        return id == null;
    }

    @PrePersist
    public void setCreatedDate() {
        this.createAt = new Date();
    }

    @PreUpdate
    public void setUpdateDate() {
        this.updateAt = new Date();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DBEntity dbEntity = (DBEntity) o;
        return Objects.equals(id, dbEntity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
