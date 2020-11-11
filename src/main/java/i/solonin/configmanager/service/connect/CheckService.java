package i.solonin.configmanager.service.connect;

import i.solonin.configmanager.model.CheckingResult;
import i.solonin.configmanager.model.Device;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface CheckService {
    void check(Device device, Consumer<CheckingResult> consumer);

    CompletableFuture<Void> check(List<Device> devices);

    boolean isCheckAllAlreadyRunning();

    boolean isDeviceChecking(Device device);

    void remove(CheckingResult checkingResult);
}
