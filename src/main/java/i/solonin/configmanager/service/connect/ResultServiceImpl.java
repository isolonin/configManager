package i.solonin.configmanager.service.connect;

import i.solonin.configmanager.model.check.CheckingResult;
import i.solonin.configmanager.model.check.ExecutionResult;
import i.solonin.configmanager.model.check.Result;
import i.solonin.configmanager.model.check.ResultType;
import i.solonin.configmanager.model.master.Device;
import i.solonin.configmanager.model.template.Divergence;
import i.solonin.configmanager.service.repos.CheckingResultRepository;
import i.solonin.configmanager.service.repos.DeviceRepository;
import i.solonin.configmanager.service.repos.ExecutionResultRepository;
import i.solonin.configmanager.service.template.Checker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResultServiceImpl implements ResultService {
    private final List<Device> currentTasks = new CopyOnWriteArrayList<>();
    private final List<String> filesNames = Arrays.asList("huawei_1.cfg", "huawei_2.cfg", "huawei_3.cfg", "huawei_4.cfg");
    private CompletableFuture<Void> futureCheckAll;

    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private CheckingResultRepository checkingResultRepository;
    private final Checker checker;
    private final EntityManager em;

    @Override
    public void check(Device device, Consumer<CheckingResult> consumer) {
        CompletableFuture.supplyAsync(() -> check(device))
                .thenAccept(r -> {
                    save(r);
                    consumer.accept(r);
                    log.info("Stop chek device: {}", device.getId());
                });
    }

    @Override
    public CompletableFuture<Void> check(List<Device> devices) {
        futureCheckAll = CompletableFuture.supplyAsync(() -> {
            List<CheckingResult> result = new ArrayList<>();
            devices.forEach(d -> result.add(check(d)));
            return result;
        }).thenAccept(list -> {
            list.forEach(this::save);
            log.info("Stop chek devices: {}", devices.size());
        });
        return futureCheckAll;
    }

    private CheckingResult check(Device device) {
        currentTasks.add(device);
        device.setCheckingNow(true);

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
            device.getChecks().add(0, result);
            Thread.sleep(1000);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            device.setCheckingNow(false);
            currentTasks.remove(device);
        }
        return result;
    }

    @Override
    public boolean isDeviceChecking(Device device) {
        return currentTasks.contains(device);
    }

    @Override
    @Transactional
    public void remove(Result result) {
        Device device = result.getDevice();
        if (result instanceof CheckingResult)
            device.getChecks().remove(result);
        if (result instanceof ExecutionResult)
            device.getExecutions().remove(result);

        deviceRepository.save(device);
    }

    private void save(CheckingResult checkingResult) {
        if (checkingResult.getDivergences().isEmpty())
            checkingResult.setType(ResultType.MATCHES_PATTERN);
        else
            checkingResult.setType(ResultType.NOT_MATCHES_PATTERN);
        checkingResultRepository.saveAndFlush(checkingResult);
    }
}
