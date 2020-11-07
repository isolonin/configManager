package i.solonin.configmanager.service.connect;

import i.solonin.configmanager.model.Device;
import i.solonin.configmanager.model.template.Result;
import i.solonin.configmanager.service.template.Checker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class CheckServiceImpl implements CheckService {
    private final Checker checker;
    private final Map<Device, Future<Void>> currentTasks = new ConcurrentHashMap<>();

    @Override
    public void check(Device device, Consumer<Result> consumer) {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
            List<String> result = new ArrayList<>();
            log.info("Start chek device: {}", device.getName());

            try (Stream<String> stream = Files.lines(Paths.get("configs/huawei_1.cfg"))) {
                List<String> deviceConfig = new ArrayList<>();
                stream.forEach(deviceConfig::add);
                checker.diff(device.getModel().getTemplate(), deviceConfig);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            return result;
        }).thenAccept(f -> {
            consumer.accept(new Result(device, f));
            currentTasks.remove(device);
            log.info("Stop chek device: {}", device);
        });
        currentTasks.put(device, future);
    }

    @Override
    public boolean isDeviceChecking(Device device) {
        return currentTasks.containsKey(device);
    }
}
