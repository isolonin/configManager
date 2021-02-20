package i.solonin.configmanager.model.check;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import i.solonin.configmanager.model.DBEntity;
import i.solonin.configmanager.model.master.Device;
import i.solonin.configmanager.model.master.Template;
import i.solonin.configmanager.model.template.Divergence;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class CheckingResult extends DBEntity implements Result {
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "deviceId")
    private Device device;
    @Lob
    private String deviceConf;
    @Lob
    private String templateConf;
    private ResultType type = ResultType.EXECUTING;
    @JsonBackReference
    @OneToMany(mappedBy = "checkingResult", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Divergence> divergences = new ArrayList<>();

    @Transient
    private List<String> deviceConfig = new ArrayList<>();
    @Transient
    private List<String> templateConfig;

    public CheckingResult(Device device) {
        this.device = device;
        Template template = device.getModel().getTemplate();
        this.templateConf = template.getConfig();
        this.templateConfig = template.getConfigByLines();
    }

    public boolean hasError(int line) {
        return divergences.stream().anyMatch(d -> d.getTemplateLine() == line + 1);
    }
}
