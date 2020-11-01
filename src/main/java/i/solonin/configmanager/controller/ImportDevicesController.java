package i.solonin.configmanager.controller;

import i.solonin.configmanager.model.Device;
import i.solonin.configmanager.service.ImportService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.primefaces.event.FileUploadEvent;
import org.springframework.beans.factory.annotation.Autowired;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@SessionScoped
@Getter
@Slf4j
@Named("importDevicesController")
public class ImportDevicesController extends AbstractController implements Serializable {
    @Autowired
    private ImportService importService;

    private String fileName;
    private List<Device> devices;
    @Setter
    private List<Device> filteredDevices;
    @Setter
    private List<Device> selectedDevices;

    public void init() {
        if (devices == null) redirect("/data/device.xhtml");
    }

    public void uploadFile(FileUploadEvent event) {
        try {
            devices = importService.getDevicesByExcel(event.getFile());
            selectedDevices = devices.stream().filter(d -> d.getImportType().equals(Device.ImportType.NEW) ||
                    d.getImportType().equals(Device.ImportType.MODIFY)).collect(Collectors.toList());
            fileName = event.getFile().getFileName();
            redirect("/data/import.xhtml");
        } catch (Exception e) {
            showErrorMessage(e.getMessage());
        }
    }

    public void importDevices() {
        try {
            importService.importDevices(selectedDevices);
            devices = null;
            fileName = null;
            redirect("/data/device.xhtml");
        } catch (Exception e) {
            showErrorMessage(e.getMessage());
        }
    }

    public long getNewCount() {
        return Optional.ofNullable(selectedDevices).orElse(Collections.emptyList()).stream()
                .filter(d -> d.getImportType().equals(Device.ImportType.NEW)).count();
    }

    public long getRemoveCount() {
        return Optional.ofNullable(selectedDevices).orElse(Collections.emptyList()).stream()
                .filter(d -> d.getImportType().equals(Device.ImportType.REMOVE)).count();
    }

    public long getModityCount() {
        return Optional.ofNullable(selectedDevices).orElse(Collections.emptyList()).stream()
                .filter(d -> d.getImportType().equals(Device.ImportType.MODIFY)).count();
    }

    public long getEqualsCount() {
        return Optional.ofNullable(selectedDevices).orElse(Collections.emptyList()).stream()
                .filter(d -> d.getImportType().equals(Device.ImportType.EQUALS)).count();
    }
}
