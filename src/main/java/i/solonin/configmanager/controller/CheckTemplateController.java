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

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;

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

    public void init() {
        devices = deviceRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        devices.forEach(d -> d.setCheckingNow(checkService.isDeviceChecking(d)));
    }

    public void checkAll() {
        Optional.ofNullable(filteredDevices).orElse(devices).stream()
                .filter(Device::isEnoughForCheck)
                .filter(d -> !checkService.isDeviceChecking(d)).forEach(this::check);
    }

    public void check(Device device) {
        device.setCheckingNow(true);
        checkService.check(device, result -> {
            try {
                init();
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
