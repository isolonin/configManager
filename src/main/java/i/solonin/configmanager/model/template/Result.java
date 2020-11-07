package i.solonin.configmanager.model.template;

import i.solonin.configmanager.model.Device;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@ToString(includeFieldNames = false)
public class Result {
    private Device device;
    private List<String> errors = new ArrayList<>();
}
