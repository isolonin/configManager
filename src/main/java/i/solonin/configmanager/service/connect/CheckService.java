package i.solonin.configmanager.service.connect;

import i.solonin.configmanager.model.Device;
import i.solonin.configmanager.model.template.Result;

import java.util.function.Consumer;

public interface CheckService {
    void check(Device device, Consumer<Result> consumer);

    boolean isDeviceChecking(Device device);
}
