package i.solonin.configmanager.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "template")
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"})
})
public class Template extends DBEntity {
    @NotNull
    private String name;
    @ManyToOne
    @JoinColumn(name = "model_id")
    private Model model;
    @NotNull
    private String fileName;
    @NotNull
    private String contentType;
    @Lob
    @NotNull
    private byte[] data;

    @Transient
    private UploadedFile file;

    public StreamedContent getDownloadFile() {
        return new DefaultStreamedContent(new ByteArrayInputStream(data), contentType, fileName);
    }
}
