package i.solonin.configmanager.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
public class DBEntity extends DBId implements WithDate {
    protected Date createAt;
    protected Date updateAt;

    @PrePersist
    public void setCreatedDate() {
        this.createAt = new Date();
        this.updateAt = new Date();
    }

    @PreUpdate
    public void setUpdateDate() {
        this.updateAt = new Date();
    }
}
