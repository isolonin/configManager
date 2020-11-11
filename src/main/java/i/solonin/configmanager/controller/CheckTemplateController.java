package i.solonin.configmanager.controller;

import i.solonin.configmanager.model.CheckingResult;
import i.solonin.configmanager.model.Device;
import i.solonin.configmanager.service.connect.CheckService;
import i.solonin.configmanager.service.repos.DeviceRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static i.solonin.configmanager.constant.Constants.NEW_LINE;

@Getter
@Slf4j
@ViewScoped
@Named("checkTemplateController")
public class CheckTemplateController extends AbstractController {
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private CheckService checkService;
    @Autowired
    private SimpMessagingTemplate webSocket;

    private List<Device> devices;
    @Setter
    private List<Device> filteredDevices;
    @Setter
    private List<Device> selectedDevices;
    @Setter
    private CheckingResult selectedCheckingResult;

    @PostConstruct
    public void init() {
        devices = deviceRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        devices.forEach(d -> {
            d.getChecks().forEach(c -> {
                c.setTemplateConfig(Arrays.asList(c.getTemplateConf().split(NEW_LINE)));
                c.setDeviceConfig(Arrays.asList(c.getDeviceConf().split(NEW_LINE)));
            });
            d.setCheckingNow(checkService.isDeviceChecking(d));
        });
    }

    public void checkAll() {
        if (!checkService.isCheckAllAlreadyRunning()) {
            try {
                List<Device> devices = Optional.ofNullable(selectedDevices).orElse(this.devices).stream()
                        .filter(Device::isEnoughForCheck)
                        .filter(d -> !checkService.isDeviceChecking(d))
                        .collect(Collectors.toList());
                checkService.check(devices).get();
                selectedDevices = null;
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
    }

    public boolean isCheckAllAlreadyRunning() {
        return checkService.isCheckAllAlreadyRunning();
    }

    public void check(Device device) {
        device.setCheckingNow(true);
        checkService.check(device, result -> {
            try {
                init();
                device.setCheckingNow(false);
                webSocket.convertAndSend("/client/update-check-status", device.getId());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });
    }

    public void removeCheck(CheckingResult checkingResult) {
        checkService.remove(checkingResult);
        init();
    }
}
