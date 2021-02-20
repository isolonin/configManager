package i.solonin.configmanager.model.check;

import com.fasterxml.jackson.annotation.JsonIgnore;
import i.solonin.configmanager.model.DBEntity;
import i.solonin.configmanager.model.master.Device;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class ExecutionResult extends DBEntity implements Result {
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "deviceId")
    private Device device;
    @ElementCollection
    @MapKeyColumn(name = "command")
    @Column(name = "response", length = 10000)
    @CollectionTable(name = "execution_result_responses", joinColumns = @JoinColumn(name = "execution_result_id"))
    private Map<String, String> results = new HashMap<>();
    private ResultType type = ResultType.EXECUTING;

    public ExecutionResult(Device device) {
        this.device = device;
    }

    public List<String> getResultsByList(String command) {
        return Arrays.asList(results.get(command).split("\r\n"));
    }
}
