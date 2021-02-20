package i.solonin.configmanager.model.master;

import com.fasterxml.jackson.annotation.JsonBackReference;
import i.solonin.configmanager.model.DBId;
import i.solonin.configmanager.model.WithDate;
import i.solonin.configmanager.model.check.CheckingResult;
import i.solonin.configmanager.model.check.ExecutionResult;
import i.solonin.configmanager.model.check.Result;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Getter
@Setter
@Entity(name = "device")
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"host"})
}, indexes = {
        @Index(name = "idx_device_name", columnList = "name"),
        @Index(name = "idx_device_host", columnList = "host")
})
public class Device extends DBId {
    @NotNull
    private String name;
    @NotNull
    private String host;
    private String login;
    private String password;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "model_id")
    private Model model;
    @ManyToOne
    @JoinColumn(name = "region_id")
    private Region region;

    private String location;
    private String firmRevision;
    private String firmware;

    @JsonBackReference
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createAt desc")
    private List<CheckingResult> checks = new ArrayList<>();

    @JsonBackReference
    @OneToMany(mappedBy = "device", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createAt desc")
    private List<ExecutionResult> executions = new ArrayList<>();

    @Transient
    private ImportType importType;
    @Transient
    private Device originDevice;
    @Transient
    private boolean isCheckingNow;

    public void restoreFrom(Device other) {
        this.name = other.getName();
        this.host = other.getHost();
        this.password = other.getPassword();
        this.model = other.getModel();
        this.region = other.getRegion();
        this.location = other.getLocation();
        this.firmRevision = other.getFirmRevision();
        this.firmware = other.getFirmware();
    }

    public List<Result> getResults() {
        List<Result> result = new ArrayList<>(checks);
        result.addAll(executions);
        result.sort(Comparator.comparing(WithDate::getCreateAt).reversed());
        return result;
    }

    public Result getLastCheckingResult() {
        return getResults().stream().max(Comparator.comparing(Result::getCreateAt,
                Comparator.nullsFirst(Comparator.naturalOrder()))).orElse(null);
    }

    public boolean isEnoughForCheck() {
        return !isCheckingNow && !StringUtils.isEmpty(login) && !StringUtils.isEmpty(password) &&
                Optional.ofNullable(model).map(m -> m.getTemplate() != null).orElse(false);
    }

    public boolean isEnoughForConnect() {
        return !StringUtils.isEmpty(login) && !StringUtils.isEmpty(password);
    }

    public boolean equalsByHost(Device other) {
        return this.host.equalsIgnoreCase(other.getHost());
    }

    public boolean isEqualsToOrigin() {
        if (originDevice != null) {
            String originName = Optional.ofNullable(originDevice.getName()).orElse("");
            String originModel = Optional.ofNullable(originDevice.getModel()).map(Model::getFullName).orElse("");
            String originRegion = Optional.ofNullable(originDevice.getRegion()).map(Region::getName).orElse(null);
            String originFirmRevision = Optional.ofNullable(originDevice.getFirmRevision()).orElse(null);
            String originFirmware = Optional.ofNullable(originDevice.getFirmware()).orElse(null);
            String originLocation = Optional.ofNullable(originDevice.getLocation()).orElse(null);

            return name.equals(originName) &&
                    model.getFullName().equals(originModel) &&
                    Objects.equals(Optional.ofNullable(region).map(Region::getName).orElse(null), originRegion) &&
                    Objects.equals(firmRevision, originFirmRevision) &&
                    Objects.equals(firmware, originFirmware) &&
                    Objects.equals(location, originLocation);
        }
        return false;
    }

    public enum ImportType {
        NEW("Добавить"),
        REMOVE("Удалить"),
        MODIFY("Корректировать"),
        EQUALS("Без изменений");

        @Getter
        private final String name;

        ImportType(String name) {
            this.name = name;
        }
    }
}
