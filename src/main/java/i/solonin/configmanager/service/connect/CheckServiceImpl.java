package i.solonin.configmanager.service.connect;

import i.solonin.configmanager.model.CheckingResult;
import i.solonin.configmanager.model.Device;
import i.solonin.configmanager.model.template.Divergence;
import i.solonin.configmanager.service.repos.CheckingResultRepository;
import i.solonin.configmanager.service.repos.DeviceRepository;
import i.solonin.configmanager.service.template.Checker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class CheckServiceImpl implements CheckService {
    private final Map<Device, Future<Void>> currentTasks = new ConcurrentHashMap<>();
    private final List<String> filesNames = Arrays.asList("huawei_1.cfg", "huawei_2.cfg", "huawei_3.cfg", "huawei_4.cfg");
    private CompletableFuture<Void> futureCheckAll;

    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private CheckingResultRepository checkingResultRepository;
    private final Checker checker;

    @Override
    public void check(Device device, Consumer<CheckingResult> consumer) {
        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> check(device))
                .thenAccept(r -> {
                    save(r);
                    currentTasks.remove(device);
                    consumer.accept(r);
                    log.info("Stop chek device: {}", device.getId());
                });
        currentTasks.put(device, future);
    }

    @Override
    public CompletableFuture<Void> check(List<Device> devices) {
        futureCheckAll = CompletableFuture.supplyAsync(() -> {
            List<CheckingResult> result = new ArrayList<>();
            devices.forEach(d -> result.add(check(d)));
            return result;
        }).thenAccept(list -> {
            list.forEach(this::save);
            log.info("Stop chek devices: {}", devices);
        });
        return futureCheckAll;
    }

    @Override
    public boolean isCheckAllAlreadyRunning() {
        return Optional.ofNullable(futureCheckAll).map(f -> !f.isDone()).orElse(false);
    }

    private CheckingResult check(Device device) {
        CheckingResult result = new CheckingResult(device);
        log.info("Start chek device: {}", device.getId());

        Random r = new Random();
        String fileName = filesNames.stream().skip(r.nextInt(filesNames.size() - 1)).findFirst().get();
        try (Stream<String> stream = Files.lines(Paths.get("config/" + fileName))) {
            stream.forEach(s -> result.getDeviceConfig().add(s));
            result.setDeviceConf(String.join("\n", result.getDeviceConfig()));

            List<Divergence> divergences = checker.diff(device.getModel().getTemplate(), result.getDeviceConfig());
            divergences.forEach(d -> d.setCheckingResult(result));
            result.setDivergences(divergences);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return result;
    }

    @Override
    public boolean isDeviceChecking(Device device) {
        return currentTasks.containsKey(device);
    }

    @Override
    @Transactional
    public void remove(CheckingResult checkingResult) {
        Device device = checkingResult.getDevice();
        device.getChecks().remove(checkingResult);
        deviceRepository.save(device);
    }

    private void save(CheckingResult checkingResult) {
        if (checkingResult.getDivergences().isEmpty())
            checkingResult.setType(CheckingResult.Type.MATCHES_PATTERN);
        else
            checkingResult.setType(CheckingResult.Type.NOT_MATCHES_PATTERN);
        checkingResultRepository.saveAndFlush(checkingResult);
    }
}
