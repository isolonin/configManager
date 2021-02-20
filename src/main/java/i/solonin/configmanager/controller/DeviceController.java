package i.solonin.configmanager.controller;

import i.solonin.configmanager.model.master.Device;
import i.solonin.configmanager.model.master.Model;
import i.solonin.configmanager.model.master.Region;
import i.solonin.configmanager.service.repos.DeviceRepository;
import i.solonin.configmanager.service.repos.ModelRepository;
import i.solonin.configmanager.service.repos.RegionRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.primefaces.PrimeFaces;
import org.primefaces.event.RowEditEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import java.util.List;
import java.util.Optional;

@Getter
@Slf4j
@ViewScoped
@Named("deviceController")
public class DeviceController extends AbstractController {
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private ModelRepository modelRepository;
    @Autowired
    private RegionRepository regionRepository;

    private Device device = new Device();
    @Setter
    private List<Device> filteredDevices;
    private List<Device> devices;
    private List<Model> models;
    private List<Region> regions;

    public void init() {
        devices = deviceRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        models = modelRepository.findAll(Sort.by(Sort.Direction.ASC, "vendor.name", "name"));
        regions = regionRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public void save() {
        try {
            save(device);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            showErrorMessage(ex.getMessage());
            PrimeFaces.current().ajax().addCallbackParam("failed", true);
        }
    }

    private boolean save(Device device) {
        Device oldDevice = deviceRepository.findByHost(device.getHost());
        if (oldDevice != null && !oldDevice.equals(device)) {
            showWarnMessage("Устройство с таким сетевым адресом адресом уже существует");
            PrimeFaces.current().ajax().addCallbackParam("failed", true);
            return false;
        }
        save(device, devices, filteredDevices, deviceRepository);
        this.device = new Device();
        return true;
    }

    public void onRowEdit(RowEditEvent event) {
        Device newDevice = (Device) event.getObject();
        Optional<Device> oldDeviceOptional = deviceRepository.findById(newDevice.getId());
        if (StringUtils.isEmpty(newDevice.getPassword()))
            newDevice.setPassword(oldDeviceOptional.map(Device::getPassword).orElse(null));
        if (!save(newDevice)) oldDeviceOptional.ifPresent(newDevice::restoreFrom);
    }

    public void remove(Device device) {
        remove(device, devices, filteredDevices, deviceRepository);
    }
}
