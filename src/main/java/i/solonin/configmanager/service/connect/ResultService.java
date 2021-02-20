package i.solonin.configmanager.service.connect;

import i.solonin.configmanager.model.check.CheckingResult;
import i.solonin.configmanager.model.check.Result;
import i.solonin.configmanager.model.master.Device;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public interface ResultService {
    void check(Device device, Consumer<CheckingResult> consumer);

    CompletableFuture<Void> check(List<Device> devices);

    boolean isDeviceChecking(Device device);

    void remove(Result result);
}
