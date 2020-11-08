package i.solonin.configmanager.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
@Entity(name = "template")
public class Template extends DBEntity implements WithName {
    @Column(unique = true, nullable = false)
    private String name;
    @NotNull
    private String fileName;
    @NotNull
    private String contentType;
    @Lob
    @NotNull
    private byte[] data;
    private EquipmentType type;
    @OneToMany(mappedBy = "template", fetch = FetchType.EAGER)
    private List<Model> models = new ArrayList<>();

    @Transient
    private UploadedFile uploadedFile;

    @Transient
    private UploadedFile file;

    public StreamedContent getDownloadFile() {
        return new DefaultStreamedContent(new ByteArrayInputStream(data), contentType, fileName);
    }

    public String getConfig() {
        return new String(data);
    }

    public List<String> getConfigByLines() {
        return Arrays.asList(getConfig().split("(\n|\r\n)"));
    }
}
