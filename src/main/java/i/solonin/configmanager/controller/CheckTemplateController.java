package i.solonin.configmanager.controller;

import i.solonin.configmanager.model.check.CheckingResult;
import i.solonin.configmanager.model.check.ExecutionResult;
import i.solonin.configmanager.model.check.Result;
import i.solonin.configmanager.model.master.Device;
import i.solonin.configmanager.service.connect.ResultService;
import i.solonin.configmanager.service.repos.DeviceRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static i.solonin.configmanager.constant.Constants.NEW_LINE;

@Getter
@Slf4j
@ViewScoped
@Named("checkTemplateController")
public class CheckTemplateController extends AbstractController {
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private ResultService resultService;
    @Autowired
    private SimpMessagingTemplate webSocket;

    private List<Device> devices;
    @Setter
    private List<Device> filteredDevices;
    @Setter
    private List<Device> selectedDevices;
    @Setter
    private CheckingResult selectedCheckingResult;
    private ExecutionResult selectedExecutionResult;

    @PostConstruct
    @Transactional
    public void init() {
        devices = deviceRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        devices.forEach(d -> {
            d.getChecks().forEach(c -> {
                c.setTemplateConfig(Arrays.asList(c.getTemplateConf().split(NEW_LINE)));
                c.setDeviceConfig(Arrays.asList(c.getDeviceConf().split(NEW_LINE)));
            });
            d.setCheckingNow(resultService.isDeviceChecking(d));
        });
    }

    public void checkAll() {
        if (!isCheckAllAlreadyRunning()) {
            Optional.ofNullable(selectedDevices).orElse(this.devices).stream()
                    .filter(Device::isEnoughForCheck)
                    .filter(d -> !resultService.isDeviceChecking(d))
                    .forEach(d -> resultService.check(d, r -> {
                        selectedDevices.remove(d);
                        init();
                        webSocket.convertAndSend("/client/update-check-status", isCheckAllAlreadyRunning());
                    }));
        } else {
            showErrorMessage("Сверка уже запущена. Дождитесь окончания операции");
        }
    }

    public synchronized boolean isCheckAllAlreadyRunning() {
        return Optional.ofNullable(selectedDevices).map(l -> l.stream().anyMatch(Device::isCheckingNow)).orElse(false);
    }

    public void check(Device device) {
        resultService.check(device, result -> {
            try {
                init();
                webSocket.convertAndSend("/client/update-check-status", false);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
    }

    public void setSelectedExecutionResult(ExecutionResult selectedExecutionResult) {
        this.selectedExecutionResult = selectedExecutionResult;
    }

    public void removeCheck(Result result) {
        resultService.remove(result);
        init();
    }
}
