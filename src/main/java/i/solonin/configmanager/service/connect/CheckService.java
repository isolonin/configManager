package i.solonin.configmanager.service.connect;

import i.solonin.configmanager.model.CheckingResult;
import i.solonin.configmanager.model.Device;

import java.util.function.Consumer;

public interface CheckService {
    void check(Device device, Consumer<CheckingResult> consumer);

    boolean isDeviceChecking(Device device);

    void remove(CheckingResult checkingResult);
}
