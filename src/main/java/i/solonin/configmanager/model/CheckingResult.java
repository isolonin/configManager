package i.solonin.configmanager.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import i.solonin.configmanager.model.template.Divergence;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class CheckingResult extends DBEntity {
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "deviceId")
    private Device device;
    @Lob
    private String deviceConf;
    @Lob
    private String templateConf;
    private Type type = Type.CHECKING;
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

    public enum Type {
        CHECKING("Проверяется"),
        CONNECTION_FAILED("Не удалось подключиться"),
        LOGIN_FAILED("Не удалось авторизоваться"),
        COMMAND_FAILED("Не удалось выполнить команду"),
        MATCHES_PATTERN("Соответствует шаблону"),
        NOT_MATCHES_PATTERN("Не соответствует шаблону");

        @Getter
        private final String name;

        Type(String name) {
            this.name = name;
        }
    }
}
