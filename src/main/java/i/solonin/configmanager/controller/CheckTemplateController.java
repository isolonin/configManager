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
    private CheckService checkService;
    @Autowired
    private SimpMessagingTemplate webSocket;

    private List<Device> devices;
    @Setter
    private List<Device> filteredDevices;
    @Setter
    private List<Device> selectedDevices;
    private Device selectedDevice;
    @Setter
    private CheckingResult selectedCheckingResult;

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

    public Device getSelectedDevice() {
        return selectedDevice;
    }

    public void setSelectedDevice(Device selectedDevice) {
        this.selectedDevice = selectedDevice;
    }
}
