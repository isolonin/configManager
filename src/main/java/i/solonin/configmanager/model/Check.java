package i.solonin.configmanager.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity(name = "Device_Check")
@ToString(includeFieldNames = false)
public class Check extends DBEntity {
    @ManyToOne
    private Device device;
    private String deviceConfig;
    @ElementCollection
    @CollectionTable(name = "Check_Error", joinColumns = @JoinColumn(name = "check_id"))
    private List<String> error;
    private Type type = Type.CHECKING;

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
